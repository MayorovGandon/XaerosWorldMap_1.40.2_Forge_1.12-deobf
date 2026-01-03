//Decompiled by Procyon!

package xaero.map.element;

import java.util.*;
import net.minecraft.client.*;
import xaero.map.gui.*;
import net.minecraft.init.*;
import net.minecraft.client.audio.*;
import net.minecraft.client.resources.*;
import xaero.lib.client.gui.util.*;
import xaero.map.element.render.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.gui.*;
import java.util.regex.*;

public abstract class MapElementMenuRenderer<E, C>
{
    private static final int MENU_RIGHT_PADDING = 27;
    public static final int MAX_MENU_SIZE = 10;
    public static final int MIN_MENU_SIZE = 2;
    protected final MapElementMenuScroll scrollUp;
    protected final MapElementMenuScroll scrollDown;
    protected final MapElementMenuHitbox extraHitbox;
    protected final MenuScrollReader scrollReader;
    protected final MenuHitboxReader hitboxReader;
    protected final C context;
    protected final MapElementRenderProvider<E, C> provider;
    protected ArrayList<E> filteredElements;
    private GuiTextField filterField;
    public int menuOffset;
    protected Pattern searchPattern;
    protected Pattern searchStartPattern;
    protected final Minecraft mc;
    
    protected MapElementMenuRenderer(final C context, final MapElementRenderProvider<E, C> provider) {
        this.menuOffset = 0;
        this.searchPattern = null;
        this.searchStartPattern = null;
        this.scrollUp = new MapElementMenuScroll("gui.xaero_wm_up", "\u25b3", 1);
        this.scrollDown = new MapElementMenuScroll("gui.xaero_wm_down", "\u25bd", -1);
        this.scrollReader = new MenuScrollReader();
        this.extraHitbox = new MapElementMenuHitbox(-150, 0, 177, 0);
        this.hitboxReader = new MenuHitboxReader();
        this.mc = Minecraft.func_71410_x();
        this.context = context;
        this.provider = provider;
    }
    
    public void onMapInit(final GuiMap screen, final Minecraft mc, final int width, final int height) {
        final String searchText = (this.filterField == null) ? "" : this.filterField.func_146179_b();
        (this.filterField = new GuiTextField(0, mc.field_71466_p, screen.field_146294_l - 172, this.menuStartPos(height) + 3 + this.menuSearchPadding(), 150, 20)).func_146180_a(searchText);
    }
    
    public HoveredMapElementHolder<?, ?> renderMenu(final GuiMap gui, final double scale, final int width, final int height, final int mouseX, final int mouseY, final boolean leftMousePressed, final boolean leftMouseClicked, final HoveredMapElementHolder<?, ?> oldHovered, final Minecraft mc) {
        if (this.filteredElements == null) {
            this.updateFilteredList();
        }
        final ArrayList<? extends E> elements = (ArrayList<? extends E>)this.filteredElements;
        final int menuElementCount = getMenuElementCount(this.menuStartPos(height));
        if (this.menuOffset + menuElementCount > elements.size()) {
            this.menuOffset = elements.size() - menuElementCount;
        }
        if (this.menuOffset < 0) {
            this.menuOffset = 0;
        }
        final int offset = this.menuOffset;
        Object viewed = null;
        int yPos;
        final int menuStartPos = yPos = this.menuStartPos(height);
        final int elementCount = getMenuElementCount(yPos);
        this.beforeMenuRender();
        yPos -= 8;
        viewed = this.renderMenuElement(this.scrollDown, width, yPos, mouseX, mouseY, viewed, leftMousePressed, (GuiScreen)gui, offset > 0, mc);
        yPos -= 8;
        for (int i = offset; i < elements.size(); ++i) {
            yPos -= 8;
            viewed = this.renderMenuElement(elements.get(i), width, yPos, mouseX, mouseY, viewed, leftMousePressed, (GuiScreen)gui, true, mc);
            yPos -= 8;
            if (i - offset == elementCount - 1) {
                break;
            }
        }
        yPos -= 8;
        viewed = this.renderMenuElement(this.scrollUp, width, yPos, mouseX, mouseY, viewed, leftMousePressed, (GuiScreen)gui, offset < elements.size() - elementCount, mc);
        yPos -= 8;
        if (viewed != null && leftMouseClicked) {
            Minecraft.func_71410_x().func_147118_V().func_147682_a((ISound)PositionedSoundRecord.func_184371_a(SoundEvents.field_187909_gi, 1.0f));
        }
        if (leftMousePressed && viewed instanceof MapElementMenuScroll) {
            final int direction = ((MapElementMenuScroll)viewed).scroll();
            this.menuOffset += direction;
        }
        if (viewed == null) {
            this.extraHitbox.setH(menuStartPos - yPos);
            this.extraHitbox.setY(yPos - menuStartPos);
            viewed = this.renderMenuElement(this.extraHitbox, width, menuStartPos, mouseX, mouseY, viewed, leftMousePressed, (GuiScreen)gui, true, mc);
        }
        this.afterMenuRender();
        return (oldHovered != null && oldHovered.equals(viewed)) ? oldHovered : ((viewed == null) ? null : MapElementRenderHandler.createResult(viewed, this.getAnyRenderer(viewed)));
    }
    
