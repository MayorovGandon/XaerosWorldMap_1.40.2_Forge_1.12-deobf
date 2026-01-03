//Decompiled by Procyon!

package xaero.map.region;

import java.util.*;
import net.minecraft.world.*;
import xaero.map.world.*;
import net.minecraft.util.math.*;
import xaero.map.biome.*;
import xaero.map.*;
import xaero.map.cache.*;
import net.minecraft.block.*;

public class MapBlock extends MapPixel
{
    protected boolean slopeUnknown;
    private byte verticalSlope;
    private byte diagonalSlope;
    private byte signed_height;
    private byte signed_topHeight;
    private ArrayList<Overlay> overlays;
    private int biome;
    
    public MapBlock() {
        this.slopeUnknown = true;
        this.biome = -1;
    }
    
    public boolean isGrass() {
        return (this.state & 0xFFFF0000) == 0x0 && (this.state & 0xFFF) == 0x2;
    }
    
    public int getParametres() {
        int parametres = 0;
        final int colourTypeToWrite = (this.colourType < 0) ? 0 : (this.colourType & 0x3);
        parametres |= (this.isGrass() ? 0 : 1);
        parametres |= ((this.getNumberOfOverlays() != 0) ? 2 : 0);
        parametres |= colourTypeToWrite << 2;
        parametres |= this.light << 8;
        parametres |= this.getHeight() << 12;
        parametres |= ((this.biome != -1) ? 1048576 : 0);
        parametres |= ((this.signed_height != this.signed_topHeight) ? 16777216 : 0);
        return parametres;
    }
    
    public void getPixelColour(final int[] result_dest, final MapWriter mapWriter, final World world, final MapDimension dim, final MapTileChunk tileChunk, final MapTileChunk prevChunk, final MapTileChunk prevChunkDiagonal, final MapTileChunk prevChunkHorisontal, final MapTile mapTile, final int x, final int z, final int caveStart, final int caveDepth, final BlockPos.MutableBlockPos mutableGlobalPos, final float shadowR, final float shadowG, final float shadowB, final BiomeColorCalculator biomeColorCalculator, final MapProcessor mapProcessor, final OverlayManager overlayManager, final int effectiveHeight, final int effectiveTopHeight, final BlockStateShortShapeCache blockStateShortShapeCache, final MapUpdateFastConfig config) {
        super.getPixelColours(result_dest, mapWriter, world, dim, tileChunk, prevChunk, prevChunkDiagonal, prevChunkHorisontal, mapTile, x, z, this, effectiveHeight, effectiveTopHeight, caveStart, caveDepth, this.overlays, mutableGlobalPos, shadowR, shadowG, shadowB, biomeColorCalculator, mapProcessor, overlayManager, blockStateShortShapeCache, config);
    }
    
    @Override
    public String toString() {
        return "ID: " + Block.field_176229_d.func_148747_b((Object)Block.func_176220_d(this.getState())) + ", S: " + this.getState() + ", VS: " + this.verticalSlope + ", DS: " + this.diagonalSlope + ", SU: " + this.slopeUnknown + ", H: " + this.getHeight() + ", CT: " + this.colourType + ", B: " + this.biome + ", CC: " + this.customColour + ", L: " + this.light + ", G: " + this.glowing + ", O: " + this.getNumberOfOverlays();
    }
    
    public boolean equalsSlopesExcluded(final MapBlock p) {
        final boolean equal = p != null && this.state == p.state && this.colourType == p.colourType && this.light == p.light && this.signed_height == p.signed_height && this.signed_topHeight == p.signed_topHeight && this.getNumberOfOverlays() == p.getNumberOfOverlays() && this.biome == p.biome && (this.colourType != 3 || this.customColour == p.customColour);
        if (equal && this.getNumberOfOverlays() != 0) {
            for (int i = 0; i < this.overlays.size(); ++i) {
                if (!this.overlays.get(i).equals(p.overlays.get(i))) {
                    return false;
                }
            }
        }
        return equal;
    }
    
    public boolean equals(final MapBlock p, final boolean equalsSlopesExcluded) {
        return p != null && this.verticalSlope == p.verticalSlope && this.diagonalSlope == p.diagonalSlope && this.slopeUnknown == p.slopeUnknown && equalsSlopesExcluded;
    }
    
