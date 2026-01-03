//Decompiled by Procyon!

package xaero.map.core.transformer;

import org.objectweb.asm.tree.*;

public class WorldClientTransformer extends ClassNodeTransformer
{
    public byte[] transform(final String name, final String transformedName, final byte[] basicClass) {
        this.className = "net.minecraft.client.multiplayer.WorldClient";
        return super.transform(name, transformedName, basicClass);
    }
    
    protected void transformNode(final ClassNode classNode, final boolean isObfuscated) {
        classNode.interfaces.add("xaero/map/mcworld/IWorldMapClientWorld");
        classNode.fields.add(new FieldNode(2, "xaero_worldmapData", "Lxaero/map/mcworld/WorldMapClientWorldData;", (String)null, (Object)null));
        this.addGetter(classNode, "xaero_worldmapData", "Lxaero/map/mcworld/WorldMapClientWorldData;");
        this.addSetter(classNode, "xaero_worldmapData", "Lxaero/map/mcworld/WorldMapClientWorldData;");
    }
}
