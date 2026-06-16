package org.brahypno.dreamtinker.tools.modifiers.events;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.common.DreamtinkerEffects;

import static org.brahypno.dreamtinker.common.DreamtinkerEffects.hasActiveCursedTime;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class HealHandler {
    @SubscribeEvent
    public static void LivingHealEvent(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.hasEffect(DreamtinkerEffects.cursed.get()) || hasActiveCursedTime(entity)){
            event.setAmount(0);
            event.setCanceled(true);
        }
    }
}
