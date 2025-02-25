package org.dreamtinker.dreamtinker.register;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static org.dreamtinker.dreamtinker.Dreamtinker.MODID;


public class DreamtinkerItem {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<Item> cruse_ingot = ITEMS.register("cruse_ingot", () -> new Item(new Item.Properties().tab(DreamtinkerTab.MATERIALS)));

}

