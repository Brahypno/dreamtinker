package org.dreamtinker.dreamtinker.tools.modifiers.events;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerDamageTypes;
import org.dreamtinker.dreamtinker.common.DreamtinkerEffects;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.tools.items.TNTArrow;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;

import java.util.List;

import static org.dreamtinker.dreamtinker.utils.DTModifierCheck.ModifierInHand;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class GeneralShortEvents {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void ExplosionEvent(ExplosionEvent.Detonate event) {
        if (event.isCanceled())
            return;
        Explosion exp = event.getExplosion();
        if (exp.getDamageSource().is(DreamtinkerDamageTypes.force_to_explosion) && null != exp.getDamageSource().getDirectEntity() &&
            exp.getDamageSource().getDirectEntity() instanceof TNTArrow.TNTArrowEntity){
            event.getAffectedEntities().removeIf(Entity::isAlive);
        }
        if (null != exp.getDamageSource().getEntity())
            event.getAffectedEntities()
                 .removeIf(entity -> entity instanceof LivingEntity victim && victim.is(exp.getDamageSource().getEntity()) &&
                                     ModifierInHand(victim, DreamtinkerModifiers.ewige_widerkunft.getId()));

    }

    @SubscribeEvent
    public static void LivingVisibilityEvent(LivingEvent.LivingVisibilityEvent event) {
        if (event.isCanceled())
            return;
        int multi = DTModifierCheck.getEntityModifierNum(event.getEntity(), DreamtinkerModifiers.Ids.golden_face);
        if (0 < multi)
            event.modifyVisibility(Mth.clamp(0.5 * multi + 1.5, 1.0, 4.0));
        int hidden_multi = DTModifierCheck.getEntityModifierNum(event.getEntity(), DreamtinkerModifiers.Ids.hidden_shape);
        if (0 < hidden_multi)
            event.modifyVisibility(0.25 + 0.75 * Math.pow(0.60, hidden_multi));
    }

    @SubscribeEvent
    public static void onChangeTarget(LivingChangeTargetEvent event) {
        if (!(event.getEntity() instanceof Mob mob))
            return;
        if (event.isCanceled())
            return;

        if (mob.hasEffect(DreamtinkerEffects.RealDarkness.get())){
            if (event.getNewTarget() != null){
                event.setCanceled(true);
                mob.setTarget(null);
            }
        }
        boolean is_otto =
                null != event.getNewTarget() && 0 < DTModifierCheck.getEntityModifierNum(event.getNewTarget(), DreamtinkerModifiers.Ids.golden_face);
        if (!is_otto){
            LivingEntity best = findBestRevealedTarget(mob);
            if (best == null)
                return;

            if (best.equals(event.getNewTarget()))
                return;
            event.setNewTarget(best);
        }
    }

    private static double getDetectionRadius(Mob mob) {
        // “发现/追踪范围”最贴近 follow range
        return mob.getAttributeValue(Attributes.FOLLOW_RANGE);
    }

    private static boolean preferDistanceFirst(Mob mob) {
        // 近战优先：没有远程接口就按距离优先
        return !(mob instanceof RangedAttackMob);
    }

    private static LivingEntity findBestRevealedTarget(Mob mob) {
        double radius = getDetectionRadius(mob);
        if (!(radius > 0))
            return null;

        AABB box = mob.getBoundingBox().inflate(radius);

        List<LivingEntity> candidates = mob.level().getEntitiesOfClass(
                LivingEntity.class,
                box,
                e -> e.isAlive()
                     && e != mob
                     && 0 < DTModifierCheck.getEntityModifierNum(e, DreamtinkerModifiers.Ids.golden_face)
                     && mob.canAttack(e)
                     && mob.hasLineOfSight(e) // 仍建议保留：避免隔墙点名
        );

        if (candidates.isEmpty())
            return null;

        boolean distanceFirst = preferDistanceFirst(mob);

        LivingEntity best = null;
        int bestAmp = Integer.MIN_VALUE;
        double bestDist2 = Double.MAX_VALUE;

        for (LivingEntity e : candidates) {
            int amp = DTModifierCheck.getEntityModifierNum(e, DreamtinkerModifiers.Ids.golden_face);
            double dist2 = mob.distanceToSqr(e);

            if (best == null){
                best = e;
                bestAmp = amp;
                bestDist2 = dist2;
                continue;
            }

            if (distanceFirst){
                // 近战：距离优先，同距离再比等级
                if (dist2 < bestDist2 || (dist2 == bestDist2 && amp > bestAmp)){
                    best = e;
                    bestAmp = amp;
                    bestDist2 = dist2;
                }
            }else {
                // 远程：等级优先，同等级再比距离
                if (amp > bestAmp || (amp == bestAmp && dist2 < bestDist2)){
                    best = e;
                    bestAmp = amp;
                    bestDist2 = dist2;
                }
            }
        }

        return best;
    }
}

