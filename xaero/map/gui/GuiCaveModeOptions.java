//Decompiled by Procyon!

package xaero.map.gui;

import xaero.map.world.*;
import xaero.map.config.util.*;
import net.minecraft.client.resources.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import xaero.map.*;
import xaero.map.config.primary.option.*;
import xaero.lib.common.config.*;
import net.minecraft.util.math.*;
import java.util.function.*;
import xaero.lib.client.gui.widget.*;
import net.minecraft.util.text.*;
import xaero.lib.client.gui.util.*;

public class GuiCaveModeOptions
{
    private MapDimension dimension;
    private boolean enabled;
    private GuiButton caveModeStartSlider;
    private GuiTextField caveModeStartField;
    private String caveModeStartFieldPlaceholder;
    
    public void onInit(final GuiMap screen, final MapProcessor mapProcessor) {
        this.caveModeStartSlider = null;
        this.caveModeStartField = null;
        this.dimension = mapProcessor.getMapWorld().getFutureDimension();
        this.enabled = (this.enabled && this.dimension != null && WorldMapClientConfigUtils.getEffectiveCaveModeAllowed());
        if (this.enabled && this.dimension != null) {
            this.updateSlider(screen);
            this.updateField(screen);
            final Tooltip caveModeTypeButtonTooltip = new Tooltip("gui.xaero_wm_box_cave_mode_type");
            screen.addGuiButton((GuiButton)new TooltipButton(20, screen.field_146295_m - 62, 150, 20, this.getCaveModeTypeButtonMessage().func_150254_d(), new Supplier<Tooltip>() {
                @Override
                public Tooltip get() {
                    return caveModeTypeButtonTooltip;
                }
            }) {
                public void onPress() {
                    GuiCaveModeOptions.this.onCaveModeTypeButton((GuiButton)this, screen);
                }
            });
        }
        this.caveModeStartFieldPlaceholder = I18n.func_135052_a("gui.xaero_wm_cave_mode_start_auto", new Object[0]);
    }
    
    private void onCaveModeTypeButton(final GuiButton b, final GuiMap screen) {
        this.dimension.toggleCaveModeType(true);
        synchronized (screen.getMapProcessor().uiSync) {
            this.dimension.saveConfigUnsynced();
        }
        b.field_146126_j = this.getCaveModeTypeButtonMessage().func_150254_d();
    }
    
    private GuiTextField createField(final GuiMap screen) {
        final GuiTextField field = new GuiTextField(0, Minecraft.func_71410_x().field_71466_p, 172, screen.field_146295_m - 40, 50, 20);
        field.func_146203_f(7);
        final int initialCaveModeStart = this.getCaveStart();
        field.func_146180_a((initialCaveModeStart == Integer.MAX_VALUE) ? "" : (initialCaveModeStart + ""));
        field.func_175207_a((GuiPageButtonList.GuiResponder)new GuiPageButtonList.GuiResponder() {
            public void func_175319_a(final int id, final String text) {
                try {
                    GuiCaveModeOptions.this.setCaveStart((text.isEmpty() || text.equalsIgnoreCase("auto")) ? Integer.MAX_VALUE : Integer.parseInt(text));
                    GuiCaveModeOptions.this.updateSlider(screen);
                }
                catch (NumberFormatException ex) {}
            }
            
            public void func_175320_a(final int id, final float value) {
            }
            
            public void func_175321_a(final int id, final boolean value) {
            }
        });
        return field;
    }
    
    private int getCaveStart() {
        final Config primaryConfig = WorldMap.INSTANCE.getConfigs().getPrimaryClientConfigManager().getConfig();
        return (int)primaryConfig.get(WorldMapPrimaryClientConfigOptions.CAVE_MODE_START);
    }
    
