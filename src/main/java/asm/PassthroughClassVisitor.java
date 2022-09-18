package asm;

import model.ClassReference;
import model.InheritanceMap;
import model.MethodReference;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.JSRInlinerAdapter;

import java.util.Map;
import java.util.Set;

public class PassthroughClassVisitor extends ClassVisitor {
    private final Map<ClassReference.Handle, ClassReference> classMap;
    private final InheritanceMap inheritanceMap;
    private final MethodReference.Handle methodToVisit;
    private final Map<MethodReference.Handle, Set<Integer>> passthroughDataflow;
    private String name;
    private PassthroughMethodAdapter PassthroughMethodAdapter;

    public PassthroughClassVisitor(Map<ClassReference.Handle, ClassReference> classMap, InheritanceMap inheritanceMap,
                                   Map<MethodReference.Handle, Set<Integer>> passthroughDataflow,
                                   int api, MethodReference.Handle methodToVisit) {
        super(api);
        this.classMap = classMap;
        this.inheritanceMap = inheritanceMap;
        this.methodToVisit = methodToVisit;
        this.passthroughDataflow = passthroughDataflow;
    }

    @Override
    public void visit(int version, int access, String name, String signature,
                      String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.name = name;
        //不是目标观察的class跳过
        if (!this.name.equals(methodToVisit.getClassReference().getName())) {
            return;
        }
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {
        //不是目标观察的method需要跳过，上一步得到的method都是有调用关系的method才需要数据流分析
        if (!name.equals(methodToVisit.getName()) || !desc.equals(methodToVisit.getDesc())) {
            return null;
        }
        if (PassthroughMethodAdapter != null) {
            return null;
        }

        //对method进行观察
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        PassthroughMethodAdapter = new PassthroughMethodAdapter(
                classMap, inheritanceMap, this.passthroughDataflow,
                api, mv, this.name, access, name, desc, signature, exceptions);
        return new JSRInlinerAdapter(PassthroughMethodAdapter, access, name, desc, signature, exceptions);
    }

    public Set<Integer> getReturnTaint() {
        if (PassthroughMethodAdapter == null) {
            return null;
        }
        return PassthroughMethodAdapter.getReturnTaint();
    }
}
