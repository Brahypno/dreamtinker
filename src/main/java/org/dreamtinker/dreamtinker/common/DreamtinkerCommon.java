package org.dreamtinker.dreamtinker.common;

import net.minecraft.ChatFormatting;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.RegistryObject;
import org.dreamtinker.dreamtinker.DreamtinkerModule;
import org.dreamtinker.dreamtinker.common.Items.star_regulus;
import org.dreamtinker.dreamtinker.common.Items.valentinite;
import org.dreamtinker.dreamtinker.common.Items.void_pearl;
import org.dreamtinker.dreamtinker.common.data.model.DreamTinkerBlockStateProvider;
import org.dreamtinker.dreamtinker.common.data.model.DreamtinkerItemModelProvider;
import org.dreamtinker.dreamtinker.common.data.render.RenderFluidProvider;
import org.dreamtinker.dreamtinker.fluids.DreamtinkerFluids;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.item.ContainerFoodItem;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.shared.block.BetterPaneBlock;

import java.util.List;
import java.util.function.Function;

import static org.dreamtinker.dreamtinker.Dreamtinker.MODID;

public class DreamtinkerCommon extends DreamtinkerModule {
    public static final RegistryObject<CreativeModeTab> ITEM =
            TABS.register("ore", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup." + MODID + ".item")).icon(() -> new ItemStack(
                    DreamtinkerCommon.metallivorous_stibium_lupus.get())).displayItems(DreamtinkerCommon::addTabs).build());
    protected static final Item.Properties ITEM_PROPS = new Item.Properties();
    //star antimony
    public static final RegistryObject<Item> raw_stibnite = ITEMS.register("raw_stibnite", () -> new Item(ITEM_PROPS.rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> valentinite = ITEMS.register("valentinite", () -> new valentinite(ITEM_PROPS.rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> nigrescence_antimony =
            ITEMS.register("nigrescence_antimony", () -> new Item(ITEM_PROPS.rarity(Rarity.UNCOMMON)) {
                public void appendHoverText(@NotNull ItemStack stack, Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
                    tooltip.add(Component.translatable("tooltip.nigrescence_antimony.desc1").withStyle(ChatFormatting.DARK_GRAY));
                    tooltip.add(Component.translatable("tooltip.nigrescence_antimony.desc2").withStyle(ChatFormatting.BLACK));
                    super.appendHoverText(stack, level, tooltip, flag);
                }
            });
    public static final RegistryObject<Item> metallivorous_stibium_lupus =
            ITEMS.register("metallivorous_stibium_lupus", () -> new Item(ITEM_PROPS.rarity(Rarity.RARE)));
    public static final RegistryObject<Item> regulus = ITEMS.register("star_regulus", () -> new star_regulus(ITEM_PROPS.rarity(Rarity.EPIC)));


    public static final RegistryObject<Item> twist_obsidian_pane =
            ITEMS.register("twist_obsidian_pane", () -> new Item(ITEM_PROPS.rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> memory_cast = ITEMS.register("memory_cast", () -> new Item(ITEM_PROPS.rarity(Rarity.UNCOMMON)) {
        public void appendHoverText(@NotNull ItemStack stack, Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
            tooltip.add(Component.translatable("tooltip.dreamtinker.memory_cast_1").withStyle(ChatFormatting.AQUA));
            tooltip.add(Component.translatable("tooltip.dreamtinker.memory_cast_2").withStyle(ChatFormatting.GREEN));
            super.appendHoverText(stack, level, tooltip, flag);
        }
    });
    public static final RegistryObject<Item> wish_cast = ITEMS.register("wish_cast", () -> new Item(ITEM_PROPS.rarity(Rarity.RARE)) {
        public void appendHoverText(@NotNull ItemStack stack, Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
            tooltip.add(Component.translatable("tooltip.dreamtinker.wish_cast_1").withStyle(ChatFormatting.GREEN));
            tooltip.add(Component.translatable("tooltip.dreamtinker.wish_cast_2").withStyle(ChatFormatting.DARK_RED));
            super.appendHoverText(stack, level, tooltip, flag);
        }
    });
    public static final RegistryObject<Item> soul_cast = ITEMS.register("soul_cast", () -> new Item(ITEM_PROPS.rarity(Rarity.RARE)) {
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
    public static final RegistryObject<Item> persona_cast = ITEMS.register("persona_cast", () -> new Item(ITEM_PROPS.rarity(Rarity.RARE)) {
        public void appendHoverText(@NotNull ItemStack stack, Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
            tooltip.add(Component.translatable("tooltip.dreamtinker.persona_cast_1").withStyle(ChatFormatting.DARK_PURPLE));
            tooltip.add(Component.translatable("tooltip.dreamtinker.persona_cast_2").withStyle(ChatFormatting.DARK_RED));
            super.appendHoverText(stack, level, tooltip, flag);
        }
    });
    public static final RegistryObject<Item> reason_cast = ITEMS.register("reason_cast", () -> new Item(ITEM_PROPS.rarity(Rarity.RARE)) {
        public void appendHoverText(@NotNull ItemStack stack, Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
            tooltip.add(Component.translatable("tooltip.dreamtinker.reason_cast_1").withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.translatable("tooltip.dreamtinker.reason_cast_2").withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.translatable("tooltip.dreamtinker.reason_cast_3").withStyle(ChatFormatting.GOLD));
            super.appendHoverText(stack, level, tooltip, flag);
        }
    });

    public static final RegistryObject<Item> unborn_egg = ITEMS.register("unborn_egg", () -> new Item(ITEM_PROPS.rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> unborn_turtle_egg =
            ITEMS.register("unborn_turtle_egg", () -> new Item(ITEM_PROPS.rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> unborn_sniffer_egg =
            ITEMS.register("unborn_sniffer_egg", () -> new Item(ITEM_PROPS.rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> unborn_dragon_egg =
            ITEMS.register("unborn_dragon_egg", () -> new Item(ITEM_PROPS.rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> unborn_spawn_egg = ITEMS.register("unborn_spawn_egg", () -> new Item(ITEM_PROPS.rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> soul_steel =
            ITEMS.register("soul_steel", () -> new Item(ITEM_PROPS.rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> black_sapphire =
            ITEMS.register("black_sapphire", () -> new Item(ITEM_PROPS.rarity(Rarity.COMMON)));

    public static final RegistryObject<Item> deep_prismarine_shard = ITEMS.register("deep_prismarine_shard", () -> new Item(ITEM_PROPS.rarity(Rarity.COMMON)));

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
    public static final RegistryObject<Item> echo_alloy = ITEMS.register("echo_alloy", () -> new Item(ITEM_PROPS.rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> malignant_gluttony = MALUM_ITEMS.register("malignant_gluttony", () -> new Item(ITEM_PROPS.rarity(Rarity.EPIC)) {
        public void appendHoverText(@NotNull ItemStack stack, Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
            tooltip.add(Component.translatable("tooltip.dreamtinker.malignant_gluttony_1").withStyle(ChatFormatting.BLACK));
            tooltip.add(Component.translatable("tooltip.dreamtinker.malignant_gluttony_2").withStyle(ChatFormatting.AQUA));
            tooltip.add(Component.translatable("tooltip.dreamtinker.malignant_gluttony_3").withStyle(ChatFormatting.DARK_PURPLE));
            super.appendHoverText(stack, level, tooltip, flag);
        }
    });
    public static final RegistryObject<Item> larimar = ITEMS.register("larimar", () -> new Item(ITEM_PROPS.rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> amber = ITEMS.register("amber", () -> new Item(ITEM_PROPS.rarity(Rarity.RARE)));
    public static final RegistryObject<Item> despair_gem = ITEMS.register("despair_gem", () -> new Item(ITEM_PROPS.rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> desire_gem = ITEMS.register("desire_gem", () -> new Item(ITEM_PROPS.rarity(Rarity.RARE)));
    public static final RegistryObject<Item> rainbow_honey_crystal = ITEMS.register("rainbow_honey_crystal", () -> new Item(ITEM_PROPS));
    public static final RegistryObject<Item> poisonousHomunculus =
            ITEMS.register("poisonous_homunculus", () -> new ContainerFoodItem.FluidContainerFoodItem(
                    new Item.Properties().craftRemainder(Items.GLASS_BOTTLE),
                    () -> new FluidStack(DreamtinkerFluids.half_festering_blood.get(), FluidValues.BOTTLE)));
    public static final RegistryObject<Item> evilHomunculus =
            ITEMS.register("evil_homunculus", () -> new ContainerFoodItem.FluidContainerFoodItem(
                    new Item.Properties().craftRemainder(Items.GLASS_BOTTLE),
                    () -> new FluidStack(DreamtinkerFluids.festering_blood.get(), FluidValues.BOTTLE)));
    public static final RegistryObject<Item> rainbow_honey =
            ITEMS.register("rainbow_honey", () -> new ContainerFoodItem.FluidContainerFoodItem(
                    new Item.Properties().craftRemainder(Items.GLASS_BOTTLE),
                    () -> new FluidStack(DreamtinkerFluids.rainbow_honey.get(), FluidValues.BOTTLE)));
    public static final RegistryObject<Item> scolecite = ITEMS.register("scolecite", () -> new Item(ITEM_PROPS.rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> shiningFlint = ITEMS.register("shining_flint", () -> new Item(ITEM_PROPS.rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> orichalcum = ITEMS.register("orichalcum_ingot", () -> new Item(ITEM_PROPS.rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> raw_orichalcum = ITEMS.register("raw_orichalcum", () -> new Item(ITEM_PROPS.rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> orichalcum_nugget = ITEMS.register("orichalcum_nugget", () -> new Item(ITEM_PROPS.rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> cold_iron_ingot = ITEMS.register("cold_iron_ingot", () -> new Item(ITEM_PROPS.rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> raw_cold_iron = ITEMS.register("raw_cold_iron", () -> new Item(ITEM_PROPS.rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> cold_iron_nugget = ITEMS.register("cold_iron_nugget", () -> new Item(ITEM_PROPS.rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> shadow_silver_ingot = ITEMS.register("shadow_silver_ingot", () -> new Item(ITEM_PROPS.rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> raw_shadow_silver = ITEMS.register("raw_shadow_silver", () -> new Item(ITEM_PROPS.rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> shadow_silver_nugget = ITEMS.register("shadow_silver_nugget", () -> new Item(ITEM_PROPS.rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> transmutation_gold_ingot =
            ITEMS.register("transmutation_gold_ingot", () -> new Item(ITEM_PROPS.rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> raw_transmutation_gold =
            ITEMS.register("raw_transmutation_gold", () -> new Item(ITEM_PROPS.rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> transmutation_gold_nugget =
            ITEMS.register("transmutation_gold_nugget", () -> new Item(ITEM_PROPS.rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> transmutation_gold_dust =
            ITEMS.register("transmutation_gold_dust", () -> new Item(ITEM_PROPS.rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> fox_fur =
            ITEMS.register("fox_fur", () -> new Item(ITEM_PROPS.rarity(Rarity.COMMON)) {
                public void appendHoverText(@NotNull ItemStack stack, Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
                    tooltip.add(Component.translatable("tooltip.dreamtinker.fox_fur_1").withStyle(s -> s.withColor(TextColor.fromRgb(0xFFB6C1))));
                    super.appendHoverText(stack, level, tooltip, flag);
                }
            });

    protected static final Item.Properties FOOD_PROPS = new Item.Properties();
    public static final RegistryObject<Item> white_peach = ITEMS.register("white_peach", () -> new Item(
            FOOD_PROPS.rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(2).saturationMod(6F).build())) {
        public void appendHoverText(@NotNull ItemStack stack, Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
            tooltip.add(Component.translatable("tooltip.dreamtinker.white_peach").withStyle(s -> s.withColor(TextColor.fromRgb(0xFFB6C1))));
            super.appendHoverText(stack, level, tooltip, flag);
        }
    });
    public static final RegistryObject<Item> void_pearl = ITEMS.register("void_pearl", () -> new void_pearl((new Item.Properties().rarity(
            Rarity.UNCOMMON)).stacksTo(16)));

    public static void addTabItems(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {
        output.accept(echo_alloy.get());
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
        output.accept(larimar.get());
        output.accept(amber.get());
        output.accept(despair_gem.get());
        output.accept(desire_gem.get());
        output.accept(poisonousHomunculus.get());
        output.accept(evilHomunculus.get());
        output.accept(soul_steel.get());
        output.accept(rainbow_honey.get());
        output.accept(rainbow_honey_crystal.get());
        output.accept(black_sapphire.get());
        output.accept(scolecite.get());
        output.accept(shiningFlint.get());
        output.accept(orichalcum_nugget.get());
        output.accept(orichalcum.get());
        output.accept(raw_orichalcum.get());
        output.accept(cold_iron_nugget.get());
        output.accept(cold_iron_ingot.get());
        output.accept(raw_cold_iron.get());
        output.accept(shadow_silver_nugget.get());
        output.accept(shadow_silver_ingot.get());
        output.accept(raw_shadow_silver.get());
        output.accept(transmutation_gold_dust.get());
        output.accept(transmutation_gold_nugget.get());
        output.accept(transmutation_gold_ingot.get());
        output.accept(raw_transmutation_gold.get());
        if (ModList.get().isLoaded("malum"))
            output.accept(malignant_gluttony.get());
        if (ModList.get().isLoaded("enigmaticlegacy"))
            output.accept(soul_etherium.get());
    }

    protected static final Function<Block, ? extends BlockItem> BLOCK_ITEM = (b) -> new BlockItem(b, new Item.Properties());
    public static final ItemObject<Block> metallivorous_stibium_lupus_block = BLOCKS.register("metallivorous_stibium_lupus_block", () -> new Block(
            builder(MapColor.METAL, SoundType.METAL).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(30f, 35f)), BLOCK_ITEM);

    public static final ItemObject<BetterPaneBlock> crying_obsidian_plane = BLOCKS.register("crying_obsidian_pane", () -> new BetterPaneBlock(
            builder(MapColor.COLOR_BLACK, SoundType.STONE).requiresCorrectToolForDrops().instrument(NoteBlockInstrument.BASEDRUM).noOcclusion()
                                                          .strength(25.0F, 400.0F)), BLOCK_ITEM);
    public static final ItemObject<FlowerBlock> narcissus = BLOCKS.register("narcissus", () -> new FlowerBlock(
            () -> MobEffects.NIGHT_VISION, 5, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission()
                                                                       .instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ)
                                                                       .pushReaction(PushReaction.DESTROY)) {
    }, BLOCK_ITEM);
    public static final ItemObject<FlowerPotBlock> potted_narcissus = BLOCKS.register("potted_narcissus", () -> new FlowerPotBlock(
            () -> (FlowerPotBlock) Blocks.FLOWER_POT, narcissus, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission()
                                                                                          .instabreak().sound(SoundType.STONE)
                                                                                          .offsetType(BlockBehaviour.OffsetType.XZ)
                                                                                          .pushReaction(PushReaction.DESTROY)), BLOCK_ITEM);
    public static final ItemObject<Block> larimarOre =
            BLOCKS.register("larimar_ore", () -> new Block(
                                    builder(MapColor.LAPIS, SoundType.LODESTONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(5.0F, 4.0f)),
                            BLOCK_ITEM);

    public static final ItemObject<RotatedPillarBlock> amberOre = BLOCKS.register("amber_ore", () -> new RotatedPillarBlock(
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops()
                                     .strength(1.25F, 4.2F).sound(SoundType.BASALT)), BLOCK_ITEM);
    public static final ItemObject<Block> blackSapphireOre =
            BLOCKS.register("black_sapphire_ore",
                            () -> new Block(
                                    builder(MapColor.STONE, SoundType.LODESTONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops()
                                                                                .strength(9.0F, 9.0f)), BLOCK_ITEM);
    public static final ItemObject<RotatedPillarBlock> scoleciteOre = BLOCKS.register("scolecite_ore", () -> new RotatedPillarBlock(
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops()
                                     .strength(0.25F, 2.2F).sound(SoundType.BASALT).lightLevel(state -> 12)), BLOCK_ITEM);

    public static final ItemObject<Block> soulSteelBlock =
            BLOCKS.register("soul_steel_block", () -> new Block(
                    builder(MapColor.COLOR_BLACK, SoundType.METAL).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops()
                                                                  .strength(10.5f, 12.5f)), BLOCK_ITEM);

    public static final ItemObject<Block> coldIronOre = BLOCKS.register("cold_iron_ore", () -> new Block(
            builder(MapColor.STONE, SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(5, 6f)), BLOCK_ITEM);
    public static final ItemObject<Block> DeepslateColdIronOre =
            BLOCKS.register("deepslate_cold_iron_ore",
                            () -> new Block(builder(MapColor.DEEPSLATE, SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops()
                                                                                        .strength(6.5f, 7f)), BLOCK_ITEM);

    public static final ItemObject<Block> RawColdIronBlock = BLOCKS.register("raw_cold_iron_block", () -> new Block(
            builder(MapColor.METAL, SoundType.METAL).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(18f, 18f)), BLOCK_ITEM);
    public static final ItemObject<Block> ColdIronBlock = BLOCKS.register("cold_iron_block", () -> new Block(
            builder(MapColor.METAL, SoundType.METAL).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(20f, 20f)), BLOCK_ITEM);

    public static final ItemObject<Block> OrichalcumOre = BLOCKS.register("orichalcum_ore", () -> new Block(
            builder(MapColor.STONE, SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(9, 9f)), BLOCK_ITEM);
    public static final ItemObject<Block> DeepslateOrichalcumOre =
            BLOCKS.register("deepslate_orichalcum_ore",
                            () -> new Block(builder(MapColor.DEEPSLATE, SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops()
                                                                                        .strength(13.5f, 13.5f)), BLOCK_ITEM);
    public static final ItemObject<Block> RawOrichalcumBlock = BLOCKS.register("raw_orichalcum_block", () -> new Block(
            builder(MapColor.METAL, SoundType.METAL).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(25f, 25f)), BLOCK_ITEM);
    public static final ItemObject<Block> OrichalcumBlock = BLOCKS.register("orichalcum_block", () -> new Block(
            builder(MapColor.METAL, SoundType.METAL).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(25f, 25f)), BLOCK_ITEM);

    public static final ItemObject<Block> ShadowSilverOre = BLOCKS.register("shadow_silver_ore", () -> new Block(
            builder(MapColor.STONE, SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(12, 12f)), BLOCK_ITEM);
    public static final ItemObject<Block> DeepslateShadowSilverOre =
            BLOCKS.register("deepslate_shadow_silver_ore",
                            () -> new Block(builder(MapColor.DEEPSLATE, SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops()
                                                                                        .strength(15f, 15f)), BLOCK_ITEM);
    public static final ItemObject<Block> RawShadowSilverBlock = BLOCKS.register("raw_shadow_silver_block", () -> new Block(
            builder(MapColor.METAL, SoundType.METAL).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(30f, 30f)), BLOCK_ITEM);
    public static final ItemObject<Block> ShadowSilverBlock = BLOCKS.register("shadow_silver_block", () -> new Block(
            builder(MapColor.METAL, SoundType.METAL).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(30f, 30f)), BLOCK_ITEM);

    public static final ItemObject<Block> TransmutationGoldOre = BLOCKS.register("transmutation_gold_ore", () -> new Block(
            builder(MapColor.STONE, SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(16, 16f)), BLOCK_ITEM);
    public static final ItemObject<Block> DeepslateTransmutationGoldOre =
            BLOCKS.register("deepslate_transmutation_gold_ore",
                            () -> new Block(builder(MapColor.DEEPSLATE, SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops()
                                                                                        .strength(18f, 18f)), BLOCK_ITEM);
    public static final ItemObject<Block> RawTransmutationGoldBlock = BLOCKS.register("raw_transmutation_gold_block", () -> new Block(
            builder(MapColor.METAL, SoundType.METAL).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(36f, 36f)), BLOCK_ITEM);
    public static final ItemObject<Block> TransmutationGoldBlock = BLOCKS.register("transmutation_gold_block", () -> new Block(
            builder(MapColor.METAL, SoundType.METAL).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(36f, 36f)), BLOCK_ITEM);

    public static void addTabBlocks(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {
        output.accept(metallivorous_stibium_lupus_block.get());
        output.accept(crying_obsidian_plane.get());
        output.accept(narcissus.get());
        output.accept(larimarOre.get());
        output.accept(amberOre);
        output.accept(blackSapphireOre);
        output.accept(scoleciteOre);
        output.accept(soulSteelBlock);
        output.accept(coldIronOre);
        output.accept(DeepslateColdIronOre);
        output.accept(RawColdIronBlock);
        output.accept(ColdIronBlock);
        output.accept(OrichalcumOre);
        output.accept(DeepslateOrichalcumOre);
        output.accept(RawOrichalcumBlock);
        output.accept(OrichalcumBlock);
        output.accept(ShadowSilverOre);
        output.accept(DeepslateShadowSilverOre);
        output.accept(RawShadowSilverBlock);
        output.accept(ShadowSilverBlock);
        output.accept(TransmutationGoldOre);
        output.accept(DeepslateTransmutationGoldOre);
        output.accept(RawTransmutationGoldBlock);
        output.accept(TransmutationGoldBlock);
    }


    public static void addTabs(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {
        addTabItems(itemDisplayParameters, output);
        addTabBlocks(itemDisplayParameters, output);
    }

    @SubscribeEvent
    void gatherData(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        boolean client = event.includeClient();

        generator.addProvider(client, new DreamtinkerItemModelProvider(output, existingFileHelper));
        generator.addProvider(client, new DreamTinkerBlockStateProvider(output, existingFileHelper));
        generator.addProvider(client, new RenderFluidProvider(output));
    }
}
