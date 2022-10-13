package Service;

import Util.StackUtil;
import Util.WriteUtil;
import model.*;

import java.nio.file.Paths;
import java.util.*;

public class SinkParseService {
    private final model.InheritanceMap InheritanceMap;
    private final Map<MethodReference.Handle, Set<MethodReference.Handle>> methodCall;
    private final Map<MethodReference.Handle, MethodReference> methodMap;
    private final Map<Integer, MethodReference.Handle> MethodByNameMap = new HashMap<>();
    private final Map<ClassReference.Handle, ClassReference> classMap;

    private Map<MethodReference.Handle, Set<MethodReference.Handle>> methodImplCall = new HashMap<>();
    private Map<MethodReference.Handle, Set<CallGraph>> submethodImplCallMap = new HashMap<>();
    private final List<List<Sink>> Sinks;
    private List<Deque<MethodReference.Handle>> stacks;
    private HashMap<Integer,Integer> hashcodeMap = new HashMap<>();
    private int hashCode;

    public SinkParseService(InheritanceMap InheritanceMap, Map<MethodReference.Handle, Set<MethodReference.Handle>> methodCall,
                                       Map<MethodReference.Handle, MethodReference> methodMap, Map<ClassReference.Handle, ClassReference> classMap, List<List<Sink>> Sinks,List<Deque<MethodReference.Handle>> stacks){
        this.InheritanceMap = InheritanceMap;
        this.methodCall = methodCall;
        this.methodMap = methodMap;
        this.classMap = classMap;
        this.Sinks = Sinks;
        this.stacks = stacks;
    }

    public void start(){
        preparemethodCall(methodMap);
        doDiscover(Sinks);
    }
    private void preparemethodCall(Map<MethodReference.Handle, MethodReference> methodMap){
        Map<MethodReference.Handle, Set<MethodReference.Handle>> methodImplMap = InheritanceService.getAllMethodImplementations(InheritanceMap, methodMap);
        methodImplCall = StackUtil.clone(methodCall);
        for (Map.Entry<MethodReference.Handle, Set<MethodReference.Handle>> entry: methodCall.entrySet()){
            for(MethodReference.Handle TargetMethod: entry.getValue()){
                ClassReference.Handle handle = TargetMethod.getClassReference();
                ClassReference classReference = classMap.get(handle);
                if (classReference != null && (classReference.isInterface()||classReference.isAbstract())){
                    Set<MethodReference.Handle> methodImp = methodImplMap.get(TargetMethod);
                    if(methodImp!=null){
                        for ( MethodReference.Handle a : methodImp ){
                            methodImplCall.get(TargetMethod).add(a);
                        }
                    }
                }
            }
        }
        for (Map.Entry<MethodReference.Handle, Set<MethodReference.Handle>> entry: methodImplCall.entrySet()) {
            for (MethodReference.Handle TargetMethod : entry.getValue()) {
                CallGraph callGraph = new CallGraph(TargetMethod,entry.getKey());
                    if (!this.submethodImplCallMap.containsKey(TargetMethod)) {
                        Set<CallGraph> graphCalls = new HashSet<>();
                        graphCalls.add(callGraph);
                        this.submethodImplCallMap.put(TargetMethod, graphCalls);
                    } else{
                        this.submethodImplCallMap.get(TargetMethod).add(callGraph);
                }
            }
        }

        WriteUtil.SavecallGraphMap(Paths.get("submethodImplCallMap.dat"),submethodImplCallMap);
        for(Map.Entry<MethodReference.Handle, Set<MethodReference.Handle>> entry:methodImplCall.entrySet()){
            if (!MethodByNameMap.containsKey(entry.getKey().hashCode())) {
                hashcodeMap.put(entry.getKey().hashCode()-entry.getKey().getDesc().hashCode(),entry.getKey().hashCode());
                MethodByNameMap.put(entry.getKey().hashCode(), entry.getKey());
                for(MethodReference.Handle method: entry.getValue() ){
                    if(!MethodByNameMap.containsKey(method.hashCode())){
                        MethodByNameMap.put(method.hashCode(),method);
                        hashcodeMap.put(method.hashCode()-method.getDesc().hashCode(),method.hashCode());
                    }
                }
            }else {
                for(MethodReference.Handle method: entry.getValue() ){
                    if(!MethodByNameMap.containsKey(method.hashCode())){
                        MethodByNameMap.put(method.hashCode(),method);
                        hashcodeMap.put(method.hashCode()-method.getDesc().hashCode(),method.hashCode());
                    }
                }
            }
        }
    }




    public void doDiscover(List<List<Sink>> Sinks){
        for(List<Sink> sink : Sinks){
            if(sink.get(0).getDesc().equals("*")&&hashcodeMap.containsKey(sink.get(0).hashCode()-sink.get(0).getDesc().hashCode())){
                int hashCode = this.hashcodeMap.get(sink.get(0).hashCode()-sink.get(0).getDesc().hashCode());
                Set<CallGraph> calls = submethodImplCallMap.get(MethodByNameMap.get(hashCode));
                if(calls!=null){
                    for(CallGraph callGraph : calls){
                        LinkedList<MethodReference.Handle> stack = new LinkedList<>();
                        stack.push(callGraph.getCallerMethod());
                        doTask(callGraph.getTargetMethod(), stack);
                        stack.pop();
                    }
                }
            }else if(!sink.get(0).getDesc().equals("*")){
                int hashCode = sink.get(0).hashCode();
                Set<CallGraph> calls = submethodImplCallMap.get(MethodByNameMap.get(hashCode));
                if(calls!=null){
                    for(CallGraph callGraph : calls){
                        LinkedList<MethodReference.Handle> stack = new LinkedList<>();
                        stack.push(callGraph.getCallerMethod());
                        doTask(callGraph.getTargetMethod(), stack);
                        stack.pop();
                    }
                }
            }
        }
    }

    private void doTask(MethodReference.Handle targetMethod,
                        Deque<MethodReference.Handle> stack) {
        if (stack.contains(targetMethod)) {
            stack.push(targetMethod);
            return;
        }
        stack.push(targetMethod);
        Set<CallGraph> calls = submethodImplCallMap.get(targetMethod);
        if (calls == null || calls.size() == 0) {
            Deque<MethodReference.Handle> copyStack = new LinkedList<>(stack);
            this.stacks.add(copyStack);
            return;
        }
        for (CallGraph callGraph : calls) {
            doTask(callGraph.getTargetMethod(), stack);
            stack.pop();
        }
    }


}
