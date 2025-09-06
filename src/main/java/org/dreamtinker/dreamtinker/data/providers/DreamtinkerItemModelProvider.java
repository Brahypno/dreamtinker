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
import org.dreamtinker.dreamtinker.register.DreamtinkerFluids;
import org.dreamtinker.dreamtinker.register.DreamtinkerItems;
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
        return new ResourceLocation(Dreamtinker.MODID, "item/" + (typePath.isEmpty() ? typePath : typePath + "/") + path);
    }

    public ResourceLocation getBlockItemLocation(String path) {
        return new ResourceLocation(Dreamtinker.MODID, "block/" + path);
    }

    @Override
    protected void registerModels() {

        generateItemModel(DreamtinkerItems.raw_stibnite, "materials");
        generateItemModel(DreamtinkerItems.valentinite, "materials");
        generateItemModel(DreamtinkerItems.nigrescence_antimony, "materials");
        generateItemModel(DreamtinkerItems.metallivorous_stibium_lupus, "materials");
        generateItemModel(DreamtinkerItems.regulus, "materials");
        generateItemModel(DreamtinkerItems.void_pearl, "");
        generateItemModel(DreamtinkerItems.soul_etherium, "materials");
        //generateBlockItemModel(object);
        generateBucketItemModel(DreamtinkerFluids.molten_echo_shard);
        generateBucketItemModel(DreamtinkerFluids.molten_albedo_stibium);
        generateBucketItemModel(DreamtinkerFluids.molten_lupi_antimony);
        generateBucketItemModel(DreamtinkerFluids.molten_ascending_antimony);
        generateBucketItemModel(DreamtinkerFluids.liquid_smoky_antimony);
        generateBucketItemModel(DreamtinkerFluids.molten_crying_obsidian);

    }
}
