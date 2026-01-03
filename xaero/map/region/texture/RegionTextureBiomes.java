//Decompiled by Procyon!

package xaero.map.region.texture;

import xaero.map.palette.*;

public class RegionTextureBiomes
{
    protected final Paletted2DFastBitArrayIntStorage biomeIndexStorage;
    
    public RegionTextureBiomes(final Paletted2DFastBitArrayIntStorage biomeIndexStorage) {
        this.biomeIndexStorage = biomeIndexStorage;
    }
    
    public Paletted2DFastBitArrayIntStorage getBiomeIndexStorage() {
        return this.biomeIndexStorage;
    }
}
