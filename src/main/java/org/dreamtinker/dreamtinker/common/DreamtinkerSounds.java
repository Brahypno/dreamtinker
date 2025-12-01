package org.dreamtinker.dreamtinker.common;

import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.DreamtinkerModule;

public class DreamtinkerSounds extends DreamtinkerModule {
    public static final RegistryObject<SoundEvent> CHAINSAW_START =
            SOUND_EVENTS.register("chainsaw.start", () -> SoundEvent.createVariableRangeEvent(Dreamtinker.getLocation("chainsaw.start")));
    public static final RegistryObject<SoundEvent> CHAINSAW_LOOP =
            SOUND_EVENTS.register("chainsaw.loop", () -> SoundEvent.createVariableRangeEvent(Dreamtinker.getLocation("chainsaw.loop")));
    public static final RegistryObject<SoundEvent> CHAINSAW_STOP =
            SOUND_EVENTS.register("chainsaw.stop", () -> SoundEvent.createVariableRangeEvent(Dreamtinker.getLocation("chainsaw.stop")));


}
