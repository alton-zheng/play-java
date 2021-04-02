# A Guide to the Java ExecutorService

Last modified: February 12, 2021

by [baeldung](https://www.baeldung.com/author/baeldung/)



- [Java](https://www.baeldung.com/category/java/)**+**

- [Java Concurrency](https://www.baeldung.com/tag/java-concurrency/)

### **Get started with Spring 5 and Spring Boot 2, through the \*Learn Spring\* course:**

**[>> CHECK OUT THE COURSE](https://www.baeldung.com/ls-course-start)**

## **1. Overview**

*[ExecutorService](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html)* is a JDK API that simplifies running tasks in asynchronous mode. Generally speaking, *ExecutorService* automatically provides a pool of threads and an API for assigning tasks to it.

## Further reading:

## [Guide to the Fork/Join Framework in Java](https://www.baeldung.com/java-fork-join)

An intro to the fork/join framework presented in Java 7 and the tools to help speed up parallel processing by attempting to use all available processor cores.

[Read more](https://www.baeldung.com/java-fork-join) →

## [Overview of the java.util.concurrent](https://www.baeldung.com/java-util-concurrent)

Discover the content of the java.util.concurrent package.

[Read more](https://www.baeldung.com/java-util-concurrent) →

## [Guide to java.util.concurrent.Locks](https://www.baeldung.com/java-concurrent-locks)

In this article, we explore various implementations of the Lock interface and the newly introduced in Java 9 StampedLock class.

[Read more](https://www.baeldung.com/java-concurrent-locks) →



## **2. Instantiating \*ExecutorService\***

### **2.1. Factory Methods of the \*Executors\* Class**

The easiest way to create *ExecutorService* is to use one of the factory methods of the *Executors* class.

For example, the following line of code will create a thread pool with 10 threads:

```java
ExecutorService executor = Executors.newFixedThreadPool(10);
```

There are several other factory methods to create a predefined *ExecutorService* that meets specific use cases. To find the best method for your needs, consult [Oracle's official documentation](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/Executors.html).

### **2.2. Directly Create an \*ExecutorService\***

Because *ExecutorService* is an interface, an instance of any its implementations can be used. There are several implementations to choose from in the *[java.util.concurrent](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executors.html)* package, or you can create your own.

For example, the *ThreadPoolExecutor* class has a few constructors that we can use to configure an executor service and its internal pool:

```java
ExecutorService executorService = 
  new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,   
  new LinkedBlockingQueue<Runnable>());
```

You may notice that the code above is very similar to the [source code](https://github.com/openjdk-mirror/jdk7u-jdk/blob/master/src/share/classes/java/util/concurrent/Executors.java#L133) of the factory method *newSingleThreadExecutor().* For most cases, a detailed manual configuration isn't necessary.

## **3. Assigning Tasks to the \*ExecutorService\***

*ExecutorService* can execute *Runnable* and *Callable* tasks. To keep things simple in this article, two primitive tasks will be used. Notice that we use lambda expressions here instead of anonymous inner classes:

```java
Runnable runnableTask = () -> {
    try {
        TimeUnit.MILLISECONDS.sleep(300);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
};

Callable<String> callableTask = () -> {
    TimeUnit.MILLISECONDS.sleep(300);
    return "Task's execution";
};

List<Callable<String>> callableTasks = new ArrayList<>();
callableTasks.add(callableTask);
callableTasks.add(callableTask);
callableTasks.add(callableTask);
```

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_0" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_0" width="300" height="250" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="c" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

We can assign tasks to the *ExecutorService* using several methods including *execute()*, which is inherited from the *Executor* interface, and also *submit()*, *invokeAny()* and *invokeAll()*.

The ***execute()\*** method is *void* and doesn't give any possibility to get the result of a task's execution or to check the task's status (is it running):

```java
executorService.execute(runnableTask);
```

***submit()\*** submits a *Callable* or a *Runnable* task to an *ExecutorService* and returns a result of type *Future*:

```java
Future<String> future = 
  executorService.submit(callableTask);
```

***invokeAny()\*** assigns a collection of tasks to an *ExecutorService*, causing each to run, and returns the result of a successful execution of one task (if there was a successful execution):

```java
String result = executorService.invokeAny(callableTasks);
```

***invokeAll()*** assigns a collection of tasks to an *ExecutorService*, causing each to run, and returns the result of all task executions in the form of a list of objects of type *Future*:

```java
List<Future<String>> futures = executorService.invokeAll(callableTasks);
```

Before going further, we need to discuss two more items: shutting down an *ExecutorService* and dealing with *Future* return types.

## **4. Shutting Down an \*ExecutorService\***

In general, the *ExecutorService* will not be automatically destroyed when there is no task to process. It will stay alive and wait for new work to do.

In some cases this is very helpful, such as when an app needs to process tasks that appear on an irregular basis or the task quantity is not known at compile time.

On the other hand, an app could reach its end but not be stopped because a waiting *ExecutorService* will cause the JVM to keep running.

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_1" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_1" width="336" height="280" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" sandbox="allow-forms allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts allow-top-navigation-by-user-activation" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="d" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

To properly shut down an *ExecutorService*, we have the *shutdown()* and *shutdownNow()* APIs.

The ***shutdown()*** method doesn't cause immediate destruction of the *ExecutorService*. It will make the *ExecutorService* stop accepting new tasks and shut down after all running threads finish their current work:

```java
executorService.shutdown();
```

The ***shutdownNow()\*** method tries to destroy the *ExecutorService* immediately, but it doesn't guarantee that all the running threads will be stopped at the same time:

```java
List<Runnable> notExecutedTasks = executorService.shutDownNow();
```

This method returns a list of tasks that are waiting to be processed. It is up to the developer to decide what to do with these tasks.

One good way to shut down the *ExecutorService* (which is also [recommended by Oracle](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ExecutorService.html)) is to use both of these methods combined with the ***awaitTermination()\*** method:

```java
executorService.shutdown();
try {
    if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
        executorService.shutdownNow();
    } 
} catch (InterruptedException e) {
    executorService.shutdownNow();
}
```

With this approach, the *ExecutorService* will first stop taking new tasks and then wait up to a specified period of time for all tasks to be completed. If that time expires, the execution is stopped immediately.

## **5. The \*Future\* Interface**

The *submit()* and *invokeAll()* methods return an object or a collection of objects of type *Future*, which allows us to get the result of a task's execution or to check the task's status (is it running).

The *Future* interface provides a special blocking method *get()*, which returns an actual result of the *Callable* task's execution or *null* in the case of a *Runnable* task:

```java
Future<String> future = executorService.submit(callableTask);
String result = null;
try {
    result = future.get();
} catch (InterruptedException | ExecutionException e) {
    e.printStackTrace();
}
```

Calling the *get()* method while the task is still running will cause execution to block until the task properly executes and the result is available.

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_2" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_2" width="300" height="250" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="e" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

With very long blocking caused by the *get()* method, an application's performance can degrade. If the resulting data is not crucial, it is possible to avoid such a problem by using timeouts:

```java
String result = future.get(200, TimeUnit.MILLISECONDS);
```

If the execution period is longer than specified (in this case, 200 milliseconds), a *TimeoutException* will be thrown.

We can use the *isDone()* method to check if the assigned task already processed or not.

The *Future* interface also provides for canceling task execution with the *cancel()* method and checking the cancellation with the *isCancelled()* method:

```java
boolean canceled = future.cancel(true);
boolean isCancelled = future.isCancelled();
```

## **6. The \*ScheduledExecutorService\* Interface**

The *ScheduledExecutorService* runs tasks after some predefined delay and/or periodically.

Once again, the best way to instantiate a *ScheduledExecutorService* is to use the factory methods of the *Executors* class.

For this section, we use a *ScheduledExecutorService* with one thread:

```java
ScheduledExecutorService executorService = Executors
  .newSingleThreadScheduledExecutor();
```

To schedule a single task's execution after a fixed delay, use the *scheduled()* method of the *ScheduledExecutorService*.

Two *scheduled()* methods allow you to execute *Runnable* or *Callable* tasks:

```java
Future<String> resultFuture = 
  executorService.schedule(callableTask, 1, TimeUnit.SECONDS);
```

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_3" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_3" width="300" height="250" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" sandbox="allow-forms allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts allow-top-navigation-by-user-activation" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="f" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

The *scheduleAtFixedRate()* method lets us run a task periodically after a fixed delay. The code above delays for one second before executing *callableTask*.

The following block of code will run a task after an initial delay of 100 milliseconds. And after that, it will run the same task every 450 milliseconds:

```java
Future<String> resultFuture = service
  .scheduleAtFixedRate(runnableTask, 100, 450, TimeUnit.MILLISECONDS);
```

If the processor needs more time to run an assigned task than the *period* parameter of the *scheduleAtFixedRate()* method, the *ScheduledExecutorService* will wait until the current task is completed before starting the next.

If it is necessary to have a fixed length delay between iterations of the task, *scheduleWithFixedDelay()* should be used.

For example, the following code will guarantee a 150-millisecond pause between the end of the current execution and the start of another one:

```java
service.scheduleWithFixedDelay(task, 100, 150, TimeUnit.MILLISECONDS);
```

According to the *scheduleAtFixedRate()* and *scheduleWithFixedDelay()* method contracts, period execution of the task will end at the termination of the *ExecutorService* or if an exception is thrown during task execution*.*

## **7. \*ExecutorService\* vs Fork/Join**

After the release of Java 7, many developers decided to replace the *ExecutorService* framework with the fork/join framework.

This is not always the right decision, however. Despite the simplicity and frequent performance gains associated with fork/join, it reduces developer control over concurrent execution.

*ExecutorService* gives the developer the ability to control the number of generated threads and the granularity of tasks that should be run by separate threads. The best use case for *ExecutorService* is the processing of independent tasks, such as transactions or requests according to the scheme “one thread for one task.”

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_4" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_4" width="728" height="90" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" sandbox="allow-forms allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts allow-top-navigation-by-user-activation" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="g" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

In contrast, [according to Oracle's documentation](https://docs.oracle.com/javase/tutorial/essential/concurrency/forkjoin.html), fork/join was designed to speed up work that can be broken into smaller pieces recursively.

## **8. Conclusion**

Despite the relative simplicity of *ExecutorService*, there are a few common pitfalls.

Let's summarize them:

**Keeping an unused \*ExecutorService\* alive**: See the detailed explanation in Section 4 on how to shut down an *ExecutorService*.

**Wrong thread-pool capacity while using fixed length thread pool**: It is very important to determine how many threads the application will need to run tasks efficiently. A too-large thread pool will cause unnecessary overhead just to create threads that will mostly be in the waiting mode. Too few can make an application seem unresponsive because of long waiting periods for tasks in the queue.

**Calling a \*Future\*‘s \*get()\* method after task cancellation**: Attempting to get the result of an already canceled task triggers a *CancellationException*.

**Unexpectedly long blocking with \*Future\*‘s \*get()\* method**: We should use timeouts to avoid unexpected waits.

As always, the code for this article is available in [the GitHub repository](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-concurrency-basic).