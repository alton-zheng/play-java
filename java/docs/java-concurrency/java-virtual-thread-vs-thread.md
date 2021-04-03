# Java中的线程和虚拟线程之间的区别



## 1. 简介

在本教程中，我们将展示 Java 中的传统线程与 [Project Loom 中](https://www.baeldung.com/openjdk-project-loom) 引入的虚拟线程之间的区别。

接下来，我们将共享虚拟线程和项目引入的 API 的几个场景。

在开始之前，我们需要注意**该项目正在积极开发中。**我们将在早期访问的 VM 上运行示例：openjdk-15-loom + 4-55_windows-x64_bin。

较新版本的构建可以自由更改和中断当前的 API 。话虽如此，该 API 已经发生了重大变化，因为以前使用的*java.lang.Fiber* 类已被删除，并被新的 *java.lang.VirtualThread* 类代替。

&nbsp;

## 2. 线程与虚拟线程的高级概述

在较高级别上，**线程是由操作系统管理和调度的，而虚拟线程是由虚拟机管理和调度的**。现在，**要创建一个新的内核线程，我们必须进行系统调用，这是一项昂贵的操作**。

这就是为什么我们使用线程池而不是根据需要重新分配和取消分配线程的原因。接下来，如果由于上下文切换及其内存占用量，我们想通过添加更多线程来扩展应用程序，则维护这些线程的成本可能会很大，并会影响处理时间。

然后，通常我们不想阻塞这些线程，这导致使用 non-blocking  I/O API 和 异步 API，这可能会使我们的代码混乱。

相反，**虚拟线程由 JVM 管理**。因此，它们的**分配不需要系统调用**，并且**没有操作系统的上下文切换**。此外，虚拟线程在 carrier thread 上运行，该 carrier thread 是在后台使用的实际内核线程。结果，由于我们摆脱了系统的上下文切换，因此我们可以产生更多这样的虚拟线程。

接下来，虚拟线程的关键属性是它们不会阻塞我们的 carrier thread。这样，由于 JVM 将调度另一个虚拟线程，从而使 carrier thread 保持不受阻塞状态，因此阻塞虚拟线程变得便宜得多。

最终，我们不需要接触NIO或异步API。这将使代码更具可读性，从而更易于理解和调试。尽管如此，continuation 可能会阻塞一个 carrier thread - 特别是当一个线程调用一个 native method 并从那里执行阻塞操作时。

&nbsp;

## 3. New Thread Builder API

在 Loom 中，我们在 *Thread* 类中获得了新的构建器 API ，以及一些工厂方法。让我们看看如何创建标准工厂和虚拟工厂并将其用于线程执行：

```java
Runnable printThread = () -> System.out.println(Thread.currentThread());
        
ThreadFactory virtualThreadFactory = Thread.builder().virtual().factory();
ThreadFactory kernelThreadFactory = Thread.builder().factory();

Thread virtualThread = virtualThreadFactory.newThread(printThread);
Thread kernelThread = kernelThreadFactory.newThread(printThread);

virtualThread.start();
kernelThread.start();
```

这是上面运行的输出：

```plaintext
Thread[Thread-0,5,main]
VirtualThread[<unnamed>,ForkJoinPool-1-worker-3,CarrierThreads]
```

在这里，第一个条目是内核线程的标准 *toString* 输出。

现在，我们在输出中看到虚拟线程没有名称，并且正在 *CarrierThread* 线程组的 Fork-Join 池的工作线程上执行。

如我们所见，无论底层实现如何，**API 都是相同的，这意味着我们可以轻松地在虚拟线程上运行现有代码**。

另外，我们无需学习新的API即可使用它们。

&nbsp;

## 4. 虚拟 Thread 构成

它是一个 $continuation$ 和一个 $scheduler$ ，共同构成了一个虚拟线程。现在，我们的用户模式调度程序可以是 *Executor* 接口的任何实现。上面的示例向我们展示了默认情况下，我们在 *ForkJoinPool* 上运行。

现在，类似于内核线程 - 可以在 CPU 上执行，然后 pack ，reschedule，然后恢复其执行 -  continuation 是可以启动，然后 park（ yield），重新调度并恢复的执行单元。它的执行方式与中断时的执行方式相同，仍然由 JVM 管理，而不是依赖于操作系统。

&nbsp;

请注意， continuation 是低级 API ，程序员应使用诸如构建器 API 的 high-level API 来运行虚拟线程。

但是，为了说明它是如何在后台运行的，现在我们将继续进行实验性的 continuation ：

```scala
var scope = new ContinuationScope("C1");
var c = new Continuation(scope, () -> {
    System.out.println("Start C1");
    Continuation.yield(scope);
    System.out.println("End C1");
});

while (!c.isDone()) {
    System.out.println("Start run()");
    c.run();
    System.out.println("End run()");
}
```

&nbsp;

这是上面运行的输出：

```plaintext
Start run()
Start C1
End run()
Start run()
End C1
End run()
```

在此示例中，我们运行了 continuation ，并在某个时候决定停止处理。然后，一旦我们重新运行它，我们的继续就从它停下来的地方继续。在输出中，我们看到 *run()* 方法被调用了两次，但是 continuation 开始了一次，然后从中断处开始第二次运行。

这就是 JVM 处理阻塞操作的方式。**一旦发生阻塞操作，将继续执行操作，从而使 carrier thread 保持 non-blocked 状态。**

因此，发生的事情是我们的主线程在其调用堆栈上为 *run()* 方法创建了一个新的 stack 框架，然后 continuation 执行。然后，在 continuation  yield 之后，JVM 保存了其执行的当前状态。

接下来，主线程继续执行，就像 run() 方法返回并继续 *while* 循环一样。在第二次调用 continuation 的 *run* 方法之后，JVM 将主线程的状态恢复到 continuation  yield 并完成执行的位置。

&nbsp;

## 5. 总结

在本文中，我们讨论了内核线程和虚拟线程之间的区别。接下来，我们展示了如何使用 Project Loom 中的新线程构建器 API 运行虚拟线程。

最后，我们展示了什么是 continuation，以及它在底层的工作方式。我们可以通过检查 [早期访问](https://jdk.java.net/loom/) VM来进一步探索 Project Loom 的状态。另外，我们可以探索更多已经标准化的 [Java并发](https://www.baeldung.com/java-concurrency) API。