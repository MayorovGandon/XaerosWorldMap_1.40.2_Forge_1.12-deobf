//Decompiled by Procyon!

package xaero.map;

import net.minecraftforge.fml.common.*;
import xaero.map.controls.*;
import xaero.map.mods.gui.*;
import xaero.deallocator.*;
import xaero.map.region.*;
import xaero.map.file.export.*;
import xaero.map.pool.buffer.*;
import xaero.map.pool.*;
import xaero.map.graphics.*;
import xaero.map.biome.*;
import net.minecraft.util.*;
import xaero.map.settings.*;
import xaero.map.element.*;
import xaero.map.server.player.*;
import xaero.map.radar.tracker.*;
import xaero.map.events.*;
import xaero.lib.common.config.channel.*;
import xaero.map.server.*;
import xaero.map.common.config.channel.register.handler.*;
import xaero.lib.common.config.channel.register.handler.*;
import java.util.function.*;
import xaero.lib.client.config.channel.register.handler.*;
import xaero.map.config.channel.register.handler.*;
import xaero.lib.common.config.channel.register.*;
import xaero.map.common.config.*;
import net.minecraftforge.fml.relauncher.*;
import net.minecraftforge.common.*;
import java.nio.file.attribute.*;
import java.nio.file.*;
import java.io.*;
import xaero.lib.common.packet.*;
import xaero.map.message.*;
import xaero.map.server.mods.*;
import xaero.map.capabilities.*;
import xaero.lib.*;
import xaero.lib.common.config.primary.option.*;
import xaero.lib.common.config.option.*;
import net.minecraft.client.*;
import xaero.map.misc.*;
import xaero.lib.patreon.*;
import xaero.map.config.primary.option.*;
import xaero.map.radar.tracker.system.impl.*;
import xaero.map.radar.tracker.system.*;
import xaero.map.mods.*;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.*;

