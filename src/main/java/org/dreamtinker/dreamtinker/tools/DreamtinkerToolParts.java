package org.dreamtinker.dreamtinker.tools;

import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;
import slimeknights.tconstruct.tools.stats.LimbMaterialStats;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.dreamtinker.dreamtinker.Dreamtinker.MODID;
import static org.dreamtinker.dreamtinker.DreamtinkerModule.*;


public class DreamtinkerToolParts {
    public DreamtinkerToolParts() {}

    private static final Item.Properties ITEM_DROPS = (new Item.Properties()).stacksTo(64);

    public static final RegistryObject<CreativeModeTab> PART =
            TABS.register("part", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup." + MODID + ".part"))
                                                       .icon(() -> {
                                                           MaterialVariantId material;
                                                           if (MaterialRegistry.isFullyLoaded()){
                                                               material = ToolBuildHandler.RANDOM.getMaterial(HeadMaterialStats.ID, RandomSource.create());
                                                           }else {
                                                               material = ToolBuildHandler.getRenderMaterial(0);
                                                           }
                                                           return DreamtinkerToolParts.explode_core.get().withMaterialForDisplay(material);
                                                       })
                                                       .displayItems(DreamtinkerToolParts::addTabItems)
                                                       .withTabsBefore(DreamtinkerTools.TOOL.getId())
                                                       .withSearchBar().build());

    public static final RegistryObject<ToolPartItem> explode_core = ITEMS.register("explode_core", () -> new ToolPartItem(ITEM_DROPS, HeadMaterialStats.ID));


    public static final RegistryObject<ToolPartItem> chainSawCore =
            ITEMS.register("chain_saw_core", () -> new ToolPartItem(ITEM_DROPS, HandleMaterialStats.ID));
    public static final RegistryObject<ToolPartItem> chainSawTeeth =
            ITEMS.register("chain_saw_teeth", () -> new ToolPartItem(ITEM_DROPS, HeadMaterialStats.ID));
    public static final CastItemObject chainSawCoreCast = MODI_TOOLS.registerCast(chainSawCore.getId().getPath(), ITEM_DROPS);
    public static final CastItemObject chainSawTeethCast = MODI_TOOLS.registerCast(chainSawTeeth.getId().getPath(), ITEM_DROPS);
    
    public static final RegistryObject<ToolPartItem> wishOrthant =
            ITEMS.register("wish_orthant", () -> new ToolPartItem(ITEM_DROPS, HandleMaterialStats.ID) {
                @Override
                public boolean isFoil(@NotNull ItemStack stack) {return true;}
            });
    public static final RegistryObject<ToolPartItem> soulOrthant =
            ITEMS.register("soul_orthant", () -> new ToolPartItem(ITEM_DROPS, LimbMaterialStats.ID));
    public static final RegistryObject<ToolPartItem> memoryOrthant =
            ITEMS.register("memory_orthant", () -> new ToolPartItem(ITEM_DROPS.rarity(Rarity.RARE), HeadMaterialStats.ID));
    public static final RegistryObject<ToolPartItem> personaOrthant =
            ITEMS.register("persona_orthant", () -> new ToolPartItem(ITEM_DROPS.rarity(Rarity.RARE), LimbMaterialStats.ID));
    public static final RegistryObject<ToolPartItem> reasonEmanation =
            ITEMS.register("reason_emanation", () -> new ToolPartItem(ITEM_DROPS.rarity(Rarity.RARE), HeadMaterialStats.ID));


    private static void addTabItems(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output tab) {
        Consumer<ItemStack> output = tab::accept;
        accept(output, explode_core);
        accept(output, memoryOrthant);
        accept(output, wishOrthant);
        accept(output, soulOrthant);
        accept(output, personaOrthant);
        accept(output, reasonEmanation);
        accept(output, chainSawCore);
        accept(output, chainSawTeeth);

        // casts
        addCasts(tab, CastItemObject::get);
        addCasts(tab, CastItemObject::getSand);
        addCasts(tab, CastItemObject::getRedSand);
    }

    private static void addCasts(CreativeModeTab.Output output, Function<CastItemObject, ItemLike> getter) {
        accept(output, getter, chainSawCoreCast);
        accept(output, getter, chainSawTeethCast);
    }

    /**
     * Adds a tool part to the tab
     */
    private static void accept(Consumer<ItemStack> output, Supplier<? extends IMaterialItem> item) {
        item.get().addVariants(output, "");
    }

    /**
     * Adds a cast to the tab
     */
    private static void accept(CreativeModeTab.Output output, Function<CastItemObject, ItemLike> getter, CastItemObject cast) {
        output.accept(getter.apply(cast));
    }

}
