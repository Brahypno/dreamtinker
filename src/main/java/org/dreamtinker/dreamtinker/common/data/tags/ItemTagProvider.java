package org.dreamtinker.dreamtinker.common.data.tags;

import com.sammy.malum.registry.common.item.ItemTagRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.dreamtinker.dreamtinker.common.DreamtinkerTagKeys.Blocks;
import org.dreamtinker.dreamtinker.common.DreamtinkerTagKeys.Items;
import org.dreamtinker.dreamtinker.tools.DreamtinkerToolParts;
import org.dreamtinker.dreamtinker.tools.DreamtinkerTools;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.tools.TinkerTools;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static org.dreamtinker.dreamtinker.common.DreamtinkerCommon.*;
import static slimeknights.tconstruct.common.TinkerTags.Items.*;

public class ItemTagProvider extends ItemTagsProvider {

    public ItemTagProvider(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_, CompletableFuture<TagLookup<Block>> p_275322_, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_275343_, p_275729_, p_275322_, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider Provider) {
        //tools
        this.tag(Items.weapon_slot_excluded).add(DreamtinkerTools.silence_glove.get(), DreamtinkerTools.tntarrow.get());
        this.tag(TinkerTags.Items.TOOL_PARTS)
            .add(DreamtinkerToolParts.explode_core.get(), DreamtinkerToolParts.memoryOrthant.get(), DreamtinkerToolParts.soulOrthant.get(),
                 DreamtinkerToolParts.wishOrthant.get(), DreamtinkerToolParts.personaOrthant.get(), DreamtinkerToolParts.reasonEmanation.get(),
                 DreamtinkerToolParts.chainSawTeeth.get(), DreamtinkerToolParts.chainSawCore.get(),
                 DreamtinkerToolParts.NovaRostrum.get(), DreamtinkerToolParts.NovaMisc.get(), DreamtinkerToolParts.NovaWrapper.get(),
                 DreamtinkerToolParts.NovaCover.get());

        addItemsTags(DreamtinkerTools.mashou, MULTIPART_TOOL, DURABILITY, HARVEST, MELEE_PRIMARY, INTERACTABLE_RIGHT, SWORD, BROAD_TOOLS, BONUS_SLOTS,
                     ItemTags.SWORDS, AOE);
        addItemsTags(DreamtinkerTools.narcissus_wing, MULTIPART_TOOL, DURABILITY, HARVEST_PRIMARY, MELEE_PRIMARY, INTERACTABLE_RIGHT, SCYTHES, BROAD_TOOLS,
                     BONUS_SLOTS, AOE, RANGED, ItemTags.PICKAXES);
        addItemsTags(DreamtinkerTools.tntarrow, MULTIPART_TOOL, DURABILITY, MELEE_WEAPON, SMALL_TOOLS, BONUS_SLOTS);
        addItemsTags(DreamtinkerTools.chain_saw_blade, MULTIPART_TOOL, DURABILITY, HARVEST_PRIMARY, MELEE_PRIMARY, INTERACTABLE_RIGHT, AOE, BROAD_TOOLS,
                     BONUS_SLOTS, ItemTags.AXES);
        addItemsOptionalTags(DreamtinkerTools.per_aspera_scriptum, MULTIPART_TOOL, MELEE_WEAPON, BROAD_RANGED, BONUS_SLOTS);

        this.tag(Items.dt_scythe).add(TinkerTools.scythe.asItem(), TinkerTools.kama.asItem(), DreamtinkerTools.narcissus_wing.asItem());
        this.tag(ItemTagRegistry.SCYTHE).addTags(Items.dt_scythe);
        this.tag(Items.dt_hammer).add(TinkerTools.veinHammer.asItem(), TinkerTools.sledgeHammer.asItem());
        this.tag(ItemTagRegistry.HIDDEN_UNTIL_BLACK_CRYSTAL)
            .addOptional(malignant_gluttony.getId());
        addItemsTags(DreamtinkerTools.silence_glove, DURABILITY, MELEE, BONUS_SLOTS, ANCIENT_TOOLS, STAFFS, SHIELDS,
                     Items.HANDS, Items.CURIOS);
        //parts
        IntrinsicTagAppender<Item> goldCasts = this.tag(TinkerTags.Items.GOLD_CASTS);
        IntrinsicTagAppender<Item> sandCasts = this.tag(TinkerTags.Items.SAND_CASTS);
        IntrinsicTagAppender<Item> redSandCasts = this.tag(TinkerTags.Items.RED_SAND_CASTS);
        IntrinsicTagAppender<Item> singleUseCasts = this.tag(TinkerTags.Items.SINGLE_USE_CASTS);
        IntrinsicTagAppender<Item> multiUseCasts = this.tag(TinkerTags.Items.MULTI_USE_CASTS);
        Consumer<CastItemObject> addCast = cast -> {
            // tag based on material
            goldCasts.add(cast.get());
            sandCasts.add(cast.getSand());
            redSandCasts.add(cast.getRedSand());
            // tag based on usage
            singleUseCasts.addTag(cast.getSingleUseTag());
            this.tag(cast.getSingleUseTag()).add(cast.getSand(), cast.getRedSand());
            multiUseCasts.addTag(cast.getMultiUseTag());
            this.tag(cast.getMultiUseTag()).add(cast.get());
        };
        addCast.accept(DreamtinkerToolParts.chainSawTeethCast);
        addCast.accept(DreamtinkerToolParts.chainSawCoreCast);
        addCast.accept(DreamtinkerToolParts.NovaWrapperCast);
        addCast.accept(DreamtinkerToolParts.NovaCoverCast);
        addCast.accept(DreamtinkerToolParts.NovaRostrumCast);
        addCast.accept(DreamtinkerToolParts.NovaMiscCast);
        this.tag(TinkerTags.Items.CASTS)
            .add(memory_cast.get(), wish_cast.get(), soul_cast.get(),
                 persona_cast.get(),
                 reason_cast.get(), DreamtinkerToolParts.explode_core.get());
        this.tag(TinkerTags.Items.PATTERNS)
            .add(memory_cast.get(), wish_cast.get(), soul_cast.get(),
                 persona_cast.get(),
                 reason_cast.get(), DreamtinkerToolParts.explode_core.get());
        this.tag(TinkerTags.Items.SINGLE_USE_CASTS)
            .add(memory_cast.get(), wish_cast.get(), soul_cast.get(),
                 persona_cast.get(),
                 reason_cast.get(), DreamtinkerToolParts.explode_core.get());
        //armor
        addArmorTags(DreamtinkerTools.underPlate, MULTIPART_TOOL, DURABILITY, TinkerTags.Items.BONUS_SLOTS,
                     TinkerTags.Items.TRIM);
        tag(BASIC_ARMOR);
        IntrinsicTagAppender<Item> bookArmor = tag(PUNY_ARMOR);
        for (ArmorItem.Type slotType : ArmorItem.Type.values()) {
            bookArmor.add(DreamtinkerTools.underPlate.get(slotType));
        }
        tag(MIGHTY_ARMOR);
        tag(FANTASTIC_ARMOR);
        tag(BOOK_ARMOR).addTags(BASIC_ARMOR, PUNY_ARMOR, MIGHTY_ARMOR, FANTASTIC_ARMOR, GADGETRY_ARMOR);
        //items


        this.tag(Tags.Items.INGOTS)
            .add(metallivorous_stibium_lupus.get(), regulus.get(), soul_steel.get(), orichalcum.get(), cold_iron_ingot.get(), shadow_silver_ingot.get(),
                 transmutation_gold_ingot.get())
            .addOptional(soul_etherium.getId())
            .addOptional(malignant_gluttony.getId());
        this.tag(Items.OrichalcumIngot).add(orichalcum.get());
        this.tag(Items.coldIronIngot).add(cold_iron_ingot.get());
        this.tag(Items.ShadowSilverIngot).add(shadow_silver_ingot.get());
        this.tag(Items.TransmutationGoldIngot).add(transmutation_gold_ingot.get());

        this.tag(Tags.Items.GEMS)
            .add(valentinite.get(), nigrescence_antimony.get(), echo_alloy.get(), larimar.get(), amber.get(), desire_gem.get(), despair_gem.get(),
                 rainbow_honey_crystal.get(), black_sapphire.get(), scolecite.get());

        this.tag(Items.raw_stibnite).add(raw_stibnite.get());
        this.tag(Items.raw_orichalcum).add(raw_orichalcum.get());
        this.tag(Items.raw_coldIron).add(raw_cold_iron.get());
        this.tag(Items.raw_ShadowSilver).add(raw_shadow_silver.get());
        this.tag(Items.raw_TransmutationGold).add(raw_transmutation_gold.get());
        this.tag(Tags.Items.RAW_MATERIALS)
            .addTags(Items.raw_stibnite, Items.raw_orichalcum, Items.raw_coldIron, Items.raw_ShadowSilver, Items.raw_TransmutationGold);

        this.tag(ItemTags.FOX_FOOD).add(white_peach.get());
        this.tag(ItemTags.ARROWS).add(DreamtinkerTools.tntarrow.get());
        addItemsTags(narcissus.asItem(), ItemTags.SMALL_FLOWERS, ItemTags.FLOWERS);

        this.tag(ItemTags.BEACON_PAYMENT_ITEMS)
            .add(soul_steel.get(), orichalcum.get(), cold_iron_ingot.get(), shadow_silver_ingot.get());

        this.tag(Items.OrichalcumNuggets).add(orichalcum_nugget.get());
        this.tag(Items.coldIronNuggets).add(cold_iron_nugget.get());
        this.tag(Items.ShadowSilverNuggets).add(shadow_silver_nugget.get());
        this.tag(Items.TransmutationGoldNuggets).add(transmutation_gold_nugget.get());
        this.tag(Tags.Items.NUGGETS)
            .addTags(Items.OrichalcumNuggets, Items.coldIronNuggets, Items.TransmutationGoldNuggets);

        this.tag(Items.TransmutationGoldDusts).add(transmutation_gold_dust.get());
        this.tag(Tags.Items.DUSTS)
            .addTags(Items.TransmutationGoldDusts);

        this.copy(Tags.Blocks.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS);
        this.copy(Tags.Blocks.ORE_RATES_SINGULAR, Tags.Items.ORE_RATES_SINGULAR);
        //this.copy(Tags.Blocks.ORES_IN_GROUND_NETHERRACK, Tags.Items.ORES_IN_GROUND_NETHERRACK);
        this.copy(Tags.Blocks.ORE_RATES_DENSE, Tags.Items.ORE_RATES_DENSE);
        this.copy(Blocks.larimarOre, Items.larimarOre);
        this.copy(Blocks.amberOre, Items.amberOre);
        this.copy(Blocks.scoleciteOre, Items.scoleciteOre);
        this.copy(Blocks.soulSteelBlock, Items.soulSteelBlock);
        this.copy(Blocks.blackSapphireOre, Items.blackSapphireOre);
        this.copy(Blocks.OrichalcumBlock, Items.OrichalcumBlock);
        this.copy(Blocks.OrichalcumOre, Items.OrichalcumOre);
        this.copy(Blocks.RawOrichalcumBlock, Items.RawOrichalcumBlock);
        this.copy(Blocks.coldIronBlock, Items.coldIronBlock);
        this.copy(Blocks.coldIronOre, Items.coldIronOre);
        this.copy(Blocks.RawColdIronBlock, Items.RawColdIronBlock);
        this.copy(Blocks.ShadowSilverBlock, Items.ShadowSilverBlock);
        this.copy(Blocks.ShadowSilverOre, Items.ShadowSilverOre);
        this.copy(Blocks.RawShadowSilverBlock, Items.RawShadowSilverBlock);
        this.copy(Blocks.TransmutationGoldBlock, Items.TransmutationGoldBlock);
        this.copy(Blocks.TransmutationGoldOre, Items.TransmutationGoldOre);
        this.copy(Blocks.RawTransmutationGoldBlock, Items.RawTransmutationGoldBlock);
        this.copy(Tags.Blocks.ORES, Tags.Items.ORES);
    }

