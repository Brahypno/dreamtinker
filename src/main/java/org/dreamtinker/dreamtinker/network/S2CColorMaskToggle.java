package org.dreamtinker.dreamtinker.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import org.dreamtinker.dreamtinker.library.client.ClientMask;

public record S2CColorMaskToggle(boolean enable, int argb, int fadeIn, int fadeOut) {
    public static S2CColorMaskToggle decode(FriendlyByteBuf buf) {
        return new S2CColorMaskToggle(buf.readBoolean(), buf.readInt(), buf.readVarInt(), buf.readVarInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(enable);
        buf.writeInt(argb);
        buf.writeVarInt(fadeIn);
        buf.writeVarInt(fadeOut);
    }

    public void handle(java.util.function.Supplier<NetworkEvent.Context> ctx) {
        var c = ctx.get();
        if (c.getDirection() != NetworkDirection.PLAY_TO_CLIENT)
            return;
        c.enqueueWork(() -> {
            if (enable)
                ClientMask.enable(argb, fadeIn);
            else
                ClientMask.disable(fadeOut);
        });
        c.setPacketHandled(true);
    }
}
