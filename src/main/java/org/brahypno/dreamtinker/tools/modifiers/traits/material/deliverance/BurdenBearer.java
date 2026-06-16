package org.brahypno.dreamtinker.tools.modifiers.traits.material.deliverance;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import org.brahypno.dreamtinker.common.DreamtinkerEffects;
import org.brahypno.dreamtinker.tools.modifiers.events.OathGuardPaleSteelEvents;
import org.brahypno.esotericismtinker.utils.ETModifierCheck;
import slimeknights.tconstruct.library.modifiers.Modifier;

public class BurdenBearer extends Modifier {
    private static final double ALLY_DEATH_RANGE = 16.0D;
    private static final int EFFECT_DURATION = 20 * 20;

    {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::onLivingDeath);
    }

    private void onLivingDeath(LivingDeathEvent event) {
        LivingEntity fallen = event.getEntity();
        if (event.isCanceled() || !(fallen.level() instanceof ServerLevel level)){
            return;
        }

        for (ServerPlayer holder : level.getEntitiesOfClass(
                ServerPlayer.class,
                fallen.getBoundingBox().inflate(ALLY_DEATH_RANGE),
                player -> player.isAlive()
                          && !player.isSpectator()
                          && player != fallen
                          && ETModifierCheck.haveModifierIn(player, getId())
                          && OathGuardPaleSteelEvents.isGuardianProtectedTarget(player, fallen))) {
            grantBurden(holder, fallen);
        }
    }

    private void grantBurden(ServerPlayer holder, LivingEntity fallen) {
        MobEffectInstance current = holder.getEffect(DreamtinkerEffects.BurdenBearer.get());
        int currentLevel = current == null ? 0 : current.getAmplifier() + 1;
        int armorModifierLevel = ETModifierCheck.getEntityBodyModifierNum(holder, getId());
        int levelGain = Math.max(1, armorModifierLevel / 2);
        int amplifier = currentLevel + levelGain - 1;
        holder.addEffect(new MobEffectInstance(DreamtinkerEffects.BurdenBearer.get(), EFFECT_DURATION, amplifier, false, true, true), fallen);
    }
}
