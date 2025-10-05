package org.dreamtinker.dreamtinker.plugin.JEI;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.dreamtinker.dreamtinker.common.DreamtinkerCommon;
import org.dreamtinker.dreamtinker.library.recipe.virtual.WorldRitualEntry;
import org.dreamtinker.dreamtinker.tools.data.DreamtinkerMaterialIds;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.recipe.ingredient.EntityIngredient;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import java.util.ArrayList;
import java.util.List;

import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.SoulCastLoveLootChance;
import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.voidPearlDropRate;
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
                List.of(new ItemStack(TinkerCommons.soulGlass)),    // A 方块图标
                hate,
                null,
                EntityIngredient.of(EntityType.WITHER_SKELETON, EntityType.PLAYER, EntityType.WITHER, EntityType.PHANTOM), // ✅ 一组实体（这里只有凋零骷髅）
                null, true, 320, 6, null, null, null
        ));
        ItemStack love = new ItemStack(DreamtinkerCommon.soul_cast.get(), 1);
        love.getOrCreateTag().putBoolean("love", true);
        // C) 生物繁殖 + 羽毛 掉落 25% 产出 B（示例限定鸡；若不限定实体可传 null）
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
                List.of(new ItemStack(Items.KELP)),
                new ItemStack(DreamtinkerCommon.memory_cast.get()),
                null,
                null,
                null, null, null, null, null,
                true,  // underwater
                true   // drowning
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
}

