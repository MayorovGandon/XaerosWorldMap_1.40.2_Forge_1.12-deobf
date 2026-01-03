//Decompiled by Procyon!

package xaero.map.region;

import xaero.map.world.*;
import java.util.*;
import net.minecraft.util.math.*;
import xaero.map.biome.*;
import xaero.map.*;
import xaero.map.cache.*;
import xaero.map.misc.*;
import net.minecraft.world.*;
import net.minecraft.block.*;
import net.minecraft.block.state.*;

public class MapPixel
{
    private static final int VOID_COLOR = -16121833;
    private static final float DEFAULT_AMBIENT_LIGHT = 0.7f;
    private static final float DEFAULT_AMBIENT_LIGHT_COLORED = 0.2f;
    private static final float DEFAULT_AMBIENT_LIGHT_WHITE = 0.5f;
    private static final float DEFAULT_MAX_DIRECT_LIGHT = 0.6666667f;
    private static final float GLOWING_MAX_DIRECT_LIGHT = 0.22222224f;
    protected int state;
    protected byte colourType;
    protected int customColour;
    protected byte light;
    protected boolean glowing;
    
    public MapPixel() {
        this.state = 0;
        this.colourType = -1;
        this.light = 0;
        this.glowing = false;
    }
    
    private int getVanillaTransparency(final Block b) {
        return (b instanceof BlockLiquid) ? 191 : ((b instanceof BlockIce) ? 216 : 127);
    }
    
