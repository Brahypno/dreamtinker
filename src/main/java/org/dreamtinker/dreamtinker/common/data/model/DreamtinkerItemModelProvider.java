package org.dreamtinker.dreamtinker.common.data.model;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.RegistryObject;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerCommon;
import org.dreamtinker.dreamtinker.fluids.DreamtinkerFluids;
import org.dreamtinker.dreamtinker.tools.DreamtinkerToolParts;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.data.model.MaterialModelBuilder;
import slimeknights.tconstruct.library.tools.part.MaterialItem;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;

public class DreamtinkerItemModelProvider extends ItemModelProvider {
    public static final String parent_item = "item/generated";
    public static final String parent_fluid = "forge:item/bucket_drip";

    public DreamtinkerItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Dreamtinker.MODID, existingFileHelper);
    }


    public void generateItemModel(RegistryObject<Item> object, String typePath) {
        withExistingParent(object.getId().getPath(), parent_item).texture("layer0", getItemLocation(object.getId().getPath(), typePath));
    }

    public void generateBlockItemModel(ItemObject<Block> object) {
        withExistingParent(object.getId().getPath(), getBlockItemLocation(object.getId().getPath()));
    }

    public void generateBucketItemModel(FluidObject<ForgeFlowingFluid> object) {
        withExistingParent(object.getId().getPath() + "_bucket", parent_fluid).customLoader(
                (itemModelBuilder, existingFileHelper) -> DynamicFluidContainerModelBuilder
                        .begin(itemModelBuilder, existingFileHelper)
                        .fluid(object.get()));
    }

    public ResourceLocation getItemLocation(String path, String typePath) {
        return new ResourceLocation(Dreamtinker.MODID, "item/" + (typePath.isEmpty() ? typePath : typePath + "/") + path);
    }

    public ResourceLocation getBlockItemLocation(String path) {
        return new ResourceLocation(Dreamtinker.MODID, "block/" + path);
    }

    @Override
    protected void registerModels() {

        generateItemModel(DreamtinkerCommon.raw_stibnite, "materials");
        generateItemModel(DreamtinkerCommon.valentinite, "materials");
        generateItemModel(DreamtinkerCommon.nigrescence_antimony, "materials");
        generateItemModel(DreamtinkerCommon.metallivorous_stibium_lupus, "materials");
        generateItemModel(DreamtinkerCommon.regulus, "materials");
        generateItemModel(DreamtinkerCommon.void_pearl, "");
        generateItemModel(DreamtinkerCommon.soul_etherium, "materials");
        generateItemModel(DreamtinkerCommon.twist_obsidian_pane, "");
        generateItemModel(DreamtinkerCommon.memory_cast, "casts");
        generateItemModel(DreamtinkerCommon.wish_cast, "casts");
        generateItemModel(DreamtinkerCommon.soul_cast, "casts");
        generateItemModel(DreamtinkerCommon.persona_cast, "casts");
        generateItemModel(DreamtinkerCommon.reason_cast, "casts");
        generateItemModel(DreamtinkerCommon.white_peach, "");
        generateItemModel(DreamtinkerCommon.unborn_egg, "");
        generateItemModel(DreamtinkerCommon.unborn_turtle_egg, "");
        generateItemModel(DreamtinkerCommon.unborn_sniffer_egg, "");
        generateItemModel(DreamtinkerCommon.unborn_dragon_egg, "");
        generateItemModel(DreamtinkerCommon.unborn_spawn_egg, "");
        generateItemModel(DreamtinkerCommon.echo_alloy, "materials");
        generateItemModel(DreamtinkerCommon.malignant_gluttony, "materials");
        generateItemModel(DreamtinkerCommon.larimar, "materials");
        generateItemModel(DreamtinkerCommon.amber, "materials");
        generateItemModel(DreamtinkerCommon.despair_gem, "materials");
        generateItemModel(DreamtinkerCommon.desire_gem, "materials");
        generateItemModel(DreamtinkerCommon.poisonousHomunculus, "materials");
        generateItemModel(DreamtinkerCommon.evilHomunculus, "materials");
        generateItemModel(DreamtinkerCommon.soul_steel, "materials");
        generateItemModel(DreamtinkerCommon.rainbow_honey, "");
        generateItemModel(DreamtinkerCommon.rainbow_honey_crystal, "materials");
        generateItemModel(DreamtinkerCommon.black_corundum, "materials");
        //generateBlockItemModel(object);
        generateBucketItemModel(DreamtinkerFluids.molten_echo_shard);
        generateBucketItemModel(DreamtinkerFluids.molten_albedo_stibium);
        generateBucketItemModel(DreamtinkerFluids.molten_lupi_antimony);
        generateBucketItemModel(DreamtinkerFluids.molten_ascending_antimony);
        generateBucketItemModel(DreamtinkerFluids.liquid_smoky_antimony);
        generateBucketItemModel(DreamtinkerFluids.molten_crying_obsidian);
        part(DreamtinkerToolParts.explode_core.get());
        part(DreamtinkerToolParts.memoryOrthant.get());
        part(DreamtinkerToolParts.wishOrthant.get());
        part(DreamtinkerToolParts.soulOrthant.get());
        part(DreamtinkerToolParts.personaOrthant.get());
        part(DreamtinkerToolParts.reasonEmanation.get());
    }

    private MaterialModelBuilder<ItemModelBuilder> part(ResourceLocation part, String texture) {
        return withExistingParent(part.getPath(), "forge:item/default")
                .texture("texture", Dreamtinker.getLocation("item/tool/" + texture))
                .customLoader(MaterialModelBuilder::new);
    }

    /**
     * Creates a part model in the parts folder
     */
    private MaterialModelBuilder<ItemModelBuilder> part(Item item, String texture) {
        return part(id(item), texture);
    }

    /**
     * Creates a part model with the given texture
     */
    private MaterialModelBuilder<ItemModelBuilder> part(ItemObject<? extends MaterialItem> part, String texture) {
        return part(part.getId(), texture);
    }

    /**
     * Creates a part model in the parts folder
     */
    private void part(ToolPartItem part) {
        part(part, "parts/" + id(part).getPath());
    }

    private ResourceLocation id(ItemLike item) {
        return BuiltInRegistries.ITEM.getKey(item.asItem());
    }
}
