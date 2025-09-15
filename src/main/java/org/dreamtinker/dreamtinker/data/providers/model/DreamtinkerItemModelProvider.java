package org.dreamtinker.dreamtinker.data.providers.model;

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
import org.dreamtinker.dreamtinker.register.DreamtinkerFluids;
import org.dreamtinker.dreamtinker.register.DreamtinkerItems;
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

        generateItemModel(DreamtinkerItems.raw_stibnite, "materials");
        generateItemModel(DreamtinkerItems.valentinite, "materials");
        generateItemModel(DreamtinkerItems.nigrescence_antimony, "materials");
        generateItemModel(DreamtinkerItems.metallivorous_stibium_lupus, "materials");
        generateItemModel(DreamtinkerItems.regulus, "materials");
        generateItemModel(DreamtinkerItems.void_pearl, "");
        generateItemModel(DreamtinkerItems.soul_etherium, "materials");
        generateItemModel(DreamtinkerItems.twist_obsidian_pane, "");
        generateItemModel(DreamtinkerItems.memory_cast, "casts");
        generateItemModel(DreamtinkerItems.wish_cast, "casts");
        generateItemModel(DreamtinkerItems.soul_cast, "casts");
        generateItemModel(DreamtinkerItems.persona_cast, "casts");
        generateItemModel(DreamtinkerItems.reason_cast, "casts");
        generateItemModel(DreamtinkerItems.white_peach, "");
        generateItemModel(DreamtinkerItems.unborn_egg, "");
        generateItemModel(DreamtinkerItems.unborn_turtle_egg, "");
        generateItemModel(DreamtinkerItems.unborn_sniffer_egg, "");
        generateItemModel(DreamtinkerItems.unborn_dragon_egg, "");
        generateItemModel(DreamtinkerItems.unborn_spawn_egg, "");
        //generateBlockItemModel(object);
        generateBucketItemModel(DreamtinkerFluids.molten_echo_shard);
        generateBucketItemModel(DreamtinkerFluids.molten_albedo_stibium);
        generateBucketItemModel(DreamtinkerFluids.molten_lupi_antimony);
        generateBucketItemModel(DreamtinkerFluids.molten_ascending_antimony);
        generateBucketItemModel(DreamtinkerFluids.liquid_smoky_antimony);
        generateBucketItemModel(DreamtinkerFluids.molten_crying_obsidian);
        part(DreamtinkerItems.explode_core.get());
        part(DreamtinkerItems.memoryOrthant.get());
        part(DreamtinkerItems.wishOrthant.get());
        part(DreamtinkerItems.soulOrthant.get());
        part(DreamtinkerItems.personaOrthant.get());
        part(DreamtinkerItems.reasonEmanation.get());
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
