# Executors newCachedThreadPool() vs newFixedThreadPool()

## 1. Overview

When it comes to [thread pool](https://www.baeldung.com/java-executor-service-tutorial) implementations, the Java standard library provides plenty of options to choose from. The fixed and cached thread pools are pretty ubiquitous among those implementations.

In this tutorial, we're going to see how thread pools are working under the hood and then compare these implementations and their use-cases.

当涉及到[线程池](https://www.baeldung.com/java-executor-service-tutorial)实现时，Java标准库提供了大量可供选择的选项。修复的和缓存的线程池在这些实现中非常普遍。

在本教程中，我们将了解线程池在底层是如何工作的，然后比较这些实现和它们的用例。

## 2. Cached Thread Pool

Let's take a look at how Java creates a cached thread pool when we call [*Executors.newCachedThreadPool()*](https://github.com/openjdk/jdk/blob/6bab0f539fba8fb441697846347597b4a0ade428/src/java.base/share/classes/java/util/concurrent/Executors.java#L217):

让我们看看当我们调用[*Executors.newCachedThreadPool()*](https://github.com/openjdk/jdk/blob/6bab0f539fba8fb441697846347597b4a0ade428/src/java.base/share/classes/java/util/concurrent/Executors.java#L217):)时，Java是如何创建缓存线程池的

```java
public static ExecutorService newCachedThreadPool() {
    return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, 
      new SynchronousQueue<Runnable>());
}
```

Cached thread pools are using “synchronous handoff” to queue new tasks. The basic idea of synchronous handoff is simple and yet counter-intuitive: One can queue an item if and only if another thread takes that item at the same time. In other words, **the \*SynchronousQueue\* can not hold any tasks whatsoever.**

Suppose a new task comes in. **If there is an idle thread waiting on the queue, then the task producer hands off the task to that thread. Otherwise, since the queue is always full, the executor creates a new thread to handle that task**.

The cached pool starts with zero threads and can potentially grow to have *Integer.MAX_VALUE* threads. Practically, the only limitation for a cached thread pool is the available system resources.

To better manage system resources, cached thread pools will remove threads that remain idle for one minute.

缓存的线程池正在使用“同步切换”来对新任务进行排队。同步切换的基本思想很简单，但却与直觉相悖:当且仅当另一个线程同时获取某个项时，可以将该项排队。换句话说，** *SynchronousQueue **不能容纳任何任务

假设出现了一个新任务。**如果队列中有一个空闲的线程在等待，那么任务生产者就会把这个任务交给这个线程。否则，由于队列总是满的，执行程序将创建一个新线程来处理该任务**。

缓存池从0个线程开始，并可能增长到*整数。MAX_VALUE *线程。实际上，缓存线程池的唯一限制是可用的系统资源。

为了更好地管理系统资源，缓存的线程池将删除保持空闲一分钟的线程。

### 2.1. Use Cases

The cached thread pool configuration caches the threads (hence the name) for a short amount of time to reuse them for other tasks. **As a result, it works best when we're dealing with a reasonable number of short-lived tasks.** 

The key here is “reasonable” and “short-lived”. To clarify this point, let's evaluate a scenario where cached pools aren't a good fit. Here we're going to submit one million tasks each taking 100 micro-seconds to finish:

缓存的线程池配置在短时间内缓存线程(因此得名)，以便在其他任务中重用它们。因此，当我们处理合理数量的短期任务时，它的工作效果最好

这里的关键是“合理”和“短暂”。为了澄清这一点，让我们评估一个缓存池并不适合的场景。在这里，我们将提交100万个任务，每个任务需要100微秒来完成:

```java
Callable<String> task = () -> {
    long oneHundredMicroSeconds = 100_000;
    long startedAt = System.nanoTime();
    while (System.nanoTime() - startedAt <= oneHundredMicroSeconds);

    return "Done";
};

var cachedPool = Executors.newCachedThreadPool();
var tasks = IntStream.rangeClosed(1, 1_000_000).mapToObj(i -> task).collect(toList());
var result = cachedPool.invokeAll(tasks);
```



This is going to create a lot of threads that translate to unreasonable memory usage, and even worse, lots of CPU context switches. Both of these anomalies would hurt the overall performance significantly.

**Therefore, we should avoid this thread pool when the execution time is unpredictable, like IO-bound tasks.**

这将创建大量线程，导致不合理的内存使用，甚至更糟糕的是，大量CPU上下文切换。这两种反常现象都将严重损害整体业绩。

**因此，当执行时间不可预测时，比如io绑定的任务，我们应该避免使用这个线程池。

## 3. Fixed Thread Pool

Let's see how [fixed thread](https://github.com/openjdk/jdk/blob/6bab0f539fba8fb441697846347597b4a0ade428/src/java.base/share/classes/java/util/concurrent/Executors.java#L91) pools work under the hood:

让我们看看[fixed thread](https://github.com/openjdk/jdk/blob/6bab0f539fba8fb441697846347597b4a0ade428/src/java.base/share/classes/java/util/concurrent/Executors.java#L91)池是如何在底层工作的:

```java
public static ExecutorService newFixedThreadPool(int nThreads) {
    return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, 
      new LinkedBlockingQueue<Runnable>());
}
```

As opposed to the cached thread pool, this one is using an unbounded queue with a fixed number of never-expiring threads**. Therefore, instead of an ever-increasing number of threads, the fixed thread pool tries to execute incoming tasks with a fixed amount of threads**. When all threads are busy, then the executor will queue new tasks.  This way, we have more control over our program's resource consumption.

As a result, fixed thread pools are better suited for tasks with unpredictable execution times.

与缓存的线程池相反，这个线程池使用一个无界队列，队列中有固定数量的永不过期的线程**。因此，固定线程池将尝试使用固定数量的线程**来执行传入任务，而不是不断增加的线程数量。当所有线程都处于繁忙状态时，执行程序将把新任务放入队列。通过这种方式，我们可以更好地控制程序的资源消耗。

因此，固定的线程池更适合执行时间不可预测的任务。

## 4. Unfortunate Similarities不幸的相似之处

So far, we've only enumerated the differences between cached and fixed thread pools.

All those differences aside, they're both use *[AbortPolicy](https://www.baeldung.com/java-rejectedexecutionhandler#1-abort-policy)* as their [saturation policy](https://www.baeldung.com/java-rejectedexecutionhandler)*.* Therefore, we expect these executors to throw an exception when they can't accept and even queue any more tasks.

Let's see what happens in the real world.

Cached thread pools will continue to create more and more threads in extreme circumstances, so, practically, **they will never reach a saturation point**. Similarly, fixed thread pools will continue to add more and more tasks in their queue. **Therefore, the fixed pools also will never reach a saturation point**.

到目前为止，我们只列举了缓存线程池和固定线程池之间的区别。

所有这些差异之外,它们都是使用* (AbortPolicy) (https://www.baeldung.com/java-rejectedexecutionhandler) 1-abort-policy *作为他们(饱和政策)(https://www.baeldung.com/java-rejectedexecutionhandler) *。*因此,我们预计这些执行人抛出异常时不能接受,甚至任何更多的任务队列。

让我们看看在现实世界中会发生什么。

缓存的线程池将在极端情况下继续创建越来越多的线程，因此，实际上，它们永远不会达到饱和点。类似地，固定的线程池将继续在其队列中添加越来越多的任务。因此，固定池也永远不会达到饱和点。



**As both pools won't be saturated, when the load is exceptionally high, they will consume a lot of memory for creating threads or queuing tasks. Adding insult to the injury, cached thread pools will also incur a lot of processor context switches.**

Anyway, to **have more control over resource consumption, it's highly recommended to create a custom** ***[ThreadPoolExecutor](https://docs.oracle.com/javase/9/docs/api/java/util/concurrent/ThreadPoolExecutor.html)***:

**由于两个池都不会饱和，当负载异常高时，它们将消耗大量内存来创建线程或排队任务。更糟糕的是，缓存的线程池还会导致大量的处理器上下文切换

无论如何，为了**对资源消耗有更多的控制，强烈建议创建一个自定义** ***[ThreadPoolExecutor](https://docs.oracle.com/javase/9/docs/api/java/util/concurrent/ThreadPoolExecutor.html)***:

```java
var boundedQueue = new ArrayBlockingQueue<Runnable>(1000);
new ThreadPoolExecutor(10, 20, 60, SECONDS, boundedQueue, new AbortPolicy());
```

Here, our thread pool can have up to 20 threads and can only queue up to 1000 tasks. Also, when it can't accept any more load, it will simply throw an exception.

在这里，我们的线程池最多可以有20个线程，并且最多只能排队1000个任务。此外，当它不能接受更多的加载时，它将简单地抛出一个异常。

## 5. Conclusion

In this tutorial, we had a peek into the JDK source code to see how different *Executors* work under the hood. Then, we compared the fixed and cached thread pools and their use-cases.

In the end, we tried to address the out-of-control resource consumption of those pools with custom thread pools.
在本教程中，我们浏览了一下JDK的源代码，看看不同的executor是如何工作的。然后，我们比较了修复的和缓存的线程池及其用例。

最后，我们尝试使用自定义线程池解决这些池的失控资源消耗问题。