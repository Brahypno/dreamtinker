package org.dreamtinker.dreamtinker.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.dreamtinker.dreamtinker.Dreamtinker;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class DNetwork {
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
                                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(packetId++, S2CColorMaskToggle.class, S2CColorMaskToggle::encode, S2CColorMaskToggle::decode, S2CColorMaskToggle::handle,
                                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(packetId++, KeyStateMsg.class, KeyStateMsg::encode, KeyStateMsg::decode, KeyStateMsg::handle,
                                Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(packetId++, RightClickEmptyPacket.class, RightClickEmptyPacket::toBytes, RightClickEmptyPacket::new,
                                RightClickEmptyPacket::handle);
        CHANNEL.registerMessage(packetId++, S2CUseRemainPacket.class, S2CUseRemainPacket::encode, S2CUseRemainPacket::decode, S2CUseRemainPacket::handle,
                                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(packetId++, WallVisionSyncPacket.class, WallVisionSyncPacket::toBytes, WallVisionSyncPacket::new, WallVisionSyncPacket::handle,
                                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(packetId++, S2CVibeBarFx.class, S2CVibeBarFx::encode, S2CVibeBarFx::decode, S2CVibeBarFx::handle,
                                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }
}
