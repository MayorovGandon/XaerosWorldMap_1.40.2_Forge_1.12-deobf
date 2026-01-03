//Decompiled by Procyon!

package xaero.map.file.worldsave;

import net.minecraft.util.math.*;
import xaero.map.biome.*;
import xaero.map.cache.*;
import net.minecraft.block.state.*;
import java.util.function.*;
import xaero.map.world.*;
import xaero.map.executor.*;
import xaero.map.*;
import xaero.map.config.primary.option.*;
import xaero.lib.common.config.option.*;
import xaero.map.common.config.option.*;
import java.util.concurrent.*;
import xaero.lib.client.config.*;
import net.minecraft.world.chunk.storage.*;
import xaero.map.region.*;
import net.minecraft.client.*;
import net.minecraft.util.datafix.*;
import java.io.*;
import net.minecraft.block.*;
import xaero.map.mods.*;
import net.minecraft.util.registry.*;
import net.minecraft.util.*;
import net.minecraft.init.*;
import net.minecraft.nbt.*;
import net.minecraft.block.material.*;
import xaero.map.misc.*;
import com.google.common.collect.*;
import net.minecraft.world.chunk.*;
import xaero.map.core.*;
import net.minecraft.world.*;
import java.util.*;

public class WorldDataReader
{
    private MapProcessor mapProcessor;
    private boolean[] shouldEnterGround;
    private boolean[] underair;
    private boolean[] blockFound;
    private byte[] lightLevels;
    private byte[] skyLightLevels;
    private int[] biomeBuffer;
    private int[] topH;
    private MapBlock buildingObject;
    private OverlayBuilder[] overlayBuilders;
    private BlockPos.MutableBlockPos mutableBlockPos;
    private BlockStateColorTypeCache colorTypeCache;
    private BiomeInfoSupplier biomeKeySupplier;
    private BlockStateShortShapeCache blockStateShortShapeCache;
    private final CachedFunction<IBlockState, Boolean> transparentCache;
    private int[] firstTransparentStateY;
    private boolean[] shouldExtendTillTheBottom;
    
