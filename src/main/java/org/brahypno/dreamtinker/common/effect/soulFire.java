package org.brahypno.dreamtinker.common.effect;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.brahypno.dreamtinker.common.DreamtinkerDamageTypes;
import org.brahypno.esotericismtinker.utils.damage.DamageProbe;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class soulFire extends MobEffect {

    public soulFire() {
        super(MobEffectCategory.HARMFUL, 0x222222);
    }

    private static void spawnSoulBurning(LivingEntity entity, int amplifier) {
        if (!(entity.level() instanceof ServerLevel sl))
            return;

        double w = entity.getBbWidth();
        double h = entity.getBbHeight();

        int baseCount = switch (amplifier) {
            case 0 -> 6;
            case 1 -> 8;
            default -> 10;
        };

        /*
         * sendParticles already lets the client randomize `count` particles from one packet.
         * The former loops emitted 11-17 packets every two ticks per burning entity. These three
         * batched calls retain the lower flame, upper flame and soul-mist layers while reducing that
         * traffic to exactly three particle packets per emission.
         */
        sl.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                         entity.getX(), entity.getY() + h * 0.18D, entity.getZ(),
                         baseCount, w * 0.45D, h * 0.18D, w * 0.45D, 0.035D);
        sl.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                         entity.getX(), entity.getY() + h * 0.58D, entity.getZ(),
                         Math.max(2, baseCount / 2), w * 0.30D, h * 0.23D, w * 0.30D, 0.025D);
        sl.sendParticles(ParticleTypes.SOUL,
                         entity.getX(), entity.getY() + h * 0.50D, entity.getZ(),
                         2, w * 0.40D, h * 0.50D, w * 0.40D, 0.02D);
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
            LivingEntity attacker = entity.getLastHurtByMob();
            AttributeInstance attack = null != attacker ? attacker.getAttribute(Attributes.ATTACK_DAMAGE) : null;
            double damage = null != attack ? attack.getValue() : 2;
            DamageProbe.damageHandler(entity, DreamtinkerDamageTypes.source(world.registryAccess(), DreamtinkerDamageTypes.arcane_damage, null,
                                                                            entity.getLastAttacker()),
                                      (float) damage * (amplifier + 1));
            entity.setLastHurtByMob(attacker);
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
