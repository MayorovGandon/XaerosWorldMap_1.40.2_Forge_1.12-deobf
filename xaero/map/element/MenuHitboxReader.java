//Decompiled by Procyon!

package xaero.map.element;

import net.minecraft.client.*;

public class MenuHitboxReader extends MenuOnlyElementReader<MapElementMenuHitbox>
{
    public int getLeftSideLength(final MapElementMenuHitbox element, final Minecraft mc) {
        return 0;
    }
    
    public boolean isMouseOverMenuElement(final MapElementMenuHitbox element, final int menuX, final int menuY, final int mouseX, final int mouseY, final Minecraft mc) {
        final int hitboxMinX = menuX + element.getX();
        final int hitboxMinY = menuY + element.getY();
        final int hitboxMaxX = hitboxMinX + element.getW();
        final int hitboxMaxY = hitboxMinY + element.getH();
        return mouseX >= hitboxMinX && mouseX < hitboxMaxX && mouseY >= hitboxMinY && mouseY < hitboxMaxY;
    }
    
    public String getMenuName(final MapElementMenuHitbox element) {
        return "";
    }
    
    public int getMenuTextFillLeftPadding(final MapElementMenuHitbox element) {
        return 0;
    }
    
    public String getFilterName(final MapElementMenuHitbox element) {
        return this.getMenuName(element);
    }
    
    public int getRightClickTitleBackgroundColor(final MapElementMenuHitbox element) {
        return 0;
    }
    
    public boolean shouldScaleBoxWithOptionalScale() {
        return false;
    }
}
