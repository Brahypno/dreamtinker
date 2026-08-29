package org.brahypno.dreamtinker.library.compat.goety;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.brahypno.dreamtinker.tools.DreamtinkerModifiers;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public final class GoetyMaterialUtil {
    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    private GoetyMaterialUtil() {}

    public static int getUsableLevel(ItemStack stack, ModifierId modifierId) {
        ToolStack tool = getUsableTool(stack);
        return tool == null ? 0 : tool.getModifierLevel(modifierId);
    }

    @Nullable
    public static ToolStack getUsableTool(ItemStack stack) {
        if (stack.isEmpty() || !stack.is(TinkerTags.Items.MODIFIABLE)){
            return null;
        }
        ToolStack tool = ToolStack.from(stack);
        return tool.isBroken() ? null : tool;
    }

    public static boolean hasFullDarkMetalSet(LivingEntity entity) {
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            if (getUsableLevel(entity.getItemBySlot(slot), DreamtinkerModifiers.Ids.goety_dark_metal_defense) <= 0){
                return false;
            }
        }
        return true;
    }
}
