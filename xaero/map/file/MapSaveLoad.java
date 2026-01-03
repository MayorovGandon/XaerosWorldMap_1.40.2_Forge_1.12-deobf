//Decompiled by Procyon!

package xaero.map.file;

import xaero.map.world.*;
import xaero.map.biome.*;
import xaero.map.cache.*;
import net.minecraft.world.*;
import net.minecraft.block.state.*;
import net.minecraft.util.math.*;
import xaero.map.gui.*;
import xaero.map.*;
import xaero.map.task.*;
import net.minecraft.client.*;
import java.awt.*;
import net.minecraft.client.gui.*;
import xaero.map.file.export.*;
import java.nio.file.attribute.*;
import java.util.function.*;
import java.util.stream.*;
import xaero.map.config.util.*;
import java.util.*;
import java.util.regex.*;
import java.nio.file.*;
import java.util.zip.*;
import java.io.*;
import xaero.map.file.worldsave.*;
import xaero.map.config.primary.option.*;
import xaero.lib.common.config.option.*;
import xaero.map.common.config.option.*;
import xaero.lib.common.util.*;
import xaero.lib.client.config.*;
import xaero.lib.common.config.single.*;
import xaero.lib.common.config.*;
import xaero.map.region.*;
import net.minecraft.init.*;
import net.minecraft.block.*;
import xaero.map.misc.*;

public class MapSaveLoad
{
    private static final int currentSaveMajorVersion = 0;
    private static final int currentSaveMinorVersion = 8;
    private static final int currentSaveVersion = 8;
    public static final int SAVE_TIME = 60000;
    public static final int currentCacheSaveVersion = 24;
    private ArrayList<MapRegion> toSave;
    private ArrayList<MapRegion> toLoad;
    private ArrayList<BranchLeveledRegion> toLoadBranchCache;
    private ArrayList<File> cacheToConvertFromTemp;
    private LeveledRegion<?> nextToLoadByViewing;
    private boolean regionDetectionComplete;
    private Path lastRealmOwnerPath;
    public boolean loadingFiles;
    private OverlayBuilder overlayBuilder;
    private PNGExporter pngExporter;
    private List<MapDimension> workingDimList;
    public boolean saveAll;
    private MapProcessor mapProcessor;
    public int mainTextureLevel;
    private BiomeInfoSupplier biomeInfoSupplier;
    private BlockStateShortShapeCache blockStateShortShapeCache;
    private boolean exporting;
    
    public MapSaveLoad(final OverlayManager overlayManager, final PNGExporter pngExporter, final BlockStateShortShapeCache blockStateShortShapeCache) {
        this.toSave = new ArrayList<MapRegion>();
        this.toLoad = new ArrayList<MapRegion>();
        this.toLoadBranchCache = new ArrayList<BranchLeveledRegion>();
        this.cacheToConvertFromTemp = new ArrayList<File>();
        this.overlayBuilder = new OverlayBuilder(overlayManager);
        this.pngExporter = pngExporter;
        this.workingDimList = new ArrayList<MapDimension>();
        this.biomeInfoSupplier = (BiomeInfoSupplier)new BiomeInfoSupplier() {
            public void getBiomeInfo(final BlockStateColorTypeCache colorTypeCache, final World world, final IBlockState state, final BlockPos pos, final int[] dest, final int biomeId) {
                colorTypeCache.getBlockBiomeColour(world, state, pos, dest, biomeId);
            }
        };
        this.blockStateShortShapeCache = blockStateShortShapeCache;
    }
    
    public void setMapProcessor(final MapProcessor mapProcessor) {
        this.mapProcessor = mapProcessor;
    }
    
