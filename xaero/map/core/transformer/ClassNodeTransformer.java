//Decompiled by Procyon!

package xaero.map.core.transformer;

import net.minecraft.launchwrapper.*;
import org.objectweb.asm.*;
import java.util.*;
import java.util.function.*;
import org.objectweb.asm.tree.*;

public abstract class ClassNodeTransformer implements IClassTransformer
{
    protected String className;
    
    public byte[] transform(final String name, final String transformedName, final byte[] basicClass) {
        if (transformedName.equals(this.className)) {
            System.out.println("Transforming class " + transformedName);
            final boolean isObfuscated = !name.equals(transformedName);
            return this.transform(basicClass, isObfuscated);
        }
        return basicClass;
    }
    
    private byte[] transform(final byte[] basicClass, final boolean isObfuscated) {
        final ClassReader classReader = new ClassReader(basicClass);
        final ClassNode classNode = new ClassNode(327680);
        classReader.accept((ClassVisitor)classNode, 0);
        this.transformNode(classNode, isObfuscated);
        final ClassWriter classWriter = new ClassWriter(0);
        classNode.accept((ClassVisitor)classWriter);
        return classWriter.toByteArray();
    }
    
    protected void addGetter(final ClassNode classNode, final String fieldName, final String fieldDesc) {
        this.addGetter(classNode, fieldName, "get" + (fieldName.charAt(0) + "").toUpperCase() + fieldName.substring(1), fieldDesc, 176);
    }
    
    protected void addGetter(final ClassNode classNode, final String fieldName, final String getterName, final String fieldDesc, final int returnType) {
        final List<MethodNode> methods = (List<MethodNode>)classNode.methods;
        final MethodNode getterNode = new MethodNode(1, getterName, "()" + fieldDesc, (String)null, (String[])null);
        final LabelNode labelNode1 = new LabelNode();
        final LabelNode labelNode2 = new LabelNode();
        final InsnList instructions = getterNode.instructions;
        instructions.add((AbstractInsnNode)labelNode1);
        instructions.add((AbstractInsnNode)new VarInsnNode(25, 0));
        instructions.add((AbstractInsnNode)new FieldInsnNode(180, classNode.name, fieldName, fieldDesc));
        instructions.add((AbstractInsnNode)new InsnNode(returnType));
        instructions.add((AbstractInsnNode)labelNode2);
        getterNode.localVariables.add(new LocalVariableNode("this", "L" + classNode.name + ";", (String)null, labelNode1, labelNode2, 0));
        getterNode.maxStack = 2;
        getterNode.maxLocals = 1;
        methods.add(getterNode);
    }
    
    protected void addSetter(final ClassNode classNode, final String fieldName, final String fieldDesc) {
        final List<MethodNode> methods = (List<MethodNode>)classNode.methods;
        final MethodNode setterNode = new MethodNode(1, "set" + (fieldName.charAt(0) + "").toUpperCase() + fieldName.substring(1), "(" + fieldDesc + ")V", (String)null, (String[])null);
        final LabelNode labelNode1 = new LabelNode();
        final LabelNode labelNode2 = new LabelNode();
        final InsnList instructions = setterNode.instructions;
        instructions.add((AbstractInsnNode)labelNode1);
        instructions.add((AbstractInsnNode)new VarInsnNode(25, 0));
        instructions.add((AbstractInsnNode)new VarInsnNode(25, 1));
        instructions.add((AbstractInsnNode)new FieldInsnNode(181, classNode.name, fieldName, fieldDesc));
        instructions.add((AbstractInsnNode)new InsnNode(177));
        instructions.add((AbstractInsnNode)labelNode2);
        setterNode.localVariables.add(new LocalVariableNode("this", "L" + classNode.name + ";", (String)null, labelNode1, labelNode2, 0));
        setterNode.localVariables.add(new LocalVariableNode("value", fieldDesc, (String)null, labelNode1, labelNode2, 1));
        setterNode.maxStack = 2;
        setterNode.maxLocals = 2;
        methods.add(setterNode);
    }
    
    protected void insertBeforeReturn2(final MethodNode methodNode, final Supplier<InsnList> patchListGetter) {
        final InsnList instructions = methodNode.instructions;
        for (int i = 0; i < instructions.size(); ++i) {
            final AbstractInsnNode insn = instructions.get(i);
            if (insn.getOpcode() >= 172 && insn.getOpcode() <= 177) {
                final InsnList toInsert = patchListGetter.get();
                final int patchSize = toInsert.size();
                instructions.insertBefore(insn, toInsert);
                i += patchSize;
            }
        }
    }
    
    protected void insertBeforeReturn(final MethodNode methodNode, final InsnList patchList) {
        final Supplier<InsnList> patchListGetter = new Supplier<InsnList>() {
            @Override
            public InsnList get() {
                return patchList;
            }
        };
        this.insertBeforeReturn2(methodNode, patchListGetter);
    }
    
    protected boolean insertOnInvoke2(final MethodNode methodNode, final Supplier<InsnList> patchListGetter, final boolean before, final String invokeOwner, final String invokeName, final String invokeNameObf, final String invokeDesc, final boolean firstOnly) {
        final InsnList instructions = methodNode.instructions;
        boolean isObfuscated = false;
        for (int i = 0; i < instructions.size(); ++i) {
            final AbstractInsnNode insn = instructions.get(i);
            if (insn instanceof MethodInsnNode && insn.getOpcode() >= 182 && insn.getOpcode() <= 185) {
                final MethodInsnNode methodInsn = (MethodInsnNode)insn;
                if (methodInsn.owner.equals(invokeOwner) && (methodInsn.name.equals(invokeName) || methodInsn.name.equals(invokeNameObf)) && methodInsn.desc.equals(invokeDesc)) {
                    if (methodInsn.name.equals(invokeNameObf)) {
                        isObfuscated = true;
                    }
                    final InsnList toInsert = patchListGetter.get();
                    final int patchSize = toInsert.size();
                    if (before) {
                        instructions.insertBefore(insn, toInsert);
                    }
                    else {
                        instructions.insert(insn, toInsert);
                    }
                    i += patchSize;
                    if (firstOnly) {
                        break;
                    }
                }
            }
        }
        return isObfuscated;
    }
    
    protected boolean insertOnInvoke(final MethodNode methodNode, final InsnList patchList, final boolean before, final String invokeOwner, final String invokeName, final String invokeNameObf, final String invokeDesc) {
        final Supplier<InsnList> patchListGetter = new Supplier<InsnList>() {
            @Override
            public InsnList get() {
                return patchList;
            }
        };
        return this.insertOnInvoke2(methodNode, patchListGetter, before, invokeOwner, invokeName, invokeNameObf, invokeDesc, true);
    }
    
    protected abstract void transformNode(final ClassNode p0, final boolean p1);
}
