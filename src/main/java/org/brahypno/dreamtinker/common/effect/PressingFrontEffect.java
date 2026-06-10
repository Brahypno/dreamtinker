package org.brahypno.dreamtinker.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.brahypno.dreamtinker.utils.DTHelper;

import javax.annotation.Nullable;
import java.util.UUID;

public class PressingFrontEffect extends MobEffect {
    private static final UUID ARMOR_UUID = UUID.fromString("6e51cf5a-3c45-4c2d-9dd5-0e2e6b6d7b01");
    private static final UUID TOUGHNESS_UUID = UUID.fromString("ac59b52d-7d96-44d3-95c3-9306b6f8c37a");
    private static final UUID GRAVITY_UUID =
            UUID.fromString("d4f0f1bc-1e7a-49d8-9b77-9e4a2b0f6671");

    public PressingFrontEffect() {
        super(MobEffectCategory.HARMFUL, 0xB8842A);
    }

    private void applyGravityModifier(LivingEntity entity, int amplifier) {
        AttributeInstance gravity = entity.getAttribute(ForgeMod.ENTITY_GRAVITY.get());
        if (gravity == null){
            return;
        }

        removeModifier(gravity, GRAVITY_UUID);

        double gravityBonus = 0.35D + 0.20D * amplifier;

        gravity.addTransientModifier(new AttributeModifier(
                GRAVITY_UUID,
                this.getDescriptionId(),
                gravityBonus,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        ));
    }

    private void applyDynamicArmorReduction(LivingEntity entity, int amplifier) {
        AttributeInstance armor = entity.getAttribute(Attributes.ARMOR);
        AttributeInstance toughness = entity.getAttribute(Attributes.ARMOR_TOUGHNESS);

        // 先移除上一 tick 的动态 modifier，避免重复叠加
        removeModifier(armor, ARMOR_UUID);
        removeModifier(toughness, TOUGHNESS_UUID);

        if (armor != null){
            double positiveArmor = DTHelper.getPositiveAttributeBonus(entity, Attributes.ARMOR);
            double armorReduction = calculateArmorReduction(positiveArmor, amplifier);

            if (armorReduction > 0.0D){
                armor.addTransientModifier(new AttributeModifier(
                        ARMOR_UUID,
                        this.getDescriptionId(),
                        -armorReduction,
                        AttributeModifier.Operation.ADDITION
                ));
            }
        }

        if (toughness != null){
            double positiveToughness = DTHelper.getPositiveAttributeBonus(entity, Attributes.ARMOR_TOUGHNESS);
            double toughnessReduction = calculateToughnessReduction(positiveToughness, amplifier);

            if (toughnessReduction > 0.0D){
                toughness.addTransientModifier(new AttributeModifier(
                        TOUGHNESS_UUID,
                        this.getDescriptionId(),
                        -toughnessReduction,
                        AttributeModifier.Operation.ADDITION
                ));
            }
        }
    }

    private static double calculateArmorReduction(double positiveArmor, int amplifier) {
        if (positiveArmor <= 0.0D){
            return 0.0D;
        }

        double scale = 0.75D + 0.22D * amplifier;
        double maxRatio = 0.40D + 0.10D * amplifier;

        double reduction = Math.pow(positiveArmor, 0.65D) * scale;
        double maxReduction = positiveArmor * maxRatio;

        return Math.min(reduction, maxReduction);
    }

    private static double calculateToughnessReduction(double positiveToughness, int amplifier) {
        if (positiveToughness <= 0.0D){
            return 0.0D;
        }

        double scale = 0.65D + 0.18D * amplifier;
        double maxRatio = 0.45D + 0.10D * amplifier;

        double reduction = Math.sqrt(positiveToughness) * scale;
        double maxReduction = positiveToughness * maxRatio;

        return Math.min(reduction, maxReduction);
    }

    private static void applyGravityPressure(LivingEntity entity, int amplifier) {
        if (entity.isFallFlying() || entity.isInWaterOrBubble() || entity.isPassenger()){
            return;
        }

        Vec3 motion = entity.getDeltaMovement();

        double extraDown = 0.015D + 0.006D * amplifier;

        // 已经下落很快时不要继续无限加速
        if (motion.y > -0.45D){
            entity.setDeltaMovement(motion.x, Math.max(motion.y - extraDown, -0.45D), motion.z);
            entity.hurtMarked = true;
        }
    }

    private static void removeDynamicModifiers(LivingEntity entity) {
        removeModifier(entity.getAttribute(Attributes.ARMOR), ARMOR_UUID);
        removeModifier(entity.getAttribute(Attributes.ARMOR_TOUGHNESS), TOUGHNESS_UUID);
    }

    private static void removeModifier(@Nullable AttributeInstance instance, UUID uuid) {
        if (instance != null && instance.getModifier(uuid) != null){
            instance.removeModifier(uuid);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(entity, attributeMap, amplifier);
        applyDynamicArmorReduction(entity, amplifier);
        applyGravityModifier(entity, amplifier);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide){
            return;
        }
        applyGravityPressure(entity, amplifier);
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        removeDynamicModifiers(entity);
        removeModifier(entity.getAttribute(ForgeMod.ENTITY_GRAVITY.get()), GRAVITY_UUID);
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
    }
}
