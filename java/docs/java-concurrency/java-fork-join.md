# Java Fork/Join Framework 指引

## 1. 概览

The fork/join framework was presented in Java 7. It provides tools to help speed up parallel processing by attempting to use all available processor cores – which is accomplished **through a divide and conquer approach**.

In practice, this means that **the framework first “forks”**, recursively breaking the task into smaller independent subtasks until they are simple enough to be executed asynchronously.

After that, **the “join” part begins**, in which results of all subtasks are recursively joined into a single result, or in the case of a task which returns void, the program simply waits until every subtask is executed.

To provide effective parallel execution, the fork/join framework uses a pool of threads called the *ForkJoinPool*, which manages worker threads of type *ForkJoinWorkerThread*.

fork/join框架是在Java 7中提出的。它提供了一些工具，通过尝试使用所有可用的处理器内核来加速并行处理——这是通过分而治之的方法实现的。

在实践中，这意味着**框架首先“分叉”**，递归地将任务分解成较小的独立子任务，直到它们足够简单，可以异步执行为止。

在此之后，**“join”部分开始于**，其中所有子任务的结果被递归地连接到一个单独的结果中，或者在返回void的任务的情况下，程序只是等待直到每个子任务执行完毕。

为了提供有效的并行执行，fork/join框架使用了一个名为*ForkJoinPool*的线程池，它管理类型为*ForkJoinWorkerThread*的工作线程。

&nbsp;

## **2. \*ForkJoinPool\***

