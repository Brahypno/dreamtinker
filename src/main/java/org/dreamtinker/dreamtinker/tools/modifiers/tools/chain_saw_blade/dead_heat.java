package org.dreamtinker.dreamtinker.tools.modifiers.tools.chain_saw_blade;

import net.minecraft.world.item.Tier;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuelLookup;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.List;

import static slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper.TANK_HELPER;

public class dead_heat extends BattleModifier {
    @Override
    public int getPriority() {
        return 10;
    }

    private final List<Tier> tiers;
    @Nullable
    private MeltingFuel lastRecipe;

    public dead_heat() {
        tiers = TierSortingRegistry.getSortedTiers();
    }

    @Nullable
    protected MeltingFuel findRecipe(Fluid fluid) {
        if (lastRecipe != null && lastRecipe.matches(fluid)){
            return lastRecipe;
        }
        MeltingFuel recipe = MeltingFuelLookup.findFuel(fluid);
        if (recipe != null){
            lastRecipe = recipe;
        }
        return recipe;
    }

    @Override
    public float onGetMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        if (!context.getAttacker().isUsingItem())
            return damage;
        FluidStack fluid = TANK_HELPER.getFluid(tool);
        if (fluid.isEmpty())
            return damage;
        MeltingFuel recipe = findRecipe(fluid.getFluid());
        if (null != recipe){
            float rate = recipe.getRate();
            return damage * (1.0f + rate / 100.0f);
        }
        return damage;
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        if (!context.getAttacker().isUsingItem())
            return;
        if (null != context.getLivingTarget()){
            Tier tier = tool.getStats().get(ToolStats.HARVEST_TIER);
            int idx = tiers.indexOf(tier);
            context.getLivingTarget().invulnerableTime = Math.round((float) context.getLivingTarget().invulnerableTime / idx);
        }
    }
}
