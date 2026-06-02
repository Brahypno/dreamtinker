package org.dreamtinker.dreamtinker.utils;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.dreamtinker.dreamtinker.common.DreamtinkerCommon;
import org.dreamtinker.dreamtinker.smeltery.DreamTinkerSmeltery;
import org.dreamtinker.dreamtinker.tools.DreamtinkerToolParts;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.IPartBuilderRecipe;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;

import java.util.*;
import java.util.function.Supplier;

public final class DTPartInfoLookup {
    public static final int DEFAULT_COST = 5;
    public static final String SUFFIX_CAST = "_cast";
    public static final String SUFFIX_SAND = "_sand_cast";
    public static final String SUFFIX_RED_SAND = "_red_sand_cast";
    private static final Map<MaterialStatsId, List<ToolPartItem>> PARTS = new HashMap<>();
    private static final Map<ToolPartItem, List<ItemStack>> DATAGEN_CASTS = new IdentityHashMap<>();
    private static final Map<Item, PartInfo> RUNTIME = new IdentityHashMap<>();
    private static Map<String, List<Item>> itemsByPath;
    private static Map<Item, Integer> datagenCosts;
    private static boolean runtimeBuilt = false;

    private DTPartInfoLookup() {}

    public static void rebuildRuntime(Level level) {
        rebuildRuntime(level.getRecipeManager(), level.registryAccess());
    }

    public static List<ToolPartItem> partList(MaterialStatsId statsId) {
        return PARTS.computeIfAbsent(statsId, DTPartInfoLookup::scanParts);
    }

    public static List<ToolPartItem> datagenParts(MaterialStatsId statsId, int cost) {
        return partList(statsId).stream()
                                .filter(part -> datagenCost(part) == cost)
                                .toList();
    }

    public static List<ToolPartItem> runtimeParts(RecipeManager recipes, RegistryAccess access, MaterialStatsId statsId, int cost) {
        return partList(statsId).stream()
                                .filter(part -> runtimeCost(recipes, access, part) == cost)
                                .toList();
    }

    public static List<ToolPartItem> runtimeParts(RecipeManager recipes, RegistryAccess access, List<MaterialStatsId> statsIds, int cost) {
        return statsIds.stream()
                       .flatMap(statsId -> runtimeParts(recipes, access, statsId, cost).stream())
                       .toList();
    }

    public static ItemStack datagenPart(MaterialId material, MaterialStatsId statsId, int cost, RandomSource random) {
        if (!MaterialRegistry.isFullyLoaded())
            return ItemStack.EMPTY;
        List<ToolPartItem> parts = datagenParts(statsId, cost);
        if (parts.isEmpty())
            return ItemStack.EMPTY;
        ToolPartItem part = random == null ? parts.get(0) : parts.get(random.nextInt(parts.size()));
        return withMaterial(part, MaterialRegistry.getMaterial(material).getIdentifier(), 1);
    }

    public static ItemStack runtimePart(RecipeManager recipes, RegistryAccess access, MaterialId material, MaterialStatsId statsId, int cost, RandomSource random) {
        if (!MaterialRegistry.isFullyLoaded())
            return ItemStack.EMPTY;
        List<ToolPartItem> parts = List.of();
        for (int candidateCost = cost; candidateCost > 0 && parts.isEmpty(); candidateCost--) {
            parts = runtimeParts(recipes, access, statsId, candidateCost);
        }
        if (parts.isEmpty())
            return ItemStack.EMPTY;
        ToolPartItem part = random == null ? parts.get(0) : parts.get(random.nextInt(parts.size()));
        return withMaterial(part, MaterialRegistry.getMaterial(material).getIdentifier(), 1);
    }

    public static ItemStack runtimePart(RecipeManager recipes, RegistryAccess access, MaterialId material, List<MaterialStatsId> statsIds, int cost, RandomSource random) {
        CostedPart result = runtimePartWithCost(recipes, access, material, statsIds, cost, random);
        return result.stack();
    }

    public static ItemStack withMaterial(ToolPartItem part, MaterialVariantId material, int count) {
        ItemStack stack = part.withMaterial(material);
        stack.setCount(count);
        return stack;
    }

    public static void rebuildRuntime(RecipeManager recipes, RegistryAccess access) {
        RUNTIME.clear();
        recipes.getAllRecipesFor(TinkerRecipeTypes.PART_BUILDER.get()).stream()
               .sorted(Comparator.comparing(IPartBuilderRecipe::getCost))
               .forEach(recipe -> putRuntimeCost(recipe, access));
        recipes.getAllRecipesFor(TinkerRecipeTypes.CASTING_TABLE.get()).stream()
               .sorted(Comparator.comparing(recipe -> recipe.getId().toString()))
               .filter(MaterialCastingRecipe.class::isInstance)
               .map(MaterialCastingRecipe.class::cast)
               .forEach(recipe -> putRuntimeCast(recipe, access));
        runtimeBuilt = true;
    }

    public static void ensureRuntime(RecipeManager recipes, RegistryAccess access) {
        if (!runtimeBuilt)
            rebuildRuntime(recipes, access);
    }

