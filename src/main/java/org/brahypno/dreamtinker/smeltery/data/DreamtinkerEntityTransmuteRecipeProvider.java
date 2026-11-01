package org.brahypno.dreamtinker.smeltery.data;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.registries.ForgeRegistries;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.common.DreamtinkerCommon;
import org.brahypno.dreamtinker.fluids.DreamtinkerFluids;
import org.brahypno.dreamtinker.tools.data.material.DreamtinkerMaterialDataProvider;
import org.brahypno.esotericismtinker.fluids.EsotericismTinkerFluids;
import org.brahypno.esotericismtinker.smeltery.EsotericismTinkerSmeltery;
import org.brahypno.esotericismtinker.smeltery.data.ByproductEntityMeltingRecipeBuilder;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.recipe.data.ICommonRecipeHelper;
import slimeknights.mantle.recipe.data.IRecipeHelper;
import slimeknights.mantle.recipe.ingredient.EntityIngredient;
import slimeknights.mantle.registration.deferred.PotionDeferredRegister.PotionType;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.fluids.fluids.PotionFluidType;
import slimeknights.tconstruct.library.data.recipe.IMaterialRecipeHelper;
import slimeknights.tconstruct.library.data.recipe.ISmelteryRecipeHelper;
import slimeknights.tconstruct.library.data.recipe.IToolRecipeHelper;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipeBuilder;
import slimeknights.tconstruct.shared.TinkerEffects;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.function.Consumer;

public class DreamtinkerEntityTransmuteRecipeProvider implements IMaterialRecipeHelper, IToolRecipeHelper, IConditionBuilder, IRecipeHelper, ICommonRecipeHelper, ISmelteryRecipeHelper {

    String Entity_Melting_folder = "smeltery/entity_melting/";

    String Casting_folder = "smeltery/casting/";

