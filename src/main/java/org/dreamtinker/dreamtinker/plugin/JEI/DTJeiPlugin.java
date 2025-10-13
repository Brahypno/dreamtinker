package org.dreamtinker.dreamtinker.plugin.JEI;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.dreamtinker.dreamtinker.common.DreamtinkerCommon;
import org.dreamtinker.dreamtinker.common.DreamtinkerTagkeys;
import org.dreamtinker.dreamtinker.library.recipe.virtual.WorldRitualEntry;
import org.dreamtinker.dreamtinker.tools.data.DreamtinkerMaterialIds;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.recipe.ingredient.EntityIngredient;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.*;
import static org.dreamtinker.dreamtinker.plugin.JEI.WorldRitualCategory.CelestialTypes.CELESTIAL;
import static org.dreamtinker.dreamtinker.plugin.JEI.WorldRitualCategory.WORLD_RITUAL;

@JeiPlugin
public final class DTJeiPlugin implements IModPlugin {
    @Override
    public @NotNull ResourceLocation getPluginUid() {return WorldRitualCategory.UID;}

    @Override
    public void registerIngredients(IModIngredientRegistration reg) {
        // 供 JEI 索引的一组“样本”成分（可以很小的集合）
        List<WorldRitualCategory.CelestialTypes.CelestialIcon> samples = new ArrayList<>();
        samples.add(WorldRitualCategory.CelestialTypes.CelestialIcon.sun());
        for (int i = 0; i < 8; i++)
            samples.add(WorldRitualCategory.CelestialTypes.CelestialIcon.moon(i));

        reg.register(CELESTIAL, samples, new WorldRitualCategory.CelestialTypes.CelestialHelper(), new WorldRitualCategory.CelestialTypes.CelestialRenderer());
    }


    @Override
    public void registerCategories(IRecipeCategoryRegistration reg) {
        IGuiHelper g = reg.getJeiHelpers().getGuiHelper();
        reg.addRecipeCategories(new WorldRitualCategory(g));
    }

