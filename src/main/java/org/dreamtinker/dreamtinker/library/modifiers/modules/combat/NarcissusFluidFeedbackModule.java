package org.dreamtinker.dreamtinker.library.modifiers.modules.combat;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MonsterMeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModuleBuilder;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;
import java.util.List;

public record NarcissusFluidFeedbackModule(ModifierCondition<IToolStackView> condition)
        implements InventoryTickModifierHook, ProjectileLaunchModifierHook, OnAttackedModifierHook, ModifyDamageModifierHook, MeleeHitModifierHook,
        MonsterMeleeHitModifierHook, ModifierModule, ModifierCondition.ConditionalModule<IToolStackView> {
    public static final RecordLoadable<NarcissusFluidFeedbackModule> LOADER =
            RecordLoadable.create(ModifierCondition.TOOL_FIELD, NarcissusFluidFeedbackModule::new);
    private static final List<ModuleHook<?>> DEFAULT_HOOKS =
            HookProvider.<NarcissusFluidFeedbackModule>defaultHooks(ModifierHooks.INVENTORY_TICK, ModifierHooks.PROJECTILE_LAUNCH, ModifierHooks.ON_ATTACKED,
                                                                    ModifierHooks.MODIFY_HURT, ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT);

    @ApiStatus.Internal
    public NarcissusFluidFeedbackModule {}

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (!world.isClientSide && condition.matches(tool, modifier) && (isSelected || isCorrectSlot)){
            NarcissusFluidFeedbacks.consumeInventoryTick(holder, modifier.getLevel());
        }
    }

    @Override
    public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
        if (!shooter.level().isClientSide && primary && condition.matches(tool, modifier)){
            NarcissusFluidFeedbacks.consumeProjectileLaunch(shooter, projectile, modifier.getLevel());
        }
    }

    @Override
    public void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        if (!context.getEntity().level().isClientSide && condition.matches(tool, modifier)){
            NarcissusFluidFeedbacks.consumeInventoryTick(context.getEntity(), modifier.getLevel());
        }
    }

    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        if (!context.getEntity().level().isClientSide && condition.matches(tool, modifier)){
            return NarcissusFluidFeedbacks.consumeGuardForDamage(context.getEntity(), source, amount, modifier.getLevel());
        }
        return amount;
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        if (!context.getLevel().isClientSide && condition.matches(tool, modifier)){
            NarcissusFluidFeedbacks.consumeMeleeHit(context, modifier.getLevel(), damageDealt);
        }
    }

    @Override
    public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
        afterMeleeHit(tool, modifier, context, damage);
    }

    @Override
    public List<ModuleHook<?>> getDefaultHooks() {
        return DEFAULT_HOOKS;
    }

    @Override
    public RecordLoadable<NarcissusFluidFeedbackModule> getLoader() {
        return LOADER;
    }

    public static class Builder extends ModuleBuilder.Stack<Builder> {
        public NarcissusFluidFeedbackModule build() {
            return new NarcissusFluidFeedbackModule(condition);
        }
    }
}
