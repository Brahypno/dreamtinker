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
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerTagKeys;
import org.dreamtinker.dreamtinker.tools.DreamtinkerToolParts;
import org.dreamtinker.dreamtinker.tools.DreamtinkerTools;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.tools.TinkerTools;

import java.util.concurrent.CompletableFuture;

import static org.dreamtinker.dreamtinker.common.DreamtinkerCommon.*;
import static slimeknights.tconstruct.common.TinkerTags.Items.*;

public class ItemTagProvider extends ItemTagsProvider {

    public ItemTagProvider(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_, CompletableFuture<TagLookup<Block>> p_275322_, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_275343_, p_275729_, p_275322_, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider Provider) {
        //tools
        this.tag(DreamtinkerTagKeys.Items.weapon_slot_excluded).add(DreamtinkerTools.silence_glove.get(), DreamtinkerTools.tntarrow.get());
        this.tag(TinkerTags.Items.TOOL_PARTS).add(DreamtinkerToolParts.explode_core.get());
        addItemsTags(DreamtinkerTools.mashou, MULTIPART_TOOL, DURABILITY, HARVEST, MELEE_PRIMARY, INTERACTABLE_RIGHT, SWORD, BROAD_TOOLS, BONUS_SLOTS,
                     ItemTags.SWORDS, AOE);
        addItemsTags(DreamtinkerTools.narcissus_wing, MULTIPART_TOOL, DURABILITY, HARVEST_PRIMARY, MELEE_PRIMARY, INTERACTABLE_RIGHT, SCYTHES, BROAD_TOOLS,
                     BONUS_SLOTS, ItemTags.SWORDS, AOE, RANGED, ItemTags.PICKAXES);
        addItemsTags(DreamtinkerTools.tntarrow, MULTIPART_TOOL, DURABILITY, MELEE_WEAPON, SMALL_TOOLS, BONUS_SLOTS);

        this.tag(ItemTagRegistry.SCYTHE).add(TinkerTools.scythe.asItem(), TinkerTools.kama.asItem(), DreamtinkerTools.narcissus_wing.asItem());
        addItemsTags(DreamtinkerTools.silence_glove, DURABILITY, MELEE_PRIMARY, BONUS_SLOTS, ANCIENT_TOOLS, STAFFS, SHIELDS,
                     DreamtinkerTagKeys.Items.HANDS, DreamtinkerTagKeys.Items.CURIOS);
        //parts
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
            .add(metallivorous_stibium_lupus.get(), regulus.get(), soul_etherium.get(), soul_steel.get());
        this.tag(Tags.Items.GEMS)
            .add(valentinite.get(), nigrescence_antimony.get(), echo_alloy.get(), larimar.get(), amber.get(), desire_gem.get(), despair_gem.get());
        this.tag(DreamtinkerTagKeys.Items.raw_stibnite).add(raw_stibnite.get());
        this.tag(Tags.Items.RAW_MATERIALS).add(raw_stibnite.get());
        this.tag(ItemTags.FOX_FOOD).add(white_peach.get());
        this.tag(ItemTags.ARROWS).add(DreamtinkerTools.tntarrow.get());
        addItemsTags(narcissus.asItem(), ItemTags.SMALL_FLOWERS, ItemTags.FLOWERS);
        this.tag(ItemTagRegistry.HIDDEN_UNTIL_BLACK_CRYSTAL).addOptional(malignant_gluttony.getId());
        this.tag(Dreamtinker.forgeItemTag("gems/larimar"))
            .add(larimar.get());
        this.tag(Dreamtinker.forgeItemTag("ores/larimar"))
            .add(larimarOre.asItem());
        this.tag(Dreamtinker.forgeItemTag("gems/amber"))
            .add(amber.get());
        this.tag(Dreamtinker.forgeItemTag("ores/amber"))
            .add(amberOre.asItem());

        this.copy(Tags.Blocks.ORE_RATES_SINGULAR, Tags.Items.ORE_RATES_SINGULAR);
        //this.copy(Tags.Blocks.ORES_IN_GROUND_NETHERRACK, Tags.Items.ORES_IN_GROUND_NETHERRACK);
        this.copy(Tags.Blocks.ORE_RATES_SINGULAR, Tags.Items.ORE_RATES_SINGULAR);
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

}
