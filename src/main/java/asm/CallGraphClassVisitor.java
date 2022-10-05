package asm;

import model.CallGraph;
import model.ClassReference;
import model.InheritanceMap;
import model.MethodReference;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.JSRInlinerAdapter;

import java.util.Map;
import java.util.Set;

public class CallGraphClassVisitor extends ClassVisitor {
    private final Set<CallGraph> discoveredCalls;
    private final Map<ClassReference.Handle, ClassReference> classMap;
    private final InheritanceMap inheritanceMap;
    private final Map<MethodReference.Handle, Set<Integer>> passthroughDataflow;
    private String name;

    public CallGraphClassVisitor(Map<ClassReference.Handle, ClassReference> classMap,
                                 InheritanceMap inheritanceMap,
                                 Map<MethodReference.Handle, Set<Integer>> passthroughDataflow,
                                 Set<CallGraph> discoveredCalls) {
        super(Opcodes.ASM7);
        this.classMap = classMap;
        this.inheritanceMap = inheritanceMap;
        this.passthroughDataflow = passthroughDataflow;
        this.discoveredCalls = discoveredCalls;
    }

    @Override
    public void visit(int version, int access, String name, String signature,
                      String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.name = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        CallGraphMethodAdapter callGraphMethodVisitor = new CallGraphMethodAdapter(classMap,
                inheritanceMap, passthroughDataflow,api, discoveredCalls,
                mv, this.name, access, name, desc);
        return new JSRInlinerAdapter(callGraphMethodVisitor, access, name, desc, signature, exceptions);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }
}

