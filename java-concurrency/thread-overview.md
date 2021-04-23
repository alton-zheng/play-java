# Thead Overview

&nbsp;

## 名词解析

进程

- **进程**（英语：process），是指计算机中已运行的[程序](https://zh.wikipedia.org/wiki/程式)。进程曾经是[分时系统](https://zh.wikipedia.org/wiki/分時系統)的基本运作单位。在面向进程设计的系统（如早期的[UNIX](https://zh.wikipedia.org/wiki/UNIX)，[Linux](https://zh.wikipedia.org/wiki/Linux) 2.4及更早的版本）中，进程是程序的基本执行实体；在面向线程设计的系统（如当代多数操作系统、[Linux](https://zh.wikipedia.org/wiki/Linux) 2.6及更新的版本）中，进程本身不是基本运行单位，而是[线程](https://zh.wikipedia.org/wiki/執行緒)的容器。

- e.g. client/server

&nbsp;

Thread

- **线程**（英语：thread）是操作系统能够进行运算调度的最小单位。大部分情况下，它被包含在进程之中，是进程中的实际运作单位。一条线程指的是进程中一个单一顺序的控制流，一个进程中可以并发多个线程，每条线程并行执行不同的任务。在 [Unix System V](https://zh.wikipedia.org/wiki/Unix) 及[SunOS](https://zh.wikipedia.org/wiki/SunOS) 中也被称为轻量进程（lightweight processes），但轻量进程更多指内核线程（kernel thread），而把用户线程（user thread）称为线程。
- 简而言之： 程序不同的执行路径。

&nbsp;

纤程

- 在[计算机科学](https://zh.wikipedia.org/wiki/計算機科學)中，**纤程**（英语：Fiber）是一种最轻量化的[线程](https://zh.wikipedia.org/wiki/线程)（lightweight threads）。它是一种用户态线程（user thread），让[应用程序](https://zh.wikipedia.org/wiki/應用程式)可以独立决定自己的线程要如何运作。操作系统[内核](https://zh.wikipedia.org/wiki/内核)不能看见它，也不会为它进行[调度](https://zh.wikipedia.org/wiki/排程)。

- 就像一般的线程，纤程有自己的[寻址空间](https://zh.wikipedia.org/wiki/定址空間)。但是纤程采取合作式多任务（Cooperative multitasking），而线程采取先占式多任务（Pre-emptive multitasking）。应用程序可以在一个线程环境中创建多个纤程，然后手动运行它。纤程不会被自动运行，必须要由[应用程序](https://zh.wikipedia.org/wiki/應用程式)自己指定让它运行，或换到下一个纤程。

  跟线程相比，纤程较不需要[操作系统](https://zh.wikipedia.org/wiki/作業系統)的支持。

&nbsp;

协程

**协程**（英语：coroutine）是计算机程序的一类组件，推广了[协作式多任务](https://zh.wikipedia.org/wiki/协作式多任务)的[子程序](https://zh.wikipedia.org/wiki/子程序)，允许执行被挂起与被恢复。相对子例程而言，协程更为一般和灵活，但在实践中使用没有子例程那样广泛。协程更适合于用来实现彼此熟悉的程序组件，如[协作式多任务](https://zh.wikipedia.org/wiki/协作式多任务)、[异常处理](https://zh.wikipedia.org/wiki/异常处理)、[事件循环](https://zh.wikipedia.org/wiki/事件循环)、[迭代器](https://zh.wikipedia.org/wiki/迭代器)、[无限列表](https://zh.wikipedia.org/wiki/惰性求值)和[管道](https://zh.wikipedia.org/wiki/管道_(软件))。

根据[高德纳](https://zh.wikipedia.org/wiki/高德纳)的说法, [马尔文·康威](https://zh.wikipedia.org/wiki/马尔文·康威)于1958年发明了术语“coroutine”并用于构建[汇编程序](https://zh.wikipedia.org/wiki/汇编语言)[[1\]](https://zh.wikipedia.org/wiki/协程#cite_note-KnuthVol1_1_4_5-1) ，关于协程最初的出版解说在1963年发表[[2\]](https://zh.wikipedia.org/wiki/协程#cite_note-Conway1963-2)。&nbsp;

&nbsp;

## Thread

### 产生 Thread 的方法

- `Thread`
  - extends

```java
new Thread().start();
new Thread().run();
```



- `Runnable`
  - implements 

```java
new Thread(new Runnable()).start();
```

&nbsp;

- 不同语法
  - `lambda`

```java
new Thread(() -> {
  System.out....
}).start();
```

&nbsp;

- `Executors`

```java
Executors.newFixedThreadPool(5, ...)
```

&nbsp;

> 锁级别，锁的定义
>
> 无锁 00
>
> 偏向锁 01
>
> 轻量级锁 10
>
> 重量级锁 11

&nbsp;

### 方法

- `start()`
  - 不阻塞
  - 当前线程和其他线程一起运行 
- `run()`
  - 当前线程执行完，才能执行其他线程
- `yield()`
  - 当前线程从 CPU 离开一会，进入到等待队列，进入就绪状态
  - 队列中有其它线程的情况下，就会将其它线程放入 CPU，执行
  - 应用 场景少
- `join()`
  - 当前线程调用其它线程的 join 方法， 等待其它另外线程执行完成，再回来执行当前线程
  - 保证线程按顺序完成的手段之一

```java
Thread t2 = new Thread(
() -> {
  try {
  t1.join();
  } catch(InterruptedException e) {
    e.printStackTrace();
  }
})
```

&nbsp;

- sleep

每个线程在 CPU 中执行， CPU 不断的切换，切换到处理某个线程，一会将这个线程移出去到等待队列，然后另一个线程进入 CPU 执行，一会又释放出去，.... 依次类推，直到所有线程执行完毕

&nbsp;

## 解析自旋锁CAS操作与volatile

- --XX:PretenureSizeThreshold 的默认值和作用
  - 作用： 不管对象多大，都是先在 eden 分配内存, 放不下后，到 old 去
  - 默认值为 0 

&nbsp;

> Lock 底层用 CAS

&nbsp;



### AQS 源码

> VarHandle 
>
> - 普通属性原子操作
> - 比反射块，直接操作二进制码



### ThreadLocal

> 不用后，需要 remove(), 不然会一直占用内存空间。消耗资源

> Reference
>
> 强软弱虚
>
> - 强： 普通对象，按 GC 规则进行回收
>
> - 软:  内存不够用时才会被收回
>
>   - memCache
>   - 平常用 redis 即可
>
>   ```java
>   SoftReference<byte[]> m = new SoftReference<>(new byte[1024*1024*10]);
>   System.out.println(m.get()); // value
>   System.gc();
>   
>   byte[] b = new byte[1024*1024*15];
>   System.out.println(m.get());  // null
>   ```

> 弱： 
>
> - 只要遭遇到 GC ， 就会被回收
> - 一般用在容器里（强引用指向弱引用，一旦强引用消失，弱引用会被自动回收）
> - WeekHashMap(需要深入源码了解)
>
> ```java
> WeekReference<M> m = new WeekReference<>(new M());
> System.out.println(m.get());
> System.gc();
> System.out.println(m.get());
> 
> ThreadLocal<M> t1 = new ThreadLocal<>();
> t1.set(new M());
> t1.remove();
> ```
>
> 
>
> 虚： 
>
> 管理堆外内存（不被 JVM 管理）
>
> 写 JVM 的人用的
>
> - 通知用，当锁使用的堆外内存被赋值为 null 时，不能被 JVM 自动回收，所以需要有想对应的通知机制。
> - 当然 JVM 可以回收堆外内存，通过 UNSAFE 类进行处理
>   - 1.8 可以通知反射机制来使用 unsafe, 后续版本被隐藏
>
> ```java
> PhantomReference<M> phantomRefence   = new PhantomReference<>(new M(), QUEUE); // 当被回收时，会将虚引用放入到 Queue 中。
> // 也就是说， Queue.poll() 有内容时，说明虚引用已经被回收
> ```
>
> 
>
> 等待
>
> 等待
>
> 等待



&nbsp;



&nbsp;



