//Decompiled by Procyon!

package xaero.map.misc;

import net.minecraft.block.state.*;
import net.minecraft.block.*;
import net.minecraft.init.*;
import net.minecraft.client.*;
import org.lwjgl.input.*;
import java.util.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.*;
import org.lwjgl.opengl.*;
import java.io.*;
import java.nio.file.*;
import xaero.map.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;
import net.minecraft.item.*;
import net.minecraft.entity.*;
import net.minecraft.scoreboard.*;
import net.minecraft.world.*;
import net.minecraft.potion.*;

public class Misc
{
    private static final long[] ZERO_LONG_586;
    private static long cpuTimerPreTime;
    private static long glTimerPreTime;
    public static final String OUTDATED_FILE_EXT = ".outdated";
    private static int shadersType;
    private static boolean EXTgeometryShader;
    
    public static IBlockState getStateById(final int id) {
        try {
            return Block.func_176220_d(id);
        }
        catch (Exception e) {
            return getDefaultBlockStateForStateId(id);
        }
    }
    
    private static IBlockState getDefaultBlockStateForStateId(final int id) {
        try {
            final Block block = Block.func_176220_d(id).func_177230_c();
            return block.func_176223_P();
        }
        catch (Exception e) {
            return Blocks.field_150350_a.func_176223_P();
        }
    }
    
    public static void glTimerPre() {
        GL11.glFinish();
        Misc.glTimerPreTime = System.nanoTime();
    }
    
    public static int glTimerResult() {
        GL11.glFinish();
        return (int)(System.nanoTime() - Misc.glTimerPreTime);
    }
    
    public static void timerPre() {
        Misc.cpuTimerPreTime = System.nanoTime();
    }
    
    public static int timerResult() {
        return (int)(System.nanoTime() - Misc.cpuTimerPreTime);
    }
    
    public static double getMouseX(final Minecraft mc) {
        return Mouse.getX();
    }
    
    public static double getMouseY(final Minecraft mc) {
        return mc.field_71440_d - Mouse.getY() - 1;
    }
    
    public static void clearHeightsData586(final long[] data) {
        System.arraycopy(Misc.ZERO_LONG_586, 0, data, 0, 586);
    }
    
    public static <T extends Comparable<? super T>> void addToListOfSmallest(final int maxSize, final List<T> list, final T element) {
        final int currentSize = list.size();
        if (currentSize == maxSize && list.get(currentSize - 1).compareTo((Object)element) <= 0) {
            return;
        }
        final int iterLimit = (currentSize == maxSize) ? maxSize : (currentSize + 1);
        int i = 0;
        while (i < iterLimit) {
            if (i == currentSize || element.compareTo((Object)list.get(i)) < 0) {
                list.add(i, element);
                if (currentSize == maxSize) {
                    list.remove(currentSize);
                    break;
                }
                break;
            }
            else {
                ++i;
            }
        }
    }
    
    public static void minecraftOrtho(final ScaledResolution scaledresolution) {
        GlStateManager.func_179096_D();
        GlStateManager.func_179130_a(0.0, scaledresolution.func_78327_c(), scaledresolution.func_78324_d(), 0.0, 1000.0, 3000.0);
    }
    
    public static void setShaderProgram(final int program) {
        if (Misc.shadersType == -500) {
            Misc.shadersType = (GLContext.getCapabilities().OpenGL20 ? 0 : (GLContext.getCapabilities().GL_ARB_shader_objects ? 1 : (GLContext.getCapabilities().GL_EXT_separate_shader_objects ? 2 : -1)));
            Misc.EXTgeometryShader = (GLContext.getCapabilities().GL_EXT_geometry_shader4 || GLContext.getCapabilities().GL_ARB_geometry_shader4);
        }
        switch (Misc.shadersType) {
            case 0: {
                GL20.glUseProgram(program);
                break;
            }
            case 1: {
                ARBShaderObjects.glUseProgramObjectARB(program);
                break;
            }
            case 2: {
                EXTSeparateShaderObjects.glUseShaderProgramEXT(35633, program);
                if (Misc.EXTgeometryShader) {
                    EXTSeparateShaderObjects.glUseShaderProgramEXT(36313, program);
                }
                EXTSeparateShaderObjects.glUseShaderProgramEXT(35632, program);
                EXTSeparateShaderObjects.glActiveProgramEXT(program);
                break;
            }
        }
    }
    
    public static Path convertToOutdated(final Path path, final int attempts) throws IOException {
        if (path.getFileName().toString().endsWith(".outdated")) {
            return path;
        }
        final Path outdatedPath = path.resolveSibling(path.getFileName().toString() + ".outdated");
        if (Files.exists(path, new LinkOption[0])) {
            convertToOutdated(path, outdatedPath, attempts);
        }
        return outdatedPath;
    }
    
    private static void convertToOutdated(final Path path, final Path outdatedPath, int attempts) throws IOException {
        --attempts;
        try {
            Files.move(path, outdatedPath, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException e) {
            if (attempts <= 0) {
                throw e;
            }
            WorldMap.LOGGER.info("Failed to convert file to outdated! Retrying... " + attempts);
            try {
                Thread.sleep(50L);
            }
            catch (InterruptedException ex) {}
            convertToOutdated(path, outdatedPath, attempts);
        }
    }
    
    public static boolean hasItem(final EntityPlayer player, final Item item) {
        return hasItem((NonNullList<ItemStack>)player.field_71071_by.field_184439_c, -1, item) || hasItem((NonNullList<ItemStack>)player.field_71071_by.field_70460_b, -1, item) || hasItem((NonNullList<ItemStack>)player.field_71071_by.field_70462_a, 9, item);
    }
    
    public static boolean hasItem(final NonNullList<ItemStack> inventory, final int limit, final Item item) {
        for (int i = 0; i < inventory.size() && (limit == -1 || i < limit); ++i) {
            if (inventory.get(i) != null && ((ItemStack)inventory.get(i)).func_77973_b() == item) {
                return true;
            }
        }
        return false;
    }
    
    public static int getTeamColour(final Entity e) {
        Integer teamColour = null;
        final Team team = e.func_96124_cp();
        if (team != null) {
            final String prefix = team.func_178775_l().toString();
            try {
                teamColour = Minecraft.func_71410_x().field_71466_p.func_175064_b(prefix.charAt(prefix.length() - 1));
            }
            catch (ArrayIndexOutOfBoundsException ex) {}
        }
        return (teamColour == null) ? -1 : teamColour;
    }
    
    public static double getDimensionTypeScale(final World world) {
        return getDimensionTypeScale(world.field_73011_w);
    }
    
    public static double getDimensionTypeScale(final WorldProvider provider) {
        return (provider.func_177495_o() || provider instanceof WorldProviderHell || provider.func_186058_p() == DimensionType.NETHER) ? 8.0 : 1.0;
    }
    
    public static boolean hasEffect(final EntityPlayer player, final Potion effect) {
        return effect != null && player != null && player.func_70644_a(effect);
    }
    
    public static boolean hasEffect(final Potion effect) {
        return hasEffect((EntityPlayer)Minecraft.func_71410_x().field_71439_g, effect);
    }
    
    static {
        ZERO_LONG_586 = new long[586];
        Misc.shadersType = -500;
    }
}
