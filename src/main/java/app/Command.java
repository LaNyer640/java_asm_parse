package app;

import com.beust.jcommander.Parameter;

import java.util.List;

public class Command {
    @Parameter(names = {"-h", "--help"}, description = "Help Info", help = true)
    public boolean help;

    @Parameter(names = {"--jar"},description = "【+】需要扫描的jar包")
    public List<String> jar = null;

    @Parameter(names = {"--jdk"}, description = "【+】使用jdk中的rt.jar")
    public boolean jdk;

    @Parameter(names = {"--all"}, description = "【+】加载所有lib")
    public boolean lib;

    @Parameter(names = {"-m","--moudles"}, description = "【+】选择sink规则")
    public String module;

    @Parameter(names = {"-r","--rule"}, description = "【+】加载sink自定义规则")
    public String rule=null;

    @Parameter(names = {"--source"}, description = "【+】source选择")
    public String source=null;

    @Parameter(names = {"-t", "--Taint"}, description = "【+】选择模式 模式一:使用污点分析 模式二:不使用污点分析 模式三:从sink逆推调用链 模式四:只分析源码中是否存在sink点")
    public int taint;

    @Parameter(names = {"--draw"}, description = "【+】画出调用图")
    public boolean draw;
}
