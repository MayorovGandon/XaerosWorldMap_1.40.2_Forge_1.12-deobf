//Decompiled by Procyon!

package xaero.map.radar.tracker;

import xaero.map.element.*;
import java.util.*;
import xaero.map.element.render.*;

public class PlayerTrackerMapElementRenderProvider<C> extends MapElementRenderProvider<PlayerTrackerMapElement<?>, C>
{
    private PlayerTrackerMapElementCollector collector;
    private Iterator<PlayerTrackerMapElement<?>> iterator;
    
    public PlayerTrackerMapElementRenderProvider(final PlayerTrackerMapElementCollector collector) {
        this.collector = collector;
    }
    
    public void begin(final ElementRenderLocation location, final C context) {
        this.iterator = this.collector.getElements().iterator();
    }
    
    public boolean hasNext(final ElementRenderLocation location, final C context) {
        return this.iterator != null && this.iterator.hasNext();
    }
    
    public PlayerTrackerMapElement<?> getNext(final ElementRenderLocation location, final C context) {
        return this.iterator.next();
    }
    
    public void end(final ElementRenderLocation location, final C context) {
        this.iterator = null;
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
    public PlayerTrackerMapElement<?> getNext(final int location, final C context) {
        return this.getNext(ElementRenderLocation.fromIndex(location), context);
    }
    
    @Deprecated
    public PlayerTrackerMapElement<?> setupContextAndGetNext(final ElementRenderLocation location, final C context) {
        return this.getNext(location, context);
    }
    
    @Deprecated
    public PlayerTrackerMapElement<?> setupContextAndGetNext(final int location, final C context) {
        return this.setupContextAndGetNext(ElementRenderLocation.fromIndex(location), context);
    }
    
    @Deprecated
    public void end(final int location, final C context) {
        this.end(ElementRenderLocation.fromIndex(location), context);
    }
}
