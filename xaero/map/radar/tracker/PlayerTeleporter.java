//Decompiled by Procyon!

package xaero.map.radar.tracker;

import net.minecraft.client.gui.*;
import xaero.map.world.*;
import net.minecraft.client.*;
import xaero.map.*;

public class PlayerTeleporter
{
    public void teleport(final GuiScreen screen, final MapWorld mapWorld, final String name, final int x, final int y, final int z) {
        Minecraft.func_71410_x().func_147108_a((GuiScreen)null);
        String tpCommand = mapWorld.getEffectivePlayerTeleportCommandFormat();
        tpCommand = tpCommand.replace("{name}", name).replace("{x}", "" + x).replace("{y}", "" + y).replace("{z}", "" + z);
        screen.func_175281_b(tpCommand, false);
    }
    
    public void teleportToPlayer(final GuiScreen screen, final MapWorld mapWorld, final PlayerTrackerMapElement<?> target) {
        this.teleport(screen, mapWorld, WorldMap.trackedPlayerRenderer.getReader().getMenuName((Object)target), (int)Math.floor(target.getX()), (int)Math.floor(target.getY()), (int)Math.floor(target.getZ()));
    }
}
