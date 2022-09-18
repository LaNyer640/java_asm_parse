package rules;

public class FileReadConstant {

    private static final String[][] rules = new String[][]{
            {"INVOKEVIRTUAL","java/nio/file/Files","readAllBytes","*","FileRead","FileRead","-1","1"},
            {"INVOKEVIRTUAL","java/nio/file/Files","readAllLines","*","FileRead","FileRead","-1","1"},
            {"INVOKEVIRTUAL","java/nio/file/Files","readString","*","FileRead","FileRead","-1","1"},
            {"INVOKEVIRTUAL","java/nio/file/Files","lines","*","FileRead","FileRead","-1","1"},
            {"INVOKEVIRTUAL","java/nio/file/Files","newBufferedReader","*","FileRead","FileRead","-1","1"},
            //{"INVOKEVIRTUAL","java/nio/file/Files","newInputStream","*","FileRead","FileRead","1","1"},
            {"INVOKEVIRTUAL","java/nio/file/Files","newByteChannel","*","FileRead","FileRead","-1","1"},
    };
    public static String[][] getRules(){
        return rules;
    }
}
