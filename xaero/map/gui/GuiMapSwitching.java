//Decompiled by Procyon!

package xaero.map.gui;

import xaero.lib.client.gui.widget.*;
import net.minecraft.client.*;
import net.minecraft.client.resources.*;
import xaero.lib.common.util.*;
import java.util.function.*;
import java.util.*;
import xaero.lib.client.gui.widget.dropdown.*;
import xaero.map.world.*;
import xaero.map.graphics.*;
import net.minecraft.client.gui.*;
import xaero.map.config.util.*;
import xaero.map.*;
import net.minecraft.util.text.*;

public class GuiMapSwitching
{
    private static final ITextComponent CONNECT_MAP;
    private static final ITextComponent DISCONNECT_MAP;
    private MapProcessor mapProcessor;
    private MapDimension settingsDimension;
    private String[] mwDropdownValues;
    private DropDownWidget createdDimensionDropdown;
    private DropDownWidget createdMapDropdown;
    private GuiButton switchingButton;
    private GuiButton multiworldTypeOptionButton;
    private GuiButton renameButton;
    private GuiButton connectButton;
    private GuiButton deleteButton;
    private GuiButton confirmButton;
    private Tooltip serverSelectionModeBox;
    private Tooltip mapSelectionBox;
    public boolean active;
    private boolean writableOnInit;
    private boolean uiPausedOnUpdate;
    private boolean mapSwitchingAllowed;
    
    public GuiMapSwitching(final MapProcessor mapProcessor) {
        this.serverSelectionModeBox = new Tooltip("gui.xaero_mw_server_box");
        this.mapSelectionBox = new Tooltip("gui.xaero_map_selection_box");
        this.mapProcessor = mapProcessor;
        this.mapSelectionBox.setStartWidth(200);
        this.serverSelectionModeBox.setStartWidth(200);
    }
    
    public void init(final GuiMap mapScreen, final Minecraft minecraft, final int width, final int height) {
        final boolean dimensionDDWasOpen = this.createdDimensionDropdown != null && !this.createdDimensionDropdown.isClosed();
        final boolean mapDDWasOpen = this.createdMapDropdown != null && !this.createdMapDropdown.isClosed();
        this.createdDimensionDropdown = null;
        this.createdMapDropdown = null;
        this.switchingButton = null;
        this.multiworldTypeOptionButton = null;
        this.renameButton = null;
        this.deleteButton = null;
        this.confirmButton = null;
        this.settingsDimension = this.mapProcessor.getMapWorld().getFutureDimension();
        this.mapSwitchingAllowed = (this.settingsDimension != null);
        synchronized (this.mapProcessor.uiPauseSync) {
            this.uiPausedOnUpdate = this.isUIPaused();
            mapScreen.addGuiButton(this.switchingButton = (GuiButton)new GuiMapSwitchingButton(this.active, 0, height - 20));
            if (this.mapSwitchingAllowed) {
                this.writableOnInit = this.settingsDimension.futureMultiworldWritable;
                if (this.active) {
                    this.createdDimensionDropdown = this.createDimensionDropdown(this.uiPausedOnUpdate, width, mapScreen, minecraft);
                    this.createdMapDropdown = this.createMapDropdown(this.uiPausedOnUpdate, width, mapScreen, minecraft);
                    mapScreen.addWidget(this.createdDimensionDropdown);
                    mapScreen.addWidget(this.createdMapDropdown);
                    if (dimensionDDWasOpen) {
                        this.createdDimensionDropdown.setClosed(false);
                    }
                    if (mapDDWasOpen) {
                        this.createdMapDropdown.setClosed(false);
                    }
                    mapScreen.addGuiButton(this.multiworldTypeOptionButton = (GuiButton)new TooltipButton(width / 2 - 90, 24, 180, 20, this.getMultiworldTypeButtonMessage(), new Supplier<Tooltip>() {
                        @Override
                        public Tooltip get() {
                            return GuiMapSwitching.this.settingsDimension.isFutureMultiworldServerBased() ? GuiMapSwitching.this.serverSelectionModeBox : GuiMapSwitching.this.mapSelectionBox;
                        }
                    }) {
                        protected void onPress() {
                            synchronized (GuiMapSwitching.this.mapProcessor.uiPauseSync) {
                                if (GuiMapSwitching.this.isMapSelectionOptionEnabled()) {
                                    GuiMapSwitching.this.mapProcessor.toggleMultiworldType(GuiMapSwitching.this.settingsDimension);
                                    GuiMapSwitching.this.multiworldTypeOptionButton.field_146126_j = GuiMapSwitching.this.getMultiworldTypeButtonMessage();
                                }
                            }
                        }
                    });
                    mapScreen.addGuiButton(this.renameButton = new GuiButton(-1, width / 2 + 109, 80, 60, 20, I18n.func_135052_a("gui.xaero_rename", new Object[0])));
                    mapScreen.addGuiButton(this.connectButton = new GuiButton(-1, width / 2 + 109, 102, 60, 20, this.getConnectButtonLabel()));
                    mapScreen.addGuiButton(this.deleteButton = new GuiButton(-1, width / 2 - 168, 80, 60, 20, I18n.func_135052_a("gui.xaero_delete", new Object[0])));
                    mapScreen.addGuiButton(this.confirmButton = new GuiButton(200, width / 2 - 50, 104, 100, 20, I18n.func_135052_a("gui.xaero_confirm", new Object[0])));
                    this.updateButtons(mapScreen, width, minecraft);
                }
                else {
                    this.switchingButton.field_146124_l = this.canToggleThisScreen();
                }
            }
            else {
                this.switchingButton.field_146124_l = false;
            }
        }
    }
    
