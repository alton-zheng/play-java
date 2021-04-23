# java.util.concurrent 概览



## **1. 概览**

`*java.util.concurrent*` 包提供创建并发应用程序的工具。

在本文中，我们将对整个包进行概述。

&nbsp;

## 2. 主要的组件

**java.util.concurrent**  包含太多 feature，无法在一篇文章中讨论完全。在本文中，我们将主要关注这个包中一些最有用的实用程序，比如:

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

您还可以在这里找到许多专门针对各个类的文章。

&nbsp;

### 2.1 Executor

[Executor](https://docs.oracle.com/javase/8/docs/fapi/java/util/concurrent/Executor.html) 是一个接口，表示执行提供的任务的对象

它取决于特定的实现(从调用开始的地方)，任务是否应该在新的线程或当前线程上运行。因此，使用这个接口，我们可以将任务执行流与实际的任务执行机制解耦。

这里需要注意的一点是，*Executor* 并不严格要求任务执行是异步的。在最简单的情况下，执行程序可以在调用线程中立即调用提交的任务。

我们需要创建一个 invoker 来创建 executor 实例:

```java
public class Invoker implements Executor {
    @Override
    public void execute(Runnable r) {
        r.run();
    }
}
```

&nbsp;

现在，我们可以使用这个 `invoker` 执行任务。

```java
public void execute() {
    Executor executor = new Invoker();
    executor.execute( () -> {
        // task to be performed
    });
}
```

这里需要注意的是，如果 executor 不能接受任务执行，它将抛出 [RejectedExecutionException](https://docs.oracle.com/javase/8/dfocs/api/java/util/concurrent/RejectedExecutionException.html) 。

&nbsp;

### 2.2. ExecutorService

*ExecutorService* 是一个完整的异步处理解决方案。它管理 `in-memory` 中的队列，并根据线程可用性调度提交的任务。

要使用 *ExecutorService* ，我们需要创建一个 *Runnable* 类。

```java
public class Task implements Runnable {
    @Override
    public void run() {
        // task details
    }
}
```

&nbsp;

现在我们能创建 *ExecutorService* 实例和分配这个任务。 在创建的同时，我们需要指定 `thread-pool` 的大小。

```java
ExecutorService executor = Executors.newFixedThreadPool(10);
```

&nbsp;

如果我们想创建一个单线程的 *ExecutorService* 实例，我们可以使用 `newSingleThreadExecutor(ThreadFactory ThreadFactory) ` 来创建实例。

一旦创建了 executor ，我们就可以使用它来提交任务。

```java
public void execute() { 
    executor.submit(new Task()); 
}
```

&nbsp;

我们还可以在提交任务时创建 *Runnable* 实例。

```java
executor.submit(() -> {
    new Task();
});
```

&nbsp;

它还提供了两个 `out-of-the-box` 的 termination 方法。

- 第一个是 *shutdown()* ; 它会一直等待，直到所有提交的任务完成执行。
- 另一个方法是 *shutdownNow()*，它会立即终止所有挂起和正在执行的任务。

还有另一个方法 `awaitTermination(long timeout, TimeUnit unit)` 强制阻塞，直到触发 `shutdown` 事件或发生 `execution-timeout`，或执行线程本身被中断后，所有任务都完成执行。

```java
try {
    executor.awaitTermination( 20l, TimeUnit.NANOSECONDS );
} catch (InterruptedException e) {
    e.printStackTrace();
}
```

&nbsp;

### 2.3. ScheduledExecutorService

*ScheduledExecutorService* 是一个类似于 *ExecutorService* 的接口，它可以定期执行任务。

`Executor` 和 `ExecutorService` 的方法在当场调度，不引入任何人为的延迟。 `0` 或任何 **负值** 表示请求需要立即执行。

我们可以使用 *Runnable* 和 *Callable* 接口来定义任务。

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

&nbsp;

*ScheduledExecutorService* 也能设定固定的延迟进行调度。

```java
executorService.scheduleAtFixedRate(() -> {
    // ...
}, 1, 10, TimeUnit.SECONDS);

executorService.scheduleWithFixedDelay(() -> {
    // ...
}, 1, 10, TimeUnit.SECONDS);
```

&nbsp;

这里，***scheduleAtFixedRate( Runnable command, long initialDelay, long period, TimeUnit unit )*** 方法创建并执行一个周期性操作，该操作在提供的初始延迟之后首先被调用，然后在给定的时间段内调用，直到服务实例关闭。

***scheduleWithFixedDelay( Runnable command, long initialDelay, long delay, TimeUnit unit )***  方法创建并执行一个周期性动作，该动作在提供的初始延迟之后首先被调用，并在当前执行的操作终止和下一个操作调用之间的给定延迟中重复调用。

&nbsp;

### 2.4. Future

Future 用于表示异步操作的结果。它附带了检查异步操作是否完成、获取计算结果等方法。

此外，*cancel(boolean mayInterruptIfRunning)*  API 取消操作并释放正在执行的线程。如果 *mayInterruptIfRunning* 的值为 `true`，执行该任务的线程将立即终止。

否则，将允许正在进行的任务完成。

&nbsp;

我们能用下面的代码片段创建一个 future 实例：

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

&nbsp;

我们能使用下面代码片段检查 future 结果是否 ready 和计算完成时，抓取数据。

```java
if (future.isDone() && !future.isCancelled()) {
    try {
        str = future.get();
    } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
    }
}
```

我们还可以为给定的操作指定超时。如果任务花费的时间超过这个时间，则抛出一个 *TimeoutException* :

```java
try {
    future.get(10, TimeUnit.SECONDS);
} catch (InterruptedException | ExecutionException | TimeoutException e) {
    e.printStackTrace();
}
```

&nbsp;

### 2.5. CountDownLatch

*CountDownLatch* (引入于 *JDK 5*)  是一个工具类，它会阻塞一组线程，直到一些操作完成。

一个 *CountDownLatch* 被初始化为一个 *counter(Integer* type) ;当依赖线程完成执行时，此计数器递减。但是一旦计数器达到 0，其他线程就会被释放。

你可以在 [这里](java-countdown-latch.md) 了解更多关于 CountDownLatch 的信息。

&nbsp;

### 2.6. CyclicBarrier

*CyclicBarrier* 的工作原理与 *CountDownLatch* 几乎相同，除了我们可以重用它。与 *CountDownLatch* 不同，它允许多个线程在调用最终任务之前使用 *await()* 方法 (称为barrier condition) 等待对方。

我们需要创建一个*Runnable* task 实例来初始化 barrier 条件:

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

&nbsp;

现在我们可以调用一些线程来竞争 barrier 条件:

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

这里，*isBroken()* 方法检查在执行期间是否有任何线程被中断。我们应该总是在执行实际过程之前执行这个检查。

&nbsp;

### 2.7. Semaphore

*Semaphore* 用于阻塞对物理或逻辑资源的某些部分的线程级访问。一个 [semaphore](semaphore.md) 包含一组许可; 当线程试图进入临界区时，它需要检查信号量是否有许可。

如果一个许可不可用 (通过 *tryAcquire()* )，线程不允许跳转到临界区; 但是，如果允许可用，则授予访问权，并且允许计数器减少。

一旦正在执行的线程释放临界区，允许计数器再次增加(由 *release()* 方法完成)。

我们可以使用 *tryAcquire(long timeout, TimeUnit unit)* 方法指定获取访问的超时时间。

我们也可以检查可用的许可数或等待获取信号量的线程数

以下代码片段可以用来实现信号量：

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

我们可以使用 $Semaphore$ 实现一个类似 `data-structure` 的互斥锁( `Mutex` )。更多细节 [可以在这里找到](java-semaphore.md)

&nbsp;

### 2.8. ThreadFactory

顾名思义，*ThreadFactory* 作为一个线程 (non-existing) 池，根据需要创建一个新的线程。它消除了实现高效线程创建机制所需的大量样板代码。

&nbsp;

我们可以定义一个 $ThreadFactory$：

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

&nbsp;

我们可以在 runtime 使用 *newThread(Runnable r)* 方法创建一个新线程：

```java
BaeldungThreadFactory factory = new BaeldungThreadFactory( 
    "BaeldungThreadFactory");
for (int i = 0; i < 10; i++) { 
    Thread t = factory.newThread(new Task());
    t.start(); 
}
```

&nbsp;

### **2.9.** *BlockingQueue*

在异步编程中，最常见的集成模式之一是 [producer-consumer 模式](Producer -consumer_problem.md)。`java.util.concurrent` 包中附带了一个称为 *BlockingQueue* 的 `data-structure` - 在这些异步场景中非常有用。

更多信息和工作示例可以在 [这里](java-blocking-queue.md) 获得。

&nbsp;

### 2.10. DelayQueue

*DelayQueue* 是一个无限大小的阻塞队列，其中一个元素只有在它的过期时间(称为用户定义的延迟)完成时才能被拉出。因此，最顶层的元素 (*head*) 将有最大的延迟，它将在最后被轮询。

更多信息和工作示例可以在 [这里](java-delay-queue.md) 获得。

&nbsp;

### 2.11. Locks

毫不奇怪，*Lock* 是一个用于阻止其他线程访问正在执行特定代码段（仅有正在执行它的线程才能访问）的工具。

`Lock` 和 `Synchronized` 的主要区别是

- `Synchronized` 完全包含在一个方法中;  
- 但是，我们可以在不同的方法中使用 Lock API 的 Lock() 和 unlock() 操作。

更多信息和工作示例可以在 [这里](java-concurrent-locks.md) 获得。

&nbsp;

### 2.12. Phaser

*Phaser* 是一个比 *CyclicBarrier* 和 *CountDownLatch* 更灵活的解决方案-用于作为一个可重用的 `barrier`，在继续执行之前动态线程数量需要等待。我们可以协调多个执行阶段，为每个程序阶段重用一个 *Phaser* 实例。

更多信息和工作示例可以在 [这里](java-phaser.md) 获得。

&nbsp;

## 3. 总结

在这篇 high-level 的概述文章中，我们重点讨论了 *`java.util.concurrent`* 包不同的可用的实用工具。

