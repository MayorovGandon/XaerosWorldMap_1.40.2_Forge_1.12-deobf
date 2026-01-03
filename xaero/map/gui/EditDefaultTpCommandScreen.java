//Decompiled by Procyon!

package xaero.map.gui;

import xaero.lib.client.gui.config.*;
import net.minecraft.client.gui.*;
import xaero.lib.common.config.*;
import xaero.lib.common.config.option.*;
import net.minecraft.util.text.*;
import xaero.map.common.config.option.*;
import xaero.map.mods.*;

public class EditDefaultTpCommandScreen extends EditStringConfigOptionScreen<String>
{
    private ITextComponent waypointCommandHint;
    private boolean playerFormat;
    private boolean dimensionFormat;
    
    public EditDefaultTpCommandScreen(final GuiScreen parent, final GuiScreen escape, final Config config, final Config enforcedConfig, final ConfigOption<String> option, final boolean allowEmpty, final boolean emptyMeansNull, final Runnable postConfirmAction) {
        super(parent, escape, config, enforcedConfig, (ConfigOption)option, allowEmpty, emptyMeansNull, postConfirmAction);
        this.waypointCommandHint = (ITextComponent)new TextComponentTranslation("gui.xaero_wm_teleport_command_waypoints_hint", new Object[0]);
        this.playerFormat = (option == WorldMapProfiledConfigOptions.DEFAULT_PLAYER_TELEPORT_FORMAT);
        this.dimensionFormat = (option == WorldMapProfiledConfigOptions.DEFAULT_MAP_TELEPORT_DIMENSION_FORMAT);
    }
    
    public void func_146270_b(final int tint) {
        super.func_146270_b(tint);
        if (SupportMods.minimap()) {
            this.func_73732_a(this.field_146289_q, this.waypointCommandHint.func_150254_d(), this.field_146294_l / 2, this.field_146295_m / 7 + 61, -5592406);
        }
        String hint = "{x} {y} {z}";
        if (this.playerFormat) {
            hint += " {name}";
        }
        if (this.dimensionFormat) {
            hint += " {d}";
        }
        this.func_73731_b(this.field_146289_q, hint, this.field_146294_l / 2 + 105, this.field_146295_m / 7 + 33, -5592406);
    }
}
