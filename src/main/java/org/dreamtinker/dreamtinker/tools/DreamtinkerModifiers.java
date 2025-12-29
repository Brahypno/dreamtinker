package org.dreamtinker.dreamtinker.tools;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.DreamtinkerModule;
import org.dreamtinker.dreamtinker.common.data.tags.ModifierTagProvider;
import org.dreamtinker.dreamtinker.library.modifiers.modules.combat.MobEffectsRemoverModule;
import org.dreamtinker.dreamtinker.library.modifiers.modules.weapon.SwappableCircleWeaponAttack;
import org.dreamtinker.dreamtinker.tools.data.DreamtinkerEnchantmentToModifierProvider;
import org.dreamtinker.dreamtinker.tools.data.DreamtinkerFluidEffectProvider;
import org.dreamtinker.dreamtinker.tools.data.DreamtinkerModifierProvider;
import org.dreamtinker.dreamtinker.tools.modifiers.tools.chain_saw_blade.DeathShredder;
import org.dreamtinker.dreamtinker.tools.modifiers.tools.mashou.RealSweep;
import org.dreamtinker.dreamtinker.tools.modifiers.tools.mashou.StrongHeavy;
import org.dreamtinker.dreamtinker.tools.modifiers.tools.narcissus_wing.FlamingMemory;
import org.dreamtinker.dreamtinker.tools.modifiers.tools.narcissus_wing.MemoryBase;
import org.dreamtinker.dreamtinker.tools.modifiers.tools.narcissus_wing.SplendourHeart;
import org.dreamtinker.dreamtinker.tools.modifiers.tools.narcissus_wing.foundationWill;
import org.dreamtinker.dreamtinker.tools.modifiers.tools.silence_glove.WeaponDreams;
import org.dreamtinker.dreamtinker.tools.modifiers.tools.tntarrow.StrongExplode;
import org.dreamtinker.dreamtinker.tools.modifiers.tools.underPlate.SpiritualWeaponTransformation;
import org.dreamtinker.dreamtinker.tools.modifiers.tools.underPlate.WeaponTransformation;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat.*;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.bic.DarkBlade;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.bic.dark_defense;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.bic.nightmare_defense;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.eidolon.EidolonDeathBringer;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.eidolon.EidolonReaper;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.eidolon.EidolonSapping;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy.*;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy.material.etherium.ELAstralBreak;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy.material.etherium.etherium_protection;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy.material.evil.EvilAttack;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy.material.soul_aether.ExilesFaulty;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.malum.*;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.armors.*;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.common.*;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.material.crying_obsidian.Isolde;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.material.crying_obsidian.SharpenedWith;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.material.despair_gem.DespairMist;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.material.despair_gem.DespairRain;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.material.despair_gem.DespairWind;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.material.echo_shard.EchoedAttack;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.material.echo_shard.echoed_defence;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.material.lupus_antimony.TheWolfAnswer;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.material.lupus_antimony.TheWolfWas;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.material.lupus_antimony.TheWolfWonder;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.material.nigrescence_antimony.BurningInVain;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.material.nigrescence_antimony.EwigeEiderkunft;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.material.nigrescence_antimony.broken_vessel;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.material.nigrescence_antimony.ouroboric_hourglass;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.material.star_regulus.TwoHeadedSeven;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.material.star_regulus.as_one;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;


public final class DreamtinkerModifiers extends DreamtinkerModule {
    @SuppressWarnings({"removal"})
    public DreamtinkerModifiers() {
        MODIFIERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        if (ModList.get().isLoaded("enigmaticlegacy")){
            EL_MODIFIERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }
        if (ModList.get().isLoaded("malum")){
            MALUM_MODIFIERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }
        if (ModList.get().isLoaded("eidolon")){
            EIDOLON_MODIFIERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }
        if (ModList.get().isLoaded("born_in_chaos_v1")){
            BIC_MODIFIERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }
    }

