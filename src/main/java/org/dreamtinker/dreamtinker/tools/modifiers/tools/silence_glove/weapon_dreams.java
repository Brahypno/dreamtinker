package org.dreamtinker.dreamtinker.tools.modifiers.tools.silence_glove;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.dreamtinker.dreamtinker.library.modifiers.DreamtinkerHook;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.tools.DreamtinkerTools;
import org.dreamtinker.dreamtinker.tools.items.SilenceGlove;
import org.dreamtinker.dreamtinker.utils.CuriosCompact;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.definition.module.mining.IsEffectiveToolHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.dreamtinker.dreamtinker.tools.modifiers.events.weaponDreamsEnsureEnds.TAG_LAST_USE;
import static org.dreamtinker.dreamtinker.tools.modifiers.events.weaponDreamsEnsureEnds.startChosenDisplay;

public class weapon_dreams extends BattleModifier {
    private static final ThreadLocal<Boolean> IN_ATTACK = ThreadLocal.withInitial(() -> false);

    @Override
    public int getUseDuration(IToolStackView tool, ModifierEntry modifier) {
        return 72000;
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void onRightClickEmpty(IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot) {
        if (IN_ATTACK.get())
            return;
        if (player instanceof ServerPlayer sp && !level.isClientSide && player.getMainHandItem().isEmpty() && !player.isUsingItem()){
            IN_ATTACK.set(true);
            sp.gameMode.useItem(sp, level, CuriosCompact.findPreferredGlove(player), InteractionHand.MAIN_HAND);
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

    private void left_click_3_in_one(@Nullable AttackEntityEvent event, IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot, @Nullable Entity target, @Nullable BlockState state) {
        if (player == null)
            return;

        // 我们自己触发的二次/递归攻击，直接放行默认流程
        if (IN_ATTACK.get())
            return;
        if (null != event)
            event.setCanceled(true); // 我们接管
        ItemStack proxy = player.getMainHandItem();
        if (player.getCooldowns().isOnCooldown(DreamtinkerTools.silence_glove.asItem()))
            return;

        if (level.isClientSide){
            // ===== 客户端：拦截默认，临时换手后调用 player.attack()（只在客户端调！） =====
            try {
                IN_ATTACK.set(true); // 二次事件放行
                // 这行会：1) 再触发一次客户端 AttackEntityEvent（被 IN_ATTACK 放行，默认链按 chosen 走）
                //       2) 发送服务端攻击包
                if (null != target)
                    player.attack(target);
            }
            finally {
                // 当帧结束前还原显示；服务端随后也会通过背包同步/强制包让客户端回到一致的 proxy
                IN_ATTACK.set(false);
            }
        }else {
            // —— 按你的方式判断是否启用代理 & 取 InventoryModule —— //
            ModifierEntry weapon_slots = tool.getModifier(DreamtinkerModifiers.Ids.weapon_slots);
            if (weapon_slots.getLevel() < 1)
                return;

            List<ItemStack> frames = new ArrayList<>();
            weapon_slots.getHook(ToolInventoryCapability.HOOK).getAllStacks(tool, weapon_slots, frames);
            boolean tool_filter = 1 <= tool.getModifier(DreamtinkerModifiers.Ids.weapon_dreams_filter).getLevel();
            boolean natural_order = 1 <= tool.getModifier(DreamtinkerModifiers.Ids.weapon_dreams_order).getLevel();
            int chosenIdx;
            int last_idx = !tool.getPersistentData().contains(TAG_LAST_USE) ? -1 : tool.getPersistentData().getInt(TAG_LAST_USE);

            chosenIdx = chooseIndex(level, frames, state, natural_order, tool_filter, last_idx);
            if (chosenIdx < 0)
                return;
            ItemStack chosen = frames.get(chosenIdx);
            ItemStack proxySnap = proxy.copy();
            if (chosen.isEmpty() || !chosen.is(TinkerTags.Items.MODIFIABLE))
                return;
            if (natural_order){
                if (proxySnap.getItem() instanceof SilenceGlove){
                    ToolStack toolStack = ToolStack.from(proxySnap);
                    toolStack.getPersistentData().putInt(TAG_LAST_USE, chosenIdx);
                    toolStack.updateStack(proxySnap);
                }else
                    tool.getPersistentData().putInt(TAG_LAST_USE, chosenIdx);
            }

            // ===== 服务端：不要再调用 player.attack()，直接执行业务逻辑（chosen → 钩子 → 回写 → 还原） =====
            ServerPlayer sp = (ServerPlayer) player;
            int cooldownTicks = computeProxyCooldownTicks(tool);
            try {
                // 临时换手仅为让某些钩子/附魔读取到正确主手；也可直接不用换手，仅把 chosen 传入钩子
                update_hand(player, chosen.copy());
                // 1) 刚切换为 chosen 时，立即让客户端主手槽显示 chosen
                startChosenDisplay(sp, chosen, proxySnap, cooldownTicks);

                player.attackStrengthTicker = (int) Math.ceil(player.getCurrentItemAttackStrengthDelay());

                // 你的实际攻击逻辑（不要再调 player.attack）
                if (null != target)
                    chosen.getItem().onLeftClickEntity(chosen, player, target);
                else if (null == state){
                    IToolStackView chosen_tool = ToolStack.from(chosen);
                    for (ModifierEntry chosen_entry : chosen_tool.getModifierList()) {
                        chosen_entry.getHook(DreamtinkerHook.LEFT_CLICK).onLeftClickEmpty(chosen_tool, chosen_entry, player, level, equipmentSlot);
                    }
                }

                // 回写 chosen 的消耗到 InventoryModule
                ItemStack after = player.getMainHandItem().copy();
                set_back(tool, after, chosenIdx);
            }
            finally {
                // 还原主手（服务端权威）
            }
        }
    }

    private void update_hand(Player player, ItemStack stack) {
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        player.getInventory().setChanged();
    }

    private void set_back(IToolStackView tool, ItemStack stack, int chosenSlot) {
        ModifierEntry weapon_slots = tool.getModifier(DreamtinkerModifiers.Ids.weapon_slots);
        weapon_slots.getHook(ToolInventoryCapability.HOOK).setStack(tool, weapon_slots, chosenSlot, stack);
    }

    public static int computeProxyCooldownTicks(IToolStackView toolStackView) {
        float chosenSpeed = toolStackView.getStats().get(ToolStats.ATTACK_SPEED);
        return Math.max(1, net.minecraft.util.Mth.ceil(20f / chosenSpeed));
    }

    public static int chooseIndex(
            Level level,
            List<ItemStack> frames,
            @Nullable BlockState targetState,
            boolean NoRandomCycle,
            boolean RequireUsable,
            int lastIndex) {

        // 收集所有非空
        List<Integer> nonEmpty = new ArrayList<>();
        for (int i = 0; i < frames.size(); i++) {
            if (!frames.get(i).isEmpty()){
                nonEmpty.add(i);
            }
        }
        if (nonEmpty.isEmpty())
            return -1; // 没东西可选
        lastIndex = (lastIndex) % nonEmpty.size();


        // B 只有在有 targetState 时才有意义，否则直接当没开 B

        List<Integer> usable = Collections.emptyList();
        if (RequireUsable){
            usable = new ArrayList<>();
            for (int i : nonEmpty) {
                ItemStack s = frames.get(i);
                if (targetState != null && canHarvest(targetState, s) || s.is(TinkerTags.Items.MELEE_PRIMARY))
                    usable.add(i);
            }
        }

        // ========== 情况分发 ==========

        if (RequireUsable && !usable.isEmpty()){
            // 有 B 且存在可用工具
            if (NoRandomCycle){
                // A + B：在 usable 中自然循环
                return naturalCycle(usable, lastIndex);
            }else {
                // 仅 B：在 usable 中随机
                if (usable.size() == 1){
                    return usable.get(0);
                }
                return usable.get(level.random.nextInt(usable.size()));
            }
        }

        // 走到这里两种情况：
        // 1) 没开 B
        // 2) 开了 B 但 usable 为空，需要按你说的回退到“非空逻辑”，由 A 决定选法

        if (NoRandomCycle){
            // 无 B 或 A+B 但无 usable：在 nonEmpty 中自然循环
            return naturalCycle(nonEmpty, lastIndex);
        }else {
            // 无 A：在 nonEmpty 中随机
            if (nonEmpty.size() == 1)
                return nonEmpty.get(0);
            return nonEmpty.get(level.random.nextInt(nonEmpty.size()));
        }
    }

    // 自然循环: 从 lastIndex 之后找下一个候选，没有就回到第一个
    public static int naturalCycle(List<Integer> candidates, int lastIndex) {

        if (candidates.isEmpty()){
            return -1;
        }
        if (candidates.size() == 1){
            return candidates.get(0);
        }

        int chosen = candidates.get(0);

        for (int idx : candidates) {
            if (idx > lastIndex){
                chosen = idx;
                break;
            }
        }
        return chosen;
    }

    // 工具是否可用于该方块（只依赖 stack + state）
    private static boolean canHarvest(BlockState state, ItemStack stack) {
        return IsEffectiveToolHook.isEffective(ToolStack.from(stack), state);
    }


}
