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
            classFileSet.addAll(JarUtil.resolveSpringBootJarFile(jarPath, useAllLib));
            String jarName = jarPath;
            for(ClassFile classFile:classFileSet){
                jarByClass.put(classFile.getClassName().split("\\.")[0],jarName);
            }
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
        classFileSet.addAll(JarUtil.resolveNormalJarFile(rtJarPath));
    }

}
