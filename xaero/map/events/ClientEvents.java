//Decompiled by Procyon!

package xaero.map.events;

import com.mojang.realmsclient.dto.*;
import java.lang.reflect.*;
import net.minecraftforge.fml.common.eventhandler.*;
import com.mojang.realmsclient.gui.screens.*;
import xaero.lib.common.reflection.util.*;
import com.mojang.realmsclient.util.*;
import xaero.map.mods.*;
import xaero.map.*;
import java.io.*;
import net.minecraftforge.fml.common.gameevent.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import com.google.common.util.concurrent.*;
import java.util.concurrent.*;
import xaero.map.misc.*;
import org.lwjgl.input.*;
import net.minecraft.client.renderer.*;
import net.minecraftforge.client.*;
import java.util.*;
import xaero.lib.patreon.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraft.client.multiplayer.*;
import net.minecraftforge.event.*;
import net.minecraft.world.*;
import net.minecraft.util.*;
import xaero.map.capabilities.*;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.event.world.*;
import xaero.map.file.worldsave.*;
import net.minecraftforge.client.event.*;
import net.minecraft.util.text.*;
import net.minecraftforge.fml.relauncher.*;

public class ClientEvents
{
    private RealmsServer latestRealm;
    private Field realmsTaskField;
    private Field realmsTaskServerField;
    
    @SubscribeEvent
    public void guiButtonClick(final GuiScreenEvent.ActionPerformedEvent event) {
    }
    
    @SubscribeEvent
    public void guiOpen(final GuiOpenEvent event) {
        if (event.getGui() instanceof GuiScreenRealmsProxy && ((GuiScreenRealmsProxy)event.getGui()).func_154321_a() instanceof RealmsLongRunningMcoTaskScreen) {
            try {
                if (this.realmsTaskField == null) {
                    (this.realmsTaskField = ReflectionUtils.getFieldReflection((Class)RealmsLongRunningMcoTaskScreen.class, "task", "", "", "task")).setAccessible(true);
                }
                if (this.realmsTaskServerField == null) {
                    (this.realmsTaskServerField = ReflectionUtils.getFieldReflection((Class)RealmsTasks.RealmsGetServerDetailsTask.class, "server", "", "", "server")).setAccessible(true);
                }
                final RealmsLongRunningMcoTaskScreen realmsTaskScreen = (RealmsLongRunningMcoTaskScreen)((GuiScreenRealmsProxy)event.getGui()).func_154321_a();
                final Object task = this.realmsTaskField.get(realmsTaskScreen);
                if (task instanceof RealmsTasks.RealmsGetServerDetailsTask) {
                    final RealmsTasks.RealmsGetServerDetailsTask realmsTask = (RealmsTasks.RealmsGetServerDetailsTask)task;
                    final RealmsServer realm = (RealmsServer)this.realmsTaskServerField.get(realmsTask);
                    if (realm != null && (this.latestRealm == null || realm.id != this.latestRealm.id)) {
                        this.latestRealm = realm;
                    }
                }
            }
            catch (Exception e) {
                WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
            }
        }
    }
    
    @SubscribeEvent
    public void modelBake(final TextureStitchEvent.Post event) throws IOException {
        final WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
        if (worldmapSession != null) {
            final MapProcessor mapProcessor = worldmapSession.getMapProcessor();
            mapProcessor.getMapWriter().getColorTypeCache().updateGrassColor();
            mapProcessor.getMapWriter().requestCachedColoursClear();
            mapProcessor.getBlockStateShortShapeCache().reset();
        }
        if (SupportMods.minimap()) {
            WorldMap.waypointSymbolCreator.resetChars();
        }
        if (WorldMap.settings != null) {
            WorldMap.settings.updateRegionCacheHashCode();
        }
    }
    
    @SubscribeEvent
    public void renderTick(final TickEvent.RenderTickEvent event) throws Exception {
        if (!WorldMap.loaded) {
            return;
        }
        final Minecraft mc = Minecraft.func_71410_x();
        if (event.phase == TickEvent.Phase.END) {
            WorldMap.glObjectDeleter.work();
        }
        if (mc.field_71439_g != null) {
            final WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
            if (worldmapSession != null) {
                final MapProcessor mapProcessor = worldmapSession.getMapProcessor();
                if (event.phase == TickEvent.Phase.END) {
                    final ScaledResolution scaledresolution = new ScaledResolution(mc);
                    mapProcessor.onRenderProcess(mc, scaledresolution);
                    mc.field_71454_w = false;
                    mapProcessor.resetRenderStartTime();
                    final Queue<FutureTask<?>> minecraftScheduledTasks = mapProcessor.getMinecraftScheduledTasks();
                    final ListenableFutureTask<?> listenablefuturetask = (ListenableFutureTask<?>)ListenableFutureTask.create((Callable)mapProcessor.getRenderStartTimeUpdater());
                    synchronized (minecraftScheduledTasks) {
                        final FutureTask<?>[] currentTasks = minecraftScheduledTasks.toArray(new FutureTask[0]);
                        minecraftScheduledTasks.clear();
                        minecraftScheduledTasks.add((FutureTask<?>)listenablefuturetask);
                        for (final FutureTask<?> t : currentTasks) {
                            minecraftScheduledTasks.add(t);
                        }
                    }
                }
                else if (event.phase == TickEvent.Phase.START) {
                    if (!SupportMods.vivecraft && MapProcessor.shouldSkipWorldRender()) {
                        Misc.setShaderProgram(0);
                        final ScaledResolution scaledresolution = new ScaledResolution(mc);
                        final int i1 = scaledresolution.func_78326_a();
                        final int j1 = scaledresolution.func_78328_b();
                        final int k1 = Mouse.getX() * i1 / mc.field_71443_c;
                        final int l1 = j1 - Mouse.getY() * j1 / mc.field_71440_d - 1;
                        GlStateManager.func_179126_j();
                        GlStateManager.func_179083_b(0, 0, mc.field_71443_c, mc.field_71440_d);
                        GlStateManager.func_179128_n(5889);
                        GlStateManager.func_179096_D();
                        GlStateManager.func_179128_n(5888);
                        GlStateManager.func_179096_D();
                        mc.field_71460_t.func_78478_c();
                        GlStateManager.func_179086_m(256);
                        ForgeHooksClient.drawScreen(mc.field_71462_r, k1, l1, 0.0f);
                        mc.field_71454_w = true;
                    }
                    if (mapProcessor != null) {
                        mapProcessor.setMainValues();
                    }
                }
            }
        }
    }
    
