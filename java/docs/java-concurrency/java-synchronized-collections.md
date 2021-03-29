# An Introduction to Synchronized Java Collections

## **1. Overview**

The [collections framework](https://docs.oracle.com/javase/8/docs/technotes/guides/collections/overview.html) is a key component of Java. It provides an extensive number of interfaces and implementations, which allows us to create and manipulate different types of collections in a straightforward manner.

Although using plain unsynchronized collections is simple overall, it can also become a daunting and error-prone process when working in multi-threaded environments (a.k.a. concurrent programming).

Hence, the Java platform provides strong support for this scenario through different synchronization *wrappers* implemented within the *[Collections](https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html#synchronizedCollection(java.util.Collection))* class.

These wrappers make it easy to create synchronized views of the supplied collections by means of several static factory methods.

In this tutorial, **we'll take a deep dive into these \**static synchronization wrappers. Also, we'll highlight the difference between synchronized collections and concurrent collections\**.**

[collections framework](https://docs.oracle.com/javase/8/docs/technotes/guides/collections/overview.html)是Java的一个关键组件。它提供了大量的接口和实现，这允许我们以一种直接的方式创建和操作不同类型的集合。

虽然使用普通的非同步集合总体上很简单，但在多线程环境(也就是并发编程)中，它也可能成为一个令人生畏且容易出错的过程。

因此，Java平台通过在*[Collections](https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html#synchronizedCollection(java.util.Collection))*类)中实现的不同同步*包装*为这种场景提供了强大的支持。

通过这些包装器，可以很容易地通过几个静态工厂方法创建所提供集合的同步视图。

在本教程中，我们将深入研究这些静态同步包装器。同样，我们将强调同步集合和并发集合之间的区别\**。

## **2. The \*synchronizedCollection()\* Method**

The first synchronization wrapper that we'll cover in this round-up is the *synchronizedCollection()* method. As the name suggests, **it returns a thread-safe collection backed up by the specified [\*Collection\*](https://docs.oracle.com/javase/8/docs/api/java/util/Collection.html)**.

Now, to understand more clearly how to use this method, let's create a basic unit test:

我们将在本文中介绍的第一个同步包装器是*synchronizedCollection()*方法。顾名思义，**它返回一个线程安全的集合，该集合由指定的[\* collection \*](https://docs.oracle.com/javase/8/docs/api/java/util/Collection.html)**。

现在，为了更清楚地理解如何使用这个方法，让我们创建一个基本的单元测试:

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

As shown above, creating a synchronized view of the supplied collection with this method is very simple.

To demonstrate that the method actually returns a thread-safe collection, we first create a couple of threads.

如上所示，使用此方法创建所提供集合的同步视图非常简单。

为了演示该方法实际上返回一个线程安全的集合，我们首先创建两个线程。

After that, we then inject a [*Runnable*](https://docs.oracle.com/javase/8/docs/api/java/lang/Runnable.html) instance into their constructors, in the form of a lambda expression. Let's keep in mind that *Runnable* is a functional interface, so we can replace it with a lambda expression.

Lastly, we just check that each thread effectively adds six elements to the synchronized collection, so its final size is twelve.

然后，我们以lambda表达式的形式将[*Runnable*](https://docs.oracle.com/javase/8/docs/api/java/lang/Runnable.html)实例注入到它们的构造函数中。让我们记住，*Runnable*是一个函数接口，所以我们可以用lambda表达式替换它。

最后，我们只是检查每个线程是否有效地向同步集合添加了6个元素，因此它的最终大小是12个。

## **3. The \*synchronizedList()\* Method**

Likewise, similar to the *synchronizedCollection()* method, we can use the *synchronizedList()* wrapper to create a synchronized [*List*](https://docs.oracle.com/javase/8/docs/api/java/util/List.html?is-external=true).

As we might expect, **the method returns a thread-safe view of the specified \*List\*:**

同样，与*synchronizedCollection()*方法类似，我们可以使用*synchronizedList()*包装器来创建一个synchronized [*List*](https://docs.oracle.com/javase/8/docs/api/java/util/List.html?is-external=true)。

正如我们所期望的，**该方法返回指定的\*List\*:**的线程安全视图

```java
List<Integer> syncList = Collections.synchronizedList(new ArrayList<>());
```

Unsurprisingly, the use of the *synchronizedList()* method looks nearly identical to its higher-level counterpart, *synchronizedCollection()*.

Therefore, as we just did in the previous unit test, once that we've created a synchronized *List*, we can spawn several threads. After doing that, we'll use them to access/manipulate the target *List* in a thread-safe fashion.

In addition, if we want to iterate over a synchronized collection and prevent unexpected results, we should explicitly provide our own thread-safe implementation of the loop. Hence, we could achieve that using a *synchronized* block:

不出所料，*synchronizedList()*方法的使用与它的高级对等体*synchronizedCollection()*几乎相同。

因此，正如我们在前面的单元测试中所做的那样，一旦我们创建了一个同步的*列表*，我们就可以生成几个线程。之后，我们将使用它们以线程安全的方式访问/操作目标*列表*。

此外，如果我们想遍历一个同步集合并防止出现意外结果，我们应该显式地提供我们自己的线程安全的循环实现。因此，我们可以使用一个*synchronized*块来实现:

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

In all cases where we need to iterate over a synchronized collection, we should implement this idiom. This is because the iteration on a synchronized collection is performed through multiple calls into the collection. Therefore they need to be performed as a single atomic operation.

**The use of the \*synchronized\* block ensures the atomicity of the operation**.

在所有需要遍历同步集合的情况下，我们都应该实现这个习惯用法。这是因为同步集合上的迭代是通过对该集合的多个调用来执行的。因此，它们需要作为单个原子操作来执行。

synchronized块的使用保证了操作的原子性。

## **4. The \*synchronizedMap()\* Method**

The *Collections* class implements another neat synchronization wrapper, called *synchronizedMap().* We could use it for easily creating a synchronized [*Map*](https://docs.oracle.com/javase/8/docs/api/java/util/Map.html).

**The method returns a thread-safe view of the supplied \*Map\* implementation**:

Collections*类实现了另一个整洁的同步包装器，称为*synchronizedMap()。*我们可以使用它轻松地创建一个同步的[*Map*](https://docs.oracle.com/javase/8/docs/api/java/util/Map.html)。

该方法返回一个线程安全的视图，该视图是提供的Map实现的。

```java
Map<Integer, String> syncMap = Collections.synchronizedMap(new HashMap<>());
```

## **5. The \*synchronizedSortedMap()\* Method**

There's also a counterpart implementation of the *synchronizedMap()* method. It is called *synchronizedSortedMap()*, which we can use for creating a synchronized *[SortedMap](https://docs.oracle.com/javase/8/docs/api/java/util/SortedMap.html)* instance:

还有一个*synchronizedMap()*方法的对等实现。它被称为*synchronizedSortedMap()*，我们可以用它来创建一个同步的*[SortedMap](https://docs.oracle.com/javase/8/docs/api/java/util/SortedMap.html)* instance:

```java
Map<Integer, String> syncSortedMap = Collections.synchronizedSortedMap(new TreeMap<>());
```

## **6. The \*synchronizedSet()\* Method**

Next, moving on in this review, we have the *synchronizedSet()* method. As its name implies, it allows us to create synchronized *[Sets](https://docs.oracle.com/javase/8/docs/api/java/util/Set.html)* with minimal fuss.

**The wrapper returns a thread-safe collection backed by the specified \*Set\***:

接下来，在这篇回顾中，我们有*synchronizedSet()*方法。顾名思义，它允许我们以最小的麻烦创建synchronized *[Sets](https://docs.oracle.com/javase/8/docs/api/java/util/Set.html)*)。

**包装器返回一个由指定的\*Set\***支持的线程安全集合:

```java
Set<Integer> syncSet = Collections.synchronizedSet(new HashSet<>());
```

## **7. The \*synchronizedSortedSet()\* Method**

Finally, the last synchronization wrapper that we'll showcase here is *synchronizedSortedSet()*.

Similar to other wrapper implementations that we've reviewed so far, **the method returns a thread-safe version of the given \*[SortedSet](https://docs.oracle.com/javase/8/docs/api/java/util/SortedSet.html)\***:

最后，我们在这里展示的最后一个同步包装器是*synchronizedSortedSet()*。

与我们到目前为止讨论的其他包装器实现类似，**该方法返回给定的\*[SortedSet](https://docs.oracle.com/javase/8/docs/api/java/util/SortedSet.html)\***的线程安全版本:

```java
SortedSet<Integer> syncSortedSet = Collections.synchronizedSortedSet(new TreeSet<>());
```

## **8. Synchronized vs Concurrent Collections**

Up to this point, we took a closer look at the collections framework's synchronization wrappers.

Now, let's focus on **the differences between synchronized collections and concurrent collections**, such as [*ConcurrentHashMap*](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ConcurrentHashMap.html) and *[BlockingQueue](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html)* implementations.

到目前为止，我们仔细研究了collections框架的同步包装器。

现在,让我们关注* *同步的集合之间的差异和并发集合* *,如(* ConcurrentHashMap *) (https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ConcurrentHashMap.html)和* (BlockingQueue) (https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html) *实现。

### **8.1. Synchronized Collections**

**Synchronized collections achieve thread-safety through [intrinsi](https://docs.oracle.com/javase/tutorial/essential/concurrency/locksync.html)[c locking](https://docs.oracle.com/javase/tutorial/essential/concurrency/locksync.html), and the entire collections are locked**. Intrinsic locking is implemented via synchronized blocks within the wrapped collection's methods.

As we might expect, synchronized collections assure data consistency/integrity in multi-threaded environments. However, they might come with a penalty in performance, as only one single thread can access the collection at a time (a.k.a. synchronized access).

For a detailed guide on how to use *synchronized* methods and blocks, please check [our article](https://www.baeldung.com/java-synchronized) on the topic.

**同步集合通过[intrinsic](https://docs.oracle.com/javase/tutorial/essential/concurrency/locksync.html)[c locking](https://docs.oracle.com/javase/tutorial/essential/concurrency/locksync.html)实现线程安全，并且整个集合被锁定**。内在锁是通过包装的集合方法中的同步块实现的。

正如我们所期望的那样，同步集合确保了多线程环境中的数据一致性/完整性。然而，它们可能会带来性能上的损失，因为一次只能有一个线程访问集合(也就是同步访问)。

关于如何使用*synchronized*方法和块的详细指南，请查看[我们的文章](https://www.baeldung.com/java-synchronized)这个主题。

### **8.2. Concurrent Collections**

**Concurrent collections (e.g. \*ConcurrentHashMap),\* achieve thread-safety by dividing their data into segments**. In a *ConcurrentHashMap*, for example, different threads can acquire locks on each segment, so multiple threads can access the *Map* at the same time (a.k.a. concurrent access).

Concurrent collections are **much more performant than synchronized collections**, due to the inherent advantages of concurrent thread access.

So, the choice of what type of thread-safe collection to use depends on the requirements of each use case, and it should be evaluated accordingly.

**并发集合(例如，\* ConcurrentHashMap)，\*通过将它们的数据分割成段来实现线程安全。例如，在*ConcurrentHashMap*中，不同的线程可以在每个段上获得锁，所以多个线程可以同时访问*Map*(也就是并发访问)。

由于并发线程访问的固有优势，并发集合比同步集合的性能要好得多。

因此，选择使用哪种类型的线程安全集合取决于每个用例的需求，应该相应地对其进行评估。

## **9. Conclusion**

**In this article, we took an in-depth look at the set of synchronization wrappers implemented within the \*Collections\* class**.

Additionally, we highlighted the differences between synchronized and concurrent collections, and also looked at the approaches they implement for achieving thread-safety.

As usual, all the code samples shown in this article are available over on [GitHub](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-collections).

在本文中，我们深入研究了在\*Collections\*类*中实现的一组同步包装器。

此外，我们还强调了同步集合和并发集合之间的区别，并研究了它们实现线程安全的方法。

和往常一样，本文中显示的所有代码示例都可以在[GitHub](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-collections)上获得。