    @Override
    public void registerRecipes(IRecipeRegistration reg) {
        List<WorldRitualEntry> list = new ArrayList<>();

        // A) 蓝冰 + 水 + 月相 → 工具部件
        List<ToolPartItem> headParts =
                ForgeRegistries.ITEMS.getValues().stream().filter(item -> item instanceof ToolPartItem part && part.getStatType() == HeadMaterialStats.ID)
                                     .map(item -> (ToolPartItem) item).toList();
        MaterialVariantId mli = MaterialRegistry.getMaterial(DreamtinkerMaterialIds.moonlight_ice.getId()).getIdentifier();
        for (ToolPartItem item : headParts) {
            list.add(new WorldRitualEntry(
                    WorldRitualEntry.Trigger.ITEM_IN_FLUID,
                    Ingredient.of(Items.BLUE_ICE),
                    new FluidStack(Fluids.WATER, 1000),
                    null,
                    item.withMaterial(mli),
                    null,
                    null,                                   // 没有实体条件
                    java.util.List.of(0, 4), null, null, null, null, null, null
            ));
        }

        // B) 白天 & 极限高度 击杀凋零骷髅，附近方块A → 替换为方块B（仅展示）
        ItemStack hate = new ItemStack(DreamtinkerCommon.soul_cast.get(), 1);
        hate.getOrCreateTag().putBoolean("desire", true);
        list.add(new WorldRitualEntry(
                WorldRitualEntry.Trigger.KILL_ENTITY,
                null, null,
                Ingredient.of(TinkerCommons.soulGlass),    // A 方块图标
                hate,
                null,
                EntityIngredient.of(EntityType.WITHER_SKELETON, EntityType.PLAYER, EntityType.WITHER, EntityType.PHANTOM), // ✅ 一组实体（这里只有凋零骷髅）
                null, true, 320, 6, null, null, null
        ));
        ItemStack love = new ItemStack(DreamtinkerCommon.soul_cast.get(), 1);
        love.getOrCreateTag().putBoolean("love", true);

        list.add(new WorldRitualEntry(
                WorldRitualEntry.Trigger.BREED_ENTITY,
                Ingredient.of(Items.FEATHER),
                null, null,
                love,
                null,
                EntityIngredient.of(EntityType.CHICKEN, EntityType.COW, EntityType.MOOSHROOM, EntityType.PIG, EntityType.RABBIT, EntityType.SHEEP,
                                    EntityType.GOAT, EntityType.HOGLIN, EntityType.PANDA, EntityType.HORSE, EntityType.DONKEY, EntityType.FOX,
                                    EntityType.TURTLE, EntityType.CAT, EntityType.WOLF, EntityType.BEE, EntityType.AXOLOTL),//They said have child
                null, null, null, 6, SoulCastLoveLootChance.get(), null, null
        ));

        list.add(new WorldRitualEntry(
                WorldRitualEntry.Trigger.ITEM_OUT_OF_WORLD,
                Ingredient.of(Items.ENDER_PEARL),
                null, null,
                new ItemStack(DreamtinkerCommon.void_pearl.get()),
                null,
                null,
                null, null, null, null, voidPearlDropRate.get(), null, null
        ));

        // E) 水下且“溺水”状态，用打火石点燃海带
        list.add(new WorldRitualEntry(
                WorldRitualEntry.Trigger.USE_ITEM_UNDERWATER,
                Ingredient.of(Items.FLINT_AND_STEEL),
                null,
                Ingredient.of(Items.KELP),
                new ItemStack(DreamtinkerCommon.memory_cast.get()),
                null,
                null,
                null, null, null, null, null,
                true,  // underwater
                true   // drowning
        ));
        list.add(new WorldRitualEntry(
                WorldRitualEntry.Trigger.FORTUNE_LOOTING,
                null,
                null,
                anyOfBlockTags(Tags.Blocks.ORES_COPPER, Tags.Blocks.ORES_GOLD, forgeBlockTag("ores/lead"), forgeBlockTag("ores/silver")),
                new ItemStack(DreamtinkerCommon.raw_stibnite.get()),
                null,
                null,
                null, null, null, null, AntimonyLootChance.get(),
                false,  // underwater
                false   // drowning
        ));
        list.add(new WorldRitualEntry(
                WorldRitualEntry.Trigger.FORTUNE_LOOTING,
                null,
                null,
                anyOfBlockTags(DreamtinkerTagkeys.Blocks.drop_peach),
                new ItemStack(DreamtinkerCommon.white_peach.get()),
                null,
                null,
                null, null, null, null, WhitePeachLootChance.get(),
                false,  // underwater
                false   // drowning
        ));

        reg.addRecipes(WORLD_RITUAL, list);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration reg) {
        reg.addRecipeCatalyst(new ItemStack(Items.BLUE_ICE), WORLD_RITUAL);
        reg.addRecipeCatalyst(new ItemStack(Items.FEATHER), WORLD_RITUAL);
        reg.addRecipeCatalyst(new ItemStack(Items.ENDER_PEARL), WORLD_RITUAL);
        reg.addRecipeCatalyst(new ItemStack(Items.FLINT_AND_STEEL), WORLD_RITUAL);
    }

    @SafeVarargs
    public static Ingredient anyOfTags(TagKey<Item>... tags) {
        if (tags == null || tags.length == 0)
            return Ingredient.EMPTY;
        // Ingredient.merge 会把多个 Ingredient 合并为“任意一个匹配即可”
        return Ingredient.merge(
                Arrays.stream(tags)
                      .filter(Objects::nonNull)
                      .map(Ingredient::of)
                      .collect(Collectors.toList())
        );
    }

    private static TagKey<Item> forgeTag(String name) {
        return TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation("forge", name));
    }

    private static TagKey<Block> forgeBlockTag(String name) {
        return TagKey.create(ForgeRegistries.BLOCKS.getRegistryKey(), new ResourceLocation("forge", name));
    }

    @SafeVarargs
    public static Ingredient anyOfBlockTags(TagKey<Block>... blockTags) {
        if (blockTags == null || blockTags.length == 0)
            return Ingredient.EMPTY;

        // 用 LinkedHashSet 去重并保持稳定顺序
        Set<Item> items = new LinkedHashSet<>();
        for (TagKey<Block> tag : blockTags) {
            if (tag == null)
                continue;
            // 运行期通过 Forge 的 tag 视图遍历标签成员
            Stream<Item> s = Objects.requireNonNull(ForgeRegistries.BLOCKS.tags())
                                    .getTag(tag).stream()        // Stream<Holder<Block>>
                                    .map(Block::asItem)          // Item（无物品时为 Items.AIR）
                                    .filter(i -> i != Items.AIR);
            s.forEach(items::add);
        }

        if (items.isEmpty())
            return Ingredient.EMPTY;
        return Ingredient.of(items.toArray(Item[]::new));
    }
}

