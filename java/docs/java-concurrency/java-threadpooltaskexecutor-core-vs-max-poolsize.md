# ThreadPoolTaskExecutor corePoolSize vs. maxPoolSize

Last modified: February 29, 2020

by [baeldung](https://www.baeldung.com/author/baeldung/)



- [Java](https://www.baeldung.com/category/java/)**+**

- [Java Concurrency](https://www.baeldung.com/tag/java-concurrency/)

### **Get started with Spring 5 and Spring Boot 2, through the \*Learn Spring\* course:**

**[>> CHECK OUT THE COURSE](https://www.baeldung.com/ls-course-start)**

## 1. Overview

The Spring [*ThreadPoolTaskExecutor*](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/scheduling/concurrent/ThreadPoolTaskExecutor.html) is a JavaBean that provides an abstraction around a [*java.util.concurrent.ThreadPoolExecutor*](https://www.baeldung.com/java-executor-service-tutorial) instance and exposes it as a Spring [*org.springframework.core.task.TaskExecutor*](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/core/task/TaskExecutor.html). Further, it is highly configurable through the properties of *corePoolSize, maxPoolSize, queueCapacity, allowCoreThreadTimeOut* and *keepAliveSeconds.* In this tutorial, we'll look at the *corePoolSize* and *maxPoolSize* properties.

## 2. *corePoolSize* vs. *maxPoolSize*

Users new to this abstraction may easily get confused about the difference in the two configuration properties. Therefore, let's look at each independently.

### 2.1. *corePoolSize*

The ***corePoolSize\* is the minimum number of workers to keep alive** without timing out. It is a configurable property of *ThreadPoolTaskExecutor*. However, the *ThreadPoolTaskExecutor* abstraction delegates setting this value to the underlying *java.util.concurrent.ThreadPoolExecutor**.* To clarify, all threads may time out — effectively setting the value of *corePoolSize* to zero if we've set *allowCoreThreadTimeOut* to *true*.

### 2.2. *maxPoolSize*

In contrast, the ***maxPoolSize\* defines the maximum number of threads that can ever be created**. Similarly, the *maxPoolSize* property of *ThreadPoolTaskExecutor* also delegates its value to the underlying *java.util.concurrent.ThreadPoolExecutor*. To clarify, ***maxPoolSize\* depends on \*queueCapacity\*** in that *ThreadPoolTaskExecutor* will only create a new thread if the number of items in its queue exceeds *queueCapacity*.

## 3. So What's the Difference?

The difference between *corePoolSize* and *maxPoolSize* may seem evident. However, there are some subtleties regarding their behavior.

When we submit a new task to the *ThreadPoolTaskExecutor,* it creates a new thread if fewer than *corePoolSize* threads are running, even if there are idle threads in the pool, or if fewer than *maxPoolSize* threads are running and the queue defined by *queueCapacity* is full.

Next, let's look at some code to see examples of when each property springs into action.

## 4. Examples

Firstly, let's say we have a method that executes new threads, from the *ThreadPoolTaskExecutor*, named *startThreads*:

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

## 5. Conclusion

*ThreadPoolTaskExecutor* is a powerful abstraction around a *java.util.concurrent.ThreadPoolExecutor*, providing options for configuring the *corePoolSize*, *maxPoolSize*, and *queueCapacity*. In this tutorial, we looked at the *corePoolSize* and *maxPoolSize* properties, as well as how *maxPoolSize* works in tandem with *queueCapacity*, allowing us to easily create thread pools for any use case.

As always, you can find the code available [over on Github](https://github.com/eugenp/tutorials/tree/master/spring-threads).