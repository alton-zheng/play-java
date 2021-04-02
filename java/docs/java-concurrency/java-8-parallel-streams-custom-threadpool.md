# Custom Thread Pools In Java 8 Parallel Streams

Last modified: March 13, 2021

by [baeldung](https://www.baeldung.com/author/baeldung/)



- [Java](https://www.baeldung.com/category/java/)**+**

- [Java 8](https://www.baeldung.com/tag/java-8/)
- [Java Concurrency](https://www.baeldung.com/tag/java-concurrency/)
- [Java Streams](https://www.baeldung.com/tag/java-streams/)

### **Get started with Spring 5 and Spring Boot 2, through the \*Learn Spring\* course:**

**[>> CHECK OUT THE COURSE](https://www.baeldung.com/ls-course-start)**

## **1. Overview**

Java 8 introduced the concept of S*treams* as an efficient way of carrying out bulk operations on data. And parallel *Streams* can be obtained in environments that support concurrency.

These streams can come with improved performance – at the cost of multi-threading overhead.

In this quick tutorial, we'll look at **one of the biggest limitations of \*Stream\* API** and see how to make a parallel stream work with a custom *ThreadPool* instance, alternatively – [there's a library that handles this](https://github.com/pivovarit/parallel-collectors).

## **2. Parallel \*Stream\***

Let's start with a simple example – calling the *parallelStream* method on any of the *Collection* types – which will return a possibly parallel *Stream*:

```java
@Test
public void givenList_whenCallingParallelStream_shouldBeParallelStream(){
    List<Long> aList = new ArrayList<>();
    Stream<Long> parallelStream = aList.parallelStream();
        
    assertTrue(parallelStream.isParallel());
}
```

The default processing that occurs in such a *Stream* uses the *ForkJoinPool.commonPool(),* **a thread pool shared by the entire application.**

## **3. Custom Thread Pool**

**We can actually pass a custom \*ThreadPool\* when processing the \*stream\*.**

The following example lets have a parallel *Stream* use a custom *ThreadPool* to calculate the sum of long values from 1 to 1,000,000, inclusive:

```java
@Test
public void giveRangeOfLongs_whenSummedInParallel_shouldBeEqualToExpectedTotal() 
  throws InterruptedException, ExecutionException {
    
    long firstNum = 1;
    long lastNum = 1_000_000;

    List<Long> aList = LongStream.rangeClosed(firstNum, lastNum).boxed()
      .collect(Collectors.toList());

    ForkJoinPool customThreadPool = new ForkJoinPool(4);
    long actualTotal = customThreadPool.submit(
      () -> aList.parallelStream().reduce(0L, Long::sum)).get();
 
    assertEquals((lastNum + firstNum) * lastNum / 2, actualTotal);
}
```

We used the *ForkJoinPool* constructor with a parallelism level of 4. Some experimentation is required to determine the optimal value for different environments, but a good rule of thumb is simply choosing the number based on how many cores your CPU has.

Next, we processed the content of the parallel *Stream*, summing them up in the *reduce* call.

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_0" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_0" width="970" height="90" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" sandbox="allow-forms allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts allow-top-navigation-by-user-activation" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="9" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

This simple example may not demonstrate the full usefulness of using a custom thread pool, but the benefits become obvious in situations where we do not want to tie-up the common thread pool with long-running tasks – such as processing data from a network source – or the common thread pool is being used by other components within the application.

If we run the test method above, it'll pass. So far, so good.

However, if we instantiate *ForkJoinPool* class in a normal method in the same way as we do in the test method, it may lead to the *OutOfMemoryError*.

Next, let's take a closer look at the cause of the memory leak.

## 4. Beware of the Memory Leak

As we've talked about earlier, the common thread pool is used by the entire application by default. **The common thread pool is a static \*ThreadPool\* instance.**

Therefore, no memory leak occurs if we use the default thread pool.

Now, let's review our test method. In the test method, we created an object of *ForkJoinPool.* When the test method is finished, **the \*customThreadPool\* object won't be dereferenced and garbage collected — instead, it will be waiting for new tasks to be assigned**.

That is to say, every time we call the test method, a new *customThreadPool* object will be created and it won't be released.

The fix to the problem is pretty simple: *shutdown* the *customThreadPool* object after we've executed the method:

```java
try {
    long actualTotal = customThreadPool.submit(
      () -> aList.parallelStream().reduce(0L, Long::sum)).get();
    assertEquals((lastNum + firstNum) * lastNum / 2, actualTotal);
} finally {
    customThreadPool.shutdown();
}
```

## **5. Conclusion**

We have briefly looked at how to run a parallel *Stream* using a custom *ThreadPool*. In the right environment and with the proper use of the parallelism level, performance gains can be had in certain situations.

If we create a custom *ThreadPool*, we should keep in mind to call its *shutdown()* method to avoid a memory leak.

The complete code samples referenced in this article can be found [over on GitHub](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-concurrency-collections).