    public static ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(Dreamtinker.MODID);
    public static ModifierDeferredRegister EL_MODIFIERS = ModifierDeferredRegister.create(Dreamtinker.MODID);
    public static ModifierDeferredRegister MALUM_MODIFIERS = ModifierDeferredRegister.create(Dreamtinker.MODID);
    public static ModifierDeferredRegister EIDOLON_MODIFIERS = ModifierDeferredRegister.create(Dreamtinker.MODID);
    public static ModifierDeferredRegister BIC_MODIFIERS = ModifierDeferredRegister.create(Dreamtinker.MODID);
    //Mashuo
    public static final StaticModifier<RealSweep> real_sweep = MODIFIERS.register("real_sweep", RealSweep::new);
    public static final StaticModifier<StrongHeavy> strong_heavy = MODIFIERS.register("strong_heavy", StrongHeavy::new);
    //tnt arrow
    public static final StaticModifier<StrongExplode> strong_explode = MODIFIERS.register("strong_explode", StrongExplode::new);
    //narcissus wing
    public static final StaticModifier<MemoryBase> memory_base = MODIFIERS.register("memory_base", MemoryBase::new);
    public static final StaticModifier<FlamingMemory> flaming_memory = MODIFIERS.register("flaming_memory", FlamingMemory::new);
    public static final StaticModifier<foundationWill> foundation_will = MODIFIERS.register("foundation_will", foundationWill::new);
    public static final StaticModifier<SplendourHeart> splendour_heart = MODIFIERS.register("splendour_heart", SplendourHeart::new);
    //underPlate
    public static final StaticModifier<WeaponTransformation> weapon_transformation = MODIFIERS.register("weapon_transformation", WeaponTransformation::new);
    public static final StaticModifier<SpiritualWeaponTransformation> spiritual_weapon_transformation =
            MALUM_MODIFIERS.register("spiritual_weapon_transformation", SpiritualWeaponTransformation::new);
    //echo Alloy
    public static final StaticModifier<EchoedAttack> echoed_attack = MODIFIERS.register("echoed_attack", EchoedAttack::new);
    public static final StaticModifier<echoed_defence> echoed_defence = MODIFIERS.register("echoed_defence", echoed_defence::new);

    //moonlight ice
    public static final StaticModifier<GlacialRiver> glacial_river = MODIFIERS.register("glacial_river", GlacialRiver::new);
    //nigrescence antimony
    public static final StaticModifier<broken_vessel> broken_vessel = MODIFIERS.register("broken_vessel", broken_vessel::new);
    public static final StaticModifier<EwigeEiderkunft> ewige_widerkunft = MODIFIERS.register("ewige_widerkunft", EwigeEiderkunft::new);
    public static final StaticModifier<ouroboric_hourglass> ouroboric_hourglass = MODIFIERS.register("ouroboric_hourglass", ouroboric_hourglass::new);
    public static final StaticModifier<BurningInVain> burning_in_vain = MODIFIERS.register("burning_in_vain", BurningInVain::new);
    //lupi
    public static final StaticModifier<TheWolfWonder> the_wolf_wonder = MODIFIERS.register("the_wolf_wonder", TheWolfWonder::new);
    public static final StaticModifier<TheWolfAnswer> the_wolf_answer = MODIFIERS.register("the_wolf_answer", TheWolfAnswer::new);
    public static final StaticModifier<TheWolfWas> the_wolf_was = MODIFIERS.register("the_wolf_was", TheWolfWas::new);
    // star regulus
    public static final StaticModifier<as_one> as_one = MODIFIERS.register("as_one", as_one::new);
    public static final StaticModifier<TwoHeadedSeven> two_headed_seven = MODIFIERS.register("two_headed_seven", TwoHeadedSeven::new);
    //crying obsidian
    public static final StaticModifier<SharpenedWith> sharpened_with = MODIFIERS.register("sharpened_with", SharpenedWith::new);
    public static final StaticModifier<Isolde> isolde = MODIFIERS.register("isolde", Isolde::new);

    public static final StaticModifier<org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat.mei> mei = MODIFIERS.register("mei", mei::new);
    public static final StaticModifier<fly> fly = MODIFIERS.register("fly", fly::new);
    public static final StaticModifier<OpenSoul> open_soul =
            MODIFIERS.register("open_soul", OpenSoul::new);
    public static final StaticModifier<soul_blessing> soul_blessing = MODIFIERS.register("soul_blessing", soul_blessing::new);
    public static final StaticModifier<RandomHit> malum_distortion = MODIFIERS.register("malum_distortion", () -> new RandomHit(0.9f, 1.2f));

