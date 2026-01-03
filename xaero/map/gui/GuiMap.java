//Decompiled by Procyon!

package xaero.map.gui;

import xaero.lib.client.gui.*;
import java.lang.reflect.*;
import net.minecraft.entity.*;
import xaero.map.element.*;
import xaero.map.*;
import xaero.map.common.config.option.*;
import xaero.lib.common.config.option.*;
import xaero.map.mods.*;
import xaero.lib.client.config.*;
import xaero.map.config.util.*;
import xaero.lib.client.gui.widget.*;
import java.util.function.*;
import xaero.map.controls.*;
import xaero.lib.client.controls.util.*;
import net.minecraft.client.*;
import net.minecraft.util.text.*;
import xaero.lib.client.gui.config.context.*;
import net.minecraft.client.gui.*;
import xaero.map.misc.*;
import java.io.*;
import org.lwjgl.input.*;
import xaero.map.effects.*;
import net.minecraft.entity.player.*;
import xaero.lib.common.util.*;
import xaero.map.animation.*;
import xaero.map.config.primary.option.*;
import java.util.*;
import xaero.map.graphics.*;
import xaero.map.common.config.*;
import net.minecraft.world.biome.*;
import net.minecraft.client.resources.*;
import xaero.map.mods.gui.*;
import xaero.lib.common.config.single.*;
import xaero.lib.common.config.*;
import net.minecraft.item.*;
import xaero.map.region.texture.*;
import xaero.map.world.*;
import xaero.map.region.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.settings.*;
import xaero.lib.common.reflection.util.*;
import xaero.map.radar.tracker.*;
import xaero.map.gui.dropdown.rightclick.*;
import xaero.map.teleport.*;
import xaero.lib.client.gui.widget.dropdown.*;

public class GuiMap extends ScreenBase implements IRightClickableElement
{
    public static final VertexFormat POSITION_TEX_TEX_TEX;
    public static final VertexFormatElement TEX_2F_1;
    public static final VertexFormatElement TEX_2F_2;
    public static final VertexFormatElement TEX_2F_3;
    public static final Field KEY_BINDING_PRESSED_FIELD;
    private static final ITextComponent FULL_RELOAD_IN_PROGRESS;
    private static final ITextComponent UNKNOWN_DIMENSION_TYPE1;
    private static final ITextComponent UNKNOWN_DIMENSION_TYPE2;
    private static final double ZOOM_STEP = 1.2;
    private static final int white = -1;
    private static final int blackTrans = 855638016;
    private static final int whiteTrans = 687865855;
    private static final int redTrans = 687800320;
    private static final int yellowTrans = 687865600;
    private static final int purpleTrans = 687800575;
    private static final int greenTrans = 671153920;
    private static final int black = -16777216;
    private static int lastAmountOfRegionsViewed;
    private long loadingAnimationStart;
    private Entity player;
    private int screenScale;
    private int mouseDownPosX;
    private int mouseDownPosY;
    private double mouseDownCameraX;
    private double mouseDownCameraZ;
    private int mouseCheckPosX;
    private int mouseCheckPosY;
    private long mouseCheckTimeNano;
    private int prevMouseCheckPosX;
    private int prevMouseCheckPosY;
    private long prevMouseCheckTimeNano;
    private double cameraX;
    private double cameraZ;
    private boolean shouldResetCameraPos;
    private int[] cameraDestination;
    private SlowingAnimation cameraDestinationAnimX;
    private SlowingAnimation cameraDestinationAnimZ;
    private double scale;
    private double userScale;
    private static double destScale;
    private boolean pauseZoomKeys;
    private int lastZoomMethod;
    private double prevPlayerDimDiv;
    private HoveredMapElementHolder<?, ?> viewed;
    private boolean viewedInList;
    private HoveredMapElementHolder<?, ?> viewedOnMousePress;
    private boolean overWaypointsMenu;
    private Animation zoomAnim;
    public boolean waypointMenu;
    private boolean overPlayersMenu;
    public boolean playersMenu;
    private static ImprovedFramebuffer primaryScaleFBO;
    private float[] colourBuffer;
    private ArrayList<MapRegion> regionBuffer;
    private ArrayList<BranchLeveledRegion> branchRegionBuffer;
    private boolean prevWaitingForBranchCache;
    private boolean prevLoadingLeaves;
    private Integer lastNonNullViewedDimensionId;
    private Integer lastViewedDimensionId;
    private String lastViewedMultiworldId;
    private int mouseBlockPosX;
    private int mouseBlockPosY;
    private int mouseBlockPosZ;
    private Integer mouseBlockDim;
    private double mouseBlockCoordinateScale;
    private long lastStartTime;
    private final GuiMapSwitching mapSwitchingGui;
    private MapMouseButtonPress leftMouseButton;
    private MapMouseButtonPress rightMouseButton;
    private MapProcessor mapProcessor;
    private MapDimension futureDimension;
    private ScaledResolution scaledresolution;
    public boolean noUploadingLimits;
    private boolean[] waitingForBranchCache;
    private GuiButton settingsButton;
    private GuiButton exportButton;
    private GuiButton waypointsButton;
    private GuiButton playersButton;
    private GuiButton radarButton;
    private GuiButton zoomInButton;
    private GuiButton zoomOutButton;
    private GuiButton keybindingsButton;
    private GuiButton caveModeButton;
    private GuiButton dimensionToggleButton;
    private GuiButton buttonPressed;
    private GuiRightClickMenu rightClickMenu;
    private int rightClickX;
    private int rightClickY;
    private int rightClickZ;
    private Integer rightClickDim;
    private double rightClickCoordinateScale;
    private GuiTextField focusedField;
    private boolean lastFrameRenderedRootTextures;
    private MapTileSelection mapTileSelection;
    private GuiCaveModeOptions caveModeOptions;
    
    public GuiMap(final GuiScreen parent, final GuiScreen escape, final MapProcessor mapProcessor, final Entity player) {
        super(parent, escape, (ITextComponent)new TextComponentTranslation("gui.xaero_world_map_screen", new Object[0]));
        this.screenScale = 0;
        this.mouseDownPosX = -1;
        this.mouseDownPosY = -1;
        this.mouseDownCameraX = -1.0;
        this.mouseDownCameraZ = -1.0;
        this.mouseCheckPosX = -1;
        this.mouseCheckPosY = -1;
        this.mouseCheckTimeNano = -1L;
        this.prevMouseCheckPosX = -1;
        this.prevMouseCheckPosY = -1;
        this.prevMouseCheckTimeNano = -1L;
        this.cameraX = 0.0;
        this.cameraZ = 0.0;
        this.cameraDestination = null;
        this.cameraDestinationAnimX = null;
        this.cameraDestinationAnimZ = null;
        this.viewed = null;
        this.viewedOnMousePress = null;
        this.waypointMenu = false;
        this.playersMenu = false;
        this.colourBuffer = new float[4];
        this.regionBuffer = new ArrayList<MapRegion>();
        this.branchRegionBuffer = new ArrayList<BranchLeveledRegion>();
        this.prevWaitingForBranchCache = true;
        this.prevLoadingLeaves = true;
        this.mouseBlockCoordinateScale = 1.0;
        this.waitingForBranchCache = new boolean[1];
        this.player = player;
        this.shouldResetCameraPos = true;
        this.leftMouseButton = new MapMouseButtonPress();
        this.rightMouseButton = new MapMouseButtonPress();
        this.mapSwitchingGui = new GuiMapSwitching(mapProcessor);
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        final boolean openingAnimationConfig = (boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.OPENING_ANIMATION);
        this.userScale = GuiMap.destScale * (openingAnimationConfig ? 1.5f : 1.0f);
        this.zoomAnim = (Animation)new SlowingAnimation(this.userScale, GuiMap.destScale, 0.88, GuiMap.destScale * 0.001);
        this.mapProcessor = mapProcessor;
        this.caveModeOptions = new GuiCaveModeOptions();
        if (SupportMods.minimap()) {
            SupportMods.xaeroMinimap.onMapConstruct();
        }
    }
    
    private double getScaleMultiplier(final int screenShortSide) {
        return (screenShortSide <= 1080) ? 1.0 : (screenShortSide / 1080.0);
    }
    
    public void addGuiButton(final GuiButton b) {
        super.func_189646_b(b);
    }
    
