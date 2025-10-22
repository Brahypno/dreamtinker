package org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy.material.etherium;

import com.aizistral.enigmaticlegacy.config.EtheriumConfigHandler;
import com.aizistral.enigmaticlegacy.objects.Vector3;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.ArmorModifier;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class etherium_protection extends ArmorModifier {

    private boolean has_shield(Player player) {
        return DTModifierCheck.ModifierALLBody(player, this.getId()) &&
               player.getHealth() / player.getMaxHealth() <= EtheriumConfigHandler.instance().getShieldThreshold(player).asMultiplier(false);
    }

    @Override
    public boolean isDamageBlocked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount) {
        if (context.getEntity() instanceof Player player && !player.level().isClientSide && EquipmentSlot.CHEST == slotType)//Full set bonus, anyway
            if (has_shield(player) && (source.getDirectEntity() instanceof AbstractHurtingProjectile || source.getDirectEntity() instanceof AbstractArrow)){
                player.level().playSound(null, player.blockPosition(), EtheriumConfigHandler.instance().getShieldTriggerSound(), SoundSource.PLAYERS, 1.0F,
                                         0.9F + (float) (Math.random() * 0.1D));
                return true;
            }
        return false;
    }

    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        if (context.getEntity() instanceof Player player && !player.level().isClientSide && EquipmentSlot.CHEST == slotType)//Full set bonus, anyway
            if (has_shield(player)){
                LivingEntity attacker = ((LivingEntity) source.getEntity());
                if (null != attacker){
                    Vector3 vec = Vector3.fromEntityCenter(player).subtract(Vector3.fromEntityCenter(attacker).normalize());
                    attacker.knockback(0.75F, vec.x, vec.z);
                }
                player.level().playSound(null, player.blockPosition(), EtheriumConfigHandler.instance().getShieldTriggerSound(), SoundSource.PLAYERS, 1.0F,
                                         0.9F + (float) (Math.random() * 0.1D));
                player.level().playSound(null, player.blockPosition(), EtheriumConfigHandler.instance().getShieldTriggerSound(), SoundSource.PLAYERS, 1.0F,
                                         0.9F + (float) (Math.random() * 0.1D));
                amount *= EtheriumConfigHandler.instance().getShieldReduction().asModifierInverted();
            }
        return amount;
    }

}
