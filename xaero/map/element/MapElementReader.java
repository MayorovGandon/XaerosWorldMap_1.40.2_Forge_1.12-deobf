//Decompiled by Procyon!

package xaero.map.element;

import xaero.map.element.render.*;

public abstract class MapElementReader<E, C, R extends ElementRenderer<E, ?, R>> extends ElementReader<E, C, R>
{
    @Deprecated
    public boolean isInteractable(final int location, final E element) {
        return super.isInteractable(ElementRenderLocation.fromIndex(location), element);
    }
    
    @Deprecated
    public float getBoxScale(final int location, final E element, final C context) {
        return super.getBoxScale(ElementRenderLocation.fromIndex(location), element, context);
    }
    
    @Deprecated
    public boolean isHoveredOnMap(final int location, final E element, final double mouseX, final double mouseZ, final double scale, final double screenSizeBasedScale, final double rendererDimDiv, final C context, final float partialTicks) {
        return super.isHoveredOnMap(ElementRenderLocation.fromIndex(location), element, mouseX, mouseZ, scale, screenSizeBasedScale, rendererDimDiv, context, partialTicks);
    }
    
    @Override
    public boolean isInteractable(final ElementRenderLocation location, final E element) {
        return this.isInteractable(location.getIndex(), element);
    }
    
    @Override
    public float getBoxScale(final ElementRenderLocation location, final E element, final C context) {
        return this.getBoxScale(location.getIndex(), element, context);
    }
    
    @Override
    public boolean isHoveredOnMap(final ElementRenderLocation location, final E element, final double mouseX, final double mouseZ, final double scale, final double screenSizeBasedScale, final double rendererDimDiv, final C context, final float partialTicks) {
        return this.isHoveredOnMap(location.getIndex(), element, mouseX, mouseZ, scale, screenSizeBasedScale, rendererDimDiv, context, partialTicks);
    }
}
