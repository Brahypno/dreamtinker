package org.dreamtinker.dreamtinker.library.recipe.virtual;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.ingredient.EntityIngredient;

import javax.annotation.Nullable;
import java.util.List;

public record WorldRitualEntry(
        Trigger trigger,                          // 触发类型（仅用于显示）
        @Nullable Ingredient catalyst,            // 催化物（如 蓝冰 / 羽毛 / 末影珍珠 / 打火石）
        @Nullable FluidStack fluid,               // 需要的流体（如 水 1000mB）
        @Nullable Ingredient needBlocksAsItems, // 需要附近存在/被替换的方块（用物品图标展示）
        @Nullable ItemStack resultItem,           // 产物（物品）
        @Nullable ItemStack resultBlockIcon,      // 产物（方块，用图标展示）
        @Nullable EntityIngredient entityIngredient,
        @Nullable List<Integer> moonPhases,              // 月相 0~7
        @Nullable Boolean daytime,                // 是否要求白天
        @Nullable Integer minY,                   // 最低高度（如 > build height）
        @Nullable Integer radius,                 // 搜索/影响半径
        @Nullable Double chance,                  // 成功概率（0~1）
        @Nullable String text,             // 是否需要水下环境
        @Nullable Boolean drowning                // 是否需要处于溺水状态
) {
    public enum Trigger {
        ITEM_IN_FLUID,       // 物品进入流体（例：蓝冰扔水里）
        KILL_ENTITY,         // 击杀实体（例：白天极限高度击杀凋零骷髅）
        BREED_ENTITY,        // 生物繁殖（周围有羽毛掉落物）
        ITEM_OUT_OF_WORLD,   // 物品出界/虚空（末影珍珠丢进虚空）
        USE_ITEM,  // 在水下使用物品（打火石点燃海带）
        FORTUNE_LOOTING,
        HIT_ENTITY,
    }
}
