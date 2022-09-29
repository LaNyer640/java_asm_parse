package Service;

import Util.WriteUtil;
import asm.VulnClassVisitor;
import model.*;
import org.objectweb.asm.ClassReader;

import java.nio.file.Paths;
import java.util.*;

public class onlySinkParseServerice {
    private final Map<String, ClassFile> classFileByName;
    private final model.InheritanceMap InheritanceMap;
    private final Map<MethodReference.Handle, Set<MethodReference.Handle>> methodCall;
    private final Map<MethodReference.Handle, MethodReference> methodMap;
    private final Map<ClassReference.Handle, ClassReference> classMap;

    private final Map<MethodReference.Handle, Set<MethodReference.Handle>> methodImplCall = new HashMap<>();

    private final List<List<Sink>> Sinks;

    private int VulnNumber = 1;

    public onlySinkParseServerice(Map<String, ClassFile> classFileByName, InheritanceMap InheritanceMap, Map<MethodReference.Handle, Set<MethodReference.Handle>> methodCall,
                                       Map<MethodReference.Handle, MethodReference> methodMap, Map<ClassReference.Handle, ClassReference> classMap, List<List<Sink>> Sinks){
        this.classFileByName = classFileByName;
        this.InheritanceMap = InheritanceMap;
        this.methodCall = methodCall;
        this.methodMap = methodMap;
        this.classMap =classMap;
        this.Sinks = Sinks;
    }

    public void start(){
        preparemethodCall(methodMap);
        doDiscover(Sinks);
    }



    private void preparemethodCall(Map<MethodReference.Handle, MethodReference> methodMap){
        Map<MethodReference.Handle, Set<MethodReference.Handle>> methodImplMap = InheritanceService.getAllMethodImplementations(InheritanceMap, methodMap);
        WriteUtil.SavemethodImplMap(Paths.get("methodImplMap.dat"),methodImplMap);
        methodImplCall.putAll(methodCall);
        for (Map.Entry<MethodReference.Handle, Set<MethodReference.Handle>> entry: methodCall.entrySet()){
            for(MethodReference.Handle TargetMethod: entry.getValue()){
                ClassReference.Handle handle = TargetMethod.getClassReference();
                ClassReference classReference = classMap.get(handle);
                if (classReference != null && classReference.isInterface()) {
                    Set<MethodReference.Handle> methodImp = methodImplMap.get(TargetMethod);
                    if(methodImp!=null){
                        for ( MethodReference.Handle a :methodImp){
                            methodImplCall.get(TargetMethod).add(a);
                        }
                    }
                }
            }
        }
    }



    public void doDiscover(List<List<Sink>> Sinks){
        for(List<Sink> sink : Sinks){
            for(Map.Entry<MethodReference.Handle, Set<MethodReference.Handle>> entry:methodImplCall.entrySet()) {
                List<MethodReference.Handle> VistedMethods = new ArrayList<>();
                for (MethodReference.Handle method : entry.getValue()) {
                    if(!VistedMethods.contains(method)) {
                        if (sink.size() == 1 && isFirstSink(sink, method) && !entry.getKey().getClassReference().getName().equals(sink.get(0).getClassName())) {
                            System.out.println();
                            System.out.println("[" + VulnNumber + "] detect vuln: " + sink.get(0).getSinkName() + "  Name: " + sink.get(0).getName());
                            System.out.println("location is:" + entry.getKey().getClassReference().getName() + ":" + entry.getKey().getName());
                            VulnNumber++;
                        }
                        if (sink.size() != 1 && isFirstSink(sink, method)) {
                            ClassFile file = classFileByName.get(entry.getKey().getClassReference().getName());
                            VulnClassVisitor dcv = new VulnClassVisitor(entry.getKey(), sink);
                            ClassReader cr = new ClassReader(file.getFile());
                            cr.accept(dcv, ClassReader.EXPAND_FRAMES);
                            if (dcv.getVulnFlag() == sink.size() && !entry.getKey().getClassReference().getName().equals(sink.get(0).getClassName())) {
                                System.out.println();
                                System.out.println("[" + VulnNumber + "] detect vuln: " + sink.get(0).getSinkName() + "  Name: " + sink.get(0).getName());
                                System.out.println("location is:" + entry.getKey().getClassReference().getName() + ":" + entry.getKey().getName());
                                VulnNumber++;
                            }
                        }
                        VistedMethods.add(method);
                    }
                }
            }
        }
    }



    public boolean isFirstSink(List<Sink> sinks,MethodReference.Handle targetMethod){
        /*System.out.println("SinkName:"+sinks.get(0).getName()+" targetMethodName:"+targetMethod.getName()+
                " SinkClass:"+sinks.get(0).getClassName()+"targetMethodClass:"+targetMethod.getClassReference().getName()+ "sinkDec:"+sinks.get(0).getDesc()
        +" sinkTarget:Index:"+ sinks.get(0).getTargetIndex());*/
        if(sinks.get(0).getName().equals(targetMethod.getName())&&sinks.get(0).getClassName().equals(targetMethod.getClassReference().getName())&&
                (sinks.get(0).getDesc().equals(targetMethod.getDesc())||sinks.get(0).getDesc().equals("*"))){
            return true;
        }
        return false;
    }
}