    public static GuiDimensionOptions getSortedDimensionOptions(final MapDimension dim) {
        int selected = 0;
        final Integer currentDim = dim.getDimId();
        final List<KeySortableByOther<Integer>> sortableList = new ArrayList<KeySortableByOther<Integer>>();
        for (final MapDimension dimension : dim.getMapWorld().getDimensionsList()) {
            sortableList.add((KeySortableByOther<Integer>)new KeySortableByOther((Object)dimension.getDimId(), new Comparable[] { dimension.getDropdownLabel() }));
        }
        Collections.sort(sortableList);
        selected = getDropdownSelectionIdFromValue(sortableList, currentDim);
        final Integer[] objectValues = (Integer[])sortableList.stream().map((Function<? super Object, ?>)new Function<KeySortableByOther<Integer>, Integer>() {
            @Override
            public Integer apply(final KeySortableByOther<Integer> ks) {
                return (Integer)ks.getKey();
            }
        }).collect((Supplier<ArrayList>)new Supplier<ArrayList<Integer>>() {
            @Override
            public ArrayList<Integer> get() {
                return new ArrayList<Integer>();
            }
        }, (BiConsumer<ArrayList, ? super Object>)new BiConsumer<ArrayList<Integer>, Integer>() {
            @Override
            public void accept(final ArrayList<Integer> al, final Integer e) {
                al.add(e);
            }
        }, (BiConsumer<ArrayList, ArrayList>)new BiConsumer<ArrayList<Integer>, ArrayList<Integer>>() {
            @Override
            public void accept(final ArrayList<Integer> al, final ArrayList<Integer> al2) {
                al.addAll(al2);
            }
        }).toArray(new Integer[0]);
        final int[] values = new int[objectValues.length];
        for (int i = 0; i < objectValues.length; ++i) {
            values[i] = objectValues[i];
        }
        return new GuiDimensionOptions(selected, values);
    }
    
