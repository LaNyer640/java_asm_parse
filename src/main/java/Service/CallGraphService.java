package Service;

import asm.CallGraphClassVisitor;
import model.*;
import org.objectweb.asm.ClassReader;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CallGraphService {
    public static void start(Map<ClassReference.Handle, ClassReference> classMap,
                             InheritanceMap inheritanceMap,
                             Map<MethodReference.Handle, Set<Integer>> passthroughDataflow,
                             Set<CallGraph> discoveredCalls, List<ClassFile> classFileList){
        for (ClassFile file : classFileList) {
            try {
                CallGraphClassVisitor cv = new CallGraphClassVisitor(classMap, inheritanceMap, passthroughDataflow,discoveredCalls);
                ClassReader cr = new ClassReader(file.getFile());
                cr.accept(cv, ClassReader.EXPAND_FRAMES);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
