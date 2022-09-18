package asm;

import model.ClassReference;
import model.MethodReference;
import org.objectweb.asm.MethodVisitor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MethodCallAdapter extends MethodVisitor {
    private String ClassName;
    private String MethodName;
    private String descriptor;

    private final Map<MethodReference.Handle, Set<MethodReference.Handle>> methodCall;
    private final Set<MethodReference.Handle> calledMethods;
    public MethodCallAdapter(int api, MethodVisitor mv,Map<MethodReference.Handle, Set<MethodReference.Handle>> methodCall, String className, String name, String descriptor) {
        super(api,mv);
        this.methodCall = methodCall;
        this.calledMethods = new HashSet<>();
        this.methodCall.put(new MethodReference.Handle(new ClassReference.Handle(className), name, descriptor), calledMethods);
    }


    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        calledMethods.add(new MethodReference.Handle(new ClassReference.Handle(owner), name, desc));
        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }


}
