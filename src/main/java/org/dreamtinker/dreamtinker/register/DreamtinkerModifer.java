package org.dreamtinker.dreamtinker.register;

import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.modifier.Combat.realsweep;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;


public class DreamtinkerModifer {
    public static ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(Dreamtinker.MODID);
    public static final StaticModifier<realsweep> rude = MODIFIERS.register("realsweep", realsweep::new);
}
