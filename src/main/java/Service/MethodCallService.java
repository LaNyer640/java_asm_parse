package Service;

import asm.MethodCallClassVisitor;
import model.ClassFile;
import model.MethodReference;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class MethodCallService {
    public static void start(List<ClassFile> classFileList, Map<MethodReference.Handle, Set<MethodReference.Handle>> methodCall){
        for (ClassFile file : classFileList) {
            try {
                MethodCallClassVisitor dcv = new MethodCallClassVisitor(Opcodes.ASM7,methodCall);
                ClassReader cr = new ClassReader(file.getFile());
                cr.accept(dcv, ClassReader.EXPAND_FRAMES);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
