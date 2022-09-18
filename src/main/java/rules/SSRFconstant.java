package rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class SSRFconstant{
    private static final String[][] rules = new String[][]{
            //{"INVOKEVIRTUAL","java/net/URL","<init>","(Ljava/lang/String;)V","SSRF","JDKSSRF"},
            {"INVOKEVIRTUAL","java/net/URL","openConnection","()Ljava/net/URLConnection;","SSRF","JDKSSRF","-1","1"},
            //{"INVOKEVIRTUAL","java/net/HttpURLConnection","getInputStream","()Ljava/io/InputStream;","SSRF","JDKSSRF"},

            //{"INVOKEVIRTUAL","org/apache/http/client/methods/HttpGet","<init>","(Ljava/lang/String;)V","SSRF","APACHE-SSRF"},
            {"INVOKEVIRTUAL","org/apache/http/impl/client/CloseableHttpClient","execute","(Lorg/apache/http/client/methods/HttpUriRequest;)Lorg/apache/http/client/methods/CloseableHttpResponse;","SSRF","APACHE-SSRF","-1","1"},
            {"INVOKEVIRTUAL","org/apache/http/impl/nio/client/CloseableHttpAsyncClient","execute","(Lorg/apache/http/client/methods/HttpUriRequest;)Lorg/apache/http/client/methods/CloseableHttpResponse;","SSRF","APACHE-SSRF","-1","1"},

            //{"INVOKEVIRTUAL","java/net/Socket","<init>","(Ljava/lang/String;I)V","SSRF","SOCKETSSRF"},
            //{"INVOKEVIRTUAL","java/net/Socket","getInputStream","*","SSRF","SOCKETSSRF"},
            //{"INVOKEVIRTUAL","java/net/Socket","getOutputStream","*","SSRF","SOCKETSSRF"},

            {"INVOKEVIRTUAL","okhttp3/Request$Builder","build","()Lokhttp3/Request;","SSRF","OKHHTPSSRF","-1","2"},
            {"INVOKEVIRTUAL","okhttp3/Call","execute","()Lokhttp3/Response;","SSRF","OKHHTPSSRF","-1","2"},
            {"INVOKEVIRTUAL","okhttp/Request$Builder","build","()Lokhttp/Request;","SSRF","OKHHTPSSRF","-1","2"},
            {"INVOKEVIRTUAL","okhttp/Call","execute","()Lokhttp/Response;","SSRF","OKHHTPSSRF","-1","2"}
            //{"INVOKEVIRTUAL","okhttp3/OkHttpClient","newCall","(Lokhttp3/Request;)Lokhttp3/Call;","SSRF","OKHHTPSSRF","-1","1"},
            //{"INVOKEVIRTUAL","okhttp/OkHttpClient","newCall","(Lokhttp/Request;)Lokhttp/Call;","SSRF","OKHHTPSSRF","-1","1"},
    };
    public static String[][] getRules(){
        return rules;
    }
}
