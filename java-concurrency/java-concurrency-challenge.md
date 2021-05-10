## 并发编程的挑战

### 测试上下文切换次数和时长

`Lmbench3` 可以测量上下文切换的时长

使用 `vmstat` 可以测量上下文切换的次数。

```bash
$ vmstat 1
```

CS(`Content Switch`) 表示上下文切换的次数

&nbsp;

### 减少上下文切换

- 无锁并发编程
  - 多线程竞争锁时，会引发上下文切换。
  - 所以多线程处理数据数据时，可以用方法来避免使用锁，如将数据的 ID 按照 Hash 算法取模分段，不同的线程处理不同段的数据。

&nbsp;

- CAS 算法
  - Java 的 Atomic 包使用 CAS 算法来更新数据， 而不需要加锁

&nbsp;

- 使用合理数量线程
  - 避免创建不需要的线程
    - 大部分线程处于等待状态

&nbsp;

- 使用协程
  - 在单线程里实现多任务的调度， 并在单线程里维持多个任务间的切换。

&nbsp;

### 减少上下文切换实现

减少线上大量 WAITING 的线程，来减少上下文切换次数。

第一步： jstack 命令 dump 线程信息

```bash
$ jstack pid_number > dump.log
```

&nbsp;

第二步： 统计所有线程分别处于什么状态，发现很多线程处于 WAITING （onobject-monitor) 状态

```bash
$ grep java.lang.Thread.State dump.log | awk '{print $2$3$4$5}' | sort | uniq -c
```

&nbsp;

第三步： 打开 dump 文件查看处于 WAITING（on object monitor）的线程在做什么。发现这些线程基本全是 JBOSS 的工作线程， 在 await.说明 JBOSS 线程池里线程接收到的任务太少，大量线程都闲着。 

第四步： 减少 JBOSS 的工作线程数， 找到 JBOSS 的线程池配置信息，将 maxThreads 降低到 100

第五步： 重启 JBOSS， 在 dump 信息，然后统计 WAITING 的线程，发现减少了 175 个。 

&nbsp;





&nbsp;

