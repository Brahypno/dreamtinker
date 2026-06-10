package org.brahypno.dreamtinker.library.client.utils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.network.DNetwork;
import org.brahypno.dreamtinker.network.WallVisionSyncPacket;

public class BlockViewerService {
    private static final java.util.Map<java.util.UUID, State> STATES = new java.util.HashMap<>();

    public static void ensureOn(ServerPlayer sp, ResourceLocation location, int radius) {
        long tick = now(sp);
        State state = STATES.get(sp.getUUID());
        if (state != null && state.enabled && location.equals(state.location) && state.radius == radius)
            return;
        sendBlockView(sp, location, radius);
        STATES.put(sp.getUUID(), new State(true, location, radius, tick));
    }

    private static long now(ServerPlayer sp) {return sp.serverLevel().getGameTime();}

    public static void ensureOff(ServerPlayer sp) {
        State state = STATES.get(sp.getUUID());
        if (state == null || !state.enabled)
            return;
        sendBlockViewOff(sp);
        STATES.put(sp.getUUID(), new State(false, null, 0, now(sp)));
    }

    public static void ensureOnThrottled(ServerPlayer sp, ResourceLocation location, int radius, int throttleTicks) {
        long tick = now(sp);
        State state = STATES.get(sp.getUUID());
        if (state != null && state.enabled && location.equals(state.location) && state.radius == radius){
            if (tick - state.lastSentTick < throttleTicks)
                return;
        }
        sendBlockView(sp, location, radius);
        STATES.put(sp.getUUID(), new State(true, location, radius, tick));
    }

    private static final class State {
        boolean enabled;
        ResourceLocation location;
        int radius;
        long lastSentTick;

        State(boolean enabled, ResourceLocation location, int radius, long lastSentTick) {
            this.enabled = enabled;
            this.location = location;
            this.radius = radius;
            this.lastSentTick = lastSentTick;
        }
    }

    public static void clear(ServerPlayer sp) {STATES.remove(sp.getUUID());}

    public static void clearAll() {STATES.clear();}

    public static void sendBlockView(ServerPlayer sp, ResourceLocation location, int radius) {
        DNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp), new WallVisionSyncPacket(true, location, radius));
    }

    public static void sendBlockViewOff(ServerPlayer sp) {
        DNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp), new WallVisionSyncPacket(false, Dreamtinker.getLocation("off"), 0));
    }
}
