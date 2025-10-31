package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.despair_gem;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EquipmentSlot;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.ArmorModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class requiem extends ArmorModifier {
    @Override
    public boolean isNoLevels() {return false;}

    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        return amount -
               context.getEntity().getActiveEffects().stream()
                      .filter(e -> e.getEffect().getCategory() == MobEffectCategory.HARMFUL).count() / 4.0f * modifier.getLevel();
    }

    @Override
    public float getProtectionModifier(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float modifierValue) {
        return modifierValue * (null == source.getEntity() ? (1 + .2f * modifier.getLevel()) : 1.0f);
    }


}
