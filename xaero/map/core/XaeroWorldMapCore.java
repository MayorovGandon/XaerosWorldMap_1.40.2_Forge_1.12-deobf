//Decompiled by Procyon!

package xaero.map.core;

import java.lang.reflect.*;
import net.minecraft.world.chunk.*;
import net.minecraft.client.*;
import net.minecraft.client.network.*;
import net.minecraft.network.play.server.*;
import xaero.map.*;
import net.minecraft.block.state.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import xaero.map.world.*;
import xaero.map.file.*;
import java.util.function.*;
import java.nio.file.*;
import xaero.lib.common.util.*;
import java.io.*;

public class XaeroWorldMapCore
{
    public static Field chunkCleanField;
    public static WorldMapSession currentSession;
    private static boolean DETECTING_BLOCK_COLOR_RESOLVERS;
    private static Object DETECTING_BLOCK_COLOR_RESOLVERS_GRASS_COLOR;
    private static Object DETECTING_BLOCK_COLOR_RESOLVERS_FOLIAGE_COLOR;
    private static Object DETECTING_BLOCK_COLOR_RESOLVERS_WATER_COLOR;
    private static Object DETECTING_BLOCK_COLOR_RESOLVERS_RESULT;
    
    public static void ensureField() {
        if (XaeroWorldMapCore.chunkCleanField == null) {
            try {
                XaeroWorldMapCore.chunkCleanField = Chunk.class.getDeclaredField("xaero_wm_chunkClean");
            }
            catch (NoSuchFieldException | SecurityException ex2) {
                final Exception ex;
                final Exception e = ex;
                throw new RuntimeException(e);
            }
        }
    }
    
    public static void chunkUpdateCallback(final int chunkX, final int chunkZ) {
        ensureField();
        final World world = (World)Minecraft.func_71410_x().field_71441_e;
        if (world != null) {
            try {
                for (int x = chunkX - 1; x < chunkX + 2; ++x) {
                    for (int z = chunkZ - 1; z < chunkZ + 2; ++z) {
                        final Chunk chunk = world.func_72964_e(x, z);
                        if (chunk != null) {
                            XaeroWorldMapCore.chunkCleanField.set(chunk, false);
                        }
                    }
                }
            }
            catch (IllegalArgumentException | IllegalAccessException ex2) {
                final Exception ex;
                final Exception e = ex;
                throw new RuntimeException(e);
            }
        }
    }
    
    public static void onChunkData(final SPacketChunkData packetIn) {
        chunkUpdateCallback(packetIn.func_149273_e(), packetIn.func_149271_f());
    }
    
    public static void onBlockChange(final SPacketBlockChange packetIn) {
        chunkUpdateCallback(packetIn.func_179827_b().func_177958_n() >> 4, packetIn.func_179827_b().func_177952_p() >> 4);
    }
    
    public static void onMultiBlockChange(final SPacketMultiBlockChange packetIn) {
        chunkUpdateCallback(packetIn.func_179844_a()[0].func_180090_a().func_177958_n() >> 4, packetIn.func_179844_a()[0].func_180090_a().func_177952_p() >> 4);
    }
    
    public static void onPlayNetHandler(final NetHandlerPlayClient netHandler, final SPacketJoinGame packet) {
        if (!WorldMap.loaded) {
            return;
        }
        try {
            final IWorldMapClientPlayNetHandler netHandlerAccess = (IWorldMapClientPlayNetHandler)netHandler;
            if (netHandlerAccess.getXaero_worldmapSession() != null) {
                return;
            }
            if (XaeroWorldMapCore.currentSession != null) {
                WorldMap.LOGGER.info("Previous world map session still active. Probably using MenuMobs. Forcing it to end...");
                cleanupCurrentSession();
            }
            final WorldMapSession worldmapSession = new WorldMapSession();
            (XaeroWorldMapCore.currentSession = worldmapSession).init();
            netHandlerAccess.setXaero_worldmapSession(worldmapSession);
            WorldMap.settings.updateRegionCacheHashCode();
        }
        catch (Throwable e) {
            if (XaeroWorldMapCore.currentSession != null) {
                cleanupCurrentSession();
            }
            final RuntimeException wrappedException = new RuntimeException("Exception initializing Xaero's World Map! ", e);
            WorldMap.crashHandler.setCrashedBy(wrappedException);
        }
    }
    
    private static void cleanupCurrentSession() {
        try {
            XaeroWorldMapCore.currentSession.cleanup();
        }
        catch (Throwable supressed) {
            WorldMap.LOGGER.error("suppressed exception", supressed);
        }
        finally {
            XaeroWorldMapCore.currentSession = null;
        }
    }
    
