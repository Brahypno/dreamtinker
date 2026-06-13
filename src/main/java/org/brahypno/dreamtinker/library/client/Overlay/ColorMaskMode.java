package org.brahypno.dreamtinker.library.client.Overlay;

public enum ColorMaskMode {
    NONE(0),
    OVERLAY(1),
    COLOR_ISOLATION(2),
    ATMOSPHERE(3);

    private final int id;

    ColorMaskMode(int id) {
        this.id = id;
    }

    public static ColorMaskMode byId(int id) {
        for (ColorMaskMode mode : values())
            if (mode.id == id)
                return mode;
        return NONE;
    }

    public int id() {
        return id;
    }
}
