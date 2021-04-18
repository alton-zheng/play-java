# Play Java

- 语言知识总结 - 非项目

&nbsp;

## 网络 & IO

&nbsp;

### 网络

- [x] [网络协议](io/docs/io-protocol.md)
- [x] [TCP](io/docs/io-tcp.md)
- [x] [HTTP & HTTPS](io/docs/io-http-https.md)
- [x] [Socket](io/docs/socket.md)

&nbsp;

### IO

- [x] [系统 IO 原理](io/docs/system-io.md)
- [x] [IO 路由](io/docs/io-route.md)
- [x] [Java File IO](io/docs/io.md)

&nbsp;

### BIO

- [x] [BIO 概览](io/docs/io-bio.md)

&nbsp;

### NIO

- [x] [nio 概览](io/docs/io-nio.md)
- [x] [buffer](io/docs/nio-buffer.md)
- [x] [ByteBuffer](io/docs/nio-buffer-bytebuffer.md)

&nbsp;

- [x] [Synchronous I/O Multiplexing](io/docs/nio-multiplexing.md)

- [x] [Channel](docs/nio-channel.md)
  - [x] [Channel 实现](io/docs/nio-channel-implement.md)
  - [x] [FileChannel](io/docs/nio-channel-filechannel.md)
  - [x] [SelectableChannel](io/docs/nio-channel-selectable-channel.md)
    - [x] [ServerSocketChannel 和 SocketChannel](io/docs/nio-channel-serversocket-and-socket-channel.md)

&nbsp;

#### 实操

- [I/O Multiplexing](io/docs/nio-channel-selector-code.md)

&nbsp;

### AIO

- [AIO 介绍](docs/io-aio.md)
  - [AsynchronousChannel](io/docs/aio-asynchronous-channel.md)

&nbsp;

## 操作系统

&nbsp;

## Java

&nbsp;

### Core

- [x] [JMH](java/docs/core/java-microbenchmark-harness.md)

&nbsp;

### [高并发](java/docs/java-concurrency)

- [多线程面试题](java/docs/java-concurrency/interview.md)
- [概览](java/docs/java-concurrency/thread-overview.md)
- [java-cas](java/docs/java-concurrency/java-cas.md)
- [cycicBarrier](java/docs/java-concurrency/java-cyclicBarrier.md)
- [java-read-write-lock](java/docs/java-concurrency/java-read-write-lock.md)
- [MarriagePhaser](java/docs/java-conccurrency/java-marriagePhaser.md)
- [Semaphore](java/docs/java-concurrency/semaphore.md)
- [Exchange](java/docs/java-concurrency/java-exchange.md)
- [Java 线程生命周期](java/docs/java-concurrency/java-thread-lifecycle.md)
- [thread jvm 面试题](java/docs/java-concurrency/thread-jvm-interview.md)[面试](java/docs/java-concurrency/thread-interview.md)

