//Decompiled by Procyon!

package xaero.map.biome;

import net.minecraft.world.biome.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

public class MapBiomes extends Biome
{
    public MapBiomes() {
        super(new Biome.BiomeProperties(""));
    }
    
    public int getBiomeGrassColour(final int biomeId, final Biome biome, final BlockPos pos) {
        if (!this.isVanilla(biomeId)) {
            return biome.func_180627_b(pos);
        }
        if (biome instanceof BiomeForest) {
            return this.forestGrassColor(biomeId, biome, pos);
        }
        if (biome instanceof BiomeMesa) {
            return 9470285;
        }
        if (biome instanceof BiomeSwamp) {
            return this.swampGrassColor(pos);
        }
        return this.defaultGrassColor(biome, pos);
    }
    
    public int getBiomeFoliageColour(final int biomeId, final Biome biome, final BlockPos pos) {
        if (!this.isVanilla(biomeId)) {
            return biome.func_180625_c(pos);
        }
        if (biome instanceof BiomeMesa) {
            return 10387789;
        }
        if (biome instanceof BiomeSwamp) {
            return 6975545;
        }
        return this.defaultFoliageColor(biome, pos);
    }
    
    public int getBiomeWaterColour(final int biomeId, final Biome biome) {
        if (!this.isVanilla(biomeId)) {
            return biome.getWaterColorMultiplier();
        }
        if (biome instanceof BiomeSwamp) {
            return 14745518;
        }
        return this.defaultWaterColor(biome);
    }
    
    private boolean isVanilla(final int biomeId) {
        return biomeId < 40 || biomeId == 127 || (biomeId >= 129 && biomeId < 135) || biomeId == 140 || biomeId == 149 || biomeId == 151 || (biomeId >= 155 && biomeId < 159) || (biomeId >= 160 && biomeId < 168);
    }
    
    private int defaultGrassColor(final Biome biome, final BlockPos pos) {
        final double d0 = MathHelper.func_76131_a(biome.func_180626_a(pos), 0.0f, 1.0f);
        final double d2 = MathHelper.func_76131_a(biome.func_76727_i(), 0.0f, 1.0f);
        return ColorizerGrass.func_77480_a(d0, d2);
    }
    
    private int forestGrassColor(final int biomeId, final Biome biome, final BlockPos pos) {
        final int i = this.defaultGrassColor(biome, pos);
        return (biomeId == 29 || biomeId == 157) ? ((i & 0xFEFEFE) + 2634762 >> 1) : i;
    }
    
    private int swampGrassColor(final BlockPos pos) {
        final double d0 = MapBiomes.field_180281_af.func_151601_a(pos.func_177958_n() * 0.0225, pos.func_177952_p() * 0.0225);
        return (d0 < -0.1) ? 5011004 : 6975545;
    }
    
    private int defaultFoliageColor(final Biome biome, final BlockPos pos) {
        final double d0 = MathHelper.func_76131_a(biome.func_180626_a(pos), 0.0f, 1.0f);
        final double d2 = MathHelper.func_76131_a(biome.func_76727_i(), 0.0f, 1.0f);
        return ColorizerFoliage.func_77470_a(d0, d2);
    }
    
    private int defaultWaterColor(final Biome biome) {
        return 16777215;
    }
}
