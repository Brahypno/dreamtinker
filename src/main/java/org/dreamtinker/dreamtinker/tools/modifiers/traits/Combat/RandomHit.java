package org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;

public class RandomHit extends BattleModifier {
    private float lower = 1f, higher = 1f;

    public RandomHit(float lower, float higher) {
        this.lower = lower;
        this.higher = higher;
    }

    @Override
    public float onGetMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        return damage * random_hit_value(context.getAttacker().level().random, modifier.getLevel());
    }

    @Override
    public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
        projectile.setDeltaMovement(projectile.getDeltaMovement()
                                              .scale(random_hit_value(shooter.level().random, modifier.getLevel())));
    }

    private float random_hit_value(RandomSource rs, int level) {
        if (rs.nextFloat() < 0.001)
            return rs.nextBoolean() ? 10.0f : 0.1f;
        if (rs.nextFloat() < 0.1)
            return 2.0f;
        return Mth.nextFloat(rs, lower * (1.1f - level * 0.1f), Math.nextUp((higher * (level + 1)) / 2));
    }
}
