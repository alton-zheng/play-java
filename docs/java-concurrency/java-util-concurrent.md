# Overview of the java.util.concurrent



## **1. Overview**

The *java.util.concurrent* package provides tools for creating concurrent applications.

In this article, we will do an overview of the whole package.

* java.util。concurrent* package提供创建并发应用程序的工具。

在本文中，我们将对整个包进行概述。

## **2. Main Components**

The *java.util.concurrent* contains way too many features to discuss in a single write-up. In this article, we will mainly focus on some of the most useful utilities from this package like:

- *Executor*
- *ExecutorService*
- *ScheduledExecutorService*
- *Future*
- *CountDownLatch*
- *CyclicBarrier*
- *Semaphore*
- *ThreadFactory*
- *BlockingQueue*
- *DelayQueue*
- *Locks*
- *Phaser*

You can also find many dedicated articles to individual classes here.

* java.util。concurrent*包含太多的特性，无法在一篇文章中讨论。在本文中，我们将主要关注这个包中一些最有用的实用程序，比如:

- *执行人*
- * ExecutorService *
- * ScheduledExecutorService *
未来- * *
- * CountDownLatch *
- * CyclicBarrier *
信号- * *
ThreadFactory - * *
- * BlockingQueue *
- * DelayQueue *
* - *锁
- *移相器*

您还可以在这里找到许多专门针对各个类的文章。



### **2.1. \*Executor\***

***[Executor](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/Executor.html)\* is an interface that represents an object that executes provided tasks.**

It depends on the particular implementation (from where the invocation is initiated) if the task should be run on a new or current thread. Hence, using this interface, we can decouple the task execution flow from the actual task execution mechanism.

One point to note here is that *Executor* does not strictly require the task execution to be asynchronous. In the simplest case, an executor can invoke the submitted task instantly in the invoking thread.

We need to create an invoker to create the executor instance:

***[Executor](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/Executor.html)\*是一个接口，表示执行提供的任务的对象

它取决于特定的实现(从调用开始的地方)，任务是否应该在新的线程或当前线程上运行。因此，使用这个接口，我们可以将任务执行流与实际的任务执行机制解耦。

这里需要注意的一点是，*Executor*并不严格要求任务执行是异步的。在最简单的情况下，执行程序可以在调用线程中立即调用提交的任务。

我们需要创建一个调用器来创建executor实例:

```java
public class Invoker implements Executor {
    @Override
    public void execute(Runnable r) {
        r.run();
    }
}
```

Now, we can use this invoker to execute the task.

```java
public void execute() {
    Executor executor = new Invoker();
    executor.execute( () -> {
        // task to be performed
    });
}
```

Point to note here is that if the executor can't accept the task for execution, it will throw *[RejectedExecutionException](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/RejectedExecutionException.html)*.

这里需要注意的是，如果执行器不能接受任务执行，它将抛出*[RejectedExecutionException](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/RejectedExecutionException.html)*. exe)。

### **2.2. \*ExecutorService\***

*ExecutorService* is a complete solution for asynchronous processing. It manages an in-memory queue and schedules submitted tasks based on thread availability.

To use *ExecutorService,* we need to create one *Runnable* class.

*ExecutorService*是一个完整的异步处理解决方案。它管理内存中的队列，并根据线程可用性调度提交的任务。

要使用*ExecutorService，*我们需要创建一个*Runnable*类。

```java
public class Task implements Runnable {
    @Override
    public void run() {
        // task details
    }
}
```

Now we can create the *ExecutorService* instance and assign this task. At the time of creation, we need to specify the thread-pool size.

```java
ExecutorService executor = Executors.newFixedThreadPool(10);
```

If we want to create a single-threaded *ExecutorService* instance, we can use ***newSingleThreadExecutor(ThreadFactory threadFactory)\*** to create the instance.

Once the executor is created, we can use it to submit the task.

如果我们想创建一个单线程的*ExecutorService*实例，我们可以使用***newSingleThreadExecutor(ThreadFactory ThreadFactory)\***来创建实例。

一旦创建了executor，我们就可以使用它来提交任务。

```java
public void execute() { 
    executor.submit(new Task()); 
}
```

We can also create the *Runnable* instance while submitting the task.

我们还可以在提交任务时创建*Runnable*实例。

```java
executor.submit(() -> {
    new Task();
});
```

It also comes with two out-of-the-box execution termination methods. The first one is *shutdown()*; it waits until all the submitted tasks finish executing. The other method is *shutdownNow()* whic*h* immediately terminates all the pending/executing tasks.

