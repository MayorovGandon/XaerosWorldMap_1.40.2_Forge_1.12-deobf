//Decompiled by Procyon!

package xaero.map.highlight;

import xaero.map.world.*;
import net.minecraft.util.text.*;
import it.unimi.dsi.fastutil.longs.*;
import java.util.*;
import xaero.map.pool.buffer.*;
import xaero.map.*;
import xaero.map.misc.*;
import java.nio.*;
import org.apache.commons.lang3.builder.*;
import xaero.map.mods.*;

public class DimensionHighlighterHandler
{
    private final MapDimension mapDimension;
    private final int dimension;
    private final HighlighterRegistry registry;
    private final Long2ObjectMap<Integer> hashCodeCache;
    private final ITextComponent SUBTLE_TOOLTIP_SEPARATOR;
    private final ITextComponent BLUNT_TOOLTIP_SEPARATOR;
    
    public DimensionHighlighterHandler(final MapDimension mapDimension, final int dimension, final HighlighterRegistry registry) {
        this.SUBTLE_TOOLTIP_SEPARATOR = (ITextComponent)new TextComponentString(" | ");
        this.BLUNT_TOOLTIP_SEPARATOR = (ITextComponent)new TextComponentString(" \n ");
        this.mapDimension = mapDimension;
        this.dimension = dimension;
        this.registry = registry;
        this.hashCodeCache = (Long2ObjectMap<Integer>)new Long2ObjectOpenHashMap();
    }
    
    public int getRegionHash(final int regionX, final int regionZ) {
        synchronized (this) {
            final long key = getKey(regionX, regionZ);
            Integer cachedHash = (Integer)this.hashCodeCache.get(key);
            if (cachedHash == null) {
                cachedHash = this.recalculateHash(regionX, regionZ);
            }
            return cachedHash;
        }
    }
    
