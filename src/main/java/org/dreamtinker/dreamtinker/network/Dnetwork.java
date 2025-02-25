package org.dreamtinker.dreamtinker.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.dreamtinker.dreamtinker.Dreamtinker;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class Dnetwork {
    // Unique channel name (use your mod id)
    public static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Dreamtinker.MODID, "msg"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    // Packet id counter
    private static int packetId = 0;

    public static void registerPackets() {
        CHANNEL.registerMessage(
                packetId++,
                LeftClickEmptyPacket.class,
                LeftClickEmptyPacket::toBytes,
                LeftClickEmptyPacket::new,
                LeftClickEmptyPacket::handle
        );
    }
}
