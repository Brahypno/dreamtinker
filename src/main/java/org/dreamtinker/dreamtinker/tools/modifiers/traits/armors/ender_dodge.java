package org.dreamtinker.dreamtinker.tools.modifiers.traits.armors;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.ArmorModifier;
import org.dreamtinker.dreamtinker.utils.DThelper;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.EnderDodgeChance;
import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.EnderDodgetimes;

public class ender_dodge extends ArmorModifier {


    @Override
    public boolean isDamageBlocked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount) {
        LivingEntity holder = context.getEntity();
        Level level = holder.level();
        if (!level.isClientSide() && !(source.getEntity() instanceof LivingEntity) && holder.level().random.nextFloat() < EnderDodgeChance.get()){
            for (int i = 0; i < EnderDodgetimes.get(); ++i) {
                if (DThelper.teleport(holder)){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void modifierOnInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (!isCorrectSlot)
            return;
        if (world.isClientSide)
            return;
        if (holder.isInWater()){
            holder.hurt(new DamageSource(world.damageSources().drown().typeHolder()), 5);
        }
    }
}
