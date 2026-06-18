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
import org.brahypno.esotericismtinker.utils.CompactUtils.CuriosCompact;
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

import static org.brahypno.dreamtinker.tools.modifiers.events.weaponDreamsEnsureEnds.*;

public class WeaponDreams extends NoLevelsModifier implements LeftClickHook, RightClickHook, GeneralInteractionModifierHook {
    private static final ThreadLocal<Boolean> IN_ATTACK = ThreadLocal.withInitial(() -> false);

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

    private static void handleClientAttack(Player player, @Nullable Entity target) {
        if (target == null)
            return;

        try {
            IN_ATTACK.set(true);
            player.attack(target);
        }
        finally {
            IN_ATTACK.set(false);
        }
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

    private static void callChosenLeftClickEmpty(ItemStack chosen, Player player, Level level, EquipmentSlot equipmentSlot) {
        IToolStackView chosenTool = ToolStack.from(chosen);
        for (ModifierEntry chosenEntry : chosenTool.getModifierList()) {
            chosenEntry.getHook(EsotericismTinkerHook.LEFT_CLICK)
                       .onLeftClickEmpty(chosenTool, chosenEntry, player, level, equipmentSlot);
        }
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

    private static void rightClickEmptyFromProxy(ServerPlayer sp, Level level) {
        endChosen(sp);

        ItemStack proxyStack = CuriosCompact.findPreferredGlove(sp);
        if (proxyStack.isEmpty())
            return;

        if (sp.getCooldowns().isOnCooldown(DreamtinkerTools.silence_glove.asItem()))
            return;

        ToolStack proxyTool = ToolStack.from(proxyStack);
        ModifierEntry weaponSlots = proxyTool.getModifier(DreamtinkerModifiers.Ids.weapon_slots);
        if (weaponSlots.getLevel() < 1)
            return;

        List<ItemStack> frames = new ArrayList<>();
        weaponSlots.getHook(ToolInventoryCapability.HOOK)
                   .getAllStacks(proxyTool, weaponSlots, frames);

        boolean toolFilter = 1 <= proxyTool.getModifier(DreamtinkerModifiers.Ids.weapon_dreams_filter).getLevel();
        boolean naturalOrder = 1 <= proxyTool.getModifier(DreamtinkerModifiers.Ids.weapon_dreams_order).getLevel();

        int lastIdx = !proxyTool.getPersistentData().contains(TAG_LAST_USE)
                      ? -1
                      : proxyTool.getPersistentData().getInt(TAG_LAST_USE);

        int chosenIdx = chooseIndex(level, frames, null, naturalOrder, toolFilter, lastIdx);
        if (chosenIdx < 0)
            return;

        ItemStack chosen = borrowChosen(proxyStack, proxyTool, weaponSlots, frames, chosenIdx, naturalOrder);

        if (chosen.isEmpty())
            return;

        int cooldownTicks = computeProxyCooldownTicks(proxyTool);
        ItemStack proxySnap = proxyStack.copy();

        update_hand(sp, chosen);
        startChosenDisplay(sp, chosenIdx, proxySnap, cooldownTicks, true);

        try {
            IN_ATTACK.set(true);
            sp.gameMode.useItem(sp, level, chosen, InteractionHand.MAIN_HAND);
        }
        finally {
            IN_ATTACK.set(false);
        }
    }


    @Override
    public void onRightClickEmpty(
            IToolStackView tool, ModifierEntry entry, Player player,
            Level level, EquipmentSlot equipmentSlot) {
        if (IN_ATTACK.get())
            return;

        if (!(player instanceof ServerPlayer sp) || level.isClientSide || player.isUsingItem())
            return;

        if (!player.getMainHandItem().isEmpty())
            return;

        rightClickEmptyFromProxy(sp, level);
    }

    private static ItemStack borrowChosen(
            ItemStack proxyStack, ToolStack proxyTool, ModifierEntry weaponSlots,
            List<ItemStack> frames, int chosenIdx, boolean naturalOrder) {
        ItemStack chosen = frames.get(chosenIdx).copy();

        if (chosen.isEmpty() || !chosen.is(TinkerTags.Items.MODIFIABLE))
            return ItemStack.EMPTY;

        if (naturalOrder)
            proxyTool.getPersistentData().putInt(TAG_LAST_USE, chosenIdx);

        weaponSlots.getHook(ToolInventoryCapability.HOOK)
                   .setStack(proxyTool, weaponSlots, chosenIdx, ItemStack.EMPTY);

        proxyTool.updateStack(proxyStack);

        return chosen;
    }

    private void left_click_3_in_one(
            @Nullable AttackEntityEvent event, IToolStackView tool, ModifierEntry entry,
            Player player, Level level, EquipmentSlot equipmentSlot,
            @Nullable Entity target, @Nullable BlockState state) {
        if (player == null)
            return;

        if (IN_ATTACK.get())
            return;

        if (event != null)
            event.setCanceled(true);

        if (player.getCooldowns().isOnCooldown(DreamtinkerTools.silence_glove.asItem()))
            return;

        if (level.isClientSide){
            try {
                IN_ATTACK.set(true);

                if (target != null)
                    player.attack(target);
            }
            finally {
                IN_ATTACK.set(false);
            }

            return;
        }

        ItemStack proxyStack = player.getMainHandItem();
        boolean mainEmpty = proxyStack.isEmpty();

        if (mainEmpty)
            proxyStack = CuriosCompact.findPreferredGlove(player);

        if (proxyStack.isEmpty())
            return;

        ToolStack proxyTool = ToolStack.from(proxyStack);
        ModifierEntry weaponSlots = proxyTool.getModifier(DreamtinkerModifiers.Ids.weapon_slots);

        if (weaponSlots.getLevel() < 1)
            return;

        List<ItemStack> frames = new ArrayList<>();
        weaponSlots.getHook(ToolInventoryCapability.HOOK).getAllStacks(proxyTool, weaponSlots, frames);

        boolean toolFilter = 1 <= proxyTool.getModifier(DreamtinkerModifiers.Ids.weapon_dreams_filter).getLevel();
        boolean naturalOrder = 1 <= proxyTool.getModifier(DreamtinkerModifiers.Ids.weapon_dreams_order).getLevel();

        int lastIdx = !proxyTool.getPersistentData().contains(TAG_LAST_USE)
                      ? -1
                      : proxyTool.getPersistentData().getInt(TAG_LAST_USE);

        int chosenIdx = chooseIndex(level, frames, state, naturalOrder, toolFilter, lastIdx);

        if (chosenIdx < 0)
            return;

        ItemStack chosen = borrowChosen(proxyStack, proxyTool, weaponSlots, frames, chosenIdx, naturalOrder);

        if (chosen.isEmpty())
            return;

        ServerPlayer sp = (ServerPlayer) player;
        int cooldownTicks = computeProxyCooldownTicks(proxyTool);
        ItemStack proxySnap = proxyStack.copy();

        update_hand(player, chosen);
        startChosenDisplay(sp, chosenIdx, proxySnap, cooldownTicks, mainEmpty);

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
}