//Decompiled by Procyon!

package xaero.map.element;

public class MapElementMenuHitbox
{
    private int x;
    private int y;
    private int w;
    private int h;
    
    public MapElementMenuHitbox(final int x, final int y, final int w, final int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public int getW() {
        return this.w;
    }
    
    public int getH() {
        return this.h;
    }
    
    public void setY(final int y) {
        this.y = y;
    }
    
    public void setH(final int h) {
        this.h = h;
    }
}
