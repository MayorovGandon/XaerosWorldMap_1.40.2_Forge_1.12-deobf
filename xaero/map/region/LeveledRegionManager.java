//Decompiled by Procyon!

package xaero.map.region;

import java.util.*;

public class LeveledRegionManager
{
    public static final int MAX_LEVEL = 3;
    private HashMap<Integer, HashMap<Integer, LeveledRegion<?>>> regionTextureMap;
    
    public LeveledRegionManager() {
        this.regionTextureMap = new HashMap<Integer, HashMap<Integer, LeveledRegion<?>>>();
    }
    
    public void putLeaf(final int X, final int Z, final MapRegion leaf) {
        final int maxLevelX = X >> 3;
        final int maxLevelZ = Z >> 3;
        HashMap<Integer, LeveledRegion<?>> column;
        synchronized (this.regionTextureMap) {
            column = this.regionTextureMap.get(maxLevelX);
            if (column == null) {
                column = new HashMap<Integer, LeveledRegion<?>>();
                this.regionTextureMap.put(maxLevelX, column);
            }
        }
        LeveledRegion<?> rootBranch;
        synchronized (column) {
            rootBranch = column.get(maxLevelZ);
            if (rootBranch == null) {
                rootBranch = (LeveledRegion<?>)new BranchLeveledRegion(leaf.getWorldId(), leaf.getDimId(), leaf.getMwId(), leaf.getDim(), 3, maxLevelX, maxLevelZ, leaf.caveLayer, (BranchLeveledRegion)null);
                column.put(maxLevelZ, rootBranch);
                leaf.getDim().getLayeredMapRegions().addListRegion((LeveledRegion)rootBranch);
            }
        }
        if (!(rootBranch instanceof MapRegion)) {
            rootBranch.putLeaf(X, Z, leaf);
        }
    }
    
    public MapRegion getLeaf(final int X, final int Z) {
        return (MapRegion)this.get(X, Z, 0);
    }
    
    public LeveledRegion<?> get(final int leveledX, final int leveledZ, final int level) {
        if (level > 3) {
            throw new RuntimeException(new IllegalArgumentException());
        }
        final int maxLevelX = leveledX >> 3 - level;
        final int maxLevelZ = leveledZ >> 3 - level;
        final HashMap<Integer, LeveledRegion<?>> column;
        synchronized (this.regionTextureMap) {
            column = this.regionTextureMap.get(maxLevelX);
        }
        if (column == null) {
            return null;
        }
        final LeveledRegion<?> rootBranch;
        synchronized (column) {
            rootBranch = column.get(maxLevelZ);
        }
        if (rootBranch == null) {
            return null;
        }
        if (level == 3) {
            return rootBranch;
        }
        return (LeveledRegion<?>)rootBranch.get(leveledX, leveledZ, level);
    }
    
    public boolean remove(final int leveledX, final int leveledZ, final int level) {
        if (level > 3) {
            throw new RuntimeException(new IllegalArgumentException());
        }
        final int maxLevelX = leveledX >> 3 - level;
        final int maxLevelZ = leveledZ >> 3 - level;
        final HashMap<Integer, LeveledRegion<?>> column;
        synchronized (this.regionTextureMap) {
            column = this.regionTextureMap.get(maxLevelX);
        }
        if (column == null) {
            return false;
        }
        final LeveledRegion<?> rootBranch;
        synchronized (column) {
            rootBranch = column.get(maxLevelZ);
        }
        if (rootBranch == null) {
            return false;
        }
        if (!(rootBranch instanceof MapRegion)) {
            return rootBranch.remove(leveledX, leveledZ, level);
        }
        synchronized (column) {
            column.remove(maxLevelZ);
        }
        return true;
    }
    
    @Deprecated
    public void bumpLoadedRegion(final MapRegion region) {
        region.getDim().getLayeredMapRegions().bumpLoadedRegion(region);
    }
}
