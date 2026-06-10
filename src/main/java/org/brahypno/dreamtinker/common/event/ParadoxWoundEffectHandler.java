package org.brahypno.dreamtinker.common.event;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.common.DreamtinkerEffects;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = Dreamtinker.MODID)
public class ParadoxWoundEffectHandler {
    @SubscribeEvent
    public static void onInverseWoundDamage(LivingDamageEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity.level().isClientSide() || entity.isDeadOrDying()){
            return;
        }

        MobEffectInstance effect = entity.getEffect(DreamtinkerEffects.ParadoxWound.get());
        if (effect == null){
            return;
        }

        float damage = event.getAmount();
        float maxHealth = entity.getMaxHealth();

        if (damage <= 0.0F || maxHealth <= 0.0F){
            return;
        }

        int level = effect.getAmplifier() + 1;

        float ratio = damage / maxHealth;

        float neutralRatio = 0.50F;
        float softness = 0.06F;
        float strength = 0.35F + 0.15F * level;


        float result = damage + maxHealth * strength * (neutralRatio - ratio) / (ratio + softness);

        if (result >= 0.0F){
            event.setAmount(result);
        }else {
            event.setAmount(0.0F);
            entity.heal(Math.min(-result, maxHealth * (0.08F + 0.02F * level)));
        }
    }
}
