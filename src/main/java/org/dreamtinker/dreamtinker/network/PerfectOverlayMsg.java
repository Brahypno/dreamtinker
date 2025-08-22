package org.dreamtinker.dreamtinker.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.network.NetworkEvent;
import org.dreamtinker.dreamtinker.Overlay.PerfectOverlay;

import java.util.function.Supplier;

public record PerfectOverlayMsg(ResourceLocation icon, int durationTicks) {
    public static void encode(PerfectOverlayMsg m, FriendlyByteBuf buf) {
        buf.writeResourceLocation(m.icon); buf.writeVarInt(m.durationTicks);
    }
    public static PerfectOverlayMsg decode(FriendlyByteBuf buf) {
        return new PerfectOverlayMsg(buf.readResourceLocation(), buf.readVarInt());
    }
    public static void handle(PerfectOverlayMsg m, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // 触发屏幕中心淡入淡出的自定义图标
            PerfectOverlay.INSTANCE.trigger(m.icon(), m.durationTicks());
            // 可选：提示音
            Minecraft mc = Minecraft.getInstance();
            mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.15f));
        });
        ctx.get().setPacketHandled(true);
    }
}
