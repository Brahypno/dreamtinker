package org.dreamtinker.dreamtinker.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import org.dreamtinker.dreamtinker.register.DreamtinkerEffects;

import java.util.UUID;

public class realDarkness extends MobEffect {
    // 固定 UUID，避免重复叠加产生多条不同 UUID 的修饰符
    private static final UUID FOLLOW_RANGE_UUID = UUID.fromString("7a8b8d90-0b9f-4e7c-9f3e-3a3f7b1d0c11");

    public realDarkness() {
        super(MobEffectCategory.HARMFUL, 0x222222);
    }

    {
        MinecraftForge.EVENT_BUS.addListener(this::onChangeTarget);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true; // 每 tick 调用 applyEffectTick
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof Mob mob){
            if (mob.getTarget() != null){
                mob.setTarget(null);
            }
        }
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, net.minecraft.world.entity.ai.attributes.AttributeMap map, int amplifier) {
        super.addAttributeModifiers(entity, map, amplifier);
        // 把 FOLLOW_RANGE 直接“归零”
        var inst = map.getInstance(Attributes.FOLLOW_RANGE);
        if (inst != null && inst.getModifier(FOLLOW_RANGE_UUID) == null){
            // MULTIPLY_TOTAL = -1.0 ⇒ 最终值 = 基础*(1-1)=0（再加上其他来源，基本≈0）
            inst.addPermanentModifier(new AttributeModifier(FOLLOW_RANGE_UUID, "obscured_follow_range_zero", -1.0, // -100%
                                                            AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, net.minecraft.world.entity.ai.attributes.AttributeMap map, int amplifier) {
        // 移除我们加的修饰符，恢复原值
        var inst = map.getInstance(Attributes.FOLLOW_RANGE);
        if (inst != null){
            var mod = inst.getModifier(FOLLOW_RANGE_UUID);
            if (mod != null)
                inst.removeModifier(mod);
        }
        super.removeAttributeModifiers(entity, map, amplifier);
    }

    public void onChangeTarget(LivingChangeTargetEvent event) {
        LivingEntity changer = event.getEntity();
        if (!(changer instanceof Mob mob))
            return;

        // 你注册的效果实例
        if (mob.hasEffect(DreamtinkerEffects.RealDarkness.get())){
            // 任何尝试设定新目标都阻止
            if (event.getNewTarget() != null){
                // 取消并清空目标
                event.setCanceled(true);
                mob.setTarget(null);
            }
        }
    }
}

