//Decompiled by Procyon!

package xaero.map.radar.tracker;

import net.minecraft.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.gui.*;

public class PlayerTrackerIconRenderer
{
    public void renderIcon(final EntityPlayer player, final ResourceLocation skinTextureLocation) {
        final boolean upsideDown = player != null && player.func_175148_a(EnumPlayerModelParts.CAPE) && ("Dinnerbone".equals(player.func_146103_bH().getName()) || "Grumm".equals(player.func_146103_bH().getName()));
        int textureY = 8 + (upsideDown ? 8 : 0);
        int textureH = 8 * (upsideDown ? -1 : 1);
        Minecraft.func_71410_x().func_110434_K().func_110577_a(skinTextureLocation);
        GlStateManager.func_179141_d();
        Gui.func_152125_a(-4, -4, 8.0f, (float)textureY, 8, textureH, 8, 8, 64.0f, 64.0f);
        if (player != null && player.func_175148_a(EnumPlayerModelParts.HAT)) {
            textureY = 8 + (upsideDown ? 8 : 0);
            textureH = 8 * (upsideDown ? -1 : 1);
            Gui.func_152125_a(-4, -4, 40.0f, (float)textureY, 8, textureH, 8, 8, 64.0f, 64.0f);
        }
    }
}
