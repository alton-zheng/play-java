# Executors newCachedThreadPool() vs newFixedThreadPool()

&nbsp;

## 1. 概览

当涉及到 [线程池](java-executor-service-tutorial.md) 实现时，Java标准库提供了大量可供选择的选项。`fixed` 和 `cached` 线程池在这些实现中非常普遍。

在本教程中，我们将了解线程池在底层是如何工作的，然后比较这些实现和它们的场景。

&nbsp;

## 2. Cached Thread Pool

让我们看看当我们调用 [*Executors.newCachedThreadPool()*](https://github.com/openjdk/jdk/blob/6bab0f539fba8fb441697846347597b4a0ade428/src/java.base/share/classes/java/util/concurrent/Executors.java#L217) 时，Java是如何创建缓存线程池的

```java
public static ExecutorService newCachedThreadPool() {
    return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, 
      new SynchronousQueue<Runnable>());
}
```

缓存的线程池正在使用 “synchronous handoff” 来对新任务进行排队。 `synchronous handoff` 的基本思想很简单，但却与直觉相悖：当且仅当另一个线程同时获取某个 item 时，可以将该 item 排队。换句话说，`SynchronousQueue`不能容纳任何任务。

假设出现了一个新任务。**如果队列中有一个空闲的线程在等待，那么任务 Producer 就会把这个任务交给这个线程。否则，由于队列总是满的，执行程序将创建一个新线程来处理该任务**。

缓存池从 $0$ 个线程开始，并可能增长到 `Integer.MAX_VALUE` 线程。实际上，`cached` 线程池的唯一限制是可用的系统资源。

为了更好地管理系统资源，cached 的线程池将删除保持空闲一分钟的线程。

&nbsp;

### 2.1. 使用场景

cached 的线程池配置在短时间内缓存线程(因此得名)，以便在其他任务中重用它们。因此，当我们处理合理数量的短期任务时，它的工作效果最好。

这里的关键是 `reasonable`(合理) 和 `short-lived`(“短暂”)。为了澄清这一点，让我们评估一个 cached pool 并不适合的场景。在这里，我们将提交 $100$ 万个任务，每个任务需要 $100$ 微秒来完成:

```scala
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

&nbsp;

这将创建大量线程，导致不合理的内存使用，甚至更糟糕的是，大量 CPU 上下文切换。这两种反常现象都将严重损害整体性能。

因此，当执行时间不可预测时，比如 io 绑定的任务，我们应该避免使用这种线程池。

&nbsp;

## 3. Fixed Thread Pool

让我们看看 [fixed thread](https://github.com/openjdk/jdk/blob/6bab0f539fba8fb441697846347597b4a0ade428/src/java.base/share/classes/java/util/concurrent/Executors.java#L91) 池是如何在底层工作的:

```java
public static ExecutorService newFixedThreadPool(int nThreads) {
    return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, 
      new LinkedBlockingQueue<Runnable>());
}
```

与 cached 的线程池相反，这个线程池使用一个 unbounded 任务队列，包含有固定数量的永不过期的线程**。因此，固定线程池将尝试使用固定数量的线程** 来执行传入任务，而不是不断增加的线程数量。当所有线程都处于繁忙状态时，执行程序将把新任务放入任务队列。通过这种方式，我们可以更好地控制程序的资源消耗。

因此，固定的线程池更适合执行时间不可预测的任务。

&nbsp;

## 4. 不幸的相似之处

到目前为止，我们只列举了 cached 线程池和 `fixed` 线程池之间的区别。

所有这些差异之外, 它们都是使用 *[AbortPolicy](https://www.baeldung.com/java-rejectedexecutionhandler#1-abort-policy)* 作为它们 [饱和 policy](https://www.baeldung.com/java-rejectedexecutionhandler)。因此, 我们期望这些 executor 在无法接受甚至无法将更多任务放入队列中时，抛出异常。

让我们看看在现实世界中会发生什么。

`cached` 的线程池将在极端情况下继续创建越来越多的线程，因此，实际上，它们永远不会达到饱和点。类似地，`fixed` 的线程池将继续在其任务队列中添加越来越多的任务。因此，固定池也永远不会达到饱和点。

&nbsp;

由于两个池都不会饱和，当负载异常高时，它们将消耗大量内存来创建线程或排队任务。更糟糕的是， cached 的线程池 还会导致大量的处理器上下文切换。

无论如何，为了**对资源消耗有更多的控制，强烈建议创建一个自定义** ***[ThreadPoolExecutor](https://docs.oracle.com/javase/9/docs/api/java/util/concurrent/ThreadPoolExecutor.html)***:

```java
var boundedQueue = new ArrayBlockingQueue<Runnable>(1000);
new ThreadPoolExecutor(10, 20, 60, SECONDS, boundedQueue, new AbortPolicy());
```

在这里，我们的线程池最多可以有 20 个线程，并且最多只能排队 1000 个任务。此外，当它不能接受更多的负载时，它将简单地抛出一个异常。

&nbsp;

## 5. 总结

在本教程中，我们浏览了一下 JDK 的源代码，看看不同的 executor 是如何工作的。然后，我们比较了 fixed 和 `cached` 线程池及其使用场景。

最后，我们尝试使用自定义线程池解决这些池的失控资源消耗问题。

