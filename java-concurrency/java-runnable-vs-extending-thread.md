# 实现 Runnable vs 继承 Thread

&nbsp;

## 1. 介绍

​		“我应该实现一个 *Runnable* 或继承 *Thread* 类”? 这是一个很常见的问题。在本文中，我们将看到哪种方法在实践中更有意义，以及为什么。

&nbsp;

## 2. 使用 Thread

首先定义一个继承了 Thread 的 *SimpleThread* 类:

```java
public class SimpleThread extends Thread {

    private String message;

    // standard logger, constructor

    @Override
    public void run() {
        log.info(message);
    }
}
```

运行这种类型的线程：

```java
@Test
public void givenAThread_whenRunIt_thenResult()
  throws Exception {
 
    Thread thread = new SimpleThread(
      "SimpleThread executed using Thread");
    thread.start();
    thread.join();
}
```

&nbsp;

我们也可以使用 *ExecutorService* 来执行线程:

```java
@Test
public void givenAThread_whenSubmitToES_thenResult()
  throws Exception {
    
    executorService.submit(new SimpleThread(
      "SimpleThread executed using ExecutorService")).get();
}
```

对于在单独的线程中运行单个日志操作来说，这是相当多的代码。

另外，请注意，$SimpleThread$ 不能扩展任何其他类，因为 Java 不支持多重继承。

&nbsp;

## 3. 实现 Runnable

现在，让我们创建一个实现 `java.lang.Runnable` 的简单任务 ：

```java
class SimpleRunnable implements Runnable {
	
    private String message;
	
    // standard logger, constructor
    
    @Override
    public void run() {
        log.info(message);
    }
}
```

上面的 *SimpleRunnable* 只是一个我们想在单独的线程中运行的任务。

有很多方法可以用来运行它; 其中一种是使用 *Thread* 类：

```java
@Test
public void givenRunnable_whenRunIt_thenResult()
 throws Exception {
    Thread thread = new Thread(new SimpleRunnable(
      "SimpleRunnable executed using Thread"));
    thread.start();
    thread.join();
}
```

&nbsp;

我们甚至可以使用 *ExecutorService* :

```java
@Test
public void givenARunnable_whenSubmitToES_thenResult()
 throws Exception {
    
    executorService.submit(new SimpleRunnable(
      "SimpleRunnable executed using ExecutorService")).get();
}
```

&nbsp;

我们可以在 [这里](java-executor-service-tutorial.md) 阅读更多关于 *ExecutorService* 的内容。

实现 Runnable 的同时，可以继承另一个父类。

从 Java 8 开始，任何公开单个抽象方法的接口都被视为函数接口，这使得它可以用 lambda表达式来表达。

我们可以用 lambda 表达式重写上面的 *Runnable* 代码：

```java
@Test
public void givenARunnableLambda_whenSubmitToES_thenResult() 
  throws Exception {
    
    executorService.submit(
      () -> log.info("Lambda runnable executed!"));
}
```

&nbsp;

## 4. Runnable 或 Thread?

简单地说，比起继承 Thread， 通常鼓励使用 *Runnable* :

- 当扩展 *Thread* 类时，我们不会覆盖它的任何方法。相反，我们覆盖了 *Runnable*  的方法(*Thread* 碰巧实现了*)*。这显然违反了 `IS-A` Thread 的原则。
- 创建一个 *Runnable* 的实现，并将它传递给 *Thread* 类使用复合而不是继承 - 这是更灵活的
- 在扩展 *Thread* 类之后，我们不能扩展任何其他类
- 从 Java 8 开始，Runnable 可以用 lambda 表达式表示

&nbsp;

## 5. 总结

在这篇快速教程中，我们看到了实现 *Runnable* 通常是比扩展 *Thread* 类更好的方法。

