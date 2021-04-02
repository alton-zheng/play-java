# Executors newCachedThreadPool() vs newFixedThreadPool()

Last modified: March 6, 2020

by [Ali Dehghani](https://www.baeldung.com/author/ali-dehghani/)



- [Java](https://www.baeldung.com/category/java/)**+**

- [Java Concurrency](https://www.baeldung.com/tag/java-concurrency/)

### **Get started with Spring 5 and Spring Boot 2, through the \*Learn Spring\* course:**

**[>> CHECK OUT THE COURSE](https://www.baeldung.com/ls-course-start)**

## 1. Overview

When it comes to [thread pool](https://www.baeldung.com/java-executor-service-tutorial) implementations, the Java standard library provides plenty of options to choose from. The fixed and cached thread pools are pretty ubiquitous among those implementations.

In this tutorial, we're going to see how thread pools are working under the hood and then compare these implementations and their use-cases.

## 2. Cached Thread Pool

Let's take a look at how Java creates a cached thread pool when we call [*Executors.newCachedThreadPool()*](https://github.com/openjdk/jdk/blob/6bab0f539fba8fb441697846347597b4a0ade428/src/java.base/share/classes/java/util/concurrent/Executors.java#L217):

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

### 2.1. Use Cases

The cached thread pool configuration caches the threads (hence the name) for a short amount of time to reuse them for other tasks. **As a result, it works best when we're dealing with a reasonable number of short-lived tasks.** 

The key here is “reasonable” and “short-lived”. To clarify this point, let's evaluate a scenario where cached pools aren't a good fit. Here we're going to submit one million tasks each taking 100 micro-seconds to finish:

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

<iframe frameborder="0" src="https://a671f9872a0470ec7b6d2be2e166550a.safeframe.googlesyndication.com/safeframe/1-0-38/html/container.html" id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_0" title="3rd party ad content" name="" scrolling="no" marginwidth="0" marginheight="0" width="300" height="250" data-is-safeframe="true" sandbox="allow-forms allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts allow-top-navigation-by-user-activation" allow="conversion-measurement ‘src’" data-google-container-id="9" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

This is going to create a lot of threads that translate to unreasonable memory usage, and even worse, lots of CPU context switches. Both of these anomalies would hurt the overall performance significantly.

**Therefore, we should avoid this thread pool when the execution time is unpredictable, like IO-bound tasks.**

## 3. Fixed Thread Pool

Let's see how [fixed thread](https://github.com/openjdk/jdk/blob/6bab0f539fba8fb441697846347597b4a0ade428/src/java.base/share/classes/java/util/concurrent/Executors.java#L91) pools work under the hood:

```java
public static ExecutorService newFixedThreadPool(int nThreads) {
    return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, 
      new LinkedBlockingQueue<Runnable>());
}
```

As opposed to the cached thread pool, this one is using an unbounded queue with a fixed number of never-expiring threads**. Therefore, instead of an ever-increasing number of threads, the fixed thread pool tries to execute incoming tasks with a fixed amount of threads**. When all threads are busy, then the executor will queue new tasks.  This way, we have more control over our program's resource consumption.

As a result, fixed thread pools are better suited for tasks with unpredictable execution times.

## 4. Unfortunate Similarities

So far, we've only enumerated the differences between cached and fixed thread pools.

All those differences aside, they're both use *[AbortPolicy](https://www.baeldung.com/java-rejectedexecutionhandler#1-abort-policy)* as their [saturation policy](https://www.baeldung.com/java-rejectedexecutionhandler)*.* Therefore, we expect these executors to throw an exception when they can't accept and even queue any more tasks.

Let's see what happens in the real world.

Cached thread pools will continue to create more and more threads in extreme circumstances, so, practically, **they will never reach a saturation point**. Similarly, fixed thread pools will continue to add more and more tasks in their queue. **Therefore, the fixed pools also will never reach a saturation point**.

<iframe frameborder="0" src="https://a671f9872a0470ec7b6d2be2e166550a.safeframe.googlesyndication.com/safeframe/1-0-38/html/container.html" id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_1" title="3rd party ad content" name="" scrolling="no" marginwidth="0" marginheight="0" width="970" height="90" data-is-safeframe="true" sandbox="allow-forms allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts allow-top-navigation-by-user-activation" allow="conversion-measurement ‘src’" data-google-container-id="a" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

**As both pools won't be saturated, when the load is exceptionally high, they will consume a lot of memory for creating threads or queuing tasks. Adding insult to the injury, cached thread pools will also incur a lot of processor context switches.**

Anyway, to **have more control over resource consumption, it's highly recommended to create a custom** ***[ThreadPoolExecutor](https://docs.oracle.com/javase/9/docs/api/java/util/concurrent/ThreadPoolExecutor.html)***:

```java
var boundedQueue = new ArrayBlockingQueue<Runnable>(1000);
new ThreadPoolExecutor(10, 20, 60, SECONDS, boundedQueue, new AbortPolicy());
```

Here, our thread pool can have up to 20 threads and can only queue up to 1000 tasks. Also, when it can't accept any more load, it will simply throw an exception.

## 5. Conclusion

In this tutorial, we had a peek into the JDK source code to see how different *Executors* work under the hood. Then, we compared the fixed and cached thread pools and their use-cases.

In the end, we tried to address the out-of-control resource consumption of those pools with custom thread pools.