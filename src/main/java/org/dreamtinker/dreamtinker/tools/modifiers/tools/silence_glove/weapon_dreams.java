package org.dreamtinker.dreamtinker.tools.modifiers.tools.silence_glove;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
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
import org.dreamtinker.dreamtinker.utils.CuriosCompact;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

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
            Selected selected = select_chosen(tool, level);
            ItemStack chosen = selected.stack;
            ItemStack proxySnap = proxy.copy();
            if (chosen.isEmpty() || !chosen.is(TinkerTags.Items.MODIFIABLE))
                return;

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
                set_back(tool, after, selected.slot);
            }
            finally {
                // 还原主手（服务端权威）
            }
        }
    }

    record Selected(ItemStack stack, int slot) {}

    private void update_hand(Player player, ItemStack stack) {
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        player.getInventory().setChanged();
    }

    private Selected select_chosen(IToolStackView tool, Level level) {
        ModifierEntry weapon_slots = tool.getModifier(DreamtinkerModifiers.Ids.weapon_slots);
        if (weapon_slots.getLevel() < 1)
            return new Selected(ItemStack.EMPTY, -1);

        List<ItemStack> frames = new ArrayList<>();
        weapon_slots.getHook(ToolInventoryCapability.HOOK).getAllStacks(tool, weapon_slots, frames);
        int chosenSlot = chooseRandomStack(frames, level.random);
        if (chosenSlot < 0)
            return new Selected(ItemStack.EMPTY, -1);
        return new Selected(frames.get(chosenSlot), chosenSlot);
    }

    private void set_back(IToolStackView tool, ItemStack stack, int chosenSlot) {
        ModifierEntry weapon_slots = tool.getModifier(DreamtinkerModifiers.Ids.weapon_slots);
        weapon_slots.getHook(ToolInventoryCapability.HOOK).setStack(tool, weapon_slots, chosenSlot, stack);
    }

    private int chooseRandomStack(List<ItemStack> itemFrames, RandomSource rdn) {
        OptionalInt idxOpt = pickNonAirIndex(itemFrames, rdn);
        if (idxOpt.isPresent()){
            return idxOpt.getAsInt();
        }else
            return -1;
    }

    private static OptionalInt pickNonAirIndex(List<ItemStack> stacks, RandomSource random) {
        int chosen = -1;
        int seen = 0; // 已遇到的非空数量
        for (int i = 0; i < stacks.size(); i++) {
            ItemStack s = stacks.get(i);
            if (!s.isEmpty()){ // 等价于“不是空气/空堆”
                seen++;
                // 以 1/seen 的概率替换当前选择
                if (random.nextInt(seen) == 0){
                    chosen = i;
                }
            }
        }
        return chosen >= 0 ? OptionalInt.of(chosen) : OptionalInt.empty();
    }

    public static int computeProxyCooldownTicks(IToolStackView toolStackView) {
        float chosenSpeed = toolStackView.getStats().get(ToolStats.ATTACK_SPEED);
        return Math.max(1, net.minecraft.util.Mth.ceil(20f / chosenSpeed));
    }

}
