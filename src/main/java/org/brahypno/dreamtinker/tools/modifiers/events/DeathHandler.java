package org.brahypno.dreamtinker.tools.modifiers.events;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.common.DreamtinkerEffects;
import org.brahypno.esotericismtinker.utils.ETModifierCheck;

import static org.brahypno.dreamtinker.common.DreamtinkerEffects.hasActiveCursedTime;
import static org.brahypno.dreamtinker.tools.modifiers.traits.armors.knockArts.TAG_KNOCK;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class DeathHandler {
    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void LivingDeathEvent(LivingDeathEvent event) {
        LivingEntity victim = event.getEntity();
        if (event.isCanceled()){
            if (victim.hasEffect(DreamtinkerEffects.cursed.get()) || hasActiveCursedTime(victim))
                event.setCanceled(false);

            return;
        }
        Level world = victim.level();
        if (world.isClientSide() || event.isCanceled())
            return;
        ETModifierCheck.resetPersistentTagValue(victim, TAG_KNOCK);
    }
}
