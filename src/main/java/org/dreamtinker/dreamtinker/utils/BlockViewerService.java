package org.dreamtinker.dreamtinker.utils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import org.dreamtinker.dreamtinker.network.Dnetwork;
import org.dreamtinker.dreamtinker.network.WallVisionSyncPacket;

public class BlockViewerService {
    private static final class State {
        boolean enabled;
        ResourceLocation location;
        int radius;

        State(boolean e, ResourceLocation c, int t) {
            enabled = e;
            location = c;
            radius = t;
        }
    }

    private static final java.util.Map<java.util.UUID, BlockViewerService.State> STATES = new java.util.HashMap<>();

    private static long now(ServerPlayer sp) {return sp.serverLevel().getGameTime();}

    /**
     * 只在“未开”或“颜色改变”时发送；否则不动（去重）
     */
    public static void ensureOn(ServerPlayer sp, ResourceLocation loc, int Redius) {
        var s = STATES.get(sp.getUUID());
        if (s != null && s.enabled && s.location == loc)
            return;   // 已经是同样的遮罩 → 不发
        sendBlockView(sp, loc, Redius);
        STATES.put(sp.getUUID(), new BlockViewerService.State(true, loc, Redius));
    }

    /**
     * 只在“当前是开”的时候发送关闭；否则不动
     */
    public static void ensureOff(ServerPlayer sp) {
        var s = STATES.get(sp.getUUID());
        if (s == null || !s.enabled)
            return;                    // 本来就关着 → 不发
        sendBlockViewOff(sp);
        STATES.put(sp.getUUID(), new BlockViewerService.State(false, null, 0));
    }

    // —— 如果你真的想容忍“每隔 N tick 刷新一次”（例如担心客户端被别的东西关掉），再加一个节流版：

    /**
     * 至少相隔 throttleTicks 才重复发送一次
     */
    public static void ensureOnThrottled(ServerPlayer sp, ResourceLocation location, int radius, int throttleTicks) {
        long t = now(sp);
        var s = STATES.get(sp.getUUID());
        if (s != null && s.enabled && s.location == location)
            return;
        sendBlockView(sp, location, radius);
        STATES.put(sp.getUUID(), new BlockViewerService.State(true, location, radius));
    }

    // 清理：玩家离线/换维度可移除记录（可选）
    public static void clear(ServerPlayer sp) {STATES.remove(sp.getUUID());}

    public static void clearAll() {STATES.clear();}

    public static void sendBlockView(ServerPlayer sp, ResourceLocation location, int radius) {
        Dnetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp), new WallVisionSyncPacket(true, location, radius));
    }

    public static void sendBlockViewOff(ServerPlayer sp) {
        Dnetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp), new WallVisionSyncPacket(false, null, 0));
    }
}
