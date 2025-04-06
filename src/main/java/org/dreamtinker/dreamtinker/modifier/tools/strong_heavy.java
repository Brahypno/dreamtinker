package org.dreamtinker.dreamtinker.modifier.tools;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.dreamtinker.dreamtinker.modifier.base.baseclass.BattleModifier;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class strong_heavy extends BattleModifier {

    private static final double SPEED_THRESHOLD = 0.2; // 速度阈值，大于此值时不触发

    @Override
    public void onInventoryTick(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, @NotNull Level world, @NotNull LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, @NotNull ItemStack stack){
        if (holder instanceof Player player && isCorrectSlot) {

            // 获取玩家速度（上一 tick 位置变化）
            double speed = player.getDeltaMovement().length();

            // 处理虚弱效果
            if (player.hasEffect(MobEffects.DAMAGE_BOOST)||player.hasEffect(MobEffects.MOVEMENT_SPEED)||(isAllowedVehicle(player) && SPEED_THRESHOLD < speed)) {
                player.removeEffect(MobEffects.WEAKNESS);
            } else {
                MobEffectInstance weaknessEffect = player.getEffect(MobEffects.WEAKNESS);
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 2, true, false));

            }
        }
    }

    private boolean isAllowedVehicle(Player player) {
        return player.getVehicle() != null && player.getVehicle().isAlive();
    }
    public boolean isNoLevels() {
        return true;
    }
}
