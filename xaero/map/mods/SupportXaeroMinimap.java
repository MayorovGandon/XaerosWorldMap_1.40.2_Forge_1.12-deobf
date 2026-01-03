//Decompiled by Procyon!

package xaero.map.mods;

import xaero.map.radar.tracker.system.*;
import xaero.map.*;
import xaero.common.mods.*;
import xaero.hud.minimap.common.config.option.*;
import xaero.lib.common.config.option.*;
import xaero.lib.client.config.*;
import xaero.map.config.primary.option.*;
import xaero.lib.common.config.single.*;
import xaero.lib.common.config.*;
import xaero.hud.minimap.common.config.*;
import xaero.map.gui.*;
import xaero.common.*;
import net.minecraft.client.*;
import xaero.map.misc.*;
import net.minecraft.world.*;
import java.io.*;
import xaero.common.effect.*;
import net.minecraft.entity.player.*;
import java.util.*;
import xaero.lib.common.util.*;
import xaero.map.world.*;
import xaero.map.element.*;
import net.minecraft.client.settings.*;
import xaero.common.settings.*;
import xaero.lib.client.controls.util.*;
import xaero.lib.common.reflection.util.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.*;
import xaero.map.mods.gui.*;
import xaero.common.minimap.highlight.*;
import xaero.map.mods.minimap.element.*;
import xaero.common.minimap.render.radar.element.*;
import xaero.common.gui.*;
import xaero.common.minimap.waypoints.*;
import xaero.hud.minimap.config.util.*;
import xaero.map.mods.minimap.tracker.system.*;

public class SupportXaeroMinimap
{
    IXaeroMinimap modMain;
    public int compatibilityVersion;
    private boolean deathpoints;
    private boolean refreshWaypoints;
    private WaypointWorld waypointWorld;
    private WaypointWorld mapWaypointWorld;
    private Integer mapDimId;
    private double dimDiv;
    private WaypointSet waypointSet;
    private boolean allSets;
    private ArrayList<Waypoint> waypoints;
    private ArrayList<Waypoint> waypointsSorted;
    private WaypointMenuRenderer waypointMenuRenderer;
    private final WaypointRenderer waypointRenderer;
    private IPlayerTrackerSystem<?> minimapSyncedPlayerTrackerSystem;
    private WaypointWorld mouseBlockWaypointWorld;
    private WaypointWorld rightClickWaypointWorld;
    
    public SupportXaeroMinimap() {
        this.deathpoints = true;
        this.refreshWaypoints = true;
        try {
            final Class mmClassTest = Class.forName("xaero.pvp.BetterPVP");
            this.modMain = SupportBetterPVP.getMain();
            WorldMap.LOGGER.info("Xaero's WorldMap Mod: Better PVP found!");
        }
        catch (ClassNotFoundException e) {
            try {
                final Class mmClassTest2 = Class.forName("xaero.minimap.XaeroMinimap");
                this.modMain = SupportMinimap.getMain();
                WorldMap.LOGGER.info("Xaero's WorldMap Mod: Xaero's minimap found!");
            }
            catch (ClassNotFoundException ex) {}
        }
        if (this.modMain != null) {
            try {
                this.compatibilityVersion = SupportXaeroWorldmap.WORLDMAP_COMPATIBILITY_VERSION;
            }
            catch (NoSuchFieldError noSuchFieldError) {}
            if (this.compatibilityVersion < 3) {
                throw new RuntimeException("Xaero's Minimap 20.23.0 or newer required!");
            }
        }
        this.waypointRenderer = WaypointRenderer.Builder.begin().setMinimap(this).setSymbolCreator(WorldMap.waypointSymbolCreator).build();
    }
    
    public void register() {
        if (this.hasTrackedPlayerSystemSupport()) {
            WorldMap.playerTrackerSystemManager.register("minimap_synced", this.getMinimapSyncedPlayerTrackerSystem());
        }
    }
    
