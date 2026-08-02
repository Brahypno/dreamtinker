package org.brahypno.dreamtinker.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Sends one compact shockwave description and lets each client build the particle rings locally.
 */
public record S2CDinosaurShockwavePacket(
        double x, double y, double z, BlockState state, int rings, float radius) {
    private static final int POINTS_PER_RING = 24;
    private static final int PARTICLES_PER_POINT = 2;

    public static void encode(S2CDinosaurShockwavePacket message, FriendlyByteBuf buffer) {
        buffer.writeDouble(message.x);
        buffer.writeDouble(message.y);
        buffer.writeDouble(message.z);
        buffer.writeVarInt(Block.getId(message.state));
        buffer.writeVarInt(message.rings);
        buffer.writeFloat(message.radius);
    }

    public static S2CDinosaurShockwavePacket decode(FriendlyByteBuf buffer) {
        return new S2CDinosaurShockwavePacket(
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readDouble(),
                Block.stateById(buffer.readVarInt()),
                buffer.readVarInt(),
                buffer.readFloat());
    }

    public static void handle(S2CDinosaurShockwavePacket message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.level == null){
                return;
            }

            int ringCount = Mth.clamp(message.rings, 1, 8);
            double maximumRadius = Mth.clamp(message.radius, 0.5F, 16.0F);
            RandomSource random = minecraft.level.random;
            BlockParticleOption particle = new BlockParticleOption(ParticleTypes.BLOCK, message.state);

            for (int ring = 1; ring <= ringCount; ring++) {
                double ringRadius = maximumRadius * ring / ringCount;
                for (int step = 0; step < POINTS_PER_RING; step++) {
                    double angle = Math.PI * 2.0D * step / POINTS_PER_RING;
                    double centerX = message.x + Math.cos(angle) * ringRadius;
                    double centerZ = message.z + Math.sin(angle) * ringRadius;
                    for (int particleIndex = 0; particleIndex < PARTICLES_PER_POINT; particleIndex++) {
                        minecraft.level.addParticle(
                                particle,
                                centerX + (random.nextDouble() - 0.5D) * 0.16D,
                                message.y + (random.nextDouble() - 0.5D) * 0.30D,
                                centerZ + (random.nextDouble() - 0.5D) * 0.16D,
                                (random.nextDouble() - 0.5D) * 0.06D,
                                random.nextDouble() * 0.03D,
                                (random.nextDouble() - 0.5D) * 0.06D);
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }
}
