package org.dreamtinker.dreamtinker.tools.modifiers.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerDamageTypes;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.tools.items.TNTArrow;

import static org.dreamtinker.dreamtinker.utils.DTModifierCheck.ModifierInHand;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class GeneralExplodeEvent {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void ExplosionEvent(ExplosionEvent.Detonate event) {
        if (event.isCanceled())
            return;
        Explosion exp = event.getExplosion();
        if (exp.getDamageSource().is(DreamtinkerDamageTypes.force_to_explosion) && null != exp.getDirectSourceEntity() &&
            exp.getDirectSourceEntity() instanceof TNTArrow.TNTArrowEntity){
            event.getAffectedEntities().removeIf(Entity::isAlive);
        }
        if (null != exp.getDamageSource().getEntity())
            event.getAffectedEntities()
                 .removeIf(entity -> entity instanceof LivingEntity victim && victim.is(exp.getDamageSource().getEntity()) &&
                                     ModifierInHand(victim, DreamtinkerModifiers.ewige_widerkunft.getId()));

    }
}
