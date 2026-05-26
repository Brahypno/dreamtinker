package org.dreamtinker.dreamtinker.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class DaylostEffect extends MobEffect {
    public DaylostEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xF2B84B);
        addAttributeModifier(Attributes.ATTACK_DAMAGE, "af64559d-41da-4e11-95c4-1a76f975ebcb", 0.15D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        addAttributeModifier(Attributes.MAX_HEALTH, "e2c20862-b806-461d-a5db-b591a652aabe", 0.15D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "046391c7-d1eb-43a7-9333-5224a2914f93", 0.15D, AttributeModifier.Operation.ADDITION);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return List.of();
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        entity.removeEffect(MobEffects.BLINDNESS);
    }
}
