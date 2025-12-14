package org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import org.dreamtinker.dreamtinker.utils.TargetTracker;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;

public class loveShooting extends BattleModifier {
    @Override
    public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
        if (projectile instanceof TargetTracker mode){
            mode.dreamtinker$setMode(target -> target instanceof LivingEntity && !(target instanceof Player));
        }
    }
}
