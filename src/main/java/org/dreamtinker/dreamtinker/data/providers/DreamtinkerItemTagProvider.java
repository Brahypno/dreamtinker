package org.dreamtinker.dreamtinker.data.providers;

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
import org.dreamtinker.dreamtinker.data.DreamtinkerTagkeys;
import org.dreamtinker.dreamtinker.register.DreamtinkerItems;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.tconstruct.common.TinkerTags;

import java.util.concurrent.CompletableFuture;

import static slimeknights.tconstruct.common.TinkerTags.Items.*;

public class DreamtinkerItemTagProvider extends ItemTagsProvider {

    public DreamtinkerItemTagProvider(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_, CompletableFuture<TagLookup<Block>> p_275322_, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_275343_, p_275729_, p_275322_, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider Provider) {
        this.tag(TinkerTags.Items.TOOL_PARTS).add(DreamtinkerItems.explode_core.get());
        addToolTags(DreamtinkerItems.masu, MULTIPART_TOOL, DURABILITY, HARVEST, MELEE_PRIMARY, INTERACTABLE_RIGHT, SWORD, BROAD_TOOLS, BONUS_SLOTS,
                    ItemTags.SWORDS, AOE);
        addToolTags(DreamtinkerItems.tntarrow, MULTIPART_TOOL, DURABILITY, HARVEST, MELEE, SMALL_TOOLS, BONUS_SLOTS);
        this.tag(TinkerTags.Items.CASTS).add(DreamtinkerItems.persona_cast.get());
        this.tag(TinkerTags.Items.PATTERNS).add(DreamtinkerItems.persona_cast.get());
        this.tag(TinkerTags.Items.SINGLE_USE_CASTS).add(DreamtinkerItems.persona_cast.get());
        this.tag(Tags.Items.INGOTS)
            .add(DreamtinkerItems.metallivorous_stibium_lupus.get(), DreamtinkerItems.regulus.get(), DreamtinkerItems.soul_etherium.get());
        this.tag(Tags.Items.GEMS).add(DreamtinkerItems.valentinite.get(), DreamtinkerItems.nigrescence_antimony.get());
        this.tag(DreamtinkerTagkeys.Items.raw_stibnite).add(DreamtinkerItems.raw_stibnite.get());
        addArmorTags(DreamtinkerItems.underPlate, MULTIPART_TOOL, DURABILITY, TinkerTags.Items.BONUS_SLOTS,
                     TinkerTags.Items.TRIM);
        tag(BASIC_ARMOR);
        IntrinsicTagAppender<Item> bookArmor = tag(PUNY_ARMOR);
        for (ArmorItem.Type slotType : ArmorItem.Type.values()) {
            bookArmor.add(DreamtinkerItems.underPlate.get(slotType));
        }
        tag(MIGHTY_ARMOR);
        tag(FANTASTIC_ARMOR);
        bookArmor = tag(GADGETRY_ARMOR);
        tag(BOOK_ARMOR).addTags(BASIC_ARMOR, PUNY_ARMOR, MIGHTY_ARMOR, FANTASTIC_ARMOR, GADGETRY_ARMOR);
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
    private void addToolTags(ItemLike tool, TagKey<Item>... tags) {
        Item item = tool.asItem();
        for (TagKey<Item> tag : tags) {
            this.tag(tag).add(item);
        }
    }

}
