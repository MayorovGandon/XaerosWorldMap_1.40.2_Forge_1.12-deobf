//Decompiled by Procyon!

package xaero.map.element;

import net.minecraft.client.*;
import net.minecraft.client.resources.*;

public class MenuScrollReader extends MenuOnlyElementReader<MapElementMenuScroll>
{
    public int getLeftSideLength(final MapElementMenuScroll element, final Minecraft mc) {
        return 9 + mc.field_71466_p.func_78256_a(I18n.func_135052_a(element.getName(), new Object[0]));
    }
    
    public String getMenuName(final MapElementMenuScroll element) {
        return I18n.func_135052_a(element.getName(), new Object[0]);
    }
    
    public String getFilterName(final MapElementMenuScroll element) {
        return this.getMenuName(element);
    }
    
    public int getMenuTextFillLeftPadding(final MapElementMenuScroll element) {
        return 0;
    }
    
    public int getRightClickTitleBackgroundColor(final MapElementMenuScroll element) {
        return 0;
    }
    
    public boolean shouldScaleBoxWithOptionalScale() {
        return false;
    }
}
