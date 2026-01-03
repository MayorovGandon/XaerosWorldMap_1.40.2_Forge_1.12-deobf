//Decompiled by Procyon!

package xaero.map.core.transformer;

import org.objectweb.asm.tree.*;
import java.util.*;

public class SaveFormatTransformer extends ClassNodeTransformer
{
    public byte[] transform(final String name, final String transformedName, final byte[] basicClass) {
        this.className = "net.minecraft.world.storage.SaveFormatOld";
        return super.transform(name, transformedName, basicClass);
    }
    
    protected void transformNode(final ClassNode classNode, final boolean isObfuscated) {
        final String methodName = isObfuscated ? "e" : "deleteWorldDirectory";
        final String methodDesc = isObfuscated ? "(Ljava/lang/String;)Z" : "(Ljava/lang/String;)Z";
        for (final MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(methodName) && methodNode.desc.equals(methodDesc)) {
                final InsnList instructions = methodNode.instructions;
                final InsnList patchList = new InsnList();
                patchList.add((AbstractInsnNode)new VarInsnNode(25, 1));
                patchList.add((AbstractInsnNode)new MethodInsnNode(184, "xaero/map/core/XaeroWorldMapCore", "onDeleteWorld", "(Ljava/lang/String;)V", false));
                for (int i = instructions.size() - 1; i >= 0; --i) {
                    if (instructions.get(i).getOpcode() == 172) {
                        instructions.insertBefore(instructions.get(i), patchList);
                        break;
                    }
                }
                break;
            }
        }
    }
}
