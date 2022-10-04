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
    private final Map<MethodReference.Handle, MethodReference> methodMap;

    private Set<CallGraph> discoveredCalls;
    private final Map<ClassReference.Handle, ClassReference> classMap;
    private Map<MethodReference.Handle, Set<MethodReference.Handle>> methodImplCall = new HashMap<>();

    private final List<List<Sink>> Sinks;
    private final Map<String,String> jarByClass;

    private int VulnNumber = 1;


    public onlySinkParseServerice(Map<String, ClassFile> classFileByName, InheritanceMap InheritanceMap,Set<CallGraph> discoveredCalls,
                                       Map<MethodReference.Handle, MethodReference> methodMap, Map<ClassReference.Handle,
            ClassReference> classMap, List<List<Sink>> Sinks,Map jarByClass){
        this.classFileByName = classFileByName;
        this.InheritanceMap = InheritanceMap;
        this.methodMap = methodMap;
        this.discoveredCalls = discoveredCalls;
        this.classMap =classMap;
        this.Sinks = Sinks;
        this.jarByClass = jarByClass;
    }

    public void start(){
        prepareCallGraphMap(methodMap);
        doDiscover(Sinks);
    }


    /*
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
        WriteUtil.SaveMethodCall(Paths.get("methodImpCall.dat"),methodImplCall);
    }
    */

    private void prepareCallGraphMap(Map<MethodReference.Handle, MethodReference> methodMap) {
        Map<MethodReference.Handle, Set<CallGraph>> graphCallMap = new HashMap<>();

        Map<MethodReference.Handle, Set<MethodReference.Handle>> methodImplMap = InheritanceService.getAllMethodImplementations(InheritanceMap, methodMap);
        WriteUtil.SavemethodImplMap(Paths.get("methodImplMap.dat"), methodImplMap);

        List<CallGraph> tempList = new ArrayList<>(discoveredCalls);
        for (int i = 0; i < discoveredCalls.size(); i++) {
            CallGraph edge = tempList.get(i);
            ClassReference.Handle handle = edge.getTargetMethod().getClassReference();
            ClassReference classReference = classMap.get(handle);
            if (classReference != null && classReference.isInterface()) {
                Set<MethodReference.Handle> implSet = methodImplMap.get(edge.getTargetMethod());
                if (implSet == null || implSet.size() == 0) {
                    continue;
                }
                for (MethodReference.Handle methodHandle : implSet) {
                    String callerDesc = methodMap.get(methodHandle).getDesc();
                    if (edge.getTargetMethod().getDesc().equals(callerDesc)) {
                        tempList.add(new CallGraph(
                                edge.getCallerMethod(),
                                methodHandle,
                                edge.getCallerArgIndex(),
                                edge.getTargetArgIndex()
                        ));
                    }
                }
            }
        }
        discoveredCalls.clear();
        discoveredCalls.addAll(tempList);
    }

    public void doDiscover(List<List<Sink>> Sinks){
        for(List<Sink> sink : Sinks){
            for(CallGraph CallGraph:discoveredCalls) {
                if (sink.size() == 1 && isFirstSink(sink, CallGraph.getTargetMethod()) && isblacklist(CallGraph.getCallerMethod().getClassReference().getName(), sink.get(0).getClassName())&&CallGraph.getCallerArgIndex()!=0) {
                    //&& isblacklist(entry.getKey().getClassReference().getName(),sink.get(0).getClassName())
                    System.out.println();
                    System.out.println("[" + VulnNumber + "] detect vuln: " + sink.get(0).getSinkName() + "  Name: " + sink.get(0).getName());
                    System.out.println("jar is: " + jarByClass.get(CallGraph.getCallerMethod().getClassReference().getName()));
                    System.out.println("location is: " + CallGraph.getCallerMethod().getClassReference().getName() + ":" + CallGraph.getCallerMethod().getName());
                    VulnNumber++;
                }
                if (sink.size() != 1 && isFirstSink(sink, CallGraph.getTargetMethod())&& isblacklist(CallGraph.getCallerMethod().getClassReference().getName(), sink.get(0).getClassName())&&CallGraph.getCallerArgIndex()!=0) {
                    ClassFile file = classFileByName.get(CallGraph.getCallerMethod().getClassReference().getName());
                    VulnClassVisitor dcv = new VulnClassVisitor(CallGraph.getCallerMethod(), sink);
                    ClassReader cr = new ClassReader(file.getFile());
                    cr.accept(dcv, ClassReader.EXPAND_FRAMES);
                    if (dcv.getVulnFlag() == sink.size() && !CallGraph.getCallerMethod().getClassReference().getName().equals(sink.get(0).getClassName())) {
                        System.out.println();
                        System.out.println("[" + VulnNumber + "] detect vuln: " + sink.get(0).getSinkName() + "  Name: " + sink.get(0).getName());
                        System.out.println("jar is: " + jarByClass.get(CallGraph.getCallerMethod().getClassReference().getName()));
                        System.out.println("location is:" + CallGraph.getCallerMethod().getClassReference().getName() + ":" + CallGraph.getCallerMethod().getName());
                        VulnNumber++;
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

    public boolean isblacklist(String ClassName,String sinkClassName){
        int flag = 0;
        if(ClassName.equals(sinkClassName)){
            flag++;
        }
        if(flag==0){
            for(String black : blacklist){
                if(ClassName.contains(black)){
                    flag++;
                    break;
                }
            }
        }
        if(flag!=0){
            return false;
        } else {
            return true;
        }
    }

    private final String[] blacklist = {
            "org/apache/naming/",
            "org/springframework/jmx/",
            "org/springframework/jndi/",
            "org/apache/catalina/"
    };
}
