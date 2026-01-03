//Decompiled by Procyon!

package xaero.map.controls;

import net.minecraft.client.settings.*;
import com.google.common.collect.*;
import net.minecraftforge.fml.client.registry.*;
import xaero.common.mods.*;
import java.util.*;

public class ControlsRegister
{
    public static final KeyBinding keyOpenMap;
    public static final KeyBinding keyOpenSettings;
    public static final KeyBinding keyOpenServerSettings;
    public static final KeyBinding keyZoomIn;
    public static final KeyBinding keyZoomOut;
    public static final KeyBinding keyQuickConfirm;
    public static final KeyBinding keyToggleDimension;
    public static KeyBinding keyToggleTrackedPlayers;
    public final List<KeyBinding> keybindings;
    
    public ControlsRegister() {
        this.keybindings = (List<KeyBinding>)Lists.newArrayList((Object[])new KeyBinding[] { ControlsRegister.keyOpenMap, ControlsRegister.keyOpenSettings, ControlsRegister.keyOpenServerSettings, ControlsRegister.keyZoomIn, ControlsRegister.keyZoomOut, ControlsRegister.keyQuickConfirm, ControlsRegister.keyToggleDimension });
        for (final KeyBinding kb : this.keybindings) {
            ClientRegistry.registerKeyBinding(kb);
        }
        boolean minimapHasTrackedPlayerSupport = false;
        try {
            Class.forName("xaero.common.IXaeroMinimap");
            minimapHasTrackedPlayerSupport = (SupportXaeroWorldmap.WORLDMAP_COMPATIBILITY_VERSION >= 17);
        }
        catch (ClassNotFoundException ex) {}
        if (!minimapHasTrackedPlayerSupport) {
            ClientRegistry.registerKeyBinding(ControlsRegister.keyToggleTrackedPlayers = new KeyBinding("gui.xaero_toggle_tracked_players", 0, "Xaero's World Map"));
        }
    }
    
    static {
        keyOpenMap = new KeyBinding("gui.xaero_open_map", 50, "Xaero's World Map");
        keyOpenSettings = new KeyBinding("gui.xaero_open_settings", 27, "Xaero's World Map");
        keyOpenServerSettings = new KeyBinding("gui.xaero_world_map_server_settings", 0, "Xaero's World Map");
        keyZoomIn = new KeyBinding("gui.xaero_map_zoom_in", 0, "Xaero's World Map");
        keyZoomOut = new KeyBinding("gui.xaero_map_zoom_out", 0, "Xaero's World Map");
        keyQuickConfirm = new KeyBinding("gui.xaero_quick_confirm", 54, "Xaero's World Map");
        keyToggleDimension = new KeyBinding("gui.xaero_toggle_dimension", 0, "Xaero's World Map");
    }
}
