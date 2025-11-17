package org.dreamtinker.dreamtinker.common;

import net.minecraft.core.registries.Registries;
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

        private static TagKey<Item> dreamtinkerTag(String name) {
            return TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation(Dreamtinker.MODID, name));
        }

        private static TagKey<Item> modTag(String path) {
            return TagKey.create(Registries.ITEM, new ResourceLocation(path));
        }

        public static final TagKey<Item> raw_stibnite = forgeTag("raw_materials/stibnite");
        public static final TagKey<Item> weapon_slot_excluded = dreamtinkerTag("modifiable/excluded_weapon_slot");
        public static final TagKey<Item> HANDS = modTag("curios:hands");
        public static final TagKey<Item> CURIOS = dreamtinkerTag("my_curios");
        public static final TagKey<Item> larimarOre = forgeTag("ores/larimar");
        public static final TagKey<Item> amberOre = forgeTag("ores/amber");
        public static final TagKey<Item> scoleciteOre = forgeTag("ores/scolecite");
        public static final TagKey<Item> blackSapphireOre = forgeTag("ores/black_sapphire");
        public static final TagKey<Item> soulSteelBlock = dreamtinkerTag("storage_blocks/soul_steel");
        public static final TagKey<Item> raw_orichalcum = forgeTag("raw_materials/orichalcum");
        public static final TagKey<Item> OrichalcumNuggets = forgeTag("nuggets/orichalcum");
        public static final TagKey<Item> OrichalcumIngot = forgeTag("ingots/orichalcum");
        public static final TagKey<Item> OrichalcumOre = forgeTag("ores/orichalcum");
        public static final TagKey<Item> OrichalcumBlock = forgeTag("storage_blocks/orichalcum");
        public static final TagKey<Item> RawOrichalcumBlock = forgeTag("storage_blocks/raw_orichalcum");
        public static final TagKey<Item> coldIronNuggets = forgeTag("nuggets/cold_iron");
        public static final TagKey<Item> coldIronIngot = forgeTag("ingots/cold_iron");
        public static final TagKey<Item> raw_coldIron = forgeTag("raw_materials/cold_iron");
        public static final TagKey<Item> coldIronOre = forgeTag("ores/cold_iron");
        public static final TagKey<Item> coldIronBlock = forgeTag("storage_blocks/cold_iron");
        public static final TagKey<Item> RawColdIronBlock = forgeTag("storage_blocks/raw_cold_iron");

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
        public static final TagKey<Block> need_netheritte = forgeTag("need_tool/netheritte");
        public static final TagKey<Block> larimarOre = forgeTag("ores/larimar");
        public static final TagKey<Block> amberOre = forgeTag("ores/amber");
        public static final TagKey<Block> scoleciteOre = forgeTag("ores/scolecite");
        public static final TagKey<Block> soulSteelBlock = dreamtinkerTag("storage_blocks/soul_steel");
        public static final TagKey<Block> OrichalcumOre = forgeTag("ores/orichalcum");
        public static final TagKey<Block> OrichalcumBlock = forgeTag("storage_blocks/orichalcum");
        public static final TagKey<Block> RawOrichalcumBlock = forgeTag("storage_blocks/raw_orichalcum");
        public static final TagKey<Block> blackSapphireOre = forgeTag("ores/black_sapphire");
        public static final TagKey<Block> coldIronOre = forgeTag("ores/cold_iron");
        public static final TagKey<Block> coldIronBlock = forgeTag("storage_blocks/cold_iron");
        public static final TagKey<Block> RawColdIronBlock = forgeTag("storage_blocks/raw_cold_iron");
    }
}
