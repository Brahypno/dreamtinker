package org.brahypno.dreamtinker.tools;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.DreamtinkerModule;
import org.brahypno.dreamtinker.common.data.tags.ModifierTagProvider;
import org.brahypno.dreamtinker.library.modifiers.fluid.block.AutoTagCycleBlockFluidEffect;
import org.brahypno.dreamtinker.library.modifiers.fluid.entity.ConditionalDamageFluidEffect;
import org.brahypno.dreamtinker.library.modifiers.fluid.entity.DespairScalingDamageFluidEffect;
import org.brahypno.dreamtinker.library.modifiers.fluid.entity.DrainLifeFluidEffect;
import org.brahypno.dreamtinker.library.modifiers.modules.combat.NarcissusFluidFeedbackModule;
import org.brahypno.dreamtinker.library.modifiers.modules.harvest.AutoPureDaisyModule;
import org.brahypno.dreamtinker.tools.data.DreamtinkerEnchantmentToModifierProvider;
import org.brahypno.dreamtinker.tools.data.DreamtinkerFluidEffectProvider;
import org.brahypno.dreamtinker.tools.data.DreamtinkerModifierProvider;
import org.brahypno.dreamtinker.tools.modifiers.tools.chain_saw_blade.DeathShredder;
import org.brahypno.dreamtinker.tools.modifiers.tools.mashou.RealSweep;
import org.brahypno.dreamtinker.tools.modifiers.tools.mashou.StrongHeavy;
import org.brahypno.dreamtinker.tools.modifiers.tools.narcissus_wing.FlamingMemory;
import org.brahypno.dreamtinker.tools.modifiers.tools.narcissus_wing.MemoryBase;
import org.brahypno.dreamtinker.tools.modifiers.tools.narcissus_wing.SplendourHeart;
import org.brahypno.dreamtinker.tools.modifiers.tools.narcissus_wing.foundationWill;
import org.brahypno.dreamtinker.tools.modifiers.tools.silence_glove.WeaponDreams;
import org.brahypno.dreamtinker.tools.modifiers.tools.underPlate.WeaponTransformation;
import org.brahypno.dreamtinker.tools.modifiers.traits.Combat.*;
import org.brahypno.dreamtinker.tools.modifiers.traits.Compact.bic.DarkBlade;
import org.brahypno.dreamtinker.tools.modifiers.traits.Compact.bic.DarkDefense;
import org.brahypno.dreamtinker.tools.modifiers.traits.Compact.bic.NightmareDefense;
import org.brahypno.dreamtinker.tools.modifiers.traits.Compact.bloodmagic.SentientWillModifier;
import org.brahypno.dreamtinker.tools.modifiers.traits.Compact.eidolon.EidolonDeathBringer;
import org.brahypno.dreamtinker.tools.modifiers.traits.Compact.eidolon.EidolonReaper;
import org.brahypno.dreamtinker.tools.modifiers.traits.Compact.eidolon.EidolonSapping;
import org.brahypno.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy.*;
import org.brahypno.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy.material.etherium.ELAstralBreak;
import org.brahypno.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy.material.etherium.EtheriumProtection;
import org.brahypno.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy.material.evil.EvilAttack;
import org.brahypno.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy.material.soul_aether.ExilesFaulty;
import org.brahypno.dreamtinker.tools.modifiers.traits.Compact.faa.CorruptionDefense;
import org.brahypno.dreamtinker.tools.modifiers.traits.Compact.legendary_monsters.SoulRage;
import org.brahypno.dreamtinker.tools.modifiers.traits.Compact.legendary_monsters.annihilatorArmorPower;
import org.brahypno.dreamtinker.tools.modifiers.traits.Compact.malum.*;
import org.brahypno.dreamtinker.tools.modifiers.traits.Compact.occ.OtherWorldView;
import org.brahypno.dreamtinker.tools.modifiers.traits.Compact.occ.OtherworldHarvest;
import org.brahypno.dreamtinker.tools.modifiers.traits.armors.*;
import org.brahypno.dreamtinker.tools.modifiers.traits.common.*;
import org.brahypno.dreamtinker.tools.modifiers.traits.harvest.ArrowHarvest;
import org.brahypno.dreamtinker.tools.modifiers.traits.harvest.HoneyTastyModifier;
import org.brahypno.dreamtinker.tools.modifiers.traits.harvest.LifeLooting;
import org.brahypno.dreamtinker.tools.modifiers.traits.harvest.SilkyCrystal;
import org.brahypno.dreamtinker.tools.modifiers.traits.material.OathSteel.*;
import org.brahypno.dreamtinker.tools.modifiers.traits.material.Serpent_Shift.Cryo.DeathCrossing;
import org.brahypno.dreamtinker.tools.modifiers.traits.material.Serpent_Shift.Cryo.RemoverHit;
import org.brahypno.dreamtinker.tools.modifiers.traits.material.Serpent_Shift.ReasonBeyondReasonModifier;
import org.brahypno.dreamtinker.tools.modifiers.traits.material.crying_obsidian.Isolde;
import org.brahypno.dreamtinker.tools.modifiers.traits.material.crying_obsidian.SharpenedWith;
import org.brahypno.dreamtinker.tools.modifiers.traits.material.deliverance.BurdenBearer;
import org.brahypno.dreamtinker.tools.modifiers.traits.material.deliverance.Metamorphosis;
import org.brahypno.dreamtinker.tools.modifiers.traits.material.deliverance.Signet;
import org.brahypno.dreamtinker.tools.modifiers.traits.material.desire_gem.EternityDefense;
import org.brahypno.dreamtinker.tools.modifiers.traits.material.desire_gem.MusouIsshinModifier;
import org.brahypno.dreamtinker.tools.modifiers.traits.material.desire_gem.VisionaryWishes;
import org.brahypno.dreamtinker.tools.modifiers.traits.material.despair_gem.*;
import org.brahypno.dreamtinker.tools.modifiers.traits.material.echo_shard.EchoedAttack;
import org.brahypno.dreamtinker.tools.modifiers.traits.material.echo_shard.EchoedDefence;
import org.brahypno.dreamtinker.tools.modifiers.traits.material.fifth_stone.FourWarning;
import org.brahypno.dreamtinker.tools.modifiers.traits.material.livingSoulSteel.AdaptionAlgorithm;
import org.brahypno.dreamtinker.tools.modifiers.traits.material.livingSoulSteel.AdaptionAlgorithmDamage;
import org.brahypno.dreamtinker.tools.modifiers.traits.material.livingSoulSteel.AdaptionAlgorithmProtection;
import org.brahypno.dreamtinker.tools.modifiers.traits.material.lupus_antimony.TheWolfAnswer;
import org.brahypno.dreamtinker.tools.modifiers.traits.material.lupus_antimony.TheWolfWas;
import org.brahypno.dreamtinker.tools.modifiers.traits.material.lupus_antimony.TheWolfWonder;
import org.brahypno.dreamtinker.tools.modifiers.traits.material.nigrescence_antimony.BurningInVain;
import org.brahypno.dreamtinker.tools.modifiers.traits.material.nigrescence_antimony.EwigeEiderkunft;
import org.brahypno.dreamtinker.tools.modifiers.traits.material.nigrescence_antimony.broken_vessel;
import org.brahypno.dreamtinker.tools.modifiers.traits.material.nigrescence_antimony.ouroboric_hourglass;
import org.brahypno.dreamtinker.tools.modifiers.traits.material.ruin_wheel.DoomTrack;
import org.brahypno.dreamtinker.tools.modifiers.traits.material.ruin_wheel.RayAttack;
import org.brahypno.dreamtinker.tools.modifiers.traits.material.scolecite.AsWing;
import org.brahypno.dreamtinker.tools.modifiers.traits.material.scolecite.AwaitingHour;
import org.brahypno.dreamtinker.tools.modifiers.traits.material.scolecite.FeatherWake;
import org.brahypno.dreamtinker.tools.modifiers.traits.material.scolecite.pupalOmen;
import org.brahypno.dreamtinker.tools.modifiers.traits.material.star_regulus.TwoHeadedSeven;
import org.brahypno.dreamtinker.tools.modifiers.traits.material.star_regulus.as_one;
import org.brahypno.dreamtinker.tools.modifiers.traits.material.whimsyGold.RhinegoldCatModifier;
import org.brahypno.dreamtinker.utils.CompactUtils.ForbiddenArcanusAurealCompact;
import slimeknights.mantle.data.predicate.block.BlockPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.json.variable.entity.EntityVariable;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffect;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;


