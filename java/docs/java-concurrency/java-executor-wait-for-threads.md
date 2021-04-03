# ExecutorService – Waiting for Threads to Finish

## **1. Overview**

The [*ExecutorService*](https://www.baeldung.com/java-executor-service-tutorial) framework makes it easy to process tasks in multiple threads. We're going to exemplify some scenarios in which we wait for threads to finish their execution.

Also, we'll show how to gracefully shutdown an *ExecutorService* and wait for already running threads to finish their execution.

[*ExecutorService*](https://www.baeldung.com/java-executor-service-tutorial)框架使得在多个线程中处理任务变得很容易。我们将举例说明一些等待线程完成执行的场景。

此外，我们还将展示如何优雅地关闭一个*ExecutorService*并等待已经运行的线程完成它们的执行。

## **2. After \*Executor's\* Shutdown**

When using an *Executor,* we can shut it down by calling the *shutdown()* or *shutdownNow()* methods. **Although, it won't wait until all threads stop executing.**

**Waiting for existing threads to complete their execution can be achieved by using the \*awaitTermination()\* method.**

This blocks the thread until all tasks complete their execution or the specified timeout is reached:

当使用*Executor时，*可以通过调用*shutdown()*或*shutdownNow()*方法来关闭它。虽然，它不会等到所有线程停止执行

**可以通过使用\* awaitterminate()\*方法来实现等待现有线程完成它们的执行

这会阻塞线程，直到所有的任务完成它们的执行或者达到指定的超时:

```java
public void awaitTerminationAfterShutdown(ExecutorService threadPool) {
    threadPool.shutdown();
    try {
        if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
            threadPool.shutdownNow();
        }
    } catch (InterruptedException ex) {
        threadPool.shutdownNow();
        Thread.currentThread().interrupt();
    }
}
```

## **3. Using \*CountDownLatch\***

Next, let's look at another approach to solving this problem – using a *CountDownLatch* to signal the completion of a task.

We can initialize it with a value that represents the number of times it can be decremented before all threads, that have called the *await()* method, are notified.

For example, if we need the current thread to wait for another *N* threads to finish their execution, we can initialize the latch using *N*:

接下来，让我们看看解决这个问题的另一种方法——使用倒计时闩锁来表示任务的完成。

我们可以用一个值对它进行初始化，该值表示在通知所有调用了*await()*方法的线程之前，它可以被减除的次数。

例如，如果我们需要当前线程等待另一个*N*线程完成它们的执行，我们可以使用*N*初始化锁存器:

```java
ExecutorService WORKER_THREAD_POOL 
  = Executors.newFixedThreadPool(10);
CountDownLatch latch = new CountDownLatch(2);
for (int i = 0; i < 2; i++) {
    WORKER_THREAD_POOL.submit(() -> {
        try {
            // ...
            latch.countDown();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    });
}

// wait for the latch to be decremented by the two remaining threads
latch.await();
```

## **4. Using \*invokeAll()\***

The first approach that we can use to run threads is the *invokeAll()* method. **The method returns a list of \*Future\* objects after all tasks finish or the timeout expires**.

我们可以用来运行线程的第一种方法是*invokeAll()*方法。在所有任务完成或超时结束后，该方法返回一个\*Future\*对象的列表。

<iframe frameborder="0" src="https://695f6c3c4f87c0215d78782663f0b71e.safeframe.googlesyndication.com/safeframe/1-0-38/html/container.html" id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_0" title="3rd party ad content" name="" scrolling="no" marginwidth="0" marginheight="0" width="970" height="90" data-is-safeframe="true" sandbox="allow-forms allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts allow-top-navigation-by-user-activation" allow="conversion-measurement ‘src’" data-google-container-id="8" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

Also, we must note that the order of the returned *Future* objects is the same as the list of the provided *Callable* objects:

同样，我们必须注意，返回的*Future*对象的顺序与提供的*Callable*对象的列表相同:

```java
ExecutorService WORKER_THREAD_POOL = Executors.newFixedThreadPool(10);

List<Callable<String>> callables = Arrays.asList(
  new DelayedCallable("fast thread", 100), 
  new DelayedCallable("slow thread", 3000));

long startProcessingTime = System.currentTimeMillis();
List<Future<String>> futures = WORKER_THREAD_POOL.invokeAll(callables);

awaitTerminationAfterShutdown(WORKER_THREAD_POOL);

long totalProcessingTime = System.currentTimeMillis() - startProcessingTime;
 
assertTrue(totalProcessingTime >= 3000);

String firstThreadResponse = futures.get(0).get();
 
assertTrue("fast thread".equals(firstThreadResponse));

String secondThreadResponse = futures.get(1).get();
assertTrue("slow thread".equals(secondThreadResponse));
```

## **5. Using \*ExecutorCompletionService\***

Another approach to running multiple threads is by using *ExecutorCompletionService.* It uses a supplied *ExecutorService* to execute tasks.

One difference over *invokeAll()* is the order in which the *Futures,* representing the executed tasks are returned. ***ExecutorCompletionService\* uses a queue to store the results in the order they are finished**, while *invokeAll()* returns a list having the same sequential order as produced by the iterator for the given task list:

运行多个线程的另一种方法是使用*ExecutorCompletionService。它使用提供的*ExecutorService*来执行任务。

*invokeAll()*的一个区别是表示已执行任务的*Futures，*的返回顺序。***ExecutorCompletionService\*使用队列来存储结果，以它们完成的顺序**，而*invokeAll()*返回一个列表，具有与迭代器生成的给定任务列表相同的顺序:

```java
CompletionService<String> service
  = new ExecutorCompletionService<>(WORKER_THREAD_POOL);

List<Callable<String>> callables = Arrays.asList(
  new DelayedCallable("fast thread", 100), 
  new DelayedCallable("slow thread", 3000));

for (Callable<String> callable : callables) {
    service.submit(callable);
}
```

The results can be accessed using the *take()* method:

可以使用*take()*方法访问结果:

```java
long startProcessingTime = System.currentTimeMillis();

Future<String> future = service.take();
String firstThreadResponse = future.get();
long totalProcessingTime
  = System.currentTimeMillis() - startProcessingTime;

assertTrue("First response should be from the fast thread", 
  "fast thread".equals(firstThreadResponse));
assertTrue(totalProcessingTime >= 100
  && totalProcessingTime < 1000);
LOG.debug("Thread finished after: " + totalProcessingTime
  + " milliseconds");

future = service.take();
String secondThreadResponse = future.get();
totalProcessingTime
  = System.currentTimeMillis() - startProcessingTime;

assertTrue(
  "Last response should be from the slow thread", 
  "slow thread".equals(secondThreadResponse));
assertTrue(
  totalProcessingTime >= 3000
  && totalProcessingTime < 4000);
LOG.debug("Thread finished after: " + totalProcessingTime
  + " milliseconds");

awaitTerminationAfterShutdown(WORKER_THREAD_POOL);
```

## **6. Conclusion**

Depending on the use case, we have various options to wait for threads to finish their execution.

**A \*CountDownLatch\* is useful when we need a mechanism to notify one or more threads that a set of operations performed by other threads has finished.**

***ExecutorCompletionService\* is useful when we need to access the task result as soon as possible and other approaches when we want to wait for all of the running tasks to finish.**

根据用例，我们有各种选项来等待线程完成它们的执行。

当我们需要一种机制来通知一个或多个线程其他线程执行的一组操作已经完成时，CountDownLatch是很有用的

***ExecutorCompletionService\*在我们需要尽快访问任务结果时是有用的，当我们想要等待所有正在运行的任务完成时其他方法是有用的