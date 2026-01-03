//Decompiled by Procyon!

package xaero.map.cache;

import java.util.*;
import net.minecraft.block.state.*;
import java.lang.reflect.*;
import net.minecraft.init.*;
import xaero.lib.common.reflection.util.*;
import net.minecraft.util.math.*;
import net.minecraft.client.*;
import xaero.map.core.*;
import net.minecraft.world.*;
import net.minecraft.world.biome.*;
import net.minecraft.block.material.*;
import xaero.map.config.util.*;
import xaero.map.*;

public class BlockStateColorTypeCache
{
    private Hashtable<IBlockState, Integer> colorTypes;
    private Hashtable<IBlockState, Object> defaultColorResolversCache;
    private int grassColor;
    private int foliageColor;
    private IBlockState grassState;
    private IBlockState oakLeavesState;
    private IBlockState waterState;
    private Field defaultGrassResolverField;
    private Field defaultFoliageResolverField;
    private Field defaultWaterResolverField;
    private Object DEFAULT_GRASS_RESOLVER;
    private Object DEFAULT_FOLIAGE_RESOLVER;
    private Object DEFAULT_WATER_RESOLVER;
    private boolean detectionWorks;
    private MapWriter mapWriter;
    
    public BlockStateColorTypeCache() {
        this.detectionWorks = true;
        this.colorTypes = new Hashtable<IBlockState, Integer>();
        this.defaultColorResolversCache = new Hashtable<IBlockState, Object>();
        this.grassState = Blocks.field_150349_c.func_176223_P();
        this.oakLeavesState = Blocks.field_150362_t.func_176223_P();
        this.waterState = Blocks.field_150355_j.func_176223_P();
        this.defaultGrassResolverField = ReflectionUtils.getFieldReflection((Class)BiomeColorHelper.class, "GRASS_COLOR", "", "", "field_180291_a");
        this.defaultFoliageResolverField = ReflectionUtils.getFieldReflection((Class)BiomeColorHelper.class, "FOLIAGE_COLOR", "", "", "field_180289_b");
        this.defaultWaterResolverField = ReflectionUtils.getFieldReflection((Class)BiomeColorHelper.class, "WATER_COLOR", "", "", "field_180290_c");
    }
    
    public void setMapWriter(final MapWriter mapWriter) {
        this.mapWriter = mapWriter;
    }
    
    public void getBlockBiomeColour(final World world, final IBlockState state, final BlockPos pos, final int[] dest, int biomeId) {
        dest[0] = (dest[2] = 0);
        dest[1] = -1;
        final Integer cachedColorType = this.colorTypes.get(state);
        int colorType = (cachedColorType != null) ? cachedColorType : -1;
        int customColour = -1;
        boolean gotFullCC = false;
        final boolean isRenderThread = Minecraft.func_71410_x().func_152345_ab();
        if (colorType == -1 && isRenderThread) {
            final int stateTint = this.mapWriter.getBlockTintIndex(state);
            Object detectedColorResolver = this.detectionWorks ? XaeroWorldMapCore.detectColorResolver(state, world, pos, stateTint, this.DEFAULT_GRASS_RESOLVER, this.DEFAULT_FOLIAGE_RESOLVER, this.DEFAULT_WATER_RESOLVER) : null;
            final boolean detected = detectedColorResolver != null;
            if (!detected) {
                if (state.func_177230_c() == Blocks.field_150355_j || state.func_177230_c() == Blocks.field_150358_i) {
                    detectedColorResolver = this.DEFAULT_WATER_RESOLVER;
                }
                else {
                    try {
                        customColour = Minecraft.func_71410_x().func_184125_al().func_186724_a(state, (IBlockAccess)null, (BlockPos)null, stateTint);
                    }
                    catch (Throwable t) {
                        customColour = 0;
                    }
                    if (customColour != -1 && customColour != this.grassColor && customColour != this.foliageColor) {
                        final Material material = state.func_185904_a();
                        if (material != null && (material.func_151565_r() == MapColor.field_151661_c || material.func_151565_r() == MapColor.field_151669_i || material.func_151565_r() == MapColor.field_151662_n)) {
                            customColour = this.tryGettingColor(state, world, pos);
                            gotFullCC = true;
                            if (material.func_151565_r() == MapColor.field_151661_c && customColour == this.tryGettingColor(this.grassState, world, pos)) {
                                detectedColorResolver = this.DEFAULT_GRASS_RESOLVER;
                            }
                            else if (material.func_151565_r() == MapColor.field_151669_i && customColour == this.tryGettingColor(this.oakLeavesState, world, pos)) {
                                detectedColorResolver = this.DEFAULT_FOLIAGE_RESOLVER;
                            }
                            else if (material.func_151565_r() == MapColor.field_151662_n && customColour == this.tryGettingColor(this.waterState, world, pos)) {
                                detectedColorResolver = this.DEFAULT_WATER_RESOLVER;
                            }
                        }
                    }
                    else if (customColour == this.grassColor) {
                        detectedColorResolver = this.DEFAULT_GRASS_RESOLVER;
                    }
                    else if (customColour == this.foliageColor) {
                        detectedColorResolver = this.DEFAULT_FOLIAGE_RESOLVER;
                    }
                }
            }
            if (detectedColorResolver != null) {
                colorType = 1;
                if (detectedColorResolver == this.DEFAULT_FOLIAGE_RESOLVER) {
                    colorType = 2;
                }
                if (!detected) {
                    this.defaultColorResolversCache.put(state, detectedColorResolver);
                }
            }
            else {
                if (!gotFullCC) {
                    customColour = this.tryGettingColor(state, world, pos);
                    gotFullCC = true;
                }
                if (customColour != 16777215 && customColour != -1) {
                    colorType = 3;
                }
                else {
                    colorType = 0;
                }
            }
            this.colorTypes.put(state, colorType);
        }
        else if (colorType == 3 && !isRenderThread) {
            colorType = -1;
        }
        if (biomeId == -1) {
            if (isRenderThread) {
                biomeId = Biome.func_185362_a(world.func_180494_b(pos));
            }
            else {
                colorType = -1;
            }
        }
        dest[0] = colorType;
        dest[1] = biomeId;
        if (colorType == 3) {
            if (!gotFullCC) {
                customColour = this.tryGettingColor(state, world, pos);
            }
            dest[2] = customColour;
        }
    }
    
