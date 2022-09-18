package framework;

import model.MethodReference;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.List;

public class JenkinsSourceVisitor extends ClassVisitor {
    private String MethodName;
    private String ClassName;
    private final List<MethodReference> SourceMethod;

    public JenkinsSourceVisitor(List<MethodReference> SourceMethod){
        super(Opcodes.ASM6);
        this.SourceMethod = SourceMethod;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.ClassName = name;
        super.visit(version,access,name,signature,superName,interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions){
        boolean isStatic = (access & Opcodes.ACC_STATIC) != 0;
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        this.MethodName = name;
        return new JenkinsAdapter(MethodName, desc, this.ClassName, Opcodes.ASM6, mv,SourceMethod,isStatic);
    }
}
