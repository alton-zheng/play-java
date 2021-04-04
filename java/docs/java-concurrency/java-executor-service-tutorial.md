# Java ExecutorService 指引

## 1. 概览

*[ExecutorService](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html)* 是一个 JDK API，它简化了在异步模式下运行任务。一般来说，*ExecutorService* 会自动提供一个线程池和一个 API 来给它分配任务。

&nbsp;

## 2.  ExecutorService 示例

### 2.1. Executors 类工厂方法

创建 *ExecutorService* 的最简单方法是使用 *Executors* 类的一个工厂方法。

例如，下面的代码将创建一个有10个线程的线程池:

```java
ExecutorService executor = Executors.newFixedThreadPool(10);
```

&nbsp;

还有其他一些工厂方法可以创建预定义的 *ExecutorService*，满足特定的用例。要找到适合您需要的最佳方法，请参阅 [Oracle的官方文档](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executors.html) 。

&nbsp;

### 2.2. 直接创建 ExecutorService 

因为 *ExecutorService* 是一个接口，所以可以使用它的任何实现的实例。在 *[java.util.concurrent](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executors.html)* 包中有几种实现可供选择，或者您也可以创建自己的。

例如，*ThreadPoolExecutor* 类有一些构造函数，我们可以使用它们来配置 executor service 及其内部池:

```java
ExecutorService executorService = 
  new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,   
  new LinkedBlockingQueue<Runnable>());
```

