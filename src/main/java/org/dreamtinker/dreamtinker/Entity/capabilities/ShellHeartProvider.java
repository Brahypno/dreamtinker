package org.dreamtinker.dreamtinker.Entity.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;
import org.dreamtinker.dreamtinker.common.capabilities.IShellHeart;
import org.dreamtinker.dreamtinker.network.DNetwork;
import org.dreamtinker.dreamtinker.network.ShellHeartSyncPacket;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ShellHeartProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    private final IShellHeart backend = new ShellHeartImpl();
    private final LazyOptional<IShellHeart> optional = LazyOptional.of(() -> backend);

    public static Optional<IShellHeart> getShellHeart(LivingEntity entity) {
        return entity.getCapability(ShellHeartImpl.INSTANCE).resolve();
    }

    public static void syncToClient(ServerPlayer player) {
        ShellHeartProvider.getShellHeart(player).ifPresent(shellHeart -> {
            DNetwork.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new ShellHeartSyncPacket(
                            shellHeart.get(),
                            shellHeart.getHeartColour()
                    )
            );
        });
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return cap == ShellHeartImpl.INSTANCE ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("ShellHeart", backend.get());
        tag.putFloat("ShellHeartColor", backend.getHeartColour());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        backend.set(tag.getFloat("ShellHeart"));
        backend.setHeartColour(tag.getInt("ShellHeartColor"));
    }
}
