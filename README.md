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
      【+】选择模式 模式一:使用污点分析 模式二:不使用污点分析 模式三:从sink逆推调用链 模式四:只分析源码中是否存在sink点
      Default: 0
    --all
      【+】加载所有lib
      Default: false
    --draw
      【+】画出调用图
      Default: false
    -h, --help
      Help Info
    --jdk
      【+】使用jdk中的rt.jar
      Default: false
    -l, --lib
      【+】需要扫描的jar包
    -ld, --libs
      【+】需要扫描的jar包所在文件夹
    -m, --moudles
      【+】选择sink规则
    -r, --rule
      【+】加载sink自定义规则
    --source
      【+】source选择
   	    --methodName
    --methodName
      【+】想要搜寻方法
    --Save
      【+】保存sink规则
```

上面一一解释用法

1. **-t, --Taint**

   【+】选择模式 模式一:使用污点分析 模式二:不使用污点分析 模式三:从sink逆推调用链 模式四 只分析源码中是否存在sink点

    -t 1 代表使用模式一，依此类推

2. **--all**
   【+】加载所有lib
     填上这个选项后会加载jar中的所有依赖的jar包

3. **-l, --lib**
   【+】需要扫描的jar包
    这里可以填上需要扫描的jar包,列如 -l 1.jar 2.jar 但是需要值得注意的是如果遇到1.jar中有com.test.Main这个类，并且2.jar中也有的时候会出现一些问题。

4. **-ld, --libs**
   【+】需要扫描的jar包
    这里可以填上需要扫描的jar包,列如 -l 1.jar 2.jar 但是需要值得注意的是如果遇到1.jar中有com.test.Main这个类，并且2.jar中也有的时候会出现一些问题。

5. **--jdk**
   【+】使用jdk中的rt.jar

6. **-m, --moudles**
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

      -m ZIPSLIP zipslip的规则

  	  -m UNSERIALIZE 反序列化漏洞的规则

​		 以上漏洞的规则大部分都是从codeql中找到的，因为知识储备有限+对于codeql代码的不熟悉，所以有些规则可能我也弄错了，如果想看一下可以再下面这里找一下。而且相对于来说，诸如FileRead和SSRF或者ZIPSLIP这一类的规则误报率会非常搞。

![image-20220918202020590.png](https://img1.imgtp.com/2022/09/18/B3a5KoTD.png)

​	当然也可以去codeql找自带的cwe规则      

7. **-r, --rule**

   【+】加载sink自定义规则

     举个例子

   ```
   java/lang/Runtime exec * Runtime-RCE -1 1
   javax/script/ScriptEngineManager getEngineByName * Script-RCE -1 2
   javax/script/ScriptEngine eval * Script-RCE -1 2
   ```

   第一项是classname，第二项是方法名，第三项是方法的入参描述，如果不了解可以直接填`*`，第四项是规则的名字，第五项是规则中期望的危险入参是第几个，比如exec(String a)我们的危险入参是a，就填1，如果不知道是几就填-1，第六项是多规则选项。比如我们拿上面的列子，需要上面第2和第3规则都存在才能说是漏洞存在，那么我们后面都填2。如果需要三个规则，那么我们就要准备三个规则的同时后面最后一项都填3。

   储存在txt文件中，使用-r 1.txt

7. **--source**

   【+】source选择
     因为这里还没有设置一些source，我这里设置source因为不像前面几个师傅们一样专注于spring-web，所以我这里直接使用了正则匹配方法名来设置，这里设置了专门匹配do开头后面接一个大写字母，然后任意字母这样的规则，比如doMain这样的方法来收集source。~~这样确实很简陋，但是可以一步步改进，后面弄个，从外部接受规则~~

   ~~现在如果想要使用的话，还是需要自己去源码中的`SourceClassVisitor` 自行更改正则规则~~
   
   - ![image-20220918205554347.png](https://img1.imgtp.com/2022/09/18/UNXNG5RE.png)
   
     使用
   
     ```
     --source "这里填正则来匹配方法名"
     ```
   
     或许后面还可以用添加一些其他的方法来使用，比如有个函数在其内部有接收数据的方法就给其添加上。

9.    --methodName
      【+】想要搜寻方法
        这个方法是有些时候在工具的使用中需要知道方法的入参描述。就可以单独使用
      
        ```
      java -jar java_asm_parse-1.0-SNAPSHOT.jar -l 1.jar --methodName "RCEtest"
        ```
      
      那么就会把所有名字叫RCEtest的函数入参描述输出。
      
10.    --Save
         【+】保存sink规则
           这个选项主要是为了在模式四中，根据已有的规则来得到的调用了该规则的方法。这样做是实际在做的时候，可以先筛选一遍减少无用链子。





## 使用例子

因为之前师傅们都是自己搭建的一个漏洞比较明显的环境来测试，我这里就不这样弄了，因为想试试在复杂环境中asm的污点分析这些还能不能用。

我这里找了一个调用链比较少的漏洞来做漏洞

这里使用CVE-2022-33980来做例子，

![image-20220918205053447.png](https://img1.imgtp.com/2022/09/18/TYoKPN7i.png)

环境我也会放在test文件夹中，可以自己下载测试。

```
java -jar java_asm_parse-1.0-SNAPSHOT.jar -l CVE-2022-33980-1.0-SNAPSHOT.jar --all -m RCE -t 1
```

使用污点分析

- ![image-20221004141902126.png](https://img1.imgtp.com/2022/10/04/evsER6XP.png)

得到四条链子，但是只有第一条链子有用，其余三条链子出现误报。为什么会出现这种误报？之所以会产生这种误报，主要是调用实现类无法在静态分析中直接得出，所以会在中途将该抽象类的所有实现类都遍历一遍。



在之前版本中，使用模式一是得不到结果的。

跟踪分析了一下

发现数据流在，数组这里断掉了。

![1.png](https://s2.loli.net/2022/09/29/n1rftaxs4pgHho2.png)



写了一个如下列子来测试

- ![1111.png](https://img1.imgtp.com/2022/10/04/9wh5aOUe.png)

![222.png](https://img1.imgtp.com/2022/10/04/JGWjMy9z.png)



后来改了一下模拟帧栈的核心类，AALLOAD这里，发现污点能继续传递下去了。

- ![3333.png](https://img1.imgtp.com/2022/10/04/CA4eaUlO.png)





模式2 不使用污点分析

```
java -jar java_asm_parse.jar --jar CVE-2022-33980-1.0-SNAPSHOT.jar --all -m RCE -t 2
```

- ![image-20221004143920726.png](https://img1.imgtp.com/2022/10/04/VXn9iMbd.png)

这样也能找到，但是呢还是会出现一些问题，相比较于模式一，首先链子普遍要长很多，很多也不准确相差比较大。但是会提供更多的路径。同样的，与模式1一样，还是会出现，关于抽象类与实现类遍历的问题。



模式三 从sink逆推调用链

```
java -jar java_asm_parse.jar -l CVE-2022-33980-1.0-SNAPSHOT.jar --all -m RCE -t 3
```

这个功能实在是一言难尽，太多了一弄就是几十个结果，根本没法看。如果真的想要使用的话，可能后期得配合neo4j数据库得到cha调用图或者使用别的优化方法才能够使用。
这里找了个老项目拿来改了一下，https://github.com/masters-info-nantes/bytecode-to-cfg
能生成调用图了，后面命令的最后需要加一个--draw，但是生成的调用图并不好看。使用的时候需要将生成的jar文件与html文件夹放在一个目录下。
[![xEx7y6.png](https://s1.ax1x.com/2022/09/26/xEx7y6.png)](https://imgse.com/i/xEx7y6)

模式三这种将所有方法都定义为sourc。逆推链子得到的数据会变得极为庞大且不适用。所以做了个小优化，将链子最后的方法进行分类，然后将这个分类中最短的链子输出。其余链子放在了Stack.dat文件中，若想比对也可以使用。

这里拿最近的CVE-2022-41852做个例子

- ![image-20221013092920050.png](https://img1.imgtp.com/2022/10/13/WKNFnjZo.png)

- ![image-20221013093112464.png](https://img1.imgtp.com/2022/10/13/60k2SzDF.png)



又或者拿昨天有师傅看到得

apache.commons.text

- ![image-20221013093640368.png](https://img1.imgtp.com/2022/10/13/eIGlMvGO.png)

虽然这个是在看CVE-2022-33980也发现了得，但是当时也没注意。

以上给出得例子，链子都不是特别复杂，在更多得分析当中，模式三可能并不适用





模式四 只分析源码中是否存在sink点

这里用使用log4j2存在漏洞的版本来做个例子，我将jar包全部储存在一个文件夹中。

使用

```
java -jar java_asm_parse-1.0-SNAPSHOT.jar -ld SSS --all -m JNDI -t 42
```

- ![image-20221004144527968.png](https://img1.imgtp.com/2022/10/04/pNOwKBOJ.png)

可以看到找到了漏洞sink点

