//Decompiled by Procyon!

package xaero.map.element;

import net.minecraft.client.*;
import xaero.map.element.render.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.gui.*;

public abstract class MapElementRenderer<E, C, R extends MapElementRenderer<E, C, R>> extends ElementRenderer<E, C, R>
{
    protected MapElementRenderer(final C context, final MapElementRenderProvider<E, C> provider, final MapElementReader<E, C, R> reader) {
        super(context, provider, (ElementReader<E, C, ElementRenderer>)reader);
    }
    
    @Override
    public void preRender(final ElementRenderInfo renderInfo, final boolean shadow) {
        this.beforeRender(renderInfo.location.getIndex(), Minecraft.func_71410_x(), renderInfo.renderPos.field_72450_a, renderInfo.renderPos.field_72449_c, renderInfo.mouseX, renderInfo.mouseZ, renderInfo.brightness, renderInfo.scale, renderInfo.screenSizeBasedScale, Minecraft.func_71410_x().func_110434_K(), Minecraft.func_71410_x().field_71466_p, renderInfo.scaledResolution, shadow);
    }
    
    @Override
    public void postRender(final ElementRenderInfo renderInfo, final boolean shadow) {
        this.afterRender(renderInfo.location.getIndex(), Minecraft.func_71410_x(), renderInfo.renderPos.field_72450_a, renderInfo.renderPos.field_72449_c, renderInfo.mouseX, renderInfo.mouseZ, renderInfo.brightness, renderInfo.scale, renderInfo.screenSizeBasedScale, Minecraft.func_71410_x().func_110434_K(), Minecraft.func_71410_x().field_71466_p, renderInfo.scaledResolution, shadow);
    }
    
    @Override
    public void renderElementShadow(final E element, final boolean hovered, final float optionalScale, final double partialX, final double partialY, final ElementRenderInfo renderInfo) {
        this.renderElementPre(renderInfo.location.getIndex(), element, hovered, Minecraft.func_71410_x(), renderInfo.renderPos.field_72450_a, renderInfo.renderPos.field_72449_c, renderInfo.mouseX, renderInfo.mouseZ, renderInfo.brightness, renderInfo.scale, renderInfo.screenSizeBasedScale, Minecraft.func_71410_x().func_110434_K(), Minecraft.func_71410_x().field_71466_p, optionalScale, partialX, partialY, renderInfo.cave, renderInfo.partialTicks, renderInfo.scaledResolution);
    }
    
    @Override
    public boolean renderElement(final E element, final boolean hovered, final double optionalDepth, final float optionalScale, final double partialX, final double partialY, final ElementRenderInfo renderInfo) {
        return this.renderElement(renderInfo.location.getIndex(), element, hovered, Minecraft.func_71410_x(), renderInfo.renderPos.field_72450_a, renderInfo.renderPos.field_72449_c, renderInfo.mouseX, renderInfo.mouseZ, renderInfo.brightness, renderInfo.scale, renderInfo.screenSizeBasedScale, Minecraft.func_71410_x().func_110434_K(), Minecraft.func_71410_x().field_71466_p, 0, optionalDepth, optionalScale, partialX, partialY, renderInfo.cave, renderInfo.partialTicks, renderInfo.scaledResolution);
    }
    
    @Override
    public boolean shouldRender(final ElementRenderLocation location, final boolean shadow) {
        return this.shouldRender(location.getIndex(), shadow);
    }
    
    @Deprecated
    public abstract void beforeRender(final int p0, final Minecraft p1, final double p2, final double p3, final double p4, final double p5, final float p6, final double p7, final double p8, final TextureManager p9, final FontRenderer p10, final ScaledResolution p11, final boolean p12);
    
    @Deprecated
    public abstract void afterRender(final int p0, final Minecraft p1, final double p2, final double p3, final double p4, final double p5, final float p6, final double p7, final double p8, final TextureManager p9, final FontRenderer p10, final ScaledResolution p11, final boolean p12);
    
    @Deprecated
    public abstract void renderElementPre(final int p0, final E p1, final boolean p2, final Minecraft p3, final double p4, final double p5, final double p6, final double p7, final float p8, final double p9, final double p10, final TextureManager p11, final FontRenderer p12, final float p13, final double p14, final double p15, final boolean p16, final float p17, final ScaledResolution p18);
    
    @Deprecated
    public abstract boolean renderElement(final int p0, final E p1, final boolean p2, final Minecraft p3, final double p4, final double p5, final double p6, final double p7, final float p8, final double p9, final double p10, final TextureManager p11, final FontRenderer p12, final int p13, final double p14, final float p15, final double p16, final double p17, final boolean p18, final float p19, final ScaledResolution p20);
    
    @Deprecated
    public abstract boolean shouldRender(final int p0, final boolean p1);
    
    @Deprecated
    public MapElementReader<E, C, R> getReader() {
        return (MapElementReader<E, C, R>)super.getReader();
    }
    
    @Deprecated
    @Override
    public MapElementRenderProvider<E, C> getProvider() {
        return (MapElementRenderProvider<E, C>)(MapElementRenderProvider)super.getProvider();
    }
}
