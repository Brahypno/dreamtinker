package org.dreamtinker.dreamtinker;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.dreamtinker.dreamtinker.library.LootModifier.ExtraDropLootModifier;
import slimeknights.mantle.registration.deferred.EntityTypeDeferredRegister;
import slimeknights.mantle.registration.deferred.FluidDeferredRegister;
import slimeknights.mantle.registration.deferred.SynchronizedDeferredRegister;
import slimeknights.tconstruct.common.registration.BlockDeferredRegisterExtension;
import slimeknights.tconstruct.common.registration.ItemDeferredRegisterExtension;

import static org.dreamtinker.dreamtinker.Dreamtinker.MODID;


public abstract class DreamtinkerModule {
    public static final DeferredRegister<Item> EL_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final ItemDeferredRegisterExtension MODI_TOOLS = new ItemDeferredRegisterExtension(MODID);
    public static final EntityTypeDeferredRegister ENTITIES = new EntityTypeDeferredRegister(MODID);
    public static final BlockDeferredRegisterExtension BLOCKS = new BlockDeferredRegisterExtension(MODID);
    public static final DeferredRegister<MobEffect> EFFECT = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);
    public static final DeferredRegister<MobEffect> EL_EFFECT = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);
    public static final FluidDeferredRegister FLUIDS = new FluidDeferredRegister(MODID);
    public static final FluidDeferredRegister EL_FLUIDS = new FluidDeferredRegister(MODID);
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MODID);
    public static final SynchronizedDeferredRegister<CreativeModeTab> TABS = SynchronizedDeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);


    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ANTIMONY_LOOT =
            LOOT_MODIFIERS.register("extra_drop_loot", () -> ExtraDropLootModifier.CODEC);

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
        TABS.register(bus);
    }
}

