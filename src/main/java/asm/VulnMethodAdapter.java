package asm;

import asm.jvmasm.JvmMethodAdapter;
import model.InheritanceMap;
import model.MethodReference;
import model.Sink;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VulnMethodAdapter extends MethodVisitor {
    private final List<Sink> sinks;
    private int flag=0;//这个flag主要是为了多个sink的时候记录是否有污点时候弄的
    private int need;
    public VulnMethodAdapter(List<Sink> sinks, int api) {
        super(api);
        this.sinks = sinks;
        this.need = sinks.size();
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        if(flag!=need&&isSink(sinks,owner,name,desc,flag)){
            this.flag = flag+1;
        }
        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }


    public int getVulnFlag() {
        return flag;
    }

    public boolean isSink(List<Sink> sinks,String owner, String name, String desc,int flag){
        if(sinks.get(flag).getName().equals(name)&&sinks.get(flag).getClassName().equals(owner)&&
                (sinks.get(flag).getDesc().equals(desc)||sinks.get(flag).getDesc().equals("*")))
        {
            return true;
        }
        return false;
    }
}
