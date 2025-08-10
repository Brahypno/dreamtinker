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

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.EchoDefenceRange;
import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.EchoDefenceSpeed;

public class echoed_defence extends ArmorModifier {
    public static final String BOUNCE_TAG = "redirect_bounce_count";

    @Override
    public boolean isDamageBlocked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount) {

        // 检查是否是弹射物
        if (!(source.getDirectEntity() instanceof Projectile projectile))
            return false;
        CompoundTag tag = projectile.getPersistentData();
        int currentBounce = tag.getInt(BOUNCE_TAG);
        int maxBounce = modifier.getLevel();

        if (currentBounce >= maxBounce)
            return false;

        // 增加反弹次数
        tag.putInt(BOUNCE_TAG, currentBounce + 1);

        List<LivingEntity> nearby = context.getEntity().level().getEntitiesOfClass(LivingEntity.class, projectile.getBoundingBox().inflate(EchoDefenceRange.get()), LivingEntity::isAlive);

        if (!nearby.isEmpty()){
            LivingEntity newTarget = nearby.get(context.getEntity().level().getRandom().nextInt(nearby.size()));

            final double ARROW_SPEED = EchoDefenceSpeed.get();
            Vec3 dest;
            if (newTarget == context.getEntity()){

                double maxOffset = Math.min(currentBounce * 0.5, 3.0);
                double xOffset = (projectile.level().random.nextDouble() - 0.5) * 4 * maxOffset;
                double zOffset = (projectile.level().random.nextDouble() - 0.5) * 4 * maxOffset;

                dest = new Vec3(newTarget.getEyePosition().x + xOffset, newTarget.getEyePosition().y + 10, newTarget.getEyePosition().z + zOffset);
                System.out.println("target: " + dest);
                projectile.setNoGravity(false);

            }else {
                dest = newTarget.getEyePosition();
                projectile.setNoGravity(true);
            }
            Vec3 newMotion = projectile.position().subtract(dest).normalize().scale(ARROW_SPEED);
            projectile.setDeltaMovement(newMotion);

            projectile.lookAt(EntityAnchorArgument.Anchor.EYES, newTarget.getEyePosition());
        }

        context.getEntity().level().playSound(null, context.getEntity().blockPosition(), SoundEvents.TRIDENT_RETURN, SoundSource.PLAYERS, 1f, 1f);

        return true;
    }

}
