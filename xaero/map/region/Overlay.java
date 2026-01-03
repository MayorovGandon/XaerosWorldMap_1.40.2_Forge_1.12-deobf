//Decompiled by Procyon!

package xaero.map.region;

import net.minecraft.world.*;
import xaero.map.world.*;
import net.minecraft.util.math.*;
import xaero.map.biome.*;
import xaero.map.*;
import java.util.*;
import xaero.map.cache.*;

public class Overlay extends MapPixel
{
    private byte opacity;
    
    public Overlay(final int state, final int colourType, final int customColour, final byte light, final boolean glowing) {
        this.write(state, colourType, customColour, light, glowing);
    }
    
    public void write(final int state, final int colourType, final int customColour, final byte light, final boolean glowing) {
        this.opacity = 0;
        this.state = state;
        this.colourType = (byte)colourType;
        this.customColour = customColour;
        this.light = light;
        this.glowing = glowing;
    }
    
    public boolean isWater() {
        final int id = this.state & 0xFFF;
        return (this.state & 0xFFFF0000) == 0x0 && (id == 9 || id == 8);
    }
    
    public int getParametres() {
        int parametres = 0;
        parametres |= (this.isWater() ? 0 : 1);
        parametres |= this.light << 4;
        final int compatibleColourType = (this.colourType >= 2) ? (this.colourType - 1) : this.colourType;
        final int colourTypeToWrite = (compatibleColourType < 0) ? 0 : (compatibleColourType & 0x3);
        parametres |= colourTypeToWrite << 8;
        parametres |= (this.opacity & 0xF) << 11;
        return parametres;
    }
    
    public void getPixelColour(final MapBlock block, final int[] result_dest, final MapWriter mapWriter, final World world, final MapDimension dim, final MapTileChunk tileChunk, final MapTileChunk prevChunk, final MapTileChunk prevChunkDiagonal, final MapTileChunk prevChunkHorisontal, final MapTile mapTile, final int x, final int z, final int caveStart, final int caveDepth, final BlockPos.MutableBlockPos mutableGlobalPos, final float shadowR, final float shadowG, final float shadowB, final BiomeColorCalculator biomeColorCalculator, final MapProcessor mapProcessor, final OverlayManager overlayManager, final MapUpdateFastConfig config) {
        super.getPixelColours(result_dest, mapWriter, world, dim, tileChunk, prevChunk, prevChunkDiagonal, prevChunkHorisontal, mapTile, x, z, block, -1, -1, caveStart, caveDepth, (ArrayList)null, mutableGlobalPos, shadowR, shadowG, shadowB, biomeColorCalculator, mapProcessor, overlayManager, (BlockStateShortShapeCache)null, config);
    }
    
    public String toString() {
        return "(S: " + this.getState() + ", CT: " + this.colourType + ", CC: " + this.getCustomColour() + ", O: " + this.opacity + ", L: " + this.light + ")";
    }
    
    public boolean equals(final Overlay p) {
        return p != null && this.opacity == p.opacity && this.light == p.light && this.getState() == p.getState();
    }
    
    void fillManagerKeyHolder(final Object[] keyHolder, final int colourType, final int customColour) {
        keyHolder[0] = this.state;
        keyHolder[1] = colourType;
        keyHolder[2] = customColour;
        keyHolder[3] = this.light;
        keyHolder[4] = this.opacity;
    }
    
    public void increaseOpacity(int toAdd) {
        if (toAdd > 15) {
            toAdd = 15;
        }
        this.opacity += (byte)toAdd;
        if (this.opacity > 15) {
            this.opacity = 15;
        }
    }
    
    public int getOpacity() {
        return this.opacity;
    }
}
