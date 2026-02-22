package org.dreamtinker.dreamtinker.smeltery;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.DreamtinkerModule;
import org.dreamtinker.dreamtinker.common.DreamtinkerTagKeys;
import org.dreamtinker.dreamtinker.smeltery.block.controller.TransmuteControllerBlock;
import org.dreamtinker.dreamtinker.smeltery.block.entity.controller.TransmuteBlockEntity;
import org.dreamtinker.dreamtinker.tools.DreamtinkerToolParts;
import slimeknights.mantle.registration.object.BuildingBlockObject;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.FenceBuildingBlockObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.mantle.util.RetexturedHelper;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.shared.block.PlaceBlockDispenserBehavior;
import slimeknights.tconstruct.smeltery.block.component.*;
import slimeknights.tconstruct.smeltery.block.controller.ControllerBlock;
import slimeknights.tconstruct.smeltery.block.entity.component.SmelteryInputOutputBlockEntity;
import slimeknights.tconstruct.smeltery.item.TankItem;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.dreamtinker.dreamtinker.Dreamtinker.configCompactDisabled;

public class DreamTinkerSmeltery extends DreamtinkerModule {

    private static final BlockBehaviour.StatePredicate NEVER = (state, level, pos) -> false;
    private static final Item.Properties ITEM_PROPS = (new Item.Properties()).stacksTo(64);
    public static final RegistryObject<CreativeModeTab> tabSmeltery = TABS.register(
            "smeltery", () -> CreativeModeTab.builder().title(Dreamtinker.makeTranslation("itemGroup", "smeltery"))
                                             .icon(() -> new ItemStack(DreamTinkerSmeltery.transmuteController))
                                             .displayItems(DreamTinkerSmeltery::addTabItems)
                                             .withTabsBefore(DreamtinkerToolParts.PART.getId())
                                             .build());
    static Supplier<BlockBehaviour.Properties> ashen =
            () -> builder(MapColor.TERRACOTTA_BROWN, SoundType.BASALT)
                    .instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops()
                    .strength(9.0F, 35f).lightLevel(s -> s.getValue(
                            ControllerBlock.ACTIVE) ? 13 : 0);

    public static final ItemObject<Block> enderMortar =
            BLOCKS.register("ender_mortar", builder(MapColor.COLOR_LIGHT_GRAY, SoundType.SCULK).instrument(NoteBlockInstrument.SNARE)
                                                                                               .strength(3.0f).friction(0.8F),
                            TOOLTIP_BLOCK_ITEM);

    public static final RegistryObject<Item> ashenBrick = ITEMS.register("ashen_brick", () -> new Item(ITEM_PROPS));


    // ashen blocks
    public static final ItemObject<Block> ashenStone, polishedAshenStone, chiseledAshenBricks;
    public static final FenceBuildingBlockObject ashenBricks;
    public static final BuildingBlockObject ashenRoad;

    static {
        Properties properties = ashenSolidProps(1);
        Supplier<SearedPillarBlock> pillar = () -> new SearedPillarBlock(properties, false);
        ashenStone = BLOCKS.register("ashen_stone", pillar, TOOLTIP_BLOCK_ITEM);
        polishedAshenStone = BLOCKS.register("polished_ashen_stone", pillar, TOOLTIP_BLOCK_ITEM);
        Supplier<SearedBlock> block = () -> new SearedBlock(properties, false);
        ashenBricks = BLOCKS.registerFenceBuilding("ashen_bricks", block, TOOLTIP_BLOCK_ITEM);
        ashenRoad = BLOCKS.registerBuilding("ashen_road", block, TOOLTIP_BLOCK_ITEM);
        chiseledAshenBricks = BLOCKS.register("chiseled_ashen_bricks", block, TOOLTIP_BLOCK_ITEM);
    }

    public static final ItemObject<Block> ashenLamp =
            BLOCKS.register("ashen_lamp", () -> new SearedBlock(ashenSolidProps(1).lightLevel(state -> 15), false), TOOLTIP_BLOCK_ITEM);
    public static final ItemObject<Block> ashenDrain, ashenDuct, ashenChute;

    static {
        Properties ashen = ashenSolidProps(2);
        ashenDrain = BLOCKS.register("ashen_drain", () -> new SearedDrainBlock(ashen), TOOLTIP_BLOCK_ITEM);
        ashenDuct = BLOCKS.register("ashen_duct", () -> new SearedDuctBlock(ashen), TOOLTIP_BLOCK_ITEM);
        ashenChute = BLOCKS.register("ashen_chute", () -> new RetexturedOrientableSmelteryBlock(ashen, SmelteryInputOutputBlockEntity.ChuteBlockEntity::new),
                                     TOOLTIP_BLOCK_ITEM);
    }

    public static final EnumObject<SearedTankBlock.TankType, SearedTankBlock> ashenTank;

