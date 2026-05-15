package org.dreamtinker.dreamtinker.utils.LootHelper;

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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;

import java.io.Reader;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.dreamtinker.dreamtinker.utils.LootHelper.LootTableFallbackResolver.resolveLootTableCandidates;

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
        List<LootTableItemScanner.LootCandidate> candidates =
                LootTableItemScanner.collect(
                        level,
                        lootTableId,
                        LootTableItemScanner.LootScanOptions.looting(lootingLevel)
                );

        List<ItemStack> stacks = new ArrayList<>();

        for (LootTableItemScanner.LootCandidate candidate : candidates) {
            ItemStack stack = candidate.getRandomCountStack(level.random);

            if (stack.isEmpty()){
                stack = candidate.getExpectedCountStack();
            }

            if (stack.isEmpty()){
                stack = new ItemStack(candidate.item());
            }

            stack.setCount(Math.max(1, Math.min(stack.getCount(), stack.getMaxStackSize())));
            stacks.add(stack);
        }

        return stacks;
    }

    public static List<ItemStack> tryExtractSomeLoot(ServerLevel level, LivingEntity target, float triggerRate, int lootingLevel) {
        List<ItemStack> out = new ArrayList<>();

        List<LootTableItemScanner.LootCandidate> candidates =
                LootTableItemScanner.collect(
                        level,
                        target.getLootTable(),
                        LootTableItemScanner.LootScanOptions.looting(lootingLevel)
                );

        candidates.removeIf(candidate -> candidate.item() == Items.AIR);

        if (candidates.isEmpty())
            return out;

        int maxRolls = 1 + Math.max(0, lootingLevel);

        for (int roll = 0; roll < maxRolls; roll++) {
            float rate = roll == 0 ? 1.0F : triggerRate / roll;

            if (level.random.nextFloat() >= rate)
                break;

            ItemStack stack = ItemStack.EMPTY;

            for (int i = 0; i < candidates.size() * 2 && stack.isEmpty(); i++) {
                LootTableItemScanner.LootCandidate candidate = pickByInverseRate(candidates, level.random);
                stack = candidate.getRandomCountStack(level.random);
            }

            if (!stack.isEmpty())
                out.add(stack);
        }

        return out;
    }

    public static List<ItemStack> tryExtractRareLoot(ServerLevel level, LivingEntity target, float triggerRate, int lootingLevel) {
        List<ItemStack> out = new ArrayList<>();

        List<LootTableItemScanner.LootCandidate> candidates =
                LootTableItemScanner.collect(
                        level,
                        target.getLootTable(),
                        LootTableItemScanner.LootScanOptions.looting(lootingLevel),
                        LootTableItemScanner.CandidateFilter
                                .rareByItemOrDropRate()
                                .and(LootTableItemScanner.CandidateFilter.estimatedRateBelow(0.25D))
                );

        candidates.removeIf(candidate -> candidate.item() == Items.AIR);

        if (candidates.isEmpty())
            return out;

        int maxRolls = 1 + Math.max(0, lootingLevel);

        for (int roll = 0; roll < maxRolls; roll++) {
            float rate = roll == 0 ? 1.0F : triggerRate / roll;

            if (level.random.nextFloat() >= rate)
                break;

            ItemStack stack = ItemStack.EMPTY;

            for (int i = 0; i < candidates.size() * 2 && stack.isEmpty(); i++) {
                LootTableItemScanner.LootCandidate candidate = pickByInverseRate(candidates, level.random);
                stack = candidate.getRandomCountStack(level.random);
            }

            if (!stack.isEmpty())
                out.add(stack);
        }

        return out;
    }

    private static LootTableItemScanner.LootCandidate pickByInverseRate(List<LootTableItemScanner.LootCandidate> candidates, RandomSource random) {
        double totalWeight = 0.0D;

        for (LootTableItemScanner.LootCandidate candidate : candidates) {
            double rate = Math.max(0.001D, candidate.estimatedRate());
            totalWeight += 1.0D / rate;
        }

        double roll = random.nextDouble() * totalWeight;

        for (LootTableItemScanner.LootCandidate candidate : candidates) {
            double rate = Math.max(0.001D, candidate.estimatedRate());
            roll -= 1.0D / rate;

            if (roll <= 0.0D)
                return candidate;
        }

        return candidates.get(candidates.size() - 1);
    }

    public static List<LootCandidate> collect(ServerLevel level, ResourceLocation lootTableId) {
        return collect(level, lootTableId, LootScanOptions.none());
    }

    public static List<LootCandidate> collect(ServerLevel level, ResourceLocation lootTableId, LootScanOptions options) {
        List<LootCandidate> out = new ArrayList<>();
        scanTable(level, lootTableId, ScanContext.root(), out, new HashSet<>(), options);
        return out;
    }

    public static Set<Item> collectItems(ServerLevel level, ResourceLocation lootTableId) {
        return collectItems(level, lootTableId, LootScanOptions.none());
    }

    public static Set<Item> collectItems(ServerLevel level, ResourceLocation lootTableId, LootScanOptions options) {
        Set<Item> out = new LinkedHashSet<>();
        for (LootCandidate candidate : collect(level, lootTableId, options)) {
            out.add(candidate.item());
        }
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
                        currentTableId,
                        item.get(),
                        path,
                        entryContext.reachRate,
                        entryContext.conditionRate,
                        entryContext.countRange,
                        entryContext.conditionTypes,
                        entryContext.functionTypes
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
                            currentTableId,
                            item,
                            path + ".tag[" + BuiltInRegistries.ITEM.getKey(item) + "]",
                            itemReachRate,
                            entryContext.conditionRate,
                            entryContext.countRange,
                            entryContext.conditionTypes,
                            entryContext.functionTypes
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

    private static double averageNumberProvider(JsonElement element, double fallback) {
        if (element == null || element.isJsonNull()){
            return fallback;
        }

        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()){
            return element.getAsDouble();
        }

        if (!element.isJsonObject()){
            return fallback;
        }

        JsonObject obj = element.getAsJsonObject();
        String type = normalizedType(getString(obj, "type", "constant"));

        switch (type) {
            case "constant" -> {
                return getDouble(obj, "value", fallback);
            }
            case "uniform" -> {
                double min = averageNumberProvider(obj.get("min"), fallback);
                double max = averageNumberProvider(obj.get("max"), fallback);
                return (min + max) * 0.5D;
            }
            case "binomial" -> {
                double n = averageNumberProvider(obj.get("n"), 0.0D);
                double p = averageNumberProvider(obj.get("p"), 0.0D);
                return n * p;
            }
        }

        return fallback;
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

    private static CountRange readCountRange(JsonElement element, CountRange fallback) {
        if (element == null || element.isJsonNull()){
            return fallback;
        }

        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()){
            int value = Math.max(0, element.getAsInt());
            return new CountRange(value, value, value, value > 0 ? 1.0D : 0.0D);
        }

        if (!element.isJsonObject()){
            return fallback;
        }

        JsonObject obj = element.getAsJsonObject();
        String type = normalizedType(getString(obj, "type", "constant"));

        switch (type) {
            case "constant" -> {
                int value = Math.max(0, (int) Math.floor(getDouble(obj, "value", fallback.expected())));
                return new CountRange(value, value, value, value > 0 ? 1.0D : 0.0D);
            }
            case "uniform" -> {
                double minRaw = averageNumberProvider(obj.get("min"), fallback.min());
                double maxRaw = averageNumberProvider(obj.get("max"), fallback.max());

                int min = Math.max(0, (int) Math.floor(Math.min(minRaw, maxRaw)));
                int max = Math.max(min, (int) Math.floor(Math.max(minRaw, maxRaw)));
                double expected = Math.max(0.0D, (minRaw + maxRaw) * 0.5D);

                return new CountRange(min, max, expected, uniformNonEmptyChance(min, max));
            }
            case "binomial" -> {
                double nRaw = averageNumberProvider(obj.get("n"), 0.0D);
                double pRaw = averageNumberProvider(obj.get("p"), 0.0D);

                int n = Math.max(0, (int) Math.floor(nRaw));
                double p = clamp(pRaw, 0.0D, 1.0D);
                double nonEmptyChance = n <= 0 ? 0.0D : 1.0D - Math.pow(1.0D - p, n);

                return new CountRange(0, n, n * p, nonEmptyChance);
            }
        }

        return fallback;
    }

    private static double uniformNonEmptyChance(int min, int max) {
        if (max <= 0){
            return 0.0D;
        }

        if (min > 0){
            return 1.0D;
        }

        int total = max - min + 1;
        int positive = max - Math.max(min, 1) + 1;

        if (total <= 0 || positive <= 0){
            return 0.0D;
        }

        return clamp((double) positive / (double) total, 0.0D, 1.0D);
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

        if ("random_chance".equals(type)){
            return clamp(getDouble(condition, "chance", 1.0D), 0.0D, 1.0D);
        }

        if ("random_chance_with_looting".equals(type)){
            double chance = getDouble(condition, "chance", 1.0D);
            double lootingMultiplier = getDouble(condition, "looting_multiplier", 0.0D);
            return clamp(chance + lootingMultiplier * options.lootingLevel(), 0.0D, 1.0D);
        }

        if ("all_of".equals(type)){
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

        if ("any_of".equals(type)){
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

        if ("inverted".equals(type)){
            double inner = readKnownConditionChance(condition.get("term"), conditionTypes, options);
            return Double.isNaN(inner) ? Double.NaN : clamp(1.0D - inner, 0.0D, 1.0D);
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

    private static String getString(JsonObject obj, String key, String fallback) {
        if (obj == null || !obj.has(key) || !obj.get(key).isJsonPrimitive()){
            return fallback;
        }
        return obj.get(key).getAsString();
    }

    private static boolean getBoolean(JsonObject obj, String key, boolean fallback) {
        if (obj == null || !obj.has(key) || !obj.get(key).isJsonPrimitive()){
            return fallback;
        }
        return obj.get(key).getAsBoolean();
    }

    private static double getDouble(JsonObject obj, String key, double fallback) {
        if (obj == null || !obj.has(key) || !obj.get(key).isJsonPrimitive() || !obj.get(key).getAsJsonPrimitive().isNumber()){
            return fallback;
        }
        return obj.get(key).getAsDouble();
    }

    private static String normalizedType(String id) {
        if (id == null || id.isEmpty()){
            return "";
        }

        ResourceLocation location = ResourceLocation.tryParse(id);
        if (location == null){
            return id;
        }

        return location.getPath();
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public enum DropRateRarity {
        GUARANTEED(0),
        COMMON(1),
        UNCOMMON(2),
        RARE(3),
        VERY_RARE(4),
        ULTRA_RARE(5),
        UNKNOWN(-1);

        private final int rarityScore;

        DropRateRarity(int rarityScore) {
            this.rarityScore = rarityScore;
        }

        public static DropRateRarity fromRate(double rate) {
            if (Double.isNaN(rate)){
                return UNKNOWN;
            }

            if (rate >= 0.999D){
                return GUARANTEED;
            }
            if (rate >= 0.25D){
                return COMMON;
            }
            if (rate >= 0.10D){
                return UNCOMMON;
            }
            if (rate >= 0.025D){
                return RARE;
            }
            if (rate >= 0.005D){
                return VERY_RARE;
            }
            return ULTRA_RARE;
        }

        public boolean atLeast(DropRateRarity threshold) {
            return rarityScore >= threshold.rarityScore && rarityScore >= 0;
        }
    }

    @FunctionalInterface
    public interface CandidateFilter {
        static CandidateFilter item(Predicate<Item> predicate) {
            return candidate -> predicate.test(candidate.item());
        }

        static CandidateFilter itemId(Predicate<ResourceLocation> predicate) {
            return candidate -> predicate.test(candidate.itemId());
        }

        static CandidateFilter itemRarityAtLeast(Rarity rarity) {
            int threshold = itemRarityScore(rarity);
            return candidate -> itemRarityScore(candidate.itemRarity()) >= threshold;
        }

        static CandidateFilter dropRateRarityAtLeast(DropRateRarity rarity) {
            return candidate -> candidate.dropRateRarity().atLeast(rarity);
        }

        static CandidateFilter estimatedRateBelow(double maxRate) {
            return candidate -> candidate.estimatedRate() <= maxRate;
        }

        static CandidateFilter estimatedRateAtLeast(double minRate) {
            return candidate -> candidate.estimatedRate() >= minRate;
        }

        static CandidateFilter hasCondition(String key) {
            return candidate -> candidate.hasCondition(key);
        }

        static CandidateFilter hasExactCondition(String key) {
            return candidate -> candidate.hasExactCondition(key);
        }

        static CandidateFilter hasFunction(String key) {
            return candidate -> candidate.hasFunction(key);
        }

        static CandidateFilter hasExactFunction(String key) {
            return candidate -> candidate.hasExactFunction(key);
        }

        static CandidateFilter rareByItemOrDropRate() {
            return itemRarityAtLeast(Rarity.RARE)
                    .or(dropRateRarityAtLeast(DropRateRarity.RARE))
                    .or(hasCondition("random_chance"))
                    .or(hasCondition("killed"))
                    .or(hasCondition("match_tool"))
                    .or(hasCondition("damage_source_properties"))
                    .or(hasCondition("entity_properties"));
        }

        private static int itemRarityScore(Rarity rarity) {
            return switch (rarity) {
                case COMMON -> 0;
                case UNCOMMON -> 1;
                case RARE -> 2;
                case EPIC -> 3;
            };
        }

        boolean test(LootCandidate candidate);

        default CandidateFilter and(CandidateFilter other) {
            return candidate -> test(candidate) && other.test(candidate);
        }

        default CandidateFilter or(CandidateFilter other) {
            return candidate -> test(candidate) || other.test(candidate);
        }
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

    public record LootCandidate(
            ResourceLocation tableId,
            Item item,
            ResourceLocation itemId,
            Rarity itemRarity,
            String sourcePath,
            double selectRate,
            double conditionRate,
            double estimatedRate,
            DropRateRarity dropRateRarity,
            CountRange countRange,
            List<String> conditionTypes,
            List<String> functionTypes
    ) {
        static LootCandidate of(
                ResourceLocation tableId, Item item, String sourcePath, double selectRate, double conditionRate,
                CountRange countRange, List<String> conditionTypes, List<String> functionTypes) {
            double estimatedRate = Math.max(0.0D, selectRate * conditionRate * countRange.expected());

            return new LootCandidate(
                    tableId,
                    item,
                    BuiltInRegistries.ITEM.getKey(item),
                    item.getDefaultInstance().getRarity(),
                    sourcePath,
                    clamp(selectRate, 0.0D, 1.0D),
                    clamp(conditionRate, 0.0D, 1.0D),
                    estimatedRate,
                    DropRateRarity.fromRate(estimatedRate),
                    countRange,
                    List.copyOf(conditionTypes),
                    List.copyOf(functionTypes)
            );
        }

        public ItemStack getItemStack() {
            return getExpectedCountStack();
        }

        public ItemStack getMinCountStack() {
            return stackWithCount(countRange.min());
        }

        public ItemStack getMaxCountStack() {
            return stackWithCount(countRange.max());
        }

        public ItemStack getExpectedCountStack() {
            return stackWithCount((int) Math.round(countRange.expected()));
        }

        public ItemStack getRandomCountStack(RandomSource random) {
            if (countRange.min() >= countRange.max()){
                return stackWithCount(countRange.min());
            }

            return stackWithCount(Mth.nextInt(random, countRange.min(), countRange.max()));
        }

        private ItemStack stackWithCount(int count) {
            if (count <= 0){
                return ItemStack.EMPTY;
            }

            ItemStack stack = new ItemStack(item);
            if (stack.isEmpty()){
                return ItemStack.EMPTY;
            }

            stack.setCount(Math.min(count, stack.getMaxStackSize()));
            return stack;
        }

        public boolean hasCondition(String key) {
            for (String type : conditionTypes) {
                if (type.contains(key)){
                    return true;
                }
            }
            return false;
        }

        public boolean hasExactCondition(String key) {
            for (String type : conditionTypes) {
                if (type.equals(key)){
                    return true;
                }
            }
            return false;
        }

        public boolean hasFunction(String key) {
            for (String type : functionTypes) {
                if (type.contains(key)){
                    return true;
                }
            }
            return false;
        }

        public boolean hasExactFunction(String key) {
            for (String type : functionTypes) {
                if (type.equals(key)){
                    return true;
                }
            }
            return false;
        }
    }

    public record CountRange(int min, int max, double expected, double nonEmptyChance) {
        public CountRange {
            min = Math.max(0, min);
            max = Math.max(min, max);
            expected = Math.max(0.0D, expected);
            nonEmptyChance = clamp(nonEmptyChance, 0.0D, 1.0D);
        }

        public static CountRange zero() {
            return new CountRange(0, 0, 0.0D, 0.0D);
        }

        public static CountRange one() {
            return new CountRange(1, 1, 1.0D, 1.0D);
        }

        public CountRange set(CountRange other) {
            return other;
        }

        public CountRange add(CountRange other) {
            return new CountRange(
                    min + other.min,
                    max + other.max,
                    expected + other.expected,
                    1.0D - (1.0D - nonEmptyChance) * (1.0D - other.nonEmptyChance)
            );
        }

        public CountRange multiply(int value) {
            if (value <= 0){
                return zero();
            }

            return new CountRange(
                    min * value,
                    max * value,
                    expected * value,
                    1.0D - Math.pow(1.0D - nonEmptyChance, value)
            );
        }

        public CountRange limit(int minLimit, int maxLimit) {
            int newMin = Mth.clamp(min, minLimit, maxLimit);
            int newMax = Mth.clamp(max, minLimit, maxLimit);
            double newExpected = Mth.clamp(expected, minLimit, maxLimit);

            double newNonEmptyChance = nonEmptyChance;
            if (newMax <= 0){
                newNonEmptyChance = 0.0D;
            }else if (newMin > 0){
                newNonEmptyChance = 1.0D;
            }

            return new CountRange(newMin, newMax, newExpected, newNonEmptyChance);
        }
    }
}