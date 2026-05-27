package org.dreamtinker.dreamtinker.utils.LootHelper;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class LootScanCommon {
    private LootScanCommon() {}

    public static List<ItemStack> toMinOneStacks(List<LootCandidate> candidates, RandomSource random) {
        List<ItemStack> stacks = new ArrayList<>();

        for (LootCandidate candidate : candidates)
            stacks.add(toMinOneStack(candidate, random));

        return stacks;
    }

    public static ItemStack toMinOneStack(LootCandidate candidate, RandomSource random) {
        ItemStack stack = candidate.getRandomCountStack(random);

        if (stack.isEmpty())
            stack = candidate.getExpectedCountStack();

        if (stack.isEmpty())
            stack = new ItemStack(candidate.item());

        stack.setCount(Math.max(1, Math.min(stack.getCount(), stack.getMaxStackSize())));
        return stack;
    }

    public static List<ItemStack> tryExtractLootStacks(
            List<LootCandidate> candidates,
            RandomSource random,
            float triggerRate,
            int lootingLevel, LootRollMode mode,
            @Nullable CandidateFilter filter,
            LootCandidatePicker picker
    ) {
        List<ItemStack> out = new ArrayList<>();

        candidates = new ArrayList<>(candidates);
        candidates.removeIf(candidate -> candidate.stack().isEmpty());

        if (filter != null)
            candidates = filter(candidates, filter);

        if (candidates.isEmpty())
            return out;

        addRolledStack(out, candidates, random, picker);

        for (int roll = 0; roll <= Math.max(0, lootingLevel); roll++) {
            float rate = getLootingExtraRollRate(triggerRate, roll, mode);

            if (random.nextFloat() >= rate)
                continue;

            addRolledStack(out, candidates, random, picker);
        }

        return out;
    }

    private static void addRolledStack(
            List<ItemStack> out,
            List<LootCandidate> candidates,
            RandomSource random,
            LootCandidatePicker picker
    ) {
        ItemStack stack = rollOneStack(candidates, random, picker);

        if (!stack.isEmpty())
            out.add(stack);
    }

    public static LootCandidate pickByInverseRate(List<LootCandidate> candidates, RandomSource random) {
        double totalWeight = 0.0D;
        for (LootCandidate candidate : candidates)
            totalWeight += 1.0D / Math.max(0.001D, candidate.estimatedRate());
        double roll = random.nextDouble() * totalWeight;
        for (LootCandidate candidate : candidates) {
            roll -= 1.0D / Math.max(0.001D, candidate.estimatedRate());
            if (roll <= 0.0D)
                return candidate;
        }
        return candidates.get(candidates.size() - 1);
    }

    public static LootCandidate pickByNaturalRate(List<LootCandidate> candidates, RandomSource random) {
        double totalWeight = 0.0D;
        for (LootCandidate candidate : candidates)
            totalWeight += Math.max(0.001D, candidate.estimatedRate());
        double roll = random.nextDouble() * totalWeight;
        for (LootCandidate candidate : candidates) {
            roll -= Math.max(0.001D, candidate.estimatedRate());
            if (roll <= 0.0D)
                return candidate;
        }
        return candidates.get(candidates.size() - 1);
    }

    public static float getLootingExtraRollRate(float triggerRate, int roll, LootRollMode mode) {
        if (roll <= 0)
            return 1.0F;

        return switch (mode) {
            case NATURAL -> Mth.clamp(triggerRate / roll, 0.0F, 1.0F);

            case RARE -> {
                float rate = triggerRate * (float) Math.pow(0.72D, roll - 1);
                yield Mth.clamp(rate, 0.0F, 0.85F);
            }
        };
    }

    public static ItemStack rollOneStack(List<LootCandidate> candidates, RandomSource random, LootCandidatePicker picker) {
        ItemStack stack = ItemStack.EMPTY;
        for (int i = 0; i < candidates.size() * 2 && stack.isEmpty(); i++)
            stack = picker.pick(candidates, random).getRandomCountStack(random);
        return stack;
    }

    public static List<LootCandidate> filter(List<LootCandidate> candidates, @Nullable CandidateFilter filter) {
        if (filter == null)
            return candidates;
        List<LootCandidate> out = new ArrayList<>();
        for (LootCandidate candidate : candidates)
            if (filter.test(candidate))
                out.add(candidate);
        return out;
    }

    public static ItemStack parseResultStackSafe(@Nullable JsonObject result) {
        if (result == null)
            return ItemStack.EMPTY;

        String idText = "";
        if (result.has("item"))
            idText = getString(result, "item", "");
        else if (result.has("id"))
            idText = getString(result, "id", "");
        else if (result.has("name"))
            idText = getString(result, "name", "");
        if (idText.isEmpty())
            return ItemStack.EMPTY;

        ResourceLocation id = ResourceLocation.tryParse(idText);
        if (id == null || !BuiltInRegistries.ITEM.containsKey(id))
            return ItemStack.EMPTY;

        Item item = BuiltInRegistries.ITEM.get(id);
        if (item == null || item == Items.AIR)
            return ItemStack.EMPTY;

        int count = getInt(result, "count", getInt(result, "Count", 1));
        ItemStack stack = new ItemStack(item, Math.max(1, count));

        String nbtText = null;
        if (result.has("nbt") && result.get("nbt").isJsonPrimitive())
            nbtText = result.get("nbt").getAsString();
        else if (result.has("tag"))
            nbtText = result.get("tag").isJsonPrimitive() ? result.get("tag").getAsString() : result.get("tag").toString();

        if (nbtText != null && !nbtText.isBlank()){
            try {stack.setTag(TagParser.parseTag(nbtText));}
            catch (CommandSyntaxException ignored) {}
        }

        return stack;
    }

    public static CountRange readCountRange(JsonElement element, CountRange fallback) {
        if (element == null || element.isJsonNull())
            return fallback;

        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()){
            int value = Math.max(0, element.getAsInt());
            return new CountRange(value, value, value, value > 0 ? 1.0D : 0.0D);
        }

        if (!element.isJsonObject())
            return fallback;
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
                return new CountRange(min, max, Math.max(0.0D, (minRaw + maxRaw) * 0.5D), uniformNonEmptyChance(min, max));
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

    public static double averageNumberProvider(JsonElement element, double fallback) {
        if (element == null || element.isJsonNull())
            return fallback;
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber())
            return element.getAsDouble();
        if (!element.isJsonObject())
            return fallback;

        JsonObject obj = element.getAsJsonObject();
        String type = normalizedType(getString(obj, "type", "constant"));

        switch (type) {
            case "constant" -> {return getDouble(obj, "value", fallback);}
            case "uniform" -> {return (averageNumberProvider(obj.get("min"), fallback) + averageNumberProvider(obj.get("max"), fallback)) * 0.5D;}
            case "binomial" -> {return averageNumberProvider(obj.get("n"), 0.0D) * averageNumberProvider(obj.get("p"), 0.0D);}
        }

        return fallback;
    }

    public static double uniformNonEmptyChance(int min, int max) {
        if (max <= 0)
            return 0.0D;
        if (min > 0)
            return 1.0D;
        int total = max - min + 1;
        int positive = max - Math.max(min, 1) + 1;
        if (total <= 0)
            return 0.0D;
        return clamp((double) positive / (double) total, 0.0D, 1.0D);
    }

    public static String normalizedType(String id) {
        if (id == null || id.isEmpty())
            return "";
        ResourceLocation location = ResourceLocation.tryParse(id);
        return location == null ? id : location.getPath();
    }

    public static String getString(JsonObject obj, String key, String fallback) {
        if (obj == null || !obj.has(key) || !obj.get(key).isJsonPrimitive())
            return fallback;
        return obj.get(key).getAsString();
    }

    public static int getInt(JsonObject obj, String key, int fallback) {
        if (obj == null || !obj.has(key) || !obj.get(key).isJsonPrimitive() || !obj.get(key).getAsJsonPrimitive().isNumber())
            return fallback;
        return obj.get(key).getAsInt();
    }

    public static boolean getBoolean(JsonObject obj, String key, boolean fallback) {
        if (obj == null || !obj.has(key) || !obj.get(key).isJsonPrimitive())
            return fallback;
        return obj.get(key).getAsBoolean();
    }

    public static double getDouble(JsonObject obj, String key, double fallback) {
        if (obj == null || !obj.has(key) || !obj.get(key).isJsonPrimitive() || !obj.get(key).getAsJsonPrimitive().isNumber())
            return fallback;
        return obj.get(key).getAsDouble();
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public enum LootRollMode {NATURAL, RARE}

    public enum SourceType {LOOT_TABLE, GLOBAL_LOOT_MODIFIER, FORCED_SCANNER_FALLBACK}

    public enum DropRateRarity {
        GUARANTEED(0), COMMON(1), UNCOMMON(2), RARE(3), VERY_RARE(4), ULTRA_RARE(5), UNKNOWN(-1);
        private final int rarityScore;

        DropRateRarity(int rarityScore) {this.rarityScore = rarityScore;}

        public static DropRateRarity fromRate(double rate) {
            if (Double.isNaN(rate))
                return UNKNOWN;
            if (rate >= 0.999D)
                return GUARANTEED;
            if (rate >= 0.25D)
                return COMMON;
            if (rate >= 0.10D)
                return UNCOMMON;
            if (rate >= 0.025D)
                return RARE;
            if (rate >= 0.005D)
                return VERY_RARE;
            return ULTRA_RARE;
        }

        public boolean atLeast(DropRateRarity threshold) {return rarityScore >= threshold.rarityScore && rarityScore >= 0;}
    }

    @FunctionalInterface
    public interface CandidateFilter {
        static CandidateFilter item(Predicate<Item> predicate) {return candidate -> predicate.test(candidate.stack().getItem());}

        static CandidateFilter itemId(Predicate<ResourceLocation> predicate) {return candidate -> predicate.test(candidate.itemId());}

        static CandidateFilter itemRarityAtLeast(Rarity rarity) {
            int threshold = itemRarityScore(rarity);
            return candidate -> itemRarityScore(candidate.itemRarity()) >= threshold;
        }

        static CandidateFilter dropRateRarityAtLeast(DropRateRarity rarity) {return candidate -> candidate.dropRateRarity().atLeast(rarity);}

        static CandidateFilter estimatedRateBelow(double maxRate) {return candidate -> candidate.estimatedRate() <= maxRate;}

        static CandidateFilter estimatedRateAtLeast(double minRate) {return candidate -> candidate.estimatedRate() >= minRate;}

        static CandidateFilter hasCondition(String key) {return candidate -> candidate.hasCondition(key);}

        static CandidateFilter hasExactCondition(String key) {return candidate -> candidate.hasExactCondition(key);}

        static CandidateFilter hasFunction(String key) {return candidate -> candidate.hasFunction(key);}

        static CandidateFilter hasExactFunction(String key) {return candidate -> candidate.hasExactFunction(key);}

        static CandidateFilter rareByItemOrDropRate() {
            return itemRarityAtLeast(Rarity.RARE).or(dropRateRarityAtLeast(DropRateRarity.RARE)).or(hasCondition("random_chance")).or(hasCondition("killed"))
                                                 .or(hasCondition("match_tool")).or(hasCondition("damage_source_properties"))
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

        default CandidateFilter and(CandidateFilter other) {return candidate -> test(candidate) && other.test(candidate);}

        default CandidateFilter or(CandidateFilter other) {return candidate -> test(candidate) || other.test(candidate);}
    }

    @FunctionalInterface
    public interface LootCandidatePicker {
        LootCandidate pick(List<LootCandidate> candidates, RandomSource random);
    }

    public record CountRange(int min, int max, double expected, double nonEmptyChance) {
        public CountRange {
            min = Math.max(0, min);
            max = Math.max(min, max);
            expected = Math.max(0.0D, expected);
            nonEmptyChance = clamp(nonEmptyChance, 0.0D, 1.0D);
        }

        public static CountRange zero() {return new CountRange(0, 0, 0.0D, 0.0D);}

        public static CountRange one() {return new CountRange(1, 1, 1.0D, 1.0D);}

        public CountRange set(CountRange other) {return other;}

        public CountRange add(CountRange other) {
            return new CountRange(min + other.min, max + other.max, expected + other.expected, 1.0D - (1.0D - nonEmptyChance) * (1.0D - other.nonEmptyChance));
        }

        public CountRange multiply(int value) {
            if (value <= 0)
                return zero();
            return new CountRange(min * value, max * value, expected * value, 1.0D - Math.pow(1.0D - nonEmptyChance, value));
        }

        public CountRange limit(int minLimit, int maxLimit) {
            int newMin = Mth.clamp(min, minLimit, maxLimit);
            int newMax = Mth.clamp(max, minLimit, maxLimit);
            double newExpected = Mth.clamp(expected, minLimit, maxLimit);
            double newNonEmptyChance = nonEmptyChance;
            if (newMax <= 0)
                newNonEmptyChance = 0.0D;
            else if (newMin > 0)
                newNonEmptyChance = 1.0D;
            return new CountRange(newMin, newMax, newExpected, newNonEmptyChance);
        }
    }

    public record LootCandidate(
            SourceType sourceType,
            ResourceLocation sourceId,
            ItemStack stack,
            ResourceLocation itemId,
            Rarity itemRarity,
            String sourcePath,
            double selectRate,
            double conditionRate,
            double estimatedRate,
            DropRateRarity dropRateRarity,
            CountRange countRange,
            List<String> conditionTypes,
            List<String> functionTypes,
            String reason
    ) {
        public static LootCandidate of(SourceType sourceType, ResourceLocation sourceId, ItemStack stack, String sourcePath, double selectRate, double conditionRate, CountRange countRange, List<String> conditionTypes, List<String> functionTypes, String reason) {
            if (stack == null || stack.isEmpty())
                stack = ItemStack.EMPTY;
            Item item = stack.getItem();
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
            double estimatedRate = Math.max(0.0D, selectRate * conditionRate * Math.max(1.0D, countRange.expected()));
            return new LootCandidate(sourceType, sourceId, stack.copy(), itemId, stack.getRarity(), sourcePath, clamp(selectRate, 0.0D, 1.0D),
                                     clamp(conditionRate, 0.0D, 1.0D), estimatedRate, DropRateRarity.fromRate(estimatedRate), countRange,
                                     List.copyOf(conditionTypes), List.copyOf(functionTypes), reason == null ? "" : reason);
        }

        public static LootCandidate lootTable(ResourceLocation tableId, ItemStack stack, String sourcePath, double selectRate, double conditionRate, CountRange countRange, List<String> conditionTypes, List<String> functionTypes) {
            return of(SourceType.LOOT_TABLE, tableId, stack, sourcePath, selectRate, conditionRate, countRange, conditionTypes, functionTypes, "loot_table");
        }

        public static LootCandidate glm(ResourceLocation glmId, ItemStack stack, String sourcePath, double selectRate, CountRange countRange, List<String> conditionTypes, List<String> functionTypes, String reason) {
            return of(SourceType.GLOBAL_LOOT_MODIFIER, glmId, stack, sourcePath, selectRate, 1.0D, countRange, conditionTypes, functionTypes, reason);
        }

        @Nullable
        public ResourceLocation tableId() {
            return sourceType == SourceType.LOOT_TABLE ? sourceId : null;
        }

        @Nullable
        public ResourceLocation glmId() {
            return sourceType == SourceType.GLOBAL_LOOT_MODIFIER ? sourceId : null;
        }

        public Item item() {
            return stack.getItem();
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
            if (countRange.min() >= countRange.max())
                return stackWithCount(countRange.min());
            return stackWithCount(Mth.nextInt(random, countRange.min(), countRange.max()));
        }

        private ItemStack stackWithCount(int count) {
            if (count <= 0 || stack.isEmpty())
                return ItemStack.EMPTY;
            ItemStack copy = stack.copy();
            copy.setCount(Math.min(count, copy.getMaxStackSize()));
            return copy;
        }

        public boolean hasCondition(String key) {
            for (String type : conditionTypes)
                if (type.contains(key))
                    return true;
            return false;
        }

        public boolean hasExactCondition(String key) {
            for (String type : conditionTypes)
                if (type.equals(key))
                    return true;
            return false;
        }

        public boolean hasFunction(String key) {
            for (String type : functionTypes)
                if (type.contains(key))
                    return true;
            return false;
        }

        public boolean hasExactFunction(String key) {
            for (String type : functionTypes)
                if (type.equals(key))
                    return true;
            return false;
        }
    }
}
