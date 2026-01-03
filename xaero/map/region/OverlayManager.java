//Decompiled by Procyon!

package xaero.map.region;

import java.util.*;

public class OverlayManager
{
    private HashMap<Integer, HashMap<Byte, HashMap<Integer, HashMap<Byte, HashMap<Short, Overlay>>>>> overlayMap;
    private int numberOfUniques;
    private Object[] keyHolder;
    
    public OverlayManager() {
        this.numberOfUniques = 0;
        this.overlayMap = new HashMap<Integer, HashMap<Byte, HashMap<Integer, HashMap<Byte, HashMap<Short, Overlay>>>>>();
        this.keyHolder = new Object[5];
    }
    
    public synchronized Overlay getOriginal(final Overlay o) {
        return this.getOriginal(o, o.colourType, o.customColour);
    }
    
    public synchronized Overlay getOriginal(final Overlay o, final int colourType, final int customColour) {
        o.fillManagerKeyHolder(this.keyHolder, colourType, customColour);
        return this.getOriginal(this.overlayMap, o, colourType, customColour, 0);
    }
    
    private Overlay getOriginal(final HashMap map, Overlay o, final int colourType, final int customColour, int index) {
        Object byKey = map.get(this.keyHolder[index]);
        if (index != this.keyHolder.length - 1) {
            if (byKey == null) {
                byKey = new HashMap();
                map.put(this.keyHolder[index], byKey);
            }
            return this.getOriginal((HashMap)byKey, o, colourType, customColour, ++index);
        }
        if (byKey == null) {
            ++this.numberOfUniques;
            if (o.colourType != colourType || o.customColour != customColour) {
                final int oldOpacity = o.getOpacity();
                o = new Overlay(o.state, colourType, customColour, o.light, o.glowing);
                o.increaseOpacity(oldOpacity);
            }
            map.put(this.keyHolder[index], o);
            return o;
        }
        return (Overlay)byKey;
    }
    
    public int getNumberOfUniqueOverlays() {
        return this.numberOfUniques;
    }
}
