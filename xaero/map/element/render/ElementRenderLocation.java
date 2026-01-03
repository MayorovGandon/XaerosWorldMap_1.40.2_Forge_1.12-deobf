//Decompiled by Procyon!

package xaero.map.element.render;

import it.unimi.dsi.fastutil.ints.*;

public class ElementRenderLocation
{
    private static final Int2ObjectMap<ElementRenderLocation> ALL;
    public static final ElementRenderLocation UNKNOWN;
    public static final ElementRenderLocation IN_MINIMAP;
    public static final ElementRenderLocation OVER_MINIMAP;
    public static final ElementRenderLocation IN_WORLD;
    public static final ElementRenderLocation WORLD_MAP;
    public static final ElementRenderLocation WORLD_MAP_MENU;
    private final int index;
    
    public ElementRenderLocation(final int index) {
        this.index = index;
        ElementRenderLocation.ALL.put(index, (Object)this);
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public static ElementRenderLocation fromIndex(final int location) {
        final ElementRenderLocation result = (ElementRenderLocation)ElementRenderLocation.ALL.get(location);
        if (result == null) {
            return ElementRenderLocation.UNKNOWN;
        }
        return result;
    }
    
    static {
        ALL = (Int2ObjectMap)new Int2ObjectOpenHashMap();
        UNKNOWN = new ElementRenderLocation(-1);
        IN_MINIMAP = new ElementRenderLocation(0);
        OVER_MINIMAP = new ElementRenderLocation(1);
        IN_WORLD = new ElementRenderLocation(2);
        WORLD_MAP = new ElementRenderLocation(3);
        WORLD_MAP_MENU = new ElementRenderLocation(4);
    }
}
