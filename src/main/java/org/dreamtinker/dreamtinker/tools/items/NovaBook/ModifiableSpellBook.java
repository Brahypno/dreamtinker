package org.dreamtinker.dreamtinker.tools.items.NovaBook;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.SpellCaster;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import com.hollingsworth.arsnouveau.api.util.StackUtil;
import com.hollingsworth.arsnouveau.client.registry.ModKeyBindings;
import com.hollingsworth.arsnouveau.common.capability.ANPlayerDataCap;
import com.hollingsworth.arsnouveau.common.capability.IPlayerCap;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.setup.config.ServerConfig;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.client.item.ModifiableItemClientExtension;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.EnchantmentModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.DurabilityDisplayModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.*;
import slimeknights.tconstruct.library.modifiers.modules.build.RarityModule;
import slimeknights.tconstruct.library.tools.IndestructibleItemEntity;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.module.display.ToolNameHook;
import slimeknights.tconstruct.library.tools.definition.module.mining.IsEffectiveToolHook;
import slimeknights.tconstruct.library.tools.definition.module.mining.MiningSpeedToolHook;
import slimeknights.tconstruct.library.tools.helper.*;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerToolActions;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ModifiableSpellBook extends SpellBook implements IModifiableDisplay {
    private final ToolDefinition toolDefinition;
    private ItemStack toolForRendering;

    public ModifiableSpellBook(Properties properties, ToolDefinition toolDefinition) {
        super(properties, SpellTier.ONE);
        this.toolDefinition = toolDefinition;
    }

    public int getMaxStackSize(ItemStack stack) {return 1;}

    public boolean isNotReplaceableByPickAction(ItemStack stack, Player player, int inventorySlot) {
        return true;
    }

    @Nullable
    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return stack.is(TinkerTags.Items.HELD_ARMOR) ? EquipmentSlot.OFFHAND : null;
    }

    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment.isCurse() && super.canApplyAtEnchantingTable(stack, enchantment);
    }

    public int getEnchantmentLevel(ItemStack stack, Enchantment enchantment) {
        return EnchantmentModifierHook.getEnchantmentLevel(stack, enchantment);
    }

    public Map<Enchantment, Integer> getAllEnchantments(ItemStack stack) {
        return EnchantmentModifierHook.getAllEnchantments(stack);
    }

    @Nullable
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ToolCapabilityProvider(stack);
    }

    public void verifyTagAfterLoad(CompoundTag nbt) {
        ToolStack.verifyTag(this, nbt, this.getToolDefinition());
    }

    public void onCraftedBy(ItemStack stack, Level worldIn, Player playerIn) {
        ToolStack.ensureInitialized(stack, this.getToolDefinition());
    }

    public boolean isFoil(ItemStack stack) {
        return ModifierUtil.checkVolatileFlag(stack, SHINY);
    }

    public Rarity getRarity(ItemStack stack) {
        return RarityModule.getRarity(stack);
    }

    public boolean hasCustomEntity(ItemStack stack) {
        return IndestructibleItemEntity.hasCustomEntity(stack);
    }

    @Nullable
    public Entity createEntity(Level world, Entity original, ItemStack stack) {
        return IndestructibleItemEntity.createFrom(world, original, stack);
    }

    public boolean isRepairable(ItemStack stack) {
        return false;
    }

    public boolean isValidRepairItem(ItemStack pToRepair, ItemStack pRepair) {
        return false;
    }

    public boolean canBeDepleted() {
        return false;
    }

    public int getMaxDamage(ItemStack stack) {
        return ToolDamageUtil.getFakeMaxDamage(stack);
    }

    public int getDamage(ItemStack stack) {
        return !this.canBeDepleted() ? 0 : ToolStack.from(stack).getDamage();
    }

    public void setDamage(ItemStack stack, int damage) {
        if (this.canBeDepleted()){
            ToolStack.from(stack).setDamage(damage);
        }

    }

    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T damager, Consumer<T> onBroken) {
        ToolDamageUtil.handleDamageItem(stack, amount, damager, onBroken);
        return 0;
    }

    public boolean isBarVisible(ItemStack stack) {
        return stack.getCount() == 1 && DurabilityDisplayModifierHook.showDurabilityBar(stack);
    }

    public int getBarColor(ItemStack pStack) {
        return DurabilityDisplayModifierHook.getDurabilityRGB(pStack);
    }

    public int getBarWidth(ItemStack pStack) {
        return DurabilityDisplayModifierHook.getDurabilityWidth(pStack);
    }

    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity target) {
        return stack.getCount() > 1 || EntityInteractionModifierHook.leftClickEntity(stack, player, target);
    }

    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(IToolStackView tool, EquipmentSlot slot) {
        return AttributesModifierHook.getHeldAttributeModifiers(tool, slot);
    }

    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        return nbt != null && slot.getType() == EquipmentSlot.Type.HAND ?
               this.getAttributeModifiers((IToolStackView) ToolStack.from(stack), (EquipmentSlot) slot) :
               ImmutableMultimap.of();
    }

    public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
        return this.canPerformAction(stack, TinkerToolActions.SHIELD_DISABLE);
    }

    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return IsEffectiveToolHook.isEffective(ToolStack.from(stack), state);
    }

    public boolean mineBlock(ItemStack stack, Level worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        return ToolHarvestLogic.mineBlock(stack, worldIn, state, pos, entityLiving);
    }

    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return stack.getCount() == 1 ? MiningSpeedToolHook.getDestroySpeed(stack, state) : 0.0F;
    }

    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, Player player) {
        return stack.getCount() > 1 || ToolHarvestLogic.handleBlockBreak(stack, pos, player);
    }

    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        InventoryTickModifierHook.heldInventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    public boolean overrideStackedOnOther(ItemStack held, Slot slot, ClickAction action, Player player) {
        return SlotStackModifierHook.overrideStackedOnOther(held, slot, action, player);
    }

    public boolean overrideOtherStackedOnMe(ItemStack slotStack, ItemStack held, Slot slot, ClickAction action, Player player, SlotAccess access) {
        return SlotStackModifierHook.overrideOtherStackedOnMe(slotStack, held, slot, action, player, access);
    }

    protected static boolean shouldInteract(@Nullable LivingEntity player, ToolStack toolStack, InteractionHand hand) {
        IModDataView volatileData = toolStack.getVolatileData();
        if (volatileData.getBoolean(NO_INTERACTION)){
            return false;
        }else if (hand == InteractionHand.OFF_HAND){
            return true;
        }else {
            return player == null || !volatileData.getBoolean(DEFER_OFFHAND) || player.getOffhandItem().isEmpty();
        }
    }

    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        if (stack.getCount() == 1){
            ToolStack tool = ToolStack.from(stack);
            InteractionHand hand = context.getHand();
            if (shouldInteract(context.getPlayer(), tool, hand)){
                for (ModifierEntry entry : tool.getModifierList()) {
                    InteractionResult result = ((BlockInteractionModifierHook) entry.getHook(ModifierHooks.BLOCK_INTERACT)).beforeBlockUse(tool, entry, context,
                                                                                                                                           InteractionSource.RIGHT_CLICK);
                    if (result.consumesAction()){
                        return result;
                    }
                }
            }
        }

        return InteractionResult.PASS;
    }

    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        if (stack.getCount() == 1){
            ToolStack tool = ToolStack.from(stack);
            InteractionHand hand = context.getHand();
            if (shouldInteract(context.getPlayer(), tool, hand)){
                for (ModifierEntry entry : tool.getModifierList()) {
                    InteractionResult result = ((BlockInteractionModifierHook) entry.getHook(ModifierHooks.BLOCK_INTERACT)).afterBlockUse(tool, entry, context,
                                                                                                                                          InteractionSource.RIGHT_CLICK);
                    if (result.consumesAction()){
                        return result;
                    }
                }
            }
        }

        return InteractionResult.PASS;
    }

    public InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity target, InteractionHand hand) {
        ToolStack tool = ToolStack.from(stack);
        if (shouldInteract(playerIn, tool, hand)){
            for (ModifierEntry entry : tool.getModifierList()) {
                InteractionResult result =
                        ((EntityInteractionModifierHook) entry.getHook(ModifierHooks.ENTITY_INTERACT)).afterEntityUse(tool, entry, playerIn, target, hand,
                                                                                                                      InteractionSource.RIGHT_CLICK);
                if (result.consumesAction()){
                    return result;
                }
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        ToolStack tool = ToolStack.from(stack);
        SpellTier tier = getTier(stack);
        if (tool.isBroken()){
            return InteractionResultHolder.fail(stack);
        }else {
            if (tier != SpellTier.CREATIVE){
                CapabilityRegistry.getMana(playerIn).ifPresent(iMana -> {
                    if (iMana.getBookTier() < tier.value){
                        iMana.setBookTier(tier.value);
                    }
                    IPlayerCap cap = CapabilityRegistry.getPlayerDataCap(playerIn).orElse(new ANPlayerDataCap());
                    if (iMana.getGlyphBonus() < cap.getKnownGlyphs().size()){
                        iMana.setGlyphBonus(cap.getKnownGlyphs().size());
                    }
                });
            }
        }
        ISpellCaster caster = getSpellCaster(stack);
        playerIn.startUsingItem(handIn);

        return caster.castSpell(worldIn, (LivingEntity) playerIn, handIn, Component.translatable("ars_nouveau.invalid_spell"));
    }

    public void onUseTick(Level pLevel, LivingEntity entityLiving, ItemStack stack, int timeLeft) {
        ToolStack tool = ToolStack.from(stack);
        ModifierEntry activeModifier = GeneralInteractionModifierHook.getActiveModifier(tool);
        GeneralInteractionModifierHook hook = (GeneralInteractionModifierHook) activeModifier.getHook(ModifierHooks.GENERAL_INTERACT);
        int duration = hook.getUseDuration(tool, activeModifier);

        for (ModifierEntry entry : tool.getModifiers()) {
            ((UsingToolModifierHook) entry.getHook(ModifierHooks.TOOL_USING)).onUsingTick(tool, entry, entityLiving, duration, timeLeft, activeModifier);
        }

        hook.onUsingTick(tool, activeModifier, entityLiving, timeLeft);
    }

    public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
        if (super.canContinueUsing(oldStack, newStack) && oldStack != newStack){
            GeneralInteractionModifierHook.finishUsing(ToolStack.from(oldStack));
        }

        return super.canContinueUsing(oldStack, newStack);
    }

    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
        ToolStack tool = ToolStack.from(stack);
        ModifierEntry activeModifier = GeneralInteractionModifierHook.getActiveModifier(tool);
        GeneralInteractionModifierHook hook = (GeneralInteractionModifierHook) activeModifier.getHook(ModifierHooks.GENERAL_INTERACT);
        int duration = hook.getUseDuration(tool, activeModifier);

        for (ModifierEntry entry : tool.getModifiers()) {
            ((UsingToolModifierHook) entry.getHook(ModifierHooks.TOOL_USING)).beforeReleaseUsing(tool, entry, entityLiving, duration, 0, activeModifier);
        }

        hook.onFinishUsing(tool, activeModifier, entityLiving);
        return stack;
    }

    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        ToolStack tool = ToolStack.from(stack);
        ModifierEntry activeModifier = GeneralInteractionModifierHook.getActiveModifier(tool);
        GeneralInteractionModifierHook hook = (GeneralInteractionModifierHook) activeModifier.getHook(ModifierHooks.GENERAL_INTERACT);
        int duration = hook.getUseDuration(tool, activeModifier);

        for (ModifierEntry entry : tool.getModifiers()) {
            ((UsingToolModifierHook) entry.getHook(ModifierHooks.TOOL_USING)).beforeReleaseUsing(tool, entry, entityLiving, duration, timeLeft, activeModifier);
        }

        hook.onStoppedUsing(tool, activeModifier, entityLiving, timeLeft);
    }

    public void onStopUsing(ItemStack stack, LivingEntity entity, int timeLeft) {
        ToolStack tool = ToolStack.from(stack);
        UsingToolModifierHook.afterStopUsing(tool, entity, timeLeft);
        GeneralInteractionModifierHook.finishUsing(tool);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return 72000;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return ToolNameHook.getName(getToolDefinition(), stack);
    }

    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return stack.getCount() == 1 && ModifierUtil.canPerformAction(ToolStack.from(stack), toolAction);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("ars_nouveau.spell_book.select",
                                           new Object[]{KeyMapping.createNameSupplier(ModKeyBindings.OPEN_RADIAL_HUD.getName()).get()}));
        tooltip.add(
                Component.translatable("ars_nouveau.spell_book.craft", new Object[]{KeyMapping.createNameSupplier(ModKeyBindings.OPEN_BOOK.getName()).get()}));
        tooltip.add(Component.translatable("tooltip.ars_nouveau.caster_level", new Object[]{this.getTier(stack).value}).setStyle(
                Style.EMPTY.withColor(ChatFormatting.BLUE)));
        TooltipUtil.addInformation(this, stack, level, tooltip, SafeClientAccess.getTooltipKey(), flag);
    }

    public int getDefaultTooltipHideFlags(ItemStack stack) {
        return TooltipUtil.getModifierHideFlags(this.getToolDefinition());
    }

    public @NotNull ItemStack getRenderTool() {
        if (this.toolForRendering == null){
            this.toolForRendering = ToolBuildHandler.buildToolForRendering(this, this.getToolDefinition());
        }

        return this.toolForRendering;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(ModifiableItemClientExtension.INSTANCE);
    }

    public static boolean shouldCauseReequip(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        if (oldStack == newStack){
            return false;
        }else if (!slotChanged && oldStack.getItem() == newStack.getItem()){
            ToolStack oldTool = ToolStack.from(oldStack);
            ToolStack newTool = ToolStack.from(newStack);
            if (!oldTool.getMaterials().equals(newTool.getMaterials())){
                return true;
            }else if (!oldTool.getModifierList().equals(newTool.getModifierList())){
                return true;
            }else {
                Multimap<Attribute, AttributeModifier> attributesNew = newStack.getAttributeModifiers(EquipmentSlot.MAINHAND);
                Multimap<Attribute, AttributeModifier> attributesOld = oldStack.getAttributeModifiers(EquipmentSlot.MAINHAND);
                if (attributesNew.size() != attributesOld.size()){
                    return true;
                }else {
                    for (Attribute attribute : attributesOld.keySet()) {
                        if (!attributesNew.containsKey(attribute)){
                            return true;
                        }

                        Iterator<AttributeModifier> iter1 = attributesNew.get(attribute).iterator();
                        Iterator<AttributeModifier> iter2 = attributesOld.get(attribute).iterator();

                        while (iter1.hasNext() && iter2.hasNext()) {
                            if (!((AttributeModifier) iter1.next()).equals(iter2.next())){
                                return true;
                            }
                        }
                    }

                    return false;
                }
            }
        }else {
            return true;
        }
    }

    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return this.shouldCauseReequipAnimation(oldStack, newStack, false);
    }

    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return shouldCauseReequip(oldStack, newStack, slotChanged);
    }

    public static BlockHitResult blockRayTrace(Level worldIn, Player player, ClipContext.Fluid fluidMode) {
        return Item.getPlayerPOVHitResult(worldIn, player, fluidMode);
    }

    public @NotNull ToolDefinition getToolDefinition() {
        return this.toolDefinition;
    }

    //Below are more like from spell book, but modify for Modifiable version
    public @NotNull SpellTier getTier(ItemStack stack) {
        ToolStack toolStack = ToolStack.from(stack);
        boolean creative = 0 < toolStack.getModifierLevel(DreamtinkerModifiers.Ids.nova_creative_tiers);
        if (creative)
            return SpellTier.CREATIVE;
        int tier = toolStack.getModifierLevel(DreamtinkerModifiers.Ids.nova_spell_tiers);
        SpellTier spell_tier = null;
        while (null == spell_tier && 0 < tier) {
            spell_tier = SpellTier.SPELL_TIER_MAP.get(tier);
            --tier;
        }
        if (null == spell_tier)
            spell_tier = SpellTier.ONE;
        return spell_tier;
    }

    @NotNull
    @Override
    public ISpellCaster getSpellCaster(ItemStack stack) {
        return new BookCaster(stack);
    }

    @Override
    public ISpellCaster getSpellCaster() {
        return new BookCaster(new CompoundTag());
    }

    @Override
    public ISpellCaster getSpellCaster(CompoundTag tag) {
        return new BookCaster(tag);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onOpenBookMenuKeyPressed(ItemStack stack, Player player) {
        InteractionHand hand = StackUtil.getBookHand(player);
        if (hand == null){
            return;
        }
        Minecraft.getInstance().setScreen(ServerConfig.INFINITE_SPELLS.get() ? new ModifiableInfinityGuiSpellBook(hand) : new ModifiableGuiSpellBook(hand));
    }

    @SuppressWarnings({"removal"})
    @Override
    public boolean canQuickCast() {
        return true;
    }

    public static class BookCaster extends SpellCaster {

        public BookCaster(ItemStack stack) {
            super(stack);
        }

        public BookCaster(CompoundTag tag) {
            super(tag);
        }

        @Override
        public int getMaxSlots() {
            return 10;
        }
    }
}
