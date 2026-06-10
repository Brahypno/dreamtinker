package org.brahypno.dreamtinker.utils.LootHelper;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;
import java.io.Reader;
import java.util.*;
import java.util.function.Function;

import static org.brahypno.dreamtinker.utils.LootHelper.LootScanCommon.*;
import static org.brahypno.dreamtinker.utils.LootHelper.LootTableFallbackResolver.resolveLootTableCandidates;

public final class LootTableItemScanner {
    private LootTableItemScanner() {}

    public static List<ItemStack> getAllPossibleLootStacksGeneral(ServerLevel level, LivingEntity victim, Function<ResourceLocation, List<ItemStack>> stackResolver) {
        List<ItemStack> out = new ArrayList<>();

        for (ResourceLocation tableId : resolveLootTableCandidates(level, victim)) {
            List<ItemStack> stacks = stackResolver.apply(tableId);
            if (stacks != null && !stacks.isEmpty())
                out.addAll(stacks);
        }

        return out;
    }

    public static List<ItemStack> getAllScannedLootStacksMinOne(ServerLevel level, ResourceLocation lootTableId, int lootingLevel) {
        List<LootCandidate> candidates = collectLootCandidates(level, lootTableId, lootingLevel);
        List<ItemStack> stacks = new ArrayList<>();

        for (LootCandidate candidate : candidates)
            stacks.add(toMinOneStack(candidate, level.random));

        return stacks;
    }

    public static List<ItemStack> tryExtractSomeLoot(ServerLevel level, LivingEntity target, float triggerRate, int lootingLevel) {
        return tryExtractLoot(level, target, triggerRate, lootingLevel, LootScanCommon.LootRollMode.NATURAL, null, LootScanCommon::pickByNaturalRate);
    }

    public static List<ItemStack> tryExtractRareLoot(ServerLevel level, LivingEntity target, float triggerRate, int lootingLevel) {
        return tryExtractLoot(
                level,
                target,
                triggerRate,
                lootingLevel, LootRollMode.RARE,
                CandidateFilter
                        .rareByItemOrDropRate()
                        .and(CandidateFilter.estimatedRateBelow(0.30D)),
                LootScanCommon::pickByInverseRate
        );
    }

    public static List<ItemStack> tryExtractLoot(
            ServerLevel level,
            LivingEntity target,
            float triggerRate,
            int lootingLevel, LootRollMode mode,
            @Nullable CandidateFilter filter,
            LootCandidatePicker picker
    ) {
        List<LootCandidate> candidates = collectCandidatesFromPossibleTables(level, target, lootingLevel, null);
        return LootScanCommon.tryExtractLootStacks(candidates, level.random, triggerRate, lootingLevel, mode, filter, picker);
    }

    private static List<LootCandidate> collectCandidatesFromPossibleTables(
            ServerLevel level,
            LivingEntity target,
            int lootingLevel,
            @Nullable CandidateFilter filter
    ) {
        List<LootCandidate> out = new ArrayList<>();

        for (ResourceLocation tableId : resolveLootTableCandidates(level, target)) {
            List<LootCandidate> candidates = filter == null
                                             ? LootTableItemScanner.collect(level, tableId,
                                                                            LootTableItemScanner.LootScanOptions.looting(lootingLevel))
                                             : LootTableItemScanner.collect(level, tableId,
                                                                            LootTableItemScanner.LootScanOptions.looting(lootingLevel),
                                                                            filter);

            candidates.removeIf(candidate -> candidate.item() == Items.AIR);
            out.addAll(candidates);
        }

        return out;
    }

    private static List<LootCandidate> collectLootCandidates(ServerLevel level, ResourceLocation lootTableId, int lootingLevel) {
        return collectLootCandidates(level, lootTableId, lootingLevel, null);
    }

    private static List<LootCandidate> collectLootCandidates(
            ServerLevel level,
            ResourceLocation lootTableId,
            int lootingLevel,
            @Nullable CandidateFilter filter
    ) {
        List<LootCandidate> candidates = filter == null
                                         ? LootTableItemScanner.collect(level, lootTableId,
                                                                        LootTableItemScanner.LootScanOptions.looting(lootingLevel))
                                         : LootTableItemScanner.collect(level, lootTableId,
                                                                        LootTableItemScanner.LootScanOptions.looting(lootingLevel),
                                                                        filter);

        candidates.removeIf(candidate -> candidate.stack().isEmpty());
        return candidates;
    }

