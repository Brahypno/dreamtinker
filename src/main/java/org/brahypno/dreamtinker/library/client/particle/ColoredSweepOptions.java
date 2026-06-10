package org.brahypno.dreamtinker.library.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import org.brahypno.dreamtinker.DreamtinkerModule;

public record ColoredSweepOptions(
        float r,
        float g,
        float b,
        float alpha,
        float size,
        float scaleX,
        float scaleY,
        float roll
) implements ParticleOptions {
    public static final Codec<ColoredSweepOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("r").forGetter(ColoredSweepOptions::r),
            Codec.FLOAT.fieldOf("g").forGetter(ColoredSweepOptions::g),
            Codec.FLOAT.fieldOf("b").forGetter(ColoredSweepOptions::b),
            Codec.FLOAT.fieldOf("alpha").forGetter(ColoredSweepOptions::alpha),
            Codec.FLOAT.fieldOf("size").forGetter(ColoredSweepOptions::size),
            Codec.FLOAT.fieldOf("scale_x").forGetter(ColoredSweepOptions::scaleX),
            Codec.FLOAT.fieldOf("scale_y").forGetter(ColoredSweepOptions::scaleY),
            Codec.FLOAT.fieldOf("roll").forGetter(ColoredSweepOptions::roll)
    ).apply(instance, ColoredSweepOptions::new));

    public static final Deserializer<ColoredSweepOptions> DESERIALIZER = new Deserializer<>() {
        @Override
        public ColoredSweepOptions fromCommand(ParticleType<ColoredSweepOptions> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float r = reader.readFloat();
            reader.expect(' ');
            float g = reader.readFloat();
            reader.expect(' ');
            float b = reader.readFloat();
            reader.expect(' ');
            float alpha = reader.readFloat();
            reader.expect(' ');
            float size = reader.readFloat();
            reader.expect(' ');
            float scaleX = reader.readFloat();
            reader.expect(' ');
            float scaleY = reader.readFloat();
            reader.expect(' ');
            float roll = reader.readFloat();
            return new ColoredSweepOptions(r, g, b, alpha, size, scaleX, scaleY, roll);
        }

        @Override
        public ColoredSweepOptions fromNetwork(ParticleType<ColoredSweepOptions> type, FriendlyByteBuf buf) {
            return new ColoredSweepOptions(
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readFloat()
            );
        }
    };

    public static ColoredSweepOptions ofARGB(int argb, float size, float scaleX, float scaleY, float roll) {
        float a = ((argb >> 24) & 255) / 255.0F;
        float r = ((argb >> 16) & 255) / 255.0F;
        float g = ((argb >> 8) & 255) / 255.0F;
        float b = (argb & 255) / 255.0F;
        return new ColoredSweepOptions(r, g, b, a, size, scaleX, scaleY, roll);
    }

    public static ColoredSweepOptions ofRGB(int rgb, int alpha, float size, float scaleX, float scaleY, float roll) {
        float a = alpha / 255.0F;
        float r = ((rgb >> 16) & 255) / 255.0F;
        float g = ((rgb >> 8) & 255) / 255.0F;
        float b = (rgb & 255) / 255.0F;
        return new ColoredSweepOptions(r, g, b, a, size, scaleX, scaleY, roll);
    }

    @Override
    public ParticleType<?> getType() {
        return DreamtinkerModule.COLORED_SWEEP.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeFloat(this.r);
        buf.writeFloat(this.g);
        buf.writeFloat(this.b);
        buf.writeFloat(this.alpha);
        buf.writeFloat(this.size);
        buf.writeFloat(this.scaleX);
        buf.writeFloat(this.scaleY);
        buf.writeFloat(this.roll);
    }

    @Override
    public String writeToString() {
        return String.format(
                "%s %.4f %.4f %.4f %.4f %.4f %.4f %.4f %.4f",
                DreamtinkerModule.COLORED_SWEEP.getId(),
                this.r,
                this.g,
                this.b,
                this.alpha,
                this.size,
                this.scaleX,
                this.scaleY,
                this.roll
        );
    }
}
