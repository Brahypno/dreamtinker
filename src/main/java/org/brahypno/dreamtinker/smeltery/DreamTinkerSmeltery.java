package org.brahypno.dreamtinker.smeltery;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.DreamtinkerModule;
import org.brahypno.dreamtinker.tools.DreamtinkerToolParts;
import org.brahypno.esotericismtinker.smeltery.EsotericismTinkerSmeltery;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.smeltery.block.component.SearedBlock;

import java.util.function.Function;

import static org.brahypno.esotericismtinker.EsotericismTinker.configCompactDisabled;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DreamTinkerSmeltery extends DreamtinkerModule {
    private static final Item.Properties ITEM_PROPS = (new Item.Properties()).stacksTo(64);
    public static final CastItemObject chainSawCoreCast = MODI_TOOLS.registerCast(DreamtinkerToolParts.chainSawCore.getId().getPath(), ITEM_PROPS);
    public static final CastItemObject chainSawTeethCast = MODI_TOOLS.registerCast(DreamtinkerToolParts.chainSawTeeth.getId().getPath(), ITEM_PROPS);
    public static final CastItemObject NovaCoverCast = MODI_TOOLS.registerCast(DreamtinkerToolParts.NovaCover.getId().getPath(), ITEM_PROPS);
    public static final CastItemObject NovaRostrumCast = MODI_TOOLS.registerCast(DreamtinkerToolParts.NovaRostrum.getId().getPath(), ITEM_PROPS);
    public static final CastItemObject NovaWrapperCast = MODI_TOOLS.registerCast(DreamtinkerToolParts.NovaWrapper.getId().getPath(), ITEM_PROPS);
    public static final CastItemObject NovaMiscCast = MODI_TOOLS.registerCast(DreamtinkerToolParts.NovaMisc.getId().getPath(), ITEM_PROPS);


    private static void addTabItems(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {
        addCasts(output, CastItemObject::get);
        addCasts(output, CastItemObject::getSand);
        addCasts(output, CastItemObject::getRedSand);
    }

    private static void addCasts(CreativeModeTab.Output output, Function<CastItemObject, ItemLike> getter) {
        accept(output, getter, chainSawCoreCast);
        accept(output, getter, chainSawTeethCast);
        if (ModList.get().isLoaded("ars_nouveau") && !configCompactDisabled("ars_nouveau")){
            accept(output, getter, NovaCoverCast);
            accept(output, getter, NovaRostrumCast);
            accept(output, getter, NovaWrapperCast);
            accept(output, getter, NovaMiscCast);
        }
    }

    private static BlockBehaviour.Properties ashenSolidProps() {
        return structureProps(MapColor.COLOR_GRAY, SoundType.METAL).strength(3.0F, 9.0F);
    }

    private static BlockBehaviour.Properties structureProps(MapColor color, SoundType sound) {
        return builder(color, sound).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().isValidSpawn(SearedBlock.VALID_SPAWN);
    }

    @SubscribeEvent
    public static void buildCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(EsotericismTinkerSmeltery.SMELTERY_TAB_KEY)){
            addTabItems(event.getParameters(), event);
        }
    }
}