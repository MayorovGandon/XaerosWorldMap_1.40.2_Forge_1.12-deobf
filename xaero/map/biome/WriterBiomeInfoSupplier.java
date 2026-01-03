//Decompiled by Procyon!

package xaero.map.biome;

import net.minecraft.util.math.*;
import xaero.map.cache.*;
import net.minecraft.world.*;
import net.minecraft.block.state.*;
import xaero.map.region.*;
import xaero.map.misc.*;

public class WriterBiomeInfoSupplier implements BiomeInfoSupplier
{
    private MapBlock currentPixel;
    private boolean canReuseBiomeColours;
    private BlockPos mutableGlobalPos;
    
    public WriterBiomeInfoSupplier(final BlockPos mutableGlobalPos) {
        this.mutableGlobalPos = mutableGlobalPos;
    }
    
    public void set(final MapBlock currentPixel, final boolean canReuseBiomeColours) {
        this.currentPixel = currentPixel;
        this.canReuseBiomeColours = canReuseBiomeColours;
    }
    
    public void getBiomeInfo(final BlockStateColorTypeCache colorTypeCache, final World world, final IBlockState state, final BlockPos pos, final int[] biomeBuffer, final int blockBiome) {
        final MapBlock currentPixel = this.currentPixel;
        if (this.canReuseBiomeColours && currentPixel != null && currentPixel.getNumberOfOverlays() > 0 && Misc.getStateById(currentPixel.getOverlays().get(0).getState()) == state) {
            final Overlay currentTopOverlay = currentPixel.getOverlays().get(0);
            biomeBuffer[0] = currentTopOverlay.getColourType();
            biomeBuffer[1] = ((currentTopOverlay.getColourType() == 1) ? currentPixel.getBiome() : -1);
            biomeBuffer[2] = currentTopOverlay.getCustomColour();
        }
        else {
            colorTypeCache.getBlockBiomeColour(world, state, this.mutableGlobalPos, biomeBuffer, blockBiome);
        }
    }
}
