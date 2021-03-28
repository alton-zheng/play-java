# An Introduction to Atomic Variables in Java

Last modified: July 17, 2020

by [Deep Jain](https://www.baeldung.com/author/deep-jain/)



- [Java](https://www.baeldung.com/category/java/)**+**

- [Java Concurrency](https://www.baeldung.com/tag/java-concurrency/)

### **Get started with Spring 5 and Spring Boot 2, through the \*Learn Spring\* course:**

**[>> CHECK OUT THE COURSE](https://www.baeldung.com/ls-course-start)**

If you have a few years of experience in the Java ecosystem, and you're interested in sharing that experience with the community (and getting paid for your work of course), have a look at the ["Write for Us" page](https://www.baeldung.com/contribution-guidelines). Cheers, Eugen

## **1. Introduction**

Simply put, a shared mutable state very easily leads to problems when concurrency is involved. If access to shared mutable objects is not managed properly, applications can quickly become prone to some hard-to-detect concurrency errors.

In this article, we'll revisit the use of locks to handle concurrent access, explore some of the disadvantages associated with locks, and finally, introduce atomic variables as an alternative.

## **2. Locks**

Let's have a look at the class:

```java
public class Counter {
    int counter; 
 
    public void increment() {
        counter++;
    }
}
```

In the case of a single-threaded environment, this works perfectly; however, as soon as we allow more than one thread to write, we start getting inconsistent results.

This is because of the simple increment operation (*counter++*), which may look like an atomic operation, but in fact is a combination of three operations: obtaining the value, incrementing, and writing the updated value back.

**If two threads try to get and update the value at the same time, it may result in lost updates.**

One of the ways to manage access to an object is to use locks. This can be achieved by using the *synchronized* keyword in the *increment* method signature. The *synchronized* keyword ensures that only one thread can enter the method at one time (to learn more about Locking and Synchronization refer to – [Guide to Synchronized Keyword in Java](https://www.baeldung.com/java-synchronized)):

```java
public class SafeCounterWithLock {
    private volatile int counter;
 
    public synchronized void increment() {
        counter++;
    }
}
```

Additionally, we need to add the *volatile* keyword to ensure proper reference visibility among threads.

**Using locks solves the problem. However, the performance takes a hit.**

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_0" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_0" width="300" height="250" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="9" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![Freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

When multiple threads attempt to acquire a lock, one of them wins, while the rest of the threads are either blocked or suspended.

**The process of suspending and then resuming a thread is very expensive** and affects the overall efficiency of the system.

In a small program, such as the *counter*, the time spent in context switching may become much more than actual code execution, thus greatly reducing overall efficiency.

## **3. Atomic Operations**

There is a branch of research focused on creating non-blocking algorithms for concurrent environments. These algorithms exploit low-level atomic machine instructions such as compare-and-swap (CAS), to ensure data integrity.

A typical CAS operation works on three operands:

1. The memory location on which to operate (M)
2. The existing expected value (A) of the variable
3. The new value (B) which needs to be set

**The CAS operation updates atomically the value in M to B, but only if the existing value in M matches A, otherwise no action is taken.**

In both cases, the existing value in M is returned. This combines three steps – getting the value, comparing the value, and updating the value – into a single machine level operation.

When multiple threads attempt to update the same value through CAS, one of them wins and updates the value. **However, unlike in the case of locks, no other thread gets suspended**; instead, they're simply informed that they did not manage to update the value. The threads can then proceed to do further work and context switches are completely avoided.

One other consequence is that the core program logic becomes more complex. This is because we have to handle the scenario when the CAS operation didn't succeed. We can retry it again and again till it succeeds, or we can do nothing and move on depending on the use case.

## **4. Atomic Variables in Java**

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_1" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_1" width="300" height="250" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="a" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![Freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

The most commonly used atomic variable classes in Java are [AtomicInteger](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/atomic/AtomicInteger.html), [AtomicLong](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/atomic/AtomicLong.html), [AtomicBoolean](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/atomic/AtomicBoolean.html), and [AtomicReference](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/atomic/AtomicReference.html). These classes represent an *int*, *long*, *boolean,* and object reference respectively which can be atomically updated. The main methods exposed by these classes are:

- *get()* – gets the value from the memory, so that changes made by other threads are visible; equivalent to reading a *volatile* variable
- *set()* – writes the value to memory, so that the change is visible to other threads; equivalent to writing a *volatile* variable
- *lazySet()* – eventually writes the value to memory, maybe reordered with subsequent relevant memory operations. One use case is nullifying references, for the sake of garbage collection, which is never going to be accessed again. In this case, better performance is achieved by delaying the null *volatile* write
- *compareAndSet()* – same as described in section 3, returns true when it succeeds, else false
- *weakCompareAndSet()* – same as described in section 3, but weaker in the sense, that it does not create happens-before orderings. This means that it may not necessarily see updates made to other variables. [As of Java 9](https://docs.oracle.com/javase/9/docs/api/java/util/concurrent/atomic/AtomicInteger.html#weakCompareAndSet-int-int-), this method has been deprecated in all atomic implementations in favor of [*weakCompareAndSetPlain()*](https://docs.oracle.com/javase/9/docs/api/java/util/concurrent/atomic/AtomicInteger.html#weakCompareAndSetPlain-int-int-). The memory effects of *weakCompareAndSet()* were plain but its names implied volatile memory effects. To avoid this confusion, they deprecated this method and added four methods with different memory effects such as *weakCompareAndSetPlain()* or [*weakCompareAndSetVolatile()*](https://docs.oracle.com/javase/9/docs/api/java/util/concurrent/atomic/AtomicInteger.html#weakCompareAndSetVolatile-int-int-)

A thread-safe counter implemented with *AtomicInteger* is shown in the example below:

```java
public class SafeCounterWithoutLock {
    private final AtomicInteger counter = new AtomicInteger(0);
    
    public int getValue() {
        return counter.get();
    }
    public void increment() {
        while(true) {
            int existingValue = getValue();
            int newValue = existingValue + 1;
            if(counter.compareAndSet(existingValue, newValue)) {
                return;
            }
        }
    }
}
```

As you can see, we retry the *compareAndSet* operation and again on failure, since we want to guarantee that the call to the *increment* method always increases the value by 1.

## **5. Conclusion**

In this quick tutorial, we described an alternate way of handling concurrency where disadvantages associated with locking can be avoided. We also looked at the main methods exposed by the atomic variable classes in Java.

As always, the examples are all available [over on GitHub](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-concurrency-advanced).

To explore more classes that internally use non-blocking algorithms refer to [a guide to ConcurrentMap](https://www.baeldung.com/java-concurrent-map).