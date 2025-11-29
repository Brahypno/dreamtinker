package org.dreamtinker.dreamtinker;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.dreamtinker.dreamtinker.Entity.AggressiveFox;
import org.dreamtinker.dreamtinker.Entity.NarcissusFluidProjectile;
import org.dreamtinker.dreamtinker.Entity.SlashOrbitEntity;
import org.dreamtinker.dreamtinker.library.LootModifier.ExtraDropLootModifier;
import org.dreamtinker.dreamtinker.tools.items.TNTArrow;
import org.dreamtinker.dreamtinker.world.TagAndTagRuleTest;
import slimeknights.mantle.registration.deferred.EntityTypeDeferredRegister;
import slimeknights.mantle.registration.deferred.FluidDeferredRegister;
import slimeknights.mantle.registration.deferred.SynchronizedDeferredRegister;
import slimeknights.tconstruct.common.registration.BlockDeferredRegisterExtension;
import slimeknights.tconstruct.common.registration.ItemDeferredRegisterExtension;

import static org.dreamtinker.dreamtinker.Dreamtinker.MODID;


public abstract class DreamtinkerModule {
    public static final DeferredRegister<Item> EL_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<Item> MALUM_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final ItemDeferredRegisterExtension MODI_TOOLS = new ItemDeferredRegisterExtension(MODID);
    public static final EntityTypeDeferredRegister ENTITIES = new EntityTypeDeferredRegister(MODID);
    public static final BlockDeferredRegisterExtension BLOCKS = new BlockDeferredRegisterExtension(MODID);
    public static final DeferredRegister<MobEffect> EFFECT = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);
    public static final DeferredRegister<MobEffect> EL_EFFECT = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);
    public static final DeferredRegister<MobEffect> MALUM_EFFECT = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);
    public static final FluidDeferredRegister FLUIDS = new FluidDeferredRegister(MODID);
    public static final FluidDeferredRegister EL_FLUIDS = new FluidDeferredRegister(MODID);
    public static final FluidDeferredRegister MALUM_FLUIDS = new FluidDeferredRegister(MODID);
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MODID);
    public static final SynchronizedDeferredRegister<CreativeModeTab> TABS = SynchronizedDeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<RuleTestType<?>> RULE_TESTS = DeferredRegister.create(Registries.RULE_TEST, MODID);

    public static final RegistryObject<RuleTestType<TagAndTagRuleTest>> TAG_AND_TAG =
            RULE_TESTS.register("tag_and_tag", () -> () -> TagAndTagRuleTest.CODEC);


    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ANTIMONY_LOOT =
            LOOT_MODIFIERS.register("extra_drop_loot", () -> ExtraDropLootModifier.CODEC);


    public static final RegistryObject<EntityType<TNTArrow.TNTArrowEntity>> TNTARROW =
            ENTITIES.register("tnt_arrow",
                              () -> EntityType.Builder.<TNTArrow.TNTArrowEntity>of(TNTArrow.TNTArrowEntity::new, MobCategory.MISC)
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
    public static final RegistryObject<EntityType<AggressiveFox>> AggressiveFOX =
            ENTITIES.register("aggressive_fox", () ->
                    EntityType.Builder.<AggressiveFox>of(AggressiveFox::new, MobCategory.CREATURE)
                                      .sized(0.6F, 0.7F) // 和原版狐狸一样
            );

    @SuppressWarnings({"removal"})
    public static void initRegisters(IEventBus bus) {
        ITEMS.register(bus);
        MODI_TOOLS.register(bus);
        BLOCKS.register(bus);
        FLUIDS.register(bus);
        ENTITIES.register(bus);
        EFFECT.register(bus);
        LOOT_MODIFIERS.register(bus);
        if (ModList.get().isLoaded("enigmaticlegacy")){
            EL_FLUIDS.register(bus);
            EL_ITEMS.register(bus);
            EL_EFFECT.register(bus);
        }
        if (ModList.get().isLoaded("malum")){
            MALUM_FLUIDS.register(bus);
            MALUM_ITEMS.register(bus);
            MALUM_EFFECT.register(bus);
        }
        TABS.register(bus);
        RULE_TESTS.register(bus);
    }
}