    public void updateGrassColor() {
        this.grassColor = this.tryGettingColor(this.grassState, null, null);
        this.foliageColor = this.tryGettingColor(this.oakLeavesState, null, null);
        if (WorldMapClientConfigUtils.getDebug()) {
            WorldMap.LOGGER.info("Default grass colour: " + this.grassColor);
        }
    }
    
    public void updateDefaultResolvers(final World world) {
        if (world == null) {
            return;
        }
        final IBlockState grassState = Blocks.field_150349_c.func_176223_P();
        this.DEFAULT_GRASS_RESOLVER = XaeroWorldMapCore.detectColorResolver(grassState, world, new BlockPos(0, 0, 0), this.mapWriter.getBlockTintIndex(grassState), null, null, null);
        if (this.DEFAULT_GRASS_RESOLVER == null) {
            this.DEFAULT_GRASS_RESOLVER = this.getDefaultGrassResolver();
            this.detectionWorks = false;
        }
        final IBlockState foliageState = Blocks.field_150362_t.func_176223_P();
        this.DEFAULT_FOLIAGE_RESOLVER = XaeroWorldMapCore.detectColorResolver(foliageState, world, new BlockPos(0, 0, 0), this.mapWriter.getBlockTintIndex(foliageState), null, null, null);
        if (this.DEFAULT_FOLIAGE_RESOLVER == null) {
            this.DEFAULT_FOLIAGE_RESOLVER = this.getDefaultFoliageResolver();
            this.detectionWorks = false;
        }
        final IBlockState waterState = Blocks.field_150355_j.func_176223_P();
        this.DEFAULT_WATER_RESOLVER = XaeroWorldMapCore.detectColorResolver(waterState, world, new BlockPos(0, 0, 0), this.mapWriter.getBlockTintIndex(waterState), null, null, null);
        if (this.DEFAULT_WATER_RESOLVER == null) {
            this.DEFAULT_WATER_RESOLVER = this.getDefaultWaterResolver();
            this.detectionWorks = false;
        }
    }
    
    private int tryGettingColor(final IBlockState state, final World world, final BlockPos pos) {
        try {
            return Minecraft.func_71410_x().func_184125_al().func_186724_a(state, (IBlockAccess)world, pos, this.mapWriter.getBlockTintIndex(state));
        }
        catch (Throwable t) {
            WorldMap.LOGGER.error("suppressed exception", t);
            return 0;
        }
    }
    
    public Object getColorResolver(final IBlockState state, final World world, final BlockPos pos) {
        final Object detection = this.detectionWorks ? XaeroWorldMapCore.detectColorResolver(state, world, pos, this.mapWriter.getBlockTintIndex(state), this.DEFAULT_GRASS_RESOLVER, this.DEFAULT_FOLIAGE_RESOLVER, this.DEFAULT_WATER_RESOLVER) : null;
        if (detection != null) {
            return detection;
        }
        return this.defaultColorResolversCache.get(state);
    }
    
    private Object getDefaultGrassResolver() {
        return ReflectionUtils.getReflectFieldValue((Object)null, this.defaultGrassResolverField);
    }
    
    private Object getDefaultFoliageResolver() {
        return ReflectionUtils.getReflectFieldValue((Object)null, this.defaultFoliageResolverField);
    }
    
    private Object getDefaultWaterResolver() {
        return ReflectionUtils.getReflectFieldValue((Object)null, this.defaultWaterResolverField);
    }
}
