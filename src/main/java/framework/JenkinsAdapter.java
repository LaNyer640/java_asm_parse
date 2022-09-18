package framework;

import model.ClassReference;
import model.MethodReference;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import rules.JenkinsConstant;

import java.util.List;
import java.util.regex.Pattern;

public class JenkinsAdapter extends MethodVisitor {
    private String MethodName;
    private String desc;
    private String ClassName;
    private List<MethodReference> SourceMethod;
    boolean isStatic;
    private int flag=0;

    public JenkinsAdapter(String name, String desc, String className, int api, MethodVisitor mv, List<MethodReference> sourceMethod,boolean isStatic) {
        super(api, mv);
        this.MethodName = name;
        this.desc = desc;
        this.ClassName = className;
        this.SourceMethod = sourceMethod;
        this.isStatic = isStatic;
        if((MethodNameIsMatch(this.MethodName)&&isneed(ClassName))|(MethodName.contains("search")&&isneed(ClassName))){
            this.flag=1;
            this.SourceMethod.add(new MethodReference(new ClassReference.Handle(ClassName),MethodName,desc,isStatic));
            System.out.println("入口点source在"+ClassName+"-->"+MethodName);
        }
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        AnnotationVisitor av = super.visitAnnotation(descriptor, visible);
        if((descriptor.equals(JenkinsConstant.JenkinsAnno1) | descriptor.equals(JenkinsConstant.JenkinsAnno2)
                |descriptor.equals(JenkinsConstant.JenkinsAnno3)| descriptor.equals(JenkinsConstant.JenkinsAnno4)|descriptor.equals(JenkinsConstant.JenkinsAnno5)
                |descriptor.equals(JenkinsConstant.JenkinsAnno6))&&(this.flag==0)){
            this.flag=1;
            this.SourceMethod.add(new MethodReference(new ClassReference.Handle(ClassName),MethodName,desc,isStatic));
            System.out.println("入口点source在"+ClassName+"-->"+MethodName);
        }
        return av;
    }


    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
        AnnotationVisitor av = super.visitParameterAnnotation(parameter, descriptor, visible);
        if((descriptor.contains(JenkinsConstant.ParamAnn1)|descriptor.contains(JenkinsConstant.ParamAnn2)
                |descriptor.contains(JenkinsConstant.ParamAnn3)|descriptor.contains(JenkinsConstant.ParamAnn4))&&(this.flag==0)&&isneed(ClassName))
        {
            this.flag=1;
            this.SourceMethod.add(new MethodReference(new ClassReference.Handle(ClassName),MethodName,desc,isStatic));
            System.out.println("入口点source在"+ClassName+"-->"+MethodName);
        }
        return av;
    }

    public boolean isneed(String ClassName){
        for(String black:blacklist){
            if(ClassName.contains(black)){
                return false;
            }
        }
        return true;
    }

    public String[] blacklist = new String[]{
            "org/apache","okhttp","javax/xml/","org/json","org/","fasterxml/","javassist/","groovy/","sun/","google/"
    };

    public boolean MethodNameIsMatch(String MethodName){
        String pattern = "^do[A-Z].*";
        boolean isMatch = Pattern.matches(pattern, MethodName);
        return isMatch;
    }
}