    private TagKey<Item> getArmorTag(ArmorItem.Type slotType) {
        return switch (slotType) {
            case BOOTS -> TinkerTags.Items.BOOTS;
            case LEGGINGS -> TinkerTags.Items.LEGGINGS;
            case CHESTPLATE -> TinkerTags.Items.CHESTPLATES;
            case HELMET -> TinkerTags.Items.HELMETS;
        };
    }

    private TagKey<Item> getForgeArmorTag(ArmorItem.Type slotType) {
        return switch (slotType) {
            case BOOTS -> Tags.Items.ARMORS_BOOTS;
            case LEGGINGS -> Tags.Items.ARMORS_LEGGINGS;
            case CHESTPLATE -> Tags.Items.ARMORS_CHESTPLATES;
            case HELMET -> Tags.Items.ARMORS_HELMETS;
        };
    }

    @SafeVarargs
    private void addArmorTags(EnumObject<ArmorItem.Type, ? extends Item> armor, TagKey<Item>... tags) {
        armor.forEach((type, item) -> {
            for (TagKey<Item> tag : tags) {
                this.tag(tag).add(item);
            }
            this.tag(getArmorTag(type)).add(item);
            this.tag(getForgeArmorTag(type)).add(item);
        });
    }

    @SafeVarargs
    private void addItemsTags(ItemLike tool, TagKey<Item>... tags) {
        Item item = tool.asItem();
        for (TagKey<Item> tag : tags) {
            this.tag(tag).add(item);
        }
    }

    @SafeVarargs
    private void addItemsOptionalTags(ItemLike tool, TagKey<Item>... tags) {
        Item item = tool.asItem();
        for (TagKey<Item> tag : tags) {
            this.tag(tag).addOptional(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)));
        }
    }

}
