package org.brahypno.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.brahypno.dreamtinker.utils.TargetTracker;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class ViewTracing extends NoLevelsModifier implements ProjectileLaunchModifierHook {
    /**
     * 返回一个 Predicate，只匹配 viewer 当前目视射线命中的那个实体。
     * 若当前没有看到实体，则该 Predicate 永远返回 false。
     */
    public static Predicate<Entity> lookedEntityPredicate(LivingEntity viewer, double range) {
        Entity target = getLookedEntity(viewer, range);
        return e -> e != null && e == target;
    }

    /**
     * 获取 viewer 当前目视射线最匹配的实体。
     * 没有则返回 null。
     */
    public static Entity getLookedEntity(LivingEntity viewer, double range) {
        Vec3 eyePos = viewer.getEyePosition();
        Vec3 look = viewer.getLookAngle();
        Vec3 end = eyePos.add(look.scale(range));

        // 先做一次方块射线，实体不能在方块遮挡之后
        HitResult blockHit = viewer.level().clip(new ClipContext(
                eyePos,
                end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                viewer
        ));

        double maxDistanceSq = range * range;
        if (blockHit.getType() != HitResult.Type.MISS){
            maxDistanceSq = blockHit.getLocation().distanceToSqr(eyePos);
        }

        // 搜索范围：从眼睛到终点的包围盒，再稍微膨胀一点，便于“尽量匹配”
        AABB searchBox = viewer.getBoundingBox()
                               .expandTowards(look.scale(range))
                               .inflate(3.0D, 3.0D, 3.0D);

        Entity best = null;
        double bestDistanceSq = maxDistanceSq;

        for (Entity candidate : viewer.level().getEntities(viewer, searchBox,
                                                           e -> e.isPickable() && !e.isSpectator())) {

            // 适当扩大碰撞箱，提升“目视选中”容错
            float pickRadius = Math.max(0.3F, candidate.getPickRadius());
            AABB box = candidate.getBoundingBox().inflate(pickRadius);

            EntityHitResult hit = box.clip(eyePos, end).map(pos -> new EntityHitResult(candidate, pos)).orElse(null);

            // 眼睛在碰撞箱内部时，优先认为命中
            if (box.contains(eyePos)){
                if (0.0D <= bestDistanceSq){
                    best = candidate;
                    bestDistanceSq = 0.0D;
                }
                continue;
            }

            if (hit != null){
                double distSq = eyePos.distanceToSqr(hit.getLocation());
                if (distSq < bestDistanceSq){
                    best = candidate;
                    bestDistanceSq = distSq;
                }
            }
        }

        return best;
    }

    public int getPriority() {
        return 10;
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.PROJECTILE_LAUNCH);
        super.registerHooks(hookBuilder);
    }

    @Override
    public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
        if (projectile instanceof TargetTracker mode){
            mode.dreamtinker$setMode(lookedEntityPredicate(shooter, 16.0D));
        }
    }
}
