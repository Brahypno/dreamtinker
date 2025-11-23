package org.dreamtinker.dreamtinker.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import org.dreamtinker.dreamtinker.common.event.client.WallVisionRenderer;

import java.util.function.Supplier;

public class WallVisionSyncPacket {
    private final boolean enabled;
    private final ResourceLocation tag;
    private final int radius;

    public WallVisionSyncPacket(boolean enabled, ResourceLocation tag, int radius) {
        this.enabled = enabled;
        this.tag = tag;
        this.radius = radius;
    }

    public WallVisionSyncPacket(FriendlyByteBuf buf) {
        this.enabled = buf.readBoolean();
        this.tag = buf.readResourceLocation();
        this.radius = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(enabled);
        buf.writeResourceLocation(tag);
        buf.writeInt(radius);
    }

    public static void handle(WallVisionSyncPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null){
                WallVisionRenderer.ClientWallVisionState.setHighlightTag(msg.tag);
                WallVisionRenderer.ClientWallVisionState.setEnabled(msg.enabled, msg.radius);
            }
        });
        ctx.setPacketHandled(true);
    }
}
