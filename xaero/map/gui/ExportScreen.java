//Decompiled by Procyon!

package xaero.map.gui;

import xaero.map.file.export.*;
import net.minecraft.util.text.*;
import xaero.lib.common.gui.widget.*;
import xaero.lib.common.config.util.*;
import java.util.function.*;
import xaero.lib.client.gui.*;
import xaero.map.config.primary.option.*;
import xaero.lib.common.config.option.*;
import net.minecraft.client.gui.*;
import java.util.*;
import xaero.lib.client.config.option.ui.*;
import xaero.map.*;
import xaero.lib.common.config.*;
import xaero.lib.common.config.channel.*;

public class ExportScreen extends GuiSettings
{
    private static final ITextComponent EXPORTING_MESSAGE;
    private final MapProcessor mapProcessor;
    private PNGExportResult result;
    private int stage;
    private final MapTileSelection selection;
    public boolean fullExport;
    
    public ExportScreen(final GuiScreen backScreen, final GuiScreen escScreen, final MapProcessor mapProcessor, final MapTileSelection selection) {
        super((ITextComponent)new TextComponentTranslation("gui.xaero_export_screen", new Object[0]), backScreen, escScreen);
        this.mapProcessor = mapProcessor;
        this.selection = selection;
        final ISettingEntry fullExportEntry = (ISettingEntry)new CustomSettingEntry((BooleanSupplier)new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() {
                return false;
            }
        }, (ITextComponent)new TextComponentTranslation("gui.xaero_export_option_full", new Object[0]), new TooltipInfo("gui.xaero_box_export_option_full"), false, (Supplier)new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return ExportScreen.this.fullExport;
            }
        }, 0, 1, (IntFunction)new IntFunction<Boolean>() {
            @Override
            public Boolean apply(final int i) {
                return i == 1;
            }
        }, (Function)new Function<Boolean, ITextComponent>() {
            @Override
            public ITextComponent apply(final Boolean v) {
                return v ? ConfigConstants.ON : ConfigConstants.OFF;
            }
        }, (BiConsumer)new BiConsumer<Boolean, Boolean>() {
            @Override
            public void accept(final Boolean o, final Boolean n) {
                ExportScreen.this.fullExport = n;
            }
        }, (BooleanSupplier)new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() {
                return true;
            }
        });
        this.entries = new ISettingEntry[] { fullExportEntry, (ISettingEntry)this.primaryOptionEntry((xaero.lib.common.config.option.ConfigOption<Object>)WorldMapPrimaryClientConfigOptions.EXPORT_MULTIPLE_IMAGES), (ISettingEntry)this.primaryOptionEntry((xaero.lib.common.config.option.ConfigOption<Object>)WorldMapPrimaryClientConfigOptions.NIGHT_EXPORT), (ISettingEntry)this.primaryOptionEntry((xaero.lib.common.config.option.ConfigOption<Object>)WorldMapPrimaryClientConfigOptions.EXPORT_HIGHLIGHTS), (ISettingEntry)this.primaryOptionEntry((xaero.lib.common.config.option.ConfigOption<Object>)WorldMapPrimaryClientConfigOptions.EXPORT_SCALE_DOWN_SQUARE) };
        this.canSearch = false;
        this.confirmButton = true;
        this.canSkipWorldRender = true;
    }
    
    public void func_73866_w_() {
        if (this.stage > 0) {
            return;
        }
        super.func_73866_w_();
    }
    
    protected void confirm() {
        this.stage = 1;
        this.func_146280_a(this.field_146297_k, this.field_146294_l, this.field_146295_m);
    }
    
    public void func_73863_a(final int par1, final int par2, final float par3) {
        this.renderEscapeScreen(par1, par2, par3);
        super.func_73863_a(par1, par2, par3);
        if (this.result != null) {
            this.func_73732_a(this.field_146297_k.field_71466_p, this.result.getMessage().func_150254_d(), this.field_146294_l / 2, this.field_146295_m / 7 + 29 + 96, -1);
        }
        if (this.stage > 0) {
            this.func_73732_a(this.field_146297_k.field_71466_p, ExportScreen.EXPORTING_MESSAGE.func_150254_d(), this.field_146294_l / 2, this.field_146295_m / 6 + 68, -1);
            if (this.stage == 1) {
                this.stage = 2;
                return;
            }
        }
        if (this.stage != 2) {
            return;
        }
        if (this.mapProcessor.getMapSaveLoad().exportPNG(this, this.fullExport ? null : this.selection)) {
            this.stage = 3;
            this.result = null;
            for (final GuiButton c : this.field_146292_n) {
                c.field_146124_l = false;
            }
            return;
        }
        this.stage = 0;
        this.func_146280_a(this.field_146297_k, this.field_146294_l, this.field_146295_m);
    }
    
    public <T> ConfigOptionScreenEntry<T> primaryOptionEntry(final ConfigOption<T> option) {
        final ConfigChannel channel = WorldMap.INSTANCE.getConfigs();
        return (ConfigOptionScreenEntry<T>)new ConfigOptionScreenEntry((ConfigOption)option, (Supplier)new Supplier<Config>() {
            @Override
            public Config get() {
                return channel.getPrimaryClientConfigManager().getConfig();
            }
        }, (Supplier)new Supplier<Config>() {
            @Override
            public Config get() {
                return null;
            }
        }, (Runnable)new Runnable() {
            @Override
            public void run() {
                channel.getPrimaryClientConfigManagerIO().save();
            }
        }, channel, true, false);
    }
    
    public void onExportDone(final PNGExportResult result) {
        this.result = result;
        this.stage = 0;
    }
    
    public MapTileSelection getSelection() {
        return this.selection;
    }
    
    static {
        EXPORTING_MESSAGE = (ITextComponent)new TextComponentTranslation("gui.xaero_export_screen_exporting", new Object[0]);
    }
}
