# java.util.concurrent.BlockingQueue

&nbsp;

## 1. 概览

在本文中，我们将研究最有用的结构之一 *java.util.concurrent* 解决并发的 producer-consumer 问题。我们将研究 *[BlockingQueue](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/BlockingQueue.html)* 接口的API，以及该接口的方法如何使编写并发程序变得更容易。

在本文后面，我们将展示一个具有多个 producer 线程和 多个 consumer 线程的简单程序示例。

&nbsp;

## 2. BlockingQueue 类型

我们可以区分两种类型的 BlockingQueue :

- unbounded queue - 几乎可以无限增长
- bounded queue - 定义了最大容量

&nbsp;

### 2.1. Unbounded Queue

创建一个简单的 `unbounded queue`：

```java
BlockingQueue<String> blockingQueue = new LinkedBlockingDeque<>();
```

*blockingQueue* 的容量将被设置为 *Integer.MAX_VALUE。* 所有向无界队列添加元素的操作都不会阻塞，因此它可能会增长到非常大的大小。

在使用  unbounded BlockingQueue 设计 producer-consumer 程序时，最重要的一点是，consumer 应该能够以 producer 向队列中添加消息的速度消费消息。否则，内存可能被填满，我们将得到一个 *OutOfMemory* 异常。

&nbsp;

### 2.2. Bounded Queue

第二种类型的队列是 `bounded queue`。我们可以通过将容量作为参数传递给构造函数来创建这样的队列:

```java
BlockingQueue<String> blockingQueue = new LinkedBlockingDeque<>(10);
```

这里我们有一个 *blockingQueue*，它的容量等于10。这意味着当 producer 试图将一个元素添加到一个已经满的队列中时，它将阻塞直到插入对象的空间可用。否则会导致操作失败。

使用 `bounded queue` 是设计并发程序的好方法，因为当我们向已经满了的队列插入元素时，操作需要等待，直到 comsumer 赶上来并在队列中留出一些可用空间。

&nbsp;

## 3. BlockingQueue API

在 *BlockingQueue* 接口中有两种方法，一种是负责向队列添加元素的方法，另一种是负责检索这些元素的方法。在队列是 full/empty 情况下，这两个组中的每个方法的行为是不同的。

&nbsp;

### 3.1. 添加元素

- *add() –* 如果插入成功，返回 *true*，否则抛出 *IllegalStateException*
- *put() –* 将指定的元素插入到 queue 中， 如果需要，等待一个空闲的 slot。
- *offer() –* 如果插入成功则返回 *true*，否则返回 *false*
- *offer(E e, long timeout, TimeUnit unit) –* 尝试插入一个元素到 queue 中， 并在指定的超时时间内等待一个可用的插槽。

&nbsp;

### 3.2. 获取元素

- *take()* – 获取一个队列的头元素并删除它。如果队列为空，它将阻塞并等待一个元素变为可用
- *poll(long timeout, TimeUnit unit) –* 获取并删除队列的头，如果需要，等待指定的时间，以便元素可用。超时后返回 $null$

在构建 producer-consumer 程序时，这些方法是 *BlockingQueue* 接口中最重要的构建块。

&nbsp;

## 4. 多线程 Producer-Consumer 示例

让我们创建一个由两个部分组成的程序 - Producer 和 Consumer。

Producer 将产生一个从 0 到 100 的随机数，并将该数字放入一个 *BlockingQueue* 中。我们将有 4 个 producer 线程，并使用 *put()* 方法阻塞，直到队列中有可用的空间。

需要记住的重要一点是，我们需要停止 consumer 线程无限期地等待一个元素出现在队列中。

从 producer 向 consumer 发出没有更多信息需要处理的信号的一种好方法是发送一个被称为 poison 的特殊信息。有多少 consumer ，我们就送多少 poison。然后，当 consumer 从队列中获取这个特殊的 `poison` 消息时，它将优雅地完成执行。

让我们来看看一个 producer 类:

