package asm;

import model.MethodReference;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.JSRInlinerAdapter;

import java.util.Map;
import java.util.Set;

public class MethodCallClassVisitor extends ClassVisitor {
    private String ClassName;
    private final Map<MethodReference.Handle, Set<MethodReference.Handle>> methodCall;

    public MethodCallClassVisitor(int api, Map<MethodReference.Handle, Set<MethodReference.Handle>> methodCall) {
        super(api);
        this.methodCall = methodCall;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        if (this.ClassName != null) {
            throw new IllegalStateException("ClassVisitor already visited a class!");
        }
        this.ClassName = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        //在visit每个method的时候，创建MethodVisitor对method进行观察
        MethodCallAdapter MethodCallAdapter = new MethodCallAdapter(
                api, mv, methodCall,ClassName,name,descriptor);
        return new JSRInlinerAdapter(MethodCallAdapter, access, name, descriptor, signature, exceptions);
    }
}
