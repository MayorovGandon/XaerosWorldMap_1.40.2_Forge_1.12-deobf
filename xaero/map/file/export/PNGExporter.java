//Decompiled by Procyon!

package xaero.map.file.export;

import xaero.map.*;
import xaero.map.config.primary.option.*;
import xaero.lib.common.config.option.*;
import net.minecraft.client.renderer.*;
import xaero.map.exception.*;
import xaero.map.graphics.*;
import org.lwjgl.*;
import xaero.map.common.config.option.*;
import xaero.map.mods.*;
import xaero.map.gui.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import xaero.map.misc.*;
import xaero.map.world.*;
import xaero.lib.client.config.*;
import xaero.lib.common.config.single.*;
import xaero.lib.common.config.*;
import java.util.*;
import java.nio.*;
import xaero.map.cache.*;
import xaero.map.biome.*;
import xaero.map.file.*;
import java.io.*;
import xaero.map.region.*;
import xaero.map.region.texture.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import javax.imageio.*;
import java.awt.image.*;

public class PNGExporter
{
    private final Calendar calendar;
    private Path destinationPath;
    
    public PNGExporter(final Path destinationPath) {
        this.calendar = Calendar.getInstance();
        this.destinationPath = destinationPath;
    }
    
    public PNGExportResult export(final MapProcessor mapProcessor, final MapTileSelection selection) throws IllegalArgumentException, IllegalAccessException, OpenGLException {
        if (!mapProcessor.getMapSaveLoad().isRegionDetectionComplete()) {
            return new PNGExportResult(PNGExportResultType.NOT_PREPARED, null);
        }
        final int exportedLayer = mapProcessor.getCurrentCaveLayer();
        final MapDimension dim = mapProcessor.getMapWorld().getCurrentDimension();
        final Set<LeveledRegion<?>> list = dim.getLayeredMapRegions().getUnsyncedSet();
        if (list.isEmpty()) {
            return new PNGExportResult(PNGExportResultType.EMPTY, null);
        }
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        final SingleConfigManager<Config> primaryConfigManager = (SingleConfigManager<Config>)configManager.getPrimaryConfigManager();
        final boolean multipleImagesSetting = (boolean)primaryConfigManager.getEffective((ConfigOption)WorldMapPrimaryClientConfigOptions.EXPORT_MULTIPLE_IMAGES);
        final boolean nightExportSetting = (boolean)primaryConfigManager.getEffective((ConfigOption)WorldMapPrimaryClientConfigOptions.NIGHT_EXPORT);
        final int exportScaleDownSquareSetting = (int)primaryConfigManager.getEffective((ConfigOption)WorldMapPrimaryClientConfigOptions.EXPORT_SCALE_DOWN_SQUARE);
        final boolean includingHighlights = (boolean)primaryConfigManager.getEffective((ConfigOption)WorldMapPrimaryClientConfigOptions.EXPORT_HIGHLIGHTS);
        final boolean full = selection == null;
        Integer minX = null;
        Integer maxX = null;
        Integer minZ = null;
        Integer maxZ = null;
        final MapLayer mapLayer = dim.getLayeredMapRegions().getLayer(exportedLayer);
        if (full) {
            for (final LeveledRegion<?> region : list) {
                if (region.getLevel() == 0 && ((MapRegion)region).hasHadTerrain()) {
                    if (region.getCaveLayer() != exportedLayer) {
                        continue;
                    }
                    if (minX == null || region.getRegionX() < minX) {
                        minX = region.getRegionX();
                    }
                    if (maxX == null || region.getRegionX() > maxX) {
                        maxX = region.getRegionX();
                    }
                    if (minZ == null || region.getRegionZ() < minZ) {
                        minZ = region.getRegionZ();
                    }
                    if (maxZ != null && region.getRegionZ() <= maxZ) {
                        continue;
                    }
                    maxZ = region.getRegionZ();
                }
            }
            final Iterable<Hashtable<Integer, RegionDetection>> regionDetectionIterable = dim.isUsingWorldSave() ? dim.getWorldSaveDetectedRegions() : mapLayer.getDetectedRegions().values();
            for (final Hashtable<Integer, RegionDetection> column : regionDetectionIterable) {
                for (final RegionDetection regionDetection : column.values()) {
                    if (!regionDetection.isHasHadTerrain()) {
                        continue;
                    }
                    if (minX == null || regionDetection.getRegionX() < minX) {
                        minX = regionDetection.getRegionX();
                    }
                    if (maxX == null || regionDetection.getRegionX() > maxX) {
                        maxX = regionDetection.getRegionX();
                    }
                    if (minZ == null || regionDetection.getRegionZ() < minZ) {
                        minZ = regionDetection.getRegionZ();
                    }
                    if (maxZ != null && regionDetection.getRegionZ() <= maxZ) {
                        continue;
                    }
                    maxZ = regionDetection.getRegionZ();
                }
            }
        }
        else {
            minX = selection.getLeft() >> 5;
            minZ = selection.getTop() >> 5;
            maxX = selection.getRight() >> 5;
            maxZ = selection.getBottom() >> 5;
        }
        int minBlockX = minX * 512;
        int minBlockZ = minZ * 512;
        int maxBlockX = (maxX + 1) * 512 - 1;
        int maxBlockZ = (maxZ + 1) * 512 - 1;
        if (!full) {
            minBlockX = Math.max(minBlockX, selection.getLeft() << 4);
            minBlockZ = Math.max(minBlockZ, selection.getTop() << 4);
            maxBlockX = Math.min(maxBlockX, (selection.getRight() << 4) + 15);
            maxBlockZ = Math.min(maxBlockZ, (selection.getBottom() << 4) + 15);
        }
        final int exportAreaWidthInRegions = maxX - minX + 1;
        final int exportAreaHeightInRegions = maxZ - minZ + 1;
        final long exportAreaSizeInRegions = exportAreaWidthInRegions * (long)exportAreaHeightInRegions;
        int exportAreaWidth = exportAreaWidthInRegions * 512;
        int exportAreaHeight = exportAreaHeightInRegions * 512;
        if (!full) {
            exportAreaWidth = maxBlockX - minBlockX + 1;
            exportAreaHeight = maxBlockZ - minBlockZ + 1;
        }
        final int scaleDownSquareSquared = exportScaleDownSquareSetting * exportScaleDownSquareSetting;
        final float scale = (exportAreaSizeInRegions < scaleDownSquareSquared || multipleImagesSetting || scaleDownSquareSquared <= 0) ? 1.0f : ((float)(exportScaleDownSquareSetting / Math.sqrt((double)exportAreaSizeInRegions)));
        int exportImageWidth = (int)(exportAreaWidth * scale);
        int exportImageHeight = (int)(exportAreaHeight * scale);
        if (!multipleImagesSetting && scaleDownSquareSquared > 0) {
            final long maxExportAreaSizeInRegions = scaleDownSquareSquared * 262144L;
            if (exportAreaWidth * (long)exportAreaHeight / 512L / 512L > maxExportAreaSizeInRegions) {
                return new PNGExportResult(PNGExportResultType.TOO_BIG, null);
            }
        }
        final int maxTextureSize = GlStateManager.func_187397_v(3379);
        OpenGLException.checkGLError();
        final int frameWidth = Math.min(1024, Math.min(maxTextureSize, exportImageWidth));
        final int frameHeight = Math.min(1024, Math.min(maxTextureSize, exportImageHeight));
        final int horizontalFrames = (int)Math.ceil(exportImageWidth / (double)frameWidth);
        final int verticalFrames = (int)Math.ceil(exportImageHeight / (double)frameHeight);
        final boolean multipleImages = multipleImagesSetting && horizontalFrames * verticalFrames > 1;
        if (multipleImages) {
            exportImageWidth = frameWidth;
            exportImageHeight = frameHeight;
        }
        final int pixelCount = exportImageWidth * exportImageHeight;
        if (pixelCount == Integer.MAX_VALUE || pixelCount / exportImageHeight != exportImageWidth) {
            return new PNGExportResult(PNGExportResultType.IMAGE_TOO_BIG, null);
        }
        final boolean debugConfig = (boolean)primaryConfigManager.getEffective((ConfigOption)WorldMapPrimaryClientConfigOptions.DEBUG);
        if (debugConfig) {
            WorldMap.LOGGER.info(String.format("Exporting PNG of size %dx%d using a framebuffer of size %dx%d.", exportImageWidth, exportImageHeight, frameWidth, frameHeight));
        }
        BufferedImage image;
        ImprovedFramebuffer exportFrameBuffer;
        ByteBuffer frameDataBuffer;
        int[] bufferArray;
        try {
            image = new BufferedImage(exportImageWidth, exportImageHeight, 1);
            exportFrameBuffer = new ImprovedFramebuffer(frameWidth, frameHeight, false);
            frameDataBuffer = BufferUtils.createByteBuffer(frameWidth * frameHeight * 4);
            bufferArray = new int[frameWidth * frameHeight];
        }
        catch (OutOfMemoryError oome) {
            return new PNGExportResult(PNGExportResultType.OUT_OF_MEMORY, null);
        }
        if (exportFrameBuffer.field_147616_f == -1) {
            return new PNGExportResult(PNGExportResultType.BAD_FBO, null);
        }
        final MapUpdateFastConfig updateConfig = new MapUpdateFastConfig();
        final boolean lighting = (boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.LIGHTING);
        final BlockStateShortShapeCache shortShapeCache = mapProcessor.getBlockStateShortShapeCache();
        final BiomeColorCalculator biomeColorCalculator = mapProcessor.getBiomeColorCalculator();
        final OverlayManager overlayManager = mapProcessor.getOverlayManager();
        final MapSaveLoad mapSaveLoad = mapProcessor.getMapSaveLoad();
        GlStateManager.func_179123_a();
        GlStateManager.func_179140_f();
        GlStateManager.func_179128_n(5889);
        GlStateManager.func_179096_D();
        GlStateManager.func_179130_a(0.0, (double)frameWidth, 0.0, (double)frameHeight, 0.0, 1000.0);
        GlStateManager.func_179128_n(5888);
        GlStateManager.func_179094_E();
        GlStateManager.func_179096_D();
        GlStateManager.func_179129_p();
        GlStateManager.func_179084_k();
        exportFrameBuffer.func_147610_a(true);
        GlStateManager.func_179094_E();
        GlStateManager.func_179152_a(scale, scale, 1.0f);
        final float brightness = nightExportSetting ? mapProcessor.getAmbientBrightness(dim.getDimensionType()) : mapProcessor.getBrightness(exportedLayer, mapProcessor.getWorld(), lighting && exportedLayer != Integer.MAX_VALUE);
        final boolean oldMinimapMessesUpTextureFilter = SupportMods.minimap() && SupportMods.xaeroMinimap.compatibilityVersion < 11;
        final boolean[] justMetaDest = { false };
        Path imageDestination = this.destinationPath;
        if (multipleImages) {
            imageDestination = this.destinationPath.resolve(this.getExportBaseName());
        }
        boolean empty = true;
        PNGExportResultType resultType = PNGExportResultType.SUCCESS;
        for (int i = 0; i < horizontalFrames; ++i) {
            for (int j = 0; j < verticalFrames; ++j) {
                boolean renderedSomething = false;
                GlStateManager.func_179144_i(0);
                GlStateManager.func_179082_a(0.0f, 0.0f, 0.0f, 1.0f);
                GlStateManager.func_179086_m(16640);
                GlStateManager.func_179094_E();
                final float frameLeft = minBlockX + i * frameWidth / scale;
                float frameRight = minBlockX + (i + 1) * frameWidth / scale - 1.0f;
                final float frameTop = minBlockZ + j * frameHeight / scale;
                float frameBottom = minBlockZ + (j + 1) * frameHeight / scale - 1.0f;
                if (!full) {
                    if (maxBlockX < frameRight) {
                        frameRight = (float)maxBlockX;
                    }
                    if (maxBlockZ < frameBottom) {
                        frameBottom = (float)maxBlockZ;
                    }
                }
                final int minTileChunkX = (int)Math.floor(frameLeft) >> 6;
                final int maxTileChunkX = (int)Math.floor(frameRight) >> 6;
                final int minTileChunkZ = (int)Math.floor(frameTop) >> 6;
                final int maxTileChunkZ = (int)Math.floor(frameBottom) >> 6;
                final int minRegionX = minTileChunkX >> 3;
                final int minRegionZ = minTileChunkZ >> 3;
                final int maxRegionX = maxTileChunkX >> 3;
                final int maxRegionZ = maxTileChunkZ >> 3;
                GlStateManager.func_179137_b(0.1, 0.0, 0.0);
                for (int regionX = minRegionX; regionX <= maxRegionX; ++regionX) {
                    for (int regionZ = minRegionZ; regionZ <= maxRegionZ; ++regionZ) {
                        MapRegionInfo regionInfo;
                        final MapRegion originalRegion = (MapRegion)(regionInfo = mapProcessor.getLeafMapRegion(exportedLayer, regionX, regionZ, false));
                        if (originalRegion == null && mapLayer.regionDetectionExists(regionX, regionZ)) {
                            regionInfo = mapLayer.getRegionDetection(regionX, regionZ);
                        }
                        final boolean regionHasHighlightsIfUndiscovered = includingHighlights && dim.getHighlightHandler().shouldApplyRegionHighlights(regionX, regionZ, false);
                        if (regionInfo != null || regionHasHighlightsIfUndiscovered) {
                            File cacheFile = null;
                            boolean loadingFromCache = regionInfo != null && (originalRegion == null || !originalRegion.isBeingWritten() || originalRegion.getLoadState() != 2);
                            if (loadingFromCache) {
                                cacheFile = regionInfo.getCacheFile();
                                if (cacheFile == null && !regionInfo.hasLookedForCache()) {
                                    try {
                                        cacheFile = mapSaveLoad.getCacheFile(regionInfo, exportedLayer, true, false);
                                    }
                                    catch (IOException ex) {}
                                }
                                if (cacheFile == null) {
                                    if (!regionHasHighlightsIfUndiscovered) {
                                        continue;
                                    }
                                    loadingFromCache = false;
                                }
                            }
                            final ExportMapRegion region2 = new ExportMapRegion(dim, regionX, regionZ, exportedLayer);
                            if (loadingFromCache) {
                                region2.setShouldCache(true, "png");
                                region2.setHasHadTerrain();
                                region2.setCacheFile(cacheFile);
                                region2.loadCacheTextures(mapProcessor, false, null, 0, null, justMetaDest, 1);
                            }
                            else if (originalRegion != null) {
                                for (int o = 0; o < 8; ++o) {
                                    for (int p = 0; p < 8; ++p) {
                                        final MapTileChunk originalTileChunk = originalRegion.getChunk(o, p);
                                        if (originalTileChunk != null && originalTileChunk.hasHadTerrain()) {
                                            final MapTileChunk tileChunk = region2.createTexture(o, p).getTileChunk();
                                            for (int tx = 0; tx < 4; ++tx) {
                                                for (int tz = 0; tz < 4; ++tz) {
                                                    tileChunk.setTile(tx, tz, originalTileChunk.getTile(tx, tz), shortShapeCache);
                                                }
                                            }
                                            tileChunk.setLoadState((byte)2);
                                            tileChunk.updateBuffers(mapProcessor, biomeColorCalculator, overlayManager, WorldMap.detailed_debug, shortShapeCache, updateConfig);
                                        }
                                    }
                                }
                            }
                            if (includingHighlights) {
                                mapProcessor.getMapRegionHighlightsPreparer().prepare(region2, true);
                            }
                            GuiMap.setupTextureMatricesAndTextures(brightness);
                            for (int localChunkX = 0; localChunkX < 8; ++localChunkX) {
                                for (int localChunkZ = 0; localChunkZ < 8; ++localChunkZ) {
                                    final ExportMapTileChunk tileChunk2 = region2.getChunk(localChunkX, localChunkZ);
                                    if (tileChunk2 != null) {
                                        final ExportLeafRegionTexture tileChunkTexture = tileChunk2.getLeafTexture();
                                        if (tileChunkTexture != null) {
                                            if (tileChunk2.getX() < minTileChunkX || tileChunk2.getX() > maxTileChunkX || tileChunk2.getZ() < minTileChunkZ || tileChunk2.getZ() > maxTileChunkZ) {
                                                tileChunkTexture.deleteColorBuffer();
                                            }
                                            else {
                                                final int textureId = tileChunkTexture.bindColorTexture(true);
                                                if (tileChunkTexture.getColorBuffer() == null) {
                                                    tileChunkTexture.prepareBuffer();
                                                }
                                                final ByteBuffer colorBuffer = tileChunkTexture.getDirectColorBuffer();
                                                if (includingHighlights) {
                                                    tileChunkTexture.applyHighlights(dim.getHighlightHandler(), tileChunkTexture.getColorBuffer());
                                                }
                                                if (tileChunkTexture.isColorBufferCompressed()) {
                                                    GL13.glCompressedTexImage2D(3553, 0, tileChunkTexture.getColorBufferFormat(), 64, 64, 0, colorBuffer);
                                                }
                                                else {
                                                    final int internalFormat = (tileChunkTexture.getColorBufferFormat() == -1) ? 32856 : tileChunkTexture.getColorBufferFormat();
                                                    GL11.glTexImage2D(3553, 0, internalFormat, 64, 64, 0, 32993, 32821, colorBuffer);
                                                }
                                                tileChunkTexture.deleteColorBuffer();
                                                if (textureId != -1) {
                                                    GL11.glTexParameteri(3553, 33085, 9);
                                                    GlStateManager.func_187403_b(3553, 33083, 9.0f);
                                                    exportFrameBuffer.generateMipmaps();
                                                    GlStateManager.func_187421_b(3553, 10241, 9987);
                                                    GuiMap.bindMapTextureWithLighting3(tileChunkTexture, 9728, oldMinimapMessesUpTextureFilter, 0, tileChunkTexture.getBufferHasLight());
                                                    GuiMap.renderTexturedModalRectWithLighting2(tileChunk2.getX() * 64 - frameLeft, tileChunk2.getZ() * 64 - frameTop, 64.0f, 64.0f, tileChunkTexture.getBufferHasLight());
                                                    renderedSomething = true;
                                                    GlStateManager.func_187421_b(3553, 10241, 9729);
                                                    WorldMap.glObjectDeleter.requestTextureDeletion(textureId);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            GuiMap.restoreTextureStates();
                            GlStateManager.func_179144_i(0);
                            WorldMap.glObjectDeleter.work();
                        }
                    }
                }
                GlStateManager.func_179121_F();
                GlStateManager.func_179124_c(1.0f, 1.0f, 1.0f);
                if (renderedSomething) {
                    empty = false;
                    exportFrameBuffer.func_147612_c();
                    frameDataBuffer.clear();
                    GL11.glGetTexImage(3553, 0, 32993, 33639, frameDataBuffer);
                    frameDataBuffer.asIntBuffer().get(bufferArray);
                    int insertOffsetX = i * frameWidth;
                    int insertOffsetZ = j * frameHeight;
                    if (multipleImages) {
                        insertOffsetX = 0;
                        insertOffsetZ = 0;
                    }
                    final int actualFrameWidth = Math.min(frameWidth, exportImageWidth - insertOffsetX);
                    final int actualFrameHeight = Math.min(frameHeight, exportImageHeight - insertOffsetZ);
                    image.setRGB(insertOffsetX, insertOffsetZ, actualFrameWidth, actualFrameHeight, bufferArray, 0, frameWidth);
                    if (multipleImages) {
                        final PNGExportResultType saveResult = this.saveImage(image, imageDestination, i + "_" + j, "_x" + (int)frameLeft + "_z" + (int)frameTop);
                        if (saveResult != PNGExportResultType.SUCCESS) {
                            resultType = saveResult;
                        }
                    }
                }
            }
        }
        GlStateManager.func_179121_F();
        GlStateManager.func_179099_b();
        exportFrameBuffer.func_147609_e();
        GlStateManager.func_179089_o();
        GlStateManager.func_179121_F();
        GlStateManager.func_179128_n(5889);
        Minecraft.func_71410_x().func_147110_a().func_147610_a(false);
        Misc.minecraftOrtho(new ScaledResolution(Minecraft.func_71410_x()));
        GlStateManager.func_179128_n(5888);
        GlStateManager.func_179144_i(0);
        exportFrameBuffer.func_147608_a();
        mapProcessor.getBufferDeallocator().deallocate(frameDataBuffer, debugConfig);
        if (empty) {
            return new PNGExportResult(PNGExportResultType.EMPTY, null);
        }
        if (multipleImages) {
            image.flush();
            return new PNGExportResult(resultType, imageDestination);
        }
        resultType = this.saveImage(image, imageDestination, null, "_x" + minBlockX + "_z" + minBlockZ);
        image.flush();
        return new PNGExportResult(resultType, imageDestination);
    }
    
    private PNGExportResultType saveImage(final BufferedImage image, final Path destinationPath, String baseName, final String suffix) {
        if (baseName == null) {
            baseName = this.getExportBaseName();
        }
        baseName += suffix;
        int additionalIndex = 1;
        try {
            if (!Files.exists(destinationPath, new LinkOption[0])) {
                Files.createDirectories(destinationPath, (FileAttribute<?>[])new FileAttribute[0]);
            }
            Path imagePath;
            for (imagePath = destinationPath.resolve(baseName + ".png"); Files.exists(imagePath, new LinkOption[0]); imagePath = destinationPath.resolve(baseName + "_" + additionalIndex + ".png")) {
                ++additionalIndex;
            }
            ImageIO.write(image, "png", imagePath.toFile());
            return PNGExportResultType.SUCCESS;
        }
        catch (IOException e1) {
            WorldMap.LOGGER.error("IO exception while exporting PNG: ", (Throwable)e1);
            return PNGExportResultType.IO_EXCEPTION;
        }
    }
    
    private String getExportBaseName() {
        this.calendar.setTimeInMillis(System.currentTimeMillis());
        final int year = this.calendar.get(1);
        final int month = 1 + this.calendar.get(2);
        final int day = this.calendar.get(5);
        final int hours = this.calendar.get(11);
        final int minutes = this.calendar.get(12);
        final int seconds = this.calendar.get(13);
        return String.format("%d-%02d-%02d_%02d.%02d.%02d", year, month, day, hours, minutes, seconds);
    }
}