public final class DreamtinkerModifiers extends DreamtinkerModule {

    public static ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(Dreamtinker.MODID);
    public static ModifierDeferredRegister EL_MODIFIERS = ModifierDeferredRegister.create(Dreamtinker.MODID);
    public static ModifierDeferredRegister MALUM_MODIFIERS = ModifierDeferredRegister.create(Dreamtinker.MODID);
    public static ModifierDeferredRegister EIDOLON_MODIFIERS = ModifierDeferredRegister.create(Dreamtinker.MODID);
    public static ModifierDeferredRegister BIC_MODIFIERS = ModifierDeferredRegister.create(Dreamtinker.MODID);
    public static ModifierDeferredRegister OCC_MODIFIERS = ModifierDeferredRegister.create(Dreamtinker.MODID);
    public static ModifierDeferredRegister LM_MODIFIERS = ModifierDeferredRegister.create(Dreamtinker.MODID);
    public static ModifierDeferredRegister FAA_MODIFIERS = ModifierDeferredRegister.create(Dreamtinker.MODID);
    public static ModifierDeferredRegister BLM_MODIFIERS = ModifierDeferredRegister.create(Dreamtinker.MODID);
    //Mashuo
    public static final StaticModifier<RealSweep> real_sweep = MODIFIERS.register("real_sweep", RealSweep::new);
    public static final StaticModifier<StrongHeavy> strong_heavy = MODIFIERS.register("strong_heavy", StrongHeavy::new);
    //narcissus wing
    public static final StaticModifier<MemoryBase> memory_base = MODIFIERS.register("memory_base", MemoryBase::new);
    public static final StaticModifier<FlamingMemory> flaming_memory = MODIFIERS.register("flaming_memory", FlamingMemory::new);
    public static final StaticModifier<foundationWill> foundation_will = MODIFIERS.register("foundation_will", foundationWill::new);
    public static final StaticModifier<SplendourHeart> splendour_heart = MODIFIERS.register("splendour_heart", SplendourHeart::new);
    //underPlate
    public static final StaticModifier<WeaponTransformation> weapon_transformation = MODIFIERS.register("weapon_transformation", WeaponTransformation::new);
    //echo Alloy
    public static final StaticModifier<EchoedAttack> echoed_attack = MODIFIERS.register("echoed_attack", EchoedAttack::new);
    public static final StaticModifier<EchoedDefence> echoed_defence = MODIFIERS.register("echoed_defence", EchoedDefence::new);

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
    // living soul steel
    public static final StaticModifier<AdaptionAlgorithm> adaption_algorithm = MODIFIERS.register("adaption_algorithm", AdaptionAlgorithm::new);
    public static final StaticModifier<AdaptionAlgorithmDamage> adaption_algorithm_damage =
            MODIFIERS.register("adaption_algorithm_damage", AdaptionAlgorithmDamage::new);
    public static final StaticModifier<AdaptionAlgorithmProtection> adaption_algorithm_protection =
            MODIFIERS.register("adaption_algorithm_protection", AdaptionAlgorithmProtection::new);
    // star regulus
    public static final StaticModifier<as_one> as_one = MODIFIERS.register("as_one", as_one::new);
    public static final StaticModifier<TwoHeadedSeven> two_headed_seven = MODIFIERS.register("two_headed_seven", TwoHeadedSeven::new);
    //crying obsidian
    public static final StaticModifier<SharpenedWith> sharpened_with = MODIFIERS.register("sharpened_with", SharpenedWith::new);
    public static final StaticModifier<Isolde> isolde = MODIFIERS.register("isolde", Isolde::new);

