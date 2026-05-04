package org.dreamtinker.dreamtinker.Entity.capabilities;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import org.dreamtinker.dreamtinker.common.capabilities.IShellHeart;

public class ShellHeartImpl implements IShellHeart {
    public static final Capability<IShellHeart> INSTANCE =
            CapabilityManager.get(new CapabilityToken<>() {});

    private float ShellHeart = 0;
    private int HeartColour = 0xFFC4B5D6;

    @Override
    public float get() {
        return ShellHeart;
    }

    @Override
    public void set(float value) {
        this.ShellHeart = Math.max(0, value);
    }

    @Override
    public void add(float value) {
        set(ShellHeart + value);
    }

    @Override
    public int getHeartColour() {
        return HeartColour;
    }

    @Override
    public void setHeartColour(int value) {
        this.HeartColour = value;
    }
}