package org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.dreamtinker.dreamtinker.library.modifiers.DreamtinkerHook;
import org.dreamtinker.dreamtinker.library.modifiers.hook.LeftClickHook;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;

public class DeflectIncomingProjectiles extends Modifier implements LeftClickHook {
    private static final double BASE_DEFLECT_SCAN_RANGE = 5.0D;
    private static final double DEFLECT_SCAN_RANGE_PER_LEVEL = 1.5D;
    private static final double BASE_DEFLECT_MISS_DISTANCE = 1.25D;
    private static final double DEFLECT_MISS_DISTANCE_PER_LEVEL = 0.5D;
    private static final double MIN_PROJECTILE_SPEED_SQR = 1.0E-6D;

    private static double getDeflectScanRange(int modifierLevel) {
        return BASE_DEFLECT_SCAN_RANGE + DEFLECT_SCAN_RANGE_PER_LEVEL * (modifierLevel - 1);
    }

    private static double getDeflectMissDistance(Player player, int modifierLevel) {
        return Math.max(
                BASE_DEFLECT_MISS_DISTANCE,
                player.getBbWidth() * 0.5D + 0.75D + DEFLECT_MISS_DISTANCE_PER_LEVEL * (modifierLevel - 1)
        );
    }

    /**
     * 判断弹射物是否正在朝玩家飞来。
     * <p>
     * 不是简单判断距离，而是判断：
     * 1. 弹射物速度方向是否指向玩家；
     * 2. 它当前轨迹是否大致会经过玩家附近。
     */
    private static boolean isFlyingTowardPlayer(Projectile projectile, Player player, int modifierLevel) {
        Vec3 velocity = projectile.getDeltaMovement();

        if (velocity.lengthSqr() <= MIN_PROJECTILE_SPEED_SQR){
            return false;
        }

        Vec3 projectilePos = projectile.position();
        Vec3 playerCenter = player.getBoundingBox().getCenter();
        Vec3 toPlayer = playerCenter.subtract(projectilePos);

        if (toPlayer.lengthSqr() <= 1.0E-6D){
            return true;
        }

        Vec3 dir = velocity.normalize();
        double forward = toPlayer.dot(dir);

        if (forward <= 0.0D){
            return false;
        }

        double missDistanceSqr = toPlayer.lengthSqr() - forward * forward;
        double allowedMissDistance = getDeflectMissDistance(player, modifierLevel);

        return missDistanceSqr <= allowedMissDistance * allowedMissDistance;
    }

    /**
     * 改主人，并沿原路径反方向射回去。
     */
    private static void reflectProjectile(Projectile projectile, Player player) {
        Vec3 oldVelocity = projectile.getDeltaMovement();

        projectile.setOwner(player);
        projectile.setDeltaMovement(oldVelocity.scale(-1.0D));

        Vec3 pushOut = oldVelocity.normalize().scale(-0.35D);
        projectile.setPos(
                projectile.getX() + pushOut.x,
                projectile.getY() + pushOut.y,
                projectile.getZ() + pushOut.z
        );

        if (projectile instanceof AbstractArrow arrow){
            arrow.setNoPhysics(false);
            arrow.pickup = AbstractArrow.Pickup.DISALLOWED;
        }

        projectile.hasImpulse = true;
        projectile.hurtMarked = true;
    }

    @Override
    public void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, DreamtinkerHook.LEFT_CLICK);
        super.registerHooks(hookBuilder);
    }

    @Override
    public void onLeftClickEmpty(IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot) {
        deflectIncomingProjectiles(tool, entry, player, level, null);
    }

    @Override
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event, IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot, BlockState state, BlockPos pos) {
        deflectIncomingProjectiles(tool, entry, player, level, null);
    }

    @Override
    public void onLeftClickEntity(AttackEntityEvent event, IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot, Entity target) {
        deflectIncomingProjectiles(tool, entry, player, level, target);
    }

    /**
     * 左键挥动时反弹附近正在飞向自己的弹射物。
     */
    private void deflectIncomingProjectiles(IToolStackView tool, ModifierEntry entry, Player player, Level level, @Nullable Entity directTarget) {
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel)){
            return;
        }

        int modifierLevel = entry.getLevel();
        double scanRange = getDeflectScanRange(modifierLevel);
        AABB area = player.getBoundingBox().inflate(scanRange);

        List<Projectile> projectiles = serverLevel.getEntitiesOfClass(
                Projectile.class,
                area,
                projectile -> projectile.isAlive()
                              && projectile != directTarget
                              && projectile.getOwner() != player
                              && isFlyingTowardPlayer(projectile, player, modifierLevel)
        );

        for (Projectile projectile : projectiles) {
            reflectProjectile(projectile, player);
        }
    }
}
