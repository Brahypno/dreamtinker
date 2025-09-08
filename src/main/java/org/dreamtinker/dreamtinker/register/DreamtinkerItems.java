package org.dreamtinker.dreamtinker.register;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.dreamtinker.dreamtinker.Item.antimony.nigrescence_antimony;
import org.dreamtinker.dreamtinker.Item.antimony.star_regulus;
import org.dreamtinker.dreamtinker.Item.antimony.valentinite;
import org.dreamtinker.dreamtinker.Item.void_pearl;
import org.dreamtinker.dreamtinker.tools.DTtoolsDefinition;
import org.dreamtinker.dreamtinker.tools.Masu.Masu;
import org.dreamtinker.dreamtinker.tools.TNTarrow.TNTarrow;
import org.dreamtinker.dreamtinker.tools.UnderArmor.UnderArmorItem;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.registration.ItemDeferredRegisterExtension;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import java.util.List;

import static org.dreamtinker.dreamtinker.Dreamtinker.MODID;


public class DreamtinkerItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<Item> EL_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final ItemDeferredRegisterExtension MODI_TOOLS = new ItemDeferredRegisterExtension(MODID);

    private static final Item.Properties TOOL = (new Item.Properties()).stacksTo(1);
    private static final Item.Properties PART = (new Item.Properties()).stacksTo(64);

    public static final ItemObject<ModifiableItem> tntarrow =
            MODI_TOOLS.register("tntarrow", () -> new TNTarrow((new Item.Properties()).stacksTo(4), DTtoolsDefinition.TNTARROW, 4));
    public static final ItemObject<ModifiableItem> masu = MODI_TOOLS.register("masu", () -> new Masu(TOOL, DTtoolsDefinition.MASU));
    public static final EnumObject<ArmorItem.Type, UnderArmorItem> underPlate =
            MODI_TOOLS.registerEnum("under_plate", ArmorItem.Type.values(), type -> new UnderArmorItem(DTtoolsDefinition.UNDER_PLATE, type, TOOL));

    public static final RegistryObject<ToolPartItem> explode_core = ITEMS.register("explode_core", () -> new ToolPartItem(PART, HeadMaterialStats.ID));

    //star antimony
    public static final RegistryObject<Item> raw_stibnite = ITEMS.register("raw_stibnite", () -> new Item(new Item.Properties().rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> valentinite = ITEMS.register("valentinite", () -> new valentinite(new Item.Properties().rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> nigrescence_antimony =
            ITEMS.register("nigrescence_antimony", () -> new nigrescence_antimony(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> metallivorous_stibium_lupus =
            ITEMS.register("metallivorous_stibium_lupus", () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> regulus = ITEMS.register("star_regulus", () -> new star_regulus(new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> void_pearl = ITEMS.register("void_pearl", () -> new void_pearl((new Item.Properties().rarity(
            Rarity.UNCOMMON)).stacksTo(16)));
    public static final RegistryObject<Item> soul_etherium = EL_ITEMS.register(
            "soul_etherium",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC)) {
                public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
                    tooltip.add(Component.translatable("tooltip.enigmaticlegacy.worthyOnesOnly1"));
                    tooltip.add(Component.translatable("tooltip.enigmaticlegacy.worthyOnesOnly2"));
                    tooltip.add(Component.translatable("tooltip.enigmaticlegacy.worthyOnesOnly3"));
                    super.appendHoverText(stack, level, tooltip, flag);
                }
            }
    );
    public static final RegistryObject<Item> twist_obsidian_pane =
            ITEMS.register("twist_obsidian_pane", () -> new Item(new Item.Properties().rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> persona_cast = ITEMS.register("persona_cast", () -> new Item(new Item.Properties().rarity(Rarity.RARE)));

    public static void addTabItems(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {
        output.accept(raw_stibnite.get());
        output.accept(valentinite.get());
        output.accept(nigrescence_antimony.get());
        output.accept(metallivorous_stibium_lupus.get());
        output.accept(regulus.get());
        output.accept(void_pearl.get());
        output.accept(twist_obsidian_pane.get());
        output.accept(persona_cast.get());
        if (ModList.get().isLoaded("enigmaticlegacy"))
            output.accept(soul_etherium.get());
    }


}

