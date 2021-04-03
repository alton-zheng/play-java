# ThreadPoolTaskExecutor corePoolSize vs. maxPoolSize

## 1. Overview

The Spring [*ThreadPoolTaskExecutor*](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/scheduling/concurrent/ThreadPoolTaskExecutor.html) is a JavaBean that provides an abstraction around a [*java.util.concurrent.ThreadPoolExecutor*](https://www.baeldung.com/java-executor-service-tutorial) instance and exposes it as a Spring [*org.springframework.core.task.TaskExecutor*](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/core/task/TaskExecutor.html). Further, it is highly configurable through the properties of *corePoolSize, maxPoolSize, queueCapacity, allowCoreThreadTimeOut* and *keepAliveSeconds.* In this tutorial, we'll look at the *corePoolSize* and *maxPoolSize* properties.

春天(* ThreadPoolTaskExecutor *) (https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/scheduling/concurrent/ThreadPoolTaskExecutor.html)是一个JavaBean,周围提供一个抽象(* java.util.concurrent.ThreadPoolExecutor *) (https://www.baeldung.com/java-executor-service-tutorial)实例,并将其公开为一个春天(* org.springframework.core.task.TaskExecutor *) (https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/core/task/TaskExecutor.html)。此外，它是高度可配置的属性*corePoolSize, maxPoolSize, queueCapacity, allowCoreThreadTimeOut*和* keepalivesseconds。在本教程中，我们将看看*corePoolSize*和*maxPoolSize*属性。

## 2. *corePoolSize* vs. *maxPoolSize*

Users new to this abstraction may easily get confused about the difference in the two configuration properties. Therefore, let's look at each independently.

 刚接触这种抽象的用户很容易对这两种配置属性的不同感到困惑。因此，让我们分别来看看它们。

### 2.1. *corePoolSize*

The ***corePoolSize\* is the minimum number of workers to keep alive** without timing out. It is a configurable property of *ThreadPoolTaskExecutor*. However, the *ThreadPoolTaskExecutor* abstraction delegates setting this value to the underlying *java.util.concurrent.ThreadPoolExecutor**.* To clarify, all threads may time out — effectively setting the value of *corePoolSize* to zero if we've set *allowCoreThreadTimeOut* to *true*.

***corePoolSize\*是在不超时的情况下保持活动的最小工作者数量。它是*ThreadPoolTaskExecutor*的可配置属性。但是，*ThreadPoolTaskExecutor*抽象将该值的设置委托给底层的*java.util.concurrent.ThreadPoolExecutor**。*为了澄清，所有的线程可能会超时-如果我们设置了*allowCoreThreadTimeOut*为*true*，那么有效地将*corePoolSize*的值设置为0。

### 2.2. *maxPoolSize*

In contrast, the ***maxPoolSize\* defines the maximum number of threads that can ever be created**. Similarly, the *maxPoolSize* property of *ThreadPoolTaskExecutor* also delegates its value to the underlying *java.util.concurrent.ThreadPoolExecutor*. To clarify, ***maxPoolSize\* depends on \*queueCapacity\*** in that *ThreadPoolTaskExecutor* will only create a new thread if the number of items in its queue exceeds *queueCapacity*.

相反，***maxPoolSize\*定义了可以创建的最大线程数**。类似地，*ThreadPoolTaskExecutor*的*maxPoolSize*属性也将其值委托给底层的*java.util.concurrent.ThreadPoolExecutor*。为了澄清，***maxPoolSize **依赖于** queueCapacity* **，因为*ThreadPoolTaskExecutor*只有在其队列中的项目数量超过*queueCapacity*时才会创建一个新线程。

## 3. So What's the Difference?

The difference between *corePoolSize* and *maxPoolSize* may seem evident. However, there are some subtleties regarding their behavior.

When we submit a new task to the *ThreadPoolTaskExecutor,* it creates a new thread if fewer than *corePoolSize* threads are running, even if there are idle threads in the pool, or if fewer than *maxPoolSize* threads are running and the queue defined by *queueCapacity* is full.

Next, let's look at some code to see examples of when each property springs into action.

*corePoolSize*和*maxPoolSize*之间的区别似乎很明显。然而，它们的行为也有一些微妙之处。

当我们提交一个新的任务* ThreadPoolTaskExecutor, *它创建一个新的线程如果少于* corePoolSize *线程正在运行,即使有空闲线程池中,或者如果少于* maxPoolSize *线程正在运行和* queueCapacity *定义的队列已满。

接下来，让我们看一些代码，看看每个属性何时启动的示例。

## 4. Examples

Firstly, let's say we have a method that executes new threads, from the *ThreadPoolTaskExecutor*, named *startThreads*:

首先，假设我们有一个执行新线程的方法，从*ThreadPoolTaskExecutor*，命名为*startThreads*:

```java
public void startThreads(ThreadPoolTaskExecutor taskExecutor, CountDownLatch countDownLatch, 
  int numThreads) {
    for (int i = 0; i < numThreads; i++) {
        taskExecutor.execute(() -> {
            try {
                Thread.sleep(100L * ThreadLocalRandom.current().nextLong(1, 10));
                countDownLatch.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
}
```

Let's test the default configuration of *ThreadPoolTaskExecutor*, which defines a *corePoolSize* of one thread, an unbounded *maxPoolSize,* and an unbounded *queueCapacity*. As a result, we expect that no matter how many tasks we start, we'll only have one thread running:

让我们测试*ThreadPoolTaskExecutor*的默认配置，它定义了一个线程的*corePoolSize*，一个不绑定的*maxPoolSize，*和一个不绑定的*queueCapacity*。因此，我们期望无论启动多少个任务，都只运行一个线程:

```java
@Test
public void whenUsingDefaults_thenSingleThread() {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.afterPropertiesSet();

    CountDownLatch countDownLatch = new CountDownLatch(10);
    this.startThreads(taskExecutor, countDownLatch, 10);

    while (countDownLatch.getCount() > 0) {
        Assert.assertEquals(1, taskExecutor.getPoolSize());
    }
}
```

Now, let's alter the *corePoolSize* to a max of five threads and ensure it behaves as advertised. As a result, we expect five threads to be started no matter the number of tasks submitted to the *ThreadPoolTaskExecutor*:

现在，让我们将*corePoolSize*更改为最多5个线程，并确保它的行为与宣传的一样。因此，无论提交给*ThreadPoolTaskExecutor*的任务数量是多少，我们都希望启动5个线程:

```java
@Test
public void whenCorePoolSizeFive_thenFiveThreads() {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setCorePoolSize(5);
    taskExecutor.afterPropertiesSet();

    CountDownLatch countDownLatch = new CountDownLatch(10);
    this.startThreads(taskExecutor, countDownLatch, 10);

    while (countDownLatch.getCount() > 0) {
        Assert.assertEquals(5, taskExecutor.getPoolSize());
    }
}
```

Similarly, we can increment the *maxPoolSize* to ten while leaving the *corePoolSize* at five. As a result, we expect to start only five threads. To clarify, only five threads start because the *queueCapacity* is still unbounded:

类似地，我们可以将*maxPoolSize*增加到10，而将*corePoolSize*保留为5。因此，我们希望只启动5个线程。需要澄清的是，只有5个线程启动，因为* queueccapacity *仍然是无限制的:

```java
@Test
public void whenCorePoolSizeFiveAndMaxPoolSizeTen_thenFiveThreads() {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setCorePoolSize(5);
    taskExecutor.setMaxPoolSize(10);
    taskExecutor.afterPropertiesSet();

    CountDownLatch countDownLatch = new CountDownLatch(10);
    this.startThreads(taskExecutor, countDownLatch, 10);

    while (countDownLatch.getCount() > 0) {
        Assert.assertEquals(5, taskExecutor.getPoolSize());
    }
}
```

Further, we'll now repeat the previous test but increment the *queueCapacity* to ten and start twenty threads. Therefore, we now expect to start ten threads in total:

此外，我们现在将重复前面的测试，但将* queueccapacity *增加到10，并启动20个线程。因此，我们现在希望总共启动10个线程:

```java
@Test
public void whenCorePoolSizeFiveAndMaxPoolSizeTenAndQueueCapacityTen_thenTenThreads() {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setCorePoolSize(5);
    taskExecutor.setMaxPoolSize(10);
    taskExecutor.setQueueCapacity(10);
    taskExecutor.afterPropertiesSet();

    CountDownLatch countDownLatch = new CountDownLatch(20);
    this.startThreads(taskExecutor, countDownLatch, 20);

    while (countDownLatch.getCount() > 0) {
        Assert.assertEquals(10, taskExecutor.getPoolSize());
    }
}
```

Likewise, if we had set the *queueCapactity* to zero and only started ten tasks, we'd also have ten threads in our *ThreadPoolTaskExecutor*.

同样，如果我们将*queueCapactity*设置为0，并且只启动10个任务，那么我们的*ThreadPoolTaskExecutor*中也会有10个线程。

## 5. Conclusion

*ThreadPoolTaskExecutor* is a powerful abstraction around a *java.util.concurrent.ThreadPoolExecutor*, providing options for configuring the *corePoolSize*, *maxPoolSize*, and *queueCapacity*. In this tutorial, we looked at the *corePoolSize* and *maxPoolSize* properties, as well as how *maxPoolSize* works in tandem with *queueCapacity*, allowing us to easily create thread pools for any use case.

ThreadPoolTaskExecutor是一个围绕java.util.concurrent. executor的强大抽象。ThreadPoolExecutor*，提供了配置*corePoolSize*， *maxPoolSize*和*queueCapacity*的选项。在本教程中，我们查看了*corePoolSize*和*maxPoolSize*属性，以及*maxPoolSize*和*queueCapacity*是如何协同工作的，这使得我们可以轻松地为任何用例创建线程池。
