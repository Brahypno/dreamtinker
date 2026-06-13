package org.brahypno.dreamtinker.smeltery.data;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.common.DreamtinkerCommon;
import org.brahypno.dreamtinker.fluids.DreamtinkerFluids;
import org.brahypno.esotericismtinker.fluids.EsotericismTinkerFluids;
import org.brahypno.esotericismtinker.smeltery.EsotericismTinkerSmeltery;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.recipe.data.ICommonRecipeHelper;
import slimeknights.mantle.recipe.data.IRecipeHelper;
import slimeknights.mantle.recipe.ingredient.EntityIngredient;
import slimeknights.tconstruct.library.data.recipe.IMaterialRecipeHelper;
import slimeknights.tconstruct.library.data.recipe.ISmelteryRecipeHelper;
import slimeknights.tconstruct.library.data.recipe.IToolRecipeHelper;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipeBuilder;

import java.util.function.Consumer;

public class DreamtinkerEntityTransmuteRecipeProvider implements IMaterialRecipeHelper, IToolRecipeHelper, IConditionBuilder, IRecipeHelper, ICommonRecipeHelper, ISmelteryRecipeHelper {

    String Entity_Melting_folder = "smeltery/entity_melting/";

    String Casting_folder = "smeltery/casting/";

    public void addEntityMeltingRecipes(Consumer<FinishedRecipe> consumer) {

        String headFolder = "smeltery/entity_melting/heads/";

        EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.WARDEN), DreamtinkerFluids.molten_echo_shard.result(FluidValues.GEM_SHARD), 5)
                                  .save(consumer, location(Entity_Melting_folder + "molten_echo_shard"));
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
