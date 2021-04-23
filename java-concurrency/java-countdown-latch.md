# Guide to CountDownLatch in Java

Last modified: May 12, 2019

by [baeldung](https://www.baeldung.com/author/baeldung/)



- [Java](https://www.baeldung.com/category/java/)**+**

- [Java Concurrency](https://www.baeldung.com/tag/java-concurrency/)

### **Get started with Spring 5 and Spring Boot 2, through the \*Learn Spring\* course:**

**[>> CHECK OUT THE COURSE](https://www.baeldung.com/ls-course-start)**

If you have a few years of experience in the Java ecosystem, and you're interested in sharing that experience with the community (and getting paid for your work of course), have a look at the ["Write for Us" page](https://www.baeldung.com/contribution-guidelines). Cheers, Eugen

## **1. Introduction**

In this article, we'll give a guide to the *[CountDownLatch](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/CountDownLatch.html)* class and demonstrate how it can be used in a few practical examples.

Essentially, by using a *CountDownLatch* we can cause a thread to block until other threads have completed a given task.

## **2. Usage in Concurrent Programming**

Simply put, a *CountDownLatch* has a *counter* field, which you can decrement as we require. We can then use it to block a calling thread until it's been counted down to zero.

If we were doing some parallel processing, we could instantiate the *CountDownLatch* with the same value for the counter as a number of threads we want to work across. Then, we could just call *countdown()* after each thread finishes, guaranteeing that a dependent thread calling *await()* will block until the worker threads are finished.

## **3. Waiting for a Pool of Threads to Complete**

Let's try out this pattern by creating a *Worker* and using a *CountDownLatch* field to signal when it has completed:

```java
public class Worker implements Runnable {
    private List<String> outputScraper;
    private CountDownLatch countDownLatch;

    public Worker(List<String> outputScraper, CountDownLatch countDownLatch) {
        this.outputScraper = outputScraper;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        doSomeWork();
        outputScraper.add("Counted down");
        countDownLatch.countDown();
    }
}
```

Then, let's create a test in order to prove that we can get a *CountDownLatch* to wait for the *Worker* instances to complete:

```java
@Test
public void whenParallelProcessing_thenMainThreadWillBlockUntilCompletion()
  throws InterruptedException {

    List<String> outputScraper = Collections.synchronizedList(new ArrayList<>());
    CountDownLatch countDownLatch = new CountDownLatch(5);
    List<Thread> workers = Stream
      .generate(() -> new Thread(new Worker(outputScraper, countDownLatch)))
      .limit(5)
      .collect(toList());

      workers.forEach(Thread::start);
      countDownLatch.await(); 
      outputScraper.add("Latch released");

      assertThat(outputScraper)
        .containsExactly(
          "Counted down",
          "Counted down",
          "Counted down",
          "Counted down",
          "Counted down",
          "Latch released"
        );
    }
```

Naturally “Latch released” will always be the last output – as it's dependant on the *CountDownLatch* releasing.

Note that if we didn't call *await()*, we wouldn't be able to guarantee the ordering of the execution of the threads, so the test would randomly fail.

## 4. **A Pool of Threads Waiting to Begin**

If we took the previous example, but this time started thousands of threads instead of five, it's likely that many of the earlier ones will have finished processing before we have even called *start()* on the later ones. This could make it difficult to try and reproduce a concurrency problem, as we wouldn't be able to get all our threads to run in parallel.

To get around this, let's get the *CountdownLatch* to work differently than in the previous example. Instead of blocking a parent thread until some child threads have finished, we can block each child thread until all the others have started.

Let's modify our *run()* method so it blocks before processing:

```java
public class WaitingWorker implements Runnable {

    private List<String> outputScraper;
    private CountDownLatch readyThreadCounter;
    private CountDownLatch callingThreadBlocker;
    private CountDownLatch completedThreadCounter;

    public WaitingWorker(
      List<String> outputScraper,
      CountDownLatch readyThreadCounter,
      CountDownLatch callingThreadBlocker,
      CountDownLatch completedThreadCounter) {

        this.outputScraper = outputScraper;
        this.readyThreadCounter = readyThreadCounter;
        this.callingThreadBlocker = callingThreadBlocker;
        this.completedThreadCounter = completedThreadCounter;
    }

    @Override
    public void run() {
        readyThreadCounter.countDown();
        try {
            callingThreadBlocker.await();
            doSomeWork();
            outputScraper.add("Counted down");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            completedThreadCounter.countDown();
        }
    }
}
```

Now, let's modify our test so it blocks until all the *Workers* have started, unblocks the *Workers,* and then blocks until the *Workers* have finished:

```java
@Test
public void whenDoingLotsOfThreadsInParallel_thenStartThemAtTheSameTime()
 throws InterruptedException {
 
    List<String> outputScraper = Collections.synchronizedList(new ArrayList<>());
    CountDownLatch readyThreadCounter = new CountDownLatch(5);
    CountDownLatch callingThreadBlocker = new CountDownLatch(1);
    CountDownLatch completedThreadCounter = new CountDownLatch(5);
    List<Thread> workers = Stream
      .generate(() -> new Thread(new WaitingWorker(
        outputScraper, readyThreadCounter, callingThreadBlocker, completedThreadCounter)))
      .limit(5)
      .collect(toList());

    workers.forEach(Thread::start);
    readyThreadCounter.await(); 
    outputScraper.add("Workers ready");
    callingThreadBlocker.countDown(); 
    completedThreadCounter.await(); 
    outputScraper.add("Workers complete");

    assertThat(outputScraper)
      .containsExactly(
        "Workers ready",
        "Counted down",
        "Counted down",
        "Counted down",
        "Counted down",
        "Counted down",
        "Workers complete"
      );
}
```

This pattern is really useful for trying to reproduce concurrency bugs, as can be used to force thousands of threads to try and perform some logic in parallel.

## **5. Terminating a \*CountdownLatch\* Early**

Sometimes, we may run into a situation where the *Workers* terminate in error before counting down the *CountDownLatch.* This could result in it never reaching zero and *await()* never terminating:

```java
@Override
public void run() {
    if (true) {
        throw new RuntimeException("Oh dear, I'm a BrokenWorker");
    }
    countDownLatch.countDown();
    outputScraper.add("Counted down");
}
```

Let's modify our earlier test to use a *BrokenWorker,* in order to show how *await()* will block forever:

```java
@Test
public void whenFailingToParallelProcess_thenMainThreadShouldGetNotGetStuck()
  throws InterruptedException {
 
    List<String> outputScraper = Collections.synchronizedList(new ArrayList<>());
    CountDownLatch countDownLatch = new CountDownLatch(5);
    List<Thread> workers = Stream
      .generate(() -> new Thread(new BrokenWorker(outputScraper, countDownLatch)))
      .limit(5)
      .collect(toList());

    workers.forEach(Thread::start);
    countDownLatch.await();
}
```

Clearly, this is not the behavior we want – it would be much better for the application to continue than infinitely block.

To get around this, let's add a timeout argument to our call to *await().*

```java
boolean completed = countDownLatch.await(3L, TimeUnit.SECONDS);
assertThat(completed).isFalse();
```

As we can see, the test will eventually time out and *await()* will return *false*.

## **6. Conclusion**

In this quick guide, we've demonstrated how we can use a *CountDownLatch* in order to block a thread until other threads have finished some processing.

We've also shown how it can be used to help debug concurrency issues by making sure threads run in parallel.

The implementation of these examples can be found [over on GitHub](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-concurrency-advanced); this is a Maven-based project, so should be easy to run as is.