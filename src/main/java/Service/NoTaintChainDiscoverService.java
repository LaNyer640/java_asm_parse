package Service;

import Util.WriteUtil;
import asm.VulnClassVisitor;
import model.*;
import org.objectweb.asm.ClassReader;

import java.nio.file.Paths;
import java.util.*;

public class NoTaintChainDiscoverService {
    private final Map<String, ClassFile> classFileByName;
    private final InheritanceMap InheritanceMap;
    private final Map<MethodReference.Handle, Set<MethodReference.Handle>> methodCall;
    private final Map<MethodReference.Handle, MethodReference> methodMap;
    private final Map<ClassReference.Handle, ClassReference> classMap;

    private final Map<MethodReference.Handle, Set<MethodReference.Handle>> methodImplCall = new HashMap<>();
    private Map<MethodReference.Handle, Set<CallGraph>> methodImplCallMap;
    private final List<MethodReference> Sources;

    private final List<List<Sink>> Sinks;
    private final List<Deque<MethodReference.Handle>> stacks;

    public NoTaintChainDiscoverService(Map<String, ClassFile> classFileByName, InheritanceMap InheritanceMap, Map<MethodReference.Handle, Set<MethodReference.Handle>> methodCall,
                                Map<MethodReference.Handle, MethodReference> methodMap, Map<ClassReference.Handle, ClassReference> classMap, List<MethodReference> Sources, List<List<Sink>> Sinks,List<Deque<MethodReference.Handle>> stacks){
        this.classFileByName = classFileByName;
        this.InheritanceMap = InheritanceMap;
        this.methodCall = methodCall;
        this.methodMap = methodMap;
        this.classMap =classMap;
        this.Sources = Sources;
        this.Sinks = Sinks;
        this.stacks = stacks;
    }

    public void start(){
        methodImplCallMap = preparemethodCall(methodMap);
        doDiscover(Sources,Sinks);
    }



    private Map<MethodReference.Handle, Set<CallGraph>> preparemethodCall(Map<MethodReference.Handle, MethodReference> methodMap){
        Map<MethodReference.Handle, Set<CallGraph>> methodImplCallMap = new HashMap<>();
        Map<MethodReference.Handle, Set<MethodReference.Handle>> methodImplMap = InheritanceService.getAllMethodImplementations(InheritanceMap, methodMap);
        methodImplCall.putAll(methodCall);
        for (Map.Entry<MethodReference.Handle, Set<MethodReference.Handle>> entry: methodCall.entrySet()){
            for(MethodReference.Handle TargetMethod: entry.getValue()){
                ClassReference.Handle handle = TargetMethod.getClassReference();
                ClassReference classReference = classMap.get(handle);
                if (classReference != null && (classReference.isInterface()||classReference.isAbstract())) {
                    Set<MethodReference.Handle> methodImp = methodImplMap.get(TargetMethod);
                    if(methodImp!=null){
                        for ( MethodReference.Handle a :methodImp){
                            methodImplCall.get(TargetMethod).add(a);
                        }
                    }
                }
            }
        }

        WriteUtil.SaveMethodCall(Paths.get("methodCall.dat"),methodImplCall);
        for (Map.Entry<MethodReference.Handle, Set<MethodReference.Handle>> entry: methodImplCall.entrySet()) {
            for (MethodReference.Handle TargetMethod : entry.getValue()) {
                CallGraph callGraph = new CallGraph(entry.getKey(),TargetMethod);
                if (!methodImplCallMap.containsKey(entry.getKey())) {
                    Set<CallGraph> graphCalls = new HashSet<>();
                    graphCalls.add(callGraph);
                    methodImplCallMap.put(entry.getKey(), graphCalls);
                } else {
                    methodImplCallMap.get(entry.getKey()).add(callGraph);
                }
            }
        }


        return methodImplCallMap;
    }

    public void doDiscover(List<MethodReference> Sources , List<List<Sink>> Sinks){
        for(MethodReference Source : Sources){
            System.out.println(Source.getName());
            Set<CallGraph> calls = methodImplCallMap.get(Source.getHandle());
            if(calls!=null){
                for(CallGraph callGraph : calls){
                    for(List<Sink> sink : Sinks){
                        LinkedList<MethodReference.Handle> stack = new LinkedList<>();
                        stack.push(callGraph.getCallerMethod());
                        doTask(callGraph.getCallerMethod(),callGraph.getTargetMethod(), stack,sink);
                        stack.pop();
                    }
                }
            }
        }
    }


    private void doTask(MethodReference.Handle callerMethod,MethodReference.Handle targetMethod,
                        Deque<MethodReference.Handle> stack,List<Sink> sinks) {
        if (stack.contains(targetMethod)) {
            stack.push(targetMethod);
            return;
        }
        if (sinks.size() == 1 && isFirstSink(sinks, targetMethod)) {
            System.out.println("[+] detect vuln: " + sinks.get(0).getName());
            Deque<MethodReference.Handle> copyStack = new LinkedList<>(stack);
            stacks.add(copyStack);
            printStackTrace(stack);
        }
        if (sinks.size() != 1 && isFirstSink(sinks, targetMethod)) {
            ClassFile file = classFileByName.get(callerMethod.getClassReference().getName());
            VulnClassVisitor dcv = new VulnClassVisitor(callerMethod, sinks);
            ClassReader cr = new ClassReader(file.getFile());
            cr.accept(dcv, ClassReader.EXPAND_FRAMES);
            if (dcv.getVulnFlag() == sinks.size()) {
                /*Set<MethodReference.Handle> stackpushmethod = methodImplCall.get(callerMethod);
                for (int s = 1; s < sinks.size(); s++) {
                    for (MethodReference.Handle a : stackpushmethod) {
                        if ((a.getClassReference().getName() + a.getName()).equals(sinks.get(s).getClassName() + sinks.get(s).getName())) {
                            stack.push(a);
                        }
                    }
                }*/
                System.out.println("[+] detect vuln: " + sinks.get(0).getName());
                Deque<MethodReference.Handle> copyStack = new LinkedList<>(stack);
                stacks.add(copyStack);
                printStackTrace(stack);
                /*for (int s = 1; s == sinks.size(); s++) {
                    stack.pop();
                }*/
                return;
            }
        }
        Set<CallGraph> calls = methodImplCallMap.get(targetMethod);
        if (calls == null || calls.size() == 0) {
            return;
        }
        for (CallGraph callGraph : calls) {
            doTask(callGraph.getCallerMethod(), callGraph.getTargetMethod(), stack, sinks);
            stack.pop();
        }
    }
    private void printStackTrace(Deque<MethodReference.Handle> stack) {
        Deque<MethodReference.Handle> copyStack = new LinkedList<>(stack);
        StringBuilder prefix=new StringBuilder("\t");
        for (MethodReference.Handle handle : copyStack) {
            System.out.println(prefix+handle.getClassReference().getName()+"."+handle.getName());
            prefix.append("\t");
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
