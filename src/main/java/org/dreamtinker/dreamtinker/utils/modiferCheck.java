package org.dreamtinker.dreamtinker.utils;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;

import static net.minecraft.world.item.Items.AIR;

public class modiferCheck {
    private static final EquipmentSlot[] slots= new EquipmentSlot[]{EquipmentSlot.FEET,EquipmentSlot.LEGS,EquipmentSlot.CHEST,EquipmentSlot.HEAD,EquipmentSlot.OFFHAND,EquipmentSlot.MAINHAND};

    private static int getModifierlevel(LivingEntity entity,ModifierId id,EquipmentSlot slot){
        if (entity != null) {
            if(entity.getItemBySlot(slot).getItem().equals(AIR)) return 0;
            ToolStack toolStack = ToolStack.from(entity.getItemBySlot(slot));
            if (!toolStack.isBroken()) {
                return ModifierUtil.getModifierLevel(entity.getItemBySlot(slot), id);
            }

        }
        return 0;
    }
    public static int getMainhandModifierlevel(LivingEntity entity, ModifierId modifierId) {
        return getModifierlevel(entity,modifierId,EquipmentSlot.MAINHAND);

    }

    public static int getOffhandModifierlevel(LivingEntity entity, ModifierId modifierId) {
        return getModifierlevel(entity,modifierId,EquipmentSlot.OFFHAND);
    }
    public static boolean ModifierInHand(LivingEntity entity, ModifierId modifierId) {
        return 0<getMainhandModifierlevel(entity, modifierId) || 0<getOffhandModifierlevel(entity, modifierId);
    }

    public static int getHeadModifierlevel(LivingEntity entity, ModifierId modifierId) {
        return getModifierlevel(entity,modifierId,EquipmentSlot.HEAD);
    }

    public static int getChestModifierlevel(LivingEntity entity, ModifierId modifierId) {
        return getModifierlevel(entity,modifierId,EquipmentSlot.CHEST);
    }

    public static int getLegModifierlevel(LivingEntity entity, ModifierId modifierId) {
        return getModifierlevel(entity,modifierId,EquipmentSlot.LEGS);
    }

    public static int getFeetModifierlevel(LivingEntity entity, ModifierId modifierId) {
        return getModifierlevel(entity,modifierId,EquipmentSlot.FEET);
    }
    public static boolean ModifierInBody(LivingEntity entity, ModifierId modifierId) {
        return 0<getHeadModifierlevel(entity, modifierId) || 0<getChestModifierlevel(entity, modifierId)||0<getLegModifierlevel(entity, modifierId) || 0<getFeetModifierlevel(entity, modifierId);
    }

    public static boolean haveModifierIn(LivingEntity entity, ModifierId modifierId) {
        return ModifierInBody(entity,modifierId)||ModifierInHand(entity, modifierId);
    }


    @Nullable
    public static ToolStack getToolWithModifier(LivingEntity entity, ModifierId modifierId){
        for(EquipmentSlot slot:slots){
            if(0<getModifierlevel(entity,modifierId,slot)){
                return ToolStack.from(entity.getItemBySlot(slot));
            }
        }
        return null;
    }

}
