package org.brahypno.dreamtinker.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import org.brahypno.dreamtinker.library.client.Overlay.ClientMask;
import org.brahypno.dreamtinker.library.client.Overlay.ColorMaskMode;

import java.util.function.Supplier;

public record S2CColorMaskToggle(boolean enable, ColorMaskMode mode, int argb, int range, float grayStrength, float vividStrength, int fadeIn, int fadeOut) {
    public static S2CColorMaskToggle decode(FriendlyByteBuf buf) {
        return new S2CColorMaskToggle(buf.readBoolean(), ColorMaskMode.byId(buf.readVarInt()), buf.readInt(), buf.readVarInt(), buf.readFloat(),
                                      buf.readFloat(), buf.readVarInt(), buf.readVarInt());
    }

    public static S2CColorMaskToggle overlay(int argb, int fadeIn) {
        return new S2CColorMaskToggle(true, ColorMaskMode.OVERLAY, argb, 0, 0.36F, 1.0F, fadeIn, 0);
    }

    public static S2CColorMaskToggle colorIsolation(int rgb, int range, float grayStrength, float vividStrength, int fadeIn) {
        return new S2CColorMaskToggle(true, ColorMaskMode.COLOR_ISOLATION, rgb, range, grayStrength, vividStrength, fadeIn, 0);
    }

    public static S2CColorMaskToggle off(int fadeOut) {
        return new S2CColorMaskToggle(false, ColorMaskMode.NONE, 0, 0, 0.36F, 1.0F, 0, fadeOut);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(enable);
        buf.writeVarInt(mode.ordinal());
        buf.writeInt(argb);
        buf.writeVarInt(range);
        buf.writeFloat(grayStrength);
        buf.writeFloat(vividStrength);
        buf.writeVarInt(fadeIn);
        buf.writeVarInt(fadeOut);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        var c = ctx.get();
        if (c.getDirection() != NetworkDirection.PLAY_TO_CLIENT){
            c.setPacketHandled(true);
            return;
        }
        c.enqueueWork(() -> {
            if (enable)
                ClientMask.enable(mode, argb, range, grayStrength, vividStrength, fadeIn);
            else
                ClientMask.disable(fadeOut);
        });
        c.setPacketHandled(true);
    }
}