@Mod(modid = "xaeroworldmap", name = "Xaero's World Map", guiFactory = "xaero.map.gui.ConfigGuiFactory", acceptedMinecraftVersions = "[1.12,1.12.2]", acceptableRemoteVersions = "*", dependencies = "required-after:xaerolib@[1.0.42,);before:xaerominimap@[25.3.2,);before:xaerobetterpvp@[25.3.2,);")
public class WorldMap
{
    public static final Logger LOGGER;
    @Mod.Instance("xaeroworldmap")
    public static WorldMap INSTANCE;
    public static int MINIMAP_COMPATIBILITY_VERSION;
    private static final String versionID_minecraft = "1.12";
    public static String versionID;
    public static int newestUpdateID;
    public static boolean isOutdated;
    public static String fileLayout;
    public static String fileLayoutID;
    public static String latestVersion;
    public static String latestVersionMD5;
    public static boolean loaded;
    public static ClientEvents events;
    public static ControlsRegister controlsRegister;
    public static WaypointSymbolCreator waypointSymbolCreator;
    public static ByteBufferDeallocator bufferDeallocator;
    public static TextureUploadBenchmark textureUploadBenchmark;
    public static OverlayManager overlayManager;
    public static PNGExporter pngExporter;
    public static TextureUploadPool.Normal normalTextureUploadPool;
    public static TextureUploadPool.Compressed compressedTextureUploadPool;
    public static TextureUploadPool.BranchUpdate branchUpdatePool;
    public static TextureUploadPool.BranchUpdate branchUpdateAllocatePool;
    public static TextureUploadPool.BranchDownload branchDownloadPool;
    public static TextureUploadPool.SubsequentNormal subsequentNormalTextureUploadPool;
    public static TextureDirectBufferPool textureDirectBufferPool;
    public static MapTilePool tilePool;
    public static MapLimiter mapLimiter;
    public static GLObjectDeleter glObjectDeleter;
    public static MapRunner mapRunner;
    public static Thread mapRunnerThread;
    public static CrashHandler crashHandler;
    public static MapBiomes mapBiomes;
    public static final ResourceLocation guiTextures;
    public static ModSettings settings;
    public static WorldMapClient worldMapClient;
    public static MapElementRenderHandler mapElementRenderHandler;
    public static ServerPlayerTickHandler serverPlayerTickHandler;
    public static PlayerTrackerSystemManager playerTrackerSystemManager;
    public static PlayerTrackerMapElementRenderer trackedPlayerRenderer;
    public static PlayerTrackerMenuRenderer trackedPlayerMenuRenderer;
    public static IPacketHandler messageHandler;
    public static CommonEvents commonEvents;
    public static ModCommonEvents modCommonEvents;
    private Path configSubFolder;
    private Path defaultConfigsSubFolder;
    private boolean shouldLoadLegacySettings;
    private ConfigChannel configChannel;
    public static boolean detailed_debug;
    public static boolean pauseRequests;
    public static boolean extraDebug;
    public static LegacyCommonConfigIO commonConfigIO;
    private WorldMapServer worldmapServer;
    public static File modJAR;
    public static File configFolder;
    public static File optionsFile;
    public static File saveFolder;
    
    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent event) throws IOException {
        WorldMap.versionID = "1.12_" + event.getModMetadata().version;
        final String modId = this.getClass().getAnnotation(Mod.class).modid();
        WorldMap.configFolder = event.getModConfigurationDirectory();
        this.configSubFolder = WorldMap.configFolder.toPath().resolve("xaero").resolve("world-map");
        this.defaultConfigsSubFolder = WorldMap.configFolder.toPath().resolveSibling("defaultconfigs").resolve("xaero").resolve("world-map");
        this.shouldLoadLegacySettings = !Files.exists(this.configSubFolder, new LinkOption[0]);
        this.configChannel = ConfigChannel.Builder.begin().setId(new ResourceLocation(modId, "main")).setCommonRegistryHandler((IConfigChannelCommonRegistryHandler)new WorldMapChannelCommonRegistryHandler()).setClientRegistryHandlerSupplier((Supplier)new Supplier<IConfigChannelClientRegistryHandler>() {
            @Override
            public IConfigChannelClientRegistryHandler get() {
                return (IConfigChannelClientRegistryHandler)new WorldMapChannelClientRegistryHandler();
            }
        }).setLogger(WorldMap.LOGGER).setConfigPath(this.configSubFolder).setDefaultConfigsPath(this.defaultConfigsSubFolder).setDefaultEnforcedServerProfileNodePath("xaero.world_map.enforced_server_profile").build();
        ConfigChannelRegistry.INSTANCE.register(this.configChannel);
        new LegacyCommonConfigInit().init(event.getSide() == Side.SERVER, event.getModConfigurationDirectory().toPath(), "xaeroworldmap-common.txt");
        WorldMap.commonEvents = new CommonEvents();
        WorldMap.modCommonEvents = new ModCommonEvents();
        MinecraftForge.EVENT_BUS.register((Object)WorldMap.commonEvents);
        MinecraftForge.EVENT_BUS.register((Object)WorldMap.modCommonEvents);
        if (event.getSide() == Side.SERVER) {
            return;
        }
        WorldMap.trackedPlayerRenderer = PlayerTrackerMapElementRenderer.Builder.begin().build();
        WorldMap.trackedPlayerMenuRenderer = PlayerTrackerMenuRenderer.Builder.begin().setRenderer(WorldMap.trackedPlayerRenderer).build();
        (WorldMap.worldMapClient = new WorldMapClient()).preInit(event, modId);
        if (event.getSourceFile().getName().endsWith(".jar")) {
            WorldMap.modJAR = event.getSourceFile();
        }
        final Path gameDir = getGameDir();
        WorldMap.optionsFile = WorldMap.configFolder.toPath().resolve("xaeroworldmap.txt").toFile();
        final Path oldSaveFolder4 = gameDir.resolve("XaeroWorldMap");
        final Path xaeroFolder = gameDir.resolve("xaero");
        if (!Files.exists(xaeroFolder, new LinkOption[0])) {
            Files.createDirectories(xaeroFolder, (FileAttribute<?>[])new FileAttribute[0]);
        }
        WorldMap.saveFolder = xaeroFolder.resolve("world-map").toFile();
        if (oldSaveFolder4.toFile().exists() && !WorldMap.saveFolder.exists()) {
            Files.move(oldSaveFolder4, WorldMap.saveFolder.toPath(), new CopyOption[0]);
        }
        final Path oldSaveFolder5 = WorldMap.configFolder.toPath().getParent().resolve("XaeroWorldMap");
        final File oldOptionsFile = gameDir.resolve("xaeroworldmap.txt").toFile();
        final File oldSaveFolder6 = gameDir.resolve("mods").resolve("XaeroWorldMap").toFile();
        final File oldSaveFolder7 = gameDir.resolve("config").resolve("XaeroWorldMap").toFile();
        if (oldOptionsFile.exists() && !WorldMap.optionsFile.exists()) {
            Files.move(oldOptionsFile.toPath(), WorldMap.optionsFile.toPath(), new CopyOption[0]);
        }
        if (oldSaveFolder6.exists() && !WorldMap.saveFolder.exists()) {
            Files.move(oldSaveFolder6.toPath(), WorldMap.saveFolder.toPath(), new CopyOption[0]);
        }
        if (oldSaveFolder7.exists() && !WorldMap.saveFolder.exists()) {
            Files.move(oldSaveFolder7.toPath(), WorldMap.saveFolder.toPath(), new CopyOption[0]);
        }
        if (oldSaveFolder5.toFile().exists() && !WorldMap.saveFolder.exists()) {
            Files.move(oldSaveFolder5, WorldMap.saveFolder.toPath(), new CopyOption[0]);
        }
        if (!WorldMap.saveFolder.exists()) {
            Files.createDirectories(WorldMap.saveFolder.toPath(), (FileAttribute<?>[])new FileAttribute[0]);
        }
    }
    
    @Mod.EventHandler
    public void load(final FMLInitializationEvent event) throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        WorldMap.messageHandler = PacketHandlerRegistry.INSTANCE.register(new ResourceLocation("xaeroworldmap", "main"), 1000000, "1.0");
        new WorldMapMessageRegister().register(WorldMap.messageHandler);
        WorldMap.serverPlayerTickHandler = new ServerPlayerTickHandler();
        SupportServerMods.check();
        if (event.getSide() == Side.SERVER) {
            (this.worldmapServer = new WorldMapServer()).load(event);
            return;
        }
        WorldMap.events = new ClientEvents();
        MinecraftForge.EVENT_BUS.register((Object)WorldMap.events);
        WorldMap.waypointSymbolCreator = new WaypointSymbolCreator();
        WorldMap.controlsRegister = new ControlsRegister();
        ServerWorldCapabilities.registerCapabilities();
        WorldMap.bufferDeallocator = new ByteBufferDeallocator();
        WorldMap.tilePool = new MapTilePool();
        WorldMap.overlayManager = new OverlayManager();
        WorldMap.pngExporter = new PNGExporter(WorldMap.configFolder.toPath().getParent().resolve("map exports"));
        WorldMap.mapLimiter = new MapLimiter();
        WorldMap.normalTextureUploadPool = new TextureUploadPool.Normal(256);
        WorldMap.compressedTextureUploadPool = new TextureUploadPool.Compressed(256);
        WorldMap.branchUpdatePool = new TextureUploadPool.BranchUpdate(256, false);
        WorldMap.branchUpdateAllocatePool = new TextureUploadPool.BranchUpdate(256, true);
        WorldMap.branchDownloadPool = new TextureUploadPool.BranchDownload(256);
        WorldMap.textureDirectBufferPool = new TextureDirectBufferPool();
        WorldMap.subsequentNormalTextureUploadPool = new TextureUploadPool.SubsequentNormal(256);
        WorldMap.textureUploadBenchmark = new TextureUploadBenchmark(new int[] { 512, 512, 512, 256, 256, 256, 256 });
        WorldMap.glObjectDeleter = new GLObjectDeleter();
        WorldMap.crashHandler = new CrashHandler();
        WorldMap.mapBiomes = new MapBiomes();
        (WorldMap.mapRunnerThread = new Thread((Runnable)(WorldMap.mapRunner = new MapRunner()))).start();
    }
    
    void loadLaterCommon() {
        if (WorldMap.commonConfigIO.shouldEnableEveryoneTracksEveryone()) {
            XaeroLib.INSTANCE.getLibConfigChannel().getPrimaryCommonConfigManager().getConfig().set((ConfigOption)LibPrimaryCommonConfigOptions.EVERYONE_TRACKS_EVERYONE, (Object)true);
        }
    }
    
    public static Path getGameDir() {
        return Minecraft.func_71410_x().field_71412_D.toPath().toAbsolutePath();
    }
    
    @Mod.EventHandler
    public void postInit(final FMLPostInitializationEvent event) {
        this.loadLaterCommon();
        if (event.getSide() == Side.SERVER) {
            this.worldmapServer.loadLater();
            this.worldmapServer = null;
            WorldMap.loaded = true;
            return;
        }
        WorldMap.settings = new ModSettings();
        if (this.shouldLoadLegacySettings) {
            try {
                WorldMap.settings.loadSettings();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.configChannel.getClientConfigProfileIO().save(this.configChannel.getClientConfigManager().getCurrentProfile());
            this.configChannel.getPrimaryClientConfigManagerIO().save();
        }
        WorldMap.settings.updateRegionCacheHashCode();
        Patreon.checkPatreon();
        Internet.checkModVersion();
        if (WorldMap.isOutdated) {
            final PatreonMod patreonEntry = Patreon.getMods().get(WorldMap.fileLayoutID);
            if (patreonEntry != null) {
                patreonEntry.modJar = WorldMap.modJAR;
                patreonEntry.currentVersion = WorldMap.versionID;
                patreonEntry.latestVersion = WorldMap.latestVersion;
                patreonEntry.md5 = WorldMap.latestVersionMD5;
                patreonEntry.onVersionIgnore = new Runnable() {
                    @Override
                    public void run() {
                        WorldMap.this.getConfigs().getPrimaryClientConfigManager().getConfig().set(WorldMapPrimaryClientConfigOptions.IGNORED_UPDATE, (Object)WorldMap.newestUpdateID);
                        WorldMap.this.getConfigs().getPrimaryClientConfigManagerIO().save();
                    }
                };
                Patreon.addOutdatedMod((Object)patreonEntry);
            }
        }
        WorldMap.playerTrackerSystemManager.register("map_synced", (IPlayerTrackerSystem)new SyncedPlayerTrackerSystem());
        SupportMods.load();
        WorldMap.mapElementRenderHandler = MapElementRenderHandler.Builder.begin().build();
        WorldMap.loaded = true;
    }
    
    @Mod.EventHandler
    public void onServerStarting(final FMLServerStartingEvent event) {
        WorldMap.commonEvents.onServerStarting(event);
    }
    
    @Mod.EventHandler
    public void onServerStopped(final FMLServerStoppedEvent event) {
        WorldMap.commonEvents.onServerStopped(event);
    }
    
    public static void onSessionFinalized() {
        WorldMap.mapLimiter.onSessionFinalized();
        if (SupportMods.minimap()) {
            SupportMods.xaeroMinimap.onSessionFinalized();
        }
    }
    
    public ConfigChannel getConfigs() {
        return this.configChannel;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        WorldMap.MINIMAP_COMPATIBILITY_VERSION = 26;
        WorldMap.fileLayout = "XaerosWorldMap_&mod_Forge_&mc.jar";
        WorldMap.fileLayoutID = "worldmap";
        guiTextures = new ResourceLocation("xaeroworldmap", "gui/gui.png");
        WorldMap.playerTrackerSystemManager = new PlayerTrackerSystemManager();
        WorldMap.detailed_debug = false;
        WorldMap.pauseRequests = false;
        WorldMap.extraDebug = false;
        WorldMap.modJAR = null;
    }
}
