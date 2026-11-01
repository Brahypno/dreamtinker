package org.brahypno.dreamtinker.library.client.sigil;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import org.brahypno.dreamtinker.library.client.DTRenderTypes;
import org.joml.Matrix4f;
import java.util.List;

public final class NarcissusSigilRenderer {
    public record Palette(int glow, int core) {}
    public static final Palette FALLBACK_PALETTE = new Palette(0x97E2FF, 0xE9FFFF);
    private NarcissusSigilRenderer() {}

    public static void render(PoseStack pose, MultiBufferSource buffer, List<NarcissusSigilGeometry.Path> paths, Palette palette, float progress, float alpha, float flash) {
        drawPaths(pose.last().pose(), buffer.getBuffer(DTRenderTypes.NARCISSUS_SIGIL_GLOW), paths, palette.glow, progress, alpha * 0.13f, 0.012f, flash, false);
        drawPaths(pose.last().pose(), buffer.getBuffer(DTRenderTypes.NARCISSUS_SIGIL_CORE), paths, palette.core, progress, alpha * 0.88f, 0.003f, flash, true);
    }

    public static void afterglow(PoseStack pose, MultiBufferSource buffer, Palette palette, float progress, float alpha) {
        drawPaths(pose.last().pose(), buffer.getBuffer(DTRenderTypes.NARCISSUS_SIGIL_GLOW), NarcissusSigilGeometry.AFTERGLOW,
                  palette.glow, progress, alpha * 0.16f, 0.01f, 0, false);
    }

    private static void drawPaths(Matrix4f matrix, VertexConsumer consumer, List<NarcissusSigilGeometry.Path> paths, int color, float progress,
                                  float alpha, float width, float flash, boolean thickness) {
        int pathIndex = 0;
        for (NarcissusSigilGeometry.Path path : paths) {
            float visible = path.visibleLength(progress);
            float[] points = path.points();
            float[] distances = path.distances();
            float[] normals = path.normals();
            float brightness = path.core() ? 1 + flash * 0.7f : 1;
            // A single perpendicular ribbon gives the outer frame and core depth, leaving internal threads light.
            float depth = thickness && (pathIndex < 2 || path.core()) ? 0.0045f * brightness : 0;
            pathIndex++;
            int colorAlpha = (int) (255 * Mth.clamp(alpha * brightness, 0, 1));
            for (int i = 1; i < distances.length && visible > distances[i - 1]; i++) {
                float fraction = Math.min(1, (visible - distances[i - 1]) / (distances[i] - distances[i - 1]));
                float x = points[i * 2 - 2];
                float y = points[i * 2 - 1];
                float endX = Mth.lerp(fraction, x, points[i * 2]);
                float endY = Mth.lerp(fraction, y, points[i * 2 + 1]);
                float halfWidth = width * brightness;
                float nx = normals[i * 2 - 2] * halfWidth;
                float ny = normals[i * 2 - 1] * halfWidth;
                float endNX = Mth.lerp(fraction, normals[i * 2 - 2], normals[i * 2]) * halfWidth;
                float endNY = Mth.lerp(fraction, normals[i * 2 - 1], normals[i * 2 + 1]) * halfWidth;
                vertex(consumer, matrix, x + nx, y + ny, 0, colorAlpha, color);
                vertex(consumer, matrix, x - nx, y - ny, 0, colorAlpha, color);
                vertex(consumer, matrix, endX - endNX, endY - endNY, 0, colorAlpha, color);
                vertex(consumer, matrix, endX + endNX, endY + endNY, 0, colorAlpha, color);
                if (depth > 0) {
                    int sideAlpha = (int) (colorAlpha * 0.45f);
                    vertex(consumer, matrix, x, y, depth, sideAlpha, color);
                    vertex(consumer, matrix, x, y, -depth, sideAlpha, color);
                    vertex(consumer, matrix, endX, endY, -depth, sideAlpha, color);
                    vertex(consumer, matrix, endX, endY, depth, sideAlpha, color);
                }
            }
        }
    }

    /** A short, unlit ribbon joins the visual muzzle to the authoritative projectile path. */
    public static void beam(PoseStack pose, MultiBufferSource buffer, Palette palette, float x, float y, float z, float alpha) {
        line(buffer.getBuffer(DTRenderTypes.NARCISSUS_SIGIL_GLOW), pose.last().pose(), 0, 0, 0, x, y, z, 0.016f, (int) (alpha * 70), palette.glow);
        line(buffer.getBuffer(DTRenderTypes.NARCISSUS_SIGIL_CORE), pose.last().pose(), 0, 0, 0, x, y, z, 0.003f, (int) (alpha * 210), palette.core);
    }

    private static void line(VertexConsumer consumer, Matrix4f matrix, float x, float y, float z, float endX, float endY, float endZ,
                             float width, int alpha, int color) {
        float dx = endX - x;
        float dy = endY - y;
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        float nx = length < 0.00001f ? width : -dy * width / length;
        float ny = length < 0.00001f ? 0 : dx * width / length;
        vertex(consumer, matrix, x + nx, y + ny, z, alpha, color);
        vertex(consumer, matrix, x - nx, y - ny, z, alpha, color);
        vertex(consumer, matrix, endX - nx, endY - ny, endZ, alpha, color);
        vertex(consumer, matrix, endX + nx, endY + ny, endZ, alpha, color);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix, float x, float y, float z, int alpha, int color) {
        consumer.vertex(matrix, x, y, z).color(color >> 16 & 255, color >> 8 & 255, color & 255, alpha).endVertex();
    }
}
