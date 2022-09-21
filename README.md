# java_asm_parse:

## 引言：

因为之前看了4ra1n师傅和r2的文章，所以对自动化代码审计很感兴趣。所以自己也跟着两位师傅的文章学习了一下。当学习到污点分析这些内容的时候感觉还是很吃力，所以就自己一边看代码一边自己上手写了一下。感觉单纯只是看的话，还是不能了解整个过程。

具体学习的话，还是主要参考看的是两位师傅的文章：

https://xz.aliyun.com/t/10433

https://xz.aliyun.com/t/10756?page=5#toc-0

核心代码依旧是参考的两位师傅和gadget-inspector的核心代码：

https://github.com/Er1cccc/ACAF

https://github.com/JackOfMostTrades/gadgetinspector

但在实际使用这些工具去分析已经有的cve的时候，感觉使用效果并不是特别好，尤其是分析的时候，经常出现污点中断导致分析失效。而且感觉这种分析不是特别适合实际操作使用，比如分析一个spring web的时候就是我不使用污点分析，而是直接匹配sink点也很少出现匹配得到的情况。

crilwa给我说java程序很复杂，soot或许比asm更适合一些抽象的分析。后续考虑去学习一下soot框架这些。

因为这是一个工具的介绍也就不多说学习上的讲解了，如果想学习还是多看看上面两个大佬的文章吧。这个工具只是我自己修复了点小bug和多增加了一点我想要的小功能吧。



## 工具使用

```
  Options:
    -t, --Taint
      【+】选择模式 模式一:使用污点分析 模式二:不使用污点分析 模式三:从sink逆推调用链
      Default: 0
    --all
      【+】加载所有lib
      Default: false
    -h, --help
      Help Info
    --jar
      【+】需要扫描的jar包
    --jdk
      【+】使用jdk中的rt.jar
      Default: false
    -m, --moudles
      【+】选择sink规则
    -r, --rule
      【+】加载sink自定义规则
    --source
      【+】source选择
```

上面一一解释用法

1. -t, --Taint

   【+】选择模式 模式一:使用污点分析 模式二:不使用污点分析 模式三:从sink逆推调用链

    -t 1 代表使用模式一，依次类推

2. --all
   【+】加载所有lib
     填上这个选项后会加载jar中的所有依赖的jar包

3. --jar
   【+】需要扫描的jar包
    这里可以填上需要扫描的jar包,列如 --jar 1.jar 2.jar 但是需要值得注意的是如果遇到1.jar中有com.test.Main这个类，并且2.jar中也有的时候会出现一些问题。

4. --jdk
   【+】使用jdk中的rt.jar

5. -m, --moudles
   【+】选择sink规则
     现在已经有 11 种规则
     使用 -m all 是加载所有规则
     -m SSRF  ssrf的规则
     -m XXE    xxe的规则
     -m SQLI   sql注入的规则  //这里有个尴尬的点，如果熟悉java中的sql注入的朋友应该不难发现，现在其实注入mybits的sql注入												应该更多的关注的是xml文件中的#参数。所以后续可能会去研究一下这里如何进行审计。不过好像        												看到过有工具已经实现了。

     -m RCE    命令执行的规则

     -m FileRead  文件读取的规则

     -m JNDI    JNDI注入的规则

     -m XSS     XSS 的规则  //其实xss这个规则是之前想要看看jenkins插件的漏洞弄得，不适用于其他。而且感觉xss这类应该和										    codeql自带规则一样，写成正则匹配方法名的形式。

      -m LDAP LDAP注入的规则

​          -m ZIPSLIP zipslip的规则

  	    -m UNSERIALIZE 反序列化漏洞的规则

​		 以上漏洞的规则大部分都是从codeql中找到的，因为知识储备有限+对于codeql代码的不熟悉，所以有些规则可能我也弄错了，如果想看一下可以再下面这里找一下。而且相对于来说，诸如FileRead和SSRF或者ZIPSLIP这一类的规则误报率会非常搞。

![image-20220918202020590.png](https://img1.imgtp.com/2022/09/18/B3a5KoTD.png)

​	当然也可以去codeql找自带的cwe规则      

6. -r, --rule

   【+】加载sink自定义规则

     举个例子

   ```
   java/lang/Runtime exec * Runtime-RCE -1 1
   javax/script/ScriptEngineManager getEngineByName RCE Script-RCE -1 2
   javax/script/ScriptEngine eval * RCE Script-RCE -1 2
   ```

   第一项是classname，第二项是方法名，第三项是方法的入参描述，如果不了解可以直接填`*`，第四项是规则的名字，第五项是规则中期望的危险入参是第几个，比如exec(String a)我们的危险入参是a，就填1，如果不知道是几就填-1，第六项是多规则选项。比如我们拿上面的列子，需要上面第2和第3规则都存在才能说是漏洞存在，那么我们后面都填2。如果需要三个规则，那么我们就要准备三个规则的同时后面最后一项都填3。

   储存在txt文件中，使用-r 1.txt

7. --source

   【+】source选择
     因为这里还没有设置一些source，我这里设置source因为不像前面几个师傅们一样专注于spring-web，所以我这里直接使用了正则匹配方法名来设置，这里设置了专门匹配do开头后面接一个大写字母，然后任意字母这样的规则，比如doMain这样的方法来收集source。这样确实很简陋，但是可以一步步改进，后面弄个，从外部接受规则？

   - ![image-20220918205554347.png](https://img1.imgtp.com/2022/09/18/UNXNG5RE.png)

## 使用例子

因为之前师傅们都是自己搭建的一个漏洞比较明显的环境来测试，我这里就不这样弄了，因为想试试在复杂环境中asm的污点分析这些还能不能用。

我这里找了一个调用链比较少的漏洞来做漏洞

这里使用CVE-2022-33980来做例子，

![image-20220918205053447.png](https://img1.imgtp.com/2022/09/18/TYoKPN7i.png)

环境我也会放在test文件夹中，可以自己下载测试。

```
java -jar java_asm_parse.jar --jar CVE-2022-33980-1.0-SNAPSHOT.jar --all -m RCE -t 1
```

使用污点分析

![image-20220918210547553.png](https://img1.imgtp.com/2022/09/18/phTOd2bj.png)



没有得到结果，跟踪分析了一下

发现污点在resolve这里断掉了

![image-20220918210742476.png](https://img1.imgtp.com/2022/09/18/VmS0OcHf.png)

后面再研究一下。这里先留着这个问题



模式2 不使用污点分析

```
java -jar java_asm_parse.jar --jar CVE-2022-33980-1.0-SNAPSHOT.jar --all -m RCE -t 2
```

![image-20220918211208936.png](https://img1.imgtp.com/2022/09/18/HuR9J78h.png)

这次倒是找到了。

不过就看到的这样，不使用污点分析的话，误报率就会非常高。



模式三 从sink逆推调用链

```
java -jar java_asm_parse.jar --jar CVE-2022-33980-1.0-SNAPSHOT.jar --all -m RCE -t 3
```

这个功能实在是一言难尽，首先使用这个给首先的要求：sink的descriptor必须要详细给出，不能使用`*`这样的替代,其次就是得出的结果太多了一弄就是几十个结果，根本没法看。如果真的想要使用的话，可能后期得配合neo4j数据库得到cha调用图或者使用别的优化方法才能够使用。
