package org.brahypno.dreamtinker.utils.LootHelper;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.brahypno.dreamtinker.Dreamtinker;

import javax.annotation.Nullable;
import java.io.Reader;
import java.util.*;

import static org.brahypno.dreamtinker.utils.LootHelper.LootScanCommon.*;

public final class GlobalLootModifierItemScanner {
    private GlobalLootModifierItemScanner() {}

    public static List<ItemStack> getAllScannedLootStacksMinOne(ServerLevel level, LivingEntity target) {
        return getAllScannedLootStacksMinOne(level, target, Options.defaultOptions());
    }

    public static List<ItemStack> getAllScannedLootStacksMinOne(ServerLevel level, LivingEntity target, Options options) {
        List<LootCandidate> candidates = collect(level, target, options);
        logGetAllCandidates(target, options, candidates);

        List<ItemStack> stacks = new ArrayList<>();
        for (LootCandidate candidate : candidates) {
            ItemStack stack = toMinOneStack(candidate, level.random);
            stacks.add(stack);
        }

        return stacks;
    }

    private static void logGetAllCandidates(LivingEntity target, Options options, List<LootCandidate> candidates) {
        ResourceLocation targetId = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType());
        Dreamtinker.LOGGER.info(
                "[GLM Scanner] getAllScannedLootStacksMinOne target={} candidates={} rejectEntityMismatch={} scanFuzzyItemFields={}",
                targetId, candidates.size(), options.rejectEntityMismatch(), options.scanFuzzyItemFields()
        );

