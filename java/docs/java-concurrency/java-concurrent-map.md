# ConcurrentMap 指引

&nbsp;

## 1. 概览

Map 是用途最广泛的 Java 集合之一。

更重要的是，[HashMap](java-hashmap.md) 不是线程安全的实现，而 *Hashtable* 通过同步操作提供了线程安全。

即使 *Hashtable* 是线程安全的，它的效率也不是很高。另一个完全同步的 *Map* *Collections.synchronizedMap*，也没有表现出很大的效率。如果我们想在高并发的情况下实现线程安全和高吞吐量，这些实现不是可行的。

为了解决这个问题，Java 集合框架在 **Java 1.5** 引入了 $ConcurrentMap$ 

下面的讨论是基于 Java 1.8 的。

&nbsp;

## 2. ConcurrentMap

*ConcurrentMap* 是 *Map* 接口的扩展。它旨在为解决吞吐量与线程安全之间的协调问题提供一种结构和指导。

通过重写几个接口默认方法，*ConcurrentMap* 为有效的实现提供了指导方针，以提供线程安全和内存一致的原子操作。

一些默认实现被覆盖，禁用 *null* key/value 支持：

- *getOrDefault*
- *forEach*
- *replaceAll*
- *computeIfAbsent*
- *computeIfPresent*
- *compute*
- *merge*

&nbsp;

下面的 api 也被覆盖以支持原子性，但没有默认的接口实现: 

- *putIfAbsent*
- *remove*
- *replace(key, oldValue, newValue)*
- *replace(key, value)

其余的操作都是直接继承的，基本上与 *Map* 一致。

&nbsp;

## 3. ConcurrentHashMap

*ConcurrentHashMap* 通过实现 *ConcurrentMap* 的方式来达到 *out-of-box*。 

