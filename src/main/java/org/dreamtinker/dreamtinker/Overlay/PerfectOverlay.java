package org.dreamtinker.dreamtinker.Overlay;


import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public enum PerfectOverlay implements IGuiOverlay {
    INSTANCE;

    // 运行时状态
    private ResourceLocation icon;
    private long startGameTime;
    private int durationTicks;
    private boolean active;

    public void trigger(ResourceLocation icon, int durationTicks) {
        Minecraft mc = Minecraft.getInstance();
        this.icon = icon;
        this.startGameTime = mc.level != null ? mc.level.getGameTime() : 0L;
        this.durationTicks = Math.max(10, durationTicks); // 至少撑一会
        this.active = true;
    }

    @Override
    public void render(ForgeGui gui, GuiGraphics g, float partialTick, int w, int h) {
        if (!active)
            return;
        Minecraft mc = gui.getMinecraft();
        if (mc.level == null){
            active = false;
            return;
        }

        long now = mc.level.getGameTime();
        float t = ((now - startGameTime) + partialTick) / (float) durationTicks;
        if (t >= 1.0f){
            active = false;
            return;
        }

        // 淡入/淡出曲线：前30%淡入，中间稳定，后30%淡出
        float alpha;
        if (t < 0.3f)
            alpha = t / 0.3f;           // 0→1
        else if (t > 0.7f)
            alpha = (1f - t) / 0.3f; // 1→0
        else
            alpha = 1.0f;

        // 居中绘制
        int size = Math.round(Math.min(w, h) * 0.35f); // 占屏 35%
        int x = (w - size) / 2;
        int y = (h - size) / 2;

        RenderSystem.enableBlend();
        g.setColor(1f, 1f, 1f, Math.max(0f, Math.min(alpha, 1f)));
        g.blit(icon, x, y, 0, 0, size, size, size, size); // 你的贴图需是正方形
        g.setColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
    }
}

