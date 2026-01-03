//Decompiled by Procyon!

package xaero.map.highlight;

import net.minecraft.util.text.*;
import java.util.*;

public abstract class AbstractHighlighter
{
    protected final boolean coveringOutsideDiscovered;
    protected final int[] resultStore;
    
    protected AbstractHighlighter(final boolean coveringOutsideDiscovered) {
        this.resultStore = new int[256];
        this.coveringOutsideDiscovered = coveringOutsideDiscovered;
    }
    
    public abstract int calculateRegionHash(final int p0, final int p1, final int p2);
    
    public abstract boolean regionHasHighlights(final int p0, final int p1, final int p2);
    
    public abstract boolean chunkIsHighlit(final int p0, final int p1, final int p2);
    
    public abstract int[] getChunkHighlitColor(final int p0, final int p1, final int p2);
    
    public abstract ITextComponent getBlockHighlightSubtleTooltip(final int p0, final int p1, final int p2);
    
    public abstract ITextComponent getBlockHighlightBluntTooltip(final int p0, final int p1, final int p2);
    
    public abstract void addMinimapBlockHighlightTooltips(final List<ITextComponent> p0, final int p1, final int p2, final int p3, final int p4);
    
    protected void setResult(final int x, final int z, final int color) {
        this.resultStore[z << 4 | x] = color;
    }
    
    protected int getBlend(final int color1, final int color2) {
        if (color1 == color2) {
            return color1;
        }
        final int red1 = color1 >> 8 & 0xFF;
        final int green1 = color1 >> 16 & 0xFF;
        final int blue1 = color1 >> 24 & 0xFF;
        final int alpha1 = color1 & 0xFF;
        final int red2 = color2 >> 8 & 0xFF;
        final int green2 = color2 >> 16 & 0xFF;
        final int blue2 = color2 >> 24 & 0xFF;
        final int alpha2 = color2 & 0xFF;
        final int red3 = red1 + red2 >> 1;
        final int green3 = green1 + green2 >> 1;
        final int blue3 = blue1 + blue2 >> 1;
        final int alpha3 = alpha1 + alpha2 >> 1;
        return blue3 << 24 | green3 << 16 | red3 << 8 | alpha3;
    }
    
    public boolean isCoveringOutsideDiscovered() {
        return this.coveringOutsideDiscovered;
    }
}
