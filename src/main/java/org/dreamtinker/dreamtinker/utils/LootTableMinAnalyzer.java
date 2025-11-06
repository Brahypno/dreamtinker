package org.dreamtinker.dreamtinker.utils;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.*;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.*;

/**
 * LootTable 白箱分析器（带 System.out 调试输出）—— Forge/Mojmap 1.20.1
 * <p>
 * 忽略 conditions/functions，仅按 weight 与 luck*quality 计算：
 * - per-roll 概率（含 empty）
 * - 至少一次命中（用 r_min 的下界）
 */
public final class LootTableMinAnalyzer {

    /**
     * 打印开关；可在运行时改
     */
    public static boolean DEBUG = true;

    private static void log(String fmt, Object... args) {
        if (DEBUG)
            System.out.println("[LootMinDBG] " + String.format(Locale.ROOT, fmt, args));
    }

    private LootTableMinAnalyzer() {}

  /* ===========================
     对外 API
     =========================== */

    /**
     * 物品“种类键”（此处忽略 NBT；如需区分可自行扩展）
     */
    public static record ItemKey(Item item) {
        public static ItemKey of(ItemStack s) {return new ItemKey(s.getItem());}
    }

    /**
     * 每个 pool 的“最低项”结果
     */
    public static final class PoolMinResult {
        public final int poolIndex;
        public final ItemKey item;             // 概率最低的物品
        public final double perRollQ;          // 仅物品内部归一（不含 empty）
        public final double perRollQIncludingEmpty; // 含 empty 的 per-roll 概率
        public final int rMin;                 // 估计的最小 roll 数
        public final double atLeastOnceLowerBound; // 1 - (1 - qIncEmpty)^rMin

        public PoolMinResult(
                int poolIndex, ItemKey item,
                double perRollQ, double perRollQIncludingEmpty,
                int rMin, double atLeastOnceLowerBound) {
            this.poolIndex = poolIndex;
            this.item = item;
            this.perRollQ = perRollQ;
            this.perRollQIncludingEmpty = perRollQIncludingEmpty;
            this.rMin = rMin;
            this.atLeastOnceLowerBound = atLeastOnceLowerBound;
        }

        @Override
        public String toString() {
            return String.format(Locale.ROOT,
                                 "Pool#%d min: %s | qItems=%.6g qIncEmpty=%.6g rMin=%d P>=1(lb)=%.6g",
                                 poolIndex,
                                 item.item() == null ? "null" : item.item().toString(),
                                 perRollQ, perRollQIncludingEmpty, rMin, atLeastOnceLowerBound);
        }
    }

    /**
     * 整表汇总（逐 pool 最小项 + 全表最小 per-roll(含 empty)）
     */
    public static final class TableMinReport {
        public final ResourceLocation tableId;
        public final List<PoolMinResult> poolResults;
        public final @Nullable PoolMinResult globalMin;

        public TableMinReport(ResourceLocation tableId, List<PoolMinResult> poolResults, @Nullable PoolMinResult globalMin) {
            this.tableId = tableId;
            this.poolResults = poolResults;
            this.globalMin = globalMin;
        }
    }

