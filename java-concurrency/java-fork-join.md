# Java Fork/Join Framework 指引

## 1. 概览

*fork/join* 框架是在 Java 7 中提出的。它提供了一些工具，通过尝试使用所有可用的处理器内核来加速并行处理——这是通过分而治之的方法实现的。

在实践中，这意味着 **框架首先 “fork”**，递归地将任务分解成较小的独立子任务，直到它们足够简单，可以异步执行为止。

在此之后，“join” 部分开始，其中所有子任务的结果被递归地连接到一个单独的结果中，或者在返回 void 的任务的情况下，程序只是等待直到每个子任务执行完毕。

为了提供有效的并行执行，fork/join 框架使用了一个名为 *ForkJoinPool* 的线程池，它管理类型为 *ForkJoinWorkerThread* 的工作线程。

&nbsp;

## 2. ForkJoinPool

ForkJoinPool 是框架的核心。它是 *[ExecutorService](https://www.baeldung.com/java-executor-service-tutorial)* 的一个实现，它管理 worker 线程，并为我们提供了获取线程池状态和性能信息的工具。

Worker 线程一次只能执行一个任务，但是 *ForkJoinPool* 不会为每个子任务创建单独的线程。相反，池中的每个线程都有自己的 double-ended queue (或[deque](https://en.wikipedia.org/wiki/Double-ended_queue)，发音为 *deck*)，用于存储任务。

在 $Work-Stealing-Algorithm$ 的帮助下，这个架构对于平衡线程的工作负载是至关重要的

&nbsp;

### 2.1. Work Stealing Algorithm

简单的put - 空闲线程试图从繁忙线程的 deque “窃取” 工作。

默认情况下，工作线程从自己的 deque 容器的头部获取任务。当它为空时，线程从另一个繁忙线程的 deque 尾部或全局进入 queue 中获取任务，因为这可能是最大的工作块所在的位置。

这种方法最小化了线程竞争任务的可能性。它还减少了线程寻找 worker 的次数，因为它首先处理最大的可用工作块。

&nbsp;

### 2.2. ForkJoinPool 实例化

在 Java 8 中,最方便获得的 `ForkJoinPool` 实例的方法是使用静态方法 [commonPool()](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ForkJoinPool.html)  。顾名思义, 将提供一个 common pool 的引用。 这是为每个 *ForkJoinTask* 默认线程池。

根据 [Oracle的文档](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ForkJoinPool.html) ，使用预定义的公共池可以减少资源消耗，因为这不鼓励为每个任务创建单独的线程池。

```java
ForkJoinPool commonPool = ForkJoinPool.commonPool();
```

在 Java 7 中可以通过创建  *ForkJoinPool* 并将其赋值给实用程序类的 `public static` 字段来实现相同的行为:

```java
public static ForkJoinPool forkJoinPool = new ForkJoinPool(2);
```

&nbsp;

现在它可以很容易地访问:

```java
ForkJoinPool forkJoinPool = PoolUtil.forkJoinPool;
```

使用 $ForkJoinPool$ 的构造函数，可以创建具有特定并行级别、线程工厂和 exception handler 的自定义线程池。在上面的示例中，池的并行级别为 2 。这意味着池将使用2个 cpu core。

&nbsp;

## 3. $ForkJoinTask<V>$

*ForkJoinTask* 是在 *ForkJoinPool* 中执行的任务的基本类型。实际上，它的两个子类中的一个应该被继承：对于 void 任务的 *RecursiveAction* 和对于返回值的任务的 *RecursiveTask<V>*。它们都有一个抽象方法 *compute()* ，在这个方法里定义逻辑。

&nbsp;

### 3.1. RecursiveAction – 示例

在下面的示例中，要处理的工作单元由一个称为 *workload* 的*字符串*表示。出于演示的目的，该任务是一个没有意义的任务：它只是将输入大写并记录它。

为了演示框架的 fork 行为，这个例子在 `workload.length()` 大于指定阈值($THRESHOLD$)时，  使用 *createSubtask()* 方法来拆分任务。

该字符串被递归地划分为子字符串，创建基于这些子字符串的 *CustomRecursiveTask* 实例。

结果，该方法返回一个 *List<CustomRecursiveAction>*

使用 invokeAll() 方法将 List 提交给 *ForkJoinPool*：

```java
public class CustomRecursiveAction extends RecursiveAction {

    private String workload = "";
    private static final int THRESHOLD = 4;

    private static Logger logger = 
      Logger.getAnonymousLogger();

    public CustomRecursiveAction(String workload) {
        this.workload = workload;
    }

    @Override
    protected void compute() {
        if (workload.length() > THRESHOLD) {
            ForkJoinTask.invokeAll(createSubtasks());
        } else {
           processing(workload);
        }
    }

    private List<CustomRecursiveAction> createSubtasks() {
        List<CustomRecursiveAction> subtasks = new ArrayList<>();

        String partOne = workload.substring(0, workload.length() / 2);
        String partTwo = workload.substring(workload.length() / 2, workload.length());

        subtasks.add(new CustomRecursiveAction(partOne));
        subtasks.add(new CustomRecursiveAction(partTwo));

        return subtasks;
    }

    private void processing(String work) {
        String result = work.toUpperCase();
        logger.info("This result - (" + result + ") - was processed by " 
          + Thread.currentThread().getName());
    }
}
```

此 pattern 可用于开发自己的 *RecursiveAction* 类。为此，创建一个代表总工作量的对象，选择一个合适的阈值，定义一个划分工作的方法，定义一个完成工作的方法。

&nbsp;

### 3.2. $RecursiveTask<V>$

对于返回值的任务，这里的逻辑是类似的，除了每个子任务的结果被统一为一个结果:

```java
public class CustomRecursiveTask extends RecursiveTask<Integer> {
    private int[] arr;

    private static final int THRESHOLD = 20;

    public CustomRecursiveTask(int[] arr) {
        this.arr = arr;
    }

    @Override
    protected Integer compute() {
        if (arr.length > THRESHOLD) {
            return ForkJoinTask.invokeAll(createSubtasks())
              .stream()
              .mapToInt(ForkJoinTask::join)
              .sum();
        } else {
            return processing(arr);
        }
    }

    private Collection<CustomRecursiveTask> createSubtasks() {
        List<CustomRecursiveTask> dividedTasks = new ArrayList<>();
        dividedTasks.add(new CustomRecursiveTask(
          Arrays.copyOfRange(arr, 0, arr.length / 2)));
        dividedTasks.add(new CustomRecursiveTask(
          Arrays.copyOfRange(arr, arr.length / 2, arr.length)));
        return dividedTasks;
    }

    private Integer processing(int[] arr) {
        return Arrays.stream(arr)
          .filter(a -> a > 10 && a < 27)
          .map(a -> a * 10)
          .sum();
    }
}
```

在本例中，工作由存储在 *CustomRecursiveTask* 类的 *arr* 字段中的数组表示， *createSubtasks()* 方法递归地将任务分成更小的工作块，直到每个工作块都小于阈值。然后，*invokeAll()* 方法将子任务提交到公共池，并返回一个  *[Future](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Future.html)* 的列表。

为了触发执行，每个子任务都会调用 *join()* 方法。

在本例中，这是使用 Java 8 的*[Stream API](https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html)*) 完成的。*sum()* 方法用于将子结果组合为最终结果的表示。

&nbsp;

## 4. 提交任务到 ForkJoinPool

要向线程池提交任务，可以使用的方法很少。

 *submit()* 或 *execute()* 方法(它们的场景相同):

```java
forkJoinPool.execute(customRecursiveTask);
int result = customRecursiveTask.join();
```

&nbsp;

*invoke()* 方法 fork 任务并等待结果，不需要任何手动连接:

```java
int result = forkJoinPool.invoke(customRecursiveTask);
```

&nbsp;

*invokeAll()* 方法是向 *ForkJoinPool* 提交 *ForkJoinTask* 序列最方便的方法。它接受 task 作为参数(两个任务，var args，或一个 collection)，然后 fork 返回一个 *Future* 对象的集合，该集合按照它们产生的顺序。

另外，你也可以使用单独的 *fork()* 和 *join()* 方法。*fork()* 方法将任务提交到池中，但不会触发它的执行。 *join()* 方法必须用于此目的。在 *RecursiveAction* 的情况下，*join()* 只返回 *null* ;对于 *RecursiveTask<V>*，它返回任务的执行结果:

```java
customRecursiveTaskFirst.fork();
result = customRecursiveTaskLast.join();
```

在 *RecursiveTask<V>* 示例中，我们使用 *invokeAll()* 方法将一系列子任务提交到池中。同样的工作也可以用 *fork()* 和 *join()* 来完成，尽管这会影响结果的顺序。

为了避免混淆，最好使用 *invokeAll()* 方法向 *ForkJoinPool* 提交多个任务

&nbsp;

## 5. 小节

使用 fork/join 框架可以加快处理大型任务的速度，但要实现这个结果，需要遵循以下准则:

- **使用尽可能少的线程池** - 在大多数情况下，最好的决策是每个应用程序或系统使用一个线程池
- **使用默认的公共线程池，**如果不需要特定的调优
- **使用合理的阈值** 将*ForkJoinTask*拆分为子任务
- **避免任何阻塞您的** *ForkJoinTask* 事件