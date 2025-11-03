package org.dreamtinker.dreamtinker.utils;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;
import org.dreamtinker.dreamtinker.tools.items.SilenceGlove;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.Optional;

public class CuriosCompact {
    private CuriosCompact() {}

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
        if (st.isEmpty())
            return false;
        Item it = st.getItem();
        // 任选一种判定方式：
        return it instanceof SilenceGlove;
    }
}
