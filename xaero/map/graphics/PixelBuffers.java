//Decompiled by Procyon!

package xaero.map.graphics;

import xaero.map.*;
import java.nio.*;
import org.lwjgl.opengl.*;

public class PixelBuffers
{
    private static int buffersType;
    
    private static int innerGenBuffers() {
        switch (PixelBuffers.buffersType) {
            case 0: {
                return GL15.glGenBuffers();
            }
            case 1: {
                return ARBVertexBufferObject.glGenBuffersARB();
            }
            default: {
                return 0;
            }
        }
    }
    
    public static int glGenBuffers() {
        int attempts = 5;
        int result;
        do {
            result = innerGenBuffers();
        } while (--attempts > 0 && result == 0);
        if (result == 0) {
            WorldMap.LOGGER.error("Failed to generate a PBO after multiple attempts. Likely caused by previous errors from other mods.");
        }
        return result;
    }
    
    public static void glBindBuffer(final int target, final int buffer) {
        switch (PixelBuffers.buffersType) {
            case 0: {
                GL15.glBindBuffer(target, buffer);
                break;
            }
            case 1: {
                ARBVertexBufferObject.glBindBufferARB(target, buffer);
                break;
            }
        }
    }
    
    public static void glBufferData(final int target, final long size, final int usage) {
        switch (PixelBuffers.buffersType) {
            case 0: {
                GL15.glBufferData(target, size, usage);
                break;
            }
            case 1: {
                ARBVertexBufferObject.glBufferDataARB(target, size, usage);
                break;
            }
        }
    }
    
    public static ByteBuffer glMapBuffer(final int target, final int access, final long length, final ByteBuffer old_buffer) {
        switch (PixelBuffers.buffersType) {
            case 0: {
                return GL15.glMapBuffer(target, access, length, old_buffer);
            }
            case 1: {
                return ARBVertexBufferObject.glMapBufferARB(target, access, length, old_buffer);
            }
            default: {
                return null;
            }
        }
    }
    
    public static boolean glUnmapBuffer(final int target) {
        switch (PixelBuffers.buffersType) {
            case 0: {
                return GL15.glUnmapBuffer(target);
            }
            case 1: {
                return ARBVertexBufferObject.glUnmapBufferARB(target);
            }
            default: {
                return false;
            }
        }
    }
    
    public static void glDeleteBuffers(final int buffer) {
        switch (PixelBuffers.buffersType) {
            case 0: {
                GL15.glDeleteBuffers(buffer);
                break;
            }
            case 1: {
                ARBVertexBufferObject.glDeleteBuffersARB(buffer);
                break;
            }
        }
    }
    
    public static void glDeleteBuffers(final IntBuffer buffers) {
        switch (PixelBuffers.buffersType) {
            case 0: {
                GL15.glDeleteBuffers(buffers);
                break;
            }
            case 1: {
                ARBVertexBufferObject.glDeleteBuffersARB(buffers);
                break;
            }
        }
    }
    
    static {
        if (GLContext.getCapabilities().OpenGL15) {
            PixelBuffers.buffersType = 0;
        }
        else {
            if (!GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
                throw new RuntimeException("Xaero's World Map requires Buffer Object support!");
            }
            PixelBuffers.buffersType = 1;
        }
        if (!GLContext.getCapabilities().OpenGL21 && !GLContext.getCapabilities().GL_EXT_pixel_buffer_object && !GLContext.getCapabilities().GL_ARB_pixel_buffer_object) {
            throw new RuntimeException("Xaero's World Map requires Pixel Buffer Object support!");
        }
    }
}
