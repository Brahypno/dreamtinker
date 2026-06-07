package org.dreamtinker.dreamtinker.tools.modifiers.events;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerEffects;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;

import static org.dreamtinker.dreamtinker.common.DreamtinkerEffects.hasActiveCursedTime;
import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.BrokenVesselBoost;
import static org.dreamtinker.dreamtinker.tools.modifiers.traits.material.nigrescence_antimony.broken_vessel.TAG_BASE_HEALTH;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class HealHandler {
    @SubscribeEvent
    public static void LivingHealEvent(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        CompoundTag data = entity.getPersistentData();
        float current = entity.getHealth();
        float heal = event.getAmount();
        if (entity.hasEffect(DreamtinkerEffects.cursed.get()) || hasActiveCursedTime(entity)){
            event.setAmount(0);
            event.setCanceled(true);
            return;
        }
        if (data.contains(TAG_BASE_HEALTH)){
            // 读取记录的基础血量上限
            float baseHealth = data.getFloat(TAG_BASE_HEALTH);
            // 计算血量允许恢复到的一半
            float cap = baseHealth / (1 + BrokenVesselBoost.get());

            if (cap <= current){
                event.setAmount(0f);
                entity.setHealth(cap);
            }else if (cap < current + heal){
                event.setAmount(cap - current);
                entity.setHealth(cap);
            }
        }
        int rain_cap = DTModifierCheck.getMainhandModifierLevel(entity, DreamtinkerModifiers.despair_rain.getId());
        if (0 < rain_cap){
            event.setAmount(0f);
            entity.setHealth(rain_cap);
            entity.setAbsorptionAmount(entity.getAbsorptionAmount() + current - rain_cap);
        }
    }
}
