# ExecutorService – 等待线程结束

## 1. 概览

[*ExecutorService*](java-executor-service-tutorial.md) 框架使得在多个线程中处理任务变得很容易。我们将举例说明一些等待线程完成执行的场景。

此外，我们还将展示如何优雅地关闭一个 *ExecutorService* 并等待已经运行的线程完成它们的执行。

&nbsp;

## 2. Executor 执行完关闭

当使用 *Executor* 时，可以通过调用 *shutdown()* 或 *shutdownNow()* 方法来关闭它。虽然，它不会等到所有线程停止执行

可以通过使用 *awaitTerminate()* 方法来实现等待现有线程完成它们的执行

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

&nbsp;

## 3. 使用 CountDownLatch

接下来，让我们看看解决这个问题的另一种方法 - 使用倒计时闩锁来表示任务的完成。

我们可以用一个值对它进行初始化，该值表示在通知所有调用了 *await()* 方法的线程之前，它可以被减除的次数。

例如，如果我们需要当前线程等待另外N 个线程完成它们的执行，我们可以使用 $N$ 初始化锁存器:

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

&nbsp;

## 4.  invokeAll()

我们可以用来运行线程的第一种方法是 *invokeAll()* 方法。在所有任务完成或超时结束后，该方法返回一个 *Future* 对象的列表。

同样，我们必须注意，返回的 *Future* 对象的顺序与提供的 *Callable* 对象的列表顺序相同：

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

&nbsp;

## 5. 使用 ExecutorCompletionService

运行多个线程的另一种方法是使用 *ExecutorCompletionService* 。它使用提供的 *ExecutorService* 来执行任务。

与 *invokeAll()* 一个区别是表示已执行任务的 *Future* 的返回顺序。**ExecutorCompletionService** 以他们完成的顺序使用队列来存储结果，而 *invokeAll()* 返回一个列表，具有与迭代器生成的给定任务列表相同的顺序：

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

&nbsp;

可以使用 *take()* 方法访问结果:

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

&nbsp;

## 6. 总结

根据场景，我们有各种选项来等待线程完成它们的执行。

当我们需要一种机制来通知一个或多个线程其他线程执行的一组操作已经完成时，CountDownLatch是很有用的

ExecutorCompletionService 在我们需要尽快访问任务结果时是有用的，当我们想要等待所有正在运行的任务完成时其他方法是有用的