    /**
     * 主入口：对白箱分析给定 LootTable（忽略 conditions/functions）
     *
     * @param level 用于展开 TagEntry 与读取注册表
     * @param table 目标表
     * @param luck  luck×quality 修正（0 表示只看权重）
     */
    public static TableMinReport analyze(ServerLevel level, LootTable table, float luck) {
        ResourceLocation tid = getTableId(table);
        log("Analyze table id=%s luck=%.2f", tid, luck);

        List<LootPool> pools = table.pools;
        log("  pools.size=%d", pools.size());


        List<PoolMinResult> results = new ArrayList<>(pools.size());
        PoolMinResult best = null;

        for (int i = 0; i < pools.size(); i++) {
            LootPool pool = pools.get(i);
            log("  -> Pool#%d begin", i);

            WeightSummary ws = buildWeightMapIgnoringConditions(pool, luck, level);

            log("     items=%d sumItemW=%d emptyW=%d",
                ws.itemWeights.size(), ws.sumItemWeights, ws.emptyWeight);

            if (ws.sumItemWeights == 0){
                log("     [WARN] sumItemWeights=0, pool 可能全 empty 或条件本应剪枝而被忽略");
                PoolMinResult r = new PoolMinResult(i, new ItemKey(null), 0.0, 0.0, 1, 0.0);
                results.add(r);
                continue;
            }

            ItemKey argmin = null;
            double bestQAll = Double.POSITIVE_INFINITY;
            double bestQItems = 0.0;

            for (Map.Entry<ItemKey, Integer> e : ws.itemWeights.entrySet()) {
                double qItems = e.getValue() / (double) ws.sumItemWeights;
                double qAll = e.getValue() / (double) (ws.sumItemWeights + ws.emptyWeight);
                log("       cand %-40s w=%d | qItems=%.6g qAll=%.6g",
                    safeItemName(e.getKey()), e.getValue(), qItems, qAll);

                if (qAll < bestQAll){
                    bestQAll = qAll;
                    bestQItems = qItems;
                    argmin = e.getKey();
                }
            }

            int rMin = minRolls(pool);
            double pAtLeastOnceLB = 1.0 - Math.pow(1.0 - bestQAll, Math.max(1, rMin));

            PoolMinResult r = new PoolMinResult(i, argmin, bestQItems, bestQAll, rMin, pAtLeastOnceLB);
            results.add(r);
            log("     Pool#%d min => %s", i, r);

            if (best == null || r.perRollQIncludingEmpty < best.perRollQIncludingEmpty)
                best = r;
        }

        log("Global min = %s", best);
        return new TableMinReport(tid, results, best);
    }

  /* ===========================
     内部实现（带日志）
     =========================== */

    private static String safeItemName(ItemKey k) {
        if (k == null || k.item() == null)
            return "null";
        return k.item().toString();
    }

    /**
     * 展开后的权重汇总
     */
    private static final class WeightSummary {
        final Map<ItemKey, Integer> itemWeights = new LinkedHashMap<>();
        int sumItemWeights = 0;
        int emptyWeight = 0;
    }

    /**
     * 表 ID（仅用于报告；拿不到就返回 unknown:unknown）
     */
    private static ResourceLocation getTableId(LootTable table) {
        try {
            Field f = LootTable.class.getDeclaredField("lootTableId");
            f.setAccessible(true);
            Object v = f.get(table);
            if (v instanceof ResourceLocation rl)
                return rl;
            log("  [WARN] lootTableId field exists but not RL");
        }
        catch (Throwable ignored) {
            log("  [WARN] lootTableId reflect not available (ok to ignore)");
        }
        return new ResourceLocation("unknown", "unknown");
    }

    /**
     * 读取 LootPool 的 entries
     */
    @SuppressWarnings("unchecked")
    private static List<LootPoolEntryContainer> getEntries(Object pool) {
        // 若有 Mixin Accessor：return ((LootPoolAccessor)pool).fox$entries();
        try {
            Field f = pool.getClass().getDeclaredField("entries");
            f.setAccessible(true);
            Object v = f.get(pool);
            if (!(v instanceof List<?>)){
                log("     [ERR] entries not a List: %s", v);
                return List.of();
            }
            return (List<LootPoolEntryContainer>) v;
        }
        catch (ReflectiveOperationException e) {
            log("     [ERR] reflect entries failed: %s", e);
            return List.of();
        }
    }
    
    /**
     * 展开一个 pool 为 {ItemKey -> 有效权重} 与 empty 权重（忽略所有 conditions/functions）
     */
    private static WeightSummary buildWeightMapIgnoringConditions(LootPool pool, float luck, ServerLevel level) {
        WeightSummary ws = new WeightSummary();

        LootPoolEntryContainer[] entries = pool.entries;//getEntries(pool);
        log("     entries.size=%d", entries.length);

        for (LootPoolEntryContainer c : entries) {
            expandEntry(ws, c, luck, level, 0);
        }

        // 去掉 <=0 的权重并统计
        ws.itemWeights.entrySet().removeIf(e -> e.getValue() <= 0);
        ws.sumItemWeights = ws.itemWeights.values().stream().mapToInt(i -> i).sum();
        ws.emptyWeight = Math.max(0, ws.emptyWeight);

        log("     weight summary: sumItems=%d empty=%d", ws.sumItemWeights, ws.emptyWeight);
        return ws;
    }

