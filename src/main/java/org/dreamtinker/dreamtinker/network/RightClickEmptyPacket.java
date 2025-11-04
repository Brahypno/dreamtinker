package org.dreamtinker.dreamtinker.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import org.dreamtinker.dreamtinker.library.modifiers.hook.RightClickHook;

import java.util.function.Supplier;

public class RightClickEmptyPacket {
    // If no additional data is needed, the class can be empty.

    // Constructor for sending
    public RightClickEmptyPacket() {}

    // Constructor for receiving from the buffer (if needed)
    public RightClickEmptyPacket(FriendlyByteBuf buf) {
        // Read data from the buffer here, if needed
    }

    // Write data into the buffer
    public void toBytes(FriendlyByteBuf buf) {
        // Write any data into the buffer here, if needed
    }

    // Packet handler method
    public static void handle(RightClickEmptyPacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context context = ctxSupplier.get();
        ServerPlayer serverPlayer = context.getSender();
        if (serverPlayer != null){
            ItemStack stack = serverPlayer.getItemInHand(serverPlayer.getUsedItemHand());
            EquipmentSlot slot = InteractionHand.MAIN_HAND == serverPlayer.getUsedItemHand() ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
            context.enqueueWork(() -> {
                RightClickHook.handleRightClick(stack, serverPlayer, slot);
            });
        }
        context.setPacketHandled(true);
    }
}

