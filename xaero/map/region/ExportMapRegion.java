//Decompiled by Procyon!

package xaero.map.region;

import xaero.map.world.*;
import xaero.map.region.texture.*;

public class ExportMapRegion extends MapRegion
{
    public ExportMapRegion(final MapDimension dim, final int x, final int z, final int caveLayer) {
        super("png", "null", null, dim, x, z, caveLayer, 0, false);
    }
    
    @Override
    protected MapTileChunk createTileChunk(final int x, final int y) {
        return new ExportMapTileChunk(this, this.regionX * 8 + x, this.regionZ * 8 + y);
    }
    
    @Override
    public ExportLeafRegionTexture getTexture(final int x, final int y) {
        return (ExportLeafRegionTexture)super.getTexture(x, y);
    }
    
    @Override
    public ExportMapTileChunk getChunk(final int x, final int z) {
        return (ExportMapTileChunk)super.getChunk(x, z);
    }
}
