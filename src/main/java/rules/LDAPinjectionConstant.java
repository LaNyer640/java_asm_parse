package rules;

import java.util.ArrayList;
import java.util.List;

public class LDAPinjectionConstant {
    private static final String[][] rules = new String[][]{
            {"INVOKEVIRTUAL","javax/naming/directory/DirContext","search","*;","LDAP","JDK-LDAP","-1","1"},
            {"INVOKEVIRTUAL","com/unboundid/ldap/sdk/LDAPConnection","search","*;","LDAP","UnboundID-LDAP","-1","1"},
            {"INVOKEVIRTUAL","com/unboundid/ldap/sdk/LDAPConnection","searchForEntry","*;","LDAP","UnboundID-LDAP","-1","1"},
            {"INVOKEVIRTUAL","com/unboundid/ldap/sdk/LDAPConnection","asyncSearch","*;","LDAP","UnboundID-LDAP","-1","1"},
            {"INVOKEVIRTUAL","org/springframework/ldap/core/LdapTemplate","find","*;","LDAP","SPRING-LDAP","-1","1"},
            {"INVOKEVIRTUAL","org/springframework/ldap/core/LdapTemplate","findOne","*;","LDAP","SPRING-LDAP","-1","1"},
            {"INVOKEVIRTUAL","org/springframework/ldap/core/LdapTemplate","search","*;","LDAP","SPRING-LDAP","-1","1"},
            {"INVOKEVIRTUAL","org/springframework/ldap/core/LdapTemplate","searchForContext","*;","LDAP","SPRING-LDAP","-1","1"},
            {"INVOKEVIRTUAL","org/springframework/ldap/core/LdapTemplate","searchForObject","*;","LDAP","SPRING-LDAP","-1","1"},
            {"INVOKEVIRTUAL","org/springframework/ldap/core/LdapTemplate","authenticate","*;","LDAP","SPRING-LDAP","-1","1"},
    };
    public static String[][] getRules(){
        return rules;
    }

}
