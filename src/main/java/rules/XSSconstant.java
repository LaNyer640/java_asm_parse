package rules;

public class XSSconstant {
    private static final String[][] rules = new String[][]{
            {"INVOKEVIRTUAL","hudson/util/FormValidation","errorWithMarkup","*","XSS","JENKINS-PLUGIN-XSS","-1","1"},
            {"INVOKEVIRTUAL","hudson/util/FormValidation","okWithMarkup","*","XSS","JENKINS-PLUGIN-XSS","-1","1"},
            {"INVOKEVIRTUAL","hudson/util/FormValidation","addMarkup","*","XSS","JENKINS-PLUGIN-XSS","-1","1"},
    };

    public static String[][] getRules(){
        return rules;
    }

}
