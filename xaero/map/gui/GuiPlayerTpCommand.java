//Decompiled by Procyon!

package xaero.map.gui;

import xaero.lib.client.gui.*;
import xaero.lib.client.gui.widget.*;
import xaero.map.world.*;
import net.minecraft.util.text.*;
import xaero.map.*;
import xaero.map.common.config.option.*;
import net.minecraft.client.resources.*;
import net.minecraft.client.gui.*;
import xaero.lib.common.config.util.*;
import xaero.lib.common.config.option.*;
import org.lwjgl.input.*;
import xaero.lib.client.config.*;
import java.io.*;

public class GuiPlayerTpCommand extends ScreenBase
{
    protected String screenTitle;
    private MySmallButton confirmButton;
    private GuiTextField commandFormatTextField;
    private boolean usingDefault;
    private String commandFormat;
    private MapWorld mapWorld;
    
    public GuiPlayerTpCommand(final GuiScreen parent, final GuiScreen escape) {
        super(parent, escape, (ITextComponent)new TextComponentTranslation("gui.xaero_wm_player_teleport_command", new Object[0]));
        final WorldMapSession session = WorldMapSession.getCurrentSession();
        final MapWorld mapWorld = session.getMapProcessor().getMapWorld();
        this.usingDefault = mapWorld.isUsingDefaultPlayerTeleport();
        this.commandFormat = mapWorld.getPlayerTeleportCommandFormat();
        this.canSkipWorldRender = true;
    }
    
    public void func_73866_w_() {
        super.func_73866_w_();
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        final String defaultPlayerTeleportFormat = (String)configManager.getEffective(WorldMapProfiledConfigOptions.DEFAULT_PLAYER_TELEPORT_FORMAT);
        this.screenTitle = I18n.func_135052_a("gui.xaero_wm_player_teleport_command", new Object[0]);
        final WorldMapSession session = WorldMapSession.getCurrentSession();
        this.mapWorld = session.getMapProcessor().getMapWorld();
        (this.commandFormatTextField = new GuiTextField(0, this.field_146289_q, this.field_146294_l / 2 - 100, this.field_146295_m / 7 + 60, 200, 20)).func_146180_a(this.usingDefault ? defaultPlayerTeleportFormat : this.commandFormat);
        this.commandFormatTextField.func_146203_f(500);
        if (this.usingDefault) {
            this.commandFormatTextField.func_146193_g(-11184811);
            this.commandFormatTextField.func_146184_c(false);
        }
        else {
            this.commandFormatTextField.func_175207_a((GuiPageButtonList.GuiResponder)new GuiPageButtonList.GuiResponder() {
                public void func_175319_a(final int id, final String text) {
                    GuiPlayerTpCommand.this.commandFormat = text;
                }
                
                public void func_175320_a(final int id, final float value) {
                    this.func_175319_a(id, value + "");
                }
                
                public void func_175321_a(final int id, final boolean value) {
                    this.func_175319_a(id, value + "");
                }
            });
        }
        this.textFields.add(this.commandFormatTextField);
        this.func_189646_b((GuiButton)(this.confirmButton = new MySmallButton(200, this.field_146294_l / 2 - 155, this.field_146295_m / 6 + 168, I18n.func_135052_a("gui.xaero_confirm", new Object[0])) {
            protected void onPress() {
                if (!GuiPlayerTpCommand.this.canConfirm()) {
                    return;
                }
                if (!GuiPlayerTpCommand.this.usingDefault && GuiPlayerTpCommand.this.commandFormat.equals(defaultPlayerTeleportFormat)) {
                    GuiPlayerTpCommand.this.usingDefault = true;
                }
                GuiPlayerTpCommand.this.mapWorld.setUseDefaultPlayerTeleport(GuiPlayerTpCommand.this.usingDefault);
                GuiPlayerTpCommand.this.mapWorld.setPlayerTeleportCommandFormat(GuiPlayerTpCommand.this.commandFormat);
                GuiPlayerTpCommand.this.mapWorld.saveConfig();
                GuiPlayerTpCommand.this.goBack();
            }
        }));
        this.func_189646_b((GuiButton)new MySmallButton(201, this.field_146294_l / 2 + 5, this.field_146295_m / 6 + 168, I18n.func_135052_a("gui.xaero_cancel", new Object[0])) {
            protected void onPress() {
                GuiPlayerTpCommand.this.goBack();
            }
        });
        this.func_189646_b((GuiButton)new MySmallButton(202, this.field_146294_l / 2 - 75, this.field_146295_m / 7 + 20, ConfigUtils.optionNameValue((ITextComponent)new TextComponentTranslation("gui.xaero_wm_use_default", new Object[0]), ConfigUtils.getDisplayForBoolean((ConfigOption)null, Boolean.valueOf(this.usingDefault)))) {
            protected void onPress() {
                GuiPlayerTpCommand.this.usingDefault = !GuiPlayerTpCommand.this.usingDefault;
                GuiPlayerTpCommand.this.func_146280_a(GuiPlayerTpCommand.this.field_146297_k, GuiPlayerTpCommand.this.field_146294_l, GuiPlayerTpCommand.this.field_146295_m);
            }
        });
        Keyboard.enableRepeatEvents(true);
    }
    
    public void func_146281_b() {
        Keyboard.enableRepeatEvents(false);
    }
    
    public void func_73863_a(final int mouseX, final int mouseY, final float partial) {
        this.renderEscapeScreen(mouseX, mouseY, partial);
        this.func_146276_q_();
        this.func_73732_a(this.field_146289_q, this.screenTitle, this.field_146294_l / 2, 20, 16777215);
        this.func_73732_a(this.field_146289_q, "{x} {y} {z} {name}", this.field_146294_l / 2, this.field_146295_m / 7 + 46, -5592406);
        super.func_73863_a(mouseX, mouseY, partial);
    }
    
    private boolean canConfirm() {
        return this.commandFormat != null && this.commandFormat.length() > 0;
    }
    
    public void func_73876_c() {
        super.func_73876_c();
        this.confirmButton.field_146124_l = this.canConfirm();
    }
    
    public void func_73869_a(final char par1, final int par2) throws IOException {
        super.func_73869_a(par1, par2);
        if ((par2 == 28 || par2 == 156) && this.canConfirm()) {
            this.func_146284_a((GuiButton)this.field_146292_n.get(0));
        }
    }
}
