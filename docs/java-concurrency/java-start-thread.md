# How to Start a Thread in Java

Last modified: August 6, 2020

by [baeldung](https://www.baeldung.com/author/baeldung/)



- [Java](https://www.baeldung.com/category/java/)**+**

- [Java Concurrency](https://www.baeldung.com/tag/java-concurrency/)

### **Get started with Spring 5 and Spring Boot 2, through the \*Learn Spring\* course:**

**[>> CHECK OUT THE COURSE](https://www.baeldung.com/ls-course-start)**

If you have a few years of experience in the Java ecosystem, and you're interested in sharing that experience with the community (and getting paid for your work of course), have a look at the ["Write for Us" page](https://www.baeldung.com/contribution-guidelines). Cheers, Eugen

## 1. Introduction

In this tutorial, we're going to explore different ways to start a thread and execute parallel tasks.

**This is very useful, in particular when dealing with long or recurring operations that can't run on the main thread**, or where the UI interaction can't be put on hold while waiting for the operation's results.

To learn more about the details of threads, definitely read our tutorial about the [Life Cycle of a Thread in Java.](https://www.baeldung.com/java-thread-lifecycle)

## 2. The Basics of Running a Thread

We can easily write some logic that runs in a parallel thread by using the *Thread* framework.

Let's try a basic example, by extending the *Thread* class:

```java
public class NewThread extends Thread {
    public void run() {
        long startTime = System.currentTimeMillis();
        int i = 0;
        while (true) {
            System.out.println(this.getName() + ": New Thread is running..." + i++);
            try {
                //Wait for one sec so it doesn't print too fast
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ...
        }
    }
}
```

And now we write a second class to initialize and start our thread:

```java
public class SingleThreadExample {
    public static void main(String[] args) {
        NewThread t = new NewThread();
        t.start();
    }
}
```

We should call the *start()* method on threads in the *[NEW](https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.State.html#NEW)* state (the equivalent of not started). Otherwise, Java will throw an instance of [*IllegalThreadStateException*](https://docs.oracle.com/javase/8/docs/api/java/lang/IllegalThreadStateException.html) exception.

Now let's assume we need to start multiple threads:

```java
public class MultipleThreadsExample {
    public static void main(String[] args) {
        NewThread t1 = new NewThread();
        t1.setName("MyThread-1");
        NewThread t2 = new NewThread();
        t2.setName("MyThread-2");
        t1.start();
        t2.start();
    }
}
```

Our code still looks quite simple and very similar to the examples we can find online.

Of course, **this is far from production-ready code, where it's of critical importance to manage resources in the correct way, to avoid too much context switching or too much memory usage.**

**So, to get production-ready we now need to write additional boilerplate** to deal with:

- the consistent creation of new threads
- the number of concurrent live threads
- the threads deallocation: very important for daemon threads in order to avoid leaks

If we want to, we can write our own code for all these case scenarios and even some more, but why should we reinvent the wheel?

## 3. The *ExecutorService* Framework

The *ExecutorService* implements the Thread Pool design pattern (also called a replicated worker or worker-crew model) and takes care of the thread management we mentioned above, plus it adds some very useful features like thread reusability and task queues.

**Thread reusability, in particular, is very important: in a large-scale application, allocating and deallocating many thread objects creates a significant memory management overhead.**

**With worker threads, we minimize the overhead caused by thread creation.**

To ease the pool configuration, *ExecutorService* comes with an easy constructor and some customization options, such as the type of queue, the minimum and the maximum number of threads and their naming convention.

For more details about the *ExecutorService,* please read our [Guide to the Java ExecutorService](https://www.baeldung.com/java-executor-service-tutorial).

## 4. Starting a Task with Executors

**Thanks to this powerful framework, we can switch our mindset from starting threads to submitting tasks.**

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_1" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_1" width="728" height="90" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="7" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![Freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

Let's look at how we can submit an asynchronous task to our executor:

```java
ExecutorService executor = Executors.newFixedThreadPool(10);
...
executor.submit(() -> {
    new Task();
});
```

There are two methods we can use: *execute*, which returns nothing, and *submit*, which returns a *Future* encapsulating the computation's result.

For more information about *Futures,* please read our [Guide to java.util.concurrent.Future](https://www.baeldung.com/java-future).

## 5. Starting a Task with *CompletableFutures*

To retrieve the final result from a *Future* object we can use the *get* method available in the object, but this would block the parent thread until the end of the computation.

Alternatively, we could avoid the block by adding more logic to our task, but we have to increase the complexity of our code.

**Java 1.8 introduced a new framework on top of the \*Future\* construct to better work with the computation's result: the \*CompletableFuture\*.**

***CompletableFuture\* implements \*CompletableStage\*, which adds a vast selection of methods to attach callbacks and avoid all the plumbing needed to run operations on the result after it's ready.**

The implementation to submit a task is a lot simpler:

```java
CompletableFuture.supplyAsync(() -> "Hello");
```

*supplyAsync* takes a *Supplier* containing the code we want to execute asynchronously — in our case the lambda parameter.

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_2" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_2" width="300" height="250" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" sandbox="allow-forms allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts allow-top-navigation-by-user-activation" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="6" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![Freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

**The task is now implicitly submitted to the \*ForkJoinPool.commonPool()\*, or we can specify the \*Executor\* we prefer as a second parameter.**

To know more about *CompletableFuture,* please read our [Guide To CompletableFuture](https://www.baeldung.com/java-completablefuture).

## 6. Running Delayed or Periodic Tasks

**When working with complex web applications, we may need to run tasks at specific times, maybe regularly.**

Java has few tools that can help us to run delayed or recurring operations:

- *java.util.Timer*
- *java.util.concurrent.ScheduledThreadPoolExecutor*

### 6.1. *Timer*

*Timer* is a facility to schedule tasks for future execution in a background thread.

Tasks may be scheduled for one-time execution, or for repeated execution at regular intervals.

Let's see what the code looks if we want to run a task after one second of delay:

```java
TimerTask task = new TimerTask() {
    public void run() {
        System.out.println("Task performed on: " + new Date() + "n" 
          + "Thread's name: " + Thread.currentThread().getName());
    }
};
Timer timer = new Timer("Timer");
long delay = 1000L;
timer.schedule(task, delay);
```

Now let's add a recurring schedule:

```java
timer.scheduleAtFixedRate(repeatedTask, delay, period);
```

This time, the task will run after the delay specified and it'll be recurrent after the period of time passed.

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_3" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_3" width="728" height="90" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="8" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![Freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

For more information, please read our guide to [Java Timer](https://www.baeldung.com/java-timer-and-timertask).

### 6.2. *ScheduledThreadPoolExecutor*

*ScheduledThreadPoolExecutor* has methods similar to the *Timer* class:

```java
ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
ScheduledFuture<Object> resultFuture
  = executorService.schedule(callableTask, 1, TimeUnit.SECONDS);
```

To end our example, we use *scheduleAtFixedRate()* for recurring tasks:

```java
ScheduledFuture<Object> resultFuture
 = executorService.scheduleAtFixedRate(runnableTask, 100, 450, TimeUnit.MILLISECONDS);
```

The code above will execute a task after an initial delay of 100 milliseconds, and after that, it'll execute the same task every 450 milliseconds.

**If the processor can't finish processing the task in time before the next occurrence, the \*ScheduledExecutorService\* will wait until the current task is completed, before starting the next.**

To avoid this waiting time, we can use *scheduleWithFixedDelay()*, which, as described by its name, guarantees a fixed length delay between iterations of the task.

For more details about *ScheduledExecutorService,* please read our [Guide to the Java ExecutorService](https://www.baeldung.com/java-executor-service-tutorial).

### 6.3. Which Tool Is Better?

If we run the examples above, the computation's result looks the same.

So, **how do we choose the right tool**?

**When a framework offers multiple choices, it's important to understand the underlying technology to make an informed decision.**

Let's try to dive a bit deeper under the hood.

***Timer\*:**

- does not offer real-time guarantees: it schedules tasks using the *Object.wait(long)* method
- there's a single background thread, so tasks run sequentially and a long-running task can delay others
- runtime exceptions thrown in a *TimerTask* would kill the only thread available, thus killing *Timer*

***ScheduledThreadPoolExecutor\*:**

- can be configured with any number of threads
- can take advantage of all available CPU cores
- catches runtime exceptions and lets us handle them if we want to (by overriding *afterExecute* method from *ThreadPoolExecutor*)
- cancels the task that threw the exception, while letting others continue to run
- relies on the OS scheduling system to keep track of time zones, delays, solar time, etc.
- provides collaborative API if we need coordination between multiple tasks, like waiting for the completion of all tasks submitted
- provides better API for management of the thread life cycle

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_4" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_4" width="300" height="250" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" sandbox="allow-forms allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts allow-top-navigation-by-user-activation" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="9" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![Freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

The choice now is obvious, right?

## 7. Difference Between *Future* and *ScheduledFuture*

**In our code examples, we can observe that \*ScheduledThreadPoolExecutor\* returns a specific type of \*Future\*: \*ScheduledFuture\*.**

*ScheduledFuture* extends both *Future* and *Delayed* interfaces, thus inheriting the additional method *getDelay* that returns the remaining delay associated with the current task. It's extended by *RunnableScheduledFuture* that adds a method to check if the task is periodic.

***ScheduledThreadPoolExecutor\* implements all these constructs through the inner class \*ScheduledFutureTask\* and uses them to control the task life cycle.**

## 8. Conclusions

In this tutorial, we experimented with the different frameworks available to start threads and run tasks in parallel.

Then, we went deeper into the differences between *Timer* and *ScheduledThreadPoolExecutor.*

The source code for the article is available [over on GitHub](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-concurrency-basic).