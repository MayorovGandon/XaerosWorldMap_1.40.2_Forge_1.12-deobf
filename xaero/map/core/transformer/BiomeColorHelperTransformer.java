//Decompiled by Procyon!

package xaero.map.core.transformer;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import java.util.*;

public class BiomeColorHelperTransformer extends ClassNodeTransformer
{
    @Override
    public byte[] transform(final String name, final String transformedName, final byte[] basicClass) {
        this.className = "net.minecraft.world.biome.BiomeColorHelper";
        return super.transform(name, transformedName, basicClass);
    }
    
    @Override
    protected void transformNode(final ClassNode classNode, final boolean isObfuscated) {
        final String methodNameGetColorAtPos = isObfuscated ? "a" : "getColorAtPos";
        final String methodDescGetColorAtPos = isObfuscated ? "(Lamy;Let;Lanj$a;)I" : "(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/biome/BiomeColorHelper$ColorResolver;)I";
        for (final MethodNode mn : classNode.methods) {
            if (mn.name.equals(methodNameGetColorAtPos) && mn.desc.equals(methodDescGetColorAtPos)) {
                final InsnList instructions = mn.instructions;
                final InsnList patchList = new InsnList();
                final LabelNode MY_LABEL = new LabelNode(new Label());
                patchList.add((AbstractInsnNode)new VarInsnNode(25, 2));
                patchList.add((AbstractInsnNode)new MethodInsnNode(184, "xaero/map/core/XaeroWorldMapCore", "onGetBlockColor", "(Ljava/lang/Object;)Z", false));
                patchList.add((AbstractInsnNode)new InsnNode(89));
                patchList.add((AbstractInsnNode)new JumpInsnNode(153, MY_LABEL));
                patchList.add((AbstractInsnNode)new FrameNode(4, 3, (Object[])null, 1, new Object[] { Opcodes.INTEGER }));
                patchList.add((AbstractInsnNode)new InsnNode(172));
                patchList.add((AbstractInsnNode)MY_LABEL);
                patchList.add((AbstractInsnNode)new FrameNode(4, 3, (Object[])null, 1, new Object[] { Opcodes.INTEGER }));
                patchList.add((AbstractInsnNode)new InsnNode(87));
                final AbstractInsnNode firstInsn = instructions.get(0);
                if (firstInsn instanceof LabelNode) {
                    instructions.insert(firstInsn, patchList);
                    break;
                }
                instructions.insertBefore(firstInsn, patchList);
                break;
            }
        }
    }
}
