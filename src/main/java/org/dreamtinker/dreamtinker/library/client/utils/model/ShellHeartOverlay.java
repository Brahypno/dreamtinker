package org.dreamtinker.dreamtinker.library.client.utils.model;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.Entity.capabilities.ShellHeartProvider;
import org.dreamtinker.dreamtinker.common.capabilities.IShellHeart;

import java.util.Optional;

import static org.dreamtinker.dreamtinker.config.DreamtinkerClientConfig.*;

public final class ShellHeartOverlay implements IGuiOverlay {
    public static final ShellHeartOverlay INSTANCE = new ShellHeartOverlay();

    private static final ResourceLocation VANILLA_ICONS =
            new ResourceLocation("minecraft", "textures/gui/icons.png");

    // 你的填充资源：只需要 full / half 两个 9x9 图块
    private static final ResourceLocation SHELL_HEART_FILL =
            new ResourceLocation(Dreamtinker.MODID, "textures/gui/shell_heart_fill.png");

    private static final int HEART_W = 9;
    private static final int HEART_H = 9;

    // 原版 icons.png 是 256x256
    private static final int VANILLA_SHEET_W = 256;
    private static final int VANILLA_SHEET_H = 256;

    // 原版普通心框：x = 16
    // 原版闪烁心框：x = 25
    private static final int VANILLA_U_CONTAINER = 16;
    private static final int VANILLA_U_CONTAINER_BLINK = 25;

    // 普通模式 y = 0，hardcore 模式 y = 45
    private static final int VANILLA_V_NORMAL = 0;
    private static final int VANILLA_V_HARDCORE = 45;

    // 你的 fill sheet：建议 20x9
    // [1..9] fulfill
    // [10..19] half fill
    private static final int U_FILL_FULL = 1;
    private static final int U_FILL_HALF = 10;

    private static final int FILL_SHEET_W = 20;
    private static final int FILL_SHEET_H = 9;

    private static final int DEFAULT_HEART_COLOUR = 0xFFC4B5D6;
    private static final int STACK_LOWER_Y_OFFSET = -3;
    private static final float STACK_LOWER_ALPHA = 0.75F;
    private float lastShellValue = 0F;
    private long shellBlinkUntilTick = 0L;
    private long shellJumpUntilTick = 0L;
    private int shellJumpIndex = -1;

    private static int heartSlots(float value) {
        return Mth.ceil(value / 2.0F);
    }

    private static int rowsForSlots(int slots) {
        if (slots <= 0){
            return 0;
        }
        return (slots + 9) / 10;
    }

    private static int extraHeartRowHeight(int vanillaRows) {
        return Mth.clamp(10 - (vanillaRows - 2), 3, 10);
    }

    private static int sanitizeHeartColour(int colour) {
        // 允许 0xRRGGBB
        if ((colour & 0xFF000000) == 0 && colour > 0){
            return 0xFF000000 | colour;
        }

        // 允许 0xAARRGGBB，但 alpha 不能是 0
        int alpha = (colour >>> 24) & 0xFF;
        if (alpha > 0){
            return colour;
        }

        return DEFAULT_HEART_COLOUR;
    }

    private static int fillUForSlot(int layerHalfHearts, int slot) {
        int remainingHalf = layerHalfHearts - slot * 2;
        if (remainingHalf >= 2)
            return U_FILL_FULL;
        if (remainingHalf == 1)
            return U_FILL_HALF;
        return -1;
    }

    private static void drawVanillaHeartContainer(
            GuiGraphics graphics, Player player,
            int x, int y, boolean highlight, float alpha) {
        int u = highlight ? VANILLA_U_CONTAINER_BLINK : VANILLA_U_CONTAINER;
        int v = player.level().getLevelData().isHardcore() ? VANILLA_V_HARDCORE : VANILLA_V_NORMAL;

        graphics.setColor(1F, 1F, 1F, alpha);
        graphics.blit(VANILLA_ICONS, x, y, u, v, HEART_W, HEART_H, VANILLA_SHEET_W, VANILLA_SHEET_H);
    }

    private static void drawShellHeartFill(GuiGraphics graphics, int x, int y, int u, float r, float g, float b, float a
    ) {
        graphics.setColor(r, g, b, a);

        graphics.blit(SHELL_HEART_FILL, x, y, u, 0, HEART_W, HEART_H, FILL_SHEET_W, FILL_SHEET_H);
    }

    private static void drawStackedShellHeart(
            GuiGraphics graphics, Player player, int x, int y,
            int lowerFillU, int upperFillU,
            float r, float g, float b, float a,
            boolean highlight) {
        if (lowerFillU >= 0){
            drawShellHeartSlot(graphics, player, x, y + STACK_LOWER_Y_OFFSET, lowerFillU,
                               r, g, b, a * STACK_LOWER_ALPHA, highlight, STACK_LOWER_ALPHA);
        }

        if (upperFillU >= 0){
            drawShellHeartSlot(graphics, player, x, y, upperFillU,
                               r, g, b, a, highlight);
        }

        graphics.setColor(1F, 1F, 1F, 1F);
    }

    private static void drawShellHeartSlot(
            GuiGraphics graphics, Player player, int x, int y,
            int fillU, float r, float g, float b, float a,
            boolean highlight) {
        drawShellHeartSlot(graphics, player, x, y, fillU, r, g, b, a, highlight, 1F);
    }