    public static final StaticModifier<mei> mei = MODIFIERS.register("mei", mei::new);
    public static final StaticModifier<acheron> acheron = MODIFIERS.register("acheron", acheron::new);
    public static final StaticModifier<OpenSoul> open_soul = MODIFIERS.register("open_soul", OpenSoul::new);
    public static final StaticModifier<SoulBlessing> soul_blessing = MODIFIERS.register("soul_blessing", SoulBlessing::new);
    public static final StaticModifier<RandomHit> malum_distortion = MODIFIERS.register("malum_distortion", () -> new RandomHit(0.9f, 1.2f));
    public static final StaticModifier<WhyICry> why_i_cry = MODIFIERS.register("why_i_cry", WhyICry::new);

    //etherium
    public static final StaticModifier<ExplosiveDefense> explosive_defense = MODIFIERS.register("explosive_defense", ExplosiveDefense::new);
    public static final StaticModifier<ExplosiveHit> explosive_hit = MODIFIERS.register("explosive_hit", ExplosiveHit::new);
    public static final StaticModifier<RangedShoot> ranged_shoot = MODIFIERS.register("ranged_shoot", RangedShoot::new);

    public static final StaticModifier<WitherShoot> wither_shoot = MODIFIERS.register("wither_shoot", WitherShoot::new);
    public static final StaticModifier<StoneHeart> stone_heart = MODIFIERS.register("stone_heart", StoneHeart::new);
    public static final StaticModifier<LifeLooting> life_looting = MODIFIERS.register("life_looting", LifeLooting::new);

