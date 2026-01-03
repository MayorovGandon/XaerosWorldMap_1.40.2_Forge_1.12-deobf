//Decompiled by Procyon!

package xaero.map.icon;

public class XaeroIcon
{
    private final XaeroIconAtlas textureAtlas;
    private final int offsetX;
    private final int offsetY;
    
    public XaeroIcon(final XaeroIconAtlas textureAtlas, final int offsetX, final int offsetY) {
        this.textureAtlas = textureAtlas;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
    
    public XaeroIconAtlas getTextureAtlas() {
        return this.textureAtlas;
    }
    
    public int getOffsetX() {
        return this.offsetX;
    }
    
    public int getOffsetY() {
        return this.offsetY;
    }
}
