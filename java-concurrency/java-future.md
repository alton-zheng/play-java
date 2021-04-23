# java.util.concurrent.Future 指引

&nbsp;

## 1. 概述

在本文中，我们将学习 [Future](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Future.html)。自 Java 1.5 以来一直存在的接口，在处理异步调用和并发处理时非常有用。

&nbsp;

## 2. 创建 Future

简而言之，*Future* 类表示异步计算的将来结果 – 在处理完成之后，该结果最终将出现在 *Future* 中。

让我们看看如何编写创建和返回 *Future* 实例的方法。

长时间运行的方法非常适合异步处理和 *Future* 接口。这使我们能够在等待 *Future中*封装的任务完成时执行其他一些过程。

可以利用 *Future* 的异步特性的一些操作示例如下：

- 密集计算过程（数学和科学计算）
- 处理大数据结构（大数据）
- 远程方法调用（下载文件，HTML 抓取，Web 服务）

&nbsp;

### 2.1. 实现有 FutureTask 的 Future

对于我们的示例，我们将创建一个非常简单的类来计算 *Integer* 的平方。这绝对不适合 “long-running” 的方法类别，但是我们将对其进行 *Thread.sleep()* 调用，使其持续1秒才能完成：

```java
public class SquareCalculator {    
    
    private ExecutorService executor 
      = Executors.newSingleThreadExecutor();
    
    public Future<Integer> calculate(Integer input) {        
        return executor.submit(() -> {
            Thread.sleep(1000);
            return input * input;
        });
    }
}
```

&nbsp;

实际执行计算的代码段包含在 *call()* 方法中，以 lambda 表达式形式提供。如您所见，除了前面提到的 *sleep()* 调用外，没有什么特别的。

当我们将注意力集中在 *[Callable](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Callable.html)*  和 *[ExecutorService](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html)* 的用法上时，它会变得更加有趣。

*Callable* 是表示任务的接口，该接口返回结果并具有单个 *call()* 方法。在这里，我们使用 lambda 表达式创建它的实例。

创建 *Callable* 实例，我们仍然必须将此实例传递给 executor，该 executor 将负责在新线程中启动该任务并把宝贵的 *Future* 对象还给我们。那就是 *ExecutorService* 作用的地方。

有几种方法可以获取 *ExecutorService* 实例，其中大多数是由实用程序类 *[Executors](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executors.html)* 的静态工厂方法提供的。在此示例中，我们使用了基本的 *newSingleThreadExecutor()*，它为我们提供了一个 *ExecutorService*，它能够一次处理一个线程。

一旦有了 *ExecutorService* 对象，我们只需要调用 submit() 并传递 *Callable* 作为参数即可。$submit()$ 将负责启动任务并返回 *[FutureTask](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/FutureTask.html)*  对象，该对象是 *Future* 接口的实现。

&nbsp;

## 3. 消费 Future

到目前为止，已经学习了如何创建 *Future* 的实例。

在本节中，我们将通过探索 *Future* API 的所有方法来学习如何使用该实例。

&nbsp;

### 3.1. 使用 isDone() 和 get() 获取结果

现在，我们需要调用  *calculate()* 并使用返回的 *Future* 来获得结果 *Integer*。*Future* API 中的两种方法将帮助我们完成此任务。