    public static final StaticModifier<DeepSleepWithRoar> deep_sleep_with_roar = MODIFIERS.register("deep_sleep_with_roar", DeepSleepWithRoar::new);
    public static final StaticModifier<WaitUntil> wait_until = MODIFIERS.register("wait_until", WaitUntil::new);
    public static final StaticModifier<AnvilHit> anvil_hit = MODIFIERS.register("anvil_hit", AnvilHit::new);
    public static final StaticModifier<AbsorptionHit> absorption_hit = MODIFIERS.register("absorption_hit", AbsorptionHit::new);
    public static final StaticModifier<AbsorptionDefense> absorption_defense = MODIFIERS.register("absorption_defense", AbsorptionDefense::new);
    public static final StaticModifier<DespairMist> despair_mist = MODIFIERS.register("despair_mist", DespairMist::new);
    public static final StaticModifier<DespairRain> despair_rain = MODIFIERS.register("despair_rain", DespairRain::new);
    public static final StaticModifier<DespairWind> despair_wind = MODIFIERS.register("despair_wind", DespairWind::new);
    public static final StaticModifier<Requiem> requiem = MODIFIERS.register("requiem", Requiem::new);
    public static final StaticModifier<Ophelia> ophelia = MODIFIERS.register("ophelia", Ophelia::new);
    public static final StaticModifier<WeaponDreams> weapon_dreams = MODIFIERS.register("weapon_dreams", WeaponDreams::new);
    public static final StaticModifier<HoneyTastyModifier> HoneyTastyModifier = MODIFIERS.register("honey_tasty", HoneyTastyModifier::new);
    public static final StaticModifier<RainbowCatcher> rainbowCatcher = MODIFIERS.register("rainbow_catcher", RainbowCatcher::new);
    public static final StaticModifier<not_like_was> not_like_was = MODIFIERS.register("not_like_was", not_like_was::new);
    public static final StaticModifier<LightInDark> light_in_dark = MODIFIERS.register("light_in_dark", LightInDark::new);
    public static final StaticModifier<LunarDurabilityDefense> lunar_defense = MODIFIERS.register("lunar_defense", LunarDurabilityDefense::new);
    public static final StaticModifier<blockViewer> OreViewer = MODIFIERS.register("ore_viewer", () -> new blockViewer(Tags.Blocks.ORES.location(), 0.8f));
    public static final StaticModifier<HiddenHit> hiddenHit = MODIFIERS.register("hidden_hit", HiddenHit::new);
    public static final StaticModifier<knockArts> knockArts = MODIFIERS.register("knock_arts", knockArts::new);
    public static final StaticModifier<AsSand> as_sand = MODIFIERS.register("as_sand", AsSand::new);
    public static final StaticModifier<TheEnd> TheEnd = MODIFIERS.register("the_end", TheEnd::new);
    public static final StaticModifier<knockBacker> SunAway = MODIFIERS.register("solar_away", knockBacker::new);
    public static final StaticModifier<DeathShredder> death_shredder = MODIFIERS.register("death_shredder", DeathShredder::new);
    public static final StaticModifier<SignalAxe> signal_axe = MODIFIERS.register("signal_axe", SignalAxe::new);
    public static final StaticModifier<LoveShooting> love_shooting = MODIFIERS.register("love_shooting", LoveShooting::new);
    public static final StaticModifier<TeleportShoot> teleport_shooting = MODIFIERS.register("teleport_shooting", TeleportShoot::new);
    public static final StaticModifier<FoxBlessing> fox_blessing = MODIFIERS.register("fox_blessing", FoxBlessing::new);
    public static final StaticModifier<VirtualDodge> virtual_dodge = MODIFIERS.register("virtual_dodge", VirtualDodge::new);
    public static final StaticModifier<GoliathDamage> goliath_damage = MODIFIERS.register("goliath_damage", GoliathDamage::new);
    public static final StaticModifier<SilkyCrystal> silky_crystal = MODIFIERS.register("silky_crystal", SilkyCrystal::new);
    public static final StaticModifier<ArrowHarvest> arrow_harvest = MODIFIERS.register("arrow_harvest", ArrowHarvest::new);
    public static final StaticModifier<OverSticky> over_sticky = MODIFIERS.register("over_sticky", OverSticky::new);
    public static final StaticModifier<SideAttack> side_attack = MODIFIERS.register("side_attack", SideAttack::new);
    public static final StaticModifier<ViewTracing> view_tracing = MODIFIERS.register("view_tracing", ViewTracing::new);
    public static final StaticModifier<BornWithMe> born_with_me = MODIFIERS.register("born_with_me", BornWithMe::new);
    public static final StaticModifier<RainbowLights> rainbow_lights = MODIFIERS.register("rainbow_lights", RainbowLights::new);
    public static final StaticModifier<AutoRefill> auto_refill = MODIFIERS.register("auto_refill", AutoRefill::new);
    public static final StaticModifier<DoomTrack> doom_track = MODIFIERS.register("doom_track", DoomTrack::new);
    public static final StaticModifier<RayAttack> doom_ray = MODIFIERS.register("doom_ray", RayAttack::new);
    public static final StaticModifier<AsWing> as_wing = MODIFIERS.register("as_wing", AsWing::new);
    public static final StaticModifier<pupalOmen> pupal_omen = MODIFIERS.register("pupal_omen", pupalOmen::new);
    public static final StaticModifier<AwaitingHour> awaiting_hour = MODIFIERS.register("awaiting_hour", AwaitingHour::new);
    public static final StaticModifier<FeatherWake> feather_wake = MODIFIERS.register("feather_wake", FeatherWake::new);
    public static final StaticModifier<PaleOath> pale_oath = MODIFIERS.register("pale_oath", PaleOath::new);
    public static final StaticModifier<HealOath> heal_oath = MODIFIERS.register("heal_oath", HealOath::new);
    public static final StaticModifier<ForOath> for_oath = MODIFIERS.register("for_oath", ForOath::new);
    public static final StaticModifier<ReturningArrow> returning_arrow = MODIFIERS.register("returning_arrow", ReturningArrow::new);
    public static final StaticModifier<Sunless> sunless = MODIFIERS.register("sunless", Sunless::new);
    public static final StaticModifier<BrokenOath> broken_oath = MODIFIERS.register("broken_oath", BrokenOath::new);
    public static final StaticModifier<ForOath> forlorn_judgment = MODIFIERS.register("forlorn_judgment", ForOath::new);
    public static final StaticModifier<LastBody> last_body = MODIFIERS.register("last_body", LastBody::new);
    public static final StaticModifier<ArcaneHit> arcane_hit = MODIFIERS.register("arcane_hit", ArcaneHit::new);
    public static final StaticModifier<FourWarning> four_warning = MODIFIERS.register("four_warning", FourWarning::new);
    public static final StaticModifier<EternityDefense> eternity_defense = MODIFIERS.register("eternity_defense", EternityDefense::new);
    public static final StaticModifier<MusouIsshinModifier> to_the_moon = MODIFIERS.register("to_the_moon", MusouIsshinModifier::new);
    public static final StaticModifier<VisionaryWishes> many_wishes = MODIFIERS.register("many_wishes", VisionaryWishes::new);
    public static final StaticModifier<Signet> signet = MODIFIERS.register("signet", Signet::new);
    public static final StaticModifier<Metamorphosis> metamorphosis = MODIFIERS.register("metamorphosis", Metamorphosis::new);
    public static final StaticModifier<BurdenBearer> burden_bearer = MODIFIERS.register("burden_bearer", BurdenBearer::new);
    public static final StaticModifier<RhinegoldCatModifier> rhinegold_cat = MODIFIERS.register("rhinegold_cat", RhinegoldCatModifier::new);

