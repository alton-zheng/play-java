# 用 Java 8 Parallel Stream 自定义线程池

&nbsp;

## 1. 概览

Java 8 引入了 Stream 的概念，作为对数据进行大容量操作的一种有效方式。在支持并发的环境中可以获得并行的*Stream*。

这些流可以带来更好的性能 - 以多线程开销为代价。

在这个快速教程中，我们将看看 Stream API 的最大限制之一，并看看如何使一个自定义的 *ThreadPool* 实例的并行流工作，或者 - [有一个库处理这个](https://github.com/pivovarit/parallel-collectors) .

&nbsp;

## 2. 并行 Stream

让我们从一个简单的例子开始 - 在任何 *Collection* 类型上调用 *parallelStream* 方法 - 它将返回一个可能并行的*Stream*:

```java
@Test
public void givenList_whenCallingParallelStream_shouldBeParallelStream(){
    List<Long> aList = new ArrayList<>();
    Stream<Long> parallelStream = aList.parallelStream();
        
    assertTrue(parallelStream.isParallel());
}
```

在这种流中发生的默认处理使用 *ForkJoinPool.commonPool()*，一个线程池被整个 application 共享。

&nbsp;

## 3. 自定义线程池

我们可以在处理 `stream` 的时候传递一个自定义的 `ThreadPool`

下面的例子让一个并行的 *Stream* 使用一个自定义的 *ThreadPool* 来计算长值的总和，从 $1$ 到 $1000,000$，包括:

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

我们使用了并行级别为 4 的 *ForkJoinPool* 构造函数。需要进行一些实验来确定不同环境的最佳值，但是一个好的经验法则是根据 CPU 的核数来选择这个数字。

接下来，我们处理并行 `Stream` 的内容，在 *reduce* 调用中汇总它们。

这个简单的例子可能无法充分展示使用自定义线程池的作用, 但好处变得明显在我们不想 tie-up 的情况下 长时间运行任务的 `common thread pool`,如从网络来源处理数据,或者 `common thread pool` 是由其他组件在应用程序中使用。

如果我们运行上面的测试方法，它将通过。到目前为止，一切顺利。

然而，如果我们像在 test 方法中那样在一个普通方法中实例化 *ForkJoinPool* 类，它可能会导致 *OutOfMemoryError*。

接下来，让我们仔细看看内存泄漏的原因。

&nbsp;

## 4. 当心 Memory Leak

如前所述，默认情况下，整个 application 都使用公共线程池。公共线程池是一个静态的线程池实例

因此，如果我们使用默认的线程池，就不会发生内存泄漏。

现在，让我们回顾一下我们的测试方法。在测试方法中，我们创建了一个 *ForkJoinPool* 对象。当测试方法完成时，customThreadPool 对象不会被解引用，也不会被垃圾回收——相反，它会等待新任务被分配。

也就是说，每次我们调用测试方法时，都会创建一个新的 *customThreadPool* 对象，并且它不会被释放。

修复的问题是相当简单的: 执行方法后， 关闭 customThreadPool 对象：

```java
try {
    long actualTotal = customThreadPool.submit(
      () -> aList.parallelStream().reduce(0L, Long::sum)).get();
    assertEquals((lastNum + firstNum) * lastNum / 2, actualTotal);
} finally {
    customThreadPool.shutdown();
}
```

&nbsp;

## 5. 总结

我们已经简要地了解了如何使用自定义的线程池运行一个并行的流。在适当的环境中，通过适当地使用并行级别，可以在某些情况下获得性能提高。

如果我们创建了一个自定义的线程池，我们应该记住调用它的 * shutdown()* 方法及时关闭线程池来避免内存泄漏。