    private static void drawShellHeartSlot(
            GuiGraphics graphics, Player player, int x, int y,
            int fillU, float r, float g, float b, float a,
            boolean highlight, float frameAlpha) {
        drawVanillaHeartContainer(graphics, player, x, y, highlight, frameAlpha);

        if (fillU >= 0){
            drawShellHeartFill(graphics, x, y, fillU, r, g, b, a);
        }

        graphics.setColor(1F, 1F, 1F, 1F);
    }

    @Override
    public void render(ForgeGui gui, GuiGraphics graphics, float partialTicks, int width, int height) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (!gui.shouldDrawSurvivalElements() || player == null || player.isSpectator() || !SHELL_HEART_DISPLAYED.get()){
            return;
        }

        Optional<IShellHeart> optional = ShellHeartProvider.getShellHeart(player);
        if (optional.isEmpty()){
            return;
        }

        IShellHeart shellHeart = optional.get();
        float shellValue = shellHeart.get();

        if (shellValue <= 0F){
            return;
        }

        int ticks = gui.getGuiTicks();

        boolean shellDecreased = shellValue < this.lastShellValue;
        boolean shellIncreased = shellValue > this.lastShellValue;

        if (shellDecreased){
            this.shellBlinkUntilTick = ticks + 20L;
        }else if (shellIncreased){
            this.shellBlinkUntilTick = ticks + 10L;
            this.shellJumpUntilTick = ticks + 10L;
        }

        boolean highlight = this.shellBlinkUntilTick > ticks
                            && (this.shellBlinkUntilTick - ticks) / 3L % 2L == 1L;

        float healthMax = (float) player.getAttributeValue(Attributes.MAX_HEALTH);
        float absorption = player.getAbsorptionAmount();

        int vanillaSlots = heartSlots(healthMax) + heartSlots(absorption);
        int vanillaRows = rowsForSlots(vanillaSlots);
        int reservedRows = SHELL_HEART_RESERVED_ROWS.get();

        int extraRowHeight = extraHeartRowHeight(vanillaRows);

        int left = width / 2 - 91;

        // ===== 壳心只显示一行，但用上下叠层压缩 =====
        int halfHearts = Mth.ceil(shellValue);
        int realShellSlots = heartSlots(shellValue);
        if (realShellSlots <= 0)
            return;

        int maxVisibleSlots = 10;
        boolean stackedOverflow = realShellSlots > maxVisibleSlots;
        int renderedShellSlots = stackedOverflow ? maxVisibleSlots : Math.min(realShellSlots, maxVisibleSlots);

        // 不再用 heartY / startIndex 反推位置。
        // 此处 gui.leftHeight 已经包含 vanilla 红心/黄心占用高度，所以直接从它上方开始画。
        int shellLineHeight = Math.max(10, extraRowHeight);
        int shellBaseY = height - gui.leftHeight - reservedRows * shellLineHeight;

        // 关键：叠层上移和跳动上移都会侵入护甲区域，所以必须额外预留。
        int stackTopOverhang = stackedOverflow ? Math.max(0, -STACK_LOWER_Y_OFFSET) : 0;
        int jumpTopOverhang = this.shellJumpUntilTick > ticks ? 2 : 0;

        gui.leftHeight += reservedRows * shellLineHeight + shellLineHeight + stackTopOverhang + jumpTopOverhang;

        if (shellIncreased){
            int jumpSlots = stackedOverflow ? Math.min(heartSlots(((halfHearts - 1) % (maxVisibleSlots * 2)) + 1), maxVisibleSlots)
                                            : renderedShellSlots;
            this.shellJumpIndex = Math.max(0, jumpSlots - 1);
        }

        int colour = sanitizeHeartColour(shellHeart.getHeartColour());

        float a = ((colour >>> 24) & 0xFF) / 255.0F;
        float r = ((colour >>> 16) & 0xFF) / 255.0F;
        float g = ((colour >>> 8) & 0xFF) / 255.0F;
        float b = (colour & 0xFF) / 255.0F;

        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(0, 0, 0.01F);

        RenderSystem.enableBlend();

        int currentLayerHalfHearts = stackedOverflow ? ((halfHearts - 1) % (maxVisibleSlots * 2)) + 1 : halfHearts;
        int lowerLayerHalfHearts = maxVisibleSlots * 2;

        for (int i = 0; i < renderedShellSlots; i++) {
            int x = left + i * 8 + SHELL_HEART_X_OFFSET.get();
            int y = shellBaseY + SHELL_HEART_Y_OFFSET.get();

            if (this.shellJumpUntilTick > ticks && i == this.shellJumpIndex){
                y -= 2;
            }

            float drawAlpha = highlight ? a * 0.45F : a;

            if (stackedOverflow){
                int lowerFillU = fillUForSlot(lowerLayerHalfHearts, i);
                int upperFillU = fillUForSlot(currentLayerHalfHearts, i);

                drawStackedShellHeart(graphics, player, x, y, lowerFillU, upperFillU,
                                      r, g, b, drawAlpha, highlight);
            }else {
                int fillU = fillUForSlot(halfHearts, i);

                drawShellHeartSlot(graphics, player, x, y, fillU,
                                   r, g, b, drawAlpha, highlight);
            }
        }

        graphics.setColor(1F, 1F, 1F, 1F);
        RenderSystem.disableBlend();

        pose.popPose();

        this.lastShellValue = shellValue;
    }
}