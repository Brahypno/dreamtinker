package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.nigrescence_antimony;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.ArmorModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.OuroboricHourglassMutiply;

public class ouroboric_hourglass extends ArmorModifier {

    public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        // 基数：装备者的最大生命值
        float Base = context.getEntity().getMaxHealth();
        // 参数
        float p = 0.8f;
        Double X = OuroboricHourglassMutiply.get();
        float final_amount;

        if (amount <= Base){
            // 小于等于基数：放大段
            final_amount = Base * (float) Math.pow(amount / Base, p);
        }else if (amount <= X * Base){
            // 基数到 X 倍之间：平滑线性减伤
            float slope = (float) ((float) Math.log(amount / Base) / (X - 1));
            final_amount = Base + (amount - Base) * slope;
        }else {
            // 超过 X 倍：对数压制
            final_amount = (Base * (float) Math.log(amount / Base / X));
        }
        if (Integer.MAX_VALUE <= amount)
            return 0;

        return final_amount;
    }
}
