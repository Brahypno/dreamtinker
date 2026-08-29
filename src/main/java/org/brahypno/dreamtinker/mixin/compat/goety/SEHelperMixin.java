package org.brahypno.dreamtinker.mixin.compat.goety;

import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.brahypno.dreamtinker.library.compat.goety.GoetyMaterialUtil;
import org.brahypno.dreamtinker.tools.DreamtinkerModifiers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

@Mixin(value = SEHelper.class, remap = false)
public abstract class SEHelperMixin {
    @Unique
    private static final EquipmentSlot[] DREAMTINKER$ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    @Inject(method = "soulDiscount", at = @At("RETURN"), cancellable = true, remap = false)
    private static void dreamtinker$applyMaterialSoulDiscount(
            LivingEntity entity,
            CallbackInfoReturnable<Float> cir) {
        float extraDiscount = 0;
        for (EquipmentSlot slot : DREAMTINKER$ARMOR_SLOTS) {
            ItemStack stack = entity.getItemBySlot(slot);
            ToolStack tool = GoetyMaterialUtil.getUsableTool(stack);
            if (tool == null){
                continue;
            }
            int darkLevel = tool.getModifierLevel(DreamtinkerModifiers.Ids.goety_dark_metal_defense);
            extraDiscount += darkLevel * 0.03f;

            int cursedLevel = Math.min(2, tool.getModifierLevel(DreamtinkerModifiers.Ids.goety_cursed_metal));
            extraDiscount += cursedLevel * 0.01f;
        }
        cir.setReturnValue(Math.max(0, cir.getReturnValueF() - extraDiscount));
    }
}
