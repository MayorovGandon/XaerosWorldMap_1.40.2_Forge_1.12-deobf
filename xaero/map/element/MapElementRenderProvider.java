//Decompiled by Procyon!

package xaero.map.element;

import xaero.map.element.render.*;

public abstract class MapElementRenderProvider<E, C> extends ElementRenderProvider<E, C>
{
    @Deprecated
    public abstract void begin(final int p0, final C p1);
    
    @Deprecated
    public abstract boolean hasNext(final int p0, final C p1);
    
    @Deprecated
    public abstract E getNext(final int p0, final C p1);
    
    @Deprecated
    public E setupContextAndGetNext(final int location, final C context) {
        return this.getNext(location, context);
    }
    
    @Deprecated
    public abstract void end(final int p0, final C p1);
    
    @Override
    public void begin(final ElementRenderLocation location, final C context) {
        this.begin(location.getIndex(), context);
    }
    
    @Override
    public boolean hasNext(final ElementRenderLocation location, final C context) {
        return this.hasNext(location.getIndex(), context);
    }
    
    @Override
    public E getNext(final ElementRenderLocation location, final C context) {
        return this.getNext(location.getIndex(), context);
    }
    
    @Override
    public E setupContextAndGetNext(final ElementRenderLocation location, final C context) {
        return this.setupContextAndGetNext(location.getIndex(), context);
    }
    
    @Override
    public void end(final ElementRenderLocation location, final C context) {
        this.end(location.getIndex(), context);
    }
}
