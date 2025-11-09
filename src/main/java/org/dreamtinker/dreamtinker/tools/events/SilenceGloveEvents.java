package org.dreamtinker.dreamtinker.tools.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.tools.DreamtinkerTools;
import org.dreamtinker.dreamtinker.tools.items.SilenceGlove;
import org.dreamtinker.dreamtinker.tools.modifiers.tools.silence_glove.weapon_dreams;
import org.dreamtinker.dreamtinker.utils.CuriosCompact;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static org.dreamtinker.dreamtinker.tools.modifiers.events.weaponDreamsEnsureEnds.TAG_LAST_USE;
import static org.dreamtinker.dreamtinker.tools.modifiers.events.weaponDreamsEnsureEnds.startChosenDisplay;
import static org.dreamtinker.dreamtinker.tools.modifiers.tools.silence_glove.weapon_dreams.computeProxyCooldownTicks;
import static slimeknights.tconstruct.library.tools.item.IModifiable.DEFER_OFFHAND;
import static slimeknights.tconstruct.library.tools.item.IModifiable.NO_INTERACTION;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class SilenceGloveEvents {
    private static final ThreadLocal<Boolean> REENTRY = ThreadLocal.withInitial(() -> Boolean.FALSE);

    /* ========== 右键空气/物品：随机 use ========== */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (Boolean.TRUE.equals(REENTRY.get()))
            return;
        Player player = event.getEntity();
        if (player == null)
            return;

        ItemStack tool = player.getMainHandItem();
        if (tool.isEmpty())
            tool = CuriosCompact.findPreferredGlove(player);

        if (!(tool.getItem() instanceof SilenceGlove))
            return;
        if (player.getCooldowns().isOnCooldown(DreamtinkerTools.silence_glove.asItem()))
            return;

        if (!player.level().isClientSide){
            ToolStack silenceGlove = ToolStack.from(tool);
            ModifierEntry entry = silenceGlove.getModifier(DreamtinkerModifiers.Ids.weapon_slots);
            if (entry.getLevel() < 1)
                return;

            List<ItemStack> itemFrames = new ArrayList<>();
            entry.getHook(ToolInventoryCapability.HOOK).getAllStacks(silenceGlove, entry, itemFrames);

            boolean tool_filter = 1 <= silenceGlove.getModifier(DreamtinkerModifiers.Ids.weapon_dreams_filter).getLevel();
            boolean natural_order = 1 <= silenceGlove.getModifier(DreamtinkerModifiers.Ids.weapon_dreams_order).getLevel();
            int last_idx = !silenceGlove.getPersistentData().contains(TAG_LAST_USE) ? -1 : silenceGlove.getPersistentData().getInt(TAG_LAST_USE);

            int slot = pickUsable(itemFrames, player, natural_order, tool_filter, last_idx);
            if (slot < 0)
                return;
            ItemStack chosen = itemFrames.get(slot);

            if (chosen.isEmpty() || !chosen.is(TinkerTags.Items.MODIFIABLE))
                return;
            if (natural_order){
                silenceGlove.getPersistentData().putInt(TAG_LAST_USE, slot);
                silenceGlove.updateStack(tool);
            }
            ItemStack proxySnap = tool.copy();

            try {
                REENTRY.set(true);
                player.setItemInHand(InteractionHand.MAIN_HAND, chosen.copy());
                player.getInventory().setChanged();
                startChosenDisplay((ServerPlayer) player, chosen, proxySnap, computeProxyCooldownTicks(silenceGlove));

                player.getMainHandItem().use(player.level(), player, InteractionHand.MAIN_HAND);
                ItemStack after = player.getMainHandItem().copy();
                entry.getHook(ToolInventoryCapability.HOOK).setStack(silenceGlove, entry, slot, after);
            }
            finally {
                REENTRY.set(false);
            }
        }
    }

    private static int pickUsable(
            List<ItemStack> stacks, Player player, boolean NoRandomCycle, boolean RequireUsable, int lastIndex) {
        // 收集所有非空
        List<Integer> nonEmpty = new ArrayList<>();
        for (int i = 0; i < stacks.size(); i++) {
            if (!stacks.get(i).isEmpty()){
                nonEmpty.add(i);
            }
        }
        if (nonEmpty.isEmpty())
            return -1; // 没东西可选
        lastIndex = (lastIndex) % nonEmpty.size();
        List<Integer> usable = new ArrayList<>();
        for (int i : nonEmpty) {
            ItemStack s = stacks.get(i);
            ToolStack tool = ToolStack.from(s);
            // 等价于“不是空气/空堆”
            if (shouldInteract(player, tool, InteractionHand.MAIN_HAND)){
                for (ModifierEntry entry : tool.getModifierList()) {
                    InteractionResult result =
                            entry.getHook(ModifierHooks.GENERAL_INTERACT)
                                 .onToolUse(tool, entry, player, InteractionHand.MAIN_HAND, InteractionSource.RIGHT_CLICK);
                    if (result.consumesAction() &&
                        (!RequireUsable ||
                         0 < entry.getHook(ModifierHooks.GENERAL_INTERACT).getUseDuration(tool, entry))){
                        usable.add(i);
                        break;
                    }
                }
            }
            InteractionResult res = s.use(player.level(), player, InteractionHand.MAIN_HAND).getResult();
            if ((InteractionResult.CONSUME == res || InteractionResult.SUCCESS == res) &&
                (!RequireUsable || 0 < s.getUseDuration() && 0 < tool.getStats().get(ToolStats.DRAW_SPEED)))
                usable.add(i);
        }
        if (NoRandomCycle && !usable.isEmpty())
            return weapon_dreams.naturalCycle(usable, lastIndex);
        if (usable.isEmpty())
            return -1;
        else
            return usable.get(player.level().random.nextInt(usable.size()));
    }

    private static boolean shouldInteract(@Nullable LivingEntity player, ToolStack toolStack, InteractionHand hand) {
        IModDataView volatileData = toolStack.getVolatileData();
        if (volatileData.getBoolean(NO_INTERACTION)){
            return false;
        }else if (hand == InteractionHand.OFF_HAND){
            return true;
        }else {
            return player == null || !volatileData.getBoolean(DEFER_OFFHAND) || player.getOffhandItem().isEmpty();
        }
    }
}