There is also another method *awaitTermination(long timeout, TimeUnit unit)* which forcefully blocks until all tasks have completed execution after a shutdown event triggered or execution-timeout occurred, or the execution thread itself is interrupted,

它还提供了两个开箱即用的执行终止方法。第一个是*shutdown()*;它会一直等待，直到所有提交的任务完成执行。另一个方法是*shutdownNow()*，它*h*立即终止所有挂起/正在执行的任务。

还有另一个方法* awaitterminate (long timeout, TimeUnit unit)*强制阻塞，直到触发关机事件或发生执行超时，或执行线程本身被中断后，所有任务都完成执行。

```java
try {
    executor.awaitTermination( 20l, TimeUnit.NANOSECONDS );
} catch (InterruptedException e) {
    e.printStackTrace();
}
```

### **2.3. \*ScheduledExecutorService\*

*ScheduledExecutorService* is a similar interface to *ExecutorService,* but it can perform tasks periodically.

***Executor and ExecutorService\*‘s methods are scheduled on the spot without introducing any artificial delay.** Zero or any negative value signifies that the request needs to be executed instantly.

We can use both *Runnable* and *Callable* interface to define the task.

*ScheduledExecutorService*是一个类似于*ExecutorService的接口，*但它可以定期执行任务。

***Executor和execuorservice的方法在现场安排，不引入任何人为的延迟。** 0或任何负值表示请求需要立即执行。

我们可以使用*Runnable*和*Callable*接口来定义任务。

```java
public void execute() {
    ScheduledExecutorService executorService
      = Executors.newSingleThreadScheduledExecutor();

    Future<String> future = executorService.schedule(() -> {
        // ...
        return "Hello world";
    }, 1, TimeUnit.SECONDS);

    ScheduledFuture<?> scheduledFuture = executorService.schedule(() -> {
        // ...
    }, 1, TimeUnit.SECONDS);

    executorService.shutdown();
}
```

*ScheduledExecutorService* can also schedule the task **after some given fixed delay**:

```java
executorService.scheduleAtFixedRate(() -> {
    // ...
}, 1, 10, TimeUnit.SECONDS);

executorService.scheduleWithFixedDelay(() -> {
    // ...
}, 1, 10, TimeUnit.SECONDS);
```

Here, the ***scheduleAtFixedRate( Runnable command, long initialDelay, long period, TimeUnit unit )*** method creates and executes a periodic action that is invoked firstly after the provided initial delay, and subsequently with the given period until the service instance shutdowns.

The ***scheduleWithFixedDelay( Runnable command, long initialDelay, long delay, TimeUnit unit )*** method creates and executes a periodic action that is invoked firstly after the provided initial delay, and repeatedly with the given delay between the termination of the executing one and the invocation of the next one.

这里，***scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit)***方法创建并执行一个周期性操作，该操作在提供的初始延迟之后首先被调用，然后在给定的时间段内调用，直到服务实例关闭。

***scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit)***方法创建并执行一个周期性动作，该动作在提供的初始延迟之后首先被调用，并在当前执行的操作终止和下一个操作调用之间的给定延迟中重复调用。

### **2.4. \*Future\***

***Future\* is used to represent the result of an asynchronous operation.** It comes with methods for checking if the asynchronous operation is completed or not, getting the computed result, etc.

What's more, the *cancel(boolean mayInterruptIfRunning)* API cancels the operation and releases the executing thread. If the value of *mayInterruptIfRunning* is true, the thread executing the task will be terminated instantly.

Otherwise, in-progress tasks will be allowed to complete.

***Future\*用于表示异步操作的结果。**它附带了检查异步操作是否完成、获取计算结果等方法。

此外，*cancel(boolean mayInterruptIfRunning)* API取消操作并释放正在执行的线程。如果* mayinterruptirunning *的值为true，执行该任务的线程将立即终止。

否则，将允许正在进行的任务完成。

We can use below code snippet to create a future instance:

```java
public void invoke() {
    ExecutorService executorService = Executors.newFixedThreadPool(10);

    Future<String> future = executorService.submit(() -> {
        // ...
        Thread.sleep(10000l);
        return "Hello world";
    });
}
```

We can use following code snippet to check if the future result is ready and fetch the data if the computation is done:

```java
if (future.isDone() && !future.isCancelled()) {
    try {
        str = future.get();
    } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
    }
}
```

We can also specify a timeout for a given operation. If the task takes more than this time, a *TimeoutException* is thrown:

我们还可以为给定的操作指定超时。如果任务花费的时间超过这个时间，则抛出一个*TimeoutException*:

```java
try {
    future.get(10, TimeUnit.SECONDS);
} catch (InterruptedException | ExecutionException | TimeoutException e) {
    e.printStackTrace();
}
```

