package org.dreamtinker.dreamtinker.common.data.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerCommon;
import slimeknights.mantle.client.model.builder.ColoredModelBuilder;
import slimeknights.mantle.client.model.builder.ConnectedModelBuilder;
import slimeknights.mantle.client.model.builder.MantleItemLayerBuilder;
import slimeknights.tconstruct.TConstruct;

import javax.annotation.Nullable;

import static net.minecraftforge.client.model.generators.ModelProvider.BLOCK_FOLDER;

public class DreamTinkerBlockStateProvider extends BlockStateProvider {
    private final ModelFile.UncheckedModelFile GENERATED = new ModelFile.UncheckedModelFile("item/generated");

    public DreamTinkerBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Dreamtinker.MODID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        ResourceLocation crying_obsidian = new ResourceLocation("block/crying_obsidian");
        paneBlock(DreamtinkerCommon.crying_obsidian_plane.get(), "crying_obsidian_pane/", crying_obsidian, crying_obsidian, false, -1, false,
                  RenderType.solid());
        simpleBlockWithItem(DreamtinkerCommon.narcissus.get(),
                            models().cross("narcissus", modLoc("block/narcissus"))
                                    .renderType("cutout").guiLight(BlockModel.GuiLight.FRONT));
        simpleBlockWithItem(DreamtinkerCommon.potted_narcissus.get(),
                            models().withExistingParent("potted_narcissus", new ResourceLocation("minecraft", "block/flower_pot_cross"))
                                    .texture("plant", blockTexture(DreamtinkerCommon.narcissus.get()))
                                    .renderType("cutout").guiLight(BlockModel.GuiLight.FRONT));
        simpleBlockWithItem(DreamtinkerCommon.larimarOre.get(), cubeAll(DreamtinkerCommon.larimarOre.get()));
        this.axisBlock(DreamtinkerCommon.amberOre.get(), "amber_ore", modLoc("block/amber_ore"), true);
        simpleBlockWithItem(DreamtinkerCommon.blackSapphireOre.get(), cubeAll(DreamtinkerCommon.blackSapphireOre.get()));
        this.axisBlock(DreamtinkerCommon.scoleciteOre.get(), "scolecite_ore", modLoc("block/scolecite_ore"), true);
        simpleBlockWithItem(DreamtinkerCommon.soulSteelBlock.get(), cubeAll(DreamtinkerCommon.soulSteelBlock.get()));
        simpleBlockWithItem(DreamtinkerCommon.coldIronOre.get(), cubeAll(DreamtinkerCommon.coldIronOre.get()));
        simpleBlockWithItem(DreamtinkerCommon.DeepslateColdIronOre.get(), cubeAll(DreamtinkerCommon.DeepslateColdIronOre.get()));
        simpleBlockWithItem(DreamtinkerCommon.ColdIronBlock.get(), cubeAll(DreamtinkerCommon.ColdIronBlock.get()));
        simpleBlockWithItem(DreamtinkerCommon.RawColdIronBlock.get(), cubeAll(DreamtinkerCommon.RawColdIronBlock.get()));
        simpleBlockWithItem(DreamtinkerCommon.OrichalcumOre.get(), cubeAll(DreamtinkerCommon.OrichalcumOre.get()));
        simpleBlockWithItem(DreamtinkerCommon.DeepslateOrichalcumOre.get(), cubeAll(DreamtinkerCommon.DeepslateOrichalcumOre.get()));
        simpleBlockWithItem(DreamtinkerCommon.OrichalcumBlock.get(), cubeAll(DreamtinkerCommon.OrichalcumBlock.get()));
        simpleBlockWithItem(DreamtinkerCommon.RawOrichalcumBlock.get(), cubeAll(DreamtinkerCommon.RawOrichalcumBlock.get()));
        simpleBlockWithItem(DreamtinkerCommon.ShadowSilverOre.get(), cubeAll(DreamtinkerCommon.ShadowSilverOre.get()));
        simpleBlockWithItem(DreamtinkerCommon.DeepslateShadowSilverOre.get(), cubeAll(DreamtinkerCommon.DeepslateShadowSilverOre.get()));
        simpleBlockWithItem(DreamtinkerCommon.ShadowSilverBlock.get(), cubeAll(DreamtinkerCommon.ShadowSilverBlock.get()));
        simpleBlockWithItem(DreamtinkerCommon.RawShadowSilverBlock.get(), cubeAll(DreamtinkerCommon.RawShadowSilverBlock.get()));
        simpleBlockWithItem(DreamtinkerCommon.TransmutationGoldOre.get(), cubeAll(DreamtinkerCommon.TransmutationGoldOre.get()));
        simpleBlockWithItem(DreamtinkerCommon.DeepslateTransmutationGoldOre.get(), cubeAll(DreamtinkerCommon.DeepslateTransmutationGoldOre.get()));
        simpleBlockWithItem(DreamtinkerCommon.TransmutationGoldBlock.get(), cubeAll(DreamtinkerCommon.TransmutationGoldBlock.get()));
        simpleBlockWithItem(DreamtinkerCommon.RawTransmutationGoldBlock.get(), cubeAll(DreamtinkerCommon.RawTransmutationGoldBlock.get()));
    }

    public void axisBlock(Block block, String location, ResourceLocation texture, boolean horizontal) {
        ResourceLocation endTexture = horizontal ? texture.withSuffix("_top") : texture;
        ModelFile model = this.models().cubeColumn(resourceString(location), texture, endTexture);
        this.axisBlock((RotatedPillarBlock) block, model,
                       (ModelFile) (horizontal ? this.models().cubeColumnHorizontal(resourceString(location + "_horizontal"), texture, endTexture) :
                                    model));
        this.simpleBlockItem(block, model);
    }

    public static String resourceString(String res) {
        return String.format("%s:%s", Dreamtinker.MODID, res);
    }

    /**
     * Creates a new pane block state
     */
    private void paneBlockWithEdge(IronBarsBlock block, ModelFile post, ModelFile side, ModelFile sideAlt, ModelFile noSide, ModelFile noSideAlt, ModelFile noSideEdge) {
        MultiPartBlockStateBuilder builder = getMultipartBuilder(block)
                .part().modelFile(post).addModel().end();
        PipeBlock.PROPERTY_BY_DIRECTION.forEach((dir, value) -> {
            if (dir.getAxis().isHorizontal()){
                boolean alt = dir == Direction.SOUTH;
                builder.part().modelFile(alt || dir == Direction.WEST ? sideAlt : side).rotationY(dir.getAxis() == Direction.Axis.X ? 90 : 0).addModel()
                       .condition(value, true).end()
                       .part().modelFile(alt || dir == Direction.EAST ? noSideAlt : noSide)
                       .rotationY(dir == Direction.WEST ? 270 : dir == Direction.SOUTH ? 90 : 0).addModel()
                       .condition(value, false).end()
                       .part().modelFile(noSideEdge).rotationY((int) dir.getOpposite().toYRot()).addModel()
                       .condition(value, false)
                       .condition(PipeBlock.PROPERTY_BY_DIRECTION.get(dir.getClockWise()), false)
                       .condition(PipeBlock.PROPERTY_BY_DIRECTION.get(dir.getCounterClockWise()), false).end();
            }
        });
    }

    /**
     * Creates a pane model using the TConstruct templates
     */
    private BlockModelBuilder paneModel(String baseName, String variant, ResourceLocation pane, @Nullable ResourceLocation edge, @Nullable RenderType renderType, boolean connected, int tint) {
        BlockModelBuilder builder =
                models().withExistingParent(BLOCK_FOLDER + "/" + baseName + variant, TConstruct.getResource("block/template/pane/" + variant));
        builder.texture("pane", pane);
        if (edge != null){
            builder.texture("edge", edge);
        }
        if (renderType != null){
            builder.renderType(renderType.name);
        }
        if (connected){
            ConnectedModelBuilder<BlockModelBuilder> cBuilder = builder.customLoader(ConnectedModelBuilder::new);
            cBuilder.connected("pane", "cornerless_full").setPredicate("pane");
            if (tint != -1){
                cBuilder.color(tint);
            }
        }else if (tint != -1){
            builder.customLoader(ColoredModelBuilder::new).color(tint);
        }
        return builder;
    }

    /**
     * Creates a new pane block with all relevant models
     */
    public void paneBlock(IronBarsBlock block, String baseName, ResourceLocation pane, ResourceLocation edge, boolean connected, int tint, boolean solidEdge, @Nullable RenderType renderType) {
        // build block models
        ModelFile post = paneModel(baseName, "post", pane, edge, renderType, connected, tint);
        ModelFile side = paneModel(baseName, "side", pane, edge, renderType, connected, tint);
        ModelFile sideAlt = paneModel(baseName, "side_alt", pane, edge, renderType, connected, tint);
        ModelFile noSide = paneModel(baseName, "noside", pane, null, renderType, connected, tint);
        ModelFile noSideAlt = paneModel(baseName, "noside_alt", pane, null, renderType, connected, tint);
        if (solidEdge && !pane.equals(edge)){
            ModelFile noSideEdge = paneModel(baseName, "noside_edge", pane, edge, renderType, false, tint);
            paneBlockWithEdge(block, post, side, sideAlt, noSide, noSideAlt, noSideEdge);
        }else {
            paneBlock(block, post, side, sideAlt, noSide, noSideAlt);
        }
        // build item model
        ItemModelBuilder item = itemModels().getBuilder(itemKey(block).toString()).parent(GENERATED).texture("layer0", pane);
        if (tint != -1){
            item.customLoader(MantleItemLayerBuilder::new).color(tint);
        }
        if (renderType != null){
            item.renderType(renderType.name);
        }

    }

    private ResourceLocation itemKey(ItemLike item) {
        return BuiltInRegistries.ITEM.getKey(item.asItem());
    }
}
