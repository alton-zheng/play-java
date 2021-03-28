# Binary Semaphore vs Reentrant Lock

Last modified: January 10, 2021

by [Anshul Bansal](https://www.baeldung.com/author/anshulbansal/)



- [Java](https://www.baeldung.com/category/java/)**+**

- [Java Concurrency](https://www.baeldung.com/tag/java-concurrency/)

### **Get started with Spring 5 and Spring Boot 2, through the \*Learn Spring\* course:**

**[>> CHECK OUT THE COURSE](https://www.baeldung.com/ls-course-start)**

If you have a few years of experience in the Java ecosystem, and you're interested in sharing that experience with the community (and getting paid for your work of course), have a look at the ["Write for Us" page](https://www.baeldung.com/contribution-guidelines). Cheers, Eugen

## 1. Overview

In this tutorial, we'll explore binary semaphores and reentrant locks. Also, we'll compare them against each other to see which one is best suited in common situations.

## 2. What Is a Binary Semaphore?

A binary [semaphore](https://www.baeldung.com/java-semaphore) provides a signaling mechanism over the access of a single resource. In other words, a binary semaphore **provides a mutual exclusion that allows only one thread to access a critical section at a time**.

For that, it keeps only one permit available for access. Hence, **a binary semaphore has only two states: one permit available or zero permits available**.

Let's discuss a simple [implementation of a binary semaphore](https://www.baeldung.com/java-semaphore#mutex) using the [*Semaphore*](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Semaphore.html) class available in Java:

```java
Semaphore binarySemaphore = new Semaphore(1);
try {
    binarySemaphore.acquire();
    assertEquals(0, binarySemaphore.availablePermits());
} catch (InterruptedException e) {
    e.printStackTrace();
} finally {
    binarySemaphore.release();
    assertEquals(1, binarySemaphore.availablePermits());
}
```

Here, we can observe that the *acquire* method decreases the available permits by one. Similarly, the *release* method increases the available permits by one.

Additionally, the *Semaphore* class provides the *fairness* parameter. When set to *true*, the *fairness* parameter ensures the order in which the requesting threads acquire permits (based on their waiting time):

```java
Semaphore binarySemaphore = new Semaphore(1, true);
```

## 3. What Is a Reentrant Lock?

A [reentrant lock is a mutual exclusion mechanism](https://www.baeldung.com/java-concurrent-locks#lock-implementations) that **allows threads to reenter into a lock on a resource (multiple times) without a deadlock situation**.

A thread entering into the lock increases the hold count by one every time. Similarly, the hold count decreases when unlock is requested. Therefore, **a resource is locked until the counter returns to zero**.

For instance, let's look at a simple implementation using the [*ReentrantLock*](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/locks/ReentrantLock.html) class available in Java:

```java
ReentrantLock reentrantLock = new ReentrantLock();
try {
    reentrantLock.lock();
    assertEquals(1, reentrantLock.getHoldCount());
    assertEquals(true, reentrantLock.isLocked());
} finally {
    reentrantLock.unlock();
    assertEquals(0, reentrantLock.getHoldCount());
    assertEquals(false, reentrantLock.isLocked());
}
```

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_0" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_0" width="970" height="90" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="5" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![Freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

Here, the *lock* method increases the hold count by one and locks the resource. Similarly, the *unlock* method decreases the hold count and unlocks a resource if the hold count is zero.

When a thread reenters the lock, it has to request for the unlock the same number of times to release the resource:

```java
reentrantLock.lock();
reentrantLock.lock();
assertEquals(2, reentrantLock.getHoldCount());
assertEquals(true, reentrantLock.isLocked());

reentrantLock.unlock();
assertEquals(1, reentrantLock.getHoldCount());
assertEquals(true, reentrantLock.isLocked());

reentrantLock.unlock();
assertEquals(0, reentrantLock.getHoldCount());
assertEquals(false, reentrantLock.isLocked());
```

Similar to the *Semaphore* class, the *ReentrantLock* class also supports the *fairness* parameter:

```java
ReentrantLock reentrantLock = new ReentrantLock(true);
```

## 4. Binary Semaphore vs. Reentrant Lock

### 4.1. Mechanism

A **binary semaphore is a type of signaling mechanism**, whereas a reentrant lock is a locking mechanism.

### 4.2. Ownership

No thread is the owner of a binary semaphore. However, **the last thread that successfully locked a resource is the owner of a reentrant lock**.

### 4.3. Nature

Binary semaphores are non-reentrant by nature, implying that the same thread can't re-acquire a critical section, else it will lead to a deadlock situation.

On the other side, a reentrant lock, by nature, allows reentering a lock by the same thread multiple times.

### 4.4. Flexibility

A **binary semaphore provides a higher-level synchronization mechanism** by allowing a custom implementation of a locking mechanism and deadlock recovery. Thus, it gives more control to the developers.

However, **the reentrant lock is a low-level synchronization with a fixed locking mechanism**.

### 4.5. Modification

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_1" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_1" width="970" height="90" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="6" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![Freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

Binary semaphores support operations like wait and signal (acquire and release in the case of Java's *Semaphore* class) to allow modification of the available permits by any process.

On the other hand, only the same thread that locked/unlocked a resource can modify a reentrant lock.

### 4.6. Deadlock Recovery

**Binary semaphores provide a non-ownership release mechanism**. Therefore, any thread can release the permit for a deadlock recovery of a binary semaphore.

On the contrary, deadlock recovery is difficult to achieve in the case of a reentrant lock. For instance, if the owner thread of a reentrant lock goes into sleep or infinite wait, it won't be possible to release the resource, and a deadlock situation will result.

## 5. Conclusion

In this short article, we've explored binary semaphore and reentrant locks.

First, we discussed the basic definition of a binary semaphore and a reentrant lock, along with a basic implementation in Java. Then, we compared them against each other based on a few parameters like mechanism, ownership, and flexibility.

We can certainly conclude that **a binary semaphore provides a non-ownership-based signaling mechanism for mutual exclusion**. At the same time, it can be further extended to provide locking capabilities with easy deadlock recovery.

On the other hand, **a reentrant lock provides a reentrant mutual exclusion with owner-based locking capabilities** and is useful as a simple mutex.

As usual, the source code is available [over on GitHub](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-concurrency-advanced-4).