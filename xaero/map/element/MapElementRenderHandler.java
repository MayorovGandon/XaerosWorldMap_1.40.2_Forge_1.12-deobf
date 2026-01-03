//Decompiled by Procyon!

package xaero.map.element;

import xaero.map.gui.*;
import net.minecraft.client.*;
import org.lwjgl.opengl.*;
import net.minecraft.util.math.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.shader.*;
import net.minecraft.client.renderer.*;
import xaero.map.*;
import xaero.map.world.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.gui.*;
import xaero.map.element.render.*;
import java.util.*;
import xaero.map.mods.*;

public class MapElementRenderHandler
{
    private final List<ElementRenderer<?, ?, ?>> renderers;
    protected final ElementRenderLocation location;
    private HoveredMapElementHolder<?, ?> previousHovered;
    private boolean previousHoveredPresent;
    private boolean renderingHovered;
    private Object workingHovered;
    private ElementRenderer<?, ?, ?> workingHoveredRenderer;
    
    private MapElementRenderHandler(final List<ElementRenderer<?, ?, ?>> renderers, final ElementRenderLocation location) {
        this.renderers = renderers;
        this.location = location;
    }
    
    public void add(final ElementRenderer<?, ?, ?> renderer) {
        this.renderers.add(renderer);
    }
    
    public static <E, C> HoveredMapElementHolder<E, C> createResult(final E hovered, final ElementRenderer<?, ?, ?> hoveredRenderer) {
        final ElementRenderer<E, C, ?> rendererCast = (ElementRenderer<E, C, ?>)hoveredRenderer;
        return (HoveredMapElementHolder<E, C>)new HoveredMapElementHolder((Object)hovered, (ElementRenderer)rendererCast);
    }
    
    private <E> ElementRenderer<E, ?, ?> getRenderer(final HoveredMapElementHolder<E, ?> holder) {
        return (ElementRenderer<E, ?, ?>)holder.getRenderer();
    }
    
    public HoveredMapElementHolder<?, ?> render(final GuiMap mapScreen, final double cameraX, final double cameraZ, final int width, final int height, final double screenSizeBasedScale, final double scale, final double playerDimDiv, final double mouseX, final double mouseZ, final float brightness, final boolean cave, final HoveredMapElementHolder<?, ?> oldHovered, final Minecraft mc, final float partialTicks, final ScaledResolution scaledRes) {
        final MapProcessor mapProcessor = WorldMapSession.getCurrentSession().getMapProcessor();
        final MapDimension mapDimension = mapProcessor.getMapWorld().getCurrentDimension();
        final double mapDimScale = mapDimension.calculateDimScale();
        final TextureManager textureManager = mc.func_110434_K();
        final FontRenderer fontRenderer = mc.field_71466_p;
        textureManager.func_110577_a(WorldMap.guiTextures);
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexParameteri(3553, 10240, 9729);
        final double baseScale = 1.0 / scale;
        Collections.sort(this.renderers);
        if (this.previousHovered == null) {
            this.previousHovered = oldHovered;
        }
        this.workingHovered = null;
        this.workingHoveredRenderer = null;
        this.previousHoveredPresent = false;
        final ElementRenderInfo renderInfo = new ElementRenderInfo(this.location, mc.func_175606_aa(), (EntityPlayer)mc.field_71439_g, new Vec3d(cameraX, -1.0, cameraZ), mouseX, mouseZ, scale, cave, partialTicks, brightness, screenSizeBasedScale, null, scaledRes, mapDimScale, mapDimension.getDimId());
        GlStateManager.func_179094_E();
        GlStateManager.func_179109_b(0.0f, 0.0f, -980.0f);
        GlStateManager.func_179152_a((float)baseScale, (float)baseScale, 1.0f);
        for (final ElementRenderer<?, ?, ?> renderer : this.renderers) {
            this.renderWithRenderer(renderer, renderInfo, width, height, baseScale, playerDimDiv, true, 0, 0);
        }
        if (this.previousHoveredPresent) {
            this.renderHoveredWithRenderer(this.previousHovered, renderInfo, baseScale, playerDimDiv, true, 0, 0);
        }
        this.previousHoveredPresent = false;
        int indexLimit = 19490;
        for (final ElementRenderer<?, ?, ?> renderer2 : this.renderers) {
            GlStateManager.func_179129_p();
            int elementIndex = 0;
            elementIndex = this.renderWithRenderer(renderer2, renderInfo, width, height, baseScale, playerDimDiv, false, elementIndex, indexLimit);
            GlStateManager.func_179137_b(0.0, 0.0, this.getElementIndexDepth(elementIndex, indexLimit));
            indexLimit -= elementIndex;
            if (indexLimit < 0) {
                indexLimit = 0;
            }
        }
        if (this.previousHoveredPresent) {
            this.renderHoveredWithRenderer(this.previousHovered, renderInfo, baseScale, playerDimDiv, false, 0, indexLimit);
        }
        GlStateManager.func_179121_F();
        textureManager.func_110577_a(WorldMap.guiTextures);
        GL11.glTexParameteri(3553, 10241, 9728);
        GL11.glTexParameteri(3553, 10240, 9728);
        return this.previousHovered = ((this.previousHovered != null && this.previousHovered.is(this.workingHovered)) ? this.previousHovered : ((this.workingHovered == null) ? null : createResult(this.workingHovered, this.workingHoveredRenderer)));
    }
    
