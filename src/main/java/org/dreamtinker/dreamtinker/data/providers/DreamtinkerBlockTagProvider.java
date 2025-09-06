package org.dreamtinker.dreamtinker.data.providers;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.dreamtinker.dreamtinker.register.DreamtinkerBlocks;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.registration.object.BuildingBlockObject;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.tconstruct.common.registration.GeodeItemObject;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static net.minecraft.tags.BlockTags.MINEABLE_WITH_PICKAXE;
import static net.minecraft.tags.BlockTags.NEEDS_DIAMOND_TOOL;

public class DreamtinkerBlockTagProvider extends BlockTagsProvider {

    public DreamtinkerBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(BlockTags.DRAGON_IMMUNE).add(DreamtinkerBlocks.crying_obsidian_plane.get());
        tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_DIAMOND_TOOL, DreamtinkerBlocks.crying_obsidian_plane);
    }

    @SafeVarargs
    private void tagBlocks(TagKey<Block> tag, Supplier<? extends Block>... blocks) {
        IntrinsicTagAppender<Block> appender = this.tag(tag);
        for (Supplier<? extends Block> block : blocks) {
            appender.add(block.get());
        }
    }

    /**
     * Applies a tag to a set of suppliers
     */
    private void tagBlocks(TagKey<Block> tag, GeodeItemObject... blocks) {
        IntrinsicTagAppender<Block> appender = this.tag(tag);
        for (GeodeItemObject geode : blocks) {
            appender.add(geode.getBlock());
            appender.add(geode.getBudding());
            for (GeodeItemObject.BudSize size : GeodeItemObject.BudSize.values()) {
                appender.add(geode.getBud(size));
            }
        }
    }

    /**
     * Applies a set of tags to a block
     */
    @SuppressWarnings("SameParameterValue")
    private void tagBlocks(TagKey<Block> tag1, TagKey<Block> tag2, Supplier<? extends Block>... blocks) {
        tagBlocks(tag1, blocks);
        tagBlocks(tag2, blocks);
    }

    /**
     * Applies a tag to a set of blocks
     */
    @SafeVarargs
    private void tagBlocks(TagKey<Block> tag, EnumObject<?, ? extends Block>... blocks) {
        IntrinsicTagAppender<Block> appender = this.tag(tag);
        for (EnumObject<?, ? extends Block> block : blocks) {
            block.forEach(b -> appender.add(b));
        }
    }

    /**
     * Applies a tag to a set of blocks
     */
    @SafeVarargs
    private void tagBlocks(TagKey<Block> tag1, TagKey<Block> tag2, EnumObject<?, ? extends Block>... blocks) {
        tagBlocks(tag1, blocks);
        tagBlocks(tag2, blocks);
    }

    /**
     * Applies a set of tags to a block
     */
    private void tagBlocks(TagKey<Block> tag, BuildingBlockObject... blocks) {
        IntrinsicTagAppender<Block> appender = this.tag(tag);
        for (BuildingBlockObject block : blocks) {
            block.values().forEach(appender::add);
        }
    }

    /**
     * Applies a set of tags to a block
     */
    @SuppressWarnings("SameParameterValue")
    private void tagBlocks(TagKey<Block> tag1, TagKey<Block> tag2, BuildingBlockObject... blocks) {
        tagBlocks(tag1, blocks);
        tagBlocks(tag2, blocks);
    }
}
