//Decompiled by Procyon!

package xaero.map.region;

import xaero.map.world.*;
import xaero.map.highlight.*;
import java.util.*;
import xaero.map.file.*;
import xaero.map.util.linked.*;

public class MapLayer
{
    private final MapDimension mapDimension;
    private final LeveledRegionManager mapRegions;
    private final RegionHighlightExistenceTracker regionHighlightExistenceTracker;
    private final Hashtable<Integer, Hashtable<Integer, RegionDetection>> detectedRegions;
    private final Hashtable<Integer, Hashtable<Integer, RegionDetection>> completeDetectedRegions;
    private final LinkedChain<RegionDetection> completeDetectedRegionsLinked;
    private int caveStart;
    
    public MapLayer(final MapDimension mapDimension, final RegionHighlightExistenceTracker regionHighlightExistenceTracker) {
        this.mapDimension = mapDimension;
        this.mapRegions = new LeveledRegionManager();
        this.regionHighlightExistenceTracker = regionHighlightExistenceTracker;
        this.detectedRegions = new Hashtable<Integer, Hashtable<Integer, RegionDetection>>();
        this.completeDetectedRegions = new Hashtable<Integer, Hashtable<Integer, RegionDetection>>();
        this.completeDetectedRegionsLinked = new LinkedChain<RegionDetection>();
    }
    
    public boolean regionDetectionExists(final int x, final int z) {
        return this.getRegionDetection(x, z) != null;
    }
    
    public void addRegionDetection(final RegionDetection regionDetection) {
        synchronized (this.detectedRegions) {
            Hashtable<Integer, RegionDetection> column = this.detectedRegions.get(regionDetection.getRegionX());
            if (column == null) {
                this.detectedRegions.put(regionDetection.getRegionX(), column = new Hashtable<Integer, RegionDetection>());
            }
            column.put(regionDetection.getRegionZ(), regionDetection);
            this.tryAddingToCompleteRegionDetection(regionDetection);
        }
    }
    
    public RegionDetection getCompleteRegionDetection(final int x, final int z) {
        if (this.mapDimension.isUsingWorldSave()) {
            return this.mapDimension.getWorldSaveRegionDetection(x, z);
        }
        final Hashtable<Integer, RegionDetection> column = this.completeDetectedRegions.get(x);
        if (column != null) {
            return column.get(z);
        }
        return null;
    }
    
    private boolean completeRegionDetectionContains(final RegionDetection regionDetection) {
        return this.getCompleteRegionDetection(regionDetection.getRegionX(), regionDetection.getRegionZ()) != null;
    }
    
    public void tryAddingToCompleteRegionDetection(final RegionDetection regionDetection) {
        if (this.completeRegionDetectionContains(regionDetection)) {
            return;
        }
        if (this.mapDimension.isUsingWorldSave()) {
            this.mapDimension.addWorldSaveRegionDetection(regionDetection);
            return;
        }
        synchronized (this.completeDetectedRegions) {
            Hashtable<Integer, RegionDetection> column = this.completeDetectedRegions.get(regionDetection.getRegionX());
            if (column == null) {
                this.completeDetectedRegions.put(regionDetection.getRegionX(), column = new Hashtable<Integer, RegionDetection>());
            }
            column.put(regionDetection.getRegionZ(), regionDetection);
            this.completeDetectedRegionsLinked.add(regionDetection);
        }
    }
    
    public RegionDetection getRegionDetection(final int x, final int z) {
        final Hashtable<Integer, RegionDetection> column = this.detectedRegions.get(x);
        RegionDetection result = null;
        if (column != null) {
            result = column.get(z);
        }
        if (result == null) {
            final RegionDetection worldSaveDetection = this.mapDimension.getWorldSaveRegionDetection(x, z);
            if (worldSaveDetection != null) {
                result = new RegionDetection(worldSaveDetection.getWorldId(), worldSaveDetection.getDimId(), worldSaveDetection.getMwId(), worldSaveDetection.getRegionX(), worldSaveDetection.getRegionZ(), worldSaveDetection.getRegionFile(), worldSaveDetection.getInitialVersion(), worldSaveDetection.isHasHadTerrain());
                this.addRegionDetection(result);
                return result;
            }
        }
        else if (result.isRemoved()) {
            return null;
        }
        return result;
    }
    
    public void removeRegionDetection(final int x, final int z) {
        if (this.mapDimension.getWorldSaveRegionDetection(x, z) != null) {
            final RegionDetection regionDetection = this.getRegionDetection(x, z);
            if (regionDetection != null) {
                regionDetection.setRemoved(true);
            }
            return;
        }
        synchronized (this.detectedRegions) {
            final Hashtable<Integer, RegionDetection> column = this.detectedRegions.get(x);
            if (column != null) {
                column.remove(z);
                if (column.isEmpty()) {
                    this.detectedRegions.remove(x);
                }
            }
        }
    }
    
    public RegionHighlightExistenceTracker getRegionHighlightExistenceTracker() {
        return this.regionHighlightExistenceTracker;
    }
    
    public LeveledRegionManager getMapRegions() {
        return this.mapRegions;
    }
    
    public Hashtable<Integer, Hashtable<Integer, RegionDetection>> getDetectedRegions() {
        return this.detectedRegions;
    }
    
    public Iterable<RegionDetection> getLinkedCompleteWorldSaveDetectedRegions() {
        return this.mapDimension.isUsingWorldSave() ? this.mapDimension.getLinkedWorldSaveDetectedRegions() : this.completeDetectedRegionsLinked;
    }
    
    public void preDetection() {
        this.detectedRegions.clear();
        this.completeDetectedRegions.clear();
        this.completeDetectedRegionsLinked.reset();
    }
    
    public int getCaveStart() {
        return this.caveStart;
    }
    
    public void setCaveStart(final int caveStart) {
        this.caveStart = caveStart;
    }
}
