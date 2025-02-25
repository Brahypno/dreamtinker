package org.dreamtinker.dreamtinker.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import org.dreamtinker.dreamtinker.hook.LeftClickHook;

import java.util.function.Supplier;

public class LeftClickEmptyPacket {
    // If no additional data is needed, the class can be empty.

    // Constructor for sending
    public LeftClickEmptyPacket() {}

    // Constructor for receiving from the buffer (if needed)
    public LeftClickEmptyPacket(FriendlyByteBuf buf) {
        // Read data from the buffer here, if needed
    }

    // Write data into the buffer
    public void toBytes(FriendlyByteBuf buf) {
        // Write any data into the buffer here, if needed
    }

    // Packet handler method
    public static void handle(LeftClickEmptyPacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context context = ctxSupplier.get();
        ServerPlayer serverPlayer = context.getSender();
        if (serverPlayer!=null) {
            ItemStack stack =serverPlayer.getItemInHand(serverPlayer.getUsedItemHand());
            EquipmentSlot slot = stack.getEquipmentSlot();
            context.enqueueWork(() -> {
                LeftClickHook.handleLeftClick(stack,serverPlayer,slot);
            });
        }
        context.setPacketHandled(true);
    }
}

