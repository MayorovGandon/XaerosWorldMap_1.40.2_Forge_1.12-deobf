//Decompiled by Procyon!

package xaero.map.mods.gui;

import xaero.map.element.*;
import xaero.map.mods.*;
import java.util.*;
import xaero.map.element.render.*;
import xaero.map.*;
import xaero.map.common.config.option.*;
import xaero.lib.common.config.option.*;
import xaero.map.config.primary.option.*;
import xaero.lib.client.config.*;
import xaero.lib.common.config.single.*;
import xaero.lib.common.config.*;

public class WaypointRenderProvider extends MapElementRenderProvider<Waypoint, WaypointRenderContext>
{
    private final SupportXaeroMinimap minimap;
    private Iterator<Waypoint> iterator;
    
    public WaypointRenderProvider(final SupportXaeroMinimap minimap) {
        this.minimap = minimap;
    }
    
    public void begin(final ElementRenderLocation location, final WaypointRenderContext context) {
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        if (!(boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.WAYPOINTS) || this.minimap.getWaypoints() == null) {
            this.iterator = null;
            return;
        }
        this.iterator = this.minimap.getWaypoints().iterator();
        context.worldmapWaypointsScale = (float)(double)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.WAYPOINT_SCALE);
        context.waypointBackgrounds = (boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.WAYPOINT_BACKGROUNDS);
        final SingleConfigManager<Config> primaryConfigManager = (SingleConfigManager<Config>)configManager.getPrimaryConfigManager();
        context.showDisabledWaypoints = (boolean)primaryConfigManager.getEffective((ConfigOption)WorldMapPrimaryClientConfigOptions.DISPLAY_DISABLED_WAYPOINTS);
    }
    
    public boolean hasNext(final ElementRenderLocation location, final WaypointRenderContext context) {
        return this.iterator != null && this.iterator.hasNext();
    }
    
    public Waypoint getNext(final ElementRenderLocation location, final WaypointRenderContext context) {
        return this.iterator.next();
    }
    
    public void end(final ElementRenderLocation location, final WaypointRenderContext context) {
    }
    
    @Deprecated
    public void begin(final int location, final WaypointRenderContext context) {
        this.begin(ElementRenderLocation.fromIndex(location), context);
    }
    
    @Deprecated
    public boolean hasNext(final int location, final WaypointRenderContext context) {
        return this.hasNext(ElementRenderLocation.fromIndex(location), context);
    }
    
    @Deprecated
    public Waypoint getNext(final int location, final WaypointRenderContext context) {
        return this.getNext(ElementRenderLocation.fromIndex(location), context);
    }
    
    @Deprecated
    public Waypoint setupContextAndGetNext(final ElementRenderLocation location, final WaypointRenderContext context) {
        return this.getNext(location, context);
    }
    
    @Deprecated
    public Waypoint setupContextAndGetNext(final int location, final WaypointRenderContext context) {
        return this.setupContextAndGetNext(ElementRenderLocation.fromIndex(location), context);
    }
    
    @Deprecated
    public void end(final int location, final WaypointRenderContext context) {
        this.end(ElementRenderLocation.fromIndex(location), context);
    }
}
