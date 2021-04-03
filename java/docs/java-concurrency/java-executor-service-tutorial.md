# A Guide to the Java ExecutorService

## **1. Overview**

*[ExecutorService](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html)* is a JDK API that simplifies running tasks in asynchronous mode. Generally speaking, *ExecutorService* automatically provides a pool of threads and an API for assigning tasks to it.

*[ExecutorService](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html)*是一个JDK API，它简化了在异步模式下运行任务。一般来说，*ExecutorService*会自动提供一个线程池和一个API来给它分配任务。



## **2. Instantiating \*ExecutorService\***

### **2.1. Factory Methods of the \*Executors\* Class**

The easiest way to create *ExecutorService* is to use one of the factory methods of the *Executors* class.

For example, the following line of code will create a thread pool with 10 threads:

创建*ExecutorService*的最简单方法是使用*Executors*类的一个工厂方法。

例如，下面的代码将创建一个有10个线程的线程池:

```java
ExecutorService executor = Executors.newFixedThreadPool(10);
```

There are several other factory methods to create a predefined *ExecutorService* that meets specific use cases. To find the best method for your needs, consult [Oracle's official documentation](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/Executors.html).

还有其他一些工厂方法可以创建预定义的*ExecutorService*，满足特定的用例。要找到适合您需要的最佳方法，请参阅[Oracle的官方文档](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/Executors.html)。

### **2.2. Directly Create an \*ExecutorService\***

Because *ExecutorService* is an interface, an instance of any its implementations can be used. There are several implementations to choose from in the *[java.util.concurrent](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executors.html)* package, or you can create your own.

For example, the *ThreadPoolExecutor* class has a few constructors that we can use to configure an executor service and its internal pool:

因为*ExecutorService*是一个接口，所以可以使用它的任何实现的实例。在*[java.util.concurrent](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executors.html)*包中有几种实现可供选择，或者您也可以创建自己的。

例如，*ThreadPoolExecutor*类有一些构造函数，我们可以使用它们来配置executor服务及其内部池:

```java
ExecutorService executorService = 
  new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,   
  new LinkedBlockingQueue<Runnable>());
```

You may notice that the code above is very similar to the [source code](https://github.com/openjdk-mirror/jdk7u-jdk/blob/master/src/share/classes/java/util/concurrent/Executors.java#L133) of the factory method *newSingleThreadExecutor().* For most cases, a detailed manual configuration isn't necessary.

您可能注意到，上面的代码与工厂方法*newSingleThreadExecutor()的[源代码](https://github.com/openjdk-mirror/jdk7u-jdk/blob/master/src/share/classes/java/util/concurrent/Executors.java#L133)非常相似。*在大多数情况下，不需要详细的手动配置。

## **3. Assigning Tasks to the \*ExecutorService\***

*ExecutorService* can execute *Runnable* and *Callable* tasks. To keep things simple in this article, two primitive tasks will be used. Notice that we use lambda expressions here instead of anonymous inner classes:

*ExecutorService*可以执行*可运行*和*可调用*任务。在本文中，为了保持简单，将使用两个基本任务。注意，这里我们使用lambda表达式而不是匿名内部类:

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

We can assign tasks to the *ExecutorService* using several methods including *execute()*, which is inherited from the *Executor* interface, and also *submit()*, *invokeAny()* and *invokeAll()*.

The ***execute()\*** method is *void* and doesn't give any possibility to get the result of a task's execution or to check the task's status (is it running):

我们可以使用几个方法将任务分配给*ExecutorService*，包括继承自*Executor*接口的*execute()*，以及*submit()*、*invokeAny()*和*invokeAll()*。

execute()\*** *方法是*void*并且不提供任何可能来获取任务的执行结果或检查任务的状态(它是否正在运行):

```java
executorService.execute(runnableTask);
```

***submit()\*** submits a *Callable* or a *Runnable* task to an *ExecutorService* and returns a result of type *Future*:

提交一个*Callable*或*Runnable*任务到*ExecutorService*，并返回类型为*Future*的结果:

```java
Future<String> future = 
  executorService.submit(callableTask);
```

***invokeAny()\*** assigns a collection of tasks to an *ExecutorService*, causing each to run, and returns the result of a successful execution of one task (if there was a successful execution):

***invokeAny()\***将一个任务集合分配给一个*ExecutorService*，导致每个任务运行，并返回一个任务成功执行的结果(如果成功执行):

```java
String result = executorService.invokeAny(callableTasks);
```

***invokeAll()*** assigns a collection of tasks to an *ExecutorService*, causing each to run, and returns the result of all task executions in the form of a list of objects of type *Future*:

***invokeAll()***将一组任务分配给一个*ExecutorService*，导致每个任务都运行，并以类型为*Future*的对象列表的形式返回所有任务执行的结果:

```java
List<Future<String>> futures = executorService.invokeAll(callableTasks);
```

Before going further, we need to discuss two more items: shutting down an *ExecutorService* and dealing with *Future* return types.

在进一步讨论之前，我们需要讨论另外两个项目:关闭*ExecutorService*和处理*Future*返回类型。

&nbsp;

## **4. Shutting Down an \*ExecutorService\***

In general, the *ExecutorService* will not be automatically destroyed when there is no task to process. It will stay alive and wait for new work to do.

In some cases this is very helpful, such as when an app needs to process tasks that appear on an irregular basis or the task quantity is not known at compile time.

On the other hand, an app could reach its end but not be stopped because a waiting *ExecutorService* will cause the JVM to keep running.

一般来说，当没有需要处理的任务时，*ExecutorService*不会被自动销毁。它会保持活力，等待新的工作来做。

在某些情况下，这是非常有用的，例如当一个应用程序需要处理不定期出现的任务或任务数量在编译时不知道。

另一方面，应用程序可能到达它的端，但不会停止，因为等待的*ExecutorService*将导致JVM继续运行。

To properly shut down an *ExecutorService*, we have the *shutdown()* and *shutdownNow()* APIs.

The ***shutdown()*** method doesn't cause immediate destruction of the *ExecutorService*. It will make the *ExecutorService* stop accepting new tasks and shut down after all running threads finish their current work:

为了正确关闭一个*ExecutorService*，我们有*shutdown()*和*shutdownNow()* api。

shutdown()***方法不会立即销毁*ExecutorService*。它将使*ExecutorService*停止接受新任务，并在所有正在运行的线程完成当前工作后关闭:

```java
executorService.shutdown();
```

The ***shutdownNow()\*** method tries to destroy the *ExecutorService* immediately, but it doesn't guarantee that all the running threads will be stopped at the same time:

***shutdownNow()\*** *方法试图立即销毁*ExecutorService*，但它不保证所有正在运行的线程会在同一时间停止:

```java
List<Runnable> notExecutedTasks = executorService.shutDownNow();
```

This method returns a list of tasks that are waiting to be processed. It is up to the developer to decide what to do with these tasks.

One good way to shut down the *ExecutorService* (which is also [recommended by Oracle](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ExecutorService.html)) is to use both of these methods combined with the ***awaitTermination()\*** method:

此方法返回等待处理的任务列表。由开发人员决定如何处理这些任务。

关闭*ExecutorService*(这也是[Oracle推荐的](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ExecutorService.html))的一个好方法是使用这两个方法结合*** awaitterminate()\*** *方法:

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

使用这种方法，*ExecutorService*将首先停止接受新任务，然后等待指定的时间，直到所有任务都完成。如果该时间到期，则立即停止执行。

## **5. The \*Future\* Interface**

The *submit()* and *invokeAll()* methods return an object or a collection of objects of type *Future*, which allows us to get the result of a task's execution or to check the task's status (is it running).

The *Future* interface provides a special blocking method *get()*, which returns an actual result of the *Callable* task's execution or *null* in the case of a *Runnable* task:

*submit()*和*invokeAll()*方法返回一个对象或一个类型为*Future*的对象集合，它允许我们获取任务的执行结果或检查任务的状态(它是否正在运行)。

*Future*接口提供了一个特殊的阻塞方法*get()*，它返回*Callable*任务的实际执行结果，或者在*Runnable*任务的情况下返回*null*:

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

在任务仍在运行时调用*get()*方法将导致执行阻塞，直到任务正确执行并且结果可用。

With very long blocking caused by the *get()* method, an application's performance can degrade. If the resulting data is not crucial, it is possible to avoid such a problem by using timeouts:

如果*get()*方法导致了很长时间的阻塞，应用程序的性能会降低。如果结果数据不是至关重要的，可以通过使用超时来避免这样的问题:

```java
String result = future.get(200, TimeUnit.MILLISECONDS);
```

If the execution period is longer than specified (in this case, 200 milliseconds), a *TimeoutException* will be thrown.

We can use the *isDone()* method to check if the assigned task already processed or not.

The *Future* interface also provides for canceling task execution with the *cancel()* method and checking the cancellation with the *isCancelled()* method:

如果执行周期比指定的时间长(在本例中为200毫秒)，将抛出一个*TimeoutException*。

我们可以使用*isDone()*方法检查所分配的任务是否已经处理。

*Future*接口还提供了使用*cancel()*方法取消任务执行，并使用*isCancelled()*方法检查取消:

```java
boolean canceled = future.cancel(true);
boolean isCancelled = future.isCancelled();
```

## **6. The \*ScheduledExecutorService\* Interface**

The *ScheduledExecutorService* runs tasks after some predefined delay and/or periodically.

Once again, the best way to instantiate a *ScheduledExecutorService* is to use the factory methods of the *Executors* class.

For this section, we use a *ScheduledExecutorService* with one thread:

ScheduledExecutorService*在预定义的延迟和/或周期性运行任务。

同样，实例化一个*ScheduledExecutorService*的最佳方法是使用*Executors*类的工厂方法。

在本节中，我们使用一个*ScheduledExecutorService*和一个线程:

```java
ScheduledExecutorService executorService = Executors
  .newSingleThreadScheduledExecutor();
```

To schedule a single task's execution after a fixed delay, use the *scheduled()* method of the *ScheduledExecutorService*.

Two *scheduled()* methods allow you to execute *Runnable* or *Callable* tasks:

要在固定延迟后安排单个任务的执行，请使用*ScheduledExecutorService*的*scheduled()*方法。

两个*scheduled()*方法允许您执行*Runnable*或*Callable*任务:

```java
Future<String> resultFuture = 
  executorService.schedule(callableTask, 1, TimeUnit.SECONDS);
```



The *scheduleAtFixedRate()* method lets us run a task periodically after a fixed delay. The code above delays for one second before executing *callableTask*.

The following block of code will run a task after an initial delay of 100 milliseconds. And after that, it will run the same task every 450 milliseconds:

*scheduleAtFixedRate()*方法允许我们在固定延迟后定期运行任务。上面的代码在执行*callableTask*之前会延迟一秒钟。

下面的代码块将在初始延迟100毫秒后运行一个任务。之后，它会每450毫秒运行一次同样的任务:

```java
Future<String> resultFuture = service
  .scheduleAtFixedRate(runnableTask, 100, 450, TimeUnit.MILLISECONDS);
```

If the processor needs more time to run an assigned task than the *period* parameter of the *scheduleAtFixedRate()* method, the *ScheduledExecutorService* will wait until the current task is completed before starting the next.

If it is necessary to have a fixed length delay between iterations of the task, *scheduleWithFixedDelay()* should be used.

For example, the following code will guarantee a 150-millisecond pause between the end of the current execution and the start of another one:

如果处理器需要比*scheduleAtFixedRate()*方法的*period*参数更多的时间来运行分配的任务，则*ScheduledExecutorService*将等待当前任务完成后再开始下一个任务。

如果需要在任务的迭代之间有一个固定长度的延迟，则应该使用*scheduleWithFixedDelay()*。

例如，下面的代码将保证在当前执行结束和另一个执行开始之间有150毫秒的暂停:

```java
service.scheduleWithFixedDelay(task, 100, 150, TimeUnit.MILLISECONDS);
```

According to the *scheduleAtFixedRate()* and *scheduleWithFixedDelay()* method contracts, period execution of the task will end at the termination of the *ExecutorService* or if an exception is thrown during task execution*.*

根据*scheduleAtFixedRate()*和*scheduleWithFixedDelay()*方法契约，任务的周期执行将在*ExecutorService*终止时结束，或者在任务执行过程中抛出异常时结束

## **7. \*ExecutorService\* vs Fork/Join**

After the release of Java 7, many developers decided to replace the *ExecutorService* framework with the fork/join framework.

This is not always the right decision, however. Despite the simplicity and frequent performance gains associated with fork/join, it reduces developer control over concurrent execution.

*ExecutorService* gives the developer the ability to control the number of generated threads and the granularity of tasks that should be run by separate threads. The best use case for *ExecutorService* is the processing of independent tasks, such as transactions or requests according to the scheme “one thread for one task.”

在Java 7发布后，许多开发人员决定用fork/join框架取代*ExecutorService*框架。

然而，这并不总是正确的决定。尽管fork/join具有简单性和频繁的性能提升，但它减少了开发人员对并发执行的控制。

*ExecutorService*让开发者能够控制生成线程的数量和应该由独立线程运行的任务粒度。*ExecutorService*的最佳用例是处理独立的任务，比如按照“一个线程处理一个任务”的方案处理事务或请求。

In contrast, [according to Oracle's documentation](https://docs.oracle.com/javase/tutorial/essential/concurrency/forkjoin.html), fork/join was designed to speed up work that can be broken into smaller pieces recursively.

相比之下，[根据Oracle的文档](https://docs.oracle.com/javase/tutorial/essential/concurrency/forkjoin.html)， fork/join的设计是为了加速递归分解成更小部分的工作。

## **8. Conclusion**

Despite the relative simplicity of *ExecutorService*, there are a few common pitfalls.

Let's summarize them:

**Keeping an unused \*ExecutorService\* alive**: See the detailed explanation in Section 4 on how to shut down an *ExecutorService*.

**Wrong thread-pool capacity while using fixed length thread pool**: It is very important to determine how many threads the application will need to run tasks efficiently. A too-large thread pool will cause unnecessary overhead just to create threads that will mostly be in the waiting mode. Too few can make an application seem unresponsive because of long waiting periods for tasks in the queue.

**Calling a \*Future\*‘s \*get()\* method after task cancellation**: Attempting to get the result of an already canceled task triggers a *CancellationException*.

**Unexpectedly long blocking with \*Future\*‘s \*get()\* method**: We should use timeouts to avoid unexpected waits.

尽管*ExecutorService*相对简单，但仍然存在一些常见的缺陷。

让我们总结一下:

**保持一个未使用的\*ExecutorService\*活着**:参见第4节关于如何关闭一个*ExecutorService*的详细说明。

**使用固定长度的线程池时错误的线程池容量**:确定应用程序有效运行任务需要多少线程是非常重要的。一个太大的线程池将导致不必要的开销，仅仅是为了创建大部分处于等待模式的线程。太少会使应用程序看起来没有响应，因为队列中任务的等待时间过长。

在任务取消后调用一个\*Future\*的\*get()\*方法:尝试获取一个已经取消的任务的结果会触发一个*CancellationException*。

使用\*Future\*的\*get()\*方法**意外长阻塞:我们应该使用超时来避免意外等待。