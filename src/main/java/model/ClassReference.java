package model;

import org.objectweb.asm.Handle;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class ClassReference implements Serializable {
    private final String name; //类名
    private final String superClass; //父类
    private final List<String> interfaces; //接口
    private final boolean isInterface; //判断是否为接口
    private final List<Variable> Variables;  //这里一直用中文不知道好不好
    private final boolean isAbstract;
    public static class Variable implements Serializable{
        private final String name;

        private final int modifiers;

        private final Handle Types;

        public Variable(String name, int modifiers, Handle type) {
            this.name = name;
            this.modifiers = modifiers;
            this.Types = type;
        }

        public String getName() {
            return name;
        }

        public int getModifiers() {
            return modifiers;
        }

        public Handle getType() {
            return Types;
        }
    }


    public ClassReference(String name, String superClass, List<String> interfaces,
                          boolean isInterface, List<Variable> Variables,boolean isAbstract) {
        this.name = name;
        this.superClass = superClass;
        this.interfaces = interfaces;
        this.isInterface = isInterface;
        this.Variables = Variables;
        this.isAbstract = isAbstract;
    }

    public String getName() {
        return name;
    }

    public String getSuperClass() {
        return superClass;
    }

    public List<String> getInterfaces() {
        return interfaces;
    }

    public boolean isInterface() {
        return isInterface;
    }

    public List<Variable> getZiDuan() {
        return Variables;
    }

    public Handle getHandle() {
        return new Handle(name);
    }

    public boolean isAbstract(){
        return isAbstract;
    }

    public static class Handle implements Serializable{
        private final String name;

        public Handle(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Handle handle = (Handle) o;
            return Objects.equals(name, handle.name);
        }

        @Override
        public int hashCode() {
            return name != null ? name.hashCode() : 0;
        }
    }

}
