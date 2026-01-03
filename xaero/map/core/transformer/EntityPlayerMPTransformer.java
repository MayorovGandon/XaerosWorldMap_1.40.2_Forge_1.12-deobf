//Decompiled by Procyon!

package xaero.map.core.transformer;

import org.objectweb.asm.tree.*;
import java.util.*;

public class EntityPlayerMPTransformer extends ClassNodeTransformer
{
    public byte[] transform(final String name, final String transformedName, final byte[] basicClass) {
        this.className = "net.minecraft.entity.player.EntityPlayerMP";
        return super.transform(name, transformedName, basicClass);
    }
    
    protected void transformNode(final ClassNode classNode, final boolean isObfuscated) {
        final List<FieldNode> fields = (List<FieldNode>)classNode.fields;
        classNode.interfaces.add("xaero/map/server/player/IServerPlayer");
        fields.add(new FieldNode(2, "xaeroWorldMapPlayerData", "Lxaero/map/server/player/ServerPlayerData;", (String)null, (Object)null));
        this.addGetter(classNode, "xaeroWorldMapPlayerData", "Lxaero/map/server/player/ServerPlayerData;");
        this.addSetter(classNode, "xaeroWorldMapPlayerData", "Lxaero/map/server/player/ServerPlayerData;");
    }
}
