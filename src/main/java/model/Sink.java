package model;

public class Sink {
    private final String ClassName;
    private final String name;
    private final String desc;
    private int targetIndex;
    private String sinkName;
    private int flag;

    public Sink(String ClassName, String name, String desc,String sinkName,int targetIndex,int flag) {
        this.ClassName = ClassName;
        this.name = name;
        this.desc = desc;
        this.sinkName = sinkName;
        this.targetIndex = targetIndex;
        this.flag = flag;
    }

    public String getClassName(){
        return this.ClassName;
    }
    public String getDesc(){
        return this.desc;
    }
    public String getName(){
        return this.name;
    }
    public int getTargetIndex(){
        return this.targetIndex;
    }
    public String getSinkName(){
        return this.sinkName;
    }
    public int getFlag(){
        return this.flag;
    }

    @Override
    public int hashCode() {
        int result = ClassName != null ? ClassName.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (desc != null ? desc.hashCode() : 0);
        return result;
    }
}
