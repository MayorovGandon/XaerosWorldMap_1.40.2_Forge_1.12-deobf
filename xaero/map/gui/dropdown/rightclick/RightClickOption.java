//Decompiled by Procyon!

package xaero.map.gui.dropdown.rightclick;

import xaero.map.gui.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.*;

public abstract class RightClickOption
{
    protected String name;
    protected int index;
    protected boolean active;
    protected IRightClickableElement target;
    protected Object[] nameFormatArgs;
    
    public RightClickOption(final String name, final int index, final IRightClickableElement target) {
        this.name = name;
        this.index = index;
        this.active = true;
        this.target = target;
        this.nameFormatArgs = new Object[0];
    }
    
    public abstract void onAction(final GuiScreen p0);
    
    public boolean onSelected(final GuiScreen screen) {
        final boolean active = this.isActive();
        if (active && this.target.isRightClickValid()) {
            this.onAction(screen);
        }
        return active;
    }
    
    protected String getName() {
        return this.name;
    }
    
    public String getDisplayName() {
        return (this.isActive() ? "" : "§8") + I18n.func_135052_a(this.getName(), this.nameFormatArgs);
    }
    
    public boolean isActive() {
        return this.active;
    }
    
    public RightClickOption setActive(final boolean isActive) {
        this.active = isActive;
        return this;
    }
    
    public RightClickOption setNameFormatArgs(final Object... nameFormatArgs) {
        this.nameFormatArgs = nameFormatArgs;
        return this;
    }
}
