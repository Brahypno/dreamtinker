package org.brahypno.dreamtinker.tools.modifiers.traits.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import org.brahypno.dreamtinker.Dreamtinker;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.hook.interaction.UsingToolModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.build.StatBoostModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import static slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper.TANK_HELPER;

public class AutoRefill extends Modifier implements GeneralInteractionModifierHook, MeleeHitModifierHook, UsingToolModifierHook {
    private static final ResourceLocation TAG_LIQUID = Dreamtinker.getLocation("liquid_storage");

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder builder) {
        builder.addHook(this, ModifierHooks.GENERAL_INTERACT, ModifierHooks.MELEE_HIT, ModifierHooks.TOOL_USING);
        builder.addModule(ToolTankHelper.TANK_HANDLER);
        builder.addModule(StatBoostModule.add(ToolTankHelper.CAPACITY_STAT).eachLevel(FluidType.BUCKET_VOLUME));
        super.registerHooks(builder);
    }

    public int getPriority() {
        return DEFAULT_PRIORITY * 100;
    }

    private void storeLiquidInUse(IToolStackView tool) {
        FluidStack fluid = TANK_HELPER.getFluid(tool);
        if (fluid.isEmpty())
            tool.getPersistentData().remove(TAG_LIQUID);
        else
            tool.getPersistentData().putInt(TAG_LIQUID, fluid.getAmount());
    }

    private void restoreLiquidInUse(IToolStackView tool, ModifierEntry modifer) {
        FluidStack fluid = TANK_HELPER.getFluid(tool);
        if (fluid.isEmpty())
            tool.getPersistentData().remove(TAG_LIQUID);
        else {
            int amount = tool.getPersistentData().getInt(TAG_LIQUID);
            if (amount != fluid.getAmount()){
                fluid.setAmount(amount / 2 + fluid.getAmount() / 2);
                TANK_HELPER.setFluid(tool, fluid);
            }
        }
    }

    @Override
    public @NotNull InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
        if (!player.level().isClientSide)
            storeLiquidInUse(tool);

        return InteractionResult.PASS;
    }

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        if (!context.getAttacker().level().isClientSide)
            storeLiquidInUse(tool);
        return knockback;
    }

    @Override
    public void afterStopUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int useDuration, int timeLeft, ModifierEntry activeModifier) {
        Level world = entity.level();
        if (!world.isClientSide)
            restoreLiquidInUse(tool, modifier);
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        if (!context.getAttacker().level().isClientSide)
            restoreLiquidInUse(tool, modifier);
    }
}
