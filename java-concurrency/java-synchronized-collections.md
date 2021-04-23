# 同步 Java Collections 介绍

## 1. 概览

[collections framework](https://docs.oracle.com/javase/8/docs/technotes/guides/collections/overview.html) 是Java的一个关键组件。它提供了大量的接口和实现，这允许我们以一种直接的方式创建和操作不同类型的集合。

虽然使用普通的非同步集合总体上很简单，但在多线程环境(也就是并发编程)中，它也可能成为一个令人生畏且容易出错的过程。

因此，Java平台通过在 *[Collections](https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html#synchronizedCollection(java.util.Collection))* 类)中实现的不同同步 *wrapper* 为这种场景提供了强大的支持。

通过这些 wrapper，可以很容易地通过几个静态工厂方法创建所提供集合的同步视图。

在本教程中，我们将深入研究这些静态同步 wrapper。同样，我们将强调同步 collection 和并发 collection 之间的区别。

&nbsp;

## 2.  *synchronizedCollection()* 方法

我们将在本文中介绍的第一个同步 wrapper 是 *synchronizedCollection()* 方法。顾名思义，**它返回一个线程安全的集合，该集合由指定的 [*collection*](https://docs.oracle.com/javase/8/docs/api/java/util/Collection.html)** 。

现在，为了更清楚地理解如何使用这个方法，让我们创建一个基本的单元测试：

```java
Collection<Integer> syncCollection = Collections.synchronizedCollection(new ArrayList<>());
    Runnable listOperations = () -> {
        syncCollection.addAll(Arrays.asList(1, 2, 3, 4, 5, 6));
    };
    
    Thread thread1 = new Thread(listOperations);
    Thread thread2 = new Thread(listOperations);
    thread1.start();
    thread2.start();
    thread1.join();
    thread2.join();
    
    assertThat(syncCollection.size()).isEqualTo(12);
}
```

如上所示，使用此方法创建所提供集合的同步视图非常简单。

&nbsp;

为了演示该方法实际上返回一个线程安全的集合，我们首先创建两个线程。

然后，我们以 lambda 表达式的形式将 [*Runnable*](https://docs.oracle.com/javase/8/docs/api/java/lang/Runnable.html) 实例注入到它们的构造函数中。让我们记住，*Runnable* 是一个函数接口，所以我们可以用 lambda 表达式替换它。

最后，我们只是检查每个线程是否有效地向同步集合添加了 6 个元素，因此它的最终大小是12个。

&nbsp;

## 3. *synchronizedList()* 方法

同样，与 *synchronizedCollection()* 方法类似，我们可以使用 *synchronizedList()*  wrapper 来创建一个synchronized  [*List*](https://docs.oracle.com/javase/8/docs/api/java/util/List.html?is-external=true) 。

正如我们所期望的，该方法返回指定的 *List* 的线程安全视图

```java
List<Integer> syncList = Collections.synchronizedList(new ArrayList<>());
```

&nbsp;

不出所料，*synchronizedList()* 方法的使用与它的高级对等体 *synchronizedCollection()* 几乎相同。

因此，正如我们在前面的单元测试中所做的那样，一旦我们创建了一个同步的*列表*，我们就可以生成几个线程。之后，我们将使用它们以线程安全的方式访问/操作目标*列表*。

此外，如果我们想遍历一个同步集合并防止出现意外结果，我们应该显式地提供我们自己的线程安全的循环实现。因此，我们可以使用一个 *synchronized* 块来实现:

```java
List<String> syncCollection = Collections.synchronizedList(Arrays.asList("a", "b", "c"));
List<String> uppercasedCollection = new ArrayList<>();
    
Runnable listOperations = () -> {
    synchronized (syncCollection) {
        syncCollection.forEach((e) -> {
            uppercasedCollection.add(e.toUpperCase());
        });
    }
};
```

在所有需要遍历同步 collection 的情况下，我们都应该实现这个习惯用法。这是因为同步集合上的迭代是通过对该集合的多个调用来执行的。因此，它们需要作为单个原子操作来执行。

synchronized 块的使用保证了操作的原子性。

&nbsp;

## 4. *synchronizedMap()* 方法

Collections* 类实现了另一个整洁的同步 wrapper ，称为 *synchronizedMap()* 。我们可以使用它轻松地创建一个同步的 [*Map*](https://docs.oracle.com/javase/8/docs/api/java/util/Map.html) 。

该方法返回一个线程安全的 view ，该视图是提供的 Map 实现的。

```java
Map<Integer, String> syncMap = Collections.synchronizedMap(new HashMap<>());
```

&nbsp;

## 5. *synchronizedSortedMap()* 方法

还有一个 *synchronizedMap()* 方法的对等实现。它被称为 *synchronizedSortedMap()* ，我们可以用它来创建一个同步的 *[SortedMap](https://docs.oracle.com/javase/8/docs/api/java/util/SortedMap.html)* 实例:

```java
Map<Integer, String> syncSortedMap = Collections.synchronizedSortedMap(new TreeMap<>());
```

&nbsp;

## 6. *synchronizedSet()* 方法

接下来，在这篇回顾中，我们有 *synchronizedSet()* 方法。顾名思义，它允许我们以最小的麻烦创建 synchronized *[Sets](https://docs.oracle.com/javase/8/docs/api/java/util/Set.html)*  。

wrapper 返回一个由指定的 *Set* 支持的 thread-safe 集合:

```java
Set<Integer> syncSet = Collections.synchronizedSet(new HashSet<>());
```

&nbsp;

## 7. *synchronizedSortedSet()* 方法

最后，我们在这里展示的最后一个同步 wrapper 是 *synchronizedSortedSet()*。

与我们到目前为止讨论的其他 wrapper 实现类似，**该方法返回给定的 [SortedSet](https://docs.oracle.com/javase/8/docs/api/java/util/SortedSet.html)** 线程安全版本:

```java
SortedSet<Integer> syncSortedSet = Collections.synchronizedSortedSet(new TreeSet<>());
```

&nbsp;

## 8. 同步 vs 并发集合

到目前为止，我们仔细研究了 collections 框架的同步 wrapper。

现在,让我们关注同步 collection 和 并发 collection 的不同 *,如*  [ConcurrentHashMap](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ConcurrentHashMap.html) 和 [BlockingQueue](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html) 实现。

&nbsp;

### 8.1. 同步集合

同步集合通过 [intrinsic](https://docs.oracle.com/javase/tutorial/essential/concurrency/locksync.html)  [c locking](https://docs.oracle.com/javase/tutorial/essential/concurrency/locksync.html) 实现线程安全，并且整个集合被锁定。内在锁是通过 wrapper 的集合方法中的同步块实现的。

正如我们所期望的那样，同步集合确保了多线程环境中的数据一致性/完整性。然而，它们可能会带来性能上的损失，因为一次只能有一个线程访问集合(也就是同步访问)。

关于如何使用 *synchronized* 方法和块的详细指南，请查看 [我们的文章](java-synchronized.md) 这个主题。

&nbsp;

### 8.2. 并发集合

并发集合(例如，*ConcurrentHashMap*，通过将它们的数据分割成 segment 来实现线程安全。例如，在 *ConcurrentHashMap* 中，不同的线程可以在每个段上获得锁，所以多个线程可以同时访问 *Map* (也就是并发访问)。

由于并发线程访问的固有优势，并发集合比同步集合的性能要好得多。

因此，选择使用哪种类型的线程安全集合取决于需求场景，应该相应地对其进行测试评估。

&nbsp;

## 9. 总结

在本文中，我们深入研究了在 *Collections* 类中实现的一组同步 wrapper。

此外，我们还强调了同步集合和并发集合之间的区别，并研究了它们实现线程安全的方法。
