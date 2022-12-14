package app;

import Service.*;
import Util.ClassUtil;
import Util.DirUtil;
import Util.StackUtil;
import com.beust.jcommander.JCommander;
import model.*;
import rules.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    private static final Map<String, ClassFile> classFileByName = new HashMap<>();
    private static final List<ClassFile> classFileList = new ArrayList<>();
    private static final List<ClassReference> discoveredClasses = new ArrayList<>();
    private static final List<MethodReference> discoveredMethods = new ArrayList<>();
    private static final Map<ClassReference.Handle, ClassReference> classMap = new HashMap<>();
    private static final Map<MethodReference.Handle, MethodReference> methodMap = new HashMap<>();

    private static final Map<MethodReference.Handle, Set<MethodReference.Handle>> methodCall = new HashMap<>();
    private static final Map<MethodReference.Handle, Set<Integer>> passthroughDataflow = new HashMap<>();
    private static final Set<CallGraph> discoveredCalls = new HashSet<>();
    private static InheritanceMap InheritanceMap;

    private static final List<MethodReference> Sources = new ArrayList<>();

    private static final List<List<Sink>> Sinks = new ArrayList<>();

    private static final List<Deque<MethodReference.Handle>> stacks = new ArrayList<>();

    private static final Map<String,String> jarByClass = new HashMap<>();

    public static void run(String[] args) throws IOException {
        Command command = new Command();
        JCommander jc = JCommander.newBuilder().addObject(command).build();
        jc.parse(args);
        if (command.help) {
            jc.usage();
            return;
        }
        if((command.jar != null && command.jar.size() != 0)||(command.libs!=null)){
            printConfig(command);
            if(command.methodName!=null){
                getClassFileList(command,jarByClass);
                getClassinfo(command.methodName);
            }else {
                start(command);
            }
        }else if (command.jar == null && command.libs ==null){
            logger.error("[-] no zips or jar input");
        }
    }

    private static void printConfig(Command command) {
        if(command.jar != null){
            System.out.print("[+] jar File: ");
            for (String jar : command.jar) {
                System.out.print(jar + " ");
            }
            System.out.println();
            if (command.lib) {
                System.out.println("[+] Use All Libs In jar");
            }
        }
        if(command.libs != null){
            System.out.print("[+] libs director: ");
            System.out.print(command.libs + " ");
            System.out.println();
            if (command.lib) {
                System.out.println("[+] Use All Libs In jar");
            }
        }
    }

    private static void start(Command command) throws IOException {
        getClassFileList(command,jarByClass);
        loadSinks(command);
        getClassinfo();
        builfInheritance();
        getMethodCall();
        if (command.taint==1){
            loadSource(command);
            buildPassthrough();
            buildCallGraph();
            startTaintParse();
        }
        if(command.taint==2) {
            loadSource(command);
            startNoTaintParse();
        }
        if(command.taint==3){
            parseSink();
        }
        if(command.taint==41){
            buildPassthrough();
            buildCallGraph();
            parseOnlySink(command);
        }
        if(command.taint==42){
            parseOnlySink2(command);
        }
        if(command.draw == true){
            startdraw();
        }
    }

    public static void loadSinks(Command command){
        if ((command.module == null || command.module.equals(""))&&command.rule==null) {
            System.out.println("[-] no module selected");
        } else {
            if(command.module != null){
                String module = command.module.toUpperCase(Locale.ROOT);
                if (module.contains("ALL")) {
                    module = "SSRF|SQLI|XXE|RCE|DOS|FileRead|JNDI|XSS|ZIPSLIP|UNSERIALIZE";
                    System.out.println("[+] ??????????????????");
                }
                if (module.contains("SSRF")) {
                    LoadSink.load(Sinks,SSRFconstant.getRules());
                    System.out.println("[+] ??????SSRF??????");
                }
                if (module.contains("XXE")) {
                    LoadSink.load(Sinks,XXEconstant.getRules());
                    System.out.println("[+] ??????XXE??????");
                }
                if (module.contains("SQLI")) {
                    LoadSink.load(Sinks,SQLinjectionConstant.getRules());
                    System.out.println("[+] ??????SQLI??????");
                }
                if (module.contains("RCE")) {
                    LoadSink.load(Sinks,RCEConstant.getRules());
                    System.out.println("[+] ??????RCE??????");
                }
                if (module.contains("FileRead")) {
                    LoadSink.load(Sinks,FileReadConstant.getRules());
                    System.out.println("[+] ??????FileRead??????");
                }
                if (module.contains("LDAP")) {
                    LoadSink.load(Sinks,LDAPinjectionConstant.getRules());
                    System.out.println("[+] ??????LDAP??????");
                }
                if (module.contains("JNDI")) {
                    LoadSink.load(Sinks,JNDIConstant.getRules());
                    System.out.println("[+] ??????JNDI??????");
                }
                if (module.contains("XSS")) {
                    LoadSink.load(Sinks,XSSconstant.getRules());
                    System.out.println("[+] ??????XSS??????");
                }
                if (module.contains("ZIPSLIP")) {
                    LoadSink.load(Sinks,ZipSlipConstant.getRules());
                    System.out.println("[+] ??????ZipSlip??????");
                }
                if (module.contains("UNSERIALIZE")) {
                    LoadSink.load(Sinks,UnserializeConstan.getRules());
                    System.out.println("[+] ??????Unserialize??????");
                }
            }
            if(command.rule!=null){
                System.out.println("[+] ???????????????rule");
                load_rules(Sinks,command);
            }
        }
    }

    private static void loadSource(Command command) {
        if (command.source != null) {
            LoadSource.loadsource(Sources, classFileList,command.source);
        } else {
            LoadSource.loadsource(Sources, classFileList);
        }
    }
    private static void getClassFileList(Command command,Map jarByClass) {
        if(command.jar!=null){
            classFileList.addAll(ClassUtil.getAllClassesFromBoots(command.jar, command.jdk, command.lib,jarByClass));
        }
        if(command.libs!=null){
            List<String> libs =DirUtil.getAllFile(command.libs);
            classFileList.addAll(ClassUtil.getAllClassesFromBoots(libs, command.jdk, command.lib,jarByClass));
        }
    }


    private static void getClassinfo(){
        DiscoverService.start(classFileList,discoveredClasses,discoveredMethods,classMap,methodMap,classFileByName);
        System.out.println("[+] ???????????????" +discoveredClasses.size()+"??????");
        System.out.println("[+] ???????????????" +discoveredMethods.size()+"?????????");
    }

    private static void getClassinfo(String methodName){
        DiscoverService.start(classFileList,methodName);
    }
    public static void load_rules(List<List<Sink>> Sinks,Command command){
        Ruleservice Ruleservice = new Ruleservice();
        Ruleservice.start(Sinks,command);
    }

    private static void builfInheritance(){
        InheritanceMap = InheritanceService.start(classMap);
    }

    private  static void getMethodCall(){
        MethodCallService.start(classFileList,methodCall);
    }

    private static void buildPassthrough(){
        PassthroughService.start(classFileByName,classMap,InheritanceMap,methodCall,passthroughDataflow);
    }

    private static void buildCallGraph(){
        CallGraphService.start(classMap,InheritanceMap,passthroughDataflow,discoveredCalls,classFileList);
    }

    private static void startTaintParse(){
        ChainDiscoverService ChainDiscoverService = new ChainDiscoverService(classFileByName,InheritanceMap,discoveredCalls,methodMap,classMap,Sources,Sinks);
        ChainDiscoverService.start();
    }

    private static void startNoTaintParse(){
        NoTaintChainDiscoverService NoTaintChainDiscoverService = new NoTaintChainDiscoverService(classFileByName,InheritanceMap,methodCall,methodMap,classMap,Sources,Sinks,stacks);
        NoTaintChainDiscoverService.start();
    }

    private static void parseSink() throws IOException {
        SinkParseService SinkParseService = new SinkParseService(InheritanceMap,methodCall,methodMap,classMap,Sinks,stacks);
        SinkParseService.start();
        List<List<Deque<MethodReference.Handle>>> paixuStack = StackUtil.paixu(stacks);
        System.out.println("???+??? ??????????????????"+paixuStack.get(0).size()+"?????????");
        for(Deque<MethodReference.Handle> i : paixuStack.get(0)){
            StackUtil.printStackTrace(i);
        }
        StackUtil.SaveStack(Paths.get("Stack.dat"),paixuStack.get(1));
    }
    private static void parseOnlySink(Command command){
        onlySinkParseServerice onlySinkParseServerice = new onlySinkParseServerice(classFileByName,InheritanceMap,discoveredCalls,methodMap,classMap,Sinks,jarByClass,command.Save);
        onlySinkParseServerice.start();
    }

    private static void parseOnlySink2(Command command){
        onlySinkParseServerice2 onlySinkParseServerice = new onlySinkParseServerice2(classFileByName,InheritanceMap,methodCall,methodMap,classMap,Sinks,command.Save);
        onlySinkParseServerice.start();
    }

    private static void startdraw() throws IOException {
        DarwService.start(stacks);
    }
}
