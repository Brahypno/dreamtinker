package org.dreamtinker.dreamtinker.common.data.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.dreamtinker.dreamtinker.Dreamtinker;
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
import static net.minecraftforge.common.Tags.Blocks.STORAGE_BLOCKS;
import static org.dreamtinker.dreamtinker.common.DreamtinkerCommon.*;

public class BlockTagProvider extends BlockTagsProvider {

    public BlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        this.tag(BlockTags.DRAGON_IMMUNE).add(crying_obsidian_plane.get());
        tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_DIAMOND_TOOL, crying_obsidian_plane, blackSapphireOre, DeepSlateBlackSapphireOre, OrichalcumOre);
        this.tag(DreamtinkerTagKeys.Blocks.drop_peach).add(Blocks.BIRCH_LEAVES);
        addBlocksTags(narcissus.get(), Dreamtinker.forgeBlockTag("mineable/shears"), TinkerTags.Blocks.SLIMY_FUNGUS_CAN_GROW_THROUGH,
                      TinkerTags.Blocks.MINABLE_WITH_DAGGER, FLOWERS, ENDERMAN_HOLDABLE, SMALL_FLOWERS, SWORD_EFFICIENT, TinkerTags.Blocks.MINABLE_WITH_SCYTHE,
                      Dreamtinker.forgeBlockTag("mineable/sword"), TinkerTags.Blocks.MINABLE_WITH_SHEARS);
        tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_IRON_TOOL, larimarOre, amberOre, scoleciteOre, soulSteelBlock, coldIronOre, DeepslateColdIronOre,
                  DeepslateOrichalcumOre, OrichalcumBlock, RawOrichalcumBlock);
        tagBlocks(BEACON_BASE_BLOCKS, soulSteelBlock, OrichalcumBlock, ColdIronBlock);

        tagBlocks(DreamtinkerTagKeys.Blocks.larimarOre, larimarOre);
        tagBlocks(DreamtinkerTagKeys.Blocks.amberOre, amberOre);
        tagBlocks(DreamtinkerTagKeys.Blocks.scoleciteOre, scoleciteOre);
        tagBlocks(DreamtinkerTagKeys.Blocks.soulSteelBlock, soulSteelBlock);
        tagBlocks(DreamtinkerTagKeys.Blocks.blackSapphireOre, blackSapphireOre, DeepSlateBlackSapphireOre);

        tagBlocks(DreamtinkerTagKeys.Blocks.OrichalcumBlock, OrichalcumBlock);
        tagBlocks(DreamtinkerTagKeys.Blocks.RawOrichalcumBlock, RawOrichalcumBlock);
        tagBlocks(DreamtinkerTagKeys.Blocks.OrichalcumOre, OrichalcumOre, DeepslateOrichalcumOre);

        tagBlocks(DreamtinkerTagKeys.Blocks.coldIronBlock, ColdIronBlock);
        tagBlocks(DreamtinkerTagKeys.Blocks.RawColdIronBlock, RawColdIronBlock);
        tagBlocks(DreamtinkerTagKeys.Blocks.coldIronOre, coldIronOre, DeepslateColdIronOre);
        this.tag(STORAGE_BLOCKS)
            .addTags(DreamtinkerTagKeys.Blocks.OrichalcumBlock, DreamtinkerTagKeys.Blocks.RawOrichalcumBlock,
                     DreamtinkerTagKeys.Blocks.RawColdIronBlock, DreamtinkerTagKeys.Blocks.coldIronBlock, DreamtinkerTagKeys.Blocks.soulSteelBlock);

        this.tag(Tags.Blocks.ORES)
            .add(larimarOre.get(), amberOre.get(), blackSapphireOre.get(), DeepSlateBlackSapphireOre.get(), scoleciteOre.get(), DeepslateColdIronOre.get(),
                 coldIronOre.get(), OrichalcumOre.get(), DeepslateOrichalcumOre.get(), coldIronOre.get(), DeepslateColdIronOre.get());
        tagBlocks(Tags.Blocks.ORE_RATES_SINGULAR, larimarOre, amberOre, larimarOre, blackSapphireOre, DeepSlateBlackSapphireOre, OrichalcumOre,
                  DeepslateOrichalcumOre);
        tagBlocks(Tags.Blocks.ORE_RATES_DENSE, scoleciteOre, DeepslateColdIronOre, coldIronOre);
        this.tag(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE).add(DeepSlateBlackSapphireOre.get(), DeepslateColdIronOre.get());
        this.tag(Tags.Blocks.ORES_IN_GROUND_STONE).add(blackSapphireOre.get(), coldIronOre.get());
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
