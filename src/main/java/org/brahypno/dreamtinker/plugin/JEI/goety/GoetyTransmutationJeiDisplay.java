package org.brahypno.dreamtinker.plugin.JEI.goety;

import net.minecraft.world.item.ItemStack;
import org.brahypno.dreamtinker.library.compat.goety.GoetyMaterialTransmutationRecipe;

import java.util.List;

/**
 * One center item and pedestal cost. Input and output lists use the same order for JEI cycling.
 */
public record GoetyTransmutationJeiDisplay(
        GoetyMaterialTransmutationRecipe recipe, List<ItemStack> inputs, List<ItemStack> outputs, int cost) {}