    static {
        Properties ashen = ashenNonSolidProps(SoundType.BASALT).lightLevel(SearedTankBlock.LIGHT_GETTER);
        ashenTank = BLOCKS.registerEnum("ashen", SearedTankBlock.TankType.values(),
                                        type -> new SearedTankBlock(ashen, type.getCapacity(), PushReaction.DESTROY),
                                        b -> new TankItem(b, ITEM_PROPS, true));
    }

    // controllers
    public static final ItemObject<TransmuteControllerBlock> transmuteController =
            BLOCKS.register("transmute_controller", () -> new TransmuteControllerBlock(ashen.get()), TOOLTIP_BLOCK_ITEM);
    public static final RegistryObject<BlockEntityType<TransmuteBlockEntity>>
            Transmute = BLOCK_ENTITIES.register("transmute", TransmuteBlockEntity::new, transmuteController);


    public static final CastItemObject chainSawCoreCast = MODI_TOOLS.registerCast(DreamtinkerToolParts.chainSawCore.getId().getPath(), ITEM_PROPS);
    public static final CastItemObject chainSawTeethCast = MODI_TOOLS.registerCast(DreamtinkerToolParts.chainSawTeeth.getId().getPath(), ITEM_PROPS);

    public static final CastItemObject NovaCoverCast = MODI_TOOLS.registerCast(DreamtinkerToolParts.NovaCover.getId().getPath(), ITEM_PROPS);
    public static final CastItemObject NovaRostrumCast = MODI_TOOLS.registerCast(DreamtinkerToolParts.NovaRostrum.getId().getPath(), ITEM_PROPS);
    public static final CastItemObject NovaWrapperCast = MODI_TOOLS.registerCast(DreamtinkerToolParts.NovaWrapper.getId().getPath(), ITEM_PROPS);
    public static final CastItemObject NovaMiscCast = MODI_TOOLS.registerCast(DreamtinkerToolParts.NovaMisc.getId().getPath(), ITEM_PROPS);

    private static void addTabItems(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, Output output) {
        output.accept(enderMortar);
        output.accept(ashenBrick.get());
        output.accept(ashenStone);
        output.accept(polishedAshenStone);
        accept(output, ashenBricks);
        accept(output, ashenRoad);
        ashenTank.forEach((searedTankBlock) -> output.accept(searedTankBlock));
        output.accept(ashenLamp);
        output.accept(chiseledAshenBricks);
        output.accept(ashenDrain);
        output.accept(ashenDuct);
        output.accept(ashenChute);

        Predicate<ItemStack> variant = stack -> {
            output.accept(stack);
            return false;
        };
        RetexturedHelper.addTagVariants(variant, transmuteController, DreamtinkerTagKeys.Items.ASHEN_BLOCKS);
        RetexturedHelper.addTagVariants(variant, ashenDrain, DreamtinkerTagKeys.Items.ASHEN_BLOCKS);
        RetexturedHelper.addTagVariants(variant, ashenDuct, DreamtinkerTagKeys.Items.ASHEN_BLOCKS);
        RetexturedHelper.addTagVariants(variant, ashenChute, DreamtinkerTagKeys.Items.ASHEN_BLOCKS);

        // casts
        addCasts(output, CastItemObject::get);
        addCasts(output, CastItemObject::getSand);
        addCasts(output, CastItemObject::getRedSand);

    }

    @SubscribeEvent
    void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Consumer<Block> dispenserBehavior = block -> DispenserBlock.registerBehavior(block.asItem(), PlaceBlockDispenserBehavior.INSTANCE);
            ashenTank.forEach(dispenserBehavior);
        });
    }

    private static void addCasts(CreativeModeTab.Output output, Function<CastItemObject, ItemLike> getter) {
        accept(output, getter, chainSawCoreCast);
        accept(output, getter, chainSawTeethCast);
        if (ModList.get().isLoaded("ars_nouveau") && configCompactDisabled("ars_nouveau")){
            accept(output, getter, NovaCoverCast);
            accept(output, getter, NovaRostrumCast);
            accept(output, getter, NovaWrapperCast);
            accept(output, getter, NovaMiscCast);
        }
    }


    /**
     * Properties for an opaque ashen block, such as bricks.
     */
    private static Properties ashenSolidProps(int factor) {
        return structureProps(MapColor.COLOR_GRAY, SoundType.METAL).strength(3.0F * factor, 9.0F * factor);
    }

    /**
     * Properties for a transparent ashen block, such as glass.
     */
    private static Properties ashenNonSolidProps(SoundType sound) {
        return structureNonSolid(MapColor.COLOR_PURPLE, sound).strength(3.0F, 9.0F);
    }

    private static Properties structureProps(MapColor color, SoundType sound) {
        return builder(color, sound).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().isValidSpawn(SearedBlock.VALID_SPAWN);
    }

    /**
     * Properties for transparent smeltery or foundry blocks, such as glass.
     */
    private static Properties structureNonSolid(MapColor color, SoundType sound) {
        return structureProps(color, sound).isValidSpawn((state, level, pos, entityType) -> false).isRedstoneConductor(NEVER).isSuffocating(NEVER)
                                           .isViewBlocking(NEVER).noOcclusion();
    }
}
