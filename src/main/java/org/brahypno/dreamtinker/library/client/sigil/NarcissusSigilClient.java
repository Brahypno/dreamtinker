package org.brahypno.dreamtinker.library.client.sigil;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.config.DreamtinkerClientConfig;
import org.brahypno.dreamtinker.network.S2CNarcissusSigilShot;
import org.brahypno.dreamtinker.tools.items.NarcissusWing;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID, value = Dist.CLIENT)
public final class NarcissusSigilClient {
    private static final int FADE_TICKS = 3;
    private static final Map<Integer, Charge> CHARGES = new HashMap<>();
    private static final Map<Integer, Shot> SHOTS = new HashMap<>();
    private static final Map<BakedModel, Tip> TIPS = new WeakHashMap<>();
    private static final Matrix4f WORLD_VIEW = new Matrix4f();
    private static final Matrix4f WORLD_PROJECTION = new Matrix4f();
    private static final RandomSource MODEL_RANDOM = RandomSource.create(42);
    private static final RandomSource VISUAL_RANDOM = RandomSource.create();
    private static ClientLevel activeLevel;

    private NarcissusSigilClient() {}

    public static void renderHeld(LivingEntity entity, ItemStack stack, ItemDisplayContext context, boolean leftHand, PoseStack pose, MultiBufferSource buffer) {
        if (!(stack.getItem() instanceof NarcissusWing))
            return;
        Minecraft minecraft = Minecraft.getInstance();
        boolean firstPerson = context.firstPerson();
        if (!firstPerson && context != ItemDisplayContext.THIRD_PERSON_LEFT_HAND && context != ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
            return;
        if (!firstPerson && entity == minecraft.getCameraEntity() && minecraft.options.getCameraType().isFirstPerson())
            return;
        HumanoidArm arm = leftHand ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
        InteractionHand hand = arm == entity.getMainArm() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        if (!entity.isUsingItem() || entity.getUsedItemHand() != hand)
            return;
        float partialTick = minecraft.getFrameTime();
        float progress = NarcissusWing.getDrawProgress(entity, stack, partialTick);
        if (progress < 0)
            return;

        int ticks = entity.getTicksUsingItem();
        long now = entity.level().getGameTime();
        Charge charge = CHARGES.get(entity.getId());
        if (charge == null || !charge.charging || charge.hand != hand || ticks < charge.useTicks) {
            charge = new Charge(hand, now - ticks, appearance(stack));
            CHARGES.put(entity.getId(), charge);
        }
        charge.useTicks = ticks;
        charge.lastSeen = now;
        if (charge.fired)
            return;

        pose.pushPose();
        try {
            // Reuse the exact resolved model and Forge/TConstruct hand transforms used by renderStatic.
            BakedModel model = minecraft.getItemRenderer().getModel(stack, entity.level(), entity, entity.getId() + context.ordinal());
            model = ForgeHooksClient.handleCameraTransforms(pose, model, context, leftHand);
            pose.translate(-0.5f, -0.5f, -0.5f);
            Tip tip = TIPS.computeIfAbsent(model, NarcissusSigilClient::findTip);
            Vector3f center = pose.last().pose().transformPosition(tip.x, tip.y, tip.z, new Vector3f());
            Vector3f scale = pose.last().pose().getScale(new Vector3f());
            float radius = tip.radius * (scale.x + scale.y) * 0.5f * DreamtinkerClientConfig.NARCISSUS_SIGIL_SIZE_FACTOR.get().floatValue();
            Vec3 worldCenter = firstPerson ? handToWorld(center) : viewToWorld(center);
            Vec3 direction = entity.getViewVector(partialTick);
            charge.anchor = new Anchor(worldCenter, direction, radius);
            charge.sampleAfterglow(charge.anchor, progress, (double) now + partialTick);
            renderAfterglow(charge, pose, buffer, firstPerson, (double) now + partialTick);
            resetPose(pose);
            pose.translate(center.x, center.y, center.z);
            Vector3f normal = firstPerson ? new Vector3f(0, 0, -1) : WORLD_VIEW.transformDirection(direction.toVector3f());
            pose.mulPose(new Quaternionf().rotationTo(new Vector3f(0, 0, 1), normal.normalize()));
            pose.mulPose(Axis.ZP.rotationDegrees(charge.appearance.rotation));
            pose.scale(radius, radius, radius);
            float breath = progress >= 1 ? 0.96f + 0.04f * Mth.sin((ticks + partialTick) * 0.09f) : 1;
            NarcissusSigilRenderer.render(pose, buffer, NarcissusSigilGeometry.VARIANTS.get(charge.appearance.variant),
                                         charge.appearance.palette, progress, breath, 0);
        } finally {
            pose.popPose();
        }
    }

    private static void renderAfterglow(Charge charge, PoseStack pose, MultiBufferSource buffer, boolean firstPerson, double time) {
        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        for (Glow glow : charge.afterglow) {
            if (glow == null)
                continue;
            float fade = 1 - (float) ((time - glow.time) / 3);
            if (fade <= 0 || fade > 1)
                continue;
            Anchor anchor = glow.anchor;
            Vector3f point = firstPerson ? worldToHand(anchor.center)
                                        : WORLD_VIEW.transformPosition(anchor.center.subtract(camera).toVector3f());
            if (point.z > -0.05f)
                continue;
            pose.pushPose();
            try {
                resetPose(pose);
                pose.translate(point.x, point.y, point.z);
                Vector3f normal = WORLD_VIEW.transformDirection(anchor.direction.toVector3f()).normalize();
                pose.mulPose(new Quaternionf().rotationTo(new Vector3f(0, 0, 1), normal));
                pose.mulPose(Axis.ZP.rotationDegrees(charge.appearance.rotation));
                pose.scale(anchor.radius, anchor.radius, anchor.radius);
                NarcissusSigilRenderer.afterglow(pose, buffer, charge.appearance.palette, glow.progress, fade * fade);
            } finally {
                pose.popPose();
            }
        }
    }

    private static Appearance appearance(ItemStack stack) {
        ToolStack tool = ToolStack.from(stack);
        NarcissusSigilRenderer.Palette palette = NarcissusSigilRenderer.FALLBACK_PALETTE;
        if (tool.getMaterials().size() > 0) {
            // Primary head is material 0, matching NarcissusFluidProjectile's existing material color selection.
            palette = MaterialRenderInfoLoader.INSTANCE.getRenderInfo(tool.getMaterial(0).getVariant())
                    .map(info -> new NarcissusSigilRenderer.Palette(info.vertexColor() & 0xFFFFFF, info.vertexColor() & 0xFFFFFF))
                    .orElse(palette);
        }
        return new Appearance(VISUAL_RANDOM.nextInt(NarcissusSigilGeometry.VARIANTS.size()), VISUAL_RANDOM.nextFloat() * 360, palette);
    }

    /** Find the real tool tip, including TConstruct's baked large-tool scale and offset. */
    private static Tip findTip(BakedModel model) {
        float best = -Float.MAX_VALUE;
        float tipX = 0.5f, tipY = 0.5f, tipZ = 0.5f;
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
        MODEL_RANDOM.setSeed(42);
        for (var quad : model.getQuads(null, null, MODEL_RANDOM, ModelData.EMPTY, null)) {
            int[] vertices = quad.getVertices();
            int stride = vertices.length / 4;
            for (int i = 0; i < vertices.length; i += stride) {
                float x = Float.intBitsToFloat(vertices[i]);
                float y = Float.intBitsToFloat(vertices[i + 1]);
                float z = Float.intBitsToFloat(vertices[i + 2]);
                minX = Math.min(minX, x);
                minY = Math.min(minY, y);
                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
                if (x + y > best) {
                    best = x + y;
                    tipX = x;
                    tipY = y;
                    tipZ = z;
                }
            }
        }
        float radius = best == -Float.MAX_VALUE ? 0.65f : (float) Math.hypot(maxX - minX, maxY - minY) / 3;
        return new Tip(tipX, tipY, tipZ, radius);
    }

    public static void onShot(S2CNarcissusSigilShot message) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null)
            return;
        Charge charge = CHARGES.get(message.entityId());
        // No render sample means the charge was out of view. Never invent a head/world-space sigil.
        if (charge == null || charge.anchor == null || charge.fired || charge.hand != message.hand()
            || charge.startedAt > message.gameTime() || minecraft.level.getGameTime() - charge.lastSeen > 10)
            return;
        charge.fired = true;
        SHOTS.put(message.entityId(), new Shot(charge.anchor, charge.appearance, message.hand(), message.progress(), minecraft.level.getGameTime(), message.projectiles()));
    }

    @SubscribeEvent
    public static void renderWorld(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
            WORLD_VIEW.set(event.getPoseStack().last().pose());
            WORLD_PROJECTION.set(event.getProjectionMatrix());
        }
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES)
            return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null)
            return;
        for (var entry : SHOTS.entrySet()) {
            if (minecraft.getCameraEntity() != null && entry.getKey() == minecraft.getCameraEntity().getId()
                && minecraft.options.getCameraType().isFirstPerson())
                continue;
            renderShot(entry.getValue(), event.getPoseStack(), minecraft.renderBuffers().bufferSource(), event.getPartialTick(), false);
        }
    }

    @SubscribeEvent
    public static void renderHand(RenderHandEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null)
            return;
        Shot shot = SHOTS.get(minecraft.player.getId());
        if (shot != null && shot.hand == event.getHand())
            renderShot(shot, event.getPoseStack(), event.getMultiBufferSource(), event.getPartialTick(), true);
    }

    private static void renderShot(Shot shot, PoseStack pose, MultiBufferSource buffer, float partialTick, boolean firstPerson) {
        Minecraft minecraft = Minecraft.getInstance();
        float age = minecraft.level.getGameTime() - shot.time + partialTick;
        if (age < 0 || age >= FADE_TICKS)
            return;
        float fade = 1 - age / FADE_TICKS;
        Vec3 camera = minecraft.gameRenderer.getMainCamera().getPosition();
        Vec3 center = shot.anchor.center;
        pose.pushPose();
        try {
            if (firstPerson) {
                resetPose(pose);
                Vector3f point = worldToHand(center);
                pose.translate(point.x, point.y, point.z);
            } else {
                pose.translate(center.x - camera.x, center.y - camera.y, center.z - camera.z);
            }
            // The brief connection changes only presentation; the server spawn and collision stay authoritative.
            for (int id : shot.projectiles) {
                Entity projectile = minecraft.level.getEntity(id);
                if (projectile == null || projectile.isRemoved())
                    continue;
                Vec3 end = projectile.getPosition(partialTick);
                Vec3 delta = end.subtract(center);
                if (delta.lengthSqr() > 16)
                    end = center.add(delta.normalize().scale(4));
                Vector3f beam = firstPerson ? worldToHand(end).sub(worldToHand(center)) : end.subtract(center).toVector3f();
                NarcissusSigilRenderer.beam(pose, buffer, shot.appearance.palette, beam.x, beam.y, beam.z, fade);
            }
            Vector3f normal = firstPerson ? new Vector3f(0, 0, -1) : shot.anchor.direction.toVector3f();
            pose.mulPose(new Quaternionf().rotationTo(new Vector3f(0, 0, 1), normal.normalize()));
            pose.mulPose(Axis.ZP.rotationDegrees(shot.appearance.rotation));
            float radius = shot.anchor.radius * (1 - 0.12f * (1 - fade));
            pose.scale(radius, radius, radius);
            NarcissusSigilRenderer.render(pose, buffer, NarcissusSigilGeometry.VARIANTS.get(shot.appearance.variant),
                                         shot.appearance.palette, shot.progress, fade, fade);
        } finally {
            pose.popPose();
        }
    }

    private static Vec3 viewToWorld(Vector3f point) {
        Vector3f world = new Matrix4f(WORLD_VIEW).invert().transformPosition(new Vector3f(point));
        return Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().add(world.x, world.y, world.z);
    }

    private static Vec3 handToWorld(Vector3f point) {
        Vector4f clip = RenderSystem.getProjectionMatrix().transform(new Vector4f(point, 1));
        new Matrix4f(WORLD_PROJECTION).invert().transform(clip);
        clip.div(clip.w);
        return viewToWorld(new Vector3f(clip.x, clip.y, clip.z));
    }

    private static Vector3f worldToHand(Vec3 point) {
        Vec3 relative = point.subtract(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition());
        Vector4f clip = WORLD_VIEW.transform(new Vector4f((float) relative.x, (float) relative.y, (float) relative.z, 1));
        WORLD_PROJECTION.transform(clip);
        new Matrix4f(RenderSystem.getProjectionMatrix()).invert().transform(clip);
        clip.div(clip.w);
        return new Vector3f(clip.x, clip.y, clip.z);
    }

    private static void resetPose(PoseStack pose) {
        pose.last().pose().identity();
        pose.last().normal().identity();
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;
        Minecraft minecraft = Minecraft.getInstance();
        if (activeLevel != minecraft.level) {
            activeLevel = minecraft.level;
            CHARGES.clear();
            SHOTS.clear();
        }
        if (minecraft.level == null) {
            CHARGES.clear();
            SHOTS.clear();
            return;
        }
        long now = minecraft.level.getGameTime();
        CHARGES.entrySet().removeIf(entry -> {
            Entity entity = minecraft.level.getEntity(entry.getKey());
            if (!(entity instanceof LivingEntity living) || !living.isAlive())
                return true;
            if (NarcissusWing.getDrawProgress(living, living.getUseItem(), 0) < 0)
                entry.getValue().charging = false;
            return !entry.getValue().charging && now - entry.getValue().lastSeen > 10;
        });
        Iterator<Map.Entry<Integer, Shot>> iterator = SHOTS.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            Entity owner = minecraft.level.getEntity(entry.getKey());
            if (now - entry.getValue().time >= FADE_TICKS || owner == null || !owner.isAlive())
                iterator.remove();
        }
    }

    private record Tip(float x, float y, float z, float radius) {}
    private record Anchor(Vec3 center, Vec3 direction, float radius) {}
    private record Appearance(int variant, float rotation, NarcissusSigilRenderer.Palette palette) {}
    private record Glow(Anchor anchor, float progress, double time) {}
    private record Shot(Anchor anchor, Appearance appearance, InteractionHand hand, float progress, long time, int[] projectiles) {}

    private static final class Charge {
        private final InteractionHand hand;
        private final long startedAt;
        private final Appearance appearance;
        private int useTicks;
        private long lastSeen;
        private boolean charging = true;
        private boolean fired;
        private Anchor anchor;
        private final Glow[] afterglow = new Glow[2];
        private Glow lastSample;

        private Charge(InteractionHand hand, long startedAt, Appearance appearance) {
            this.hand = hand;
            this.startedAt = startedAt;
            this.appearance = appearance;
        }

        private void sampleAfterglow(Anchor current, float progress, double time) {
            if (lastSample != null && (long) time == (long) lastSample.time)
                return;
            if (lastSample != null) {
                Anchor previous = lastSample.anchor;
                double threshold = current.radius * current.radius * 0.0004;
                if (previous.center.distanceToSqr(current.center) > threshold || previous.direction.dot(current.direction) < 0.9994) {
                    afterglow[1] = afterglow[0];
                    afterglow[0] = lastSample;
                }
            }
            lastSample = new Glow(current, progress, time);
        }
    }
}
