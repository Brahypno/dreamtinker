package org.dreamtinker.dreamtinker.register;

import com.mojang.serialization.Codec;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.LootModifier.AntimonyLootModifier;

public class DreamtinkerLoots {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOTMODIFIERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Dreamtinker.MODID);


    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ANTIMONY_LOOT =
            LOOTMODIFIERS.register("antimony_loot", () -> AntimonyLootModifier.CODEC);
}