    public void addEntityMeltingRecipes(Consumer<FinishedRecipe> consumer) {

        EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.WARDEN), DreamtinkerFluids.molten_echo_shard.result(FluidValues.GEM_SHARD), 5)
                                  .save(consumer, location(Entity_Melting_folder + "molten_echo_shard"));

        addByproductEntityMeltingRecipes(consumer);
        addCompatByproductEntityMeltingRecipes(consumer);
    }

    private void addByproductEntityMeltingRecipes(Consumer<FinishedRecipe> consumer) {
        String folder = Entity_Melting_folder + "byproduct/";

        ByproductEntityMeltingRecipeBuilder.melting(EntityIngredient.of(TinkerTags.EntityTypes.BEES), TinkerFluids.honey.result(25), 2)
                                           .addByproduct(TinkerFluids.venom.result(5))
                                           .save(consumer, location(folder + "bee"));
        // Keep the shared regeneration and strength recipes intact for their other entities.
        ByproductEntityMeltingRecipeBuilder.melting(EntityIngredient.of(TinkerTags.EntityTypes.GHASTS),
                                                    PotionFluidType.potionResult(Potions.REGENERATION, 50), 2)
                                           .addByproduct(DreamtinkerFluids.liquid_trist.result(10))
                                           .save(consumer, location(folder + "ghast"));
        ByproductEntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.ENDERMAN), TinkerFluids.moltenEnder.result(25), 2)
                                           .addByproduct(DreamtinkerFluids.molten_void.result(5))
                                           .save(consumer, location(folder + "enderman"));
        ByproductEntityMeltingRecipeBuilder.melting(EntityIngredient.of(TinkerTags.EntityTypes.VILLAGERS), TinkerFluids.moltenEmerald.result(25), 5)
                                           .addByproduct(DreamtinkerFluids.half_festering_blood.result(5))
                                           .save(consumer, location(folder + "villager"));
        ByproductEntityMeltingRecipeBuilder.melting(EntityIngredient.of(TinkerTags.EntityTypes.ILLAGERS), TinkerFluids.moltenEmerald.result(25), 2)
                                           .addByproduct(DreamtinkerFluids.festering_blood.result(5))
                                           .save(consumer, location(folder + "illager"));
        ByproductEntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.RAVAGER), PotionFluidType.potionResult(Potions.STRENGTH, 50), 4)
                                           .addByproduct(TinkerFluids.meatSoup.result(10))
                                           .save(consumer, location(folder + "ravager"));

        // Match all sky/ender slimes by type, including those without a metal core.
        ByproductEntityMeltingRecipeBuilder.melting(EntityIngredient.of(TinkerWorld.skySlimeEntity.get()), TinkerFluids.skySlime.result(25), 2)
                                           .addByproduct(TinkerFluids.moltenSteel.result(5))
                                           .save(consumer, location(folder + "sky_slime"));
        ByproductEntityMeltingRecipeBuilder.melting(EntityIngredient.of(TinkerWorld.enderSlimeEntity.get()), TinkerFluids.enderSlime.result(25), 2)
                                           .addByproduct(TinkerFluids.moltenKnightmetal.result(5))
                                           .save(consumer, location(folder + "ender_slime"));
    }

    private void addCompatByproductEntityMeltingRecipes(Consumer<FinishedRecipe> consumer) {
        String folder = Entity_Melting_folder + "byproduct/";
        Consumer<FinishedRecipe> bornInChaos = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("born_in_chaos_v1"));
        ByproductEntityMeltingRecipeBuilder.melting(
                                                   EntityIngredient.of(ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation("born_in_chaos_v1", "diamond_termite"))),
                                                   DreamtinkerFluids.molten_dark_metal.result(10), 1)
                                           .addByproduct(TinkerFluids.moltenDiamond.result(5))
                                           .save(bornInChaos, location(folder + "diamond_termite"));

        Consumer<FinishedRecipe> undergarden = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("undergarden"));
        ByproductEntityMeltingRecipeBuilder.melting(
                                                   EntityIngredient.of(ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation("undergarden", "forgotten_guardian"))),
                                                   DreamtinkerFluids.molten_forgotten_metal.result(10), 2)
                                           .addByproduct(DreamtinkerFluids.molten_cloggrum.result(5))
                                           .save(undergarden, location(folder + "forgotten_guardian"));

        Consumer<FinishedRecipe> legendaryMonsters = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("legendary_monsters"));
        ByproductEntityMeltingRecipeBuilder.melting(
                                                   EntityIngredient.of(ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation("legendary_monsters", "shulker_mimic"))),
                                                   PotionFluidType.potionResult(TinkerEffects.levitationPotion.get(PotionType.NORMAL), 50), 3)
                                           .addByproduct(TinkerFluids.moltenEnder.result(5))
                                           .save(legendaryMonsters, location(folder + "shulker_mimic"));
        ByproductEntityMeltingRecipeBuilder.melting(
                                                   EntityIngredient.of(ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation("legendary_monsters", "endersent"))),
                                                   TinkerFluids.moltenEnder.result(25), 2)
                                           .addByproduct(DreamtinkerFluids.molten_void.result(5))
                                           .save(legendaryMonsters, location(folder + "endersent"));

        Consumer<FinishedRecipe> goety = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("goety"));
        ByproductEntityMeltingRecipeBuilder.melting(
                                                   EntityIngredient.of(ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation("goety", "ender_keeper"))),
                                                   TinkerFluids.moltenEnder.result(25), 2)
                                           .addByproduct(DreamtinkerFluids.molten_void.result(5))
                                           .save(goety, location(folder + "ender_keeper"));
    }

    public void addTransmuteRecipes(Consumer<FinishedRecipe> consumer) {

        ItemCastingRecipeBuilder.basinRecipe(EsotericismTinkerSmeltery.ashenHeater)
                                .setCast(DreamtinkerCommon.UnbornDragonEgg, true)
                                .setFluidAndTime(EsotericismTinkerFluids.molten_ender_ash, FluidValues.BRICK_BLOCK * 9)
                                .save(consumer, location(Casting_folder + "ashen_heater"));
        ItemCastingRecipeBuilder.basinRecipe(EsotericismTinkerSmeltery.ashenAccel)
                                .setCast(DreamtinkerCommon.UnbornSnifferEgg, true)
                                .setFluidAndTime(EsotericismTinkerFluids.molten_ender_ash, FluidValues.BRICK_BLOCK * 9)
                                .save(consumer, location(Casting_folder + "ashen_accelerator"));
    }

    @Override
    public @NotNull String getModId() {
        return Dreamtinker.MODID;
    }

}