    private <E, C> int renderHoveredWithRenderer(final HoveredMapElementHolder<E, C> hoveredHolder, final ElementRenderInfo renderInfo, final double baseScale, final double playerDimDiv, final boolean pre, int elementIndex, final int indexLimit) {
        final ElementRenderer<E, C, ?> renderer = (ElementRenderer<E, C, ?>)hoveredHolder.getRenderer();
        if (!renderer.shouldRenderHovered(pre)) {
            return elementIndex;
        }
        final ElementReader<E, C, ?> reader = renderer.getReader();
        final E hoveredCast = (E)hoveredHolder.getElement();
        renderer.preRender(renderInfo, pre);
        GlStateManager.func_179094_E();
        if (!pre) {
            GlStateManager.func_179109_b(0.0f, 0.0f, 1.0f);
        }
        final double rendererDimDiv = renderer.shouldBeDimScaled() ? playerDimDiv : 1.0;
        this.renderingHovered = true;
        if (!reader.isHidden(hoveredCast, renderer.getContext()) && this.transformAndRenderElement(renderer, hoveredCast, true, renderInfo, baseScale, rendererDimDiv, pre, elementIndex, indexLimit) && !pre) {
            ++elementIndex;
        }
        this.renderingHovered = false;
        GlStateManager.func_179121_F();
        renderer.postRender(renderInfo, pre);
        return elementIndex;
    }
    
    private <E, C, R extends ElementRenderer<E, C, R>> int renderWithRenderer(final ElementRenderer<E, C, R> renderer, final ElementRenderInfo renderInfo, final int width, final int height, final double baseScale, final double playerDimDiv, final boolean pre, int elementIndex, final int indexLimit) {
        final ElementRenderLocation location = this.location;
        if (!renderer.shouldRender(location, pre)) {
            return elementIndex;
        }
        final ElementReader<E, C, R> reader = renderer.getReader();
        final ElementRenderProvider<E, C> provider = renderer.getProvider();
        final C context = renderer.getContext();
        final double rendererDimDiv = renderer.shouldBeDimScaled() ? playerDimDiv : 1.0;
        renderer.preRender(renderInfo, pre);
        provider.begin(location, context);
        while (provider.hasNext(location, context)) {
            final E e = provider.setupContextAndGetNext(location, context);
            if (e != null && !reader.isHidden(e, context) && reader.isOnScreen(e, renderInfo.renderPos.field_72450_a, renderInfo.renderPos.field_72449_c, width, height, renderInfo.scale, renderInfo.screenSizeBasedScale, rendererDimDiv, context, renderInfo.partialTicks) && this.transformAndRenderElement(renderer, e, false, renderInfo, baseScale, rendererDimDiv, pre, elementIndex, indexLimit) && !pre) {
                ++elementIndex;
            }
        }
        provider.end(location, context);
        renderer.postRender(renderInfo, pre);
        return elementIndex;
    }
    
    private <E, C, R extends ElementRenderer<E, C, R>> boolean transformAndRenderElement(final ElementRenderer<E, C, R> renderer, final E e, final boolean highlighted, final ElementRenderInfo renderInfo, final double baseScale, final double rendererDimDiv, final boolean pre, final int elementIndex, final int indexLimit) {
        final ElementReader<E, C, R> reader = renderer.getReader();
        final C context = renderer.getContext();
        if (!this.renderingHovered) {
            if (reader.isInteractable(renderInfo.location, e) && reader.isHoveredOnMap(this.location, e, renderInfo.mouseX, renderInfo.mouseZ, renderInfo.scale, renderInfo.screenSizeBasedScale, rendererDimDiv, context, renderInfo.partialTicks)) {
                this.workingHovered = e;
                this.workingHoveredRenderer = renderer;
            }
            if (!this.previousHoveredPresent && this.previousHovered != null && this.previousHovered.is((Object)e)) {
                this.previousHoveredPresent = true;
                return false;
            }
        }
        GlStateManager.func_179094_E();
        final double offX = (reader.getRenderX(e, context, renderInfo.partialTicks) / rendererDimDiv - renderInfo.renderPos.field_72450_a) / baseScale;
        final double offZ = (reader.getRenderZ(e, context, renderInfo.partialTicks) / rendererDimDiv - renderInfo.renderPos.field_72449_c) / baseScale;
        final long roundedOffX = Math.round(offX);
        final long roundedOffZ = Math.round(offZ);
        final double partialX = offX - roundedOffX;
        final double partialY = offZ - roundedOffZ;
        GlStateManager.func_179109_b((float)roundedOffX, (float)roundedOffZ, 0.0f);
        boolean result = false;
        if (pre) {
            renderer.renderElementShadow(e, highlighted, (float)renderInfo.screenSizeBasedScale, partialX, partialY, renderInfo);
        }
        else {
            final double optionalDepth = this.getElementIndexDepth(elementIndex, indexLimit);
            result = renderer.renderElement(e, highlighted, optionalDepth, (float)renderInfo.screenSizeBasedScale, partialX, partialY, renderInfo);
        }
        GlStateManager.func_179121_F();
        return result;
    }
    
    private double getElementIndexDepth(final int elementIndex, final int indexLimit) {
        return ((elementIndex >= indexLimit) ? indexLimit : elementIndex) * 0.1;
    }
    
    public static final class Builder
    {
        private Builder() {
        }
        
        public MapElementRenderHandler build() {
            final List<ElementRenderer<?, ?, ?>> renderers = new ArrayList<ElementRenderer<?, ?, ?>>();
            if (SupportMods.minimap()) {
                renderers.add((ElementRenderer<?, ?, ?>)SupportMods.xaeroMinimap.getWaypointRenderer());
            }
            renderers.add((ElementRenderer<?, ?, ?>)WorldMap.trackedPlayerRenderer);
            return new MapElementRenderHandler(renderers, ElementRenderLocation.WORLD_MAP, null);
        }
        
        public static Builder begin() {
            return new Builder();
        }
    }
}
