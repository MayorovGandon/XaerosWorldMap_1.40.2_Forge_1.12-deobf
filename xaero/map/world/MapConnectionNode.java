//Decompiled by Procyon!

package xaero.map.world;

public class MapConnectionNode
{
    private final int dimId;
    private final String mw;
    private String cachedString;
    
    public MapConnectionNode(final int dimId, final String mw) {
        this.dimId = dimId;
        this.mw = mw;
    }
    
    @Override
    public String toString() {
        if (this.cachedString == null) {
            this.cachedString = this.dimId + "/" + this.mw;
        }
        return this.cachedString;
    }
    
    public String getNamedString(final MapWorld mapWorld) {
        final MapDimension dim = mapWorld.getDimension(this.dimId);
        return dim.getDropdownLabel() + "/" + dim.getMultiworldName(this.mw);
    }
    
    public static MapConnectionNode fromString(final String s) {
        final int dividerIndex = s.lastIndexOf(47);
        if (dividerIndex == -1) {
            return null;
        }
        final String dimString = s.substring(0, dividerIndex);
        int dimId;
        try {
            if (dimString.equals("minecraft$overworld")) {
                dimId = 0;
            }
            else if (dimString.equals("minecraft$the_nether")) {
                dimId = -1;
            }
            else if (dimString.equals("minecraft$the_end")) {
                dimId = 1;
            }
            else {
                dimId = Integer.parseInt(dimString);
            }
        }
        catch (Throwable t) {
            return null;
        }
        final String mwString = s.substring(dividerIndex + 1);
        return new MapConnectionNode(dimId, mwString);
    }
    
    @Override
    public boolean equals(final Object another) {
        if (this == another) {
            return true;
        }
        if (another == null || !(another instanceof MapConnectionNode)) {
            return false;
        }
        final MapConnectionNode anotherNode = (MapConnectionNode)another;
        return this.dimId == anotherNode.dimId && this.mw.equals(anotherNode.mw);
    }
    
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
    
    public int getDimId() {
        return this.dimId;
    }
    
    public String getMw() {
        return this.mw;
    }
}
