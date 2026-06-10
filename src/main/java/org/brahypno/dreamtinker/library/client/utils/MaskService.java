package org.brahypno.dreamtinker.library.client.utils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraftforge.network.PacketDistributor;
import org.brahypno.dreamtinker.library.client.Overlay.ColorMaskMode;
import org.brahypno.dreamtinker.network.DNetwork;
import org.brahypno.dreamtinker.network.S2CColorMaskToggle;

import java.util.*;

public final class MaskService {
    private static final Map<UUID, Map<ResourceLocation, Instance>> ACTIVE = new HashMap<>();
    private static final Map<UUID, SentState> SENT = new HashMap<>();
    private static long orderCounter = 0;

    private MaskService() {}

    public static void overlay(ServerPlayer sp, ResourceLocation source, int argb, int priority, int fadeIn) {
        apply(sp, source, ColorMaskMode.OVERLAY, argb, 0, 0.36F, 1.0F, priority, fadeIn);
    }

    public static void colorIsolation(ServerPlayer sp, ResourceLocation source, int rgb, int range, int priority, int fadeIn) {
        colorIsolation(sp, source, rgb, range, 0.62F, 1.18F, priority, fadeIn);
    }

    public static void colorIsolation(ServerPlayer sp, ResourceLocation source, int rgb, int range, float grayStrength, float vividStrength, int priority, int fadeIn) {
        apply(sp, source, ColorMaskMode.COLOR_ISOLATION, rgb, range, grayStrength, vividStrength, priority, fadeIn);
    }

    public static void atmosphere(ServerPlayer sp, ResourceLocation source, int argb, int priority, int fadeIn) {
        atmosphere(sp, source, argb, 0.10F, 0.18F, priority, fadeIn);
    }

    public static void atmosphere(ServerPlayer sp, ResourceLocation source, int argb, float darkenStrength, float tintStrength, int priority, int fadeIn) {
        apply(sp, source, ColorMaskMode.ATMOSPHERE, argb, 0, darkenStrength, tintStrength, priority, fadeIn);
    }

    public static void apply(ServerPlayer sp, ResourceLocation source, ColorMaskMode mode, int argb, int range, float grayStrength, float vividStrength, int priority, int fadeIn) {
        if (sp == null || source == null || mode == null || mode == ColorMaskMode.NONE)
            return;

        Instance instance =
                new Instance(source, mode, argb, Mth.clamp(range, 0, 255), Mth.clamp(grayStrength, 0.0F, 1.0F), Mth.clamp(vividStrength, 0.0F, 3.0F), priority,
                             ++orderCounter);
        ACTIVE.computeIfAbsent(sp.getUUID(), id -> new HashMap<>()).put(source, instance);
        syncSelected(sp, fadeIn, 0);
    }

    public static void remove(ServerPlayer sp, ResourceLocation source, int holdTicks, int fadeOutTicks, int fadeInForFallback) {
        if (sp == null || source == null)
            return;

        UUID uuid = sp.getUUID();
        Map<ResourceLocation, Instance> map = ACTIVE.get(uuid);
        if (map == null || map.isEmpty())
            return;

        Instance visibleBeforeRemove = selected(sp);
        Instance instance = map.get(source);
        if (instance == null)
            return;

        boolean removedWasVisible = visibleBeforeRemove != null && visibleBeforeRemove.source.equals(source);

        if (removedWasVisible && holdTicks > 0){
            instance.holdingRemove = true;
            instance.holdUntil = sp.serverLevel().getGameTime() + holdTicks;
            instance.fadeOutTicks = Math.max(0, fadeOutTicks);
            instance.fadeInForFallback = Math.max(0, fadeInForFallback);
            return;
        }

        map.remove(source);

        if (map.isEmpty()){
            ACTIVE.remove(uuid);
            sendOffIfNeeded(sp, fadeOutTicks);
        }else {
            if (removedWasVisible)
                sendOffIfNeeded(sp, fadeOutTicks);
            syncSelected(sp, fadeInForFallback, 0);
        }
    }

    public static void remove(ServerPlayer sp, ResourceLocation source) {
        remove(sp, source, 10, 0, 6);
    }

    public static void clear(ServerPlayer sp, int fadeOut) {
        if (sp == null)
            return;
        ACTIVE.remove(sp.getUUID());
        sendOffIfNeeded(sp, fadeOut);
    }

    public static void clear(ServerPlayer sp) {
        clear(sp, 6);
    }

    public static void clearSilent(ServerPlayer sp) {
        if (sp == null)
            return;
        ACTIVE.remove(sp.getUUID());
        SENT.remove(sp.getUUID());
    }

    public static void clearAll() {
        ACTIVE.clear();
        SENT.clear();
    }

