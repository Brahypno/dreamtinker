package org.brahypno.dreamtinker.library.compat.goety;

import net.minecraftforge.eventbus.api.IEventBus;

/**
 * Optional Goety integration entry point. This class is only initialized after Forge confirms that Goety is loaded.
 */
public final class GoetyCompat {
    public static final String MOD_ID = "goety";

    private GoetyCompat() {}

    public static void register(IEventBus modEventBus) {
        GoetyTransmutationRegistry.SERIALIZERS.register(modEventBus);
        GoetyTransmutationRegistry.RITUALS.register(modEventBus);
    }
}
