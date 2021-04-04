# ThreadPoolTaskExecutor corePoolSize vs. maxPoolSize

&nbsp;

## 1. 概览

Spring [*ThreadPoolTaskExecutor*](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/scheduling/concurrent/ThreadPoolTaskExecutor.html) 是一个 JavaBean ，周围提供一个抽象 [*java.util.concurrent.ThreadPoolExecutor*](https://www.baeldung.com/java-executor-service-tutorial) 实例，并将其公开为 Spring [*org.springframework.core.task.TaskExecutor*](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/core/task/TaskExecutor.html)。此外, 它有 *corePoolSize, maxPoolSize, queueCapacity, allowCoreThreadTimeOut* and *keepAliveSeconds* 这么多高度可配置的属性。在本篇文章中，我们将看看  corePoolSize 和 maxPoolSize 。

&nbsp;

## 2. *corePoolSize* vs. *maxPoolSize*

刚接触这种抽象的用户很容易对这两种配置属性的不同感到困惑。因此，让我们分别来看看它们。

&nbsp;

### 2.1. *corePoolSize*

**corePoolSize** 是在不超时的情况下保持 alive 的最小 worker 数量。它是 *ThreadPoolTaskExecutor* 的可配置属性。但是，*ThreadPoolTaskExecutor* 抽象将该值的设置委托给底层的 *java.util.concurrent.ThreadPoolExecutor*。为了澄清，所有的线程可能会超时 - 如果我们设置了*allowCoreThreadTimeOut* 为 *true*，那么有效地将 *corePoolSize* 的值设置为 $0$。

&nbsp;

### 2.2. *maxPoolSize*

相反，**maxPoolSize** 定义了可以创建的最大线程数。 类似地，*ThreadPoolTaskExecutor* 的 *maxPoolSize* 属性也将其值委托给底层的 *java.util.concurrent.ThreadPoolExecutor*。为了澄清，**maxPoolSize **依赖于 `queueCapacity`，因为 *ThreadPoolTaskExecutor* 只有在其队列中的项目数量超过 *queueCapacity* 时才会创建一个新线程。

&nbsp;

## 3. 区别?

*corePoolSize* 和 *maxPoolSize* 之间的区别似乎很明显。然而，它们的行为也有一些微妙之处。

当我们向 ThreadPoolTaskExecutor 提交一个新的任务时， 如果运行的线程少于 corePoolSize，即使池中有空闲线程， 或者运行的线程少于 maxPoolSize ， 且 queueCapacity 定义的队列已满， 它就会创建一个新 thread。

接下来，让我们看一些代码，看看每个属性何时启动的示例。

&nbsp;

## 4. 示例

首先，假设我们有一个执行新线程的方法，从 *ThreadPoolTaskExecutor*，命名为 *startThreads*:

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

让我们测试 *ThreadPoolTaskExecutor* 的默认配置，它定义了一个线程的 *corePoolSize*，一个无界的*maxPoolSize，*和一个无界的 *queueCapacity*。因此，我们期望无论启动多少个任务，都只运行一个线程：

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

现在，让我们将 *corePoolSize* 更改为最多5个线程，并确保它的行为与宣传的一样。因此，无论提交给*ThreadPoolTaskExecutor* 的任务数量是多少，我们都希望启动 5 个线程：

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

&nbsp;

类似地，我们可以将 *maxPoolSize* 增加到10，而将 *corePoolSize* 保留为5。因此，我们希望只启动5个线程。需要澄清的是，只有5个线程启动，因为 `queueCapacity` 仍然是无限制的:

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

&nbsp;

此外，我们现在将重复前面的测试，但将 `queueCapacity` 增加到10，并启动 20 个线程。因此，我们现在希望总共启动10个线程： 

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

同样，如果我们将 *queueCapactity* 设置为 0，并且只启动 10 个任务，那么我们的 *ThreadPoolTaskExecutor* 中也会有10个线程。

&nbsp;

## 5. 总结

ThreadPoolTaskExecutor 是一个围绕 java.util.concurrent.executor 的强大抽象。ThreadPoolExecutor*，提供了配置  *corePoolSize*, *maxPoolSize*, and *queueCapacity* 的选项。在本教程中，我们查看了 *corePoolSize* 和*maxPoolSize* 属性，以及 *maxPoolSize* 和 *queueCapacity* 是如何协同工作的，这使得我们可以轻松地为任何场景创建线程池。
