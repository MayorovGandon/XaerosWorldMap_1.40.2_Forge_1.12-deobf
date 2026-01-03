//Decompiled by Procyon!

package xaero.map.mods.gui;

import net.minecraft.client.*;
import net.minecraft.client.resources.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.*;

public class WaypointMenuScroll extends WaypointMenuElement
{
    private String name;
    private String icon;
    private int direction;
    private long lastScroll;
    
    public WaypointMenuScroll(final String name, final String icon, final int direction) {
        this.name = name;
        this.icon = icon;
        this.direction = direction;
    }
    
    public int getLeftSideLength(final Minecraft mc) {
        return 9 + mc.field_71466_p.func_78256_a(I18n.func_135052_a(this.name, new Object[0]));
    }
    
    public void renderInMenu(final GuiScreen gui, final int x, final int y, final int mouseX, final int mouseY, final double scale, final boolean enabled, final boolean hovered, final Minecraft mc, final boolean pressed) {
        GlStateManager.func_179094_E();
        if (enabled && hovered) {
            GlStateManager.func_179109_b(pressed ? 1.0f : 2.0f, 0.0f, 0.0f);
        }
        GlStateManager.func_179109_b((float)x, (float)y, 0.0f);
        if (enabled) {
            GlStateManager.func_179139_a(scale, scale, 1.0);
        }
        GlStateManager.func_179109_b(-4.0f, -4.0f, 0.0f);
        GlStateManager.func_179147_l();
        GlStateManager.func_179131_c(1.0f, 1.0f, 1.0f, 1.0f);
        final int color = enabled ? -1 : -11184811;
        mc.field_71466_p.func_175063_a(this.icon, (float)(5 - mc.field_71466_p.func_78256_a(this.icon) / 2), 1.0f, color);
        final String name = I18n.func_135052_a(this.name, new Object[0]);
        final int len = mc.field_71466_p.func_78256_a(name);
        mc.field_71466_p.func_175063_a(name, (float)(-3 - len), 0.0f, color);
        GlStateManager.func_179147_l();
        GlStateManager.func_179121_F();
    }
    
    public int getDirection() {
        return this.direction;
    }
    
    public int scroll() {
        final long currentTime = System.currentTimeMillis();
        if (this.lastScroll == 0L || currentTime - this.lastScroll > 100L) {
            this.lastScroll = currentTime;
            return this.direction;
        }
        return 0;
    }
    
    public void onMouseRelease() {
        this.lastScroll = 0L;
    }
}
