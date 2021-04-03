# Custom Thread Pools In Java 8 Parallel Streams

## **1. Overview**

Java 8 introduced the concept of S*treams* as an efficient way of carrying out bulk operations on data. And parallel *Streams* can be obtained in environments that support concurrency.

These streams can come with improved performance – at the cost of multi-threading overhead.

In this quick tutorial, we'll look at **one of the biggest limitations of \*Stream\* API** and see how to make a parallel stream work with a custom *ThreadPool* instance, alternatively – [there's a library that handles this](https://github.com/pivovarit/parallel-collectors).

Java 8引入了S流的概念，作为对数据进行大容量操作的一种有效方式。在支持并发的环境中可以获得并行的*Streams*。

这些流可以带来更好的性能-以多线程开销为代价。

在这个快速教程中，我们将看看\*Stream\* API**的最大限制之一，并看看如何使一个自定义的*ThreadPool*实例的并行流工作，或者-[有一个库处理这个](https://github.com/pivovarit/parallel-collectors).s

## **2. Parallel \*Stream\***

Let's start with a simple example – calling the *parallelStream* method on any of the *Collection* types – which will return a possibly parallel *Stream*:

让我们从一个简单的例子开始——在任何*Collection*类型上调用*parallelStream*方法——它将返回一个可能并行的*Stream*:

```java
@Test
public void givenList_whenCallingParallelStream_shouldBeParallelStream(){
    List<Long> aList = new ArrayList<>();
    Stream<Long> parallelStream = aList.parallelStream();
        
    assertTrue(parallelStream.isParallel());
}
```

The default processing that occurs in such a *Stream* uses the *ForkJoinPool.commonPool(),* **a thread pool shared by the entire application.**

在这种流中发生的默认处理使用*ForkJoinPool.commonPool()，* **一个由整个应用程序共享的线程池

## **3. Custom Thread Pool**

**We can actually pass a custom \*ThreadPool\* when processing the \*stream\*.**

The following example lets have a parallel *Stream* use a custom *ThreadPool* to calculate the sum of long values from 1 to 1,000,000, inclusive:

我们可以在处理流的时候传递一个自定义的线程池

下面的例子让一个并行的*Stream*使用一个自定义的*ThreadPool*来计算长值的总和，从1到1,000,000，包括:

```java
@Test
public void giveRangeOfLongs_whenSummedInParallel_shouldBeEqualToExpectedTotal() 
  throws InterruptedException, ExecutionException {
    
    long firstNum = 1;
    long lastNum = 1_000_000;

    List<Long> aList = LongStream.rangeClosed(firstNum, lastNum).boxed()
      .collect(Collectors.toList());

    ForkJoinPool customThreadPool = new ForkJoinPool(4);
    long actualTotal = customThreadPool.submit(
      () -> aList.parallelStream().reduce(0L, Long::sum)).get();
 
    assertEquals((lastNum + firstNum) * lastNum / 2, actualTotal);
}
```

We used the *ForkJoinPool* constructor with a parallelism level of 4. Some experimentation is required to determine the optimal value for different environments, but a good rule of thumb is simply choosing the number based on how many cores your CPU has.

Next, we processed the content of the parallel *Stream*, summing them up in the *reduce* call.

我们使用了并行级别为4的*ForkJoinPool*构造函数。需要进行一些实验来确定不同环境的最佳值，但是一个好的经验法则是根据CPU的核数来选择这个数字。

接下来，我们处理并行*流*的内容，在*reduce*调用中汇总它们。

This simple example may not demonstrate the full usefulness of using a custom thread pool, but the benefits become obvious in situations where we do not want to tie-up the common thread pool with long-running tasks – such as processing data from a network source – or the common thread pool is being used by other components within the application.

If we run the test method above, it'll pass. So far, so good.

However, if we instantiate *ForkJoinPool* class in a normal method in the same way as we do in the test method, it may lead to the *OutOfMemoryError*.

Next, let's take a closer look at the cause of the memory leak.

这个简单的例子可能无法充分展示使用自定义线程池的作用,但好处变得明显在我们不想合作的情况下常见的线程池长时间运行的任务,如处理数据从一个网络来源,或者常见的线程池是由其他组件在应用程序中使用。

如果我们运行上面的测试方法，它将通过。到目前为止，一切顺利。

然而，如果我们像在test方法中那样在一个普通方法中实例化*ForkJoinPool*类，它可能会导致*OutOfMemoryError*。

接下来，让我们仔细看看内存泄漏的原因。

## 4. Beware of the Memory Leak

As we've talked about earlier, the common thread pool is used by the entire application by default. **The common thread pool is a static \*ThreadPool\* instance.**

Therefore, no memory leak occurs if we use the default thread pool.

Now, let's review our test method. In the test method, we created an object of *ForkJoinPool.* When the test method is finished, **the \*customThreadPool\* object won't be dereferenced and garbage collected — instead, it will be waiting for new tasks to be assigned**.

That is to say, every time we call the test method, a new *customThreadPool* object will be created and it won't be released.

The fix to the problem is pretty simple: *shutdown* the *customThreadPool* object after we've executed the method:

如前所述，默认情况下，整个应用程序都使用公共线程池。公共线程池是一个静态的线程池实例

因此，如果我们使用默认的线程池，就不会发生内存泄漏。

现在，让我们回顾一下我们的测试方法。在测试方法中，我们创建了一个*ForkJoinPool对象。当测试方法完成时，customThreadPool对象不会被解引用，也不会被垃圾回收——相反，它会等待新任务被分配。

也就是说，每次我们调用测试方法时，都会创建一个新的*customThreadPool*对象，并且它不会被释放。

修复的问题是相当简单的:*关闭* *customThreadPool*对象后，我们已经执行方法:

```java
try {
    long actualTotal = customThreadPool.submit(
      () -> aList.parallelStream().reduce(0L, Long::sum)).get();
    assertEquals((lastNum + firstNum) * lastNum / 2, actualTotal);
} finally {
    customThreadPool.shutdown();
}
```

## **5. Conclusion**

We have briefly looked at how to run a parallel *Stream* using a custom *ThreadPool*. In the right environment and with the proper use of the parallelism level, performance gains can be had in certain situations.

If we create a custom *ThreadPool*, we should keep in mind to call its *shutdown()* method to avoid a memory leak.

我们已经简要地了解了如何使用自定义的线程池运行一个并行的流。在适当的环境中，通过适当地使用并行级别，可以在某些情况下获得性能提高。

如果我们创建了一个自定义的线程池，我们应该记住调用它的shutdown()*方法来避免内存泄漏。
