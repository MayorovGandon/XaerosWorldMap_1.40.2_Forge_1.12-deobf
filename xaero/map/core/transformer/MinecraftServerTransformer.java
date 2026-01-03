//Decompiled by Procyon!

package xaero.map.core.transformer;

import org.objectweb.asm.tree.*;
import java.util.*;

public class MinecraftServerTransformer extends ClassNodeTransformer
{
    public byte[] transform(final String name, final String transformedName, final byte[] basicClass) {
        this.className = "net.minecraft.server.MinecraftServer";
        return super.transform(name, transformedName, basicClass);
    }
    
    protected void transformNode(final ClassNode classNode, final boolean isObfuscated) {
        final List<FieldNode> fields = (List<FieldNode>)classNode.fields;
        classNode.interfaces.add("xaero/map/server/IMinecraftServer");
        fields.add(new FieldNode(2, "xaeroWorldMapServerData", "Lxaero/map/server/MinecraftServerData;", (String)null, (Object)null));
        this.addGetter(classNode, "xaeroWorldMapServerData", "Lxaero/map/server/MinecraftServerData;");
        this.addSetter(classNode, "xaeroWorldMapServerData", "Lxaero/map/server/MinecraftServerData;");
    }
}
