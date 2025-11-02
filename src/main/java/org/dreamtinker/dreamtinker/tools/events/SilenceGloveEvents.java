package org.dreamtinker.dreamtinker.tools.events;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.tools.items.SilenceGlove;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

public class SilenceGloveEvents {
    private static final ThreadLocal<Boolean> REENTRY = ThreadLocal.withInitial(() -> Boolean.FALSE);

    // —— 挖掘期临时状态 —— //
    private static final String KEY_ACTIVE = "tp_active";
    private static final String KEY_ACTIVE_SLOT = "tp_active_slot";
    private static final String KEY_PROXY_NBT = "tp_proxy_nbt";
    private static final String KEY_LAST_POS = "tp_last_pos";
    private static final String KEY_TIMEOUT = "tp_timeout";
    private static final int CANCEL_GRACE_TICKS = 10;

    private static CompoundTag pdata(Player p) {return p.getPersistentData();}

    private static boolean isActive(Player p) {return pdata(p).getBoolean(KEY_ACTIVE);}

    private static void clearActive(Player p) {
        CompoundTag t = pdata(p);
        t.remove(KEY_ACTIVE);
        t.remove(KEY_ACTIVE_SLOT);
        t.remove(KEY_PROXY_NBT);
        t.remove(KEY_LAST_POS);
        t.remove(KEY_TIMEOUT);
    }

    /* ========== 右键空气/物品：随机 use ========== */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (Boolean.TRUE.equals(REENTRY.get()))
            return;
        Player player = event.getEntity();
        if (player == null)
            return;

        InteractionHand hand = event.getHand();
        ItemStack proxy = player.getMainHandItem();
        if (!(proxy.getItem() instanceof SilenceGlove))
            return;
        ToolStack silenceGlove = ToolStack.from(proxy);
        ModifierEntry entry = silenceGlove.getModifier(DreamtinkerModifiers.Ids.weapon_slots);
        if (entry.getLevel() < 1)
            return;
        ItemStack chosen;

        List<ItemStack> itemFrames = new ArrayList<>();
        entry.getHook(ToolInventoryCapability.HOOK).getAllStacks(silenceGlove, entry, itemFrames);
        OptionalInt idxOpt = pickNonAirIndex(itemFrames, RandomSource.create());
        int slot;
        if (idxOpt.isPresent()){
            slot = idxOpt.getAsInt();
            chosen = itemFrames.get(slot);
        }else
            return;

        event.setCanceled(true);

