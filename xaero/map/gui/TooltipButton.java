//Decompiled by Procyon!

package xaero.map.gui;

import java.util.function.*;
import xaero.lib.client.gui.widget.*;

public abstract class TooltipButton extends GuiActionButton
{
    public TooltipButton(final int x, final int y, final int w, final int h, final String message, final Supplier<Tooltip> tooltipSupplier) {
        super(x, y, w, h, message);
        this.tooltipSupplier = tooltipSupplier;
    }
}
