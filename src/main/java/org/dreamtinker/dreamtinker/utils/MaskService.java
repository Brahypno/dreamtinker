package org.dreamtinker.dreamtinker.utils;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import org.dreamtinker.dreamtinker.network.Dnetwork;
import org.dreamtinker.dreamtinker.network.S2CColorMaskToggle;

// MaskService.java  (server-side)
public final class MaskService {
    private static final class State {
        boolean enabled;
        int argb;
        long lastSent;

        State(boolean e, int c, long t) {
            enabled = e;
            argb = c;
            lastSent = t;
        }
    }

    private static final java.util.Map<java.util.UUID, State> STATES = new java.util.HashMap<>();

    private static long now(ServerPlayer sp) {return sp.serverLevel().getGameTime();}

    /**
     * 只在“未开”或“颜色改变”时发送；否则不动（去重）
     */
    public static void ensureOn(ServerPlayer sp, int argb, int fadeIn) {
        var s = STATES.get(sp.getUUID());
        if (s != null && s.enabled && s.argb == argb)
            return;   // 已经是同样的遮罩 → 不发
        sendMaskOn(sp, argb, fadeIn);
        STATES.put(sp.getUUID(), new State(true, argb, now(sp)));
    }

    /**
     * 只在“当前是开”的时候发送关闭；否则不动
     */
    public static void ensureOff(ServerPlayer sp, int fadeOut) {
        var s = STATES.get(sp.getUUID());
        if (s == null || !s.enabled)
            return;                    // 本来就关着 → 不发
        sendMaskOff(sp, fadeOut);
        STATES.put(sp.getUUID(), new State(false, 0, now(sp)));
    }

    // —— 如果你真的想容忍“每隔 N tick 刷新一次”（例如担心客户端被别的东西关掉），再加一个节流版：

    /**
     * 至少相隔 throttleTicks 才重复发送一次
     */
    public static void ensureOnThrottled(ServerPlayer sp, int argb, int fadeIn, int throttleTicks) {
        long t = now(sp);
        var s = STATES.get(sp.getUUID());
        if (s != null && s.enabled && s.argb == argb && (t - s.lastSent) < throttleTicks)
            return;
        sendMaskOn(sp, argb, fadeIn);
        STATES.put(sp.getUUID(), new State(true, argb, t));
    }

    // 清理：玩家离线/换维度可移除记录（可选）
    public static void clear(ServerPlayer sp) {STATES.remove(sp.getUUID());}

    public static void clearAll() {STATES.clear();}

    public static void sendMaskOn(ServerPlayer sp, int argb, int fadeIn) {
        Dnetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp), new S2CColorMaskToggle(true, argb, fadeIn, 0));
    }

    public static void sendMaskOff(ServerPlayer sp, int fadeOut) {
        Dnetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp), new S2CColorMaskToggle(false, 0, 0, fadeOut));
    }
}

