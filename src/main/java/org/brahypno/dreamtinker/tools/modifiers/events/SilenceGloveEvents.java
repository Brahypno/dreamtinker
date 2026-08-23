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
import org.brahypno.dreamtinker.tools.modifiers.tools.silence_glove.WeaponDreams;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.ranged.BowAmmoModifierHook;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableBowItem;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableCrossbowItem;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static slimeknights.tconstruct.library.tools.item.IModifiable.DEFER_OFFHAND;
import static slimeknights.tconstruct.library.tools.item.IModifiable.NO_INTERACTION;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class SilenceGloveEvents {
    /* ========== 右键方块：从饰品栏借出后重放原版物品交互 ========== */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (WeaponDreams.isDispatchingBorrowedAction()
            || event.getHand() != InteractionHand.MAIN_HAND
            || !WeaponDreams.canBorrowFromCurio(event.getEntity()))
            return;

        if (event.getLevel().isClientSide){
            // The block-use packet has already been queued. Returning success here
            // prevents the empty client hand from also sending a second use-item
            // packet for the same click; the server replays both vanilla phases.
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        if (!(event.getEntity() instanceof ServerPlayer player))
            return;

        WeaponDreams.BorrowContext borrow = WeaponDreams.beginBorrow(player);
        if (borrow == null || !borrow.proxyInCurio())
            return;

        int selectedSlot = pickUsable(
                borrow.frames(), player, borrow.naturalOrder(),
                borrow.requireUsable(), borrow.lastUsedIndex()
        );
        // useOn() has no side-effect-free general predicate. If the ordinary
        // right-click filter finds nothing (for example a hoe), keep the same
        // non-empty fallback used by the left-click selector.
        if (selectedSlot < 0){
            selectedSlot = WeaponDreams.chooseIndex(
                    player.level(), borrow.frames(),
                    player.level().getBlockState(event.getPos()),
                    borrow.naturalOrder(), false, borrow.lastUsedIndex()
            );
        }
        if (selectedSlot < 0 || borrow.borrow(selectedSlot).isEmpty())
            return;

        InteractionResult result = WeaponDreams.dispatchBorrowedAction(() -> {
            ItemStack borrowed = player.getMainHandItem();
            InteractionResult blockResult = player.gameMode.useItemOn(
                    player, player.level(), borrowed,
                    InteractionHand.MAIN_HAND, event.getHitVec()
            );
            if (blockResult == InteractionResult.PASS){
                return player.gameMode.useItem(
                        player, player.level(), player.getMainHandItem(), InteractionHand.MAIN_HAND
                );
            }
            return blockResult;
        });

        weaponDreamsEnsureEnds.syncBorrowedUseState(player);
        event.setCancellationResult(result);
        event.setCanceled(true);
    }

    /* ========== 右键空气/物品：随机 use ========== */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (WeaponDreams.isDispatchingBorrowedAction())
            return;
        Player player = event.getEntity();
        if (player == null || player.level().isClientSide)
            return;

        if (!(player instanceof ServerPlayer serverPlayer))
            return;

        selectAndUseTool(event, serverPlayer);
    }

    /**
     * Selects a usable tool from the glove's inventory and triggers its use.
     */
    private static void selectAndUseTool(PlayerInteractEvent.RightClickItem event, ServerPlayer player) {
        WeaponDreams.BorrowContext borrow = WeaponDreams.beginBorrow(player);
        if (borrow == null)
            return;

        int selectedSlot = pickUsable(
                borrow.frames(), player, borrow.naturalOrder(),
                borrow.requireUsable(), borrow.lastUsedIndex()
        );
        if (selectedSlot < 0){
            return; // No valid tool selected
        }

        ItemStack selectedTool = borrow.borrow(selectedSlot);
        if (selectedTool.isEmpty()){
            return; // Invalid tool
        }

        InteractionResult result = WeaponDreams.dispatchBorrowedAction(
                () -> player.gameMode.useItem(
                        player, player.level(), player.getMainHandItem(), InteractionHand.MAIN_HAND
                )
        );
        weaponDreamsEnsureEnds.syncBorrowedUseState(player);
        event.setCancellationResult(result);
        event.setCanceled(true);
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

        if (!requireUsable)
            return true;

        // Do not call onToolUse() as a predicate: it is the real action hook and
        // may mutate the tool or player before the selected stack is borrowed.
        for (ModifierEntry entry : tool.getModifierList()) {
            if (entry.getHook(ModifierHooks.GENERAL_INTERACT).getUseDuration(tool, entry) > 0){
                return true;
            }
        }

        // Fallback: Check bow/crossbow specific logic
        InteractionResult result = InteractionResult.FAIL;
        if (stack.getItem() instanceof ModifiableBowItem || stack.getItem() instanceof ModifiableCrossbowItem){
            result = bow_use(player.level(), player, InteractionHand.MAIN_HAND, stack).getResult();
        }
        return (result == InteractionResult.CONSUME || result == InteractionResult.SUCCESS)
               && stack.getUseDuration() > 0
               && tool.getStats().get(ToolStats.DRAW_SPEED) > 0;
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
