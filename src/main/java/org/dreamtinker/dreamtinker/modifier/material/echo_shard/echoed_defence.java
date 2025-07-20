package org.dreamtinker.dreamtinker.modifier.material.echo_shard;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import org.dreamtinker.dreamtinker.modifier.base.baseclass.ArmorModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

public class echoed_defence extends ArmorModifier {
    public static final String BOUNCE_TAG = "redirect_bounce_count";

    @Override
    public boolean isDamageBlocked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount) {

        // 检查是否是弹射物
        if (!(source.getDirectEntity() instanceof Projectile projectile)) return false;
        CompoundTag tag = projectile.getPersistentData();
        int currentBounce = tag.getInt(BOUNCE_TAG);
        int maxBounce = modifier.getLevel();

        if (currentBounce >= maxBounce) return false;

        // 增加反弹次数
        tag.putInt(BOUNCE_TAG, currentBounce + 1);

        // 搜索周围 10 格内的其他实体（可能包含原目标）
        List<LivingEntity> nearby = context.getEntity().level.getEntitiesOfClass(
                LivingEntity.class,
                projectile.getBoundingBox().inflate(10), LivingEntity::isAlive
        );

        if (!nearby.isEmpty()) {
            LivingEntity newTarget = nearby.get(context.getEntity().level.getRandom().nextInt(nearby.size()));

            Vec3 newMotion = newTarget.getEyePosition().subtract(projectile.position()).normalize()
                    .scale(projectile.getDeltaMovement().length());

            projectile.setDeltaMovement(newMotion);
            projectile.lookAt(EntityAnchorArgument.Anchor.EYES, newTarget.getEyePosition());
        }
        CompoundTag tag1 = projectile.getPersistentData();
        System.out.println("Projectile Tag After Redirect: " + tag1);

        // 可选：添加音效/粒子
        context.getEntity().level.playSound(null, context.getEntity().blockPosition(), SoundEvents.TRIDENT_RETURN, SoundSource.PLAYERS, 1f, 1f);

        return true;
    }

}
