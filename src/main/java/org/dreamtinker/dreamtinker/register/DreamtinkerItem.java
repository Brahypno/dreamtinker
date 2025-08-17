package org.dreamtinker.dreamtinker.register;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.dreamtinker.dreamtinker.Item.antimony.nigrescence_antimony;
import org.dreamtinker.dreamtinker.Item.antimony.star_regulus;
import org.dreamtinker.dreamtinker.Item.antimony.valentinite;
import org.dreamtinker.dreamtinker.tools.Masu.Masu;
import org.dreamtinker.dreamtinker.tools.TNTarrow.TNTarrow;
import org.dreamtinker.dreamtinker.tools.toolsDefinition;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import static org.dreamtinker.dreamtinker.Dreamtinker.MODID;


public class DreamtinkerItem {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    private static final Item.Properties TOOL = (new Item.Properties()).stacksTo(1);
    private static final Item.Properties PART = (new Item.Properties()).stacksTo(64);

    public static final RegistryObject<ModifiableItem> tntarrow = ITEMS.register("tntarrow", () -> new TNTarrow((new Item.Properties()).stacksTo(4), toolsDefinition.TNTARROW, 4));
    public static final RegistryObject<ModifiableItem> masu = ITEMS.register("masu", () -> new Masu(TOOL, toolsDefinition.MASU));

    public static final RegistryObject<ToolPartItem> explode_core = ITEMS.register("explode_core", () -> new ToolPartItem(PART, HeadMaterialStats.ID));

    //star antimony
    public static final RegistryObject<Item> raw_stibnite = ITEMS.register("raw_stibnite", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> valentinite = ITEMS.register("valentinite", () -> new valentinite(new Item.Properties()));
    public static final RegistryObject<Item> nigrescence_antimony = ITEMS.register("nigrescence_antimony", () -> new nigrescence_antimony(new Item.Properties()));
    public static final RegistryObject<Item> metallivorous_stibium_lupus = ITEMS.register("metallivorous_stibium_lupus", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> regulus = ITEMS.register("star_regulus", () -> new star_regulus(new Item.Properties()));

    public static void addTabItems(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {
        output.accept(raw_stibnite.get());
        output.accept(valentinite.get());
        output.accept(nigrescence_antimony.get());
        output.accept(metallivorous_stibium_lupus.get());
        output.accept(regulus.get());

    }


}

