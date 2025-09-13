package org.dreamtinker.dreamtinker.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import org.dreamtinker.dreamtinker.client.Overlay.PerfectOverlay;

import java.util.function.Supplier;

public record PerfectOverlayMsg(ResourceLocation icon, int durationTicks) {
    public static void encode(PerfectOverlayMsg m, FriendlyByteBuf buf) {
        buf.writeResourceLocation(m.icon);
        buf.writeVarInt(m.durationTicks);
    }

    public static PerfectOverlayMsg decode(FriendlyByteBuf buf) {
        return new PerfectOverlayMsg(buf.readResourceLocation(), buf.readVarInt());
    }

    public static void handle(PerfectOverlayMsg m, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> net.minecraftforge.fml.DistExecutor.safeRunWhenOn(
                net.minecraftforge.api.distmarker.Dist.CLIENT,
                () -> new net.minecraftforge.fml.DistExecutor.SafeRunnable() {//cannot use lambda,why?在部分 Forge 版本里有类型推断/擦除上的坑
                    @Override
                    public void run() {
                        ClientOnly.apply(m);
                    }
                }
        ));
        ctx.get().setPacketHandled(true);
    }

    @net.minecraftforge.api.distmarker.OnlyIn(net.minecraftforge.api.distmarker.Dist.CLIENT)
    private static final class ClientOnly {
        static void apply(PerfectOverlayMsg m) {
            PerfectOverlay.INSTANCE.trigger(m.icon(), m.durationTicks());
        }
    }
}
