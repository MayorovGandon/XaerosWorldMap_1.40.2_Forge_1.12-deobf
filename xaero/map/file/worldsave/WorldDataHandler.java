//Decompiled by Procyon!

package xaero.map.file.worldsave;

import xaero.map.executor.*;
import net.minecraft.world.*;
import net.minecraftforge.common.*;
import xaero.map.world.*;
import java.nio.file.*;
import xaero.map.region.*;
import xaero.map.*;
import xaero.map.capabilities.*;
import net.minecraft.util.*;
import java.io.*;

public class WorldDataHandler
{
    private final Executor renderExecutor;
    private WorldDataReader reader;
    private MapDimensionTypeInfo dimType;
    private WorldServer worldServer;
    private File worldDir;
    
    public WorldDataHandler(final WorldDataReader reader, final Executor renderExecutor) {
        this.reader = reader;
        this.renderExecutor = renderExecutor;
    }
    
    public void handleRenderExecutor() {
        this.renderExecutor.drainTasks();
    }
    
    public void prepareSingleplayer(final World world, final MapProcessor mapProcessor) {
        final MapWorld mapWorld = mapProcessor.getMapWorld();
        if (world != null && mapWorld.getCurrentDimension().isUsingWorldSave()) {
            final Integer dimId = mapWorld.getCurrentDimensionId();
            this.dimType = mapWorld.getCurrentDimension().getDimensionType();
            this.worldServer = DimensionManager.getWorld((int)dimId, false);
            if (this.worldServer != null) {
                this.worldDir = this.worldServer.getChunkSaveLocation();
            }
            else {
                final WorldServer overworldWorld = DimensionManager.getWorld(0);
                final Path rootSaveLocation = (overworldWorld == null) ? null : overworldWorld.getChunkSaveLocation().toPath();
                if (rootSaveLocation != null && this.dimType != null) {
                    this.worldDir = rootSaveLocation.resolve(this.dimType.getSavePath()).toFile();
                }
                else {
                    this.worldDir = null;
                }
            }
        }
        else {
            this.worldDir = null;
        }
    }
    
    public Result buildRegion(final World world, final MapRegion region, final boolean loading, final int[] chunkCountDest) throws IOException {
        if (this.worldDir == null) {
            WorldMap.LOGGER.info("Tried loading a region for a null world dir!");
            return Result.CANCEL;
        }
        final ServerWorldLoaded loadedCap = (this.worldServer == null) ? null : ((ServerWorldLoaded)this.worldServer.getCapability(ServerWorldCapabilities.LOADED_CAP, (EnumFacing)null));
        final boolean buildResult = this.reader.buildRegion(world, (loadedCap == null || !loadedCap.loaded) ? null : this.worldServer, this.dimType, region, this.worldDir, loading, chunkCountDest, this.renderExecutor);
        return buildResult ? Result.SUCCESS : Result.FAIL;
    }
    
    public static void onServerWorldUnload(final WorldServer sw) {
        final ServerWorldLoaded loadedCap = (ServerWorldLoaded)sw.getCapability(ServerWorldCapabilities.LOADED_CAP, (EnumFacing)null);
        if (loadedCap != null) {
            synchronized (loadedCap) {
                loadedCap.loaded = false;
            }
        }
    }
    
    public WorldDataReader getWorldDataReader() {
        return this.reader;
    }
    
    public File getWorldDir() {
        return this.worldDir;
    }
    
    Executor getWorldDataRenderExecutor() {
        return this.renderExecutor;
    }
    
    public enum Result
    {
        SUCCESS, 
        FAIL, 
        CANCEL;
    }
}
