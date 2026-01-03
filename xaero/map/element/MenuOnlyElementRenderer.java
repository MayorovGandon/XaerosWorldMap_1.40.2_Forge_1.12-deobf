//Decompiled by Procyon!

package xaero.map.element;

import net.minecraft.client.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.gui.*;
import xaero.map.element.render.*;

public final class MenuOnlyElementRenderer<E> extends MapElementRenderer<E, Object, MenuOnlyElementRenderer<E>>
{
    protected MenuOnlyElementRenderer(final MenuOnlyElementReader<E> reader) {
        super((Object)null, (MapElementRenderProvider)null, (MapElementReader)reader);
    }
    
    public boolean shouldRender(final int location, final boolean shadow) {
        return false;
    }
    
    @Deprecated
    public void beforeRender(final int location, final Minecraft mc, final double cameraX, final double cameraZ, final double mouseX, final double mouseZ, final float brightness, final double scale, final double screenSizeBasedScale, final TextureManager textureManager, final FontRenderer fontRenderer, final ScaledResolution scaledRes, final boolean pre) {
    }
    
    @Deprecated
    public void afterRender(final int location, final Minecraft mc, final double cameraX, final double cameraZ, final double mouseX, final double mouseZ, final float brightness, final double scale, final double screenSizeBasedScale, final TextureManager textureManager, final FontRenderer fontRenderer, final ScaledResolution scaledRes, final boolean pre) {
    }
    
    @Deprecated
    public void renderElementPre(final int location, final E element, final boolean hovered, final Minecraft mc, final double cameraX, final double cameraZ, final double mouseX, final double mouseZ, final float brightness, final double scale, final double screenSizeBasedScale, final TextureManager textureManager, final FontRenderer fontRenderer, final float optionalScale, final double partialX, final double partialY, final boolean cave, final float partialTicks, final ScaledResolution scaledRes) {
    }
    
    @Deprecated
    public boolean renderElement(final int location, final E element, final boolean hovered, final Minecraft mc, final double cameraX, final double cameraZ, final double mouseX, final double mouseZ, final float brightness, final double scale, final double screenSizeBasedScale, final TextureManager textureManager, final FontRenderer fontRenderer, final int elementIndex, final double optionalDepth, final float optionalScale, final double partialX, final double partialY, final boolean cave, final float partialTicks, final ScaledResolution scaledRes) {
        return false;
    }
    
    public void preRender(final ElementRenderInfo renderInfo, final boolean shadow) {
    }
    
    public void postRender(final ElementRenderInfo renderInfo, final boolean shadow) {
    }
    
    public void renderElementShadow(final E element, final boolean hovered, final float optionalScale, final double partialX, final double partialY, final ElementRenderInfo renderInfo) {
    }
    
    public boolean renderElement(final E element, final boolean hovered, final double optionalDepth, final float optionalScale, final double partialX, final double partialY, final ElementRenderInfo renderInfo) {
        return false;
    }
}
