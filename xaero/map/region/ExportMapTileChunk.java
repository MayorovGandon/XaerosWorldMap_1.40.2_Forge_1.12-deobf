//Decompiled by Procyon!

package xaero.map.region;

import xaero.map.region.texture.*;

public class ExportMapTileChunk extends MapTileChunk
{
    public ExportMapTileChunk(final MapRegion r, final int x, final int z) {
        super(r, x, z);
    }
    
    @Override
    protected LeafRegionTexture createLeafTexture() {
        return new ExportLeafRegionTexture(this);
    }
    
    @Override
    public ExportLeafRegionTexture getLeafTexture() {
        return (ExportLeafRegionTexture)super.getLeafTexture();
    }
}
