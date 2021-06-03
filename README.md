# Play Java

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

### [高并发](java-concurrency)

- [并发编程的挑战](java-concurrency/java-concurrency-challenge.md)

- [多线程面试题](java-concurrency/interview.md)
- [概览](java-concurrency/thread-overview.md)
- [java-cas](java-concurrency/java-cas.md)
- [cycicBarrier](java-concurrency/java-cyclicBarrier.md)
- [java-read-write-lock](java-concurrency/java-read-write-lock.md)
- [MarriagePhaser](java/docs/java-conccurrency/java-marriagePhaser.md)
- [Semaphore](java-concurrency/semaphore.md)
- [Exchange](java-concurrency/java-exchange.md)
- [Java 线程生命周期](java-concurrency/java-thread-lifecycle.md)
- [thread jvm 面试题](java-concurrency/thread-jvm-interview.md)[面试](java-concurrency/thread-interview.md)

- [解析自旋锁 CAS 操作与 volatile ]() 
### **Java Concurrency Basics**
 - [x] [>> java.util.concurrent 概览](java-concurrency/java-util-concurrent.md)
 - [x] [>> Java 如何启动一个线程](java-concurrency/java-start-thread.md)
 - [x] [>> Java Runnable vs. Callable](java-concurrency/java-runnable-callable.md)
 - [x] [>> Implementing a Runnable vs Extending a Thread](java-concurrency/java-runnable-vs-extending-thread.md)
 - [x] [>>  Java Synchronized 关键字](java-concurrency/java-synchronized.md)
 - [x] [>> Java Volatile 关键字](java-concurrency/java-volatile.md)
 - [ ] [>> Guide to java.util.concurrent.Locks](java-concurrency/java-concurrent-locks.md)
 - [ ] [>> Java CyclicBarrier](java-concurrency/java-cyclic-barrier.md)
 - [ ] [>> An Introduction to Atomic Variables in Java](java-concurrency/java-atomic-variables.md)
 - [ ] [>> Semaphores in Java](java-concurrency/java-semaphore.md)
 - [ ] [>> Binary Semaphore vs Reentrant Lock](java-concurrency/java-binary-semaphore-vs-reentrant-lock.md)
 - [ ] [Guide to the Java Phaser](java-concurrency/java-phaser.md)
 - [ ] [>> The Dining Philosophers Problem in Java](java-concurrency/java-dining-philoshophers.md)
 - [ ] [Guide to CountDownLatch in Java](java-concurrency/java-countdown-latch.md)
 - [ ] [Java CyclicBarrier vs CountDownLatch](java-concurrency/java-cyclicbarrier-countdownlatch.md)
 - [ ] [>> Introduction to Exchanger in Java](java-concurrency/java-exchanger.md)
 - [x] [Java Thread 生命周期](java-concurrency/java-thread-lifecycle.md)
 - [ ] [How to Kill a Java Thread](https://www.baeldung.com/java-thread-stop)
 - [ ] [wait and notify() Methods in Java](https://www.baeldung.com/java-wait-notify)
 - [ ] [Difference Between Wait and Sleep in Java](https://www.baeldung.com/java-wait-and-sleep)
 - [ ] [The Thread.join() Method in Java](https://www.baeldung.com/java-thread-join)
 - [ ] [Using a Mutex Object in Java](https://www.baeldung.com/java-mutex)
 - [ ] [>> The Thread.join() Method in Java](https://www.baeldung.com/java-thread-join)

&nbsp;

### **Advanced Concurrency in Java**

- [x] [Java 容器](java-concurrency/java-container.md)
  - [x] [>> ConcurrentMap 指导](java-concurrency/java-concurrent-map.md)
  - [x] [>>  Java 同步集合介绍](java-concurrency/java-synchronized-collections.md)
  - [x] [>> Collections.synchronizedMap vs. ConcurrentHashMap](java-concurrency/java-synchronizedmap-vs-concurrenthashmap.md)
  - [x] [>>  java.util.concurrent.BlockingQueue 指导](java-concurrency/java-blocking-queue.md)
  - [x] [>> LinkedBlockingQueue vs ConcurrentLinkedQueue](java-concurrency/java-queue-linkedblocking-concurrentlinked)
  - [x] [>> ConcurrentSkipListMap 介绍](java-concurrency/java-concurrent-skip-list-map.md)

&nbsp;

- [x] [>> Java Thread Pool 介绍](java-concurrency/thread-pool-java-and-guava.md)
  - [x] [>> Java  Thread 和 虚拟 Thread 区别](java-concurrency/java-virtual-thread-vs-thread.md)
  - [x] [>> java.util.concurrent.Future 介绍](java-concurrency/java-future.md)
  - [x] [>> CompletableFuture 介绍](java-concurrency/java-completablefuture.md)
  - [x] [>> Java Fork/Join 框架介绍](java-concurrency/java-fork-join.md)
  - [x] [>> Java ExecutorService 框架介绍](java-concurrency/java-executor-service-tutorial.md)
  - [x] [>> ExecutorService – 等待线程结束](java-concurrency/java-executor-wait-for-threads.md)
  - [x] [>> Vavr Future 介绍](java-concurrency/vavr-future.md)
  - [x] [>> 用 Java 8 Parallel Streams 自定义线程池](java-concurrency/java-8-parallel-streams-custom-threadpool.md)
  - [x] [>> Executors newCachedThreadPool() vs newFixedThreadPool()](java-concurrency/java-executors-cached-fixed-threadpool.md)
  - [x] [>> Spring ThreadPoolTaskExecutor corePoolSize vs. maxPoolSize](java-concurrency/java-threadpooltaskexecutor-core-vs-max-poolsize.md)
  - [x] [>> Java Web 服务 Thread Pool 配置](java-concurrency/java-web-thread-pool-config.md)

&nbsp;

 - [ ] [Daemon Threads in Java](https://www.baeldung.com/java-daemon-thread)
 - [ ] [Guide to ThreadLocalRandom in Java](https://www.baeldung.com/java-thread-local-random)
 - [ ] [What is Thread-Safety and How to Achieve it?](https://www.baeldung.com/java-thread-safety)
 - [ ] [How to Delay Code Execution in Java](https://www.baeldung.com/java-delay-code-execution)
 - [ ] [An Introduction to ThreadLocal in Java](java-concurrensacy/java-concurrency-threadlocal.md)
 - [x] [>> Concurrency with LMAX Disruptor – 介绍](java-concurrency/lmax-disruptor-concurrency.md)
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

## 设计模式

- [ ] [软件设计模式概述](design-patterns-in-java/design-patterns-overview.md)

- [ ] [GoF 的 23 种设计模式的分类和功能](design-patterns-in-java/design-patterns-type-and-function.md)

- [ ] [UML统一建模语言是什么？](design-patterns-in-java/design-patterns-uml.md)

- [ ] [UML类图及类图之间的关系](design-patterns-in-java/design-patterns-uml-relation.md)

- [ ] [类关系记忆技巧](design-patterns-in-java/design-patterns-class-relation.md)

- [ ] [UMLet的使用与类图的设计](design-patterns-in-java/design-patterns-umlet-user-and-class-design.md)

- [ ] [什么才是优秀的软件架构？](design-patterns-in-java/design-patterns-good-software.md)

- [ ] [如何正确使用设计模式？](design-patterns-in-java/design-patterns-correct-use.md)

- 面向对象设计原则
  - [ ] [开闭原则](design-patterns-in-java/design-patterns-ocp.md)
  - [ ] [里氏替换原则](design-patterns-in-java/design-patterns-lsp.md)
  - [ ] [依赖倒置原则](design-patterns-in-java/design-patterns-dip.md)
  - [ ] [单一职责原则](design-patterns-in-java/design-patterns-srp.md)
  - [ ] [接口隔离原则](design-patterns-in-java/design-patterns-isp.md)
  - [ ] [迪米特法则](design-patterns-in-java/design-patterns-lkp.md)
  - [ ] [合成复用原则](design-patterns-in-java/design-patterns-crp.md)
  - [ ] [总结](design-patterns-in-java/design-patterns-oop-design-principle.md)

&nbsp;

- Creational Patterns

  - [x] [Crateional Patterns Overview](design-patterns-in-java/design-patterns-create-feature-and-type.md)
  - [x] [1 - Singleton Pattern](design-patterns-in-java/design-patterns-singleton.md)
  - [x] [2.1 - Static Factory method Pattern](design-patterns-in-java/design-patterns-static-factory-method-pattern.md)
  - [x] [2.2  - Factory method Pattern](design-patterns-in-java/design-patterns-factory-method-pattern.md)
  - [x] [3 - Abstract Factory Pattern](design-patterns-in-java/design-patterns-abstract-factory.md)
  - [x] [Compare of Factory Pattern](design-patterns-in-java/design-patterns-factory-comparison.md)
  - [x] [4 - Builder Pattern](design-patterns-in-java/design-patterns-bulider.md)
  - [x] [5 - Prototype Pattern](design-patterns-in-java/design-patterns-prototype.md)

&nbsp;

- Structural Patterns
  - [x] [Structural Patterns overview](design-patterns-in-java/design-patterns-structual-patterns-overview.md)
  - [x] [Adapt Pattern](design-patterns-in-java/design-patterns-adapter.md)
  - [x] [Bridge Pattern](design-patterns-in-java/design-patterns-bridge.md)
  - [x] [Conposite Pattern](design-patterns-in-java/design-patterns-composite.md)
  - [x] [Decorate Pattern](design-patterns-in-java/design-patterns-decorator.md)
  - [x] [Facade Pattern](design-patterns-in-java/design-patterns-facade.md)
  - [x] [Flyweight Pattern](design-patterns-in-java/design-patterns-flyweight.md)
  - [x] [Proxy Pattern](design-patterns-in-java/design-patterns-proxy.md)
  
- Behavioral Patterns
  - [ ] [Behavioral Patterns 概览](design-patterns-in-java/design-patterns-behavioral-patterns-overview.md)
  - [ ] [](design-patterns-in-java/design-patterns-chain-of-responsibility.md)
  - [ ] [命令模式](design-patterns-in-java/design-patterns-command.md)
  - [ ] [迭代器模式](design-patterns-in-java/design-patterns-iterator.md)
  - [ ] [中介者模式](design-patterns-in-java/design-patterns-mediator.md)
  - [ ] [备忘录模式](design-patterns-in-java/design-patterns-memento.md)

  - [ ] [观察者模式](design-patterns-in-java/design-patterns-observer.md)
  - [ ] [状态模式](design-patterns-in-java/design-patterns-state.md)
  - [x] [策略模式](design-patterns-in-java/design-patterns-strategy.md)
  - [ ] [模板方法模式](design-patterns-in-java/design-patterns-template-method.md)
  - [ ] [访问者模式](design-patterns-in-java/design-patterns-visitor.md)
  - [ ] [访问者和双分派](design-patterns-in-java/design-patterns-visitor-double-dispatch.md)

&nbsp;

## Netty

- [x] [netty 简介](netty/docs/netty-overview.md)
- [x] [Netty VS Java NIO](netty/docs/netty-vs-javanio.md)

- [x] [Netty 介绍](netty/docs/netty-introduction.md)

&nbsp;

### Netty 核心

- [x] [ByteBuf](netty/docs/netty-bytebuf.md)
- [x] [Reactor 线程模型](netty/docs/netty-reactor-thread-pattern.md)
- [x] [Reactor 应用](netty/docs/netty-reactor-reference.md)

&nbsp;

### Netty 使用

- [x] [底层服务场景](netty/docs/netty-bootstrap-case.md)
- [x] [HTTP Server with Netty](netty/docs/netty-http-server.md)
- [x] [HTTP/2 in Netty](netty/docs/netty-http2.md)
- [x] [Spring Boot Reactor Netty Configuration](netty/docs/netty-spring-boot-reactor-netty.md)
- [x] [Testing Netty with EmbeddedChannel](netty/docs/netty-testing-netty-embedded-channel.md)
- [x] [Exceptions in Netty](netty/docs/netty-exception-handling.md)

&nbsp;

### Netty 源码分析

- [netty源码分析之揭开reactor线程的面纱（一）](https://www.jianshu.com/p/0d0eece6d467) "Source code analysis of reactor thread model(1)"
- [netty源码分析之揭开reactor线程的面纱（二）](https://www.jianshu.com/p/467a9b41833e) "Source code analysis of reactor thread model(2)"
- [netty源码分析之揭开reactor线程的面纱（三）](https://www.jianshu.com/p/58fad8e42379) "Source code analysis of reactor thread model(3)"
- [netty源码分析之服务端启动全解析](https://www.jianshu.com/p/c5068caab217) "Source code analysis of server startup"
- [netty源码分析之新连接接入全解析](https://www.jianshu.com/p/0242b1d4dd21) "Source code analysis of new connection"
- [netty源码分析之pipeline(一)](https://www.jianshu.com/p/6efa9c5fa702) "Source code analysis of pipeline(1)"
- [netty源码分析之pipeline(二)](https://www.jianshu.com/p/087b7e9a27a2) "Source code analysis of pipeline(2)"
- [netty源码分析之writeAndFlush全解析](https://www.jianshu.com/p/feaeaab2ce56) "Source code analysis of writeAndFlush()"
- [netty源码分析之拆包器的奥秘](https://www.jianshu.com/p/dc26e944da95) "Source code analysis of ByteToMessageDecoder"
- [netty源码分析之LengthFieldBasedFrameDecoder](https://www.jianshu.com/p/a0a51fd79f62) "Source code analysis of LengthFieldBasedFrameDecoder"
- [netty 堆外内存泄露排查盛宴](https://www.jianshu.com/p/4e96beb37935) "A memory leak troubleshoot of using netty-socketio"
- [x] [Netty 源码分析- Reactor](netty/docs/netty-source-code-analysis-reactor.md)
- [x] [Netty 源码深入剖析之旅 - 介绍](netty/docs/netty-source-analysis-introduction.md)
- [x] [Netty 源码深入剖析之旅- 示例](netty/docs/netty-source-analysis-example.md)
- [x] [Netty 源码深入剖析 - SelectorProvider (Multiplux)](netty/docs/netty-source-analysis-selector-provider.md)
- [x] [Netty 源码深入剖析 - RejectedExecutionHandlers](netty/docs/netty-source-analysis-rejected-execution-handlers.md)
- [x] [Netty 源码深入剖析 - MultithreadEventLoopGroup](netty/docs/netty-source-analysis-multithread-eventloop-group.md)
  - [x] [Netty 源码深入剖析 - ThreadFactory](netty/docs/netty-source-analysis-thread-factory.md)
  - [x] [Netty 源码深入剖析 - EventExecutor](netty/docs/netty-source-analysis-event-executor.md)
  - [x] [Netty 源码深入剖析 - EventExecutorChooser](netty/docs/netty-source-analysis-event-executor-chooser.md)

&nbsp;

## Dubbo

&nbsp;

## RocketMQ

https://www.alibabacloud.com/help/doc-detail/95837.htm?spm=a2c63.q38357.b99.171.47ca2b90B1HBUJ

### 1. 概念和特性

- [x] [什么是 RocketMQ](rocketmq/rocketmq-introduction.md)
- [ ] [场景](rocketmq/rocketmq-scenarios.md)
- [x] [概念(Concept)](rocketmq/concept.md)：介绍RocketMQ的基本概念模型。
- [x] [特性(Features)](rocketmq/features.md)：介绍RocketMQ实现的功能特性。 

&nbsp;


### 2. 架构设计

- [x] [架构(Architecture)](rocketmq/architecture.md)：介绍RocketMQ部署架构和技术架构。
- [ ] [Name Server](rocketmq/rocketmq-nameserver.md)
- [ ] [设计(Design)](rocketmq/design.md)：介绍RocketMQ关键机制的设计原理，主要包括消息存储、通信机制、消息过滤、负载均衡、事务消息等。

&nbsp;


### 3. 样例

- [ ] [样例(Example)](rocketmq/RocketMQ_Example.md) ：介绍RocketMQ的常见用法，包括基本样例、顺序消息样例、延时消息样例、批量消息样例、过滤消息样例、事务消息样例等。

&nbsp;

### 4. 最佳实践

- [x] [Rocket MQ 安装](rocketmq/rocketmq-install.md)
- [ ] [最佳实践（Best Practice）](rocketmq/best_practice.md)：介绍RocketMQ的最佳实践，包括生产者、消费者、Broker以及NameServer的最佳实践，客户端的配置方式以及JVM和linux的最佳参数配置。
- [ ] [消息轨迹指南(Message Trace)](rocketmq/msg_trace/user_guide.md)：介绍RocketMQ消息轨迹的使用方法。[权限管理(Auth Management)](rocketmq/acl/user_guide.md)：介绍如何快速部署和使用支持权限控制特性的RocketMQ集群。
- [x] [Dledger 快速搭建(Quick Start)](rocketmq/dledger/quick_start.md)：介绍 Dledger 的快速搭建方法。
- [x] [集群部署(Cluster Deployment)](rocketmq/dledger/deploy_guide.md)：介绍 Dledger 的集群部署方式。

&nbsp;

### 5. 运维管理

- [ ] [集群部署(Operation)](rocketmq/operation.md)：介绍单Master模式、多Master模式、多Master多slave模式等RocketMQ集群各种形式的部署方法以及运维工具mqadmin的使用方式。

&nbsp;

### 6. API Reference（待补充）

- [ ] [DefaultMQProducer API Reference](rocketmq/client/java/API_Reference_DefaultMQProducer.md)

&nbsp;

### 7. 消息类型

- [x] [顺序消息](rocketmq/rocketmq-ordered-message.md)
- [x] [事务消息](rocketmq/rocketmq-transactional-message.md)
- [x]  [定时和延时消息](rocketmq/rocketmq-scheduled-messages-and-delayed-messages.md)

&nbsp;

### 8. 高级特性

- [x] [消息重试](rocketmq/rocketmq-message-retry.md)
- [x] [消息过滤](rocketmq/rocketmq-message-filtering.md)
- [x] [topic 和 tag 应用](rocketmq/rocketmq-topic-and-tag.md)
- [x] [消费幂等](rocketmq/rocketmq-consumption-idempotence.md)
- [x] [Exactly-once 传输语义](rocketmq/rocketmq-exactly-once-delivery-semantics.md)
- [x] [消息堆积和延迟问题](rocketmq/rocketmq-message-accumulation-and-latency.md)
- [x] [Cluster 消费和 Broadcast 消费](rocketmq/rocketmq-clustering-consumption-and-broadcasting-consumption.md)

&nbsp;

### 9 面试

- [x] [RocketMQ - 面试](rocketmq/rocketmq-interview.md)

&nbsp;

## Kafka
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

&nbsp;

## 号外

- [Scala 与 Java](scala-and-java.md)
- [Scala-高级函数式编程](scala-high-level-fun-program.md)