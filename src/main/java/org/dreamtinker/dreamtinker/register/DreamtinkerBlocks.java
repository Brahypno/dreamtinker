package org.dreamtinker.dreamtinker.register;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
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

    public static void addTabBlocks(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {
        output.accept(crying_obsidian_plane.get());
    }
}
