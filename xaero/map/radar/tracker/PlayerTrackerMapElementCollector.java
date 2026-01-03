//Decompiled by Procyon!

package xaero.map.radar.tracker;

import net.minecraft.client.*;
import java.util.*;
import xaero.map.radar.tracker.system.*;
import net.minecraft.entity.player.*;

public class PlayerTrackerMapElementCollector
{
    private Map<UUID, PlayerTrackerMapElement<?>> elements;
    private final PlayerTrackerSystemManager systemManager;
    private final Runnable onElementsChange;
    
    public PlayerTrackerMapElementCollector(final PlayerTrackerSystemManager systemManager, final Runnable onElementsChange) {
        this.elements = new HashMap<UUID, PlayerTrackerMapElement<?>>();
        this.systemManager = systemManager;
        this.onElementsChange = onElementsChange;
    }
    
    public void update(final Minecraft mc) {
        if (this.elements == null) {
            this.elements = new HashMap<UUID, PlayerTrackerMapElement<?>>();
        }
        final Map<UUID, PlayerTrackerMapElement<?>> updatedMap = new HashMap<UUID, PlayerTrackerMapElement<?>>();
        boolean hasNewPlayer = false;
        for (final IPlayerTrackerSystem<?> system : this.systemManager.getSystems()) {
            hasNewPlayer = (this.updateForSystem(system, updatedMap, this.elements) || hasNewPlayer);
        }
        if (hasNewPlayer || updatedMap.size() != this.elements.size()) {
            this.elements = updatedMap;
            this.onElementsChange.run();
        }
    }
    
    private <P> boolean updateForSystem(final IPlayerTrackerSystem<P> system, final Map<UUID, PlayerTrackerMapElement<?>> destination, final Map<UUID, PlayerTrackerMapElement<?>> current) {
        final Iterator<P> playerIterator = system.getTrackedPlayerIterator();
        if (playerIterator == null) {
            return false;
        }
        final ITrackedPlayerReader<P> reader = system.getReader();
        boolean hasNewPlayer = false;
        while (playerIterator.hasNext()) {
            final P player = playerIterator.next();
            final UUID playerId = reader.getId(player);
            PlayerTrackerMapElement<?> element = current.get(playerId);
            if (destination.containsKey(playerId)) {
                continue;
            }
            if (element == null || element.getPlayer() != player) {
                element = (PlayerTrackerMapElement<?>)new PlayerTrackerMapElement((Object)player, (IPlayerTrackerSystem)system);
                hasNewPlayer = true;
            }
            destination.put(element.getPlayerId(), element);
        }
        return hasNewPlayer;
    }
    
    public boolean playerExists(final UUID id) {
        return this.elements != null && this.elements.containsKey(id);
    }
    
    public Iterable<PlayerTrackerMapElement<?>> getElements() {
        return this.elements.values();
    }
    
    public void resetRenderedOnRadarFlags() {
        for (final PlayerTrackerMapElement<?> e : this.elements.values()) {
            e.setRenderedOnRadar(false);
        }
    }
    
    public void confirmPlayerRadarRender(final EntityPlayer p) {
        this.elements.get(p.func_110124_au()).setRenderedOnRadar(true);
    }
}
