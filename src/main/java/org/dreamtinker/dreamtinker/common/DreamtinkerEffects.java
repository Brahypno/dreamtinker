package org.dreamtinker.dreamtinker.common;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.RegistryObject;
import org.dreamtinker.dreamtinker.common.effect.realDarkness;
import org.dreamtinker.dreamtinker.common.effect.unholy;

import static org.dreamtinker.dreamtinker.DreamtinkerModule.EFFECT;
import static org.dreamtinker.dreamtinker.DreamtinkerModule.EL_EFFECT;

public class DreamtinkerEffects {
    public DreamtinkerEffects() {}

    public static final RegistryObject<MobEffect> SilverNameBee =
            EFFECT.register("silver_name_bee", () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0x7f7f7f) {});
    public static final RegistryObject<MobEffect> RealDarkness = EFFECT.register("real_darkness", realDarkness::new);
    public static final RegistryObject<MobEffect> unholy = EL_EFFECT.register("unholy", unholy::new);
    public static final RegistryObject<MobEffect> cursed = EFFECT.register("cursed", () -> new MobEffect(MobEffectCategory.HARMFUL, 0xA64DFF) {});

}
