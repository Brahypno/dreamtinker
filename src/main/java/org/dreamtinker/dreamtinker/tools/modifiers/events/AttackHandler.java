package org.dreamtinker.dreamtinker.tools.modifiers.events;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerDamageTypes;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import org.dreamtinker.dreamtinker.utils.DamageProbe;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class AttackHandler {
    private static final int allowed_extra_times = 2;
    private static final ThreadLocal<Integer> extra_attack_depth = ThreadLocal.withInitial(() -> 0);

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void LivingAttackEvent(LivingAttackEvent event) {
        DamageSource dmg = event.getSource();
        Entity dmgEntity = dmg.getEntity();
        float damageAmount = event.getAmount();
        LivingEntity victim = event.getEntity();
        if (0 == damageAmount || event.isCanceled())
            return;
        Level world = victim.level();
        if (world.isClientSide())
            return;
        RegistryAccess registryAccess = world.registryAccess();
        if (dmgEntity instanceof LivingEntity attacker){
            if (DTModifierCheck.haveModifierIn(attacker, DreamtinkerModifiers.despair_wind.getId())){
                int depth = extra_attack_depth.get();
                if (depth < allowed_extra_times){
                    try {
                        int inv = victim.invulnerableTime;
                        victim.invulnerableTime = 0;
                        extra_attack_depth.set(depth + 1);
                        DamageProbe.finalDamageMethod(victim,
                                                      DreamtinkerDamageTypes.source(registryAccess, DreamtinkerDamageTypes.NULL_VOID, null, attacker),
                                                      damageAmount);
                        victim.invulnerableTime = inv;
                    }
                    finally {
                        extra_attack_depth.set(depth);
                    }
                }
            }
        }
    }
}
