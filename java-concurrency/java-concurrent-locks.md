# Guide to java.util.concurrent.Locks

Last modified: April 27, 2020

by [baeldung](https://www.baeldung.com/author/baeldung/)



- [Java](https://www.baeldung.com/category/java/)**+**

- [Java Concurrency](https://www.baeldung.com/tag/java-concurrency/)

### **Get started with Spring 5 and Spring Boot 2, through the \*Learn Spring\* course:**

**[>> CHECK OUT THE COURSE](https://www.baeldung.com/ls-course-start)**

If you have a few years of experience in the Java ecosystem, and you're interested in sharing that experience with the community (and getting paid for your work of course), have a look at the ["Write for Us" page](https://www.baeldung.com/contribution-guidelines). Cheers, Eugen

 

## **1. Overview**

Simply put, a lock is a more flexible and sophisticated thread synchronization mechanism than the standard *synchronized* block.

The *Lock* interface has been around since Java 1.5. It's defined inside the *java.util.concurrent.lock* package and it provides extensive operations for locking.

In this article, we'll explore different implementations of the *Lock* interface and their applications.

## **2. Differences Between Lock and Synchronized Block**

There are few differences between the use of synchronized *block* and using *Lock* API's:

- **A \*synchronized\* \*block\* is fully contained within a method –** we can have *Lock* API's *lock()* and *unlock()* operation in separate methods
- A s*ynchronized block* doesn't support the fairness, any thread can acquire the lock once released, no preference can be specified. **We can achieve fairness within the \*Lock\* APIs by specifying the \*fairness\* property**. It makes sure that longest waiting thread is given access to the lock
- A thread gets blocked if it can't get an access to the synchronized *block*. **The \*Lock\* API provides \*tryLock()\* method. The thread acquires lock only if it's available and not held by any other thread.** This reduces blocking time of thread waiting for the lock
- A thread which is in “waiting” state to acquire the access to *synchronized block*, can't be interrupted. **The \*Lock\* API provides a method \*lockInterruptibly()\* which can be used to interrupt the thread when it's waiting for the lock**

## **3. \*Lock\* API**

Let's take a look at the methods in the *Lock* interface:

- ***void lock()** –* acquire the lock if it's available; if the lock isn't available a thread gets blocked until the lock is released
- ***void lockInterruptibly()\*** – this is similar to the *lock(),* but it allows the blocked thread to be interrupted and resume the execution through a thrown *java.lang.InterruptedException*
- ***boolean tryLock()\*** – this is a non-blocking version of *lock()* method; it attempts to acquire the lock immediately, return true if locking succeeds
- ***boolean tryLock(long timeout, TimeUnit timeUnit)** –* this is similar to *tryLock(),* except it waits up the given timeout before giving up trying to acquire the *Lock*
- **void \*unlock()\*** – unlocks the *Lock* instance

A locked instance should always be unlocked to avoid deadlock condition. A recommended code block to use the lock should contain a *try/catch* and *finally* block:

```java
Lock lock = ...; 
lock.lock();
try {
    // access to the shared resource
} finally {
    lock.unlock();
}
```

In addition to the *Lock* interface*,* we have a *ReadWriteLock* interface which maintains a pair of locks, one for read-only operations, and one for the write operation. The read lock may be simultaneously held by multiple threads as long as there is no write.

*ReadWriteLock* declares methods to acquire read or write locks:

- ***Lock readLock()** –* returns the lock that's used for reading
- ***Lock writeLock()\*** – returns the lock that's used for writing

## **4. Lock Implementations**

### **4.1. \*ReentrantLock\***

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_0" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_0" width="300" height="250" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" sandbox="allow-forms allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts allow-top-navigation-by-user-activation" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="9" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![Freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

*ReentrantLock* class implements the *Lock* interface. It offers the same concurrency and memory semantics, as the implicit monitor lock accessed using *synchronized* methods and statements, with extended capabilities.

Let's see, how we can use *ReenrtantLock for* synchronization:

```java
public class SharedObject {
    //...
    ReentrantLock lock = new ReentrantLock();
    int counter = 0;

    public void perform() {
        lock.lock();
        try {
            // Critical section here
            count++;
        } finally {
            lock.unlock();
        }
    }
    //...
}
```

We need to make sure that we are wrapping the *lock*() and the *unlock()* calls in the *try-finally* block to avoid the deadlock situations.

Let's see how the *tryLock()* works:

```java
public void performTryLock(){
    //...
    boolean isLockAcquired = lock.tryLock(1, TimeUnit.SECONDS);
    
    if(isLockAcquired) {
        try {
            //Critical section here
        } finally {
            lock.unlock();
        }
    }
    //...
}
```

In this case, the thread calling *tryLock(),* will wait for one second and will give up waiting if the lock isn't available.

### **4.2. \*ReentrantReadWriteLock\***

*ReentrantReadWriteLock* class implements the *ReadWriteLock* interface.

Let's see rules for acquiring the *ReadLock* or *WriteLock* by a thread:

- **Read Lock** – if no thread acquired the write lock or requested for it then multiple threads can acquire the read lock
- **Write Lock** – if no threads are reading or writing then only one thread can acquire the write lock

Let's see how to make use of the *ReadWriteLock*:

```java
public class SynchronizedHashMapWithReadWriteLock {

    Map<String,String> syncHashMap = new HashMap<>();
    ReadWriteLock lock = new ReentrantReadWriteLock();
    // ...
    Lock writeLock = lock.writeLock();

    public void put(String key, String value) {
        try {
            writeLock.lock();
            syncHashMap.put(key, value);
        } finally {
            writeLock.unlock();
        }
    }
    ...
    public String remove(String key){
        try {
            writeLock.lock();
            return syncHashMap.remove(key);
        } finally {
            writeLock.unlock();
        }
    }
    //...
}
```

For both the write methods, we need to surround the critical section with the write lock, only one thread can get access to it:

```java
Lock readLock = lock.readLock();
//...
public String get(String key){
    try {
        readLock.lock();
        return syncHashMap.get(key);
    } finally {
        readLock.unlock();
    }
}

public boolean containsKey(String key) {
    try {
        readLock.lock();
        return syncHashMap.containsKey(key);
    } finally {
        readLock.unlock();
    }
}
```

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_1" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_1" width="300" height="250" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="a" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![Freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

For both read methods, we need to surround the critical section with the read lock. Multiple threads can get access to this section if no write operation is in progress.

### **4.3. \*StampedLock\***

*StampedLock* is introduced in Java 8. It also supports both read and write locks. However, lock acquisition methods return a stamp that is used to release a lock or to check if the lock is still valid:

```java
public class StampedLockDemo {
    Map<String,String> map = new HashMap<>();
    private StampedLock lock = new StampedLock();

    public void put(String key, String value){
        long stamp = lock.writeLock();
        try {
            map.put(key, value);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public String get(String key) throws InterruptedException {
        long stamp = lock.readLock();
        try {
            return map.get(key);
        } finally {
            lock.unlockRead(stamp);
        }
    }
}
```

Another feature provided by *StampedLock* is optimistic locking. Most of the time read operations don't need to wait for write operation completion and as a result of this, the full-fledged read lock isn't required.

Instead, we can upgrade to read lock:

```java
public String readWithOptimisticLock(String key) {
    long stamp = lock.tryOptimisticRead();
    String value = map.get(key);

    if(!lock.validate(stamp)) {
        stamp = lock.readLock();
        try {
            return map.get(key);
        } finally {
            lock.unlock(stamp);               
        }
    }
    return value;
}
```

## **5. Working With \*Conditions\***

The *Condition* class provides the ability for a thread to wait for some condition to occur while executing the critical section.

This can occur when a thread acquires the access to the critical section but doesn't have the necessary condition to perform its operation. For example, a reader thread can get access to the lock of a shared queue, which still doesn't have any data to consume.

Traditionally Java provides *wait(), notify() and notifyAll()* methods for thread intercommunication. *Conditions* have similar mechanisms, but in addition, we can specify multiple conditions:

```java
public class ReentrantLockWithCondition {

    Stack<String> stack = new Stack<>();
    int CAPACITY = 5;

    ReentrantLock lock = new ReentrantLock();
    Condition stackEmptyCondition = lock.newCondition();
    Condition stackFullCondition = lock.newCondition();

    public void pushToStack(String item){
        try {
            lock.lock();
            while(stack.size() == CAPACITY) {
                stackFullCondition.await();
            }
            stack.push(item);
            stackEmptyCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public String popFromStack() {
        try {
            lock.lock();
            while(stack.size() == 0) {
                stackEmptyCondition.await();
            }
            return stack.pop();
        } finally {
            stackFullCondition.signalAll();
            lock.unlock();
        }
    }
}
```

## **6. Conclusion**

In this article, we have seen different implementations of the *Lock* interface and the newly introduced *StampedLock* class. We also explored how we can make use of the *Condition* class to work with multiple conditions.

The complete code for this tutorial is available [over on GitHub](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-concurrency-advanced).