package org.dreamtinker.dreamtinker.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import org.dreamtinker.dreamtinker.Dreamtinker;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierManager;

public class DreamtinkerTagkeys {

    public static class Modifiers {
        private static TagKey<Modifier> DreamtinkerTag(String name) {
            return ModifierManager.getTag(new ResourceLocation(Dreamtinker.MODID, name));
        }

        public static final TagKey<Modifier> CURSED_MODIFIERS = DreamtinkerTag("cursed_modifiers");
    }

    public static class Fluids {
        private static TagKey<Fluid> forgeTag(String name) {
            return TagKey.create(ForgeRegistries.FLUIDS.getRegistryKey(), new ResourceLocation("forge", name));
        }

        public static final TagKey<Fluid> molten_echo_shard = forgeTag("molten_echo_shard");
        public static final TagKey<Fluid> molten_nigrescence_antimony = forgeTag("molten_nigrescence_antimony");
        public static final TagKey<Fluid> molten_albedo_stibium = forgeTag("molten_albedo_stibium");
        public static final TagKey<Fluid> molten_lupi_antimony = forgeTag("molten_lupi_antimony");
        public static final TagKey<Fluid> molten_ascending_antimony = forgeTag("molten_ascending_antimony");
        public static final TagKey<Fluid> liquid_smoky_antimony = forgeTag("liquid_smoky_antimony");
        public static final TagKey<Fluid> molten_crying_obsidian = forgeTag("molten_crying_obsidian");
    }

    public static class Items {
        private static TagKey<Item> forgeTag(String name) {
            return TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation("forge", name));
        }

        public static final TagKey<Item> raw_stibnite = forgeTag("raw_materials/stibnite");
    }
}