    private DropDownWidget createDimensionDropdown(final boolean paused, final int width, final GuiMap mapScreen, final Minecraft minecraft) {
        final GuiDimensionOptions dimOptions = getSortedDimensionOptions(this.settingsDimension);
        final List<String> dropdownLabels = new ArrayList<String>();
        final int currentWorldDim = (this.mapProcessor.getWorld() == null) ? 0 : this.mapProcessor.getWorld().field_73011_w.getDimension();
        for (final int k : dimOptions.values) {
            final MapDimension dim = this.settingsDimension.getMapWorld().getDimension(k);
            String result = dim.getDropdownLabel();
            if (k == currentWorldDim) {
                result += " (auto)";
            }
            dropdownLabels.add(result);
        }
        final int[] finalValues = dimOptions.values;
        final DropDownWidget result2 = DropDownWidget.Builder.begin().setOptions((String[])dropdownLabels.toArray(new String[0])).setX(width / 2 - 100).setY(64).setW(200).setSelected(Integer.valueOf(dimOptions.selected)).setCallback((IDropDownWidgetCallback)new IDropDownWidgetCallback() {
            public boolean onSelected(final DropDownWidget dd, final int i) {
                Integer selectedValue = finalValues[i];
                GuiMapSwitching.this.settingsDimension = GuiMapSwitching.this.settingsDimension.getMapWorld().getDimension(selectedValue);
                if (selectedValue == currentWorldDim) {
                    selectedValue = null;
                }
                GuiMapSwitching.this.settingsDimension.getMapWorld().setCustomDimensionId(selectedValue);
                GuiMapSwitching.this.mapProcessor.checkForWorldUpdate();
                final DropDownWidget newDropDown = GuiMapSwitching.this.createMapDropdown(GuiMapSwitching.this.uiPausedOnUpdate, width, mapScreen, minecraft);
                mapScreen.replaceWidget((GuiButton)GuiMapSwitching.this.createdMapDropdown, (GuiButton)newDropDown);
                GuiMapSwitching.this.createdMapDropdown = newDropDown;
                GuiMapSwitching.this.updateButtons(mapScreen, width, minecraft);
                return true;
            }
        }).setContainer((IDropDownContainer)mapScreen).build();
        return result2;
    }
    
    private DropDownWidget createMapDropdown(final boolean paused, final int width, final GuiMap mapScreen, final Minecraft minecraft) {
        int selected = 0;
        List<String> mwDropdownNames;
        if (!paused) {
            final String currentMultiworld = this.settingsDimension.getFutureMultiworldUnsynced();
            final List<KeySortableByOther<String>> sortableList = new ArrayList<KeySortableByOther<String>>();
            for (final String mwId : this.settingsDimension.getMultiworldIdsCopy()) {
                sortableList.add((KeySortableByOther<String>)new KeySortableByOther((Object)mwId, new Comparable[] { this.settingsDimension.getMultiworldName(mwId).toLowerCase() }));
            }
            if (currentMultiworld != null) {
                final int currentIndex = getDropdownSelectionIdFromValue(sortableList, currentMultiworld);
                if (currentIndex == -1) {
                    sortableList.add((KeySortableByOther<String>)new KeySortableByOther((Object)currentMultiworld, new Comparable[] { this.settingsDimension.getMultiworldName(currentMultiworld).toLowerCase() }));
                }
            }
            Collections.sort(sortableList);
            if (currentMultiworld != null) {
                selected = getDropdownSelectionIdFromValue(sortableList, currentMultiworld);
            }
            final List<String> dropdownValuesList = new ArrayList<String>();
            mwDropdownNames = new ArrayList<String>();
            for (final KeySortableByOther<String> sortableKey : sortableList) {
                dropdownValuesList.add((String)sortableKey.getKey());
                mwDropdownNames.add(this.settingsDimension.getMultiworldName((String)sortableKey.getKey()));
            }
            this.mwDropdownValues = dropdownValuesList.toArray(new String[0]);
            mwDropdownNames.add("§8" + I18n.func_135052_a("gui.xaero_create_new_map", new Object[0]));
        }
        else {
            mwDropdownNames = new ArrayList<String>();
            this.mwDropdownValues = null;
            mwDropdownNames.add("§7" + I18n.func_135052_a("gui.xaero_map_menu_please_wait", new Object[0]));
        }
        final DropDownWidget result = DropDownWidget.Builder.begin().setOptions((String[])mwDropdownNames.toArray(new String[0])).setX(width / 2 - 100).setY(84).setW(200).setSelected(Integer.valueOf(selected)).setCallback((IDropDownWidgetCallback)new IDropDownWidgetCallback() {
            public boolean onSelected(final DropDownWidget dd, final int i) {
                synchronized (GuiMapSwitching.this.mapProcessor.uiPauseSync) {
                    if (GuiMapSwitching.this.isUIPaused() || GuiMapSwitching.this.uiPausedOnUpdate) {
                        return false;
                    }
                    if (i < GuiMapSwitching.this.mwDropdownValues.length) {
                        GuiMapSwitching.this.mapProcessor.setMultiworld(GuiMapSwitching.this.settingsDimension, GuiMapSwitching.this.mwDropdownValues[i]);
                        GuiMapSwitching.this.updateButtons(mapScreen, width, minecraft);
                        return true;
                    }
                    minecraft.func_147108_a((GuiScreen)new GuiMapName(GuiMapSwitching.this.mapProcessor, (GuiScreen)mapScreen, (GuiScreen)mapScreen, GuiMapSwitching.this.settingsDimension, (String)null));
                    return false;
                }
            }
        }).setContainer((IDropDownContainer)mapScreen).build();
        result.setActive(!paused);
        return result;
    }
    
