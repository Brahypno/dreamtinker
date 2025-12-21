package org.dreamtinker.dreamtinker.library.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.dreamtinker.dreamtinker.DreamtinkerModule;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public record VibeBarParticleOptions(
        int targetId,
        float barDirX, float barDirZ,
        float along,
        int argb,
        int lifetimeTicks,
        float amplitude,
        float frequencyHz,
        float yFrac,
        float phase
) implements ParticleOptions {

    // 注意：这里的 CODEC 不包含 type 字段
    public static final Codec<VibeBarParticleOptions> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("targetId").forGetter(VibeBarParticleOptions::targetId),
            Codec.FLOAT.fieldOf("barDirX").forGetter(VibeBarParticleOptions::barDirX),
            Codec.FLOAT.fieldOf("barDirZ").forGetter(VibeBarParticleOptions::barDirZ),
            Codec.FLOAT.fieldOf("along").forGetter(VibeBarParticleOptions::along),
            Codec.INT.fieldOf("argb").forGetter(VibeBarParticleOptions::argb),
            Codec.INT.fieldOf("life").forGetter(VibeBarParticleOptions::lifetimeTicks),
            Codec.FLOAT.fieldOf("amp").forGetter(VibeBarParticleOptions::amplitude),
            Codec.FLOAT.fieldOf("hz").forGetter(VibeBarParticleOptions::frequencyHz),
            Codec.FLOAT.fieldOf("yFrac").forGetter(VibeBarParticleOptions::yFrac),
            Codec.FLOAT.fieldOf("phase").forGetter(VibeBarParticleOptions::phase)
    ).apply(i, VibeBarParticleOptions::new));

    @SuppressWarnings("deprecation")
    public static final Deserializer<VibeBarParticleOptions> DESERIALIZER = new Deserializer<>() {
        @Override
        public @NotNull VibeBarParticleOptions fromNetwork(ParticleType<VibeBarParticleOptions> type, FriendlyByteBuf buf) {
            // 这里 type 参数不用存起来，忽略即可
            return new VibeBarParticleOptions(
                    buf.readVarInt(),
                    buf.readFloat(), buf.readFloat(),
                    buf.readFloat(),
                    buf.readInt(),
                    buf.readVarInt(),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readFloat()
            );
        }

        @Override
        public @NotNull VibeBarParticleOptions fromCommand(ParticleType<VibeBarParticleOptions> type, StringReader reader)
                throws CommandSyntaxException {

            // 必填：targetId dx dz along
            reader.expect(' ');
            int targetId = reader.readInt();

            reader.expect(' ');
            float dx = reader.readFloat();

            reader.expect(' ');
            float dz = reader.readFloat();

            reader.expect(' ');
            float along = reader.readFloat();

            // 下面都是可选参数，给默认值
            int argb = 0xFF69B5DB;   // 默认颜色（你可换）
            int life = 12;           // 默认 0.6s
            float amp = 0.07f;
            float hz = 26.0f;
            float yFrac = 0.60f;
            float phase = 0.0f;

            // 如果没有更多内容，直接返回
            if (!reader.canRead()){
                return new VibeBarParticleOptions(targetId, dx, dz, along, argb, life, amp, hz, yFrac, phase);
            }

            // 若后面还有参数，按顺序读取；颜色支持 #RRGGBB / 0xAARRGGBB / 十进制
            if (reader.canRead() && reader.peek() == ' '){
                reader.skip();
                argb = readColorInt(reader, argb);
            }else {
                return new VibeBarParticleOptions(targetId, dx, dz, along, argb, life, amp, hz, yFrac, phase);
            }

            if (reader.canRead() && reader.peek() == ' '){
                reader.skip();
                life = reader.readInt();
            }else
                return new VibeBarParticleOptions(targetId, dx, dz, along, argb, life, amp, hz, yFrac, phase);

            if (reader.canRead() && reader.peek() == ' '){
                reader.skip();
                amp = reader.readFloat();
            }else
                return new VibeBarParticleOptions(targetId, dx, dz, along, argb, life, amp, hz, yFrac, phase);

            if (reader.canRead() && reader.peek() == ' '){
                reader.skip();
                hz = reader.readFloat();
            }else
                return new VibeBarParticleOptions(targetId, dx, dz, along, argb, life, amp, hz, yFrac, phase);

            if (reader.canRead() && reader.peek() == ' '){
                reader.skip();
                yFrac = reader.readFloat();
            }else
                return new VibeBarParticleOptions(targetId, dx, dz, along, argb, life, amp, hz, yFrac, phase);

            if (reader.canRead() && reader.peek() == ' '){
                reader.skip();
                phase = reader.readFloat();
            }

            return new VibeBarParticleOptions(targetId, dx, dz, along, argb, life, amp, hz, yFrac, phase);
        }

        /**
         * 读取颜色：支持：
         *  - #RRGGBB
         *  - 0xAARRGGBB 或 0xRRGGBB
         *  - 十进制 int
         */
        private static int readColorInt(StringReader reader, int fallback) throws CommandSyntaxException {
            int start = reader.getCursor();

            // 读一个 token（到空格结束）
            String token = reader.readUnquotedString();
            if (token.isEmpty())
                return fallback;

            try {
                // #RRGGBB
                if (token.charAt(0) == '#'){
                    String hex = token.substring(1);
                    if (hex.length() == 6){
                        int rgb = Integer.parseUnsignedInt(hex, 16);
                        return 0xFF000000 | rgb;
                    }
                    if (hex.length() == 8){
                        int argb = (int) Long.parseLong(hex, 16);
                        return argb;
                    }
                    throw new NumberFormatException("Invalid hex length");
                }

                // 0x...
                if (token.startsWith("0x") || token.startsWith("0X")){
                    String hex = token.substring(2);
                    long v = Long.parseLong(hex, 16);
                    if (hex.length() <= 6){
                        return (int) (0xFF000000L | v);
                    }
                    return (int) v;
                }

                // 十进制 int
                return Integer.parseInt(token);

            }
            catch (Exception ex) {
                reader.setCursor(start);
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().createWithContext(reader, token);
            }
        }

    };

    @Override
    public ParticleType<?> getType() {
        // 关键：type 由这里决定
        return DreamtinkerModule.VIBE_BAR.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeVarInt(targetId);
        buf.writeFloat(barDirX);
        buf.writeFloat(barDirZ);
        buf.writeFloat(along);
        buf.writeInt(argb);
        buf.writeVarInt(lifetimeTicks);
        buf.writeFloat(amplitude);
        buf.writeFloat(frequencyHz);
        buf.writeFloat(yFrac);
        buf.writeFloat(phase);
    }

    @Override
    public @NotNull String writeToString() {
        // 建议包含粒子 type 的 key + 参数，符合原版风格
        ResourceLocation key = ForgeRegistries.PARTICLE_TYPES.getKey(getType());
        return String.format(Locale.ROOT,
                             "%s %d %.5f %.5f %.5f %d %d %.5f %.5f %.5f %.5f",
                             key, targetId, barDirX, barDirZ, along, argb, lifetimeTicks, amplitude, frequencyHz, yFrac, phase
        );
    }
}
