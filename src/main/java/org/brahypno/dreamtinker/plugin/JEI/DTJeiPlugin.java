package org.brahypno.dreamtinker.plugin.JEI;

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
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.common.DreamtinkerCommon;
import org.brahypno.dreamtinker.common.DreamtinkerTagKeys;
import org.brahypno.dreamtinker.library.recipe.virtual.WorldRitualEntry;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.recipe.ingredient.EntityIngredient;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;
import slimeknights.tconstruct.tools.stats.StatlessMaterialStats;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.brahypno.dreamtinker.config.DreamtinkerCachedConfig.*;
import static org.brahypno.dreamtinker.plugin.JEI.WorldRitualCategory.CelestialTypes.CELESTIAL;
import static org.brahypno.dreamtinker.plugin.JEI.WorldRitualCategory.WORLD_RITUAL;

@JeiPlugin
public final class DTJeiPlugin implements IModPlugin {
    private static final List<MaterialStatsId> MOONLIGHT_PART_STATS = List.of(
            HeadMaterialStats.ID,
            StatlessMaterialStats.BINDING.getIdentifier(),
            HandleMaterialStats.ID
    );

    @Override
    public @NotNull ResourceLocation getPluginUid() {return WorldRitualCategory.UID;}

    @Override
    public void registerIngredients(@NotNull IModIngredientRegistration reg) {
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
        if (ModList.get().isLoaded("ars_nouveau")){
            ArsJeiCompact.registerRecipes(reg);
        }

        List<WorldRitualEntry> list = new ArrayList<>();

        // B) 白天 & 极限高度 击杀凋零骷髅，附近方块A → 替换为方块B（仅展示）
        ItemStack hate = new ItemStack(DreamtinkerCommon.soul_cast.get(), 1);
        hate.getOrCreateTag().putBoolean("desire", true);
        list.add(new WorldRitualEntry(
                WorldRitualEntry.Trigger.KILL_ENTITY,
                null, null,
                Ingredient.of(TinkerCommons.soulGlass),    // A 方块图标
                hate,
                null,
                EntityIngredient.of(EntityType.WITHER_SKELETON, EntityType.PLAYER, EntityType.WITHER, EntityType.PHANTOM),
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
                WorldRitualEntry.Trigger.USE_ITEM,
                Ingredient.of(Items.FLINT_AND_STEEL),
                null,
                Ingredient.of(Items.KELP),
                new ItemStack(DreamtinkerCommon.memory_cast.get()),
                null,
                null,
                null, null, null, null, null,
                "jei.dreamtinker.condition.under_water",  // underwater
                true   // drowning
        ));
        list.add(new WorldRitualEntry(
                WorldRitualEntry.Trigger.FORTUNE_LOOTING,
                null,
                null,
                anyOfBlockTags(Tags.Blocks.ORES_COPPER, Tags.Blocks.ORES_GOLD, Dreamtinker.forgeBlockTag("ores/lead"),
                               Dreamtinker.forgeBlockTag("ores/silver")),
                new ItemStack(DreamtinkerCommon.raw_stibnite.get()),
                null,
                null,
                null, null, null, null, 0.05,
                null,  // underwater
                false   // drowning
        ));
        list.add(new WorldRitualEntry(
                WorldRitualEntry.Trigger.FORTUNE_LOOTING,
                null,
                null,
                anyOfBlockTags(DreamtinkerTagKeys.Blocks.drop_peach),
                new ItemStack(DreamtinkerCommon.white_peach.get()),
                null,
                null,
                null, null, null, null, 0.2,
                null,  // underwater
                false   // drowning
        ));

        list.add(new WorldRitualEntry(
                WorldRitualEntry.Trigger.HIT_ENTITY,
                Ingredient.of(Dreamtinker.mcItemTag("anvil")),
                null,
                Ingredient.of(Tags.Items.GLASS),
                new ItemStack(DreamtinkerCommon.poisonousHomunculus.get()),
                null,
                EntityIngredient.of(EntityType.VILLAGER),
                null, null, null, null, null,
                null,  // underwater
                false   // drowning
        ));
        list.add(new WorldRitualEntry(
                WorldRitualEntry.Trigger.HIT_ENTITY,
                Ingredient.of(Dreamtinker.mcItemTag("anvil")),
                null,
                Ingredient.of(Tags.Items.GLASS),
                new ItemStack(DreamtinkerCommon.evilHomunculus.get()),
                null,
                EntityIngredient.of(TinkerTags.EntityTypes.ILLAGERS),
                null, null, null, null, null,
                null,  // underwater
                false   // drowning
        ));
        list.add(new WorldRitualEntry(
                WorldRitualEntry.Trigger.USE_ITEM,
                Ingredient.of(Items.GLASS_BOTTLE),
                null,
                Ingredient.of(Items.BEEHIVE, Items.BEE_NEST),
                new ItemStack(DreamtinkerCommon.rainbow_honey.get()),
                null,
                EntityIngredient.of(EntityType.PLAYER),
                null, null, null, null, rainbowHoneyRate.get(),
                "jei.dreamtinker.condition.in_rain",  // underwater
                false   // drowning
        ));

        reg.addRecipes(WORLD_RITUAL, list);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration reg) {
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