    public static final StaticModifier<ReasonBeyondReasonModifier> ultra_logic = MODIFIERS.register("ultra_logic", ReasonBeyondReasonModifier::new);
    public static final StaticModifier<RemoverHit> obol_of_all_rivers = MODIFIERS.register("obol_of_all_rivers", RemoverHit::new);
    public static final StaticModifier<DeathCrossing> ford_the_broken_crossing = MODIFIERS.register("ford_the_broken_crossing", DeathCrossing::new);

    //etherium
    public static final StaticModifier<ELAstralBreak> astral_break = EL_MODIFIERS.register("astral_break", ELAstralBreak::new);
    public static final StaticModifier<EtheriumProtection> etherium_protection = EL_MODIFIERS.register("etherium_protection", EtheriumProtection::new);

    public static final StaticModifier<CursedRingBound> cursed_ring_bound = EL_MODIFIERS.register("cursed_ring_bound", CursedRingBound::new);
    //evil
    public static final StaticModifier<EvilAttack> evil_attack = EL_MODIFIERS.register("evil_attack", EvilAttack::new);
    public static final StaticModifier<ELEnderSlayer> ender_slayer = EL_MODIFIERS.register("ender_slayer", ELEnderSlayer::new);
    public static final StaticModifier<WeaponBooks> weapon_books = EL_MODIFIERS.register("weapon_books", WeaponBooks::new);
    public static final StaticModifier<EldritchPan> eldritch_pan = EL_MODIFIERS.register("eldritch_pan", EldritchPan::new);
    public static final StaticModifier<ExilesFaulty> exiles_faulty = EL_MODIFIERS.register("exiles_faulty", ExilesFaulty::new);
    public static final StaticModifier<DesolationRing> desolation_ring = EL_MODIFIERS.register("desolation_ring", DesolationRing::new);

