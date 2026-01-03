//Decompiled by Procyon!

package xaero.map.element;

public abstract class MenuOnlyElementReader<E> extends MapElementReader<E, Object, MenuOnlyElementRenderer<E>>
{
    public boolean isHidden(final E element, final Object context) {
        return false;
    }
    
    public double getRenderX(final E element, final Object context, final float partialTicks) {
        return 0.0;
    }
    
    public double getRenderZ(final E element, final Object context, final float partialTicks) {
        return 0.0;
    }
    
    public int getInteractionBoxLeft(final E element, final Object context, final float partialTicks) {
        return 0;
    }
    
    public int getInteractionBoxRight(final E element, final Object context, final float partialTicks) {
        return 0;
    }
    
    public int getInteractionBoxTop(final E element, final Object context, final float partialTicks) {
        return 0;
    }
    
    public int getInteractionBoxBottom(final E element, final Object context, final float partialTicks) {
        return 0;
    }
    
    public int getRenderBoxLeft(final E element, final Object context, final float partialTicks) {
        return 0;
    }
    
    public int getRenderBoxRight(final E element, final Object context, final float partialTicks) {
        return 0;
    }
    
    public int getRenderBoxTop(final E element, final Object context, final float partialTicks) {
        return 0;
    }
    
    public int getRenderBoxBottom(final E element, final Object context, final float partialTicks) {
        return 0;
    }
}
