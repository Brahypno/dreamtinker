package org.dreamtinker.dreamtinker.common.effect;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.dreamtinker.dreamtinker.common.DreamtinkerDamageTypes;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class soulFire extends MobEffect {

    public soulFire() {
        super(MobEffectCategory.HARMFUL, 0x222222);
    }

    private static void spawnSoulBurning(LivingEntity entity, int amplifier) {
        if (!(entity.level() instanceof ServerLevel sl))
            return;

        RandomSource r = entity.getRandom();
        double w = entity.getBbWidth();
        double h = entity.getBbHeight();

        int baseCount = switch (amplifier) {
            case 0 -> 6;
            case 1 -> 8;
            default -> 10;
        };

        // 底部魂火
        for (int i = 0; i < baseCount; i++) {
            double angle = r.nextDouble() * Math.PI * 2.0;
            double radius = w * (0.25 + r.nextDouble() * 0.20);

            double x = entity.getX() + Math.cos(angle) * radius;
            double y = entity.getY() + r.nextDouble() * (h * 0.35);
            double z = entity.getZ() + Math.sin(angle) * radius;

            double vx = (r.nextDouble() - 0.5) * 0.02;
            double vy = 0.02 + r.nextDouble() * 0.04;
            double vz = (r.nextDouble() - 0.5) * 0.02;

            sl.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 1, vx, vy, vz, 0.0);
        }

        // 中段魂火
        for (int i = 0; i < Math.max(2, baseCount / 2); i++) {
            double angle = r.nextDouble() * Math.PI * 2.0;
            double radius = w * (0.15 + r.nextDouble() * 0.15);

            double x = entity.getX() + Math.cos(angle) * radius;
            double y = entity.getY() + h * (0.35 + r.nextDouble() * 0.45);
            double z = entity.getZ() + Math.sin(angle) * radius;

            double vx = (r.nextDouble() - 0.5) * 0.015;
            double vy = 0.015 + r.nextDouble() * 0.03;
            double vz = (r.nextDouble() - 0.5) * 0.015;

            sl.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 1, vx, vy, vz, 0.0);
        }

        // 魂雾
        for (int i = 0; i < 2; i++) {
            double x = entity.getX() + (r.nextDouble() - 0.5) * w * 0.8;
            double y = entity.getY() + r.nextDouble() * h;
            double z = entity.getZ() + (r.nextDouble() - 0.5) * w * 0.8;

            sl.sendParticles(ParticleTypes.SOUL, x, y, z, 1, 0.0, 0.02, 0.0, 0.0);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true; // 每 tick 调用 applyEffectTick
    }

    @Override
    public List<ItemStack> getCurativeItems() {return List.of();}

    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        Level world = entity.level();
        if (!(world instanceof ServerLevel sl))
            return;

        if (entity.tickCount % 2 == 0){
            spawnSoulBurning(entity, amplifier);
        }
        if (entity.tickCount % 20 == 0){
            LivingEntity attacker = entity.getLastAttacker();
            double damage = null != attacker ? attacker.getAttributeValue(Attributes.ATTACK_DAMAGE) : 2;
            entity.hurt(DreamtinkerDamageTypes.source(world.registryAccess(), DreamtinkerDamageTypes.arcane_damage, null, entity.getLastAttacker()),
                        (float) damage * (amplifier + 1));
        }

        MobEffectInstance ent = entity.getEffect(this);
        if (null != ent && entity.tickCount % 10 == 0)
            for (LivingEntity aoeTarget : sl.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(0.4, 0.25D, 0.4))) {
                if (aoeTarget != entity){
                    MobEffectInstance ins = aoeTarget.getEffect(this);
                    if (null == ins || ins.getAmplifier() < amplifier || ins.getAmplifier() == amplifier && ins.getDuration() < ent.getDuration()){
                        aoeTarget.addEffect(
                                new MobEffectInstance(this, Math.max(ent.getDuration(), null != ins ? ins.getDuration() : 1),
                                                      Math.max(null != ins ? ins.getAmplifier() : 0, amplifier)));
                    }
                }
            }
    }

}

