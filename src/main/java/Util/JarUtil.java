package Util;


import model.ClassFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JarUtil {

    private Set<ClassFile> classFileSet = new HashSet<>();

    public  void resolveJarFile(String jarPath) {
        try {
            final Path tmpDir = Files.createTempDirectory(
                    Paths.get(jarPath).getFileName().toString() + "_");
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                DirUtil.removeDir(tmpDir.toFile());
            }));
            resolve(jarPath, tmpDir);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public  List<ClassFile> resolveNormalJarFile(String jarPath) {
        try {
            final Path tmpDir = Files.createTempDirectory(
                    Paths.get(jarPath).getFileName().toString() + "_");
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                DirUtil.removeDir(tmpDir.toFile());
            }));
            resolve(jarPath, tmpDir);
            return new ArrayList<>(classFileSet);
        } catch (Exception e) {
            System.out.println(e);
        }
        return new ArrayList<>();
    }

    private void resolve(String jarPath, Path tmpDir) {
        try {
            InputStream is = new FileInputStream(jarPath);
            JarInputStream jarInputStream = new JarInputStream(is);
            JarEntry jarEntry;
            while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
                Path fullPath = tmpDir.resolve(jarEntry.getName());
                if (!jarEntry.isDirectory()) {
                    if (!jarEntry.getName().endsWith(".class")) {
                        continue;
                    }
                    Path dirName = fullPath.getParent();
                    if (!Files.exists(dirName)) {
                        Files.createDirectories(dirName);
                    }
                    OutputStream outputStream = Files.newOutputStream(fullPath);
                    IOUtil.copy(jarInputStream, outputStream);
                    ClassFile classFile = new ClassFile(jarEntry.getName(), fullPath);
                    classFileSet.add(classFile);
                }
            }
        } catch (Exception e) {
        }
    }


    public  List<ClassFile> resolveSpringBootJarFile(String jarPath, boolean useAllLib) {
        try {
            final Path tmpDir = Files.createTempDirectory(
                    Paths.get(jarPath).getFileName().toString() + "_");
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                DirUtil.removeDir(tmpDir.toFile());
            }));
            resolve(jarPath, tmpDir);
            if (useAllLib) {
                resolveBoot(jarPath, tmpDir);
                if(Files.exists(tmpDir)){
                    allfile(tmpDir.toFile());
                }
            }
            return new ArrayList<>(classFileSet);
        } catch (Exception e) {
            System.out.println(e);
        }
        return new ArrayList<>();
    }

    private void resolveBoot(String jarPath, Path tmpDir) {
        try {
            InputStream is = new FileInputStream(jarPath);
            JarInputStream jarInputStream = new JarInputStream(is);
            JarEntry jarEntry;
            while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
                Path fullPath = tmpDir.resolve(jarEntry.getName());
                if (!jarEntry.isDirectory()) {
                    if (!jarEntry.getName().endsWith(".jar")) {
                        continue;
                    }
                    Path dirName = fullPath.getParent();
                    if (!Files.exists(dirName)) {
                        Files.createDirectories(dirName);
                    }
                    OutputStream outputStream = Files.newOutputStream(fullPath);
                    IOUtil.copy(jarInputStream, outputStream);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    public void allfile(File file) {
        File[] files = file.listFiles();
        if(files!=null && files.length>0) {
            for(File temp:files) {
                //???????????????????????????????????????
                if(temp.isDirectory()) {
                    allfile(temp);
                }else if(temp.getAbsoluteFile().getName().endsWith(".jar")){
                    resolveJarFile(temp.getAbsolutePath());
                }
            }
        }
    }
}