    //etherium
    public static final StaticModifier<ender_dodge> ender_dodge = MODIFIERS.register("ender_dodge", ender_dodge::new);
    public static final StaticModifier<ExplosiveHit> explosive_hit =
            MODIFIERS.register("explosive_hit", ExplosiveHit::new);
    public static final StaticModifier<RangedShoot> ranged_shoot =
            MODIFIERS.register("ranged_shoot", RangedShoot::new);

    public static final StaticModifier<WitherShoot> wither_shoot =
            MODIFIERS.register("wither_shoot", WitherShoot::new);
    public static final StaticModifier<stone_heart> stone_heart = MODIFIERS.register("stone_heart", stone_heart::new);
    public static final StaticModifier<life_looting> life_looting = MODIFIERS.register("life_looting", life_looting::new);

    public static final StaticModifier<DeepSleepWithRoar> deep_sleep_with_roar = MODIFIERS.register("deep_sleep_with_roar", DeepSleepWithRoar::new);
    public static final StaticModifier<WaitUntil> wait_until = MODIFIERS.register("wait_until", WaitUntil::new);
    public static final StaticModifier<AnvilHit> anvil_hit = MODIFIERS.register("anvil_hit", AnvilHit::new);
    public static final StaticModifier<AbsorptionHit> absorption_hit = MODIFIERS.register("absorption_hit", AbsorptionHit::new);
    public static final StaticModifier<absorption_defense> absorption_defense = MODIFIERS.register("absorption_defense", absorption_defense::new);
    public static final StaticModifier<DespairMist> despair_mist = MODIFIERS.register("despair_mist", DespairMist::new);
    public static final StaticModifier<DespairRain> despair_rain = MODIFIERS.register("despair_rain", DespairRain::new);
    public static final StaticModifier<DespairWind> despair_wind = MODIFIERS.register("despair_wind", DespairWind::new);
    public static final StaticModifier<WeaponDreams> weapon_dreams = MODIFIERS.register("weapon_dreams", WeaponDreams::new);
    public static final StaticModifier<HoneyTastyModifier> HoneyTastyModifier = MODIFIERS.register("honey_tasty", HoneyTastyModifier::new);
    public static final StaticModifier<rainbowCatcher> rainbowCatcher = MODIFIERS.register("rainbow_catcher", rainbowCatcher::new);
    public static final StaticModifier<not_like_was> not_like_was = MODIFIERS.register("not_like_was", not_like_was::new);
    public static final StaticModifier<LightInDark> light_in_dark = MODIFIERS.register("light_in_dark", LightInDark::new);
    public static final StaticModifier<lightRangeBoost> light_emanation = MODIFIERS.register("light_emanation", lightRangeBoost::new);
    public static final StaticModifier<LunarDurabilityDefense> lunar_defense = MODIFIERS.register("lunar_defense", LunarDurabilityDefense::new);
    public static final StaticModifier<blockViewer> OreViewer = MODIFIERS.register("ore_viewer", () -> new blockViewer(Tags.Blocks.ORES.location(), 0.2f));
    public static final StaticModifier<HiddenHit> hiddenHit = MODIFIERS.register("hidden_hit", HiddenHit::new);
    public static final StaticModifier<knockArts> knockArts = MODIFIERS.register("knock_arts", knockArts::new);
    public static final StaticModifier<TheEnd> TheEnd = MODIFIERS.register("the_end", TheEnd::new);
    public static final StaticModifier<BlockMultiplier> OreMultiplier =
            MODIFIERS.register("ore_multiplier", () -> new BlockMultiplier(Tags.Blocks.ORES.location(), 0.4f, 3));
    public static final StaticModifier<knockBacker> SunAway = MODIFIERS.register("solar_away", knockBacker::new);
    public static final StaticModifier<DeathShredder> death_shredder = MODIFIERS.register("death_shredder", DeathShredder::new);
    public static final StaticModifier<SignalAxe> signal_axe = MODIFIERS.register("signal_axe", SignalAxe::new);
    public static final StaticModifier<LoveShooting> love_shooting = MODIFIERS.register("love_shooting", LoveShooting::new);
    public static final StaticModifier<TeleportShoot> teleport_shooting = MODIFIERS.register("teleport_shooting", TeleportShoot::new);

