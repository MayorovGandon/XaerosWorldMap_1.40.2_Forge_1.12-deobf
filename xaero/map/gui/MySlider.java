//Decompiled by Procyon!

package xaero.map.gui;

import net.minecraft.client.gui.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.*;
import org.lwjgl.input.*;
import net.minecraft.util.math.*;

public abstract class MySlider extends GuiButton
{
    protected double sliderValue;
    protected boolean dragging;
    protected int scaledScreenWidth;
    
    public MySlider(final int p_i45017_1_, final int p_i45017_2_, final int p_i45017_3_, final int w, final int h, final float p_i45017_5_, final float p_i45017_6_, final int scaledScreenWidth) {
        super(p_i45017_1_, p_i45017_2_, p_i45017_3_, w, h, "");
        this.sliderValue = 1.0;
        this.scaledScreenWidth = scaledScreenWidth;
    }
    
    protected int func_146114_a(final boolean mouseOver) {
        return 0;
    }
    
    protected void func_146119_b(final Minecraft mc, final int mouseX, final int mouseY) {
        if (this.field_146125_m) {
            if (this.dragging) {
                this.updateValue(mc, mouseX);
            }
            mc.func_110434_K().func_110577_a(MySlider.field_146122_a);
            GlStateManager.func_179131_c(1.0f, 1.0f, 1.0f, 1.0f);
            this.func_73729_b(this.field_146128_h + (int)(this.sliderValue * (float)(this.field_146120_f - 8)), this.field_146129_i, 0, 66, 4, 20);
            this.func_73729_b(this.field_146128_h + (int)(this.sliderValue * (float)(this.field_146120_f - 8)) + 4, this.field_146129_i, 196, 66, 4, 20);
        }
    }
    
    public boolean func_146116_c(final Minecraft mc, final int mouseX, final int mouseY) {
        if (super.func_146116_c(mc, mouseX, mouseY)) {
            this.updateValue(mc, mouseX);
            return this.dragging = true;
        }
        return false;
    }
    
    private void updateValue(final Minecraft mc, final int mouseX) {
        final double actualMouseX = Mouse.getX() / (double)mc.field_71443_c * this.scaledScreenWidth;
        final double partialMouseX = actualMouseX - (int)actualMouseX;
        final double finalMouseX = mouseX + partialMouseX;
        this.sliderValue = (finalMouseX - (this.field_146128_h + 4)) / (this.field_146120_f - 8);
        this.sliderValue = MathHelper.func_151237_a(this.sliderValue, 0.0, 1.0);
        this.applyValue();
        this.updateMessage();
    }
    
    protected abstract void applyValue();
    
    protected abstract void updateMessage();
    
    public void func_146118_a(final int mouseX, final int mouseY) {
        this.dragging = false;
    }
}
