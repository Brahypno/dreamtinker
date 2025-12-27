package org.dreamtinker.dreamtinker.tools;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.RegistryObject;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.DreamtinkerModule;
import org.dreamtinker.dreamtinker.common.DreamtinkerCommon;
import org.dreamtinker.dreamtinker.common.data.tags.MaterialTagProvider;
import org.dreamtinker.dreamtinker.tools.data.DreamtinkerArmorModel;
import org.dreamtinker.dreamtinker.tools.data.DreamtinkerStationLayout;
import org.dreamtinker.dreamtinker.tools.data.DreamtinkerToolDefinitionProvider;
import org.dreamtinker.dreamtinker.tools.data.DreamtinkerToolItemModelProvider;
import org.dreamtinker.dreamtinker.tools.data.material.DreamtinkerMaterialDataProvider;
import org.dreamtinker.dreamtinker.tools.data.material.DreamtinkerMaterialRenderInfoProvider;
import org.dreamtinker.dreamtinker.tools.data.material.DreamtinkerMaterialStatProvider;
import org.dreamtinker.dreamtinker.tools.data.material.DreamtinkerMaterialTraitProvider;
import org.dreamtinker.dreamtinker.tools.data.sprite.DreamtinkerMaterialSpriteProvider;
import org.dreamtinker.dreamtinker.tools.data.sprite.DreamtinkerPartSpriteProvider;
import org.dreamtinker.dreamtinker.tools.items.*;
import org.dreamtinker.dreamtinker.tools.items.NovaBook.ModifiableSpellBook;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.library.client.data.material.GeneratorPartTextureJsonGenerator;
import slimeknights.tconstruct.library.client.data.material.MaterialPartTextureGenerator;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.tools.data.sprite.TinkerMaterialSpriteProvider;
import slimeknights.tconstruct.tools.data.sprite.TinkerPartSpriteProvider;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class DreamtinkerTools extends DreamtinkerModule {
    public DreamtinkerTools() {
        DtTiers.init();
    }

    private static final Item.Properties UNSTACKABLE_PROPS = new Item.Properties().stacksTo(1);
    public static final RegistryObject<CreativeModeTab> TOOL =
            DreamtinkerModule.TABS.register("tool", () -> CreativeModeTab.builder().title(Dreamtinker.makeTranslation("itemGroup", "tool"))
                                                                         .icon(() -> DreamtinkerTools.mashou.get().getRenderTool())
                                                                         .displayItems(DreamtinkerTools::addTabItems)
                                                                         .withTabsBefore(DreamtinkerCommon.ITEM.getId()).withSearchBar().build());

    public static final ItemObject<ModifiableItem> tntarrow =
            MODI_TOOLS.register("tntarrow", () -> new TNTArrow((new Item.Properties()).stacksTo(4), DTtoolsDefinition.TNTARROW, 4));
    public static final ItemObject<ModifiableItem> mashou = MODI_TOOLS.register("mashou", () -> new MaShou(UNSTACKABLE_PROPS, DTtoolsDefinition.MASHOU));
    public static final ItemObject<ModifiableItem> narcissus_wing =
            MODI_TOOLS.register("narcissus_wing", () -> new NarcissusWing(UNSTACKABLE_PROPS.rarity(Rarity.EPIC), DTtoolsDefinition.NarcissusWing));
    public static final EnumObject<ArmorItem.Type, UnderArmorItem> underPlate =
            MODI_TOOLS.registerEnum("under_plate", ArmorItem.Type.values(), type -> new UnderArmorItem(DTtoolsDefinition.UNDER_PLATE, type, UNSTACKABLE_PROPS));
    public static final ItemObject<ModifiableItem> silence_glove =
            MODI_TOOLS.register("silence_glove", () -> new SilenceGlove(UNSTACKABLE_PROPS, DTtoolsDefinition.SilenceGlove));
    public static final ItemObject<ChainSawBlade> chain_saw_blade =
            MODI_TOOLS.register("chain_saw_blade", () -> new ChainSawBlade(UNSTACKABLE_PROPS, DTtoolsDefinition.ChainSawBlade));
    public static final ItemObject<ModifiableSpellBook> per_aspera_scriptum =
            NOVA_MODI_TOOLS.register("per_aspera_scriptum", () -> new ModifiableSpellBook(UNSTACKABLE_PROPS, DTtoolsDefinition.PerAsperaScriptum));

    @SubscribeEvent
    void gatherData(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        generator.addProvider(event.includeClient(), new DreamtinkerMaterialDataProvider(output));
        generator.addProvider(event.includeClient(), new DreamtinkerMaterialStatProvider(output));
        generator.addProvider(event.includeClient(), new DreamtinkerMaterialTraitProvider(output));

        generator.addProvider(event.includeClient(),
                              new DreamtinkerMaterialRenderInfoProvider(output, new DreamtinkerMaterialSpriteProvider(), existingFileHelper));
        generator.addProvider(event.includeClient(),
                              new MaterialPartTextureGenerator(output, existingFileHelper, new TinkerPartSpriteProvider(),
                                                               new DreamtinkerMaterialSpriteProvider()));

        generator.addProvider(event.includeClient(),
                              new MaterialPartTextureGenerator(output, existingFileHelper, new DreamtinkerPartSpriteProvider(),
                                                               new TinkerMaterialSpriteProvider(),
                                                               new DreamtinkerMaterialSpriteProvider()));
        generator.addProvider(event.includeClient(), new DreamtinkerToolItemModelProvider(output, existingFileHelper));
        generator.addProvider(event.includeClient(), new DreamtinkerArmorModel(output));
        generator.addProvider(event.includeServer(), new DreamtinkerToolDefinitionProvider(output));
        generator.addProvider(event.includeServer(), new DreamtinkerStationLayout(output));
        generator.addProvider(event.includeClient(), new MaterialTagProvider(output, existingFileHelper));
        generator.addProvider(event.includeClient(), new GeneratorPartTextureJsonGenerator(output, Dreamtinker.MODID, new DreamtinkerPartSpriteProvider()));
    }

    /**
     * Adds all relevant items to the creative tab
     */
    private static void addTabItems(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output tab) {
        // start with tools that lack materials
        Consumer<ItemStack> output = tab::accept;

        // small tools
        acceptTool(output, tntarrow);
        // broad tools
        acceptTool(output, mashou);
        acceptTool(output, narcissus_wing);
        acceptTool(output, chain_saw_blade);

        // ranged tools

        // ancient tools
        acceptTool(output, silence_glove);

        acceptTools(output, underPlate);
        if (ModList.get().isLoaded("ars_nouveau")){
            acceptTool(output, per_aspera_scriptum);
        }
    }

    /**
     * Adds a tool to the tab
     */
    private static void acceptTool(Consumer<ItemStack> output, Supplier<? extends IModifiable> tool) {
        ToolBuildHandler.addVariants(output, tool.get(), "");
    }

    /**
     * Adds a tool to the tab
     */
    private static void acceptTools(Consumer<ItemStack> output, EnumObject<?, ? extends IModifiable> tools) {
        tools.forEach(tool -> ToolBuildHandler.addVariants(output, tool, ""));
    }
}
