package org.dreamtinker.dreamtinker.register;

import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.modifier.Combat.*;
import org.dreamtinker.dreamtinker.modifier.Compact.enigmaticLegacy.*;
import org.dreamtinker.dreamtinker.modifier.Compact.enigmaticLegacy.material.etherium.astral_break;
import org.dreamtinker.dreamtinker.modifier.Compact.enigmaticLegacy.material.etherium.etherium_protection;
import org.dreamtinker.dreamtinker.modifier.Compact.enigmaticLegacy.material.evil.evil_attack;
import org.dreamtinker.dreamtinker.modifier.Compact.enigmaticLegacy.material.soul_aether.exiles_faulty;
import org.dreamtinker.dreamtinker.modifier.armors.ender_dodge;
import org.dreamtinker.dreamtinker.modifier.armors.soul_blessing;
import org.dreamtinker.dreamtinker.modifier.armors.stone_heart;
import org.dreamtinker.dreamtinker.modifier.common.fly;
import org.dreamtinker.dreamtinker.modifier.common.life_looting;
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
import org.dreamtinker.dreamtinker.modifier.tools.narcissus_wing.memoryBase;
import org.dreamtinker.dreamtinker.modifier.tools.tntarrow.strong_explode;
import org.dreamtinker.dreamtinker.modifier.tools.underPlate.weapon_transformation;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;


public class DreamtinkerModifers {
    public static ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(Dreamtinker.MODID);
    public static ModifierDeferredRegister EL_MODIFIERS = ModifierDeferredRegister.create(Dreamtinker.MODID);
    //Mashuo
    public static final StaticModifier<realsweep> real_sweep = MODIFIERS.register("real_sweep", realsweep::new);
    public static final StaticModifier<strong_heavy> strong_heavy = MODIFIERS.register("strong_heavy", strong_heavy::new);
    public static final StaticModifier<silvernamebee> silver_name_bee = MODIFIERS.register("silver_name_bee", silvernamebee::new);
    //tnt arrow
    public static final StaticModifier<strong_explode> strong_explode = MODIFIERS.register("strong_explode", strong_explode::new);
    //narcissus wing
    public static final StaticModifier<memoryBase> memory_base = MODIFIERS.register("memory_base", memoryBase::new);
    //echo shard
    public static final StaticModifier<echoed_attack> echoed_attack = MODIFIERS.register("echoed_attack", echoed_attack::new);
    public static final StaticModifier<echoed_defence> echoed_defence = MODIFIERS.register("echoed_defence", echoed_defence::new);

    //moonlight ice
    public static final StaticModifier<glacialriver> glacial_river = MODIFIERS.register("glacial_river", glacialriver::new);
    //valentinite
    public static final StaticModifier<antimony_usage> antimony_usage = MODIFIERS.register("antimony_usage", antimony_usage::new);
    //nigrescence antimony
    public static final StaticModifier<broken_vessel> broken_vessel = MODIFIERS.register("broken_vessel", broken_vessel::new);
    public static final StaticModifier<ewige_widerkunft> ewige_widerkunft = MODIFIERS.register("ewige_widerkunft", ewige_widerkunft::new);
    public static final StaticModifier<ouroboric_hourglass> ouroboric_hourglass = MODIFIERS.register("ouroboric_hourglass", ouroboric_hourglass::new);
    public static final StaticModifier<burning_in_vain> burning_in_vain = MODIFIERS.register("burning_in_vain", burning_in_vain::new);
    //lupi
    public static final StaticModifier<the_wolf_wonder> the_wolf_wonder = MODIFIERS.register("the_wolf_wonder", the_wolf_wonder::new);
    public static final StaticModifier<the_wolf_answer> the_wolf_answer = MODIFIERS.register("the_wolf_answer", the_wolf_answer::new);
    public static final StaticModifier<the_wolf_was> the_wolf_was = MODIFIERS.register("the_wolf_was", the_wolf_was::new);
    // star regulus
    public static final StaticModifier<as_one> as_one = MODIFIERS.register("as_one", as_one::new);
    public static final StaticModifier<two_headed_seven> two_headed_seven = MODIFIERS.register("two_headed_seven", two_headed_seven::new);
    //crying obsidian
    public static final StaticModifier<sharpened_with> sharpened_with = MODIFIERS.register("sharpened_with", sharpened_with::new);
    public static final StaticModifier<in_rain> in_rain = MODIFIERS.register("in_rain", in_rain::new);
    public static final StaticModifier<isolde> isolde = MODIFIERS.register("isolde", isolde::new);

