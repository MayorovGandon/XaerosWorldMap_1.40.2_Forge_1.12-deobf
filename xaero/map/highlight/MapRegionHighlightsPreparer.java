//Decompiled by Procyon!

package xaero.map.highlight;

import net.minecraft.client.*;
import xaero.map.region.*;

public class MapRegionHighlightsPreparer
{
    public void prepare(final MapRegion region, final boolean tileChunkDiscoveryKnown) {
        if (!Minecraft.func_71410_x().func_152345_ab()) {
            throw new RuntimeException(new IllegalAccessException());
        }
        region.updateTargetHighlightsHash();
        final DimensionHighlighterHandler highlighterHandler = region.getDim().getHighlightHandler();
        final boolean regionHasHighlights = highlighterHandler.shouldApplyRegionHighlights(region.getRegionX(), region.getRegionZ(), region.hasHadTerrain());
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                this.prepare(region, i, j, regionHasHighlights, tileChunkDiscoveryKnown);
            }
        }
    }
    
    private void prepare(final MapRegion region, final int x, final int z, final boolean regionHasHighlights, final boolean tileChunkDiscoveryKnown) {
        final DimensionHighlighterHandler highlighterHandler = region.getDim().getHighlightHandler();
        MapTileChunk tileChunk = region.getChunk(x, z);
        final boolean tileChunkHasHighlights = regionHasHighlights && highlighterHandler.shouldApplyTileChunkHighlights(region.getRegionX(), region.getRegionZ(), x, z, !tileChunkDiscoveryKnown || tileChunk != null);
        final boolean tileChunkHasHighlightsUndiscovered = regionHasHighlights && highlighterHandler.shouldApplyTileChunkHighlights(region.getRegionX(), region.getRegionZ(), x, z, false);
        if (tileChunk == null) {
            if (!tileChunkHasHighlights) {
                return;
            }
            tileChunk = region.createTexture(x, z).getTileChunk();
        }
        tileChunk.setHasHighlights(tileChunkHasHighlights);
        tileChunk.setHasHighlightsIfUndiscovered(tileChunkHasHighlightsUndiscovered);
    }
    
    public void prepare(final MapRegion region, final int x, final int z, final boolean tileChunkDiscoveryKnown) {
        if (!Minecraft.func_71410_x().func_152345_ab()) {
            throw new RuntimeException(new IllegalAccessException());
        }
        final DimensionHighlighterHandler highlighterHandler = region.getDim().getHighlightHandler();
        this.prepare(region, x, z, highlighterHandler.shouldApplyRegionHighlights(region.getRegionX(), region.getRegionZ(), region.hasHadTerrain()), tileChunkDiscoveryKnown);
    }
}
