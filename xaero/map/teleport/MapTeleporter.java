//Decompiled by Procyon!

package xaero.map.teleport;

import net.minecraft.client.gui.*;
import net.minecraft.client.*;
import net.minecraft.client.resources.*;
import xaero.map.*;
import xaero.map.common.config.option.*;
import xaero.lib.common.config.option.*;
import xaero.map.world.*;
import net.minecraft.util.text.*;
import xaero.lib.client.config.*;

public class MapTeleporter
{
    public void teleport(final GuiScreen screen, final MapWorld mapWorld, final int x, final int y, final int z, final Integer d) {
        Minecraft.func_71410_x().func_147108_a((GuiScreen)null);
        if (Minecraft.func_71410_x().field_71442_b.func_78763_f()) {
            final MapDimension destinationDim = mapWorld.getDimension((d != null) ? ((int)d) : Minecraft.func_71410_x().field_71441_e.field_73011_w.getDimension());
            final MapConnectionNode playerMapKey = mapWorld.getPlayerMapKey();
            if (playerMapKey == null) {
                final TextComponentBase messageComponent = (TextComponentBase)new TextComponentString(I18n.func_135052_a("gui.xaero_wm_teleport_never_confirmed", new Object[0]));
                messageComponent.func_150255_a(messageComponent.func_150256_b().func_150238_a(TextFormatting.RED));
                Minecraft.func_71410_x().field_71456_v.func_146158_b().func_146227_a((ITextComponent)messageComponent);
                return;
            }
            final MapConnectionNode destinationMapKey = (destinationDim == null) ? null : destinationDim.getSelectedMapKeyUnsynced();
            if (!mapWorld.getMapConnections().isConnected(playerMapKey, destinationMapKey)) {
                final TextComponentBase messageComponent2 = (TextComponentBase)new TextComponentString(I18n.func_135052_a("gui.xaero_wm_teleport_not_connected", new Object[0]));
                messageComponent2.func_150255_a(messageComponent2.func_150256_b().func_150238_a(TextFormatting.RED));
                Minecraft.func_71410_x().field_71456_v.func_146158_b().func_146227_a((ITextComponent)messageComponent2);
                return;
            }
        }
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        final boolean partialYTeleportConfig = (boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.PARTIAL_Y_TELEPORT);
        String tpCommand = (d == null) ? mapWorld.getEffectiveTeleportCommandFormat() : mapWorld.getEffectiveDimensionTeleportCommandFormat();
        final String yString = (y == -1) ? "~" : (partialYTeleportConfig ? (y + 0.5 + "") : (y + ""));
        tpCommand = tpCommand.replace("{x}", "" + x).replace("{y}", yString).replace("{z}", "" + z);
        if (d != null) {
            tpCommand = tpCommand.replace("{d}", d + "");
        }
        screen.func_175281_b(tpCommand, false);
        mapWorld.setCustomDimensionId(null);
        mapWorld.getMapProcessor().checkForWorldUpdate();
    }
}