    private boolean isUIPaused() {
        return this.mapProcessor.isUIPaused() || this.mapProcessor.isWaitingForWorldUpdate();
    }
    
    private boolean isMapSelectionOptionEnabled() {
        return !this.isUIPaused() && !this.settingsDimension.isFutureMultiworldServerBased() && this.settingsDimension.getMapWorld().isMultiplayer();
    }
    
    private boolean canToggleThisScreen() {
        return !this.isUIPaused() && this.settingsDimension != null && this.settingsDimension.futureMultiworldWritable;
    }
    
    private boolean canDeleteMap() {
        return !this.isUIPaused() && !this.settingsDimension.isFutureUsingWorldSaveUnsynced() && this.mwDropdownValues != null && this.mwDropdownValues.length > 1 && this.settingsDimension.getFutureCustomSelectedMultiworld() != null;
    }
    
    private boolean canRenameMap() {
        return !this.isUIPaused() && !this.settingsDimension.isFutureUsingWorldSaveUnsynced();
    }
    
    private boolean canConnectMap() {
        if (!this.mapProcessor.getMapWorld().isMultiplayer()) {
            return false;
        }
        final MapConnectionNode playerMapKey = this.settingsDimension.getMapWorld().getPlayerMapKey();
        if (playerMapKey == null) {
            return false;
        }
        final MapConnectionNode destinationMapKey = this.settingsDimension.getSelectedMapKeyUnsynced();
        return destinationMapKey != null && !destinationMapKey.equals(playerMapKey);
    }
    
    private boolean canConfirm() {
        return !this.isUIPaused();
    }
    
    private String getConnectButtonLabel() {
        synchronized (this.mapProcessor.uiPauseSync) {
            if (this.isUIPaused()) {
                return GuiMapSwitching.CONNECT_MAP.func_150254_d();
            }
            final MapConnectionNode playerMapKey = this.settingsDimension.getMapWorld().getPlayerMapKey();
            if (playerMapKey == null) {
                return GuiMapSwitching.CONNECT_MAP.func_150254_d();
            }
            final MapConnectionNode destinationMapKey = this.settingsDimension.getSelectedMapKeyUnsynced();
            if (destinationMapKey == null) {
                return GuiMapSwitching.CONNECT_MAP.func_150254_d();
            }
            final MapConnectionManager mapConnections = this.settingsDimension.getMapWorld().getMapConnections();
            if (mapConnections.isConnected(playerMapKey, destinationMapKey)) {
                return GuiMapSwitching.DISCONNECT_MAP.func_150254_d();
            }
            return GuiMapSwitching.CONNECT_MAP.func_150254_d();
        }
    }
    
