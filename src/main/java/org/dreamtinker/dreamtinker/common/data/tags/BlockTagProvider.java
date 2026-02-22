package org.dreamtinker.dreamtinker.common.data.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerTagKeys;
import org.dreamtinker.dreamtinker.smeltery.DreamTinkerSmeltery;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.registration.object.BuildingBlockObject;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.registration.GeodeItemObject;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static net.minecraft.tags.BlockTags.*;
import static org.dreamtinker.dreamtinker.common.DreamtinkerCommon.*;
import static slimeknights.tconstruct.common.TinkerTags.Blocks.MINEABLE_MELTING_BLACKLIST;

public class BlockTagProvider extends BlockTagsProvider {

    public BlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        addCommon(provider);
        addWorld(provider);
        addHarvest(provider);
        addSmeltery(provider);
    }

    private void addCommon(HolderLookup.@NotNull Provider provider) {

    }

    private void addWorld(HolderLookup.@NotNull Provider provider) {
        this.tag(DreamtinkerTagKeys.Blocks.drop_peach).add(Blocks.BIRCH_LEAVES);
        addBlocksTags(narcissus.get(), Dreamtinker.forgeBlockTag("mineable/shears"), TinkerTags.Blocks.SLIMY_FUNGUS_CAN_GROW_THROUGH,
                      TinkerTags.Blocks.MINABLE_WITH_DAGGER, FLOWERS, ENDERMAN_HOLDABLE, SMALL_FLOWERS, SWORD_EFFICIENT, TinkerTags.Blocks.MINABLE_WITH_SCYTHE,
                      Dreamtinker.forgeBlockTag("mineable/sword"), TinkerTags.Blocks.MINABLE_WITH_SHEARS);

        tagBlocks(DreamtinkerTagKeys.Blocks.larimarOre, larimarOre);
        tagBlocks(DreamtinkerTagKeys.Blocks.amberOre, amberOre);
        tagBlocks(DreamtinkerTagKeys.Blocks.scoleciteOre, scoleciteOre);
        tagBlocks(DreamtinkerTagKeys.Blocks.soulSteelBlock, soulSteelBlock);
        tagBlocks(DreamtinkerTagKeys.Blocks.blackSapphireOre, blackSapphireOre);

        tagBlocks(DreamtinkerTagKeys.Blocks.OrichalcumBlock, OrichalcumBlock);
        tagBlocks(DreamtinkerTagKeys.Blocks.RawOrichalcumBlock, RawOrichalcumBlock);
        tagBlocks(DreamtinkerTagKeys.Blocks.OrichalcumOre, OrichalcumOre, DeepslateOrichalcumOre);

        tagBlocks(DreamtinkerTagKeys.Blocks.coldIronBlock, ColdIronBlock);
        tagBlocks(DreamtinkerTagKeys.Blocks.RawColdIronBlock, RawColdIronBlock);
        tagBlocks(DreamtinkerTagKeys.Blocks.coldIronOre, coldIronOre, DeepslateColdIronOre);

        tagBlocks(DreamtinkerTagKeys.Blocks.ShadowSilverBlock, ShadowSilverBlock);
        tagBlocks(DreamtinkerTagKeys.Blocks.RawShadowSilverBlock, RawShadowSilverBlock);
        tagBlocks(DreamtinkerTagKeys.Blocks.ShadowSilverOre, ShadowSilverOre, DeepslateShadowSilverOre);

        tagBlocks(DreamtinkerTagKeys.Blocks.TransmutationGoldBlock, TransmutationGoldBlock);
        tagBlocks(DreamtinkerTagKeys.Blocks.RawTransmutationGoldBlock, RawTransmutationGoldBlock);
        tagBlocks(DreamtinkerTagKeys.Blocks.TransmutationGoldOre, TransmutationGoldOre, DeepslateTransmutationGoldOre);

        tagBlocks(Tags.Blocks.STORAGE_BLOCKS, ColdIronBlock, RawColdIronBlock,
                  OrichalcumOre, DeepslateOrichalcumOre, OrichalcumBlock, RawOrichalcumBlock,
                  ShadowSilverBlock, TransmutationGoldBlock, RawTransmutationGoldBlock, soulSteelBlock, metallivorous_stibium_lupus_block);

        this.tag(Tags.Blocks.ORES)
            .addTags(DreamtinkerTagKeys.Blocks.larimarOre, DreamtinkerTagKeys.Blocks.amberOre, DreamtinkerTagKeys.Blocks.scoleciteOre,
                     DreamtinkerTagKeys.Blocks.blackSapphireOre, DreamtinkerTagKeys.Blocks.OrichalcumOre, DreamtinkerTagKeys.Blocks.coldIronOre,
                     DreamtinkerTagKeys.Blocks.ShadowSilverOre, DreamtinkerTagKeys.Blocks.TransmutationGoldOre);

        tagBlocks(Tags.Blocks.ORE_RATES_SINGULAR, larimarOre, amberOre, larimarOre, blackSapphireOre, OrichalcumOre,
                  DeepslateOrichalcumOre, ShadowSilverOre, DeepslateShadowSilverOre, TransmutationGoldOre, DeepslateTransmutationGoldOre);
        tagBlocks(Tags.Blocks.ORE_RATES_DENSE, scoleciteOre, DeepslateColdIronOre, coldIronOre);

        this.tag(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE)
            .add(DeepslateColdIronOre.get(), DeepslateShadowSilverOre.get(), DeepslateTransmutationGoldOre.get());
        this.tag(Tags.Blocks.ORES_IN_GROUND_STONE).add(blackSapphireOre.get(), coldIronOre.get(), ShadowSilverOre.get(), TransmutationGoldOre.get());


        this.tag(BlockTags.ENDERMAN_HOLDABLE).
            add(DreamTinkerSmeltery.enderMortar.get());
    }

    private void addSmeltery(HolderLookup.@NotNull Provider provider) {
        this.tag(BlockTags.DRAGON_IMMUNE).add(crying_obsidian_plane.get());

        this.tag(DreamtinkerTagKeys.Blocks.ASHEN_BLOCKS)
            .add(DreamTinkerSmeltery.ashenStone.get(), DreamTinkerSmeltery.polishedAshenStone.get(), DreamTinkerSmeltery.ashenBricks.get(),
                 DreamTinkerSmeltery.ashenRoad.get(), DreamTinkerSmeltery.chiseledAshenBricks.get());
        //this.tag(DreamtinkerTagKeys.Blocks.TRANSMUTE_BRICKS).addTag(TinkerTags.Blocks.ASHEN_BLOCKS);
        this.tag(BlockTags.FENCES).add(DreamTinkerSmeltery.ashenBricks.getFence());

        IntrinsicTagAppender<Block> scorchedTankTagAppender = this.tag(DreamtinkerTagKeys.Blocks.TRANSMUTE_TANKS);
        DreamTinkerSmeltery.ashenTank.values().forEach(scorchedTankTagAppender::add);

        this.tag(DreamtinkerTagKeys.Blocks.TRANSMUTE_WALL)
            .addTags(DreamtinkerTagKeys.Blocks.ASHEN_BLOCKS)
            .add(DreamTinkerSmeltery.ashenLamp.get(), DreamTinkerSmeltery.ashenDrain.get(), DreamTinkerSmeltery.ashenChute.get(),
                 DreamTinkerSmeltery.ashenDuct.get());
        this.tag(DreamtinkerTagKeys.Blocks.TRANSMUTE_FLOOR)
            .addTags(DreamtinkerTagKeys.Blocks.ASHEN_BLOCKS)
            .add(DreamTinkerSmeltery.ashenLamp.get(), DreamTinkerSmeltery.ashenDrain.get(), DreamTinkerSmeltery.ashenChute.get(),
                 DreamTinkerSmeltery.ashenDuct.get());
        this.tag(DreamtinkerTagKeys.Blocks.TRANSMUTE)
            .addTags(DreamtinkerTagKeys.Blocks.TRANSMUTE_WALL)
            .addTags(DreamtinkerTagKeys.Blocks.TRANSMUTE_FLOOR)
            .addTags(DreamtinkerTagKeys.Blocks.TRANSMUTE_TANKS);
        this.tag(DreamtinkerTagKeys.Blocks.TRANSMUTE_BLOCKS).addTag(DreamtinkerTagKeys.Blocks.ASHEN_BLOCKS);


    }

    private void addHarvest(HolderLookup.@NotNull Provider provider) {
        tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_DIAMOND_TOOL, crying_obsidian_plane, blackSapphireOre);
        tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_IRON_TOOL, larimarOre, amberOre, scoleciteOre, soulSteelBlock,
                  coldIronOre, DeepslateColdIronOre, ColdIronBlock, RawColdIronBlock,
                  OrichalcumOre, DeepslateOrichalcumOre, OrichalcumBlock, RawOrichalcumBlock,
                  ShadowSilverBlock, ShadowSilverOre, DeepslateShadowSilverOre, RawShadowSilverBlock,
                  TransmutationGoldBlock, TransmutationGoldOre, DeepslateTransmutationGoldOre, RawTransmutationGoldBlock);
        tagBlocks(BEACON_BASE_BLOCKS, soulSteelBlock, OrichalcumBlock, ColdIronBlock, ShadowSilverBlock, TransmutationGoldBlock,
                  metallivorous_stibium_lupus_block);
        tagBlocks(MINEABLE_WITH_PICKAXE, Tiers.NETHERITE.getTag(), metallivorous_stibium_lupus_block);

        tagBlocks(MINEABLE_WITH_SHOVEL, DreamTinkerSmeltery.enderMortar);
        tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_DIAMOND_TOOL, DreamTinkerSmeltery.ashenBricks, DreamTinkerSmeltery.ashenRoad);
        tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_DIAMOND_TOOL, DreamTinkerSmeltery.ashenStone, DreamTinkerSmeltery.polishedAshenStone/*,
                  DreamTinkerSmeltery.chiseledScorchedBricks, DreamTinkerSmeltery.ashenLadder, DreamTinkerSmeltery.ashenLamp, DreamTinkerSmeltery.ashenGlass,
                  DreamTinkerSmeltery.ashenSoulGlass, DreamTinkerSmeltery.ashenTintedGlass, DreamTinkerSmeltery.ashenGlassPane,
                  DreamTinkerSmeltery.ashenSoulGlassPane*/);

        tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_DIAMOND_TOOL, DreamTinkerSmeltery.ashenDrain, DreamTinkerSmeltery.ashenChute,
                  DreamTinkerSmeltery.transmuteController);
        tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_DIAMOND_TOOL, DreamTinkerSmeltery.ashenTank);
        tagBlocks(MINEABLE_MELTING_BLACKLIST, DreamTinkerSmeltery.transmuteController);
        tagBlocks(MINEABLE_MELTING_BLACKLIST, DreamTinkerSmeltery.ashenTank);
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
