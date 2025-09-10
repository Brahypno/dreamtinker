package org.dreamtinker.dreamtinker.register;


import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import static org.dreamtinker.dreamtinker.Dreamtinker.MODID;

public class DreamtinkerTab {

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<CreativeModeTab> TOOL = TABS.register("tool", () -> CreativeModeTab.builder().title(Component.translatable(
            "itemGroup." + MODID + ".tool")).icon(() -> DreamtinkerItems.masu.get().getRenderTool()).displayItems((params, output) -> {
        ToolBuildHandler.addVariants(output::accept, DreamtinkerItems.masu.get(), "");
        ToolBuildHandler.addVariants(output::accept, DreamtinkerItems.tntarrow.get(), "");
        for (var item : DreamtinkerItems.underPlate.values()) {
            ToolBuildHandler.addVariants(output::accept, item, "");
        }
    }).build());

    public static final RegistryObject<CreativeModeTab> PART =
            TABS.register("part", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup." + MODID + ".part")).icon(() -> {
                MaterialVariantId material;
                if (MaterialRegistry.isFullyLoaded()){
                    material = ToolBuildHandler.RANDOM.getMaterial(HeadMaterialStats.ID, RandomSource.create());
                }else {
                    material = ToolBuildHandler.getRenderMaterial(0);
                }
                return DreamtinkerItems.explode_core.get().withMaterialForDisplay(material);
            }).displayItems((params, output) -> {
                DreamtinkerItems.explode_core.get().addVariants(output::accept, "");
                DreamtinkerItems.memoryOrthant.get().addVariants(output::accept, "");
                DreamtinkerItems.wishOrthant.get().addVariants(output::accept, "");
                DreamtinkerItems.soulOrthant.get().addVariants(output::accept, "");
                DreamtinkerItems.personaOrthant.get().addVariants(output::accept, "");
                DreamtinkerItems.reasonEmanation.get().addVariants(output::accept, "");
            }).build());

    public static final RegistryObject<CreativeModeTab> ORE =
            TABS.register("ore", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup." + MODID + ".ore")).icon(() -> new ItemStack(
                    DreamtinkerItems.metallivorous_stibium_lupus.get())).displayItems((params, output) -> {
                DreamtinkerItems.addTabItems(params, output);
                DreamtinkerBlocks.addTabBlocks(params, output);
            }).build());
}