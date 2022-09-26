package rules;

public class RCEConstant  {
    //xmldecode和ELProcessor规则需要添加
    private static final String[][] rules = new String[][]{
            {"INVOKEVIRTUAL","java/lang/Runtime","exec","*","RCE","Runtime-RCE","-1","1"},
            {"INVOKEVIRTUAL","java/lang/ProcessBuilder","exec","*","RCE","ProcessBuilder-RCE","-1","1"},
            {"INVOKEVIRTUAL","java/lang/ProcessBuilder","start","*","RCE","ProcessBuilder-RCE","-1","1"},
            {"INVOKEVIRTUAL","groovy/lang/GroovyShell","evaluate","*","RCE","GroovyShell-RCE","-1","1"},
            {"INVOKEVIRTUAL","javax/script/ScriptEngineManager","getEngineByName","(Ljava/lang/String;)Ljavax/script/ScriptEngine;","RCE","Script-RCE","-1","2"},
            {"INVOKEVIRTUAL","javax/script/ScriptEngine","eval","*","RCE","Script-RCE","-1","2"},
            {"INVOKEVIRTUAL","bsh/Interpreter","eval","*","RCE","BeanShell-RCE","-1","1"}
    };

    public static String[][] getRules(){
        return rules;
    }
}
