package org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;

public class deepSleepWithRoar extends BattleModifier {
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
        if (null != target && null != attacker && !target.level().isClientSide)
            for (MobEffectInstance mobs : attacker.getActiveEffects())
                if (MobEffectCategory.HARMFUL == mobs.getEffect().getCategory())
                    target.addEffect(mobs);
        return false;
    }

    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        if (null != context.getLivingTarget() && !context.getLivingTarget().level().isClientSide)
            for (MobEffectInstance mobs : context.getAttacker().getActiveEffects())
                if (MobEffectCategory.HARMFUL == mobs.getEffect().getCategory())
                    context.getLivingTarget().addEffect(mobs);
        return knockback;
    }
}
