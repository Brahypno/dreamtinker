#version 150

uniform sampler2D DiffuseSampler;

uniform int Mode;
uniform vec3 TargetColor;
uniform float Range;
uniform float GrayStrength;
uniform float VividStrength;
uniform float EffectStrength;
uniform vec2 InSize;
uniform vec2 OutSize;

in vec2 texCoord;

out vec4 fragColor;

vec3 rgb2hsv(vec3 c) {
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;

    return vec3(
        abs(q.z + (q.w - q.y) / (6.0 * d + e)),
        d / (q.x + e),
        q.x
    );
}

float circularHueDistance(float a, float b) {
    float d = abs(a - b);
    return min(d, 1.0 - d);
}

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);
    vec3 rgb = color.rgb;

    if (InSize.x < 0.0 || InSize.y < 0.0 || OutSize.x < 0.0 || OutSize.y < 0.0) {
        fragColor = color;
        return;
    }
    /*
     * EffectStrength:
     * Java 端建议传 alphaFactor() * argbAlphaFactor()。
     * 这里再做一层曲线，让 0xCC 这种强度不会残留过多原图颜色。
     */
    float rawStrength = clamp(EffectStrength, 0.0, 1.0);

    if (Mode != 2 && Mode != 3) {
        fragColor = color;
        return;
    }

    float strength = rawStrength;
    if (Mode == 2) {
        strength = 1.0 - pow(1.0 - rawStrength, 2.2);
    }
    if (Mode == 3) {
        vec3 hsv = rgb2hsv(rgb);
        float luminance = dot(rgb, vec3(0.299, 0.587, 0.114));

        /*
         * 天空/亮部影响权重。
         * 起点降低一点，让白天环境更容易被氛围影响。
         */
        float brightMask = smoothstep(0.30, 0.88, luminance);

        /*
         * 保护太阳、雨、白色粒子：
         * 它们通常是高亮 + 低饱和。
         */
        float highLum = smoothstep(0.70, 0.98, luminance);
        float lowSat = 1.0 - smoothstep(0.08, 0.24, hsv.y);
        float whiteProtect = highLum * lowSat;

        /*
         * 低亮度细节保护，避免暗处被压成一团黑。
         */
        float lowLightProtect = 1.0 - smoothstep(0.10, 0.36, luminance);

        /*
         * 最终影响权重：
         * - 太阳/雨这类白亮低饱和像素保护较强
         * - 低亮度区域保护中等
         * - 普通天空、远景、亮雾、环境正常受影响
         */
        float affect = brightMask;
        affect *= 1.0 - whiteProtect * 0.85;
        affect *= 1.0 - lowLightProtect * 0.45;
        affect = clamp(affect, 0.0, 1.0);

        /*
         * ATMOSPHERE 参数语义：
         * GrayStrength  = 暗化强度
         * VividStrength = 紫黑染色强度
         */
        float darkenAmount = GrayStrength * (0.18 + affect * 0.82);
        float tintAmount = VividStrength * (0.18 + affect * 1.35);

        /*
         * 白亮保护像素少暗化、少染色。
         */
        darkenAmount *= 1.0 - whiteProtect * 0.90;
        tintAmount *= 1.0 - whiteProtect * 0.75;

        darkenAmount = clamp(darkenAmount, 0.0, 0.42);
        tintAmount = clamp(tintAmount, 0.0, 0.72);

        vec3 atmosphereColor = TargetColor;

        /*
         * 先往紫黑色偏移，但不直接牺牲全部亮度。
         */
        vec3 colorShift = mix(rgb, atmosphereColor, tintAmount);

        /*
         * 保留一部分原亮度结构，避免天空/雨直接脏黑。
         */
        float shiftedLum = dot(colorShift, vec3(0.299, 0.587, 0.114));
        float lumFix = luminance / max(shiftedLum, 0.001);
        colorShift *= mix(1.0, lumFix, 0.42);

        /*
         * 再轻微压暗，营造开大氛围。
         */
        colorShift *= 1.0 - darkenAmount;

        /*
         * strength 用原始强度，不要用 COLOR_ISOLATION 的激进曲线。
         */
        vec3 finalColor = mix(rgb, colorShift, strength);

        fragColor = vec4(finalColor, color.a);
        return;
    }
    vec3 hsv = rgb2hsv(rgb);
    vec3 targetHsv = rgb2hsv(TargetColor);

    /*
     * Hue gate:
     * Range 来自 Java 的 0..255，然后在 Java 端除以 255 传入。
     * 这里把它压缩成 hue 容忍度。
     * 例如 Range = 50，则 Range/255≈0.196，hueTolerance≈0.039。
     */
    float hueDist = circularHueDistance(hsv.x, targetHsv.x);
    float hueTolerance = clamp(Range * 0.20, 0.012, 0.13);
    float hueFeather = max(0.008, hueTolerance * 0.75);
    float hueKeep = 1.0 - smoothstep(hueTolerance, hueTolerance + hueFeather, hueDist);

    /*
     * Saturation similarity:
     * 目标色越饱和，越要求候选像素的饱和度接近。
     * 这可以压掉泥土、木头、草地这类 hue 可能接近但颜色很脏/偏灰的像素。
     */
    float satDiff = abs(hsv.y - targetHsv.y);
    float satTolerance = mix(0.30, 0.16, targetHsv.y);
    float satKeep = 1.0 - smoothstep(satTolerance, satTolerance + 0.10, satDiff);

    /*
     * Saturation floor:
     * 如果目标色本身比较饱和，候选像素饱和度太低就不应通过。
     * 如果目标色接近灰色，则这个门槛自然很低。
     */
    float minSat = targetHsv.y * 0.48;
    float satFloorKeep = smoothstep(minSat * 0.65, minSat, hsv.y);

    /*
     * Value similarity:
     * 防止特别亮的岩浆、火焰、金色高光，或特别暗的脏色，
     * 因 hue 接近目标色而被错误保留。
     */
    float valueDiff = abs(hsv.z - targetHsv.z);
    float valueTolerance = 0.34;
    float valueKeep = 1.0 - smoothstep(valueTolerance, valueTolerance + 0.14, valueDiff);

    /*
     * Visibility gate:
     * 极暗像素不要强行当作目标色，否则阴影处会出现异常色块。
     */
    float visibleGate = smoothstep(0.025, 0.12, hsv.z);

    float keep = hueKeep * satKeep * satFloorKeep * valueKeep * visibleGate;
    keep = clamp(keep, 0.0, 1.0);

    /*
     * 非目标色处理：
     * 不混入原 rgb，否则草、史莱姆、天空、岩浆会残留原色。
     * 用 pow 提升暗部可读性，保留轮廓而不是变成纯黑。
     */
    float luminance = dot(rgb, vec3(0.299, 0.587, 0.114));
    float readableLum = pow(luminance, 0.78);
    float floorLum = 0.075;
    vec3 muted = vec3(max(readableLum * GrayStrength, floorLum));

    /*
     * 目标色处理：
     * 保留原色，并轻微染向目标色，使目标色系更突出。
     */
    vec3 vivid = clamp(rgb * VividStrength + TargetColor * 0.10, 0.0, 1.0);

    vec3 filtered = mix(muted, vivid, keep);



    vec3 finalColor = mix(rgb, filtered, strength);

    fragColor = vec4(finalColor, color.a);
}