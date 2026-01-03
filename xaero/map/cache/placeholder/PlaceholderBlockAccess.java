//Decompiled by Procyon!

package xaero.map.cache.placeholder;

import net.minecraft.block.state.*;
import net.minecraft.util.math.*;
import net.minecraft.tileentity.*;
import net.minecraft.block.*;
import net.minecraft.world.biome.*;
import net.minecraft.init.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

public class PlaceholderBlockAccess implements IBlockAccess
{
    private IBlockState placeholderState;
    
    public void setPlaceholderState(final IBlockState placeholderState) {
        this.placeholderState = placeholderState;
    }
    
    public TileEntity func_175625_s(final BlockPos pos) {
        return null;
    }
    
    public int func_175626_b(final BlockPos pos, final int lightValue) {
        return 0;
    }
    
    public IBlockState func_180495_p(final BlockPos pos) {
        return this.placeholderState;
    }
    
    public boolean func_175623_d(final BlockPos pos) {
        return this.placeholderState != null && this.placeholderState instanceof BlockAir;
    }
    
    public Biome func_180494_b(final BlockPos pos) {
        return Biomes.field_76772_c;
    }
    
    public int func_175627_a(final BlockPos pos, final EnumFacing direction) {
        return 0;
    }
    
    public WorldType func_175624_G() {
        return WorldType.field_77137_b;
    }
    
    public boolean isSideSolid(final BlockPos pos, final EnumFacing side, final boolean _default) {
        return false;
    }
}
