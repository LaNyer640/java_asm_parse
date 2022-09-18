package model;

import java.util.*;

import model.ClassReference;

public class InheritanceMap {
    //子-父关系集合
    private final Map<ClassReference.Handle, Set<ClassReference.Handle>> inheritanceMap;
    //父-子关系集合
    private final Map<ClassReference.Handle, Set<ClassReference.Handle>> subClassMap;

    public InheritanceMap(Map<ClassReference.Handle, Set<ClassReference.Handle>> inheritanceMap){
        this.inheritanceMap = inheritanceMap;
        subClassMap = new HashMap<>();
        for(Map.Entry<ClassReference.Handle, Set<ClassReference.Handle>> entry : inheritanceMap.entrySet()){
            ClassReference.Handle child = entry.getKey();
            for (ClassReference.Handle parent : entry.getValue()) {
                subClassMap.computeIfAbsent(parent, k -> new HashSet<>()).add(child); //computeIfAbsent 如果不存在key就将值添加进去
            }
        }
    }

    public Set<Map.Entry<ClassReference.Handle, Set<ClassReference.Handle>>> entrySet() {
        return inheritanceMap.entrySet();
    }

    public Set<ClassReference.Handle> getSuperClasses(ClassReference.Handle clazz) {
        Set<ClassReference.Handle> parents = inheritanceMap.get(clazz);
        if (parents == null) {
            return null;
        }
        return Collections.unmodifiableSet(parents); //unmodifiableSet() 用于获取指定集合的不可修改视图。
    }

    public boolean isSubclassOf(ClassReference.Handle clazz, ClassReference.Handle superClass) {
        Set<ClassReference.Handle> parents = inheritanceMap.get(clazz);
        if (parents == null) {
            return false;
        }
        return parents.contains(superClass);
    }

    public Set<ClassReference.Handle> getSubClasses(ClassReference.Handle clazz) {
        Set<ClassReference.Handle> subClasses = subClassMap.get(clazz);
        if (subClasses == null) {
            return null;
        }
        return Collections.unmodifiableSet(subClasses);
    }
}
