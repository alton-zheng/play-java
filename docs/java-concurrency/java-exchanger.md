# Introduction to Exchanger in Java

Last modified: June 2, 2020

by [Dhrubajyoti Bhattacharjee](https://www.baeldung.com/author/dhrubajyoti-bhattacharjee/)



- [Java](https://www.baeldung.com/category/java/)**+**

- [Java Concurrency](https://www.baeldung.com/tag/java-concurrency/)

### **Get started with Spring 5 and Spring Boot 2, through the \*Learn Spring\* course:**

**[>> CHECK OUT THE COURSE](https://www.baeldung.com/ls-course-start)**

If you have a few years of experience in the Java ecosystem, and you're interested in sharing that experience with the community (and getting paid for your work of course), have a look at the ["Write for Us" page](https://www.baeldung.com/contribution-guidelines). Cheers, Eugen

## 1. Overview

In this tutorial, we'll look into *java.util.concurrent.Exchanger<T>.* This works as a common point for two threads in Java to exchange objects between them.

## 2. Introduction to Exchanger

**The \*Exchanger\* class in Java can be used to share objects between two threads of type \*T\*.** The class provides only a single overloaded method *exchange(T t)*.

When invoked *exchange* waits for the other thread in the pair to call it as well. At this point, the second thread finds the first thread is waiting with its object. The thread exchanges the objects they are holding and signals the exchange, and now they can return.

Let's look into an example to understand message exchange between two threads with *Exchanger*:

```java
@Test
public void givenThreads_whenMessageExchanged_thenCorrect() {
    Exchanger<String> exchanger = new Exchanger<>();

    Runnable taskA = () -> {
        try {
            String message = exchanger.exchange("from A");
            assertEquals("from B", message);
        } catch (InterruptedException e) {
            Thread.currentThread.interrupt();
            throw new RuntimeException(e);
        }
    };

    Runnable taskB = () -> {
        try {
            String message = exchanger.exchange("from B");
            assertEquals("from A", message);
        } catch (InterruptedException e) {
            Thread.currentThread.interrupt();
            throw new RuntimeException(e);
        }
    };
    CompletableFuture.allOf(
      runAsync(taskA), runAsync(taskB)).join();
}
```

Here, we have the two threads exchanging messages between each other using the common exchanger. Let's see an example where we exchange an object from the main thread with a new thread:

```java
@Test
public void givenThread_WhenExchangedMessage_thenCorrect() throws InterruptedException {
    Exchanger<String> exchanger = new Exchanger<>();

    Runnable runner = () -> {
        try {
            String message = exchanger.exchange("from runner");
            assertEquals("to runner", message);
        } catch (InterruptedException e) {
            Thread.currentThread.interrupt();
            throw new RuntimeException(e);
        }
    };
    CompletableFuture<Void> result 
      = CompletableFuture.runAsync(runner);
    String msg = exchanger.exchange("to runner");
    assertEquals("from runner", msg);
    result.join();
}
```

Note that, we need to start the *runner* thread first and later call *exchange()* in the main thread.

Also, note that the first thread's call may timeout if the second thread does not reach the exchange point in time. How long the first thread should wait can be controlled using the overloaded *exchange(T t, long timeout, TimeUnit timeUnit).*

## 3. No GC Data Exchange

*Exchanger* could be used to create pipeline kind of patterns with passing data from one thread to the other. In this section, we'll create a simple stack of threads continuously pass data between each other as a pipeline.

```java
@Test
public void givenData_whenPassedThrough_thenCorrect() throws InterruptedException {

    Exchanger<Queue<String>> readerExchanger = new Exchanger<>();
    Exchanger<Queue<String>> writerExchanger = new Exchanger<>();

    Runnable reader = () -> {
        Queue<String> readerBuffer = new ConcurrentLinkedQueue<>();
        while (true) {
            readerBuffer.add(UUID.randomUUID().toString());
            if (readerBuffer.size() >= BUFFER_SIZE) {
                readerBuffer = readerExchanger.exchange(readerBuffer);
            }
        }
    };

    Runnable processor = () -> {
        Queue<String> processorBuffer = new ConcurrentLinkedQueue<>();
        Queue<String> writerBuffer = new ConcurrentLinkedQueue<>();
        processorBuffer = readerExchanger.exchange(processorBuffer);
        while (true) {
            writerBuffer.add(processorBuffer.poll());
            if (processorBuffer.isEmpty()) {
                processorBuffer = readerExchanger.exchange(processorBuffer);
                writerBuffer = writerExchanger.exchange(writerBuffer);
            }
        }
    };

    Runnable writer = () -> {
        Queue<String> writerBuffer = new ConcurrentLinkedQueue<>();
        writerBuffer = writerExchanger.exchange(writerBuffer);
        while (true) {
            System.out.println(writerBuffer.poll());
            if (writerBuffer.isEmpty()) {
                writerBuffer = writerExchanger.exchange(writerBuffer);
            }
        }
    };
    CompletableFuture.allOf(
      runAsync(reader), 
      runAsync(processor),
      runAsync(writer)).join();
}
```

Here, we have three threads: *reader*, *processor*, and *writer*. Together, they work as a single pipeline exchanging data between them.

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_0" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_0" width="300" height="250" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" sandbox="allow-forms allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts allow-top-navigation-by-user-activation" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="5" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![Freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

The *readerExchanger* is shared between the *reader* and the *processor* thread, while the *writerExchanger* is shared between the *processor* and the *writer* thread.

Note that the example here is only for demonstration. We must be careful while creating infinite loops with *while(true)*. Also to keep the code readable, we've omitted some exceptions handling.

**This pattern of exchanging data while reusing the buffer allows having less garbage collection.** The exchange method returns the same queue instances and thus there would be no GC for these objects. Unlike any blocking queue, the exchanger does not create any nodes or objects to hold and share data.

Creating such a pipeline is similar to the Disrupter pattern, with a key difference, the Disrupter pattern supports multiple producers and consumers, while an exchanger could be used between a pair of consumers and producers.

## 4.Conclusion

So, we have learned what *Exchanger<T>* is in Java, how it works, and we have seen how to use the *Exchanger* class. Also, we created a pipeline and demonstrated GC-less data exchange between threads.

As always, the code is available [over on GitHub](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-concurrency-advanced-3).