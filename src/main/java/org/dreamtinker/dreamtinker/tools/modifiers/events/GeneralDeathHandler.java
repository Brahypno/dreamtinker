package org.dreamtinker.dreamtinker.tools.modifiers.events;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;

import static org.dreamtinker.dreamtinker.tools.modifiers.traits.armors.knockArts.TAG_KNOCK;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class GeneralDeathHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void LivingDeathEvent(LivingDeathEvent event) {
        LivingEntity victim = event.getEntity();
        if (event.isCanceled())
            return;
        Level world = victim.level();
        if (world.isClientSide() || event.isCanceled())
            return;
        DTModifierCheck.resetPersistentTagValue(victim, TAG_KNOCK);
    }
}
