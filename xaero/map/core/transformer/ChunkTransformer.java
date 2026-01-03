//Decompiled by Procyon!

package xaero.map.core.transformer;

import org.objectweb.asm.tree.*;

public class ChunkTransformer extends ClassNodeTransformer
{
    @Override
    public byte[] transform(final String name, final String transformedName, final byte[] basicClass) {
        this.className = "net.minecraft.world.chunk.Chunk";
        return super.transform(name, transformedName, basicClass);
    }
    
    @Override
    protected void transformNode(final ClassNode classNode, final boolean isObfuscated) {
        classNode.fields.add(new FieldNode(1, "xaero_wm_chunkClean", "Z", (String)null, (Object)0));
        classNode.interfaces.add("xaero/map/core/IWorldMapChunk");
        final String lastSaveTimeFieldName = isObfuscated ? "field_76641_n" : "lastSaveTime";
        this.addGetter(classNode, lastSaveTimeFieldName, "xaero_getLastSaveTime", "J", 173);
    }
}