    public static void clearRuntime() {
        RUNTIME.clear();
        runtimeBuilt = false;
    }

    public static void clearCaches() {
        PARTS.clear();
        DATAGEN_CASTS.clear();
        itemsByPath = null;
        clearRuntime();
    }

    public static PartInfo runtimeInfo(RecipeManager recipes, RegistryAccess access, ToolPartItem part) {
        ensureRuntime(recipes, access);
        return RUNTIME.getOrDefault(part, datagenInfo(part));
    }

    public static int runtimeCost(RecipeManager recipes, RegistryAccess access, ToolPartItem part) {
        return runtimeInfo(recipes, access, part).cost();
    }

    public static Ingredient runtimeCastIngredient(RecipeManager recipes, RegistryAccess access, ToolPartItem part) {
        return runtimeInfo(recipes, access, part).castIngredient();
    }

    public static PartInfo datagenInfo(ToolPartItem part) {
        return new PartInfo(datagenCost(part), datagenCastStacks(part));
    }

    public static int datagenCost(ToolPartItem part) {
        return datagenCosts().getOrDefault(part, DEFAULT_COST);
    }

    public static Ingredient datagenCastIngredient(ToolPartItem part) {
        return datagenInfo(part).castIngredient();
    }

    private static void putRuntimeCost(IPartBuilderRecipe recipe, RegistryAccess access) {
        ItemStack output = recipe.getResultItem(access);
        if (!output.isEmpty()){
            PartInfo old = RUNTIME.get(output.getItem());
            List<ItemStack> casts = old == null ? List.of() : old.casts();
            RUNTIME.putIfAbsent(output.getItem(), new PartInfo(recipe.getCost(), casts));
        }
    }

    public static CostedPart runtimePartWithCost(
            RecipeManager recipes, RegistryAccess access, MaterialId material,
            MaterialStatsId statsId, int maxCost, RandomSource random) {
        return runtimePartWithCost(recipes, access, material, List.of(statsId), maxCost, random);
    }

    public static CostedPart runtimePartWithCost(
            RecipeManager recipes, RegistryAccess access, MaterialId material,
            List<MaterialStatsId> statsIds, int maxCost, RandomSource random) {
        if (!MaterialRegistry.isFullyLoaded())
            return CostedPart.empty();

        for (int candidateCost = maxCost; candidateCost > 0; candidateCost--) {
            List<ToolPartItem> parts = runtimeParts(recipes, access, statsIds, candidateCost);

            if (parts.isEmpty())
                continue;

            ToolPartItem part = random == null ? parts.get(0) : parts.get(random.nextInt(parts.size()));
            ItemStack stack = withMaterial(part, MaterialRegistry.getMaterial(material).getIdentifier(), 1);

            return new CostedPart(stack, candidateCost);
        }

        return CostedPart.empty();
    }

    private static List<ItemStack> datagenCastStacks(ToolPartItem part) {
        return DATAGEN_CASTS.computeIfAbsent(part, DTPartInfoLookup::scanDatagenCastStacks);
    }

    private static List<ToolPartItem> scanParts(MaterialStatsId statsId) {
        return ForgeRegistries.ITEMS.getValues().stream()
                                    .filter(item -> item instanceof ToolPartItem part
                                                    && part.getStatType() == statsId)
                                    .map(item -> (ToolPartItem) item)
                                    .toList();
    }

    private static void putRuntimeCast(MaterialCastingRecipe recipe, RegistryAccess access) {
        ItemStack output = recipe.getResultItem(access);
        if (output.isEmpty())
            return;
        PartInfo old = RUNTIME.get(output.getItem());
        List<ItemStack> casts = old == null ? new ArrayList<>() : new ArrayList<>(old.casts());
        addCastItems(casts, recipe.getCast().getItems());
        int cost = old == null ? DEFAULT_COST : old.cost();
        RUNTIME.put(output.getItem(), new PartInfo(cost, List.copyOf(casts)));
    }

