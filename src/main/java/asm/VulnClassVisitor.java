package asm;

import model.InheritanceMap;
import model.MethodReference;
import model.Sink;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.JSRInlinerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VulnClassVisitor extends ClassVisitor {

    private String name;
    private MethodReference.Handle methodHandle;
    private final List<Sink> sinks;


    private VulnMethodAdapter vulnMethodAdapter;

    public VulnClassVisitor(MethodReference.Handle callerMethod,List<Sink> sinks) {
        super(Opcodes.ASM6);
        this.methodHandle = callerMethod;
        this.sinks = sinks;
    }

    @Override
    public void visit(int version, int access, String name, String signature,
                      String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.name = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor,
                                     String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (name.equals(this.methodHandle.getName())) {
            VulnMethodAdapter vulnMethodAdapter = new VulnMethodAdapter(
                    this.sinks,Opcodes.ASM6
            );
            this.vulnMethodAdapter = vulnMethodAdapter;
            return new JSRInlinerAdapter(vulnMethodAdapter,
                    access, name, descriptor, signature, exceptions);
        }
        return mv;
    }

    public Integer getVulnFlag() {
        if (vulnMethodAdapter == null) {
            return null;
        }
        return vulnMethodAdapter.getVulnFlag();
    }
}