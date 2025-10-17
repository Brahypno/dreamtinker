package org.dreamtinker.dreamtinker.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.dreamtinker.dreamtinker.library.client.PlayerKeyStateProvider;

import java.util.function.Supplier;

// KeyStateMsg.java
public record KeyStateMsg(KeyKind kind, boolean down) {
    public static void encode(KeyStateMsg m, FriendlyByteBuf buf) {
        buf.writeEnum(m.kind);
        buf.writeBoolean(m.down);
    }

    public static KeyStateMsg decode(FriendlyByteBuf buf) {
        return new KeyStateMsg(buf.readEnum(KeyKind.class), buf.readBoolean());
    }

    public static void handle(KeyStateMsg m, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context c = ctx.get();

        // 只接受服务端方向的包（可选但推荐）
        if (!c.getDirection().getReceptionSide().isServer()){
            c.setPacketHandled(true);
            return;
        }

        c.enqueueWork(() -> {
            ServerPlayer sp = c.getSender();
            if (sp == null)
                return; // 断线/维度切换等场景

            // 正确获取并写入能力
            sp.getCapability(PlayerKeyStateProvider.PlayerKeyState.CAP).ifPresent(cap -> {
                cap.set(m.kind(), m.down());   // 注意：KeyStateMsg 是 record -> 访问器是 kind()/down()
            });
        });

        c.setPacketHandled(true);
    }

    public enum KeyKind {TOOL_INTERACT, MODE}
}

