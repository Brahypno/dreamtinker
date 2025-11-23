package org.dreamtinker.dreamtinker.utils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;

public class DTModifierCheck {
    public static final EquipmentSlot[] slots =
            new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD, EquipmentSlot.OFFHAND, EquipmentSlot.MAINHAND};

    public static int getModifierLevel(@NotNull LivingEntity entity, ModifierId id, EquipmentSlot slot) {
        if (!(entity.getItemBySlot(slot).getItem() instanceof IModifiable))
            return 0;
        ToolStack toolStack = ToolStack.from(entity.getItemBySlot(slot));
        if (!toolStack.isBroken())
            return ModifierUtil.getModifierLevel(entity.getItemBySlot(slot), id);
        return 0;
    }

    public static int getMainhandModifierLevel(LivingEntity entity, ModifierId modifierId) {
        return getModifierLevel(entity, modifierId, EquipmentSlot.MAINHAND);

    }

    public static int getOffhandModifierLevel(LivingEntity entity, ModifierId modifierId) {
        return getModifierLevel(entity, modifierId, EquipmentSlot.OFFHAND);
    }

    public static boolean ModifierInHand(LivingEntity entity, ModifierId modifierId) {
        return 0 < getMainhandModifierLevel(entity, modifierId) || 0 < getOffhandModifierLevel(entity, modifierId);
    }

    public static int getHeadModifierLevel(LivingEntity entity, ModifierId modifierId) {
        return getModifierLevel(entity, modifierId, EquipmentSlot.HEAD);
    }

    public static int getChestModifierLevel(LivingEntity entity, ModifierId modifierId) {
        return getModifierLevel(entity, modifierId, EquipmentSlot.CHEST);
    }

    public static int getLegModifierLevel(LivingEntity entity, ModifierId modifierId) {
        return getModifierLevel(entity, modifierId, EquipmentSlot.LEGS);
    }

    public static int getFeetModifierLevel(LivingEntity entity, ModifierId modifierId) {
        return getModifierLevel(entity, modifierId, EquipmentSlot.FEET);
    }

    public static boolean ModifierInBody(LivingEntity entity, ModifierId modifierId) {
        return 0 < getHeadModifierLevel(entity, modifierId) || 0 < getChestModifierLevel(entity, modifierId) ||
               0 < getLegModifierLevel(entity, modifierId) || 0 < getFeetModifierLevel(entity, modifierId);
    }

    public static boolean ModifierALLBody(LivingEntity entity, ModifierId modifierId) {
        return 0 < getHeadModifierLevel(entity, modifierId) && 0 < getChestModifierLevel(entity, modifierId) &&
               0 < getLegModifierLevel(entity, modifierId) && 0 < getFeetModifierLevel(entity, modifierId);
    }

    public static boolean haveModifierIn(LivingEntity entity, ModifierId modifierId) {
        return ModifierInBody(entity, modifierId) || ModifierInHand(entity, modifierId);
    }


    @Nullable
    public static ToolStack getToolWithModifier(LivingEntity entity, ModifierId modifierId) {
        for (EquipmentSlot slot : slots)
            if (0 < getModifierLevel(entity, modifierId, slot))
                return ToolStack.from(entity.getItemBySlot(slot));

        return null;
    }

    @Nullable
    public static ToolStack getPossibleToolWithModifier(LivingEntity entity, ModifierId modifierId) {
        for (EquipmentSlot slot : slots)
            if (0 < getModifierLevel(entity, modifierId, slot))
                return ToolStack.from(entity.getItemBySlot(slot));
        if (entity instanceof Player player)
            for (ItemStack item : player.getInventory().items)
                if (0 < getItemModifierNum(item, modifierId))
                    return ToolStack.from(item);

        return null;
    }

    public static int getItemModifierNum(ItemStack stack, TagKey<Modifier> tag) {
        int matched = 0;
        if (null != stack && !stack.isEmpty() && stack.getItem() instanceof IModifiable){
            ToolStack toolStack = ToolStack.from(stack);
            for (ModifierEntry modifier : toolStack.getModifiers()) {
                matched += modifier.getModifier().is(tag) ? 1 : 0;
            }
        }
        return matched;
    }

    public static int getItemModifierNum(ItemStack stack, ModifierId id) {
        int matched = 0;
        if (null != stack && !stack.isEmpty() && stack.getItem() instanceof IModifiable){
            ToolStack toolStack = ToolStack.from(stack);
            matched += toolStack.getModifier(id).getLevel();
        }
        return matched;
    }

    public static int getEntityBodyModifierNum(LivingEntity entity, ModifierId id) {
        int matched = 0;
        matched += getHeadModifierLevel(entity, id);
        matched += getChestModifierLevel(entity, id);
        matched += getLegModifierLevel(entity, id);
        matched += getFeetModifierLevel(entity, id);
        return matched;
    }

    public static int getEntityModifierNum(LivingEntity entity, ModifierId id) {
        int matched = 0;
        matched += getEntityBodyModifierNum(entity, id);
        matched += getMainhandModifierLevel(entity, id);
        matched += getOffhandModifierLevel(entity, id);
        return matched;
    }

    public static float getPersistentTagValue(LivingEntity entity, ModifierId modifierId, ResourceLocation tag) {
        float value = 0;
        for (EquipmentSlot slot : slots) {
            int level = getModifierLevel(entity, modifierId, slot);
            if (0 < level){
                ToolStack tool = ToolStack.from(entity.getItemBySlot(slot));
                value += tool.getPersistentData().getInt(tag) * level;
            }
        }
        return value;
    }

    public static void resetPersistentTagValue(LivingEntity entity, ResourceLocation tag) {
        for (EquipmentSlot slot : slots) {
            ItemStack itemStack = entity.getItemBySlot(slot);
            if (!(itemStack.getItem() instanceof IModifiable))
                continue;

            ToolStack toolStack = ToolStack.from(itemStack);
            toolStack.getPersistentData().remove(tag);
            toolStack.updateStack(itemStack);
        }
    }


}
