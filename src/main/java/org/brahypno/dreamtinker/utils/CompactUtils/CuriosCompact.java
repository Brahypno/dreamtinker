package org.brahypno.dreamtinker.utils.CompactUtils;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;
import org.brahypno.dreamtinker.tools.DreamtinkerTools;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import static org.brahypno.esotericismtinker.EsotericismTinker.configCompactDisabled;

public class CuriosCompact {
    private CuriosCompact() {}

    public static ItemStack findFirstItemWithModifier(Player player, ModifierId id) {
        if (!ModList.get().isLoaded("curios"))
            return ItemStack.EMPTY;
        return doFindModifierItem(player, id).orElse(ItemStack.EMPTY);
    }

    public static int getCurioModifierNumber(Player player, ModifierId id) {
        if (!ModList.get().isLoaded("curios"))
            return 0;
        return doFindModifierNum(player, id);
    }

    public static List<ItemStack> getCurioStacks(Player player) {
        if (!ModList.get().isLoaded("curios"))
            return List.of();
        return doFindListItemStack(player);
    }

    public static void damageAllCurios(LivingEntity target, int amount, Predicate<ItemStack> filter) {
        if (!ModList.get().isLoaded("curios") || configCompactDisabled("curios")
            || target.level().isClientSide || amount <= 0)
            return;
        doDamageAllCurios(target, amount, filter);
    }

    private static int doFindModifierNum(Player player, ModifierId id) {
        return CuriosApi.getCuriosInventory(player).map(h -> {
            int result = 0;
            for (ICurioStacksHandler handler : h.getCurios().values()) {
                IDynamicStackHandler stacks = handler.getStacks();
                for (int i = 0; i < stacks.getSlots(); i++) {
                    ItemStack stack = stacks.getStackInSlot(i);
                    if (stack.is(TinkerTags.Items.MODIFIABLE))
                        result += ModifierUtil.getModifierLevel(stack, id);
                }
            }
            return result;
        }).orElse(0);
    }

    private static List<ItemStack> doFindListItemStack(Player player) {
        return CuriosApi.getCuriosInventory(player).map(h -> {
            List<ItemStack> result = new ArrayList<>();
            for (ICurioStacksHandler handler : h.getCurios().values()) {
                IDynamicStackHandler stacks = handler.getStacks();
                for (int i = 0; i < stacks.getSlots(); i++) {
                    ItemStack stack = stacks.getStackInSlot(i);
                    if (stack.is(TinkerTags.Items.MODIFIABLE))
                        result.add(stack);
                }
            }
            return result;
        }).orElse(List.of());
    }

    private static void doDamageAllCurios(LivingEntity target, int amount, Predicate<ItemStack> filter) {
        CuriosApi.getCuriosInventory(target).ifPresent(inv -> {
            for (Map.Entry<String, ICurioStacksHandler> entry : inv.getCurios().entrySet()) {
                IDynamicStackHandler stacks = entry.getValue().getStacks();
                for (int i = 0; i < stacks.getSlots(); i++) {
                    ItemStack stack = stacks.getStackInSlot(i);
                    if (!stack.isEmpty() && stack.isDamageableItem() && (filter == null || filter.test(stack))){
                        stack.hurtAndBreak(amount, target, entity -> {});
                    }
                }
            }
        });
    }

    private static Optional<ItemStack> doFindModifierItem(Player player, ModifierId id) {
        LazyOptional<ICuriosItemHandler> opt = CuriosApi.getCuriosInventory(player);
        return Optional.of(opt.map(h -> {
            for (ICurioStacksHandler handler : h.getCurios().values()) {
                IDynamicStackHandler stacks = handler.getStacks();
                for (int i = 0; i < stacks.getSlots(); i++) {
                    ItemStack stack = stacks.getStackInSlot(i);
                    if (stack.is(TinkerTags.Items.MODIFIABLE) && 0 < ModifierUtil.getModifierLevel(stack, id))
                        return stack;
                }
            }
            return ItemStack.EMPTY;
        }).orElse(ItemStack.EMPTY));
    }

    /**
     * 优先从 Curios 的 hands/hand 槽找“静默手套”，找不到返回 ItemStack.EMPTY
     */
    public static ItemStack findPreferredGlove(Player player) {
        if (!ModList.get().isLoaded("curios"))
            return ItemStack.EMPTY;
        return doFindPreferredGlove(player).orElse(ItemStack.EMPTY);
    }

    // 单独拆出来，避免类加载时引用 Curios API
    private static Optional<ItemStack> doFindPreferredGlove(Player player) {
        LazyOptional<ICuriosItemHandler> opt = CuriosApi.getCuriosInventory(player);
        return Optional.of(opt.map(h -> {
            // 1) 先搜 "hands" 槽
            ItemStack fromHands = getFirstFromSlot(h, "hands");
            if (!fromHands.isEmpty())
                return fromHands;
            // 3) 兜底：遍历全部槽找第一件符合的
            for (String id : h.getCurios().keySet()) {
                ItemStack st = getFirstFromSlot(h, id);
                if (isSilentGlove(st))
                    return st;
            }
            return ItemStack.EMPTY;
        }).orElse(ItemStack.EMPTY));
    }

    private static ItemStack getFirstFromSlot(ICuriosItemHandler h, String slotId) {
        java.util.Optional<ICurioStacksHandler> oh = h.getStacksHandler(slotId);
        if (oh.isEmpty())
            return ItemStack.EMPTY;
        ICurioStacksHandler sh = oh.get();
        IDynamicStackHandler stacks = sh.getStacks();
        for (int i = 0; i < sh.getSlots(); i++) {
            ItemStack st = stacks.getStackInSlot(i);
            if (isSilentGlove(st))
                return st;
        }
        return ItemStack.EMPTY;
    }


    private static boolean isSilentGlove(ItemStack st) {
        return st.is(DreamtinkerTools.silence_glove.asItem());
    }
}
