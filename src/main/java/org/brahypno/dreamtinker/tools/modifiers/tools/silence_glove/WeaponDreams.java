package org.brahypno.dreamtinker.tools.modifiers.tools.silence_glove;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.brahypno.dreamtinker.tools.DreamtinkerModifiers;
import org.brahypno.dreamtinker.tools.DreamtinkerTools;
import org.brahypno.esotericismtinker.library.modifiers.EsotericismTinkerHook;
import org.brahypno.esotericismtinker.library.modifiers.hook.LeftClickHook;
import org.brahypno.esotericismtinker.library.modifiers.hook.RightClickHook;
import org.brahypno.esotericismtinker.utils.CompatUtils.CuriosCompat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.definition.module.mining.IsEffectiveToolHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static org.brahypno.dreamtinker.tools.modifiers.events.weaponDreamsEnsureEnds.*;

public class WeaponDreams extends NoLevelsModifier implements LeftClickHook, RightClickHook, GeneralInteractionModifierHook {
    private static final ThreadLocal<Boolean> DISPATCHING_BORROWED_ACTION = ThreadLocal.withInitial(() -> false);

    public static boolean isDispatchingBorrowedAction() {
        return DISPATCHING_BORROWED_ACTION.get();
    }

    public static void dispatchBorrowedAction(Runnable action) {
        boolean previous = DISPATCHING_BORROWED_ACTION.get();
        try {
            DISPATCHING_BORROWED_ACTION.set(true);
            action.run();
        }
        finally {
            DISPATCHING_BORROWED_ACTION.set(previous);
        }
    }