    @SubscribeEvent
    public void renderTick(final GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!Patreon.needsNotification() && WorldMap.isOutdated) {
            WorldMap.isOutdated = false;
        }
    }
    
    @SubscribeEvent
    public void spawnSet(final PlayerSetSpawnEvent event) {
        if (event.getEntityPlayer().field_70170_p instanceof WorldClient) {
            final WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
            if (worldmapSession != null) {
                final MapProcessor mapProcessor = worldmapSession.getMapProcessor();
                mapProcessor.updateWorldSpawn(event.getNewSpawn(), (WorldClient)event.getEntityPlayer().field_70170_p);
            }
        }
    }
    
    @SubscribeEvent
    public void worldCapabilities(final AttachCapabilitiesEvent<World> event) {
        if (event.getObject() instanceof WorldServer) {
            event.addCapability(new ResourceLocation("xaeroworldmap", "server_world_caps"), (ICapabilityProvider)new ServerWorldCapabilities());
        }
    }
    
    @SubscribeEvent
    public void worldUnload(final WorldEvent.Unload event) {
        if (Minecraft.func_71410_x().field_71439_g != null) {
            final WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
            if (worldmapSession != null) {
                final MapProcessor mapProcessor = worldmapSession.getMapProcessor();
                if (event.getWorld() == mapProcessor.mainWorld) {
                    mapProcessor.onWorldUnload();
                }
            }
        }
        if (event.getWorld() instanceof WorldServer) {
            final WorldServer sw = (WorldServer)event.getWorld();
            WorldDataHandler.onServerWorldUnload(sw);
        }
    }
    
    public RealmsServer getLatestRealm() {
        return this.latestRealm;
    }
    
    @SubscribeEvent
    protected void handleRenderGameOverlayEventPost(final RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
            final WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
            final MapProcessor mapProcessor = (worldmapSession == null) ? null : worldmapSession.getMapProcessor();
            final String crosshairMessage = (mapProcessor == null) ? null : mapProcessor.getCrosshairMessage();
            if (crosshairMessage != null) {
                final int messageWidth = Minecraft.func_71410_x().field_71466_p.func_78256_a(crosshairMessage);
                GlStateManager.func_179084_k();
                Minecraft.func_71410_x().field_71466_p.func_175063_a(crosshairMessage, (float)(event.getResolution().func_78326_a() / 2 - messageWidth / 2), (float)(event.getResolution().func_78328_b() / 2 + 60), -1);
                GlStateManager.func_179147_l();
            }
        }
    }
    
    @SubscribeEvent
    public void handleClientChatReceivedEvent(final ClientChatReceivedEvent e) {
        if (e.getMessage() == null) {
            return;
        }
        final ITextComponent text = e.getMessage();
        final String textString = text.func_150254_d();
        if (e.getType() == ChatType.SYSTEM && textString.contains("§r§e§s§e§t§x§a§e§r§o")) {
            final WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
            worldmapSession.getMapProcessor().setConsideringNetherFairPlayMessage(false);
        }
        if (e.getType() == ChatType.SYSTEM && textString.contains("§x§a§e§r§o§w§m§n§e§t§h§e§r§i§s§f§a§i§r")) {
            final WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
            worldmapSession.getMapProcessor().setConsideringNetherFairPlayMessage(true);
        }
        if (e.getType() == ChatType.SYSTEM && textString.contains("§f§a§i§r§x§a§e§r§o")) {
            final WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
            worldmapSession.getMapProcessor().setFairplayMessageReceived(true);
        }
    }
    
    @SubscribeEvent
    public void playerTick(final TickEvent.PlayerTickEvent event) throws Exception {
        if (event.side == Side.CLIENT && event.player == Minecraft.func_71410_x().field_71439_g && event.phase == TickEvent.Phase.START) {
            final WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
            if (worldmapSession != null) {
                worldmapSession.getControlsHandler().handleKeyEvents();
            }
        }
    }
    
    @SubscribeEvent
    public void clientTick(final TickEvent.ClientTickEvent event) throws Exception {
        if (event.phase == TickEvent.Phase.START) {
            if (!WorldMap.loaded) {
                return;
            }
            WorldMap.worldMapClient.onTick();
            if (Minecraft.func_71410_x().field_71439_g != null) {
                WorldMap.crashHandler.checkForCrashes();
                final WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
                if (worldmapSession != null) {
                    final MapProcessor mapProcessor = worldmapSession.getMapProcessor();
                    mapProcessor.onClientTickStart();
                }
            }
        }
    }
    
    public void handleClientRunTickStart() {
        if (Minecraft.func_71410_x().field_71439_g != null) {
            if (!WorldMap.loaded) {
                return;
            }
            WorldMap.crashHandler.checkForCrashes();
            final WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
            if (worldmapSession != null) {
                worldmapSession.getMapProcessor().getWorldDataHandler().handleRenderExecutor();
            }
        }
    }
}
