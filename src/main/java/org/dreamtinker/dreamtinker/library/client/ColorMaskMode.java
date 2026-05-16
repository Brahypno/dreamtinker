package org.dreamtinker.dreamtinker.library.client;

public enum ColorMaskMode {
    NONE,
    OVERLAY,
    COLOR_ISOLATION,
    ATMOSPHERE;

    public static ColorMaskMode byId(int id) {
        ColorMaskMode[] values = values();
        return id >= 0 && id < values.length ? values[id] : NONE;
    }
}