    public static <T> T dispatchBorrowedAction(Supplier<T> action) {
        boolean previous = DISPATCHING_BORROWED_ACTION.get();
        try {
            DISPATCHING_BORROWED_ACTION.set(true);
            return action.get();
        }
        finally {
            DISPATCHING_BORROWED_ACTION.set(previous);
        }
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, EsotericismTinkerHook.LEFT_CLICK, EsotericismTinkerHook.RIGHT_CLICK, ModifierHooks.GENERAL_INTERACT);
        super.registerHooks(hookBuilder);
    }

    @Override
    public int getUseDuration(IToolStackView tool, ModifierEntry modifier) {
        return 72000;
    }

    @Override
    public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
        return InteractionResult.PASS;
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void onLeftClickEmpty(IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot) {
        left_click_3_in_one(null, tool, entry, player, level, equipmentSlot, null, null);
    }

    @Override
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event, IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot, BlockState state, BlockPos pos) {
        left_click_3_in_one(null, tool, entry, player, level, equipmentSlot, null, state);
    }

    @Override
    public void onLeftClickEntity(AttackEntityEvent event, IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot, Entity target) {
        left_click_3_in_one(event, tool, entry, player, level, equipmentSlot, target, null);
    }

    private static void update_hand(Player player, ItemStack stack) {
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        player.getInventory().setChanged();
    }

    public static int chooseIndex(Level level, List<ItemStack> frames, @Nullable BlockState targetState, boolean noRandomCycle, boolean requireUsable, int lastIndex) {
        List<Integer> nonEmpty = new ArrayList<>();
        for (int i = 0; i < frames.size(); i++) {
            if (!frames.get(i).isEmpty())
                nonEmpty.add(i);
        }

        if (nonEmpty.isEmpty())
            return -1;

        List<Integer> usable = Collections.emptyList();
        if (requireUsable){
            usable = new ArrayList<>();
            for (int i : nonEmpty) {
                ItemStack stack = frames.get(i);
                if ((targetState != null && canHarvest(targetState, stack)) || stack.is(TinkerTags.Items.MELEE_PRIMARY)){
                    usable.add(i);
                }
            }
        }

        if (requireUsable && !usable.isEmpty()){
            if (noRandomCycle)
                return naturalCycle(usable, lastIndex);
            if (usable.size() == 1)
                return usable.get(0);
            return usable.get(level.random.nextInt(usable.size()));
        }

        if (noRandomCycle)
            return naturalCycle(nonEmpty, lastIndex);
        if (nonEmpty.size() == 1)
            return nonEmpty.get(0);
        return nonEmpty.get(level.random.nextInt(nonEmpty.size()));
    }

    public static List<ItemStack> readWeaponSlots(IToolStackView tool, ModifierEntry weaponSlots) {
        ToolInventoryCapability.InventoryModifierHook inventory =
                weaponSlots.getHook(ToolInventoryCapability.HOOK);
        int slotCount = inventory.getSlots(tool, weaponSlots);
        List<ItemStack> frames = new ArrayList<>(slotCount);

        for (int slot = 0; slot < slotCount; slot++) {
            frames.add(inventory.getStack(tool, weaponSlots, slot).copy());
        }

        return frames;
    }

    public static int naturalCycle(List<Integer> candidates, int lastIndex) {
        if (candidates.isEmpty())
            return -1;
        if (candidates.size() == 1)
            return candidates.get(0);

        for (int idx : candidates) {
            if (idx > lastIndex)
                return idx;
        }

        return candidates.get(0);
    }

    public static int computeProxyCooldownTicks(IToolStackView toolStackView) {
        float chosenSpeed = toolStackView.getStats().get(ToolStats.ATTACK_SPEED);
        return Math.max(1, net.minecraft.util.Mth.ceil(20f / chosenSpeed));
    }

    /**
     * Checks whether an empty main hand can currently proxy at least one stored
     * weapon from the Curios glove. This is read-only and safe to call from the
     * client interaction event before the server starts the borrow transaction.
     */
    public static boolean canBorrowFromCurio(Player player) {
        if (!player.getMainHandItem().isEmpty()
            || player.getCooldowns().isOnCooldown(DreamtinkerTools.silence_glove.asItem()))
            return false;

        ItemStack proxyStack = CuriosCompat.findPreferredGlove(player);
        if (proxyStack.isEmpty() || !proxyStack.is(DreamtinkerTools.silence_glove.asItem()))
            return false;

        ToolStack proxyTool = ToolStack.from(proxyStack);
        ModifierEntry weaponSlots = proxyTool.getModifier(DreamtinkerModifiers.Ids.weapon_slots);
        if (weaponSlots.getLevel() < 1)
            return false;

        for (ItemStack stack : readWeaponSlots(proxyTool, weaponSlots)) {
            if (!stack.isEmpty() && stack.is(TinkerTags.Items.MODIFIABLE))
                return true;
        }
        return false;
    }

    /**
     * Settles any previous loan and snapshots the glove state used by the next loan.
     * Both click paths go through this method so slot numbering and source tracking
     * cannot drift apart again.
     */
    public static @Nullable BorrowContext beginBorrow(ServerPlayer player) {
        endChosen(player);

        if (player.getCooldowns().isOnCooldown(DreamtinkerTools.silence_glove.asItem()))
            return null;

        boolean proxyInCurio = player.getMainHandItem().isEmpty();
        ItemStack proxyStack = proxyInCurio
                               ? CuriosCompat.findPreferredGlove(player)
                               : player.getMainHandItem();
        if (proxyStack.isEmpty() || !proxyStack.is(DreamtinkerTools.silence_glove.asItem()))
            return null;

        ToolStack proxyTool = ToolStack.from(proxyStack);
        ModifierEntry weaponSlots = proxyTool.getModifier(DreamtinkerModifiers.Ids.weapon_slots);
        if (weaponSlots.getLevel() < 1)
            return null;

        List<ItemStack> frames = readWeaponSlots(proxyTool, weaponSlots);
        boolean requireUsable = proxyTool.getModifier(DreamtinkerModifiers.Ids.weapon_dreams_filter).getLevel() >= 1;
        boolean naturalOrder = proxyTool.getModifier(DreamtinkerModifiers.Ids.weapon_dreams_order).getLevel() >= 1;
        int lastUsedIndex = proxyTool.getPersistentData().contains(TAG_LAST_USE)
                            ? proxyTool.getPersistentData().getInt(TAG_LAST_USE)
                            : -1;

        return new BorrowContext(
                player, proxyStack, proxyTool, weaponSlots, frames,
                proxyInCurio, requireUsable, naturalOrder, lastUsedIndex
        );
    }

    private static void rightClickEmptyFromProxy(ServerPlayer sp, Level level) {
        BorrowContext borrow = beginBorrow(sp);
        if (borrow == null || !borrow.proxyInCurio())
            return;

        int chosenIdx = chooseIndex(
                level, borrow.frames(), null,
                borrow.naturalOrder(), borrow.requireUsable(), borrow.lastUsedIndex()
        );
        if (chosenIdx < 0)
            return;

        ItemStack chosen = borrow.borrow(chosenIdx);
        if (chosen.isEmpty())
            return;

        dispatchBorrowedAction(
                () -> sp.gameMode.useItem(sp, level, sp.getMainHandItem(), InteractionHand.MAIN_HAND)
        );
        syncBorrowedUseState(sp);
    }

    @Override
    public void onRightClickEmpty(
            IToolStackView tool, ModifierEntry entry, Player player,
            Level level, EquipmentSlot equipmentSlot) {
        if (DISPATCHING_BORROWED_ACTION.get())
            return;

        if (!(player instanceof ServerPlayer sp) || level.isClientSide || player.isUsingItem())
            return;

        if (!player.getMainHandItem().isEmpty())
            return;

        rightClickEmptyFromProxy(sp, level);
    }

    private void left_click_3_in_one(
            @Nullable AttackEntityEvent event, IToolStackView tool, ModifierEntry entry,
            Player player, Level level, EquipmentSlot equipmentSlot,
            @Nullable Entity target, @Nullable BlockState state) {
        if (player == null)
            return;

        if (DISPATCHING_BORROWED_ACTION.get())
            return;

        if (event != null)
            event.setCanceled(true);

        if (level.isClientSide && player.getCooldowns().isOnCooldown(DreamtinkerTools.silence_glove.asItem()))
            return;

        if (level.isClientSide){
            dispatchBorrowedAction(() -> {
                if (target != null)
                    player.attack(target);
            });

            return;
        }

        if (!(player instanceof ServerPlayer sp))
            return;

        BorrowContext borrow = beginBorrow(sp);
        if (borrow == null)
            return;

        int chosenIdx = chooseIndex(
                level, borrow.frames(), state,
                borrow.naturalOrder(), borrow.requireUsable(), borrow.lastUsedIndex()
        );

        if (chosenIdx < 0)
            return;

        ItemStack chosen = borrow.borrow(chosenIdx);
        if (chosen.isEmpty())
            return;

        player.attackStrengthTicker = (int) Math.ceil(player.getCurrentItemAttackStrengthDelay());

        if (target != null){
            chosen.getItem().onLeftClickEntity(chosen, player, target);
            return;
        }

        if (state == null){
            IToolStackView chosenTool = ToolStack.from(chosen);

            for (ModifierEntry chosenEntry : chosenTool.getModifierList()) {
                chosenEntry.getHook(EsotericismTinkerHook.LEFT_CLICK)
                           .onLeftClickEmpty(chosenTool, chosenEntry, player, level, equipmentSlot);
            }
        }
    }

    private static boolean canHarvest(BlockState state, ItemStack stack) {
        return IsEffectiveToolHook.isEffective(ToolStack.from(stack), state);
    }

    public static final class BorrowContext {
        private final ServerPlayer player;
        private final ItemStack proxyStack;
        private final ToolStack proxyTool;
        private final ModifierEntry weaponSlots;
        private final List<ItemStack> frames;
        private final boolean proxyInCurio;
        private final boolean requireUsable;
        private final boolean naturalOrder;
        private final int lastUsedIndex;
        private boolean borrowed;

        private BorrowContext(
                ServerPlayer player, ItemStack proxyStack, ToolStack proxyTool,
                ModifierEntry weaponSlots, List<ItemStack> frames,
                boolean proxyInCurio, boolean requireUsable,
                boolean naturalOrder, int lastUsedIndex) {
            this.player = player;
            this.proxyStack = proxyStack;
            this.proxyTool = proxyTool;
            this.weaponSlots = weaponSlots;
            this.frames = frames;
            this.proxyInCurio = proxyInCurio;
            this.requireUsable = requireUsable;
            this.naturalOrder = naturalOrder;
            this.lastUsedIndex = lastUsedIndex;
        }

        public List<ItemStack> frames() {
            return Collections.unmodifiableList(frames);
        }

        public boolean proxyInCurio() {
            return proxyInCurio;
        }

        public boolean requireUsable() {
            return requireUsable;
        }

        public boolean naturalOrder() {
            return naturalOrder;
        }

        public int lastUsedIndex() {
            return lastUsedIndex;
        }

        public ItemStack borrow(int slot) {
            if (borrowed || slot < 0 || slot >= frames.size())
                return ItemStack.EMPTY;

            ItemStack chosen = frames.get(slot).copy();
            if (chosen.isEmpty() || !chosen.is(TinkerTags.Items.MODIFIABLE))
                return ItemStack.EMPTY;

            borrowed = true;
            if (naturalOrder)
                proxyTool.getPersistentData().putInt(TAG_LAST_USE, slot);

            weaponSlots.getHook(ToolInventoryCapability.HOOK)
                       .setStack(proxyTool, weaponSlots, slot, ItemStack.EMPTY);
            proxyTool.updateStack(proxyStack);

            update_hand(player, chosen);
            startChosenDisplay(
                    player, slot, proxyStack.copy(),
                    computeProxyCooldownTicks(proxyTool), proxyInCurio
            );
            return player.getMainHandItem();
        }
    }
}
