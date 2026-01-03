//Decompiled by Procyon!

package xaero.map.highlight;

import net.minecraft.util.text.*;

public abstract class ChunkHighlighter extends AbstractHighlighter
{
    protected ChunkHighlighter(final boolean coveringOutsideDiscovered) {
        super(coveringOutsideDiscovered);
    }
    
    protected abstract int[] getColors(final int p0, final int p1, final int p2);
    
    public int[] getChunkHighlitColor(final int dimension, final int chunkX, final int chunkZ) {
        final int[] colors = this.getColors(dimension, chunkX, chunkZ);
        if (colors == null) {
            return null;
        }
        final int centerColor = colors[0];
        final int topColor = colors[1];
        final int rightColor = colors[2];
        final int bottomColor = colors[3];
        final int leftColor = colors[4];
        final int topLeftColor = this.getSideBlend(topColor, leftColor, centerColor);
        final int topRightColor = this.getSideBlend(topColor, rightColor, centerColor);
        final int bottomRightColor = this.getSideBlend(bottomColor, rightColor, centerColor);
        final int bottomLeftColor = this.getSideBlend(bottomColor, leftColor, centerColor);
        this.setResult(0, 0, topLeftColor);
        this.setResult(15, 0, topRightColor);
        this.setResult(15, 15, bottomRightColor);
        this.setResult(0, 15, bottomLeftColor);
        for (int i = 1; i < 15; ++i) {
            this.setResult(i, 0, topColor);
            this.setResult(15, i, rightColor);
            this.setResult(i, 15, bottomColor);
            this.setResult(0, i, leftColor);
            for (int j = 1; j < 15; ++j) {
                this.setResult(i, j, centerColor);
            }
        }
        return this.resultStore;
    }
    
    private int getSideBlend(final int color1, final int color2, final int centerColor) {
        return this.getBlend((color1 == centerColor) ? color2 : color1, (color2 == centerColor) ? color1 : color2);
    }
    
    public ITextComponent getBlockHighlightBluntTooltip(final int dimension, final int blockX, final int blockZ) {
        if (!this.chunkIsHighlit(dimension, blockX >> 4, blockZ >> 4)) {
            return null;
        }
        return this.getChunkHighlightBluntTooltip(dimension, blockX >> 4, blockZ >> 4);
    }
    
    public ITextComponent getBlockHighlightSubtleTooltip(final int dimension, final int blockX, final int blockZ) {
        if (!this.chunkIsHighlit(dimension, blockX >> 4, blockZ >> 4)) {
            return null;
        }
        return this.getChunkHighlightSubtleTooltip(dimension, blockX >> 4, blockZ >> 4);
    }
    
    public abstract ITextComponent getChunkHighlightSubtleTooltip(final int p0, final int p1, final int p2);
    
    public abstract ITextComponent getChunkHighlightBluntTooltip(final int p0, final int p1, final int p2);
}