    /**
     * 递归展开任意条目；depth 用于缩进日志
     */
    private static void expandEntry(
            WeightSummary ws, LootPoolEntryContainer c, float luck,
            ServerLevel level, int depth) {
        String pad = "       " + "  ".repeat(Math.max(0, depth));

        if (c instanceof LootItem li){
            int w = effectiveWeight(li, luck);
            Item item = getItem(li);
            log(pad + "LootItem %s wEff=%d (w=%d,q=%d,luck=%.2f)",
                item, w, getIntFieldFromSuperclass(li, "weight", 1),
                getIntFieldFromSuperclass(li, "quality", 0), luck);
            if (w > 0)
                ws.itemWeights.merge(ItemKey.of(new ItemStack(item)), w, Integer::sum);
            return;
        }

        if (c instanceof TagEntry te){
            int w = effectiveWeight(te, luck);
            log(pad + "TagEntry %s wEff=%d", getTag(te), w);
            if (w > 0){
                var reg = level.registryAccess().registryOrThrow(Registries.ITEM);
                var tag = reg.getTagOrEmpty(getTag(te));
                int cnt = 0;
                for (Holder<Item> h : tag) {
                    ws.itemWeights.merge(new ItemKey(h.value()), w, Integer::sum);
                    cnt++;
                }
                log(pad + "  -> expanded %d items from tag", cnt);
            }
            return;
        }

        if (c instanceof EmptyLootItem emp){
            int w = Math.max(0, effectiveWeight(emp, luck));
            ws.emptyWeight += w;
            log(pad + "Empty wEff=%d (accum empty=%d)", w, ws.emptyWeight);
            return;
        }

        if (c instanceof AlternativesEntry alt){
            log(pad + "Alternatives children=%d", getChildren(alt).size());
            for (LootPoolEntryContainer sub : getChildren(alt)) {
                expandEntry(ws, sub, luck, level, depth + 1);
            }
            return;
        }

        // 其它复合条目：尝试递归其 children
        List<LootPoolEntryContainer> ch = tryGetChildren(c);
        if (!ch.isEmpty()){
            log(pad + "%s children=%d", c.getClass().getSimpleName(), ch.size());
            for (LootPoolEntryContainer sub : ch) {
                expandEntry(ws, sub, luck, level, depth + 1);
            }
            return;
        }

        log(pad + "[WARN] Unhandled entry type: %s (treated as no-op)", c.getClass().getName());
    }

    /* ---- 读取权重/quality/item/tag/children（反射 or 安全 getter） ---- */

    private static int effectiveWeight(LootPoolEntryContainer container, float luck) {
        int weight = getIntFieldFromSuperclass(container, "weight", 1);   // LootPoolSingletonContainer
        int quality = getIntFieldFromSuperclass(container, "quality", 0); // LootPoolSingletonContainer
        int eff = weight + Mth.floor(quality * luck);
        return Math.max(0, eff);
    }

    private static Item getItem(LootItem li) {
        try {
            Field f = LootItem.class.getDeclaredField("item");
            f.setAccessible(true);
            return (Item) f.get(li);
        }
        catch (ReflectiveOperationException e) {
            log("       [ERR] reflect LootItem.item failed: %s", e);
            return ItemStack.EMPTY.getItem();
        }
    }

    private static net.minecraft.tags.TagKey<Item> getTag(TagEntry te) {
        try {
            Field f = TagEntry.class.getDeclaredField("tag");
            f.setAccessible(true);
            @SuppressWarnings("unchecked")
            net.minecraft.tags.TagKey<Item> tag = (net.minecraft.tags.TagKey<Item>) f.get(te);
            return tag;
        }
        catch (ReflectiveOperationException e) {
            log("       [ERR] reflect TagEntry.tag failed: %s", e);
            return net.minecraft.tags.TagKey.create(Registries.ITEM, new ResourceLocation("null:null"));
        }
    }

