package rules;

import java.util.ArrayList;
import java.util.List;

public class UnserializeConstan {
    private static final String[][] rules = new String[][]{
            //{"INVOKEVIRTUAL","java/io/ObjectInputStream","readObject","*","unserialize","JAVAX-unserialize"},
            {"INVOKEVIRTUAL", "org/yaml/snakeyaml/Yaml", "load", "*", "unserialize", "SNAKEYAML-unserialize","-1","1"},
            {"INVOKEVIRTUAL", "org/yaml/snakeyaml/Yaml", "compose", "*", "unserialize", "SNAKEYAML-unserialize","-1","1"},
            {"INVOKEVIRTUAL", "org/yaml/snakeyaml/Yaml", "composeAll", "*", "unserialize", "SNAKEYAML-unserialize","-1","1"},
            {"INVOKEVIRTUAL", "org/yaml/snakeyaml/Yaml", "loadAll", "*", "unserialize", "SNAKEYAML-unserialize","-1","1"},
            {"INVOKEVIRTUAL", "org/yaml/snakeyaml/Yaml", "loadAs", "*", "unserialize", "SNAKEYAML-unserialize","-1","1"},
            {"INVOKEVIRTUAL", "org/yaml/snakeyaml/Yaml", "parse", "*", "unserialize", "SNAKEYAML-unserialize","-1","1"},

            {"INVOKEVIRTUAL", "com/fasterxml/jackson/databind/ObjectMapper", "readValue", "*", "unserialize", "JACKSON-unserialize","-1","1"},
            {"INVOKEVIRTUAL", "com/fasterxml/jackson/databind/ObjectMapper", "readValues", "*", "unserialize", "JACKSON-unserialize","-1","1"},
            {"INVOKEVIRTUAL", "com/fasterxml/jackson/databind/ObjectMapper", "treeToValue", "*", "unserialize", "JACKSON-unserialize","-1","1"},

            {"INVOKEVIRTUAL", "com/thoughtworks/xstream/XStream", "fromXML", "*", "unserialize", "XStream-unserialize","-1","1"},
            {"INVOKEVIRTUAL", "com/thoughtworks/xstream/XStream", "unmarshal", "*", "unserialize", "XStream-unserialize","-1","1"},

            {"INVOKEVIRTUAL", "java/beans/XMLDecoder", "readObject", "*", "unserialize", "XMLDecoder-unserialize","-1","1"},

            {"INVOKEVIRTUAL", "com/alibaba/fastjson/JSON", "parse", "*", "unserialize", "FASTJSON-unserialize","-1","1"},
            {"INVOKEVIRTUAL", "com/alibaba/fastjson/JSON", "parseObject", "*", "unserialize", "FASTJSON-unserialize","-1","1"},

            {"INVOKEVIRTUAL", "com/cedarsoftware/util/io/JsonReader", "jsonToJava", "*", "unserialize", "JsonReader-unserialize","-1","1"},
            {"INVOKEVIRTUAL", "com/cedarsoftware/util/io/JsonReader", "readObject", "*", "unserialize", "JsonReader-unserialize","-1","1"},

            {"INVOKEVIRTUAL", "com/esotericsoftware/yamlbeans/YamlReader", "read", "*", "unserialize", "YamlReader-unserialize","-1","1"},

            {"INVOKEVIRTUAL", "com.caucho.hessian.io.AbstractHessianInput", "readObject", "*", "unserialize", "HESSIAN-unserialize","-1","1"},
            {"INVOKEVIRTUAL", "com.alibaba.com.caucho.hessian.io.AbstractHessianInput", "readObject", "*", "unserialize", "HESSIAN-unserialize","-1","1"},
            {"INVOKEVIRTUAL", "com.caucho.hessian.io.Hessian2StreamingInput", "readObject", "*", "unserialize", "HESSIAN-unserialize","-1","1"},
            {"INVOKEVIRTUAL", "com.alibaba.com.caucho.hessian.io.Hessian2StreamingInput", "readObject", "*", "unserialize", "HESSIAN-unserialize","-1","1"},

            {"INVOKEVIRTUAL", "org.exolab.castor.xml.Unmarshaller", "unmarshal", "*", "unserialize", "exolab-unserialize","-1","1"},

            {"INVOKEVIRTUAL", "org.jabsorb.JSONSerializer", "unmarshall", "*", "unserialize", "jabsorb-unserialize","-1","1"},
            {"INVOKEVIRTUAL", "org.jabsorb.JSONSerializer", "fromJSON", "*", "unserialize", "jabsorb-unserialize","-1","1"},

            {"INVOKEVIRTUAL", "com.google.gson.Gson", "fromJson", "*", "unserialize", "Gson-unserialize","-1","1"},

            {"INVOKEVIRTUAL", "org.apache.commons.lang.RandomStringUtils", "deserialize", "*", "unserialize", "lang-unserialize","-1","1"},
            {"INVOKEVIRTUAL", "org.apache.commons.lang3.RandomStringUtils", "deserialize", "*", "unserialize", "lang-unserialize","-1","1"}
    };
    public static String[][] getRules(){
        return rules;
    }
}
