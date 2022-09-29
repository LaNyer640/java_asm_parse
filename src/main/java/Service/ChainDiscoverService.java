package Service;

import Util.WriteUtil;
import asm.VulnClassVisitor;
import model.*;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;

import java.nio.file.Paths;
import java.util.*;

public class ChainDiscoverService {
    private final Map<String, ClassFile> classFileByName;
    private final InheritanceMap InheritanceMap;
    private final Set<CallGraph> discoveredCalls;
    private final Map<MethodReference.Handle, MethodReference> methodMap;
    private final Map<ClassReference.Handle, ClassReference> classMap;
    private Map<MethodReference.Handle, Set<CallGraph>> callGraphMap;

    List<MethodReference> Sources;

    List<List<Sink>> Sinks;

    public ChainDiscoverService(Map<String, ClassFile> classFileByName, InheritanceMap InheritanceMap,Set<CallGraph> discoveredCalls,
                                Map<MethodReference.Handle, MethodReference> methodMap, Map<ClassReference.Handle, ClassReference> classMap,List<MethodReference> Sources,List<List<Sink>> Sinks){
        this.classFileByName = classFileByName;
        this.InheritanceMap = InheritanceMap;
        this.discoveredCalls = discoveredCalls;
        this.methodMap = methodMap;
        this.classMap =classMap;
        this.Sources = Sources;
        this.Sinks = Sinks;
    }


    public void start(){
        callGraphMap = prepareCallGraphMap(methodMap);
        WriteUtil.SavecallGraphMap(Paths.get("callGraphMap.dat"),callGraphMap);
        doDiscover(Sources,Sinks);
    }


    private Map<MethodReference.Handle, Set<CallGraph>> prepareCallGraphMap(Map<MethodReference.Handle, MethodReference> methodMap){
        Map<MethodReference.Handle, Set<CallGraph>> graphCallMap=new HashMap<>();

        Map<MethodReference.Handle, Set<MethodReference.Handle>> methodImplMap = InheritanceService.getAllMethodImplementations(InheritanceMap, methodMap);
        WriteUtil.SavemethodImplMap(Paths.get("methodImplMap.dat"),methodImplMap);

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
        for (CallGraph graphCall : discoveredCalls) {
            MethodReference.Handle caller = graphCall.getCallerMethod();
            if (!graphCallMap.containsKey(caller)) {
                Set<CallGraph> graphCalls = new HashSet<>();
                graphCalls.add(graphCall);
                graphCallMap.put(caller, graphCalls);
            } else {
                graphCallMap.get(caller).add(graphCall);
            }
        }
        return graphCallMap;
    }

    public void doDiscover(List<MethodReference> Sources , List<List<Sink>> Sinks){
        for(MethodReference Source : Sources){
            Type[] argTypes = Type.getArgumentTypes(Source.getDesc());
            Type[] extendedArgTypes = new Type[argTypes.length + 1];
            System.arraycopy(argTypes, 0, extendedArgTypes, 1, argTypes.length);
            argTypes = extendedArgTypes;
            boolean[] vulnerableIndex = new boolean[argTypes.length];
            for (int i = 1; i < argTypes.length; i++) {
                if (!isPrimitive(argTypes[i])) {
                    vulnerableIndex[i] = true;
                }
            }
            Set<CallGraph> calls = callGraphMap.get(Source.getHandle());
            if(calls == null || calls.size() ==0){
                continue;
            }

            for (CallGraph callGraph : calls){
                int callerIndex = callGraph.getCallerArgIndex();
                if(callerIndex == -1){
                    continue;
                }
                if(vulnerableIndex[callerIndex]){
                    for(List<Sink> sink : Sinks){
                        LinkedList<MethodReference.Handle> stack = new LinkedList<>();
                        stack.push(callGraph.getCallerMethod());
                        doTask(callGraph.getCallerMethod(),callGraph.getTargetMethod(), callGraph.getTargetArgIndex(), stack,sink);
                    }
                }
            }
        }
    }

    private boolean isPrimitive(Type argType) {
        int sort = argType.getSort();
        return sort==Type.BYTE||
                sort==Type.INT||
                sort==Type.SHORT||
                sort==Type.LONG||
                sort==Type.FLOAT||
                sort==Type.DOUBLE||
                sort==Type.BOOLEAN||
                sort==Type.CHAR;
    }


    private void doTask(MethodReference.Handle callerMethod,MethodReference.Handle targetMethod, int targetIndex,
                        Deque<MethodReference.Handle> stack,List<Sink> sinks) {
        if (stack.contains(targetMethod)) {
            stack.push(targetMethod);
            return;
        }
        stack.push(targetMethod);
        if(sinks.size()==1&&isFirstSink(sinks,targetMethod,targetIndex)){
            System.out.println("[+] detect vuln: " + sinks.get(0).getSinkName());
            printStackTrace(stack);
            System.out.println();
            System.out.println();
        }
        if(sinks.size()!=1&&isFirstSink(sinks,targetMethod,targetIndex)){
            ClassFile file = classFileByName.get(callerMethod.getClassReference().getName());
            VulnClassVisitor dcv = new VulnClassVisitor(callerMethod,sinks);
            ClassReader cr = new ClassReader(file.getFile());
            cr.accept(dcv, ClassReader.EXPAND_FRAMES);
            if(dcv.getVulnFlag()==sinks.size()){
                System.out.println("[+] detect vuln: " + sinks.get(0).getSinkName());
                printStackTrace(stack);
                System.out.println();
                System.out.println();
                return;
            }

        }
        Set<CallGraph> calls = callGraphMap.get(targetMethod);
        if (calls == null || calls.size() == 0) {
            return;
        }
        for (CallGraph callGraph : calls) {
            if (callGraph.getCallerArgIndex() == targetIndex && targetIndex != -1) {
                doTask(callGraph.getCallerMethod(),callGraph.getTargetMethod(), callGraph.getTargetArgIndex(), stack, sinks);
                stack.pop();
            }
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

    public boolean isFirstSink(List<Sink> sinks,MethodReference.Handle targetMethod,int targetIndex){
        /*System.out.println("SinkName:"+sinks.get(0).getName()+" targetMethodName:"+targetMethod.getName()+
                " SinkClass:"+sinks.get(0).getClassName()+"targetMethodClass:"+targetMethod.getClassReference().getName()+ "sinkDec:"+sinks.get(0).getDesc()
        +" sinkTarget:Index:"+ sinks.get(0).getTargetIndex());*/
        if(sinks.get(0).getName().equals(targetMethod.getName())&&sinks.get(0).getClassName().equals(targetMethod.getClassReference().getName())&&
                (sinks.get(0).getDesc().equals(targetMethod)||sinks.get(0).getDesc().equals("*"))&&
                (targetIndex==sinks.get(0).getTargetIndex()||sinks.get(0).getTargetIndex()==-1)){
            return true;
        }
        return false;
    }

}
