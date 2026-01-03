//Decompiled by Procyon!

package xaero.map.region.texture;

import xaero.map.region.*;
import xaero.map.highlight.*;
import xaero.map.pool.buffer.*;

public class ExportLeafRegionTexture extends LeafRegionTexture
{
    public ExportLeafRegionTexture(final MapTileChunk tileChunk) {
        super(tileChunk);
    }
    
    public void applyHighlights(final DimensionHighlighterHandler highlighterHandler, final PoolTextureDirectBufferUnit colorBuffer) {
        super.applyHighlights(highlighterHandler, colorBuffer, false);
    }
}
