package org.dreamtinker.dreamtinker.common.capabilities;

public interface IShellHeart {
    float get();

    void set(float value);

    void add(float value);

    int getHeartColour();

    void setHeartColour(int value);

    default void copyFrom(IShellHeart other) {
        set(other.get());
        setHeartColour(other.getHeartColour());
    }
}
