//Decompiled by Procyon!

package xaero.map.mods.gui;

import net.minecraft.util.*;
import xaero.map.animation.*;
import net.minecraft.client.*;
import net.minecraft.client.resources.*;

public class Waypoint implements Comparable<Waypoint>
{
    private Object original;
    public static final ResourceLocation minimapTextures;
    public static final int white = -1;
    private int x;
    private int y;
    private int z;
    private String text;
    private String symbol;
    private int color;
    private boolean disabled;
    private int type;
    private boolean rotation;
    private int yaw;
    private float destAlpha;
    private float alpha;
    private SlowingAnimation alphaAnim;
    private boolean editable;
    private boolean temporary;
    private boolean global;
    private String setName;
    private boolean yIncluded;
    private double dimDiv;
    private int cachedNameLength;
    
    public Waypoint(final Object original, final int x, final int y, final int z, final String name, final String symbol, final int color, final int type, final boolean editable, final String setName, final boolean yIncluded, final double dimDiv) {
        this.disabled = false;
        this.type = 0;
        this.rotation = false;
        this.yaw = 0;
        this.destAlpha = 0.0f;
        this.alpha = 0.0f;
        this.alphaAnim = null;
        this.original = original;
        this.x = x;
        this.y = y;
        this.z = z;
        this.symbol = symbol;
        this.color = color;
        this.type = type;
        this.text = name;
        this.editable = editable;
        this.setName = setName;
        this.yIncluded = yIncluded;
        this.dimDiv = dimDiv;
        this.cachedNameLength = Minecraft.func_71410_x().field_71466_p.func_78256_a(this.getName());
    }
    
    public String getName() {
        return I18n.func_135052_a(this.text, new Object[0]);
    }
    
    @Override
    public int compareTo(final Waypoint arg0) {
        return (this.z > arg0.z) ? 1 : ((this.z != arg0.z) ? -1 : 0);
    }
    
    @Override
    public String toString() {
        return this.getName();
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public int getZ() {
        return this.z;
    }
    
    public boolean isDisabled() {
        return this.disabled;
    }
    
    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }
    
    public int getType() {
        return this.type;
    }
    
    public int getYaw() {
        return this.yaw;
    }
    
    public void setYaw(final int yaw) {
        this.yaw = yaw;
    }
    
    public boolean isRotation() {
        return this.rotation;
    }
    
    public void setRotation(final boolean rotation) {
        this.rotation = rotation;
    }
    
    public boolean isEditable() {
        return this.editable;
    }
    
    public Object getOriginal() {
        return this.original;
    }
    
    public String getSymbol() {
        return this.symbol;
    }
    
    public void setTemporary(final boolean temporary) {
        this.temporary = temporary;
    }
    
    public void setGlobal(final boolean global) {
        this.global = global;
    }
    
    public String getSetName() {
        return this.setName;
    }
    
    public String getComparisonName() {
        String comparisonName = this.getName().toLowerCase().trim();
        if (comparisonName.startsWith("the ")) {
            comparisonName = comparisonName.substring(4);
        }
        if (comparisonName.startsWith("a ")) {
            comparisonName = comparisonName.substring(2);
        }
        return comparisonName;
    }
    
    public int getColor() {
        return this.color;
    }
    
    public boolean isGlobal() {
        return this.global;
    }
    
    public double getRenderX() {
        if (this.dimDiv == 1.0) {
            return this.x + 0.5;
        }
        return Math.floor(this.x / this.dimDiv) + 0.5;
    }
    
    public double getRenderZ() {
        if (this.dimDiv == 1.0) {
            return this.z + 0.5;
        }
        return Math.floor(this.z / this.dimDiv) + 0.5;
    }
    
    public boolean isTemporary() {
        return this.temporary;
    }
    
    public float getDestAlpha() {
        return this.destAlpha;
    }
    
    public void setDestAlpha(final float destAlpha) {
        this.destAlpha = destAlpha;
    }
    
    public SlowingAnimation getAlphaAnim() {
        return this.alphaAnim;
    }
    
    public void setAlphaAnim(final SlowingAnimation alphaAnim) {
        this.alphaAnim = alphaAnim;
    }
    
    public float getAlpha() {
        return this.alpha;
    }
    
    public void setAlpha(final float alpha) {
        this.alpha = alpha;
    }
    
    public boolean isyIncluded() {
        return this.yIncluded;
    }
    
    public int getCachedNameLength() {
        return this.cachedNameLength;
    }
    
    static {
        minimapTextures = new ResourceLocation("xaerobetterpvp", "gui/guis.png");
    }
}
