package org.brahypno.dreamtinker.common.event.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.network.DNetwork;
import org.brahypno.dreamtinker.network.KeyStateMsg;

import static org.brahypno.dreamtinker.common.event.client.KeyBindings.KEY_MODE;
import static org.brahypno.dreamtinker.common.event.client.KeyBindings.TOOL_INTERACT;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class KeyPress {
    private static boolean lastToolInteract = false;
    private static boolean lastMode = false;

    public static void resetKeyState() {
        lastToolInteract = false;
        lastMode = false;
    }

    @SubscribeEvent
    public static void onClientTickTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;
        var mc = Minecraft.getInstance();
        if (mc.player == null){
            resetKeyState();
            return;
        }

        // Apply focus before comparing states, so a stale key cannot send down/up every tick.
        boolean weapon_interact = mc.isWindowActive() && TOOL_INTERACT.isDown();
        boolean mode = mc.isWindowActive() && KEY_MODE.isDown();

        if (weapon_interact != lastToolInteract){
            DNetwork.CHANNEL.sendToServer(new KeyStateMsg(KeyStateMsg.KeyKind.TOOL_INTERACT, weapon_interact));
            lastToolInteract = weapon_interact;
        }
        if (mode != lastMode){
            DNetwork.CHANNEL.sendToServer(new KeyStateMsg(KeyStateMsg.KeyKind.MODE, mode));
            lastMode = mode;
        }

    }
}
