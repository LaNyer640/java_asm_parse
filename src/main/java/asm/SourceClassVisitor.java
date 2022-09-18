package asm;

import model.ClassReference;
import model.MethodReference;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.List;
import java.util.regex.Pattern;

public class SourceClassVisitor extends ClassVisitor {
    private String ClassName;
    private ClassReference.Handle classHandle;
    private List<MethodReference> Sources;

    public SourceClassVisitor(List<MethodReference> Sources) {
        super(Opcodes.ASM6);
        this.Sources = Sources;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        if (this.ClassName != null) {
            throw new IllegalStateException("ClassVisitor already visited a class!");
        }
        this.classHandle = new ClassReference.Handle(name);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (MethodNameIsMatch(name)) {
            System.out.println(name);
            boolean isStatic = (access & Opcodes.ACC_STATIC) != 0;
            Sources.add(new MethodReference(
                    classHandle,
                    name,
                    descriptor,
                    isStatic));
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    public boolean MethodNameIsMatch(String MethodName) {
        String pattern = "^do[A-Z].*";
        boolean isMatch = Pattern.matches(pattern, MethodName);
        return isMatch;
    }
}