    public WorldDataReader(final OverlayManager overlayManager, final BlockStateColorTypeCache colorTypeCache, final BlockStateShortShapeCache blockStateShortShapeCache) {
        this.colorTypeCache = colorTypeCache;
        this.buildingObject = new MapBlock();
        this.underair = new boolean[256];
        this.shouldEnterGround = new boolean[256];
        this.blockFound = new boolean[256];
        this.lightLevels = new byte[256];
        this.skyLightLevels = new byte[256];
        this.biomeBuffer = new int[3];
        this.overlayBuilders = new OverlayBuilder[256];
        this.mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < this.overlayBuilders.length; ++i) {
            this.overlayBuilders[i] = new OverlayBuilder(overlayManager);
        }
        this.biomeKeySupplier = (BiomeInfoSupplier)new BiomeInfoSupplier() {
            public void getBiomeInfo(final BlockStateColorTypeCache colorTypeCache, final World world, final IBlockState state, final BlockPos pos, final int[] dest, final int biomeId) {
                colorTypeCache.getBlockBiomeColour(world, state, pos, dest, biomeId);
            }
        };
        this.topH = new int[256];
        this.blockStateShortShapeCache = blockStateShortShapeCache;
        this.transparentCache = new CachedFunction<IBlockState, Boolean>(new Function<IBlockState, Boolean>() {
            @Override
            public Boolean apply(final IBlockState state) {
                return WorldDataReader.this.mapProcessor.getMapWriter().shouldOverlay(state);
            }
        });
        this.shouldExtendTillTheBottom = new boolean[256];
        this.firstTransparentStateY = new int[256];
    }
    
    public void setMapProcessor(final MapProcessor mapProcessor) {
        this.mapProcessor = mapProcessor;
    }
    
    public boolean buildRegion(final World world, final WorldServer worldServer, final MapDimensionTypeInfo dimType, final MapRegion region, final File worldDir, final boolean loading, final int[] chunkCountDest, final Executor renderExecutor) {
        if (!loading) {
            region.pushWriterPause();
        }
        boolean result = true;
        final int prevRegX = region.getRegionX();
        final int prevRegZ = region.getRegionZ() - 1;
        final MapRegion prevRegion = this.mapProcessor.getLeafMapRegion(region.getCaveLayer(), prevRegX, prevRegZ, false);
        region.updateCaveMode();
        final int caveStart = region.getCaveStart();
        final int caveDepth = region.getCaveDepth();
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        final boolean debugConfig = (boolean)configManager.getPrimaryConfigManager().getEffective((ConfigOption)WorldMapPrimaryClientConfigOptions.DEBUG);
        final boolean worldHasSkylight = dimType == null || dimType.hasSkyLight();
        final boolean ignoreHeightmaps = this.mapProcessor.getMapWorld().isIgnoreHeightmaps() || !worldHasSkylight;
        final boolean flowers = (boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.FLOWERS);
        Label_0519: {
            if (!loading) {
                if (region.getLoadState() != 2) {
                    result = false;
                    break Label_0519;
                }
            }
            try {
                if (worldServer != null) {
                    this.saveUnsavedChunks(worldServer, debugConfig);
                }
            }
            catch (InterruptedException e) {
                WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
            }
            catch (ExecutionException e2) {
                WorldMap.LOGGER.error("suppressed exception", (Throwable)e2);
            }
            final RegionFile regionFile = RegionFileCache.func_76550_a(worldDir, region.getRegionX() * 32, region.getRegionZ() * 32);
            for (int i = 0; i < 8; ++i) {
                for (int j = 0; j < 8; ++j) {
                    MapTileChunk tileChunk = region.getChunk(i, j);
                    if (tileChunk == null) {
                        region.setChunk(i, j, tileChunk = new MapTileChunk(region, (region.getRegionX() << 3) + i, (region.getRegionZ() << 3) + j));
                        synchronized (region) {
                            region.setAllCachePrepared(false);
                        }
                    }
                    if (region.isMetaLoaded()) {
                        tileChunk.getLeafTexture().setBufferedTextureVersion(region.getAndResetCachedTextureVersion(i, j));
                    }
                    this.buildTileChunk(tileChunk, caveStart, caveDepth, worldHasSkylight, ignoreHeightmaps, prevRegion, regionFile, world, dimType, flowers);
                    if (!tileChunk.includeInSave() && !tileChunk.hasHighlightsIfUndiscovered()) {
                        region.setChunk(i, j, null);
                        tileChunk.getLeafTexture().deleteTexturesAndBuffers();
                        tileChunk = null;
                    }
                    else {
                        if (!loading && !tileChunk.includeInSave() && tileChunk.hasHadTerrain()) {
                            tileChunk.getLeafTexture().deleteColorBuffer();
                            tileChunk.unsetHasHadTerrain();
                            tileChunk.setChanged(false);
                        }
                        if (chunkCountDest != null) {
                            final int n = 0;
                            ++chunkCountDest[n];
                        }
                    }
                }
            }
            if (region.isNormalMapData()) {
                region.setLastSaveTime(System.currentTimeMillis() - 60000L + 1500L);
            }
        }
        if (!loading) {
            region.popWriterPause();
        }
        return result;
    }
    
    private void buildTileChunk(final MapTileChunk tileChunk, final int caveStart, final int caveDepth, final boolean worldHasSkylight, final boolean ignoreHeightmaps, final MapRegion prevRegion, final RegionFile regionFile, final World world, final MapDimensionTypeInfo dimType, final boolean flowers) {
        tileChunk.unincludeInSave();
        tileChunk.resetHeights();
        for (int insideX = 0; insideX < 4; ++insideX) {
            for (int insideZ = 0; insideZ < 4; ++insideZ) {
                MapTile tile = tileChunk.getTile(insideX, insideZ);
                final int chunkX = (tileChunk.getX() << 2) + insideX;
                final int chunkZ = (tileChunk.getZ() << 2) + insideZ;
                final DataInputStream datainputstream = regionFile.func_76704_a(chunkX & 0x1F, chunkZ & 0x1F);
                if (datainputstream == null) {
                    if (tile != null) {
                        tileChunk.setChanged(true);
                        tileChunk.setTile(insideX, insideZ, null, this.blockStateShortShapeCache);
                        this.mapProcessor.getTilePool().addToPool(tile);
                    }
                }
                else {
                    boolean createdTile = false;
                    if (tile == null) {
                        tile = this.mapProcessor.getTilePool().get(this.mapProcessor.getCurrentDimension(), chunkX, chunkZ);
                        createdTile = true;
                    }
                    NBTTagCompound nbttagcompound;
                    try {
                        nbttagcompound = CompressedStreamTools.func_74794_a(datainputstream);
                        datainputstream.close();
                    }
                    catch (IOException e2) {
                        try {
                            datainputstream.close();
                        }
                        catch (IOException e1) {
                            WorldMap.LOGGER.error("suppressed exception", (Throwable)e1);
                        }
                        WorldMap.LOGGER.error(String.format("Error loading chunk nbt for chunk %d %d!", chunkX, chunkZ), (Throwable)e2);
                        if (tile != null) {
                            tileChunk.setTile(insideX, insideZ, null, this.blockStateShortShapeCache);
                            this.mapProcessor.getTilePool().addToPool(tile);
                        }
                        continue;
                    }
                    nbttagcompound = Minecraft.func_71410_x().func_184126_aj().func_188257_a((IFixType)FixTypes.CHUNK, nbttagcompound);
                    if (this.buildTile(nbttagcompound, tile, tileChunk, chunkX, chunkZ, chunkX & 0x1F, chunkZ & 0x1F, caveStart, caveDepth, worldHasSkylight, ignoreHeightmaps, world, dimType, flowers)) {
                        tile.setWrittenCave(caveStart, caveDepth);
                        tileChunk.setTile(insideX, insideZ, tile, this.blockStateShortShapeCache);
                        if (createdTile) {
                            tileChunk.setChanged(true);
                        }
                    }
                    else {
                        tileChunk.setTile(insideX, insideZ, null, this.blockStateShortShapeCache);
                        this.mapProcessor.getTilePool().addToPool(tile);
                    }
                }
            }
        }
        if (tileChunk.includeInSave()) {
            tileChunk.setToUpdateBuffers(true);
            tileChunk.setChanged(false);
            tileChunk.setLoadState((byte)2);
        }
    }
    
    private boolean buildTile(final NBTTagCompound nbttagcompound, final MapTile tile, final MapTileChunk tileChunk, final int chunkX, final int chunkZ, final int insideRegionX, final int insideRegionZ, final int caveStart, final int caveDepth, final boolean worldHasSkylight, final boolean ignoreHeightmaps, final World world, final MapDimensionTypeInfo dimType, final boolean flowers) {
        final NBTTagCompound levelCompound = nbttagcompound.func_74775_l("Level");
        if (levelCompound.func_74771_c("TerrainPopulated") == 0) {
            return false;
        }
        final NBTTagList sectionsList = levelCompound.func_150295_c("Sections", 10);
        int fillCounter = 256;
        final int[] topH = this.topH;
        final boolean[] shouldExtendTillTheBottom = this.shouldExtendTillTheBottom;
        final boolean cave = caveStart != Integer.MAX_VALUE;
        final boolean fullCave = caveStart == Integer.MIN_VALUE;
        for (int i = 0; i < this.blockFound.length; ++i) {
            this.overlayBuilders[i].startBuilding();
            this.blockFound[i] = false;
            this.underair[i] = (this.shouldEnterGround[i] = fullCave);
            this.lightLevels[i] = 0;
            this.skyLightLevels[i] = (byte)(worldHasSkylight ? 15 : 0);
            topH[i] = 0;
            shouldExtendTillTheBottom[i] = false;
        }
        final int[] heightMap = levelCompound.func_74759_k("HeightMap");
        final boolean heightMapExists = heightMap.length == 256;
        byte[] biomes = null;
        int[] biomesInt = null;
        boolean biomesDataExists = false;
        if (levelCompound.func_150297_b("Biomes", 7)) {
            biomes = levelCompound.func_74770_j("Biomes");
            biomesDataExists = (biomes.length == 256);
        }
        else if (levelCompound.func_150297_b("Biomes", 11)) {
            biomesInt = levelCompound.func_74759_k("Biomes");
            biomesDataExists = (biomesInt.length == 256);
        }
        final int caveStartSectionHeight = (fullCave ? ((dimType == null) ? 255 : (dimType.getHeight() - 1)) : caveStart) >> 4 << 4;
        int lowH = 0;
        if (cave && !fullCave) {
            lowH = caveStart + 1 - caveDepth;
            if (lowH < 0) {
                lowH = 0;
            }
        }
        final int lowHSection = lowH >> 4 << 4;
        final boolean transparency = true;
        if (sectionsList.func_74745_c() == 0) {
            this.biomeBuffer[0] = (this.biomeBuffer[2] = 0);
            this.biomeBuffer[1] = -1;
            for (int j = 0; j < 16; ++j) {
                for (int k = 0; k < 16; ++k) {
                    final MapBlock currentPixel = tile.getBlock(j, k);
                    this.buildingObject.prepareForWriting();
                    this.buildingObject.write(0, 0, 0, this.biomeBuffer, (byte)0, false, cave);
                    tile.setBlock(j, k, this.buildingObject);
                    if (currentPixel != null) {
                        this.buildingObject = currentPixel;
                    }
                    else {
                        this.buildingObject = new MapBlock();
                    }
                }
            }
        }
        else {
            final NBTTagList tileEntitiesNbt = levelCompound.func_150295_c("TileEntities", 10);
            WorldDataChunkTileEntityLookup tileEntityLookup = null;
            if (!tileEntitiesNbt.func_82582_d()) {
                tileEntityLookup = new WorldDataChunkTileEntityLookup(tileEntitiesNbt);
            }
            int prevSectionHeight = Integer.MAX_VALUE;
            int sectionHeight = Integer.MAX_VALUE;
            for (int l = sectionsList.func_74745_c() - 1; l >= 0 && fillCounter > 0; --l) {
                final NBTTagCompound sectionCompound = sectionsList.func_150305_b(l);
                sectionHeight = sectionCompound.func_74771_c("Y") * 16;
                final boolean hasBlocks = sectionCompound.func_150297_b("Blocks", 7) && sectionHeight >= lowHSection;
                if (l > 0 && !hasBlocks && !sectionCompound.func_150297_b("BlockLight", 7)) {
                    if (!cave) {
                        continue;
                    }
                    if (!sectionCompound.func_150297_b("SkyLight", 7)) {
                        continue;
                    }
                }
                final boolean previousSectionExists = prevSectionHeight - sectionHeight == 16;
                final boolean underAirByDefault = cave && !previousSectionExists && caveStartSectionHeight > sectionHeight;
                final int sectionBasedHeight = sectionHeight + 15;
                final boolean hasPalette = hasBlocks && sectionCompound.func_150297_b("Palette", 11);
                byte[] blockIds_a = null;
                byte[] dataArray = null;
                byte[] addArray = null;
                byte[] add2Array = null;
                int[] palette = null;
                boolean preparedSectionData = false;
                byte[] lightMap = null;
                byte[] skyLightMap = null;
                prevSectionHeight = sectionHeight;
                for (int z = 0; z < 16; ++z) {
                    for (int x = 0; x < 16; ++x) {
                        final int pos_2d = (z << 4) + x;
                        if (!this.blockFound[pos_2d]) {
                            final int heightMapValue = heightMapExists ? heightMap[pos_2d] : 255;
                            int startHeight;
                            if (cave && !fullCave) {
                                startHeight = caveStart;
                            }
                            else if (ignoreHeightmaps || heightMapValue <= 0) {
                                startHeight = sectionBasedHeight;
                            }
                            else {
                                startHeight = heightMapValue + 3;
                            }
                            ++startHeight;
                            if (l <= 0 || startHeight >= sectionHeight) {
                                final int biome = biomesDataExists ? ((biomesInt != null) ? biomesInt[pos_2d] : (biomes[pos_2d] & 0xFF)) : 0;
                                int localStartHeight = 15;
                                if (startHeight >> 4 << 4 == sectionHeight) {
                                    localStartHeight = (startHeight & 0xF);
                                }
                                if (!preparedSectionData) {
                                    if (hasBlocks) {
                                        blockIds_a = sectionCompound.func_74770_j("Blocks");
                                        addArray = (byte[])(sectionCompound.func_150297_b("Add", 7) ? sectionCompound.func_74770_j("Add") : null);
                                        add2Array = (byte[])(sectionCompound.func_150297_b("Add2", 7) ? sectionCompound.func_74770_j("Add2") : null);
                                        dataArray = sectionCompound.func_74770_j("Data");
                                    }
                                    if (hasPalette) {
                                        palette = sectionCompound.func_74759_k("Palette");
                                    }
                                    if (sectionCompound.func_150297_b("BlockLight", 7)) {
                                        lightMap = sectionCompound.func_74770_j("BlockLight");
                                        if (lightMap.length != 2048) {
                                            lightMap = null;
                                        }
                                    }
                                    if (cave && sectionCompound.func_150297_b("SkyLight", 7)) {
                                        skyLightMap = sectionCompound.func_74770_j("SkyLight");
                                        if (skyLightMap.length != 2048) {
                                            skyLightMap = null;
                                        }
                                    }
                                    preparedSectionData = true;
                                }
                                if (underAirByDefault) {
                                    this.underair[pos_2d] = true;
                                }
                                for (int y = localStartHeight; y >= 0; --y) {
                                    int h = sectionHeight + y;
                                    final int pos = y << 8 | pos_2d;
                                    int blockId = 0;
                                    int blockMeta = 0;
                                    if (hasPalette) {
                                        final int leftIndexPart = hasBlocks ? ((blockIds_a[pos] & 0xFF) << 4) : 0;
                                        final int paletteIndex = leftIndexPart | this.nibbleValue(dataArray, pos);
                                        final int blockStateOtherId = palette[paletteIndex];
                                        blockId = blockStateOtherId >> 4;
                                        blockMeta = (blockStateOtherId & 0xF);
                                    }
                                    else if (hasBlocks) {
                                        blockId = ((blockIds_a[pos] & 0xFF) | ((addArray == null) ? 0 : (this.nibbleValue(addArray, pos) << 8)));
                                        if (add2Array != null) {
                                            blockId |= this.nibbleValue(add2Array, pos) << 12;
                                        }
                                        blockMeta = this.nibbleValue(dataArray, pos);
                                    }
                                    IBlockState state = Block.func_149729_e(blockId).func_176203_a(blockMeta);
                                    if (state != null && tileEntityLookup != null && !(state.func_177230_c() instanceof BlockAir) && SupportMods.framedBlocks() && SupportMods.supportFramedBlocks.isFrameBlock(null, state)) {
                                        final NBTTagCompound tileEntityNbt = tileEntityLookup.getTileEntityNbt(x, h, z);
                                        if (tileEntityNbt != null) {
                                            if (tileEntityNbt.func_150297_b("camo_state", 10)) {
                                                try {
                                                    state = NBTUtil.func_190008_d(tileEntityNbt.func_74775_l("camo_state"));
                                                }
                                                catch (IllegalArgumentException iae) {
                                                    state = null;
                                                }
                                            }
                                            else if (tileEntityNbt.func_150297_b("camo", 10)) {
                                                final NBTTagCompound camoNbt = tileEntityNbt.func_74775_l("camo");
                                                if (camoNbt.func_150297_b("state", 10)) {
                                                    try {
                                                        state = NBTUtil.func_190008_d(camoNbt.func_74775_l("state"));
                                                    }
                                                    catch (IllegalArgumentException iae2) {
                                                        state = null;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    int stateId = Block.func_176210_f(state);
                                    this.mutableBlockPos.func_181079_c(chunkX << 4 | x, sectionHeight | y, chunkZ << 4 | z);
                                    final OverlayBuilder overlayBuilder = this.overlayBuilders[pos_2d];
                                    if (!shouldExtendTillTheBottom[pos_2d] && !overlayBuilder.isEmpty() && this.firstTransparentStateY[pos_2d] - h >= 5) {
                                        shouldExtendTillTheBottom[pos_2d] = true;
                                    }
                                    boolean buildResult = h >= lowH && h < startHeight && this.buildPixel(this.buildingObject, state, stateId, x, h, z, pos_2d, this.biomeBuffer, this.lightLevels[pos_2d], this.skyLightLevels[pos_2d], biome, cave, fullCave, overlayBuilder, world, this.mutableBlockPos, topH, shouldExtendTillTheBottom[pos_2d], flowers, transparency);
                                    if (!buildResult && ((y == 0 && l == 0) || h <= lowH)) {
                                        this.lightLevels[pos_2d] = 0;
                                        if (cave) {
                                            this.skyLightLevels[pos_2d] = 0;
                                        }
                                        h = 0;
                                        stateId = 0;
                                        state = Blocks.field_150350_a.func_176223_P();
                                        buildResult = true;
                                    }
                                    if (buildResult) {
                                        this.buildingObject.prepareForWriting();
                                        overlayBuilder.finishBuilding(this.buildingObject);
                                        this.colorTypeCache.getBlockBiomeColour(world, state, (BlockPos)this.mutableBlockPos, this.biomeBuffer, biome);
                                        if (overlayBuilder.getOverlayBiome() != -1) {
                                            this.biomeBuffer[1] = overlayBuilder.getOverlayBiome();
                                        }
                                        final boolean glowing = this.mapProcessor.getMapWriter().isGlowing(state);
                                        byte light = this.lightLevels[pos_2d];
                                        if (cave && light < 15 && this.buildingObject.getNumberOfOverlays() == 0) {
                                            final byte skyLight = this.skyLightLevels[pos_2d];
                                            if (skyLight > light) {
                                                light = skyLight;
                                            }
                                        }
                                        this.buildingObject.write(stateId, h, topH[pos_2d], this.biomeBuffer, light, glowing, cave);
                                        final MapBlock currentPixel2 = tile.getBlock(x, z);
                                        final boolean equalsSlopesExcluded = this.buildingObject.equalsSlopesExcluded(currentPixel2);
                                        final boolean fullyEqual = this.buildingObject.equals(currentPixel2, equalsSlopesExcluded);
                                        if (!fullyEqual) {
                                            tile.setBlock(x, z, this.buildingObject);
                                            if (currentPixel2 != null) {
                                                this.buildingObject = currentPixel2;
                                            }
                                            else {
                                                this.buildingObject = new MapBlock();
                                            }
                                            if (!equalsSlopesExcluded) {
                                                tileChunk.setChanged(true);
                                            }
                                        }
                                        this.blockFound[pos_2d] = true;
                                        --fillCounter;
                                        break;
                                    }
                                    final byte dataLight = (byte)((lightMap == null) ? 0 : this.nibbleValue(lightMap, pos));
                                    if (cave && dataLight < 15 && worldHasSkylight) {
                                        byte dataSkyLight;
                                        if (!ignoreHeightmaps && !fullCave && startHeight > heightMapValue) {
                                            dataSkyLight = 15;
                                        }
                                        else {
                                            dataSkyLight = (byte)((skyLightMap == null) ? 0 : this.nibbleValue(skyLightMap, pos));
                                        }
                                        this.skyLightLevels[pos_2d] = dataSkyLight;
                                    }
                                    this.lightLevels[pos_2d] = dataLight;
                                }
                            }
                        }
                    }
                }
            }
        }
        tile.setWrittenOnce(true);
        tile.setLoaded(true);
        tile.setWorldInterpretationVersion(1);
        return true;
    }
    
    private boolean buildPixel(final MapBlock pixel, final IBlockState state, final int stateId, final int x, final int h, final int z, final int pos_2d, final int[] biomeBuffer, final byte light, final byte skyLight, final int dataBiome, final boolean cave, final boolean fullCave, final OverlayBuilder overlayBuilder, final World world, final BlockPos.MutableBlockPos mutableBlockPos, final int[] topH, final boolean shouldExtendTillTheBottom, final boolean flowers, final boolean transparency) {
        final Block b = state.func_177230_c();
        if (b instanceof BlockAir) {
            this.underair[pos_2d] = true;
            return false;
        }
        if (!this.underair[pos_2d] && cave) {
            return false;
        }
        if (this.mapProcessor.getMapWriter().isInvisible(state, b, flowers)) {
            return false;
        }
        if (cave && this.shouldEnterGround[pos_2d]) {
            if (!state.func_185904_a().func_76217_h() && !state.func_185904_a().func_76222_j() && state.func_185904_a().func_186274_m() != EnumPushReaction.DESTROY && !this.shouldOverlayCached(state)) {
                this.underair[pos_2d] = false;
                this.shouldEnterGround[pos_2d] = false;
            }
            return false;
        }
        final int lightOpacity = state.getLightOpacity((IBlockAccess)world, (BlockPos)mutableBlockPos);
        if (this.shouldOverlayCached(state)) {
            if (h > topH[pos_2d]) {
                topH[pos_2d] = h;
            }
            byte overlayLight = light;
            if (overlayBuilder.isEmpty()) {
                this.firstTransparentStateY[pos_2d] = h;
                if (cave && skyLight > overlayLight) {
                    overlayLight = skyLight;
                }
            }
            if (shouldExtendTillTheBottom) {
                overlayBuilder.getCurrentOverlay().increaseOpacity(Misc.getStateById(overlayBuilder.getCurrentOverlay().getState()).getLightOpacity((IBlockAccess)world, (BlockPos)mutableBlockPos));
            }
            else {
                overlayBuilder.build(stateId, biomeBuffer, lightOpacity, overlayLight, world, this.mapProcessor, (BlockPos)mutableBlockPos, dataBiome, this.colorTypeCache, this.biomeKeySupplier);
            }
            return !transparency;
        }
        if (!this.mapProcessor.getMapWriter().hasVanillaColor(state, world, (BlockPos)mutableBlockPos)) {
            return false;
        }
        if (cave && !this.underair[pos_2d]) {
            return true;
        }
        if (h > topH[pos_2d]) {
            topH[pos_2d] = h;
        }
        return true;
    }
    
    private boolean shouldOverlayCached(final IBlockState state) {
        return this.transparentCache.apply(state);
    }
    
    private byte nibbleValue(final byte[] array, final int index) {
        final byte b = array[index >> 1];
        if ((index & 0x1) == 0x0) {
            return (byte)(b & 0xF);
        }
        return (byte)(b >> 4 & 0xF);
    }
    
    private void saveUnsavedChunks(final WorldServer worldServer, final boolean debugConfig) throws InterruptedException, ExecutionException {
        worldServer.func_73046_m().func_152344_a((Runnable)new Runnable() {
            @Override
            public void run() {
                final long before = System.currentTimeMillis();
                final List<Chunk> allChunks = (List<Chunk>)Lists.newArrayList((Iterable)worldServer.func_72863_F().func_189548_a());
                int saveCount = 0;
                for (final Chunk chunk : allChunks) {
                    final IWorldMapChunk chunkAccess = (IWorldMapChunk)chunk;
                    if (chunkAccess.xaero_getLastSaveTime() != 0L) {
                        continue;
                    }
                    try {
                        chunk.func_177432_b(worldServer.func_82737_E());
                        worldServer.func_72863_F().field_73247_e.func_75816_a((World)worldServer, chunk);
                        chunk.func_177427_f(false);
                        ++saveCount;
                    }
                    catch (MinecraftException ex) {}
                    catch (IOException ex2) {}
                }
                worldServer.func_72863_F().func_104112_b();
                if (debugConfig) {
                    WorldMap.LOGGER.info("saved {} chunks in {} ms", (Object)saveCount, (Object)(System.currentTimeMillis() - before));
                }
            }
        }).get();
    }
}
