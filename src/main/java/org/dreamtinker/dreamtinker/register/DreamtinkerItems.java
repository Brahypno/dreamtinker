package org.dreamtinker.dreamtinker.register;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.dreamtinker.dreamtinker.Items.antimony.nigrescence_antimony;
import org.dreamtinker.dreamtinker.Items.antimony.star_regulus;
import org.dreamtinker.dreamtinker.Items.antimony.valentinite;
import org.dreamtinker.dreamtinker.Items.tools.DTtoolsDefinition;
import org.dreamtinker.dreamtinker.Items.tools.MaShuo.MaShou;
import org.dreamtinker.dreamtinker.Items.tools.NarcissusWing.NarcissusWing;
import org.dreamtinker.dreamtinker.Items.tools.TNTarrow.TNTarrow;
import org.dreamtinker.dreamtinker.Items.tools.UnderArmor.UnderArmorItem;
import org.dreamtinker.dreamtinker.Items.void_pearl;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.registration.ItemDeferredRegisterExtension;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;
import slimeknights.tconstruct.tools.stats.LimbMaterialStats;

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
    public static final ItemObject<ModifiableItem> mashou = MODI_TOOLS.register("mashou", () -> new MaShou(TOOL, DTtoolsDefinition.MASU));
    public static final ItemObject<ModifiableItem> narcissus_wing =
            MODI_TOOLS.register("narcissus_wing", () -> new NarcissusWing(TOOL.rarity(Rarity.EPIC), DTtoolsDefinition.narcissus_wing));
    public static final EnumObject<ArmorItem.Type, UnderArmorItem> underPlate =
            MODI_TOOLS.registerEnum("under_plate", ArmorItem.Type.values(), type -> new UnderArmorItem(DTtoolsDefinition.UNDER_PLATE, type, TOOL));

    public static final RegistryObject<ToolPartItem> explode_core = ITEMS.register("explode_core", () -> new ToolPartItem(PART, HeadMaterialStats.ID));
    public static final RegistryObject<ToolPartItem> memoryOrthant =
            ITEMS.register("memory_orthant", () -> new ToolPartItem(PART.rarity(Rarity.RARE), HeadMaterialStats.ID));
    public static final RegistryObject<ToolPartItem> wishOrthant =
            ITEMS.register("wish_orthant", () -> new ToolPartItem(PART, HandleMaterialStats.ID) {
                @Override
                public boolean isFoil(@NotNull ItemStack stack) {return true;}
            });
    public static final RegistryObject<ToolPartItem> soulOrthant =
            ITEMS.register("soul_orthant", () -> new ToolPartItem(PART, LimbMaterialStats.ID));
    public static final RegistryObject<ToolPartItem> personaOrthant =
            ITEMS.register("persona_orthant", () -> new ToolPartItem(PART.rarity(Rarity.RARE), LimbMaterialStats.ID));
    public static final RegistryObject<ToolPartItem> reasonEmanation =
            ITEMS.register("reason_emanation", () -> new ToolPartItem(PART.rarity(Rarity.RARE), HeadMaterialStats.ID));

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

    public static final RegistryObject<Item> twist_obsidian_pane =
            ITEMS.register("twist_obsidian_pane", () -> new Item(new Item.Properties().rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> memory_cast = ITEMS.register("memory_cast", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)) {
        public void appendHoverText(@NotNull ItemStack stack, Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
            tooltip.add(Component.translatable("tooltip.dreamtinker.memory_cast_1").withStyle(ChatFormatting.AQUA));
            tooltip.add(Component.translatable("tooltip.dreamtinker.memory_cast_2").withStyle(ChatFormatting.GREEN));
            super.appendHoverText(stack, level, tooltip, flag);
        }
    });
    public static final RegistryObject<Item> wish_cast = ITEMS.register("wish_cast", () -> new Item(new Item.Properties().rarity(Rarity.RARE)) {
        public void appendHoverText(@NotNull ItemStack stack, Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
            tooltip.add(Component.translatable("tooltip.dreamtinker.wish_cast_1").withStyle(ChatFormatting.GREEN));
            tooltip.add(Component.translatable("tooltip.dreamtinker.wish_cast_2").withStyle(ChatFormatting.DARK_RED));
            super.appendHoverText(stack, level, tooltip, flag);
        }
    });
    public static final RegistryObject<Item> soul_cast = ITEMS.register("soul_cast", () -> new Item(new Item.Properties().rarity(Rarity.RARE)) {
        public void appendHoverText(@NotNull ItemStack stack, Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
            tooltip.add(Component.translatable("tooltip.dreamtinker.soul_cast_1").withStyle(ChatFormatting.DARK_RED));
            tooltip.add(Component.translatable("tooltip.dreamtinker.soul_cast_2").withStyle(ChatFormatting.DARK_RED));
            if (!(null != stack.getTag() && stack.getTag().getBoolean("love"))){
                tooltip.add(Component.translatable("tooltip.dreamtinker.soul_cast_3").withStyle(ChatFormatting.DARK_GRAY));
                tooltip.add(Component.translatable("tooltip.dreamtinker.soul_cast_4").withStyle(ChatFormatting.GOLD));
            }
            if (!(null != stack.getTag() && stack.getTag().getBoolean("desire"))){
                tooltip.add(Component.translatable("tooltip.dreamtinker.soul_cast_5").withStyle(ChatFormatting.GREEN));
                //tooltip.add(Component.translatable("tooltip.dreamtinker.soul_cast_6").withStyle(ChatFormatting.DARK_RED));
            }
            if (null != stack.getTag() && stack.getTag().getBoolean("love"))
                tooltip.add(Component.translatable("tooltip.dreamtinker.soul_cast_7").withStyle(ChatFormatting.DARK_RED));
            if (null != stack.getTag() && stack.getTag().getBoolean("desire"))
                tooltip.add(Component.translatable("tooltip.dreamtinker.soul_cast_8").withStyle(ChatFormatting.DARK_RED));
            super.appendHoverText(stack, level, tooltip, flag);
        }
    });
    public static final RegistryObject<Item> persona_cast = ITEMS.register("persona_cast", () -> new Item(new Item.Properties().rarity(Rarity.RARE)) {
        public void appendHoverText(@NotNull ItemStack stack, Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
            tooltip.add(Component.translatable("tooltip.dreamtinker.persona_cast_1").withStyle(ChatFormatting.DARK_PURPLE));
            tooltip.add(Component.translatable("tooltip.dreamtinker.persona_cast_2").withStyle(ChatFormatting.DARK_RED));
            super.appendHoverText(stack, level, tooltip, flag);
        }
    });
    public static final RegistryObject<Item> reason_cast = ITEMS.register("reason_cast", () -> new Item(new Item.Properties().rarity(Rarity.RARE)) {
        public void appendHoverText(@NotNull ItemStack stack, Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
            tooltip.add(Component.translatable("tooltip.dreamtinker.reason_cast_1").withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.translatable("tooltip.dreamtinker.reason_cast_2").withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.translatable("tooltip.dreamtinker.reason_cast_3").withStyle(ChatFormatting.GOLD));
            super.appendHoverText(stack, level, tooltip, flag);
        }
    });

    public static final RegistryObject<Item> white_peach = ITEMS.register("white_peach", () -> new Item(
            new Item.Properties().rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(2).saturationMod(6F).build())) {
        public void appendHoverText(@NotNull ItemStack stack, Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
            tooltip.add(Component.translatable("tooltip.dreamtinker.white_peach").withStyle(s -> s.withColor(TextColor.fromRgb(0xFFB6C1))));
            super.appendHoverText(stack, level, tooltip, flag);
        }
    });
    public static final RegistryObject<Item> unborn_egg = ITEMS.register("unborn_egg", () -> new Item(new Item.Properties().rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> unborn_turtle_egg =
            ITEMS.register("unborn_turtle_egg", () -> new Item(new Item.Properties().rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> unborn_sniffer_egg =
            ITEMS.register("unborn_sniffer_egg", () -> new Item(new Item.Properties().rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> unborn_dragon_egg =
            ITEMS.register("unborn_dragon_egg", () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> unborn_spawn_egg = ITEMS.register("unborn_spawn_egg", () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));

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

    public static void addTabItems(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {
        output.accept(raw_stibnite.get());
        output.accept(valentinite.get());
        output.accept(nigrescence_antimony.get());
        output.accept(metallivorous_stibium_lupus.get());
        output.accept(regulus.get());
        output.accept(void_pearl.get());
        output.accept(twist_obsidian_pane.get());
        output.accept(memory_cast.get());
        output.accept(wish_cast.get());
        output.accept(soul_cast.get());
        output.accept(persona_cast.get());
        output.accept(reason_cast.get());
        output.accept(white_peach.get());
        output.accept(unborn_egg.get());
        output.accept(unborn_turtle_egg.get());
        output.accept(unborn_dragon_egg.get());
        output.accept(unborn_sniffer_egg.get());
        output.accept(unborn_spawn_egg.get());
        if (ModList.get().isLoaded("enigmaticlegacy"))
            output.accept(soul_etherium.get());
    }


}

