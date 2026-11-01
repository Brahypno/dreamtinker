package org.brahypno.dreamtinker.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent;
import org.brahypno.dreamtinker.library.client.sigil.NarcissusSigilClient;

import java.util.function.Supplier;

/** One successful charged volley, regardless of the number of projectiles. */
public record S2CNarcissusSigilShot(int entityId, InteractionHand hand, float progress, long gameTime, int[] projectiles) {
    public static void encode(S2CNarcissusSigilShot message, FriendlyByteBuf buffer) {
        buffer.writeVarInt(message.entityId);
        buffer.writeEnum(message.hand);
        buffer.writeFloat(message.progress);
        buffer.writeLong(message.gameTime);
        buffer.writeVarIntArray(message.projectiles);
    }

    public static S2CNarcissusSigilShot decode(FriendlyByteBuf buffer) {
        return new S2CNarcissusSigilShot(buffer.readVarInt(), buffer.readEnum(InteractionHand.class), buffer.readFloat(), buffer.readLong(),
                                        buffer.readVarIntArray());
    }

    public static void handle(S2CNarcissusSigilShot message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> NarcissusSigilClient.onShot(message));
        context.setPacketHandled(true);
    }
}
