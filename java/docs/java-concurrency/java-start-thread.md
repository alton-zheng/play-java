# Java 如何启动 Thread

&nbsp;

## 1. 介绍

在本教程中，我们将探索启动线程和执行并行任务的不同方法。

这是非常有用的，特别是当处理长或重复操作不能在主线程上运行时，或者 UI 交互不能在等待操作结果时暂停。

要想了解更多关于线程的细节，一定要阅读我们的教程 [Java中线程的生命周期](java-thread-lifecycle.md)

&nbsp;

## 2. 运行线程的基础

通过使用 *Thread* 框架，我们可以很容易地编写一些运行在并行线程中的逻辑。

让我们尝试一个基本的例子，通过扩展*Thread*类:

&nbsp;

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

&nbsp;

现在我们编写第二个类来初始化和启动线程:

```java
public class SingleThreadExample {
    public static void main(String[] args) {
        NewThread t = new NewThread();
        t.start();
    }
}
```

&nbsp;

我们应该在 [NEW](https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.State.html#NEW) state(相当于 not started ) 中的线程上调用 *start()* 方法。否则，Java 将抛出[*IllegalThreadStateException*](https://docs.oracle.com/javase/8/docs/api/java/lang/IllegalThreadStateException.html) exception 的实例。

现在假设我们需要启动多个线程:

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

我们的代码看起来仍然非常简单，与我们可以在网上找到的示例非常相似。

当然，这远非生产代码，在生产代码中，以正确的方式管理资源至关重要，以避免过多的上下文切换或过多的内存使用

因此，为了准备生产，我们现在需要编写额外的样板文件来处理:

- 一致地创建新线程
- 并发活动线程数
- 线程释放: 为了避免线程泄露，守护线程非常重要

如果我们愿意，我们可以为所有这些情况甚至更多情况编写我们自己的代码，但是为什么我们要白费口力呢?

&nbsp;

## 3. ExecutorService 框架

*ExecutorService* 实现了 `Thread Pool` 设计模式(也称为复制的 `worker` 或 `worker-crew` 模型)，并负责我们上面提到的线程管理，此外它还添加了一些非常有用的特性，如线程可重用性和任务队列。

线程的可重用性尤其重要: 在一个大规模的应用程序中，分配和释放许多线程对象会造成很大的内存管理开销

对于工作线程，我们最小化了创建线程所带来的开销

为了简化池的配置，*ExecutorService* 提供了一个简单的构造函数和一些定制选项，比如 queue 的类型、最小和最大线程数以及它们的命名约定。

关于 *ExecutorService* 的更多细节，请阅读我们的 [Java ExecutorService指南](java-executor-service-tutorial.md)。

&nbsp;

## 4. Executor 

感谢这个强大的框架，我们可以从开始线程转换到提交任务

看看如何向执行程序提交异步任务:

```java
ExecutorService executor = Executors.newFixedThreadPool(10);
...
executor.submit(() -> {
    new Task();
});
```

我们可以使用两个方法: 

- *execute*，它不返回任何结果; 

- *submit*，它返回封装了计算结果的 *Future*。

更多关于 *Future* 的信息，请阅读我们的 [java.util.concurrent.Future 指导](java-future.md)。

&nbsp;

## 5. CompletableFuture 启动线程

要从一个 *Future* 对象中检索最终结果，我们可以使用对象中可用的 *get* 方法，但这会阻塞 parent 线程，直到计算结束。

另外，我们也可以通过向任务添加更多逻辑来避免阻塞，但是我们必须增加代码的复杂性。

Java 1.8在 Future 之上引入了一个新的框架来更好地处理计算结果: $CompletableFuture$

CompletableFuture 实现了 CompletableStage，它添加了大量的方法选择来附加 `callback`，并避免在结果准备好后运行操作所需的所有通道

提交任务的实现要简单得多:

```java
CompletableFuture.supplyAsync(() -> "Hello");
```

*supplyAsync* 接受一个 *Supplier* 包含我们想要异步执行的代码——在我们的例子中是 lambda 参数。

任务现在隐式地提交给  ForkJoinPool.commonPool()，或者我们可以指定我们喜欢的 *Executor* 作为第二个参数

要了解更多关于 *CompletableFuture* 请阅读我们的 [CompletableFuture 指导](java-completablefuture.md)。

&nbsp;

## 6. Running 延迟或周期性 Task


当处理复杂的 web 应用程序时，我们可能需要在特定的时间运行任务，可能定期运行

Java很少有工具可以帮助我们运行延迟或重复的操作:

- `java.util.Timer`
- `java.util.concurrent.ScheduledThreadPoolExecutor`

&nbsp;

### 6.1. *Timer*

*Timer* is a facility to schedule tasks for future execution in a background thread.

Tasks may be scheduled for one-time execution, or for repeated execution at regular intervals.

Let's see what the code looks if we want to run a task after one second of delay:

*Timer* 是一个用于在后台线程中调度任务的工具。

可以将任务安排为一次性执行，或定期重复执行。

让我们看看如果我们想在延迟一秒钟后运行一个任务，代码是什么样子的:

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

&nbsp;

现在让我们添加一个循环的时间表:

```java
timer.scheduleAtFixedRate(repeatedTask, delay, period);
```

这一次，任务将在指定的延迟后运行，并在一段时间后重复运行。

欲了解更多信息，请阅读我们的 [Java Timer](java-timer-and-timertask.md)。

&nbsp;

### 6.2. *ScheduledThreadPoolExecutor*

* *schedulethreadpoolexecutor* 有类似于 *Timer* 类的方法:

```java
ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
ScheduledFuture<Object> resultFuture
  = executorService.schedule(callableTask, 1, TimeUnit.SECONDS);
```

在结束我们的示例时，我们使用 *scheduleAtFixedRate()* 来完成重复的任务:

```java
ScheduledFuture<Object> resultFuture
 = executorService.scheduleAtFixedRate(runnableTask, 100, 450, TimeUnit.MILLISECONDS);
```

&nbsp;

上面的代码将在初始延迟 100 ms 后执行一个任务，之后，它将每 450 ms 执行一次相同的任务。

如果处理器不能在下一次发生前及时完成任务处理，则 `ScheduledExecutorService` 将等待当前任务完成，然后再启动下一个任务。

为了避免这个等待时间，我们可以使用 *scheduleWithFixedDelay()* ，正如其名称所描述的那样，它保证了任务迭代之间的固定长度延迟。

有关 *ScheduledExecutorService* 的更多信息，请阅读我们的 [Java ExecutorService指南](java-executor-service-tutorial)。

&nbsp;

### 6.3. 那个工具 Better?

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

如果我们运行上面的例子，计算结果看起来是一样的。

那么，我们如何选择合适的工具呢?

当一个框架提供多个选择时，理解底层技术以便做出明智的决定是很重要的

让我们试着再深入一点。

&nbsp;

**Timer** ：

- 不提供实时保证: 它使用 *Object.wait(long)* 方法调度任务
- 有一个单一的后台线程，所以任务按顺序运行，一个长时间运行的任务会延迟其他任务
- 在 *TimerTask* 中抛出的运行时异常会杀死唯一可用的线程，从而杀死 *Timer*

&nbsp;
**ScheduledThreadPoolExecutor**：

- 可以配置任意数量的线程
- 可以利用所有可用的 CPU 内核
- 捕获运行时异常并允许我们处理它们(通过重写 *ThreadPoolExecutor* 中的 *afterExecute* 方法)
- 取消抛出异常的任务，同时让其他任务继续运行
- 依赖于操作系统的调度系统来跟踪 zone、delay、solar time 等。
- 提供协作API，如果我们需要协调多个任务，如等待完成所有提交的任务
- 为线程生命周期的管理提供更好的 `API`

现在的选择是显而易见的，对吧?

&nbsp;

## 7. Future 和 *ScheduledFuture* 区别

在我们的代码示例中，我们可以看到 $ScheduledThreadPoolExecutor$ 返回特定类型的 Future: $ScheduledFuture$

$ScheduledFuture$  extends 了 *Future* 和 *Delayed* 接口，因此继承了额外的方法 getDelay*，该方法返回与当前任务相关的剩余延迟。它由 *RunnableScheduledFuture* 扩展，添加了一个检查任务是否周期性的方法。

$ScheduledThreadPoolExecutor$ 通过内部类 $ScheduledFutureTask$ 实现了所有这些构造，并使用它们来控制任务的生命周期

&nbsp;

## 8 总结

在本教程中，我们尝试使用了可用来启动 $Thread$ 和并行运行任务的不同框架。

然后，我们深入研究了 $Timer$ 和 $ScheduledThreadPoolExecutor$ 之间的差异！
