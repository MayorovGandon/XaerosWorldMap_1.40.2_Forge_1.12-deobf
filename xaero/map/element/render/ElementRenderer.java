//Decompiled by Procyon!

package xaero.map.element.render;

public abstract class ElementRenderer<E, C, R extends ElementRenderer<E, C, R>> implements Comparable<ElementRenderer<?, ?, ?>>
{
    protected final R self;
    protected final ElementReader<E, C, R> reader;
    protected final C context;
    protected final ElementRenderProvider<E, C> provider;
    
    protected ElementRenderer(final C context, final ElementRenderProvider<E, C> provider, final ElementReader<E, C, R> reader) {
        this.self = (R)this;
        this.context = context;
        this.provider = provider;
        this.reader = reader;
    }
    
    public boolean shouldRenderHovered(final boolean pre) {
        return true;
    }
    
    public ElementReader<E, C, R> getReader() {
        return this.reader;
    }
    
    public C getContext() {
        return this.context;
    }
    
    public ElementRenderProvider<E, C> getProvider() {
        return this.provider;
    }
    
    public int getOrder() {
        return 0;
    }
    
    public boolean shouldBeDimScaled() {
        return true;
    }
    
    @Override
    public int compareTo(final ElementRenderer<?, ?, ?> o) {
        return Integer.compare(this.getOrder(), o.getOrder());
    }
    
    public abstract void preRender(final ElementRenderInfo p0, final boolean p1);
    
    public abstract void postRender(final ElementRenderInfo p0, final boolean p1);
    
    public abstract void renderElementShadow(final E p0, final boolean p1, final float p2, final double p3, final double p4, final ElementRenderInfo p5);
    
    public abstract boolean renderElement(final E p0, final boolean p1, final double p2, final float p3, final double p4, final double p5, final ElementRenderInfo p6);
    
    public abstract boolean shouldRender(final ElementRenderLocation p0, final boolean p1);
}
