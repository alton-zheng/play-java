# ExecutorService – Waiting for Threads to Finish

Last modified: May 7, 2019

by [baeldung](https://www.baeldung.com/author/baeldung/)



- [Java](https://www.baeldung.com/category/java/)**+**

- [Java Concurrency](https://www.baeldung.com/tag/java-concurrency/)

### **Get started with Spring 5 and Spring Boot 2, through the \*Learn Spring\* course:**

**[>> CHECK OUT THE COURSE](https://www.baeldung.com/ls-course-start)**

## **1. Overview**

The [*ExecutorService*](https://www.baeldung.com/java-executor-service-tutorial) framework makes it easy to process tasks in multiple threads. We're going to exemplify some scenarios in which we wait for threads to finish their execution.

Also, we'll show how to gracefully shutdown an *ExecutorService* and wait for already running threads to finish their execution.

## **2. After \*Executor's\* Shutdown**

When using an *Executor,* we can shut it down by calling the *shutdown()* or *shutdownNow()* methods. **Although, it won't wait until all threads stop executing.**

**Waiting for existing threads to complete their execution can be achieved by using the \*awaitTermination()\* method.**

This blocks the thread until all tasks complete their execution or the specified timeout is reached:

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

<iframe frameborder="0" src="https://695f6c3c4f87c0215d78782663f0b71e.safeframe.googlesyndication.com/safeframe/1-0-38/html/container.html" id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_0" title="3rd party ad content" name="" scrolling="no" marginwidth="0" marginheight="0" width="970" height="90" data-is-safeframe="true" sandbox="allow-forms allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts allow-top-navigation-by-user-activation" allow="conversion-measurement ‘src’" data-google-container-id="8" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

Also, we must note that the order of the returned *Future* objects is the same as the list of the provided *Callable* objects:

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

The source code for the article is available [over on GitHub](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-concurrency-basic).