# LinkedBlockingQueue vs ConcurrentLinkedQueue

## 1. Introduction

***LinkedBlockingQueue\* and \*ConcurrentLinkedQueue\* are the two most frequently used concurrent queues in Java**. Although both queues are often used as a concurrent data structure, there are subtle characteristics and behavioral differences between them.

In this short tutorial, we'll discuss both of these queues and explain their similarities and differences.

***LinkedBlockingQueue\*和\*ConcurrentLinkedQueue\*是Java**中最常用的两个并发队列。尽管这两个队列经常被用作并发数据结构，但它们之间存在细微的特征和行为差异。

在这个简短的教程中，我们将讨论这两个队列，并解释它们的异同。

## 2. *LinkedBlockingQueue*

The *LinkedBlockingQueue* **is an \*optionally-bounded\* blocking queue implementation,** meaning that the queue size can be specified if needed.

Let's create a *LinkedBlockingQueue* which can contain up to 100 elements:

LinkedBlockingQueue* **是一个\*可选绑定的\*阻塞队列实现，意味着如果需要，可以指定队列大小。

让我们创建一个*LinkedBlockingQueue*，它最多可以包含100个元素:

```java
BlockingQueue<Integer> boundedQueue = new LinkedBlockingQueue<>(100);
```

We can also create an unbounded *LinkedBlockingQueue* just by not specifying the size:

我们也可以通过不指定大小来创建一个不受限制的*LinkedBlockingQueue*:

```java
BlockingQueue<Integer> unboundedQueue = new LinkedBlockingQueue<>();
```

An unbounded queue implies that the size of the queue is not specified while creating. Therefore, the queue can grow dynamically as elements are added to it. However, if there is no memory left, then the queue throws a *java.lang.OutOfMemoryError.*

We can create a *LinkedBlockingQueue* from an existing collection as well:

无界队列意味着在创建队列时没有指定队列的大小。因此，当向队列中添加元素时，该队列可以动态增长。但是，如果没有剩余内存，则队列抛出一个*java.lang.OutOfMemoryError.*

我们也可以从现有的集合中创建一个*LinkedBlockingQueue*:

```java
Collection<Integer> listOfNumbers = Arrays.asList(1,2,3,4,5);
BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(listOfNumbers);
```

The *LinkedBlockingQueue* class **implements the \*BlockingQueue\* interface, which provides the blocking nature to it**.

A blocking queue indicates that the queue blocks the accessing thread if it is full (when the queue is bounded) or becomes empty. If the queue is full, then adding a new element will block the accessing thread unless there is space available for the new element. Similarly, if the queue is empty, then accessing an element blocks the calling thread:

LinkedBlockingQueue类实现了\*BlockingQueue\*接口，该接口提供了阻塞特性。

阻塞队列表示如果队列满了(当队列有界时)或变成空，该队列就阻塞访问线程。如果队列已满，则添加新元素将阻塞访问线程，除非有空间可用于新元素。类似地，如果队列为空，则访问元素会阻塞调用线程:

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

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_0" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_0" width="300" height="250" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" sandbox="allow-forms allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts allow-top-navigation-by-user-activation" srcdoc="" data-google-container-id="5" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

In the above code snippet, we are accessing an empty queue. Therefore, the *take* method blocks the calling thread.

The blocking feature of the *LinkedBlockingQueue* is associated with some cost. This cost is because every *put* or the *take* operation is lock contended between the producer or the consumer threads. Therefore, in scenarios with many producers and consumers, *put* and take actions could be slower.

在上面的代码片段中，我们正在访问一个空队列。因此，*take*方法阻塞了调用线程。

*LinkedBlockingQueue*的阻塞特性与一些开销有关。这个代价是因为每个*put*或*take*操作在生产者线程或消费者线程之间都是锁竞争的。因此，在有许多生产者和消费者的情况下，“投入”和“采取”行动可能会更慢。

## 3. *ConcurrentLinkedQueue*

A *ConcurrentLinkedQueue* **is an unbounded, thread-safe, and non-blocking queue.**

Let's create an empty *ConcurrentLinkedQueue*:

ConcurrentLinkedQueue* **是一个无界的、线程安全的、非阻塞的队列

让我们创建一个空的*ConcurrentLinkedQueue*:

```java
ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();
```

We can create a *ConcurrentLinkedQueue* from an existing collection as well:

