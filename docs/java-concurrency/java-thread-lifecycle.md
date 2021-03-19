# Java 线程生命周期

&nbsp;

## **1. 简介**

在本文中，我们将详细讨论 Java 的核心概念-线程的生命周期。

我们将使用快速图解说明的图表，当然还有实用的代码片段，以更好地理解线程执行期间的状态变化。

为了开始理解 Java 中的线程，这篇关于创建线程的 [文章](java-runnable-vs-extending-thread.md) 是一个不错的起点。

&nbsp;

## **2. Java 中的多线程**

**在Java语言中，多线程由Thread的核心概念驱动**。在线程的生命周期中，它们会经历各种状态：



<img src="images/Life_cycle_of_a_Thread_in_Java.jpeg" alt="Life_cycle_of_a_Thread_in_Java" style="zoom:75%;" />

&nbsp;

## 3. Java中线程的生命周期

该 `java.lang.Thread` 的类包含一个 *static state enum* ， 它定义了线程的 6 种状态。在任何给定的时间点，线程只能处于以下状态之一：

- **`NEW`** – 尚未开始执行的新创建的线程

- **`RUNNABLE`** – `Running` 或处于正在等待资源分配的 `Ready to Run` 状态

- **`BLOCK`** - 等待获取 monitor lock 以 enter 或 re-enter 进入 synchronized block/method。

- **`WAITING`** - 等待其它线程执行特定操作， 且没有任何时间限制

- **`TIMED_WAITING`** - 具有指定等待时间的等待线程的线程状态。

- **TERMINATED** - 执行完成的状态

```java
public enum State {
        NEW,
        RUNNABLE,
        BLOCKED,
        WAITING,
        TIMED_WAITING,
        TERMINATED;
    }
```

所有这些状态均在上图中覆盖；现在让我们详细讨论每个。

&nbsp;

### **3.1. New**

 `NEW` Thread 或 `Born` Thread 指已创建，但尚未启动的线程。它保持这种状态，直到我们使用 `start()` 或 `run()` 方法启动它为止。

以下为 `NEW` 状态线程代码片段 ：

```java
Runnable runnable = new NewState();
Thread t = new Thread(runnable);
Log.info(t.getState());
```

由于我们尚未启动线程，此时线程的 `getState()` 就会打印 `NEW`: 

```plaintext
NEW
```

&nbsp;

### **3.2. Runnable**

当我们创建了一个新线程并在其上调用 `start()` 方法时，它已从 *`NEW`*  变为 *`RUNNABLE`*  状态。 Thread 此时处于 `Running` 或正在等待系统分配资源（CPU）的 `Ready to Run` 状态

在多线程环境中, Thread-Scheduler ( JVM 的一部分) 为每个线程分配固定的时间量。因此它会运行特定的时间，然后离开 CPU 进行等待队列，让给正在等待的处于 RUNNABLE 线程使用。

例如，在之前的代码中添加 `t.start()` 方法， 再看其当前状态： 

```java
Runnable runnable = new NewState();
Thread t = new Thread(runnable);
t.start();
Log.info(t.getState());
```

此代码**最有可能**返回以下输出：

```plaintext
RUNNABLE
```

&nbsp;

请注意，在此示例中，并不总是保证在我们执行 `t.getState()` 时，它仍处于 *`RUNNABLE`* 状态。

可能是 *Thread-Scheduler* 立即对其进行了调度，并可能完成了执行。在这种情况下，我们可能会得到不同的输出。

&nbsp;

### **3.3. Blocked**

当前不符合运行条件的线程处于 `BLOCKED` 状态。**当它等待 monitor lock 并尝试访问被某个其他线程锁定的一段代码时，它将进入 `BLOCKED` 状态。**

让我们尝试重现此状态：

```java
public class BlockedState {
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new DemoThreadB());
        Thread t2 = new Thread(new DemoThreadB());
        
        t1.start();
        t2.start();
        
        Thread.sleep(1000);
        
        Log.info(t2.getState());
        System.exit(0);
    }
}

class DemoThreadB implements Runnable {
    @Override
    public void run() {
        commonResource();
    }
    
    public static synchronized void commonResource() {
        while(true) {
            // Infinite loop to mimic heavy processing
            // 't1' won't leave this method
            // when 't2' try to enter this
        }
    }
}
```

在此代码中：

1. 我们创建了两个不同的线程 – $t1$ 和 $t2$
2. $t1$ 开始并进入被 `synchronized` 修饰的 *commonResource()* 方法； 这意味着只有一个线程可以访问它；尝试访问此方法的所有其它后续线程将被阻止进一步执行，直到当前线程完成处理为止。
3. 当 $t1$ 进入此方法时，它将保持在无限的 while 循环中；这只是为了模仿繁重的处理，以便所有其它线程都无法进入此方法
4. 现在，当我们启动 $t2$ 时*，它将尝试进入 *commonResource()* 方法，该方法已经被 $t1$ 访问*，*因此，$t2$ 将保持在 *BLOCKED* 状态

&nbsp;

在这种状态下，我们调用 `t2.getState()` 并获得如下输出：

```plaintext
BLOCKED
```

&nbsp;

