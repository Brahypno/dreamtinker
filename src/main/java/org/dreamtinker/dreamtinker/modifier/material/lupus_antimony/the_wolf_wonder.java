package org.dreamtinker.dreamtinker.modifier.material.lupus_antimony;

import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.registries.ForgeRegistries;
import org.dreamtinker.dreamtinker.modifier.base.baseclass.BattleModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.*;

public class the_wolf_wonder extends BattleModifier {
    private static final int minDuration = TheWolfWonderEffectMinTime.get() * 20;
    private static final int maxDuration = TheWolfWonderEffectMaxTime.get() * 20;

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        LivingEntity target = context.getLivingTarget();
        if (target == null)
            return knockback;
        applyRandomEffects(target, context.getAttacker());

        return knockback;
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
        if (target == null)
            return false;
        applyRandomEffects(target, attacker);
        return false;
    }

    private static void applyRandomEffects(LivingEntity target, LivingEntity attacker) {
        RandomSource rand = target.getRandom();
        List<MobEffect> negatives =
                ForgeRegistries.MOB_EFFECTS.getValues().stream().filter(e -> e.getCategory() == MobEffectCategory.HARMFUL).collect(Collectors.toList());
        if (negatives.isEmpty())
            return;

        for (int i = negatives.size() - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            Collections.swap(negatives, i, j);
        }

        // 取前 N 项
        List<MobEffect> pick = negatives.subList(0, Math.min(TheWolfWonderEffectNum.get(), negatives.size()));
        //debugEffects(pick);

        List<MobEffectInstance> instances = new ArrayList<>();
        for (MobEffect effect : pick) {
            // 随机持续时长与等级
            int duration = minDuration + rand.nextInt(Math.max(1, maxDuration - minDuration + 1));
            int amplifier = rand.nextInt(TheWolfWonderEffectAmplifier.get() + 1);

            MobEffectInstance inst = new MobEffectInstance(effect, duration, amplifier, false, true);
            target.forceAddEffect(inst, target);
            instances.add(inst);
        }
        if (attacker != null && rand.nextInt(666) < TheWolfWonderSurpriseNumber.get()){
            for (MobEffectInstance inst : instances) {
                attacker.addEffect(new MobEffectInstance(inst.getEffect(), inst.getDuration(), inst.getAmplifier(), inst.isAmbient(), inst.isVisible()));
            }
        }
    }
}