    public void getPixelColours(final int[] result_dest, final MapWriter mapWriter, final World world, final MapDimension dim, final MapTileChunk tileChunk, final MapTileChunk prevChunk, final MapTileChunk prevChunkDiagonal, final MapTileChunk prevChunkHorisontal, final MapTile mapTile, final int x, final int z, final MapBlock block, final int height, final int topHeight, final int caveStart, final int caveDepth, final ArrayList<Overlay> overlays, final BlockPos.MutableBlockPos mutableGlobalPos, final float shadowR, final float shadowG, final float shadowB, final BiomeColorCalculator biomeColorCalculator, final MapProcessor mapProcessor, final OverlayManager overlayManager, final BlockStateShortShapeCache blockStateShortShapeCache, final MapUpdateFastConfig updateConfig) {
        int colour = (block != null && caveStart != Integer.MAX_VALUE) ? 0 : -16121833;
        int topLightValue = this.light;
        final int lightMin = 9;
        float brightnessR = 1.0f;
        float brightnessG = 1.0f;
        float brightnessB = 1.0f;
        mutableGlobalPos.func_181079_c(mapTile.getChunkX() * 16 + x, height, mapTile.getChunkZ() * 16 + z);
        final int state = this.state;
        final IBlockState blockState = Misc.getStateById(state);
        final boolean isAir = blockState.func_177230_c() instanceof BlockAir;
        final boolean isFinalBlock = this instanceof MapBlock;
        if (!isAir) {
            if (updateConfig.blockColors == 0) {
                colour = mapWriter.loadBlockColourFromTexture(state, true, world, (BlockPos)mutableGlobalPos);
            }
            else {
                try {
                    final Block b = blockState.func_177230_c();
                    final int a = this.getVanillaTransparency(b);
                    colour = blockState.func_185909_g((IBlockAccess)world, (BlockPos)mutableGlobalPos).field_76291_p;
                    if (!isFinalBlock && colour == 0) {
                        result_dest[0] = -1;
                        return;
                    }
                    colour = (a << 24 | (colour & 0xFFFFFF));
                }
                catch (Exception ex) {}
            }
            if (!isFinalBlock && !updateConfig.stainedGlass && (blockState.func_177230_c() instanceof BlockStainedGlass || blockState.func_177230_c() instanceof BlockStainedGlassPane)) {
                result_dest[0] = -1;
                return;
            }
        }
        int r = colour >> 16 & 0xFF;
        int g = colour >> 8 & 0xFF;
        int b2 = colour & 0xFF;
        if (this.colourType == -1) {
            if (!isFinalBlock) {
                throw new RuntimeException("Can't modify colour type stuff for overlays!");
            }
            mapWriter.getColorTypeCache().getBlockBiomeColour(world, blockState, (BlockPos)mutableGlobalPos, result_dest, block.getBiome());
            this.colourType = (byte)result_dest[0];
            if (result_dest[1] != -1) {
                block.setBiome(result_dest[1]);
            }
            this.customColour = result_dest[2];
        }
        if (this.colourType != 0 && (updateConfig.biomeColorsInVanilla || updateConfig.blockColors == 0)) {
            int c = this.customColour;
            if (this.colourType == 1 || this.colourType == 2) {
                c = biomeColorCalculator.getBiomeColor(blockState, !isFinalBlock, mutableGlobalPos, mapTile, tileChunk.getInRegion().getCaveLayer(), world, mapProcessor, mapWriter.getColorTypeCache());
            }
            final float rMultiplier = r / 255.0f;
            final float gMultiplier = g / 255.0f;
            final float bMultiplier = b2 / 255.0f;
            r = (int)((c >> 16 & 0xFF) * rMultiplier);
            g = (int)((c >> 8 & 0xFF) * gMultiplier);
            b2 = (int)((c & 0xFF) * bMultiplier);
        }
        if (this.glowing) {
            final int total = r + g + b2;
            final float minBrightness = 407.0f;
            final float brightener = Math.max(1.0f, minBrightness / total);
            r *= (int)brightener;
            g *= (int)brightener;
            b2 *= (int)brightener;
            topLightValue = 15;
        }
        int overlayRed = 0;
        int overlayGreen = 0;
        int overlayBlue = 0;
        float currentTransparencyMultiplier = 1.0f;
        final boolean legibleCaveMaps = updateConfig.legibleCaveMaps && caveStart != Integer.MAX_VALUE;
        boolean hasValidOverlay = false;
        if (overlays != null && !overlays.isEmpty()) {
            int sun = 15;
            for (int i = 0; i < overlays.size(); ++i) {
                Overlay o = overlays.get(i);
                if (o.colourType == -1) {
                    mapWriter.getColorTypeCache().getBlockBiomeColour(world, Misc.getStateById(o.state), (BlockPos)mutableGlobalPos, result_dest, block.getBiome());
                    final int overlayColourType = (byte)result_dest[0];
                    if (overlayColourType == -1) {
                        continue;
                    }
                    final int overlayCustomColour = result_dest[2];
                    o = overlayManager.getOriginal(o, overlayColourType, overlayCustomColour);
                    overlays.set(i, o);
                }
                o.getPixelColour(block, result_dest, mapWriter, world, dim, tileChunk, prevChunk, prevChunkDiagonal, prevChunkHorisontal, mapTile, x, z, caveStart, caveDepth, mutableGlobalPos, shadowR, shadowG, shadowB, biomeColorCalculator, mapProcessor, overlayManager, updateConfig);
                if (result_dest[0] != -1) {
                    hasValidOverlay = true;
                    if (i == 0) {
                        topLightValue = o.light;
                    }
                    final float transparency = result_dest[3] / 255.0f;
                    final float overlayIntensity = this.getBlockBrightness((float)lightMin, o.light, sun) * transparency * currentTransparencyMultiplier;
                    overlayRed += (int)(result_dest[0] * overlayIntensity);
                    overlayGreen += (int)(result_dest[1] * overlayIntensity);
                    overlayBlue += (int)(result_dest[2] * overlayIntensity);
                    sun -= o.getOpacity();
                    if (sun < 0) {
                        sun = 0;
                    }
                    currentTransparencyMultiplier *= 1.0f - transparency;
                }
            }
            if (!legibleCaveMaps && hasValidOverlay && !this.glowing && !isAir) {
                brightnessG = (brightnessR = (brightnessB = this.getBlockBrightness((float)lightMin, this.light, sun)));
            }
        }
        if (isFinalBlock) {
            if (block.slopeUnknown) {
                if (!isAir) {
                    block.fixHeightType(x, z, mapTile, tileChunk, prevChunk, prevChunkDiagonal, prevChunkHorisontal, height, false, blockStateShortShapeCache, updateConfig);
                }
                else {
                    block.setVerticalSlope((byte)0);
                    block.setDiagonalSlope((byte)0);
                    block.slopeUnknown = false;
                }
            }
            float depthBrightness = 1.0f;
            final int slopes = updateConfig.terrainSlopes;
            if (legibleCaveMaps) {
                topLightValue = 15;
            }
            if (height != -1) {
                if (legibleCaveMaps && (!isAir || hasValidOverlay)) {
                    int depthCalculationBase = 0;
                    int depthCalculationHeight = height;
                    int depthCalculationBottom = caveStart + 1 - caveDepth;
                    int depthCalculationTop;
                    if ((depthCalculationTop = caveStart) == Integer.MIN_VALUE) {
                        depthCalculationBottom = 0;
                        depthCalculationTop = 63;
                        final int odd = depthCalculationHeight >> 6 & 0x1;
                        depthCalculationHeight = 63 * odd + (1 - 2 * odd) * (depthCalculationHeight & 0x3F);
                        depthCalculationBase = 16;
                    }
                    final int caveRange = 1 + depthCalculationTop - depthCalculationBottom;
                    if (!isAir && !this.glowing) {
                        final float caveBrightness = (1.0f + depthCalculationBase + depthCalculationHeight - depthCalculationBottom) / (depthCalculationBase + caveRange);
                        brightnessR *= caveBrightness;
                        brightnessG *= caveBrightness;
                        brightnessB *= caveBrightness;
                    }
                    if (hasValidOverlay) {
                        depthCalculationHeight = topHeight;
                        if (caveStart == Integer.MIN_VALUE) {
                            final int odd2 = depthCalculationHeight >> 6 & 0x1;
                            depthCalculationHeight = 63 * odd2 + (1 - 2 * odd2) * (depthCalculationHeight & 0x3F);
                        }
                        final float caveBrightness = (1.0f + depthCalculationBase + depthCalculationHeight - depthCalculationBottom) / (depthCalculationBase + caveRange);
                        overlayRed *= (int)caveBrightness;
                        overlayGreen *= (int)caveBrightness;
                        overlayBlue *= (int)caveBrightness;
                    }
                }
                else if (!isAir && !this.glowing && updateConfig.terrainDepth) {
                    if (caveStart == Integer.MAX_VALUE) {
                        depthBrightness = height / 63.0f;
                    }
                    else if (caveStart == Integer.MIN_VALUE) {
                        depthBrightness = 0.7f + 0.3f * height / dim.getDimensionType().getLogicalHeight();
                    }
                    else {
                        final int caveBottom = caveStart - caveDepth;
                        depthBrightness = 0.7f + 0.3f * (height - caveBottom) / caveDepth;
                    }
                    final float max = (slopes >= 2) ? 1.0f : 1.15f;
                    final float min = (slopes >= 2) ? 0.9f : 0.7f;
                    if (depthBrightness > max) {
                        depthBrightness = max;
                    }
                    else if (depthBrightness < min) {
                        depthBrightness = min;
                    }
                }
            }
            if (!isAir && slopes > 0 && !block.slopeUnknown) {
                final int verticalSlope = block.getVerticalSlope();
                if (slopes == 1) {
                    if (verticalSlope > 0) {
                        depthBrightness *= (float)1.15;
                    }
                    else if (verticalSlope < 0) {
                        depthBrightness *= (float)0.85;
                    }
                }
                else {
                    final int diagonalSlope = block.getDiagonalSlope();
                    float ambientLightColored = 0.2f;
                    float ambientLightWhite = 0.5f;
                    float maxDirectLight = 0.6666667f;
                    if (this.glowing) {
                        ambientLightColored = 0.0f;
                        ambientLightWhite = 1.0f;
                        maxDirectLight = 0.22222224f;
                    }
                    float cos = 0.0f;
                    if (slopes == 2) {
                        final float crossZ = (float)(-verticalSlope);
                        if (crossZ < 1.0f) {
                            if (verticalSlope == 1 && diagonalSlope == 1) {
                                cos = 1.0f;
                            }
                            else {
                                final float crossX = (float)(verticalSlope - diagonalSlope);
                                final float cast = 1.0f - crossZ;
                                final float crossMagnitude = (float)Math.sqrt(crossX * crossX + 1.0f + crossZ * crossZ);
                                cos = (float)(cast / crossMagnitude / Math.sqrt(2.0));
                            }
                        }
                    }
                    else if (verticalSlope >= 0) {
                        if (verticalSlope == 1) {
                            cos = 1.0f;
                        }
                        else {
                            final float surfaceDirectionMagnitude = (float)Math.sqrt(verticalSlope * verticalSlope + 1);
                            final float castToMostLit = (float)(verticalSlope + 1);
                            cos = (float)(castToMostLit / surfaceDirectionMagnitude / Math.sqrt(2.0));
                        }
                    }
                    float directLightClamped = 0.0f;
                    if (cos == 1.0f) {
                        directLightClamped = maxDirectLight;
                    }
                    else if (cos > 0.0f) {
                        directLightClamped = (float)Math.ceil(cos * 10.0f) / 10.0f * maxDirectLight * 0.88388f;
                    }
                    final float whiteLight = ambientLightWhite + directLightClamped;
                    brightnessR *= shadowR * ambientLightColored + whiteLight;
                    brightnessG *= shadowG * ambientLightColored + whiteLight;
                    brightnessB *= shadowB * ambientLightColored + whiteLight;
                }
            }
            brightnessR *= depthBrightness;
            brightnessG *= depthBrightness;
            brightnessB *= depthBrightness;
            result_dest[3] = (int)(this.getPixelLight((float)lightMin, topLightValue) * 255.0f);
        }
        else {
            result_dest[3] = (colour >> 24 & 0xFF);
            if (result_dest[3] == 0) {
                result_dest[3] = this.getVanillaTransparency(blockState.func_177230_c());
            }
        }
        result_dest[0] = (int)(r * brightnessR * currentTransparencyMultiplier + overlayRed);
        if (result_dest[0] > 255) {
            result_dest[0] = 255;
        }
        result_dest[1] = (int)(g * brightnessG * currentTransparencyMultiplier + overlayGreen);
        if (result_dest[1] > 255) {
            result_dest[1] = 255;
        }
        result_dest[2] = (int)(b2 * brightnessB * currentTransparencyMultiplier + overlayBlue);
        if (result_dest[2] > 255) {
            result_dest[2] = 255;
        }
    }
    
    public float getBlockBrightness(final float min, final int l, final int sun) {
        return (min + Math.max(sun, l)) / (15.0f + min);
    }
    
    private float getPixelLight(final float min, final int topLightValue) {
        return (topLightValue == 0) ? 0.0f : this.getBlockBrightness(min, topLightValue, 0);
    }
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
    
    public void setLight(final byte light) {
        this.light = light;
    }
    
    public void setGlowing(final boolean glowing) {
        this.glowing = glowing;
    }
    
    public byte getColourType() {
        return this.colourType;
    }
    
    public void setColourType(final byte colourType) {
        this.colourType = colourType;
    }
    
    public int getCustomColour() {
        return this.customColour;
    }
    
    public void setCustomColour(final int customColour) {
        this.customColour = customColour;
    }
}
