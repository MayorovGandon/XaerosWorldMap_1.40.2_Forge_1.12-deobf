//Decompiled by Procyon!

package xaero.map.mods;

import xaero.map.*;
import java.lang.reflect.*;

public class SupportMods
{
    public static SupportXaeroMinimap xaeroMinimap;
    public static boolean vivecraft;
    public static boolean iris;
    public static SupportIris supportIris;
    public static SupportFramedBlocks supportFramedBlocks;
    
    public static boolean minimap() {
        return SupportMods.xaeroMinimap != null && SupportMods.xaeroMinimap.modMain != null;
    }
    
    public static boolean framedBlocks() {
        return SupportMods.supportFramedBlocks != null;
    }
    
    public static void load() {
        try {
            final Class mmClassTest = Class.forName("xaero.common.IXaeroMinimap");
            (SupportMods.xaeroMinimap = new SupportXaeroMinimap()).register();
        }
        catch (ClassNotFoundException ex) {}
        try {
            final Class vivecraftClassTest = Class.forName("org.vivecraft.api.VRData");
            SupportMods.vivecraft = true;
            try {
                final Class<?> vrStateClass = Class.forName("org.vivecraft.VRState");
                final Method checkVRMethod = vrStateClass.getDeclaredMethod("checkVR", (Class<?>[])new Class[0]);
                SupportMods.vivecraft = (boolean)checkVRMethod.invoke(null, new Object[0]);
            }
            catch (ClassNotFoundException ex2) {}
            catch (NoSuchMethodException ex3) {}
            catch (IllegalAccessException ex4) {}
            catch (IllegalArgumentException ex5) {}
            catch (InvocationTargetException ex6) {}
        }
        catch (ClassNotFoundException ex7) {}
        if (SupportMods.vivecraft) {
            WorldMap.LOGGER.info("Xaero's World Map: Vivecraft!");
        }
        else {
            WorldMap.LOGGER.info("Xaero's World Map: No Vivecraft!");
        }
        try {
            final Class mmClassTest = Class.forName("xfacthd.framedblocks.FramedBlocks");
            SupportMods.supportFramedBlocks = new SupportFramedBlocks();
            WorldMap.LOGGER.info("Xaero's World Map: Framed Blocks found!");
        }
        catch (ClassNotFoundException ex8) {}
        try {
            Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            SupportMods.supportIris = new SupportIris();
            SupportMods.iris = true;
            WorldMap.LOGGER.info("Xaero's World Map: Iris found!");
        }
        catch (Exception ex9) {}
    }
    
    static {
        SupportMods.xaeroMinimap = null;
        SupportMods.supportFramedBlocks = null;
    }
}
