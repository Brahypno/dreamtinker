package org.brahypno.dreamtinker.library.client.sound;

import net.minecraft.world.entity.LivingEntity;
import org.brahypno.dreamtinker.common.DreamtinkerSounds;

public class ChainSawSounds extends EntityLoopSound {
    public ChainSawSounds(LivingEntity user) {
        super(user, DreamtinkerSounds.CHAINSAW_LOOP.get());
    }
}
