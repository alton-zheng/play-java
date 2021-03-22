# 面试

## 什么是并发？ 什么是并行？ 并发和并行会造成什么影响？ 如何解决? 操作系统基本概念 聊一下？

>并发： CPU 轮询处理事务
>
>并行： 多个线程一起处理自己的事务，当CPU 轮询时间到了很短时，也可以成为并行
>
>影响： 会造成资源争抢
>
>
>
>



```java
int a=1; // 1 获取锁成功， 0 放等待队列
p: --a; // 原子操作
v: ++a; // 原子操作
```

```c
typedef struct {
  volatile unsigned long lock;
  char *owner;
} spinlock_t;

class spinlock {
  long lock;
  Thread owner;
}

// lock 为 1 时持有锁，为0 时 不持有锁
// cpu （8259A APIC 硬件 每隔 100ms 搞一下 CPU， CPU就会相应这个信号， 切换任务，时钟中断）， 关中断时，硬件就不会影响到 CPU， 开中断时，硬件信号才能影响 CPU，进行切换。
```

或

```汇编
lock dec;
lock incr; 
#
lock cmpxchg;
```

&nbsp;

>如果这个东西是所有人都能轻易学会的，都懂得，适用如何体现你的价值？ 为什么要给你高工资？
>
>当今社会，不缺知识， 缺思想
>
>JAVA 语言的开发背景： C语言学习成本不算太高，但是用起来不大方便，特别是写业务， C++过度繁琐不适合， Python 等等，脚本语言成本低， 但是用起来很爽，维护起来我的吗
>
>开发一种语言，不需要程序员花费太多时间学习复杂的知识， 屏蔽掉底层繁琐且不易懂知识， 让程序员快速开发，专注于业务代码，而不是 细节



>学而不思则罔，思而不学则殆

&nbsp;

## 简单描述一下 ABA 问题？

言简意赅， ABA 不管程序， 你想要的事务，它换成别的状态，然后又变回原来的问题。

&nbsp;

版本号，每次加1 可以解决这个问题

> SeqLock
>
> unsigned sequence;
>
> spinlock_t lock;
>
> 

&nbsp;

实现一下 DCL？

> 为什么要实现双重

&nbsp;

> EntryList 竞争队列
>
> CXQ 队列

&nbsp;

volatile 原理和

> volatile init 方法， 默认值， 
>
> volatile 可以防止重排序问题

&nbsp;

12 实现一个阻塞队列（用 condition 写生产者与消费者）？ 

> condition : 条件等待队列
>
> ArrayBlockingQueue

&nbsp;

实现多个线程顺序打印 abc?

&nbsp;

后续细化，目前先整理这么多



### `synchronized​` 和  `reentrant-lock​` 的区别?

>synchronized 自动加解锁, reentrantLock  手动加解锁
>
>reentrantLock 可以有不同 condition（等待队列）
>
>reentrantLock 底层 CAS 的实现， synchronized 4种状态锁升级

&nbsp;

