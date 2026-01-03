//Decompiled by Procyon!

package xaero.map.mods.minimap.element;

import java.util.function.*;
import xaero.map.element.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.gui.*;
import xaero.map.element.render.*;
import net.minecraft.util.math.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.shader.*;
import xaero.map.*;
import xaero.map.world.*;
import xaero.common.*;
import xaero.map.mods.*;
import xaero.common.minimap.render.*;
import xaero.common.minimap.element.render.*;

public final class MinimapElementRendererWrapper<E, C> extends MapElementRenderer<E, C, MinimapElementRendererWrapper<E, C>>
{
    private final int order;
    private final IXaeroMinimap modMain;
    private final MinimapElementRenderer<E, C> renderer;
    private final Supplier<Boolean> shouldRenderSupplier;
    private ElementRenderInfo compatibleRenderInfo;
    
    private MinimapElementRendererWrapper(final IXaeroMinimap modMain, final C context, final MinimapElementRenderProviderWrapper<E, C> provider, final MinimapElementReaderWrapper<E, C> reader, final MinimapElementRenderer<E, C> renderer, final Supplier<Boolean> shouldRenderSupplier, final int order) {
        super((Object)context, (MapElementRenderProvider)provider, (MapElementReader)reader);
        this.order = order;
        this.renderer = renderer;
        this.modMain = modMain;
        this.shouldRenderSupplier = shouldRenderSupplier;
    }
    
    @Deprecated
    public void beforeRender(final int location, final Minecraft mc, final double cameraX, final double cameraZ, final double mouseX, final double mouseZ, final float brightness, final double scale, final double screenSizeBasedScale, final TextureManager textureManager, final FontRenderer fontRenderer, final ScaledResolution scaledRes, final boolean pre) {
        final ElementRenderInfo renderInfo = this.getFakeRenderInfo(location, mc, cameraX, cameraZ, mouseX, mouseZ, brightness, scale, screenSizeBasedScale, textureManager, fontRenderer, pre, scaledRes);
        this.preRender(renderInfo, pre);
    }
    
    @Deprecated
    public void afterRender(final int location, final Minecraft mc, final double cameraX, final double cameraZ, final double mouseX, final double mouseZ, final float brightness, final double scale, final double screenSizeBasedScale, final TextureManager textureManager, final FontRenderer fontRenderer, final ScaledResolution scaledRes, final boolean pre) {
        if (this.compatibleRenderInfo == null) {
            this.compatibleRenderInfo = this.getFakeRenderInfo(location, mc, cameraX, cameraZ, mouseX, mouseZ, brightness, scale, screenSizeBasedScale, textureManager, fontRenderer, pre, scaledRes);
        }
        this.postRender(this.compatibleRenderInfo, pre);
        this.compatibleRenderInfo = null;
    }
    
    private ElementRenderInfo getFakeRenderInfo(final int location, final Minecraft mc, final double cameraX, final double cameraZ, final double mouseX, final double mouseZ, final float brightness, final double scale, final double screenSizeBasedScale, final TextureManager textureManager, final FontRenderer fontRenderer, final boolean pre, final ScaledResolution scaledRes) {
        final MapProcessor mapProcessor = WorldMapSession.getCurrentSession().getMapProcessor();
        final MapDimension mapDimension = mapProcessor.getMapWorld().getCurrentDimension();
        final double mapDimScale = mapDimension.calculateDimScale();
        return new ElementRenderInfo(ElementRenderLocation.fromIndex(location), mc.func_175606_aa(), (EntityPlayer)mc.field_71439_g, new Vec3d(cameraX, -1.0, cameraZ), mouseX, mouseZ, scale, false, 1.0f, brightness, screenSizeBasedScale, (Framebuffer)null, scaledRes, mapDimScale, mapDimension.getDimId());
    }
    
    @Deprecated
    public boolean renderElement(final int location, final E element, final boolean hovered, final Minecraft mc, final double cameraX, final double cameraZ, final double mouseX, final double mouseZ, final float brightness, final double scale, final double screenSizeBasedScale, final TextureManager textureManager, final FontRenderer fontRenderer, final int elementIndex, final double optionalDepth, final float optionalScale, final double partialX, final double partialY, final boolean cave, final float partialTicks, final ScaledResolution scaledRes) {
        if (this.compatibleRenderInfo == null) {
            final MapProcessor mapProcessor = WorldMapSession.getCurrentSession().getMapProcessor();
            final MapDimension mapDimension = mapProcessor.getMapWorld().getCurrentDimension();
            final double mapDimScale = mapDimension.calculateDimScale();
            this.compatibleRenderInfo = new ElementRenderInfo(ElementRenderLocation.fromIndex(location), mc.func_175606_aa(), (EntityPlayer)mc.field_71439_g, new Vec3d(cameraX, -1.0, cameraZ), mouseX, mouseZ, scale, cave, partialTicks, brightness, screenSizeBasedScale, (Framebuffer)null, scaledRes, mapDimScale, mapDimension.getDimId());
        }
        return this.renderElement(element, hovered, optionalDepth, optionalScale, partialX, partialY, this.compatibleRenderInfo);
    }
    
