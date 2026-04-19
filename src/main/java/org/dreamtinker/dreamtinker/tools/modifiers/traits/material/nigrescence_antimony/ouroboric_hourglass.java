package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.nigrescence_antimony;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.ArmorModifier;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.technical.SlotInChargeModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.OuroboricHourglassMultiply;

public class ouroboric_hourglass extends ArmorModifier {
    private static final TinkerDataCapability.TinkerDataKey<SlotInChargeModule.SlotInCharge> SLOT_KEY =
            TinkerDataCapability.TinkerDataKey.of(Dreamtinker.getLocation("absorption_defense"));

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addModule(new SlotInChargeModule(SLOT_KEY));
        super.registerHooks(hookBuilder);
    }

    @Override
    public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
        int level = SlotInChargeModule.getLevel(context.getTinkerData(), SLOT_KEY, slotType);
        if (0 < level){
            // 基数：装备者的最大生命值
            float Base = context.getEntity().getMaxHealth();
            // 参数
            float p = 0.8f;
            Double X = OuroboricHourglassMultiply.get();
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
                final_amount = ((float) Math.log(amount / Base / X));
            }
            return final_amount;
        }

        return amount;
    }
}
