package org.dreamtinker.dreamtinker.common.event.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.network.DNetwork;
import org.dreamtinker.dreamtinker.network.KeyStateMsg;

import static org.dreamtinker.dreamtinker.common.event.client.KeyBindings.KEY_MODE;
import static org.dreamtinker.dreamtinker.common.event.client.KeyBindings.TOOL_INTERACT;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
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
            DNetwork.CHANNEL.sendToServer(new KeyStateMsg(KeyStateMsg.KeyKind.TOOL_INTERACT, weapon_interact));
            lastToolInteract = weapon_interact;
        }
        if (mode != lastMode){
            DNetwork.CHANNEL.sendToServer(new KeyStateMsg(KeyStateMsg.KeyKind.MODE, mode));
            lastMode = mode;
        }

        // 可选：窗口失焦时清空（避免卡住“按下”状态）
        if (!mc.isWindowActive()){
            if (lastToolInteract){
                DNetwork.CHANNEL.sendToServer(new KeyStateMsg(KeyStateMsg.KeyKind.TOOL_INTERACT, false));
                lastToolInteract = false;
            }
            if (lastMode){
                DNetwork.CHANNEL.sendToServer(new KeyStateMsg(KeyStateMsg.KeyKind.MODE, false));
                lastMode = false;
            }
        }
    }
}
