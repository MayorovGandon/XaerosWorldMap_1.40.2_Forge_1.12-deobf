//Decompiled by Procyon!

package xaero.map.gui.dropdown.rightclick;

import net.minecraft.client.gui.*;
import xaero.lib.client.gui.widget.dropdown.*;
import xaero.map.gui.*;
import java.util.*;
import java.util.function.*;

public class GuiRightClickMenu extends DropDownWidget
{
    private IRightClickableElement target;
    private ArrayList<RightClickOption> actionOptions;
    private GuiScreen screen;
    private boolean removed;
    
    private GuiRightClickMenu(final IRightClickableElement target, final ArrayList<RightClickOption> options, final GuiScreen screen, final int x, final int y, final int w, final int titleBackgroundColor, final IDropDownContainer container) {
        super(convertOptions(options), x - (shouldOpenLeft(options.size(), x, w, screen.field_146294_l) ? w : 0), y, w, Integer.valueOf(-1), false, (IDropDownWidgetCallback)null, container, false);
        this.openingUp = false;
        this.target = target;
        this.screen = screen;
        this.setClosed(false);
        this.actionOptions = options;
        this.selectedHoveredBackground = titleBackgroundColor;
        this.selectedBackground = titleBackgroundColor;
        this.shortenFromTheRight = true;
    }
    
    private static boolean shouldOpenLeft(final int optionCount, final int x, final int w, final int screenWidth) {
        return x + w - screenWidth > 0;
    }
    
    private static boolean shouldOpenUp(final int optionCount, final int y, final int screenHeight) {
        final int potentialHeight = 11 * optionCount;
        return y + potentialHeight - screenHeight > potentialHeight / 2;
    }
    
    public void setClosed(final boolean closed) {
        if (!this.isClosed() && closed) {
            this.removed = true;
        }
        super.setClosed(closed);
    }
    
    public void selectId(final int id, final boolean callCallback) {
        if (id == -1) {
            return;
        }
        if (this.removed) {
            return;
        }
        this.actionOptions.get(id).onSelected(this.screen);
        this.setClosed(true);
    }
    
    public static GuiRightClickMenu getMenu(final IRightClickableElement rightClickable, final GuiMap screen, final int x, final int y, final int w) {
        return new GuiRightClickMenu(rightClickable, rightClickable.getRightClickOptions(), (GuiScreen)screen, x, y, w, rightClickable.getRightClickTitleBackgroundColor(), (IDropDownContainer)screen);
    }
    
    public IRightClickableElement getTarget() {
        return this.target;
    }
    
    private static String[] convertOptions(final ArrayList<RightClickOption> options) {
        final Supplier<ArrayList<Object>> factory = new Supplier<ArrayList<Object>>() {
            @Override
            public ArrayList<Object> get() {
                return new ArrayList<Object>();
            }
        };
        final BiConsumer<ArrayList<Object>, Object> accumulator = new BiConsumer<ArrayList<Object>, Object>() {
            @Override
            public void accept(final ArrayList<Object> t, final Object u) {
                t.add(u);
            }
        };
        final BiConsumer<ArrayList<Object>, ArrayList<Object>> combiner = new BiConsumer<ArrayList<Object>, ArrayList<Object>>() {
            @Override
            public void accept(final ArrayList<Object> t, final ArrayList<Object> u) {
                t.addAll(u);
            }
        };
        return options.stream().map((Function<? super Object, ?>)new Function<RightClickOption, Object>() {
            @Override
            public Object apply(final RightClickOption o) {
                return o.getDisplayName();
            }
        }).collect(factory, accumulator, combiner).toArray(new String[0]);
    }
}
