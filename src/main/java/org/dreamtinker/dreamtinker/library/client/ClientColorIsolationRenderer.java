package org.dreamtinker.dreamtinker.library.client;


import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import org.dreamtinker.dreamtinker.Dreamtinker;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

public final class ClientColorIsolationRenderer {
    private static final ResourceLocation SHADER = Dreamtinker.getLocation("shaders/post/color_mask.json");
    private static PostChain chain;
    private static int lastWidth = -1;
    private static int lastHeight = -1;
    private static Field POST_CHAIN_PASSES;
    private static Field POST_PASS_EFFECT;

    private ClientColorIsolationRenderer() {}

    public static void render(float partialTick) {
        Minecraft mc = Minecraft.getInstance();

        if (!ClientMask.enabled || (ClientMask.mode != ColorMaskMode.COLOR_ISOLATION && ClientMask.mode != ColorMaskMode.ATMOSPHERE))
            return;

        if (mc.level == null || mc.player == null || mc.player.isDeadOrDying()){
            ClientMask.clearNow();
            return;
        }

        float strength = ClientMask.alphaFactor();
        if (strength <= 0F)
            return;

        ensureChain();

        if (chain == null)
            return;
        resizeIfNeeded();

        updateUniforms(strength);

        chain.process(partialTick);
        mc.getMainRenderTarget().bindWrite(false);
    }

    public static void close() {
        if (chain != null){
            chain.close();
            chain = null;
        }
        lastWidth = -1;
        lastHeight = -1;
    }

    private static void resizeIfNeeded() {
        Minecraft mc = Minecraft.getInstance();
        int width = mc.getWindow().getWidth();
        int height = mc.getWindow().getHeight();

        if (width != lastWidth || height != lastHeight){
            lastWidth = width;
            lastHeight = height;
            if (chain != null)
                chain.resize(width, height);
        }
    }

    public static void resize(int width, int height) {
        if (chain != null)
            chain.resize(width, height);
    }

    private static void ensureChain() {
        if (chain != null)
            return;

        Minecraft mc = Minecraft.getInstance();

        try {
            chain = new PostChain(mc.getTextureManager(), mc.getResourceManager(), mc.getMainRenderTarget(), SHADER);
            lastWidth = mc.getWindow().getWidth();
            lastHeight = mc.getWindow().getHeight();
            chain.resize(mc.getWindow().getWidth(), mc.getWindow().getHeight());
        }
        catch (IOException e) {
            Dreamtinker.LOGGER.error("Failed to load color isolation shader", e);
            ClientMask.clearNow();
            chain = null;
        }
    }

    private static void updateUniforms(float strength) {
        if (chain == null)
            return;

        for (PostPass pass : getPasses(chain)) {
            EffectInstance effect = pass.getEffect();

            set(effect, "TargetColor", ClientMask.targetR(), ClientMask.targetG(), ClientMask.targetB());
            set(effect, "Range", ClientMask.range01());
            set(effect, "GrayStrength", ClientMask.grayStrength);
            set(effect, "VividStrength", ClientMask.vividStrength);
            set(effect, "EffectStrength", strength * ClientMask.argbAlphaFactor());
            set(effect, "Mode", ClientMask.mode.ordinal());
        }
    }

    private static void set(EffectInstance effect, String name, int value) {
        Uniform uniform = effect.getUniform(name);
        if (uniform != null)
            uniform.set(value);
    }

    private static void set(EffectInstance effect, String name, float value) {
        Uniform uniform = effect.getUniform(name);
        if (uniform != null)
            uniform.set(value);
    }

    private static void set(EffectInstance effect, String name, float x, float y, float z) {
        Uniform uniform = effect.getUniform(name);
        if (uniform != null)
            uniform.set(x, y, z);
    }

    @SuppressWarnings("unchecked")
    private static List<PostPass> getPasses(PostChain chain) {
        try {
            if (POST_CHAIN_PASSES == null){
                POST_CHAIN_PASSES = findField(PostChain.class, "passes", "f_110009_");
                POST_CHAIN_PASSES.setAccessible(true);
            }
            return (List<PostPass>) POST_CHAIN_PASSES.get(chain);
        }
        catch (ReflectiveOperationException e) {
            Dreamtinker.LOGGER.error("Failed to access PostChain passes", e);
            return java.util.Collections.emptyList();
        }
    }

    private static Field findField(Class<?> owner, String... names) throws NoSuchFieldException {
        for (String name : names) {
            try {
                return owner.getDeclaredField(name);
            }
            catch (NoSuchFieldException ignored) {}
        }
        throw new NoSuchFieldException("Cannot find field in " + owner.getName() + ": " + java.util.Arrays.toString(names));
    }
}
