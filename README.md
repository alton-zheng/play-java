# Play Java

- 语言知识总结 - 非项目

&nbsp;

## 网络 和 IO

- [面试题](io/docs/io-interview.md)

&nbsp;

## 操作系统

&nbsp;

## 网络原理



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

&nbsp;

## Netty

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


