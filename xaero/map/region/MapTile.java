//Decompiled by Procyon!

package xaero.map.region;

import xaero.map.pool.*;

public class MapTile implements PoolUnit
{
    public static final int CURRENT_WORLD_INTERPRETATION_VERSION = 1;
    private boolean loaded;
    private byte signed_worldInterpretationVersion;
    private int chunkX;
    private int chunkZ;
    private MapBlock[][] blocks;
    private boolean writtenOnce;
    private int writtenCaveStart;
    private byte writtenCaveDepth;
    
    public MapTile(final Object... args) {
        this.blocks = new MapBlock[16][16];
        this.create(args);
    }
    
    public void create(final Object... args) {
        this.chunkX = (int)args[1];
        this.chunkZ = (int)args[2];
        this.loaded = false;
        this.signed_worldInterpretationVersion = 0;
        this.writtenOnce = false;
        this.writtenCaveStart = Integer.MAX_VALUE;
        this.writtenCaveDepth = 0;
    }
    
    public boolean isLoaded() {
        return this.loaded;
    }
    
    public void setLoaded(final boolean loaded) {
        this.loaded = loaded;
    }
    
    public MapBlock getBlock(final int x, final int z) {
        return this.blocks[x][z];
    }
    
    public MapBlock[] getBlockColumn(final int x) {
        return this.blocks[x];
    }
    
    public void setBlock(final int x, final int z, final MapBlock block) {
        this.blocks[x][z] = block;
    }
    
    public int getChunkX() {
        return this.chunkX;
    }
    
    public int getChunkZ() {
        return this.chunkZ;
    }
    
    public boolean wasWrittenOnce() {
        return this.writtenOnce;
    }
    
    public void setWrittenOnce(final boolean writtenOnce) {
        this.writtenOnce = writtenOnce;
    }
    
    public int getWorldInterpretationVersion() {
        return this.signed_worldInterpretationVersion & 0xFF;
    }
    
    public void setWorldInterpretationVersion(final int version) {
        this.signed_worldInterpretationVersion = (byte)version;
    }
    
    public int getWrittenCaveStart() {
        return this.writtenCaveStart;
    }
    
    public void setWrittenCave(final int writtenCaveStart, final int writtenCaveDepth) {
        this.writtenCaveStart = writtenCaveStart;
        this.writtenCaveDepth = (byte)writtenCaveDepth;
    }
    
    public int getWrittenCaveDepth() {
        return this.writtenCaveDepth & 0xFF;
    }
}
