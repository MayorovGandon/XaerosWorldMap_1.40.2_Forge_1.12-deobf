//Decompiled by Procyon!

package xaero.map.biome;

import xaero.map.cache.*;
import net.minecraft.world.*;
import net.minecraft.block.state.*;
import net.minecraft.util.math.*;

public interface BiomeInfoSupplier
{
    void getBiomeInfo(final BlockStateColorTypeCache p0, final World p1, final IBlockState p2, final BlockPos p3, final int[] p4, final int p5);
}
