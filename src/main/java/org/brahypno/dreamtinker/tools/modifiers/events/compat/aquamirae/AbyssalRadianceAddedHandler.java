package org.brahypno.dreamtinker.tools.modifiers.events.compat.aquamirae;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.tools.DreamtinkerModifiers;
import org.joml.Vector3f;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class AbyssalRadianceAddedHandler {
    /**
     * Two representative colors from Aquamirae's abyssal amethyst texture.
     */
    private static final DustParticleOptions RADIANCE_DUST =
            new DustParticleOptions(new Vector3f(0xE7 / 255.0F, 0x3E / 255.0F, 0xE7 / 255.0F), 0.75F);
    private static final DustParticleOptions RADIANCE_GLEAM =
            new DustParticleOptions(new Vector3f(1.0F, 0x42 / 255.0F, 1.0F), 0.55F);

    private AbyssalRadianceAddedHandler() {
    }

    private static void playRadianceEffect(ServerLevel level, LivingEntity entity, int strengthLevel) {
        double y = entity.getY() + entity.getBbHeight() * 0.45D;
        double horizontalSpread = Math.max(0.15D, entity.getBbWidth() * 0.3D);
        double verticalSpread = Math.max(0.2D, entity.getBbHeight() * 0.3D);
        level.sendParticles(RADIANCE_DUST, entity.getX(), y, entity.getZ(),
                            5, horizontalSpread, verticalSpread, horizontalSpread, 0.01D);
        level.sendParticles(RADIANCE_GLEAM, entity.getX(), y, entity.getZ(),
                            3, horizontalSpread * 0.65D, verticalSpread, horizontalSpread * 0.65D, 0.015D);
        level.playSound(null, entity.getX(), y, entity.getZ(), SoundEvents.AMETHYST_BLOCK_CHIME,
                        SoundSource.PLAYERS, 0.3F, Math.min(1.6F, 0.9F + 0.12F * strengthLevel));
    }

    @SubscribeEvent
    public static void onHarmfulEffectAdded(MobEffectEvent.Added event) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance added = event.getEffectInstance();
        if (!(entity.level() instanceof ServerLevel level)
            || added.getEffect().getCategory() != MobEffectCategory.HARMFUL){
            return;
        }

        MobEffectInstance previous = event.getOldEffectInstance();
        if (previous != null && previous.getAmplifier() >= added.getAmplifier()){
            return;
        }

        int maximumStrength = 0;
        for (ItemStack armor : entity.getArmorSlots()) {
            maximumStrength += ModifierUtil.getModifierLevel(armor, DreamtinkerModifiers.abyssal_radiance.getId());
        }
        if (maximumStrength <= 0){
            return;
        }

        MobEffectInstance currentStrength = entity.getEffect(MobEffects.DAMAGE_BOOST);
        int currentLevel = currentStrength == null ? 0 : currentStrength.getAmplifier() + 1;
        int nextLevel = Math.min(maximumStrength, currentLevel + 1);
        if (nextLevel > currentLevel){
            if (entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, nextLevel - 1, false, true, true))){
                playRadianceEffect(level, entity, nextLevel);
            }
        }else if (currentLevel == maximumStrength && currentStrength.getDuration() < 100){
            entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, currentLevel - 1, false, true, true));
        }
    }
}