    private static void syncSelected(ServerPlayer sp, int fadeIn, int fadeOutIfEmpty) {
        Instance selected = selected(sp);

        if (selected == null){
            //Dreamtinker.LOGGER.info("[Mask] selected none for {}", sp.getName().getString());
            sendOffIfNeeded(sp, fadeOutIfEmpty);
            return;
        }


        SentState sent = SENT.get(sp.getUUID());
        if (sent != null && sent.sameAs(selected)){
            return;
        }

        sendOn(sp, selected, fadeIn);
        SENT.put(sp.getUUID(), new SentState(true, selected.mode, selected.argb, selected.range, selected.grayStrength, selected.vividStrength));
    }

    private static Instance selected(ServerPlayer sp) {
        Map<ResourceLocation, Instance> map = ACTIVE.get(sp.getUUID());
        if (map == null || map.isEmpty())
            return null;

        return map.values().stream()
                  .max(Comparator.comparingInt((Instance i) -> i.priority).thenComparingLong(i -> i.order))
                  .orElse(null);
    }

    private static void sendOn(ServerPlayer sp, Instance i, int fadeIn) {
        DNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp),
                              new S2CColorMaskToggle(true, i.mode, i.argb, i.range, i.grayStrength, i.vividStrength, fadeIn, 0));
    }

    private static void sendOffIfNeeded(ServerPlayer sp, int fadeOut) {
        SentState sent = SENT.get(sp.getUUID());
        if (sent == null || !sent.enabled)
            return;

        DNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp), S2CColorMaskToggle.off(fadeOut));
        SENT.put(sp.getUUID(), new SentState(false, ColorMaskMode.NONE, 0, 0, 0.36F, 1.0F));
    }

    public static void tick(MinecraftServer server) {
        if (server == null || ACTIVE.isEmpty())
            return;

        for (ServerPlayer sp : server.getPlayerList().getPlayers()) {
            UUID uuid = sp.getUUID();
            Map<ResourceLocation, Instance> map = ACTIVE.get(uuid);
            if (map == null || map.isEmpty())
                continue;

            long now = sp.serverLevel().getGameTime();
            boolean changed = false;
            int fadeOut = 0;
            int fallbackFadeIn = 0;
            boolean removedVisible = false;

            Instance visibleBeforeRemove = selected(sp);

            Iterator<Map.Entry<ResourceLocation, Instance>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<ResourceLocation, Instance> entry = it.next();
                Instance i = entry.getValue();

                if (!i.holdingRemove || now < i.holdUntil)
                    continue;

                removedVisible = visibleBeforeRemove != null && visibleBeforeRemove.source.equals(i.source);
                fadeOut = i.fadeOutTicks;
                fallbackFadeIn = i.fadeInForFallback;
                it.remove();
                changed = true;
            }

            if (!changed)
                continue;

            if (map.isEmpty()){
                ACTIVE.remove(uuid);
                if (removedVisible)
                    sendOffIfNeeded(sp, fadeOut);
            }else {
                if (removedVisible)
                    sendOffIfNeeded(sp, fadeOut);
                syncSelected(sp, fallbackFadeIn, 0);
            }
        }
    }

    private static final class SentState {
        final boolean enabled;
        final ColorMaskMode mode;
        final int argb, range;
        final float grayStrength, vividStrength;

        SentState(boolean enabled, ColorMaskMode mode, int argb, int range, float grayStrength, float vividStrength) {
            this.enabled = enabled;
            this.mode = mode;
            this.argb = argb;
            this.range = range;
            this.grayStrength = grayStrength;
            this.vividStrength = vividStrength;
        }

        boolean sameAs(Instance i) {
            return enabled && i != null && mode == i.mode && argb == i.argb && range == i.range && Float.compare(grayStrength, i.grayStrength) == 0 &&
                   Float.compare(vividStrength, i.vividStrength) == 0;
        }
    }

    private static final class Instance {
        final ResourceLocation source;
        final ColorMaskMode mode;
        final int argb, range, priority;
        final float grayStrength, vividStrength;
        final long order;

        boolean holdingRemove;
        long holdUntil;
        int fadeOutTicks;
        int fadeInForFallback;

        Instance(ResourceLocation source, ColorMaskMode mode, int argb, int range, float grayStrength, float vividStrength, int priority, long order) {
            this.source = source;
            this.mode = mode;
            this.argb = argb;
            this.range = range;
            this.grayStrength = grayStrength;
            this.vividStrength = vividStrength;
            this.priority = priority;
            this.order = order;
            this.holdingRemove = false;
            this.holdUntil = Long.MAX_VALUE;
            this.fadeOutTicks = 0;
            this.fadeInForFallback = 0;
        }
    }
}