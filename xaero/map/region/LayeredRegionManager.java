//Decompiled by Procyon!

package xaero.map.region;

import xaero.map.world.*;
import it.unimi.dsi.fastutil.ints.*;
import xaero.map.highlight.*;
import it.unimi.dsi.fastutil.objects.*;
import java.util.function.*;
import java.util.*;

public class LayeredRegionManager
{
    private final MapDimension mapDimension;
    private final Int2ObjectMap<MapLayer> mapLayers;
    private Set<LeveledRegion<?>> regionsListAll;
    private List<LeveledRegion<?>> regionsListLoaded;
    
    public LayeredRegionManager(final MapDimension mapDimension) {
        this.mapDimension = mapDimension;
        this.mapLayers = (Int2ObjectMap<MapLayer>)new Int2ObjectOpenHashMap();
        this.regionsListAll = new HashSet<LeveledRegion<?>>();
        this.regionsListLoaded = new ArrayList<LeveledRegion<?>>();
    }
    
    public void putLeaf(final int X, final int Z, final MapRegion leaf) {
        this.getLayer(leaf.caveLayer).getMapRegions().putLeaf(X, Z, leaf);
    }
    
    public MapRegion getLeaf(final int caveLayer, final int X, final int Z) {
        return this.getLayer(caveLayer).getMapRegions().getLeaf(X, Z);
    }
    
    public LeveledRegion<?> get(final int caveLayer, final int leveledX, final int leveledZ, final int level) {
        return this.getLayer(caveLayer).getMapRegions().get(leveledX, leveledZ, level);
    }
    
    public boolean remove(final int caveLayer, final int leveledX, final int leveledZ, final int level) {
        return this.getLayer(caveLayer).getMapRegions().remove(leveledX, leveledZ, level);
    }
    
    public MapLayer getLayer(final int caveLayer) {
        MapLayer mapLayer;
        synchronized (this.mapLayers) {
            mapLayer = (MapLayer)this.mapLayers.get(caveLayer);
            if (mapLayer == null) {
                this.mapLayers.put(caveLayer, (Object)(mapLayer = new MapLayer(this.mapDimension, new RegionHighlightExistenceTracker(this.mapDimension, caveLayer))));
            }
        }
        return mapLayer;
    }
    
    public void clear() {
        synchronized (this.mapLayers) {
            this.mapLayers.clear();
        }
        synchronized (this.regionsListAll) {
            this.regionsListAll.clear();
        }
        synchronized (this.regionsListLoaded) {
            this.regionsListLoaded.clear();
        }
    }
    
    public int loadedCount() {
        return this.regionsListLoaded.size();
    }
    
    public void removeListRegion(final LeveledRegion<?> reg) {
        synchronized (this.regionsListAll) {
            this.regionsListAll.remove(reg);
        }
    }
    
    public void addListRegion(final LeveledRegion<?> reg) {
        synchronized (this.regionsListAll) {
            this.regionsListAll.add(reg);
        }
    }
    
    public void bumpLoadedRegion(final MapRegion reg) {
        this.bumpLoadedRegion((LeveledRegion<?>)reg);
    }
    
    public void bumpLoadedRegion(final LeveledRegion<?> reg) {
        synchronized (this.regionsListLoaded) {
            if (this.regionsListLoaded.remove(reg)) {
                this.regionsListLoaded.add(reg);
            }
        }
    }
    
    public List<LeveledRegion<?>> getLoadedListUnsynced() {
        return this.regionsListLoaded;
    }
    
    public LeveledRegion<?> getLoadedRegion(final int index) {
        synchronized (this.regionsListLoaded) {
            return this.regionsListLoaded.get(index);
        }
    }
    
    public void addLoadedRegion(final LeveledRegion<?> reg) {
        synchronized (this.regionsListLoaded) {
            this.regionsListLoaded.add(reg);
        }
    }
    
    public void removeLoadedRegion(final LeveledRegion<?> reg) {
        synchronized (this.regionsListLoaded) {
            this.regionsListLoaded.remove(reg);
        }
    }
    
    public int size() {
        return this.regionsListAll.size();
    }
    
    public Set<LeveledRegion<?>> getUnsyncedSet() {
        return this.regionsListAll;
    }
    
    public void onClearCachedHighlightHash(final int regionX, final int regionZ) {
        synchronized (this.mapLayers) {
            for (final MapLayer layer : this.mapLayers.values()) {
                layer.getRegionHighlightExistenceTracker().onClearCachedHash(regionX, regionZ);
            }
        }
    }
    
    public void onClearCachedHighlightHashes() {
        synchronized (this.mapLayers) {
            for (final MapLayer layer : this.mapLayers.values()) {
                layer.getRegionHighlightExistenceTracker().onClearCachedHashes();
            }
        }
    }
    
    public void applyToEachLoadedLayer(final BiConsumer<Integer, MapLayer> consumer) {
        synchronized (this.mapLayers) {
            for (final Map.Entry<Integer, MapLayer> layerEntry : this.mapLayers.entrySet()) {
                consumer.accept(layerEntry.getKey(), layerEntry.getValue());
            }
        }
    }
    
    public void preDetection() {
        synchronized (this.mapLayers) {
            for (final MapLayer layer : this.mapLayers.values()) {
                layer.preDetection();
            }
        }
    }
}
