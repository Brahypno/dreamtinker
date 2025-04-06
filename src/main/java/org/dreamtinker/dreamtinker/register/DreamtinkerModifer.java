package org.dreamtinker.dreamtinker.register;

import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.modifier.Combat.realsweep;
import org.dreamtinker.dreamtinker.modifier.Combat.strong_explode;
import org.dreamtinker.dreamtinker.modifier.material.echoed_attack;
import org.dreamtinker.dreamtinker.modifier.tools.strong_heavy;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;


public class DreamtinkerModifer {
    public static ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(Dreamtinker.MODID);
    public static final StaticModifier<realsweep> realsweep = MODIFIERS.register("realsweep", realsweep::new);
    public static final StaticModifier<strong_heavy> strong_heavy = MODIFIERS.register("strong_heavy", strong_heavy::new);
    public static final StaticModifier<echoed_attack> echoed_attack = MODIFIERS.register("echoed_attack", echoed_attack::new);
    public static final StaticModifier<strong_explode> strong_explode = MODIFIERS.register("strong_explode", strong_explode::new);
}
