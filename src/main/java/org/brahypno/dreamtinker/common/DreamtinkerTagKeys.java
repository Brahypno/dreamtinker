package org.brahypno.dreamtinker.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import org.brahypno.dreamtinker.Dreamtinker;
import slimeknights.mantle.Mantle;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialManager;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierManager;

public class DreamtinkerTagKeys {
    public static class Materials {
        public static final TagKey<IMaterial> THROW_STONE = DreamtinkerTag("thrown_stone");
        public static final TagKey<IMaterial> FIRE_FLAME = DreamtinkerTag("fire_flame");
        public static final TagKey<IMaterial> ROTATING_WHEEL = DreamtinkerTag("rotating_wheel");

        private static TagKey<IMaterial> DreamtinkerTag(String name) {
            return MaterialManager.getTag(new ResourceLocation(Dreamtinker.MODID, name));
        }
    }

    public static class Modifiers {
        private static TagKey<Modifier> DreamtinkerTag(String name) {
            return ModifierManager.getTag(new ResourceLocation(Dreamtinker.MODID, name));
        }

        public static final TagKey<Modifier> EL_CURSED_MODIFIERS = DreamtinkerTag("cursed_modifiers");
        public static final TagKey<Modifier> EL_CURSED_RELIEF = DreamtinkerTag("cursed_relief");
        public static final TagKey<Modifier> MALUM_EXPOSE_SOUL = DreamtinkerTag("malum_expose_soul");
        public static final TagKey<Modifier> ArmorWorkingWhenUnequipped = DreamtinkerTag("armor_working_when_unequipped");
    }

    public static class Fluids {
        private static TagKey<Fluid> forgeTag(String name) {
            return TagKey.create(ForgeRegistries.FLUIDS.getRegistryKey(), new ResourceLocation("forge", name));
        }

        public static final TagKey<Fluid> molten_echo_shard = forgeTag("molten_echo_shard");
        public static final TagKey<Fluid> molten_echo = forgeTag("molten_echo");
        public static final TagKey<Fluid> molten_crying_obsidian = forgeTag("molten_crying_obsidian");
        public static final TagKey<Fluid> molten_orichalcum = forgeTag("molten_orichalcum");
        public static final TagKey<Fluid> molten_arcane_gold = forgeTag("molten_arcane_gold");
    }

    public static class Items {
        public static TagKey<Item> forgeTag(String name) {
            return TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation("forge", name));
        }

