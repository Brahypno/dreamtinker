package org.dreamtinker.dreamtinker.tools.modifiers.traits.armors;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.ArmorModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.AbsorptionDefenseRate;

public class absorption_defense extends ArmorModifier {
    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        float absorption = context.getEntity().getAbsorptionAmount();
        if (0 < absorption){
            amount *= (1 - modifier.getLevel() * AbsorptionDefenseRate.get().floatValue());
            if (absorption < amount && source.getEntity() instanceof LivingEntity entity)
                entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, modifier.getLevel() * 20, modifier.getLevel()));
        }else
            amount *= (1 + modifier.getLevel() * AbsorptionDefenseRate.get().floatValue());
        
        return amount;
    }

    @Override
    public boolean isNoLevels() {return false;}
}