The *ForkJoinPool* is the heart of the framework. It is an implementation of the *[ExecutorService](https://www.baeldung.com/java-executor-service-tutorial)* that manages worker threads and provides us with tools to get information about the thread pool state and performance.

Worker threads can execute only one task at a time, but the *ForkJoinPool* doesn’t create a separate thread for every single subtask. Instead, each thread in the pool has its own double-ended queue (or [deque](https://en.wikipedia.org/wiki/Double-ended_queue), pronounced *deck*) which stores tasks.

This architecture is vital for balancing the thread’s workload with the help of the **work-stealing algorithm.**

ForkJoinPool是框架的核心。它是*[ExecutorService](https://www.baeldung.com/java-executor-service-tutorial)*)的一个实现，它管理工作线程，并为我们提供了获取线程池状态和性能信息的工具。

Worker线程一次只能执行一个任务，但是*ForkJoinPool*不会为每个子任务创建单独的线程。相反，池中的每个线程都有自己的双端队列(或[deque](https://en.wikipedia.org/wiki/Double-ended_queue)，发音为*deck*)，用于存储任务。

在**工作窃取算法的帮助下，这个架构对于平衡线程的工作负载是至关重要的

### **2.1. Work Stealing Algorithm**

**Simply put – free threads try to “steal” work from deques of busy threads.**

By default, a worker thread gets tasks from the head of its own deque. When it is empty, the thread takes a task from the tail of the deque of another busy thread or from the global entry queue, since this is where the biggest pieces of work are likely to be located.

**Simply put - free threads try to " steal " work from deques of busy threads.**

默认情况下，工作线程从自己的deque容器的头部获取任务。当它为空时，线程从另一个繁忙线程的deque尾部或全局进入队列中获取任务，因为这可能是最大的工作块所在的位置。

This approach minimizes the possibility that threads will compete for tasks. It also reduces the number of times the thread will have to go looking for work, as it works on the biggest available chunks of work first.

这种方法最小化了线程竞争任务的可能性。它还减少了线程寻找工作的次数，因为它首先处理最大的可用工作块。

### **2.2. \*ForkJoinPool\*** **Instantiation**

In Java 8, the most convenient way to get access to the instance of the *ForkJoinPool* is to use its static method *[commonPool](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ForkJoinPool.html#commonPool--)().* As its name suggests, this will provide a reference to the common pool, which is a default thread pool for every *ForkJoinTask*.

According to [Oracle’s documentation](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ForkJoinPool.html), using the predefined common pool reduces resource consumption, since this discourages the creation of a separate thread pool per task.

在Java 8中,最方便的方式获得的实例* ForkJoinPool *是使用静态方法* (commonPool) (https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ForkJoinPool.html) commonPool——()。*顾名思义,这将提供一个参考常见的池,这是一个为每个* ForkJoinTask *默认线程池。

根据[Oracle的文档](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ForkJoinPool.html)，使用预定义的公共池可以减少资源消耗，因为这不鼓励为每个任务创建单独的线程池。

```java
ForkJoinPool commonPool = ForkJoinPool.commonPool();
```

The same behavior can be achieved in Java 7 by creating a *ForkJoinPool* and assigning it to a *public static* field of a utility class:

在Java 7中可以通过创建ForkJoinPool*并将其赋值给实用程序类的*公共静态*字段来实现相同的行为:

```java
public static ForkJoinPool forkJoinPool = new ForkJoinPool(2);
```

Now it can be easily accessed:

现在它可以很容易地访问:

```java
ForkJoinPool forkJoinPool = PoolUtil.forkJoinPool;
```

With *ForkJoinPool’s* constructors, it is possible to create a custom thread pool with a specific level of parallelism, thread factory, and exception handler. In the example above, the pool has a parallelism level of 2. This means that pool will use 2 processor cores.

使用ForkJoinPool的*构造函数，可以创建具有特定并行级别、线程工厂和异常处理程序的自定义线程池。在上面的示例中，池的并行级别为2。这意味着池将使用2个处理器核。

## **3. \*ForkJoinTask<V>\***

*ForkJoinTask* is the base type for tasks executed inside *ForkJoinPool.* In practice, one of its two subclasses should be extended: the *RecursiveAction* for *void* tasks and the *RecursiveTask<V>* for tasks that return a value. They both have an abstract method *compute()* in which the task’s logic is defined.

*ForkJoinTask*是在*ForkJoinPool中执行的任务的基类型。实际上，它的两个子类中的一个应该被扩展:对于void*任务的*RecursiveAction*和对于返回值的任务的* recursiveask <V>*。它们都有一个抽象方法*compute()*，在这个方法中定义了任务的逻辑。

### **3.1. \*RecursiveAction – an Example\***

In the example below, the unit of work to be processed is represented by a *String* called *workload*. For demonstration purposes, the task is a nonsensical one: it simply uppercases its input and logs it.

To demonstrate the forking behavior of the framework, **the example splits the task if \*workload\**.length()\* is larger than a specified threshold** using the *createSubtask()* method.

在下面的示例中，要处理的工作单元由一个称为*workload*的*字符串*表示。出于演示的目的，该任务是一个没有意义的任务:它只是将输入大写并记录它。

为了演示框架的分叉行为，这个例子使用*createSubtask()*方法在\*workload\**.length()\*大于指定阈值时拆分任务。

The String is recursively divided into substrings, creating *CustomRecursiveTask* instances which are based on these substrings.

As a result, the method returns a *List<CustomRecursiveAction>.*

The list is submitted to the *ForkJoinPool* using the *invokeAll()* method:

该字符串被递归地划分为子字符串，创建基于这些子字符串的*CustomRecursiveTask*实例。

结果，该方法返回一个*List<CustomRecursiveAction>.*

列表被提交给*ForkJoinPool*使用*invokeAll()*方法:

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

This pattern can be used to develop your own *RecursiveAction* classes*.* To do this, create an object which represents the total amount of work, chose a suitable threshold, define a method to divide the work, and define a method to do the work.

此模式可用于开发您自己的*RecursiveAction*类。*为此，创建一个代表总工作量的对象，选择一个合适的阈值，定义一个划分工作的方法，定义一个完成工作的方法。

### **3.2. \*RecursiveTask<V>\***

For tasks that return a value, the logic here is similar, except that the result for each subtask is united in a single result:

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

In this example, the work is represented by an array stored in the *arr* field of the *CustomRecursiveTask* class. The *createSubtasks()* method recursively divides the task into smaller pieces of work until each piece is smaller than the threshold*.* Then, the *invokeAll()* method submits the subtasks to the common pool and returns a list of *[Future](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Future.html)*.

To trigger execution, the *join()* method is called for each subtask.

In this example, this is accomplished using Java 8's *[Stream API](https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html);* the *sum()* method is used as a representation of combining sub results into the final result.

在本例中，工作由存储在*CustomRecursiveTask*类的*arr*字段中的数组表示。createSubtasks()*方法递归地将任务分成更小的工作块，直到每个工作块都小于阈值*。然后，*invokeAll()*方法将子任务提交到公共池，并返回一个*[Future]的列表(https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Future.html)*。

为了触发执行，每个子任务都会调用*join()*方法。

在本例中，这是使用Java 8的*[Stream API](https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html);*)完成的。*sum()*方法被用作将子结果组合为最终结果的表示。

## **4. Submitting Tasks to the \*ForkJoinPool\***

To submit tasks to the thread pool, few approaches can be used.

要向线程池提交任务，可以使用的方法很少。

The ***submit()\*** or ***execute()*** method (their use cases are the same):

***submit()\***或***execute()***方法(它们的用例相同):

```java
forkJoinPool.execute(customRecursiveTask);
int result = customRecursiveTask.join();
```

The ***invoke()*** method forks the task and waits for the result, and doesn’t need any manual joining:

***invoke()***方法fork任务并等待结果，不需要任何手动连接:

```java
int result = forkJoinPool.invoke(customRecursiveTask);
```

The ***invokeAll()\*** method is the most convenient way to submit a sequence of *ForkJoinTasks* to the *ForkJoinPool.* It takes tasks as parameters (two tasks, var args, or a collection), forks then returns a collection of *Future* objects in the order in which they were produced.

Alternatively, you can use separate ***fork()\* and \*join()\*** methods. The *fork()* method submits a task to a pool, but it doesn't trigger its execution. The *join()* method must be used for this purpose. In the case of *RecursiveAction*, the *join()* returns nothing but *null*; for *RecursiveTask<V>,* it returns the result of the task's execution:

***invokeAll()\*** *方法是向*ForkJoinPool提交*ForkJoinTasks序列最方便的方法。*它接受tasks作为参数(两个任务，var args，或一个集合)，然后fork返回一个*Future*对象的集合，该集合按照它们产生的顺序。

另外，你也可以使用单独的***fork()\*和** join()\***方法。fork()*方法将任务提交到池中，但不会触发它的执行。*join()*方法必须用于此目的。在*RecursiveAction*的情况下，*join()*只返回*null*;对于*RecursiveTask<V>，*它返回任务的执行结果:

```java
customRecursiveTaskFirst.fork();
result = customRecursiveTaskLast.join();
```

In our *RecursiveTask<V>* example we used the *invokeAll()* method to submit a sequence of subtasks to the pool. The same job can be done with *fork()* and *join()*, though this has consequences for the ordering of the results.

To avoid confusion, it is generally a good idea to use *invokeAll()* method to submit more than one task to the *ForkJoinPool.*

在我们的*RecursiveTask<V>*示例中，我们使用*invokeAll()*方法将一系列子任务提交到池中。同样的工作也可以用*fork()*和*join()*来完成，尽管这会影响结果的顺序。

为了避免混淆，最好使用*invokeAll()*方法向*ForkJoinPool.*提交多个任务

## **5. Conclusions**

Using the fork/join framework can speed up processing of large tasks, but to achieve this outcome, some guidelines should be followed:

- **Use as few thread pools as possible** – in most cases, the best decision is to use one thread pool per application or system
- **Use the default common thread pool,** if no specific tuning is needed
- **Use a reasonable threshold** for splitting *ForkJoinTask* into subtasks
- **Avoid any blocking in your** ***ForkJoinTasks\*

使用fork/join框架可以加快处理大型任务的速度，但要实现这个结果，需要遵循以下准则:

- **使用尽可能少的线程池** -在大多数情况下，最好的决策是每个应用程序或系统使用一个线程池
- **使用默认的公共线程池，**如果不需要特定的调优
- **使用合理的阈值**将*ForkJoinTask*拆分为子任务
- **避免任何阻塞您的** ***ForkJoinTasks\*