*[Future.isDone()](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Future.html#isDone--)* 告诉我们执行者是否已完成任务的处理。如果任务完成，则返回 *$true$*，否则返回 *$false$*。

从计算中返回实际结果的方法是 *[Future.get()](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Future.html#get--)* 。请注意，该方法将阻塞执行直到任务完成为止，但是在我们的示例中，这不会成为问题，因为我们将通过调用 *isDone()* 首先检查任务是否完成。

通过使用这两种方法，我们可以在等待主要任务完成时运行其他代码：

```java
Future<Integer> future = new SquareCalculator().calculate(10);

while(!future.isDone()) {
    System.out.println("Calculating...");
    Thread.sleep(300);
}

Integer result = future.get();
```

&nbsp;

在这个例子中，我们在输出上写了一条简单的消息，让用户知道程序正在执行计算。

方法 *get()* 将 block 执行，直到任务完成。但是我们不必担心，因为示例仅在确保任务完成之后才到达调用 *get()*的 调用点。因此，在这种情况下，$future.get()$ 将始终立即返回。

值得一提的是，*get()* 具有一个重载版本，该版本需要一个 timeout 和一个 [*TimeUnit*](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/TimeUnit.html) 作为参数：

```java
Integer result = future.get(500, TimeUnit.MILLISECONDS);
```

*[get(long, TimeUnit)](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Future.html#get-long-java.util.concurrent.TimeUnit-)* 和 *[get()](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Future.html#get--)* 之间的区别在于如果任务在指定的超时期限之前未返回，则前者将抛出*[TimeoutException](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/TimeoutException.html)* 。

&nbsp;

### 3.2. 使用 Future 的 cannel() 方法取消执行

假设我们已经触发了任务，但是由于某种原因，我们不再关心结果了。我们可以使用 *[Future.cancel(boolean)](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Future.html#cancel-boolean-)* 告诉 executor 停止操作并中断其基础线程：

```java
Future<Integer> future = new SquareCalculator().calculate(4);
boolean canceled = future.cancel(true);
```

&nbsp;

上面代码中的 *Future* 实例永远无法完成其操作。实际上，如果尝试在调用 $cancel()$ 之后，从该实例调用 *get()*，结果将是 *[CancellationException](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CancellationException.html)*.  *[Future.isCancelled()](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Future.html#isCancelled--)*  会告诉我们 *Future* 是否已被取消。这对于避免出现*CancellationException* 非常有用。

对 cancel() 的调用可能失败。在这种情况下，其返回值将为 *false*。请注意，cancel() 将 boolean value 作为参数 –这控制执行此任务的线程是否应该被中断。

&nbsp;

## 4. 更多 Thread Pool 多线程

我们当前的 *ExecutorService* 是单线程的，因为它是通过  [Executors.newSingleThreadExecutor](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executors.html#newSingleThreadExecutor--) 获得的。为了突出显示这种 “单线程”，让我们同时触发两个计算：

```java
SquareCalculator squareCalculator = new SquareCalculator();

Future<Integer> future1 = squareCalculator.calculate(10);
Future<Integer> future2 = squareCalculator.calculate(100);

while (!(future1.isDone() && future2.isDone())) {
    System.out.println(
      String.format(
        "future1 is %s and future2 is %s", 
        future1.isDone() ? "done" : "not done", 
        future2.isDone() ? "done" : "not done"
      )
    );
    Thread.sleep(300);
}

Integer result1 = future1.get();
Integer result2 = future2.get();

System.out.println(result1 + " and " + result2);

squareCalculator.shutdown();
```



现在，让我们分析一下此代码的输出：

```java
calculating square for: 10
future1 is not done and future2 is not done
future1 is not done and future2 is not done
future1 is not done and future2 is not done
future1 is not done and future2 is not done
calculating square for: 100
future1 is done and future2 is not done
future1 is done and future2 is not done
future1 is done and future2 is not done
100 and 10000
```

显然，该过程不是并行的。请注意，第二个任务仅在第一个任务完成后才开始，因此整个过程大约需要 2 秒钟才能完成。

&nbsp;

为了使我们的程序真正成为多线程，我们应该使用另一种风格的 *ExecutorService* 。使用工厂方法 *[Executors.newFixedThreadPool()](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executors.html#newFixedThreadPool-int-)* 提供的线程池，示例的行为将如何改变：

```java
public class SquareCalculator {
 
    private ExecutorService executor = Executors.newFixedThreadPool(2);
    
    //...
}
```

&nbsp;

通过对 *SquareCalculator* 类进行简单的更改，现在我们有了一个执行程序，该执行程序可以同时使用2个线程。

如果再次运行完全相同的客户端代码，将得到以下输出：

```java
calculating square for: 10
calculating square for: 100
future1 is not done and future2 is not done
future1 is not done and future2 is not done
future1 is not done and future2 is not done
future1 is not done and future2 is not done
100 and 10000
```

This is looking much better now. Notice how the 2 tasks start and finish running simultaneously, and the whole process takes around 1 second to complete.

There are other factory methods that can be used to create thread pools, like *[Executors.newCachedThreadPool()](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executors.html#newCachedThreadPool--)* that reuses previously used *Thread*s when they are available, and *[Executors.newScheduledThreadPool()](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executors.html#newScheduledThreadPool-int-)* which schedules commands to run after a given delay*.*

For more information about *ExecutorService*, read our [article](https://www.baeldung.com/java-executor-service-tutorial) dedicated to the subject.

现在看起来好多了。请注意，这两个任务是如何同时开始和完成运行的，整个过程大约需要1秒钟才能完成。

还有其他工厂方法可用于创建线程池，例如 *[Executors.newCachedThreadPool()](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executors.html#newCachedThreadPool--)*  在可用的情况下重用以前使用的*Thread*，以及 *[Executors.newScheduledThreadPool()](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executors.html#newScheduledThreadPool-int-)*  安排命令在给定的延迟后运行*。*

有关 *ExecutorService* 的更多信息，请阅读我们专门针对该主题的 [java executor service 指引](java-executor-service-tutorial.md) 。

&nbsp;

## 5. ForkJoinTask 概览

*[ForkJoinTask](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ForkJoinTask.html)* 是一个抽象类，它实现 *Future* ，并且能够运行由 *[ForkJoinPool](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ForkJoinPool.html)*  中的少量实际线程托管的大量任务。

在本节中，我们将快速介绍 *ForkJoinPool* 的主要特征。有关该主题的全面指南，请查看 [《 Java中的Fork / Join框架指南》](java-fork-join.md)。

&nbsp;

Then the main characteristic of a *ForkJoinTask* is that it usually will spawn new subtasks as part of the work required to complete its main task. It generates new tasks by calling *[fork()](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ForkJoinTask.html#fork--)* and it gathers all results with *[join()](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ForkJoinTask.html#join--),* thus the name of the class.

There are two abstract classes that implement *ForkJoinTask*: *[RecursiveTask](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/RecursiveTask.html)* which returns a value upon completion, and *[RecursiveAction](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/RecursiveAction.html)* which doesn't return anything. As the names imply, those classes are to be used for recursive tasks, like for example file-system navigation or complex mathematical computation.

Let's expand our previous example to create a class that, given an *Integer*, will calculate the sum squares for all its factorial elements. So, for instance, if we pass the number 4 to our calculator, we should get the result from the sum of 4² + 3² + 2² + 1² which is 30.

First of all, we need to create a concrete implementation of *RecursiveTask* and implement its *compute()* method. This is where we'll write our business logic:

然后，*ForkJoinTask* 的主要特征是它通常会生成新的子任务，作为完成其主要任务所需工作的一部分。它通过调用 [fork()](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ForkJoinTask.html#fork--) *生成新任务，并使用 *[join()](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ForkJoinTask.html#join--)* 收集所有结果*，*因此是类的名称称为 $ForkJoin$。

有两个实现 *ForkJoinTask* 的抽象类：*[RecursiveTask](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/RecursiveTask.html)* 在完成时返回一个值，而 *[RecursiveAction](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/RecursiveAction.html)* 不返回任何值。顾名思义，这些类将用于递归任务，例如文件系统导航或复杂的数学计算。

让我们扩展前面的示例，以创建一个类，给定一个 *Integer*，该类将为其所有阶乘元素计算平方和。因此，例如，如果将数字 4 传递给我们的计算器，我们应该从 $4²+3²+2²+1²$ 的总和中得到 30 的结果。

首先，我们需要创建 *RecursiveTask* 的具体实现，并实现其 *compute()* 方法。这是我们编写业务逻辑的地方：

```java
public class FactorialSquareCalculator extends RecursiveTask<Integer> {
 
    private Integer n;

    public FactorialSquareCalculator(Integer n) {
        this.n = n;
    }

    @Override
    protected Integer compute() {
        if (n <= 1) {
            return n;
        }

        FactorialSquareCalculator calculator 
          = new FactorialSquareCalculator(n - 1);

        calculator.fork();

        return n * n + calculator.join();
    }
}
```

&nbsp;

注意，我们如何通过在 *compute()* 创建一个新的 *FactorialSquareCalculator* 实例来实现递归。通过调用非阻塞方法*fork()*，我们要求 *ForkJoinPool* 初始化该子任务的执行。

*join()* 方法从计算中返回目前正在访问数的平方结果。

现在我们只需要创建一个 *ForkJoinPool* 来处理执行和线程管理：

```java
ForkJoinPool forkJoinPool = new ForkJoinPool();

FactorialSquareCalculator calculator = new FactorialSquareCalculator(10);

forkJoinPool.execute(calculator);
```

&nbsp;

## **6. 小节**

在本文中，我们对 *Future* 接口进行了全面的介绍，并访问了其所有方法。我们还学习了如何利用线程池的功能来触发多个并行操作。还简要介绍了 *ForkJoinTask* 类的主要方法 *fork()* 和 *join()* 。

我们还有许多其他有关 Java 并行和异步操作的文章。这是与 *Future* 接口密切相关的三个（其中一些已在本文中提到）：

- [*CompletableFuture* 指南 ](java-completablefuture.md) – *Future*的实现，具有 Java 8 中引入的许多其他功能
- [Java Fork/Join 框架指南](java-fork-join.md) – 有关第5节中介绍的 *ForkJoinTask* 更多信息
- [Java *ExecutorService* 指南](java-executor-service-tutorial.md) – 专门用于*ExecutorService*接口

