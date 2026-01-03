//Decompiled by Procyon!

package xaero.map.exception;

import org.lwjgl.opengl.*;
import xaero.map.*;

public class OpenGLException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    
    public OpenGLException(final int error) {
        super("OpenGL error: " + error);
    }
    
    public static void checkGLError() throws OpenGLException {
        checkGLError(true, null);
    }
    
    public static void checkGLError(final boolean crash, final String where) throws OpenGLException {
        final int error = GL11.glGetError();
        if (error != 0) {
            if (crash) {
                throw new OpenGLException(error);
            }
            WorldMap.LOGGER.warn("Ignoring OpenGL error " + error + " when " + where + ". Most likely caused by another mod.");
        }
    }
}
