package org.brahypno.dreamtinker.plugin.JEI.goety;

import com.Polarice3.Goety.common.crafting.RitualRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.SlotType;

import java.util.List;

/**
 * One center item and pedestal cost. Input and output lists use the same order for JEI cycling.
 */
public record GoetyTransmutationJeiDisplay(
        RitualRecipe recipe, List<ItemStack> inputs, List<ItemStack> outputs,
        List<Ingredient> pedestalInputs, @Nullable ModifierId modifier,
        @Nullable SlotType.SlotCount slots, @Nullable IntRange level) {}
