package org.dreamtinker.dreamtinker.library.client;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.network.KeyStateMsg;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.EnumMap;

public class PlayerKeyStateProvider implements ICapabilitySerializable<CompoundTag> {
    public static final ResourceLocation KEY = new ResourceLocation(Dreamtinker.MODID, "key_state");

    private final PlayerKeyState backend = new PlayerKeyState();
    private final LazyOptional<IPlayerKeyState> optional = LazyOptional.of(() -> backend);

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        return cap == PlayerKeyState.CAP ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return backend.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        backend.deserializeNBT(nbt);
    }

    // IPlayerKeyState.java
    public interface IPlayerKeyState extends INBTSerializable<CompoundTag> {
        boolean isDown(KeyStateMsg.KeyKind k);

        void set(KeyStateMsg.KeyKind k, boolean down);
    }

    // PlayerKeyState.java
    public static class PlayerKeyState implements IPlayerKeyState {
        private final EnumMap<KeyStateMsg.KeyKind, Boolean> map = new EnumMap<>(KeyStateMsg.KeyKind.class);

        public PlayerKeyState() {
            for (KeyStateMsg.KeyKind k : KeyStateMsg.KeyKind.values())
                map.put(k, false);
        }

        @Override
        public boolean isDown(KeyStateMsg.KeyKind k) {return map.getOrDefault(k, false);}

        @Override
        public void set(KeyStateMsg.KeyKind k, boolean down) {map.put(k, down);}

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag t = new CompoundTag();
            for (var e : map.entrySet())
                t.putBoolean(e.getKey().name(), e.getValue());
            return t;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            for (KeyStateMsg.KeyKind k : KeyStateMsg.KeyKind.values())
                map.put(k, nbt.getBoolean(k.name()));
        }

        // Capability 声明/注册略：CAP、Provider、Storage、Attach 等标准样板
        public static final Capability<IPlayerKeyState> CAP = CapabilityManager.get(new CapabilityToken<>() {});
    }
}