    protected abstract void beforeMenuRender();
    
    protected abstract void afterMenuRender();
    
    public void postMapRender(final GuiMap gui, final int scaledMouseX, final int scaledMouseY, final int width, final int height, final float partialTicks) {
        final String searchText = this.filterField.func_146179_b();
        final boolean searchFieldPlaceHolder = searchText.isEmpty() && !this.filterField.func_146206_l();
        boolean invalidRegex = false;
        if (searchFieldPlaceHolder) {
            GuiUtils.setFieldText(this.filterField, I18n.func_135052_a(this.getFilterPlaceholder(), new Object[0]), -11184811);
        }
        else if (!searchText.isEmpty() && this.searchPattern == null) {
            invalidRegex = true;
        }
        this.filterField.func_146194_f();
        if (searchFieldPlaceHolder) {
            GuiUtils.setFieldText(this.filterField, "");
        }
        else if (invalidRegex) {
            final String errorMessage = I18n.func_135052_a("gui.xaero_wm_search_invalid_regex", new Object[0]);
            gui.func_73731_b(this.mc.field_71466_p, errorMessage, width - 176 - this.mc.field_71466_p.func_78256_a(errorMessage), this.filterField.field_146210_g + 6, -43691);
        }
    }
    
    public static int getMenuElementCount(final int menuStartPos) {
        return Math.min(10, Math.max(2, (menuStartPos - 34) / 16 - 2));
    }
    
    private <O> Object renderMenuElement(final O element, final int width, final int yPos, final int mouseX, final int mouseY, Object viewed, final boolean leftMousePressed, final GuiScreen gui, final boolean enabled, final Minecraft mc) {
        final ElementReader<? super O, ?, ?> reader = (ElementReader<? super O, ?, ?>)((element == this.scrollDown || element == this.scrollUp) ? this.scrollReader : ((element == this.extraHitbox) ? this.hitboxReader : this.getAnyRenderer(element).getReader()));
        final int xPos = width - 27;
        final boolean hovered = viewed == null && reader.isMouseOverMenuElement((Object)element, xPos, yPos, mouseX, mouseY, mc);
        if (hovered) {
            viewed = element;
        }
        if (element != this.extraHitbox) {
            this.renderMenuElement(reader, (Object)element, gui, xPos, yPos, mouseX, mouseY, 1.0, enabled, hovered, mc, leftMousePressed);
        }
        return viewed;
    }
    
