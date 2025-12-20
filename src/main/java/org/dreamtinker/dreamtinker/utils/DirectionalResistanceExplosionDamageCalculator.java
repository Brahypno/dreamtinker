package org.dreamtinker.dreamtinker.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class DirectionalResistanceExplosionDamageCalculator extends ExplosionDamageCalculator {
    private final Vec3 origin;
    private final Vec3 axisN;
    private final double cosHalf;
    private final float insideMul;   // < 1：圆锥内更易炸
    private final float outsideMul;  // > 1：圆锥外更难炸

    /**
     * @param origin       爆心
     * @param axis         方向向量（只决定方向，建议用命中瞬间速度/缓存速度）
     * @param halfAngleDeg 圆锥半角（建议 25~50）
     * @param insideMul    圆锥内抗性倍率（0.55~0.85 比较稳）
     * @param outsideMul   圆锥外抗性倍率（1.5~4.0 比较稳；太大容易“几乎不炸”）
     */
    public DirectionalResistanceExplosionDamageCalculator(
            Vec3 origin, Vec3 axis,
            float halfAngleDeg,
            float insideMul, float outsideMul) {
        this.origin = origin;

        Vec3 a = axis;
        if (a.lengthSqr() < 1.0e-8)
            a = new Vec3(0, 1, 0);
        this.axisN = a.normalize();

        float clamped = Mth.clamp(halfAngleDeg, 0.1f, 179.9f);
        this.cosHalf = Math.cos(Math.toRadians(clamped));

        this.insideMul = insideMul;
        this.outsideMul = outsideMul;
    }

    // 0..1：越接近 1 越“在轴上”，越接近 0 越“在圆锥外”
    private float coneWeight(Vec3 point) {
        Vec3 d = point.subtract(origin);
        if (d.lengthSqr() < 1.0e-10)
            return 1.0f;

        double dot = d.normalize().dot(axisN); // [-1,1]
        // 把 [cosHalf, 1] 映射到 [0,1]；小于 cosHalf 视作 0
        double t = (dot - cosHalf) / (1.0 - cosHalf);
        t = Mth.clamp(t, 0.0, 1.0);

        // smoothstep：边缘更平滑，避免硬切
        return (float) (t * t * (3.0 - 2.0 * t));
    }

    @Override
    public Optional<Float> getBlockExplosionResistance(
            Explosion explosion, BlockGetter level, BlockPos pos,
            BlockState state, FluidState fluid) {
        Optional<Float> baseOpt = super.getBlockExplosionResistance(explosion, level, pos, state, fluid);
        if (baseOpt.isEmpty())
            return baseOpt;

        float base = baseOpt.get();
        Vec3 p = Vec3.atCenterOf(pos);

        float w = coneWeight(p); // 0..1
        // w=1 -> insideMul；w=0 -> outsideMul
        float mul = Mth.lerp(w, outsideMul, insideMul);

        // 钳制避免极端：防止出现“完全不炸”或“无脑打穿”
        mul = Mth.clamp(mul, 0.25f, 8.0f);

        return Optional.of(base * mul);
    }
}