你可能注意到，上面的代码与工厂方法 *newSingleThreadExecutor()* 的 [源代码](https://github.com/openjdk-mirror/jdk7u-jdk/blob/master/src/share/classes/java/util/concurrent/Executors.java#L133) 非常相似。在大多数情况下，不需要详细的手动配置。

&nbsp;

## 3. 分配任务给 ExecutorService

*ExecutorService* 可以执行 $Runnable$ 和 $Callable$ 任务。在本文中，为了保持简单，将使用两个基本任务。注意，这里我们使用 lambda 表达式而不是匿名内部类：

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

&nbsp;

我们可以使用几个方法将任务分配给 *ExecutorService*，包括继承自 *Executor* 接口的 *execute()* ，以及*submit()*、*invokeAny()* 和 *invokeAll()* 。

execute() 方法是 *void* 并且不提供任何可能来获取任务的执行结果或检查任务的状态(它是否正在运行)：

```java
executorService.execute(runnableTask);
```

&nbsp;

*submit()* 提交 *Callable* 或 *Runnable* 任务到 *ExecutorService* ，并返回类型为 $Future$ 的结果：

```java
Future<String> future = 
  executorService.submit(callableTask);
```

&nbsp;

*invokeAny()* 将一个任务集合分配给一个 ExecutorService，引起每个任务运行， 并返回一个任务成功执行的结果（如果成功执行）：

```java
String result = executorService.invokeAny(callableTasks);
```

&nbsp;

***invokeAll()*** 将一组任务分配给一个 *ExecutorService* ，导致每个任务都运行，并以类型为 *Future* 的对象列表的形式返回所有任务执行的结果：

```java
List<Future<String>> futures = executorService.invokeAll(callableTasks);
```

在进一步讨论之前，我们需要讨论另外两个项目： 关闭 *ExecutorService* 和 处理 *Future* 返回类型。

&nbsp;

## 4. 关闭 ExecutorService

一般来说，当没有需要处理的任务时，*ExecutorService* 不会被自动销毁。它会保持活力，等待新的工作来做。

- 在某些情况下，这是非常有用的，例如当一个 application 需要处理不定期出现的任务或在编译时不知道任务数量。
- 另一方面，application 可能到达它的端，但不会停止，因为等待的 *ExecutorService* 将导致 JVM 继续运行。

&nbsp;

为了正确关闭一个 *ExecutorService*，我们有 *shutdown()* 和 *shutdownNow()* api。

$shutdown()$ 方法不会立即销毁 *ExecutorService*。它将使 *ExecutorService* 停止接受新任务，并在所有正在运行的线程完成当前工作后关闭：

```java
executorService.shutdown();
```

The ***shutdownNow()\*** method tries to destroy the *ExecutorService* immediately, but it doesn't guarantee that all the running threads will be stopped at the same time:

&nbsp;

**shutdownNow()** 方法试图立即销毁 *ExecutorService*，但它不保证所有正在运行的线程会在同一时间停止:

```java
List<Runnable> notExecutedTasks = executorService.shutDownNow();
```

此方法返回等待处理的任务列表。由开发人员决定如何处理这些任务。

关闭 *ExecutorService* (这也是 [Oracle推荐](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html) )的一个好方法是使用这两个方法结合 *awaitTermination()* 方法：

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

使用这种方法，*ExecutorService* 将首先停止接受新任务，然后等待指定的时间，直到所有任务都完成。如果该时间到期，则立即停止执行。

&nbsp;

## 5.  Future 接口

*submit()* 和 *invokeAll()* 方法返回一个对象或一个类型为 *Future* 的对象集合，它允许我们获取任务的执行结果或检查任务的状态(它是否正在运行)。

*Future* 接口提供了一个特殊的阻塞方法 *get()*，它返回 *Callable* 任务的实际执行结果，或者在 *Runnable* 任务的情况下返回 *null*：

```java
Future<String> future = executorService.submit(callableTask);
String result = null;
try {
    result = future.get();
} catch (InterruptedException | ExecutionException e) {
    e.printStackTrace();
}
```

在任务仍在运行时调用 *get()* 方法将导致执行阻塞，直到任务正确执行并且结果可用。

如果 *get()* 方法导致了很长时间的阻塞，application 的性能会降低。如果结果数据不是至关重要的，可以通过使用超时来避免这样的问题：

```java
String result = future.get(200, TimeUnit.MILLISECONDS);
```

&nbsp;

如果执行周期比指定的时间长(在本例中为200毫秒)，将抛出一个 *TimeoutException*。

我们可以使用 *isDone()* 方法检查所分配的任务是否已经处理。

*Future* 接口还提供了使用 *cancel()* 方法取消任务执行，并使用 *isCancelled()* 方法检查取消：

```java
boolean canceled = future.cancel(true);
boolean isCancelled = future.isCancelled();
```

&nbsp;

## 6. ScheduledExecutorService 接口

*`ScheduledExecutorService`* 在预定义的延迟和/或周期性运行任务。

同样，实例化一个 *ScheduledExecutorService* 的最佳方法是使用 *Executors* 类的工厂方法。

在本节中，我们使用一个 *`ScheduledExecutorService`* 和一个线程：

```java
ScheduledExecutorService executorService = Executors
  .newSingleThreadScheduledExecutor();
```

&nbsp;

要在固定延迟后安排单个任务的执行，请使用 *ScheduledExecutorService* 的 *scheduled()* 方法。

两个 *scheduled()* 方法允许您执行 *Runnable* 或 *Callable* 任务：

```java
Future<String> resultFuture = 
  executorService.schedule(callableTask, 1, TimeUnit.SECONDS);
```

&nbsp;

*scheduleAtFixedRate()* 方法允许我们在固定延迟后定期运行任务。上面的代码在执行 *callableTask* 之前会延迟一秒钟。

下面的代码块将在初始延迟100毫秒后运行一个任务。之后，它会每450毫秒运行一次同样的任务:

```java
Future<String> resultFuture = service
  .scheduleAtFixedRate(runnableTask, 100, 450, TimeUnit.MILLISECONDS);
```

如果 *processor* 需要比 *scheduleAtFixedRate()* 方法的 *period* 参数更多的时间来运行分配的任务，则 *ScheduledExecutorService* 将等待当前任务完成后再开始下一个任务。

&nbsp;

如果需要在任务的迭代之间有一个固定长度的延迟，则应该使用 *scheduleWithFixedDelay()*。

例如，下面的代码将保证在当前执行结束和另一个执行开始之间有 150 毫秒的暂停：

```java
service.scheduleWithFixedDelay(task, 100, 150, TimeUnit.MILLISECONDS);
```

根据 *scheduleAtFixedRate()* 和 *scheduleWithFixedDelay()* 方法契约，任务的周期执行将在 *ExecutorService* 终止时结束，或者在任务执行过程中抛出异常时结束

&nbsp;

## 7. ExecutorService vs Fork/Join

在 Java 7 发布后，许多开发人员决定用 fork/join 框架取代 *ExecutorService* 框架。

然而，这并不总是正确的决定。尽管 fork/join 具有简单性和频繁的性能提升，但它减少了开发人员对并发执行的控制。

*ExecutorService* 让开发者能够控制生成线程的数量和应该由独立线程运行的任务粒度。*ExecutorService* 的最佳场景是处理独立的任务，比如按照 “一个线程处理一个任务” 的方案处理事务或请求。

相比之下，[根据Oracle的文档](https://docs.oracle.com/javase/tutorial/essential/concurrency/forkjoin.html) ， fork/join 的设计是为了加速递归分解成更小部分的工作。

&nbsp;

## 8. 小节

尽管 *ExecutorService* 相对简单，但仍然存在一些常见的缺陷。

让我们总结一下：

保持一个未使用的 *ExecutorService* 活着:  参见第 4 节关于如何关闭一个 ExecutorService 的详细说明。

**使用固定长度的线程池时错误的线程池容量**：确定应用程序有效运行任务需要多少线程是非常重要的。一个太大的线程池将导致不必要的开销，仅仅是为了创建大部分处于等待模式的线程。太少会使应用程序看起来没有响应，因为队列中任务的等待时间过长。

在任务取消后调用一个 \*Future\* 的 *get()* 方法： 尝试获取一个已经取消的任务的结果会触发一个 *CancellationException* 。

使用 *Future* 的 `get()` 方法意外长阻塞 ： 我们应该使用超时来避免意外等待。