    private void updateButtons(final GuiMap mapScreen, final int width, final Minecraft minecraft) {
        synchronized (this.mapProcessor.uiPauseSync) {
            final boolean isPaused = this.isUIPaused();
            if (this.uiPausedOnUpdate != isPaused) {
                final DropDownWidget newDropDown = this.active ? this.createMapDropdown(isPaused, width, mapScreen, minecraft) : null;
                if (newDropDown != null) {
                    if (this.createdMapDropdown != null) {
                        mapScreen.replaceWidget((GuiButton)this.createdMapDropdown, (GuiButton)newDropDown);
                    }
                    else {
                        mapScreen.addWidget(newDropDown);
                    }
                }
                else if (this.createdMapDropdown != null) {
                    mapScreen.removeWidget((GuiButton)this.createdMapDropdown);
                }
                this.createdMapDropdown = (this.active ? newDropDown : null);
                this.uiPausedOnUpdate = isPaused;
            }
            this.switchingButton.field_146124_l = this.canToggleThisScreen();
            if (this.deleteButton != null) {
                this.deleteButton.field_146124_l = this.canDeleteMap();
            }
            if (this.renameButton != null) {
                this.renameButton.field_146124_l = this.canRenameMap();
            }
            if (this.connectButton != null) {
                this.connectButton.field_146124_l = this.canConnectMap();
                this.connectButton.field_146126_j = this.getConnectButtonLabel();
            }
            if (this.multiworldTypeOptionButton != null) {
                this.multiworldTypeOptionButton.field_146124_l = this.isMapSelectionOptionEnabled();
            }
            if (this.confirmButton != null) {
                this.confirmButton.field_146124_l = this.canConfirm();
            }
        }
    }
    
    private String getMultiworldTypeButtonMessage() {
        final int multiworldType = this.settingsDimension.getMapWorld().getFutureMultiworldType(this.settingsDimension);
        return I18n.func_135052_a("gui.xaero_map_selection", new Object[0]) + ": " + I18n.func_135052_a(this.settingsDimension.isFutureMultiworldServerBased() ? "gui.xaero_mw_server" : ((multiworldType == 0) ? "gui.xaero_mw_single" : ((multiworldType == 1) ? "gui.xaero_mw_manual" : "gui.xaero_mw_spawn")), new Object[0]);
    }
    
    public void confirm(final GuiMap mapScreen, final Minecraft minecraft, final int width, final int height) {
        if (this.mapProcessor.confirmMultiworld(this.settingsDimension)) {
            this.active = false;
            mapScreen.func_146280_a(minecraft, width, height);
        }
    }
    
    private static <S> int getDropdownSelectionIdFromValue(final List<KeySortableByOther<S>> values, final S value) {
        for (int selected = 0; selected < values.size(); ++selected) {
            if (values.get(selected).getKey().equals(value)) {
                return selected;
            }
        }
        return -1;
    }
    
    public void preMapRender(final GuiMap mapScreen, final Minecraft minecraft, final int width, final int height) {
        if (!this.active && this.settingsDimension != null && !this.settingsDimension.futureMultiworldWritable) {
            this.active = true;
            mapScreen.func_146280_a(minecraft, width, height);
        }
        if (this.mapSwitchingAllowed && (this.createdMapDropdown == null || this.createdMapDropdown.isClosed())) {
            synchronized (this.mapProcessor.uiPauseSync) {
                if (this.uiPausedOnUpdate != this.isUIPaused()) {
                    this.updateButtons(mapScreen, width, minecraft);
                }
            }
        }
        if (this.active && this.settingsDimension != null && this.createdMapDropdown.isClosed() && !this.uiPausedOnUpdate) {
            final String currentMultiworld = this.settingsDimension.getFutureMultiworldUnsynced();
            if (currentMultiworld != null) {
                final String currentDropdownSelection = this.mwDropdownValues[this.createdMapDropdown.getSelected()];
                if (!currentMultiworld.equals(currentDropdownSelection) || this.writableOnInit != this.settingsDimension.futureMultiworldWritable) {
                    mapScreen.func_146280_a(minecraft, width, height);
                }
            }
        }
    }
    
