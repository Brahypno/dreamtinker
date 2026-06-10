package org.brahypno.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy.material.etherium;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.phys.Vec3;
import org.brahypno.dreamtinker.utils.CompactUtils.EnigmaticLegacyCompact;
import org.brahypno.dreamtinker.utils.DTModifierCheck;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.DamageBlockModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class EtheriumProtection extends Modifier implements DamageBlockModifierHook, ModifyDamageModifierHook {

    private static Vec3 fromEntityCenter(Entity entity) {
        return new Vec3(entity.getX(), entity.getY() - entity.getMyRidingOffset() + entity.getBbHeight() / 2.0D, entity.getZ());
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.DAMAGE_BLOCK, ModifierHooks.MODIFY_HURT);
        super.registerHooks(hookBuilder);
    }

    private boolean has_shield(Player player) {
        return DTModifierCheck.ModifierALLBody(player, this.getId()) &&
               player.getHealth() / player.getMaxHealth() <= EnigmaticLegacyCompact.etheriumShieldThresholdMultiplier(player);
    }

    @Override
    public boolean isDamageBlocked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount) {
        if (context.getEntity() instanceof Player player && !player.level().isClientSide && EquipmentSlot.CHEST == slotType)
            if (has_shield(player) && (source.getDirectEntity() instanceof AbstractHurtingProjectile || source.getDirectEntity() instanceof AbstractArrow)){
                player.level().playSound(null, player.blockPosition(), EnigmaticLegacyCompact.etheriumShieldTriggerSound(), SoundSource.PLAYERS, 1.0F,
                                         0.9F + (float) (Math.random() * 0.1D));
                return true;
            }

        return false;
    }

    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        if (context.getEntity() instanceof Player player && !player.level().isClientSide && EquipmentSlot.CHEST == slotType)
            if (has_shield(player)){
                if (source.getEntity() instanceof LivingEntity attacker){
                    Vec3 vec = fromEntityCenter(player).subtract(fromEntityCenter(attacker).normalize());
                    attacker.knockback(0.75F, vec.x, vec.z);
                }

                player.level().playSound(null, player.blockPosition(), EnigmaticLegacyCompact.etheriumShieldTriggerSound(), SoundSource.PLAYERS, 1.0F,
                                         0.9F + (float) (Math.random() * 0.1D));
                player.level().playSound(null, player.blockPosition(), EnigmaticLegacyCompact.etheriumShieldTriggerSound(), SoundSource.PLAYERS, 1.0F,
                                         0.9F + (float) (Math.random() * 0.1D));

                amount *= (float) EnigmaticLegacyCompact.etheriumShieldReductionModifierInverted();
            }

        return amount;
    }
}