    private static ItemStack toMinOneStack(LootCandidate candidate, net.minecraft.util.RandomSource random) {
        ItemStack stack = candidate.getRandomCountStack(random);

        if (stack.isEmpty())
            stack = candidate.getExpectedCountStack();

        if (stack.isEmpty())
            stack = new ItemStack(candidate.item());

        stack.setCount(Math.max(1, Math.min(stack.getCount(), stack.getMaxStackSize())));
        return stack;
    }

    public static List<LootCandidate> collect(ServerLevel level, ResourceLocation lootTableId) {
        return collect(level, lootTableId, LootScanOptions.none());
    }

    public static List<LootCandidate> collect(ServerLevel level, ResourceLocation lootTableId, LootScanOptions options) {
        List<LootCandidate> out = new ArrayList<>();
        scanTable(level, lootTableId, ScanContext.root(), out, new HashSet<>(), options);
        return out;
    }

    public static List<LootCandidate> collect(ServerLevel level, ResourceLocation lootTableId, CandidateFilter filter) {
        return collect(level, lootTableId, LootScanOptions.none(), filter);
    }

    public static List<LootCandidate> collect(ServerLevel level, ResourceLocation lootTableId, LootScanOptions options, CandidateFilter filter) {
        List<LootCandidate> out = new ArrayList<>();
        for (LootCandidate candidate : collect(level, lootTableId, options)) {
            if (filter.test(candidate)){
                out.add(candidate);
            }
        }
        return out;
    }

    private static void scanTable(ServerLevel level, ResourceLocation lootTableId, ScanContext context, List<LootCandidate> out, Set<ResourceLocation> stack, LootScanOptions options) {
        if (!stack.add(lootTableId)){
            return;
        }

        JsonObject table = readLootTableJson(level, lootTableId);
        if (table == null){
            stack.remove(lootTableId);
            return;
        }

        JsonArray pools = getArray(table, "pools");
        if (pools == null){
            stack.remove(lootTableId);
            return;
        }

        for (int poolIndex = 0; poolIndex < pools.size(); poolIndex++) {
            JsonElement poolElement = pools.get(poolIndex);
            if (!poolElement.isJsonObject()){
                continue;
            }

            JsonObject pool = poolElement.getAsJsonObject();
            JsonArray entries = getArray(pool, "entries");
            if (entries == null || entries.isEmpty()){
                continue;
            }

            ScanContext poolContext = context.withOwner(pool, options);

            double rolls = averageNumberProvider(pool.get("rolls"), 1.0D)
                           + averageNumberProvider(pool.get("bonus_rolls"), 0.0D);

            rolls = Math.max(0.0D, rolls);

            double totalWeight = 0.0D;
            for (JsonElement entryElement : entries) {
                if (entryElement.isJsonObject()){
                    totalWeight += effectiveEntryWeight(level, entryElement.getAsJsonObject());
                }
            }

            if (totalWeight <= 0.0D){
                continue;
            }

            for (int entryIndex = 0; entryIndex < entries.size(); entryIndex++) {
                JsonElement entryElement = entries.get(entryIndex);
                if (!entryElement.isJsonObject()){
                    continue;
                }

                JsonObject entry = entryElement.getAsJsonObject();
                double weight = effectiveEntryWeight(level, entry);
                double localSelectRate = atLeastOnceRate(weight / totalWeight, rolls);

                scanEntry(
                        level,
                        lootTableId,
                        entry,
                        poolContext,
                        localSelectRate,
                        out,
                        stack,
                        lootTableId + "#pools[" + poolIndex + "].entries[" + entryIndex + "]",
                        totalWeight,
                        rolls,
                        options
                );
            }
        }

        stack.remove(lootTableId);
    }

