package Util;

import javassist.bytecode.stackmap.TypeData;
import model.ClassFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ClassUtil {
    public static List<ClassFile> getAllClassesFromBoots(List<String> bootPathList,
                                                         boolean runtime,
                                                         boolean useAllLib, Map jarByClass) {
        Set<ClassFile> classFileSet = new HashSet<>();
        if (runtime) {
            getRuntime(classFileSet);
        }
        for (String jarPath : bootPathList) {
            JarUtil JarUtil = new JarUtil();
            List<ClassFile> jarclassist = JarUtil.resolveSpringBootJarFile(jarPath, useAllLib);
            String jarName = jarPath;
            for(ClassFile classFile:jarclassist){
                jarByClass.put(classFile.getClassName().split("\\.")[0],jarName);
            }
            classFileSet.addAll(jarclassist);
        }
        return new ArrayList<>(classFileSet);
    }
    private static void getRuntime(Set<ClassFile> classFileSet) {
        String rtJarPath = System.getenv("JAVA_HOME") +
                File.separator + "jre" +
                File.separator + "lib" +
                File.separator + "rt.jar";
        Path rtPath = Paths.get(rtJarPath);
        if (!Files.exists(rtPath)) {
            throw new RuntimeException("rt.jar not exists");
        }
        JarUtil JarUtil = new JarUtil();
        classFileSet.addAll(JarUtil.resolveNormalJarFile(rtJarPath));
    }

}