- [解析自旋锁 CAS 操作与 volatile ]() 
### **Java Concurrency Basics**
 - [x] [>> java.util.concurrent 概览](java/docs/java-concurrency/java-util-concurrent.md)
 - [x] [>> Java 如何启动一个线程](java/docs/java-concurrency/java-start-thread.md)
 - [x] [>> Java Runnable vs. Callable](java/docs/java-concurrency/java-runnable-callable.md)
 - [x] [>> Implementing a Runnable vs Extending a Thread](java/docs/java-concurrency/java-runnable-vs-extending-thread.md)
 - [x] [>>  Java Synchronized 关键字](java/docs/java-concurrency/java-synchronized.md)
 - [x] [>> Java Volatile 关键字](java/docs/java-concurrency/java-volatile.md)
 - [ ] [>> Guide to java.util.concurrent.Locks](java/docs/java-concurrency/java-concurrent-locks.md)
 - [ ] [>> Java CyclicBarrier](java/docs/java-concurrency/java-cyclic-barrier.md)
 - [ ] [>> An Introduction to Atomic Variables in Java](java/docs/java-concurrency/java-atomic-variables.md)
 - [ ] [>> Semaphores in Java](java/docs/java-concurrency/java-semaphore.md)
 - [ ] [>> Binary Semaphore vs Reentrant Lock](java/docs/java-concurrency/java-binary-semaphore-vs-reentrant-lock.md)
 - [ ] [Guide to the Java Phaser](java/docs/java-concurrency/java-phaser.md)
 - [ ] [>> The Dining Philosophers Problem in Java](java/docs/java-concurrency/java-dining-philoshophers.md)
 - [ ] [Guide to CountDownLatch in Java](java/docs/java-concurrency/java-countdown-latch.md)
 - [ ] [Java CyclicBarrier vs CountDownLatch](java/docs/java-concurrency/java-cyclicbarrier-countdownlatch.md)
 - [ ] [>> Introduction to Exchanger in Java](java/docs/java-concurrency/java-exchanger.md)
 - [x] [Java Thread 生命周期](java/docs/java-concurrency/java-thread-lifecycle.md)
 - [ ] [How to Kill a Java Thread](https://www.baeldung.com/java-thread-stop)
 - [ ] [wait and notify() Methods in Java](https://www.baeldung.com/java-wait-notify)
 - [ ] [Difference Between Wait and Sleep in Java](https://www.baeldung.com/java-wait-and-sleep)
 - [ ] [The Thread.join() Method in Java](https://www.baeldung.com/java-thread-join)
 - [ ] [Using a Mutex Object in Java](https://www.baeldung.com/java-mutex)
 - [ ] [>> The Thread.join() Method in Java](https://www.baeldung.com/java-thread-join)

&nbsp;

### **Advanced Concurrency in Java**

- [x] [Java 容器](java/docs/java-concurrency/java-container.md)
  - [x] [>> ConcurrentMap 指导](java/docs/java-concurrency/java-concurrent-map.md)
  - [x] [>>  Java 同步集合介绍](java/docs/java-concurrency/java-synchronized-collections.md)
  - [x] [>> Collections.synchronizedMap vs. ConcurrentHashMap](java/docs/java-concurrency/java-synchronizedmap-vs-concurrenthashmap.md)
  - [x] [>>  java.util.concurrent.BlockingQueue 指导](java/docs/java-concurrency/java-blocking-queue.md)
  - [x] [>> LinkedBlockingQueue vs ConcurrentLinkedQueue](java/docs/java-concurrency/java-queue-linkedblocking-concurrentlinked)
  - [x] [>> ConcurrentSkipListMap 介绍](java/docs/java-concurrency/java-concurrent-skip-list-map.md)

&nbsp;

- [x] [>> Java Thread Pool 介绍](java/docs/java-concurrency/thread-pool-java-and-guava.md)
  - [x] [>> Java  Thread 和 虚拟 Thread 区别](java/docs/java-concurrency/java-virtual-thread-vs-thread.md)
  - [x] [>> java.util.concurrent.Future 介绍](java/docs/java-concurrency/java-future.md)
  - [x] [>> CompletableFuture 介绍](java/docs/java-concurrency/java-completablefuture.md)
  - [x] [>> Java Fork/Join 框架介绍](java/docs/java-concurrency/java-fork-join.md)
  - [x] [>> Java ExecutorService 框架介绍](java/docs/java-concurrency/java-executor-service-tutorial.md)
  - [x] [>> ExecutorService – 等待线程结束](java/docs/java-concurrency/java-executor-wait-for-threads.md)
  - [x] [>> Vavr Future 介绍](java/docs/java-concurrency/vavr-future.md)
  - [x] [>> 用 Java 8 Parallel Streams 自定义线程池](java/docs/java-concurrency/java-8-parallel-streams-custom-threadpool.md)
  - [x] [>> Executors newCachedThreadPool() vs newFixedThreadPool()](java/docs/java-concurrency/java-executors-cached-fixed-threadpool.md)
  - [x] [>> Spring ThreadPoolTaskExecutor corePoolSize vs. maxPoolSize](java/docs/java-concurrency/java-threadpooltaskexecutor-core-vs-max-poolsize.md)
  - [x] [>> Java Web 服务 Thread Pool 配置](java/docs/java-concurrency/java-web-thread-pool-config.md)

&nbsp;

 - [ ] [Daemon Threads in Java](https://www.baeldung.com/java-daemon-thread)
 - [ ] [Guide to ThreadLocalRandom in Java](https://www.baeldung.com/java-thread-local-random)
 - [ ] [What is Thread-Safety and How to Achieve it?](https://www.baeldung.com/java-thread-safety)
 - [ ] [How to Delay Code Execution in Java](https://www.baeldung.com/java-delay-code-execution)
 - [ ] [An Introduction to ThreadLocal in Java](https://www.baeldung.com/java-threadlocal)
 - [x] [>> Concurrency with LMAX Disruptor – 介绍](java/docs/java-concurrency/lmax-disruptor-concurrency.md)
 - [ ] [>> Java Concurrency Interview Questions (+ Answers)](https://www.baeldung.com/java-concurrency-interview-questions)
 - [ ] [>> Priority-based Job Scheduling in Java](https://www.baeldung.com/java-priority-job-schedule)
 - [ ] [>> wait and notify() Methods in Java](https://www.baeldung.com/java-wait-notify)
 - [ ] [>> Guide to ThreadLocalRandom in Java](https://www.baeldung.com/java-thread-local-random)
 - [ ] [>> Difference Between Wait and Sleep in Java](https://www.baeldung.com/java-wait-and-sleep)
 - [ ] [>> Design Principles and Patterns for Highly Concurrent Applications](https://www.baeldung.com/concurrency-principles-patterns)
 - [ ] [>> Guide to Work Stealing in Java](https://www.baeldung.com/java-work-stealing)
 - [ ] [>> Asynchronous Programming in Java](https://www.baeldung.com/java-asynchronous-programming)
 - [ ] [>> Guide to RejectedExecutionHandler](https://www.baeldung.com/java-rejectedexecutionhandler)
 - [ ] [>> Common Concurrency Pitfalls in Java](https://www.baeldung.com/java-common-concurrency-pitfalls)
 - [ ] [>> Threading Models in Java](https://www.baeldung.com/java-threading-models)
 - [ ] [>> Using a Mutex Object in Java](https://www.baeldung.com/java-mutex)
 - [ ] [>> How to Delay Code Execution in Java](https://www.baeldung.com/java-delay-code-execution)
 - [ ] [>> What is Thread-Safety and How to Achieve it?](https://www.baeldung.com/java-thread-safety)
 - [ ] [>> Passing Parameters to Java Threads](https://www.baeldung.com/java-thread-parameters)
 - [ ] [>> Print Even and Odd Numbers Using 2 Threads](https://www.baeldung.com/java-even-odd-numbers-with-2-threads)
 - [ ] [>> Thread Safe LIFO Data Structure Implementations](https://www.baeldung.com/java-lifo-thread-safe)
 - [ ] [>> Bad Practices With Synchronization](https://www.baeldung.com/java-synchronization-bad-practices)
 - [ ] [>> How to Analyze Java Thread Dumps](https://www.baeldung.com/java-analyze-thread-dumps)
 - [ ] [>> How to Stop Execution After a Certain Time in Java](https://www.baeldung.com/java-stop-execution-after-certain-time)
 - [ ] [>> IllegalMonitorStateException in Java](https://www.baeldung.com/java-illegalmonitorstateexception)
 - [ ] [>> A Guide to False Sharing and @Contended](https://www.baeldung.com/java-false-sharing-contended)
 - [ ] [>> Why are Local Variables Thread-Safe in Java](https://www.baeldung.com/java-local-variables-thread-safe)
 - [ ] [>> Why Not To Start A Thread In The Constructor?](https://www.baeldung.com/java-thread-constructor)
 - [ ] [>> What Causes java.lang.OutOfMemoryError: unable to create new native thread](https://www.baeldung.com/java-outofmemoryerror-unable-to-create-new-native-thread)
 - [ ] [>> Guide to AtomicStampedReference in Java](https://www.baeldung.com/java-atomicstampedreference)
 - [ ] [>> Java Thread Deadlock and Livelock](https://www.baeldung.com/java-deadlock-livelock)
 - [ ] [>> Intro to Coroutines with Quasar](https://www.baeldung.com/java-quasar-coroutines)
 - [ ] [>> Testing Multi-Threaded Code in Java](https://www.baeldung.com/java-testing-multithreaded)
 - [ ] [>> Guide to AtomicMarkableReference](https://www.baeldung.com/java-atomicmarkablereference)
 - [ ] [>> Introduction to Lock Striping](https://www.baeldung.com/java-lock-stripping)
 - [ ] [>> Capturing a Java Thread Dump](https://www.baeldung.com/java-thread-dump)

  &nbsp;

### Other Concurrency Resources
 - [ ] [The Dining Philosophers Problem in Java](https://www.baeldung.com/java-dining-philoshophers)
 - [ ] [Java Concurrency Interview Questions (+ Answers)](https://www.baeldung.com/java-concurrency-interview-questions)
 - [ ] [Java Concurrency Utility with JCTools](https://www.baeldung.com/java-concurrency-jc-tools)

&nbsp;

### JVM
&nbsp;

## 数据结构和算法

&nbsp;

&nbsp;

## 设计模式

- [x] [软件设计模式概述](design-patterns-in-java/design-patterns-overview.md)

- [x] [GoF 的 23 种设计模式的分类和功能](design-patterns-in-java/design-patterns-type-and-function.md)

- [x] [UML统一建模语言是什么？](design-patterns-in-java/design-patterns-uml.md)

- [x] [UML类图及类图之间的关系](design-patterns-in-java/design-patterns-uml-relation.md)

- [x] [类关系记忆技巧](design-patterns-in-java/design-patterns-class-relation.md)

- [x] [UMLet的使用与类图的设计](design-patterns-in-java/design-patterns-umlet-user-and-class-design.md)

- [x] [什么才是优秀的软件架构？](design-patterns-in-java/design-patterns-good-software.md)

- [x] [如何正确使用设计模式？](design-patterns-in-java/design-patterns-correct-use.md)

- 面向对象设计原则
  - [x] [开闭原则](design-patterns-in-java/design-patterns-ocp.md)
  - [x] [里氏替换原则](design-patterns-in-java/design-patterns-lsp.md)
  - [x] [依赖倒置原则](design-patterns-in-java/design-patterns-dip.md)
  - [x] [单一职责原则](design-patterns-in-java/design-patterns-srp.md)
  - [x] [接口隔离原则](design-patterns-in-java/design-patterns-isp.md)
  - [x] [迪米特法则](design-patterns-in-java/design-patterns-lkp.md)
  - [x] [合成复用原则](design-patterns-in-java/design-patterns-crp.md)
  - [x] [总结](design-patterns-in-java/design-patterns-oop-design-principle.md)

- Creational Patterns

  - [x] [创建型设计模式特点和分类](design-patterns-in-java/design-patterns-create-feature-and-type.md)
  - [x] [工厂方法比较](design-patterns-in-java/design-patterns-factory-comparison.md)
  - [x] [抽象工厂模式](design-patterns-in-java/design-patterns-abstract-factory.md)
  - [x] [生成器模式](design-patterns-in-java/design-patterns-bulider.md)
  - [x] [原型（Prototype）模式](design-patterns-in-java/design-patterns-prototype.md)
  - [x] [单例模式](design-patterns-in-java/design-patterns-singleton.md)
  - [x] [简单工厂模式](design-patterns-in-java/design-patterns-static-factory-method-pattern.md)
  - [x] [工厂方法模式](design-patterns-in-java/design-patterns-factory-method-pattern.md)
  - [x] [对象池模式](design-patterns-in-java/design-patterns-object-pool-pattern.md)
  - [x] [实验](design-patterns-in-java/design-patterns-create-experiment.md)

- Structural Patterns
  - [x] [结构性设计模式概述](design-patterns-in-java/design-patterns-structual-patterns-overview.md)
  - [x] [适配器模式](design-patterns-in-java/design-patterns-adapter.md)
- [x] [桥接模式](design-patterns-in-java/design-patterns-bridge.md)
  - [x] [组合模式](design-patterns-in-java/design-patterns-composite.md)
  - [x] [装饰模式](design-patterns-in-java/design-patterns-decorator.md)
  - [x] [外观模式](design-patterns-in-java/design-patterns-facade.md)
  - [x] [享元模式](design-patterns-in-java/design-patterns-flyweight.md)
  - [x] [代理模式](design-patterns-in-java/design-patterns-proxy.md)
  
- Behavioral Patterns
  - [x] [Behavioral Patterns 概览](design-patterns-in-java/design-patterns-behavioral-patterns-overview.md)
  - [x] [责任链模式](design-patterns-in-java/design-patterns-chain-of-responsibility.md)
  - [x] [命令模式](design-patterns-in-java/design-patterns-command.md)
  - [x] [迭代器模式](design-patterns-in-java/design-patterns-iterator.md)
  - [x] [中介者模式](design-patterns-in-java/design-patterns-mediator.md)
  - [x] [备忘录模式](design-patterns-in-java/design-patterns-memento.md)

  - [x] [观察者模式](design-patterns-in-java/design-patterns-observer.md)
  - [x] [状态模式](design-patterns-in-java/design-patterns-state.md)
  - [x] [策略模式](design-patterns-in-java/design-patterns-strategy.md)
  - [x] [模板方法模式](design-patterns-in-java/design-patterns-template-method.md)
  - [x] [访问者模式](design-patterns-in-java/design-patterns-visitor.md)
  - [x] [访问者和双分派](design-patterns-in-java/design-patterns-visitor-double-dispatch.md)

&nbsp;

## Netty

- []

&nbsp;

## Dubbo

&nbsp;

## MQ

### ActiveMQ

&nbsp;

### RocketMQ

&nbsp;

### Kafka
- [Kafka](http://github.com/alton-zheng/kafka.git)
&nbsp;
## Redis
- [Redis](https://github.com/alton-zheng/redis.git)

&nbsp;

## 微服务篇

&nbsp;

## 项目篇

&nbsp;

## 面试题

- [运维 安全 DevOps 网络 DBA 等面试专题](java/docs/other/safe-interview.md)
- [jvm-高并发-interview](java/docs/jvm/jvm-interview.md)
- [项目-interview](java/docs/project-interview.md)


