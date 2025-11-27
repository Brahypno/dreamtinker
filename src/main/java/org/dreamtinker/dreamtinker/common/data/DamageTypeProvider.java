package org.dreamtinker.dreamtinker.common.data;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import org.dreamtinker.dreamtinker.Dreamtinker;
import slimeknights.tconstruct.library.modifiers.fluid.entity.DamageFluidEffect;

import static org.dreamtinker.dreamtinker.common.DreamtinkerDamageTypes.*;

public class DamageTypeProvider implements RegistrySetBuilder.RegistryBootstrap<DamageType> {
    public DamageTypeProvider() {}

    @Override
    public void run(BootstapContext<DamageType> context) {
        context.register(NULL_VOID, new DamageType(Dreamtinker.MODID + ".null_void", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 1f, DamageEffects.HURT));
        context.register(rain_bow, new DamageType(Dreamtinker.MODID + ".rain_bow", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 1f, DamageEffects.THORNS));
        context.register(force_to_explosion,
                         new DamageType(Dreamtinker.MODID + ".rain_bow", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 1f, DamageEffects.HURT));
    }

    /**
     * Registers a damage type pair for a fluid effect
     */
    private static void register(BootstapContext<DamageType> context, DamageFluidEffect.DamageTypePair pair, DamageType damageType) {
        context.register(pair.melee(), damageType);
        context.register(pair.ranged(), damageType);
    }
}