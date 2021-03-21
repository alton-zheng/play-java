# Java  Synchronized 关键字

&nbsp;

## 1. 概览

这篇简短的文章将介绍如何在 Java 中使用 *synchronized* 块。

简单地说，在多线程环境中，当两个或多个线程试图同时更新可变共享数据时，就会发生 [竞争条件](race-conditions.md) 。Java提供了一种机制，通过 synchronize 对共享数据的线程访问来避免竞争条件。

标记为 *synchronized* 的一块逻辑成为一个 synchronized 块，**在任何给定时间只允许一个线程执行**。

&nbsp;

## 2. 为什么 Synchronization?

考虑一个典型的情况，我们用多线程执行这个 `calculate()` 方法来计算 sum 值：

```java
public class BaeldungSynchronizedMethods {

    private int sum = 0;

    public void calculate() {
        setSum(getSum() + 1);
    }

    // standard setters and getters
}
```

&nbsp;

写一个简单的测试： 

```java
@Test
public void givenMultiThread_whenNonSyncMethod() {
    ExecutorService service = Executors.newFixedThreadPool(3);
    BaeldungSynchronizedMethods summation = new BaeldungSynchronizedMethods();

    IntStream.range(0, 1000)
      .forEach(count -> service.submit(summation::calculate));
    service.awaitTermination(1000, TimeUnit.MILLISECONDS);

    assertEquals(1000, summation.getSum());
}
```

&nbsp;

我们只是使用一个线程数为 3 的线程池的 *ExecutorService* 来执行 *calculate()* 1000 次。

如果我们按顺序执行，预期输出将是1000，但是 **我们的多线程执行几乎每次都失败** 实际输出不一致，例如:

```shell
java.lang.AssertionError: expected:<1000> but was:<965>
at org.junit.Assert.fail(Assert.java:88)
at org.junit.Assert.failNotEquals(Assert.java:834)
...
```

这个结果当然是意料之中的。

避免竞争条件的一种简单方法是使用 *synchronized* 关键字使操作线程安全。

&nbsp;

## 3. Synchronized 关键字

关键字 *synchronized* 可以在不同的级别上使用:

- instance 方法
- static 方法
- code 块

当我们使用一个 *synchronized* 块时，Java内部使用一个 [monitor](monitor.md) ，也称为 monitor lock 或 intrinsic lock，来提供同步。这些 monitor 绑定到一个 object，因此同一 object 的所有 synchronized 块在同一时间只能有一个线程执行它们。

&nbsp;

### 3.1. Synchronized intance 方法

简单地在方法签名中添加一个 *synchronized* 关键字使得方法 *synchronized*: 

```java
public synchronized void synchronisedCalculate() {
    setSum(getSum() + 1);
}
```

&nbsp;

> 注意一旦我们使得方法 synchronize， 这个测试就会 pass, 实际输出为 1000：

```java
@Test
public void givenMultiThread_whenMethodSync() {
    ExecutorService service = Executors.newFixedThreadPool(3);
    SynchronizedMethods method = new SynchronizedMethods();

    IntStream.range(0, 1000)
      .forEach(count -> service.submit(method::synchronisedCalculate));
    service.awaitTermination(1000, TimeUnit.MILLISECONDS);

    assertEquals(1000, method.getSum());
}
```

实例方法在拥有该方法的类的实例上 *synchronized* 。这意味着该类的每个实例只有一个线程可以执行这个方法。

&nbsp;

### 3.2. Synchronized Static 方法

static 方法 *synchronized* 与处理 instance 方法是一样的，仅仅是一个 instance 方法 和 static 方法而已： 

```java
 public static synchronized void syncStaticCalculate() {
     staticSum = staticSum + 1;
 }
```

这些方法在与类相关联的类的 $Class$ 对象上是 `synchronized` 的，因为每个 JVM 每个类只有一个 $Class$ 对象，所以每个类的 static synchronized 方法中只有一个线程可以执行，不管它有多少个 instance。

让我们测试它, 同样会得到 1000 的结果:

