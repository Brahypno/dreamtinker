package org.dreamtinker.dreamtinker.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.ForgeMod;
import org.dreamtinker.dreamtinker.common.DreamtinkerAttributes;

public class BurdenBearerEffect extends MobEffect {
    public BurdenBearerEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x68B2CD);
        addAttributeModifier(Attributes.ATTACK_DAMAGE, "99cadfb3-043c-42b2-a251-8725052f823e", 0.15D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        addAttributeModifier(ForgeMod.ENTITY_REACH.get(), "498e978a-dd3b-41cc-9720-e550e8280504", 0.15D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        addAttributeModifier(DreamtinkerAttributes.FATE_VEIL.get(), "b9f9c196-13eb-4d77-a8ac-5ecb451b70c3", 20D, AttributeModifier.Operation.ADDITION);
    }
}
