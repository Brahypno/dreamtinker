package org.dreamtinker.dreamtinker.modifier.Combat;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.dreamtinker.dreamtinker.modifier.base.BaseModifier;
import org.dreamtinker.dreamtinker.register.DreamtinkerEffect;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class SilverNameBee extends BaseModifier {
    public SilverNameBee(){}

    @Override
    public boolean isNoLevels() {
        return true;
    }
    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack){
        if (holder instanceof Player player && isCorrectSlot && !player.hasEffect(DreamtinkerEffect.SilverNameBee.get())) {
            player.addEffect(new MobEffectInstance(DreamtinkerEffect.SilverNameBee.get(), 40, 0, false, false,false));
        }
    }
}
