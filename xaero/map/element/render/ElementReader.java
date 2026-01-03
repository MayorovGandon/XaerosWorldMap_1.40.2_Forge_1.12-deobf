//Decompiled by Procyon!

package xaero.map.element.render;

import net.minecraft.client.*;
import xaero.map.gui.*;
import java.util.*;
import xaero.map.gui.dropdown.rightclick.*;
import xaero.lib.client.gui.widget.*;

public abstract class ElementReader<E, C, R extends ElementRenderer<E, ?, R>>
{
    public abstract boolean isHidden(final E p0, final C p1);
    
    public abstract double getRenderX(final E p0, final C p1, final float p2);
    
    public abstract double getRenderZ(final E p0, final C p1, final float p2);
    
    public abstract int getInteractionBoxLeft(final E p0, final C p1, final float p2);
    
    public abstract int getInteractionBoxRight(final E p0, final C p1, final float p2);
    
    public abstract int getInteractionBoxTop(final E p0, final C p1, final float p2);
    
    public abstract int getInteractionBoxBottom(final E p0, final C p1, final float p2);
    
    public abstract int getRenderBoxLeft(final E p0, final C p1, final float p2);
    
    public abstract int getRenderBoxRight(final E p0, final C p1, final float p2);
    
    public abstract int getRenderBoxTop(final E p0, final C p1, final float p2);
    
    public abstract int getRenderBoxBottom(final E p0, final C p1, final float p2);
    
    public abstract int getLeftSideLength(final E p0, final Minecraft p1);
    
    public abstract String getMenuName(final E p0);
    
    public abstract String getFilterName(final E p0);
    
    public abstract int getMenuTextFillLeftPadding(final E p0);
    
    public abstract int getRightClickTitleBackgroundColor(final E p0);
    
    public abstract boolean shouldScaleBoxWithOptionalScale();
    
    public boolean isInteractable(final ElementRenderLocation location, final E element) {
        return false;
    }
    
    public float getBoxScale(final ElementRenderLocation location, final E element, final C context) {
        return 1.0f;
    }
    
    public boolean isMouseOverMenuElement(final E element, final int x, final int y, final int mouseX, final int mouseY, final Minecraft mc) {
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
        final int leftEdge = x - this.getLeftSideLength(element, mc);
        return mouseX >= leftEdge;
    }
    
    public boolean isHoveredOnMap(final ElementRenderLocation location, final E element, final double mouseX, final double mouseZ, final double scale, final double screenSizeBasedScale, final double rendererDimDiv, final C context, final float partialTicks) {
        double fullScale = this.getBoxScale(location, element, context);
        if (this.shouldScaleBoxWithOptionalScale()) {
            fullScale *= screenSizeBasedScale;
        }
        final double left = this.getInteractionBoxLeft(element, context, partialTicks) * fullScale;
        final double right = this.getInteractionBoxRight(element, context, partialTicks) * fullScale;
        final double top = this.getInteractionBoxTop(element, context, partialTicks) * fullScale;
        final double bottom = this.getInteractionBoxBottom(element, context, partialTicks) * fullScale;
        final double screenOffX = (mouseX - this.getRenderX(element, context, partialTicks) / rendererDimDiv) * scale;
        if (screenOffX < left || screenOffX >= right) {
            return false;
        }
        final double screenOffY = (mouseZ - this.getRenderZ(element, context, partialTicks) / rendererDimDiv) * scale;
        return screenOffY >= top && screenOffY < bottom;
    }
    
    public boolean isOnScreen(final E element, final double cameraX, final double cameraZ, final int width, final int height, final double scale, final double screenSizeBasedScale, final double rendererDimDiv, final C context, final float partialTicks) {
        final double xOnScreen = (this.getRenderX(element, context, partialTicks) / rendererDimDiv - cameraX) * scale + width / 2;
        final double zOnScreen = (this.getRenderZ(element, context, partialTicks) / rendererDimDiv - cameraZ) * scale + height / 2;
        float boxScale = this.getBoxScale(ElementRenderLocation.WORLD_MAP, element, context);
        if (this.shouldScaleBoxWithOptionalScale()) {
            boxScale *= (float)screenSizeBasedScale;
        }
        final double left = xOnScreen + this.getRenderBoxLeft(element, context, partialTicks) * boxScale;
        if (left >= width) {
            return false;
        }
        final double right = xOnScreen + this.getRenderBoxRight(element, context, partialTicks) * boxScale;
        if (right <= 0.0) {
            return false;
        }
        final double top = zOnScreen + this.getRenderBoxTop(element, context, partialTicks) * boxScale;
        if (top >= height) {
            return false;
        }
        final double bottom = zOnScreen + this.getRenderBoxBottom(element, context, partialTicks) * boxScale;
        return bottom > 0.0;
    }
    
    public ArrayList<RightClickOption> getRightClickOptions(final E element, final IRightClickableElement target) {
        return null;
    }
    
    public boolean isRightClickValid(final E element) {
        return false;
    }
    
    public Tooltip getTooltip(final E element, final C context, final boolean overMenu) {
        return null;
    }
}
