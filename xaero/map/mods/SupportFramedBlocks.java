//Decompiled by Procyon!

package xaero.map.mods;

import java.lang.reflect.*;
import net.minecraft.block.*;
import xaero.map.*;
import net.minecraft.util.registry.*;
import net.minecraft.util.*;
import java.util.*;
import java.util.function.*;
import net.minecraft.block.state.*;
import net.minecraft.tileentity.*;
import xaero.lib.common.reflection.util.*;

public class SupportFramedBlocks
{
    private Class<?> framedTileBlockClass;
    private Method framedTileEntityCamoStateMethod;
    private Method framedTileEntityCamoMethod;
    private Method camoContainerStateMethod;
    private Method camoContainerContentMethod;
    private Method camoContentStateMethod;
    private boolean usable;
    private Set<Block> framedBlocks;
    
    public SupportFramedBlocks() {
        try {
            this.framedTileBlockClass = Class.forName("xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity");
        }
        catch (ClassNotFoundException cnfe4) {
            try {
                this.framedTileBlockClass = Class.forName("xfacthd.framedblocks.common.tileentity.FramedTileEntity");
            }
            catch (ClassNotFoundException cnfe5) {
                try {
                    this.framedTileBlockClass = Class.forName("xfacthd.framedblocks.api.block.FramedBlockEntity");
                }
                catch (ClassNotFoundException cnfe3) {
                    WorldMap.LOGGER.info("Failed to init Framed Blocks support!", (Throwable)cnfe3);
                    return;
                }
            }
        }
        try {
            this.framedTileEntityCamoStateMethod = this.framedTileBlockClass.getDeclaredMethod("getCamoState", (Class<?>[])new Class[0]);
        }
        catch (NoSuchMethodException | SecurityException ex4) {
            final Exception ex;
            final Exception e1 = ex;
            try {
                Class<?> camoContainerClass;
                try {
                    camoContainerClass = Class.forName("xfacthd.framedblocks.api.data.CamoContainer");
                }
                catch (ClassNotFoundException cnfe6) {
                    camoContainerClass = Class.forName("xfacthd.framedblocks.api.camo.CamoContainer");
                }
                this.framedTileEntityCamoMethod = this.framedTileBlockClass.getDeclaredMethod("getCamo", (Class<?>[])new Class[0]);
                try {
                    this.camoContainerStateMethod = camoContainerClass.getDeclaredMethod("getState", (Class<?>[])new Class[0]);
                }
                catch (NoSuchMethodException | SecurityException ex5) {
                    final Exception ex2;
                    final Exception e2 = ex2;
                    this.camoContainerContentMethod = camoContainerClass.getDeclaredMethod("getContent", (Class<?>[])new Class[0]);
                    final Class<?> camoContentClass = Class.forName("xfacthd.framedblocks.api.camo.CamoContent");
                    this.camoContentStateMethod = camoContentClass.getDeclaredMethod("getAppearanceState", (Class<?>[])new Class[0]);
                }
            }
            catch (ClassNotFoundException | NoSuchMethodException | SecurityException ex6) {
                final Exception ex3;
                final Exception e3 = ex3;
                WorldMap.LOGGER.info("Failed to init Framed Blocks support!", (Throwable)e1);
                WorldMap.LOGGER.info("Failed to init Framed Blocks support!", (Throwable)e3);
            }
        }
        this.usable = (this.framedTileBlockClass != null && (this.framedTileEntityCamoStateMethod != null || (this.framedTileEntityCamoMethod != null && (this.camoContainerStateMethod != null || (this.camoContainerContentMethod != null && this.camoContentStateMethod != null)))));
    }
    
    public void onWorldChange() {
        this.framedBlocks = null;
    }
    
    private void findFramedBlocks(IRegistry<ResourceLocation, Block> registry) {
        if (this.framedBlocks == null) {
            this.framedBlocks = new HashSet<Block>();
            if (registry == null) {
                registry = (IRegistry<ResourceLocation, Block>)Block.field_149771_c;
            }
            final IRegistry<ResourceLocation, Block> forwardedRegistry = registry;
            registry.func_148742_b().forEach(new Consumer<ResourceLocation>() {
                @Override
                public void accept(final ResourceLocation key) {
                    if (key.func_110624_b().equals("framedblocks") && key.func_110623_a().startsWith("framed_")) {
                        SupportFramedBlocks.this.framedBlocks.add(forwardedRegistry.func_82594_a((Object)key));
                    }
                }
            });
        }
    }
    
    public boolean isFrameBlock(final IRegistry<ResourceLocation, Block> registry, final IBlockState state) {
        if (!this.usable) {
            return false;
        }
        this.findFramedBlocks(registry);
        return this.framedBlocks.contains(state.func_177230_c());
    }
    
    public IBlockState unpackFramedBlock(final IRegistry<ResourceLocation, Block> registry, final IBlockState original, final TileEntity tileEntity) {
        if (!this.usable) {
            return original;
        }
        if (!this.framedTileBlockClass.isAssignableFrom(tileEntity.getClass())) {
            return original;
        }
        if (this.framedTileEntityCamoStateMethod != null) {
            return (IBlockState)ReflectionUtils.getReflectMethodValue((Object)tileEntity, this.framedTileEntityCamoStateMethod, new Object[0]);
        }
        final Object camoContainer = ReflectionUtils.getReflectMethodValue((Object)tileEntity, this.framedTileEntityCamoMethod, new Object[0]);
        if (this.camoContainerStateMethod != null) {
            return (IBlockState)ReflectionUtils.getReflectMethodValue(camoContainer, this.camoContainerStateMethod, new Object[0]);
        }
        final Object camoContent = ReflectionUtils.getReflectMethodValue(camoContainer, this.camoContainerContentMethod, new Object[0]);
        if (camoContent == null) {
            return original;
        }
        final IBlockState state = (IBlockState)ReflectionUtils.getReflectMethodValue(camoContent, this.camoContentStateMethod, new Object[0]);
        if (state == null) {
            return original;
        }
        return state;
    }
}
