//Decompiled by Procyon!

package xaero.map.gui;

import net.minecraft.util.*;
import net.minecraft.client.gui.*;
import java.util.function.*;
import xaero.lib.client.gui.widget.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.*;

public class GuiTexturedButton extends TooltipButton
{
    protected int textureX;
    protected int textureY;
    protected int textureW;
    protected int textureH;
    protected ResourceLocation texture;
    private final Consumer<GuiButton> action;
    
    public GuiTexturedButton(final int x, final int y, final int w, final int h, final int textureX, final int textureY, final int textureW, final int textureH, final ResourceLocation texture, final Consumer<GuiButton> action, final Supplier<Tooltip> tooltip) {
        super(x, y, w, h, "", tooltip);
        this.textureX = textureX;
        this.textureY = textureY;
        this.textureW = textureW;
        this.textureH = textureH;
        this.texture = texture;
        this.action = action;
    }
    
    public void func_191745_a(final Minecraft mc, final int mouseX, final int mouseY, final float partialTicks) {
        Minecraft.func_71410_x().func_110434_K().func_110577_a(this.texture);
        final int iconX = this.field_146128_h + this.field_146120_f / 2 - this.textureW / 2;
        int iconY = this.field_146129_i + this.field_146121_g / 2 - this.textureH / 2;
        if (this.field_146124_l) {
            if (mouseX >= this.field_146128_h && mouseY >= this.field_146129_i && mouseX < this.field_146128_h + this.field_146120_f && mouseY < this.field_146129_i + this.field_146121_g) {
                --iconY;
                GlStateManager.func_179131_c(0.9f, 0.9f, 0.9f, 1.0f);
            }
            else {
                GlStateManager.func_179131_c(0.9882f, 0.9882f, 0.9882f, 1.0f);
            }
        }
        else {
            GlStateManager.func_179131_c(0.25f, 0.25f, 0.25f, 1.0f);
        }
        this.func_73729_b(iconX, iconY, this.textureX, this.textureY, this.textureW, this.textureH);
        GlStateManager.func_179131_c(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    protected void onPress() {
        this.action.accept((GuiButton)this);
    }
}
