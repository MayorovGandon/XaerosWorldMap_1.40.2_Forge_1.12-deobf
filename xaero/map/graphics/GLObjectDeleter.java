//Decompiled by Procyon!

package xaero.map.graphics;

import java.util.*;
import xaero.map.misc.*;
import java.nio.*;
import org.lwjgl.opengl.*;
import org.lwjgl.*;

public class GLObjectDeleter
{
    private static final int DELETE_PER_FRAME = 5;
    private static ByteBuffer buffer;
    private static IntBuffer bufferIntView;
    private ArrayList<Integer> texturesToDelete;
    private ArrayList<Integer> buffersToDelete;
    
    public GLObjectDeleter() {
        this.texturesToDelete = new ArrayList<Integer>();
        this.buffersToDelete = new ArrayList<Integer>();
    }
    
    public void work() {
        if (!this.texturesToDelete.isEmpty()) {
            do {
                synchronized (this.texturesToDelete) {
                    BufferCompatibilityFix.clear(GLObjectDeleter.bufferIntView);
                    for (int i = 0; i < 5 && !this.texturesToDelete.isEmpty(); ++i) {
                        GLObjectDeleter.bufferIntView.put(this.texturesToDelete.remove(this.texturesToDelete.size() - 1));
                    }
                    BufferCompatibilityFix.flip(GLObjectDeleter.bufferIntView);
                    GL11.glDeleteTextures(GLObjectDeleter.bufferIntView);
                }
            } while (this.texturesToDelete.size() > 640);
        }
        if (!this.buffersToDelete.isEmpty()) {
            do {
                synchronized (this.buffersToDelete) {
                    BufferCompatibilityFix.clear(GLObjectDeleter.bufferIntView);
                    for (int i = 0; i < 5 && !this.buffersToDelete.isEmpty(); ++i) {
                        GLObjectDeleter.bufferIntView.put(this.buffersToDelete.remove(this.buffersToDelete.size() - 1));
                    }
                    BufferCompatibilityFix.flip(GLObjectDeleter.bufferIntView);
                    PixelBuffers.glDeleteBuffers(GLObjectDeleter.bufferIntView);
                }
            } while (this.buffersToDelete.size() > 640);
        }
    }
    
    public void requestTextureDeletion(final int texture) {
        synchronized (this.texturesToDelete) {
            this.texturesToDelete.add(texture);
        }
    }
    
    public void requestBufferToDelete(final int bufferId) {
        synchronized (this.buffersToDelete) {
            this.buffersToDelete.add(bufferId);
        }
    }
    
    static {
        GLObjectDeleter.buffer = BufferUtils.createByteBuffer(20);
        GLObjectDeleter.bufferIntView = GLObjectDeleter.buffer.asIntBuffer();
    }
}