    private void setCaveStart(final int y) {
        final Config primaryConfig = WorldMap.INSTANCE.getConfigs().getPrimaryClientConfigManager().getConfig();
        primaryConfig.set(WorldMapPrimaryClientConfigOptions.CAVE_MODE_START, (Object)y);
        WorldMap.INSTANCE.getConfigs().getPrimaryClientConfigManagerIO().save();
    }
    
    private GuiButton createSlider(final GuiMap screen) {
        final String displayName = I18n.func_135052_a("gui.xaero_wm_cave_mode_start", new Object[0]);
        final Supplier<String> labelGetter = new Supplier<String>() {
            @Override
            public String get() {
                return displayName;
            }
        };
        final int initialCaveStart = this.getCaveStart();
        final int minY = 0;
        final int maxY = 255;
        final int minOption = minY - 1;
        final int range = maxY - minOption;
        final double initialSliderValue = MathHelper.func_151237_a((initialCaveStart - minOption) / (double)range, 0.0, 1.0);
        return (GuiButton)new XaeroSliderWidget(20, screen.field_146295_m - 40, 150, 20, (String)labelGetter.get(), initialSliderValue, (DoubleConsumer)new DoubleConsumer() {
            @Override
            public void accept(final double newSliderValue) {
                int selectedY = (int)Math.round(newSliderValue * range) + minOption;
                if (selectedY == minOption) {
                    selectedY = Integer.MAX_VALUE;
                }
                GuiCaveModeOptions.this.setCaveStart(selectedY);
                screen.onCaveModeStartSet();
            }
        }, (Supplier)labelGetter, screen.field_146294_l);
    }
    
    private void updateField(final GuiMap screen) {
        this.caveModeStartField = this.createField(screen);
    }
    
    private void updateSlider(final GuiMap screen) {
        if (this.caveModeStartSlider == null) {
            screen.addGuiButton(this.caveModeStartSlider = this.createSlider(screen));
        }
        else {
            screen.replaceWidget(this.caveModeStartSlider, this.caveModeStartSlider = this.createSlider(screen));
        }
    }
    
    public void toggle(final GuiMap screen) {
        this.enabled = (WorldMapClientConfigUtils.getEffectiveCaveModeAllowed() && !this.enabled);
        screen.func_146280_a(Minecraft.func_71410_x(), screen.field_146294_l, screen.field_146295_m);
    }
    
    public void onCaveModeStartSet(final GuiMap screen) {
        if (this.enabled) {
            this.updateField(screen);
        }
    }
    
    public void tick(final GuiMap screen) {
        if (this.enabled) {
            this.caveModeStartField.func_146178_a();
        }
    }
    
    public void unfocusAll() {
        if (this.caveModeStartField != null) {
            this.caveModeStartField.func_146195_b(false);
        }
    }
    
    private ITextComponent getCaveModeTypeButtonMessage() {
        return (ITextComponent)new TextComponentString(I18n.func_135052_a("gui.xaero_wm_cave_mode_type", new Object[0]) + ": " + I18n.func_135052_a((this.dimension == null) ? "N/A" : ((this.dimension.getCaveModeType() == 0) ? "gui.xaero_off" : ((this.dimension.getCaveModeType() == 1) ? "gui.xaero_wm_cave_mode_type_layered" : "gui.xaero_wm_cave_mode_type_full")), new Object[0]));
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public GuiTextField getCaveModeStartField() {
        return this.caveModeStartField;
    }
    
    public void postMapRender(final GuiMap gui, final int scaledMouseX, final int scaledMouseY, final int width, final int height, final float partialTicks) {
        if (this.enabled) {
            final boolean placeholder = this.caveModeStartField.func_146179_b().isEmpty();
            if (placeholder) {
                GuiUtils.setFieldText(this.caveModeStartField, this.caveModeStartFieldPlaceholder, -11184811);
                this.caveModeStartField.func_146190_e(0);
            }
            this.caveModeStartField.func_146194_f();
            if (placeholder) {
                GuiUtils.setFieldText(this.caveModeStartField, "");
            }
        }
    }
}