    public void fixHeightType(final int x, final int z, final MapTile mapTile, final MapTileChunk tileChunk, final MapTileChunk prevChunk, final MapTileChunk prevChunkDiagonal, final MapTileChunk prevChunkHorisontal, final int height, final boolean useSourceData, final BlockStateShortShapeCache blockStateShortShapeCache, final MapUpdateFastConfig updateConfig) {
        int prevHeight = -1;
        int prevHeightDiagonal = -1;
        if (useSourceData && z > 0) {
            prevHeight = mapTile.getBlock(x, z - 1).getEffectiveHeight(blockStateShortShapeCache, updateConfig);
            if (x > 0) {
                prevHeightDiagonal = mapTile.getBlock(x - 1, z - 1).getEffectiveHeight(blockStateShortShapeCache, updateConfig);
            }
        }
        if (prevHeight == -1 || prevHeightDiagonal == -1) {
            final int inTileChunkX = ((mapTile.getChunkX() & 0x3) << 4) + x;
            final int inTileChunkZ = ((mapTile.getChunkZ() & 0x3) << 4) + z;
            int inTileChunkXPrev = inTileChunkX - 1;
            int inTileChunkZPrev = inTileChunkZ - 1;
            MapTileChunk verticalSlopeSrc = tileChunk;
            MapTileChunk diagonalSlopeSrc = tileChunk;
            final boolean verticalEdge = inTileChunkZPrev < 0;
            final boolean horisontalEdge = inTileChunkXPrev < 0;
            if (verticalEdge) {
                diagonalSlopeSrc = prevChunk;
                verticalSlopeSrc = prevChunk;
                inTileChunkZPrev = 63;
            }
            if (horisontalEdge) {
                inTileChunkXPrev = 63;
                diagonalSlopeSrc = (verticalEdge ? prevChunkDiagonal : prevChunkHorisontal);
            }
            if (prevHeight == -1 && verticalSlopeSrc != null && verticalSlopeSrc.getLoadState() >= 2) {
                prevHeight = verticalSlopeSrc.getLeafTexture().getHeight(inTileChunkX, inTileChunkZPrev);
            }
            if (prevHeightDiagonal == -1 && diagonalSlopeSrc != null && diagonalSlopeSrc.getLoadState() >= 2) {
                prevHeightDiagonal = diagonalSlopeSrc.getLeafTexture().getHeight(inTileChunkXPrev, inTileChunkZPrev);
            }
            if (prevHeight == -1 || prevHeightDiagonal == -1) {
                if (useSourceData) {
                    return;
                }
                final int reX = (x < 15) ? (x + 1) : x;
                final int reZ = (z < 15) ? (z + 1) : z;
                if (reX == x && reZ == z) {
                    this.verticalSlope = 0;
                    this.diagonalSlope = 0;
                    this.slopeUnknown = false;
                    return;
                }
                final int inTileChunkReX = ((mapTile.getChunkX() & 0x3) << 4) + reX;
                final int inTileChunkReZ = ((mapTile.getChunkZ() & 0x3) << 4) + reZ;
                final int reHeight = tileChunk.getLeafTexture().getHeight(inTileChunkReX, inTileChunkReZ);
                if (reHeight != -1) {
                    this.fixHeightType(reX, reZ, mapTile, tileChunk, prevChunk, prevChunkDiagonal, prevChunkHorisontal, reHeight, useSourceData, blockStateShortShapeCache, updateConfig);
                }
                return;
            }
        }
        this.verticalSlope = (byte)Math.max(-128, Math.min(127, height - prevHeight));
        this.diagonalSlope = (byte)Math.max(-128, Math.min(127, height - prevHeightDiagonal));
        this.slopeUnknown = false;
    }
    
    public void prepareForWriting() {
        if (this.overlays != null) {
            this.overlays.clear();
        }
        this.customColour = 0;
        this.colourType = 0;
        this.biome = -1;
        this.state = 0;
        this.slopeUnknown = true;
        this.light = 0;
        this.glowing = false;
        this.signed_height = 0;
        this.signed_topHeight = 0;
    }
    
    public void write(final int state, final int height, final int topHeight, final int[] biomeStuff, final byte light, final boolean glowing, final boolean cave) {
        this.state = state;
        this.setHeight(height);
        this.setTopHeight(topHeight);
        this.setColourType((byte)biomeStuff[0]);
        if (biomeStuff[1] != -1) {
            this.biome = biomeStuff[1];
        }
        this.setCustomColour(biomeStuff[2]);
        this.light = light;
        this.glowing = glowing;
        if (this.overlays != null && this.overlays.isEmpty()) {
            this.overlays = null;
        }
    }
    
    public void addOverlay(final Overlay o) {
        if (this.overlays == null) {
            this.overlays = new ArrayList<Overlay>();
        }
        this.overlays.add(o);
    }
    
    public int getHeight() {
        return this.signed_height & 0xFF;
    }
    
    public byte getSignedHeight() {
        return this.signed_height;
    }
    
    public void setHeight(final int h) {
        this.signed_height = (byte)h;
    }
    
    public int getTopHeight() {
        return this.signed_topHeight & 0xFF;
    }
    
    public byte getSignedTopHeight() {
        return this.signed_topHeight;
    }
    
    public void setTopHeight(final int h) {
        this.signed_topHeight = (byte)h;
    }
    
    public int getEffectiveHeight(final BlockStateShortShapeCache blockStateShortShapeCache, final MapUpdateFastConfig updateConfig) {
        return this.getEffectiveHeight(updateConfig.adjustHeightForShortBlocks && blockStateShortShapeCache.isShort(this.state));
    }
    
    public int getEffectiveHeight(final boolean subtractOne) {
        int height = this.getHeight();
        if (subtractOne) {
            --height;
        }
        return height;
    }
    
    public int getEffectiveTopHeight(final boolean subtractOne) {
        int topHeight = this.getTopHeight();
        if (subtractOne && topHeight == this.getHeight()) {
            --topHeight;
        }
        return topHeight;
    }
    
    public int getBiome() {
        return this.biome;
    }
    
    public void setBiome(final int biome) {
        this.biome = biome;
    }
    
    public ArrayList<Overlay> getOverlays() {
        return this.overlays;
    }
    
    public byte getVerticalSlope() {
        return this.verticalSlope;
    }
    
    public void setVerticalSlope(final byte slope) {
        this.verticalSlope = slope;
    }
    
    public byte getDiagonalSlope() {
        return this.diagonalSlope;
    }
    
    public void setDiagonalSlope(final byte slope) {
        this.diagonalSlope = slope;
    }
    
    public void setSlopeUnknown(final boolean slopeUnknown) {
        this.slopeUnknown = slopeUnknown;
    }
    
    public int getNumberOfOverlays() {
        return (this.overlays == null) ? 0 : this.overlays.size();
    }
}