    public void func_73866_w_() {
        super.func_73866_w_();
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        final MapWorld mapWorld = this.mapProcessor.getMapWorld();
        this.futureDimension = ((mapWorld == null) ? null : mapWorld.getFutureDimension());
        final boolean waypointsEnabled = (boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.WAYPOINTS);
        this.waypointMenu = (this.waypointMenu && waypointsEnabled);
        this.mapSwitchingGui.init(this, this.field_146297_k, this.field_146294_l, this.field_146295_m);
        final boolean effectiveCaveModeAllowed = WorldMapClientConfigUtils.getEffectiveCaveModeAllowed();
        final Tooltip caveModeButtonTooltip = new Tooltip((ITextComponent)new TextComponentTranslation(effectiveCaveModeAllowed ? "gui.xaero_box_cave_mode" : "gui.xaero_box_cave_mode_not_allowed", new Object[0]));
        this.caveModeButton = (GuiButton)new GuiTexturedButton(0, this.field_146295_m - 40, 20, 20, 229, 64, 16, 16, WorldMap.guiTextures, new Consumer<GuiButton>() {
            @Override
            public void accept(final GuiButton b) {
                GuiMap.this.onCaveModeButton(b);
            }
        }, new Supplier<Tooltip>() {
            @Override
            public Tooltip get() {
                return caveModeButtonTooltip;
            }
        });
        this.caveModeButton.field_146124_l = effectiveCaveModeAllowed;
        this.func_189646_b(this.caveModeButton);
        this.caveModeOptions.onInit(this, this.mapProcessor);
        final Tooltip dimensionToggleButtonTooltip = new Tooltip((ITextComponent)new TextComponentTranslation("gui.xaero_dimension_toggle_button", new Object[] { KeyMappingUtils.getKeyName(ControlsRegister.keyToggleDimension) }));
        this.func_189646_b(this.dimensionToggleButton = (GuiButton)new GuiTexturedButton(0, this.field_146295_m - 60, 20, 20, 197, 80, 16, 16, WorldMap.guiTextures, new Consumer<GuiButton>() {
            @Override
            public void accept(final GuiButton b) {
                GuiMap.this.onDimensionToggleButton(b);
            }
        }, new Supplier<Tooltip>() {
            @Override
            public Tooltip get() {
                return dimensionToggleButtonTooltip;
            }
        }));
        this.loadingAnimationStart = System.currentTimeMillis();
        this.scaledresolution = new ScaledResolution(Minecraft.func_71410_x());
        this.screenScale = this.scaledresolution.func_78325_e();
        this.pauseZoomKeys = false;
        final Tooltip openSettingsTooltip = new Tooltip((ITextComponent)new TextComponentTranslation("gui.xaero_box_open_settings", new Object[] { KeyMappingUtils.getKeyName(ControlsRegister.keyOpenSettings) }));
        this.func_189646_b(this.settingsButton = (GuiButton)new GuiTexturedButton(0, 0, 30, 30, 113, 0, 20, 20, WorldMap.guiTextures, new Consumer<GuiButton>() {
            @Override
            public void accept(final GuiButton b) {
                GuiMap.this.onSettingsButton(b);
            }
        }, new Supplier<Tooltip>() {
            @Override
            public Tooltip get() {
                return openSettingsTooltip;
            }
        }));
        Tooltip waypointsTooltip;
        if (waypointsEnabled) {
            waypointsTooltip = new Tooltip(this.waypointMenu ? "gui.xaero_box_close_waypoints" : "gui.xaero_box_open_waypoints");
        }
        else {
            waypointsTooltip = new Tooltip(SupportMods.minimap() ? "gui.xaero_box_waypoints_disabled" : "gui.xaero_box_waypoints_minimap_required");
        }
        final Tooltip playersTooltip = new Tooltip(this.playersMenu ? "gui.xaero_box_close_players" : "gui.xaero_box_open_players");
        this.func_189646_b(this.waypointsButton = (GuiButton)new GuiTexturedButton(this.field_146294_l - 20, this.field_146295_m - 20, 20, 20, 213, 0, 16, 16, WorldMap.guiTextures, new Consumer<GuiButton>() {
            @Override
            public void accept(final GuiButton b) {
                GuiMap.this.onWaypointsButton(b);
            }
        }, new Supplier<Tooltip>() {
            @Override
            public Tooltip get() {
                return waypointsTooltip;
            }
        }));
        this.waypointsButton.field_146124_l = waypointsEnabled;
        this.func_189646_b(this.playersButton = (GuiButton)new GuiTexturedButton(this.field_146294_l - 20, this.field_146295_m - 40, 20, 20, 197, 32, 16, 16, WorldMap.guiTextures, new Consumer<GuiButton>() {
            @Override
            public void accept(final GuiButton b) {
                GuiMap.this.onPlayersButton(b);
            }
        }, new Supplier<Tooltip>() {
            @Override
            public Tooltip get() {
                return playersTooltip;
            }
        }));
        final boolean minimapRadarConfig = (boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.MINIMAP_RADAR);
        final Tooltip radarButtonTooltip = new Tooltip((ITextComponent)new TextComponentTranslation(minimapRadarConfig ? "gui.xaero_box_minimap_radar" : "gui.xaero_box_no_minimap_radar", new Object[] { new TextComponentString(KeyMappingUtils.getKeyName(SupportMods.minimap() ? SupportMods.xaeroMinimap.getToggleRadarKey() : null)).func_150255_a(new Style().func_150238_a(TextFormatting.DARK_GREEN)) }));
        this.func_189646_b(this.radarButton = (GuiButton)new GuiTexturedButton(this.field_146294_l - 20, this.field_146295_m - 60, 20, 20, minimapRadarConfig ? 213 : 229, 32, 16, 16, WorldMap.guiTextures, new Consumer<GuiButton>() {
            @Override
            public void accept(final GuiButton b) {
                GuiMap.this.onRadarButton(b);
            }
        }, new Supplier<Tooltip>() {
            @Override
            public Tooltip get() {
                return radarButtonTooltip;
            }
        }));
        this.getRadarButton().field_146124_l = SupportMods.minimap();
        final Tooltip exportButtonTooltip = new Tooltip("gui.xaero_box_export");
        this.func_189646_b(this.exportButton = (GuiButton)new GuiTexturedButton(this.field_146294_l - 20, this.field_146295_m - 80, 20, 20, 133, 0, 16, 16, WorldMap.guiTextures, new Consumer<GuiButton>() {
            @Override
            public void accept(final GuiButton b) {
                GuiMap.this.onExportButton(b);
            }
        }, new Supplier<Tooltip>() {
            @Override
            public Tooltip get() {
                return exportButtonTooltip;
            }
        }));
        final Tooltip controlsButtonTooltip = new Tooltip((ITextComponent)new TextComponentTranslation("gui.xaero_box_controls", new Object[] { SupportMods.minimap() ? SupportMods.xaeroMinimap.getControlsTooltip() : "" }));
        this.func_189646_b(this.keybindingsButton = (GuiButton)new GuiTexturedButton(this.field_146294_l - 20, this.field_146295_m - 100, 20, 20, 197, 0, 16, 16, WorldMap.guiTextures, new Consumer<GuiButton>() {
            @Override
            public void accept(final GuiButton b) {
                GuiMap.this.onKeybindingsButton(b);
            }
        }, new Supplier<Tooltip>() {
            @Override
            public Tooltip get() {
                return controlsButtonTooltip;
            }
        }));
        final Tooltip zoomInButtonTooltip = new Tooltip((ITextComponent)new TextComponentTranslation("gui.xaero_box_zoom_in", new Object[] { new TextComponentString(KeyMappingUtils.getKeyName(ControlsRegister.keyZoomIn)).func_150255_a(new Style().func_150238_a(TextFormatting.DARK_GREEN)) }));
        this.zoomInButton = (GuiButton)new GuiTexturedButton(this.field_146294_l - 20, this.field_146295_m - 140, 20, 20, 165, 0, 16, 16, WorldMap.guiTextures, new Consumer<GuiButton>() {
            @Override
            public void accept(final GuiButton b) {
                GuiMap.this.onZoomInButton(b);
            }
        }, new Supplier<Tooltip>() {
            @Override
            public Tooltip get() {
                return zoomInButtonTooltip;
            }
        });
        final Tooltip zoomOutButtonTooltip = new Tooltip((ITextComponent)new TextComponentTranslation("gui.xaero_box_zoom_out", new Object[] { new TextComponentString(KeyMappingUtils.getKeyName(ControlsRegister.keyZoomOut)).func_150255_a(new Style().func_150238_a(TextFormatting.DARK_GREEN)) }));
        this.zoomOutButton = (GuiButton)new GuiTexturedButton(this.field_146294_l - 20, this.field_146295_m - 120, 20, 20, 181, 0, 16, 16, WorldMap.guiTextures, new Consumer<GuiButton>() {
            @Override
            public void accept(final GuiButton b) {
                GuiMap.this.onZoomOutButton(b);
            }
        }, new Supplier<Tooltip>() {
            @Override
            public Tooltip get() {
                return zoomOutButtonTooltip;
            }
        });
        if (configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.ZOOM_BUTTONS)) {
            this.func_189646_b(this.zoomOutButton);
            this.func_189646_b(this.zoomInButton);
        }
        if (SupportMods.minimap()) {
            SupportMods.xaeroMinimap.requestWaypointsRefresh();
            if (this.waypointMenu) {
                SupportMods.xaeroMinimap.onMapInit(this, this.field_146297_k, this.field_146294_l, this.field_146295_m);
            }
        }
        if (this.playersMenu) {
            WorldMap.trackedPlayerMenuRenderer.onMapInit(this, this.field_146297_k, this.field_146294_l, this.field_146295_m);
        }
        if (this.rightClickMenu != null) {
            this.rightClickMenu.setClosed(true);
            this.rightClickMenu = null;
        }
        Keyboard.enableRepeatEvents(true);
    }
    
    private void onCaveModeButton(final GuiButton b) {
        this.caveModeOptions.toggle(this);
    }
    
    private void onDimensionToggleButton(final GuiButton b) {
        this.mapProcessor.getMapWorld().toggleDimension(!func_146272_n());
        final String messageType = (this.mapProcessor.getMapWorld().getCustomDimensionId() == null) ? "gui.xaero_switched_to_current_dimension" : "gui.xaero_switched_to_dimension";
        this.mapProcessor.getMessageBox().addMessage((ITextComponent)new TextComponentTranslation(messageType, new Object[] { this.mapProcessor.getMapWorld().getFutureDimension().getDropdownLabel() }));
        this.func_146280_a(this.field_146297_k, this.field_146294_l, this.field_146295_m);
    }
    
    private void onSettingsButton(final GuiButton b) {
        this.field_146297_k.func_147108_a((GuiScreen)new GuiWorldMapSettings((GuiScreen)this, (GuiScreen)this, BuiltInEditConfigScreenContexts.CLIENT));
    }
    
    private void onKeybindingsButton(final GuiButton b) {
        this.field_146297_k.func_147108_a((GuiScreen)new GuiControls((GuiScreen)this, this.field_146297_k.field_71474_y));
    }
    
    private void onExportButton(final GuiButton b) {
        this.field_146297_k.func_147108_a((GuiScreen)new ExportScreen((GuiScreen)this, (GuiScreen)this, this.mapProcessor, this.mapTileSelection));
    }
    
    private void toggleWaypointMenu() {
        if (this.playersMenu) {
            this.togglePlayerMenu();
        }
        if (!(this.waypointMenu = !this.waypointMenu)) {
            SupportMods.xaeroMinimap.getWaypointMenuRenderer().onMenuClosed();
            this.unfocusAll();
        }
    }
    
    private void togglePlayerMenu() {
        if (this.waypointMenu) {
            this.toggleWaypointMenu();
        }
        if (!(this.playersMenu = !this.playersMenu)) {
            WorldMap.trackedPlayerMenuRenderer.onMenuClosed();
            this.unfocusAll();
        }
    }
    
    private void onPlayersButton(final GuiButton b) {
        this.togglePlayerMenu();
        this.func_146280_a(this.field_146297_k, this.field_146294_l, this.field_146295_m);
    }
    
    private void onWaypointsButton(final GuiButton b) {
        this.toggleWaypointMenu();
        this.func_146280_a(this.field_146297_k, this.field_146294_l, this.field_146295_m);
    }
    
    public void onRadarButton(final GuiButton b) {
        WorldMapClientConfigUtils.tryTogglingCurrentProfileOption((ConfigOption)WorldMapProfiledConfigOptions.MINIMAP_RADAR);
        this.func_146280_a(this.field_146297_k, this.field_146294_l, this.field_146295_m);
    }
    
    private void onZoomInButton(final GuiButton b) {
        this.buttonPressed = b;
    }
    
    private void onZoomOutButton(final GuiButton b) {
        this.buttonPressed = b;
    }
    
    public void func_73864_a(final int par1, final int par2, final int par3) throws IOException {
        super.func_73864_a(par1, par2, par3);
        if (this.handledMouseInput) {
            return;
        }
        boolean result = this.waypointMenu && SupportMods.xaeroMinimap.getWaypointMenuRenderer().getFilterField().func_146192_a(par1, par2, par3);
        if (!result) {
            result = (this.playersMenu && WorldMap.trackedPlayerMenuRenderer.getFilterField().func_146192_a(par1, par2, par3));
        }
        if (!result) {
            result = (this.caveModeOptions.isEnabled() && this.caveModeOptions.getCaveModeStartField().func_146192_a(par1, par2, par3));
        }
        if (!result) {
            if (par3 == 0) {
                final MapMouseButtonPress leftMouseButton = this.leftMouseButton;
                final MapMouseButtonPress leftMouseButton2 = this.leftMouseButton;
                final boolean b = true;
                leftMouseButton2.clicked = b;
                leftMouseButton.isDown = b;
                this.leftMouseButton.pressedAtX = (int)Misc.getMouseX(this.field_146297_k);
                this.leftMouseButton.pressedAtY = (int)Misc.getMouseY(this.field_146297_k);
            }
            else if (par3 == 1) {
                final MapMouseButtonPress rightMouseButton = this.rightMouseButton;
                final MapMouseButtonPress rightMouseButton2 = this.rightMouseButton;
                final boolean b2 = true;
                rightMouseButton2.clicked = b2;
                rightMouseButton.isDown = b2;
                this.rightMouseButton.pressedAtX = (int)Misc.getMouseX(this.field_146297_k);
                this.rightMouseButton.pressedAtY = (int)Misc.getMouseY(this.field_146297_k);
                this.viewedOnMousePress = this.viewed;
                this.rightClickX = this.mouseBlockPosX;
                this.rightClickY = this.mouseBlockPosY;
                this.rightClickZ = this.mouseBlockPosZ;
                this.rightClickDim = this.mouseBlockDim;
                this.rightClickCoordinateScale = this.mouseBlockCoordinateScale;
                if (SupportMods.minimap()) {
                    SupportMods.xaeroMinimap.onRightClick();
                }
                if (this.viewedOnMousePress == null || !this.viewedOnMousePress.isRightClickValid()) {
                    this.mapTileSelection = new MapTileSelection(this.rightClickX >> 4, this.rightClickZ >> 4);
                }
            }
            else {
                result = this.onInputPress(true, par3);
            }
            if (!result && this.caveModeOptions.isEnabled()) {
                this.caveModeOptions.toggle(this);
                result = true;
            }
        }
        if (this.waypointMenu && SupportMods.xaeroMinimap.getWaypointMenuRenderer().getFilterField().func_146206_l()) {
            this.setFocused(SupportMods.xaeroMinimap.getWaypointMenuRenderer().getFilterField());
        }
        else if (this.playersMenu && WorldMap.trackedPlayerMenuRenderer.getFilterField().func_146206_l()) {
            this.setFocused(WorldMap.trackedPlayerMenuRenderer.getFilterField());
        }
        else if (this.caveModeOptions.isEnabled() && this.caveModeOptions.getCaveModeStartField().func_146206_l()) {
            this.setFocused(this.caveModeOptions.getCaveModeStartField());
        }
        else {
            this.setFocused(null);
        }
    }
    
    public void func_146286_b(final int par1, final int par2, final int par3) {
        this.buttonPressed = null;
        final int mouseX = (int)Misc.getMouseX(this.field_146297_k);
        final int mouseY = (int)Misc.getMouseY(this.field_146297_k);
        if (this.leftMouseButton.isDown && par3 == 0) {
            this.leftMouseButton.isDown = false;
            if (Math.abs(this.leftMouseButton.pressedAtX - mouseX) < 5 && Math.abs(this.leftMouseButton.pressedAtY - mouseY) < 5) {
                this.mapClicked(0, this.leftMouseButton.pressedAtX, this.leftMouseButton.pressedAtY);
            }
            this.leftMouseButton.pressedAtX = -1;
            this.leftMouseButton.pressedAtY = -1;
        }
        if (this.rightMouseButton.isDown && par3 == 1) {
            this.rightMouseButton.isDown = false;
            this.mapClicked(1, mouseX, mouseY);
            this.rightMouseButton.pressedAtX = -1;
            this.rightMouseButton.pressedAtY = -1;
        }
        if (this.waypointMenu) {
            SupportMods.xaeroMinimap.onMapMouseRelease(par1, par2, par3);
        }
        if (this.playersMenu) {
            WorldMap.trackedPlayerMenuRenderer.onMapMouseRelease((double)par1, (double)par2, par3);
        }
        super.func_146286_b(par1, par2, par3);
        this.onInputRelease(true, par3);
    }
    
    public void func_146274_d() throws IOException {
        super.func_146274_d();
        if (this.handledMouseInput) {
            return;
        }
        final int wheel = Mouse.getEventDWheel() / 120;
        if (wheel != 0) {
            final int direction = (wheel > 0) ? 1 : -1;
            if (this.overWaypointsMenu) {
                SupportMods.xaeroMinimap.getWaypointMenuRenderer().mouseScrolled(direction);
            }
            else if (this.playersMenu && this.overPlayersMenu) {
                WorldMap.trackedPlayerMenuRenderer.mouseScrolled(direction);
            }
            else {
                this.changeZoom(wheel, 0);
            }
        }
    }
    
    private void changeZoom(final double factor, final int zoomMethod) {
        this.closeDropdowns();
        this.lastZoomMethod = zoomMethod;
        this.cameraDestinationAnimX = null;
        this.cameraDestinationAnimZ = null;
        if (func_146271_m()) {
            final double destScaleBefore = GuiMap.destScale;
            if (GuiMap.destScale >= 1.0) {
                if (factor > 0.0) {
                    GuiMap.destScale = Math.ceil(GuiMap.destScale);
                }
                else {
                    GuiMap.destScale = Math.floor(GuiMap.destScale);
                }
                if (destScaleBefore == GuiMap.destScale) {
                    GuiMap.destScale += ((factor > 0.0) ? 1.0 : -1.0);
                }
                if (GuiMap.destScale == 0.0) {
                    GuiMap.destScale = 0.5;
                }
            }
            else {
                final double reversedScale = 1.0 / GuiMap.destScale;
                double log2 = Math.log(reversedScale) / Math.log(2.0);
                if (factor > 0.0) {
                    log2 = Math.floor(log2);
                }
                else {
                    log2 = Math.ceil(log2);
                }
                GuiMap.destScale = 1.0 / Math.pow(2.0, log2);
                if (destScaleBefore == GuiMap.destScale) {
                    GuiMap.destScale = 1.0 / Math.pow(2.0, log2 + ((factor > 0.0) ? -1 : 1));
                }
            }
        }
        else {
            GuiMap.destScale *= Math.pow(1.2, factor);
        }
        if (GuiMap.destScale < 0.0625) {
            GuiMap.destScale = 0.0625;
        }
        else if (GuiMap.destScale > 50.0) {
            GuiMap.destScale = 50.0;
        }
    }
    
    public void func_146281_b() {
        super.func_146281_b();
        this.leftMouseButton.isDown = false;
        this.rightMouseButton.isDown = false;
    }
    
    public void func_73863_a(final int scaledMouseX, final int scaledMouseY, final float partialTicks) {
        OpenGlHelper.func_176072_g(OpenGlHelper.field_176089_P, 0);
        while (GL11.glGetError() != 0) {}
        GlStateManager.func_179082_a(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        final Minecraft mc = Minecraft.func_71410_x();
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        final SingleConfigManager<Config> primaryConfigManager = (SingleConfigManager<Config>)WorldMap.INSTANCE.getConfigs().getPrimaryClientConfigManager();
        final long startTime = System.currentTimeMillis();
        final MapDimension currentFutureDim = this.mapProcessor.isMapWorldUsable() ? this.mapProcessor.getMapWorld().getFutureDimension() : null;
        if (currentFutureDim != this.futureDimension) {
            this.func_146280_a(mc, this.field_146294_l, this.field_146295_m);
        }
        double playerDimDiv = this.prevPlayerDimDiv;
        synchronized (this.mapProcessor.renderThreadPauseSync) {
            if (!this.mapProcessor.isRenderingPaused()) {
                final MapDimension mapDim = this.mapProcessor.getMapWorld().getCurrentDimension();
                if (mapDim != null) {
                    playerDimDiv = mapDim.calculateDimDiv(this.player.field_70170_p.field_73011_w);
                }
            }
        }
        final double scaledPlayerX = this.player.field_70165_t / playerDimDiv;
        final double scaledPlayerZ = this.player.field_70161_v / playerDimDiv;
        if (this.shouldResetCameraPos) {
            this.cameraX = (float)scaledPlayerX;
            this.cameraZ = (float)scaledPlayerZ;
            this.shouldResetCameraPos = false;
        }
        else if (this.prevPlayerDimDiv != 0.0 && playerDimDiv != this.prevPlayerDimDiv) {
            final double oldScaledPlayerX = this.player.field_70165_t / this.prevPlayerDimDiv;
            final double oldScaledPlayerZ = this.player.field_70161_v / this.prevPlayerDimDiv;
            this.cameraX = this.cameraX - oldScaledPlayerX + scaledPlayerX;
            this.cameraZ = this.cameraZ - oldScaledPlayerZ + scaledPlayerZ;
            this.cameraDestinationAnimX = null;
            this.cameraDestinationAnimZ = null;
            this.cameraDestination = null;
        }
        this.prevPlayerDimDiv = playerDimDiv;
        final double cameraXBefore = this.cameraX;
        final double cameraZBefore = this.cameraZ;
        final double scaleBefore = this.scale;
        this.mapSwitchingGui.preMapRender(this, mc, this.field_146294_l, this.field_146295_m);
        final long passed = (this.lastStartTime == 0L) ? 16L : (startTime - this.lastStartTime);
        final double passedScrolls = passed / 64.0f;
        final int direction = (this.buttonPressed == this.zoomInButton || KeyMappingUtils.isPhysicallyDown(ControlsRegister.keyZoomIn)) ? 1 : ((this.buttonPressed == this.zoomOutButton || KeyMappingUtils.isPhysicallyDown(ControlsRegister.keyZoomOut)) ? -1 : 0);
        if (direction != 0) {
            final boolean ctrlKey = func_146271_m();
            if (!ctrlKey || !this.pauseZoomKeys) {
                this.changeZoom(direction * passedScrolls, (this.buttonPressed == this.zoomInButton || this.buttonPressed == this.zoomOutButton) ? 2 : 1);
                if (ctrlKey) {
                    this.pauseZoomKeys = true;
                }
            }
        }
        else {
            this.pauseZoomKeys = false;
        }
        this.lastStartTime = startTime;
        if (this.cameraDestination != null) {
            this.cameraDestinationAnimX = new SlowingAnimation(this.cameraX, (double)this.cameraDestination[0], 0.9, 0.01);
            this.cameraDestinationAnimZ = new SlowingAnimation(this.cameraZ, (double)this.cameraDestination[1], 0.9, 0.01);
            this.cameraDestination = null;
        }
        if (this.cameraDestinationAnimX != null) {
            this.cameraX = this.cameraDestinationAnimX.getCurrent();
            if (this.cameraX == this.cameraDestinationAnimX.getDestination()) {
                this.cameraDestinationAnimX = null;
            }
        }
        if (this.cameraDestinationAnimZ != null) {
            this.cameraZ = this.cameraDestinationAnimZ.getCurrent();
            if (this.cameraZ == this.cameraDestinationAnimZ.getDestination()) {
                this.cameraDestinationAnimZ = null;
            }
        }
        this.lastViewedDimensionId = null;
        this.lastViewedMultiworldId = null;
        this.mouseBlockPosY = -1;
        boolean discoveredForHighlights = false;
        synchronized (this.mapProcessor.renderThreadPauseSync) {
            if (!this.mapProcessor.isRenderingPaused()) {
                final boolean mapLoaded = this.mapProcessor.getCurrentWorldId() != null && !this.mapProcessor.isWaitingForWorldUpdate() && this.mapProcessor.getMapSaveLoad().isRegionDetectionComplete();
                final boolean noWorldMapEffect = mc.field_71439_g == null || Misc.hasEffect((EntityPlayer)mc.field_71439_g, Effects.NO_WORLD_MAP) || Misc.hasEffect((EntityPlayer)mc.field_71439_g, Effects.NO_WORLD_MAP_HARMFUL);
                final Item mapItem = this.mapProcessor.getMapItem();
                final boolean allowedBasedOnItem = mapItem == null || (mc.field_71439_g != null && Misc.hasItem((EntityPlayer)mc.field_71439_g, mapItem));
                final boolean isLocked = this.mapProcessor.isCurrentMapLocked();
                if (mapLoaded && !noWorldMapEffect && allowedBasedOnItem && !isLocked) {
                    if (SupportMods.vivecraft) {
                        GlStateManager.func_179082_a(0.0f, 0.0f, 0.0f, 1.0f);
                        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                        GlStateManager.func_179086_m(16384);
                    }
                    this.mapProcessor.updateCaveStart();
                    final Integer value = this.mapProcessor.getMapWorld().getCurrentDimension().getDimId();
                    this.lastViewedDimensionId = value;
                    this.lastNonNullViewedDimensionId = value;
                    this.lastViewedMultiworldId = this.mapProcessor.getMapWorld().getCurrentDimension().getCurrentMultiworld();
                    if (SupportMods.minimap()) {
                        SupportMods.xaeroMinimap.checkWaypoints(this.mapProcessor.getMapWorld().isMultiplayer(), this.lastViewedDimensionId, this.lastViewedMultiworldId, this.field_146294_l, this.field_146295_m, this, this.mapProcessor.getMapWorld());
                    }
                    final int mouseXPos = (int)Misc.getMouseX(mc);
                    final int mouseYPos = (int)Misc.getMouseY(mc);
                    final double scaleMultiplier = this.getScaleMultiplier(Math.min(mc.field_71443_c, mc.field_71440_d));
                    this.scale = this.userScale * scaleMultiplier;
                    if (this.mouseCheckPosX == -1 || System.nanoTime() - this.mouseCheckTimeNano > 30000000L) {
                        this.prevMouseCheckPosX = this.mouseCheckPosX;
                        this.prevMouseCheckPosY = this.mouseCheckPosY;
                        this.prevMouseCheckTimeNano = this.mouseCheckTimeNano;
                        this.mouseCheckPosX = mouseXPos;
                        this.mouseCheckPosY = mouseYPos;
                        this.mouseCheckTimeNano = System.nanoTime();
                    }
                    if (!this.leftMouseButton.isDown) {
                        if (this.mouseDownPosX != -1) {
                            this.mouseDownPosX = -1;
                            this.mouseDownPosY = -1;
                            if (this.prevMouseCheckTimeNano != -1L) {
                                double downTime = 0.0;
                                int draggedX = 0;
                                int draggedY = 0;
                                downTime = (double)(System.nanoTime() - this.prevMouseCheckTimeNano);
                                draggedX = mouseXPos - this.prevMouseCheckPosX;
                                draggedY = mouseYPos - this.prevMouseCheckPosY;
                                final double frameTime60FPS = 1.6666666666666666E7;
                                final double speedScale = downTime / frameTime60FPS;
                                final double speed_x = -draggedX / this.scale / speedScale;
                                final double speed_z = -draggedY / this.scale / speedScale;
                                double speed = Math.sqrt(speed_x * speed_x + speed_z * speed_z);
                                if (speed > 0.0) {
                                    final double cos = speed_x / speed;
                                    final double sin = speed_z / speed;
                                    final double maxSpeed = 500.0 / this.userScale;
                                    speed = ((Math.abs(speed) > maxSpeed) ? Math.copySign(maxSpeed, speed) : speed);
                                    final double speed_factor = 0.9;
                                    final double ln = Math.log(speed_factor);
                                    final double move_distance = -speed / ln;
                                    final double moveX = cos * move_distance;
                                    final double moveZ = sin * move_distance;
                                    this.cameraDestinationAnimX = new SlowingAnimation(this.cameraX, this.cameraX + moveX, 0.9, 0.01);
                                    this.cameraDestinationAnimZ = new SlowingAnimation(this.cameraZ, this.cameraZ + moveZ, 0.9, 0.01);
                                }
                            }
                        }
                    }
                    else if (this.viewed == null || !this.viewedInList || this.mouseDownPosX != -1) {
                        if (this.mouseDownPosX != -1) {
                            this.cameraX = (this.mouseDownPosX - mouseXPos) / this.scale + this.mouseDownCameraX;
                            this.cameraZ = (this.mouseDownPosY - mouseYPos) / this.scale + this.mouseDownCameraZ;
                        }
                        else {
                            this.mouseDownPosX = mouseXPos;
                            this.mouseDownPosY = mouseYPos;
                            this.mouseDownCameraX = this.cameraX;
                            this.mouseDownCameraZ = this.cameraZ;
                            this.cameraDestinationAnimX = null;
                            this.cameraDestinationAnimZ = null;
                        }
                    }
                    final int mouseFromCentreX = mouseXPos - mc.field_71443_c / 2;
                    final int mouseFromCentreY = mouseYPos - mc.field_71440_d / 2;
                    final double oldMousePosX = mouseFromCentreX / this.scale + this.cameraX;
                    final double oldMousePosZ = mouseFromCentreY / this.scale + this.cameraZ;
                    final double preScale = this.scale;
                    if (GuiMap.destScale != this.userScale) {
                        if (this.zoomAnim != null) {
                            this.userScale = this.zoomAnim.getCurrent();
                            this.scale = this.userScale * scaleMultiplier;
                        }
                        if (this.zoomAnim == null || MathUtils.round(this.zoomAnim.getDestination(), 4) != MathUtils.round(GuiMap.destScale, 4)) {
                            this.zoomAnim = (Animation)new SinAnimation(this.userScale, GuiMap.destScale, 100L);
                        }
                    }
                    if (this.scale > preScale && this.lastZoomMethod != 2) {
                        this.cameraX = oldMousePosX - mouseFromCentreX / this.scale;
                        this.cameraZ = oldMousePosZ - mouseFromCentreY / this.scale;
                    }
                    int textureLevel = 0;
                    double fboScale;
                    if (this.scale >= 1.0) {
                        fboScale = Math.max(1.0, Math.floor(this.scale));
                    }
                    else {
                        fboScale = this.scale;
                    }
                    if (this.userScale < 1.0) {
                        final double reversedScale = 1.0 / this.userScale;
                        final double log2 = Math.floor(Math.log(reversedScale) / Math.log(2.0));
                        textureLevel = Math.min((int)log2, 3);
                    }
                    this.mapProcessor.getMapSaveLoad().mainTextureLevel = textureLevel;
                    final int leveledRegionShift = 9 + textureLevel;
                    final double secondaryScale = this.scale / fboScale;
                    GlStateManager.func_179094_E();
                    final double mousePosX = mouseFromCentreX / this.scale + this.cameraX;
                    final double mousePosZ = mouseFromCentreY / this.scale + this.cameraZ;
                    GlStateManager.func_179094_E();
                    GlStateManager.func_179109_b(0.0f, 0.0f, 971.0f);
                    this.mouseBlockPosX = (int)Math.floor(mousePosX);
                    this.mouseBlockPosZ = (int)Math.floor(mousePosZ);
                    this.mouseBlockDim = this.mapProcessor.getMapWorld().getCurrentDimension().getDimId();
                    this.mouseBlockCoordinateScale = this.getCurrentMapCoordinateScale();
                    if (SupportMods.minimap()) {
                        SupportMods.xaeroMinimap.onBlockHover();
                    }
                    final int mouseRegX = this.mouseBlockPosX >> leveledRegionShift;
                    final int mouseRegZ = this.mouseBlockPosZ >> leveledRegionShift;
                    final int renderedCaveLayer = this.mapProcessor.getCurrentCaveLayer();
                    final LeveledRegion<?> reg = this.mapProcessor.getLeveledRegion(renderedCaveLayer, mouseRegX, mouseRegZ, textureLevel);
                    final int maxRegBlockCoord = (1 << leveledRegionShift) - 1;
                    final int mouseRegPixelX = (this.mouseBlockPosX & maxRegBlockCoord) >> textureLevel;
                    final int mouseRegPixelZ = (this.mouseBlockPosZ & maxRegBlockCoord) >> textureLevel;
                    this.mouseBlockPosX = (mouseRegX << leveledRegionShift) + (mouseRegPixelX << textureLevel);
                    this.mouseBlockPosZ = (mouseRegZ << leveledRegionShift) + (mouseRegPixelZ << textureLevel);
                    if (this.mapTileSelection != null && this.rightClickMenu == null) {
                        this.mapTileSelection.setEnd(this.mouseBlockPosX >> 4, this.mouseBlockPosZ >> 4);
                    }
                    final MapRegion leafRegion = this.mapProcessor.getLeafMapRegion(renderedCaveLayer, this.mouseBlockPosX >> 9, this.mouseBlockPosZ >> 9, false);
                    final MapTileChunk chunk = (leafRegion == null) ? null : leafRegion.getChunk(this.mouseBlockPosX >> 6 & 0x7, this.mouseBlockPosZ >> 6 & 0x7);
                    final int debugTextureX = this.mouseBlockPosX >> leveledRegionShift - 3 & 0x7;
                    final int debugTextureY = this.mouseBlockPosZ >> leveledRegionShift - 3 & 0x7;
                    final RegionTexture tex = (RegionTexture)((reg != null && reg.hasTextures()) ? reg.getTexture(debugTextureX, debugTextureY) : null);
                    final boolean debugConfig = (boolean)primaryConfigManager.getEffective((ConfigOption)WorldMapPrimaryClientConfigOptions.DEBUG);
                    if (debugConfig) {
                        if (reg != null) {
                            final List<String> debugLines = new ArrayList<String>();
                            if (tex != null) {
                                tex.addDebugLines(debugLines);
                                final MapTile mouseTile = (chunk == null) ? null : chunk.getTile(this.mouseBlockPosX >> 4 & 0x3, this.mouseBlockPosZ >> 4 & 0x3);
                                if (mouseTile != null) {
                                    final MapBlock block = mouseTile.getBlock(this.mouseBlockPosX & 0xF, this.mouseBlockPosZ & 0xF);
                                    if (block != null) {
                                        this.func_73732_a(mc.field_71466_p, block.toString(), this.field_146294_l / 2, 22, -1);
                                        if (block.getNumberOfOverlays() != 0) {
                                            for (int i = 0; i < block.getOverlays().size(); ++i) {
                                                this.func_73732_a(mc.field_71466_p, block.getOverlays().get(i).toString(), this.field_146294_l / 2, 32 + i * 10, -1);
                                            }
                                        }
                                    }
                                }
                            }
                            debugLines.add("");
                            debugLines.add(reg.toString());
                            reg.addDebugLines(debugLines, this.mapProcessor, debugTextureX, debugTextureY);
                            for (int j = 0; j < debugLines.size(); ++j) {
                                this.func_73731_b(mc.field_71466_p, (String)debugLines.get(j), 5, 15 + 10 * j, -1);
                            }
                        }
                        final MapDimensionTypeInfo dimType = this.mapProcessor.getMapWorld().getCurrentDimension().getDimensionType();
                        this.func_73731_b(mc.field_71466_p, "MultiWorld ID: " + this.mapProcessor.getMapWorld().getCurrentMultiworld() + " Dim Type: " + ((dimType == null) ? "unknown" : dimType.getName()), 5, 265, -1);
                        final LayeredRegionManager regions = this.mapProcessor.getMapWorld().getCurrentDimension().getLayeredMapRegions();
                        this.func_73731_b(mc.field_71466_p, String.format("regions: %d loaded: %d processed: %d viewed: %d benchmarks %s", regions.size(), regions.loadedCount(), this.mapProcessor.getProcessedCount(), GuiMap.lastAmountOfRegionsViewed, WorldMap.textureUploadBenchmark.getTotalsString()), 5, 275, -1);
                        this.func_73731_b(mc.field_71466_p, String.format("toLoad: %d toSave: %d tile pool: %d overlays: %d toLoadBranchCache: %d buffers: %d", this.mapProcessor.getMapSaveLoad().getSizeOfToLoad(), this.mapProcessor.getMapSaveLoad().getToSave().size(), this.mapProcessor.getTilePool().size(), this.mapProcessor.getOverlayManager().getNumberOfUniqueOverlays(), this.mapProcessor.getMapSaveLoad().getSizeOfToLoadBranchCache(), WorldMap.textureDirectBufferPool.size()), 5, 285, -1);
                        final long k = Runtime.getRuntime().maxMemory();
                        final long l = Runtime.getRuntime().totalMemory();
                        final long m = Runtime.getRuntime().freeMemory();
                        final long l2 = l - m;
                        this.func_73731_b(mc.field_71466_p, String.format("FPS: %d", Minecraft.func_175610_ah()), 5, 295, -1);
                        this.func_73731_b(mc.field_71466_p, String.format("Mem: % 2d%% %03d/%03dMB", l2 * 100L / k, bytesToMb(l2), bytesToMb(k)), 5, 315, -1);
                        this.func_73731_b(mc.field_71466_p, String.format("Allocated: % 2d%% %03dMB", l * 100L / k, bytesToMb(l)), 5, 325, -1);
                        this.func_73731_b(mc.field_71466_p, String.format("Available VRAM: %dMB", this.mapProcessor.getMapLimiter().getAvailableVRAM() / 1024), 5, 335, -1);
                    }
                    final int pixelInsideTexX = mouseRegPixelX & 0x3F;
                    final int pixelInsideTexZ = mouseRegPixelZ & 0x3F;
                    boolean hasAmbiguousHeight = false;
                    int mouseBlockBottomY = -1;
                    int mouseBlockTopY = -1;
                    int pointedAtBiome = -1;
                    if (tex != null) {
                        final int height = tex.getHeight(pixelInsideTexX, pixelInsideTexZ);
                        this.mouseBlockPosY = height;
                        mouseBlockBottomY = height;
                        mouseBlockTopY = tex.getTopHeight(pixelInsideTexX, pixelInsideTexZ);
                        hasAmbiguousHeight = (this.mouseBlockPosY != mouseBlockTopY);
                        pointedAtBiome = tex.getBiome(pixelInsideTexX, pixelInsideTexZ);
                    }
                    if (hasAmbiguousHeight) {
                        if (mouseBlockTopY != -1) {
                            this.mouseBlockPosY = mouseBlockTopY;
                        }
                        else if (configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.DETECT_AMBIGUOUS_Y)) {
                            this.mouseBlockPosY = -1;
                        }
                    }
                    GlStateManager.func_179121_F();
                    if (GuiMap.primaryScaleFBO == null || GuiMap.primaryScaleFBO.field_147621_c != mc.field_71443_c || GuiMap.primaryScaleFBO.field_147618_d != mc.field_71440_d) {
                        if (!Minecraft.func_71410_x().field_71474_y.field_151448_g) {
                            Minecraft.func_71410_x().field_71474_y.func_74306_a(GameSettings.Options.FBO_ENABLE, 0);
                            WorldMap.LOGGER.info("FBO is off. Turning it on.");
                        }
                        GuiMap.primaryScaleFBO = new ImprovedFramebuffer(mc.field_71443_c, mc.field_71440_d, false);
                    }
                    if (GuiMap.primaryScaleFBO.field_147616_f == -1) {
                        GlStateManager.func_179121_F();
                        return;
                    }
                    GuiMap.primaryScaleFBO.func_147610_a(false);
                    GlStateManager.func_179082_a(0.0f, 0.0f, 0.0f, 1.0f);
                    GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                    GlStateManager.func_179086_m(16384);
                    GlStateManager.func_179131_c(1.0f, 1.0f, 1.0f, 1.0f);
                    GlStateManager.func_179152_a(1.0f / this.screenScale, 1.0f / this.screenScale, 1.0f);
                    GlStateManager.func_179109_b((float)(mc.field_71443_c / 2), (float)(mc.field_71440_d / 2), 0.0f);
                    GlStateManager.func_179094_E();
                    GlStateManager.func_179129_p();
                    int flooredCameraX = (int)Math.floor(this.cameraX);
                    int flooredCameraZ = (int)Math.floor(this.cameraZ);
                    double primaryOffsetX = 0.0;
                    double primaryOffsetY = 0.0;
                    double secondaryOffsetX;
                    double secondaryOffsetY;
                    if (fboScale < 1.0) {
                        final double pixelInBlocks = 1.0 / fboScale;
                        final int xInFullPixels = (int)Math.floor(this.cameraX / pixelInBlocks);
                        final int zInFullPixels = (int)Math.floor(this.cameraZ / pixelInBlocks);
                        final double fboOffsetX = xInFullPixels * pixelInBlocks;
                        final double fboOffsetZ = zInFullPixels * pixelInBlocks;
                        flooredCameraX = (int)Math.floor(fboOffsetX);
                        flooredCameraZ = (int)Math.floor(fboOffsetZ);
                        primaryOffsetX = fboOffsetX - flooredCameraX;
                        primaryOffsetY = fboOffsetZ - flooredCameraZ;
                        secondaryOffsetX = (this.cameraX - fboOffsetX) * fboScale;
                        secondaryOffsetY = (this.cameraZ - fboOffsetZ) * fboScale;
                    }
                    else {
                        secondaryOffsetX = (this.cameraX - flooredCameraX) * fboScale;
                        secondaryOffsetY = (this.cameraZ - flooredCameraZ) * fboScale;
                        if (secondaryOffsetX >= 1.0) {
                            final int offset = (int)secondaryOffsetX;
                            GlStateManager.func_179109_b((float)(-offset), 0.0f, 0.0f);
                            secondaryOffsetX -= offset;
                        }
                        if (secondaryOffsetY >= 1.0) {
                            final int offset = (int)secondaryOffsetY;
                            GlStateManager.func_179109_b(0.0f, (float)offset, 0.0f);
                            secondaryOffsetY -= offset;
                        }
                    }
                    GlStateManager.func_179139_a(fboScale, -fboScale, 1.0);
                    GlStateManager.func_179137_b(-primaryOffsetX, -primaryOffsetY, 0.0);
                    GlStateManager.func_179098_w();
                    final double leftBorder = this.cameraX - mc.field_71443_c / 2 / this.scale;
                    final double rightBorder = leftBorder + mc.field_71443_c / this.scale;
                    final double topBorder = this.cameraZ - mc.field_71440_d / 2 / this.scale;
                    final double bottomBorder = topBorder + mc.field_71440_d / this.scale;
                    final int minRegX = (int)Math.floor(leftBorder) >> leveledRegionShift;
                    final int maxRegX = (int)Math.floor(rightBorder) >> leveledRegionShift;
                    final int minRegZ = (int)Math.floor(topBorder) >> leveledRegionShift;
                    final int maxRegZ = (int)Math.floor(bottomBorder) >> leveledRegionShift;
                    final int blockToTextureConversion = 6 + textureLevel;
                    final int minTextureX = (int)Math.floor(leftBorder) >> blockToTextureConversion;
                    final int maxTextureX = (int)Math.floor(rightBorder) >> blockToTextureConversion;
                    final int minTextureZ = (int)Math.floor(topBorder) >> blockToTextureConversion;
                    final int maxTextureZ = (int)Math.floor(bottomBorder) >> blockToTextureConversion;
                    final int minLeafRegX = minTextureX << blockToTextureConversion >> 9;
                    final int maxLeafRegX = (maxTextureX + 1 << blockToTextureConversion) - 1 >> 9;
                    final int minLeafRegZ = minTextureZ << blockToTextureConversion >> 9;
                    final int maxLeafRegZ = (maxTextureZ + 1 << blockToTextureConversion) - 1 >> 9;
                    GuiMap.lastAmountOfRegionsViewed = (maxRegX - minRegX + 1) * (maxRegZ - minRegZ + 1);
                    if (this.mapProcessor.getMapLimiter().getMostRegionsAtATime() < GuiMap.lastAmountOfRegionsViewed) {
                        this.mapProcessor.getMapLimiter().setMostRegionsAtATime(GuiMap.lastAmountOfRegionsViewed);
                    }
                    GlStateManager.func_179084_k();
                    GlStateManager.func_179118_c();
                    this.regionBuffer.clear();
                    this.branchRegionBuffer.clear();
                    final float brightness = this.mapProcessor.getBrightness();
                    final int globalRegionCacheHashCode = WorldMap.settings.getRegionCacheHashCode();
                    final int globalCaveStart = this.mapProcessor.getMapWorld().getCurrentDimension().getLayeredMapRegions().getLayer(renderedCaveLayer).getCaveStart();
                    final int globalCaveDepth = (int)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.CAVE_MODE_DEPTH);
                    final boolean reloadEverything = (boolean)primaryConfigManager.getEffective((ConfigOption)WorldMapPrimaryClientConfigOptions.RELOAD_VIEWED);
                    final int globalReloadVersion = (int)primaryConfigManager.getEffective(WorldMapPrimaryClientConfigOptions.RELOAD_VIEWED_VERSION);
                    final boolean oldMinimapMessesUpTextureFilter = SupportMods.minimap() && SupportMods.xaeroMinimap.compatibilityVersion < 11;
                    final int globalVersion = (int)primaryConfigManager.getEffective(WorldMapPrimaryClientConfigOptions.GLOBAL_VERSION);
                    final boolean prevWaitingForBranchCache = this.prevWaitingForBranchCache;
                    this.waitingForBranchCache[0] = false;
                    setupTextureMatricesAndTextures(brightness);
                    LeveledRegion.setComparison(this.mouseBlockPosX >> leveledRegionShift, this.mouseBlockPosZ >> leveledRegionShift, textureLevel, this.mouseBlockPosX >> 9, this.mouseBlockPosZ >> 9);
                    LeveledRegion<?> lastUpdatedRootLeveledRegion = null;
                    final boolean cacheOnlyMode = this.mapProcessor.getMapWorld().isCacheOnlyMode();
                    boolean frameRenderedRootTextures = false;
                    boolean loadingLeaves = false;
                    for (int leveledRegX = minRegX; leveledRegX <= maxRegX; ++leveledRegX) {
                        for (int leveledRegZ = minRegZ; leveledRegZ <= maxRegZ; ++leveledRegZ) {
                            final int leveledSideInRegions = 1 << textureLevel;
                            final int leveledSideInBlocks = leveledSideInRegions * 512;
                            final int leafRegionMinX = leveledRegX * leveledSideInRegions;
                            final int leafRegionMinZ = leveledRegZ * leveledSideInRegions;
                            LeveledRegion<?> leveledRegion = null;
                            for (int leafX = 0; leafX < leveledSideInRegions; ++leafX) {
                                for (int leafZ = 0; leafZ < leveledSideInRegions; ++leafZ) {
                                    final int regX = leafRegionMinX + leafX;
                                    if (regX >= minLeafRegX) {
                                        if (regX <= maxLeafRegX) {
                                            final int regZ = leafRegionMinZ + leafZ;
                                            if (regZ >= minLeafRegZ) {
                                                if (regZ <= maxLeafRegZ) {
                                                    MapRegion region = this.mapProcessor.getLeafMapRegion(renderedCaveLayer, regX, regZ, false);
                                                    if (region == null) {
                                                        region = this.mapProcessor.getLeafMapRegion(renderedCaveLayer, regX, regZ, this.mapProcessor.regionExists(renderedCaveLayer, regX, regZ));
                                                    }
                                                    if (region != null) {
                                                        if (leveledRegion == null) {
                                                            leveledRegion = this.mapProcessor.getLeveledRegion(renderedCaveLayer, leveledRegX, leveledRegZ, textureLevel);
                                                        }
                                                        if (!prevWaitingForBranchCache) {
                                                            synchronized (region) {
                                                                if (textureLevel != 0 && region.getLoadState() == 0 && region.loadingNeededForBranchLevel != 0 && region.loadingNeededForBranchLevel != textureLevel) {
                                                                    region.loadingNeededForBranchLevel = 0;
                                                                    region.getParent().setShouldCheckForUpdatesRecursive(true);
                                                                }
                                                                if (region.canRequestReload_unsynced() && ((!cacheOnlyMode && ((reloadEverything && region.getReloadVersion() != globalReloadVersion) || region.getCacheHashCode() != globalRegionCacheHashCode || region.caveStartOutdated(globalCaveStart, globalCaveDepth) || region.getVersion() != globalVersion || (region.getLoadState() != 2 && region.shouldCache()))) || (region.getLoadState() == 0 && (!region.isMetaLoaded() || textureLevel == 0 || region.loadingNeededForBranchLevel == textureLevel)) || ((region.isMetaLoaded() || region.getLoadState() != 0 || !region.hasHadTerrain()) && region.getHighlightsHash() != region.getDim().getHighlightHandler().getRegionHash(region.getRegionX(), region.getRegionZ())))) {
                                                                    loadingLeaves = true;
                                                                    region.calculateSortingDistance();
                                                                    Misc.addToListOfSmallest(10, this.regionBuffer, region);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (leveledRegion != null) {
                                LeveledRegion<?> rootLeveledRegion = leveledRegion.getRootRegion();
                                if (rootLeveledRegion == leveledRegion) {
                                    rootLeveledRegion = null;
                                }
                                if (rootLeveledRegion != null && !rootLeveledRegion.isLoaded()) {
                                    if (!rootLeveledRegion.recacheHasBeenRequested() && !rootLeveledRegion.reloadHasBeenRequested()) {
                                        rootLeveledRegion.calculateSortingDistance();
                                        Misc.addToListOfSmallest(10, this.branchRegionBuffer, (BranchLeveledRegion)rootLeveledRegion);
                                    }
                                    this.waitingForBranchCache[0] = true;
                                    rootLeveledRegion = null;
                                }
                                if (!this.mapProcessor.isUploadingPaused() && !WorldMap.pauseRequests) {
                                    if (leveledRegion instanceof BranchLeveledRegion) {
                                        final BranchLeveledRegion branchRegion = (BranchLeveledRegion)leveledRegion;
                                        branchRegion.checkForUpdates(this.mapProcessor, prevWaitingForBranchCache, this.waitingForBranchCache, this.branchRegionBuffer, textureLevel, minLeafRegX, minLeafRegZ, maxLeafRegX, maxLeafRegZ);
                                    }
                                    if (((textureLevel != 0 && !prevWaitingForBranchCache) || (textureLevel == 0 && !this.prevLoadingLeaves)) && this.lastFrameRenderedRootTextures && rootLeveledRegion != null && rootLeveledRegion != lastUpdatedRootLeveledRegion) {
                                        final BranchLeveledRegion branchRegion = (BranchLeveledRegion)rootLeveledRegion;
                                        branchRegion.checkForUpdates(this.mapProcessor, prevWaitingForBranchCache, this.waitingForBranchCache, this.branchRegionBuffer, textureLevel, minLeafRegX, minLeafRegZ, maxLeafRegX, maxLeafRegZ);
                                        lastUpdatedRootLeveledRegion = rootLeveledRegion;
                                    }
                                    this.mapProcessor.getMapWorld().getCurrentDimension().getLayeredMapRegions().bumpLoadedRegion(leveledRegion);
                                    if (rootLeveledRegion != null) {
                                        this.mapProcessor.getMapWorld().getCurrentDimension().getLayeredMapRegions().bumpLoadedRegion(rootLeveledRegion);
                                    }
                                }
                                else {
                                    this.waitingForBranchCache[0] = prevWaitingForBranchCache;
                                }
                                final int minXBlocks = leveledRegX * leveledSideInBlocks;
                                final int minZBlocks = leveledRegZ * leveledSideInBlocks;
                                final int textureSize = 64 * leveledSideInRegions;
                                final int firstTextureX = leveledRegX << 3;
                                final int firstTextureZ = leveledRegZ << 3;
                                final int levelDiff = 3 - textureLevel;
                                final int rootSize = 1 << levelDiff;
                                final int maxInsideCoord = rootSize - 1;
                                final int firstRootTextureX = firstTextureX >> levelDiff & 0x7;
                                final int firstRootTextureZ = firstTextureZ >> levelDiff & 0x7;
                                final int firstInsideTextureX = firstTextureX & maxInsideCoord;
                                final int firstInsideTextureZ = firstTextureZ & maxInsideCoord;
                                final boolean hasTextures = leveledRegion.hasTextures();
                                final boolean rootHasTextures = rootLeveledRegion != null && rootLeveledRegion.hasTextures();
                                if (hasTextures || rootHasTextures) {
                                    for (int o = 0; o < 8; ++o) {
                                        final int textureX = minXBlocks + o * textureSize;
                                        if (textureX <= rightBorder) {
                                            if (textureX + textureSize >= leftBorder) {
                                                for (int p = 0; p < 8; ++p) {
                                                    final int textureZ = minZBlocks + p * textureSize;
                                                    if (textureZ <= bottomBorder) {
                                                        if (textureZ + textureSize >= topBorder) {
                                                            RegionTexture<?> regionTexture = (RegionTexture<?>)(hasTextures ? leveledRegion.getTexture(o, p) : null);
                                                            if (regionTexture == null || regionTexture.getGlColorTexture() == -1) {
                                                                if (rootHasTextures) {
                                                                    final int insideX = firstInsideTextureX + o;
                                                                    final int insideZ = firstInsideTextureZ + p;
                                                                    final int rootTextureX = firstRootTextureX + (insideX >> levelDiff);
                                                                    final int rootTextureZ = firstRootTextureZ + (insideZ >> levelDiff);
                                                                    regionTexture = (RegionTexture<?>)rootLeveledRegion.getTexture(rootTextureX, rootTextureZ);
                                                                    if (regionTexture != null) {
                                                                        synchronized (regionTexture) {
                                                                            if (regionTexture.getGlColorTexture() != -1) {
                                                                                frameRenderedRootTextures = true;
                                                                                final int insideTextureX = insideX & maxInsideCoord;
                                                                                final int insideTextureZ = insideZ & maxInsideCoord;
                                                                                final float textureX2 = insideTextureX / (float)rootSize;
                                                                                final float textureX3 = (insideTextureX + 1) / (float)rootSize;
                                                                                final float textureY1 = insideTextureZ / (float)rootSize;
                                                                                final float textureY2 = (insideTextureZ + 1) / (float)rootSize;
                                                                                final boolean hasLight = regionTexture.getTextureHasLight();
                                                                                bindMapTextureWithLighting3(regionTexture, 9728, oldMinimapMessesUpTextureFilter, 0, hasLight);
                                                                                renderTexturedModalSubRectWithLighting((float)(textureX - flooredCameraX), (float)(textureZ - flooredCameraZ), textureX2, textureY1, textureX3, textureY2, (float)textureSize, (float)textureSize, hasLight);
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                            else {
                                                                synchronized (regionTexture) {
                                                                    if (regionTexture.getGlColorTexture() != -1) {
                                                                        final boolean hasLight2 = regionTexture.getTextureHasLight();
                                                                        bindMapTextureWithLighting3(regionTexture, 9728, oldMinimapMessesUpTextureFilter, 0, hasLight2);
                                                                        renderTexturedModalRectWithLighting2((float)(textureX - flooredCameraX), (float)(textureZ - flooredCameraZ), (float)textureSize, (float)textureSize, hasLight2);
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                if (leveledRegion.loadingAnimation()) {
                                    GlStateManager.func_179094_E();
                                    GlStateManager.func_179137_b(leveledSideInBlocks * (leveledRegX + 0.5) - flooredCameraX, leveledSideInBlocks * (leveledRegZ + 0.5) - flooredCameraZ, 0.0);
                                    final float loadingAnimationPassed = (float)(System.currentTimeMillis() - this.loadingAnimationStart);
                                    if (loadingAnimationPassed > 0.0f) {
                                        restoreTextureStates();
                                        final int period = 2000;
                                        final int numbersOfActors = 3;
                                        final float loadingAnimation = loadingAnimationPassed % period / period * 360.0f;
                                        final float step = 360.0f / numbersOfActors;
                                        GlStateManager.func_179114_b(loadingAnimation, 0.0f, 0.0f, 1.0f);
                                        final int numberOfVisibleActors = 1 + (int)loadingAnimationPassed % (3 * period) / period;
                                        GlStateManager.func_179152_a((float)leveledSideInRegions, (float)leveledSideInRegions, 1.0f);
                                        for (int i2 = 0; i2 < numberOfVisibleActors; ++i2) {
                                            GlStateManager.func_179114_b(step, 0.0f, 0.0f, 1.0f);
                                            func_73734_a(16, -8, 32, 8, -1);
                                        }
                                        GlStateManager.func_179084_k();
                                        setupTextureMatricesAndTextures(brightness);
                                    }
                                    GlStateManager.func_179121_F();
                                }
                                if (debugConfig && leveledRegion instanceof MapRegion) {
                                    final MapRegion region2 = (MapRegion)leveledRegion;
                                    restoreTextureStates();
                                    GlStateManager.func_179094_E();
                                    GlStateManager.func_179109_b((float)(512 * region2.getRegionX() + 32 - flooredCameraX), (float)(512 * region2.getRegionZ() + 32 - flooredCameraZ), 0.0f);
                                    GlStateManager.func_179152_a(10.0f, 10.0f, 1.0f);
                                    this.func_73731_b(mc.field_71466_p, "" + region2.getLoadState(), 0, 0, -1);
                                    GlStateManager.func_179121_F();
                                    GlStateManager.func_179084_k();
                                    setupTextureMatricesAndTextures(brightness);
                                }
                                if (debugConfig && textureLevel > 0) {
                                    restoreTextureStates();
                                    for (int leafX2 = 0; leafX2 < leveledSideInRegions; ++leafX2) {
                                        for (int leafZ2 = 0; leafZ2 < leveledSideInRegions; ++leafZ2) {
                                            final int regX2 = leafRegionMinX + leafX2;
                                            final int regZ2 = leafRegionMinZ + leafZ2;
                                            final MapRegion region3 = this.mapProcessor.getLeafMapRegion(renderedCaveLayer, regX2, regZ2, false);
                                            if (region3 != null) {
                                                final boolean currentlyLoading = this.mapProcessor.getMapSaveLoad().getNextToLoadByViewing() == region3;
                                                if (currentlyLoading || region3.isLoaded() || region3.isMetaLoaded()) {
                                                    GlStateManager.func_179094_E();
                                                    GlStateManager.func_179109_b((float)(512 * region3.getRegionX() - flooredCameraX), (float)(512 * region3.getRegionZ() - flooredCameraZ), 0.0f);
                                                    func_73734_a(0, 0, 512, 512, currentlyLoading ? 687800575 : (region3.isLoaded() ? 671153920 : 687865600));
                                                    GlStateManager.func_179121_F();
                                                }
                                            }
                                        }
                                    }
                                    GlStateManager.func_179084_k();
                                    setupTextureMatricesAndTextures(brightness);
                                }
                            }
                        }
                    }
                    this.lastFrameRenderedRootTextures = frameRenderedRootTextures;
                    restoreTextureStates();
                    final LeveledRegion<?> nextToLoad = (LeveledRegion<?>)this.mapProcessor.getMapSaveLoad().getNextToLoadByViewing();
                    boolean shouldRequest = false;
                    shouldRequest = (nextToLoad == null || nextToLoad.shouldAllowAnotherRegionToLoad());
                    shouldRequest = (shouldRequest && this.mapProcessor.getAffectingLoadingFrequencyCount() < 16);
                    if (shouldRequest && !WorldMap.pauseRequests) {
                        for (int toRequest = 2, counter = 0, i3 = 0; i3 < this.branchRegionBuffer.size() && counter < toRequest; ++i3) {
                            final BranchLeveledRegion region4 = this.branchRegionBuffer.get(i3);
                            if (!region4.reloadHasBeenRequested() && !region4.recacheHasBeenRequested()) {
                                if (!region4.isLoaded()) {
                                    region4.setReloadHasBeenRequested(true, "Gui");
                                    this.mapProcessor.getMapSaveLoad().requestBranchCache(region4, "Gui");
                                    if (counter == 0) {
                                        this.mapProcessor.getMapSaveLoad().setNextToLoadByViewing((LeveledRegion)region4);
                                    }
                                    ++counter;
                                }
                            }
                        }
                        final int toRequest = 1;
                        int counter = 0;
                        if (!prevWaitingForBranchCache) {
                            for (int i3 = 0; i3 < this.regionBuffer.size() && counter < toRequest; ++i3) {
                                final MapRegion region5 = this.regionBuffer.get(i3);
                                if (region5 != nextToLoad || this.regionBuffer.size() <= 1) {
                                    synchronized (region5) {
                                        if (region5.canRequestReload_unsynced()) {
                                            if (region5.getLoadState() == 2) {
                                                region5.requestRefresh(this.mapProcessor);
                                            }
                                            else {
                                                this.mapProcessor.getMapSaveLoad().requestLoad(region5, "Gui");
                                            }
                                            if (counter == 0) {
                                                this.mapProcessor.getMapSaveLoad().setNextToLoadByViewing((LeveledRegion)region5);
                                            }
                                            ++counter;
                                            if (region5.getLoadState() == 4) {
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    this.prevWaitingForBranchCache = this.waitingForBranchCache[0];
                    this.prevLoadingLeaves = loadingLeaves;
                    final int chunkHighlightLeftX = this.mouseBlockPosX >> 4 << 4;
                    final int chunkHighlightRightX = (this.mouseBlockPosX >> 4) + 1 << 4;
                    final int chunkHighlightTopZ = this.mouseBlockPosZ >> 4 << 4;
                    final int chunkHighlightBottomZ = (this.mouseBlockPosZ >> 4) + 1 << 4;
                    MapRenderHelper.renderDynamicHighlight(flooredCameraX, flooredCameraZ, chunkHighlightLeftX, chunkHighlightRightX, chunkHighlightTopZ, chunkHighlightBottomZ, 0.0f, 0.0f, 0.0f, 0.2f, 1.0f, 1.0f, 1.0f, 0.1569f);
                    MapTileSelection mapTileSelectionToRender = this.mapTileSelection;
                    if (mapTileSelectionToRender == null && mc.field_71462_r instanceof ExportScreen) {
                        mapTileSelectionToRender = ((ExportScreen)mc.field_71462_r).getSelection();
                    }
                    if (mapTileSelectionToRender != null) {
                        MapRenderHelper.renderDynamicHighlight(flooredCameraX, flooredCameraZ, mapTileSelectionToRender.getLeft() << 4, mapTileSelectionToRender.getRight() + 1 << 4, mapTileSelectionToRender.getTop() << 4, mapTileSelectionToRender.getBottom() + 1 << 4, 0.0f, 0.0f, 0.0f, 0.2f, 1.0f, 0.5f, 0.5f, 0.4f);
                    }
                    GlStateManager.func_179147_l();
                    GlStateManager.func_179141_d();
                    GlStateManager.func_179084_k();
                    GlStateManager.func_179118_c();
                    GuiMap.primaryScaleFBO.func_147609_e();
                    Minecraft.func_71410_x().func_147110_a().func_147610_a(false);
                    GlStateManager.func_179089_o();
                    GlStateManager.func_179121_F();
                    GlStateManager.func_179094_E();
                    GlStateManager.func_179139_a(secondaryScale, secondaryScale, 1.0);
                    GuiMap.primaryScaleFBO.func_147612_c();
                    GL11.glTexParameteri(3553, 10240, 9729);
                    GL11.glTexParameteri(3553, 10241, 9729);
                    GlStateManager.func_179132_a(false);
                    int lineX = -mc.field_71443_c / 2;
                    int lineY = mc.field_71440_d / 2 - 5;
                    int lineW = mc.field_71443_c;
                    int lineH = 6;
                    func_73734_a(lineX, lineY, lineX + lineW, lineY + lineH, -16777216);
                    lineX = mc.field_71443_c / 2 - 5;
                    lineY = -mc.field_71440_d / 2;
                    lineW = 6;
                    lineH = mc.field_71440_d;
                    func_73734_a(lineX, lineY, lineX + lineW, lineY + lineH, -16777216);
                    GlStateManager.func_179131_c(1.0f, 1.0f, 1.0f, 1.0f);
                    GlStateManager.func_179126_j();
                    if (SupportMods.vivecraft) {
                        GlStateManager.func_179147_l();
                        GlStateManager.func_179120_a(1, 0, 0, 1);
                    }
                    renderTexturedModalRect(-mc.field_71443_c / 2 - (float)secondaryOffsetX, -mc.field_71440_d / 2 - (float)secondaryOffsetY, (float)mc.field_71443_c, (float)mc.field_71440_d);
                    GlStateManager.func_179132_a(true);
                    if (SupportMods.vivecraft) {
                        GlStateManager.func_179120_a(770, 771, 1, 0);
                    }
                    GlStateManager.func_179121_F();
                    GlStateManager.func_179139_a(this.scale, this.scale, 1.0);
                    GlStateManager.func_179147_l();
                    GlStateManager.func_179141_d();
                    final double screenSizeBasedScale = scaleMultiplier;
                    GlStateManager.func_179129_p();
                    WorldMap.trackedPlayerRenderer.update(mc);
                    try {
                        this.viewed = (HoveredMapElementHolder<?, ?>)WorldMap.mapElementRenderHandler.render(this, this.cameraX, this.cameraZ, mc.field_71443_c, mc.field_71440_d, screenSizeBasedScale, this.scale, playerDimDiv, mousePosX, mousePosZ, brightness, renderedCaveLayer != Integer.MAX_VALUE, (HoveredMapElementHolder)this.viewed, mc, partialTicks, this.scaledresolution);
                    }
                    catch (Throwable t) {
                        WorldMap.LOGGER.error("error rendering map elements", t);
                        throw t;
                    }
                    this.viewedInList = false;
                    GlStateManager.func_179089_o();
                    GlStateManager.func_179147_l();
                    GlStateManager.func_179094_E();
                    GlStateManager.func_179109_b(0.0f, 0.0f, 970.0f);
                    if (configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.FOOTSTEPS)) {
                        final ArrayList<Double[]> footprints = this.mapProcessor.getFootprints();
                        synchronized (footprints) {
                            for (int i4 = 0; i4 < footprints.size(); ++i4) {
                                final Double[] coords = footprints.get(i4);
                                this.setColourBuffer(1.0f, 0.1f, 0.1f, 1.0f);
                                this.drawDotOnMap(coords[0] / playerDimDiv - this.cameraX, coords[1] / playerDimDiv - this.cameraZ, 0.0f, 1.0 / this.scale);
                            }
                        }
                    }
                    if (configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.ARROW)) {
                        final boolean toTheLeft = scaledPlayerX < leftBorder;
                        final boolean toTheRight = scaledPlayerX > rightBorder;
                        final boolean down = scaledPlayerZ > bottomBorder;
                        final boolean up = scaledPlayerZ < topBorder;
                        GlStateManager.func_179147_l();
                        float configuredR = 1.0f;
                        float configuredG = 1.0f;
                        float configuredB = 1.0f;
                        int effectiveArrowColorIndex = (int)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.ARROW_COLOR);
                        if (effectiveArrowColorIndex == -2 && !SupportMods.minimap()) {
                            effectiveArrowColorIndex = 0;
                        }
                        if (effectiveArrowColorIndex == -2 && SupportMods.xaeroMinimap.getArrowColorIndex() == -1) {
                            effectiveArrowColorIndex = -1;
                        }
                        if (effectiveArrowColorIndex == -1) {
                            final int rgb = Misc.getTeamColour((Entity)((mc.field_71439_g == null) ? mc.func_175606_aa() : mc.field_71439_g));
                            if (rgb == -1) {
                                effectiveArrowColorIndex = 0;
                            }
                            else {
                                configuredR = (rgb >> 16 & 0xFF) / 255.0f;
                                configuredG = (rgb >> 8 & 0xFF) / 255.0f;
                                configuredB = (rgb & 0xFF) / 255.0f;
                            }
                        }
                        else if (effectiveArrowColorIndex == -2) {
                            final float[] c = SupportMods.xaeroMinimap.getArrowColor();
                            if (c == null) {
                                effectiveArrowColorIndex = 0;
                            }
                            else {
                                configuredR = c[0];
                                configuredG = c[1];
                                configuredB = c[2];
                            }
                        }
                        if (effectiveArrowColorIndex >= 0) {
                            final float[] c = WorldMapConfigConstants.ARROW_COLORS[effectiveArrowColorIndex];
                            configuredR = c[0];
                            configuredG = c[1];
                            configuredB = c[2];
                        }
                        if (toTheLeft || toTheRight || up || down) {
                            double arrowX = scaledPlayerX;
                            double arrowZ = scaledPlayerZ;
                            float a = 0.0f;
                            if (toTheLeft) {
                                a = (up ? 1.5f : (down ? 0.5f : 1.0f));
                                arrowX = leftBorder;
                            }
                            else if (toTheRight) {
                                a = (up ? 2.5f : (down ? 3.5f : 3.0f));
                                arrowX = rightBorder;
                            }
                            if (down) {
                                arrowZ = bottomBorder;
                            }
                            else if (up) {
                                if (a == 0.0f) {
                                    a = 2.0f;
                                }
                                arrowZ = topBorder;
                            }
                            this.setColourBuffer(0.0f, 0.0f, 0.0f, 0.9f);
                            this.drawFarArrowOnMap(arrowX - this.cameraX, arrowZ + 2.0 * screenSizeBasedScale / this.scale - this.cameraZ, a, screenSizeBasedScale / this.scale);
                            this.setColourBuffer(configuredR, configuredG, configuredB, 1.0f);
                            this.drawFarArrowOnMap(arrowX - this.cameraX, arrowZ - this.cameraZ, a, screenSizeBasedScale / this.scale);
                        }
                        else {
                            this.setColourBuffer(0.0f, 0.0f, 0.0f, 0.9f);
                            this.drawArrowOnMap(scaledPlayerX - this.cameraX, scaledPlayerZ + 2.0 * screenSizeBasedScale / this.scale - this.cameraZ, this.player.field_70177_z, screenSizeBasedScale / this.scale);
                            this.setColourBuffer(configuredR, configuredG, configuredB, 1.0f);
                            this.drawArrowOnMap(scaledPlayerX - this.cameraX, scaledPlayerZ - this.cameraZ, this.player.field_70177_z, screenSizeBasedScale / this.scale);
                        }
                    }
                    GlStateManager.func_179121_F();
                    GlStateManager.func_179121_F();
                    int cursorDisplayOffset = 0;
                    if (configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.COORDINATES)) {
                        String coordsString = "X: " + this.mouseBlockPosX;
                        if (mouseBlockBottomY != -1) {
                            coordsString = coordsString + " Y: " + mouseBlockBottomY;
                        }
                        if (hasAmbiguousHeight && mouseBlockTopY != -1) {
                            coordsString = coordsString + " (" + mouseBlockTopY + ")";
                        }
                        coordsString = coordsString + " Z: " + this.mouseBlockPosZ;
                        MapRenderHelper.drawCenteredStringWithBackground(this.field_146289_q, coordsString, this.field_146294_l / 2, 2 + cursorDisplayOffset, -1, 0.0f, 0.0f, 0.0f, 0.4f);
                        cursorDisplayOffset += 10;
                    }
                    if ((boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.DISPLAY_HOVERED_BIOME) && pointedAtBiome != -1) {
                        final Biome biome = Biome.func_150568_d(pointedAtBiome);
                        final String biomeText = (biome == null) ? I18n.func_135052_a("gui.xaero_wm_unknown_biome", new Object[0]) : biome.func_185359_l();
                        MapRenderHelper.drawCenteredStringWithBackground(this.field_146289_q, biomeText, this.field_146294_l / 2, 2 + cursorDisplayOffset, -1, 0.0f, 0.0f, 0.0f, 0.4f);
                    }
                    int subtleTooltipOffset = 12;
                    if (configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.DISPLAY_ZOOM)) {
                        final String zoomString = Math.round(GuiMap.destScale * 1000.0) / 1000.0 + "x";
                        MapRenderHelper.drawCenteredStringWithBackground(mc.field_71466_p, zoomString, this.field_146294_l / 2, this.field_146295_m - subtleTooltipOffset, -1, 0.0f, 0.0f, 0.0f, 0.4f);
                    }
                    if (this.mapProcessor.getMapWorld().getCurrentDimension().getFullReloader() != null) {
                        subtleTooltipOffset += 12;
                        MapRenderHelper.drawCenteredStringWithBackground(mc.field_71466_p, GuiMap.FULL_RELOAD_IN_PROGRESS, this.field_146294_l / 2, this.field_146295_m - subtleTooltipOffset, -1, 0.0f, 0.0f, 0.0f, 0.4f);
                    }
                    if (this.mapProcessor.getMapWorld().isUsingUnknownDimensionType()) {
                        subtleTooltipOffset += 24;
                        MapRenderHelper.drawCenteredStringWithBackground(mc.field_71466_p, GuiMap.UNKNOWN_DIMENSION_TYPE2, this.field_146294_l / 2, this.field_146295_m - subtleTooltipOffset, -1, 0.0f, 0.0f, 0.0f, 0.4f);
                        subtleTooltipOffset += 12;
                        MapRenderHelper.drawCenteredStringWithBackground(mc.field_71466_p, GuiMap.UNKNOWN_DIMENSION_TYPE1, this.field_146294_l / 2, this.field_146295_m - subtleTooltipOffset, -1, 0.0f, 0.0f, 0.0f, 0.4f);
                    }
                    if (configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.DISPLAY_CAVE_MODE_START)) {
                        subtleTooltipOffset += 12;
                        if (globalCaveStart != Integer.MAX_VALUE && globalCaveStart != Integer.MIN_VALUE) {
                            final String caveModeStartString = I18n.func_135052_a("gui.xaero_wm_cave_mode_start_display", new Object[] { globalCaveStart });
                            MapRenderHelper.drawCenteredStringWithBackground(mc.field_71466_p, caveModeStartString, this.field_146294_l / 2, this.field_146295_m - subtleTooltipOffset, -1, 0.0f, 0.0f, 0.0f, 0.4f);
                        }
                    }
                    if (SupportMods.minimap()) {
                        final String subWorldNameToRender = SupportMods.xaeroMinimap.getSubWorldNameToRender();
                        if (subWorldNameToRender != null) {
                            subtleTooltipOffset += 24;
                            MapRenderHelper.drawCenteredStringWithBackground(mc.field_71466_p, subWorldNameToRender, this.field_146294_l / 2, this.field_146295_m - subtleTooltipOffset, -1, 0.0f, 0.0f, 0.0f, 0.4f);
                        }
                    }
                    discoveredForHighlights = (mouseBlockBottomY != -1);
                    final ITextComponent subtleHighlightTooltip = this.mapProcessor.getMapWorld().getCurrentDimension().getHighlightHandler().getBlockHighlightSubtleTooltip(this.mouseBlockPosX, this.mouseBlockPosZ, discoveredForHighlights);
                    if (subtleHighlightTooltip != null) {
                        subtleTooltipOffset += 12;
                        MapRenderHelper.drawCenteredStringWithBackground(mc.field_71466_p, subtleHighlightTooltip, this.field_146294_l / 2, this.field_146295_m - subtleTooltipOffset, -1, 0.0f, 0.0f, 0.0f, 0.4f);
                    }
                    this.overWaypointsMenu = false;
                    this.overPlayersMenu = false;
                    final boolean renderingMenus = this.waypointMenu || this.playersMenu;
                    if (renderingMenus) {
                        GlStateManager.func_179094_E();
                        GlStateManager.func_179109_b(0.0f, 0.0f, 972.0f);
                    }
                    if (this.waypointMenu) {
                        if (SupportMods.xaeroMinimap.getWaypointsSorted() != null) {
                            final HoveredMapElementHolder<?, ?> hovered = SupportMods.xaeroMinimap.renderWaypointsMenu(this, this.scale, this.field_146294_l, this.field_146295_m, scaledMouseX, scaledMouseY, this.leftMouseButton.isDown, this.leftMouseButton.clicked, this.viewed, mc);
                            if (hovered != null) {
                                this.overWaypointsMenu = true;
                                if (hovered.getElement() instanceof Waypoint) {
                                    this.viewed = hovered;
                                    this.viewedInList = true;
                                    if (this.leftMouseButton.clicked) {
                                        this.cameraDestination = new int[] { (int)((Waypoint)this.viewed.getElement()).getRenderX(), (int)((Waypoint)this.viewed.getElement()).getRenderZ() };
                                        this.leftMouseButton.isDown = false;
                                        final boolean closeWaypointsWhenHopping = (boolean)primaryConfigManager.getEffective((ConfigOption)WorldMapPrimaryClientConfigOptions.CLOSE_WAYPOINTS_AFTER_HOP);
                                        if (closeWaypointsWhenHopping) {
                                            this.onWaypointsButton(this.waypointsButton);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if (this.playersMenu) {
                        final HoveredMapElementHolder<?, ?> hovered = (HoveredMapElementHolder<?, ?>)WorldMap.trackedPlayerMenuRenderer.renderMenu(this, this.scale, this.field_146294_l, this.field_146295_m, scaledMouseX, scaledMouseY, this.leftMouseButton.isDown, this.leftMouseButton.clicked, (HoveredMapElementHolder)this.viewed, mc);
                        if (hovered != null) {
                            this.overPlayersMenu = true;
                            if (hovered.getElement() instanceof PlayerTrackerMapElement && WorldMap.trackedPlayerMenuRenderer.canJumpTo((PlayerTrackerMapElement<?>)hovered.getElement())) {
                                this.viewed = hovered;
                                this.viewedInList = true;
                                if (this.leftMouseButton.clicked) {
                                    final PlayerTrackerMapElement<?> clickedPlayer = (PlayerTrackerMapElement<?>)this.viewed.getElement();
                                    final MapDimension clickedPlayerDim = this.mapProcessor.getMapWorld().getDimension(clickedPlayer.getDimension());
                                    final MapDimensionTypeInfo clickedPlayerDimType = MapDimension.getDimensionType(clickedPlayerDim, clickedPlayer.getDimension());
                                    final double clickedPlayerDimDiv = this.mapProcessor.getMapWorld().getCurrentDimension().calculateDimDiv(clickedPlayerDimType);
                                    final double jumpX = clickedPlayer.getX() / clickedPlayerDimDiv;
                                    final double jumpZ = clickedPlayer.getZ() / clickedPlayerDimDiv;
                                    this.cameraDestination = new int[] { (int)jumpX, (int)jumpZ };
                                    this.leftMouseButton.isDown = false;
                                }
                            }
                        }
                    }
                    if (renderingMenus) {
                        GlStateManager.func_179121_F();
                    }
                    if (SupportMods.minimap()) {
                        SupportMods.xaeroMinimap.drawSetChange(this.scaledresolution);
                    }
                    GlStateManager.func_179131_c(1.0f, 1.0f, 1.0f, 1.0f);
                }
                else if (!mapLoaded) {
                    this.renderLoadingScreen();
                }
                else if (isLocked) {
                    this.renderMessageScreen(I18n.func_135052_a("gui.xaero_current_map_locked1", new Object[0]), I18n.func_135052_a("gui.xaero_current_map_locked2", new Object[0]));
                }
                else if (noWorldMapEffect) {
                    this.renderMessageScreen(I18n.func_135052_a("gui.xaero_no_world_map_message", new Object[0]));
                }
                else if (!allowedBasedOnItem) {
                    final String configuredMapItemString = (String)configManager.getEffective(WorldMapProfiledConfigOptions.MAP_ITEM);
                    this.renderMessageScreen(I18n.func_135052_a("gui.xaero_no_world_map_item_message", new Object[0]), configuredMapItemString);
                }
            }
            else {
                this.renderLoadingScreen();
            }
            this.mapSwitchingGui.renderText(mc, scaledMouseX, scaledMouseY, this.field_146294_l, this.field_146295_m);
            mc.func_110434_K().func_110577_a(WorldMap.guiTextures);
            this.func_73729_b(this.field_146294_l - 34, 2, 0, 37, 32, 32);
        }
        GlStateManager.func_179126_j();
        GlStateManager.func_179094_E();
        GlStateManager.func_179109_b(0.0f, 0.0f, 973.0f);
        super.func_73863_a(scaledMouseX, scaledMouseY, partialTicks);
        if (this.rightClickMenu != null) {
            this.rightClickMenu.func_191745_a(mc, scaledMouseX, scaledMouseY, partialTicks);
        }
        GlStateManager.func_179109_b(0.0f, 0.0f, 10.0f);
        if (mc.field_71462_r == this) {
            if (!this.renderTooltips(scaledMouseX, scaledMouseY, partialTicks) && !this.leftMouseButton.isDown && !this.rightMouseButton.isDown) {
                if (this.viewed != null) {
                    final Tooltip hoveredTooltip = this.hoveredElementTooltipHelper(this.viewed, this.viewedInList);
                    if (hoveredTooltip != null) {
                        hoveredTooltip.drawBox(scaledMouseX, scaledMouseY, this.field_146294_l, this.field_146295_m);
                    }
                }
                else {
                    synchronized (this.mapProcessor.renderThreadPauseSync) {
                        if (!this.mapProcessor.isRenderingPaused() && this.mapProcessor.getCurrentWorldId() != null && this.mapProcessor.getMapSaveLoad().isRegionDetectionComplete()) {
                            final ITextComponent bluntHighlightTooltip = this.mapProcessor.getMapWorld().getCurrentDimension().getHighlightHandler().getBlockHighlightBluntTooltip(this.mouseBlockPosX, this.mouseBlockPosZ, discoveredForHighlights);
                            if (bluntHighlightTooltip != null) {
                                new Tooltip(bluntHighlightTooltip).drawBox(scaledMouseX, scaledMouseY, this.field_146294_l, this.field_146295_m);
                            }
                        }
                    }
                }
            }
            GlStateManager.func_179109_b(0.0f, 0.0f, 1.0f);
            this.mapProcessor.getMessageBoxRenderer().render(this.mapProcessor.getMessageBox(), this.field_146289_q, 1, this.field_146295_m / 2, false);
        }
        GlStateManager.func_179121_F();
        final MapMouseButtonPress leftMouseButton = this.leftMouseButton;
        final MapMouseButtonPress rightMouseButton = this.rightMouseButton;
        final boolean b = false;
        rightMouseButton.clicked = b;
        leftMouseButton.clicked = b;
        this.noUploadingLimits = (this.cameraX == cameraXBefore && this.cameraZ == cameraZBefore && scaleBefore == this.scale);
    }
    
    protected void renderPreDropdown(final int scaledMouseX, final int scaledMouseY, final float partialTicks) {
        super.renderPreDropdown(scaledMouseX, scaledMouseY, partialTicks);
        if (this.waypointMenu) {
            SupportMods.xaeroMinimap.getWaypointMenuRenderer().postMapRender(this, scaledMouseX, scaledMouseY, this.field_146294_l, this.field_146295_m, partialTicks);
        }
        if (this.playersMenu) {
            WorldMap.trackedPlayerMenuRenderer.postMapRender(this, scaledMouseX, scaledMouseY, this.field_146294_l, this.field_146295_m, partialTicks);
        }
        this.caveModeOptions.postMapRender(this, scaledMouseX, scaledMouseY, this.field_146294_l, this.field_146295_m, partialTicks);
        this.mapSwitchingGui.postMapRender(this.field_146297_k, scaledMouseX, scaledMouseY, this.field_146294_l, this.field_146295_m);
    }
    
    private <E, C> Tooltip hoveredElementTooltipHelper(final HoveredMapElementHolder<E, C> hovered, final boolean viewedInList) {
        return hovered.getRenderer().getReader().getTooltip(hovered.getElement(), hovered.getRenderer().getContext(), viewedInList);
    }
    
    private void renderLoadingScreen() {
        this.renderMessageScreen("Preparing World Map...");
    }
    
    private void renderMessageScreen(final String message) {
        this.renderMessageScreen(message, null);
    }
    
    private void renderMessageScreen(final String message, final String message2) {
        func_73734_a(0, 0, this.field_146297_k.field_71443_c, this.field_146297_k.field_71440_d, -16777216);
        GlStateManager.func_179094_E();
        GlStateManager.func_179109_b(0.0f, 0.0f, 500.0f);
        this.func_73732_a(this.field_146297_k.field_71466_p, message, this.scaledresolution.func_78326_a() / 2, this.scaledresolution.func_78328_b() / 2, -1);
        if (message2 != null) {
            this.func_73732_a(this.field_146297_k.field_71466_p, message2, this.scaledresolution.func_78326_a() / 2, this.scaledresolution.func_78328_b() / 2 + 10, -1);
        }
        GlStateManager.func_179121_F();
    }
    
    public void drawDotOnMap(final double x, final double z, final float angle, final double sc) {
        this.drawObjectOnMap(x, z, angle, sc, 2.5f, 2.5f, 0, 69, 5, 5, 9729);
    }
    
    public void drawArrowOnMap(final double x, final double z, final float angle, final double sc) {
        this.drawObjectOnMap(x, z, angle, sc, 13.0f, 5.0f, 0, 0, 26, 28, 9729);
    }
    
    public void drawFarArrowOnMap(final double x, final double z, final float angle, final double sc) {
        this.drawObjectOnMap(x, z, angle * 90.0f, sc, 27.0f, 13.0f, 26, 0, 54, 13, 9729);
    }
    
    public void drawObjectOnMap(final double x, final double z, final float angle, final double sc, final float offX, final float offY, final int textureX, final int textureY, final int w, final int h, final int filter) {
        GlStateManager.func_179094_E();
        GlStateManager.func_179131_c(this.colourBuffer[0], this.colourBuffer[1], this.colourBuffer[2], this.colourBuffer[3]);
        GlStateManager.func_179137_b(x, z, 0.0);
        GlStateManager.func_179139_a(sc, sc, 1.0);
        if (angle != 0.0f) {
            GlStateManager.func_179114_b(angle, 0.0f, 0.0f, 1.0f);
        }
        this.field_146297_k.func_110434_K().func_110577_a(WorldMap.guiTextures);
        GL11.glTexParameteri(3553, 10240, filter);
        GL11.glTexParameteri(3553, 10241, filter);
        this.func_175174_a(-offX, -offY, textureX, textureY, w, h);
        if (filter != 9728) {
            GL11.glTexParameteri(3553, 10240, 9728);
            GL11.glTexParameteri(3553, 10241, 9728);
        }
        GlStateManager.func_179131_c(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.func_179121_F();
    }
    
    @Deprecated
    public static void renderTexturedModalRectWithLighting(final float x, final float y, final int textureX, final int textureY, final float width, final float height) {
        GL14.glBlendFuncSeparate(1, 0, 0, 1);
        renderTexturedModalRectWithLighting(x, y, width, height);
        GL14.glBlendFuncSeparate(770, 771, 1, 771);
    }
    
    @Deprecated
    public static void renderTexturedModalRectWithLighting(final float x, final float y, final float width, final float height) {
        renderTexturedModalRectWithLighting2(x, y, width, height, true);
    }
    
    public static void renderTexturedModalRectWithLighting2(final float x, final float y, final float width, final float height, final boolean hasLight) {
        final Tessellator tessellator = Tessellator.func_178181_a();
        final BufferBuilder vertexBuffer = tessellator.func_178180_c();
        if (hasLight) {
            vertexBuffer.func_181668_a(7, GuiMap.POSITION_TEX_TEX_TEX);
            vertexBuffer.func_181662_b((double)(x + 0.0f), (double)(y + height), 0.0).func_187315_a(0.0, 1.0).func_187315_a(0.0, 1.0).func_187315_a(0.0, 1.0).func_187315_a(0.0, 1.0).func_181675_d();
            vertexBuffer.func_181662_b((double)(x + width), (double)(y + height), 0.0).func_187315_a(1.0, 1.0).func_187315_a(1.0, 1.0).func_187315_a(1.0, 1.0).func_187315_a(1.0, 1.0).func_181675_d();
            vertexBuffer.func_181662_b((double)(x + width), (double)(y + 0.0f), 0.0).func_187315_a(1.0, 0.0).func_187315_a(1.0, 0.0).func_187315_a(1.0, 0.0).func_187315_a(1.0, 0.0).func_181675_d();
            vertexBuffer.func_181662_b((double)(x + 0.0f), (double)(y + 0.0f), 0.0).func_187315_a(0.0, 0.0).func_187315_a(0.0, 0.0).func_187315_a(0.0, 0.0).func_187315_a(0.0, 0.0).func_181675_d();
        }
        else {
            vertexBuffer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
            vertexBuffer.func_181662_b((double)(x + 0.0f), (double)(y + height), 0.0).func_187315_a(0.0, 1.0).func_181675_d();
            vertexBuffer.func_181662_b((double)(x + width), (double)(y + height), 0.0).func_187315_a(1.0, 1.0).func_181675_d();
            vertexBuffer.func_181662_b((double)(x + width), (double)(y + 0.0f), 0.0).func_187315_a(1.0, 0.0).func_181675_d();
            vertexBuffer.func_181662_b((double)(x + 0.0f), (double)(y + 0.0f), 0.0).func_187315_a(0.0, 0.0).func_181675_d();
        }
        tessellator.func_78381_a();
    }
    
    public static void renderTexturedModalSubRectWithLighting(final float x, final float y, final float textureX1, final float textureY1, final float textureX2, final float textureY2, final float width, final float height, final boolean hasLight) {
        final Tessellator tessellator = Tessellator.func_178181_a();
        final BufferBuilder vertexBuffer = tessellator.func_178180_c();
        if (hasLight) {
            vertexBuffer.func_181668_a(7, GuiMap.POSITION_TEX_TEX_TEX);
            vertexBuffer.func_181662_b((double)(x + 0.0f), (double)(y + height), 0.0).func_187315_a((double)textureX1, (double)textureY2).func_187315_a((double)textureX1, (double)textureY2).func_187315_a((double)textureX1, (double)textureY2).func_187315_a((double)textureX1, (double)textureY2).func_181675_d();
            vertexBuffer.func_181662_b((double)(x + width), (double)(y + height), 0.0).func_187315_a((double)textureX2, (double)textureY2).func_187315_a((double)textureX2, (double)textureY2).func_187315_a((double)textureX2, (double)textureY2).func_187315_a((double)textureX2, (double)textureY2).func_181675_d();
            vertexBuffer.func_181662_b((double)(x + width), (double)(y + 0.0f), 0.0).func_187315_a((double)textureX2, (double)textureY1).func_187315_a((double)textureX2, (double)textureY1).func_187315_a((double)textureX2, (double)textureY1).func_187315_a((double)textureX2, (double)textureY1).func_181675_d();
            vertexBuffer.func_181662_b((double)(x + 0.0f), (double)(y + 0.0f), 0.0).func_187315_a((double)textureX1, (double)textureY1).func_187315_a((double)textureX1, (double)textureY1).func_187315_a((double)textureX1, (double)textureY1).func_187315_a((double)textureX1, (double)textureY1).func_181675_d();
        }
        else {
            vertexBuffer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
            vertexBuffer.func_181662_b((double)(x + 0.0f), (double)(y + height), 0.0).func_187315_a((double)textureX1, (double)textureY2).func_181675_d();
            vertexBuffer.func_181662_b((double)(x + width), (double)(y + height), 0.0).func_187315_a((double)textureX2, (double)textureY2).func_181675_d();
            vertexBuffer.func_181662_b((double)(x + width), (double)(y + 0.0f), 0.0).func_187315_a((double)textureX2, (double)textureY1).func_181675_d();
            vertexBuffer.func_181662_b((double)(x + 0.0f), (double)(y + 0.0f), 0.0).func_187315_a((double)textureX1, (double)textureY1).func_181675_d();
        }
        tessellator.func_78381_a();
    }
    
    public static void renderTexturedModalRect(final float x, final float y, final float width, final float height) {
        final Tessellator tessellator = Tessellator.func_178181_a();
        final BufferBuilder vertexBuffer = tessellator.func_178180_c();
        vertexBuffer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
        vertexBuffer.func_181662_b((double)(x + 0.0f), (double)(y + height), 0.0).func_187315_a(0.0, 1.0).func_181675_d();
        vertexBuffer.func_181662_b((double)(x + width), (double)(y + height), 0.0).func_187315_a(1.0, 1.0).func_181675_d();
        vertexBuffer.func_181662_b((double)(x + width), (double)(y + 0.0f), 0.0).func_187315_a(1.0, 0.0).func_181675_d();
        vertexBuffer.func_181662_b((double)(x + 0.0f), (double)(y + 0.0f), 0.0).func_187315_a(0.0, 0.0).func_181675_d();
        tessellator.func_78381_a();
    }
    
    public void mapClicked(final int button, final int x, final int y) {
        if (button == 1) {
            if (this.viewedOnMousePress != null && this.viewedOnMousePress.isRightClickValid() && (!(this.viewedOnMousePress.getElement() instanceof Waypoint) || SupportMods.xaeroMinimap.waypointExists((Waypoint)this.viewedOnMousePress.getElement()))) {
                this.handleRightClick((IRightClickableElement)this.viewedOnMousePress, x / this.screenScale, y / this.screenScale);
                this.mouseDownPosX = -1;
                this.mouseDownPosY = -1;
                this.mapTileSelection = null;
            }
            else {
                this.handleRightClick(this, x / this.screenScale, y / this.screenScale);
            }
        }
    }
    
    private void handleRightClick(final IRightClickableElement target, final int x, final int y) {
        if (this.rightClickMenu != null) {
            this.rightClickMenu.setClosed(true);
        }
        this.rightClickMenu = GuiRightClickMenu.getMenu(target, this, x, y, 150);
    }
    
    protected void func_146284_a(final GuiButton button) throws IOException {
        super.func_146284_a(button);
        this.mapSwitchingGui.actionPerformed(this, this.field_146297_k, this.field_146294_l, this.field_146295_m, button);
    }
    
    public void func_73869_a(final char par1, final int par2) throws IOException {
        super.func_73869_a(par1, par2);
        boolean result = false;
        if (this.getFocused() != null) {
            this.getFocused().func_146201_a(par1, par2);
            result = true;
        }
        if (this.isUsingTextField()) {
            if (this.waypointMenu && SupportMods.xaeroMinimap.getWaypointMenuRenderer().keyPressed(this, par2)) {
                return;
            }
            if (this.playersMenu && WorldMap.trackedPlayerMenuRenderer.keyPressed(this, par2)) {
                return;
            }
            result = true;
        }
        if (this.waypointMenu && SupportMods.xaeroMinimap.getWaypointMenuRenderer().charTyped()) {
            result = true;
            return;
        }
        if (this.playersMenu && WorldMap.trackedPlayerMenuRenderer.charTyped()) {
            result = true;
            return;
        }
        if (!result) {
            this.onInputPress(false, Keyboard.getEventKey());
        }
    }
    
    public void func_146282_l() throws IOException {
        if (!Keyboard.getEventKeyState()) {
            this.onInputRelease(false, Keyboard.getEventKey());
        }
        super.func_146282_l();
    }
    
    public static void bindMapTextureWithLighting(final float brightness, final MapTileChunk chunk, final int magFilter, final int lod) {
        setupTextureMatricesAndTextures(brightness);
        bindMapTextureWithLighting3(brightness, chunk, magFilter, lod);
    }
    
    @Deprecated
    public static void bindMapTextureWithLighting2(final float brightness, final MapTileChunk chunk, final int magFilter, final int lod) {
    }
    
    @Deprecated
    public static void bindMapTextureWithLighting3(final float brightness, final MapTileChunk chunk, final int magFilter, final int lod) {
        GlStateManager.func_179131_c(brightness, brightness, brightness, 1.0f);
        final boolean hasLight = chunk.getLeafTexture().getTextureHasLight();
        bindMapTextureWithLighting3(chunk.getLeafTexture(), magFilter, true, lod, hasLight);
    }
    
    public static void bindMapTextureWithLighting3(final MapTileChunk chunk, final int magFilter, final int lod) {
        final boolean hasLight = chunk.getLeafTexture().getTextureHasLight();
        bindMapTextureWithLighting3(chunk.getLeafTexture(), magFilter, true, lod, hasLight);
    }
    
    public static void bindMapTextureWithLighting4(final MapTileChunk chunk, final int lod) {
        final boolean hasLight = chunk.getLeafTexture().getTextureHasLight();
        bindMapTextureWithLighting3(chunk.getLeafTexture(), 0, false, lod, hasLight);
    }
    
    public static void bindMapTextureWithLighting3(final RegionTexture<?> regionTexture, final int magFilter, final boolean changeFilter, final int lod, final boolean hasLight) {
        GlStateManager.func_179138_g(33984);
        final int glTexture = changeFilter ? regionTexture.bindColorTexture(false, magFilter) : regionTexture.bindColorTexture(false);
        if (hasLight) {
            GlStateManager.func_187399_a(8960, 8704, 34160);
        }
        else {
            GlStateManager.func_187399_a(8960, 8704, 8448);
        }
        GlStateManager.func_179138_g(33985);
        GlStateManager.func_179090_x();
        GlStateManager.func_179138_g(33986);
        if (hasLight) {
            GlStateManager.func_179098_w();
            GlStateManager.func_179144_i(glTexture);
        }
        else {
            GlStateManager.func_179090_x();
        }
        GlStateManager.func_179138_g(33987);
        if (hasLight) {
            GlStateManager.func_179098_w();
            GlStateManager.func_179144_i(glTexture);
        }
        else {
            GlStateManager.func_179090_x();
        }
    }
    
    @Deprecated
    public static void setupTextureMatrices() {
    }
    
    private static void setupTexture0() {
        GlStateManager.func_187399_a(8960, 8704, 34160);
        GlStateManager.func_187399_a(8960, 34161, 34023);
        GlStateManager.func_187399_a(8960, 34176, 34167);
        GlStateManager.func_187399_a(8960, 34192, 768);
        GlStateManager.func_187399_a(8960, 34177, 5890);
        GlStateManager.func_187399_a(8960, 34193, 770);
        GlStateManager.func_187399_a(8960, 34162, 7681);
        GlStateManager.func_187399_a(8960, 34184, 34167);
        GlStateManager.func_187399_a(8960, 34200, 770);
    }
    
    private static void setupTexture2() {
        GlStateManager.func_187399_a(8960, 8704, 34160);
        GlStateManager.func_187399_a(8960, 34161, 260);
        GlStateManager.func_187399_a(8960, 34176, 34168);
        GlStateManager.func_187399_a(8960, 34192, 768);
        GlStateManager.func_187399_a(8960, 34177, 5890);
        GlStateManager.func_187399_a(8960, 34193, 770);
        GlStateManager.func_187399_a(8960, 34162, 7681);
        GlStateManager.func_187399_a(8960, 34184, 34167);
        GlStateManager.func_187399_a(8960, 34200, 770);
    }
    
    private static void setupTexture3() {
        GlStateManager.func_187399_a(8960, 8704, 8448);
    }
    
    public static void setupTextures(final float brightness) {
        GlStateManager.func_179131_c(brightness, brightness, brightness, 1.0f);
        GlStateManager.func_179118_c();
        GlStateManager.func_179138_g(33984);
        GlStateManager.func_179098_w();
        setupTexture0();
        GlStateManager.func_179138_g(33985);
        GlStateManager.func_179090_x();
        GlStateManager.func_179138_g(33986);
        setupTexture2();
        GlStateManager.func_179138_g(33987);
        setupTexture3();
        GlStateManager.func_179138_g(33984);
    }
    
    public static void setupTextureMatricesAndTextures(final float brightness) {
        GlStateManager.func_179131_c(brightness, brightness, brightness, 1.0f);
        GlStateManager.func_179118_c();
        GlStateManager.func_179138_g(33984);
        GlStateManager.func_179098_w();
        setupTexture0();
        GlStateManager.func_179138_g(33985);
        GlStateManager.func_179090_x();
        GlStateManager.func_179138_g(33986);
        setupTexture2();
        GlStateManager.func_179138_g(33987);
        setupTexture3();
        GlStateManager.func_179138_g(33984);
    }
    
    public static void restoreTextureStates() {
        GlStateManager.func_179131_c(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.func_179141_d();
        GlStateManager.func_179138_g(33987);
        GlStateManager.func_179090_x();
        GlStateManager.func_179138_g(33986);
        GlStateManager.func_187399_a(8960, 34161, 8448);
        GlStateManager.func_187399_a(8960, 34193, 768);
        GlStateManager.func_187399_a(8960, 34162, 8448);
        GlStateManager.func_187399_a(8960, 34184, 5890);
        GlStateManager.func_187399_a(8960, 8704, 8448);
        GlStateManager.func_179090_x();
        GlStateManager.func_179138_g(33985);
        GlStateManager.func_187399_a(8960, 8704, 8448);
        GlStateManager.func_179090_x();
        GlStateManager.func_179138_g(33984);
        GlStateManager.func_187399_a(8960, 34161, 8448);
        GlStateManager.func_187399_a(8960, 34176, 5890);
        GlStateManager.func_187399_a(8960, 34192, 768);
        GlStateManager.func_187399_a(8960, 34177, 34168);
        GlStateManager.func_187399_a(8960, 34193, 768);
        GlStateManager.func_187399_a(8960, 34162, 8448);
        GlStateManager.func_187399_a(8960, 34184, 5890);
        GlStateManager.func_187399_a(8960, 8704, 8448);
        OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, OpenGlHelper.lastBrightnessX, OpenGlHelper.lastBrightnessY);
    }
    
    private static long bytesToMb(final long bytes) {
        return bytes / 1024L / 1024L;
    }
    
    private void setColourBuffer(final float r, final float g, final float b, final float a) {
        this.colourBuffer[0] = r;
        this.colourBuffer[1] = g;
        this.colourBuffer[2] = b;
        this.colourBuffer[3] = a;
    }
    
    private boolean isUsingTextField() {
        final GuiTextField currentFocused = this.getFocused();
        return currentFocused != null && currentFocused.func_146206_l();
    }
    
    public void func_73876_c() {
        super.func_73876_c();
        if (this.waypointMenu) {
            SupportMods.xaeroMinimap.getWaypointMenuRenderer().tick();
        }
        if (this.playersMenu) {
            WorldMap.trackedPlayerMenuRenderer.tick();
        }
        this.caveModeOptions.tick(this);
    }
    
    public KeyBinding getTrackedPlayerKeyBinding() {
        if (SupportMods.minimap() && SupportMods.xaeroMinimap.hasTrackedPlayerSystemSupport()) {
            return SupportMods.xaeroMinimap.getToggleAllyPlayersKey();
        }
        return ControlsRegister.keyToggleTrackedPlayers;
    }
    
    private boolean onInputPress(final boolean mouse, final int code) {
        if (KeyMappingUtils.inputMatches(mouse, code, ControlsRegister.keyOpenSettings, 0)) {
            this.onSettingsButton(this.settingsButton);
            return true;
        }
        boolean result = false;
        if (KeyMappingUtils.inputMatches(mouse, code, this.field_146297_k.field_71474_y.field_74321_H, 0)) {
            ReflectionUtils.setReflectFieldValue((Object)this.field_146297_k.field_71474_y.field_74321_H, GuiMap.KEY_BINDING_PRESSED_FIELD, (Object)true);
            result = true;
        }
        if (KeyMappingUtils.inputMatches(mouse, code, ControlsRegister.keyOpenMap, 0)) {
            this.goBack();
            result = true;
        }
        if (KeyMappingUtils.inputMatches(mouse, code, this.getTrackedPlayerKeyBinding(), 0)) {
            WorldMap.trackedPlayerMenuRenderer.onShowPlayersButton(this, this.field_146294_l, this.field_146295_m);
            return true;
        }
        if (((!mouse && code == 13) || KeyMappingUtils.inputMatches(mouse, code, ControlsRegister.keyQuickConfirm, 0)) && this.mapSwitchingGui.active) {
            this.mapSwitchingGui.confirm(this, this.field_146297_k, this.field_146294_l, this.field_146295_m);
            result = true;
        }
        if (KeyMappingUtils.inputMatches(mouse, code, ControlsRegister.keyToggleDimension, 1)) {
            this.onDimensionToggleButton(this.dimensionToggleButton);
            result = true;
        }
        if (SupportMods.minimap()) {
            SupportMods.xaeroMinimap.onMapKeyPressed(mouse, code, this);
            result = true;
        }
        final IRightClickableElement hoverTarget = this.getHoverTarget();
        if (hoverTarget != null && !mouse) {
            final boolean isValid = hoverTarget.isRightClickValid();
            if (isValid) {
                if (hoverTarget instanceof HoveredMapElementHolder && ((HoveredMapElementHolder)hoverTarget).getElement() instanceof Waypoint) {
                    switch (code) {
                        case 35: {
                            SupportMods.xaeroMinimap.disableWaypoint((Waypoint)((HoveredMapElementHolder)hoverTarget).getElement());
                            this.closeRightClick();
                            result = true;
                            break;
                        }
                        case 211: {
                            SupportMods.xaeroMinimap.deleteWaypoint((Waypoint)((HoveredMapElementHolder)hoverTarget).getElement());
                            this.closeRightClick();
                            result = true;
                            break;
                        }
                    }
                }
            }
            else {
                this.closeRightClick();
            }
        }
        return result;
    }
    
    private double getCurrentMapCoordinateScale() {
        return this.mapProcessor.getMapWorld().getCurrentDimension().calculateDimScale();
    }
    
    private boolean onInputRelease(final boolean mouse, final int code) {
        boolean result = false;
        if (KeyMappingUtils.inputMatches(mouse, code, this.field_146297_k.field_71474_y.field_74321_H, 0)) {
            ReflectionUtils.setReflectFieldValue((Object)this.field_146297_k.field_71474_y.field_74321_H, GuiMap.KEY_BINDING_PRESSED_FIELD, (Object)false);
            result = true;
        }
        if (SupportMods.minimap() && SupportMods.xaeroMinimap.onMapKeyReleased(mouse, code, this)) {
            result = true;
        }
        if (SupportMods.minimap() && this.lastViewedDimensionId != null && !this.isUsingTextField()) {
            final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
            final boolean waypointsConfig = (boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.WAYPOINTS);
            int waypointDestinationX = this.mouseBlockPosX;
            int waypointDestinationY = this.mouseBlockPosY;
            int waypointDestinationZ = this.mouseBlockPosZ;
            double waypointDestinationCoordinateScale = this.mouseBlockCoordinateScale;
            boolean waypointDestinationRightClick = false;
            if (this.rightClickMenu != null && this.rightClickMenu.getTarget() == this) {
                waypointDestinationX = this.rightClickX;
                waypointDestinationY = this.rightClickY;
                waypointDestinationZ = this.rightClickZ;
                waypointDestinationCoordinateScale = this.rightClickCoordinateScale;
                waypointDestinationRightClick = true;
            }
            if (SupportMods.xaeroMinimap.canCreateWaypoint(waypointDestinationY, waypointDestinationRightClick)) {
                if (KeyMappingUtils.inputMatches(mouse, code, SupportMods.xaeroMinimap.getWaypointKeyBinding(), 0) && waypointsConfig) {
                    SupportMods.xaeroMinimap.createWaypoint(this, waypointDestinationX, (waypointDestinationY == -1) ? -1 : (waypointDestinationY + 1), waypointDestinationZ, waypointDestinationCoordinateScale, waypointDestinationRightClick);
                    this.closeRightClick();
                    result = true;
                }
                if (KeyMappingUtils.inputMatches(mouse, code, SupportMods.xaeroMinimap.getTempWaypointKeyBinding(), 0) && waypointsConfig) {
                    this.closeRightClick();
                    SupportMods.xaeroMinimap.createTempWaypoint(waypointDestinationX, (waypointDestinationY == -1) ? -1 : (waypointDestinationY + 1), waypointDestinationZ, waypointDestinationCoordinateScale, waypointDestinationRightClick);
                    result = true;
                }
            }
            final IRightClickableElement hoverTarget = this.getHoverTarget();
            if (hoverTarget != null && !mouse && !KeyMappingUtils.inputMatches(mouse, code, ControlsRegister.keyOpenMap, 0)) {
                final boolean isValid = hoverTarget.isRightClickValid();
                if (isValid) {
                    if (hoverTarget instanceof HoveredMapElementHolder && ((HoveredMapElementHolder)hoverTarget).getElement() instanceof Waypoint) {
                        switch (code) {
                            case 20: {
                                SupportMods.xaeroMinimap.teleportToWaypoint((GuiScreen)this, (Waypoint)((HoveredMapElementHolder)hoverTarget).getElement());
                                this.closeRightClick();
                                result = true;
                                break;
                            }
                            case 18: {
                                SupportMods.xaeroMinimap.openWaypoint(this, (Waypoint)((HoveredMapElementHolder)hoverTarget).getElement());
                                this.closeRightClick();
                                result = true;
                                break;
                            }
                        }
                    }
                    else if (hoverTarget instanceof HoveredMapElementHolder && ((HoveredMapElementHolder)hoverTarget).getElement() instanceof PlayerTrackerMapElement) {
                        switch (code) {
                            case 20: {
                                new PlayerTeleporter().teleportToPlayer((GuiScreen)this, this.mapProcessor.getMapWorld(), (PlayerTrackerMapElement<?>)((HoveredMapElementHolder)hoverTarget).getElement());
                                this.closeRightClick();
                                result = true;
                                break;
                            }
                        }
                    }
                    else {
                        this.closeRightClick();
                    }
                }
            }
        }
        return result;
    }
    
    private IRightClickableElement getHoverTarget() {
        return (IRightClickableElement)((this.rightClickMenu != null) ? this.rightClickMenu.getTarget() : this.viewed);
    }
    
    private void unfocusAll() {
        if (SupportMods.minimap()) {
            SupportMods.xaeroMinimap.getWaypointMenuRenderer().unfocusAll();
        }
        WorldMap.trackedPlayerMenuRenderer.unfocusAll();
        this.caveModeOptions.unfocusAll();
        this.setFocused(null);
    }
    
    public void closeRightClick() {
        if (this.rightClickMenu != null) {
            this.rightClickMenu.setClosed(true);
        }
    }
    
    public void onRightClickClosed() {
        this.rightClickMenu = null;
        this.mapTileSelection = null;
    }
    
    private void closeDropdowns() {
        if (this.openDropdown != null) {
            this.openDropdown.setClosed(true);
        }
    }
    
    public ArrayList<RightClickOption> getRightClickOptions() {
        final ArrayList<RightClickOption> options = new ArrayList<RightClickOption>();
        options.add(new RightClickOption("gui.xaero_right_click_map_title", options.size(), this) {
            public void onAction(final GuiScreen screen) {
            }
        });
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        final boolean coordinatesConfig = (boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.COORDINATES);
        final boolean waypointsConfig = (boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.WAYPOINTS);
        if (coordinatesConfig && (!SupportMods.minimap() || !SupportMods.xaeroMinimap.hidingWaypointCoordinates())) {
            if (this.mapTileSelection != null) {
                final String chunkOption = (this.mapTileSelection.getStartX() != this.mapTileSelection.getEndX() || this.mapTileSelection.getStartZ() != this.mapTileSelection.getEndZ()) ? String.format("C: (%d;%d):(%d;%d)", this.mapTileSelection.getLeft(), this.mapTileSelection.getTop(), this.mapTileSelection.getRight(), this.mapTileSelection.getBottom()) : String.format("C: (%d;%d)", this.mapTileSelection.getLeft(), this.mapTileSelection.getTop());
                options.add(new RightClickOption(chunkOption, options.size(), this) {
                    public void onAction(final GuiScreen screen) {
                    }
                });
            }
            options.add(new RightClickOption(String.format((this.rightClickY != -1) ? "X: %1$d, Y: %2$d, Z: %3$d" : "X: %1$d, Z: %3$d", this.rightClickX, this.rightClickY, this.rightClickZ), options.size(), this) {
                public void onAction(final GuiScreen screen) {
                }
            });
        }
        if (SupportMods.minimap() && waypointsConfig) {
            options.add(new RightClickOption("gui.xaero_right_click_map_create_waypoint", options.size(), this) {
                public void onAction(final GuiScreen screen) {
                    SupportMods.xaeroMinimap.createWaypoint(GuiMap.this, GuiMap.this.rightClickX, (GuiMap.this.rightClickY == -1) ? -1 : (GuiMap.this.rightClickY + 1), GuiMap.this.rightClickZ, GuiMap.this.rightClickCoordinateScale, true);
                }
            }.setNameFormatArgs(new Object[] { KeyMappingUtils.getKeyName(SupportMods.xaeroMinimap.getWaypointKeyBinding()) }));
            options.add(new RightClickOption("gui.xaero_right_click_map_create_temporary_waypoint", options.size(), this) {
                public void onAction(final GuiScreen screen) {
                    SupportMods.xaeroMinimap.createTempWaypoint(GuiMap.this.rightClickX, (GuiMap.this.rightClickY == -1) ? -1 : (GuiMap.this.rightClickY + 1), GuiMap.this.rightClickZ, GuiMap.this.rightClickCoordinateScale, true);
                }
            }.setNameFormatArgs(new Object[] { KeyMappingUtils.getKeyName(SupportMods.xaeroMinimap.getTempWaypointKeyBinding()) }));
        }
        final MapDimension currentDimension = this.mapProcessor.getMapWorld().getCurrentDimension();
        if (!this.field_146297_k.field_71442_b.func_78763_f() || currentDimension != null) {
            final boolean teleportAllowed = (boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.MAP_TELEPORT_ALLOWED);
            if (teleportAllowed && (this.rightClickY != -1 || !this.field_146297_k.field_71442_b.func_78763_f())) {
                options.add(new RightClickOption("gui.xaero_right_click_map_teleport", options.size(), this) {
                    public void onAction(final GuiScreen screen) {
                        final MapDimension currentDimension = GuiMap.this.mapProcessor.getMapWorld().getCurrentDimension();
                        if ((!GuiMap.this.field_146297_k.field_71442_b.func_78763_f() || currentDimension != null) && (GuiMap.this.rightClickY != -1 || !GuiMap.this.field_146297_k.field_71442_b.func_78763_f())) {
                            final Integer tpDim = (GuiMap.this.rightClickDim != GuiMap.this.field_146297_k.field_71441_e.field_73011_w.getDimension()) ? GuiMap.this.rightClickDim : null;
                            new MapTeleporter().teleport((GuiScreen)GuiMap.this, GuiMap.this.mapProcessor.getMapWorld(), GuiMap.this.rightClickX, (GuiMap.this.rightClickY == -1) ? -1 : (GuiMap.this.rightClickY + 1), GuiMap.this.rightClickZ, tpDim);
                        }
                    }
                });
            }
            else if (!teleportAllowed) {
                options.add(new RightClickOption("gui.xaero_wm_right_click_map_teleport_not_allowed", options.size(), this) {
                    public void onAction(final GuiScreen screen) {
                    }
                });
            }
            else {
                options.add(new RightClickOption("gui.xaero_right_click_map_cant_teleport", options.size(), this) {
                    public void onAction(final GuiScreen screen) {
                    }
                });
            }
        }
        else {
            options.add(new RightClickOption("gui.xaero_right_click_map_cant_teleport_world", options.size(), this) {
                public void onAction(final GuiScreen screen) {
                }
            });
        }
        if (SupportMods.minimap()) {
            if (SupportMods.xaeroMinimap.canShareLocation(this.rightClickY)) {
                options.add(new RightClickOption("gui.xaero_right_click_map_share_location", options.size(), this) {
                    public void onAction(final GuiScreen screen) {
                        SupportMods.xaeroMinimap.shareLocation(GuiMap.this, GuiMap.this.rightClickX, (GuiMap.this.rightClickY == -1) ? -1 : (GuiMap.this.rightClickY + 1), GuiMap.this.rightClickZ);
                    }
                });
            }
            else {
                options.add(new RightClickOption("gui.xaero_right_click_map_cant_share_location", options.size(), this) {
                    public void onAction(final GuiScreen screen) {
                    }
                });
            }
            if (waypointsConfig) {
                options.add(new RightClickOption("gui.xaero_right_click_map_waypoints_menu", options.size(), this) {
                    public void onAction(final GuiScreen screen) {
                        SupportMods.xaeroMinimap.openWaypointsMenu(GuiMap.this.field_146297_k, GuiMap.this);
                    }
                }.setNameFormatArgs(new Object[] { KeyMappingUtils.getKeyName(SupportMods.xaeroMinimap.getTempWaypointsMenuKeyBinding()) }));
            }
        }
        options.add(new RightClickOption("gui.xaero_right_click_box_map_export", options.size(), this) {
            public void onAction(final GuiScreen screen) {
                GuiMap.this.onExportButton(GuiMap.this.exportButton);
            }
        });
        options.add(new RightClickOption("gui.xaero_right_click_box_map_settings", options.size(), this) {
            public void onAction(final GuiScreen screen) {
                GuiMap.this.onSettingsButton(GuiMap.this.settingsButton);
            }
        }.setNameFormatArgs(new Object[] { KeyMappingUtils.getKeyName(ControlsRegister.keyOpenSettings) }));
        return options;
    }
    
    public boolean isRightClickValid() {
        return true;
    }
    
    public int getRightClickTitleBackgroundColor() {
        return -10461088;
    }
    
    public GuiTextField getFocused() {
        return this.focusedField;
    }
    
    public void setFocused(final GuiTextField field) {
        this.focusedField = field;
    }
    
    public boolean shouldSkipWorldRender() {
        return true;
    }
    
    public double getUserScale() {
        return this.userScale;
    }
    
    public GuiButton getRadarButton() {
        return this.radarButton;
    }
    
    public void onDropdownOpen(final DropDownWidget menu) {
        super.onDropdownOpen(menu);
        this.unfocusAll();
    }
    
    public void onDropdownClosed(final DropDownWidget menu) {
        super.onDropdownClosed(menu);
        if (menu == this.rightClickMenu) {
            this.onRightClickClosed();
        }
    }
    
    public void onCaveModeStartSet() {
        this.caveModeOptions.onCaveModeStartSet(this);
    }
    
    public MapDimension getFutureDimension() {
        return this.futureDimension;
    }
    
    public MapProcessor getMapProcessor() {
        return this.mapProcessor;
    }
    
    public void enableCaveModeOptions() {
        if (!this.caveModeOptions.isEnabled()) {
            this.caveModeOptions.toggle(this);
        }
    }
    
    public void removeWidget(final GuiButton current) {
        super.removeWidget(current);
    }
    
    public void addWidget(final DropDownWidget current) {
        super.addWidget(current);
    }
    
    static {
        POSITION_TEX_TEX_TEX = new VertexFormat();
        TEX_2F_1 = new VertexFormatElement(1, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.UV, 2);
        TEX_2F_2 = new VertexFormatElement(2, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.UV, 2);
        TEX_2F_3 = new VertexFormatElement(3, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.UV, 2);
        GuiMap.POSITION_TEX_TEX_TEX.func_181721_a(DefaultVertexFormats.field_181713_m).func_181721_a(DefaultVertexFormats.field_181715_o).func_181721_a(GuiMap.TEX_2F_1).func_181721_a(GuiMap.TEX_2F_2).func_181721_a(GuiMap.TEX_2F_3);
        KEY_BINDING_PRESSED_FIELD = ReflectionUtils.getFieldReflection((Class)KeyBinding.class, "pressed", "", "", "field_74513_e");
        FULL_RELOAD_IN_PROGRESS = (ITextComponent)new TextComponentTranslation("gui.xaero_full_reload_in_progress", new Object[0]);
        UNKNOWN_DIMENSION_TYPE1 = (ITextComponent)new TextComponentTranslation("gui.xaero_unknown_dimension_type1", new Object[0]);
        UNKNOWN_DIMENSION_TYPE2 = (ITextComponent)new TextComponentTranslation("gui.xaero_unknown_dimension_type2", new Object[0]);
        GuiMap.lastAmountOfRegionsViewed = 1;
        GuiMap.destScale = 3.0;
        GuiMap.primaryScaleFBO = null;
    }
}
