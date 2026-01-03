//Decompiled by Procyon!

package xaero.map.highlight;

import xaero.map.world.*;
import it.unimi.dsi.fastutil.longs.*;
import xaero.map.region.*;
import java.util.function.*;

public class RegionHighlightExistenceTracker
{
    private final MapDimension mapDimension;
    private final int caveLayer;
    private final LongSet regionsToTrackExistenceOf;
    
    public RegionHighlightExistenceTracker(final MapDimension mapDimension, final int caveLayer) {
        this.mapDimension = mapDimension;
        this.caveLayer = caveLayer;
        this.regionsToTrackExistenceOf = (LongSet)new LongOpenHashSet();
    }
    
    private void requestBranchUpdates(final int regionX, final int regionZ) {
        for (int i = 1; i <= 3; ++i) {
            final int leveledRegionX = regionX >> i;
            final int leveledRegionZ = regionZ >> i;
            final LeveledRegion<?> leveledParent = this.mapDimension.getLayeredMapRegions().get(this.caveLayer, leveledRegionX, leveledRegionZ, i);
            if (leveledParent != null) {
                ((BranchLeveledRegion)leveledParent).setShouldCheckForUpdatesRecursive(true);
                break;
            }
        }
    }
    
    public void onClearCachedHash(final int regionX, final int regionZ) {
        final long key = DimensionHighlighterHandler.getKey(regionX, regionZ);
        if (this.regionsToTrackExistenceOf.remove(key)) {
            this.requestBranchUpdates(regionX, regionZ);
        }
    }
    
    public void onClearCachedHashes() {
        this.regionsToTrackExistenceOf.forEach((Consumer)new Consumer<Long>() {
            @Override
            public void accept(final Long key) {
                RegionHighlightExistenceTracker.this.requestBranchUpdates(DimensionHighlighterHandler.getXFromKey((long)key), DimensionHighlighterHandler.getZFromKey((long)key));
            }
        });
        this.regionsToTrackExistenceOf.clear();
    }
    
    public void track(final int regionX, final int regionZ) {
        this.regionsToTrackExistenceOf.add(DimensionHighlighterHandler.getKey(regionX, regionZ));
    }
    
    public void stopTracking(final int regionX, final int regionZ) {
        this.regionsToTrackExistenceOf.remove(DimensionHighlighterHandler.getKey(regionX, regionZ));
    }
}
