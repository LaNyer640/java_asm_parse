package Service;

import model.MethodReference;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class DarwService {

    private int flag =0;

    private MethodReference.Handle qianji;

    public static void start(List<Deque<MethodReference.Handle>> stacks) throws IOException {
        File file = new File("html/data.js");
        DarwService  DarwService = new DarwService();
        DarwService.display(file,stacks);
    }

    public void display(File file, List<Deque<MethodReference.Handle>> stacks) throws IOException {

        String path = file.getAbsolutePath();
        file.delete();
        BufferedWriter nfile = new BufferedWriter(new FileWriter(path, true));
        nfile.write("var nodes = [];\n");
        nfile.write("var edges = [];\n");

        // 2 loops because all nodes must declared before be used in edges
        ArrayList<Integer> idList = new ArrayList<Integer>();
        for ( Deque<MethodReference.Handle> stack: stacks){
            for(MethodReference.Handle method : stack){
                if(!idList.contains(method.hashCode())){
                    idList.add(method.hashCode());
                    nfile.write("nodes.push({ id: " + method.hashCode()+ ", label: String(\"" + method.getName() + "\"), title: String(\"" + method.getClassReference().getName() + "\") });\n");
                }
            }
        }

        ArrayList<Integer> idList2 = new ArrayList<Integer>();
        for ( Deque<MethodReference.Handle> stack: stacks){
            this.flag =0;
            for(MethodReference.Handle method : stack){
                if(this.flag ==0){
                    this.qianji = method;
                    this.flag = 1;
                } else if(!idList2.contains(qianji.hashCode()+method.hashCode())){
                    idList2.add(qianji.hashCode()+method.hashCode());
                    nfile.write("edges.push({ from: " + method.hashCode()+ ", to: " + this.qianji.hashCode()+ " });\n");
                    this.qianji =method;
                }
            }
        }
        nfile.close();
    }
}
