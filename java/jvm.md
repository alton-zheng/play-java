## JVM 优化

### 内存划分为几个区，每个区的作用

- 方法区：
  - 也称为永久代。
  - 在该区很少发生 `gc`，一般 `gc` 是针对常量池和对类型的卸载。
  - 主要用于存储已经被 `jvm`加载的 `类信息`，`常量`，`静态变量`，`代码`等。
  - 线程共享。

- 虚拟机栈：
  - 为方法服务，每个方法在执行时会创建一个栈帧，用于存储局部变量表，方法出口等。
  - 线程私有。

- 本地方法栈：
  - 与虚拟机栈类似，
  - 只不过本地方法栈为 native 方法服务。

- 堆：
  - 存储对象的实例。
  - 线程共享。

- 程序计数器：
  - pc
  - 存储下一条所需要执行的字节码的地址

&nbsp;

### 如何判断一个对象是否存活

判断对象是否存活有两种方法:

- 引用计数法:
  - 给每个对象设置一个引用计数器，每当有一个地方引用这个对象时就+1，引用计算器为0时则进行回收。单纯的引用计数无法解决对象相互引用的问题。

```java
objA.instance = objB
objB.instance = objA

// 其实当赋值完成后，两个对象已经不可能再被引用，但此时他们的 引用计数 为 1，这样永远无法回收， 直到整个程序关闭
```

&nbsp;

- 可达性分析: JVM 用
  - 从 `gc Roots` 开始向下搜索，如果一个对象到 Gc Roots 没有引用则说明该对象不可达。
  - `Gc Roots`
    - 对下列引用的对象
      - 虚拟机栈 （栈帧中的本地变量表， 各个线程被调用的方法堆栈中使用的参数， 局部变量， 临时变量等）
      - 方法区静态属性 (JAVA 类的引用类型静态变量)
      - 方法区中常量池 （e.g. String Table）
      - 本地方法栈 JNI （Native 方法）
      - 虚拟机内部的引用
        - 如基本数据类型对应的 Class 对象
        - 常驻的异常对象 (`NullPointException`, `OutOfMemoryError` 等)
        - 系统器加载器
      - 被同步锁（synchronized 关键字）持有的对象
      - 反映 JAVA 虚拟机内部情况的 JMXBean/JVMT1 中注册的回调，本地方法缓存等。
      - “临时性”加入
        - 分代收集
        - 局部回收
  - 当一个对象不可达时，会被标记一次进行 “缓行” 阶段
  - 然后对象是否有必要执行 finalize() 方法（最后一次来逃脱被回收的机会， 调用它后，一定时间范围内只要重新和 `GC Roots` 任何一个对象建立引用即可逃脱。）， 下面2种情况，表明没有必要执行 finalize() 方法：
    - 没有覆盖 finalize() 方法
    - finalize() 方法已经被虚拟机调用过

&nbsp;

### 简述java垃圾回收机制

垃圾收集算法大致有三种:

- 标记-清除

  - 基础算法
  - 缺点
    - 执行效率不稳定
    - 内存空间的碎片化问题， 产生连续的大量内存碎片， 空间碎片太多导致程序运行中，需要分配较大对象时， 无法找到足够的连续内存而不得不提前触发 GC
  - 回收器
    - CMS
      - 优秀的收集器
      - 获取最短回收停顿时间为目标
      - (coreNum + 3) / 4
      - 步骤
        - 初始标记
          - “Stop The World"
          - 标记 GC Roots 能直接关联到的对象
        - 并发标记
          - 直接关联对象 -> 遍历整个对象图
          - 耗时长
        - 重新标记
          - “Stop the world"
          - 修正并发标记的对象产生变动部分
        - 并发清除
          - 耗时长

- 标记-复制

  - 解决 标记-清除算法执行效率不高的问题
  - “半区复制”
  - 缺陷
    - 产生大量的内存间复制开销
    - 可用内存缩小为原来的一半，内存空间浪费
  - Java 虚拟机商用采用这种方式
  - 半区比例调整优化后，在新生代收集器中采用  `朝生夕灭`
  - 收集器
    - `Paraller Scavenge` 新生代收集器
      - 达到可控的吞吐量（Throughput)
        - 运行用户代码时间/（运行用户代码时间 + 运行垃圾收集时间）

- 标记-整理（Mark - Compact）

  - 主要针对老年代回收（大部分对象存活）

  - 原理与 `标记-清理` 算法类似， 只是在标记后，不直接清理，而是所有存活对象都向内存空间一端移动，然后直接清理存活对象区域以外的内存空间

  - 缺陷

    - 移动存活对象， 造成用户应用程序暂停， “Stop the world” 现象

  - 垃圾回收器

    - Serial Old:
      - 老年代
      - 单线程
    - Parallel Old
      - 老年代
      - 多线程并发



Java中垃圾回收器：

- G1

  - 面向堆内存任何部分来组成回收集（Collection Set, CSet）进行回收
  - CMS 的替代者和继承人
  - Mixed GC
    - 哪里垃圾多，就收集哪里
  - G1 算法复杂，从提出到商用，经历了8年（2004年 -》 2012年）
  - 引用 Region 概念
    - 将 heap 划分为大小相等的独立区域
    - 初始标记，并发标记，最后标记，筛选回收