    //etherium
    public static final StaticModifier<ELAstralBreak> astral_break = EL_MODIFIERS.register("astral_break", ELAstralBreak::new);
    public static final StaticModifier<etherium_protection> etherium_protection = EL_MODIFIERS.register("etherium_protection", etherium_protection::new);

    public static final StaticModifier<org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy.cursed_ring_bound> cursed_ring_bound =
            EL_MODIFIERS.register("cursed_ring_bound", cursed_ring_bound::new);
    //evil
    public static final StaticModifier<EvilAttack> evil_attack = EL_MODIFIERS.register("evil_attack", EvilAttack::new);
    public static final StaticModifier<ELEnderSlayer> ender_slayer =
            EL_MODIFIERS.register("ender_slayer", ELEnderSlayer::new);
    public static final StaticModifier<WeaponBooks> weapon_books =
            EL_MODIFIERS.register("weapon_books", WeaponBooks::new);
    public static final StaticModifier<EldritchPan> eldritch_pan =
            EL_MODIFIERS.register("eldritch_pan", EldritchPan::new);
    public static final StaticModifier<Modifier> by_pass_worthy = EL_MODIFIERS.register("by_pass_worthy", Modifier::new);
    public static final StaticModifier<ExilesFaulty> exiles_faulty = EL_MODIFIERS.register("exiles_faulty", ExilesFaulty::new);
    public static final StaticModifier<org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy.desolation_ring> desolation_ring =
            EL_MODIFIERS.register("desolation_ring", desolation_ring::new);

    //Malum modifiers
    public static final StaticModifier<MalumBase> malum_base =
            MALUM_MODIFIERS.register("malum_base", MalumBase::new);
    public static final StaticModifier<malum_attributes> malum_spirit_attributes =
            MALUM_MODIFIERS.register("malum_spirit_attributes", () -> new malum_attributes(1));
    public static final StaticModifier<malum_attributes> malum_soul_attributes =
            MALUM_MODIFIERS.register("malum_soul_attributes", () -> new malum_attributes(2));
    public static final StaticModifier<malum_attributes> malum_malignant_attributes =
            MALUM_MODIFIERS.register("malum_malignant_attributes", () -> new malum_attributes(3));

    public static final StaticModifier<MalumHexStaff> malum_hex_staff =
            MALUM_MODIFIERS.register("malum_hex_staff", () -> new MalumHexStaff(false));
    public static final StaticModifier<range_accelerator> malum_range_accelerator =
            MALUM_MODIFIERS.register("malum_spirit_accelerator", () -> new range_accelerator(malumTag("spirit")));
    public static final StaticModifier<MalumMagicAttack> malum_magic_attack =
            MALUM_MODIFIERS.register("malum_magic_attack", MalumMagicAttack::new);
    public static final StaticModifier<MalumHexStaff> malum_erosion =
            MALUM_MODIFIERS.register("malum_erosion", () -> new MalumHexStaff(true));
    public static final StaticModifier<MalumThirsty> malum_thirsty =
            MALUM_MODIFIERS.register("malum_thirsty", MalumThirsty::new);
    public static final StaticModifier<MalumEvolution> malum_evolution =
            MALUM_MODIFIERS.register("malum_evolution", MalumEvolution::new);
    public static final StaticModifier<malum_spirit_defense> malum_spirit_defense =
            MALUM_MODIFIERS.register("malum_spirit_defense", malum_spirit_defense::new);
    public static final StaticModifier<MalumSolTiferet> malum_sol_tiferet =
            MALUM_MODIFIERS.register("malum_sol_tiferet", MalumSolTiferet::new);
    public static final StaticModifier<malumCatalystLobber> malum_catalyst_lobber =
            MALUM_MODIFIERS.register("malum_catalyst_lobber", malumCatalystLobber::new);
    public static final StaticModifier<MalumSoulExposer> malum_expose_soul =
            MALUM_MODIFIERS.register("malum_expose_soul", MalumSoulExposer::new);
    public static final StaticModifier<MalumMagicHit> malum_magic_hit =
            MALUM_MODIFIERS.register("malum_magic_hit", MalumMagicHit::new);

