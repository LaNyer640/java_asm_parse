package Service;

import asm.SourceClassVisitor;
import model.ClassFile;
import model.MethodReference;
import org.objectweb.asm.ClassReader;

import java.util.List;

public class LoadSource {
    public static void loadsource(List<MethodReference> Sources, List<ClassFile> classFileList,String sourcePattern) {
        for (ClassFile file : classFileList) {
            try {
                ClassReader cr = new ClassReader(file.getFile());
                SourceClassVisitor cv = new SourceClassVisitor(Sources,sourcePattern);
                cr.accept(cv, ClassReader.EXPAND_FRAMES);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    public static void loadsource(List<MethodReference> Sources, List<ClassFile> classFileList) {
        for (ClassFile file : classFileList) {
            try {
                ClassReader cr = new ClassReader(file.getFile());
                SourceClassVisitor cv = new SourceClassVisitor(Sources);
                cr.accept(cv, ClassReader.EXPAND_FRAMES);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


}
