//Decompiled by Procyon!

package xaero.map.element.render;

import net.minecraft.entity.*;
import net.minecraft.util.math.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.shader.*;
import net.minecraft.client.gui.*;
import xaero.map.entity.util.*;
import net.minecraft.client.*;
import xaero.map.misc.*;
import net.minecraft.world.*;

public class ElementRenderInfo
{
    public final ElementRenderLocation location;
    public final Entity renderEntity;
    public final Vec3d renderEntityPos;
    public final EntityPlayer player;
    public final Vec3d renderPos;
    public final double mouseX;
    public final double mouseZ;
    public final float brightness;
    public final double scale;
    public final double screenSizeBasedScale;
    public final boolean cave;
    public final float partialTicks;
    public final Framebuffer framebuffer;
    public final ScaledResolution scaledResolution;
    public final double renderEntityDimensionScale;
    public final int renderEntityDimension;
    public final double backgroundCoordinateScale;
    public final int mapDimension;
    
    public ElementRenderInfo(final ElementRenderLocation location, final Entity renderEntity, final EntityPlayer player, final Vec3d renderPos, final double mouseX, final double mouseZ, final double scale, final boolean cave, final float partialTicks, final float brightness, final double screenSizeBasedScale, final Framebuffer framebuffer, final ScaledResolution scaledResolution, final double backgroundCoordinateScale, final int mapDimension) {
        this.location = location;
        this.renderEntity = renderEntity;
        this.mouseX = mouseX;
        this.mouseZ = mouseZ;
        this.scale = scale;
        this.brightness = brightness;
        this.screenSizeBasedScale = screenSizeBasedScale;
        this.renderEntityPos = EntityUtil.getEntityPos(renderEntity, partialTicks);
        this.player = player;
        this.renderPos = renderPos;
        this.cave = cave;
        this.partialTicks = partialTicks;
        this.framebuffer = framebuffer;
        this.scaledResolution = scaledResolution;
        this.renderEntityDimensionScale = Misc.getDimensionTypeScale((World)Minecraft.func_71410_x().field_71441_e);
        this.renderEntityDimension = Minecraft.func_71410_x().field_71441_e.field_73011_w.getDimension();
        this.backgroundCoordinateScale = backgroundCoordinateScale;
        this.mapDimension = mapDimension;
    }
}