    private static List<LootPoolEntryContainer> getChildren(AlternativesEntry alt) {
        try {
            Field f = AlternativesEntry.class.getDeclaredField("children");
            f.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<LootPoolEntryContainer> list = (List<LootPoolEntryContainer>) f.get(alt);
            return list != null ? list : List.of();
        }
        catch (ReflectiveOperationException e) {
            log("       [ERR] reflect Alternatives.children failed: %s", e);
            return List.of();
        }
    }

    private static List<LootPoolEntryContainer> tryGetChildren(LootPoolEntryContainer c) {
        try {
            Field f = c.getClass().getDeclaredField("children");
            f.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<LootPoolEntryContainer> list = (List<LootPoolEntryContainer>) f.get(c);
            return list != null ? list : List.of();
        }
        catch (ReflectiveOperationException e) {
            return List.of();
        }
    }

    private static int getIntFieldFromSuperclass(Object obj, String name, int def) {
        Class<?> cls = obj.getClass();
        while (cls != null) {
            try {
                Field f = cls.getDeclaredField(name);
                f.setAccessible(true);
                return (int) f.get(obj);
            }
            catch (NoSuchFieldException ignored) {
                cls = cls.getSuperclass();
            }
            catch (ReflectiveOperationException e) {
                log("       [ERR] reflect int field %s failed on %s: %s", name, obj.getClass().getName(), e);
                return def;
            }
        }
        log("       [WARN] field %s not found in hierarchy of %s", name, obj.getClass().getName());
        return def;
    }

    /* ---- rolls 的 r_min 估计（打印细节） ---- */

    private static int minRolls(LootPool pool) {
        NumberProvider rolls = pool.getRolls();
        NumberProvider bonus = pool.getBonusRolls();
        int base = minOf(rolls);
        int extra = minOf(bonus);
        int r = Math.max(1, base + extra);
        log("     r_min = max(1, %d + %d) = %d", base, extra, r);
        return r;
    }

    private static int minOf(NumberProvider np) {
        if (np == null)
            return 0;
        if (np instanceof ConstantValue cv){
            int v = Math.max(0, Mth.floor(cv.getFloat(null)));
            log("       minOf Constant=%.3f -> %d", cv.getFloat(null), v);
            return v;
        }
        if (np instanceof UniformGenerator ug){
            try {
                float min = getUniformEndpoint(ug, "min");
                int v = Math.max(0, Mth.floor(min));
                log("       minOf Uniform(min=%.3f) -> %d", min, v);
                return v;
            }
            catch (Throwable t) {
                log("       [WARN] Uniform min reflect failed: %s", t);
            }
        }
        log("       [WARN] minOf unknown NumberProvider=%s -> 0", np.getClass().getName());
        return 0;
    }

    private static float getUniformEndpoint(UniformGenerator ug, String field) throws Exception {
        Field f = UniformGenerator.class.getDeclaredField(field);
        f.setAccessible(true);
        Object provider = f.get(ug); // NumberProvider
        if (provider instanceof ConstantValue cv)
            return cv.getFloat(null);
        try {
            Field fv = provider.getClass().getDeclaredField("value");
            fv.setAccessible(true);
            return (float) fv.get(provider);
        }
        catch (Throwable t) {
            log("       [WARN] Uniform endpoint reflect fallback failed: %s", t);
            return 0f;
        }
    }

  /* ===========================
     用法示例（调试）
     =========================== */

    public static void debug(ServerLevel level, LootTable table, float luck) {
        log("== DEBUG analyze start ==");
        TableMinReport report = analyze(level, table, luck);
        log("== DEBUG result ==");
        for (PoolMinResult r : report.poolResults)
            log("  " + r);
        log("== DEBUG global ==");
        log("  " + report.globalMin);
    }
}
