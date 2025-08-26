package org.dreamtinker.dreamtinker.data.providers;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.RegistryObject;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.register.DreamtinkerFluid;
import org.dreamtinker.dreamtinker.register.DreamtinkerItem;
import slimeknights.mantle.registration.object.FluidObject;

public class DreamtinkerItemModelProvider extends ItemModelProvider {
    public static final String parent_item = "item/generated";
    public static final String parent_fluid = "forge:item/bucket_drip";

    public DreamtinkerItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Dreamtinker.MODID, existingFileHelper);
    }


    public void generateItemModel(RegistryObject<Item> object, String typePath) {
        withExistingParent(object.getId().getPath(), parent_item).texture("layer0", getItemLocation(object.getId().getPath(), typePath));
    }

    public void generateBlockItemModel(RegistryObject<BlockItem> object) {
        withExistingParent(object.getId().getPath(), getBlockItemLocation(object.getId().getPath()));
    }

    public void generateBucketItemModel(FluidObject<ForgeFlowingFluid> object) {
        withExistingParent(object.getId().getPath() + "_bucket", parent_fluid).customLoader(
                (itemModelBuilder, existingFileHelper) -> DynamicFluidContainerModelBuilder
                        .begin(itemModelBuilder, existingFileHelper)
                        .fluid(object.get()));
    }

    public ResourceLocation getItemLocation(String path, String typePath) {
        return new ResourceLocation(Dreamtinker.MODID, "item/" + typePath + "/" + path);
    }

    public ResourceLocation getBlockItemLocation(String path) {
        return new ResourceLocation(Dreamtinker.MODID, "block/" + path);
    }

    @Override
    protected void registerModels() {

        generateItemModel(DreamtinkerItem.raw_stibnite, "materials");
        generateItemModel(DreamtinkerItem.valentinite, "materials");
        generateItemModel(DreamtinkerItem.nigrescence_antimony, "materials");
        generateItemModel(DreamtinkerItem.metallivorous_stibium_lupus, "materials");
        generateItemModel(DreamtinkerItem.regulus, "materials");
        //generateBlockItemModel(object);
        generateBucketItemModel(DreamtinkerFluid.molten_echo_shard);
        generateBucketItemModel(DreamtinkerFluid.molten_albedo_stibium);
        generateBucketItemModel(DreamtinkerFluid.molten_lupi_antimony);
        generateBucketItemModel(DreamtinkerFluid.molten_ascending_antimony);
        generateBucketItemModel(DreamtinkerFluid.liquid_smoky_antimony);
        generateBucketItemModel(DreamtinkerFluid.molten_crying_obsidian);

    }
}