    public static void onPlayNetHandlerCleanup(final NetHandlerPlayClient netHandler) {
        if (!WorldMap.loaded) {
            return;
        }
        try {
            final WorldMapSession netHandlerSession = ((IWorldMapClientPlayNetHandler)netHandler).getXaero_worldmapSession();
            if (netHandlerSession == null) {
                return;
            }
            try {
                netHandlerSession.cleanup();
            }
            finally {
                if (netHandlerSession == XaeroWorldMapCore.currentSession) {
                    XaeroWorldMapCore.currentSession = null;
                }
                ((IWorldMapClientPlayNetHandler)netHandler).setXaero_worldmapSession((WorldMapSession)null);
            }
        }
        catch (Throwable e) {
            final RuntimeException wrappedException = new RuntimeException("Exception finalizing Xaero's World Map! ", e);
            WorldMap.crashHandler.setCrashedBy(wrappedException);
        }
    }
    
    public static boolean onGetBlockColor(final Object colorResolver) {
        if (XaeroWorldMapCore.DETECTING_BLOCK_COLOR_RESOLVERS && Minecraft.func_71410_x().func_152345_ab()) {
            if (XaeroWorldMapCore.DETECTING_BLOCK_COLOR_RESOLVERS_RESULT == null || (isVanillaResolver(colorResolver) && !isVanillaResolver(XaeroWorldMapCore.DETECTING_BLOCK_COLOR_RESOLVERS_RESULT))) {
                XaeroWorldMapCore.DETECTING_BLOCK_COLOR_RESOLVERS_RESULT = colorResolver;
            }
            return true;
        }
        return false;
    }
    
    private static boolean isVanillaResolver(final Object colorResolver) {
        return colorResolver == XaeroWorldMapCore.DETECTING_BLOCK_COLOR_RESOLVERS_GRASS_COLOR || colorResolver == XaeroWorldMapCore.DETECTING_BLOCK_COLOR_RESOLVERS_FOLIAGE_COLOR || colorResolver == XaeroWorldMapCore.DETECTING_BLOCK_COLOR_RESOLVERS_WATER_COLOR;
    }
    
    public static Object detectColorResolver(final IBlockState state, final World world, final BlockPos pos, final int tint, final Object GRASS_COLOR, final Object FOLIAGE_COLOR, final Object WATER_COLOR) {
        XaeroWorldMapCore.DETECTING_BLOCK_COLOR_RESOLVERS = true;
        XaeroWorldMapCore.DETECTING_BLOCK_COLOR_RESOLVERS_GRASS_COLOR = GRASS_COLOR;
        XaeroWorldMapCore.DETECTING_BLOCK_COLOR_RESOLVERS_FOLIAGE_COLOR = FOLIAGE_COLOR;
        XaeroWorldMapCore.DETECTING_BLOCK_COLOR_RESOLVERS_WATER_COLOR = WATER_COLOR;
        XaeroWorldMapCore.DETECTING_BLOCK_COLOR_RESOLVERS_RESULT = null;
        try {
            Minecraft.func_71410_x().func_184125_al().func_186724_a(state, (IBlockAccess)world, pos, tint);
        }
        catch (Throwable t) {
            WorldMap.LOGGER.error("suppressed exception", t);
        }
        XaeroWorldMapCore.DETECTING_BLOCK_COLOR_RESOLVERS = false;
        return XaeroWorldMapCore.DETECTING_BLOCK_COLOR_RESOLVERS_RESULT;
    }
    
    public static void onDeleteWorld(final String saveName) {
        if (!WorldMap.loaded) {
            return;
        }
        final String folderName = saveName;
        final String worldRootId = MapWorld.convertWorldFolderToRootId(4, folderName);
        if (!worldRootId.isEmpty()) {
            final Path worldMapCacheFolder = MapSaveLoad.getRootFolder(worldRootId);
            if (worldMapCacheFolder.toFile().exists()) {
                try {
                    IOUtils.deleteFileIf(worldMapCacheFolder, (Predicate)new Predicate<Path>() {
                        @Override
                        public boolean test(final Path path) {
                            final String pathString = worldMapCacheFolder.relativize(path).toString().replace('\\', '/');
                            return pathString.contains("/cache/") || pathString.endsWith("/cache") || pathString.contains("/cache_");
                        }
                    }, 20);
                    WorldMap.LOGGER.info(String.format("Deleted world map cache at %s", worldMapCacheFolder));
                }
                catch (IOException e) {
                    WorldMap.LOGGER.error(String.format("Failed to delete world map cache at %s!", worldMapCacheFolder), (Throwable)e);
                }
            }
        }
    }
    
    public static void onMinecraftRunTick() {
        if (WorldMap.events != null) {
            WorldMap.events.handleClientRunTickStart();
        }
    }
    
    static {
        XaeroWorldMapCore.chunkCleanField = null;
        XaeroWorldMapCore.DETECTING_BLOCK_COLOR_RESOLVERS_RESULT = null;
    }
}