```java
public class NumbersProducer implements Runnable {
    private BlockingQueue<Integer> numbersQueue;
    private final int poisonPill;
    private final int poisonPillPerProducer;
    
    public NumbersProducer(BlockingQueue<Integer> numbersQueue, int poisonPill, int poisonPillPerProducer) {
        this.numbersQueue = numbersQueue;
        this.poisonPill = poisonPill;
        this.poisonPillPerProducer = poisonPillPerProducer;
    }
    public void run() {
        try {
            generateNumbers();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private void generateNumbers() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            numbersQueue.put(ThreadLocalRandom.current().nextInt(100));
        }
        for (int j = 0; j < poisonPillPerProducer; j++) {
            numbersQueue.put(poisonPill);
        }
     }
}
```

我们的 producer 构造函数接受 *BlockingQueue* 作为参数，该参数用于协调 producer 和 consumer 之间的处理。我们看到方法 *generateNumbers()* 将在一个队列中放入 100 个元素。还需要 poison 消息，以便知道在执行完成时必须将哪种类型的消息放入队列。需要将该消息放入队列中 *poisonPillPerProducer* 次。

&nbsp;

每个 consuer 将使用 *take()* 方法从 *BlockingQueue* 中获取一个元素，因此它将阻塞，直到队列中有一个元素为止。在从队列中获取一个 *Integer* 后，它检查消息是否为 poison，如果是，则线程的执行结束。否则，它将在标准输出上打印结果以及当前 thread 的名称。

这将使我们深入了解我们的 consumer 的内部工作:

```java
public class NumbersConsumer implements Runnable {
    private BlockingQueue<Integer> queue;
    private final int poisonPill;
    
    public NumbersConsumer(BlockingQueue<Integer> queue, int poisonPill) {
        this.queue = queue;
        this.poisonPill = poisonPill;
    }
    public void run() {
        try {
            while (true) {
                Integer number = queue.take();
                if (number.equals(poisonPill)) {
                    return;
                }
                System.out.println(Thread.currentThread().getName() + " result: " + number);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

需要注意的重要一点是 $queue$ 的使用。与 producer 构造函数中一样，队列作为参数传递。我们可以这样做，因为 *$BlockingQueue$* 可以在线程之间共享，而不需要任何显式的同步。

既然我们有了 $producer$ 和 $consumer$ ，就可以开始程序了。我们需要定义 $queue$ 容量，并将其设置为 $100$ 个元素。

我们希望有 4 个生产者线程，而 $consumer$ 线程的数量将等于可用处理器的数量:

```java
int BOUND = 10;
int N_PRODUCERS = 4;
int N_CONSUMERS = Runtime.getRuntime().availableProcessors();
int poisonPill = Integer.MAX_VALUE;
int poisonPillPerProducer = N_CONSUMERS / N_PRODUCERS;
int mod = N_CONSUMERS % N_PRODUCERS;

BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(BOUND);

for (int i = 1; i < N_PRODUCERS; i++) {
    new Thread(new NumbersProducer(queue, poisonPill, poisonPillPerProducer)).start();
}

for (int j = 0; j < N_CONSUMERS; j++) {
    new Thread(new NumbersConsumer(queue, poisonPill)).start();
}

new Thread(new NumbersProducer(queue, poisonPill, poisonPillPerProducer + mod)).start();
```

&nbsp;

*BlockingQueue* 是使用带容量的构造创建的。我们创造了 $4$ 个 producer 和 N 个 consumer 。我们将 poison 消息指定为一个*整数。MAX_VALUE* 因为在正常的工作条件下，这个值永远不会被我们的 producer 发送。这里需要注意的最重要的一点是，*BlockingQueue* 用于协调它们之间的工作。

当我们运行程序时，$4$ 个 producer 线程将把随机的*整数*放入 *BlockingQueue* 中，而 consumer 将从 queue 中取出这些元素。每个 thread 将向标准输出输出 thread 的名称和结果。

&nbsp;

## 5. 总结

本文展示了 *BlockingQueue* 的实际用途，并解释了用于添加和从中获取元素的方法。此外，我们还展示了如何使用 *BlockingQueue* 构建一个多线程的 producer-consumer 程序来协调 producer 和 consumer 之间的工作。
