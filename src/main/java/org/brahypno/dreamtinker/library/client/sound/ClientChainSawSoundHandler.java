package org.brahypno.dreamtinker.library.client.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.tools.DreamtinkerModifiers;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ClientChainSawSoundHandler {
    private ClientChainSawSoundHandler() {}

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null){
            ClientSoundChecker.clearAllSoundCaches();
            return;
        }

        for (Player player : minecraft.level.players()) {
            if (shouldPlay(player)){
                ClientSoundChecker.playWorldSound(player, (byte) 1);
            }else {
                ClientSoundChecker.clearSoundCacheFor(player);
            }
        }
    }

    private static boolean shouldPlay(Player player) {
        if (!player.isAlive())
            return false;
        if (player.isSilent())
            return false;
        if (!player.isUsingItem())
            return false;

        ItemStack stack = player.getUseItem();
        if (stack.isEmpty())
            return false;

        return 0 < ModifierUtil.getModifierLevel(stack, DreamtinkerModifiers.death_shredder.getId());
    }
}