        private static TagKey<Item> dreamtinkerTag(String name) {
            return TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation(Dreamtinker.MODID, name));
        }

        public static TagKey<Item> modTag(String path) {
            return TagKey.create(Registries.ITEM, new ResourceLocation(path));
        }

        public static final TagKey<Item> CursedDroplet = dreamtinkerTag("cursed_droplet");

        public static final TagKey<Item> IesniumIngot = forgeTag("ingots/iesnium");

        public static final TagKey<Item> dt_scythe = dreamtinkerTag("tools/scythe");
        public static final TagKey<Item> dt_hammer = dreamtinkerTag("tools/hammer");
        public static final TagKey<Item> raw_stibnite = forgeTag("raw_materials/stibnite");
        public static final TagKey<Item> weapon_slot_excluded = dreamtinkerTag("modifiable/excluded_weapon_slot");
        public static final TagKey<Item> HANDS = modTag("curios:hands");
        public static final TagKey<Item> CURIOS = dreamtinkerTag("my_curios");
        public static final TagKey<Item> larimarOre = forgeTag("ores/larimar");
        public static final TagKey<Item> amberOre = forgeTag("ores/amber");
        public static final TagKey<Item> scoleciteOre = forgeTag("ores/scolecite");
        public static final TagKey<Item> blackSapphireOre = forgeTag("ores/black_sapphire");
        public static final TagKey<Item> soulSteelBlock = dreamtinkerTag("storage_blocks/soul_steel");
        public static final TagKey<Item> sulfur_dust = forgeTag("dusts/sulfur");


        public static final TagKey<Item> utheriumNugget = forgeTag("nuggets/utherium");
        public static final TagKey<Item> utheriumIngot = forgeTag("ingots/utherium");
        public static final TagKey<Item> forgottenMetalIngot = forgeTag("ingots/forgotten_metal");
        public static final TagKey<Item> CloggrumIngot = forgeTag("ingots/cloggrum");
        public static final TagKey<Item> FroststeelIngot = forgeTag("ingots/froststeel");
        public static final TagKey<Item> RegaliumIngot = forgeTag("ingots/regalium");

        public static TagKey<Item> modTag(String modid, String path) {
            return TagKey.create(Registries.ITEM, new ResourceLocation(modid, path));
        }

        public static final TagKey<Item> arcaneGoldIngot = forgeTag("ingots/arcane_gold");

        public static final TagKey<Item> DarkMetalNuggets = forgeTag("nuggets/dark_metal");
        public static final TagKey<Item> DarkMetalIngot = forgeTag("ingots/dark_metal");
        public static final TagKey<Item> DarkMetalBlock = forgeTag("storage_blocks/dark_metal");

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

        public static final TagKey<Item> ShadowSilverNuggets = forgeTag("nuggets/shadow_silver");
        public static final TagKey<Item> ShadowSilverIngot = forgeTag("ingots/shadow_silver");
        public static final TagKey<Item> raw_ShadowSilver = forgeTag("raw_materials/shadow_silver");
        public static final TagKey<Item> ShadowSilverOre = forgeTag("ores/shadow_silver");
        public static final TagKey<Item> ShadowSilverBlock = forgeTag("storage_blocks/shadow_silver");
        public static final TagKey<Item> RawShadowSilverBlock = forgeTag("storage_blocks/raw_shadow_silver");

        public static final TagKey<Item> TransmutationGoldDusts = forgeTag("dusts/transmutation_gold");
        public static final TagKey<Item> TransmutationGoldNuggets = forgeTag("nuggets/transmutation_gold");
        public static final TagKey<Item> TransmutationGoldIngot = forgeTag("ingots/transmutation_gold");
        public static final TagKey<Item> raw_TransmutationGold = forgeTag("raw_materials/transmutation_gold");
        public static final TagKey<Item> TransmutationGoldOre = forgeTag("ores/transmutation_gold");
        public static final TagKey<Item> TransmutationGoldBlock = forgeTag("storage_blocks/transmutation_gold");
        public static final TagKey<Item> RawTransmutationGoldBlock = forgeTag("storage_blocks/raw_transmutation_gold");

        public static final TagKey<Item> ASHEN_BLOCKS = dreamtinkerTag("ashen_blocks");
        public static final TagKey<Item> ASHEN_TANKS = dreamtinkerTag("ashen_tanks");
        public static final TagKey<Item> TRANSMUTE_BLOCKS = dreamtinkerTag("transmute_blocks");
        public static final TagKey<Item> TRANSMUTE = dreamtinkerTag("transmute");
        public static final TagKey<Item> TRANSMUTE_HEATER = dreamtinkerTag("transmute_heater");
        public static final TagKey<Item> TRANSMUTE_ACCEL = dreamtinkerTag("transmute_accelerator");

    }

    public static class Blocks {
        public static TagKey<Block> forgeTag(String name) {
            return TagKey.create(ForgeRegistries.BLOCKS.getRegistryKey(), new ResourceLocation("forge", name));
        }

        private static TagKey<Block> dreamtinkerTag(String name) {
            return TagKey.create(ForgeRegistries.BLOCKS.getRegistryKey(), new ResourceLocation(Dreamtinker.MODID, name));
        }

        private static TagKey<Block> create(String p_203847_) {
            return TagKey.create(Registries.BLOCK, new ResourceLocation(p_203847_));
        }

        public static final TagKey<Block> drop_peach = dreamtinkerTag("drops/peach");
        public static final TagKey<Block> need_lupus = create("needs_lupus_tool");
        public static final TagKey<Block> need_netheritte = create("needs_netheritte_tool");
        public static final TagKey<Block> need_transmutation_gold = create("needs_transmutation_gold_tool");
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
        public static final TagKey<Block> ShadowSilverOre = forgeTag("ores/shadow_silver");
        public static final TagKey<Block> ShadowSilverBlock = forgeTag("storage_blocks/shadow_silver");
        public static final TagKey<Block> RawShadowSilverBlock = forgeTag("storage_blocks/raw_shadow_silver");
        public static final TagKey<Block> TransmutationGoldOre = forgeTag("ores/transmutation_gold");
        public static final TagKey<Block> TransmutationGoldBlock = forgeTag("storage_blocks/transmutation_gold");
        public static final TagKey<Block> RawTransmutationGoldBlock = forgeTag("storage_blocks/raw_transmutation_gold");

        public static final TagKey<Block> ASHEN_BLOCKS = dreamtinkerTag("ashen_blocks");
        public static final TagKey<Block> TRANSMUTE_BLOCKS = dreamtinkerTag("transmute_blocks");
        public static final TagKey<Block> ASHEN_TANKS = dreamtinkerTag("ashen_tanks");
        public static final TagKey<Block> TRANSMUTE_HEATER = dreamtinkerTag("transmute_heater");
        public static final TagKey<Block> TRANSMUTE_ACCEL = dreamtinkerTag("transmute_accelerator");
        public static final TagKey<Block> TRANSMUTE_ALLOY_SWITCH = dreamtinkerTag("transmute_alloyer_switch");
        public static final TagKey<Block> TRANSMUTE_MELTING_SWITCH = dreamtinkerTag("transmute_melting_switch");
        /**
         * Blocks that make up the transmute structure
         */
        public static final TagKey<Block> TRANSMUTE = dreamtinkerTag("transmute");
        /**
         * Blocks valid as a transmute tank, required for fuel
         */
        public static final TagKey<Block> TRANSMUTE_TANKS = dreamtinkerTag("transmute/tanks");
        /**
         * Blocks valid as a transmute floor
         */
        public static final TagKey<Block> TRANSMUTE_FLOOR = dreamtinkerTag("transmute/floor");
        /**
         * Blocks valid in the transmute wall
         */
        public static final TagKey<Block> TRANSMUTE_WALL = dreamtinkerTag("transmute/wall");
        /**
         * Blocks valid in the transmute wall
         */
        public static final TagKey<Block> TRANSMUTE_CEILING = dreamtinkerTag("transmute/ceiling");
    }

    public static class EntityTypes {
        public static final TagKey<EntityType<?>> ENDER_ENTITY = common("ender_entity");
        public static final TagKey<EntityType<?>> ROTSPAWN = common("undergarden", "rotspawn");
        public static final TagKey<EntityType<?>> CHAOS_ELITE = common("bic_dark_metal_elite");
        public static final TagKey<EntityType<?>> CHAOS_BOSS = common("bic_dark_metal_normal");
        public static final TagKey<EntityType<?>> CHAOS_MINOR = common("bic_dark_metal_minor");
        public static final TagKey<EntityType<?>> CHAOS_HEAD = common("bic_dark_metal_head");

        private static TagKey<EntityType<?>> local(String name) {
            return TagKey.create(Registries.ENTITY_TYPE, Dreamtinker.getLocation(name));
        }

        private static TagKey<EntityType<?>> common(String name) {
            return TagKey.create(Registries.ENTITY_TYPE, Mantle.commonResource(name));
        }

        public static TagKey<EntityType<?>> common(String modid, String name) {
            return TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(modid, name));
        }
    }

    public static class MobEffects {
        public static final TagKey<MobEffect> EDICTS = DreamtinkerTag("edicts");

        private static TagKey<MobEffect> DreamtinkerTag(String name) {
            return TagKey.create(ForgeRegistries.MOB_EFFECTS.getRegistryKey(), Dreamtinker.getLocation(name));
        }
    }
}
