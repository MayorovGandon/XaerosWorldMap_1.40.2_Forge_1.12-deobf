//Decompiled by Procyon!

package xaero.map.gui;

import xaero.lib.client.gui.*;
import java.io.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.*;
import xaero.lib.client.gui.widget.dropdown.*;
import java.util.*;
import net.minecraft.client.gui.*;

public class ConfirmScreenBase extends GuiYesNo implements IScreenBase
{
    public GuiScreen parent;
    public GuiScreen escape;
    private boolean renderEscapeInBackground;
    protected boolean canSkipWorldRender;
    
    public ConfirmScreenBase(final GuiScreen parent, final GuiScreen escape, final boolean renderEscapeInBackground, final GuiYesNoCallback parentScreenIn, final String messageLine1In, final String messageLine2In, final int parentButtonClickedIdIn) {
        super(parentScreenIn, messageLine1In, messageLine2In, parentButtonClickedIdIn);
        this.parent = parent;
        this.escape = escape;
        this.renderEscapeInBackground = renderEscapeInBackground;
        this.canSkipWorldRender = true;
    }
    
    public ConfirmScreenBase(final GuiScreen parent, final GuiScreen escape, final boolean renderEscapeInBackground, final GuiYesNoCallback parentScreenIn, final String messageLine1In, final String messageLine2In, final String confirmButtonTextIn, final String cancelButtonTextIn, final int parentButtonClickedIdIn) {
        super(parentScreenIn, messageLine1In, messageLine2In, confirmButtonTextIn, cancelButtonTextIn, parentButtonClickedIdIn);
        this.parent = parent;
        this.escape = escape;
        this.renderEscapeInBackground = renderEscapeInBackground;
        this.canSkipWorldRender = true;
    }
    
    protected void onExit(final GuiScreen screen) {
        this.field_146297_k.func_147108_a(screen);
    }
    
    protected void goBack() {
        this.onExit(this.parent);
    }
    
    protected void func_73869_a(final char typedChar, final int keyCode) throws IOException {
        if (keyCode == 1) {
            this.onClose();
            if (this.field_146297_k.field_71462_r == null) {
                this.field_146297_k.func_71381_h();
            }
        }
    }
    
    public void onClose() {
        this.onExit(this.escape);
    }
    
    public void renderEscapeScreen(final int p_230430_2_, final int p_230430_3_, final float p_230430_4_) {
        if (this.escape != null) {
            this.escape.func_73863_a(p_230430_2_, p_230430_3_, p_230430_4_);
        }
        GlStateManager.func_179086_m(256);
    }
    
    public void func_73863_a(final int p_230430_2_, final int p_230430_3_, final float p_230430_4_) {
        if (this.renderEscapeInBackground) {
            this.renderEscapeScreen(p_230430_2_, p_230430_3_, p_230430_4_);
        }
        super.func_73863_a(p_230430_2_, p_230430_3_, p_230430_4_);
    }
    
    public void func_146280_a(final Minecraft p_231158_1_, final int p_231158_2_, final int p_231158_3_) {
        super.func_146280_a(p_231158_1_, p_231158_2_, p_231158_3_);
        if (this.escape != null) {
            this.escape.func_146280_a(p_231158_1_, p_231158_2_, p_231158_3_);
        }
    }
    
    public boolean shouldSkipWorldRender() {
        return this.canSkipWorldRender && this.renderEscapeInBackground && this.escape instanceof IScreenBase && ((IScreenBase)this.escape).shouldSkipWorldRender();
    }
    
    public void onDropdownOpen(final DropDownWidget menu) {
    }
    
    public void onDropdownClosed(final DropDownWidget menu) {
    }
    
    public List<GuiButton> getButtonList() {
        return (List<GuiButton>)this.field_146292_n;
    }
    
    public GuiScreen getEscape() {
        return this.escape;
    }
}