    public <O> void renderMenuElement(final ElementReader<O, ?, ?> reader, final O element, final GuiScreen gui, final int x, final int y, final int mouseX, final int mouseY, final double scale, final boolean enabled, final boolean hovered, final Minecraft mc, final boolean pressed) {
        GlStateManager.func_179094_E();
        if (hovered) {
            GlStateManager.func_179109_b(pressed ? 1.0f : 2.0f, 0.0f, 0.0f);
        }
        GlStateManager.func_179109_b((float)x, (float)y, 0.0f);
        GlStateManager.func_179152_a((float)scale, (float)scale, 1.0f);
        GlStateManager.func_179109_b(-4.0f, -4.0f, 0.0f);
        final String name = reader.getMenuName(element);
        final int len = mc.field_71466_p.func_78256_a(name);
        final int textX = -3 - len;
        Gui.func_73734_a(textX - 2 - reader.getMenuTextFillLeftPadding(element), -2, textX + len + 2, 11, 1996488704);
        mc.field_71466_p.func_175063_a(name, (float)textX, 0.0f, enabled ? -1 : -11184811);
        GlStateManager.func_179131_c(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.func_179147_l();
        GlStateManager.func_179109_b(4.0f, 4.0f, 0.0f);
        if (element == this.scrollUp || element == this.scrollDown) {
            this.renderScroll((MapElementMenuScroll)element, gui, mouseX, mouseY, scale, enabled, hovered, mc, pressed);
        }
        else {
            final E elementCast = (E)element;
            this.renderInMenu(elementCast, gui, mouseX, mouseY, scale, enabled, hovered, mc, pressed, textX);
        }
        GlStateManager.func_179121_F();
    }
    
    public void onMapMouseRelease(final double par1, final double par2, final int par3) {
        this.releaseScroll();
    }
    
    private void releaseScroll() {
        this.scrollUp.onMouseRelease();
        this.scrollDown.onMouseRelease();
    }
    
    private void renderScroll(final MapElementMenuScroll scroll, final GuiScreen gui, final int mouseX, final int mouseY, final double scale, final boolean enabled, final boolean hovered, final Minecraft mc, final boolean pressed) {
        if (enabled && hovered) {
            GlStateManager.func_179109_b(pressed ? 1.0f : 2.0f, 0.0f, 0.0f);
        }
        GlStateManager.func_179109_b(-4.0f, -4.0f, 0.0f);
        GlStateManager.func_179147_l();
        GlStateManager.func_179131_c(1.0f, 1.0f, 1.0f, 1.0f);
        final int color = enabled ? -1 : -11184811;
        mc.field_71466_p.func_175063_a(scroll.getIcon(), (float)(5 - mc.field_71466_p.func_78256_a(scroll.getIcon()) / 2), 1.0f, color);
        GlStateManager.func_179147_l();
    }
    
    private void updateSearch() {
        final String search = this.filterField.func_146179_b();
        try {
            this.searchPattern = Pattern.compile(search.toLowerCase());
            if (search.length() > 0) {
                if (search.charAt(0) == '^') {
                    this.searchStartPattern = this.searchPattern;
                }
                else {
                    this.searchStartPattern = Pattern.compile('^' + search.toString().toLowerCase());
                }
            }
            else {
                final Pattern pattern = null;
                this.searchStartPattern = pattern;
                this.searchPattern = pattern;
            }
        }
        catch (PatternSyntaxException e) {
            final Pattern pattern2 = null;
            this.searchStartPattern = pattern2;
            this.searchPattern = pattern2;
        }
        this.updateFilteredList();
    }
    
    public boolean charTyped() {
        if (this.filterField.func_146206_l()) {
            this.updateSearch();
            return true;
        }
        return false;
    }
    
    public boolean keyPressed(final GuiMap screen, final int keyCode) {
        if (screen.getFocused() == this.filterField && keyCode == 28) {
            this.filterField.func_146180_a("");
            this.updateSearch();
            return true;
        }
        return false;
    }
    
    public void mouseScrolled(final int direction) {
        this.scroll(direction);
    }
    
    public void tick() {
        this.filterField.func_146178_a();
    }
    
    public void unfocusAll() {
        if (this.filterField != null) {
            this.filterField.func_146195_b(false);
        }
    }
    
    public void onMenuClosed() {
        this.menuOffset = 0;
        this.searchPattern = null;
        this.searchStartPattern = null;
        this.updateFilteredList();
        this.filterField = null;
    }
    
    private void scroll(final int direction) {
        this.menuOffset += direction;
    }
    
    public Pattern getSearchPattern() {
        return this.searchPattern;
    }
    
    public Pattern getSearchStartPattern() {
        return this.searchStartPattern;
    }
    
    public void updateFilteredList() {
        final MapElementRenderProvider<E, C> provider = this.provider;
        if (provider == null) {
            this.filteredElements = null;
            return;
        }
        if (this.filteredElements == null) {
            this.filteredElements = new ArrayList<E>();
        }
        else {
            this.filteredElements.clear();
        }
        final Pattern regex = this.searchPattern;
        final Pattern regexStartsWith = this.searchStartPattern;
        this.beforeFiltering();
        provider.begin(4, this.context);
        try {
            while (provider.hasNext(4, this.context)) {
                final E e = provider.getNext(4, this.context);
                if (regex == null) {
                    this.filteredElements.add(e);
                }
                else {
                    final String filterName = this.getRenderer(e).getReader().getFilterName((Object)e).toLowerCase();
                    if (regexStartsWith.matcher(filterName).find()) {
                        this.filteredElements.add(0, e);
                    }
                    else {
                        if (!regex.matcher(filterName).find()) {
                            continue;
                        }
                        this.filteredElements.add(e);
                    }
                }
            }
        }
        finally {
            provider.end(4, this.context);
        }
    }
    
    protected <O> ElementRenderer<? super O, ?, ?> getAnyRenderer(final O element) {
        if (element == this.scrollDown || element == this.scrollUp || element == this.extraHitbox) {
            return null;
        }
        return this.getRenderer(element);
    }
    
    protected abstract ElementRenderer<? super E, ?, ?> getRenderer(final E p0);
    
    public abstract int menuStartPos(final int p0);
    
    public abstract int menuSearchPadding();
    
    public abstract void renderInMenu(final E p0, final GuiScreen p1, final int p2, final int p3, final double p4, final boolean p5, final boolean p6, final Minecraft p7, final boolean p8, final int p9);
    
    protected abstract String getFilterPlaceholder();
    
    protected abstract void beforeFiltering();
    
    public GuiTextField getFilterField() {
        return this.filterField;
    }
}
