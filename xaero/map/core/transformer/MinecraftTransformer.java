//Decompiled by Procyon!

package xaero.map.core.transformer;

import org.objectweb.asm.tree.*;
import java.util.*;

public class MinecraftTransformer extends ClassNodeTransformer
{
    public byte[] transform(final String name, final String transformedName, final byte[] basicClass) {
        this.className = "net.minecraft.client.Minecraft";
        return super.transform(name, transformedName, basicClass);
    }
    
    protected void transformNode(final ClassNode classNode, final boolean isObfuscated) {
        final String methodName = isObfuscated ? "az" : "runGameLoop";
        final String methodDesc = isObfuscated ? "()V" : "()V";
        for (final MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(methodName) && methodNode.desc.equals(methodDesc)) {
                final InsnList instructions = methodNode.instructions;
                final InsnList patchList = new InsnList();
                patchList.add((AbstractInsnNode)new MethodInsnNode(184, "xaero/map/core/XaeroWorldMapCore", "onMinecraftRunTick", "()V", false));
                instructions.insert(instructions.get(0), patchList);
                break;
            }
        }
    }
}