    public static final StaticModifier<EidolonReaper> eidolon_reaper =
            EIDOLON_MODIFIERS.register("eidolon_reaper", EidolonReaper::new);
    public static final StaticModifier<EidolonSapping> eidolon_sapping =
            EIDOLON_MODIFIERS.register("eidolon_sapping", EidolonSapping::new);
    public static final StaticModifier<EidolonDeathBringer> eidolon_death_bringer =
            EIDOLON_MODIFIERS.register("eidolon_death_bringer", EidolonDeathBringer::new);

    public static final StaticModifier<dark_defense> bic_dark_defense =
            BIC_MODIFIERS.register("bic_dark_defense", dark_defense::new);
    public static final StaticModifier<DarkBlade> bic_dark_blade =
            BIC_MODIFIERS.register("bic_dark_blade", DarkBlade::new);
    public static final StaticModifier<nightmare_defense> bic_nightmare_defense =
            BIC_MODIFIERS.register("bic_nightmare_defense", nightmare_defense::new);

    public static class Ids {
        public static final ModifierId long_tool = id("long_tool");
        public static final ModifierId antimony_usage = id("antimony_usage");
        public static final ModifierId with_tears = id("with_tears");
        public static final ModifierId in_rain = id("in_rain");
        public static final ModifierId soul_form = id("soul_form");
        public static final ModifierId wither_body = id("wither_body");
        public static final ModifierId soul_upgrade = id("soul_upgrade");
        public static final ModifierId continuous_explode = id("continuous_explode");
        public static final ModifierId soul_core = id("soul_core");
        public static final ModifierId icy_memory = id("icy_memory");
        public static final ModifierId hate_memory = id("hate_memory");
        public static final ModifierId huge_ego = id("huge_ego");
        public static final ModifierId full_concentration = id("full_concentration");
        public static final ModifierId thundering_curse = id("thundering_curse");
        public static final ModifierId why_i_cry = id("why_i_cry");
        public static final ModifierId ykhEULA = id("ykh_eula");
        public static final ModifierId MorningLordEULA = id("morning_lord_eula");
        public static final ModifierId EULA = id("eula");
        public static final ModifierId AsSand = id("as_sand");
        public static final ModifierId FragileButBright = id("fragile_but_bright");
        public static final ModifierId homunculusLifeCurse = id("homunculus_life_curse");
        public static final ModifierId homunculusGift = id("homunculus_gift");
        public static final ModifierId ophelia = id("ophelia");
        public static final ModifierId peaches_in_memory = id("peaches_in_memory");
        public static final ModifierId requiem = id("requiem");
        public static final ModifierId weapon_slots = id("weapon_slots");
        public static final ModifierId shadow_blessing = id("shadow_blessing");
        public static final ModifierId silver_name_bee = id("silver_name_bee");
        public static final ModifierId the_romantic = id("the_romantic");
        public static final ModifierId all_slayer = id("all_slayer");
        public static final ModifierId weapon_dreams_filter = id("weapon_dreams_filter");
        public static final ModifierId weapon_dreams_order = id("weapon_dreams_order");
        public static final ModifierId fiber_glass_fragments = id("fiber_glass_fragments");
        public static final ModifierId lunarProtection = id("lunar_protection");
        public static final ModifierId lunarAttractive = id("lunar_attractive");
        public static final ModifierId lunarRejection = id("lunar_rejection");
        public static final ModifierId slowness = id("ssss_slowness");
        public static final ModifierId soul_unchanged = id("soul_unchanged");
        public static final ModifierId force_to_explosion = id("force_to_explosion");
        public static final ModifierId aggressiveFoxUsage = id("aggressive_fox_usage");
        public static final ModifierId five_creations = id("five_creations");
        public static final ModifierId golden_face = id("golden_face");
        public static final ModifierId arcane_hit = id("arcane_hit");
        public static final ModifierId arcane_protection = id("arcane_protection");
        public static final ModifierId drinker_magic = id("drinker_magic");
        public static final ModifierId monster_blood = id("monster_blood");
        public static final ModifierId deeper_water_killer = id("deeper_water_killer");
        public static final ModifierId sun_shine = id("sun_shine");
        public static final ModifierId ender_slayer = id("ender_slayer");
        public static final ModifierId heavy_arrow = id("heavy_arrow");
        public static final ModifierId light_arrow = id("light_arrow");
        public static final ModifierId null_void = id("null_void");
        public static final ModifierId hidden_shape = id("hidden_shape");
        public static final ModifierId wrath = id("wrath");
        public static final ModifierId torrent = id("torrent");
        public static final ModifierId poison = id("poison");

