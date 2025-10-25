package org.dreamtinker.dreamtinker.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import org.dreamtinker.dreamtinker.Dreamtinker;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierManager;

public class DreamtinkerTagKeys {

    public static class Modifiers {
        private static TagKey<Modifier> DreamtinkerTag(String name) {
            return ModifierManager.getTag(new ResourceLocation(Dreamtinker.MODID, name));
        }

        public static final TagKey<Modifier> EL_CURSED_MODIFIERS = DreamtinkerTag("cursed_modifiers");
        public static final TagKey<Modifier> EL_CURSED_RELIEF = DreamtinkerTag("cursed_relief");
        public static final TagKey<Modifier> MALUM_EXPOSE_SOUL = DreamtinkerTag("malum_expose_soul");
    }

    public static class Fluids {
        private static TagKey<Fluid> forgeTag(String name) {
            return TagKey.create(ForgeRegistries.FLUIDS.getRegistryKey(), new ResourceLocation("forge", name));
        }

        private static TagKey<Fluid> dtTag(String name) {
            return TagKey.create(ForgeRegistries.FLUIDS.getRegistryKey(), new ResourceLocation(Dreamtinker.MODID, name));
        }

        public static final TagKey<Fluid> molten_echo_shard = forgeTag("molten_echo_shard");
        public static final TagKey<Fluid> molten_crying_obsidian = forgeTag("molten_crying_obsidian");
        public static final TagKey<Fluid> molten_soul_stained_steel = dtTag("molten_soul_stained_steel");
        public static final TagKey<Fluid> molten_malignant_pewter = dtTag("molten_malignant_pewter");
        public static final TagKey<Fluid> molten_malignant_gluttony = dtTag("molten_malignant_gluttony");

        public static final TagKey<Fluid> narcissus_wing_used = dtTag("narcissus_wing_used");
    }

    public static class Items {
        private static TagKey<Item> forgeTag(String name) {
            return TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation("forge", name));
        }

        public static final TagKey<Item> raw_stibnite = forgeTag("raw_materials/stibnite");
    }

    public static class Blocks {
        private static TagKey<Block> forgeTag(String name) {
            return TagKey.create(ForgeRegistries.BLOCKS.getRegistryKey(), new ResourceLocation("forge", name));
        }

        private static TagKey<Block> dreamtinkerTag(String name) {
            return TagKey.create(ForgeRegistries.BLOCKS.getRegistryKey(), new ResourceLocation(Dreamtinker.MODID, name));
        }

        public static final TagKey<Block> drop_peach = dreamtinkerTag("drops/peach");
        public static final TagKey<Block> need_lupus = forgeTag("need_tool/lupus");
        public static final TagKey<Block> larimarOre = forgeTag("ores/larimar");
        public static final TagKey<Block> amberOre = forgeTag("ores/amber");
    }
}
