//Decompiled by Procyon!

package xaero.map.highlight;

import net.minecraft.util.text.*;
import java.util.*;

public class TestHighlighter extends ChunkHighlighter
{
    public TestHighlighter() {
        super(false);
    }
    
    public boolean regionHasHighlights(final int dimension, final int regionX, final int regionZ) {
        return true;
    }
    
    protected int[] getColors(final int dimension, final int chunkX, final int chunkZ) {
        if (!this.chunkIsHighlit(dimension, chunkX, chunkZ)) {
            return null;
        }
        final int centerColor = 1442796919;
        final int sideColor = 1442797004;
        this.resultStore[0] = centerColor;
        this.resultStore[1] = (((chunkZ & 0x3) == 0x0) ? sideColor : centerColor);
        this.resultStore[2] = (((chunkX & 0x3) == 0x3) ? sideColor : centerColor);
        this.resultStore[3] = (((chunkZ & 0x3) == 0x3) ? sideColor : centerColor);
        this.resultStore[4] = (((chunkX & 0x3) == 0x0) ? sideColor : centerColor);
        return this.resultStore;
    }
    
    public int calculateRegionHash(final int dimension, final int regionX, final int regionZ) {
        return 51;
    }
    
    public boolean chunkIsHighlit(final int dimension, final int chunkX, final int chunkZ) {
        return (chunkX >> 2 & 0x1) == (chunkZ >> 2 & 0x1);
    }
    
    public ITextComponent getChunkHighlightSubtleTooltip(final int dimension, final int chunkX, final int chunkZ) {
        return (ITextComponent)new TextComponentString("subtle!");
    }
    
    public ITextComponent getChunkHighlightBluntTooltip(final int dimension, final int chunkX, final int chunkZ) {
        return (ITextComponent)new TextComponentString("blunt!");
    }
    
    public void addMinimapBlockHighlightTooltips(final List<ITextComponent> list, final int dimension, final int blockX, final int blockZ, final int width) {
        list.add((ITextComponent)new TextComponentString("minimap tooltip!"));
    }
}
