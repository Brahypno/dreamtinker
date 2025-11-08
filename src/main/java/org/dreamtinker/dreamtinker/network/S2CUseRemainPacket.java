package org.dreamtinker.dreamtinker.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.dreamtinker.dreamtinker.library.client.ClientApplyUseRemaining;

import java.util.function.Supplier;

public record S2CUseRemainPacket(int entityId, int hand, int remaining, boolean active) {
    public static void encode(S2CUseRemainPacket p, FriendlyByteBuf b) {
        b.writeVarInt(p.entityId);
        b.writeVarInt(p.hand);
        b.writeVarInt(p.remaining);
        b.writeBoolean(p.active);
    }

    public static S2CUseRemainPacket decode(FriendlyByteBuf b) {
        return new S2CUseRemainPacket(b.readVarInt(), b.readVarInt(), b.readVarInt(), b.readBoolean());
    }

    public static void handle(S2CUseRemainPacket p, Supplier<NetworkEvent.Context> ctxSup) {
        NetworkEvent.Context ctx = ctxSup.get();
        ctx.enqueueWork(() -> ClientApplyUseRemaining.apply(p.entityId(), p.hand(), p.remaining(), p.active()));
        ctx.setPacketHandled(true);
    }
}

