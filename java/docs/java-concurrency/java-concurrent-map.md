# A Guide to ConcurrentMap

## **1. Overview**

*Maps* are naturally one of the most widely style of Java collection.

And, importantly, [*HashMap*](https://www.baeldung.com/java-hashmap) is not a thread-safe implementation, while *Hashtable* does provide thread-safety by synchronizing operations.

Even though *Hashtable* is thread safe, it is not very efficient. Another fully synchronized *Map,* *Collections.synchronizedMap,* does not exhibit great efficiency either. If we want thread-safety with high throughput under high concurrency, these implementations aren't the way to go.

To solve the problem, the *Java Collections Framework* **introduced \*ConcurrentMap\* in \*Java 1.5\*.**

The following discussions are based on *Java 1.8*.

* map *自然是最广泛的Java集合之一。

更重要的是，[*HashMap*](https://www.baeldung.com/java-hashmap)不是线程安全的实现，而*Hashtable*通过同步操作提供了线程安全。

即使*Hashtable*是线程安全的，它的效率也不是很高。另一个完全同步的*Map，* *集合。synchronizedMap，*也没有表现出很大的效率。如果我们想在高并发的情况下实现线程安全和高吞吐量，这些实现不是可行的。

为了解决这个问题，*Java集合框架** *在** Java 1.5 ** .**中引入了** ConcurrentMap **

下面的讨论是基于*Java 1.8*的。

## **2. \*ConcurrentMap\***

*ConcurrentMap* is an extension of the *Map* interface. It aims to provides a structure and guidance to solving the problem of reconciling throughput with thread-safety.

By overriding several interface default methods, *ConcurrentMap* gives guidelines for valid implementations to provide thread-safety and memory-consistent atomic operations.

Several default implementations are overridden, disabling the *null* key/value support:

- *getOrDefault*
- *forEach*
- *replaceAll*
- *computeIfAbsent*
- *computeIfPresent*
- *compute*
- *merge*

The following *APIs* are also overridden to support atomicity, without a default interface implementation:

- *putIfAbsent*
- *remove*
- *replace(key, oldValue, newValue)*
- *replace(key, value)![freestar](https://a.pub.network/core/imgs/fslogo-green.svg)

The rest of actions are directly inherited with basically consistent with *Map*.

## **3. \*ConcurrentHashMap\***

*ConcurrentHashMap* is the out-of-box ready *ConcurrentMap* implementation.

For better performance, it consists of an array of nodes as table buckets (used to be table segments prior to *Java 8*) under the hood, and mainly uses [CAS](https://en.wikipedia.org/wiki/Compare-and-swap) operations during updating.

The table buckets are initialized lazily, upon the first insertion. Each bucket can be independently locked by locking the very first node in the bucket. Read operations do not block, and update contentions are minimized.

The number of segments required is relative to the number of threads accessing the table so that the update in progress per segment would be no more than one most of time.

**Before \*Java 8\*, the number of “segments” required was relative to the number of threads accessing the table so that the update in progress per segment would be no more than one most of time.**

That's why constructors, compared to *HashMap*, provides the extra *concurrencyLevel* argument to control the number of estimated threads to use:

```java
public ConcurrentHashMap(
public ConcurrentHashMap(
 int initialCapacity, float loadFactor, int concurrencyLevel)
```

The other two arguments: *initialCapacity* and *loadFactor* worked quite the same [as *HashMap*](https://www.baeldung.com/java-hashmap).

**However, since \*Java 8\*, the constructors are only present for backward compatibility: the parameters can only affect the initial size of the map**.

### **3.1. Thread-Safety**

*ConcurrentMap* guarantees memory consistency on key/value operations in a multi-threading environment.

Actions in a thread prior to placing an object into a *ConcurrentMap* as a key or value *happen-before* actions subsequent to the access or removal of that object in another thread.

To confirm, let's have a look at a memory inconsistent case:

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

For each *map.computeIfPresent* action in parallel, *HashMap* does not provide a consistent view of what should be the present integer value, leading to inconsistent and undesirable results.

As for *ConcurrentHashMap*, we can get a consistent and correct result:

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

### **3.2. \*Null\* Key/Value**

Most *API*s provided by *ConcurrentMap* does not allow *null* key or value, for example:

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

However, **for \*compute\** and \*merge\* actions, the computed value can be \*null\*, which indicates the key-value mapping is removed if present or remains absent if previously absent**.

```java
@Test
public void givenKeyPresent_whenComputeRemappingNull_thenMappingRemoved() {
    Object oldValue = new Object();
    concurrentMap.put("test", oldValue);
    concurrentMap.compute("test", (s, o) -> null);

    assertNull(concurrentMap.get("test"));
}
```

### **3.3. Stream Support**

*Java 8* provides *Stream* support in the *ConcurrentHashMap* as well.

Unlike most stream methods, the bulk (sequential and parallel) operations allow concurrent modification safely. *ConcurrentModificationException* won't be thrown, which also applies to its iterators. Relevant to streams, several *forEach**, *search*, and *reduce** methods are also added to support richer traversal and map-reduce operations.

### **3.4. Performance**

**Under the hood, \*ConcurrentHashMap\* is somewhat similar to \*HashMap\***, with data access and update based on a hash table (though more complex).

And of course, the *ConcurrentHashMap* should yield much better performance in most concurrent cases for data retrieval and update.

Let's write a quick micro-benchmark for *get* and *put* performance and compare that to *Hashtable* and *Collections.synchronizedMap*, running both operations for 500,000 times in 4 threads.

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

Keep in mind micro-benchmarks are only looking at a single scenario and aren't always a good reflection of real world performance.

That being said, on an OS X system with an average dev system, we're seeing an average sample result for 100 consecutive runs (in nanoseconds):

```plaintext
Hashtable: 1142.45
SynchronizedHashMap: 1273.89
ConcurrentHashMap: 230.2
```

In a multi-threading environment, where multiple threads are expected to access a common *Map*, the *ConcurrentHashMap* is clearly preferable.

However, when the *Map* is only accessible to a single thread, *HashMap* can be a better choice for its simplicity and solid performance.

### **3.5. Pitfalls**

Retrieval operations generally do not block in *ConcurrentHashMap* and could overlap with update operations. So for better performance, they only reflect the results of the most recently completed update operations, as stated in the [official Javadoc](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ConcurrentHashMap.html).

There are several other facts to bear in mind:

- results of aggregate status methods including *size*, *isEmpty*, and *containsValue* are typically useful only when a map is not undergoing concurrent updates in other threads:

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

If concurrent updates are under strict control, aggregate status would still be reliable.

Although these **aggregate status methods do not guarantee the real-time accuracy, they may be adequate for monitoring or estimation purposes**.

Note that usage of *size()* of *ConcurrentHashMap* should be replaced by *mappingCount()*, for the latter method returns a *long* count, although deep down they are based on the same estimation.

- ***hashCode\* matters**: note that using many keys with exactly the same *hashCode()* is a sure way to slow down a performance of any hash table.

To ameliorate impact when keys are *Comparable*, *ConcurrentHashMap* may use comparison order among keys to help break ties. Still, we should avoid using the same *hashCode()* as much as we can.

- iterators are only designed to use in a single thread as they provide weak consistency rather than fast-fail traversal, and they will never throw *ConcurrentModificationException.*
- the default initial table capacity is 16, and it's adjusted by the specified concurrency level:

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

- caution on remapping functions: though we can do remapping operations with provided *compute* and *merge** methods, we should keep them fast, short and simple, and focus on the current mapping to avoid unexpected blocking.
- keys in *ConcurrentHashMap* are not in sorted order, so for cases when ordering is required, *ConcurrentSkipListMap* is a suitable choice.

## **4. \*ConcurrentNavigableMap\***

For cases when ordering of keys is required, we can use *ConcurrentSkipListMap*, a concurrent version of *TreeMap*.

As a supplement for *ConcurrentMap*, *ConcurrentNavigableMap* supports total ordering of its keys (in ascending order by default) and is concurrently navigable. Methods that return views of the map are overridden for concurrency compatibility:

- *subMap*
- *headMap*
- *tailMap*
- *subMap*
- *headMap*
- *tailMap*
- *descendingMap*

*keySet()* views' iterators and spliterators are enhanced with weak-memory-consistency:

- *navigableKeySet*
- *keySet*
- *descendingKeySet*

## **5. \*ConcurrentSkipListMap\***

Previously, we have covered *NavigableMap* interface and its implementation [*TreeMap*](https://www.baeldung.com/java-treemap). *ConcurrentSkipListMap* can be seen a scalable concurrent version of *TreeMap*.

In practice, there's no concurrent implementation of the red-black tree in Java. A concurrent variant of [*SkipLists*](https://en.wikipedia.org/wiki/Skip_list) is implemented in *ConcurrentSkipListMap*, providing an expected average log(n) time cost for the *containsKey*, *get*, *put* and *remove* operations and their variants.

In addition to *TreeMap*‘s features, key insertion, removal, update and access operations are guaranteed with thread-safety. Here's a comparison to *TreeMap* when navigating concurrently:

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

A full explanation of the performance concerns behind the scenes is beyond the scope of this article. The details can be found in *ConcurrentSkipListMap's* Javadoc, which is located under *java/util/concurrent* in the *src.zip* file.

## **6. Conclusion**

In this article, we mainly introduced the *ConcurrentMap* interface and the features of *ConcurrentHashMap* and covered on *ConcurrentNavigableMap* being key-ordering required.

The full source code for all the examples used in this article can be found [in the GitHub project](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-concurrency-collections).