        ItemStack proxySnap = proxy.copy();
        try {
            REENTRY.set(true);
            player.setItemInHand(hand, chosen.copy());
            player.getInventory().setChanged();

            player.getMainHandItem().use(player.level(), player, hand);

            ItemStack after = player.getMainHandItem().copy();
            entry.getHook(ToolInventoryCapability.HOOK).setStack(silenceGlove, entry, slot, after);
        }
        finally {
            player.setItemInHand(hand, proxySnap);
            player.getInventory().setChanged();
            REENTRY.set(false);
        }
    }

    /* ========== 右键方块：随机 useOn ========== */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (Boolean.TRUE.equals(REENTRY.get()))
            return;
        Player player = event.getEntity();
        if (player == null)
            return;

        InteractionHand hand = event.getHand();
        ItemStack proxy = player.getMainHandItem();
        if (!(proxy.getItem() instanceof SilenceGlove))
            return;
        ToolStack silenceGlove = ToolStack.from(proxy);
        ModifierEntry entry = silenceGlove.getModifier(DreamtinkerModifiers.Ids.weapon_slots);
        if (entry.getLevel() < 1)
            return;
        ItemStack chosen;

        List<ItemStack> itemFrames = new ArrayList<>();
        entry.getHook(ToolInventoryCapability.HOOK).getAllStacks(silenceGlove, entry, itemFrames);
        OptionalInt idxOpt = pickNonAirIndex(itemFrames, RandomSource.create());
        int slot;
        if (idxOpt.isPresent()){
            slot = idxOpt.getAsInt();
            chosen = itemFrames.get(slot);
        }else
            return;

        event.setCanceled(true);

        ItemStack proxySnap = proxy.copy();
        try {
            REENTRY.set(true);
            player.setItemInHand(hand, chosen.copy());
            player.getInventory().setChanged();

            var ctx = new UseOnContext(player, hand, event.getHitVec());
            player.getMainHandItem().useOn(ctx);

            ItemStack after = player.getMainHandItem().copy();
            entry.getHook(ToolInventoryCapability.HOOK).setStack(silenceGlove, entry, slot, after);
        }
        finally {
            player.setItemInHand(hand, proxySnap);
            player.getInventory().setChanged();
            REENTRY.set(false);
        }
    }

    /* ========== 左键挖掘：期间主手临时为内部工具，破坏成功后写回并还原 ========== */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (Boolean.TRUE.equals(REENTRY.get()))
            return;
        Player player = event.getEntity();
        if (player == null)
            return;

        ItemStack proxy = player.getMainHandItem();
        if (!(proxy.getItem() instanceof SilenceGlove))
            return;
        ToolStack silenceGlove = ToolStack.from(proxy);
        ModifierEntry entry = silenceGlove.getModifier(DreamtinkerModifiers.Ids.weapon_slots);
        if (entry.getLevel() < 1)
            return;
        ItemStack chosen;

        List<ItemStack> itemFrames = new ArrayList<>();
        entry.getHook(ToolInventoryCapability.HOOK).getAllStacks(silenceGlove, entry, itemFrames);
        OptionalInt idxOpt = pickNonAirIndex(itemFrames, RandomSource.create());
        int slot;
        if (idxOpt.isPresent()){
            slot = idxOpt.getAsInt();
            chosen = itemFrames.get(slot);
        }else
            return;

        // 记录快照 + 状态
        CompoundTag snap = new CompoundTag();
        proxy.save(snap);
        CompoundTag pd = pdata(player);
        pd.putBoolean(KEY_ACTIVE, true);
        pd.putInt(KEY_ACTIVE_SLOT, slot);
        pd.put(KEY_PROXY_NBT, snap);
        pd.putLong(KEY_LAST_POS, event.getPos().asLong());
        pd.putInt(KEY_TIMEOUT, 0);

        try {
            REENTRY.set(true);
            player.setItemInHand(InteractionHand.MAIN_HAND, chosen.copy());
            player.getInventory().setChanged();
            // 放行：让原版开始破坏
        }
        finally {
            REENTRY.set(false);
        }
    }

    /* ========== 破坏成功：写回并还原 ========== */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player p = event.getPlayer();
        if (p == null || !isActive(p))
            return;
        if (pdata(p).getLong(KEY_LAST_POS) != event.getPos().asLong())
            return;
        // restoreIfHoldingProxySnapshot(p, true);
    }

    /* ========== Tick：取消监控 ========== */
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;
        Player p = event.player;
        if (!isActive(p))
            return;

        // 如果主手又变回 Proxy（玩家手动切了），直接清理
        if (!p.getMainHandItem().isEmpty() && p.getMainHandItem().getItem() instanceof SilenceGlove){
            clearActive(p);
            return;
        }

        int t = pdata(p).getInt(KEY_TIMEOUT) + 1;
        if (t >= CANCEL_GRACE_TICKS){
            // restoreIfHoldingProxySnapshot(p, false); // 未破坏成功不强制写回
        }else {
            pdata(p).putInt(KEY_TIMEOUT, t);
        }
    }


    private static OptionalInt pickNonAirIndex(List<ItemStack> stacks, RandomSource random) {
        int chosen = -1;
        int seen = 0; // 已遇到的非空数量
        for (int i = 0; i < stacks.size(); i++) {
            ItemStack s = stacks.get(i);
            if (!s.isEmpty()){ // 等价于“不是空气/空堆”
                seen++;
                // 以 1/seen 的概率替换当前选择
                if (random.nextInt(seen) == 0){
                    chosen = i;
                }
            }
        }
        return chosen >= 0 ? OptionalInt.of(chosen) : OptionalInt.empty();
    }
}