### **3.4. Waiting**

**等待其它线程执行特定操作时，该线程处于 `WAITING` 状态。 [根据JavaDocs的说法](https://docs.oracle.com/javase/9/docs/api/java/lang/Thread.State.html#WAITING) ，任何线程都可以通过调用以下三种方法中的任何一种来进入此状态：

- [`Object.wait`](https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html#wait--) 

- [`Thread.join`](https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.html#join--) 

- [`LockSupport.park`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/locks/LockSupport.html#park--)

&nbsp;

请注意，在 `wait()` 和 `join()` 方法中 - 没有定义任何超时期限，因为下一节将介绍这种情况。

有 [一个单独的教程](java-wait-notify.md)，详细讨论了 *wait()*, *notify()* 和 *notifyAll()* 用法

&nbsp;

现在，让我们尝试重现此状态：

```java
public class WaitingState implements Runnable {
    public static Thread t1;

    public static void main(String[] args) {
        t1 = new Thread(new WaitingState());
        t1.start();
    }

    public void run() {
        Thread t2 = new Thread(new DemoThreadWS());
        t2.start();

        try {
            t2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Log.error("Thread interrupted", e);
        }
    }
}

class DemoThreadWS implements Runnable {
    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Log.error("Thread interrupted", e);
        }
        
        Log.info(WaitingState.t1.getState());
    }
}
```

让我们讨论一下我们在做什么：

1. 我们已经创建并启动了 $t1$ 
2. $t1$ 创建一个 $t2$  并启动它
3. 在继续执行  $t2$ 的过程中，我们调用 $t2.join()$ ，这会将 `t1` 置于 `WAITING` 状态，直到 $t2$ 完成执行
4. 由于 $t1$ 正在等待 $t2$ 完成，因此我们正在从 $t2$ 调用 $t1.getState()$

如您所料，这里的输出是：

```plaintext
WAITING
```

&nbsp;

### **3.5. Timed Waiting**

当一个线程正在等待另一个线程在规定的时间内执行特定操作时，该线程处于 `TIMED_WAITING` 状态。

[根据 JavaDocs 的介绍](https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.State.html#TIMED_WAITING)，有五种方法可以将线程置于 `TIMED_WAITING` 状态：

- [`Thread.sleep`](https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.html#sleep-long-)

- [`Object.wait`](https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html#wait-long-) with timeout

- [`Thread.join`](https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.html#join-long-) with timeout

- [`LockSupport.parkNanos`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/locks/LockSupport.html#parkNanos-java.lang.Object-long-)

- [`LockSupport.parkUntil`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/locks/LockSupport.html#parkUntil-java.lang.Object-long-)

  

要了解有关Java中 `wait()` 和 `sleep()` 之间的区别的更多信息，请在 [此处阅读这篇专门的文章](java-wait-and-sleep.md) 。

现在，让我们尝试快速重现此状态：

```java
public class TimedWaitingState {
    public static void main(String[] args) throws InterruptedException {
        DemoThread obj1 = new DemoThread();
        Thread t1 = new Thread(obj1);
        t1.start();
        
        // The following sleep will give enough time for ThreadScheduler
        // to start processing of thread t1
        Thread.sleep(1000);
        Log.info(t1.getState());
    }
}

class DemoThread implements Runnable {
    @Override
    public void run() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Log.error("Thread interrupted", e);
        }
    }
}
```

在这里，创建并启动了线程 $t1$，该线程进入睡眠状态的超时时间为 5 秒。输出将是：

```plaintext
TIMED_WAITING
```

&nbsp;

### **3.6. Terminated**

这是 dead 线程的状态。当线程完成执行或异常终止时，它处于 `TERMINATED` 状态。

我们有 [一篇专门的文章](java-thread-stop.md) ，讨论了停止线程的不同方法。

在下面的示例中，让我们尝试达到这种状态：

```java
public class TerminatedState implements Runnable {
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new TerminatedState());
        t1.start();
        // The following sleep method will give enough time for 
        // thread t1 to complete
        Thread.sleep(1000);
        Log.info(t1.getState());
    }
    
    @Override
    public void run() {
        // No processing in this block
    }
}
```

在这里，当我们启动线程 $t1$ 时*，下*一条语句 *Thread.sleep(1000)* 为 $t1$ 完成提供了足够的时间，因此该程序将输出显示为：

```plaintext
TERMINATED
```

除了线程状态之外，我们还可以检查  *[isAlive()](https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.html#isAlive--)* 方法以确定线程是否处于活动状态。例如，如果我们在此线程上调用 *isAlive()* 方法：

```java
Assert.assertFalse(t1.isAlive());
```

返回 *false。* 简而言之，线程只有在已经启动并且尚未死亡的情况下才是 alive 的 **。**

&nbsp;

## **4. 总结**

在这里，基本了解了 Java 中线程的生命周期。我们查看了 *Thread.State* 枚举定义的所有六个状态，并通过快速示例重现了它们。

尽管代码片段几乎在每台机器上都会提供相同的输出，但是在某些特殊情况下，由于无法确定 *Thread-Scheduler*  的确切行为，我们可能会得到一些不同的输出。