package org.dreamtinker.dreamtinker.register;

import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.modifier.material.crying_obsidian.in_rain;
import org.dreamtinker.dreamtinker.modifier.material.crying_obsidian.isolde;
import org.dreamtinker.dreamtinker.modifier.material.crying_obsidian.sharpened_with;
import org.dreamtinker.dreamtinker.modifier.material.echo_shard.echoed_attack;
import org.dreamtinker.dreamtinker.modifier.material.echo_shard.echoed_defence;
import org.dreamtinker.dreamtinker.modifier.material.lupus_antimony.the_wolf_answer;
import org.dreamtinker.dreamtinker.modifier.material.lupus_antimony.the_wolf_was;
import org.dreamtinker.dreamtinker.modifier.material.lupus_antimony.the_wolf_wonder;
import org.dreamtinker.dreamtinker.modifier.material.moonlight.glacialriver;
import org.dreamtinker.dreamtinker.modifier.material.nigrescence_antimony.broken_vessel;
import org.dreamtinker.dreamtinker.modifier.material.nigrescence_antimony.burning_in_vain;
import org.dreamtinker.dreamtinker.modifier.material.nigrescence_antimony.ewige_widerkunft;
import org.dreamtinker.dreamtinker.modifier.material.nigrescence_antimony.ouroboric_hourglass;
import org.dreamtinker.dreamtinker.modifier.material.star_regulus.as_one;
import org.dreamtinker.dreamtinker.modifier.material.star_regulus.two_headed_seven;
import org.dreamtinker.dreamtinker.modifier.material.valentinite.antimony_usage;
import org.dreamtinker.dreamtinker.modifier.tools.masu.realsweep;
import org.dreamtinker.dreamtinker.modifier.tools.masu.silvernamebee;
import org.dreamtinker.dreamtinker.modifier.tools.masu.strong_heavy;
import org.dreamtinker.dreamtinker.modifier.tools.tntarrow.strong_explode;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;


public class DreamtinkerModifer {
    public static ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(Dreamtinker.MODID);
    public static final StaticModifier<realsweep> realsweep = MODIFIERS.register("realsweep", realsweep::new);
    public static final StaticModifier<strong_heavy> strong_heavy = MODIFIERS.register("strong_heavy", strong_heavy::new);
    public static final StaticModifier<silvernamebee> silvernamebee = MODIFIERS.register("silvernamebee", silvernamebee::new);

    public static final StaticModifier<echoed_attack> echoed_attack = MODIFIERS.register("echoed_attack", echoed_attack::new);
    public static final StaticModifier<echoed_defence> echoed_defence = MODIFIERS.register("echoed_defence", echoed_defence::new);

    public static final StaticModifier<strong_explode> strong_explode = MODIFIERS.register("strong_explode", strong_explode::new);

    public static final StaticModifier<glacialriver> glacial_river = MODIFIERS.register("glacial_river", glacialriver::new);

    public static final StaticModifier<antimony_usage> antimony_usage = MODIFIERS.register("antimony_usage", antimony_usage::new);

    public static final StaticModifier<broken_vessel> broken_vessel = MODIFIERS.register("broken_vessel", broken_vessel::new);
    public static final StaticModifier<ewige_widerkunft> ewige_widerkunft = MODIFIERS.register("ewige_widerkunft", ewige_widerkunft::new);
    public static final StaticModifier<ouroboric_hourglass> ouroboric_hourglass = MODIFIERS.register("ouroboric_hourglass", ouroboric_hourglass::new);
    public static final StaticModifier<burning_in_vain> burning_in_vain = MODIFIERS.register("burning_in_vain", burning_in_vain::new);

    public static final StaticModifier<the_wolf_wonder> the_wolf_wonder = MODIFIERS.register("the_wolf_wonder", the_wolf_wonder::new);
    public static final StaticModifier<the_wolf_answer> the_wolf_answer = MODIFIERS.register("the_wolf_answer", the_wolf_answer::new);
    public static final StaticModifier<the_wolf_was> the_wolf_was = MODIFIERS.register("the_wolf_was", the_wolf_was::new);

    public static final StaticModifier<as_one> as_one = MODIFIERS.register("as_one", as_one::new);
    public static final StaticModifier<two_headed_seven> two_headed_seven = MODIFIERS.register("two_headed_seven", two_headed_seven::new);

    public static final StaticModifier<sharpened_with> sharpened_with = MODIFIERS.register("sharpened_with", sharpened_with::new);
    public static final StaticModifier<in_rain> in_rain = MODIFIERS.register("in_rain", in_rain::new);
    public static final StaticModifier<isolde> isolde = MODIFIERS.register("isolde", isolde::new);
}
