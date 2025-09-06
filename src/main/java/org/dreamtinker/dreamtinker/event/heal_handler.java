package org.dreamtinker.dreamtinker.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.register.DreamtinkerEffects;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class heal_handler {
    @SubscribeEvent
    public static void LivingHealEvent(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.hasEffect(DreamtinkerEffects.cursed.get())){
            event.setAmount(0);
            event.setCanceled(true);
        }
    }
}
