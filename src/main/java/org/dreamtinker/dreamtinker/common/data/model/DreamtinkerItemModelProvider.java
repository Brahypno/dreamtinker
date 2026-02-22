package org.dreamtinker.dreamtinker.common.data.model;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerCommon;
import org.dreamtinker.dreamtinker.library.compact.ars_nouveau.Spell.AugmentTinker;
import org.dreamtinker.dreamtinker.smeltery.DreamTinkerSmeltery;
import org.dreamtinker.dreamtinker.tools.DreamtinkerToolParts;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.data.model.MaterialModelBuilder;
import slimeknights.tconstruct.common.registration.CastItemObject;
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

    public void generateItemModel(ResourceLocation rs, String typePath) {
        withExistingParent(rs.getPath(), parent_item).texture("layer0", getItemLocation(rs.getPath(), typePath));
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
        generateItemModel(DreamtinkerCommon.memory_cast, "cast");
        generateItemModel(DreamtinkerCommon.wish_cast, "cast");
        generateItemModel(DreamtinkerCommon.soul_cast, "cast");
        generateItemModel(DreamtinkerCommon.persona_cast, "cast");
        generateItemModel(DreamtinkerCommon.reason_cast, "cast");
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
        generateItemModel(DreamtinkerCommon.black_sapphire, "materials");
        generateItemModel(DreamtinkerCommon.shiningFlint, "materials");
        generateItemModel(DreamtinkerCommon.scolecite, "materials");
        generateItemModel(DreamtinkerCommon.orichalcum, "materials");
        generateItemModel(DreamtinkerCommon.orichalcum_nugget, "materials");
        generateItemModel(DreamtinkerCommon.raw_orichalcum, "");
        generateItemModel(DreamtinkerCommon.cold_iron_ingot, "materials");
        generateItemModel(DreamtinkerCommon.cold_iron_nugget, "materials");
        generateItemModel(DreamtinkerCommon.raw_cold_iron, "");
        generateItemModel(DreamtinkerCommon.shadow_silver_ingot, "materials");
        generateItemModel(DreamtinkerCommon.shadow_silver_nugget, "materials");
        generateItemModel(DreamtinkerCommon.raw_shadow_silver, "");
        generateItemModel(DreamtinkerCommon.transmutation_gold_ingot, "materials");
        generateItemModel(DreamtinkerCommon.transmutation_gold_nugget, "materials");
        generateItemModel(DreamtinkerCommon.transmutation_gold_dust, "materials");
        generateItemModel(DreamtinkerCommon.raw_transmutation_gold, "");
        generateItemModel(DreamtinkerCommon.deep_prismarine_shard, "materials");
        generateItemModel(AugmentTinker.INSTANCE.getRegistryName(), "");

        booleanItem("red_fur", DreamtinkerCommon.fox_fur.getId(), getItemLocation("white_fur", ""), getItemLocation("red_fur", ""));
        //generateBlockItemModel(object);
        part(DreamtinkerToolParts.explode_core.get());
        part(DreamtinkerToolParts.memoryOrthant.get());
        part(DreamtinkerToolParts.wishOrthant.get());
        part(DreamtinkerToolParts.soulOrthant.get());
        part(DreamtinkerToolParts.personaOrthant.get());
        part(DreamtinkerToolParts.reasonEmanation.get());
        part(DreamtinkerToolParts.chainSawCore.get());
        part(DreamtinkerToolParts.chainSawTeeth.get());
        part(DreamtinkerToolParts.NovaCover.get());
        part(DreamtinkerToolParts.NovaMisc.get());
        part(DreamtinkerToolParts.NovaWrapper.get());
        part(DreamtinkerToolParts.NovaRostrum.get());

        cast(DreamTinkerSmeltery.chainSawTeethCast);
        cast(DreamTinkerSmeltery.chainSawCoreCast);
        cast(DreamTinkerSmeltery.NovaCoverCast);
        cast(DreamTinkerSmeltery.NovaMiscCast);
        cast(DreamTinkerSmeltery.NovaRostrumCast);
        cast(DreamTinkerSmeltery.NovaWrapperCast);
        generateItemModel(DreamTinkerSmeltery.ashenBrick, "transmute");
    }

    private MaterialModelBuilder<ItemModelBuilder> part(ResourceLocation part, String texture) {
        return withExistingParent(part.getPath(), "forge:item/default")
                .texture("texture", Dreamtinker.getLocation("item/tool/" + texture))
                .customLoader(MaterialModelBuilder::new);
    }

    /**
     * Creates models for the given cast object
     */
    private void cast(CastItemObject cast) {
        String name = cast.getName().getPath();
        basicItem(cast.getId(), "cast/" + name);
        basicItem(cast.getSand(), "sand_cast/" + name);
        basicItem(cast.getRedSand(), "red_sand_cast/" + name);
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
        return ForgeRegistries.ITEMS.getKey(item.asItem());
    }

    private ItemModelBuilder generated(ResourceLocation item, ResourceLocation texture) {
        return getBuilder(item.toString()).parent(new ModelFile.UncheckedModelFile(parent_item)).texture("layer0", texture);
    }

    /**
     * Generated item with a texture
     */
    private ItemModelBuilder generated(ResourceLocation item, String texture) {
        return generated(item, new ResourceLocation(item.getNamespace(), texture));
    }

    /**
     * Generated item with a texture
     */
    private ItemModelBuilder generated(ItemLike item, String texture) {
        return generated(id(item), texture);
    }

    /**
     * Generated item with a texture
     */
    private ItemModelBuilder basicItem(ResourceLocation item, String texture) {
        return generated(item, "item/" + texture);
    }

    /**
     * Generated item with a texture
     */
    private ItemModelBuilder basicItem(ItemLike item, String texture) {
        return basicItem(id(item), texture);
    }

    private void booleanItem(
            String predicate,
            ResourceLocation id,
            ResourceLocation offTex,
            ResourceLocation onTex) {

        // base / false
        withExistingParent(id.getPath(), "item/generated")
                .texture("layer0", offTex)
                .override()
                .predicate(new ResourceLocation(Dreamtinker.MODID, predicate), 1.0F)
                .model(
                        withExistingParent(id.getPath() + "_on", "item/generated")
                                .texture("layer0", onTex)
                )
                .end();
    }
}
