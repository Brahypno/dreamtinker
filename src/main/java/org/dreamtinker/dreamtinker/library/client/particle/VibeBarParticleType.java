package org.dreamtinker.dreamtinker.library.client.particle;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;
import org.jetbrains.annotations.NotNull;

// VibeBarParticleType.java
public class VibeBarParticleType extends ParticleType<VibeBarParticleOptions> {
    public VibeBarParticleType() {
        super(false, VibeBarParticleOptions.DESERIALIZER);
    }

    @Override
    public @NotNull Codec<VibeBarParticleOptions> codec() {
        return VibeBarParticleOptions.CODEC;
    }

}

