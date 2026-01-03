//Decompiled by Procyon!

package xaero.map.element.render;

public abstract class ElementRenderProvider<E, C>
{
    public abstract void begin(final ElementRenderLocation p0, final C p1);
    
    public abstract boolean hasNext(final ElementRenderLocation p0, final C p1);
    
    public abstract E getNext(final ElementRenderLocation p0, final C p1);
    
    public E setupContextAndGetNext(final ElementRenderLocation location, final C context) {
        return this.getNext(location, context);
    }
    
    public abstract void end(final ElementRenderLocation p0, final C p1);
}