    public boolean exportPNG(final ExportScreen destScreen, final MapTileSelection selection) {
        if (this.exporting) {
            return false;
        }
        this.exporting = true;
        WorldMap.mapRunner.addTask(new MapRunnerTask() {
            @Override
            public void run(final MapProcessor mapProcessor) {
                Minecraft.func_71410_x().func_152344_a((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final PNGExportResult result = MapSaveLoad.this.pngExporter.export(mapProcessor, selection);
                            WorldMap.LOGGER.info(result.getMessage().func_150260_c());
                            if (destScreen != null) {
                                destScreen.onExportDone(result);
                            }
                            if (result.getFolderToOpen() != null && Files.exists(result.getFolderToOpen(), new LinkOption[0])) {
                                final Desktop d = Desktop.getDesktop();
                                try {
                                    d.open(result.getFolderToOpen().toFile());
                                }
                                catch (IOException e) {
                                    WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
                                }
                            }
                        }
                        catch (Throwable e2) {
                            WorldMap.LOGGER.error("Failed to export PNG with exception!", e2);
                            WorldMap.crashHandler.setCrashedBy(e2);
                        }
                        MapSaveLoad.this.exporting = false;
                        Minecraft.func_71410_x().func_147108_a((GuiScreen)destScreen);
                    }
                });
                while (MapSaveLoad.this.exporting) {
                    try {
                        Thread.sleep(100L);
                    }
                    catch (InterruptedException ex) {}
                }
            }
        });
        return true;
    }
    
    private File getSecondaryFile(final String extension, final File realFile) {
        if (realFile == null) {
            return null;
        }
        String p = realFile.getPath();
        if (p.endsWith(".outdated")) {
            p = p.substring(0, p.length() - ".outdated".length());
        }
        return new File(p.substring(0, p.lastIndexOf(".")) + extension);
    }
    
    public File getTempFile(final File realFile) {
        return this.getSecondaryFile(".zip.temp", realFile);
    }
    
    private Path getCacheFolder(final Path subFolder) {
        if (subFolder != null) {
            return subFolder.resolve("cache_" + this.mapProcessor.getGlobalVersion());
        }
        return null;
    }
    
    public File getCacheFile(final MapRegionInfo region, final int caveLayer, final boolean checkOutdated, final boolean requestCache) throws IOException {
        final Path subFolder = this.getMWSubFolder(region.getWorldId(), region.getDimId(), region.getMwId());
        final Path layerFolder = this.getCaveLayerFolder(caveLayer, subFolder);
        final Path latestCacheFolder = this.getCacheFolder(layerFolder);
        if (latestCacheFolder == null) {
            return null;
        }
        if (!Files.exists(latestCacheFolder, new LinkOption[0])) {
            Files.createDirectories(latestCacheFolder, (FileAttribute<?>[])new FileAttribute[0]);
        }
        final Path cacheFile = latestCacheFolder.resolve(region.getRegionX() + "_" + region.getRegionZ() + ".xwmc");
        if (!checkOutdated || Files.exists(cacheFile, new LinkOption[0])) {
            return cacheFile.toFile();
        }
        if (requestCache) {
            region.setShouldCache(true, "cache file");
        }
        final Path outdatedCacheFile = cacheFile.resolveSibling(cacheFile.getFileName().toString() + ".outdated");
        if (Files.exists(outdatedCacheFile, new LinkOption[0])) {
            return outdatedCacheFile.toFile();
        }
        return cacheFile.toFile();
    }
    
    public File getFile(final MapRegion region) {
        if (region.getWorldId() == null) {
            return null;
        }
        final File detectedFile = region.getRegionFile();
        final boolean normalMapData = region.isNormalMapData();
        if (normalMapData) {
            return this.getNormalFile(region);
        }
        if (detectedFile != null) {
            return detectedFile;
        }
        return this.mapProcessor.getWorldDataHandler().getWorldDir().toPath().resolve("region").resolve("r." + region.getRegionX() + "." + region.getRegionZ() + ".mca").toFile();
    }
    
    public File getNormalFile(final MapRegion region) {
        if (region.getWorldId() == null) {
            return null;
        }
        final File detectedFile = region.isNormalMapData() ? region.getRegionFile() : null;
        final MapProcessor mapProcessor = this.mapProcessor;
        final boolean realms = MapProcessor.isWorldRealms(region.getWorldId());
        final String mwId = region.isNormalMapData() ? region.getMwId() : "cm$converted";
        final Path mainFolder = this.getMainFolder(region.getWorldId(), region.getDimId());
        Path layerFolder;
        final Path subFolder = layerFolder = this.getMWSubFolder(region.getWorldId(), mainFolder, mwId);
        if (region.getCaveLayer() != Integer.MAX_VALUE) {
            layerFolder = layerFolder.resolve("caves").resolve("" + region.getCaveLayer());
        }
        try {
            final File subFolderFile = layerFolder.toFile();
            if (!subFolderFile.exists()) {
                Files.createDirectories(subFolderFile.toPath(), (FileAttribute<?>[])new FileAttribute[0]);
                if (realms && WorldMap.events.getLatestRealm() != null) {
                    final Path ownerPath = mainFolder.resolve(WorldMap.events.getLatestRealm().owner + ".owner");
                    if (!ownerPath.equals(this.lastRealmOwnerPath)) {
                        if (!Files.exists(ownerPath, new LinkOption[0])) {
                            Files.createFile(ownerPath, (FileAttribute<?>[])new FileAttribute[0]);
                        }
                        this.lastRealmOwnerPath = ownerPath;
                    }
                }
            }
        }
        catch (IOException e1) {
            WorldMap.LOGGER.error("suppressed exception", (Throwable)e1);
        }
        if (detectedFile != null && detectedFile.getName().endsWith(".xaero")) {
            final File zipFile = layerFolder.resolve(region.getRegionX() + "_" + region.getRegionZ() + ".zip").toFile();
            if (detectedFile.exists() && !zipFile.exists()) {
                this.xaeroToZip(detectedFile);
            }
            region.setRegionFile(zipFile);
            return zipFile;
        }
        return (detectedFile == null) ? layerFolder.resolve(region.getRegionX() + "_" + region.getRegionZ() + ".zip").toFile() : detectedFile;
    }
    
    public static Path getRootFolder(final String world) {
        if (world == null) {
            return null;
        }
        return WorldMap.saveFolder.toPath().resolve(world);
    }
    
    public Path getMainFolder(final String world, final String dim) {
        if (world == null) {
            return null;
        }
        return WorldMap.saveFolder.toPath().resolve(world).resolve(dim);
    }
    
    Path getMWSubFolder(final String world, final Path mainFolder, final String mw) {
        if (world == null) {
            return null;
        }
        if (mw == null) {
            return mainFolder;
        }
        return mainFolder.resolve(mw);
    }
    
    public Path getCaveLayerFolder(final int caveLayer, final Path subFolder) {
        Path layerFolder = subFolder;
        if (caveLayer != Integer.MAX_VALUE) {
            layerFolder = subFolder.resolve("caves").resolve("" + caveLayer);
        }
        return layerFolder;
    }
    
    public Path getMWSubFolder(final String world, final String dim, final String mw) {
        if (world == null) {
            return null;
        }
        return this.getMWSubFolder(world, this.getMainFolder(world, dim), mw);
    }
    
    public Path getOldFolder(final String oldUnfixedMainId, final String dim) {
        if (oldUnfixedMainId == null) {
            return null;
        }
        return WorldMap.saveFolder.toPath().resolve(oldUnfixedMainId + "_" + dim);
    }
    
    private void xaeroToZip(final File xaero) {
        final File zipFile = xaero.toPath().getParent().resolve(xaero.getName().substring(0, xaero.getName().lastIndexOf(46)) + ".zip").toFile();
        try {
            final BufferedInputStream in = new BufferedInputStream(new FileInputStream(xaero), 1024);
            final ZipOutputStream zipOutput = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
            final ZipEntry e = new ZipEntry("region.xaero");
            zipOutput.putNextEntry(e);
            final byte[] bytes = new byte[1024];
            int got;
            while ((got = in.read(bytes)) > 0) {
                zipOutput.write(bytes, 0, got);
            }
            zipOutput.closeEntry();
            zipOutput.flush();
            zipOutput.close();
            in.close();
            Files.deleteIfExists(xaero.toPath());
        }
        catch (IOException e2) {
            WorldMap.LOGGER.error("suppressed exception", (Throwable)e2);
        }
    }
    
    public void detectRegions(int attempts) {
        final MapDimension mapDimension = this.mapProcessor.getMapWorld().getCurrentDimension();
        mapDimension.preDetection();
        final String worldId = this.mapProcessor.getCurrentWorldId();
        if (worldId == null || this.mapProcessor.isCurrentMapLocked()) {
            return;
        }
        final String dimId = this.mapProcessor.getCurrentDimId();
        final String mwId = this.mapProcessor.getCurrentMWId();
        final boolean usingNormalMapData = !mapDimension.isUsingWorldSave();
        final Path mapFolder = this.getMWSubFolder(worldId, dimId, mwId);
        final boolean mapFolderExists = mapFolder.toFile().exists();
        final String multiplayerMapRegex = "^(-?\\d+)_(-?\\d+)\\.(zip|xaero)$";
        final MapLayer mainLayer = mapDimension.getLayeredMapRegions().getLayer(Integer.MAX_VALUE);
        if (usingNormalMapData) {
            if (mapFolderExists) {
                this.detectRegionsFromFiles(mapDimension, worldId, dimId, mwId, mapFolder, "^(-?\\d+)_(-?\\d+)\\.(zip|xaero)$", 1, 2, 0, 20, new Consumer<RegionDetection>() {
                    @Override
                    public void accept(final RegionDetection detect) {
                        mainLayer.addRegionDetection(detect);
                    }
                });
            }
        }
        else {
            final File worldDir = this.mapProcessor.getWorldDataHandler().getWorldDir();
            if (worldDir == null) {
                return;
            }
            final Path worldFolder = worldDir.toPath().resolve("region");
            if (!worldFolder.toFile().exists()) {
                return;
            }
            this.detectRegionsFromFiles(mapDimension, worldId, dimId, mwId, worldFolder, "^r\\.(-{0,1}[0-9]+)\\.(-{0,1}[0-9]+)\\.mc[ar]$", 1, 2, 8192, 20, new Consumer<RegionDetection>() {
                @Override
                public void accept(final RegionDetection detect) {
                    mapDimension.addWorldSaveRegionDetection(detect);
                }
            });
        }
        if (mapFolderExists) {
            final Path cavesFolder = mapFolder.resolve("caves");
            try {
                if (!Files.exists(cavesFolder, new LinkOption[0])) {
                    Files.createDirectories(cavesFolder, (FileAttribute<?>[])new FileAttribute[0]);
                }
                try (final Stream<Path> cavesFolderStream = Files.list(cavesFolder)) {
                    cavesFolderStream.forEach(new Consumer<Path>() {
                        @Override
                        public void accept(final Path layerFolder) {
                            if (!Files.isDirectory(layerFolder, new LinkOption[0])) {
                                return;
                            }
                            final String folderName = layerFolder.getFileName().toString();
                            try {
                                final int layerInt = Integer.parseInt(folderName);
                                final MapLayer layer = mapDimension.getLayeredMapRegions().getLayer(layerInt);
                                if (usingNormalMapData) {
                                    MapSaveLoad.this.detectRegionsFromFiles(mapDimension, worldId, dimId, mwId, layerFolder, "^(-?\\d+)_(-?\\d+)\\.(zip|xaero)$", 1, 2, 0, 20, new Consumer<RegionDetection>() {
                                        @Override
                                        public void accept(final RegionDetection detect) {
                                            layer.addRegionDetection(detect);
                                        }
                                    });
                                }
                            }
                            catch (NumberFormatException ex) {}
                        }
                    });
                }
            }
            catch (IOException e) {
                WorldMap.LOGGER.error("IOException trying to detect map layers!");
                if (attempts > 1) {
                    --attempts;
                    WorldMap.LOGGER.error("Retrying... " + attempts);
                    try {
                        Thread.sleep(30L);
                    }
                    catch (InterruptedException ex) {}
                    this.detectRegions(attempts);
                    return;
                }
                throw new RuntimeException("Couldn't detect map layers after multiple attempts.", e);
            }
        }
    }
    
    public void detectRegionsFromFiles(final MapDimension mapDimension, final String worldId, final String dimId, final String mwId, final Path folder, final String regex, final int xIndex, final int zIndex, final int emptySize, int attempts, final Consumer<RegionDetection> detectionConsumer) {
        int total = 0;
        final Pattern fileRegexPattern = Pattern.compile(regex);
        final long before = System.currentTimeMillis();
        try {
            final Stream<Path> files = Files.list(folder);
            final Iterator<Path> iter = files.iterator();
            final int globalVersion = this.mapProcessor.getGlobalVersion();
            while (!this.mapProcessor.isFinalizing() && iter.hasNext()) {
                final Path file = iter.next();
                final String regionName = file.getFileName().toString();
                final Matcher matcher = fileRegexPattern.matcher(regionName);
                if (!matcher.matches()) {
                    continue;
                }
                final int x = Integer.parseInt(matcher.group(xIndex));
                final int z = Integer.parseInt(matcher.group(zIndex));
                final RegionDetection regionDetection = new RegionDetection(worldId, dimId, mwId, x, z, file.toFile(), globalVersion, true);
                detectionConsumer.accept(regionDetection);
                ++total;
            }
            files.close();
        }
        catch (IOException e) {
            WorldMap.LOGGER.error("IOException trying to detect map files!");
            if (attempts > 1) {
                --attempts;
                WorldMap.LOGGER.error("Retrying... " + attempts);
                try {
                    Thread.sleep(30L);
                }
                catch (InterruptedException ex) {}
                this.detectRegionsFromFiles(mapDimension, worldId, dimId, mwId, folder, regex, xIndex, zIndex, emptySize, attempts, detectionConsumer);
                return;
            }
            throw new RuntimeException("Couldn't detect map files after multiple attempts.", e);
        }
        if (WorldMapClientConfigUtils.getDebug()) {
            WorldMap.LOGGER.info(String.format("%d regions detected in %d ms!", total, System.currentTimeMillis() - before));
        }
    }
    
    private boolean saveRegion(final MapRegion region, final boolean debugConfig, final int extraAttempts) {
        try {
            if (!region.hasHadTerrain()) {
                if (debugConfig) {
                    WorldMap.LOGGER.info("Save not required for highlight-only region: " + region + " " + region.getWorldId() + " " + region.getDimId());
                }
                return region.countChunks() > 0;
            }
            if (!region.isResaving() && !region.isNormalMapData()) {
                if (debugConfig) {
                    WorldMap.LOGGER.info("Save not required for world save map: " + region + " " + region.getWorldId() + " " + region.getDimId());
                }
                return region.countChunks() > 0;
            }
            final File permFile = this.getNormalFile(region);
            if (!permFile.toPath().startsWith(WorldMap.saveFolder.toPath())) {
                throw new IllegalArgumentException();
            }
            final File file = this.getTempFile(permFile);
            if (file == null) {
                return true;
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            boolean hasAnything = false;
            boolean regionWasSavedEmpty = true;
            DataOutputStream out = null;
            try {
                final ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
                out = new DataOutputStream(zipOut);
                final ZipEntry e = new ZipEntry("region.xaero");
                zipOut.putNextEntry(e);
                out.write(255);
                out.writeInt(8);
                for (int o = 0; o < 8; ++o) {
                    for (int p = 0; p < 8; ++p) {
                        final MapTileChunk chunk = region.getChunk(o, p);
                        if (chunk != null) {
                            hasAnything = true;
                            if (chunk.includeInSave()) {
                                out.write(o << 4 | p);
                                boolean chunkIsEmpty = true;
                                for (int i = 0; i < 4; ++i) {
                                    for (int j = 0; j < 4; ++j) {
                                        final MapTile tile = chunk.getTile(i, j);
                                        if (tile != null && tile.isLoaded()) {
                                            chunkIsEmpty = false;
                                            for (int x = 0; x < 16; ++x) {
                                                final MapBlock[] c = tile.getBlockColumn(x);
                                                for (int z = 0; z < 16; ++z) {
                                                    this.savePixel(c[z], out);
                                                }
                                            }
                                            out.write(tile.getWorldInterpretationVersion());
                                            out.writeInt(tile.getWrittenCaveStart());
                                            out.write(tile.getWrittenCaveDepth());
                                        }
                                        else {
                                            out.writeInt(-1);
                                        }
                                    }
                                }
                                if (!chunkIsEmpty) {
                                    regionWasSavedEmpty = false;
                                }
                            }
                            else {
                                if (!chunk.hasHighlightsIfUndiscovered()) {
                                    region.setChunk(o, p, null);
                                    synchronized (chunk) {
                                        chunk.getLeafTexture().deleteTexturesAndBuffers();
                                    }
                                }
                                final BranchLeveledRegion parentRegion = region.getParent();
                                if (parentRegion != null) {
                                    parentRegion.setShouldCheckForUpdatesRecursive(true);
                                }
                            }
                        }
                    }
                }
                zipOut.closeEntry();
            }
            finally {
                if (out != null) {
                    out.close();
                }
            }
            if (regionWasSavedEmpty) {
                this.safeDelete(permFile.toPath(), ".zip");
                this.safeDelete(file.toPath(), ".temp");
                if (debugConfig) {
                    WorldMap.LOGGER.info("Save cancelled because the region would be saved empty: " + region + " " + region.getWorldId() + " " + region.getDimId() + " " + region.getMwId());
                }
                return hasAnything;
            }
            this.safeMoveAndReplace(file.toPath(), permFile.toPath(), ".temp", ".zip");
            if (debugConfig) {
                WorldMap.LOGGER.info("Region saved: " + region + " " + region.getWorldId() + " " + region.getDimId() + " " + region.getMwId() + ", " + this.mapProcessor.getMapWriter().getUpdateCounter());
            }
            return true;
        }
        catch (IOException ioe) {
            WorldMap.LOGGER.error("IO exception while trying to save " + region, (Throwable)ioe);
            if (extraAttempts > 0) {
                WorldMap.LOGGER.info("(World Map) Retrying...");
                try {
                    Thread.sleep(20L);
                }
                catch (InterruptedException ex) {}
                return this.saveRegion(region, debugConfig, extraAttempts - 1);
            }
            return true;
        }
    }
    
    private Path getBackupFolder(final Path filePath, final int saveVersion, final int backupVersion) {
        return filePath.getParent().resolve(saveVersion + "_backup_" + backupVersion);
    }
    
    public void backupFile(final File file, final int saveVersion) throws IOException {
        if (file.getName().endsWith(".mca") || file.getName().endsWith(".mcr")) {
            throw new RuntimeException("World save protected: " + file);
        }
        final Path filePath = file.toPath();
        int backupVersion = 0;
        Path backupFolder;
        String backupName;
        Path backup;
        for (backupFolder = this.getBackupFolder(filePath, saveVersion, backupVersion), backupName = filePath.getFileName().toString(), backup = backupFolder.resolve(backupName); Files.exists(backup, new LinkOption[0]); backup = backupFolder.resolve(backupName)) {
            ++backupVersion;
            backupFolder = this.getBackupFolder(filePath, saveVersion, backupVersion);
        }
        if (!Files.exists(backupFolder, new LinkOption[0])) {
            Files.createDirectories(backupFolder, (FileAttribute<?>[])new FileAttribute[0]);
        }
        Files.move(file.toPath(), backup, new CopyOption[0]);
        WorldMap.LOGGER.info("File " + file.getPath() + " backed up to " + backupFolder.toFile().getPath());
    }
    
    public boolean loadRegion(final World world, final MapRegion region, final BlockStateColorTypeCache colourTypeCache, final boolean debugConfig, final int extraAttempts) {
        final boolean multiplayer = region.isNormalMapData();
        final int emptySize = multiplayer ? 0 : 8192;
        int saveVersion = -1;
        boolean versionReached = false;
        final int[] biomeBuffer = new int[3];
        try {
            final File file = this.getFile(region);
            if (!region.hasHadTerrain() || file == null || !file.exists() || Files.size(file.toPath()) <= emptySize) {
                if (region.getLoadState() == 4 || region.hasHadTerrain()) {
                    region.setSaveExists(null);
                }
                if (region.hasHadTerrain()) {
                    return false;
                }
                synchronized (region) {
                    region.setLoadState((byte)1);
                }
                region.restoreBufferUpdateObjects();
                if (debugConfig) {
                    WorldMap.LOGGER.info("Highlight region fake-loaded: " + region + " " + region.getWorldId() + " " + region.getDimId() + " " + region.getMwId());
                }
                return true;
            }
            else {
                synchronized (region) {
                    region.setLoadState((byte)1);
                }
                region.setSaveExists(true);
                region.restoreBufferUpdateObjects();
                int totalChunks = 0;
                if (multiplayer) {
                    DataInputStream in = null;
                    try {
                        final ZipInputStream zipIn = new ZipInputStream(new BufferedInputStream(new FileInputStream(file), 2048));
                        in = new DataInputStream(zipIn);
                        zipIn.getNextEntry();
                        int firstByte = in.read();
                        if (firstByte == 255) {
                            saveVersion = in.readInt();
                            if (8 < saveVersion) {
                                zipIn.closeEntry();
                                in.close();
                                WorldMap.LOGGER.info("Trying to load a newer region " + region + " save using an older version of Xaero's World Map!");
                                this.backupFile(file, saveVersion);
                                region.setSaveExists(null);
                                return false;
                            }
                            firstByte = -1;
                        }
                        versionReached = true;
                        Object o3;
                        MapRegion parent;
                        if (region.getLevel() == 3) {
                            o3 = region;
                            parent = region;
                        }
                        else {
                            o3 = (parent = (MapRegion)region.getParent());
                        }
                        final MapRegion mapRegion = parent;
                        synchronized (o3) {
                            synchronized (region) {
                                for (int o = 0; o < 8; ++o) {
                                    for (int p = 0; p < 8; ++p) {
                                        final MapTileChunk chunk = region.getChunk(o, p);
                                        if (chunk != null) {
                                            chunk.setLoadState((byte)1);
                                        }
                                    }
                                }
                            }
                        }
                        while (true) {
                            final int chunkCoords = (firstByte == -1) ? in.read() : firstByte;
                            if (chunkCoords == -1) {
                                zipIn.closeEntry();
                                break;
                            }
                            firstByte = -1;
                            final int o2 = chunkCoords >> 4;
                            final int p2 = chunkCoords & 0xF;
                            MapTileChunk chunk2 = region.getChunk(o2, p2);
                            if (chunk2 == null) {
                                region.setChunk(o2, p2, chunk2 = new MapTileChunk(region, region.getRegionX() * 8 + o2, region.getRegionZ() * 8 + p2));
                            }
                            else if (chunk2.getLoadState() >= 2) {
                                throw new Exception("Map data for region " + region + " is probably corrupt! Has the same map tile chunk saved twice.");
                            }
                            if (region.isMetaLoaded()) {
                                chunk2.getLeafTexture().setBufferedTextureVersion(region.getAndResetCachedTextureVersion(o2, p2));
                            }
                            chunk2.resetHeights();
                            for (int i = 0; i < 4; ++i) {
                                for (int j = 0; j < 4; ++j) {
                                    Integer nextTile = in.readInt();
                                    if (nextTile != -1) {
                                        final MapTile tile = this.mapProcessor.getTilePool().get(this.mapProcessor.getCurrentDimension(), chunk2.getX() * 4 + i, chunk2.getZ() * 4 + j);
                                        for (int x = 0; x < 16; ++x) {
                                            final MapBlock[] c = tile.getBlockColumn(x);
                                            for (int z = 0; z < 16; ++z) {
                                                if (c[z] == null) {
                                                    c[z] = new MapBlock();
                                                }
                                                else {
                                                    c[z].prepareForWriting();
                                                }
                                                this.loadPixel(nextTile, c[z], in, saveVersion, world, biomeBuffer, colourTypeCache);
                                                nextTile = null;
                                            }
                                        }
                                        if (saveVersion >= 4) {
                                            tile.setWorldInterpretationVersion(in.read());
                                        }
                                        if (saveVersion >= 6) {
                                            tile.setWrittenCave(in.readInt(), (saveVersion >= 7) ? in.read() : 32);
                                        }
                                        chunk2.setTile(i, j, tile, this.blockStateShortShapeCache);
                                        tile.setLoaded(true);
                                    }
                                }
                            }
                            if (!chunk2.includeInSave()) {
                                if (chunk2.hasHighlightsIfUndiscovered()) {
                                    continue;
                                }
                                region.setChunk(o2, p2, null);
                                chunk2.getLeafTexture().deleteTexturesAndBuffers();
                                chunk2 = null;
                            }
                            else {
                                region.pushWriterPause();
                                ++totalChunks;
                                chunk2.setToUpdateBuffers(true);
                                chunk2.setLoadState((byte)2);
                                region.popWriterPause();
                            }
                        }
                    }
                    finally {
                        if (in != null) {
                            in.close();
                        }
                    }
                    if (totalChunks > 0) {
                        if (debugConfig) {
                            WorldMap.LOGGER.info("Region loaded: " + region + " " + region.getWorldId() + " " + region.getDimId() + " " + region.getMwId() + ", " + saveVersion);
                        }
                        return true;
                    }
                    region.setSaveExists(null);
                    this.safeDelete(file.toPath(), ".zip");
                    if (debugConfig) {
                        WorldMap.LOGGER.info("Cancelled loading an empty region: " + region + " " + region.getWorldId() + " " + region.getDimId() + " " + region.getMwId() + ", " + saveVersion);
                    }
                    return false;
                }
                else {
                    final int[] chunkCount = { 0 };
                    final WorldDataHandler.Result buildResult = this.mapProcessor.getWorldDataHandler().buildRegion(world, region, true, chunkCount);
                    if (buildResult == WorldDataHandler.Result.CANCEL) {
                        if (region.hasHadTerrain()) {
                            final RegionDetection restoredDetection = new RegionDetection(region.getWorldId(), region.getDimId(), region.getMwId(), region.getRegionX(), region.getRegionZ(), region.getRegionFile(), this.mapProcessor.getGlobalVersion(), true);
                            restoredDetection.transferInfoFrom(region);
                            region.getDim().getLayeredMapRegions().getLayer(region.getCaveLayer()).addRegionDetection(restoredDetection);
                        }
                        this.mapProcessor.removeMapRegion(region);
                        WorldMap.LOGGER.info("Region cancelled from world save: " + region + " " + region.getWorldId() + " " + region.getDimId() + " " + region.getMwId());
                        return false;
                    }
                    region.setRegionFile(file);
                    final boolean result = buildResult == WorldDataHandler.Result.SUCCESS && chunkCount[0] > 0;
                    if (!result) {
                        region.setSaveExists(null);
                        if (debugConfig) {
                            WorldMap.LOGGER.info("Region failed to load from world save: " + region + " " + region.getWorldId() + " " + region.getDimId() + " " + region.getMwId());
                        }
                    }
                    else if (debugConfig) {
                        WorldMap.LOGGER.info("Region loaded from world save: " + region + " " + region.getWorldId() + " " + region.getDimId() + " " + region.getMwId());
                    }
                    return result;
                }
            }
        }
        catch (IOException ioe) {
            WorldMap.LOGGER.error("IO exception while trying to load " + region, (Throwable)ioe);
            if (extraAttempts > 0) {
                synchronized (region) {
                    region.setLoadState((byte)4);
                }
                WorldMap.LOGGER.info("(World Map) Retrying...");
                try {
                    Thread.sleep(20L);
                }
                catch (InterruptedException ex) {}
                return this.loadRegion(world, region, colourTypeCache, debugConfig, extraAttempts - 1);
            }
            region.setSaveExists(null);
            return false;
        }
        catch (Throwable e) {
            region.setSaveExists(null);
            WorldMap.LOGGER.error("Region failed to load: " + region + (versionReached ? (" " + saveVersion) : ""), e);
            return false;
        }
    }
    
    public boolean beingSaved(final MapDimension dim, final int regX, final int regZ) {
        for (int i = 0; i < this.toSave.size(); ++i) {
            final MapRegion r = this.toSave.get(i);
            if (r != null && r.getDim() == dim && r.getRegionX() == regX && r.getRegionZ() == regZ) {
                return true;
            }
        }
        return false;
    }
    
    public void requestLoad(final MapRegion region, final String reason) {
        this.requestLoad(region, reason, true);
    }
    
    public void requestLoad(final MapRegion region, final String reason, final boolean prioritize) {
        this.addToLoad(region, reason, prioritize);
    }
    
    public void requestBranchCache(final BranchLeveledRegion region, final String reason) {
        this.requestBranchCache(region, reason, true);
        if (reason == null) {
            return;
        }
        if (WorldMapClientConfigUtils.getDebug()) {
            WorldMap.LOGGER.info("Requesting branch load for: " + region + ", " + reason);
        }
    }
    
    public void requestBranchCache(final BranchLeveledRegion region, final String reason, final boolean prioritize) {
        synchronized (this.toLoadBranchCache) {
            if (prioritize) {
                this.toLoadBranchCache.remove(region);
                this.toLoadBranchCache.add(0, region);
            }
            else if (!this.toLoadBranchCache.contains(region)) {
                this.toLoadBranchCache.add(region);
            }
        }
    }
    
    public void addToLoad(final MapRegion region, final String reason, final boolean prioritize) {
        synchronized (this.toLoad) {
            if (prioritize) {
                region.setReloadHasBeenRequested(true, reason);
                this.toLoad.remove(region);
                this.toLoad.add(0, region);
                if (WorldMapClientConfigUtils.getDebug() && reason != null) {
                    WorldMap.LOGGER.info("Requesting load for: " + region + " " + region.getWorldId() + " " + region.getDimId() + " " + region.getMwId() + ", " + reason);
                }
            }
            else if (!this.loadingFiles && !this.toLoad.contains(region)) {
                region.setReloadHasBeenRequested(true, reason);
                this.toLoad.add(region);
                if (WorldMapClientConfigUtils.getDebug() && reason != null) {
                    WorldMap.LOGGER.info("Requesting load for: " + region + " " + region.getWorldId() + " " + region.getDimId() + " " + region.getMwId() + ", " + reason);
                }
            }
        }
        this.mapProcessor.getMapRegionHighlightsPreparer().prepare(region, false);
    }
    
    public void removeToLoad(final MapRegion region) {
        synchronized (this.toLoad) {
            this.toLoad.remove(region);
        }
    }
    
    public void clearToLoad() {
        synchronized (this.toLoad) {
            this.toLoad.clear();
        }
        synchronized (this.toLoadBranchCache) {
            this.toLoadBranchCache.clear();
        }
    }
    
    public int getSizeOfToLoad() {
        return this.toLoad.size();
    }
    
    public boolean saveExists(final MapRegion region) {
        if (region.getSaveExists() != null) {
            return region.getSaveExists();
        }
        boolean result = true;
        final File file = this.getFile(region);
        if (file == null || !file.exists()) {
            result = false;
        }
        region.setSaveExists(result);
        return result;
    }
    
    public void updateSave(final LeveledRegion<?> leveledRegion, final long currentTime, final int currentLayer) {
        if (leveledRegion.getLevel() == 0) {
            final MapRegion region = (MapRegion)leveledRegion;
            int saveTime = 60000;
            if (region.getCaveLayer() != currentLayer) {
                saveTime /= 100;
            }
            if (region.getLoadState() == 2 && region.isBeingWritten() && currentTime - region.getLastSaveTime() >= saveTime && !this.beingSaved(region.getDim(), region.getRegionX(), region.getRegionZ())) {
                this.toSave.add(region);
                region.setSaveExists(true);
                region.setLastSaveTime(currentTime);
            }
        }
        else {
            final BranchLeveledRegion region2 = (BranchLeveledRegion)leveledRegion;
            if (region2.eligibleForSaving(currentTime)) {
                region2.startDownloadingTexturesForCache(this.mapProcessor);
            }
        }
    }
    
    public void run(final World world, final BlockStateColorTypeCache colourTypeCache) throws Exception {
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        final SingleConfigManager<Config> primaryConfigManager = (SingleConfigManager<Config>)configManager.getPrimaryConfigManager();
        final boolean debugConfig = (boolean)primaryConfigManager.getEffective((ConfigOption)WorldMapPrimaryClientConfigOptions.DEBUG);
        final int globalVersion = this.mapProcessor.getGlobalVersion();
        if (!this.toLoad.isEmpty()) {
            boolean loaded = false;
            this.mapProcessor.pushIsLoading();
            this.loadingFiles = true;
            final boolean reloadEverything = (boolean)primaryConfigManager.getEffective((ConfigOption)WorldMapPrimaryClientConfigOptions.RELOAD_VIEWED);
            int limit = this.toLoad.size();
            while (limit > 0 && !this.mapProcessor.isWaitingForWorldUpdate() && !loaded && !this.toLoad.isEmpty()) {
                --limit;
                final MapRegion region;
                synchronized (this.toLoad) {
                    if (this.toLoad.isEmpty()) {
                        break;
                    }
                    region = this.toLoad.get(0);
                }
                if (region.hasHadTerrain() && region.getCacheFile() == null && !region.hasLookedForCache()) {
                    final File potentialCacheFile = this.getCacheFile((MapRegionInfo)region, region.getCaveLayer(), true, true);
                    if (potentialCacheFile.exists()) {
                        region.setCacheFile(potentialCacheFile);
                    }
                    region.setLookedForCache(true);
                }
                final int globalRegionCacheHashCode = WorldMap.settings.getRegionCacheHashCode();
                final int globalReloadVersion = (int)primaryConfigManager.getEffective(WorldMapPrimaryClientConfigOptions.RELOAD_VIEWED_VERSION);
                final int globalCaveDepth = (int)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.CAVE_MODE_DEPTH);
                final boolean needsLoading;
                synchronized (region) {
                    needsLoading = (region.getLoadState() == 0 || region.getLoadState() == 4);
                    if (needsLoading) {
                        if ((region.hasVersion() && region.getVersion() != globalVersion) || (!region.hasVersion() && region.getInitialVersion() != globalVersion) || (region.getLoadState() == 4 && reloadEverything && region.getReloadVersion() != globalReloadVersion) || ((region.getLoadState() == 4 || (region.isMetaLoaded() && this.mainTextureLevel != region.getLevel())) && (globalRegionCacheHashCode != region.getCacheHashCode() || region.caveStartOutdated(region.getUpToDateCaveStart(), globalCaveDepth))) || (region.getDim().getFullReloader() != null && region.getDim().getFullReloader().isPartOfReload(region))) {
                            region.setShouldCache(true, "loading");
                        }
                        region.setVersion(globalVersion);
                    }
                }
                if (needsLoading) {
                    synchronized (region) {
                        region.setAllCachePrepared(false);
                    }
                    final boolean cacheOnlyMode = region.getDim().getMapWorld().isCacheOnlyMode();
                    final boolean fromNothing = region.getLoadState() == 0;
                    boolean hasSomething = false;
                    boolean justMetaData = false;
                    final boolean[] leafShouldAffectBranchesDest = { false };
                    final int targetHighlightsHash = region.getTargetHighlightsHash();
                    final boolean[] metaLoadedDest = { false };
                    boolean[][] textureLoaded = null;
                    if (cacheOnlyMode || (region.getLoadState() == 0 && (!region.shouldCache() || !region.isMetaLoaded() || this.mainTextureLevel == region.getLevel())) || (!region.shouldCache() && region.getLoadState() == 4)) {
                        textureLoaded = new boolean[8][8];
                        justMetaData = region.loadCacheTextures(this.mapProcessor, !region.isMetaLoaded() && this.mainTextureLevel != region.getLevel(), textureLoaded, targetHighlightsHash, leafShouldAffectBranchesDest, metaLoadedDest, 10);
                    }
                    if (justMetaData) {
                        hasSomething = this.cleanupLoadedCache(region, textureLoaded, justMetaData, hasSomething, targetHighlightsHash, metaLoadedDest[0]);
                        if (debugConfig) {
                            WorldMap.LOGGER.info("Loaded meta data for " + region);
                        }
                    }
                    else {
                        region.setHighlightsHash(targetHighlightsHash);
                        final boolean shouldAddToLoaded = region.getLoadState() == 0;
                        boolean shouldLoadProperly;
                        synchronized (region) {
                            boolean goingToPrepareCache = region.shouldCache() && ((region.isMetaLoaded() && this.mainTextureLevel != region.getLevel()) || region.getLoadState() == 4 || region.getCacheFile() == null || !region.getCacheFile().exists());
                            if (!goingToPrepareCache) {
                                goingToPrepareCache = (region.getDim().getFullReloader() != null && region.getDim().getFullReloader().isPartOfReload(region));
                            }
                            shouldLoadProperly = ((region.getLoadState() == 4 && region.isBeingWritten()) || goingToPrepareCache);
                            if (cacheOnlyMode) {
                                shouldLoadProperly = false;
                            }
                            if (!shouldLoadProperly) {
                                if (leafShouldAffectBranchesDest[0]) {
                                    region.setRecacheHasBeenRequested(true, "cache affects branches");
                                    region.setShouldCache(true, "cache affects branches");
                                }
                                region.setLoadState((byte)3);
                            }
                            else if (region.shouldCache()) {
                                region.setRecacheHasBeenRequested(true, "loading");
                            }
                        }
                        if (!shouldLoadProperly && textureLoaded != null) {
                            hasSomething = this.cleanupLoadedCache(region, textureLoaded, justMetaData, hasSomething, targetHighlightsHash, metaLoadedDest[0]);
                        }
                        this.mapProcessor.addToProcess(region);
                        if (shouldAddToLoaded) {
                            this.mapProcessor.getMapWorld().getCurrentDimension().getLayeredMapRegions().addLoadedRegion(region);
                        }
                        if (shouldLoadProperly) {
                            region.setCacheHashCode(globalRegionCacheHashCode);
                            region.setReloadVersion(globalReloadVersion);
                            loaded = this.loadRegion(world, region, colourTypeCache, debugConfig, 10);
                            hasSomething = false;
                            if (!loaded) {
                                region.setShouldCache(false, "couldn't load");
                                region.setRecacheHasBeenRequested(false, "couldn't load");
                                if (region.getSaveExists() == null) {
                                    synchronized (region) {
                                        region.setLoadState((byte)4);
                                    }
                                    region.deleteTexturesAndBuffers();
                                    this.mapProcessor.removeMapRegion(region);
                                }
                            }
                            else {
                                for (int i = 0; i < 8; ++i) {
                                    for (int j = 0; j < 8; ++j) {
                                        final MapTileChunk mapTileChunk = region.getChunk(i, j);
                                        if (mapTileChunk != null) {
                                            if (!mapTileChunk.includeInSave()) {
                                                mapTileChunk.getLeafTexture().resetBiomes();
                                                if (!mapTileChunk.hasHighlightsIfUndiscovered()) {
                                                    region.setChunk(i, j, null);
                                                    mapTileChunk.getLeafTexture().deleteTexturesAndBuffers();
                                                }
                                                else {
                                                    mapTileChunk.setLoadState((byte)2);
                                                    mapTileChunk.unsetHasHadTerrain();
                                                    mapTileChunk.getLeafTexture().requestHighlightOnlyUpload();
                                                    hasSomething = true;
                                                    synchronized (region) {
                                                        region.updateLeafTextureVersion(i, j, targetHighlightsHash);
                                                    }
                                                }
                                            }
                                            else {
                                                hasSomething = true;
                                            }
                                        }
                                        else if (region.leafTextureVersionSum[i][j] != 0) {
                                            synchronized (region) {
                                                region.updateLeafTextureVersion(i, j, 0);
                                            }
                                        }
                                    }
                                }
                                if (!hasSomething) {
                                    synchronized (region) {
                                        if (!region.isBeingWritten() && region.getLoadState() <= 1) {
                                            region.setLoadState((byte)3);
                                        }
                                    }
                                    loaded = false;
                                }
                            }
                            synchronized (region) {
                                if (region.getLoadState() <= 1) {
                                    region.setLoadState((byte)2);
                                }
                                region.setLastSaveTime(region.isResaving() ? -60000L : System.currentTimeMillis());
                            }
                            final BranchLeveledRegion parentRegion = region.getParent();
                            if (parentRegion != null) {
                                parentRegion.setShouldCheckForUpdatesRecursive(true);
                            }
                        }
                        else if (debugConfig) {
                            WorldMap.LOGGER.info("Loaded from cache only for " + region);
                        }
                        region.loadingNeededForBranchLevel = 0;
                    }
                    if (fromNothing && !hasSomething) {
                        final BranchLeveledRegion parentRegion2 = region.getParent();
                        if (parentRegion2 != null) {
                            parentRegion2.setShouldCheckForUpdatesRecursive(true);
                        }
                    }
                }
                region.setReloadHasBeenRequested(false, "loading");
                this.removeToLoad(region);
            }
            this.loadingFiles = false;
            this.mapProcessor.popIsLoading();
        }
        int regionsToSave = 3;
        while (!this.toSave.isEmpty() && (this.saveAll || regionsToSave > 0)) {
            final MapRegion region2 = this.toSave.get(0);
            final boolean regionLoaded;
            synchronized (region2) {
                regionLoaded = (region2.getLoadState() == 2);
            }
            if (regionLoaded) {
                if (!region2.isBeingWritten()) {
                    throw new Exception("Saving a weird region: " + region2);
                }
                region2.pushWriterPause();
                final boolean notEmpty = this.saveRegion(region2, debugConfig, 20);
                region2.setResaving(false);
                if (notEmpty) {
                    if (!region2.isAllCachePrepared()) {
                        synchronized (region2) {
                            if (!region2.isAllCachePrepared()) {
                                region2.requestRefresh(this.mapProcessor, false);
                            }
                        }
                    }
                    region2.setRecacheHasBeenRequested(true, "saving");
                    region2.setShouldCache(true, "saving");
                    region2.setBeingWritten(false);
                    --regionsToSave;
                }
                else {
                    this.mapProcessor.removeMapRegion(region2);
                }
                region2.popWriterPause();
                if (region2.getWorldId() == null || !this.mapProcessor.isEqual(region2.getWorldId(), region2.getDimId(), region2.getMwId())) {
                    if (region2.getCacheFile() != null) {
                        region2.convertCacheToOutdated(this, "is outdated");
                        if (debugConfig) {
                            WorldMap.LOGGER.info(String.format("Converting cache for region %s because it IS outdated.", region2));
                        }
                    }
                    region2.clearRegion(this.mapProcessor);
                }
            }
            else if (debugConfig) {
                WorldMap.LOGGER.info("Tried to save a weird region: " + region2 + " " + region2.getWorldId() + " " + region2.getDimId() + " " + region2.getMwId() + " " + region2.getLoadState());
            }
            this.toSave.remove(region2);
        }
        this.saveAll = false;
        if (!this.toLoadBranchCache.isEmpty()) {
            int limit2 = this.toLoadBranchCache.size();
            this.mapProcessor.pushIsLoading();
            if (!this.mapProcessor.isWaitingForWorldUpdate()) {
                while (limit2 > 0) {
                    --limit2;
                    final BranchLeveledRegion region3;
                    synchronized (this.toLoadBranchCache) {
                        if (this.toLoadBranchCache.isEmpty()) {
                            break;
                        }
                        region3 = this.toLoadBranchCache.get(0);
                    }
                    region3.preCacheLoad();
                    final LayeredRegionManager regionManager = this.mapProcessor.getMapWorld().getCurrentDimension().getLayeredMapRegions();
                    regionManager.addLoadedRegion(region3);
                    region3.setCacheFile(region3.findCacheFile(this));
                    final boolean[] metaLoadedDest2 = { false };
                    final boolean[][] textureLoaded2 = new boolean[8][8];
                    region3.loadCacheTextures(this.mapProcessor, false, textureLoaded2, 0, null, metaLoadedDest2, 10);
                    if (metaLoadedDest2[0]) {
                        region3.confirmMetaLoaded();
                    }
                    this.mapProcessor.addToProcess(region3);
                    if (region3.getCacheFile() == null) {
                        region3.setShouldCheckForUpdatesRecursive(true);
                    }
                    else {
                        region3.setShouldCheckForUpdatesSingle(true);
                    }
                    region3.setShouldCache(false, "branch loading");
                    region3.setLoaded(true);
                    if (debugConfig) {
                        WorldMap.LOGGER.info("Loaded cache for branch region " + region3);
                    }
                    region3.setReloadHasBeenRequested(false, "loading");
                    synchronized (this.toLoadBranchCache) {
                        this.toLoadBranchCache.remove(region3);
                    }
                }
            }
            this.mapProcessor.popIsLoading();
        }
        if (this.mapProcessor.getMapWorld().getCurrentDimensionId() != null) {
            this.workingDimList.clear();
            this.mapProcessor.getMapWorld().getDimensions(this.workingDimList);
            for (int d = 0; d < this.workingDimList.size(); ++d) {
                final MapDimension dim = this.workingDimList.get(d);
                while (!dim.regionsToCache.isEmpty()) {
                    final LeveledRegion<?> region4 = this.removeToCache(dim, 0);
                    region4.preCache();
                    final boolean skipCaching = region4.skipCaching(globalVersion);
                    if (!region4.shouldCache() || !region4.recacheHasBeenRequested() || skipCaching) {
                        if (WorldMap.detailed_debug) {
                            WorldMap.LOGGER.info("toCache cancel: " + region4 + " " + !region4.shouldCache() + " " + !region4.recacheHasBeenRequested() + " " + !region4.isAllCachePrepared() + " " + skipCaching + " " + globalVersion);
                        }
                        if (region4.shouldCache()) {
                            region4.deleteBuffers();
                        }
                        region4.setShouldCache(false, "toCache cancel");
                        region4.setRecacheHasBeenRequested(false, "toCache cancel");
                        region4.postCache(null, this, false);
                    }
                    else {
                        if (!region4.isAllCachePrepared()) {
                            throw new RuntimeException("Trying to save cache for a region with cache not prepared: " + region4 + " " + region4.getExtraInfo());
                        }
                        if (region4.getCacheFile() != null) {
                            this.removeTempCacheRequest(region4.getCacheFile());
                        }
                        final File permFile = region4.findCacheFile(this);
                        final File tempFile = this.getSecondaryFile(".xwmc.temp", permFile);
                        final boolean successfullySaved = region4.saveCacheTextures(tempFile, debugConfig, 10);
                        if (successfullySaved) {
                            this.cacheToConvertFromTemp.add(permFile);
                            region4.setCacheFile(permFile);
                        }
                        region4.setShouldCache(false, "toCache normal");
                        region4.setRecacheHasBeenRequested(false, "toCache normal");
                        region4.postCache(permFile, this, successfullySaved);
                    }
                }
            }
        }
        for (int k = 0; k < this.cacheToConvertFromTemp.size(); ++k) {
            final File permFile2 = this.cacheToConvertFromTemp.get(k);
            final File tempFile2 = this.getSecondaryFile(".xwmc.temp", permFile2);
            try {
                if (Files.exists(tempFile2.toPath(), new LinkOption[0])) {
                    IOUtils.safeMoveAndReplace(tempFile2.toPath(), permFile2.toPath(), true);
                }
                this.cacheToConvertFromTemp.remove(k);
                --k;
            }
            catch (IOException ex) {}
        }
    }
    
    public boolean removeTempCacheRequest(final File file) {
        boolean result = false;
        while (this.cacheToConvertFromTemp.remove(file)) {
            result = true;
        }
        return result;
    }
    
    public void addTempCacheRequest(final File file) {
        this.cacheToConvertFromTemp.add(file);
    }
    
    private boolean cleanupLoadedCache(final MapRegion region, final boolean[][] textureLoaded, final boolean justMetaData, boolean hasSomething, final int targetHighlightsHash, final boolean metaLoaded) {
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                final boolean loaded = textureLoaded[i][j];
                if (justMetaData || !loaded) {
                    final MapTileChunk mapTileChunk = region.getChunk(i, j);
                    if (mapTileChunk != null) {
                        if (justMetaData || !mapTileChunk.hasHighlightsIfUndiscovered()) {
                            region.setChunk(i, j, null);
                            if (!justMetaData) {
                                mapTileChunk.getLeafTexture().deleteTexturesAndBuffers();
                            }
                        }
                        else {
                            mapTileChunk.getLeafTexture().requestHighlightOnlyUpload();
                            hasSomething = true;
                        }
                        if (!loaded && mapTileChunk.hasHighlightsIfUndiscovered()) {
                            region.updateLeafTextureVersion(i, j, targetHighlightsHash);
                        }
                    }
                    else if (!loaded && region.leafTextureVersionSum[i][j] != 0) {
                        region.updateLeafTextureVersion(i, j, 0);
                    }
                }
                else {
                    hasSomething = true;
                }
            }
        }
        if (metaLoaded) {
            region.confirmMetaLoaded();
        }
        return hasSomething;
    }
    
    private void savePixel(final MapBlock pixel, final DataOutputStream out) throws IOException {
        final int parametres = pixel.getParametres();
        out.writeInt(parametres);
        if (!pixel.isGrass()) {
            out.writeInt(pixel.getState());
        }
        if ((parametres & 0x1000000) != 0x0) {
            out.write(pixel.getTopHeight());
        }
        if (pixel.getNumberOfOverlays() != 0) {
            out.write(pixel.getOverlays().size());
            for (int i = 0; i < pixel.getOverlays().size(); ++i) {
                this.saveOverlay(pixel.getOverlays().get(i), out);
            }
        }
        if (pixel.getColourType() == 3) {
            out.writeInt(pixel.getCustomColour());
        }
        final int biome = pixel.getBiome();
        if (biome != -1) {
            if (biome < 255) {
                out.write(pixel.getBiome());
            }
            else {
                out.write(255);
                out.writeInt(biome);
            }
        }
    }
    
    private void loadPixel(final Integer next, final MapBlock pixel, final DataInputStream in, final int saveVersion, final World world, final int[] biomeBuffer, final BlockStateColorTypeCache colorTypeCache) throws IOException {
        int parametres;
        if (next != null) {
            parametres = next;
        }
        else {
            parametres = in.readInt();
        }
        if ((parametres & 0x1) != 0x0) {
            pixel.setState(in.readInt());
        }
        else {
            pixel.setState(Block.func_176210_f(Blocks.field_150349_c.func_176223_P()));
        }
        if ((parametres & 0x40) != 0x0) {
            pixel.setHeight(in.read());
        }
        else {
            pixel.setHeight(parametres >> 12 & 0xFF);
        }
        final boolean topHeightIsDifferent = saveVersion >= 4 && (parametres & 0x1000000) != 0x0;
        if (topHeightIsDifferent) {
            pixel.setTopHeight(in.read());
        }
        else {
            pixel.setTopHeight(pixel.getHeight());
        }
        this.overlayBuilder.startBuilding();
        if ((parametres & 0x2) != 0x0) {
            for (int amount = in.read(), i = 0; i < amount; ++i) {
                this.loadOverlay(pixel, in, saveVersion, world, biomeBuffer, colorTypeCache);
            }
        }
        this.overlayBuilder.finishBuilding(pixel);
        final int savedColourType = parametres >> 2 & 0x3;
        if (savedColourType == 3) {
            pixel.setColourType((byte)3);
            pixel.setCustomColour(in.readInt());
        }
        int biomeKey = -1;
        if ((savedColourType != 0 && savedColourType != 3) || (parametres & 0x100000) != 0x0) {
            final int biomeByte = in.read();
            if (saveVersion < 3 || biomeByte < 255) {
                biomeKey = biomeByte;
            }
            else {
                biomeKey = in.readInt();
            }
        }
        if (savedColourType != 3) {
            colorTypeCache.getBlockBiomeColour(world, Misc.getStateById(pixel.getState()), (BlockPos)null, biomeBuffer, biomeKey);
            pixel.setColourType((byte)biomeBuffer[0]);
            pixel.setCustomColour(biomeBuffer[2]);
        }
        pixel.setBiome(biomeKey);
        if (pixel.getColourType() == 3 && pixel.getCustomColour() == -1) {
            pixel.setColourType((byte)0);
        }
        pixel.setLight((byte)(parametres >> 8 & 0xF));
        pixel.setGlowing(this.mapProcessor.getMapWriter().isGlowing(Misc.getStateById(pixel.getState())));
    }
    
    private void saveOverlay(final Overlay o, final DataOutputStream out) throws IOException {
        out.writeInt(o.getParametres());
        if (!o.isWater()) {
            out.writeInt(o.getState());
        }
        if (o.getColourType() == 3) {
            out.writeInt(o.getCustomColour());
        }
    }
    
    private void loadOverlay(final MapBlock pixel, final DataInputStream in, final int saveVersion, final World world, final int[] biomeBuffer, final BlockStateColorTypeCache colourTypeCache) throws IOException {
        final int parametres = in.readInt();
        int state;
        if ((parametres & 0x1) != 0x0) {
            state = in.readInt();
        }
        else {
            state = Block.func_176210_f(Blocks.field_150355_j.func_176223_P());
        }
        int opacity = 1;
        if (saveVersion < 1 && (parametres & 0x2) != 0x0) {
            in.readInt();
        }
        byte savedColourType = (byte)(parametres >> 8 & 0x3);
        if (savedColourType == 2 || (parametres & 0x4) != 0x0) {
            biomeBuffer[0] = 3;
            biomeBuffer[2] = in.readInt();
            if (biomeBuffer[2] == -1) {
                biomeBuffer[0] = 0;
            }
            savedColourType = (byte)biomeBuffer[0];
        }
        if (saveVersion < 8) {
            if ((parametres & 0x8) != 0x0) {
                opacity = in.readInt();
            }
        }
        else {
            opacity = (parametres >> 11 & 0xF);
        }
        final byte light = (byte)(parametres >> 4 & 0xF);
        this.overlayBuilder.build(state, biomeBuffer, opacity, light, world, this.mapProcessor, null, 0, colourTypeCache, (savedColourType == 3) ? null : this.biomeInfoSupplier);
    }
    
    public boolean isRegionDetectionComplete() {
        return this.regionDetectionComplete;
    }
    
    public void setRegionDetectionComplete(final boolean regionDetectionComplete) {
        this.regionDetectionComplete = regionDetectionComplete;
    }
    
    public void requestCache(final LeveledRegion<?> region) {
        if (!this.toCacheContains(region)) {
            synchronized (region.getDim().regionsToCache) {
                region.getDim().regionsToCache.add(region);
            }
            if (WorldMapClientConfigUtils.getDebug()) {
                WorldMap.LOGGER.info("Requesting cache! " + region);
            }
        }
    }
    
    public LeveledRegion<?> removeToCache(final MapDimension mapDim, final int index) {
        synchronized (mapDim.regionsToCache) {
            return mapDim.regionsToCache.remove(index);
        }
    }
    
    public void removeToCache(final LeveledRegion<?> region) {
        synchronized (region.getDim().regionsToCache) {
            region.getDim().regionsToCache.remove(region);
        }
    }
    
    public boolean toCacheContains(final LeveledRegion<?> region) {
        synchronized (region.getDim().regionsToCache) {
            return region.getDim().regionsToCache.contains(region);
        }
    }
    
    public ArrayList<MapRegion> getToSave() {
        return this.toSave;
    }
    
    public LeveledRegion<?> getNextToLoadByViewing() {
        return this.nextToLoadByViewing;
    }
    
    @Deprecated
    public void setNextToLoadByViewing(final MapRegion nextToLoadByViewing) {
        this.setNextToLoadByViewing((LeveledRegion<?>)nextToLoadByViewing);
    }
    
    public void setNextToLoadByViewing(final LeveledRegion<?> nextToLoadByViewing) {
        this.nextToLoadByViewing = nextToLoadByViewing;
    }
    
    public void safeDelete(final Path filePath, final String extension) throws IOException {
        if (!filePath.getFileName().toString().endsWith(extension)) {
            throw new RuntimeException("Incorrect file extension: " + filePath);
        }
        Files.deleteIfExists(filePath);
    }
    
    public void safeMoveAndReplace(final Path fromPath, final Path toPath, final String fromExtension, final String toExtension) throws IOException {
        if (!toPath.getFileName().toString().endsWith(toExtension) || !fromPath.getFileName().toString().endsWith(fromExtension)) {
            throw new RuntimeException("Incorrect file extension: " + fromPath + " " + toPath);
        }
        IOUtils.safeMoveAndReplace(fromPath, toPath, true);
    }
    
    public int getSizeOfToLoadBranchCache() {
        return this.toLoadBranchCache.size();
    }
}