    public ArrayList<Waypoint> convertWaypoints(final double dimDiv) {
        if (this.waypointSet == null) {
            return null;
        }
        final ArrayList<Waypoint> result = new ArrayList<Waypoint>();
        if (!this.allSets) {
            this.convertSet(this.waypointSet, result, dimDiv);
        }
        else {
            final HashMap<String, WaypointSet> sets = (HashMap<String, WaypointSet>)this.waypointWorld.getSets();
            for (final WaypointSet set : sets.values()) {
                this.convertSet(set, result, dimDiv);
            }
        }
        final ClientConfigManager minimapConfigManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        this.deathpoints = (boolean)minimapConfigManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.DEATHPOINTS);
        return result;
    }
    
    private void convertSet(final WaypointSet set, final ArrayList<Waypoint> result, final double dimDiv) {
        final ArrayList<xaero.common.minimap.waypoints.Waypoint> list = (ArrayList<xaero.common.minimap.waypoints.Waypoint>)set.getList();
        final String setName = set.getName();
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        final SingleConfigManager<Config> primaryConfigManager = (SingleConfigManager<Config>)configManager.getPrimaryConfigManager();
        final boolean showingDisabled = (boolean)primaryConfigManager.getEffective((ConfigOption)WorldMapPrimaryClientConfigOptions.DISPLAY_DISABLED_WAYPOINTS);
        for (int i = 0; i < list.size(); ++i) {
            final xaero.common.minimap.waypoints.Waypoint w = list.get(i);
            if (showingDisabled || !w.isDisabled()) {
                result.add(this.convertWaypoint(w, true, setName, dimDiv));
            }
        }
    }
    
    public Waypoint convertWaypoint(final xaero.common.minimap.waypoints.Waypoint w, final boolean editable, final String setName, final double dimDiv) {
        int waypointType = 0;
        if (this.compatibilityVersion < 9) {
            waypointType = w.getType();
        }
        else {
            waypointType = w.getWaypointType();
        }
        final Waypoint converted = new Waypoint((Object)w, w.getX(), w.getY(), w.getZ(), w.getName(), w.getSymbol(), MinimapConfigConstants.COLORS[w.getColor()], waypointType, editable, setName, this.compatibilityVersion < 7 || w.isYIncluded(), dimDiv);
        converted.setDisabled(w.isDisabled());
        converted.setYaw(w.getYaw());
        converted.setRotation(w.isRotation());
        converted.setTemporary(w.isTemporary());
        converted.setGlobal(w.isGlobal());
        return converted;
    }
    
    public void openWaypoint(final GuiMap parent, final Waypoint waypoint) {
        if (!waypoint.isEditable()) {
            return;
        }
        final XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
        GuiScreen addScreen;
        if (this.compatibilityVersion >= 6) {
            addScreen = (GuiScreen)new GuiAddWaypoint(this.modMain, minimapSession.getWaypointsManager(), (GuiScreen)parent, (GuiScreen)parent, (xaero.common.minimap.waypoints.Waypoint)waypoint.getOriginal(), this.waypointWorld.getContainer().getRootContainer().getKey(), this.waypointWorld, waypoint.getSetName());
        }
        else {
            addScreen = (GuiScreen)new GuiAddWaypoint(this.modMain, minimapSession.getWaypointsManager(), (GuiScreen)parent, (xaero.common.minimap.waypoints.Waypoint)waypoint.getOriginal(), this.waypointWorld.getContainer().getRootContainer().getKey(), this.waypointWorld);
        }
        Minecraft.func_71410_x().func_147108_a(addScreen);
    }
    
    public void createWaypoint(final GuiMap parent, final int x, final int y, final int z, final double coordDimensionScale, final boolean rightClick) {
        if (this.waypointWorld == null) {
            return;
        }
        final XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
        final WaypointsManager waypointsManager = minimapSession.getWaypointsManager();
        final WaypointWorld coordSourceWaypointWorld = rightClick ? this.rightClickWaypointWorld : this.mouseBlockWaypointWorld;
        GuiScreen addScreen;
        if (this.hasDimSwitchSupport()) {
            addScreen = (GuiScreen)new GuiAddWaypoint(this.modMain, minimapSession.getWaypointsManager(), (GuiScreen)parent, (GuiScreen)parent, (xaero.common.minimap.waypoints.Waypoint)null, this.waypointWorld.getContainer().getRootContainer().getKey(), this.waypointWorld, this.waypointWorld.getCurrent(), true, x, y, z, coordDimensionScale, coordSourceWaypointWorld);
        }
        else {
            int legacyX = x;
            int legacyY = y;
            int legacyZ = z;
            if (coordDimensionScale != Misc.getDimensionTypeScale((World)Minecraft.func_71410_x().field_71441_e)) {
                final double legacyDimDiv = Misc.getDimensionTypeScale((World)Minecraft.func_71410_x().field_71441_e) / coordDimensionScale;
                legacyX = (int)Math.floor(legacyX / legacyDimDiv);
                legacyZ = (int)Math.floor(legacyZ / legacyDimDiv);
            }
            if (coordSourceWaypointWorld != this.waypointWorld) {
                legacyY = -1;
            }
            if (this.compatibilityVersion < 8) {
                double dimDiv;
                if (this.compatibilityVersion < 2) {
                    dimDiv = (waypointsManager.divideBy8(this.waypointWorld.getContainer().getKey()) ? 8.0 : 1.0);
                }
                else {
                    dimDiv = waypointsManager.getDimensionDivision(this.waypointWorld.getContainer().getKey());
                }
                xaero.common.minimap.waypoints.Waypoint w;
                if (this.compatibilityVersion >= 7 && legacyY == -1) {
                    w = new xaero.common.minimap.waypoints.Waypoint((int)Math.floor(legacyX * dimDiv), legacyY, (int)Math.floor(legacyZ * dimDiv), "", "", -1, 0, false, false);
                }
                else {
                    w = new xaero.common.minimap.waypoints.Waypoint((int)Math.floor(legacyX * dimDiv), legacyY, (int)Math.floor(legacyZ * dimDiv), "", "", -1);
                }
                if (this.compatibilityVersion >= 6) {
                    addScreen = (GuiScreen)new GuiAddWaypoint(this.modMain, minimapSession.getWaypointsManager(), (GuiScreen)parent, (GuiScreen)parent, w, this.waypointWorld.getContainer().getRootContainer().getKey(), this.waypointWorld, this.waypointWorld.getCurrent());
                }
                else {
                    addScreen = (GuiScreen)new GuiAddWaypoint(this.modMain, minimapSession.getWaypointsManager(), (GuiScreen)parent, w, this.waypointWorld.getContainer().getRootContainer().getKey(), this.waypointWorld);
                }
            }
            else {
                addScreen = (GuiScreen)new GuiAddWaypoint(this.modMain, minimapSession.getWaypointsManager(), (GuiScreen)parent, (GuiScreen)parent, (xaero.common.minimap.waypoints.Waypoint)null, this.waypointWorld.getContainer().getRootContainer().getKey(), this.waypointWorld, this.waypointWorld.getCurrent(), true, legacyX, legacyY, legacyZ);
            }
        }
        Minecraft.func_71410_x().func_147108_a(addScreen);
    }
    
    public boolean canCreateWaypoint(final int y, final boolean rightClick) {
        if (this.compatibilityVersion > 6) {
            return true;
        }
        final WaypointWorld coordSourceWaypointWorld = rightClick ? this.rightClickWaypointWorld : this.mouseBlockWaypointWorld;
        return coordSourceWaypointWorld == this.waypointWorld && y != -1;
    }
    
    public boolean canShareLocation(final int y) {
        return y != -1 || this.compatibilityVersion > 6;
    }
    
    public void createTempWaypoint(final int x, final int y, final int z, final double mapDimensionScale, final boolean rightClick) {
        if (this.waypointWorld == null) {
            return;
        }
        final XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
        final WaypointsManager waypointsManager = minimapSession.getWaypointsManager();
        final WaypointWorld coordSourceWaypointWorld = rightClick ? this.rightClickWaypointWorld : this.mouseBlockWaypointWorld;
        if (this.hasDimSwitchSupport()) {
            waypointsManager.createTemporaryWaypoints(this.waypointWorld, x, y, z, y != -1 && coordSourceWaypointWorld == this.waypointWorld, mapDimensionScale);
        }
        else {
            int legacyX = x;
            int legacyY = y;
            int legacyZ = z;
            if (mapDimensionScale != Misc.getDimensionTypeScale((World)Minecraft.func_71410_x().field_71441_e)) {
                final double legacyDimDiv = Misc.getDimensionTypeScale((World)Minecraft.func_71410_x().field_71441_e) / mapDimensionScale;
                legacyX = (int)Math.floor(legacyX / legacyDimDiv);
                legacyZ = (int)Math.floor(legacyZ / legacyDimDiv);
            }
            if (coordSourceWaypointWorld != this.waypointWorld) {
                legacyY = -1;
            }
            if (this.compatibilityVersion >= 7 && legacyY == -1) {
                waypointsManager.createTemporaryWaypoints(this.waypointWorld, legacyX, legacyY, legacyZ, false);
            }
            else {
                waypointsManager.createTemporaryWaypoints(this.waypointWorld, legacyX, legacyY, legacyZ);
            }
        }
        this.requestWaypointsRefresh();
    }
    
    public boolean canTeleport(final WaypointWorld world) {
        final XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
        final WaypointsManager waypointsManager = minimapSession.getWaypointsManager();
        return world != null && waypointsManager.canTeleport(waypointsManager.isWorldTeleportable(world), world);
    }
    
    public void teleportToWaypoint(final GuiScreen screen, final Waypoint w) {
        this.teleportToWaypoint(screen, w, this.waypointWorld);
    }
    
    public void teleportToWaypoint(final GuiScreen screen, final Waypoint w, final WaypointWorld world) {
        if (world == null) {
            return;
        }
        final XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
        final WaypointsManager waypointsManager = minimapSession.getWaypointsManager();
        waypointsManager.teleportToWaypoint((xaero.common.minimap.waypoints.Waypoint)w.getOriginal(), world, screen);
    }
    
    public void disableWaypoint(final Waypoint waypoint) {
        ((xaero.common.minimap.waypoints.Waypoint)waypoint.getOriginal()).setDisabled(!((xaero.common.minimap.waypoints.Waypoint)waypoint.getOriginal()).isDisabled());
        try {
            this.modMain.getSettings().saveWaypoints(this.waypointWorld);
        }
        catch (IOException e) {
            WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
        }
        waypoint.setDisabled(((xaero.common.minimap.waypoints.Waypoint)waypoint.getOriginal()).isDisabled());
        waypoint.setTemporary(((xaero.common.minimap.waypoints.Waypoint)waypoint.getOriginal()).isTemporary());
    }
    
    public void deleteWaypoint(final Waypoint waypoint) {
        if (!this.allSets) {
            this.waypointSet.getList().remove(waypoint.getOriginal());
        }
        else {
            final HashMap<String, WaypointSet> sets = (HashMap<String, WaypointSet>)this.waypointWorld.getSets();
            for (final WaypointSet set : sets.values()) {
                if (set.getList().remove(waypoint.getOriginal())) {
                    break;
                }
            }
        }
        try {
            this.modMain.getSettings().saveWaypoints(this.waypointWorld);
        }
        catch (IOException e) {
            WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
        }
        this.waypoints.remove(waypoint);
        this.waypointsSorted.remove(waypoint);
        this.waypointMenuRenderer.updateFilteredList();
    }
    
    public void checkWaypoints(final boolean multiplayer, final int dimId, final String multiworldId, final int width, final int height, final GuiMap screen, final MapWorld mapWorld) {
        final XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
        final WaypointsManager waypointsManager = minimapSession.getWaypointsManager();
        final String containerId = waypointsManager.getAutoRootContainerID() + "/" + waypointsManager.getDimensionDirectoryName(dimId);
        final String mapBasedMW = multiplayer ? multiworldId : "waypoints";
        this.mapWaypointWorld = waypointsManager.getWorld(containerId, mapBasedMW);
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        final SingleConfigManager<Config> primaryConfigManager = (SingleConfigManager<Config>)configManager.getPrimaryConfigManager();
        WaypointWorld checkingWaypointWorld;
        if (primaryConfigManager.getEffective((ConfigOption)WorldMapPrimaryClientConfigOptions.ONLY_CURRENT_MAP_WAYPOINTS)) {
            checkingWaypointWorld = this.mapWaypointWorld;
        }
        else {
            checkingWaypointWorld = waypointsManager.getCurrentWorld();
        }
        final Minecraft mc = Minecraft.func_71410_x();
        if (this.compatibilityVersion >= 4 && Misc.hasEffect((EntityPlayer)mc.field_71439_g, Effects.NO_WAYPOINTS)) {
            checkingWaypointWorld = null;
        }
        else if (this.compatibilityVersion >= 5 && (Misc.hasEffect((EntityPlayer)mc.field_71439_g, Effects.NO_WAYPOINTS_BENEFICIAL) || Misc.hasEffect((EntityPlayer)mc.field_71439_g, Effects.NO_WAYPOINTS_HARMFUL))) {
            checkingWaypointWorld = null;
        }
        boolean shouldRefresh = this.refreshWaypoints;
        if (this.mapDimId == null || dimId != this.mapDimId) {
            shouldRefresh = true;
            this.mapDimId = dimId;
        }
        if (checkingWaypointWorld != this.waypointWorld) {
            this.waypointWorld = checkingWaypointWorld;
            screen.closeRightClick();
            if (screen.waypointMenu) {
                screen.func_146280_a(Minecraft.func_71410_x(), width, height);
            }
            shouldRefresh = true;
        }
        final WaypointSet checkingSet = (checkingWaypointWorld == null) ? null : checkingWaypointWorld.getCurrentSet();
        if (checkingSet != this.waypointSet) {
            this.waypointSet = checkingSet;
            shouldRefresh = true;
        }
        final ClientConfigManager minimapConfigManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        final boolean renderAllSetsConfig = (boolean)minimapConfigManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.WAYPOINTS_ALL_SETS);
        if (this.allSets != renderAllSetsConfig) {
            this.allSets = renderAllSetsConfig;
            shouldRefresh = true;
        }
        if (shouldRefresh) {
            this.dimDiv = ((this.waypointWorld == null) ? 1.0 : this.getDimensionDivision(mapWorld, waypointsManager, this.waypointWorld.getContainer().getKey(), dimId));
            this.waypoints = this.convertWaypoints(this.dimDiv);
            if (this.waypoints != null) {
                Collections.sort(this.waypoints);
                this.waypointsSorted = new ArrayList<Waypoint>();
                final ArrayList<KeySortableByOther<Waypoint>> sortingList = new ArrayList<KeySortableByOther<Waypoint>>();
                for (final Waypoint w : this.waypoints) {
                    sortingList.add((KeySortableByOther<Waypoint>)new KeySortableByOther((Object)w, new Comparable[] { w.getComparisonName(), w.getName() }));
                }
                Collections.sort(sortingList);
                for (final KeySortableByOther<Waypoint> e : sortingList) {
                    this.waypointsSorted.add((Waypoint)e.getKey());
                }
            }
            else {
                this.waypointsSorted = null;
            }
            this.waypointMenuRenderer.updateFilteredList();
        }
        this.refreshWaypoints = false;
    }
    
    private double getDimensionDivision(final MapWorld mapWorld, final WaypointsManager waypointsManager, final String worldContainerID, final int mapDimId) {
        if (worldContainerID == null || Minecraft.func_71410_x().field_71441_e == null) {
            return 1.0;
        }
        final String dimPart = worldContainerID.substring(worldContainerID.lastIndexOf(47) + 1);
        final Integer waypointDimId = waypointsManager.getDimensionForDirectoryName(dimPart);
        final MapDimension waypointMapDimension = mapWorld.getDimension(waypointDimId);
        final MapDimension mapDimension = mapWorld.getDimension(mapDimId);
        final MapDimensionTypeInfo waypointDimType = MapDimension.getDimensionType(waypointMapDimension, waypointDimId);
        final MapDimensionTypeInfo mapDimType = MapDimension.getDimensionType(mapDimension, mapDimId);
        final double waypointDimScale = (waypointDimType == null) ? 1.0 : waypointDimType.getCoordinateScale();
        final double mapDimScale = (mapDimType == null) ? 1.0 : mapDimType.getCoordinateScale();
        return mapDimScale / waypointDimScale;
    }
    
    public HoveredMapElementHolder<?, ?> renderWaypointsMenu(final GuiMap gui, final double scale, final int width, final int height, final int mouseX, final int mouseY, final boolean leftMousePressed, final boolean leftMouseClicked, final HoveredMapElementHolder<?, ?> hovered, final Minecraft mc) {
        return (HoveredMapElementHolder<?, ?>)this.waypointMenuRenderer.renderMenu(gui, scale, width, height, mouseX, mouseY, leftMousePressed, leftMouseClicked, (HoveredMapElementHolder)hovered, mc);
    }
    
    public void requestWaypointsRefresh() {
        this.refreshWaypoints = true;
    }
    
    public KeyBinding getWaypointKeyBinding() {
        return ModSettings.newWaypoint;
    }
    
    public KeyBinding getTempWaypointKeyBinding() {
        return ModSettings.keyInstantWaypoint;
    }
    
    public KeyBinding getTempWaypointsMenuKeyBinding() {
        return ModSettings.keyWaypoints;
    }
    
    public void onMapKeyPressed(final boolean mouse, final int code, final GuiMap screen) {
        KeyBinding kb = null;
        final Minecraft mc = Minecraft.func_71410_x();
        if (KeyMappingUtils.inputMatches(mouse, code, this.getToggleRadarKey(), 0)) {
            screen.onRadarButton(screen.getRadarButton());
        }
        if (KeyMappingUtils.inputMatches(mouse, code, ModSettings.keyToggleMapWaypoints, 0)) {
            this.getWaypointMenuRenderer().onRenderWaypointsButton(screen, screen.field_146294_l, screen.field_146295_m);
        }
        if (this.compatibilityVersion >= 8 && KeyMappingUtils.inputMatches(mouse, code, ModSettings.keyReverseEntityRadar, 0)) {
            ReflectionUtils.setReflectFieldValue((Object)ModSettings.keyReverseEntityRadar, GuiMap.KEY_BINDING_PRESSED_FIELD, (Object)true);
        }
        if (KeyMappingUtils.inputMatches(mouse, code, ModSettings.keySwitchSet, 0)) {
            kb = ModSettings.keySwitchSet;
        }
        if (KeyMappingUtils.inputMatches(mouse, code, ModSettings.keyAllSets, 0)) {
            kb = ModSettings.keyAllSets;
        }
        if (KeyMappingUtils.inputMatches(mouse, code, ModSettings.keyWaypoints, 0)) {
            kb = ModSettings.keyWaypoints;
        }
        final KeyBinding minimapSettingsKB = (KeyBinding)this.modMain.getSettingsKey();
        if (KeyMappingUtils.inputMatches(mouse, code, minimapSettingsKB, 0)) {
            kb = minimapSettingsKB;
        }
        final KeyBinding listPlayerAlternative = this.getMinimapListPlayersAlternative();
        if (listPlayerAlternative != null && KeyMappingUtils.inputMatches(mouse, code, listPlayerAlternative, 0)) {
            ReflectionUtils.setReflectFieldValue((Object)listPlayerAlternative, GuiMap.KEY_BINDING_PRESSED_FIELD, (Object)true);
        }
        if (kb != null) {
            if (kb == ModSettings.keyWaypoints) {
                this.openWaypointsMenu(mc, screen);
                return;
            }
            if (minimapSettingsKB != null && kb == minimapSettingsKB) {
                mc.func_147108_a(this.getSettingsScreen((GuiScreen)screen));
                return;
            }
            this.handleMinimapKeyBinding(kb, screen);
        }
    }
    
    public boolean onMapKeyReleased(final boolean mouse, final int code, final GuiMap screen) {
        boolean result = false;
        if (this.compatibilityVersion >= 8 && KeyMappingUtils.inputMatches(mouse, code, ModSettings.keyReverseEntityRadar, 0)) {
            ReflectionUtils.setReflectFieldValue((Object)ModSettings.keyReverseEntityRadar, GuiMap.KEY_BINDING_PRESSED_FIELD, (Object)false);
            result = true;
        }
        final KeyBinding listPlayerAlternative = this.getMinimapListPlayersAlternative();
        if (listPlayerAlternative != null && KeyMappingUtils.inputMatches(mouse, code, listPlayerAlternative, 0)) {
            ReflectionUtils.setReflectFieldValue((Object)listPlayerAlternative, GuiMap.KEY_BINDING_PRESSED_FIELD, (Object)false);
            result = true;
        }
        return result;
    }
    
    public void handleMinimapKeyBinding(final KeyBinding kb, final GuiMap screen) {
        final XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
        minimapSession.getControls().keyDown(kb, false, false);
        if ((kb == ModSettings.keySwitchSet || kb == ModSettings.keyAllSets) && screen.waypointMenu) {
            screen.func_146280_a(Minecraft.func_71410_x(), screen.field_146294_l, screen.field_146295_m);
        }
    }
    
    public void drawSetChange(final ScaledResolution resolution) {
        final XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
        this.modMain.getInterfaces().getMinimapInterface().getWaypointsGuiRenderer().drawSetChange(minimapSession.getWaypointsManager(), resolution);
    }
    
    public float getMinimapBrightnessOldCompatibility() {
        return this.modMain.getSupportMods().worldmapSupport.getMinimapBrightness();
    }
    
    public GuiScreen getSettingsScreen(final GuiScreen current) {
        return (GuiScreen)this.modMain.getGuiHelper().getMinimapSettingsFromScreen(current);
    }
    
    public String getControlsTooltip() {
        return I18n.func_135052_a("gui.xaero_box_controls_minimap", new Object[] { KeyMappingUtils.getKeyName(ModSettings.newWaypoint), KeyMappingUtils.getKeyName(ModSettings.keyInstantWaypoint), KeyMappingUtils.getKeyName(ModSettings.keySwitchSet), KeyMappingUtils.getKeyName(ModSettings.keyAllSets), KeyMappingUtils.getKeyName(ModSettings.keyWaypoints) });
    }
    
    public void onMapMouseRelease(final double par1, final double par2, final int par3) {
        this.waypointMenuRenderer.onMapMouseRelease(par1, par2, par3);
    }
    
    public void onMapConstruct() {
        this.waypointMenuRenderer = new WaypointMenuRenderer(new WaypointMenuRenderContext(), new WaypointMenuRenderProvider(this), this.waypointRenderer);
    }
    
    public void onMapInit(final GuiMap mapScreen, final Minecraft mc, final int width, final int height) {
        this.waypointMenuRenderer.onMapInit(mapScreen, mc, width, height, this.waypointWorld, this.modMain, XaeroMinimapSession.getCurrentSession());
    }
    
    public ArrayList<Waypoint> getWaypointsSorted() {
        return this.waypointsSorted;
    }
    
    public boolean waypointExists(final Waypoint w) {
        return this.waypoints != null && this.waypoints.contains(w);
    }
    
    public void toggleTemporaryWaypoint(final Waypoint waypoint) {
        ((xaero.common.minimap.waypoints.Waypoint)waypoint.getOriginal()).setTemporary(!((xaero.common.minimap.waypoints.Waypoint)waypoint.getOriginal()).isTemporary());
        try {
            this.modMain.getSettings().saveWaypoints(this.waypointWorld);
        }
        catch (IOException e) {
            WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
        }
        waypoint.setDisabled(((xaero.common.minimap.waypoints.Waypoint)waypoint.getOriginal()).isDisabled());
        waypoint.setTemporary(((xaero.common.minimap.waypoints.Waypoint)waypoint.getOriginal()).isTemporary());
    }
    
    public void openWaypointsMenu(final Minecraft mc, final GuiMap screen) {
        final XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
        if (this.compatibilityVersion >= 6) {
            mc.func_147108_a((GuiScreen)new GuiWaypoints(this.modMain, minimapSession, (GuiScreen)screen, (GuiScreen)screen));
        }
        else {
            mc.func_147108_a((GuiScreen)new GuiWaypoints(this.modMain, minimapSession, (GuiScreen)screen));
        }
    }
    
    public boolean hidingWaypointCoordinates() {
        final ClientConfigManager minimapConfigManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        return (boolean)minimapConfigManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.HIDE_WAYPOINT_COORDINATES);
    }
    
    public void shareWaypoint(final Waypoint waypoint, final GuiMap screen, final WaypointWorld world) {
        final XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
        minimapSession.getWaypointSharing().shareWaypoint((GuiScreen)screen, (xaero.common.minimap.waypoints.Waypoint)waypoint.getOriginal(), world);
    }
    
    public void shareLocation(final GuiMap guiMap, final int rightClickX, final int rightClickY, final int rightClickZ) {
        final int wpColor = (int)(MinimapConfigConstants.COLORS.length * Math.random());
        xaero.common.minimap.waypoints.Waypoint minimapLocationWaypoint;
        if (this.compatibilityVersion < 7) {
            minimapLocationWaypoint = new xaero.common.minimap.waypoints.Waypoint(rightClickX, rightClickY, rightClickZ, "Shared Location", "S", wpColor);
        }
        else {
            minimapLocationWaypoint = new xaero.common.minimap.waypoints.Waypoint(rightClickX, (rightClickY == -1) ? 0 : rightClickY, rightClickZ, "Shared Location", "S", wpColor, 0, false, rightClickY != -1);
        }
        final Waypoint locationWaypoint = this.convertWaypoint(minimapLocationWaypoint, false, "", 1.0);
        this.shareWaypoint(locationWaypoint, guiMap, this.rightClickWaypointWorld);
    }
    
    public WaypointWorld getMapWaypointWorld() {
        return this.mapWaypointWorld;
    }
    
    public WaypointWorld getWaypointWorld() {
        return this.waypointWorld;
    }
    
    public double getDimDiv() {
        return this.dimDiv;
    }
    
    public int getArrowColorIndex() {
        final ClientConfigManager minimapConfigManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        return (int)minimapConfigManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.ARROW_COLOR);
    }
    
    public float[] getArrowColor() {
        final int arrowColour = this.getArrowColorIndex();
        if (arrowColour < 0 || arrowColour >= MinimapConfigConstants.ARROW_COLORS.length) {
            return null;
        }
        return MinimapConfigConstants.ARROW_COLORS[arrowColour];
    }
    
    public String getSubWorldNameToRender() {
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        final SingleConfigManager<Config> primaryConfigManager = (SingleConfigManager<Config>)configManager.getPrimaryConfigManager();
        final boolean onlyCurrentMapWaypoints = (boolean)primaryConfigManager.getEffective((ConfigOption)WorldMapPrimaryClientConfigOptions.ONLY_CURRENT_MAP_WAYPOINTS);
        if (onlyCurrentMapWaypoints || this.waypointWorld == null) {
            return null;
        }
        if (this.waypointWorld != this.mapWaypointWorld) {
            return I18n.func_135052_a("gui.xaero_wm_using_custom_subworld", new Object[] { this.waypointWorld.getContainer().getSubName() });
        }
        return null;
    }
    
    public void registerMinimapHighlighters(final Object highlighterRegistry) {
    }
    
    public ArrayList<Waypoint> getWaypoints() {
        return this.waypoints;
    }
    
    public boolean getDeathpoints() {
        return this.deathpoints;
    }
    
    public WaypointRenderer getWaypointRenderer() {
        return this.waypointRenderer;
    }
    
    public WaypointMenuRenderer getWaypointMenuRenderer() {
        return this.waypointMenuRenderer;
    }
    
    public void onClearHighlightHash(final int regionX, final int regionZ) {
        if (this.compatibilityVersion >= 11) {
            final XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
            if (minimapSession != null) {
                final DimensionHighlighterHandler highlightHandler = minimapSession.getMinimapProcessor().getMinimapWriter().getDimensionHighlightHandler();
                if (highlightHandler != null) {
                    highlightHandler.requestRefresh(regionX, regionZ);
                }
            }
        }
    }
    
    public void createRadarRendererWrapper(final Object radarRenderer) {
        new RadarRendererWrapperHelper().createWrapper(this.modMain, (RadarRenderer)radarRenderer);
    }
    
    public KeyBinding getToggleRadarKey() {
        if (this.compatibilityVersion < 10) {
            return null;
        }
        return ModSettings.keyToggleRadar;
    }
    
    public void onClearHighlightHashes() {
        if (this.compatibilityVersion >= 13) {
            final XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
            if (minimapSession != null) {
                final DimensionHighlighterHandler highlightHandler = minimapSession.getMinimapProcessor().getMinimapWriter().getDimensionHighlightHandler();
                if (highlightHandler != null) {
                    highlightHandler.requestRefresh();
                }
            }
        }
    }
    
    public KeyBinding getToggleAllyPlayersKey() {
        if (this.hasTrackedPlayerSystemSupport()) {
            return ModSettings.keyToggleTrackedPlayers;
        }
        return null;
    }
    
    public void onSessionFinalized() {
        this.waypointWorld = null;
        this.mapWaypointWorld = null;
    }
    
    public void openWaypointWorldTeleportCommandScreen(final GuiScreen parent, final GuiScreen escape) {
        final XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
        if (minimapSession == null) {
            return;
        }
        final WaypointsManager waypointsManager = minimapSession.getWaypointsManager();
        final String containerId = waypointsManager.getAutoRootContainerID();
        final WaypointWorldRootContainer container = waypointsManager.getWorldContainerNullable(containerId).getRootContainer();
        if (container != null) {
            if (this.compatibilityVersion >= 15) {
                Minecraft.func_71410_x().func_147108_a((GuiScreen)new GuiWorldTpCommand(this.modMain, parent, escape, container));
            }
            else {
                final WaypointWorld firstWorld = container.getFirstWorld();
                if (firstWorld != null) {
                    Minecraft.func_71410_x().func_147108_a((GuiScreen)new GuiWorldTpCommand(this.modMain, parent, escape, firstWorld));
                }
            }
        }
    }
    
    public KeyBinding getMinimapListPlayersAlternative() {
        if (this.compatibilityVersion < 16) {
            return null;
        }
        return ModSettings.keyAlternativeListPlayers;
    }
    
    public int getCaveStart(final int defaultWorldMapStart, final boolean isMapScreen) {
        if (!this.modMain.getSettings().getMinimap()) {
            return defaultWorldMapStart;
        }
        if (!MinimapConfigClientUtils.getEffectiveCaveModeAllowed()) {
            return isMapScreen ? defaultWorldMapStart : Integer.MAX_VALUE;
        }
        final int usedCaving = this.getUsedCaving();
        if (usedCaving == -1) {
            final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
            final SingleConfigManager<Config> primaryConfigManager = (SingleConfigManager<Config>)configManager.getPrimaryConfigManager();
            return (int)primaryConfigManager.getEffective(WorldMapPrimaryClientConfigOptions.CAVE_MODE_START);
        }
        return usedCaving;
    }
    
    public int getUsedCaving() {
        final XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
        if (minimapSession != null) {
            return minimapSession.getMinimapProcessor().getMinimapWriter().getLoadedCaving();
        }
        return -1;
    }
    
    public boolean isFairPlay() {
        return this.compatibilityVersion >= 8 && this.modMain.isFairPlay();
    }
    
    public boolean hasTrackedPlayerSystemSupport() {
        return this.compatibilityVersion >= 17;
    }
    
    public IPlayerTrackerSystem<?> getMinimapSyncedPlayerTrackerSystem() {
        if (this.minimapSyncedPlayerTrackerSystem == null) {
            this.minimapSyncedPlayerTrackerSystem = (IPlayerTrackerSystem<?>)new MinimapSyncedPlayerTrackerSystem(this);
        }
        return this.minimapSyncedPlayerTrackerSystem;
    }
    
    public boolean hasDimSwitchSupport() {
        return this.compatibilityVersion >= 20;
    }
    
    public void onBlockHover() {
        this.mouseBlockWaypointWorld = this.mapWaypointWorld;
    }
    
    public void onRightClick() {
        this.rightClickWaypointWorld = this.mouseBlockWaypointWorld;
    }
}
