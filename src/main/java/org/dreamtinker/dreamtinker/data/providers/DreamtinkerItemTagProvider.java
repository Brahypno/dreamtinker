package org.dreamtinker.dreamtinker.data.providers;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.dreamtinker.dreamtinker.data.DreamtinkerTagkeys;
import org.dreamtinker.dreamtinker.register.DreamtinkerItems;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.common.TinkerTags;

import java.util.concurrent.CompletableFuture;

public class DreamtinkerItemTagProvider extends ItemTagsProvider {

    public DreamtinkerItemTagProvider(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_, CompletableFuture<TagLookup<Block>> p_275322_, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_275343_, p_275729_, p_275322_, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider Provider) {
        this.tag(TinkerTags.Items.TOOL_PARTS).add(DreamtinkerItems.explode_core.get());
        this.tag(Tags.Items.INGOTS)
            .add(DreamtinkerItems.metallivorous_stibium_lupus.get(), DreamtinkerItems.regulus.get(), DreamtinkerItems.soul_etherium.get());
        this.tag(Tags.Items.GEMS).add(DreamtinkerItems.valentinite.get(), DreamtinkerItems.nigrescence_antimony.get());
        this.tag(DreamtinkerTagkeys.Items.raw_stibnite).add(DreamtinkerItems.raw_stibnite.get());
    }

}
