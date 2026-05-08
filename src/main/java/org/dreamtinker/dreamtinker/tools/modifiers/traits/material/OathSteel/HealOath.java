package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.OathSteel;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

import static org.dreamtinker.dreamtinker.tools.modifiers.events.OathGuardPaleSteelEvents.isGuardianProtectedTarget;

public class HealOath extends Modifier implements ModifyDamageModifierHook, InventoryTickModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MODIFY_HURT, ModifierHooks.INVENTORY_TICK);
        super.registerHooks(hookBuilder);
    }

    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        LivingEntity entity = context.getEntity();

        if (entity instanceof ServerPlayer player && context.getLevel() instanceof ServerLevel serverLevel){

            List<LivingEntity> protectedTargets = serverLevel.getEntitiesOfClass(
                    LivingEntity.class,
                    player.getBoundingBox().inflate(16.0D),
                    target -> target != player
                              && target.isAlive()
                              && isGuardianProtectedTarget(player, target));
            float strongest = 1;
            for (LivingEntity target : protectedTargets) {
                float healthRatio = target.getHealth() / target.getMaxHealth();
                if (healthRatio < 0.5F){
                    float bonus = (0.5F - healthRatio) * 0.40F;
                    strongest = Math.max(strongest, bonus);
                }
            }
            amount *= strongest;
        }
        return amount;
    }

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if ((isSelected || isCorrectSlot) && holder instanceof ServerPlayer player && world instanceof ServerLevel serverLevel &&
            world.getGameTime() % 20 == 0){

            List<LivingEntity> protectedTargets = serverLevel.getEntitiesOfClass(
                    LivingEntity.class,
                    player.getBoundingBox().inflate(16.0D),
                    target -> target != player
                              && target.isAlive()
                              && isGuardianProtectedTarget(player, target));
            for (LivingEntity target : protectedTargets) {
                float healthRatio = target.getHealth() / target.getMaxHealth();
                if (healthRatio < 0.5F && target.getActiveEffects().stream().anyMatch(o -> o.getEffect().equals(MobEffects.REGENERATION))){
                    target.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20 * modifier.getLevel(), modifier.getLevel() - 1));
                }
            }
        }
    }
}
