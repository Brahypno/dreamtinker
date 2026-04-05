package org.dreamtinker.dreamtinker.common;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.RegistryObject;
import org.dreamtinker.dreamtinker.common.effect.realDarkness;
import org.dreamtinker.dreamtinker.common.effect.thirsty;
import org.dreamtinker.dreamtinker.common.effect.unholy;

import static org.dreamtinker.dreamtinker.DreamtinkerModule.*;

public class DreamtinkerEffects {
    public DreamtinkerEffects() {}

    public static final RegistryObject<MobEffect> SilverNameBee =
            EFFECT.register("silver_name_bee", () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0x7f7f7f) {});
    public static final RegistryObject<MobEffect> RealDarkness = EFFECT.register("real_darkness", realDarkness::new);
    public static final RegistryObject<MobEffect> unholy = EL_EFFECT.register("unholy", unholy::new);
    public static final RegistryObject<MobEffect> cursed = EFFECT.register("cursed", () -> new MobEffect(MobEffectCategory.HARMFUL, 0xA64DFF) {});
    public static final RegistryObject<thirsty> thirsty = MALUM_EFFECT.register("thirsty", thirsty::new);

    public static final RegistryObject<MobEffect> Ahimsa = EFFECT.register("ahimsa", () -> new MobEffect(MobEffectCategory.NEUTRAL, 0xD8B7A1) {});
    public static final RegistryObject<MobEffect> EdictOfStillness =
            EFFECT.register("edict_of_stillness", () -> new MobEffect(MobEffectCategory.NEUTRAL, 0x8E7AAE) {});
    public static final RegistryObject<MobEffect> LawOfTheSilentStep =
            EFFECT.register("law_of_the_silent_step", () -> new MobEffect(MobEffectCategory.NEUTRAL, 0x6FAAB2) {});
    public static final RegistryObject<MobEffect> InterdictOfAscent =
            EFFECT.register("interdict_of_ascent", () -> new MobEffect(MobEffectCategory.NEUTRAL, 0xC6A86B) {});
    public static final RegistryObject<MobEffect> InterdictOfGuard =
            EFFECT.register("interdict_of_guard", () -> new MobEffect(MobEffectCategory.NEUTRAL, 0x7E8892) {});
    public static final RegistryObject<MobEffect> InterdictOfRestoration =
            EFFECT.register("interdict_of_restoration", () -> new MobEffect(MobEffectCategory.NEUTRAL, 0xA7C97F) {});
    public static final RegistryObject<MobEffect> EdictOfUntouched =
            EFFECT.register("edict_of_untouched", () -> new MobEffect(MobEffectCategory.NEUTRAL, 0xD8D4E8) {});
    public static final RegistryObject<MobEffect> LawOfLoweredEyes =
            EFFECT.register("law_of_the_lowered_eyes", () -> new MobEffect(MobEffectCategory.NEUTRAL, 0x5F6C9B) {});
}
