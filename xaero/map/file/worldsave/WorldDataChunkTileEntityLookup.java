//Decompiled by Procyon!

package xaero.map.file.worldsave;

import it.unimi.dsi.fastutil.ints.*;
import java.util.function.*;
import net.minecraft.nbt.*;

public class WorldDataChunkTileEntityLookup
{
    private NBTTagList tileEntitiesNbt;
    private Int2ObjectMap<Int2ObjectMap<Int2ObjectMap<NBTTagCompound>>> tileEntities;
    
    public WorldDataChunkTileEntityLookup(final NBTTagList tileEntitiesNbt) {
        this.tileEntitiesNbt = tileEntitiesNbt;
    }
    
    private void loadIfNeeded() {
        if (this.tileEntities == null) {
            this.tileEntities = (Int2ObjectMap<Int2ObjectMap<Int2ObjectMap<NBTTagCompound>>>)new Int2ObjectOpenHashMap();
            this.tileEntitiesNbt.forEach((Consumer)new Consumer<NBTBase>() {
                @Override
                public void accept(final NBTBase tag) {
                    if (tag instanceof NBTTagCompound) {
                        final NBTTagCompound compoundNbt = (NBTTagCompound)tag;
                        if (!compoundNbt.func_150297_b("x", 99)) {
                            return;
                        }
                        final int x = compoundNbt.func_74762_e("x") & 0xF;
                        if (!compoundNbt.func_150297_b("y", 99)) {
                            return;
                        }
                        final int y = compoundNbt.func_74762_e("y");
                        if (!compoundNbt.func_150297_b("z", 99)) {
                            return;
                        }
                        final int z = compoundNbt.func_74762_e("z") & 0xF;
                        Int2ObjectMap<Int2ObjectMap<NBTTagCompound>> byX = (Int2ObjectMap<Int2ObjectMap<NBTTagCompound>>)WorldDataChunkTileEntityLookup.this.tileEntities.get(x);
                        if (byX == null) {
                            WorldDataChunkTileEntityLookup.this.tileEntities.put(x, (Object)(byX = (Int2ObjectMap<Int2ObjectMap<NBTTagCompound>>)new Int2ObjectOpenHashMap()));
                        }
                        Int2ObjectMap<NBTTagCompound> byY = (Int2ObjectMap<NBTTagCompound>)byX.get(y);
                        if (byY == null) {
                            byX.put(y, (Object)(byY = (Int2ObjectMap<NBTTagCompound>)new Int2ObjectOpenHashMap()));
                        }
                        byY.put(z, (Object)compoundNbt);
                    }
                }
            });
            this.tileEntitiesNbt = null;
        }
    }
    
    public NBTTagCompound getTileEntityNbt(final int x, final int y, final int z) {
        this.loadIfNeeded();
        final Int2ObjectMap<Int2ObjectMap<NBTTagCompound>> byX = (Int2ObjectMap<Int2ObjectMap<NBTTagCompound>>)this.tileEntities.get(x);
        if (byX == null) {
            return null;
        }
        final Int2ObjectMap<NBTTagCompound> byY = (Int2ObjectMap<NBTTagCompound>)byX.get(y);
        if (byY == null) {
            return null;
        }
        return (NBTTagCompound)byY.get(z);
    }
}
