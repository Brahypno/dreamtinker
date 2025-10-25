package org.dreamtinker.dreamtinker.common.data.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.dreamtinker.dreamtinker.common.DreamtinkerCommon;
import org.dreamtinker.dreamtinker.common.DreamtinkerTagKeys;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.registration.object.BuildingBlockObject;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.registration.GeodeItemObject;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static net.minecraft.tags.BlockTags.*;

public class BlockTagProvider extends BlockTagsProvider {

    public BlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        this.tag(BlockTags.DRAGON_IMMUNE).add(DreamtinkerCommon.crying_obsidian_plane.get());
        tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_DIAMOND_TOOL, DreamtinkerCommon.crying_obsidian_plane);
        this.tag(DreamtinkerTagKeys.Blocks.drop_peach).add(Blocks.BIRCH_LEAVES);
        addBlocksTags(DreamtinkerCommon.narcissus.get(), forgeBlockTag("mineable/shears"), TinkerTags.Blocks.SLIMY_FUNGUS_CAN_GROW_THROUGH,
                      TinkerTags.Blocks.MINABLE_WITH_DAGGER, FLOWERS, ENDERMAN_HOLDABLE, SMALL_FLOWERS, SWORD_EFFICIENT, TinkerTags.Blocks.MINABLE_WITH_SCYTHE,
                      forgeBlockTag("mineable/sword"), TinkerTags.Blocks.MINABLE_WITH_SHEARS);
        tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_IRON_TOOL, DreamtinkerCommon.larimarOre);
        tagBlocks(DreamtinkerTagKeys.Blocks.larimarOre, DreamtinkerCommon.larimarOre);
        tagBlocks(DreamtinkerTagKeys.Blocks.amberOre, DreamtinkerCommon.amberOre);
    }

    private static TagKey<Block> mcBlockTag(String name) {
        return TagKey.create(ForgeRegistries.BLOCKS.getRegistryKey(), new ResourceLocation("minecraft", name));
    }

    private static TagKey<Block> forgeBlockTag(String name) {
        return TagKey.create(ForgeRegistries.BLOCKS.getRegistryKey(), new ResourceLocation("forge", name));
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
    @SafeVarargs
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

    @SafeVarargs
    private void addBlocksTags(Block block, TagKey<Block>... tags) {
        for (TagKey<Block> tag : tags) {
            this.tag(tag).add(block);
        }
    }
}
