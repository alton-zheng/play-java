# Collections.synchronizedMap vs. ConcurrentHashMap

## 1. 概述

在本教程中，我们将讨论  *[Collections.synchronizedMap()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Collections.html#synchronizedMap(java.util.Map))*  和 [ConcurrentHashMap](java-concurrent-map.md) 之间的区别

此外，我们将查看每个操作的读和写操作的性能输出。

&nbsp;

## 2. 不同点

*Collections.synchronizedMap()* 和 *ConcurrentHashMap* 都提供了对数据集合的线程安全操作。

[*Collections*](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Collections.html) 工具类提供了对集合进行操作的 **多态算法，并返回包装的集合**。它的 *synchronizedMap()* 方法提供线程安全的功能。

顾名思义，*synchronizedMap()* 返回一个同步的 *Map*，该 *Map* 由我们在形参中提供的 *Map* 支持。为了提供线程安全，*synchronizedMap()* 允许所有通过返回的 *Map* 访问 back 的 *Map*。

在 JDK 1.5 中引入了 ConcurrentHashMap，作为 *HashMap* 的增强，支持检索和更新的高并发性。HashMap 不是线程安全的，所以它可能会在线程争用期间导致不正确的结果。

ConcurrentHashMap类是线程安全的。因此，多个线程可以对单个对象进行操作，而不会产生任何麻烦。

在 *ConcurrentHashMap* 中，读操作是非阻塞的，而写操作需要对特定的 segment 或 bucket 进行锁。默认的bucket 或并发级别是 16，这意味着在对一个 segment 或 bucket 进行锁定后，16 个线程可以在任何时刻进行写操作。

&nbsp;

### 2.1. *ConcurrentModificationException*

对于像 *HashMap* 这样的对象，不允许执行并发操作。因此，如果我们试图在遍历一个 *HashMap* 时更新它，我们将收到一个 *ConcurrentModificationException*。当使用 *synchronizedMap()* 时也会出现这种情况：

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

&nbsp;

然而，*ConcurrentHashMap* 并不会抛出此异常:

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

&nbsp;

### 2.2. *null* Support

*Collections.synchronizedMap()* 和*ConcurrentHashMap* **handle \*null\* key和值不同**。

*ConcurrentHashMap* 不允许在 key 或 value 中使用 *null*

```java
@Test(expected = NullPointerException.class)
public void allowNullKey_In_ConcurrentHasMap() {
    Map<String, Integer> map = new ConcurrentHashMap<>();
    map.put(null, 1);
}
```

&nbsp;

然而，当使用 Collections.synchronizedMap() 时，*null* 支持取决于输入Map。当 *Collections.synchronizedMap()* 由 *HashMap* 或 *LinkedHashMap* *支持时，我们可以有一个 *null* 值作为 key 和任意数量的 *null* 值，而如果我们使用 *TreeMap*，我们可以有 *null* value，但不能有 *null* key。

让我们断言，我们可以为一个 HashMap 的  *Collections.synchronizedMap()* 使用一个 *null*  key ： 

```java
Map<String, Integer> map = Collections
  .synchronizedMap(new HashMap<String, Integer>());
map.put(null, 1);
Assert.assertTrue(map.get(null).equals(1));
```

类似地，我们可以验证 *Collections.synchronizedMap()* 和 *ConcurrentHashMap* 的值对 *null* 的支持。

&nbsp;

## 3. 性能比对

让我们比较一下 *ConcurrentHashMap* 和 *Collections.synchronizedMap()* 的性能。在本例中，我们使用开源框架[Java Microbenchmark Harness](java-microbenchmark-harness.md) (JMH) 来**在纳秒内比较方法的性能**。

我们对这些映射进行了随机读写操作的比较。让我们快速浏览一下 JMH 的基准代码:

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



我们使用 5 个迭代和 10 个线程运行我们的性能基准测试，1000 个条目。让我们看看基准测试的结果:

```java
Benchmark                                                     Mode  Cnt        Score        Error  Units
MapPerformanceComparison.randomReadAndWriteConcurrentHashMap  avgt  100  3061555.822 ±  84058.268  ns/op
MapPerformanceComparison.randomReadAndWriteSynchronizedMap    avgt  100  3234465.857 ±  60884.889  ns/op
MapPerformanceComparison.randomReadConcurrentHashMap          avgt  100  2728614.243 ± 148477.676  ns/op
MapPerformanceComparison.randomReadSynchronizedMap            avgt  100  3471147.160 ± 174361.431  ns/op
MapPerformanceComparison.randomWriteConcurrentHashMap         avgt  100  3081447.009 ±  69533.465  ns/op
MapPerformanceComparison.randomWriteSynchronizedMap           avgt  100  3385768.422 ± 141412.744  ns/op
```

以上结果表明，ConcurrentHashMap 的性能优于 Collections.synchronizedMap()。

&nbsp;

## 4. 如何选择

当数据一致性是最重要的，我们应该选择 `Collections.synchronizedMap()`，而对于性能关键的应用程序，当写操作远远多于读操作时，我们应该选择 `ConcurrentHashMap`。

&nbsp;

## 5. 总结

在本文中，我们演示了 *ConcurrentHashMap* 和 *Collections.synchronizedMap()* 之间的区别。我们还使用一个简单的JMH基准测试展示了这两种方法的性能。