        public static final ModifierId el_nemesis_curse = id("el_nemesis_curse");
        public static final ModifierId el_sorrow = id("el_sorrow");
        public static final ModifierId el_eternal_binding = id("el_eternal_binding");
        public static final ModifierId el_etherium = id("el_etherium");

        public static final ModifierId malum_rebound = id("malum_rebound");
        public static final ModifierId malum_ascension = id("malum_ascension");
        public static final ModifierId malum_animated = id("malum_animated");
        public static final ModifierId malum_haunted = id("malum_haunted");
        public static final ModifierId malum_replenishing = id("malum_replenishing");
        public static final ModifierId malum_spirit_plunder = id("malum_spirit_plunder");
        public static final ModifierId malum_tyrving = id("malum_tyrving");
        public static final ModifierId malum_world_of_weight = id("malum_world_of_weight");
        public static final ModifierId malum_edge_of_deliverance = id("malum_edge_of_deliverance");
        public static final ModifierId malum_sol_tiferet = id("malum_sol_tiferet");

        public static final ModifierId eidolon_vulnerable = id("eidolon_vulnerable");
        public static final ModifierId eidolon_warlock = id("eidolon_warlock");
        public static final ModifierId eidolon_soul_hearts = id("eidolon_soul_hearts");
        public static final ModifierId eidolon_paladin_bone = id("eidolon_paladin_bone");
        public static final ModifierId eidolon_bone_chill = id("eidolon_bone_chill");

        public static final ModifierId bic_dark_armor_plate = id("bic_dark_armor_plate");
        public static final ModifierId bic_frostbitten = id("bic_frostbitten");
        public static final ModifierId bic_intoxicating = id("bic_intoxicating");
        public static final ModifierId bic_life_stealer = id("bic_life_stealer");
        public static final ModifierId bic_krampus_horn = id("bic_krampus_horn");
        public static final ModifierId bic_nightmare_claw = id("bic_nightmare_claw");
        public static final ModifierId bic_infernal_ember = id("bic_infernal_ember");
        public static final ModifierId bic_hound_fang = id("bic_hound_fang");


        public static final ModifierId nova_spell_tiers = id("nova_spell_tiers");
        public static final ModifierId nova_creative_tiers = id("nova_creative_tiers");
        public static final ModifierId nova_spell_slots = id("nova_spell_slots");


        private static ModifierId id(String name) {
            return new ModifierId(Dreamtinker.MODID, name);
        }
    }

    @SubscribeEvent
    void gatherData(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        boolean server = event.includeServer();

        generator.addProvider(server, new ModifierTagProvider(packOutput, event.getExistingFileHelper()));
        generator.addProvider(server, new DreamtinkerModifierProvider(packOutput));
        generator.addProvider(server, new DreamtinkerFluidEffectProvider(packOutput));
        generator.addProvider(server, new DreamtinkerEnchantmentToModifierProvider(packOutput));
    }

    @SubscribeEvent
    void registerSerializers(RegisterEvent event) {
        if (event.getRegistryKey() == Registries.RECIPE_SERIALIZER){
            ModifierModule.LOADER.register(Dreamtinker.getLocation("swappable_circle_weapon_attack"), SwappableCircleWeaponAttack.LOADER);
            ModifierModule.LOADER.register(Dreamtinker.getLocation("effects_remover"), MobEffectsRemoverModule.LOADER);
        }
    }

    private static TagKey<Item> malumTag(String name) {
        return ItemTags.create(new ResourceLocation("malum", name));
    }
}
