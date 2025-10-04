package org.dreamtinker.dreamtinker.common.event.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.dreamtinker.dreamtinker.network.Dnetwork;
import org.dreamtinker.dreamtinker.network.KeyStateMsg;

import static org.dreamtinker.dreamtinker.common.event.client.KeyBindings.KEY_MODE;
import static org.dreamtinker.dreamtinker.common.event.client.KeyBindings.TOOL_INTERACT;

public class KeyPress {
    private static boolean lastToolInteract = false;
    private static boolean lastMode = false;

    @SubscribeEvent
    public static void onClientTickTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;
        var mc = Minecraft.getInstance();
        if (mc.player == null)
            return;

        boolean weapon_interact = TOOL_INTERACT.isDown(); // 是否正按着
        boolean mode = KEY_MODE.isDown();

        if (weapon_interact != lastToolInteract){
            Dnetwork.CHANNEL.sendToServer(new KeyStateMsg(KeyStateMsg.KeyKind.TOOL_INTERACT, weapon_interact));
            lastToolInteract = weapon_interact;
        }
        if (mode != lastMode){
            Dnetwork.CHANNEL.sendToServer(new KeyStateMsg(KeyStateMsg.KeyKind.MODE, mode));
            lastMode = mode;
        }

        // 可选：窗口失焦时清空（避免卡住“按下”状态）
        if (!mc.isWindowActive()){
            if (lastToolInteract){
                Dnetwork.CHANNEL.sendToServer(new KeyStateMsg(KeyStateMsg.KeyKind.TOOL_INTERACT, false));
                lastToolInteract = false;
            }
            if (lastMode){
                Dnetwork.CHANNEL.sendToServer(new KeyStateMsg(KeyStateMsg.KeyKind.MODE, false));
                lastMode = false;
            }
        }
    }
}
