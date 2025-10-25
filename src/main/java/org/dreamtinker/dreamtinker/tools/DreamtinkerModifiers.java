package org.dreamtinker.dreamtinker.tools;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.DreamtinkerModule;
import org.dreamtinker.dreamtinker.common.data.tags.ModifierTagProvider;
import org.dreamtinker.dreamtinker.tools.data.DreamtinkerEnchantmentToModifierProvider;
import org.dreamtinker.dreamtinker.tools.data.DreamtinkerFluidEffectProvider;
import org.dreamtinker.dreamtinker.tools.data.DreamtinkerModifierProvider;
import org.dreamtinker.dreamtinker.tools.entity.NarcissusFluidProjectile;
import org.dreamtinker.dreamtinker.tools.entity.SlashOrbitEntity;
import org.dreamtinker.dreamtinker.tools.items.TNTarrow.TNTarrow;
import org.dreamtinker.dreamtinker.tools.modifiers.tools.masu.realsweep;
import org.dreamtinker.dreamtinker.tools.modifiers.tools.masu.silvernamebee;
import org.dreamtinker.dreamtinker.tools.modifiers.tools.masu.strong_heavy;
import org.dreamtinker.dreamtinker.tools.modifiers.tools.narcissus_wing.flamingMemory;
import org.dreamtinker.dreamtinker.tools.modifiers.tools.narcissus_wing.foundationWill;
import org.dreamtinker.dreamtinker.tools.modifiers.tools.narcissus_wing.memoryBase;
import org.dreamtinker.dreamtinker.tools.modifiers.tools.narcissus_wing.splendourHeart;
import org.dreamtinker.dreamtinker.tools.modifiers.tools.tntarrow.strong_explode;
import org.dreamtinker.dreamtinker.tools.modifiers.tools.underPlate.weapon_transformation;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat.*;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy.*;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy.material.etherium.astral_break;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy.material.etherium.etherium_protection;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy.material.evil.evil_attack;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy.material.soul_aether.exiles_faulty;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.malum.*;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.armors.ender_dodge;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.armors.soul_blessing;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.armors.stone_heart;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.common.fly;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.common.life_looting;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.material.amber.wait_until;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.material.crying_obsidian.isolde;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.material.crying_obsidian.sharpened_with;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.material.echo_shard.echoed_attack;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.material.echo_shard.echoed_defence;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.material.lupus_antimony.the_wolf_answer;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.material.lupus_antimony.the_wolf_was;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.material.lupus_antimony.the_wolf_wonder;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.material.moonlight.glacialriver;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.material.nigrescence_antimony.broken_vessel;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.material.nigrescence_antimony.burning_in_vain;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.material.nigrescence_antimony.ewige_widerkunft;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.material.nigrescence_antimony.ouroboric_hourglass;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.material.star_regulus.as_one;
import org.dreamtinker.dreamtinker.tools.modifiers.traits.material.star_regulus.two_headed_seven;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
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
    }

    public static ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(Dreamtinker.MODID);
    public static ModifierDeferredRegister EL_MODIFIERS = ModifierDeferredRegister.create(Dreamtinker.MODID);
    public static ModifierDeferredRegister MALUM_MODIFIERS = ModifierDeferredRegister.create(Dreamtinker.MODID);
    //Mashuo
    public static final StaticModifier<realsweep> real_sweep = MODIFIERS.register("real_sweep", realsweep::new);
    public static final StaticModifier<strong_heavy> strong_heavy = MODIFIERS.register("strong_heavy", strong_heavy::new);
    public static final StaticModifier<silvernamebee> silver_name_bee = MODIFIERS.register("silver_name_bee", silvernamebee::new);
    //tnt arrow
    public static final StaticModifier<strong_explode> strong_explode = MODIFIERS.register("strong_explode", strong_explode::new);
    //narcissus wing
    public static final StaticModifier<memoryBase> memory_base = MODIFIERS.register("memory_base", memoryBase::new);
    public static final StaticModifier<flamingMemory> flaming_memory = MODIFIERS.register("flaming_memory", flamingMemory::new);
    public static final StaticModifier<foundationWill> foundation_will = MODIFIERS.register("foundation_will", foundationWill::new);
    public static final StaticModifier<splendourHeart> splendour_heart = MODIFIERS.register("splendour_heart", splendourHeart::new);
    //underplate
    public static final StaticModifier<weapon_transformation> weapon_transformation = MODIFIERS.register("weapon_transformation", weapon_transformation::new);
    //echo shard
    public static final StaticModifier<echoed_attack> echoed_attack = MODIFIERS.register("echoed_attack", echoed_attack::new);
    public static final StaticModifier<echoed_defence> echoed_defence = MODIFIERS.register("echoed_defence", echoed_defence::new);

    //moonlight ice
    public static final StaticModifier<glacialriver> glacial_river = MODIFIERS.register("glacial_river", glacialriver::new);
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
    public static final StaticModifier<isolde> isolde = MODIFIERS.register("isolde", isolde::new);

    public static final StaticModifier<org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat.mei> mei = MODIFIERS.register("mei", mei::new);
    public static final StaticModifier<fly> fly = MODIFIERS.register("fly", fly::new);
    public static final StaticModifier<org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat.open_soul> open_soul =
            MODIFIERS.register("open_soul", open_soul::new);
    public static final StaticModifier<soul_blessing> soul_blessing = MODIFIERS.register("soul_blessing", soul_blessing::new);
    public static final StaticModifier<random_hit> malum_distortion = MODIFIERS.register("malum_distortion", () -> new random_hit(0.9f, 1.2f));

    //etherium
    public static final StaticModifier<ender_dodge> ender_dodge = MODIFIERS.register("ender_dodge", ender_dodge::new);
    public static final StaticModifier<org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat.explosive_hit> explosive_hit =
            MODIFIERS.register("explosive_hit", explosive_hit::new);
    public static final StaticModifier<org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat.ranged_shoot> ranged_shoot =
            MODIFIERS.register("ranged_shoot", ranged_shoot::new);

    public static final StaticModifier<org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat.wither_shoot> wither_shoot =
            MODIFIERS.register("wither_shoot", wither_shoot::new);
    public static final StaticModifier<stone_heart> stone_heart = MODIFIERS.register("stone_heart", stone_heart::new);
    public static final StaticModifier<life_looting> life_looting = MODIFIERS.register("life_looting", life_looting::new);

    public static final StaticModifier<deepSleepWithRoar> deep_sleep_with_roar = MODIFIERS.register("deep_sleep_with_roar", deepSleepWithRoar::new);
    public static final StaticModifier<wait_until> wait_until = MODIFIERS.register("wait_until", wait_until::new);


    //etherium
    public static final StaticModifier<astral_break> astral_break = EL_MODIFIERS.register("astral_break", astral_break::new);
    public static final StaticModifier<etherium_protection> etherium_protection = EL_MODIFIERS.register("etherium_protection", etherium_protection::new);

    public static final StaticModifier<org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy.cursed_ring_bound> cursed_ring_bound =
            EL_MODIFIERS.register("cursed_ring_bound", cursed_ring_bound::new);
    //evil
    public static final StaticModifier<evil_attack> evil_attack = EL_MODIFIERS.register("evil_attack", evil_attack::new);
    public static final StaticModifier<org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy.ender_slayer> ender_slayer =
            EL_MODIFIERS.register("ender_slayer", ender_slayer::new);
    public static final StaticModifier<org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy.weapon_books> weapon_books =
            EL_MODIFIERS.register("weapon_books", weapon_books::new);
    public static final StaticModifier<org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy.eldritch_pan> eldritch_pan =
            EL_MODIFIERS.register("eldritch_pan", eldritch_pan::new);
    public static final StaticModifier<Modifier> by_pass_worthy = EL_MODIFIERS.register("by_pass_worthy", Modifier::new);
    public static final StaticModifier<exiles_faulty> exiles_faulty = EL_MODIFIERS.register("exiles_faulty", exiles_faulty::new);
    public static final StaticModifier<org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy.desolation_ring> desolation_ring =
            EL_MODIFIERS.register("desolation_ring", desolation_ring::new);

    //Malum modifiers
    public static final StaticModifier<malum_base> malum_base =
            MALUM_MODIFIERS.register("malum_base", malum_base::new);
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
    public static final StaticModifier<malum_magic_attack> malum_magic_attack =
            MALUM_MODIFIERS.register("malum_magic_attack", malum_magic_attack::new);
    public static final StaticModifier<MalumHexStaff> malum_erosion =
            MALUM_MODIFIERS.register("malum_erosion", () -> new MalumHexStaff(true));
    public static final StaticModifier<malum_thirsty> malum_thirsty =
            MALUM_MODIFIERS.register("malum_thirsty", malum_thirsty::new);
    public static final StaticModifier<malum_evolution> malum_evolution =
            MALUM_MODIFIERS.register("malum_evolution", malum_evolution::new);
    public static final StaticModifier<malum_spirit_defense> malum_spirit_defense =
            MALUM_MODIFIERS.register("malum_spirit_defense", malum_spirit_defense::new);
    public static final StaticModifier<MalumSolTiferet> malum_sol_tiferet =
            MALUM_MODIFIERS.register("malum_sol_tiferet", MalumSolTiferet::new);
    public static final StaticModifier<malumCatalystLobber> malum_catalyst_lobber =
            MALUM_MODIFIERS.register("malum_catalyst_lobber", malumCatalystLobber::new);

    public static class Ids {
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

        public static final ModifierId el_nemesis_curse = id("el_nemesis_curse");
        public static final ModifierId el_sorrow = id("el_sorrow");
        public static final ModifierId el_eternal_binding = id("el_eternal_binding");
        public static final ModifierId el_slayer = id("el_slayer");
        public static final ModifierId el_wrath = id("el_wrath");
        public static final ModifierId el_torrent = id("el_torrent");

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


        private static ModifierId id(String name) {
            return new ModifierId(Dreamtinker.MODID, name);
        }
    }


    public static final RegistryObject<EntityType<TNTarrow.TNTArrowEntity>> TNTARROW =
            ENTITIES.register("tnt_arrow",
                              () -> EntityType.Builder.<TNTarrow.TNTArrowEntity>of(TNTarrow.TNTArrowEntity::new, MobCategory.MISC)
                                                      .sized(0.5F, 0.5F) // 确保箭矢有合适的 hitbox
                                                      .clientTrackingRange(4) // 追踪范围，避免箭矢丢失
                                                      .updateInterval(20)

            );

    public static final RegistryObject<EntityType<NarcissusFluidProjectile>> NarcissusSpitEntity =
            ENTITIES.register("narcissus_fluid_spit",
                              () -> EntityType.Builder.<NarcissusFluidProjectile>of(NarcissusFluidProjectile::new, MobCategory.MISC).sized(1F, 1F)
                                                      .clientTrackingRange(4)
                                                      .updateInterval(10));
    public static final RegistryObject<EntityType<SlashOrbitEntity>> SLASH_ORBIT =
            ENTITIES.register("slash_orbit",
                              () -> EntityType.Builder.<SlashOrbitEntity>of(SlashOrbitEntity::new, MobCategory.MISC)
                                                      .sized(0.5f, 0.5f).clientTrackingRange(64).updateInterval(2));

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

    private static TagKey<Item> malumTag(String name) {
        return ItemTags.create(new ResourceLocation("malum", name));
    }
}