为了获得更好的性能，它在底层由作为表 bucket 的节点数组( 在 *Java 8* 之前是表 segments ) 组成，主要在更新期间使用 [CAS](https://en.wikipedia.org/wiki/Compare-and-swap) 操作。

在第一次插入时，表 bucket 被惰性初始化。通过锁定 bucket 中的第一个节点，每个桶都可以被独立地锁定。读取操作不会阻塞，并且更新争用最小化。

所需的 segment 数量与访问表的线程数量相关，这样每个段正在进行的更新在大多数情况下不会超过一个。

在 Java 8 之前，需要的 *segment* 的数量与访问表的线程数量有关，这样每个段的更新进度在大多数情况下不会超过一个

这就是为什么与 *HashMap* 相比，构造函数提供了额外的 *concurrencyLevel* 参数来控制估计使用的线程数量：

```java
public ConcurrentHashMap(
public ConcurrentHashMap(int initialCapacity, float loadFactor, int concurrencyLevel)
```

&nbsp;

其他两个参数: *initialCapacity* 和 *loadFactor* 与 [*HashMap*](java-hashmap.md) 的参数一样。

然而，由于 Java 8，构造函数的存在只是为了向后兼容: 形参只能影响 **map** 的初始大小。

&nbsp;
### 3.1. Thread-Safety


在多线程环境中，保证 key/value 操作的内存一致性。

*happens-before* 保证了在另一个线程中访问或删除该对象之前，当前线程将一个对象放入一个 *ConcurrentMap* 作为一个 key 或 value 。

为了证实这一点，让我们来看看一个内存不一致的情况:

```java
@Test
public void givenHashMap_whenSumParallel_thenError() throws Exception {
    Map<String, Integer> map = new HashMap<>();
    List<Integer> sumList = parallelSum100(map, 100);

    assertNotEquals(1, sumList
      .stream()
      .distinct()
      .count());
    long wrongResultCount = sumList
      .stream()
      .filter(num -> num != 100)
      .count();
    
    assertTrue(wrongResultCount > 0);
}

private List<Integer> parallelSum100(Map<String, Integer> map, 
  int executionTimes) throws InterruptedException {
    List<Integer> sumList = new ArrayList<>(1000);
    for (int i = 0; i < executionTimes; i++) {
        map.put("test", 0);
        ExecutorService executorService = 
          Executors.newFixedThreadPool(4);
        for (int j = 0; j < 10; j++) {
            executorService.execute(() -> {
                for (int k = 0; k < 10; k++)
                    map.computeIfPresent(
                      "test", 
                      (key, value) -> value + 1
                    );
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
        sumList.add(map.get("test"));
    }
    return sumList;
}
```

对于每个并行的 *map.computeIfPresent* 操作，*HashMap* 不提供一个一致的当前整数值视图，导致不一致和不期望的结果。

对于 *ConcurrentHashMap* ，我们可以得到一致且正确的结果： 

```java
@Test
public void givenConcurrentMap_whenSumParallel_thenCorrect() 
  throws Exception {
    Map<String, Integer> map = new ConcurrentHashMap<>();
    List<Integer> sumList = parallelSum100(map, 1000);

    assertEquals(1, sumList
      .stream()
      .distinct()
      .count());
    long wrongResultCount = sumList
      .stream()
      .filter(num -> num != 100)
      .count();
    
    assertEquals(0, wrongResultCount);
}
```
&nbsp;
### 3.2.Null Key/Value


大多数由 ConcurrentMap 提供的 API 不允许 *null* key或value，例如：

```java
@Test(expected = NullPointerException.class)
public void givenConcurrentHashMap_whenPutWithNullKey_thenThrowsNPE() {
    concurrentMap.put(null, new Object());
}

@Test(expected = NullPointerException.class)
public void givenConcurrentHashMap_whenPutNullValue_thenThrowsNPE() {
    concurrentMap.put("test", null);
}
```

然而，对于 compute 和 merge 操作，计算值可以是 null，这表示如果 key-value 映射存在，则删除，如果之前没有，则仍然没有 key-value 映射。

```java
@Test
public void givenKeyPresent_whenComputeRemappingNull_thenMappingRemoved() {
    Object oldValue = new Object();
    concurrentMap.put("test", oldValue);
    concurrentMap.compute("test", (s, o) -> null);

    assertNull(concurrentMap.get("test"));
}
```

&nbsp;

### 3.3. Stream 支持

Java 8 在 *ConcurrentHashMap* 中也提供了 *Stream* 支持。

与大多数流方法不同，批量 (顺序和并行) 操作允许安全地并发修改。 *ConcurrentModificationException* 不会被抛出，这也适用于它的迭代器。与流相关，几个 forEach*， *search*，和 *reduce* 方法也被添加，以支持更丰富的遍历和 map-reduce 操作。

&nbsp;

### 3.4. 性能

在底层，ConcurrentHashMap 有点类似于 HashMap ，基于哈希表进行数据访问和更新(尽管更复杂)。

当然，在大多数并发情况下，*ConcurrentHashMap* 应该为数据检索和更新提供更好的性能。

让我们为 *get* 和 *put* 的性能编写一个快速的微基准测试，并将其与 *Hashtable* 和 *Collections.synchronizedMap\** 比较测试，在4个线程中运行这 get 和 put 操作 500,000次。

```java
@Test
public void givenMaps_whenGetPut500KTimes_thenConcurrentMapFaster() 
  throws Exception {
    Map<String, Object> hashtable = new Hashtable<>();
    Map<String, Object> synchronizedHashMap = 
      Collections.synchronizedMap(new HashMap<>());
    Map<String, Object> concurrentHashMap = new ConcurrentHashMap<>();

    long hashtableAvgRuntime = timeElapseForGetPut(hashtable);
    long syncHashMapAvgRuntime = 
      timeElapseForGetPut(synchronizedHashMap);
    long concurrentHashMapAvgRuntime = 
      timeElapseForGetPut(concurrentHashMap);

    assertTrue(hashtableAvgRuntime > concurrentHashMapAvgRuntime);
    assertTrue(syncHashMapAvgRuntime > concurrentHashMapAvgRuntime);
}

private long timeElapseForGetPut(Map<String, Object> map) 
  throws InterruptedException {
    ExecutorService executorService = 
      Executors.newFixedThreadPool(4);
    long startTime = System.nanoTime();
    for (int i = 0; i < 4; i++) {
        executorService.execute(() -> {
            for (int j = 0; j < 500_000; j++) {
                int value = ThreadLocalRandom
                  .current()
                  .nextInt(10000);
                String key = String.valueOf(value);
                map.put(key, value);
                map.get(key);
            }
        });
    }
    executorService.shutdown();
    executorService.awaitTermination(1, TimeUnit.MINUTES);
    return (System.nanoTime() - startTime) / 500_000;
}
```

&nbsp;

请记住，微基准测试只关注一个场景，并不总是能很好地反映现实世界的性能。

也就是说，在一个具有平均开发系统的 OS X 系统上，我们可以看到连续运行 100 次的平均样本结果(以纳秒为单位)：

```plaintext
Hashtable: 1142.45
SynchronizedHashMap: 1273.89
ConcurrentHashMap: 230.2
```

在多线程环境中，期望多个线程访问一个公共的 *Map* ， *ConcurrentHashMap* 显然是更可取的。

然而，当 *Map* 只能被单个线程访问时，*HashMap* 因其简单和可靠的性能而成为更好的选择。

&nbsp;

>ConcurrentHashMap vs Hashtable HashMap, Collections.synchronizedMap() 读写测试： 
>
>写性能上，ConcurrentHashMap 比 HashTable , HashMap , Collections.synchronizedMap() 慢
>
>- ConcurrentHashMap 底层红黑树， cas 构建树慢
>
>ConcurrentHashMap 主要的优化点在读性能上
>
>- 速度快 20x+

&nbsp;

>- Collections.synchronizedMap(Map<K, V> m)
>  - 可以将 m 锁化， 可以通过参数可以细化到需要锁化对象
>  - 和 HashTable 性能上没有多大差异

&nbsp;

### **3.5. Pitfalls**

在 *ConcurrentHashMap* 中， 检索操作通常不会阻塞，并且可能与更新操作重叠。因此，为了获得更好的性能，它们只反映最近完成的更新操作的结果，如 [官方Javadoc](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ConcurrentHashMap.html) 所述。

还有一些其他的情况需要记住：

- 聚合状态方法的结果包括 *size*， *isEmpty* 和 *containsValue* 通常只有在 map 没有在其他线程中并发更新时才有用：

```java
@Test
public void givenConcurrentMap_whenUpdatingAndGetSize_thenError() 
  throws InterruptedException {
    Runnable collectMapSizes = () -> {
        for (int i = 0; i < MAX_SIZE; i++) {
            mapSizes.add(concurrentMap.size());
        }
    };
    Runnable updateMapData = () -> {
        for (int i = 0; i < MAX_SIZE; i++) {
            concurrentMap.put(String.valueOf(i), i);
        }
    };
    executorService.execute(updateMapData);
    executorService.execute(collectMapSizes);
    executorService.shutdown();
    executorService.awaitTermination(1, TimeUnit.MINUTES);

    assertNotEquals(MAX_SIZE, mapSizes.get(MAX_SIZE - 1).intValue());
    assertEquals(MAX_SIZE, concurrentMap.size());
}
```



如果并发更新受到严格的控制，聚合状态仍然是可靠的。

虽然这些聚合状态方法不能保证实时准确性，但对于监测或评估目的来说，它们可能足够了。

请注意，*ConcurrentHashMap* 的 *size()* 的用法应该被 *mappingCount()* 所取代，因为后者返回一个 *long* 的计数，尽管实际上它们是基于相同的估计。

- **hashCode matters**：注意，使用许多 key 与完全相同的 *hashCode()* 是一个确保减慢任何哈希表的性能的方法。

为了改善 key 具有*可比性*时的影响，*ConcurrentHashMap* 可以使用 key 之间的比较顺序来帮助打破联系。尽管如此，我们还是应该尽量避免使用相同的 *hashCode()*。

- 迭代器只设计用于单个线程，因为它们提供弱一致性而不是快速失败遍历，而且它们永远不会抛出 *ConcurrentModificationException.*
- 默认的初始表容量是 16，并根据指定的并发级别进行调整

&nbsp;

```java
public ConcurrentHashMap(
  int initialCapacity, float loadFactor, int concurrencyLevel) {
 
    //...
    if (initialCapacity < concurrencyLevel) {
        initialCapacity = concurrencyLevel;
    }
    //...
}
```

- 注意 remapping 函数: 虽然我们可以使用提供的 *compute* 和 *merge* 方法来进行重映射操作，但我们应该保持它们快速、简短和简单，并关注当前的映射，以避免意外阻塞。
- *ConcurrentHashMap* 中的 key 不是排序，所以当需要排序时，*ConcurrentSkipListMap*是一个合适的选择。

&nbsp;

## 4. ConcurrentNavigableMap

对于需要排序 key 的情况，可以使用 *ConcurrentSkipListMap*，它是 *TreeMap* 的并发版本。

作为对 *ConcurrentMap* 的补充，*ConcurrentNavigableMap* 支持 key 的总排序(默认为升序) 。返回 map 视图的方法将被重写，以实现并发兼容性:

- *subMap*
- *headMap*
- *tailMap*
- *subMap*
- *headMap*
- *tailMap*
- *descendingMap*

&nbsp;

keySet() 视图的 iterator 和 spliterator 通过 `week-memory-consistency` 增强:

- *navigableKeySet*
- *keySet*
- *descendingKeySet*

&nbsp;

## 5. ConcurrentSkipListMap

在前面，我们已经介绍了 *NavigableMap* 接口及其实现 [*TreeMap*](java-treemap.md)。 `ConcurrentSkipListMap` 可以看作是 *TreeMap* 的可扩展并发版本。

实际上，在 Java 中没有红黑树的并发实现。[*SkipList*](https://en.wikipedia.org/wiki/Skip_list) 的并发变量在 *ConcurrentSkipListMap* 中实现，为 *containsKey*， *get*， *put* 和 *remove* 操作及其变量提供了一个期望的平均日志(n) 时间成本。

除了 *TreeMap* 的特性之外，key 插入、删除、更新和访问操作都是通过线程安全来保证的。以下是与*TreeMap*在同时导航时的比较

```java
@Test
public void givenSkipListMap_whenNavConcurrently_thenCountCorrect() 
  throws InterruptedException {
    NavigableMap<Integer, Integer> skipListMap
      = new ConcurrentSkipListMap<>();
    int count = countMapElementByPollingFirstEntry(skipListMap, 10000, 4);
 
    assertEquals(10000 * 4, count);
}

@Test
public void givenTreeMap_whenNavConcurrently_thenCountError() 
  throws InterruptedException {
    NavigableMap<Integer, Integer> treeMap = new TreeMap<>();
    int count = countMapElementByPollingFirstEntry(treeMap, 10000, 4);
 
    assertNotEquals(10000 * 4, count);
}

private int countMapElementByPollingFirstEntry(
  NavigableMap<Integer, Integer> navigableMap, 
  int elementCount, 
  int concurrencyLevel) throws InterruptedException {
 
    for (int i = 0; i < elementCount * concurrencyLevel; i++) {
        navigableMap.put(i, i);
    }
    
    AtomicInteger counter = new AtomicInteger(0);
    ExecutorService executorService
      = Executors.newFixedThreadPool(concurrencyLevel);
    for (int j = 0; j < concurrencyLevel; j++) {
        executorService.execute(() -> {
            for (int i = 0; i < elementCount; i++) {
                if (navigableMap.pollFirstEntry() != null) {
                    counter.incrementAndGet();
                }
            }
        });
    }
    executorService.shutdown();
    executorService.awaitTermination(1, TimeUnit.MINUTES);
    return counter.get();
}
```

对幕后性能问题的完整解释超出了本文的范围。详细信息可以在 *ConcurrentSkipListMap* 的 Javadoc 中找到，它位于 *src.zip* 中的*java/util/concurrent* 下 

> 因 ConcurrentHashMap 本身实现复杂
>
> 所以没有基于它 TreeMap 相关的底层实现
>
> 因此设计了 ConcurrentSkipListMap<K, V> 
>
> - 底层是跳表机构

&nbsp;

## 6. 总结

在本文中，我们主要介绍了 *ConcurrentMap* 接口和 *ConcurrentHashMap* 的特性，并介绍了 *ConcurrentNavigableMap* 作为 key 排序所需的特性。
