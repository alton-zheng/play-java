# Collections.synchronizedMap vs. ConcurrentHashMap

## 1. Overview

In this tutorial, we'll discuss the differences between *[Collections.synchronizedMap()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Collections.html#synchronizedMap(java.util.Map))* and *[ConcurrentHashMap](https://www.baeldung.com/java-concurrent-map)**.*

Additionally, we'll look at the performance outputs of the read and write operations for each.

在本教程中，我们将讨论*[Collections.synchronizedMap()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Collections.html#synchronizedMap(java.util.Map))*)和*[ConcurrentHashMap](https://www.baeldung.com/java-concurrent-map)**.*)之间的区别

此外，我们将查看每个操作的读和写操作的性能输出。

## 2. The Differences

*Collections.synchronizedMap()* and *ConcurrentHashMap* both provide thread-safe operations on collections of data.

The [*Collections*](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Collections.html) utility class provides **polymorphic algorithms that operate on collections and return wrapped collections**. Its *synchronizedMap()* method provides thread-safe functionality.

As the name implies, *synchronizedMap()* returns a synchronized *Map* backed by the *Map* that we provide in the parameter. To provide thread-safety, *synchronizedMap()* allows all accesses to the backing *Map* via the returned *Map*.

*ConcurrentHashMap* was introduced in JDK 1.5 as an **enhancement of \*HashMap\* that supports high concurrency for retrievals as well as updates**. *HashMap* isn't thread-safe, so it might lead to incorrect results during thread contention.

The *ConcurrentHashMap* class is thread-safe. Therefore, multiple threads can operate on a single object with no complications.

**In \*ConcurrentHashMap,\* read operations are non-blocking, whereas write operations take a lock on a particular segment or bucket.** The default bucket or concurrency level is 16, which means 16 threads can write at any instant after taking a lock on a segment or bucket.

* collections . synchronizedmap()*和*ConcurrentHashMap*都提供了对数据集合的线程安全操作。

[*Collections*](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Collections.html)工具类提供了对集合进行操作的**多态算法，并返回包装的集合**。它的*synchronizedMap()*方法提供线程安全的功能。

顾名思义，*synchronizedMap()*返回一个同步的*Map*，该*Map*由我们在形参中提供的*Map*支持。为了提供线程安全，*synchronizedMap()*允许所有通过返回的*Map*访问后备的*Map*。

在JDK 1.5中引入了ConcurrentHashMap，作为\*HashMap\*的增强，支持检索和更新的高并发性。HashMap*不是线程安全的，所以它可能会在线程争用期间导致不正确的结果。

ConcurrentHashMap*类是线程安全的。因此，多个线程可以对单个对象进行操作，而不会产生任何麻烦。

在\*ConcurrentHashMap中，\*读操作是非阻塞的，而写操作需要对特定的段或桶进行锁。**默认的bucket或并发级别是16，这意味着在对一个段或bucket进行锁定后，16个线程可以在任何时刻进行写操作。

### 2.1. *ConcurrentModificationException*

For objects like *HashMap*, performing concurrent operations is not allowed. Therefore, if we try to update a *HashMap* while iterating over it, we will receive a *ConcurrentModificationException*. This will also occur when using *synchronizedMap()*:

对于像*HashMap*这样的对象，不允许执行并发操作。因此，如果我们试图在遍历一个*HashMap*时更新它，我们将收到一个*ConcurrentModificationException*。当使用*synchronizedMap()*时也会出现这种情况:

```java
@Test(expected = ConcurrentModificationException.class)
public void whenRemoveAndAddOnHashMap_thenConcurrentModificationError() {
    Map<Integer, String> map = new HashMap<>();
    map.put(1, "baeldung");
    map.put(2, "HashMap");
    Map<Integer, String> synchronizedMap = Collections.synchronizedMap(map);
    Iterator<Entry<Integer, String>> iterator = synchronizedMap.entrySet().iterator();
    while (iterator.hasNext()) {
        synchronizedMap.put(3, "Modification");
        iterator.next();
    }
}
```

However, this is not the case with *ConcurrentHashMap*:

然而，这不是*ConcurrentHashMap*的情况:

```java
Map<Integer, String> map = new ConcurrentHashMap<>();
map.put(1, "baeldung");
map.put(2, "HashMap");
 
Iterator<Entry<Integer, String>> iterator = map.entrySet().iterator();
while (iterator.hasNext()) {
    synchronizedMap.put(3, "Modification");
    iterator.next()
}
 
Assert.assertEquals(3, map.size());
```

### 2.2. *null* Support

*Collections.synchronizedMap()* and *ConcurrentHashMap* **handle \*null\* keys and values differently**.

*ConcurrentHashMap* doesn't allow *null* in keys or values:

Collections.synchronizedMap()*和*ConcurrentHashMap* **handle \*null\* key和值不同**。

*ConcurrentHashMap*不允许在键或值中使用*null*

```java
@Test(expected = NullPointerException.class)
public void allowNullKey_In_ConcurrentHasMap() {
    Map<String, Integer> map = new ConcurrentHashMap<>();
    map.put(null, 1);
}
```

However, **when using \*Collections.synchronizedMap()\*, \*null\* support depends on the input \*Map\****.* We can have one *null* as a key and any number of *null* values when *Collections.synchronizedMap()* is backed by *HashMap* or *LinkedHashMap,* whereas if we're using *TreeMap*, we can have *null* values but not *null* keys.

Let's assert that we can use a *null* key for *Collections.synchronizedMap()* backed by a *HashMap*:

然而，**当使用\*Collections.synchronizedMap()\*时，\*null\*支持取决于输入\*Map\****。当Collections.synchronizedMap()*由*HashMap*或*LinkedHashMap *支持时，我们可以有一个*null*值作为键和任意数量的*null*值，而如果我们使用*TreeMap*，我们可以有*null*值，但不能有*null*键。

让我们断言，我们可以为*Collections.synchronizedMap()*使用一个*null*键，并支持一个*HashMap*:

```java
Map<String, Integer> map = Collections
  .synchronizedMap(new HashMap<String, Integer>());
map.put(null, 1);
Assert.assertTrue(map.get(null).equals(1));
```

Similarly, we can validate *null* support in values for both *Collections.synchronizedMap()* and *ConcurrentHashMap*.

类似地，我们可以验证*Collections.synchronizedMap()*和*ConcurrentHashMap*的值对*null*的支持。

## 3. Performance Comparison

Let's compare the performances of *ConcurrentHashMap* versus *Collections.synchronizedMap().* In this case, we're using the open-source framework [Java Microbenchmark Harness](https://www.baeldung.com/java-microbenchmark-harness) (JMH) to **compare the performances of the methods in nanoseconds**.

We ran the comparison for random read and write operations on these maps. Let's take a quick look at our JMH benchmark code:

让我们比较一下*ConcurrentHashMap*和*Collections.synchronizedMap()的性能。*在本例中，我们使用开源框架[Java Microbenchmark Harness](https://www.baeldung.com/java-microbenchmark-harness) (JMH)来**在纳秒内比较方法的性能**。

我们对这些映射进行了随机读写操作的比较。让我们快速浏览一下JMH的基准代码:

```java
@Benchmark
public void randomReadAndWriteSynchronizedMap() {
    Map<String, Integer> map = Collections.synchronizedMap(new HashMap<String, Integer>());
    performReadAndWriteTest(map);
}

@Benchmark
public void randomReadAndWriteConcurrentHashMap() {
    Map<String, Integer> map = new ConcurrentHashMap<>();
    performReadAndWriteTest(map);
}

private void performReadAndWriteTest(final Map<String, Integer> map) {
    for (int i = 0; i < TEST_NO_ITEMS; i++) {
        Integer randNumber = (int) Math.ceil(Math.random() * TEST_NO_ITEMS);
        map.get(String.valueOf(randNumber));
        map.put(String.valueOf(randNumber), randNumber);
    }
}
```

We ran our performance benchmarks using 5 iterations with 10 threads for 1,000 items. Let's see the benchmark results:

我们使用5个迭代和10个线程运行我们的性能基准测试，1000个条目。让我们看看基准测试的结果:

```java
Benchmark                                                     Mode  Cnt        Score        Error  Units
MapPerformanceComparison.randomReadAndWriteConcurrentHashMap  avgt  100  3061555.822 ±  84058.268  ns/op
MapPerformanceComparison.randomReadAndWriteSynchronizedMap    avgt  100  3234465.857 ±  60884.889  ns/op
MapPerformanceComparison.randomReadConcurrentHashMap          avgt  100  2728614.243 ± 148477.676  ns/op
MapPerformanceComparison.randomReadSynchronizedMap            avgt  100  3471147.160 ± 174361.431  ns/op
MapPerformanceComparison.randomWriteConcurrentHashMap         avgt  100  3081447.009 ±  69533.465  ns/op
MapPerformanceComparison.randomWriteSynchronizedMap           avgt  100  3385768.422 ± 141412.744  ns/op
```

The above results show that ***ConcurrentHashMap\* performs better than** ***Collections.synchronizedMap()***.

以上结果表明，***ConcurrentHashMap\*的性能优于** ***Collections.synchronizedMap()***。

## 4. When to Use

We should favor *Collections.synchronizedMap()* when data consistency is of utmost importance, and we should choose *ConcurrentHashMap* for performance-critical applications where there are far more write operations than there are read operations.

当数据一致性是最重要的，我们应该选择Collections.synchronizedMap()*，而对于性能关键的应用程序，当写操作远远多于读操作时，我们应该选择ConcurrentHashMap*。

## **5. Conclusion**

In this article, we've demonstrated the differences between *ConcurrentHashMap* and *Collections.synchronizedMap()*. We've also shown the performances of both of them using a simple JMH benchmark.

As always, the code samples are available [over on GitHub](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-collections-maps-3).

在本文中，我们演示了*ConcurrentHashMap*和*Collections.synchronizedMap()*之间的区别。我们还使用一个简单的JMH基准测试展示了这两种方法的性能。

和往常一样，代码示例可以[在GitHub上](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-collections-maps-3)获得。