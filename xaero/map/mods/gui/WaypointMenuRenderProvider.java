//Decompiled by Procyon!

package xaero.map.mods.gui;

import xaero.map.element.*;
import xaero.map.mods.*;
import xaero.map.element.render.*;
import java.util.*;

public class WaypointMenuRenderProvider extends MapElementRenderProvider<Waypoint, WaypointMenuRenderContext>
{
    private final SupportXaeroMinimap minimap;
    private Iterator<Waypoint> iterator;
    
    public WaypointMenuRenderProvider(final SupportXaeroMinimap minimap) {
        this.minimap = minimap;
    }
    
    public void begin(final ElementRenderLocation location, final WaypointMenuRenderContext context) {
        final ArrayList<Waypoint> sortedList = this.minimap.getWaypointsSorted();
        if (sortedList == null) {
            this.iterator = null;
        }
        else {
            this.iterator = this.minimap.getWaypointsSorted().iterator();
        }
    }
    
    public boolean hasNext(final ElementRenderLocation location, final WaypointMenuRenderContext context) {
        return this.iterator != null && this.iterator.hasNext();
    }
    
    public Waypoint getNext(final ElementRenderLocation location, final WaypointMenuRenderContext context) {
        return this.iterator.next();
    }
    
    public void end(final ElementRenderLocation location, final WaypointMenuRenderContext context) {
    }
    
    @Deprecated
    public void begin(final int location, final WaypointMenuRenderContext context) {
        this.begin(ElementRenderLocation.fromIndex(location), context);
    }
    
    @Deprecated
    public boolean hasNext(final int location, final WaypointMenuRenderContext context) {
        return this.hasNext(ElementRenderLocation.fromIndex(location), context);
    }
    
    @Deprecated
    public Waypoint getNext(final int location, final WaypointMenuRenderContext context) {
        return this.getNext(ElementRenderLocation.fromIndex(location), context);
    }
    
    @Deprecated
    public Waypoint setupContextAndGetNext(final ElementRenderLocation location, final WaypointMenuRenderContext context) {
        return this.getNext(location, context);
    }
    
    @Deprecated
    public Waypoint setupContextAndGetNext(final int location, final WaypointMenuRenderContext context) {
        return this.setupContextAndGetNext(ElementRenderLocation.fromIndex(location), context);
    }
    
    @Deprecated
    public void end(final int location, final WaypointMenuRenderContext context) {
        this.end(ElementRenderLocation.fromIndex(location), context);
    }
}
