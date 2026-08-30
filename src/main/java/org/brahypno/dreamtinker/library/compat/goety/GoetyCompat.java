package org.brahypno.dreamtinker.library.compat.goety;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.tools.modifiers.events.compat.goety.GoetyMaterialEvents;
import org.brahypno.dreamtinker.tools.modifiers.traits.Compat.goety.*;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;

/**
 * Optional Goety integration entry point. This class is only initialized after Forge confirms that Goety is loaded.
 */
public final class GoetyCompat {
    public static final String MOD_ID = "goety";
    private static final ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(Dreamtinker.MODID);

    static {
        MODIFIERS.register("goety_dark_metal_attack", GoetyDarkMetalAttackModifier::new);
        MODIFIERS.register("goety_dark_metal_defense", GoetyDarkMetalDefenseModifier::new);
        MODIFIERS.register("goety_dark_metal_repair", GoetyDarkMetalRepairModifier::new);
        MODIFIERS.register("goety_eerie_pickaxe", GoetyEeriePickaxeModifier::new);
        MODIFIERS.register("goety_soul_eater", GoetySoulEaterModifier::new);
        MODIFIERS.register("goety_death_scythe", GoetyDeathScytheModifier::new);
        MODIFIERS.register("goety_blade_of_ender", GoetyBladeOfEnderModifier::new);
    }

    private GoetyCompat() {}

    public static void register(IEventBus modEventBus) {
        MODIFIERS.register(modEventBus);
        GoetyTransmutationRegistry.SERIALIZERS.register(modEventBus);
        GoetyTransmutationRegistry.RITUALS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(GoetyMaterialEvents.class);
    }
}
