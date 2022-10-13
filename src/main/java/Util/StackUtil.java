package Util;

import com.google.common.io.Files;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import model.MethodReference;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

public class StackUtil {
    public static  <T extends Serializable> T clone(Map obj) {
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

    public static void printStackTrace(Deque<MethodReference.Handle> stack) {
        Deque<MethodReference.Handle> copyStack = new LinkedList<>(stack);
        StringBuilder prefix=new StringBuilder("\t");
        for (MethodReference.Handle handle : copyStack) {
            System.out.println(prefix+handle.getClassReference().getName()+"."+handle.getName());
            prefix.append("\t");
        }
        System.out.println("");
        System.out.println("");
    }

    public static void SaveStack(Path filePath, List<Deque<MethodReference.Handle>> StackList) throws IOException {
        try {
        File file = new File(filePath.toString());
        if(file.exists()){
            file.delete();
        }
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath.toString(), true)));
            for (Deque<MethodReference.Handle> Stack: StackList) {
                StringBuilder sb = new StringBuilder();
                Deque<MethodReference.Handle> copyStack = new LinkedList<>(Stack);
                StringBuilder prefix=new StringBuilder("\t");
                for (MethodReference.Handle handle : copyStack) {
                    sb.append(prefix).append(handle.getClassReference().getName()).append(".").append(handle.getName()).append("\n");
                    prefix.append("\t");
                }
                sb.append("\n");
                writer.write(String.valueOf(sb));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<List<Deque<MethodReference.Handle>>> paixu(List<Deque<MethodReference.Handle>> stacks){
        List<List<Deque<MethodReference.Handle>>> fenleiList = new ArrayList<>();
        List<List<Deque<MethodReference.Handle>>> fenleiList2 = new ArrayList<>();
        fenleiList2.add(new ArrayList<>());
        fenleiList2.add(new ArrayList<>());
        List<MethodReference.Handle> storeList = new ArrayList<>();
        for(Deque<MethodReference.Handle> stack:stacks){
            MethodReference.Handle First = stack.getFirst();
            if(storeList.contains(First)){
                int i = storeList.indexOf(First);
                fenleiList.get(i).add(stack);
            }else {
                storeList.add(First);
                List<Deque<MethodReference.Handle>> newList = new ArrayList<>();
                newList.add(stack);
                fenleiList.add(newList);
            }
        }
        for(List<Deque<MethodReference.Handle>> a:fenleiList){
            Deque<MethodReference.Handle> b = a.stream().min(Comparator.comparing(Deque<MethodReference.Handle>::size)).get();
            fenleiList2.get(0).add(b);
            fenleiList2.get(1).addAll(a);
        }
        return fenleiList2;
    }


}
