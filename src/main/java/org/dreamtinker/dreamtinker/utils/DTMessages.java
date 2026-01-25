package org.dreamtinker.dreamtinker.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public final class DTMessages {
    private DTMessages() {}

    /**
     * 安全：在 Dedicated Server 上调用也不会崩
     */
    public static void clientChat(Component msg, boolean actionBar) {
        DistExecutor.safeRunWhenOn(
                Dist.CLIENT,
                () -> new DistExecutor.SafeRunnable() {
                    @Override
                    public void run() {
                        ClientOnly.send(msg, actionBar);
                    }
                }
        );
    }

    @net.minecraftforge.api.distmarker.OnlyIn(net.minecraftforge.api.distmarker.Dist.CLIENT)
    public static final class ClientOnly {
        private ClientOnly() {}

        public static void send(Component msg, boolean actionBar) {
            Minecraft mc = Minecraft.getInstance();
            mc.execute(() -> {
                if (mc.player != null){
                    mc.player.displayClientMessage(msg, actionBar);
                }
            });
        }
    }
}
