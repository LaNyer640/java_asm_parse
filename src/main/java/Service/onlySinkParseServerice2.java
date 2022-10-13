package Service;

import Util.WriteUtil;
import asm.VulnClassVisitor;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import model.*;
import org.objectweb.asm.ClassReader;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.*;

public class onlySinkParseServerice2 {
    private final Map<String, ClassFile> classFileByName;
    private final model.InheritanceMap InheritanceMap;
    private final Map<MethodReference.Handle, Set<MethodReference.Handle>> methodCall;
    private final Map<MethodReference.Handle, MethodReference> methodMap;
    private final Map<ClassReference.Handle, ClassReference> classMap;

    private Map<MethodReference.Handle, Set<MethodReference.Handle>> methodImplCall = new HashMap<>();

    private final List<List<Sink>> Sinks;

    private List<MethodReference.Handle> SaveSinkList = new ArrayList<>();

    private int VulnNumber = 1;
    private boolean Save;


    public onlySinkParseServerice2(Map<String, ClassFile> classFileByName, InheritanceMap InheritanceMap, Map<MethodReference.Handle, Set<MethodReference.Handle>> methodCall,
                                  Map<MethodReference.Handle, MethodReference> methodMap, Map<ClassReference.Handle, ClassReference> classMap, List<List<Sink>> Sinks,Boolean Save){
        this.classFileByName = classFileByName;
        this.InheritanceMap = InheritanceMap;
        this.methodCall = methodCall;
        this.methodMap = methodMap;
        this.classMap =classMap;
        this.Sinks = Sinks;
        this.Save = Save;
    }

    public void start(){
        preparemethodCall(methodMap);
        doDiscover(Sinks);
    }



    private void preparemethodCall(Map<MethodReference.Handle, MethodReference> methodMap){
        Map<MethodReference.Handle, Set<MethodReference.Handle>> methodImplMap = InheritanceService.getAllMethodImplementations(InheritanceMap, methodMap);
        methodImplCall = clone(methodCall);
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
    }



    public void doDiscover(List<List<Sink>> Sinks){
        for(List<Sink> sink : Sinks){
            for(Map.Entry<MethodReference.Handle, Set<MethodReference.Handle>> entry:methodImplCall.entrySet()) {
                for (MethodReference.Handle method : entry.getValue()) {
                    if (sink.size() == 1 && isFirstSink(sink, method) && !entry.getKey().getClassReference().getName().equals(sink.get(0).getClassName())) {
                        System.out.println();
                        System.out.println("[" + VulnNumber + "] detect vuln: " + sink.get(0).getSinkName() + "  Name: " + sink.get(0).getName());
                        System.out.println("location is:" + entry.getKey().getClassReference().getName() + ":" + entry.getKey().getName());
                        VulnNumber++;
                        SaveSinkList.add(entry.getKey());
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
                            SaveSinkList.add(entry.getKey());
                        }
                    }
                }
            }
        }
        if(Save==true){
            WriteUtil.SaveSinkRule(Paths.get("SinkRule.dat"),SaveSinkList);
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


    private <T extends Serializable> T clone(Map obj) {
        T cloneObj = null;
        try {
            ByteOutputStream bos = new ByteOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.close();
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);
            cloneObj = (T) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cloneObj;
    }
}
