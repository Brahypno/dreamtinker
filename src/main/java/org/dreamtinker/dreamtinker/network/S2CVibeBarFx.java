package org.dreamtinker.dreamtinker.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;
import org.dreamtinker.dreamtinker.library.client.particle.VibeBarParticleOptions;

import java.util.function.Supplier;

public record S2CVibeBarFx(
        int targetId,
        float barDirX, float barDirZ,
        int argb,
        int lifetimeTicks,
        float amplitude,
        float frequencyHz,
        float yFrac
) {
    public static void encode(S2CVibeBarFx m, FriendlyByteBuf buf) {
        buf.writeVarInt(m.targetId);
        buf.writeFloat(m.barDirX);
        buf.writeFloat(m.barDirZ);
        buf.writeInt(m.argb);
        buf.writeVarInt(m.lifetimeTicks);
        buf.writeFloat(m.amplitude);
        buf.writeFloat(m.frequencyHz);
        buf.writeFloat(m.yFrac);
    }

    public static S2CVibeBarFx decode(FriendlyByteBuf buf) {
        return new S2CVibeBarFx(
                buf.readVarInt(),
                buf.readFloat(), buf.readFloat(),
                buf.readInt(),
                buf.readVarInt(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat()
        );
    }

    public static void handle(S2CVibeBarFx m, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null)
                return;

            Entity e = mc.level.getEntity(m.targetId);
            if (!(e instanceof LivingEntity target))
                return;

            // 横条长度 = 碰撞箱宽度
            float len = Math.max(0.2f, target.getBbWidth());
            float half = len * 0.5f;

            // 点数：20 左右足够（<1s 性能很稳）
            int points = 20;

            // 在条上均匀布点：每个点一个粒子，自带 tick 跟随与抖动
            for (int i = 0; i < points; i++) {
                float t = (points == 1) ? 0.5f : (i / (float) (points - 1));
                float along = Mth.lerp(t, -half, half);
                float phase = (float) (Math.random() * Math.PI * 2.0);

                VibeBarParticleOptions opt = new VibeBarParticleOptions(
                        m.targetId,
                        m.barDirX, m.barDirZ,
                        along,
                        m.argb,
                        m.lifetimeTicks,
                        m.amplitude,
                        m.frequencyHz,
                        m.yFrac,
                        phase
                );

                // x/y/z 在粒子 tick 中会被立即重设，这里给 0/目标位置都行
                mc.level.addParticle(opt, target.getX(), target.getY(), target.getZ(), 0, 0, 0);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
