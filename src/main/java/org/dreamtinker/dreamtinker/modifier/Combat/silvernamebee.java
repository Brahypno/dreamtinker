package org.dreamtinker.dreamtinker.modifier.Combat;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.dreamtinker.dreamtinker.modifier.base.baseclass.BattleModifier;
import org.dreamtinker.dreamtinker.register.DreamtinkerEffect;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.AsOneA;

public class silvernamebee extends BattleModifier {
    public silvernamebee(){}

    @Override
    public boolean isNoLevels() {
        return true;
    }
    @Override
    public void modifierOnInventoryTick(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, @NotNull Level world, @NotNull LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, @NotNull ItemStack stack){
        if (holder instanceof Player player && isCorrectSlot && !player.hasEffect(DreamtinkerEffect.SilverNameBee.get())) {
            player.addEffect(new MobEffectInstance(DreamtinkerEffect.SilverNameBee.get(), 40, AsOneA.get(), false, false,false));
        }
    }
}
