# Semaphores in Java

Last modified: January 9, 2021

by [baeldung](https://www.baeldung.com/author/baeldung/)



- [Java](https://www.baeldung.com/category/java/)**+**

- [Java Concurrency](https://www.baeldung.com/tag/java-concurrency/)

### **Get started with Spring 5 and Spring Boot 2, through the \*Learn Spring\* course:**

**[>> CHECK OUT THE COURSE](https://www.baeldung.com/ls-course-start)**

If you have a few years of experience in the Java ecosystem, and you're interested in sharing that experience with the community (and getting paid for your work of course), have a look at the ["Write for Us" page](https://www.baeldung.com/contribution-guidelines). Cheers, Eugen

## **1. Overview**

In this quick tutorial, we'll explore the basics of [semaphores](https://www.baeldung.com/cs/semaphore) and mutexes in Java.

## **2. \*Semaphore\***

We'll start with *java.util.concurrent.Semaphore.* We can use semaphores to limit the number of concurrent threads accessing a specific resource.

In the following example, we will implement a simple login queue to limit the number of users in the system:

```java
class LoginQueueUsingSemaphore {

    private Semaphore semaphore;

    public LoginQueueUsingSemaphore(int slotLimit) {
        semaphore = new Semaphore(slotLimit);
    }

    boolean tryLogin() {
        return semaphore.tryAcquire();
    }

    void logout() {
        semaphore.release();
    }

    int availableSlots() {
        return semaphore.availablePermits();
    }

}
```

Notice how we used the following methods:

- *tryAcquire()* – return true if a permit is available immediately and acquire it otherwise return false, but *acquire()* acquires a permit and blocking until one is available
- *release() – release a permit*
- *availablePermits() –* return number of current permits available

To test our login queue, we will first try to reach the limit and check if the next login attempt will be blocked:

```java
@Test
public void givenLoginQueue_whenReachLimit_thenBlocked() {
    int slots = 10;
    ExecutorService executorService = Executors.newFixedThreadPool(slots);
    LoginQueueUsingSemaphore loginQueue = new LoginQueueUsingSemaphore(slots);
    IntStream.range(0, slots)
      .forEach(user -> executorService.execute(loginQueue::tryLogin));
    executorService.shutdown();

    assertEquals(0, loginQueue.availableSlots());
    assertFalse(loginQueue.tryLogin());
}
```

Next, we will see if any slots are available after a logout:

```java
@Test
public void givenLoginQueue_whenLogout_thenSlotsAvailable() {
    int slots = 10;
    ExecutorService executorService = Executors.newFixedThreadPool(slots);
    LoginQueueUsingSemaphore loginQueue = new LoginQueueUsingSemaphore(slots);
    IntStream.range(0, slots)
      .forEach(user -> executorService.execute(loginQueue::tryLogin));
    executorService.shutdown();
    assertEquals(0, loginQueue.availableSlots());
    loginQueue.logout();

    assertTrue(loginQueue.availableSlots() > 0);
    assertTrue(loginQueue.tryLogin());
}
```

## **3. Timed \*Semaphore\***

Next, we will discuss Apache Commons *TimedSemaphore.* *TimedSemaphore* allows a number of permits as a simple Semaphore but in a given period of time, after this period the time reset and all permits are released.

We can use *TimedSemaphore* to build a simple delay queue as follows:

```java
class DelayQueueUsingTimedSemaphore {

    private TimedSemaphore semaphore;

    DelayQueueUsingTimedSemaphore(long period, int slotLimit) {
        semaphore = new TimedSemaphore(period, TimeUnit.SECONDS, slotLimit);
    }

    boolean tryAdd() {
        return semaphore.tryAcquire();
    }

    int availableSlots() {
        return semaphore.getAvailablePermits();
    }

}
```

When we use a delay queue with one second as time period and after using all the slots within one second, none should be available:

```java
public void givenDelayQueue_whenReachLimit_thenBlocked() {
    int slots = 50;
    ExecutorService executorService = Executors.newFixedThreadPool(slots);
    DelayQueueUsingTimedSemaphore delayQueue 
      = new DelayQueueUsingTimedSemaphore(1, slots);
    
    IntStream.range(0, slots)
      .forEach(user -> executorService.execute(delayQueue::tryAdd));
    executorService.shutdown();

    assertEquals(0, delayQueue.availableSlots());
    assertFalse(delayQueue.tryAdd());
}
```

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_0" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_0" width="300" height="250" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" sandbox="allow-forms allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts allow-top-navigation-by-user-activation" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="9" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![Freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

But after sleeping for some time, **the semaphore should reset and release the permits**:

```java
@Test
public void givenDelayQueue_whenTimePass_thenSlotsAvailable() throws InterruptedException {
    int slots = 50;
    ExecutorService executorService = Executors.newFixedThreadPool(slots);
    DelayQueueUsingTimedSemaphore delayQueue = new DelayQueueUsingTimedSemaphore(1, slots);
    IntStream.range(0, slots)
      .forEach(user -> executorService.execute(delayQueue::tryAdd));
    executorService.shutdown();

    assertEquals(0, delayQueue.availableSlots());
    Thread.sleep(1000);
    assertTrue(delayQueue.availableSlots() > 0);
    assertTrue(delayQueue.tryAdd());
}
```

## **4. Semaphore vs. Mutex**

[Mutex](https://www.baeldung.com/cs/what-is-mutex) acts similarly to a binary semaphore, we can use it to implement mutual exclusion.

In the following example, we'll use a simple binary semaphore to build a counter:

```java
class CounterUsingMutex {

    private Semaphore mutex;
    private int count;

    CounterUsingMutex() {
        mutex = new Semaphore(1);
        count = 0;
    }

    void increase() throws InterruptedException {
        mutex.acquire();
        this.count = this.count + 1;
        Thread.sleep(1000);
        mutex.release();

    }

    int getCount() {
        return this.count;
    }

    boolean hasQueuedThreads() {
        return mutex.hasQueuedThreads();
    }
}
```

When a lot of threads try to access the counter at once, **they'll simply be blocked in a queue**:

```java
@Test
public void whenMutexAndMultipleThreads_thenBlocked()
 throws InterruptedException {
    int count = 5;
    ExecutorService executorService
     = Executors.newFixedThreadPool(count);
    CounterUsingMutex counter = new CounterUsingMutex();
    IntStream.range(0, count)
      .forEach(user -> executorService.execute(() -> {
          try {
              counter.increase();
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
      }));
    executorService.shutdown();

    assertTrue(counter.hasQueuedThreads());
}
```

When we wait, all threads will access the counter and no threads left in the queue:

```java
@Test
public void givenMutexAndMultipleThreads_ThenDelay_thenCorrectCount()
 throws InterruptedException {
    int count = 5;
    ExecutorService executorService
     = Executors.newFixedThreadPool(count);
    CounterUsingMutex counter = new CounterUsingMutex();
    IntStream.range(0, count)
      .forEach(user -> executorService.execute(() -> {
          try {
              counter.increase();
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
      }));
    executorService.shutdown();

    assertTrue(counter.hasQueuedThreads());
    Thread.sleep(5000);
    assertFalse(counter.hasQueuedThreads());
    assertEquals(count, counter.getCount());
}
```

## **5. Conclusion**

In this article, we explored the basics of semaphores in Java.

As always, the full source code is available [over on GitHub](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-concurrency-advanced-2).