```java
@Test
public void givenMultiThread_whenStaticSyncMethod() {
    ExecutorService service = Executors.newCachedThreadPool();

    IntStream.range(0, 1000)
      .forEach(count -> 
        service.submit(BaeldungSynchronizedMethods::syncStaticCalculate));
    service.awaitTermination(100, TimeUnit.MILLISECONDS);

    assertEquals(1000, BaeldungSynchronizedMethods.staticSum);
}
```

&nbsp;

### 3.3. 方法里的 Synchronized

有时我们不想同步整个方法，而只想同步其中的一些指令。这可以通过应用 *synchronized* 块来实现:

```java
public void performSynchronisedTask() {
    synchronized (this) {
        setCount(getCount()+1);
    }
}
```

&nbsp;

测试下这个代码：

```java
@Test
public void givenMultiThread_whenBlockSync() {
    ExecutorService service = Executors.newFixedThreadPool(3);
    BaeldungSynchronizedBlocks synchronizedBlocks = new BaeldungSynchronizedBlocks();

    IntStream.range(0, 1000)
      .forEach(count -> 
        service.submit(synchronizedBlocks::performSynchronisedTask));
    service.awaitTermination(100, TimeUnit.MILLISECONDS);

    assertEquals(1000, synchronizedBlocks.getCount());
}
```

注意，我们向 *synchronized* 块传递了一个参数 this。这是 monitor 对象，块内的代码在 monitor 对象上得到 `synchronized`。简单地说，每个 monitor 对象中只能有一个线程可以在代码块内执行。

&nbsp;

如果方法是 static 的，我们将传递类名来代替对象引用( == ClassName.class, 同修饰 static 方法的 synchronized 都是相同的含义)。并且这个类将是 block synchronized 的 monitor :

```java
public static void performStaticSyncTask(){
    synchronized (SynchronisedBlocks.class) {
        setStaticCount(getStaticCount() + 1);
    }
}
```

&nbsp;

测试下这个 static 方法中的 同步 code 块, 同样得到 1000 的结果： 

```java
@Test
public void givenMultiThread_whenStaticSyncBlock() {
    ExecutorService service = Executors.newCachedThreadPool();

    IntStream.range(0, 1000)
      .forEach(count -> 
        service.submit(BaeldungSynchronizedBlocks::performStaticSyncTask));
    service.awaitTermination(100, TimeUnit.MILLISECONDS);

    assertEquals(1000, BaeldungSynchronizedBlocks.getStaticCount());
}
```

&nbsp;

### 3.4. Reentrancy(可重入)

*synchronized* 方法和块后面的 lock 是可重入的。也就是说，当前线程可以在持有相同的 *synchronized* 锁的同时，反复获取相同的 *synchronized* 锁:

```java
Object lock = new Object();
synchronized (lock) {
    System.out.println("First time acquiring it");

    synchronized (lock) {
        System.out.println("Entering again");

         synchronized (lock) {
             System.out.println("And again");
         }
    }
}
```

如上所示，当我们处于 *synchronized* 块中时，我们可以重复地获取相同的 monitor 锁。

理论： 因为 `synchronized` 修饰的都是同一个对象。在这种 case 下的 lock 是可重入的。

&nbsp;

## 4. 深入知识，后续有机会深入探讨

>- `synchronized` 方法和非 `synchronized` 方法可以同时执行
>
>- synchronized 的 底层实现
>  - JDK 早期的 重量级 - OS
>  - 后来进行了改进
>    - e.g. synchronized(object)
>      - markdown 记录这个线程 id (偏向锁)
>      - 如果发生线程争用，升级为自旋锁
>      - 自旋超过设置的次数（默认为 10 次）， 升级为重量级锁，这时候的锁对应操作系统中的锁 1：1
>
>
>
>执行时间短（加锁代码），线程数少，用自旋
>
>执行时间长，线程数多，用系统锁
>
>&nbsp;
>
> synchronized(o)
>
>- 不能用 String 常量， Integer, Long
>- o 在使用的过程，不能被重新定义，不然并发会出现问题
>
>

&nbsp;

## **5. 总结**

在这篇快速文章中，我们看到了使用 *synchronized* 关键字来实现线程同步的不同方法。

我们还探讨了竞态条件如何影响我们的应用程序，以及同步如何帮助我们避免这种情况。有关在 Java 中使用锁的线程安全的更多信息，请参阅 [文章](java-concurrent-locks.md)。