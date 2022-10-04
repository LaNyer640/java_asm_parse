package rules;

import java.util.ArrayList;
import java.util.List;

public class JNDIConstant {
    private static final String[][] rules = new String[][]{
            {"INVOKEVIRTUAL","javax/naming/Context","lookup","*","JNDI","JAVAX-JNDI","-1","1"},
            //{"INVOKEVIRTUAL","javax/naming/Context","lookupLink","*","JNDI","JAVAX-JNDI","-1","1"},
            //{"INVOKEVIRTUAL","javax/naming/Context","list","*","JNDI","JAVAX-JNDI","-1","1"},
            //{"INVOKEVIRTUAL","javax/naming/Context","listBindings","*","JNDI","JAVAX-JNDI","-1","1"},
            {"INVOKEVIRTUAL","javax/naming/InitialContext","doLookup","*","JNDI","JAVAX-JNDI","-1","1"},
            {"INVOKEVIRTUAL","javax/naming/InitialContext","lookup","*","JNDI","JAVAX-JNDI","1","1"},
            {"INVOKEVIRTUAL","javax/management/remote/JMXConnector","connect","*","JNDI","JAVAX-JNDI","-1","1"},
            {"INVOKEVIRTUAL","javax/management/remote/JMXConnectorFactory","connect","*","JNDI","JAVAX-JNDI","-1","1"},
            //{"INVOKEVIRTUAL","javax/naming/Context","list","*","JNDI","JAVAX-JNDI","-1","1"},
            {"INVOKEVIRTUAL","org/springframework/jndi/JndiTemplate","lookup","*","JNDI","SPRING-JNDI","-1","1"},
            {"INVOKEVIRTUAL","org/springframework/ldap/core/LdapOperations","lookup","*","JNDI","SPRING-JNDI","-1","1"},
            {"INVOKEVIRTUAL","org/springframework/ldap/core/LdapOperations","findByDn","*","JNDI","SPRING-JNDI","-1","1"},
            {"INVOKEVIRTUAL","org/springframework/ldap/core/LdapOperations","rename","*","JNDI","SPRING-JNDI","-1","1"},
            //{"INVOKEVIRTUAL","org/springframework/ldap/core/LdapOperations","list","*","JNDI","SPRING-JNDI","-1","1"},
            //{"INVOKEVIRTUAL","org/springframework/ldap/core/LdapOperations","listBindings","*","JNDI","SPRING-JNDI","-1","1"},
            {"INVOKEVIRTUAL","org/springframework/ldap/core/LdapOperations","search","*","JNDI","SPRING-JNDI","-1","1"},
            {"INVOKEVIRTUAL","org/springframework/ldap/core/LdapOperations","searchForObject","*","JNDI","SPRING-JNDI","-1","1"},
            {"INVOKEVIRTUAL","org/apache/shiro/jndi/JndiTemplate","lookup","*","JNDI","JAVAX-RCE","-1","1"}
    };
    public static String[][] getRules(){
        return rules;
    }
}
