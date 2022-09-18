package Service;

import Util.WriteUtil;
import asm.PassthroughClassVisitor;
import javassist.bytecode.stackmap.TypeData;
import model.ClassFile;
import model.ClassReference;
import model.InheritanceMap;
import model.MethodReference;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

import java.nio.file.Paths;
import java.util.*;

public class PassthroughService {
    public static void start(Map<String,ClassFile> ClassFileByName, Map<ClassReference.Handle, ClassReference> classMap, InheritanceMap InheritanceMap,
                             Map<MethodReference.Handle, Set<MethodReference.Handle>> methodCall, Map<MethodReference.Handle, Set<Integer>> passthroughDataflow){
        List<MethodReference.Handle> sortedMethods = BaseDfsSortMethod(methodCall);
        WriteUtil.SaveSortedMethods(Paths.get("sortedMethods.dat"),sortedMethods);
        calculatePassthroughDataflow(ClassFileByName, classMap, InheritanceMap, sortedMethods,passthroughDataflow);
    }



    public static List<MethodReference.Handle> BaseDfsSortMethod(Map<MethodReference.Handle, Set<MethodReference.Handle>> methodCall){
        Map<MethodReference.Handle, Set<MethodReference.Handle>> outgoingReferences = new HashMap<>();
        for (Map.Entry<MethodReference.Handle, Set<MethodReference.Handle>> entry : methodCall.entrySet()) {
            MethodReference.Handle method = entry.getKey();
            outgoingReferences.put(method, new HashSet<>(entry.getValue()));
        }
        Set<MethodReference.Handle> dfsStack = new HashSet<>();
        Set<MethodReference.Handle> visitedNodes = new HashSet<>();
        List<MethodReference.Handle> sortedMethods = new ArrayList<>(outgoingReferences.size());
        for (MethodReference.Handle root : outgoingReferences.keySet()) {
            dfsSort(outgoingReferences, sortedMethods, visitedNodes, dfsStack, root);
        }
        return sortedMethods;
    }

    //使用dfs排序将所有方法进行排序，这样做的目的是让前面0出度的方法先被分析，那么后面进行分析的时候，前面的方法必定被分析过
    private static void dfsSort(Map<MethodReference.Handle, Set<MethodReference.Handle>> outgoingReferences, List<MethodReference.Handle> sortedMethods,
                                Set<MethodReference.Handle> visitedNodes, Set<MethodReference.Handle> dfsStack, MethodReference.Handle root) {
        if (dfsStack.contains(root)) {
            return;
        }
        if (visitedNodes.contains(root)) {
            return;
        }
        Set<MethodReference.Handle> outgoingRefs = outgoingReferences.get(root);
        if (outgoingRefs == null) {
            return;
        }
        dfsStack.add(root);
        for (MethodReference.Handle child : outgoingRefs) {
            dfsSort(outgoingReferences, sortedMethods, visitedNodes, dfsStack, child);
        }
        dfsStack.remove(root);
        visitedNodes.add(root);
        sortedMethods.add(root);
    }


    private static Map<MethodReference.Handle, Set<Integer>> calculatePassthroughDataflow(Map<String,ClassFile> ClassFileByName, Map<ClassReference.Handle, ClassReference> classMap, InheritanceMap inheritanceMap, List<MethodReference.Handle> sortedMethods,Map<MethodReference.Handle,Set<Integer>> passthroughDataflow) {
        //遍历所有方法，然后asm观察所属类，经过前面DFS的排序，调用链最末端的方法在最前面
        for (MethodReference.Handle method : sortedMethods) {
            //跳过static静态初始化代码
            if (method.getName().equals("<clinit>")) {
                continue;
            }
            //获取所属类进行观察
            ClassFile file = ClassFileByName.get(method.getClassReference().getName());
            try {
                PassthroughClassVisitor dcv = new PassthroughClassVisitor(classMap,inheritanceMap,passthroughDataflow,Opcodes.ASM6,method);
                ClassReader cr = new ClassReader(file.getFile());
                cr.accept(dcv, ClassReader.EXPAND_FRAMES);
                passthroughDataflow.put(method, dcv.getReturnTaint());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return passthroughDataflow;
    }

}
