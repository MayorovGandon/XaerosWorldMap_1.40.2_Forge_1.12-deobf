//Decompiled by Procyon!

package xaero.map.element;

import xaero.map.gui.*;
import xaero.map.element.render.*;
import java.util.*;
import xaero.map.gui.dropdown.rightclick.*;

public class HoveredMapElementHolder<E, C> implements IRightClickableElement
{
    private final E element;
    private final ElementRenderer<E, C, ?> renderer;
    
    public HoveredMapElementHolder(final E element, final ElementRenderer<E, C, ?> renderer) {
        this.element = element;
        this.renderer = renderer;
    }
    
    @Override
    public ArrayList<RightClickOption> getRightClickOptions() {
        return this.renderer.getReader().getRightClickOptions(this.element, this);
    }
    
    @Override
    public boolean isRightClickValid() {
        return this.renderer.getReader().isRightClickValid(this.element);
    }
    
    @Override
    public int getRightClickTitleBackgroundColor() {
        return this.renderer.getReader().getRightClickTitleBackgroundColor(this.element);
    }
    
    public E getElement() {
        return this.element;
    }
    
    public ElementRenderer<E, C, ?> getRenderer() {
        return this.renderer;
    }
    
    public boolean is(final Object o) {
        return this.element == o;
    }
}
