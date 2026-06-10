package org.brahypno.dreamtinker.common.event.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.brahypno.dreamtinker.Dreamtinker;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Dreamtinker.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WallVisionRenderer {
    private static final int CACHE_REFRESH_TICKS = 5;
    private static final List<HighlightedBlock> HIGHLIGHT_CACHE = new ArrayList<>();
    private static Level cachedLevel;
    private static BlockPos cachedCenter;
    private static TagKey<Block> cachedTag;
    private static int cachedRadius = -1;
    private static long nextCacheRefreshTick = -1L;

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS)
            return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        Level level = mc.level;
        if (player == null || level == null)
            return;

        if (!ClientWallVisionState.isEnabled())
            return;

        TagKey<Block> tag = ClientWallVisionState.getHighlightTag();
        if (tag == null)
            return;

        int radius = ClientWallVisionState.Radius();
        if (radius <= 0)
            return;

        PoseStack poseStack = event.getPoseStack();
        Camera camera = event.getCamera();
        Vec3 camPos = camera.getPosition();

        double camX = camPos.x();
        double camY = camPos.y();
        double camZ = camPos.z();

        BlockPos center = player.blockPosition();
        refreshHighlightCache(level, center, tag, radius);

        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
        VertexConsumer builder = buffer.getBuffer(ModRenderTypes.WALL_VISION_LINES);

        poseStack.pushPose();
        poseStack.translate(-camX, -camY, -camZ);

        for (HighlightedBlock block : HIGHLIGHT_CACHE) {
            BlockPos pos = block.pos();
            block.shape().forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
                LevelRenderer.renderLineBox(
                        poseStack,
                        builder,
                        pos.getX() + minX, pos.getY() + minY, pos.getZ() + minZ,
                        pos.getX() + maxX, pos.getY() + maxY, pos.getZ() + maxZ,
                        0.1F, 0.8F, 1.0F, 1.0F
                );
            });
        }

        poseStack.popPose();
        buffer.endBatch(ModRenderTypes.WALL_VISION_LINES);
    }

    private static void refreshHighlightCache(Level level, BlockPos center, TagKey<Block> tag, int radius) {
        long gameTime = level.getGameTime();
        if (level == cachedLevel && radius == cachedRadius && tag == cachedTag && center.equals(cachedCenter) && gameTime < nextCacheRefreshTick){
            return;
        }

        HIGHLIGHT_CACHE.clear();
        cachedLevel = level;
        cachedCenter = center.immutable();
        cachedTag = tag;
        cachedRadius = radius;
        nextCacheRefreshTick = gameTime + CACHE_REFRESH_TICKS;

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    pos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    BlockState state = level.getBlockState(pos);
                    if (!state.is(tag))
                        continue;

                    VoxelShape shape = state.getShape(level, pos, CollisionContext.empty());
                    if (!shape.isEmpty())
                        HIGHLIGHT_CACHE.add(new HighlightedBlock(pos.immutable(), shape));
                }
            }
        }
    }

    private record HighlightedBlock(BlockPos pos, VoxelShape shape) {}

    @OnlyIn(Dist.CLIENT)
    public static class ClientWallVisionState {
        private static boolean enabled = false;
        private static int Radius = 0;
        private static TagKey<Block> highlightTag = null;

        public static void setEnabled(boolean value, int radius) {
            enabled = value;
            Radius = radius;
        }

        public static boolean isEnabled() {return enabled;}

        public static int Radius() {return Radius;}

        public static void setHighlightTag(ResourceLocation id) {
            highlightTag = TagKey.create(Registries.BLOCK, id);
        }

        public static TagKey<Block> getHighlightTag() {return highlightTag;}
    }

    public static class ModRenderTypes extends RenderType {
        private ModRenderTypes(
                String name, VertexFormat format, VertexFormat.Mode mode,
                int bufferSize, boolean affectsCrumbling, boolean sortOnUpload,
                Runnable setupState, Runnable clearState) {
            super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
        }

        public static final RenderType WALL_VISION_LINES = create(
                "dreamtinker:wall_vision_lines",
                DefaultVertexFormat.POSITION_COLOR,
                VertexFormat.Mode.LINES,
                256,
                false,
                false,
                RenderType.CompositeState.builder()
                                         .setShaderState(RENDERTYPE_LINES_SHADER)
                                         .setLineState(new LineStateShard(java.util.OptionalDouble.of(2.0)))
                                         .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                                         .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                                         .setDepthTestState(NO_DEPTH_TEST)
                                         .setCullState(NO_CULL)
                                         .setWriteMaskState(COLOR_WRITE)
                                         .createCompositeState(false)
        );
    }
}