- ZGC
- Serial:单线程收集器，只会使用一个线程完成垃圾收集工作。在进行垃圾收集时必须暂停其余线程。
- Parnew：Serial收集器的多线程版本，使用多条线程进行垃圾回收工作。
- Parallel Scavenge：吞吐量优先，主要适合在后台运算而不需要太多交互的任务。配合有自适应策略。
- Serial old：serial收集器的老年代版本
- Parallel old：parallel 收集器的老年代版本
- CMS收集器：最短回收停顿；

&nbsp;

### Java类加载过程

加载：通过一个类的全限定名获取该类的二进制流，将二进制流中的静态存储结构转化为运行时数据结构，生成该类的Class对象。

验证：为了确保Class文件的信息不会危害到计算机而进行了文件格式验证，元数据验证，字节码验证，符号引用验证等。

准备：为类的静态变量分配内存初始化为默认值

解析：完成符号引用到直接引用的转换

初始化：执行类中的java代码（静态代码块和静态变量初始化）

&nbsp;

### 类加载器双亲委派模型机制

当一个类收到了类加载请求时，不会自己先去加载这个类，而是将其委派给父类，父类若不能加载，反馈给子类，由子类完成类的加载。以保证所有相同的类在各种类加载器的环境中都是同一个类，行为保持一致。例如如果自己写了一个 Object 类，在加载过程中加载请求会最终提交到 Bootstrap classloader，其会默认加载位于 lib 下的 rt.jar 包，找到 java 原生的 Object 类进行加载。

&nbsp;

### 什么是类加载器，类加载器有几种

负责将可能是网络上也可能是磁盘上的 class 文件加载到内存并生成Class对象。一旦一个类被加载进了jvm，同一个类就不会再次被加载，java识别是包名加类名。在 jvm 中是使用全限定类名和加载器作为标识，若加载器不同则生成的 class 文件也认为不同。

启动类加载器 BootstrapClassLoader：JVM 内核，主要负责 lib 下的类库，无法直接被程序使用。

扩展类加载器 ExtensionClassLoader：启动类加载器的子类，主要加载 lib/ext 的类库

系统类加载器 SystemClassLoader：classpath下的 jar包 和 class 文件，扩展类加载器的子类

User-Defined class loader：继承于系统类加载器

&nbsp;

### Java 内存分配和回收策略以及 Minor GC 和 Major GC

> JVM 采用分代内存回收，主要回收堆和持久代。堆内分为年轻代和老年代，年轻代再划分为 Eden 与 Survivor ，比例为8:1:1，针对年轻代垃圾回收的机制称为 minor GC，针对老年代和永久代垃圾回收的称为major GC。
>
> 对象优先分配到 `Eden` 区域，如果内存不够则触发 minor GC ，若对象为大对象（很长的字符串以及数组）会直接进入老年代。
>
> Minor GC 触发：
>
> - Eden 区域内存不足，将存活的对象复制到空的 Survivor 中，并且年龄 +1，年龄达到一定阈值会被移动到老年代。
> - 如果 Survivor 空间中相同年龄所有对象大小的总和大于 Survivor 一半，年龄大于这个值就可以进入老年区。发生 minor GC 之前，如果新生代所有空间大于了老年代的剩余空间，如果允许担保失败，会检查剩余空间是否大于历次平均晋升大小，如果大于会进行尝试，如果不允许冒险就会进行一次 major GC（空间分配担保）。Java1.6后有更新。
>
> Major gc:
>
> - 在老年代或永久代内存不足，以及统计得到 minor GC 晋升到老年代的平均大小大于老年代和永久代的剩余空间时触发。
> - 至少伴随一次Minor GC。

&nbsp;

### 类的实例化顺序

> - 父类的 static 代码块及 static 变量（依赖书写顺序)
>
> - 当前类的 static 代码块及 static 变量（依赖书写顺序)
>
> - 父类普通代码块及变量，父类的构造函数
>
> - 子类普通代码及变量，子类构造函数。

&nbsp;

### Volatile类型变量

>  Java提供的轻量级同步机制，用其修饰的变量有两种特性:
>
>  - 第一是保证此变量对所有线程的可见性，普通变量的值在线程中传递需要通过主内存来完成。但在java中的运算不是原子操作，导致 volatile 运算在并发下也不一定是安全的。
>  - 第二是禁止了指令的重排序优化。Volatile 的开销小于锁的开销。
>
>  在符合运算结果不依赖变量的当前值，或者能够确保只有单一的线程修改变量的值，变量不需要和其他的状态变量一同参与不变约束的情况下。使用 volatile 可以保证原子性。

&nbsp;

### JVM常见参数

>Xmx ：heap的最大值
>
>Xms ：heap的初始值
>
>Xmn ：young区的大小
>
>Xss :    stack的大小
>
>Xx: permsize : 永久区的大小（初始）