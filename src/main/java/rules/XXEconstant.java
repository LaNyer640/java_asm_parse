package rules;

public class XXEconstant {

    private static final String[][] rules = new String[][]{
            {"INVOKEVIRTUAL","javax/xml/parsers/SAXParser","parse","*","XXE","SAX-PARSER-XXE","-1","1"},
            {"INVOKEVIRTUAL","org/xml/sax/XMLReader","parse","*","XXE","XML-READER-XXE","-1","1"},
            {"INVOKEVIRTUAL","org/dom4j/io/SAXReader","read","*","XXE","SAX-READER-XXE","-1","1"},
            {"INVOKEVIRTUAL","javax/xml/parsers/DocumentBuilder","parse","*","XXE","Document-Builder-XXE","-1","1"},
            {"INVOKEVIRTUAL","javax/xml/bind/Unmarshaller","unmarshal","*","XXE","UNMARSHALLER","-1","1"},
            {"INVOKEVIRTUAL","javax/xml/xpath/XPathExpression","evaluate","*","XXE","XPathExpression","-1","1"},
            {"INVOKEVIRTUAL","org/simpleframework/xml/core/Persister","read","*","XXE","SIMPLE-XXE","-1","1"},
            {"INVOKEVIRTUAL","org/simpleframework/xml/stream/Formatter","format","*","XXE","SIMPLE-XXE","-1","1"},
    };

    public static String[][] getRules(){
        return rules;
    }
}
