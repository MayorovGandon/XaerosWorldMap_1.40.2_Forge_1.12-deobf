//Decompiled by Procyon!

package xaero.map.gui;

import java.util.function.*;
import xaero.lib.client.gui.widget.*;
import net.minecraft.client.resources.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.*;
import xaero.lib.client.gui.*;

public class ScreenSwitchSettingEntry implements ISettingEntry
{
    private String name;
    private BiFunction<GuiScreen, GuiScreen, GuiScreen> screenFactory;
    private Supplier<Tooltip> tooltipSupplier;
    private boolean active;
    private final boolean consideredAnExit;
    
    public ScreenSwitchSettingEntry(final String name, final BiFunction<GuiScreen, GuiScreen, GuiScreen> screenFactoryFromCurrentAndEscape, final Tooltip tooltip, final boolean active) {
        this(name, screenFactoryFromCurrentAndEscape, tooltip, active, true);
    }
    
    public ScreenSwitchSettingEntry(final String name, final BiFunction<GuiScreen, GuiScreen, GuiScreen> screenFactoryFromCurrentAndEscape, final Tooltip tooltip, final boolean active, final boolean consideredAnExit) {
        this.name = name;
        this.screenFactory = screenFactoryFromCurrentAndEscape;
        this.tooltipSupplier = new Supplier<Tooltip>() {
            @Override
            public Tooltip get() {
                return tooltip;
            }
        };
        this.active = active;
        this.consideredAnExit = consideredAnExit;
    }
    
    public String getStringForSearch() {
        final Tooltip entryTooltip = (this.tooltipSupplier == null) ? null : this.tooltipSupplier.get();
        final String tooltipFullCode = (entryTooltip == null) ? null : entryTooltip.getFullCode();
        return I18n.func_135052_a(this.name, new Object[0]) + " " + this.name.replace("gui.xaero", "") + ((tooltipFullCode != null) ? (" " + tooltipFullCode.replace("gui.xaero", "")) : "") + ((entryTooltip != null) ? (" " + entryTooltip.getPlainText()) : "");
    }
    
    public GuiButton createWidget(final int x, final int y, final int w, final int width) {
        final TooltipButton button = new TooltipButton(x, y, w, 20, I18n.func_135052_a(this.name, new Object[0]), this.tooltipSupplier) {
            protected void onPress() {
                final Minecraft mc = Minecraft.func_71410_x();
                final GuiScreen current = mc.field_71462_r;
                final GuiScreen currentEscScreen = (current instanceof ScreenBase) ? ((ScreenBase)current).escape : null;
                final GuiScreen targetScreen = ScreenSwitchSettingEntry.this.screenFactory.apply(current, currentEscScreen);
                if (ScreenSwitchSettingEntry.this.consideredAnExit && current instanceof ScreenBase) {
                    ((ScreenBase)current).onExit(targetScreen);
                }
                else {
                    mc.func_147108_a(targetScreen);
                }
            }
        };
        button.field_146124_l = this.active;
        return (GuiButton)button;
    }
    
    public BiFunction<GuiScreen, GuiScreen, GuiScreen> getScreenFactory() {
        return this.screenFactory;
    }
}
