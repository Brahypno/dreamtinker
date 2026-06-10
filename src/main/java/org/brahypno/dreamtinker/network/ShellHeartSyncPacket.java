package org.brahypno.dreamtinker.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import org.brahypno.dreamtinker.Entity.capabilities.ShellHeartProvider;

import java.util.function.Supplier;

public record ShellHeartSyncPacket(float value, int colour) {

    public static void encode(ShellHeartSyncPacket msg, FriendlyByteBuf buf) {
        buf.writeFloat(msg.value);
        buf.writeInt(msg.colour);
    }

    public static ShellHeartSyncPacket decode(FriendlyByteBuf buf) {
        return new ShellHeartSyncPacket(
                buf.readFloat(),
                buf.readInt()
        );
    }

    public static void handle(ShellHeartSyncPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();

        ctx.enqueueWork(() -> DistExecutor.safeRunWhenOn(
                Dist.CLIENT,
                () -> new DistExecutor.SafeRunnable() {
                    @Override
                    public void run() {
                        ClientHandler.handle(msg);
                    }
                }
        ));

        ctx.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static final class ClientHandler {
        private static void handle(ShellHeartSyncPacket msg) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player == null){
                return;
            }

            ShellHeartProvider.getShellHeart(mc.player).ifPresent(shellHeart -> {
                shellHeart.set(msg.value());
                shellHeart.setHeartColour(msg.colour());
            });
        }
    }
}
