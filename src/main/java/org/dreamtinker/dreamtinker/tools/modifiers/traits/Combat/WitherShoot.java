package org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseinterface.ArrowInterface;
import org.dreamtinker.dreamtinker.utils.DTHelper;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.shared.TinkerEffects;

import javax.annotation.Nullable;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.WitherShootDangerPercentage;

public class WitherShoot extends NoLevelsModifier implements ArrowInterface {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        this.ArrowInterfaceInit(hookBuilder);
        super.registerHooks(hookBuilder);
    }

    public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
        if (shooter.level().isClientSide)
            return;
        ServerLevel world = (ServerLevel) shooter.level();

        double px = shooter.getX(), pz = shooter.getZ();
        DTHelper.clearProjectile(world, px, pz);

        Vec3 motion = projectile.getDeltaMovement();
        WitherSkull newProj = EntityType.WITHER_SKULL.create(shooter.level());
        if (newProj != null){
            if (shooter.getHealth() <= shooter.getMaxHealth() * WitherShootDangerPercentage.get())
                newProj.setDangerous(true);
            newProj.setOwner(shooter);
            newProj.setPos(projectile.getX(), projectile.getY(), projectile.getZ());
            newProj.setNoGravity(false);
            float speed = (float) Math.sqrt(motion.x * motion.x + motion.y * motion.y + motion.z * motion.z);
            float inaccuracy = shooter.level().random.nextFloat();
            newProj.shoot(motion.x, motion.y, motion.z, speed, inaccuracy);
            world.addFreshEntity(newProj);
        }
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target, boolean notBlocked) {
        if (null != attacker)
            generateCloud(hit.getEntity().position(), attacker);
        return false;
    }

    @Override
    public boolean onProjectileHitsBlock(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, BlockHitResult hit, @Nullable LivingEntity owner) {
        if (null != owner)
            generateCloud(hit.getBlockPos().getCenter(), owner);
        return false;
    }

    private void generateCloud(Vec3 pos, LivingEntity owner) {
        if (!(owner.level() instanceof ServerLevel level))
            return;
        AreaEffectCloud cloud = new AreaEffectCloud(level, pos.x, pos.y, pos.z);
        cloud.setOwner(owner);                        // 归属（用于伤害归因/统计）
        cloud.setParticle(ParticleTypes.DRAGON_BREATH); // 粒子外观（像龙息）
        cloud.setFixedColor(0x5C7A54);                // 可选：自定义颜色（十六进制 RGB）

        // 形态参数（可按需调节）
        cloud.setRadius(3.0F);          // 初始半径（方块）
        cloud.setDuration(200);         // 存在 200 tick = 10s
        cloud.setWaitTime(0);           // 立刻生效（不等待）
        cloud.setRadiusPerTick(-0.005F);// 每 tick 缩小半径（淡出）
        cloud.setRadiusOnUse(-0.5F);    // 命中实体时额外缩小

        // 携带的效果（两种写法选一种或同时用）
        cloud.setPotion(Potions.EMPTY); // 先清空
        // A) 持续效果：例如凋零 3 秒（会按 reapplicationDelay 反复刷新/叠层规则）
        cloud.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 0));
        cloud.addEffect(new MobEffectInstance(TinkerEffects.bleeding.get(), 60, 0));
        // B) 即时伤害：像“龙息很疼”的感觉（每次命中触发一次）
        // cloud.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 0));

        level.addFreshEntity(cloud);    // 只在服务端调用

    }
}