    //Malum modifiers
    public static final StaticModifier<MalumBase> malum_base = MALUM_MODIFIERS.register("malum_base", MalumBase::new);
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

    public static final StaticModifier<DarkDefense> bic_dark_defense =
            BIC_MODIFIERS.register("bic_dark_defense", DarkDefense::new);
    public static final StaticModifier<DarkBlade> bic_dark_blade =
            BIC_MODIFIERS.register("bic_dark_blade", DarkBlade::new);
    public static final StaticModifier<NightmareDefense> bic_nightmare_defense =
            BIC_MODIFIERS.register("bic_nightmare_defense", NightmareDefense::new);
    public static final StaticModifier<OtherWorldView> occ_view =
            OCC_MODIFIERS.register("occ_view", OtherWorldView::new);
    public static final StaticModifier<OtherworldHarvest> occ_harvest = OCC_MODIFIERS.register("occ_harvest", OtherworldHarvest::new);

    public static final StaticModifier<SoulRage> soul_rage = LM_MODIFIERS.register("soul_rage", SoulRage::new);
    public static final StaticModifier<annihilatorArmorPower> annihilator_armor_power =
            LM_MODIFIERS.register("annihilator_armor_power", annihilatorArmorPower::new);

    public static final StaticModifier<CorruptionDefense> corruption_defense = FAA_MODIFIERS.register("faa_corruption_defense", CorruptionDefense::new);
    public static final StaticModifier<SentientWillModifier> sentient_will = BLM_MODIFIERS.register("sentient_will", SentientWillModifier::new);

