package org.brahypno.dreamtinker.library.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DTRenderTypes extends RenderStateShard {
    private static final Map<ResourceLocation, RenderType> ADDITIVE_TRAILS = new ConcurrentHashMap<>();
    private static final Map<ResourceLocation, RenderType> ADDITIVE_TRAIL_STRIPS = new ConcurrentHashMap<>();

    private DTRenderTypes() {
        super("dreamtinker_render_types", null, null);
    }

    public static RenderType additiveTrail(ResourceLocation texture) {
        return ADDITIVE_TRAILS.computeIfAbsent(texture, DTRenderTypes::createAdditiveTrail);
    }

    public static RenderType additiveTrailStrip(ResourceLocation texture) {
        return ADDITIVE_TRAIL_STRIPS.computeIfAbsent(texture, DTRenderTypes::createAdditiveTrailStrip);
    }

    private static RenderType createAdditiveTrail(ResourceLocation texture) {
        return RenderType.create(
                "dreamtinker_additive_trail_" + safeName(texture),
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                256,
                false,
                true,
                RenderType.CompositeState.builder()
                                         .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                                         .setTextureState(new TextureStateShard(texture, false, false))
                                         .setTransparencyState(ADDITIVE_TRANSPARENCY)
                                         .setCullState(NO_CULL)
                                         .setLightmapState(LIGHTMAP)
                                         .setOverlayState(OVERLAY)
                                         .createCompositeState(false)
        );
    }

    private static RenderType createAdditiveTrailStrip(ResourceLocation texture) {
        return RenderType.create(
                "dreamtinker_additive_trail_strip_" + safeName(texture),
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.TRIANGLE_STRIP,
                256,
                false,
                true,
                RenderType.CompositeState.builder()
                                         .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                                         .setTextureState(new TextureStateShard(texture, false, false))
                                         .setTransparencyState(ADDITIVE_TRANSPARENCY)
                                         .setCullState(NO_CULL)
                                         .setLightmapState(LIGHTMAP)
                                         .setOverlayState(OVERLAY)
                                         .setWriteMaskState(COLOR_WRITE)
                                         .createCompositeState(false)
        );
    }

    private static String safeName(ResourceLocation texture) {
        return texture.toString().replace(':', '_').replace('/', '_');
    }
}