我们也可以从现有的集合中创建一个*ConcurrentLinkedQueue*:

```java
Collection<Integer> listOfNumbers = Arrays.asList(1,2,3,4,5);
ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<>(listOfNumbers);
```

Unlike a *LinkedBlockingQueue,* **a \*ConcurrentLinkedQueue\* is a non-blocking queue**. Thus, it does not block a thread once the queue is empty. Instead, it returns *null*. Since its unbounded, it'll throw a *java.lang.OutOfMemoryError* if there's no extra memory to add new elements.

Apart from being non-blocking, a *ConcurrentLinkedQueue* has additional functionality.

In any producer-consumer scenario, consumers will not content with producers; however, multiple producers will contend with one another:

与*LinkedBlockingQueue不同，* **a \*ConcurrentLinkedQueue\*是一个非阻塞队列**。因此，一旦队列为空，它就不会阻塞线程。相反，它返回*null*。因为它是无界的，所以它将抛出一个*java.lang。OutOfMemoryError*如果没有额外的内存来添加新元素。

除了非阻塞，一个*ConcurrentLinkedQueue*还有额外的功能。

在任何生产者-消费者情形下，消费者不会满足于生产者;然而，多个生产者将会相互竞争:

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

The first task, *offerTask*, adds an element to the queue, and the second task, *pollTask,* retrieve an element from the queue. The poll task additionally **checks the queue for an element first as \*ConcurrentLinkedQueue\* is non-blocking and can return a \*null\* value**.

第一个任务*offerTask*向队列中添加一个元素，第二个任务*pollTask *从队列中检索一个元素。此外，poll任务**首先检查元素的队列，因为\*ConcurrentLinkedQueue\*是非阻塞的，并且可以返回一个\*null\*值。

## 4. Similarities

Both *LinkedBlockingQueue* and the *ConcurrentLinkedQueue* are queue implementations and share some common characteristics. Let's discuss the similarities of these two queues:

1. Both **implements the \*Queue\* Interface**
2. They both **use linked nodes** to store their elements
3. Both **are suitable for concurrent access scenarios**

*LinkedBlockingQueue*和*ConcurrentLinkedQueue*都是队列实现，有一些共同的特征。让我们来讨论一下这两个队列的相似性:

1. 两个**都实现了\*Queue\*接口
2. 它们都**使用链接节点**存储元素
3.这两个**都适合并发访问场景**

## 5. Differences

| Feature               | LinkedBlockingQueue                                          | ConcurrentLinkedQueue                                        |
| --------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| **Blocking Nature**   | It is a blocking queue and implements the *BlockingQueue* interface | It is a non-blocking queue and does not implement the *BlockingQueue* interface |
| **Queue Size**        | It is an optionally bounded queue, which means there are provisions to define the queue size during creation | It is an unbounded queue, and there is no provision to specify the queue size during creation |
| **Locking Nature**    | It is **a lock-based queue**                                 | It is **a lock-free queue**                                  |
| **Algorithm**         | It implements **its locking based on \*two-lock queue\* algorithm** | It relies on the **Michael & Scott algorithm for non-blocking, lock-free queues** |
| **Implementation**    | In the *two-lock queue* algorithm mechanism, *LinkedBlockingQueue* uses two different locks – the *putLock* and the *takeLock*. The *put/take* operations uses the first lock type, and the *take/poll* operations use the other lock type | **It uses CAS (Compare-And-Swap**) for its operations        |
| **Blocking Behavior** | It is a blocking queue. So, it blocks the accessing threads when the queue is empty | It does not block the accessing thread when the queue is empty and returns *null* |

## 6. Conclusion

In this article, we learned about *LinkedBlockingQueue* and *ConcurrentLinkedQueue.*

First, we individually discussed these two queue implementations and some of their characteristics. Then, we saw the similarities between these two queue implementations. Finally, we explored the differences between these two queue implementations.

As always, the source code of the examples is available [over on GitHub](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-concurrency-collections).

在本文中，我们了解了*LinkedBlockingQueue*和*ConcurrentLinkedQueue.*

首先，我们分别讨论了这两种队列实现及其一些特征。然后，我们看到了这两种队列实现之间的相似性。最后，我们探讨了这两种队列实现之间的差异。

和往常一样，示例的源代码可以[在GitHub上](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-concurrency-collections)获得。