        for (int i = 0; i < candidates.size(); i++) {
            LootCandidate c = candidates.get(i);
            Dreamtinker.LOGGER.info(
                    "[GLM Scanner] candidate[{}] sourceType={} glm={} item={} rarity={} path={} selectRate={} conditionRate={} estimatedRate={} dropRateRarity={} countRange=[min={}, max={}, expected={}, nonEmpty={}] conditions={} functions={} reason={} stackTag={}",
                    i,
                    c.sourceType(),
                    c.sourceId(),
                    c.itemId(),
                    c.itemRarity(),
                    c.sourcePath(),
                    fmt(c.selectRate()),
                    fmt(c.conditionRate()),
                    fmt(c.estimatedRate()),
                    c.dropRateRarity(),
                    c.countRange().min(),
                    c.countRange().max(),
                    fmt(c.countRange().expected()),
                    fmt(c.countRange().nonEmptyChance()),
                    c.conditionTypes(),
                    c.functionTypes(),
                    c.reason(),
                    shortTag(c.stack())
            );
        }
    }

    private static String fmt(double value) {
        return String.format(Locale.ROOT, "%.6f", value);
    }

    private static String shortTag(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !stack.hasTag())
            return "none";

        String tag = String.valueOf(stack.getTag());
        return tag.length() <= 300 ? tag : tag.substring(0, 300) + "...";
    }

    public static List<ItemStack> tryExtractSomeLoot(ServerLevel level, LivingEntity target, float triggerRate, int lootingLevel) {
        return tryExtractLoot(
                level,
                target,
                triggerRate,
                lootingLevel, LootRollMode.RARE,
                null,
                LootScanCommon::pickByNaturalRate
        );
    }

    public static List<ItemStack> tryExtractRareLoot(ServerLevel level, LivingEntity target, float triggerRate, int lootingLevel) {
        return tryExtractLoot(
                level,
                target,
                triggerRate,
                lootingLevel, LootRollMode.RARE,
                CandidateFilter.rareByItemOrDropRate()
                               .and(CandidateFilter.estimatedRateBelow(0.25D)),
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
        return tryExtractLoot(
                level,
                target,
                triggerRate,
                lootingLevel, mode,
                filter,
                picker,
                Options.defaultOptions()
        );
    }

    public static List<ItemStack> tryExtractLoot(
            ServerLevel level,
            LivingEntity target,
            float triggerRate,
            int lootingLevel, LootRollMode mode,
            @Nullable CandidateFilter filter,
            LootCandidatePicker picker,
            Options options
    ) {
        List<ItemStack> out = new ArrayList<>();
        List<LootCandidate> candidates = collect(level, target, options);

        candidates.removeIf(candidate -> candidate.stack().isEmpty());

        if (filter != null)
            candidates = LootScanCommon.filter(candidates, filter);

        if (candidates.isEmpty())
            return out;

        addRolledStack(out, candidates, level.random, picker);

        for (int roll = 0; roll <= Math.max(0, lootingLevel); roll++) {
            float rate = LootScanCommon.getLootingExtraRollRate(triggerRate, roll, mode);

            if (level.random.nextFloat() >= rate)
                continue;

            addRolledStack(out, candidates, level.random, picker);
        }

        return out;
    }

    private static void addRolledStack(
            List<ItemStack> out,
            List<LootCandidate> candidates,
            RandomSource random,
            LootCandidatePicker picker
    ) {
        ItemStack stack = LootScanCommon.rollOneStack(candidates, random, picker);

        if (!stack.isEmpty())
            out.add(stack);
    }

    private static ItemStack toMinOneStack(LootCandidate candidate, RandomSource random) {
        ItemStack stack = candidate.getRandomCountStack(random);

        if (stack.isEmpty())
            stack = candidate.getExpectedCountStack();

        if (stack.isEmpty())
            stack = new ItemStack(candidate.item());

        stack.setCount(Math.max(1, Math.min(stack.getCount(), stack.getMaxStackSize())));
        return stack;
    }

    public static List<LootCandidate> collect(ServerLevel level, LivingEntity target) {
        return collect(level.getServer(), target, Options.defaultOptions());
    }

    public static List<LootCandidate> collect(ServerLevel level, LivingEntity target, Options options) {
        return collect(level.getServer(), target, options);
    }

    public static List<LootCandidate> collect(MinecraftServer server, LivingEntity target, Options options) {
        List<LootCandidate> out = new ArrayList<>();
        ResourceManager rm = server.getResourceManager();
        Set<ResourceLocation> enabled = loadEnabledGLMIds(rm);

        for (ResourceLocation glmId : enabled) {
            ResourceLocation fileId = new ResourceLocation(glmId.getNamespace(), "loot_modifiers/" + glmId.getPath() + ".json");
            Optional<Resource> res = rm.getResource(fileId);
            if (res.isEmpty())
                continue;

            try (Reader reader = res.get().openAsReader()) {
                JsonObject json = GsonHelper.parse(reader);
                out.addAll(parseModifier(glmId, json, target, options));
            }
            catch (Throwable ignored) {}
        }

        return out;
    }

    private static List<LootCandidate> parseModifier(ResourceLocation glmId, JsonObject json, LivingEntity target, Options options) {
        List<LootCandidate> out = new ArrayList<>();
        String glmType = getString(json, "type", "");
        EntityAnalysis analysis = analyzeEntityMatch(json, target);

        if ((analysis.match() == EntityMatchResult.MISMATCH || analysis.match() == EntityMatchResult.NOT_ENTITY_SOURCE) && options.rejectEntityMismatch())
            return out;

        double chance = readModifierBaseChance(json);
        List<String> conditionTypes = collectConditionTypes(json);
        List<String> functionTypes = List.of(glmType);

        if (json.has("result") && json.get("result").isJsonObject()){
            JsonObject result = json.getAsJsonObject("result");
            ItemStack stack = parseResultStackSafe(result);
            CountRange localCount = readResultCountRange(result);
            if (!stack.isEmpty())
                out.add(LootCandidate.of(SourceType.GLOBAL_LOOT_MODIFIER, glmId, stack, "result", chance, 1.0D, localCount, conditionTypes, functionTypes,
                                         buildReason(glmType, analysis)));
        }

        if (json.has("results") && json.get("results").isJsonArray()){
            JsonArray results = json.getAsJsonArray("results");
            for (int i = 0; i < results.size(); i++) {
                JsonElement element = results.get(i);
                if (!element.isJsonObject())
                    continue;
                JsonObject result = element.getAsJsonObject();
                ItemStack stack = parseResultStackSafe(result);
                CountRange localCount = readResultCountRange(result);
                if (!stack.isEmpty())
                    out.add(LootCandidate.of(SourceType.GLOBAL_LOOT_MODIFIER, glmId, stack, "results[" + i + "]", chance, 1.0D, localCount, conditionTypes,
                                             functionTypes, buildReason(glmType, analysis)));
            }
        }

        if (out.isEmpty() && options.scanFuzzyItemFields()){
            List<FuzzyItemHit> hits = new ArrayList<>();
            scanFuzzyItemResults(json, "", hits);
            for (FuzzyItemHit hit : hits) {
                ItemStack stack = parseResultStackSafe(hit.obj());
                CountRange localCount = readResultCountRange(hit.obj());
                if (!stack.isEmpty())
                    out.add(LootCandidate.of(SourceType.GLOBAL_LOOT_MODIFIER, glmId, stack, hit.path(), chance, 1.0D, localCount, conditionTypes, functionTypes,
                                             buildReason(glmType, analysis)));
            }
        }

        return out;
    }

    private static double readModifierBaseChance(JsonObject json) {
        if (json.has("drop") && json.get("drop").isJsonObject()){
            JsonObject drop = json.getAsJsonObject("drop");
            if (drop.has("base_chance"))
                return clamp(getDouble(drop, "base_chance", 1.0D), 0.0D, 1.0D);
            if (drop.has("chance"))
                return clamp(getDouble(drop, "chance", 1.0D), 0.0D, 1.0D);
        }
        if (json.has("chance"))
            return clamp(getDouble(json, "chance", 1.0D), 0.0D, 1.0D);
        if (json.has("base_chance"))
            return clamp(getDouble(json, "base_chance", 1.0D), 0.0D, 1.0D);
        return 1.0D;
    }

    private static CountRange readResultCountRange(JsonObject result) {
        if (result.has("count"))
            return readCountRange(result.get("count"), CountRange.one());
        if (result.has("Count"))
            return readCountRange(result.get("Count"), CountRange.one());
        return CountRange.one();
    }

    private static String buildReason(String glmType, EntityAnalysis analysis) {
        return "glmType=" + glmType + "; entityMatch=" + analysis.match() + "; unknownConditions=" + analysis.unknownConditionIds().size() + "; constraints=" +
               analysis.entityConstraints().size();
    }

    private static Set<ResourceLocation> loadEnabledGLMIds(ResourceManager rm) {
        Set<ResourceLocation> out = new LinkedHashSet<>();
        ResourceLocation indexId = new ResourceLocation("forge", "loot_modifiers/global_loot_modifiers.json");
        List<Resource> stack = rm.getResourceStack(indexId);

        for (Resource res : stack) {
            try (Reader reader = res.openAsReader()) {
                JsonObject json = GsonHelper.parse(reader);
                if (getBoolean(json, "replace", false))
                    out.clear();
                JsonArray entries = json.has("entries") && json.get("entries").isJsonArray() ? json.getAsJsonArray("entries") : new JsonArray();
                for (JsonElement e : entries) {
                    if (!e.isJsonPrimitive())
                        continue;
                    ResourceLocation id = ResourceLocation.tryParse(e.getAsString());
                    if (id != null)
                        out.add(id);
                }
            }
            catch (Throwable ignored) {}
        }

        return out;
    }

    private static EntityAnalysis analyzeEntityMatch(JsonObject json, LivingEntity target) {
        List<ResourceLocation> unknownConditions = collectUnknownConditionIds(json);
        List<EntityConstraint> constraints = new ArrayList<>();

        if (hasExplicitNonEntitySource(json))
            return new EntityAnalysis(EntityMatchResult.NOT_ENTITY_SOURCE, unknownConditions, List.copyOf(constraints));

        EntityMatchResult vanilla = matchEntityConditions(json, target, constraints);
        scanFuzzyEntityConstraints(json, "", constraints);
        EntityMatchResult fuzzy = matchConstraints(constraints, target);

        EntityMatchResult finalResult;
        if (vanilla == EntityMatchResult.NOT_ENTITY_SOURCE || fuzzy == EntityMatchResult.NOT_ENTITY_SOURCE)
            finalResult = EntityMatchResult.NOT_ENTITY_SOURCE;
        else if (vanilla == EntityMatchResult.MISMATCH || fuzzy == EntityMatchResult.MISMATCH)
            finalResult = EntityMatchResult.MISMATCH;
        else if (vanilla == EntityMatchResult.MATCH || fuzzy == EntityMatchResult.MATCH)
            finalResult = EntityMatchResult.MATCH;
        else if (vanilla == EntityMatchResult.UNKNOWN || fuzzy == EntityMatchResult.UNKNOWN)
            finalResult = EntityMatchResult.UNKNOWN;
        else
            finalResult = EntityMatchResult.ANY;

        return new EntityAnalysis(finalResult, unknownConditions, List.copyOf(constraints));
    }

    private static EntityMatchResult matchEntityConditions(JsonObject json, LivingEntity target, List<EntityConstraint> constraints) {
        if (!json.has("conditions") || !json.get("conditions").isJsonArray())
            return EntityMatchResult.ANY;

        JsonArray conditions = json.getAsJsonArray("conditions");
        boolean sawMatch = false, sawUnknown = false;

        for (JsonElement e : conditions) {
            if (!e.isJsonObject())
                continue;
            EntityMatchResult r = matchCondition(e.getAsJsonObject(), target, constraints, "conditions[]");
            if (r == EntityMatchResult.MISMATCH)
                return EntityMatchResult.MISMATCH;
            if (r == EntityMatchResult.MATCH)
                sawMatch = true;
            if (r == EntityMatchResult.UNKNOWN)
                sawUnknown = true;
        }

        if (sawMatch)
            return EntityMatchResult.MATCH;
        if (sawUnknown)
            return EntityMatchResult.UNKNOWN;
        return EntityMatchResult.ANY;
    }

    private static EntityMatchResult matchCondition(JsonObject cond, LivingEntity target, List<EntityConstraint> constraints, String path) {
        String type = getString(cond, "condition", "");
        String normalized = normalizedType(type);

        return switch (normalized) {
            case "entity_properties" -> matchVanillaEntityProperties(cond, target, constraints, path);
            case "inverted" -> matchInverted(cond, target, constraints, path + ".term");
            case "any_of" -> matchAnyOf(cond, target, constraints, path + ".terms");
            case "all_of" -> matchAllOf(cond, target, constraints, path + ".terms");
            default -> EntityMatchResult.UNKNOWN;
        };
    }

    private static EntityMatchResult matchVanillaEntityProperties(JsonObject cond, LivingEntity target, List<EntityConstraint> constraints, String path) {
        String entity = getString(cond, "entity", "");
        if (!entity.equals("this"))
            return EntityMatchResult.UNKNOWN;
        if (!cond.has("predicate") || !cond.get("predicate").isJsonObject())
            return EntityMatchResult.ANY;

        JsonObject predicate = cond.getAsJsonObject("predicate");
        if (!predicate.has("type") || !predicate.get("type").isJsonPrimitive())
            return EntityMatchResult.ANY;

        String typeText = predicate.get("type").getAsString();
        EntityConstraint c = constraintFromText(typeText, ConstraintSource.VANILLA_ENTITY_PROPERTIES, path + ".predicate.type");
        if (c == null)
            return EntityMatchResult.UNKNOWN;
        constraints.add(c);
        return matchOneConstraint(c, target);
    }

    private static EntityMatchResult matchInverted(JsonObject cond, LivingEntity target, List<EntityConstraint> constraints, String path) {
        if (!cond.has("term") || !cond.get("term").isJsonObject())
            return EntityMatchResult.UNKNOWN;
        EntityMatchResult r = matchCondition(cond.getAsJsonObject("term"), target, constraints, path);
        return switch (r) {
            case MATCH -> EntityMatchResult.MISMATCH;
            case MISMATCH -> EntityMatchResult.MATCH;
            case ANY, UNKNOWN -> EntityMatchResult.UNKNOWN;
            case NOT_ENTITY_SOURCE -> EntityMatchResult.NOT_ENTITY_SOURCE;
        };
    }

    private static EntityMatchResult matchAnyOf(JsonObject cond, LivingEntity target, List<EntityConstraint> constraints, String path) {
        JsonArray terms = cond.has("terms") && cond.get("terms").isJsonArray() ? cond.getAsJsonArray("terms") : new JsonArray();
        boolean sawMismatch = false, sawUnknown = false;

        for (int i = 0; i < terms.size(); i++) {
            JsonElement e = terms.get(i);
            if (!e.isJsonObject())
                continue;
            EntityMatchResult r = matchCondition(e.getAsJsonObject(), target, constraints, path + "[" + i + "]");
            if (r == EntityMatchResult.MATCH || r == EntityMatchResult.ANY)
                return r;
            if (r == EntityMatchResult.MISMATCH)
                sawMismatch = true;
            if (r == EntityMatchResult.UNKNOWN)
                sawUnknown = true;
        }

        if (sawUnknown)
            return EntityMatchResult.UNKNOWN;
        return sawMismatch ? EntityMatchResult.MISMATCH : EntityMatchResult.ANY;
    }

    private static EntityMatchResult matchAllOf(JsonObject cond, LivingEntity target, List<EntityConstraint> constraints, String path) {
        JsonArray terms = cond.has("terms") && cond.get("terms").isJsonArray() ? cond.getAsJsonArray("terms") : new JsonArray();
        boolean sawMatch = false, sawUnknown = false;

        for (int i = 0; i < terms.size(); i++) {
            JsonElement e = terms.get(i);
            if (!e.isJsonObject())
                continue;
            EntityMatchResult r = matchCondition(e.getAsJsonObject(), target, constraints, path + "[" + i + "]");
            if (r == EntityMatchResult.MISMATCH)
                return EntityMatchResult.MISMATCH;
            if (r == EntityMatchResult.MATCH)
                sawMatch = true;
            if (r == EntityMatchResult.UNKNOWN)
                sawUnknown = true;
        }

        if (sawMatch)
            return EntityMatchResult.MATCH;
        if (sawUnknown)
            return EntityMatchResult.UNKNOWN;
        return EntityMatchResult.ANY;
    }

    private static void scanFuzzyEntityConstraints(JsonElement element, String path, List<EntityConstraint> out) {
        if (element == null || element.isJsonNull())
            return;

        if (element.isJsonObject()){
            JsonObject obj = element.getAsJsonObject();

            for (var entry : obj.entrySet()) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();
                String childPath = path.isEmpty() ? key : path + "." + key;

                if (isEntityConstraintKey(key, childPath))
                    extractEntityConstraintValues(value, childPath, out);
                scanFuzzyEntityConstraints(value, childPath, out);
            }

            return;
        }

        if (element.isJsonArray()){
            JsonArray arr = element.getAsJsonArray();
            for (int i = 0; i < arr.size(); i++)
                scanFuzzyEntityConstraints(arr.get(i), path + "[" + i + "]", out);
        }
    }

    private static boolean isEntityConstraintKey(String key, String path) {
        if (path.endsWith("source.entities.entities"))
            return true;
        if (key.equals("entity_type") || key.equals("entity_types"))
            return true;

        if (key.equals("entity") || key.equals("entities")){
            if (path.endsWith("condition.entity") || path.endsWith(".entity"))
                return false;
            return path.startsWith("source.") || path.contains(".source.");
        }

        return false;
    }

    private static void extractEntityConstraintValues(JsonElement value, String path, List<EntityConstraint> out) {
        if (value == null || value.isJsonNull())
            return;

        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()){
            addEntityConstraint(value.getAsString(), path, out, ConstraintSource.FUZZY_SOURCE_ENTITIES);
            return;
        }

        if (value.isJsonArray()){
            JsonArray arr = value.getAsJsonArray();

            for (int i = 0; i < arr.size(); i++) {
                JsonElement e = arr.get(i);
                if (e.isJsonPrimitive() && e.getAsJsonPrimitive().isString())
                    addEntityConstraint(e.getAsString(), path + "[" + i + "]", out, ConstraintSource.FUZZY_SOURCE_ENTITIES);
            }
        }
    }

    private static void addEntityConstraint(String text, String path, List<EntityConstraint> out, ConstraintSource source) {
        EntityConstraint c = constraintFromText(text, source, path);
        if (c != null && !out.contains(c))
            out.add(c);
    }

    @Nullable
    private static EntityConstraint constraintFromText(String text, ConstraintSource source, String path) {
        if (text == null || text.isBlank())
            return null;
        if (text.equals("this") || text.equals("killer") || text.equals("direct_killer") || text.equals("attacker"))
            return null;

        boolean tag = text.startsWith("#");
        ResourceLocation id = ResourceLocation.tryParse(tag ? text.substring(1) : text);
        if (id == null)
            return null;

        if (!tag && !BuiltInRegistries.ENTITY_TYPE.containsKey(id))
            return null;
        return new EntityConstraint(source, tag, id, path);
    }

    private static EntityMatchResult matchConstraints(List<EntityConstraint> constraints, LivingEntity target) {
        if (constraints.isEmpty())
            return EntityMatchResult.ANY;

        boolean sawNonTag = false;
        boolean sawTag = false;

        for (EntityConstraint c : constraints) {
            EntityMatchResult r = matchOneConstraint(c, target);
            if (r == EntityMatchResult.MATCH)
                return EntityMatchResult.MATCH;
            if (c.tag())
                sawTag = true;
            else
                sawNonTag = true;
        }

        return sawTag && !sawNonTag ? EntityMatchResult.UNKNOWN : EntityMatchResult.MISMATCH;
    }

    private static EntityMatchResult matchOneConstraint(EntityConstraint c, LivingEntity target) {
        EntityType<?> actualType = target.getType();
        ResourceLocation actualId = BuiltInRegistries.ENTITY_TYPE.getKey(actualType);

        if (c.tag()){
            TagKey<EntityType<?>> tag = TagKey.create(Registries.ENTITY_TYPE, c.id());
            return actualType.is(tag) ? EntityMatchResult.MATCH : EntityMatchResult.UNKNOWN;
        }

        return c.id().equals(actualId) ? EntityMatchResult.MATCH : EntityMatchResult.MISMATCH;
    }

    private static List<String> collectConditionTypes(JsonObject json) {
        List<String> out = new ArrayList<>();
        if (!json.has("conditions") || !json.get("conditions").isJsonArray())
            return out;
        collectConditionTypesFromArray(json.getAsJsonArray("conditions"), out);
        return out;
    }

    private static void collectConditionTypesFromArray(JsonArray arr, List<String> out) {
        for (JsonElement e : arr) {
            if (!e.isJsonObject())
                continue;

            JsonObject cond = e.getAsJsonObject();
            String type = getString(cond, "condition", "");
            if (!type.isEmpty())
                out.add(type);

            if (cond.has("terms") && cond.get("terms").isJsonArray())
                collectConditionTypesFromArray(cond.getAsJsonArray("terms"), out);

            if (cond.has("term") && cond.get("term").isJsonObject()){
                JsonArray single = new JsonArray();
                single.add(cond.getAsJsonObject("term"));
                collectConditionTypesFromArray(single, out);
            }
        }
    }

    private static List<ResourceLocation> collectUnknownConditionIds(JsonObject json) {
        List<ResourceLocation> out = new ArrayList<>();
        if (!json.has("conditions") || !json.get("conditions").isJsonArray())
            return out;
        collectUnknownConditionIdsFromArray(json.getAsJsonArray("conditions"), out);
        return out;
    }

    private static boolean hasExplicitNonEntitySource(JsonObject json) {
        if (json == null)
            return false;

        if (json.has("source") && json.get("source").isJsonObject()){
            JsonObject source = json.getAsJsonObject("source");

            if (source.has("blocks") || source.has("block") || source.has("block_state") || source.has("blockstate"))
                return true;

            if (source.has("fluids") || source.has("fluid"))
                return true;

            if (source.has("chests") || source.has("chest") || source.has("containers") || source.has("container"))
                return true;

            if (source.has("fishing") || source.has("archaeology"))
                return true;

            if (source.has("entities") || source.has("entity") || source.has("entity_type") || source.has("entity_types"))
                return false;
        }

        return hasExplicitNonEntitySourceRecursive(json, "");
    }

    private static boolean hasExplicitNonEntitySourceRecursive(JsonElement element, String path) {
        if (element == null || element.isJsonNull())
            return false;

        if (element.isJsonObject()){
            JsonObject obj = element.getAsJsonObject();

            for (var entry : obj.entrySet()) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();
                String childPath = path.isEmpty() ? key : path + "." + key;

                if (isNonEntitySourceKey(key, childPath))
                    return true;

                if (hasExplicitNonEntitySourceRecursive(value, childPath))
                    return true;
            }

            return false;
        }

        if (element.isJsonArray()){
            JsonArray arr = element.getAsJsonArray();

            for (int i = 0; i < arr.size(); i++) {
                if (hasExplicitNonEntitySourceRecursive(arr.get(i), path + "[" + i + "]"))
                    return true;
            }
        }

        return false;
    }

    private static boolean isNonEntitySourceKey(String key, String path) {
        if (path.equals("source.blocks") || path.equals("source.block") || path.equals("source.block_state") || path.equals("source.blockstate"))
            return true;

        if (path.equals("source.fluids") || path.equals("source.fluid"))
            return true;

        if (path.equals("source.chests") || path.equals("source.chest") || path.equals("source.containers") || path.equals("source.container"))
            return true;

        if (path.equals("source.fishing") || path.equals("source.archaeology"))
            return true;

        return false;
    }

    private static void collectUnknownConditionIdsFromArray(JsonArray arr, List<ResourceLocation> out) {
        for (JsonElement e : arr) {
            if (!e.isJsonObject())
                continue;

            JsonObject cond = e.getAsJsonObject();
            String type = getString(cond, "condition", "");

            if (!type.isEmpty() && !isKnownGenericCondition(type)){
                ResourceLocation id = ResourceLocation.tryParse(type);
                if (id != null)
                    out.add(id);
            }

            if (cond.has("terms") && cond.get("terms").isJsonArray())
                collectUnknownConditionIdsFromArray(cond.getAsJsonArray("terms"), out);

            if (cond.has("term") && cond.get("term").isJsonObject()){
                JsonArray single = new JsonArray();
                single.add(cond.getAsJsonObject("term"));
                collectUnknownConditionIdsFromArray(single, out);
            }
        }
    }

    private static boolean isKnownGenericCondition(String type) {
        String n = normalizedType(type);
        return n.equals("entity_properties") || n.equals("any_of") || n.equals("all_of") || n.equals("inverted") || n.equals("killed_by_player") ||
               n.equals("random_chance") || n.equals("random_chance_with_looting") || n.equals("match_tool") || n.equals("damage_source_properties") ||
               n.equals("location_check");
    }

    private static void scanFuzzyItemResults(JsonElement element, String path, List<FuzzyItemHit> out) {
        if (element == null || element.isJsonNull())
            return;

        if (element.isJsonObject()){
            JsonObject obj = element.getAsJsonObject();
            boolean hasItem =
                    (obj.has("item") || obj.has("id") || obj.has("name")) && (obj.has("count") || obj.has("Count") || obj.has("nbt") || obj.has("tag"));

            if (hasItem)
                out.add(new FuzzyItemHit(path.isEmpty() ? "$" : path, obj));

            for (var entry : obj.entrySet()) {
                String childPath = path.isEmpty() ? entry.getKey() : path + "." + entry.getKey();
                scanFuzzyItemResults(entry.getValue(), childPath, out);
            }

            return;
        }

        if (element.isJsonArray()){
            JsonArray arr = element.getAsJsonArray();
            for (int i = 0; i < arr.size(); i++)
                scanFuzzyItemResults(arr.get(i), path + "[" + i + "]", out);
        }
    }

    public enum EntityMatchResult {MATCH, ANY, MISMATCH, UNKNOWN, NOT_ENTITY_SOURCE}

    public enum ConstraintSource {VANILLA_ENTITY_PROPERTIES, FUZZY_SOURCE_ENTITIES}

    public record Options(boolean rejectEntityMismatch, boolean scanFuzzyItemFields) {
        public static Options defaultOptions() {return new Options(true, true);}
    }

    public record EntityConstraint(ConstraintSource source, boolean tag, ResourceLocation id, String jsonPath) {}

    public record EntityAnalysis(EntityMatchResult match, List<ResourceLocation> unknownConditionIds, List<EntityConstraint> entityConstraints) {}

    private record FuzzyItemHit(String path, JsonObject obj) {}
}