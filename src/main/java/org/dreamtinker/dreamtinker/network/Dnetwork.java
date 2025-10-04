package org.dreamtinker.dreamtinker.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.dreamtinker.dreamtinker.Dreamtinker;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class Dnetwork {
    // Unique channel name (use your mod id)
    public static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL =
            NetworkRegistry.newSimpleChannel(new ResourceLocation(Dreamtinker.MODID, "msg"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals,
                                             PROTOCOL_VERSION::equals);

    // Packet id counter
    private static int packetId = 0;

    public static void registerPackets() {
        CHANNEL.registerMessage(packetId++, LeftClickEmptyPacket.class, LeftClickEmptyPacket::toBytes, LeftClickEmptyPacket::new, LeftClickEmptyPacket::handle);
        CHANNEL.registerMessage(packetId++, PerfectOverlayMsg.class, PerfectOverlayMsg::encode, PerfectOverlayMsg::decode, PerfectOverlayMsg::handle,
                                // 仅客户端处理
                                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(packetId++, S2CColorMaskToggle.class, S2CColorMaskToggle::encode, S2CColorMaskToggle::decode, S2CColorMaskToggle::handle,
                                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(packetId++, KeyStateMsg.class, KeyStateMsg::encode, KeyStateMsg::decode, KeyStateMsg::handle,
                                Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }
}
