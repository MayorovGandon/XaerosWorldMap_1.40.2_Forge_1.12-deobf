//Decompiled by Procyon!

package xaero.map.pool;

import xaero.map.region.*;

public class MapTilePool extends MapPool<MapTile>
{
    public MapTilePool() {
        super(2048);
    }
    
    protected MapTile construct(final Object... args) {
        return new MapTile(args);
    }
    
    public MapTile get(final String dimension, final int chunkX, final int chunkZ) {
        return (MapTile)super.get(new Object[] { dimension, chunkX, chunkZ });
    }
}