    private static void scanEntry(
            ServerLevel level, ResourceLocation currentTableId, JsonObject entry, ScanContext parentContext, double entrySelectRate,
            List<LootCandidate> out, Set<ResourceLocation> stack, String path, double poolTotalWeight, double poolRolls, LootScanOptions options) {
        String type = normalizedType(getString(entry, "type", ""));

        ScanContext selectedContext = parentContext.withReachRate(parentContext.reachRate * entrySelectRate);
        ScanContext entryContext = selectedContext.withOwner(entry, options);

        switch (type) {
            case "item" -> {
                ResourceLocation itemId = readResourceLocation(entry, "name");
                if (itemId == null){
                    return;
                }

                Optional<Item> item = BuiltInRegistries.ITEM.getOptional(itemId);
                if (item.isEmpty()){
                    return;
                }

                out.add(LootCandidate.of(
                        SourceType.LOOT_TABLE,
                        currentTableId,
                        item.get().getDefaultInstance(),
                        path,
                        entryContext.reachRate,
                        entryContext.conditionRate,
                        entryContext.countRange,
                        entryContext.conditionTypes,
                        entryContext.functionTypes,
                        "loot_table"
                ));
                return;
            }
            case "tag" -> {
                ResourceLocation tagId = readTagLocation(entry, "name");
                if (tagId == null){
                    return;
                }

                TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tagId);
                List<Item> items = new ArrayList<>();
                for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(tagKey)) {
                    items.add(holder.value());
                }

                if (items.isEmpty()){
                    return;
                }

                boolean expand = getBoolean(entry, "expand", false);

                for (Item item : items) {
                    double itemReachRate = entryContext.reachRate;

                    if (expand && poolTotalWeight > 0.0D){
                        double weight = rawEntryWeight(entry);
                        double itemSelectRate = atLeastOnceRate(weight / poolTotalWeight, poolRolls);
                        itemReachRate = parentContext.reachRate * itemSelectRate;
                    }

                    out.add(LootCandidate.of(
                            SourceType.LOOT_TABLE,
                            currentTableId,
                            item.getDefaultInstance(),
                            path + ".tag[" + BuiltInRegistries.ITEM.getKey(item) + "]",
                            itemReachRate,
                            entryContext.conditionRate,
                            entryContext.countRange,
                            entryContext.conditionTypes,
                            entryContext.functionTypes,
                            "loot_table"
                    ));
                }
                return;
            }
            case "loot_table" -> {
                ResourceLocation ref = readResourceLocation(entry, "name");
                if (ref != null){
                    scanTable(level, ref, entryContext, out, stack, options);
                }
                return;
            }
        }

        JsonArray children = getArray(entry, "children");
        if (children != null){
            for (int i = 0; i < children.size(); i++) {
                JsonElement childElement = children.get(i);
                if (childElement.isJsonObject()){
                    scanEntry(
                            level,
                            currentTableId,
                            childElement.getAsJsonObject(),
                            entryContext,
                            1.0D,
                            out,
                            stack,
                            path + ".children[" + i + "]",
                            0.0D,
                            1.0D,
                            options
                    );
                }
            }
        }
    }

    private static JsonObject readLootTableJson(ServerLevel level, ResourceLocation lootTableId) {
        ResourceLocation jsonId = new ResourceLocation(
                lootTableId.getNamespace(),
                "loot_tables/" + lootTableId.getPath() + ".json"
        );

        Optional<Resource> resource = level.getServer().getResourceManager().getResource(jsonId);
        if (resource.isEmpty()){
            return null;
        }

        try (Reader reader = resource.get().openAsReader()) {
            return GsonHelper.parse(reader);
        }
        catch (Exception e) {
            return null;
        }
    }

    private static double effectiveEntryWeight(ServerLevel level, JsonObject entry) {
        double weight = rawEntryWeight(entry);
        String type = normalizedType(getString(entry, "type", ""));

        if ("tag".equals(type) && getBoolean(entry, "expand", false)){
            ResourceLocation tagId = readTagLocation(entry, "name");
            if (tagId != null){
                int size = 0;
                TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tagId);
                for (Holder<Item> ignored : BuiltInRegistries.ITEM.getTagOrEmpty(tagKey)) {
                    size++;
                }
                return Math.max(0.0D, weight * Math.max(1, size));
            }
        }

        return Math.max(0.0D, weight);
    }

    private static double rawEntryWeight(JsonObject entry) {
        return Math.max(0.0D, getDouble(entry, "weight", 1.0D));
    }

    private static double atLeastOnceRate(double perRollRate, double rolls) {
        perRollRate = clamp(perRollRate, 0.0D, 1.0D);
        if (rolls <= 0.0D){
            return 0.0D;
        }
        return 1.0D - Math.pow(1.0D - perRollRate, rolls);
    }

    private static OwnerInfo readOwnerInfo(JsonObject owner, LootScanOptions options) {
        OwnerInfo info = new OwnerInfo();

        JsonArray conditions = getArray(owner, "conditions");
        if (conditions != null){
            for (JsonElement condition : conditions) {
                info.conditionRate *= readConditionChance(condition, info.conditionTypes, options);
            }
        }

        JsonArray functions = getArray(owner, "functions");
        if (functions != null){
            for (JsonElement fnElement : functions) {
                if (!fnElement.isJsonObject()){
                    continue;
                }

                JsonObject fn = fnElement.getAsJsonObject();
                String functionType = normalizedType(getString(fn, "function", ""));
                if (!functionType.isEmpty()){
                    info.functionTypes.add(functionType);
                }

                JsonArray functionConditions = getArray(fn, "conditions");
                if (functionConditions != null){
                    for (JsonElement condition : functionConditions) {
                        info.conditionRate *= readConditionChance(condition, info.conditionTypes, options);
                    }
                }

                if ("set_count".equals(functionType)){
                    CountRange count = readCountRange(fn.get("count"), CountRange.one());
                    boolean add = getBoolean(fn, "add", false);

                    info.setCount = count;
                    info.addCount = add;
                }

                if ("looting_enchant".equals(functionType)){
                    CountRange bonusPerLevel = readCountRange(fn.get("count"), CountRange.zero());
                    CountRange totalBonus = bonusPerLevel.multiply(options.lootingLevel());
                    info.lootingBonusCount = info.lootingBonusCount.add(totalBonus);

                    if (fn.has("limit") && fn.get("limit").isJsonPrimitive() && fn.get("limit").getAsJsonPrimitive().isNumber()){
                        int limit = Math.max(0, fn.get("limit").getAsInt());
                        info.limitMax = info.limitMax == null ? limit : Math.min(info.limitMax, limit);
                    }
                }

                if ("limit_count".equals(functionType)){
                    readLimitCount(fn, info);
                }
            }
        }

        return info;
    }

    private static void readLimitCount(JsonObject fn, OwnerInfo info) {
        JsonElement limitElement = fn.get("limit");
        if (limitElement == null || !limitElement.isJsonObject()){
            return;
        }

        JsonObject limit = limitElement.getAsJsonObject();

        if (limit.has("min") && limit.get("min").isJsonPrimitive() && limit.get("min").getAsJsonPrimitive().isNumber()){
            info.limitMin = Math.max(0, limit.get("min").getAsInt());
        }

        if (limit.has("max") && limit.get("max").isJsonPrimitive() && limit.get("max").getAsJsonPrimitive().isNumber()){
            info.limitMax = Math.max(0, limit.get("max").getAsInt());
        }
    }

    private static double readConditionChance(JsonElement element, List<String> conditionTypes, LootScanOptions options) {
        double knownRate = readKnownConditionChance(element, conditionTypes, options);
        return Double.isNaN(knownRate) ? 1.0D : clamp(knownRate, 0.0D, 1.0D);
    }

    private static double readKnownConditionChance(JsonElement element, List<String> conditionTypes, LootScanOptions options) {
        if (element == null || !element.isJsonObject()){
            return Double.NaN;
        }

        JsonObject condition = element.getAsJsonObject();
        String type = normalizedType(getString(condition, "condition", ""));

        if (!type.isEmpty()){
            conditionTypes.add(type);
        }

        switch (type) {
            case "random_chance" -> {
                return clamp(getDouble(condition, "chance", 1.0D), 0.0D, 1.0D);
            }
            case "random_chance_with_looting" -> {
                double chance = getDouble(condition, "chance", 1.0D);
                double lootingMultiplier = getDouble(condition, "looting_multiplier", 0.0D);
                return clamp(chance + lootingMultiplier * options.lootingLevel(), 0.0D, 1.0D);
            }
            case "all_of" -> {
                JsonArray terms = getArray(condition, "terms");
                if (terms == null || terms.isEmpty()){
                    return Double.NaN;
                }

                boolean hasKnown = false;
                double rate = 1.0D;

                for (JsonElement term : terms) {
                    double termRate = readKnownConditionChance(term, conditionTypes, options);
                    if (!Double.isNaN(termRate)){
                        hasKnown = true;
                        rate *= termRate;
                    }
                }

                return hasKnown ? clamp(rate, 0.0D, 1.0D) : Double.NaN;
            }
            case "any_of" -> {
                JsonArray terms = getArray(condition, "terms");
                if (terms == null || terms.isEmpty()){
                    return Double.NaN;
                }

                boolean hasKnown = false;
                double missRate = 1.0D;

                for (JsonElement term : terms) {
                    double termRate = readKnownConditionChance(term, conditionTypes, options);
                    if (!Double.isNaN(termRate)){
                        hasKnown = true;
                        missRate *= 1.0D - clamp(termRate, 0.0D, 1.0D);
                    }
                }

                return hasKnown ? clamp(1.0D - missRate, 0.0D, 1.0D) : Double.NaN;
            }
            case "inverted" -> {
                double inner = readKnownConditionChance(condition.get("term"), conditionTypes, options);
                return Double.isNaN(inner) ? Double.NaN : clamp(1.0D - inner, 0.0D, 1.0D);
            }
        }

        return Double.NaN;
    }

    private static ResourceLocation readResourceLocation(JsonObject obj, String key) {
        if (!obj.has(key) || !obj.get(key).isJsonPrimitive()){
            return null;
        }
        return ResourceLocation.tryParse(obj.get(key).getAsString());
    }

    private static ResourceLocation readTagLocation(JsonObject obj, String key) {
        if (!obj.has(key) || !obj.get(key).isJsonPrimitive()){
            return null;
        }

        String raw = obj.get(key).getAsString();
        if (raw.startsWith("#")){
            raw = raw.substring(1);
        }

        return ResourceLocation.tryParse(raw);
    }

    private static JsonArray getArray(JsonObject obj, String key) {
        if (obj == null || !obj.has(key) || !obj.get(key).isJsonArray()){
            return null;
        }
        return obj.getAsJsonArray(key);
    }

    public record LootScanOptions(int lootingLevel) {
        public LootScanOptions {
            lootingLevel = Math.max(0, lootingLevel);
        }

        public static LootScanOptions none() {
            return new LootScanOptions(0);
        }

        public static LootScanOptions looting(int lootingLevel) {
            return new LootScanOptions(lootingLevel);
        }
    }

    private record ScanContext(double reachRate, double conditionRate, CountRange countRange, List<String> conditionTypes, List<String> functionTypes) {

        static ScanContext root() {
            return new ScanContext(1.0D, 1.0D, CountRange.one(), new ArrayList<>(), new ArrayList<>());
        }

        ScanContext withReachRate(double reachRate) {
            return new ScanContext(reachRate, conditionRate, countRange, new ArrayList<>(conditionTypes), new ArrayList<>(functionTypes));
        }

        ScanContext withOwner(JsonObject owner, LootScanOptions options) {
            OwnerInfo info = readOwnerInfo(owner, options);

            List<String> newConditions = new ArrayList<>(conditionTypes);
            newConditions.addAll(info.conditionTypes);

            List<String> newFunctions = new ArrayList<>(functionTypes);
            newFunctions.addAll(info.functionTypes);

            return new ScanContext(
                    reachRate,
                    conditionRate * info.conditionRate,
                    info.applyCount(countRange),
                    newConditions,
                    newFunctions
            );
        }
    }

    private static final class OwnerInfo {
        final List<String> conditionTypes = new ArrayList<>();
        final List<String> functionTypes = new ArrayList<>();
        double conditionRate = 1.0D;
        CountRange setCount = null;
        boolean addCount = false;
        CountRange lootingBonusCount = CountRange.zero();
        Integer limitMin = null;
        Integer limitMax = null;

        CountRange applyCount(CountRange current) {
            CountRange result = current;

            if (setCount != null){
                result = addCount ? result.add(setCount) : result.set(setCount);
            }

            result = result.add(lootingBonusCount);

            if (limitMin != null || limitMax != null){
                int min = limitMin != null ? limitMin : 0;
                int max = limitMax != null ? limitMax : Integer.MAX_VALUE;
                result = result.limit(min, max);
            }

            return result;
        }
    }
}