### **2.5. \*CountDownLatch\***

*CountDownLatch* (introduced in *JDK 5*) is a utility class which blocks a set of threads until some operation completes.

A *CountDownLatch* is initialized with a *counter(Integer* type); this counter decrements as the dependent threads complete execution. But once the counter reaches zero, other threads get released.

You can learn more about *CountDownLatch* [here](https://www.baeldung.com/java-countdown-latch).

CountDownLatch*(引入于*JDK 5*)是一个工具类，它会阻塞一组线程，直到一些操作完成。

一个*CountDownLatch*被初始化为一个*counter(Integer* type);当依赖线程完成执行时，此计数器递减。但是一旦计数器达到0，其他线程就会被释放。

你可以在这里(https://www.baeldung.com/java-countdown-latch)了解更多关于CountDownLatch的信息。

### **2.6. \*CyclicBarrier\***

*CyclicBarrier* works almost the same as *CountDownLatch* except that we can reuse it. Unlike *CountDownLatch*, it allows multiple threads to wait for each other using *await()* method(known as barrier condition) before invoking the final task.

We need to create a *Runnable* task instance to initiate the barrier condition:

*CyclicBarrier*的工作原理与*CountDownLatch*几乎相同，除了我们可以重用它。与*CountDownLatch*不同，它允许多个线程在调用最终任务之前使用*await()*方法(称为barrier condition)等待对方。

我们需要创建一个*Runnable* task实例来初始化barrier条件:

```java
public class Task implements Runnable {

    private CyclicBarrier barrier;

    public Task(CyclicBarrier barrier) {
        this.barrier = barrier;
    }

    @Override
    public void run() {
        try {
            LOG.info(Thread.currentThread().getName() + 
              " is waiting");
            barrier.await();
            LOG.info(Thread.currentThread().getName() + 
              " is released");
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

}
```

Now we can invoke some threads to race for the barrier condition:

```java
public void start() {

    CyclicBarrier cyclicBarrier = new CyclicBarrier(3, () -> {
        // ...
        LOG.info("All previous tasks are completed");
    });

    Thread t1 = new Thread(new Task(cyclicBarrier), "T1"); 
    Thread t2 = new Thread(new Task(cyclicBarrier), "T2"); 
    Thread t3 = new Thread(new Task(cyclicBarrier), "T3"); 

    if (!cyclicBarrier.isBroken()) { 
        t1.start(); 
        t2.start(); 
        t3.start(); 
    }
}
```

Here, the *isBroken()* method checks if any of the threads got interrupted during the execution time. We should always perform this check before performing the actual process.

这里，*isBroken()*方法检查在执行期间是否有任何线程被中断。我们应该总是在执行实际过程之前执行这个检查。

### **2.7. \*Semaphore\***

The *Semaphore* is used for blocking thread level access to some part of the physical or logical resource. A [semaphore](https://www.baeldung.com/cs/semaphore) contains a set of permits; whenever a thread tries to enter the critical section, it needs to check the semaphore if a permit is available or not.

**If a permit is not available (via \*tryAcquire()\*), the thread is not allowed to jump into the critical section; however, if the permit is available the access is granted, and the permit counter decreases.**

Once the executing thread releases the critical section, again the permit counter increases (done by *release()* method).

We can specify a timeout for acquiring access by using the *tryAcquire(long timeout, TimeUnit unit)* method.

**We can also check the number of available permits or the number of threads waiting to acquire the semaphore.**

Following code snippet can be used to implement a semaphore:

*Semaphore*用于阻塞对物理或逻辑资源的某些部分的线程级访问。一个[semaphore](https://www.baeldung.com/cs/semaphore)包含一组许可;当线程试图进入临界区时，它需要检查信号量是否有许可。

**如果一个许可不可用(通过\*tryAcquire()\*)，线程不允许跳转到临界区;但是，如果允许可用，则授予访问权，并且允许计数器减少。**

一旦正在执行的线程释放临界区，允许计数器再次增加(由*release()*方法完成)。

我们可以使用*tryAcquire(long timeout, TimeUnit unit)*方法指定获取访问的超时时间。

我们也可以检查可用的许可数或等待获取信号量的线程数

以下代码片段可以用来实现信号量:

```java
static Semaphore semaphore = new Semaphore(10);

public void execute() throws InterruptedException {

    LOG.info("Available permit : " + semaphore.availablePermits());
    LOG.info("Number of threads waiting to acquire: " + 
      semaphore.getQueueLength());

    if (semaphore.tryAcquire()) {
        try {
            // ...
        }
        finally {
            semaphore.release();
        }
    }

}
```

We can implement a *Mutex* like data-structure using *Semaphore*. More details on this[ can be found here.](https://www.baeldung.com/java-semaphore)

我们可以使用信号量实现一个类似数据结构的互斥锁。更多细节[可以在这里找到](https://www.baeldung.com/java-semaphore)

### **2.8. \*ThreadFactory\***

As the name suggests, *ThreadFactory* acts as a thread (non-existing) pool which creates a new thread on demand. It eliminates the need of a lot of boilerplate coding for implementing efficient thread creation mechanisms.

顾名思义，*ThreadFactory*作为一个线程(不存在)池，根据需要创建一个新的线程。它消除了实现高效线程创建机制所需的大量样板代码。



We can define a *ThreadFactory*:

```java
public class BaeldungThreadFactory implements ThreadFactory {
    private int threadId;
    private String name;

    public BaeldungThreadFactory(String name) {
        threadId = 1;
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, name + "-Thread_" + threadId);
        LOG.info("created new thread with id : " + threadId +
            " and name : " + t.getName());
        threadId++;
        return t;
    }
}
```

We can use this *newThread(Runnable r)* method to create a new thread at runtime:

```java
BaeldungThreadFactory factory = new BaeldungThreadFactory( 
    "BaeldungThreadFactory");
for (int i = 0; i < 10; i++) { 
    Thread t = factory.newThread(new Task());
    t.start(); 
}
```

### **2.9.** *BlockingQueue*

In asynchronous programming, one of the most common integration patterns is the [producer-consumer pattern](https://en.wikipedia.org/wiki/Producer–consumer_problem). The *java.util.concurrent* package comes with a data-structure know as *BlockingQueue* – which can be very useful in these async scenarios.

More information and a working example on this is available [here](https://www.baeldung.com/java-blocking-queue).

在异步编程中，最常见的集成模式之一是[生产者-消费者模式](https://en.wikipedia.org/wiki/Producer -consumer_problem)。* java.util。concurrent* package附带了一个称为*BlockingQueue*的数据结构——这在这些异步场景中非常有用。

更多信息和工作示例可以在[这里](https://www.baeldung.com/java-blocking-queue)获得。

### **2.10. \*DelayQueue\***

*DelayQueue* is an infinite-size blocking queue of elements where an element can only be pulled if it's expiration time (known as user defined delay) is completed. Hence, the topmost element (*head*) will have the most amount delay and it will be polled last.

More information and a working example on this is available [here](https://www.baeldung.com/java-delay-queue).

*DelayQueue*是一个无限大小的阻塞队列，其中一个元素只有在它的过期时间(称为用户定义的延迟)完成时才能被拉出。因此，最顶层的元素(*head*)将有最大的延迟，它将在最后被轮询。

更多信息和工作示例可以在[这里](https://www.baeldung.com/java-delay-queue)获得。

### **2.11. \*Locks\***

Not surprisingly, *Lock* is a utility for blocking other threads from accessing a certain segment of code, apart from the thread that's executing it currently.

The main difference between a Lock and a Synchronized block is that synchronized block is fully contained in a method; however, we can have Lock API’s lock() and unlock() operation in separate methods.

More information and a working example on this is available [here](https://www.baeldung.com/java-concurrent-locks).

毫不奇怪，*Lock*是一个用于阻止其他线程访问特定代码段的工具，除了当前正在执行它的线程。

锁和同步块的主要区别是同步块完全包含在一个方法中;但是，我们可以在不同的方法中使用Lock API的Lock()和unlock()操作。

更多信息和工作示例可以在[这里](https://www.baeldung.com/java-concurrent-locks)获得。

### **2.12. \*Phaser\*

*Phaser* is a more flexible solution than *CyclicBarrier* and *CountDownLatch* – used to act as a reusable barrier on which the dynamic number of threads need to wait before continuing execution. We can coordinate multiple phases of execution, reusing a *Phaser* instance for each program phase.

More information and a working example on this is available [here](https://www.baeldung.com/java-phaser).

*Phaser*是一个比*CyclicBarrier*和*CountDownLatch*更灵活的解决方案-用于作为一个可重用的barrier，在继续执行之前动态线程数量需要等待。我们可以协调多个执行阶段，为每个程序阶段重用一个*Phaser*实例。

更多信息和工作示例可以在[这里](https://www.baeldung.com/java-phaser)获得。

## **3. Conclusion**

In this high-level, overview article, we've focused on the different utilities available of *java.util.concurrent* package.

As always, the full source code is available [over on GitHub](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-concurrency-basic).

在这篇高级的概述文章中，我们重点讨论了*java.util的不同实用程序。并发*包。
