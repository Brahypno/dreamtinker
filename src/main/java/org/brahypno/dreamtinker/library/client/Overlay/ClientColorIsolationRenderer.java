package org.brahypno.dreamtinker.library.client.Overlay;

import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.brahypno.dreamtinker.Dreamtinker;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public final class ClientColorIsolationRenderer {
    private static final ResourceLocation SHADER =
            Dreamtinker.getLocation("shaders/post/color_mask.json");

    private static PostChain chain;
    private static int lastWidth = -1;
    private static int lastHeight = -1;
    private static Field postChainPasses;

    /**
     * 本次客户端连接中的永久熔断状态。失败后不再逐帧重试，避免日志刷屏和重复创建 PostChain。
     * 新连接会由 ClientMaskEvents 显式调用 resetFailureState()，允许再次尝试一次。
     */
    private static boolean permanentlyDisabled;
    private static boolean failureLogged;

    private ClientColorIsolationRenderer() {}

    public static void render(float partialTick) {
        if (!isPostEffectRequested()){
            return;
        }
        if (permanentlyDisabled){
            ClientMask.clearNow();
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null
            || minecraft.player == null
            || minecraft.player.isDeadOrDying()){
            ClientMask.clearNow();
            return;
        }

        float strength = ClientMask.alphaFactor();
        if (strength <= 0.0F){
            return;
        }

        try {
            ensureChain(minecraft);
            resizeIfNeeded(minecraft);
            updateUniforms(strength);
            chain.process(partialTick);
            minecraft.getMainRenderTarget().bindWrite(false);
        }
        catch (IOException | ReflectiveOperationException | RuntimeException exception) {
            disablePermanently("Color isolation post-processing failed and was disabled for this connection", exception);
        }
    }

    private static boolean isPostEffectRequested() {
        return ClientMask.enabled
               && (ClientMask.mode == ColorMaskMode.COLOR_ISOLATION
                   || ClientMask.mode == ColorMaskMode.ATMOSPHERE);
    }

    public static boolean isPermanentlyDisabled() {
        return permanentlyDisabled;
    }

    /**
     * 仅应在新的客户端连接开始时调用。资源/映射错误仍存在时，只会再次失败并记录一次。
     */
    public static void resetFailureState() {
        close();
        permanentlyDisabled = false;
        failureLogged = false;
        postChainPasses = null;
    }

    public static void close() {
        PostChain oldChain = chain;
        chain = null;
        lastWidth = -1;
        lastHeight = -1;

        if (oldChain == null){
            return;
        }
        try {
            oldChain.close();
        }
        catch (RuntimeException exception) {
            if (!permanentlyDisabled){
                Dreamtinker.LOGGER.warn("Failed to close color isolation shader cleanly", exception);
            }
        }
    }

    public static void resize(int width, int height) {
        if (chain == null || permanentlyDisabled){
            return;
        }
        try {
            chain.resize(width, height);
            lastWidth = width;
            lastHeight = height;
        }
        catch (RuntimeException exception) {
            disablePermanently("Color isolation shader resize failed and was disabled for this connection", exception);
        }
    }

    private static void ensureChain(Minecraft minecraft) throws IOException {
        if (chain != null){
            return;
        }

        PostChain newChain = new PostChain(
                minecraft.getTextureManager(),
                minecraft.getResourceManager(),
                minecraft.getMainRenderTarget(),
                SHADER
        );

        int width = minecraft.getWindow().getWidth();
        int height = minecraft.getWindow().getHeight();
        try {
            newChain.resize(width, height);
        }
        catch (RuntimeException exception) {
            newChain.close();
            throw exception;
        }

        chain = newChain;
        lastWidth = width;
        lastHeight = height;
    }

    private static void resizeIfNeeded(Minecraft minecraft) {
        int width = minecraft.getWindow().getWidth();
        int height = minecraft.getWindow().getHeight();
        if (width == lastWidth && height == lastHeight){
            return;
        }

        chain.resize(width, height);
        lastWidth = width;
        lastHeight = height;
    }

    private static void updateUniforms(float strength) throws ReflectiveOperationException {
        for (PostPass pass : getPasses(chain)) {
            EffectInstance effect = pass.getEffect();
            set(effect, "TargetColor", ClientMask.targetR(), ClientMask.targetG(), ClientMask.targetB());
            set(effect, "Range", ClientMask.range01());
            set(effect, "GrayStrength", ClientMask.grayStrength);
            set(effect, "VividStrength", ClientMask.vividStrength);
            set(effect, "EffectStrength", strength * ClientMask.argbAlphaFactor());
            set(effect, "Mode", ClientMask.mode.id());
        }
    }

    private static void set(EffectInstance effect, String name, int value) {
        Uniform uniform = effect.getUniform(name);
        if (uniform != null){
            uniform.set(value);
        }
    }

    private static void set(EffectInstance effect, String name, float value) {
        Uniform uniform = effect.getUniform(name);
        if (uniform != null){
            uniform.set(value);
        }
    }

    private static void set(EffectInstance effect, String name, float x, float y, float z) {
        Uniform uniform = effect.getUniform(name);
        if (uniform != null){
            uniform.set(x, y, z);
        }
    }

    @SuppressWarnings("unchecked")
    private static List<PostPass> getPasses(PostChain postChain) throws ReflectiveOperationException {
        if (postChainPasses == null){
            postChainPasses = findField(PostChain.class, "passes", "f_110009_");
            postChainPasses.setAccessible(true);
        }
        return (List<PostPass>) postChainPasses.get(postChain);
    }

    private static Field findField(Class<?> owner, String... names) throws NoSuchFieldException {
        for (String name : names) {
            try {
                return owner.getDeclaredField(name);
            }
            catch (NoSuchFieldException ignored) {
                // 尝试下一个开发/生产映射名称。
            }
        }
        throw new NoSuchFieldException(
                "Cannot find field in " + owner.getName() + ": " + Arrays.toString(names)
        );
    }

    private static void disablePermanently(String message, Exception exception) {
        boolean firstFailure = !permanentlyDisabled;
        permanentlyDisabled = true;
        close();
        ClientMask.clearNow();

        if (firstFailure && !failureLogged){
            failureLogged = true;
            Dreamtinker.LOGGER.error(message, exception);
        }
    }
}