    private static void addCastItems(List<ItemStack> out, ItemStack[] stacks) {
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty() && out.stream().noneMatch(existing -> ItemStack.isSameItemSameTags(existing, stack)))
                out.add(stack.copy());
        }
    }

    private static List<ItemStack> scanDatagenCastStacks(ToolPartItem part) {
        ItemLike[] special = specialCasts(part);
        if (special != null){
            List<ItemStack> stacks = new ArrayList<>(special.length);
            for (ItemLike item : special)
                stacks.add(new ItemStack(item));
            return List.copyOf(stacks);
        }
        return List.copyOf(findCastStacks(part));
    }

    private static List<ItemStack> findCastStacks(ToolPartItem part) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(part);
        if (id == null)
            return List.of();

        String baseName = id.getPath();
        List<ItemStack> stacks = new ArrayList<>(3);
        addCastStack(stacks, findCastItem(id, baseName, SUFFIX_CAST));
        addCastStack(stacks, findCastItem(id, baseName, SUFFIX_SAND));
        addCastStack(stacks, findCastItem(id, baseName, SUFFIX_RED_SAND));
        return stacks;
    }

    private static Item findCastItem(ResourceLocation partId, String baseName, String suffix) {
        Item cast = ForgeRegistries.ITEMS.getValue(new ResourceLocation("tconstruct", baseName + suffix));
        if (cast == null)
            cast = ForgeRegistries.ITEMS.getValue(new ResourceLocation(partId.getNamespace(), baseName + suffix));
        if (cast == null)
            cast = findByPath(baseName + suffix);
        return cast;
    }

    private static Item findByPath(String path) {
        if (itemsByPath == null){
            itemsByPath = new HashMap<>();
            for (Item item : ForgeRegistries.ITEMS.getValues()) {
                ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
                if (id != null)
                    itemsByPath.computeIfAbsent(id.getPath(), key -> new ArrayList<>()).add(item);
            }
        }
        List<Item> items = itemsByPath.get(path);
        return items == null || items.isEmpty() ? null : items.get(0);
    }

    private static void addCastStack(List<ItemStack> stacks, Item item) {
        if (item != null)
            stacks.add(new ItemStack(item));
    }

    public record CostedPart(ItemStack stack, int cost) {
        public static CostedPart empty() {
            return new CostedPart(ItemStack.EMPTY, 0);
        }

        public boolean isEmpty() {
            return stack.isEmpty() || cost <= 0;
        }
    }

    private static ItemLike[] specialCasts(ToolPartItem part) {
        if (part == DreamtinkerToolParts.memoryOrthant.get())
            return new ItemLike[]{DreamtinkerCommon.memory_cast.get()};
        if (part == DreamtinkerToolParts.wishOrthant.get())
            return new ItemLike[]{DreamtinkerCommon.wish_cast.get()};
        if (part == DreamtinkerToolParts.soulOrthant.get())
            return new ItemLike[]{DreamtinkerCommon.soul_cast.get()};
        if (part == DreamtinkerToolParts.personaOrthant.get())
            return new ItemLike[]{DreamtinkerCommon.persona_cast.get()};
        if (part == DreamtinkerToolParts.reasonEmanation.get())
            return new ItemLike[]{DreamtinkerCommon.reason_cast.get()};
        if (part == DreamtinkerToolParts.explode_core.get())
            return new ItemLike[]{DreamtinkerToolParts.explode_core.get()};
        if (part == DreamtinkerToolParts.chainSawCore.get())
            return DreamTinkerSmeltery.chainSawCoreCast.values().toArray(new ItemLike[0]);
        if (part == DreamtinkerToolParts.chainSawTeeth.get())
            return DreamTinkerSmeltery.chainSawTeethCast.values().toArray(new ItemLike[0]);
        if (part == DreamtinkerToolParts.NovaMisc.get())
            return DreamTinkerSmeltery.NovaMiscCast.values().toArray(new ItemLike[0]);
        if (part == DreamtinkerToolParts.NovaRostrum.get())
            return DreamTinkerSmeltery.NovaRostrumCast.values().toArray(new ItemLike[0]);
        if (part == DreamtinkerToolParts.NovaCover.get())
            return DreamTinkerSmeltery.NovaCoverCast.values().toArray(new ItemLike[0]);
        if (part == DreamtinkerToolParts.NovaWrapper.get())
            return DreamTinkerSmeltery.NovaWrapperCast.values().toArray(new ItemLike[0]);
        return null;
    }

    private static Map<Item, Integer> datagenCosts() {
        if (datagenCosts == null){
            Map<Item, Integer> costs = new HashMap<>();
            putCost(costs, DreamtinkerToolParts.explode_core::get, 8);
            putCost(costs, DreamtinkerToolParts.chainSawTeeth::get, 12);
            putCost(costs, DreamtinkerToolParts.chainSawCore::get, 8);
            putCost(costs, DreamtinkerToolParts.NovaCover::get, 2);
            putCost(costs, DreamtinkerToolParts.NovaMisc::get, 3);
            putCost(costs, DreamtinkerToolParts.NovaWrapper::get, 2);
            putCost(costs, DreamtinkerToolParts.NovaRostrum::get, 4);
            putCost(costs, DreamtinkerToolParts.memoryOrthant::get, 8);
            putCost(costs, DreamtinkerToolParts.wishOrthant::get, 3);
            putCost(costs, DreamtinkerToolParts.soulOrthant::get, 3);
            putCost(costs, DreamtinkerToolParts.personaOrthant::get, 3);
            putCost(costs, DreamtinkerToolParts.reasonEmanation::get, 8);
            datagenCosts = costs;
        }
        return datagenCosts;
    }

    private static void putCost(Map<Item, Integer> costs, Supplier<? extends ItemLike> part, int cost) {
        costs.put(part.get().asItem(), cost);
    }

    public record PartInfo(int cost, List<ItemStack> casts) {
        public Ingredient castIngredient() {
            return casts.isEmpty() ? Ingredient.EMPTY : Ingredient.of(casts.toArray(ItemStack[]::new));
        }
    }
}
