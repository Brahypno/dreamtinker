package org.dreamtinker.dreamtinker.register;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.dreamtinker.dreamtinker.tools.Masu.Masu;
import org.dreamtinker.dreamtinker.tools.TNTarrow.TNTarrow;
import org.dreamtinker.dreamtinker.tools.toolsDefinition;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import static org.dreamtinker.dreamtinker.Dreamtinker.MODID;


public class DreamtinkerItem {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    private static final Item.Properties TOOL = (new Item.Properties()).tab(DreamtinkerTab.TOOL).stacksTo(1);
    private static final Item.Properties PART = (new Item.Properties()).tab(DreamtinkerTab.PART).stacksTo(64);

    public static final RegistryObject<ModifiableItem> tntarrow = ITEMS.register("tntarrow", () -> new TNTarrow(TOOL,toolsDefinition.TNTARROW));
    public static final RegistryObject<ModifiableItem> masu = ITEMS.register("masu", () -> new Masu(TOOL,toolsDefinition.MASU));

    public static final RegistryObject<ToolPartItem> explode_core = ITEMS.register("explode_core", () -> new ToolPartItem(PART, HeadMaterialStats.ID));
}

