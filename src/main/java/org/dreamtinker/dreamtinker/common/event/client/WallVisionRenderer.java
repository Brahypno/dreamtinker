package org.dreamtinker.dreamtinker.common.event.client;

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
import org.dreamtinker.dreamtinker.Dreamtinker;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Dreamtinker.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WallVisionRenderer {

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        // 只在特定阶段渲染，避免和别的通道打架
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
        // 渲染范围（自己调）
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

        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
        VertexConsumer builder = buffer.getBuffer(ModRenderTypes.WALL_VISION_LINES);

        poseStack.pushPose();
        // 把世界坐标移到相机原点，避免抖动
        poseStack.translate(-camX, -camY, -camZ);

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    pos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    BlockState state = level.getBlockState(pos);
                    if (!state.is(tag))
                        continue;

                    VoxelShape shape = state.getShape(level, pos, CollisionContext.empty());
                    if (shape.isEmpty())
                        continue;

                    // 通过墙也可见：lines 本身不做深度测试（RenderType.lines 默认是深度测试的，
                    // 你可以自定义一个 render type 或者直接开启线模式 + 禁用深度，简单做法如下）
                    shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
                        LevelRenderer.renderLineBox(
                                poseStack,
                                builder,
                                pos.getX() + minX, pos.getY() + minY, pos.getZ() + minZ,
                                pos.getX() + maxX, pos.getY() + maxY, pos.getZ() + maxZ,
                                0.1F, 0.8F, 1.0F, 1.0F  // 线条颜色 RGBA：青蓝发光感
                        );
                    });
                }
            }
        }

        poseStack.popPose();
        buffer.endBatch(RenderType.lines());
    }

    @OnlyIn(Dist.CLIENT)
    public static class ClientWallVisionState {
        private static boolean enabled = false;
        private static int Radius = 0;


        // 当前生效的 tag（方块 tag 为例）
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
        // 只是为了调用父类私有构造，直接照抄就行
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
                false,  // affectsCrumbling
                false,  // sortOnUpload
                RenderType.CompositeState.builder()
                                         .setShaderState(RENDERTYPE_LINES_SHADER)
                                         .setLineState(new LineStateShard(java.util.OptionalDouble.of(2.0))) // 线条粗一点
                                         .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                                         .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                                         .setDepthTestState(NO_DEPTH_TEST) // ★ 关键：不做深度测试
                                         .setCullState(NO_CULL)
                                         .setWriteMaskState(COLOR_WRITE)
                                         .createCompositeState(false)
        );
    }


}

