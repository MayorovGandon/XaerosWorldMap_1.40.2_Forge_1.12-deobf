//Decompiled by Procyon!

package xaero.map.icon;

import net.minecraft.client.renderer.*;
import xaero.map.exception.*;
import org.lwjgl.opengl.*;
import java.nio.*;
import xaero.map.*;

public final class XaeroIconAtlas
{
    private final int textureId;
    private final int width;
    private int currentIndex;
    private final int iconWidth;
    private final int sideIconCount;
    private final int maxIconCount;
    
    private XaeroIconAtlas(final int textureId, final int width, final int iconWidth) {
        this.textureId = textureId;
        this.width = width;
        this.iconWidth = iconWidth;
        this.sideIconCount = width / iconWidth;
        this.maxIconCount = this.sideIconCount * this.sideIconCount;
    }
    
    public int getTextureId() {
        return this.textureId;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getCurrentIndex() {
        return this.currentIndex;
    }
    
    public boolean isFull() {
        return this.currentIndex >= this.maxIconCount;
    }
    
    public XaeroIcon createIcon() {
        if (!this.isFull()) {
            final int offsetX = this.currentIndex % this.sideIconCount * this.iconWidth;
            final int offsetY = this.currentIndex / this.sideIconCount * this.iconWidth;
            ++this.currentIndex;
            return new XaeroIcon(this, offsetX, offsetY);
        }
        return null;
    }
    
    public static class Builder
    {
        private int width;
        private int preparedTexture;
        private int iconWidth;
        
        private Builder() {
        }
        
        public Builder setDefault() {
            this.setIconWidth(64);
            return this;
        }
        
        public Builder setPreparedTexture(final int preparedTexture) {
            this.preparedTexture = preparedTexture;
            return this;
        }
        
        public Builder setWidth(final int width) {
            this.width = width;
            return this;
        }
        
        public Builder setIconWidth(final int iconWidth) {
            this.iconWidth = iconWidth;
            return this;
        }
        
        private int createGlTexture(final int actualWidth) {
            final int texture = GlStateManager.func_179146_y();
            OpenGLException.checkGLError();
            if (texture == 0) {
                return 0;
            }
            GlStateManager.func_179144_i(texture);
            GL11.glTexParameteri(3553, 33085, 0);
            GL11.glTexParameterf(3553, 33082, 0.0f);
            GL11.glTexParameterf(3553, 33083, 0.0f);
            GL11.glTexParameterf(3553, 34049, 0.0f);
            GL11.glTexParameteri(3553, 10241, 9729);
            GL11.glTexParameteri(3553, 10240, 9729);
            GL11.glTexParameteri(3553, 10242, 33071);
            GL11.glTexParameteri(3553, 10243, 33071);
            GlStateManager.func_187419_a(3553, 0, 32856, actualWidth, actualWidth, 0, 32993, 32821, (IntBuffer)null);
            GlStateManager.func_179144_i(0);
            OpenGLException.checkGLError();
            return texture;
        }
        
        public XaeroIconAtlas build() {
            if (this.width == 0 || this.iconWidth <= 0) {
                throw new IllegalStateException();
            }
            if (this.width / this.iconWidth * this.iconWidth != this.width) {
                throw new IllegalArgumentException();
            }
            final int texture = (this.preparedTexture == 0) ? this.createGlTexture(this.width) : this.preparedTexture;
            if (texture == 0) {
                WorldMap.LOGGER.error("Failed to create a GL texture for a new xaero icon atlas!");
                return null;
            }
            return new XaeroIconAtlas(texture, this.width, this.iconWidth, null);
        }
        
        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}
