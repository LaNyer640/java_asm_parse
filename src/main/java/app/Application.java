package app;

import Service.*;
import Util.ClassUtil;
import Util.DirUtil;
import com.beust.jcommander.JCommander;
import model.*;
import rules.*;

import java.io.IOException;
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
            start(command);
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
        loadSource(command);
        getClassinfo();
        builfInheritance();
        getMethodCall();
        if (command.taint==1){
            buildPassthrough();
            buildCallGraph();
            startTaintParse();
        }
        if(command.taint==2) {
            startNoTaintParse();
        }
        if(command.taint==3){
            parseSink();
        }
        if(command.taint==4){
            buildPassthrough();
            buildCallGraph();
            parseOnlySink();
        }
        if(command.draw == true){
            startdraw();
        }
    }

    public static void loadSinks(Command command){
        if (command.module == null || command.module.equals("")) {
            System.out.println("[-] no module selected");
        } else {
            String module = command.module.toUpperCase(Locale.ROOT);
            if (module.contains("ALL")) {
                module = "SSRF|SQLI|XXE|RCE|DOS|FileRead|JNDI|XSS|ZIPSLIP|UNSERIALIZE";
                System.out.println("[+] 加载所有规则");
            }

            if (module.contains("SSRF")) {
                LoadSink.load(Sinks,SSRFconstant.getRules());
                System.out.println("加载SSRF规则");
            }
            if (module.contains("XXE")) {
                LoadSink.load(Sinks,XXEconstant.getRules());
                System.out.println("[+] 加载XXE规则");
            }
            if (module.contains("SQLI")) {
                LoadSink.load(Sinks,SQLinjectionConstant.getRules());
                System.out.println("[+] 加载SQLI规则");
            }
            if (module.contains("RCE")) {
                LoadSink.load(Sinks,RCEConstant.getRules());
                System.out.println("[+] 加载RCE规则");
            }
            if (module.contains("FileRead")) {
                LoadSink.load(Sinks,FileReadConstant.getRules());
                System.out.println("[+] 加载FileRead规则");
            }
            if (module.contains("LDAP")) {
                LoadSink.load(Sinks,LDAPinjectionConstant.getRules());
                System.out.println("[+] 加载LDAP规则");
            }
            if (module.contains("JNDI")) {
                LoadSink.load(Sinks,JNDIConstant.getRules());
                System.out.println("[+] 加载JNDI规则");
            }
            if (module.contains("XSS")) {
                LoadSink.load(Sinks,XSSconstant.getRules());
                System.out.println("[+] 加载XSS规则");
            }
            if (module.contains("ZIPSLIP")) {
                LoadSink.load(Sinks,ZipSlipConstant.getRules());
                System.out.println("[+] 加载ZipSlip规则");
            }
            if (module.contains("UNSERIALIZE")) {
                LoadSink.load(Sinks,UnserializeConstan.getRules());
                System.out.println("[+] 加载Unserialize规则");
            }
            if(command.rule!=null){
                System.out.println("[+] 使用自定义rule");
                load_rules(Sinks,command);
            }
        }
    }

    private static void loadSource(Command command){
        if(command.source!=null&&command.source.equals("jenkins")) {
            LoadSource.loadJenkins(Sources,classFileList);
        }else{
            LoadSource.loadsource(Sources,classFileList);
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
        System.out.println("[+] 一共分析了" +classFileList.size()+"个类");
    }


    private static void getClassinfo(){
        DiscoverService.start(classFileList,discoveredClasses,discoveredMethods,classMap,methodMap,classFileByName);
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

    private static void parseSink(){
        SinkParseService SinkParseService = new SinkParseService(InheritanceMap,methodCall,methodMap,classMap,Sinks,stacks);
        SinkParseService.start();
    }
    private static void parseOnlySink(){
        onlySinkParseServerice onlySinkParseServerice = new onlySinkParseServerice(classFileByName,InheritanceMap,discoveredCalls,methodMap,classMap,Sinks,jarByClass);
        onlySinkParseServerice.start();
    }
    private static void startdraw() throws IOException {
        DarwService.start(stacks);
    }
}
