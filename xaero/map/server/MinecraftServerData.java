//Decompiled by Procyon!

package xaero.map.server;

import xaero.map.server.radar.tracker.*;
import java.nio.file.*;
import xaero.map.server.level.*;
import java.util.*;
import xaero.map.*;
import xaero.map.config.util.*;
import java.io.*;
import net.minecraft.server.*;

public class MinecraftServerData
{
    private final SyncedPlayerTrackerSystemManager syncedPlayerTrackerSystemManager;
    private final SyncedPlayerTracker syncedPlayerTracker;
    private final Map<Path, LevelMapProperties> levelProperties;
    private final LevelMapPropertiesIO propertiesIO;
    
    public MinecraftServerData(final SyncedPlayerTrackerSystemManager syncedPlayerTrackerSystemManager, final SyncedPlayerTracker syncedPlayerTracker) {
        this.syncedPlayerTrackerSystemManager = syncedPlayerTrackerSystemManager;
        this.syncedPlayerTracker = syncedPlayerTracker;
        this.levelProperties = new HashMap<Path, LevelMapProperties>();
        this.propertiesIO = new LevelMapPropertiesIO();
    }
    
    public LevelMapProperties getLevelProperties(final Path path) {
        LevelMapProperties properties = this.levelProperties.get(path);
        if (properties == null) {
            properties = new LevelMapProperties();
            try {
                this.propertiesIO.load(path, properties);
            }
            catch (FileNotFoundException fnfe) {
                try {
                    this.propertiesIO.save(path, properties);
                }
                catch (IOException e) {
                    properties.setUsable(false);
                    WorldMap.LOGGER.warn("Failed to initialize map properties for a world due to an IO exception. This shouldn't be a problem if it's not a \"real\" world. Message: {}", (Object)e.getMessage());
                    if (WorldMapClientConfigUtils.getDebug()) {
                        WorldMap.LOGGER.warn("Full exception: ", (Throwable)e);
                    }
                }
            }
            catch (IOException e2) {
                throw new RuntimeException(e2);
            }
            this.levelProperties.put(path, properties);
        }
        return properties;
    }
    
    public SyncedPlayerTrackerSystemManager getSyncedPlayerTrackerSystemManager() {
        return this.syncedPlayerTrackerSystemManager;
    }
    
    public SyncedPlayerTracker getSyncedPlayerTracker() {
        return this.syncedPlayerTracker;
    }
    
    public static MinecraftServerData get(final MinecraftServer server) {
        return ((IMinecraftServer)server).getXaeroWorldMapServerData();
    }
}
