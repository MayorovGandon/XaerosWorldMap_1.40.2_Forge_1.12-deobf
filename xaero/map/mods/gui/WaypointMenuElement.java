//Decompiled by Procyon!

package xaero.map.mods.gui;

import net.minecraft.client.*;
import net.minecraft.client.gui.*;

public abstract class WaypointMenuElement
{
    public abstract int getLeftSideLength(final Minecraft p0);
    
    public abstract void renderInMenu(final GuiScreen p0, final int p1, final int p2, final int p3, final int p4, final double p5, final boolean p6, final boolean p7, final Minecraft p8, final boolean p9);
    
    public boolean isMouseOverElement(final int x, final int y, final int mouseX, final int mouseY, final Minecraft mc) {
        final int topEdge = y - 8;
        if (mouseY < topEdge) {
            return false;
        }
        final int bottomEdge = y + 8;
        if (mouseY >= bottomEdge) {
            return false;
        }
        final int rightEdge = x + 5;
        if (mouseX >= rightEdge) {
            return false;
        }
        final int leftEdge = x - this.getLeftSideLength(mc);
        return mouseX >= leftEdge;
    }
}