    public boolean shouldApplyRegionHighlights(final int regionX, final int regionZ, final boolean discovered) {
        final int dimension = this.dimension;
        for (final AbstractHighlighter hl : this.registry.getHighlighters()) {
            if ((discovered || hl.isCoveringOutsideDiscovered()) && hl.regionHasHighlights(dimension, regionX, regionZ)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean shouldApplyTileChunkHighlights(final int regionX, final int regionZ, final int insideTileChunkX, final int insideTileChunkZ, final boolean discovered) {
        final int startChunkX = regionX << 5 | insideTileChunkX << 2;
        final int startChunkZ = regionZ << 5 | insideTileChunkZ << 2;
        for (final AbstractHighlighter hl : this.registry.getHighlighters()) {
            if (this.shouldApplyTileChunkHighlightsHelp(hl, regionX, regionZ, startChunkX, startChunkZ, discovered)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean shouldApplyTileChunkHighlights(final AbstractHighlighter hl, final int regionX, final int regionZ, final int insideTileChunkX, final int insideTileChunkZ, final boolean discovered) {
        final int startChunkX = regionX << 5 | insideTileChunkX << 2;
        final int startChunkZ = regionZ << 5 | insideTileChunkZ << 2;
        return this.shouldApplyTileChunkHighlightsHelp(hl, regionX, regionZ, startChunkX, startChunkZ, discovered);
    }
    
    private boolean shouldApplyTileChunkHighlightsHelp(final AbstractHighlighter hl, final int regionX, final int regionZ, final int startChunkX, final int startChunkZ, final boolean discovered) {
        if (!discovered && !hl.isCoveringOutsideDiscovered()) {
            return false;
        }
        final int dimension = this.dimension;
        if (!hl.regionHasHighlights(dimension, regionX, regionZ)) {
            return false;
        }
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                if (hl.chunkIsHighlit(dimension, startChunkX | i, startChunkZ | j)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public PoolTextureDirectBufferUnit applyChunkHighlightColors(final int chunkX, final int chunkZ, final int innerChunkX, final int innerChunkZ, final PoolTextureDirectBufferUnit buffer, PoolTextureDirectBufferUnit highlitColorBuffer, boolean highlitBufferPrepared, final boolean discovered, final boolean separateBuffer) {
        boolean hasSomething = false;
        final int dimension = this.dimension;
        if (!separateBuffer) {
            highlitBufferPrepared = true;
            highlitColorBuffer = buffer;
        }
        ByteBuffer highlitColorBufferDirect = (highlitColorBuffer == null) ? null : highlitColorBuffer.getDirectBuffer();
        for (final AbstractHighlighter hl : this.registry.getHighlighters()) {
            if (!discovered && !hl.isCoveringOutsideDiscovered()) {
                continue;
            }
            final int[] highlightColors = hl.getChunkHighlitColor(dimension, chunkX, chunkZ);
            if (highlightColors == null) {
                continue;
            }
            if (!hasSomething && !highlitBufferPrepared) {
                highlitColorBuffer = WorldMap.textureDirectBufferPool.get(buffer == null);
                highlitColorBufferDirect = highlitColorBuffer.getDirectBuffer();
                if (buffer != null) {
                    highlitColorBufferDirect.put(buffer.getDirectBuffer());
                }
                BufferCompatibilityFix.position(highlitColorBufferDirect, 0);
                if (buffer != null) {
                    BufferCompatibilityFix.position(buffer.getDirectBuffer(), 0);
                }
            }
            hasSomething = true;
            final int textureOffset = innerChunkZ << 4 << 6 | innerChunkX << 4;
            for (int i = 0; i < highlightColors.length; ++i) {
                final int highlightColor = highlightColors[i];
                final int hlAlpha = highlightColor & 0xFF;
                final float hlAlphaFloat = hlAlpha / 255.0f;
                final float oneMinusHlAlpha = 1.0f - hlAlphaFloat;
                final int hlRed = highlightColor >> 8 & 0xFF;
                final int hlGreen = highlightColor >> 16 & 0xFF;
                final int hlBlue = highlightColor >> 24 & 0xFF;
                final int index = textureOffset | i >> 4 << 6 | (i & 0xF);
                final int originalColor = highlitColorBufferDirect.getInt(index * 4);
                int red = originalColor >> 8 & 0xFF;
                int green = originalColor >> 16 & 0xFF;
                int blue = originalColor >> 24 & 0xFF;
                final int alpha = originalColor & 0xFF;
                red = (int)(red * oneMinusHlAlpha + hlRed * hlAlphaFloat);
                green = (int)(green * oneMinusHlAlpha + hlGreen * hlAlphaFloat);
                blue = (int)(blue * oneMinusHlAlpha + hlBlue * hlAlphaFloat);
                if (red > 255) {
                    red = 255;
                }
                if (green > 255) {
                    green = 255;
                }
                if (blue > 255) {
                    blue = 255;
                }
                highlitColorBufferDirect.putInt(index * 4, blue << 24 | green << 16 | red << 8 | alpha);
            }
        }
        if (!hasSomething) {
            return null;
        }
        return highlitColorBuffer;
    }
    
    private int recalculateHash(final int regionX, final int regionZ) {
        final HashCodeBuilder hashcodeBuilder = new HashCodeBuilder();
        for (final AbstractHighlighter hl : this.registry.getHighlighters()) {
            hashcodeBuilder.append(hl.calculateRegionHash(this.dimension, regionX, regionZ));
            hashcodeBuilder.append(hl.isCoveringOutsideDiscovered());
        }
        final int builtHash = hashcodeBuilder.build();
        final long key = getKey(regionX, regionZ);
        this.hashCodeCache.put(key, (Object)builtHash);
        return builtHash;
    }
    
    public void clearCachedHash(final int regionX, final int regionZ) {
        final long key = getKey(regionX, regionZ);
        this.hashCodeCache.remove(key);
        this.mapDimension.onClearCachedHighlightHash(regionX, regionZ);
        if (SupportMods.minimap()) {
            SupportMods.xaeroMinimap.onClearHighlightHash(regionX, regionZ);
        }
    }
    
    public void clearCachedHashes() {
        this.hashCodeCache.clear();
        this.mapDimension.onClearCachedHighlightHashes();
        if (SupportMods.minimap()) {
            SupportMods.xaeroMinimap.onClearHighlightHashes();
        }
    }
    
    public ITextComponent getBlockHighlightSubtleTooltip(final int blockX, final int blockZ, final boolean discovered) {
        return this.getBlockHighlightTooltip(blockX, blockZ, discovered, true);
    }
    
    public ITextComponent getBlockHighlightBluntTooltip(final int blockX, final int blockZ, final boolean discovered) {
        return this.getBlockHighlightTooltip(blockX, blockZ, discovered, false);
    }
    
    private ITextComponent getBlockHighlightTooltip(final int blockX, final int blockZ, final boolean discovered, final boolean subtle) {
        final int dimension = this.dimension;
        final int tileChunkX = blockX >> 6;
        final int tileChunkZ = blockZ >> 6;
        final int regionX = tileChunkX >> 3;
        final int regionZ = tileChunkZ >> 3;
        if (!this.shouldApplyRegionHighlights(regionX, regionZ, discovered)) {
            return null;
        }
        final int localTileChunkX = tileChunkX & 0x7;
        final int localTileChunkZ = tileChunkZ & 0x7;
        ITextComponent result = null;
        for (final AbstractHighlighter hl : this.registry.getHighlighters()) {
            if (!this.shouldApplyTileChunkHighlights(hl, regionX, regionZ, localTileChunkX, localTileChunkZ, discovered)) {
                continue;
            }
            final ITextComponent hlTooltip = subtle ? hl.getBlockHighlightSubtleTooltip(dimension, blockX, blockZ) : hl.getBlockHighlightBluntTooltip(dimension, blockX, blockZ);
            if (hlTooltip == null) {
                continue;
            }
            if (result == null) {
                result = (ITextComponent)new TextComponentString("");
            }
            else {
                result.func_150253_a().add(subtle ? this.SUBTLE_TOOLTIP_SEPARATOR : this.BLUNT_TOOLTIP_SEPARATOR);
            }
            result.func_150253_a().add(hlTooltip);
        }
        return result;
    }
    
    public static long getKey(final int regionX, final int regionZ) {
        return (long)regionZ << 32 | ((long)regionX & 0xFFFFFFFFL);
    }
    
    public static int getXFromKey(final long key) {
        return (int)(key & -1L);
    }
    
    public static int getZFromKey(final long key) {
        return (int)(key >> 32);
    }
}