    public static final StaticModifier<mei> mei = MODIFIERS.register("mei", mei::new);
    public static final StaticModifier<fly> fly = MODIFIERS.register("fly", fly::new);
    public static final StaticModifier<open_soul> open_soul = MODIFIERS.register("open_soul", open_soul::new);
    public static final StaticModifier<soul_blessing> soul_blessing = MODIFIERS.register("soul_blessing", soul_blessing::new);

    //etherium
    public static final StaticModifier<ender_dodge> ender_dodge = MODIFIERS.register("ender_dodge", ender_dodge::new);
    public static final StaticModifier<explosive_hit> explosive_hit = MODIFIERS.register("explosive_hit", explosive_hit::new);
    public static final StaticModifier<ranged_shoot> ranged_shoot = MODIFIERS.register("ranged_shoot", ranged_shoot::new);

    public static final StaticModifier<wither_shoot> wither_shoot = MODIFIERS.register("wither_shoot", wither_shoot::new);
    public static final StaticModifier<stone_heart> stone_heart = MODIFIERS.register("stone_heart", stone_heart::new);
    public static final StaticModifier<life_looting> life_looting = MODIFIERS.register("life_looting", life_looting::new);
    //underplate
    public static final StaticModifier<weapon_transformation> weapon_transformation = MODIFIERS.register("weapon_transformation", weapon_transformation::new);

    //etherium
    public static final StaticModifier<astral_break> astral_break = EL_MODIFIERS.register("astral_break", astral_break::new);
    public static final StaticModifier<etherium_protection> etherium_protection = EL_MODIFIERS.register("etherium_protection", etherium_protection::new);

    public static final StaticModifier<cursed_ring_bound> cursed_ring_bound = EL_MODIFIERS.register("cursed_ring_bound", cursed_ring_bound::new);
    //evil
    public static final StaticModifier<evil_attack> evil_attack = EL_MODIFIERS.register("evil_attack", evil_attack::new);
    public static final StaticModifier<ender_slayer> ender_slayer = EL_MODIFIERS.register("ender_slayer", ender_slayer::new);
    public static final StaticModifier<weapon_books> weapon_books = EL_MODIFIERS.register("weapon_books", weapon_books::new);
    public static final StaticModifier<eldritch_pan> eldritch_pan = EL_MODIFIERS.register("eldritch_pan", eldritch_pan::new);
    public static final StaticModifier<Modifier> by_pass_worthy = EL_MODIFIERS.register("by_pass_worthy", Modifier::new);
    public static final StaticModifier<exiles_faulty> exiles_faulty = EL_MODIFIERS.register("exiles_faulty", exiles_faulty::new);
    public static final StaticModifier<desolation_ring> desolation_ring = EL_MODIFIERS.register("desolation_ring", desolation_ring::new);


    public static class Ids {
        public static final ModifierId soul_form = id("soul_form");
        public static final ModifierId wither_body = id("wither_body");
        public static final ModifierId soul_upgrade = id("soul_upgrade");
        public static final ModifierId continuous_explode = id("continuous_explode");
        public static final ModifierId moonlight_ice_info = id("moonlight_ice_info");
        public static final ModifierId soul_core = id("soul_core");

        private static ModifierId id(String name) {
            return new ModifierId(Dreamtinker.MODID, name);
        }
    }
}
