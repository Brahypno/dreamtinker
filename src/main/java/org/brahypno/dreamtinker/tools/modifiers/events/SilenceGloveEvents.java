package org.brahypno.dreamtinker.tools.modifiers.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.tools.DreamtinkerModifiers;
import org.brahypno.dreamtinker.tools.DreamtinkerTools;
import org.brahypno.dreamtinker.tools.modifiers.tools.silence_glove.WeaponDreams;
import org.brahypno.esotericismtinker.utils.CompactUtils.CuriosCompact;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.hook.ranged.BowAmmoModifierHook;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableBowItem;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableCrossbowItem;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static org.brahypno.dreamtinker.tools.modifiers.events.weaponDreamsEnsureEnds.TAG_LAST_USE;
import static org.brahypno.dreamtinker.tools.modifiers.events.weaponDreamsEnsureEnds.startChosenDisplay;
import static org.brahypno.dreamtinker.tools.modifiers.tools.silence_glove.WeaponDreams.computeProxyCooldownTicks;
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
        if (player == null || player.level().isClientSide)
            return;

        ItemStack silenceGlove = getSilenceGlove(player);
        if (silenceGlove.isEmpty() || !silenceGlove.is(DreamtinkerTools.silence_glove.asItem())){
            return; // No valid glove found
        }

        if (player.getCooldowns().isOnCooldown(DreamtinkerTools.silence_glove.asItem())){
            return; // On cooldown
        }

        selectAndUseTool(player, silenceGlove, event.getHand() == InteractionHand.OFF_HAND);
    }

    /**
     * Retrieves the Silence Glove from the player's main hand or Curios.
     */
    private static ItemStack getSilenceGlove(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        if (!mainHand.isEmpty()){
            return mainHand;
        }
        return CuriosCompact.findPreferredGlove(player);
    }

    /**
     * Selects a usable tool from the glove's inventory and triggers its use.
     */
    private static void selectAndUseTool(Player player, ItemStack silenceGlove, boolean mainHandWasEmpty) {
        ToolStack gloveTool = ToolStack.from(silenceGlove);
        ModifierEntry weaponSlotsEntry = gloveTool.getModifier(DreamtinkerModifiers.Ids.weapon_slots);
        if (weaponSlotsEntry.getLevel() < 1){
            return; // No weapon slots available
        }

        List<ItemStack> storedTools = new ArrayList<>();
        weaponSlotsEntry.getHook(ToolInventoryCapability.HOOK).getAllStacks(gloveTool, weaponSlotsEntry, storedTools);

        boolean requireUsable = gloveTool.getModifier(DreamtinkerModifiers.Ids.weapon_dreams_filter).getLevel() >= 1;
        boolean naturalOrder = gloveTool.getModifier(DreamtinkerModifiers.Ids.weapon_dreams_order).getLevel() >= 1;
        int lastUsedIndex = gloveTool.getPersistentData().contains(TAG_LAST_USE)
                            ? gloveTool.getPersistentData().getInt(TAG_LAST_USE)
                            : -1;

        int selectedSlot = pickUsable(storedTools, player, naturalOrder, requireUsable, lastUsedIndex);
        if (selectedSlot < 0 || selectedSlot >= storedTools.size()){
            return; // No valid tool selected
        }

        ItemStack selectedTool = storedTools.get(selectedSlot);
        if (selectedTool.isEmpty() || !selectedTool.is(TinkerTags.Items.MODIFIABLE)){
            return; // Invalid tool
        }

        // Update last used index for natural order
        if (naturalOrder){
            gloveTool.getPersistentData().putInt(TAG_LAST_USE, selectedSlot);
            gloveTool.updateStack(silenceGlove);
        }

        // Temporarily swap and use the tool
        ItemStack originalMainHand = player.getMainHandItem();
        try {
            REENTRY.set(true);
            player.setItemInHand(InteractionHand.MAIN_HAND, selectedTool);
            player.getInventory().setChanged();
            startChosenDisplay((ServerPlayer) player, selectedSlot, silenceGlove, computeProxyCooldownTicks(gloveTool), mainHandWasEmpty);

            selectedTool.use(player.level(), player, InteractionHand.MAIN_HAND);
        }
        finally {
            REENTRY.set(false);
            // Note: The tool is not automatically restored; rely on game mechanics or other events for cleanup
        }
    }


    private static int pickUsable(
            List<ItemStack> stacks, Player player, boolean NoRandomCycle, boolean RequireUsable, int lastIndex) {
        if (stacks == null || stacks.isEmpty() || player == null)
            return -1;

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
        List<Integer> usable = nonEmpty.stream().filter(index -> isUsable(stacks.get(index), player, RequireUsable))
                                       .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        if (usable.isEmpty())
            return -1;
        if (NoRandomCycle)
            return WeaponDreams.naturalCycle(usable, lastIndex);
        else
            return usable.get(player.level().random.nextInt(usable.size()));
    }

    private static boolean isUsable(ItemStack stack, Player player, boolean requireUsable) {
        ToolStack tool = ToolStack.from(stack);
        if (!shouldInteract(player, tool, InteractionHand.MAIN_HAND)){
            return false;
        }

        // Check general modifier interactions
        for (ModifierEntry entry : tool.getModifierList()) {
            InteractionResult result = entry.getHook(ModifierHooks.GENERAL_INTERACT)
                                            .onToolUse(tool, entry, player, InteractionHand.MAIN_HAND, InteractionSource.RIGHT_CLICK);
            if (result.consumesAction() && (!requireUsable || entry.getHook(ModifierHooks.GENERAL_INTERACT).getUseDuration(tool, entry) > 0)){
                return true;
            }
        }

        // Fallback: Check bow/crossbow specific logic
        InteractionResult result = InteractionResult.FAIL;
        if (stack.getItem() instanceof ModifiableBowItem || stack.getItem() instanceof ModifiableCrossbowItem){
            result = bow_use(player.level(), player, InteractionHand.MAIN_HAND, stack).getResult();
        }
        return (result == InteractionResult.CONSUME || result == InteractionResult.SUCCESS) &&
               (!requireUsable || (stack.getUseDuration() > 0 && tool.getStats().get(ToolStats.DRAW_SPEED) > 0));
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

    public static InteractionResultHolder<ItemStack> bow_use(Level level, Player player, InteractionHand hand, ItemStack bow) {
        ToolStack tool = ToolStack.from(bow);
        if (tool.isBroken()){
            return InteractionResultHolder.fail(bow);
        }else {
            ItemStack ammo = BowAmmoModifierHook.getAmmo(tool, bow, player, ((ProjectileWeaponItem) bow.getItem()).getSupportedHeldProjectiles());
            InteractionResultHolder<ItemStack> override = ForgeEventFactory.onArrowNock(bow, level, player, hand, !ammo.isEmpty());

            if (override != null){
                return override;
            }else if (!player.getAbilities().instabuild && ammo.isEmpty() && !tool.getModifiers().has(TinkerTags.Modifiers.CHARGE_EMPTY_BOW_WITH_DRAWTIME)){
                if (tool.getModifiers().has(TinkerTags.Modifiers.CHARGE_EMPTY_BOW_WITHOUT_DRAWTIME)){
                    return InteractionResultHolder.consume(bow);
                }else {
                    return InteractionResultHolder.fail(bow);
                }
            }else {
                return InteractionResultHolder.consume(bow);
            }
        }
    }
}
