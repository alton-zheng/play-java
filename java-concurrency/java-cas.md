# CAS

>无锁 自旋
>
>Compare And Set
>
>```java
>// CPU 原语支持
>cas(v, e, new) {
>	if v == e
>	v == n
>	otherwise try again or fail
>}
>```
>
>java.util.concurrency.atomic.Atomic*  开头的类都是用 CAS 来保证原子性

&nbsp;

```java
AtomicInteger atomicInteger = new AtomicInteger(0);

void m() {
  for(int i = 0; i < 10000; i++) {
    atomicInteger.incrementAndGet();
  }
}
```

&nbsp;

## ABA 问题，怎么解决？

cas 过程 预期的 e = 1; 

- e = 1; 
- 被其它线程 e = 2; 又变成了 e = 1;

ABA 问题就此产生

> 看情况，如果产生了影响，可以加版本号解决
>
> cas(v, e,version, new) {
> 	if v == e
> 	v == n
> 	otherwise try again or fail
> }

&nbsp;

## LongAdder

> 性能比 Atmic 类高，底层用了分段 cas, 一段段进行分别 add 操作, 最后一起汇总。

&nbsp;

### `ReentrantLock` 

>ReentrantLock(cas) 和 synchronized 比对
>
>都是可重入的



### CountDownLatch





&nbsp; 

&nbsp;
