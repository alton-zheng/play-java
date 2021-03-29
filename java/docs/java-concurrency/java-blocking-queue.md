# java.util.concurrent.BlockingQueue

&nbsp;

## 1. 概览

In this article, we will look at one of the most useful constructs *java.util.concurrent* to solve the concurrent producer-consumer problem. We'll look at an API of the *[BlockingQueue](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/BlockingQueue.html)* interface and how methods from that interface make writing concurrent programs easier.

Later in the article, we will show an example of a simple program that has multiple producer threads and multiple consumer threads.

在本文中，我们将研究最有用的结构之一*java.util。concurrent*解决并发的生产者-消费者问题。我们将研究*[BlockingQueue](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/BlockingQueue.html)*接口的API，以及该接口的方法如何使编写并发程序变得更容易。

在本文后面，我们将展示一个具有多个生产者线程和多个消费者线程的简单程序示例。

&nbsp;

## 2. BlockingQueue Types

We can distinguish two types of *BlockingQueue*:

- unbounded queue – can grow almost indefinitely

- bounded queue – with maximal capacity defined

- 我们可以区分两种类型的BlockingQueue*:

  -无界队列-几乎可以无限增长
  -有界队列-定义了最大容量

&nbsp;

### 2.1. Unbounded Queue

Creating unbounded queues is simple:

```java
BlockingQueue<String> blockingQueue = new LinkedBlockingDeque<>();
```

The Capacity of *blockingQueue* will be set to *Integer.MAX_VALUE.* All operations that add an element to the unbounded queue will never block, thus it could grow to a very large size.

The most important thing when designing a producer-consumer program using unbounded BlockingQueue is that consumers should be able to consume messages as quickly as producers are adding messages to the queue. Otherwise, the memory could fill up and we would get an *OutOfMemory* exception.

*blockingQueue*的容量将被设置为*Integer.MAX_VALUE。*所有向无界队列添加元素的操作都不会阻塞，因此它可能会增长到非常大的大小。

在使用无界BlockingQueue设计生产者-消费者程序时，最重要的一点是，消费者应该能够以生产者向队列中添加消息的速度消费消息。否则，内存可能被填满，我们将得到一个*OutOfMemory*异常。

&nbsp;

### 2.2. Bounded Queue

The second type of queues is the bounded queue. We can create such queues by passing the capacity as an argument to a constructor:

第二种类型的队列是有界队列。我们可以通过将容量作为参数传递给构造函数来创建这样的队列:

```java
BlockingQueue<String> blockingQueue = new LinkedBlockingDeque<>(10);
```

Here we have a *blockingQueue* that has a capacity equal to 10. It means that when a producer tries to add an element to an already full queue, depending on a method that was used to add it (*offer()*, *add()* or *put()*), it will block until space for inserting object becomes available. Otherwise, the operations will fail.

Using bounded queue is a good way to design concurrent programs because when we insert an element to an already full queue, that operations need to wait until consumers catch up and make some space available in the queue. It gives us throttling without any effort on our part.

这里我们有一个*blockingQueue*，它的容量等于10。这意味着当生产者试图将一个元素添加到一个已经满的队列中时，它将阻塞直到插入对象的空间可用。否则会导致操作失败。

使用有界队列是设计并发程序的好方法，因为当我们向已经满了的队列插入元素时，操作需要等待，直到消费者赶上来并在队列中留出一些可用空间。它让我们无需任何努力就可以进行节流。

&nbsp;

## 3. BlockingQueue API

There are two types of methods in the *BlockingQueue* interface *–* methods responsible for adding elements to a queue and methods that retrieve those elements. Each method from those two groups behaves differently in case the queue is full/empty.

在*BlockingQueue*接口* - *中有两种方法，一种是负责向队列添加元素的方法，另一种是负责检索这些元素的方法。在队列是满的/空的情况下，这两个组中的每个方法的行为是不同的。

&nbsp;

### **3.1. Adding Elements**

- *add() –* returns *true* if insertion was successful, otherwise throws an *IllegalStateException*
- *put() –* inserts the specified element into a queue, waiting for a free slot if necessary
- *offer() –* returns *true* if insertion was successful, otherwise *false*
- *offer(E e, long timeout, TimeUnit unit) –* tries to insert element into a queue and waits for an available slot within a specified timeout
- *add() - *如果插入成功，返回*true*，否则抛出*IllegalStateException*
- *put() - *将指定的元素插入到队列中，如果需要，等待一个空闲的槽位
- *offer() - *如果插入成功则返回*true*，否则返回*false*
- *offer(E E, long timeout, TimeUnit unit) - *尝试插入一个元素到队列中，并在指定的超时时间内等待一个可用的插槽

&nbsp;

### **3.2. Retrieving Elements**

- *take()* – waits for a head element of a queue and removes it. If the queue is empty, it blocks and waits for an element to become available
- *poll(long timeout, TimeUnit unit) –* retrieves and removes the head of the queue, waiting up to the specified wait time if necessary for an element to become available. Returns *null* after a timeout*
  *

These methods are the most important building blocks from *BlockingQueue* interface when building producer-consumer programs.

- *take()* -等待一个队列的头元素并删除它。如果队列为空，它将阻塞并等待一个元素变为可用
- *poll(long timeout, TimeUnit unit) - *获取并删除队列的头，如果需要，等待到指定的等待时间，以便元素可用。超时后返回*null*
*

