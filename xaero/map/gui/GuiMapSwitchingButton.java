//Decompiled by Procyon!

package xaero.map.gui;

import xaero.lib.client.gui.widget.*;
import xaero.map.*;
import net.minecraft.client.gui.*;
import java.util.function.*;

public class GuiMapSwitchingButton extends GuiTexturedButton
{
    public static final Tooltip TOOLTIP;
    
    public GuiMapSwitchingButton(final boolean menuActive, final int x, final int y) {
        super(x, y, 20, 20, menuActive ? 97 : 81, 0, 16, 16, WorldMap.guiTextures, new Consumer<GuiButton>() {
            @Override
            public void accept(final GuiButton t) {
            }
        }, new Supplier<Tooltip>() {
            @Override
            public Tooltip get() {
                return GuiMapSwitchingButton.TOOLTIP;
            }
        });
    }
    
    static {
        TOOLTIP = new Tooltip("gui.xaero_box_map_switching");
    }
}
