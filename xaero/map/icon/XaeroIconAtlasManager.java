//Decompiled by Procyon!

package xaero.map.icon;

import net.minecraft.client.renderer.*;
import java.util.*;

public class XaeroIconAtlasManager
{
    private final int iconWidth;
    private final int atlasTextureSize;
    private final List<XaeroIconAtlas> atlases;
    private int currentAtlasIndex;
    
    public XaeroIconAtlasManager(final int iconWidth, final int atlasTextureSize, final List<XaeroIconAtlas> atlases) {
        this.iconWidth = iconWidth;
        this.atlasTextureSize = atlasTextureSize;
        this.atlases = atlases;
        this.currentAtlasIndex = -1;
    }
    
    public void clearAtlases() {
        for (final XaeroIconAtlas entityIconAtlas : this.atlases) {
            GlStateManager.func_179150_h(entityIconAtlas.getTextureId());
        }
        this.currentAtlasIndex = -1;
        this.atlases.clear();
    }
    
    public XaeroIconAtlas getCurrentAtlas() {
        if (this.currentAtlasIndex < 0 || this.atlases.get(this.currentAtlasIndex).isFull()) {
            this.atlases.add(XaeroIconAtlas.Builder.begin().setWidth(this.atlasTextureSize).setIconWidth(this.iconWidth).build());
            this.currentAtlasIndex = this.atlases.size() - 1;
        }
        return this.atlases.get(this.currentAtlasIndex);
    }
}
