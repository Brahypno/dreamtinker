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
import org.dreamtinker.dreamtinker.utils.CuriosCompact;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

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

        ItemStack tool;
        ItemStack proxy = player.getMainHandItem();
        if (proxy.isEmpty())
            tool = CuriosCompact.findPreferredGlove(player);
        else
            tool = proxy.copy();

        if (!(tool.getItem() instanceof SilenceGlove))
            return;
        if (player.getCooldowns().isOnCooldown(DreamtinkerTools.silence_glove.asItem()))
            return;

        if (!player.level().isClientSide){
            ToolStack silenceGlove = ToolStack.from(tool);
            ModifierEntry entry = silenceGlove.getModifier(DreamtinkerModifiers.Ids.weapon_slots);
            if (entry.getLevel() < 1)
                return;
            ItemStack chosen;
            List<ItemStack> itemFrames = new ArrayList<>();
            entry.getHook(ToolInventoryCapability.HOOK).getAllStacks(silenceGlove, entry, itemFrames);

            OptionalInt idxOpt = pickUsable(itemFrames, player);
            int slot;
            if (idxOpt.isPresent()){
                slot = idxOpt.getAsInt();
                chosen = itemFrames.get(slot);
            }else
                return;

            if (chosen.isEmpty() || !chosen.is(TinkerTags.Items.MODIFIABLE))
                return;
            ItemStack proxySnap = proxy.copy();

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

    private static OptionalInt pickUsable(List<ItemStack> stacks, Player player) {
        int chosen = -1;
        int seen = 0; // 已遇到的非空数量
        for (int i = 0; i < stacks.size(); i++) {
            ItemStack s = stacks.get(i);
            ToolStack tool = ToolStack.from(s);
            if (!s.isEmpty()){ // 等价于“不是空气/空堆”
                boolean verify = false;
                if (shouldInteract(player, tool, InteractionHand.MAIN_HAND)){
                    for (ModifierEntry entry : tool.getModifierList()) {
                        InteractionResult result =
                                ((GeneralInteractionModifierHook) entry.getHook(ModifierHooks.GENERAL_INTERACT)).onToolUse(tool, entry, player,
                                                                                                                           InteractionHand.MAIN_HAND,
                                                                                                                           InteractionSource.RIGHT_CLICK);
                        if (result.consumesAction()){
                            verify = true;
                            break;
                        }
                    }
                }
                InteractionResult res = s.use(player.level(), player, InteractionHand.MAIN_HAND).getResult();

                if (InteractionResult.CONSUME == res || InteractionResult.SUCCESS == res || verify){
                    seen++;
                    // 以 1/seen 的概率替换当前选择
                    if (player.level().random.nextInt(seen) == 0){
                        chosen = i;
                    }
                }
            }
        }
        return chosen >= 0 ? OptionalInt.of(chosen) : OptionalInt.empty();
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
