package org.dreamtinker.dreamtinker.register;

import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.modifier.Combat.glacialriver;
import org.dreamtinker.dreamtinker.modifier.Combat.realsweep;
import org.dreamtinker.dreamtinker.modifier.Combat.silvernamebee;
import org.dreamtinker.dreamtinker.modifier.Combat.strong_explode;
import org.dreamtinker.dreamtinker.modifier.material.echo_shard.echoed_attack;
import org.dreamtinker.dreamtinker.modifier.material.echo_shard.echoed_defence;
import org.dreamtinker.dreamtinker.modifier.material.nigrescence_antimony.broken_vessel;
import org.dreamtinker.dreamtinker.modifier.material.nigrescence_antimony.burning_in_vain;
import org.dreamtinker.dreamtinker.modifier.material.nigrescence_antimony.ewige_widerkunft;
import org.dreamtinker.dreamtinker.modifier.material.nigrescence_antimony.ouroboric_hourglass;
import org.dreamtinker.dreamtinker.modifier.material.valentinite.antimony_usage;
import org.dreamtinker.dreamtinker.modifier.tools.strong_heavy;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;



public class DreamtinkerModifer {
    public static ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(Dreamtinker.MODID);
    public static final StaticModifier<realsweep> realsweep = MODIFIERS.register("realsweep", realsweep::new);
    public static final StaticModifier<strong_heavy> strong_heavy = MODIFIERS.register("strong_heavy", strong_heavy::new);
    public static final StaticModifier<echoed_attack> echoed_attack = MODIFIERS.register("echoed_attack", echoed_attack::new);
    public static final StaticModifier<echoed_defence> echoed_defence = MODIFIERS.register("echoed_defence", echoed_defence::new);
    public static final StaticModifier<strong_explode> strong_explode = MODIFIERS.register("strong_explode", strong_explode::new);
    public static final StaticModifier<silvernamebee> silvernamebee = MODIFIERS.register("silvernamebee", silvernamebee::new);
    public static final StaticModifier<glacialriver> glacial_river = MODIFIERS.register("glacial_river", glacialriver::new);
    public static final StaticModifier<antimony_usage> antimony_usage = MODIFIERS.register("antimony_usage", antimony_usage::new);
    public static final StaticModifier<broken_vessel> broken_vessel = MODIFIERS.register("broken_vessel", broken_vessel::new);
    public static final StaticModifier<ewige_widerkunft> ewige_widerkunft = MODIFIERS.register("ewige_widerkunft", ewige_widerkunft::new);
    public static final StaticModifier<ouroboric_hourglass> ouroboric_hourglass = MODIFIERS.register("ouroboric_hourglass", ouroboric_hourglass::new);
    public static final StaticModifier<burning_in_vain> burning_in_vain = MODIFIERS.register("burning_in_vain", burning_in_vain::new);
}
