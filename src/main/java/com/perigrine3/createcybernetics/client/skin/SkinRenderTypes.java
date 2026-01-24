package com.perigrine3.createcybernetics.client.skin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.vertex.VertexFormat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SkinRenderTypes {
    private SkinRenderTypes() {}

    private static final Map<ResourceLocation, RenderType> EMISSIVE_TINTED_CACHE = new ConcurrentHashMap<>();

    private static final RenderStateShard.TransparencyStateShard CC_ADDITIVE_ALPHA =
            new RenderStateShard.TransparencyStateShard("cc_additive_alpha",
                    () -> {
                        RenderSystem.enableBlend();
                        RenderSystem.blendFuncSeparate(
                                GlStateManager.SourceFactor.SRC_ALPHA,
                                GlStateManager.DestFactor.ONE,
                                GlStateManager.SourceFactor.ONE,
                                GlStateManager.DestFactor.ONE
                        );
                    },
                    () -> {
                        RenderSystem.defaultBlendFunc();
                        RenderSystem.disableBlend();
                    });

    public static RenderType emissiveTinted(ResourceLocation tex) {
        return EMISSIVE_TINTED_CACHE.computeIfAbsent(tex, t ->
                RenderType.create(
                        "cc_emissive_tinted",
                        DefaultVertexFormat.NEW_ENTITY,
                        VertexFormat.Mode.QUADS,
                        256,
                        true,
                        true,
                        RenderType.CompositeState.builder()
                                .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                                .setTextureState(new RenderStateShard.TextureStateShard(t, false, false))
                                .setTransparencyState(CC_ADDITIVE_ALPHA)
                                .setCullState(RenderStateShard.NO_CULL)
                                .setLightmapState(RenderStateShard.LIGHTMAP)
                                .setOverlayState(RenderStateShard.OVERLAY)
                                .setDepthTestState(RenderStateShard.EQUAL_DEPTH_TEST)
                                .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                                .createCompositeState(true)
                )
        );
    }
}
