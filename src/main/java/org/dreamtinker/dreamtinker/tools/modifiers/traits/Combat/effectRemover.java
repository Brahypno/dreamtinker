package org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class effectRemover extends BattleModifier {
    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
        if (null != target){
            removeEffects(target, modifier.getLevel());
        }
        return false;
    }

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        if (null != context.getLivingTarget()){
            removeEffects(context.getLivingTarget(), modifier.getLevel());
        }
        return knockback;
    }

    private void removeEffects(LivingEntity target, int count) {
        List<MobEffectInstance> all = new ArrayList<>(target.getActiveEffects());
        if (all.isEmpty()){
            return;
        }

        Collections.shuffle(all, new java.util.Random(target.level().random.nextLong()));

        int removeCount = Math.min(count, all.size());
        for (int i = 0; i < removeCount; i++) {
            MobEffectInstance inst = all.get(i);
            target.removeEffect(inst.getEffect());
        }
    }

}