    public void preRender(final ElementRenderInfo renderInfo, final boolean shadow) {
        final XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
        this.renderer.preRender(MinimapElementRenderLocation.fromWorldMap(renderInfo.location.getIndex()), renderInfo.renderEntity, renderInfo.player, renderInfo.renderPos.field_72450_a, renderInfo.renderPos.field_72448_b, renderInfo.renderPos.field_72449_c, renderInfo.scaledResolution, this.modMain);
    }
    
    public void postRender(final ElementRenderInfo renderInfo, final boolean shadow) {
        final XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
        this.renderer.postRender(MinimapElementRenderLocation.fromWorldMap(renderInfo.location.getIndex()), renderInfo.renderEntity, renderInfo.player, renderInfo.renderPos.field_72450_a, renderInfo.renderPos.field_72448_b, renderInfo.renderPos.field_72449_c, renderInfo.scaledResolution, this.modMain);
    }
    
    public boolean renderElement(final E element, final boolean hovered, final double optionalDepth, final float optionalScale, final double partialX, final double partialY, final ElementRenderInfo renderInfo) {
        final Minecraft mc = Minecraft.func_71410_x();
        final MinimapRendererHelper helper = this.modMain.getInterfaces().getMinimapInterface().getMinimapFBORenderer().getHelper();
        if (SupportMods.xaeroMinimap.compatibilityVersion >= 12) {
            return this.renderer.renderElement(MinimapElementRenderLocation.fromWorldMap(renderInfo.location.getIndex()), hovered, false, mc.field_71466_p, renderInfo.framebuffer, helper, mc.func_175606_aa(), (EntityPlayer)mc.field_71439_g, renderInfo.renderPos.field_72450_a, renderInfo.renderPos.field_72448_b, renderInfo.renderPos.field_72449_c, 0, optionalDepth, optionalScale, (Object)element, partialX, partialY, renderInfo.cave, renderInfo.partialTicks, renderInfo.scaledResolution);
        }
        return this.renderer.renderElement(MinimapElementRenderLocation.fromWorldMap(renderInfo.location.getIndex()), hovered, mc.field_71466_p, renderInfo.framebuffer, helper, mc.func_175606_aa(), (EntityPlayer)mc.field_71439_g, renderInfo.renderPos.field_72450_a, renderInfo.renderPos.field_72448_b, renderInfo.renderPos.field_72449_c, 0, optionalDepth, optionalScale, (Object)element, partialX, partialY, renderInfo.cave, renderInfo.partialTicks, renderInfo.scaledResolution);
    }
    
    @Deprecated
    public void renderElementPre(final int location, final E element, final boolean hovered, final Minecraft mc, final double cameraX, final double cameraZ, final double mouseX, final double mouseZ, final float brightness, final double scale, final double screenSizeBasedScale, final TextureManager textureManager, final FontRenderer fontRenderer, final float optionalScale, final double partialX, final double partialY, final boolean cave, final float partialTicks, final ScaledResolution scaledRes) {
    }
    
    public void renderElementShadow(final E element, final boolean hovered, final float optionalScale, final double partialX, final double partialY, final ElementRenderInfo renderInfo) {
    }
    
    public boolean shouldRender(final int location, final boolean shadow) {
        return !shadow && this.shouldRenderSupplier.get() && this.renderer.shouldRender(location);
    }
    
    public int getOrder() {
        return this.order;
    }
    
    public static final class Builder<E, C>
    {
        private final MinimapElementRenderer<E, C> renderer;
        private Supplier<Boolean> shouldRenderSupplier;
        private IXaeroMinimap modMain;
        private int order;
        
        private Builder(final MinimapElementRenderer<E, C> renderer) {
            this.renderer = renderer;
        }
        
        private Builder<E, C> setDefault() {
            this.setModMain(null);
            this.setShouldRenderSupplier(new Supplier<Boolean>() {
                @Override
                public Boolean get() {
                    return true;
                }
            });
            this.setOrder(0);
            return this;
        }
        
        public Builder<E, C> setModMain(final IXaeroMinimap modMain) {
            this.modMain = modMain;
            return this;
        }
        
        public Builder<E, C> setShouldRenderSupplier(final Supplier<Boolean> shouldRenderSupplier) {
            this.shouldRenderSupplier = shouldRenderSupplier;
            return this;
        }
        
        public Builder<E, C> setOrder(final int order) {
            this.order = order;
            return this;
        }
        
        public MinimapElementRendererWrapper<E, C> build() {
            if (this.modMain == null || this.shouldRenderSupplier == null) {
                throw new IllegalStateException();
            }
            final MinimapElementRenderProviderWrapper<E, C> providerWrapper = new MinimapElementRenderProviderWrapper<E, C>((xaero.common.minimap.element.render.MinimapElementRenderProvider<E, C>)this.renderer.getProvider());
            final MinimapElementReaderWrapper<E, C> readerWrapper = (MinimapElementReaderWrapper<E, C>)new MinimapElementReaderWrapper(this.renderer.getElementReader());
            final C context = (C)this.renderer.getContext();
            return new MinimapElementRendererWrapper<E, C>(this.modMain, context, providerWrapper, readerWrapper, this.renderer, this.shouldRenderSupplier, this.order, null);
        }
        
        public static <E, C> Builder<E, C> begin(final MinimapElementRenderer<E, C> renderer) {
            return new Builder<E, C>(renderer).setDefault();
        }
    }
}
