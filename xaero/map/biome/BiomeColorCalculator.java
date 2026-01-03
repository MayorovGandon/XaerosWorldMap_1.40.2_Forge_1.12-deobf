//Decompiled by Procyon!

package xaero.map.biome;

import java.lang.reflect.*;
import xaero.lib.common.reflection.util.*;
import net.minecraft.world.biome.*;
import net.minecraft.util.math.*;
import net.minecraft.block.state.*;
import xaero.map.region.*;
import net.minecraft.world.*;
import xaero.map.*;
import xaero.map.cache.*;
import net.minecraft.init.*;

public class BiomeColorCalculator
{
    private Method colorResolverGetColorMethod;
    private int startO;
    private int endO;
    private int startP;
    private int endP;
    
    public BiomeColorCalculator() {
        try {
            final Class<?> colorResolverInterface = (Class<?>)ReflectionUtils.getClassForName("net.minecraft.world.biome.BiomeColorHelper$ColorResolver", "net.minecraft.world.biome.BiomeColorHelper$ColorResolver");
            this.colorResolverGetColorMethod = ReflectionUtils.getMethodReflection((Class)colorResolverInterface, "getColorAtPos", "", "", "func_180283_a", (Class)Integer.TYPE, new Class[] { Biome.class, BlockPos.class });
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void prepare(final boolean biomeBlending) {
        final int n = 0;
        this.endP = n;
        this.startP = n;
        this.endO = n;
        this.startO = n;
        if (biomeBlending) {
            this.startO = -1;
            this.endO = 1;
            this.startP = -1;
            this.endP = 1;
        }
    }
    
    public int getBiomeColor(final IBlockState state, final boolean overlay, final BlockPos.MutableBlockPos pos, final MapTile tile, final int caveLayer, final World world, final MapProcessor mapProcessor, final BlockStateColorTypeCache blockStateColorTypeCache) {
        int i = 0;
        int j = 0;
        int k = 0;
        int total = 0;
        final int initX = pos.func_177958_n();
        final int initZ = pos.func_177952_p();
        final Object stateColorResolver = blockStateColorTypeCache.getColorResolver(state, world, (BlockPos)pos);
        if (stateColorResolver == null) {
            return -1;
        }
        for (int o = this.startO; o <= this.endO; ++o) {
            for (int p = this.startP; p <= this.endP; ++p) {
                if (o == 0 || p == 0) {
                    pos.func_181079_c(initX + o, pos.func_177956_o(), initZ + p);
                    Integer b = this.getBiomeAtPos((BlockPos)pos, tile, caveLayer, mapProcessor);
                    if (b != null) {
                        if (b == -1 && overlay) {
                            b = Biome.func_185362_a(Biomes.field_76781_i);
                        }
                        if (b != -1) {
                            int l = 0;
                            Biome gen = Biome.func_150568_d((int)b);
                            if (gen == null) {
                                gen = world.field_73011_w.func_177499_m().func_180300_a((BlockPos)pos, Biomes.field_76772_c);
                                b = Biome.func_185362_a(gen);
                            }
                            if (gen != null) {
                                l = this.resolve(stateColorResolver, gen, (BlockPos)pos);
                                i += (l & 0xFF0000);
                                j += (l & 0xFF00);
                                k += (l & 0xFF);
                                ++total;
                            }
                        }
                    }
                }
            }
        }
        pos.func_181079_c(initX, pos.func_177956_o(), initZ);
        if (total != 0) {
            return (i / total & 0xFF0000) | (j / total & 0xFF00) | k / total;
        }
        final Biome defaultBiome = Biomes.field_76781_i;
        if (defaultBiome == null) {
            return -1;
        }
        return this.resolve(stateColorResolver, defaultBiome, (BlockPos)pos);
    }
    
    public Integer getBiomeAtPos(final BlockPos pos, final MapTile centerTile, final int caveLayer, final MapProcessor mapProcessor) {
        final int tileX = pos.func_177958_n() >> 4;
        final int tileZ = pos.func_177952_p() >> 4;
        final MapTile tile = (tileX == centerTile.getChunkX() && tileZ == centerTile.getChunkZ()) ? centerTile : mapProcessor.getMapTile(caveLayer, tileX, tileZ);
        if (tile != null && tile.isLoaded()) {
            return tile.getBlock(pos.func_177958_n() & 0xF, pos.func_177952_p() & 0xF).getBiome();
        }
        return null;
    }
    
    private int resolve(final Object colorResolver, final Biome biome, final BlockPos pos) {
        return (int)ReflectionUtils.getReflectMethodValue(colorResolver, this.colorResolverGetColorMethod, new Object[] { biome, pos });
    }
}
