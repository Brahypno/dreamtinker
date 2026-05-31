package org.dreamtinker.dreamtinker.library.client;


import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public final class DTRenderTypes extends RenderStateShard {
    private DTRenderTypes() {
        super("dreamtinker_render_types", null, null);
    }

    public static RenderType additiveTrail(ResourceLocation texture) {
        return RenderType.create(
                "dreamtinker_additive_trail",
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

    public static RenderType additiveTrailStrip(ResourceLocation texture) {
        return RenderType.create(
                "dreamtinker_additive_trail_strip",
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
}
