package org.dreamtinker.dreamtinker.common.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.library.client.PlayerKeyStateProvider;
import org.dreamtinker.dreamtinker.network.KeyStateMsg;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = Dreamtinker.MODID)
public final class PlayerKeyBindingCap {

    @SubscribeEvent
    public static void onAttach(AttachCapabilitiesEvent<Entity> e) {
        if (e.getObject() instanceof Player){
            e.addCapability(PlayerKeyStateProvider.KEY, new PlayerKeyStateProvider());
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone e) {
        // 死亡/跨维度复制能力数据
        e.getOriginal().reviveCaps();
        e.getOriginal().getCapability(PlayerKeyStateProvider.PlayerKeyState.CAP)
         .ifPresent(oldCap -> e.getEntity()
                               .getCapability(PlayerKeyStateProvider.PlayerKeyState.CAP)
                               .ifPresent(newCap -> {
                                   CompoundTag nbt = oldCap.serializeNBT();
                                   newCap.deserializeNBT(nbt);
                               })
         );
        e.getOriginal().invalidateCaps();
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent e) {
        if (e.getEntity() instanceof ServerPlayer sp){
            sp.getCapability(PlayerKeyStateProvider.PlayerKeyState.CAP).ifPresent(cap -> {
                for (KeyStateMsg.KeyKind k : KeyStateMsg.KeyKind.values())
                    cap.set(k, false); // 保险：清状态
            });
        }
    }

}
