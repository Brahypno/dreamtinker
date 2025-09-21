package org.dreamtinker.dreamtinker.register;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.dreamtinker.dreamtinker.Dreamtinker;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.registration.BlockDeferredRegisterExtension;
import slimeknights.tconstruct.shared.block.BetterPaneBlock;

public class DreamtinkerBlocks extends TinkerModule {
    //public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Dreamtinker.MODID);
    public static final BlockDeferredRegisterExtension BLOCKS = new BlockDeferredRegisterExtension(Dreamtinker.MODID);

    public static final ItemObject<BetterPaneBlock> crying_obsidian_plane = BLOCKS.register("crying_obsidian_pane", () -> new BetterPaneBlock(
            builder(MapColor.COLOR_BLACK, SoundType.STONE).requiresCorrectToolForDrops().instrument(NoteBlockInstrument.BASEDRUM).noOcclusion()
                                                          .strength(25.0F, 400.0F)), BLOCK_ITEM);
    public static final ItemObject<FlowerBlock> narcissus = BLOCKS.register("narcissus", () -> new FlowerBlock(
            () -> MobEffects.NIGHT_VISION, 5, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission()
                                                                       .instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ)
                                                                       .pushReaction(PushReaction.DESTROY)) {
    }, BLOCK_ITEM);
    public static final ItemObject<FlowerPotBlock> potted_narcissus = BLOCKS.register("potted_narcissus", () -> new FlowerPotBlock(
            () -> (FlowerPotBlock) Blocks.FLOWER_POT, narcissus, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission()
                                                                                          .instabreak().sound(SoundType.STONE)
                                                                                          .offsetType(BlockBehaviour.OffsetType.XZ)
                                                                                          .pushReaction(PushReaction.DESTROY)), BLOCK_ITEM);

    public static void addTabBlocks(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {
        output.accept(crying_obsidian_plane.get());
        output.accept(narcissus.get());
        output.accept(potted_narcissus);
    }
}
