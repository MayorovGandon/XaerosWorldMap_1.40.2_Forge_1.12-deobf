//Decompiled by Procyon!

package xaero.map.controls;

import net.minecraft.client.settings.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import xaero.lib.client.gui.config.context.*;
import xaero.map.gui.*;
import net.minecraft.util.text.*;
import xaero.lib.client.controls.util.*;
import xaero.map.*;
import java.util.*;

public class ControlsHandler
{
    private MapProcessor mapProcessor;
    private ArrayList<KeyEvent> keyEvents;
    private ArrayList<KeyEvent> oldKeyEvents;
    
    public ControlsHandler(final MapProcessor mapProcessor) {
        this.keyEvents = new ArrayList<KeyEvent>();
        this.oldKeyEvents = new ArrayList<KeyEvent>();
        this.mapProcessor = mapProcessor;
    }
    
    public static boolean isKeyRepeat(final KeyBinding kb) {
        return kb != ControlsRegister.keyOpenMap && kb != ControlsRegister.keyOpenSettings && kb != ControlsRegister.keyOpenServerSettings && kb != ControlsRegister.keyToggleDimension;
    }
    
    public void keyDown(final KeyBinding kb, final boolean tickEnd, final boolean isRepeat) {
        final Minecraft mc = Minecraft.func_71410_x();
        if (!tickEnd) {
            if (kb == ControlsRegister.keyOpenMap) {
                mc.func_147108_a((GuiScreen)new GuiMap(null, null, this.mapProcessor, mc.func_175606_aa()));
            }
            else if (kb == ControlsRegister.keyOpenSettings) {
                mc.func_147108_a((GuiScreen)new GuiWorldMapSettings(BuiltInEditConfigScreenContexts.CLIENT));
            }
            else if (kb == ControlsRegister.keyOpenServerSettings) {
                mc.func_147108_a((GuiScreen)new GuiWorldMapSettings(BuiltInEditConfigScreenContexts.SERVER));
            }
            else if (kb == ControlsRegister.keyQuickConfirm) {
                final WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
                final MapProcessor mapProcessor = worldmapSession.getMapProcessor();
                synchronized (mapProcessor.uiPauseSync) {
                    if (!mapProcessor.isUIPaused()) {
                        mapProcessor.quickConfirmMultiworld();
                    }
                }
            }
            else if (kb == ControlsRegister.keyToggleDimension) {
                this.mapProcessor.getMapWorld().toggleDimension(!GuiScreen.func_146272_n());
                final String messageType = (this.mapProcessor.getMapWorld().getCustomDimensionId() == null) ? "gui.xaero_switched_to_current_dimension" : "gui.xaero_switched_to_dimension";
                mc.field_71456_v.func_146158_b().func_146227_a((ITextComponent)new TextComponentTranslation(messageType, new Object[] { this.mapProcessor.getMapWorld().getFutureDimension().getDropdownLabel() }));
            }
        }
    }
    
    public void keyUp(final KeyBinding kb, final boolean tickEnd) {
        if (!tickEnd) {}
    }
    
    public void handleKeyEvents() {
        final Minecraft mc = Minecraft.func_71410_x();
        this.onKeyInput(mc);
        for (int i = 0; i < this.keyEvents.size(); ++i) {
            final KeyEvent ke = this.keyEvents.get(i);
            if (mc.field_71462_r == null) {
                this.keyDown(ke.getKb(), ke.isTickEnd(), ke.isRepeat());
            }
            if (!ke.isRepeat()) {
                if (!this.oldEventExists(ke.getKb())) {
                    this.oldKeyEvents.add(ke);
                }
                this.keyEvents.remove(i);
                --i;
            }
            else if (!KeyMappingUtils.isPhysicallyDown(ke.getKb())) {
                this.keyUp(ke.getKb(), ke.isTickEnd());
                this.keyEvents.remove(i);
                --i;
            }
        }
        for (int i = 0; i < this.oldKeyEvents.size(); ++i) {
            final KeyEvent ke = this.oldKeyEvents.get(i);
            if (!KeyMappingUtils.isPhysicallyDown(ke.getKb())) {
                this.keyUp(ke.getKb(), ke.isTickEnd());
                this.oldKeyEvents.remove(i);
                --i;
            }
        }
    }
    
    public void onKeyInput(final Minecraft mc) {
        final List<KeyBinding> kbs = WorldMap.controlsRegister.keybindings;
        for (int i = 0; i < kbs.size(); ++i) {
            final KeyBinding kb = kbs.get(i);
            try {
                final boolean pressed = kb.func_151468_f();
                while (kb.func_151468_f()) {}
                if (Minecraft.func_71410_x().field_71462_r == null && !this.eventExists(kb) && pressed) {
                    this.keyEvents.add(new KeyEvent(kb, false, isKeyRepeat(kb), true));
                }
            }
            catch (Exception ex) {}
        }
    }
    
    private boolean eventExists(final KeyBinding kb) {
        for (final KeyEvent o : this.keyEvents) {
            if (o.getKb() == kb) {
                return true;
            }
        }
        return this.oldEventExists(kb);
    }
    
    private boolean oldEventExists(final KeyBinding kb) {
        for (final KeyEvent o : this.oldKeyEvents) {
            if (o.getKb() == kb) {
                return true;
            }
        }
        return false;
    }
}
