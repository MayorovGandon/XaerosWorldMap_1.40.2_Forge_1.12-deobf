//Decompiled by Procyon!

package xaero.map.mods.minimap.element;

import xaero.map.element.*;
import xaero.map.element.render.*;
import xaero.common.minimap.element.render.*;

public class MinimapElementRenderProviderWrapper<E, C> extends MapElementRenderProvider<E, C>
{
    private final MinimapElementRenderProvider<E, C> provider;
    
    public MinimapElementRenderProviderWrapper(final MinimapElementRenderProvider<E, C> provider) {
        this.provider = provider;
    }
    
    public void begin(final ElementRenderLocation location, final C context) {
        this.provider.begin(MinimapElementRenderLocation.fromWorldMap(location.getIndex()), (Object)context);
    }
    
    public boolean hasNext(final ElementRenderLocation location, final C context) {
        return this.provider.hasNext(MinimapElementRenderLocation.fromWorldMap(location.getIndex()), (Object)context);
    }
    
    public E setupContextAndGetNext(final ElementRenderLocation location, final C context) {
        return (E)this.provider.setupContextAndGetNext(MinimapElementRenderLocation.fromWorldMap(location.getIndex()), (Object)context);
    }
    
    public E getNext(final ElementRenderLocation location, final C context) {
        return (E)this.provider.getNext(MinimapElementRenderLocation.fromWorldMap(location.getIndex()), (Object)context);
    }
    
    public void end(final ElementRenderLocation location, final C context) {
        this.provider.end(MinimapElementRenderLocation.fromWorldMap(location.getIndex()), (Object)context);
    }
    
    @Deprecated
    public void begin(final int location, final C context) {
        this.begin(ElementRenderLocation.fromIndex(location), context);
    }
    
    @Deprecated
    public boolean hasNext(final int location, final C context) {
        return this.hasNext(ElementRenderLocation.fromIndex(location), context);
    }
    
    @Deprecated
    public E getNext(final int location, final C context) {
        return this.getNext(ElementRenderLocation.fromIndex(location), context);
    }
    
    @Deprecated
    public E setupContextAndGetNext(final int location, final C context) {
        return this.setupContextAndGetNext(ElementRenderLocation.fromIndex(location), context);
    }
    
    @Deprecated
    public void end(final int location, final C context) {
        this.end(ElementRenderLocation.fromIndex(location), context);
    }
}