    public static final EntityVariable AUREAL =
            EntityVariable.simple(entity -> entity instanceof Player player ? ForbiddenArcanusAurealCompact.getAureal(player) : 0);
    public static final EntityVariable CORRUPTION =
            EntityVariable.simple(entity -> entity instanceof Player player ? ForbiddenArcanusAurealCompact.getCorruption(player) : 0);
    public static BlockPredicate BLOCK_OF_UNDER_GARDEN = BlockPredicate.simple(state -> {
        ResourceLocation id = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        return id != null && id.getNamespace().matches("undergarden");
    });
    public static LivingEntityPredicate LIVING_OF_UNDER_GARDEN = LivingEntityPredicate.simple(le -> {
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(le.getType());
        return id != null && id.getNamespace().matches("undergarden");
    });

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
        if (ModList.get().isLoaded("legendary_monsters")){
            LM_MODIFIERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }
        if (ModList.get().isLoaded("occultism")){
            OCC_MODIFIERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }
        if (ModList.get().isLoaded("forbidden_arcanus")){
            FAA_MODIFIERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }
        if (ModList.get().isLoaded("bloodmagic")){
            BLM_MODIFIERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }
    }

    @SubscribeEvent
    void registerSerializers(RegisterEvent event) {
        if (event.getRegistryKey() == Registries.RECIPE_SERIALIZER){
            ModifierModule.LOADER.register(Dreamtinker.getLocation("narcissus_fluid_feedback"), NarcissusFluidFeedbackModule.LOADER);
            ModifierModule.LOADER.register(Dreamtinker.getLocation("bontania_auto_pure_module"), AutoPureDaisyModule.LOADER);

            FluidEffect.ENTITY_EFFECTS.register(Dreamtinker.getLocation("drain_life_fluid"), DrainLifeFluidEffect.LOADER);
            FluidEffect.ENTITY_EFFECTS.register(Dreamtinker.getLocation("conditional_damage_fluid"), ConditionalDamageFluidEffect.LOADER);
            FluidEffect.ENTITY_EFFECTS.register(Dreamtinker.getLocation("despair_scaling_damage_fluid"), DespairScalingDamageFluidEffect.LOADER);
            FluidEffect.BLOCK_EFFECTS.register(Dreamtinker.getLocation("auto_tag_cycle_block_fluid"), AutoTagCycleBlockFluidEffect.LOADER);

            LivingEntityPredicate.LOADER.register(Dreamtinker.getLocation("living_of_undergarden"), LIVING_OF_UNDER_GARDEN.getLoader());
            BlockPredicate.LOADER.register(Dreamtinker.getLocation("block_of_undergarden"), BLOCK_OF_UNDER_GARDEN.getLoader());

            EntityVariable.LOADER.register(Dreamtinker.getLocation("faa_aureal"), AUREAL.getLoader());

            EntityVariable.LOADER.register(Dreamtinker.getLocation("faa_corruption"), CORRUPTION.getLoader());
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

    public static class Ids {
        public static final ModifierId fly = id("fly");
        public static final ModifierId soul_form = id("soul_form");
        public static final ModifierId soul_upgrade = id("soul_upgrade");
        public static final ModifierId abyss_inside = id("abyss_inside");
        public static final ModifierId meta_morphosis = id("meta_morphosis");
        public static final ModifierId blighted_sigil = id("blighted_sigil");
        public static final ModifierId many_us = id("many_us");
        public static final ModifierId ashen_soul = id("ashen_soul");
        public static final ModifierId naughty_chaos = id("naughty_chaos");
        public static final ModifierId cosmogony_tetrad = id("cosmogony_tetrad");
        public static final ModifierId otherworld_precious = id("otherworld_precious");

        public static final ModifierId long_tool = id("long_tool");
        public static final ModifierId strong_explode = id("strong_explode");

        public static final ModifierId antimony_usage = id("antimony_usage");
        public static final ModifierId with_tears = id("with_tears");
        public static final ModifierId in_rain = id("in_rain");
        public static final ModifierId wither_body = id("wither_body");
        public static final ModifierId continuous_explode = id("continuous_explode");
        public static final ModifierId soul_core = id("soul_core");
        public static final ModifierId icy_memory = id("icy_memory");
        public static final ModifierId hate_memory = id("hate_memory");
        public static final ModifierId huge_ego = id("huge_ego");
        public static final ModifierId full_concentration = id("full_concentration");
        public static final ModifierId thundering_curse = id("thundering_curse");
        public static final ModifierId ykhEULA = id("ykh_eula");
        public static final ModifierId MorningLordEULA = id("morning_lord_eula");
        public static final ModifierId EULA = id("eula");
        public static final ModifierId FragileButBright = id("fragile_but_bright");
        public static final ModifierId homunculus_life_curse = id("homunculus_life_curse");
        public static final ModifierId homunculusGift = id("homunculus_gift");
        public static final ModifierId peaches_in_memory = id("peaches_in_memory");
        public static final ModifierId weapon_slots = id("weapon_slots");
        public static final ModifierId shadow_blessing = id("shadow_blessing");
        public static final ModifierId silver_name_bee = id("silver_name_bee");
        public static final ModifierId the_romantic = id("the_romantic");
        public static final ModifierId all_slayer = id("all_slayer");
        public static final ModifierId weapon_dreams_filter = id("weapon_dreams_filter");
        public static final ModifierId weapon_dreams_order = id("weapon_dreams_order");
        public static final ModifierId fiber_glass_fragments = id("fiber_glass_fragments");
        public static final ModifierId light_emanation = id("light_emanation");
        public static final ModifierId lunarProtection = id("lunar_protection");
        public static final ModifierId lunarRejection = id("lunar_rejection");
        public static final ModifierId slowness = id("ssss_slowness");
        public static final ModifierId soul_unchanged = id("soul_unchanged");
        public static final ModifierId sun_changed = id("sun_changed");
        public static final ModifierId force_to_explosion = id("force_to_explosion");
        public static final ModifierId aggressiveFoxUsage = id("aggressive_fox_usage");
        public static final ModifierId five_creations = id("five_creations");
        public static final ModifierId golden_face = id("golden_face");
        public static final ModifierId whimsy_face = id("whimsy_face");
        public static final ModifierId arcane_protection = id("arcane_protection");
        public static final ModifierId drinker_magic = id("drinker_magic");
        public static final ModifierId monster_blood = id("monster_blood");
        public static final ModifierId deeper_water_killer = id("deeper_water_killer");
        public static final ModifierId sun_shine = id("sun_shine");
        public static final ModifierId heavy_arrow = id("heavy_arrow");
        public static final ModifierId light_arrow = id("light_arrow");
        public static final ModifierId balanced_arrow = id("balanced_arrow");
        public static final ModifierId null_void = id("null_void");
        public static final ModifierId hidden_shape = id("hidden_shape");
        public static final ModifierId wrath = id("wrath");
        public static final ModifierId torrent = id("torrent");
        public static final ModifierId poison = id("poison");
        public static final ModifierId weakness = id("weakness");
        public static final ModifierId sweet_death = id("sweet_death");
        public static final ModifierId last_kiss = id("last_kiss");
        public static final ModifierId curse_fire = id("curse_fire");
        public static final ModifierId falsify_fate = id("falsify_fate");
        public static final ModifierId frost_steel_shell = id("frost_steel_shell");
        public static final ModifierId sticky_string = id("sticky_string");
        public static final ModifierId pressing_front = id("pressing_front");
        public static final ModifierId with_wing_with_scale = id("with_wing_with_scale");
        public static final ModifierId scale_within = id("scale_within");
        public static final ModifierId wing_without = id("wing_without");
        public static final ModifierId carapace_fall = id("carapace_fall");
        public static final ModifierId reprise_protection = id("reprise_protection");
        public static final ModifierId huge_explosion = id("huge_explosion");
        public static final ModifierId unbreakable = id("unbreakable");
        public static final ModifierId divineMaledictus = id("divine_maledictus");


        public static final ModifierId el_nemesis_curse = id("el_nemesis_curse");
        public static final ModifierId el_sorrow = id("el_sorrow");
        public static final ModifierId el_eternal_binding = id("el_eternal_binding");
        public static final ModifierId el_etherium = id("el_etherium");
        public static final ModifierId el_by_pass_worthy = id("el_by_pass_worthy");

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
        public static final ModifierId spiritual_weapon_transformation = id("spiritual_weapon_transformation");

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
        public static final ModifierId nova_abjuration_essence = id("nova_abjuration_essence");
        public static final ModifierId nova_air_essence = id("nova_air_essence");
        public static final ModifierId nova_earth_essence = id("nova_earth_essence");
        public static final ModifierId nova_fire_essence = id("nova_fire_essence");
        public static final ModifierId nova_manipulation_essence = id("nova_manipulation_essence");
        public static final ModifierId nova_water_essence = id("nova_water_essence");
        public static final ModifierId nova_mana_reduce = id("nova_mana_reduce");
        public static final ModifierId nova_ashen_resolve = id("nova_ashen_resolve");
        public static final ModifierId nova_reactive = id("nova_reactive");

        public static final ModifierId undergarden_rot_killer = id("undergarden_rot_killer");
        public static final ModifierId undergarden_rot_protection = id("undergarden_rot_protection");
        public static final ModifierId undergarden_killer = id("undergarden_killer");
        public static final ModifierId undergarden_miner = id("undergarden_miner");
        public static final ModifierId undergarden_protection = id("undergarden_protection");

        public static final ModifierId botania_pure_smeltery = id("botania_pure_smeltery");

        public static final ModifierId not_end_er = id("not_end_er");
        public static final ModifierId ender_end = id("ender_end");
        public static final ModifierId ender_protection = id("ender_protection");

        public static final ModifierId faa_aureal_protection = id("faa_aureal_protection");
        public static final ModifierId faa_aureal_attack = id("faa_aureal_attack");
        public static final ModifierId faa_corruption_attack = id("faa_corruption_attack");

        private static ModifierId id(String name) {
            return new ModifierId(Dreamtinker.MODID, name);
        }
    }

    private static TagKey<Item> malumTag(String name) {
        return ItemTags.create(new ResourceLocation("malum", name));
    }
}
