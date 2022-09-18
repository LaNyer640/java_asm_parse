package rules;

import java.util.ArrayList;
import java.util.List;

public class SQLinjectionConstant  {
    private static final String[][] rules = new String[][]{
            {"INVOKEVIRTUAL","org/springframework/jdbc/core/JdbcTemplate","update","*","sqlinjection","JdbcTemplate-SQL","-1","1"},
            {"INVOKEVIRTUAL","org/springframework/jdbc/core/JdbcTemplate","execute","*","sqlinjection","JdbcTemplate-SQL","-1","1"},
            {"INVOKEVIRTUAL","org/springframework/jdbc/core/JdbcTemplate","query","*","sqlinjection","JdbcTemplate-SQL","-1","1"},
            {"INVOKEVIRTUAL","org/springframework/jdbc/core/JdbcTemplate","queryForStream","*","sqlinjection","JdbcTemplate-SQL","-1","1"},
            {"INVOKEVIRTUAL","org/springframework/jdbc/core/JdbcTemplate","queryForList","*","sqlinjection","JdbcTemplate-SQL","-1","1"},
            {"INVOKEVIRTUAL","org/springframework/jdbc/core/JdbcTemplate","queryForMap","*","sqlinjection","JdbcTemplate-SQL","-1","1"},
            {"INVOKEVIRTUAL","org/springframework/jdbc/core/JdbcTemplate","queryForObject","*","sqlinjection","JdbcTemplate-SQL","-1","1"},

            {"INVOKEVIRTUAL","java/sql/Statement","executeQuery","*","sqlinjection","Statement-SQL","-1","1"},
            {"INVOKEVIRTUAL","java/sql/Statement","execute","*","sqlinjection","Statement-SQL","-1","1"},
            {"INVOKEVIRTUAL","java/sql/Statement","executeUpdate","*","sqlinjection","Statement-SQL","-1","1"},

            {"INVOKEVIRTUAL","com/mongodb/BasicDBObject","parse","*","sqlinjection","Statement-SQL","-1","1"},
    };
    public static String[][] getRules(){
        return rules;
    }
}
