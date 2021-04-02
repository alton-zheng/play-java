# LinkedBlockingQueue vs ConcurrentLinkedQueue

## 1. 概述

*LinkedBlockingQueue* 和 *ConcurrentLinkedQueue* 是 Java 中最常用的两个并发 queue。尽管这两个队列经常被用作并发数据结构，但它们之间存在细微的特征和行为差异。

在这个简短的教程中，我们将讨论这两个队列，并解释它们的异同。

&nbsp;

## 2. *LinkedBlockingQueue*

*LinkedBlockingQueue* 是一个 *optional-bounded* 阻塞队列实现，意味着如果需要，可以指定队列大小。

让我们创建一个 *LinkedBlockingQueue*，它最多可以包含 100 个元素：

```java
BlockingQueue<Integer> boundedQueue = new LinkedBlockingQueue<>(100);
```

&nbsp;

我们也可以通过不指定大小来创建一个 unbounded *LinkedBlockingQueue*:

```java
BlockingQueue<Integer> unboundedQueue = new LinkedBlockingQueue<>();
```

unbounded queue 意味着在创建队列时没有指定队列的大小。因此，当向队列中添加元素时，该队列可以动态增长。但是，如果没有剩余内存，则队列抛出一个 *java.lang.OutOfMemoryError.*

&nbsp;

我们也可以从现有的集合中创建一个 *LinkedBlockingQueue*：

```java
Collection<Integer> listOfNumbers = Arrays.asList(1,2,3,4,5);
BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(listOfNumbers);
```

&nbsp;

LinkedBlockingQueue 类实现了 BlockingQueue 接口，该接口提供了阻塞特性。

blocking queue 表示如果队列满了(当队列 bounded)或变成 null，该队列就阻塞访问线程。如果队列已满，则添加新元素将阻塞访问线程，除非有空间可用于新元素。类似地，如果队列为 empty，则访问元素会阻塞调用线程：

```java
ExecutorService executorService = Executors.newFixedThreadPool(1);
LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<>();
executorService.submit(() -> {
  try {
    queue.take();
  } 
  catch (InterruptedException e) {
    // exception handling
  }
});
```

&nbsp;

在上面的代码片段中，我们正在访问一个空队列。因此，*take* 方法阻塞了调用线程。

*LinkedBlockingQueue* 的阻塞特性与一些开销有关。这个代价是因为每个 *put* 或 *take* 操作在 producer 线程或 consumer 线程之间都是锁竞争的。因此，在有许多 producer 和 consumer 的情况下， put 和 take 行动可能会更慢。

&nbsp;

## 3. *ConcurrentLinkedQueue*

$ConcurrentLinkedQueue$ 是一个无界的、线程安全的、非阻塞的队列

创建一个空的 *$ConcurrentLinkedQueue$*:

```java
ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();
```

&nbsp;

我们也可以从现有的集合中创建一个 $ConcurrentLinkedQueue$:

```java
Collection<Integer> listOfNumbers = Arrays.asList(1,2,3,4,5);
ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<>(listOfNumbers);
```

&nbsp;

与 *LinkedBlockingQueue* 不同，*ConcurrentLinkedQueue* 是一个 non-blocking queue。因此，一旦队列为空，它就不会阻塞线程。相反，它返回 *null*。因为它是 unbounded，如果没有足够的内存来添加新元素，它将抛出一个 $java.lang.OutOfMemoryError$。

除了 non-blocking，一个 *ConcurrentLinkedQueue* 还有额外的功能。

在任何 producer-consumer 情形下，consumer 不会满足于 producer；然而，多个 producer 将会相互竞争：

```java
int element = 1;
ExecutorService executorService = Executors.newFixedThreadPool(2);
ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<>();

Runnable offerTask = () -> queue.offer(element);

Callable<Integer> pollTask = () -> {
  while (queue.peek() != null) {
    return queue.poll().intValue();
  }
  return null;
};

executorService.submit(offerTask);
Future<Integer> returnedElement = executorService.submit(pollTask);
assertThat(returnedElement.get().intValue(), is(equalTo(element)));
```

第一个任务 *offerTask* 向队列中添加一个元素，第二个任务 *pollTask* 从队列中检索一个元素。此外，poll任务，首先检查元素的队列，因为 *ConcurrentLinkedQueue* 是非阻塞的，并且可以返回一个 *null* 值。

&nbsp;

## 4. 相似点

*LinkedBlockingQueue* 和 *ConcurrentLinkedQueue* 都是队列实现，有一些共同的特征。让我们来讨论一下这两个队列的相似性:

- 两个都实现了 *Queue* 接口
- 它们都**使用链表节点** 来存储元素
- 这两个**都适合并发访问场景**

&nbsp;

## 5. 不同点

| Feature               | LinkedBlockingQueue                                          | ConcurrentLinkedQueue                                        |
| --------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| **Blocking Nature**   | It is a blocking queue and implements the *BlockingQueue* interface | It is a non-blocking queue and does not implement the *BlockingQueue* interface |
| **Queue Size**        | It is an optionally bounded queue, which means there are provisions to define the queue size during creation | It is an unbounded queue, and there is no provision to specify the queue size during creation |
| **Locking Nature**    | It is **a lock-based queue**                                 | It is **a lock-free queue**                                  |
| **Algorithm**         | It implements **its locking based on \*two-lock queue\* algorithm** | It relies on the **Michael & Scott algorithm for non-blocking, lock-free queues** |
| **Implementation**    | In the *two-lock queue* algorithm mechanism, *LinkedBlockingQueue* uses two different locks – the *putLock* and the *takeLock*. The *put/take* operations uses the first lock type, and the *take/poll* operations use the other lock type | **It uses CAS (Compare-And-Swap**) for its operations        |
| **Blocking Behavior** | It is a blocking queue. So, it blocks the accessing threads when the queue is empty | It does not block the accessing thread when the queue is empty and returns *null* |

&nbsp;

## 6. 总结

在本文中，我们了解了 *LinkedBlockingQueue* 和 *ConcurrentLinkedQueue.*

首先，我们分别讨论了这两种队列实现及其一些特征。然后，我们看到了这两种队列实现之间的相似性。最后，我们探讨了这两种队列实现之间的差异。
