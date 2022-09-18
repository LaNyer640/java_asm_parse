package Util;

import com.google.common.io.Files;
import model.CallGraph;
import model.ClassReference;
import model.MethodReference;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WriteUtil {
    public static void SaveSortedMethods(Path filePath,List<MethodReference.Handle> sortedMethods){
        try (BufferedWriter writer = Files.newWriter(filePath.toFile(), StandardCharsets.UTF_8)) {
                StringBuilder sb = new StringBuilder();
                for (MethodReference.Handle method : sortedMethods) {
                    String MethodName =method.getName();
                    String ClassName  =method.getClassReference().getName();
                    if (method == null) {
                        sb.append("\n");
                    } else {
                        sb.append("\n").append(ClassName).append("#").append(MethodName);
                    }
                }
                writer.write(sb.substring(1));
                writer.write("\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    };

    public static void SaveAllClass(Path filePath, Map<ClassReference.Handle, ClassReference> classMap) {
        try (BufferedWriter writer = Files.newWriter(filePath.toFile(), StandardCharsets.UTF_8)) {
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<ClassReference.Handle,ClassReference> entry: classMap.entrySet()) {
                    String ClassName = entry.getKey().getName();
                    if (ClassName == null) {
                        sb.append("\n");
                    } else {
                        sb.append("\n").append(ClassName);
                    }
                }
                writer.write(sb.substring(1));
                writer.write("\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    };

    public static void SaveMethodCall(Path filePath, Map<MethodReference.Handle, Set<MethodReference.Handle>> methodCall) {
        try (BufferedWriter writer = Files.newWriter(filePath.toFile(), StandardCharsets.UTF_8)) {
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<MethodReference.Handle, Set<MethodReference.Handle>> entry: methodCall.entrySet()) {
                    String MethodName = entry.getKey().getName();
                    sb.append("\n").append(entry.getKey().getClassReference().getName()).append("#").append(MethodName);
                    for(MethodReference.Handle TargetMethod: entry.getValue()){
                        String TargetMethodName = TargetMethod.getName();
                        if (TargetMethodName == null) {
                            sb.append("\n");
                        } else {
                            sb.append("\n\t").append(TargetMethod.getClassReference().getName()).append("#").append(TargetMethodName);
                        }
                    }
                }
            writer.write(String.valueOf(sb));
            writer.write("\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void SavePassthroughs(Path filePath, Map<MethodReference.Handle, Set<Integer>> passthroughDataflow) {
        try (BufferedWriter writer = Files.newWriter(filePath.toFile(), StandardCharsets.UTF_8)) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<MethodReference.Handle, Set<Integer>> entry: passthroughDataflow.entrySet()) {
                String MethodName = entry.getKey().getName();
                String ClassName = entry.getKey().getClassReference().getName();
                sb.append("\n").append(ClassName).append("\t").append(MethodName).append("\t");
                if (entry.getValue() != null) {
                    sb.append("\n");
                }else {
                    sb.append(entry.getValue());
                }
            }
            writer.write(String.valueOf(sb));
            writer.write("\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void SavecallGraphMap(Path filePath, Map<MethodReference.Handle, Set<CallGraph>> callGraphMap) {
        try (BufferedWriter writer = Files.newWriter(filePath.toFile(), StandardCharsets.UTF_8)) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<MethodReference.Handle, Set<CallGraph>> entry: callGraphMap.entrySet()) {
                String MethodName = entry.getKey().getName();
                String ClassName = entry.getKey().getClassReference().getName();
                sb.append(ClassName).append("#").append(MethodName).append(":").append("\n");
                for(CallGraph CallGraph : entry.getValue()) {
                    sb.append("\t").append(CallGraph.getCallerMethod().getName()).append("==>")
                            .append(CallGraph.getTargetMethod().getClassReference().getName()).append("#").append(CallGraph.getTargetMethod().getName()).append("\t")
                            .append(CallGraph.getCallerArgIndex()).append("\t").append(CallGraph.getTargetArgIndex()).append("\n");
                }
            }
            writer.write(String.valueOf(sb));
            writer.write("\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void SavemethodImplMap(Path filePath, Map<MethodReference.Handle, Set<MethodReference.Handle>> methodImplMap) {
        try (BufferedWriter writer = Files.newWriter(filePath.toFile(), StandardCharsets.UTF_8)) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<MethodReference.Handle, Set<MethodReference.Handle>> entry: methodImplMap.entrySet()) {
                String MethodName = entry.getKey().getName();
                String ClassName = entry.getKey().getClassReference().getName();

                sb.append(ClassName).append("#").append(MethodName).append(":").append("\n");
                for(MethodReference.Handle Method : entry.getValue()) {
                    sb.append("\t").append(Method.getClassReference().getName()).append("#").append(Method.getName()).append("\n");
                }
            }
            writer.write(String.valueOf(sb));
            writer.write("\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