    public void renderText(final Minecraft minecraft, final int mouseX, final int mouseY, final int width, final int height) {
        if (!this.active) {
            return;
        }
        final String selectMapString = I18n.func_135052_a("gui.xaero_select_map", new Object[0]) + ":";
        MapRenderHelper.drawStringWithBackground(minecraft.field_71466_p, selectMapString, width / 2 - minecraft.field_71466_p.func_78256_a(selectMapString) / 2, 49, -1, 0.0f, 0.0f, 0.0f, 0.4f);
    }
    
    public void postMapRender(final Minecraft minecraft, final int mouseX, final int mouseY, final int width, final int height) {
    }
    
    public void actionPerformed(final GuiMap mapScreen, final Minecraft minecraft, final int width, final int height, final GuiButton b) {
        if (b.field_146124_l) {
            if (b == this.switchingButton) {
                synchronized (this.mapProcessor.uiPauseSync) {
                    if (!this.canToggleThisScreen()) {
                        return;
                    }
                    this.active = !this.active;
                    mapScreen.func_146280_a(minecraft, width, height);
                }
            }
            else if (b == this.renameButton) {
                synchronized (this.mapProcessor.uiPauseSync) {
                    if (!this.canRenameMap()) {
                        return;
                    }
                    final String currentMultiworld = this.settingsDimension.getFutureMultiworldUnsynced();
                    if (currentMultiworld == null) {
                        return;
                    }
                    minecraft.func_147108_a((GuiScreen)new GuiMapName(this.mapProcessor, (GuiScreen)mapScreen, (GuiScreen)mapScreen, this.settingsDimension, currentMultiworld));
                }
            }
            else if (b == this.connectButton) {
                if (!this.canConnectMap()) {
                    return;
                }
                final MapConnectionNode playerMapKey = this.settingsDimension.getMapWorld().getPlayerMapKey();
                if (playerMapKey == null) {
                    return;
                }
                final MapConnectionNode destinationMapKey = this.settingsDimension.getSelectedMapKeyUnsynced();
                if (destinationMapKey == null) {
                    return;
                }
                final String autoMapName = playerMapKey.getNamedString(this.settingsDimension.getMapWorld());
                final String selectedMapName = destinationMapKey.getNamedString(this.settingsDimension.getMapWorld());
                final String connectionDisplayString = autoMapName + "   §e<=>§r   " + selectedMapName;
                final MapConnectionManager mapConnections = this.settingsDimension.getMapWorld().getMapConnections();
                final boolean connected = mapConnections.isConnected(playerMapKey, destinationMapKey);
                final GuiYesNoCallback confirmationConsumer = (GuiYesNoCallback)new GuiYesNoCallback() {
                    public void func_73878_a(final boolean result, final int id) {
                        if (result) {
                            synchronized (GuiMapSwitching.this.mapProcessor.uiSync) {
                                if (connected) {
                                    mapConnections.removeConnection(playerMapKey, destinationMapKey);
                                }
                                else {
                                    mapConnections.addConnection(playerMapKey, destinationMapKey);
                                }
                                b.field_146126_j = GuiMapSwitching.this.getConnectButtonLabel();
                                GuiMapSwitching.this.settingsDimension.getMapWorld().saveConfig();
                            }
                        }
                        minecraft.func_147108_a((GuiScreen)mapScreen);
                    }
                };
                if (connected) {
                    minecraft.func_147108_a((GuiScreen)new GuiYesNo(confirmationConsumer, I18n.func_135052_a("gui.xaero_wm_disconnect_from_auto_msg", new Object[0]), connectionDisplayString, 0));
                }
                else {
                    minecraft.func_147108_a((GuiScreen)new GuiYesNo(confirmationConsumer, I18n.func_135052_a("gui.xaero_wm_connect_with_auto_msg", new Object[0]), connectionDisplayString, 0));
                }
            }
            else if (b == this.deleteButton) {
                synchronized (this.mapProcessor.uiPauseSync) {
                    if (!this.canDeleteMap()) {
                        return;
                    }
                    final String selectedMWId = this.settingsDimension.getFutureCustomSelectedMultiworld();
                    minecraft.func_147108_a((GuiScreen)new GuiYesNo((GuiYesNoCallback)new YesNoCallbackImplementation() {
                        @Override
                        public void func_73878_a(final boolean result, final int id) {
                            if (result) {
                                final String mapNameAndIdLine = I18n.func_135052_a("gui.xaero_delete_map_msg4", new Object[0]) + ": " + GuiMapSwitching.this.settingsDimension.getMultiworldName(selectedMWId) + " (" + selectedMWId + ")";
                                minecraft.func_147108_a((GuiScreen)new GuiYesNo((GuiYesNoCallback)new YesNoCallbackImplementation() {
                                    @Override
                                    public void func_73878_a(final boolean result2, final int id) {
                                        if (result2) {
                                            synchronized (GuiMapSwitching.this.mapProcessor.uiSync) {
                                                if (GuiMapSwitching.this.mapProcessor.getMapWorld() == GuiMapSwitching.this.settingsDimension.getMapWorld()) {
                                                    final MapDimension currentDimension = GuiMapSwitching.this.mapProcessor.isMapWorldUsable() ? GuiMapSwitching.this.mapProcessor.getMapWorld().getCurrentDimension() : null;
                                                    if (GuiMapSwitching.this.settingsDimension == currentDimension && GuiMapSwitching.this.settingsDimension.getCurrentMultiworld().equals(selectedMWId)) {
                                                        if (WorldMapClientConfigUtils.getDebug()) {
                                                            WorldMap.LOGGER.info("Delayed map deletion!");
                                                        }
                                                        GuiMapSwitching.this.mapProcessor.requestCurrentMapDeletion();
                                                    }
                                                    else {
                                                        if (WorldMapClientConfigUtils.getDebug()) {
                                                            WorldMap.LOGGER.info("Instant map deletion!");
                                                        }
                                                        GuiMapSwitching.this.settingsDimension.deleteMultiworldMapDataUnsynced(selectedMWId);
                                                    }
                                                    GuiMapSwitching.this.settingsDimension.deleteMultiworldId(selectedMWId);
                                                    GuiMapSwitching.this.settingsDimension.pickDefaultCustomMultiworldUnsynced();
                                                    GuiMapSwitching.this.settingsDimension.saveConfigUnsynced();
                                                    GuiMapSwitching.this.settingsDimension.futureMultiworldWritable = false;
                                                }
                                            }
                                        }
                                        minecraft.func_147108_a((GuiScreen)mapScreen);
                                    }
                                }, I18n.func_135052_a("gui.xaero_delete_map_msg3", new Object[0]), mapNameAndIdLine, -1));
                            }
                            else {
                                minecraft.func_147108_a((GuiScreen)mapScreen);
                            }
                        }
                    }, I18n.func_135052_a("gui.xaero_delete_map_msg1", new Object[0]), I18n.func_135052_a("gui.xaero_delete_map_msg2", new Object[0]), -1));
                }
            }
            else if (b.field_146127_k == 200) {
                synchronized (this.mapProcessor.uiPauseSync) {
                    if (!this.canConfirm()) {
                        return;
                    }
                    this.confirm(mapScreen, minecraft, width, height);
                }
            }
        }
    }
    
    static {
        CONNECT_MAP = (ITextComponent)new TextComponentTranslation("gui.xaero_connect_map", new Object[0]);
        DISCONNECT_MAP = (ITextComponent)new TextComponentTranslation("gui.xaero_disconnect_map", new Object[0]);
    }
}
