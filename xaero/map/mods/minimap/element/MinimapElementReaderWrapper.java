//Decompiled by Procyon!

package xaero.map.mods.minimap.element;

import xaero.map.element.*;
import xaero.map.element.render.*;
import xaero.common.minimap.element.render.*;
import net.minecraft.client.*;

public class MinimapElementReaderWrapper<E, C> extends MapElementReader<E, C, MinimapElementRendererWrapper<E, C>>
{
    private final MinimapElementReader<E, C> reader;
    
    public MinimapElementReaderWrapper(final MinimapElementReader<E, C> reader) {
        this.reader = reader;
    }
    
    public boolean isHidden(final E element, final C context) {
        return this.reader.isHidden((Object)element, (Object)context);
    }
    
    @Deprecated
    public float getBoxScale(final int location, final E element, final C context) {
        return this.getBoxScale(ElementRenderLocation.fromIndex(location), element, context);
    }
    
    public float getBoxScale(final ElementRenderLocation location, final E element, final C context) {
        return this.reader.getBoxScale(MinimapElementRenderLocation.fromWorldMap(location.getIndex()), (Object)element, (Object)context);
    }
    
    public double getRenderX(final E element, final C context, final float partialTicks) {
        return this.reader.getRenderX((Object)element, (Object)context, partialTicks);
    }
    
    public double getRenderZ(final E element, final C context, final float partialTicks) {
        return this.reader.getRenderZ((Object)element, (Object)context, partialTicks);
    }
    
    public int getInteractionBoxLeft(final E element, final C context, final float partialTicks) {
        return this.reader.getInteractionBoxLeft((Object)element, (Object)context, partialTicks);
    }
    
    public int getInteractionBoxRight(final E element, final C context, final float partialTicks) {
        return this.reader.getInteractionBoxRight((Object)element, (Object)context, partialTicks);
    }
    
    public int getInteractionBoxTop(final E element, final C context, final float partialTicks) {
        return this.reader.getInteractionBoxTop((Object)element, (Object)context, partialTicks);
    }
    
    public int getInteractionBoxBottom(final E element, final C context, final float partialTicks) {
        return this.reader.getInteractionBoxBottom((Object)element, (Object)context, partialTicks);
    }
    
    public int getLeftSideLength(final E element, final Minecraft mc) {
        return this.reader.getLeftSideLength((Object)element, mc);
    }
    
    public String getMenuName(final E element) {
        return this.reader.getMenuName((Object)element);
    }
    
    public String getFilterName(final E element) {
        return this.reader.getFilterName((Object)element);
    }
    
    public int getMenuTextFillLeftPadding(final E element) {
        return this.reader.getMenuTextFillLeftPadding((Object)element);
    }
    
    public int getRightClickTitleBackgroundColor(final E element) {
        return this.reader.getRightClickTitleBackgroundColor((Object)element);
    }
    
    public int getRenderBoxLeft(final E element, final C context, final float partialTicks) {
        return this.reader.getRenderBoxLeft((Object)element, (Object)context, partialTicks);
    }
    
    public int getRenderBoxRight(final E element, final C context, final float partialTicks) {
        return this.reader.getRenderBoxRight((Object)element, (Object)context, partialTicks);
    }
    
    public int getRenderBoxTop(final E element, final C context, final float partialTicks) {
        return this.reader.getRenderBoxTop((Object)element, (Object)context, partialTicks);
    }
    
    public int getRenderBoxBottom(final E element, final C context, final float partialTicks) {
        return this.reader.getRenderBoxBottom((Object)element, (Object)context, partialTicks);
    }
    
    @Deprecated
    public boolean isInteractable(final int location, final E element) {
        return this.isInteractable(ElementRenderLocation.fromIndex(location), element);
    }
    
    public boolean isInteractable(final ElementRenderLocation location, final E element) {
        return this.reader.isInteractable(location.getIndex(), (Object)element);
    }
    
    public boolean shouldScaleBoxWithOptionalScale() {
        return this.reader.shouldScaleBoxWithOptionalScale();
    }
}
