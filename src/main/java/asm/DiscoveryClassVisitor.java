package asm;

import model.ClassReference;
import model.MethodReference;
import org.objectweb.asm.*;

import java.util.*;

public class DiscoveryClassVisitor extends ClassVisitor {
    private String name;
    private String superName;
    private String[] interfaces;
    private boolean isInterface;
    private boolean isAbstract;
    private List<ClassReference.Variable> Variables;
    private ClassReference.Handle classHandle;
    private  List<ClassReference> discoveredClasses;
    private  List<MethodReference> discoveredMethods;

    private  String methodName;



    public DiscoveryClassVisitor(List<ClassReference> discoveredClasses,
                                 List<MethodReference> discoveredMethods) {
        super(Opcodes.ASM7);
        this.discoveredClasses = discoveredClasses;
        this.discoveredMethods = discoveredMethods;
    }

    public DiscoveryClassVisitor(String methodName) {
        super(Opcodes.ASM7);
        this.methodName = methodName;
    }

    @Override
    public void visit(int version, int access, String name,
                      String signature, String superName, String[] interfaces) {
        this.name = name;
        this.superName = superName;
        this.interfaces = interfaces;
        this.isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
        this.isAbstract = (access & Opcodes.ACC_ABSTRACT) != 0;
        this.Variables = new ArrayList<>();
        this.classHandle = new ClassReference.Handle(name);
        super.visit(version, access, name, signature, superName, interfaces);
    }

    public String getName(){
        return this.name;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc,
                                   String signature, Object value) {
        if ((access & Opcodes.ACC_STATIC) == 0) {
            Type type = Type.getType(desc);
            String typeName;
            if (type.getSort() == Type.OBJECT || type.getSort() == Type.ARRAY) {
                typeName = type.getInternalName();
            } else {
                typeName = type.getDescriptor();
            }
            Variables.add(new ClassReference.Variable(name, access, new ClassReference.Handle(typeName)));
        }
        return super.visitField(access, name, desc, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {
        if(methodName!=null){
            if(methodName.equals(name)){
                System.out.println(this.name);
                System.out.println(desc);
            }
        } else {
            boolean isStatic = (access & Opcodes.ACC_STATIC) != 0;
            discoveredMethods.add(new MethodReference(
                    classHandle,
                    name,
                    desc,
                    isStatic,isAbstract));
        }
        return super.visitMethod(access, name, desc, signature, exceptions);

    }

    @Override
    public void visitEnd() {
        if(methodName==null){
            ClassReference classReference = new ClassReference(
                    name,
                    superName,
                    Arrays.asList(interfaces),
                    isInterface,
                    Variables,
                    isAbstract);
            discoveredClasses.add(classReference);
        }
        super.visitEnd();
    }
}
