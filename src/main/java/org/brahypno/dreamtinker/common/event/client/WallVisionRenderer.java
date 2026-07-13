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
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.brahypno.dreamtinker.Dreamtinker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mod.EventBusSubscriber(
        value = Dist.CLIENT,
        modid = Dreamtinker.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class WallVisionRenderer {
    /**
     * 一轮扫描完成后，至少等待多少 tick 才进行下一轮刷新。
     * <p>
     * 玩家跨方块移动时不受该值限制，会尽快开始新扫描。
     */
    private static final int CACHE_REFRESH_TICKS = 5;

    /**
     * 每个客户端 tick 最多检查的方块数。
     * <p>
     * 若仍有客户端 tick 卡顿，可降至 4096；
     * 若机器性能较好、希望刷新更快，可提高至 16384。
     */
    private static final int SCAN_BUDGET_PER_TICK = 8192;

    /**
     * 服务端同步过来的半径最终会被钳制到该值。
     * <p>
     * 半径 32 对应 65³ = 274625 个候选位置。
     */
    private static final int MAX_RADIUS = 32;

    /**
     * 最多保存和渲染的匹配方块数。
     * <p>
     * 16384 个线框已经非常多，继续提高通常只会让 GPU 和内存压力暴涨。
     */
    private static final int MAX_HIGHLIGHTS = 16_384;

    private static final CollisionContext EMPTY_CONTEXT = CollisionContext.empty();

    /**
     * 不再使用 final ArrayList。
     * <p>
     * 完成扫描时直接交换 List 引用，旧的大数组便可以被 GC，
     * 而不是 clear() 后永久保留历史最大容量。
     */
    private static List<HighlightedBlock> highlightCache = List.of();

    private static Level cachedLevel;
    private static BlockPos cachedCenter;
    private static TagKey<Block> cachedTag;
    private static int cachedRadius = -1;
    private static long nextRefreshTick = -1L;

    /**
     * 当前正在分批执行的扫描。
     */
    private static ScanState activeScan;

    /**
     * 扫描放在客户端 tick，而不是 RenderLevelStageEvent。
     * <p>
     * 这样高刷新率不会导致一秒执行几十至几百次扫描预算。
     */
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END){
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        Level level = mc.level;

        if (player == null || level == null){
            resetClientState();
            return;
        }

        if (!ClientWallVisionState.isEnabled()){
            invalidateRenderCache();
            return;
        }

        TagKey<Block> tag = ClientWallVisionState.getHighlightTag();
        int radius = ClientWallVisionState.Radius();

        if (tag == null || radius <= 0){
            invalidateRenderCache();
            return;
        }

        radius = Mth.clamp(radius, 1, MAX_RADIUS);

        updateHighlightCache(
                level,
                player.blockPosition(),
                tag,
                radius
        );
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS){
            return;
        }

        if (!ClientWallVisionState.isEnabled()){
            return;
        }

        List<HighlightedBlock> snapshot = highlightCache;
        if (snapshot.isEmpty()){
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        Level level = mc.level;

        if (player == null || level == null || level != cachedLevel){
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        Camera camera = event.getCamera();
        Vec3 camPos = camera.getPosition();

        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
        VertexConsumer builder = buffer.getBuffer(ModRenderTypes.WALL_VISION_LINES);

        poseStack.pushPose();
        poseStack.translate(
                -camPos.x(),
                -camPos.y(),
                -camPos.z()
        );

        for (HighlightedBlock block : snapshot) {
            renderHighlightedBlock(
                    poseStack,
                    builder,
                    block
            );
        }

        poseStack.popPose();
        buffer.endBatch(ModRenderTypes.WALL_VISION_LINES);
    }

    @SubscribeEvent
    public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        resetClientState();
    }

    private static void updateHighlightCache(
            Level level,
            BlockPos center,
            TagKey<Block> tag,
            int radius
    ) {
        long gameTime = level.getGameTime();

        boolean contextChanged = level != cachedLevel
                                 || cachedRadius != radius
                                 || !Objects.equals(cachedTag, tag);

        if (contextChanged){
            beginNewContext(
                    level,
                    tag,
                    radius
            );
        }

        /*
         * 有扫描正在执行时，不因为玩家每移动一格就立即丢弃重来。
         *
         * 否则大半径情况下，玩家持续走动会导致扫描永远无法完成。
         * 当前扫描完成后，会自动发现中心变化并开启下一轮。
         */
        if (activeScan == null && shouldBeginScan(center, gameTime)){
            activeScan = new ScanState(
                    center.immutable(),
                    tag,
                    radius
            );
        }

        if (activeScan == null){
            return;
        }

        activeScan.process(
                level,
                SCAN_BUDGET_PER_TICK
        );

        /*
         * 初次扫描没有旧缓存时，允许结果逐步显示。
         *
         * 定期刷新已有缓存时，则继续显示上一轮完整结果，
         * 避免画面每次刷新都从空白开始逐渐出现。
         */
        if (highlightCache.isEmpty()){
            highlightCache = activeScan.results();
        }

        if (!activeScan.isFinished()){
            return;
        }

        highlightCache = activeScan.results();
        cachedCenter = activeScan.center();
        nextRefreshTick = gameTime + CACHE_REFRESH_TICKS;
        activeScan = null;
    }

    private static void beginNewContext(
            Level level,
            TagKey<Block> tag,
            int radius
    ) {
        /*
         * 直接替换引用，使旧缓存的底层数组、方块记录和 VoxelShape
         * 在没有其他引用时可以正常回收。
         */
        highlightCache = List.of();
        activeScan = null;

        cachedLevel = level;
        cachedCenter = null;
        cachedTag = tag;
        cachedRadius = radius;
        nextRefreshTick = -1L;
    }

    private static boolean shouldBeginScan(
            BlockPos center,
            long gameTime
    ) {
        if (cachedCenter == null){
            return true;
        }

        if (!cachedCenter.equals(center)){
            return true;
        }

        return gameTime >= nextRefreshTick;
    }

    private static void renderHighlightedBlock(
            PoseStack poseStack,
            VertexConsumer builder,
            HighlightedBlock block
    ) {
        block.shape().forAllBoxes(
                (minX, minY, minZ, maxX, maxY, maxZ) ->
                        LevelRenderer.renderLineBox(
                                poseStack,
                                builder,
                                block.x() + minX,
                                block.y() + minY,
                                block.z() + minZ,
                                block.x() + maxX,
                                block.y() + maxY,
                                block.z() + maxZ,
                                0.1F,
                                0.8F,
                                1.0F,
                                1.0F
                        )
        );
    }

    /**
     * 只清除渲染缓存，不改变服务端同步过来的启用状态。
     */
    private static void invalidateRenderCache() {
        highlightCache = List.of();
        activeScan = null;

        cachedLevel = null;
        cachedCenter = null;
        cachedTag = null;
        cachedRadius = -1;
        nextRefreshTick = -1L;
    }

    /**
     * 用于退出世界时彻底重置客户端状态。
     */
    public static void resetClientState() {
        invalidateRenderCache();
        ClientWallVisionState.reset();
    }

    /**
     * 不再保存 BlockPos 对象。
     * <p>
     * 三个 int 可以直接供渲染使用，避免每个高亮结果额外持有一个
     * BlockPos 实例。
     */
    private record HighlightedBlock(
            int x,
            int y,
            int z,
            VoxelShape shape
    ) {}

    /**
     * 单轮渐进式扫描状态。
     * <p>
     * 不保存 Level 引用，避免 activeScan 自身额外持有旧世界。
     */
    private static final class ScanState {
        private final BlockPos center;
        private final TagKey<Block> tag;
        private final int radius;

        private final int side;
        private final int planeSize;
        private final int totalPositions;

        private final ArrayList<HighlightedBlock> results =
                new ArrayList<>(Math.min(1024, MAX_HIGHLIGHTS));

        private final BlockPos.MutableBlockPos cursor =
                new BlockPos.MutableBlockPos();

        private int nextIndex;

        private ScanState(
                BlockPos center,
                TagKey<Block> tag,
                int radius
        ) {
            this.center = center;
            this.tag = tag;
            this.radius = radius;

            this.side = radius * 2 + 1;
            this.planeSize = side * side;
            this.totalPositions = planeSize * side;
        }

        private void process(
                Level level,
                int budget
        ) {
            int remaining = Math.max(1, budget);

            while (remaining-- > 0
                   && nextIndex < totalPositions
                   && results.size() < MAX_HIGHLIGHTS) {
                processNextPosition(level);
            }

            /*
             * 匹配数量达到上限后立即结束扫描。
             *
             * 防止标签过宽时，仍继续无意义地遍历剩余几十万个方块。
             */
            if (results.size() >= MAX_HIGHLIGHTS){
                nextIndex = totalPositions;
            }
        }

        private void processNextPosition(Level level) {
            int index = nextIndex++;

            int dx = index / planeSize - radius;
            int remainder = index % planeSize;
            int dy = remainder / side - radius;
            int dz = remainder % side - radius;

            cursor.set(
                    center.getX() + dx,
                    center.getY() + dy,
                    center.getZ() + dz
            );

            /*
             * 不访问未加载区块，避免无意义查找或意外触发额外工作。
             */
            if (!level.hasChunkAt(cursor)){
                return;
            }

            BlockState state = level.getBlockState(cursor);
            if (!state.is(tag)){
                return;
            }

            VoxelShape shape = state.getShape(
                    level,
                    cursor,
                    EMPTY_CONTEXT
            );

            if (shape.isEmpty()){
                return;
            }

            results.add(
                    new HighlightedBlock(
                            cursor.getX(),
                            cursor.getY(),
                            cursor.getZ(),
                            shape
                    )
            );
        }

        private boolean isFinished() {
            return nextIndex >= totalPositions;
        }

        private BlockPos center() {
            return center;
        }

        private List<HighlightedBlock> results() {
            return results;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class ClientWallVisionState {
        private static boolean enabled;
        private static int radius;
        private static TagKey<Block> highlightTag;

        public static void setEnabled(
                boolean value,
                int requestedRadius
        ) {
            boolean newEnabled = value && requestedRadius > 0;
            int newRadius = newEnabled
                            ? Mth.clamp(requestedRadius, 1, MAX_RADIUS)
                            : 0;

            if (enabled == newEnabled && radius == newRadius){
                return;
            }

            enabled = newEnabled;
            radius = newRadius;

            /*
             * 关闭或改变半径时立即丢弃旧缓存，
             * 不再等到退出世界或下一次成功扫描。
             */
            invalidateRenderCache();
        }

        public static boolean isEnabled() {
            return enabled;
        }

        /**
         * 保留现有方法名，避免其他类调用 Radius() 时需要同步修改。
         */
        public static int Radius() {
            return radius;
        }

        public static void setHighlightTag(ResourceLocation id) {
            TagKey<Block> newTag = id == null
                                   ? null
                                   : TagKey.create(Registries.BLOCK, id);

            if (Objects.equals(highlightTag, newTag)){
                return;
            }

            highlightTag = newTag;
            invalidateRenderCache();
        }

        public static TagKey<Block> getHighlightTag() {
            return highlightTag;
        }

        private static void reset() {
            enabled = false;
            radius = 0;
            highlightTag = null;
        }
    }

    public static class ModRenderTypes extends RenderType {
        private ModRenderTypes(
                String name,
                VertexFormat format,
                VertexFormat.Mode mode,
                int bufferSize,
                boolean affectsCrumbling,
                boolean sortOnUpload,
                Runnable setupState,
                Runnable clearState
        ) {
            super(
                    name,
                    format,
                    mode,
                    bufferSize,
                    affectsCrumbling,
                    sortOnUpload,
                    setupState,
                    clearState
            );
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
                                         .setLineState(
                                                 new LineStateShard(
                                                         java.util.OptionalDouble.of(2.0D)
                                                 )
                                         )
                                         .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                                         .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                                         .setDepthTestState(NO_DEPTH_TEST)
                                         .setCullState(NO_CULL)
                                         .setWriteMaskState(COLOR_WRITE)
                                         .createCompositeState(false)
        );
    }
}