package rules;

public class ZipSlipConstant {
    private static final String[][] rules = new String[][]{
            {"INVOKEVIRTUAL","java/util/zip/ZipEntry","getName","*","ZipSlip","ZipSlip","-1","1"},
            {"INVOKEVIRTUAL","org/apache/commons/compress/archivers/ArchiveEntry","getName","*","ZipSlip","ZipSlip","-1","1"},
    };

    public static String[][] getRules(){
        return rules;
    }
}