在构建生产者-消费者程序时，这些方法是*BlockingQueue*接口中最重要的构建块。

&nbsp;

## **4. Multithreaded Producer-Consumer Example**

Let's create a program that consists of two parts – a Producer and a Consumer.

The Producer will be producing a random number from 0 to 100 and will put that number in a *BlockingQueue*. We'll have 4 producer threads and use the *put()* method to block until there's space available in the queue.

The important thing to remember is that we need to stop our consumer threads from waiting for an element to appear in a queue indefinitely.

A good technique to signal from producer to the consumer that there are no more messages to process is to send a special message called a poison pill. We need to send as many poison pills as we have consumers. Then when a consumer will take that special poison pill message from a queue, it will finish execution gracefully.

Let's look at a producer class:

让我们创建一个由两个部分组成的程序——生产者和消费者。

生产者将产生一个从0到100的随机数，并将该数字放入一个*BlockingQueue*中。我们将有4个producer线程，并使用*put()*方法阻塞，直到队列中有可用的空间。

需要记住的重要一点是，我们需要停止消费者线程无限期地等待一个元素出现在队列中。

从生产者向消费者发出没有更多信息需要处理的信号的一种好方法是发送一个被称为毒丸的特殊信息。有多少消费者，我们就送多少毒丸。然后，当消费者从队列中获取这个特殊的毒丸消息时，它将优雅地完成执行。

让我们来看看一个producer类:

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

Our producer constructor takes as an argument the *BlockingQueue* that is used to coordinate processing between the producer and the consumer. We see that method *generateNumbers()* will put 100 elements in a queue. It takes also poison pill message, to know what type of message must be put into a queue when the execution will be finished. That message needs to be put *poisonPillPerProducer* times into a queue.

Each consumer will take an element from a *BlockingQueue* using *take()* method so it will block until there is an element in a queue. After taking an *Integer* from a queue it checks if the message is a poison pill, if yes then execution of a thread is finished. Otherwise, it will print out the result on standard output along with current thread's name.

This will give us insight into inner workings of our consumers:

我们的生产者构造函数接受*BlockingQueue*作为参数，该参数用于协调生产者和消费者之间的处理。我们看到方法*generateNumbers()*将在一个队列中放入100个元素。还需要毒丸消息，以便知道在执行完成时必须将哪种类型的消息放入队列。需要将该消息放入队列中*poisonPillPerProducer*次。

每个消费者将使用*take()*方法从*BlockingQueue*中获取一个元素，因此它将阻塞，直到队列中有一个元素为止。在从队列中获取一个*Integer*后，它检查消息是否为毒丸，如果是，则线程的执行结束。否则，它将在标准输出上打印结果以及当前线程的名称。

这将使我们深入了解我们的消费者的内部工作:

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

The important thing to notice is the usage of a queue. Same as in the producer constructor, a queue is passed as an argument. We can do it because *BlockingQueue* can be shared between threads without any explicit synchronization.

Now that we have our producer and consumer, we can start our program. We need to define the queue's capacity, and we set it to 100 elements.

We want to have 4 producer threads and a number of consumers threads will be equal to the number of available processors:

需要注意的重要一点是队列的使用。与生产者构造函数中一样，队列作为参数传递。我们可以这样做，因为*BlockingQueue*可以在线程之间共享，而不需要任何显式的同步。

既然我们有了生产者和消费者，就可以开始程序了。我们需要定义队列的容量，并将其设置为100个元素。

我们希望有4个生产者线程，而消费者线程的数量将等于可用处理器的数量:

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

*BlockingQueue* is created using construct with a capacity. We're creating 4 producers and N consumers. We specify our poison pill message to be an *Integer.MAX_VALUE* because such value will never be sent by our producer under normal working conditions. The most important thing to notice here is that *BlockingQueue* is used to coordinate work between them.

When we run the program, 4 producer threads will be putting random *Integers* in a *BlockingQueue* and consumers will be taking those elements from the queue. Each thread will print to standard output the name of the thread together with a result.

*BlockingQueue*是使用带容量的构造创建的。我们创造了4个生产者和N个消费者。我们将毒丸消息指定为一个*整数。MAX_VALUE*因为在正常的工作条件下，这个值永远不会被我们的生产者发送。这里需要注意的最重要的一点是，*BlockingQueue*用于协调它们之间的工作。

当我们运行程序时，4个生产者线程将把随机的*整数*放入*BlockingQueue*中，而消费者将从队列中取出这些元素。每个线程将向标准输出输出线程的名称和结果。

&nbsp;

## **5. Conclusion**

This article shows a practical use of *BlockingQueue* and explains methods that are used to add and retrieve elements from it. Also, we've shown how to build a multithreaded producer-consumer program using *BlockingQueue* to coordinate work between producers and consumers.

The implementation of all these examples and code snippets can be found in the [GitHub project](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-concurrency-collections) – this is a Maven-based project, so it should be easy to import and run as it is.

本文展示了*BlockingQueue*的实际用途，并解释了用于添加和从中检索元素的方法。此外，我们还展示了如何使用*BlockingQueue*构建一个多线程的生产者-消费者程序来协调生产者和消费者之间的工作。

所有这些例子的实现和代码片段可以找到(GitHub项目)(https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-concurrency-collections)——这是一个Maven-based项目,所以它应该易于导入并运行。