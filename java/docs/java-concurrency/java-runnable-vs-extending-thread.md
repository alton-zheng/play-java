# Implementing a Runnable vs Extending a Thread

Last modified: May 7, 2019

by [baeldung](https://www.baeldung.com/author/baeldung/)



- [Java](https://www.baeldung.com/category/java/)**+**

- [Java Concurrency](https://www.baeldung.com/tag/java-concurrency/)

### **Get started with Spring 5 and Spring Boot 2, through the \*Learn Spring\* course:**

**[>> CHECK OUT THE COURSE](https://www.baeldung.com/ls-course-start)**

## **1. Introduction**

“Should I implement a *Runnable* or extend the *Thread* class”? is quite a common question.

In this article, we'll see which approach makes more sense in practice and why.

## **2. Using \*Thread\***

Let's first define a *SimpleThread* class that extends *Thread*:

```java
public class SimpleThread extends Thread {

    private String message;

    // standard logger, constructor

    @Override
    public void run() {
        log.info(message);
    }
}
```

Let's also see how we can run a thread of this type:

```java
@Test
public void givenAThread_whenRunIt_thenResult()
  throws Exception {
 
    Thread thread = new SimpleThread(
      "SimpleThread executed using Thread");
    thread.start();
    thread.join();
}
```

We can also use an *ExecutorService* to execute the thread:

```java
@Test
public void givenAThread_whenSubmitToES_thenResult()
  throws Exception {
    
    executorService.submit(new SimpleThread(
      "SimpleThread executed using ExecutorService")).get();
}
```

That's quite a lot of code for running a single log operation in a separate thread.

Also, note that ***SimpleThread\* cannot extend any other class**, as Java doesn't support multiple inheritance.

## **3. Implementing a \*Runnable\***

Now, let's create a simple task which implements the *java.lang.Runnable* interface:

```java
class SimpleRunnable implements Runnable {
	
    private String message;
	
    // standard logger, constructor
    
    @Override
    public void run() {
        log.info(message);
    }
}
```

The above *SimpleRunnable* is just a task which we want to run in a separate thread.

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_0" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_0" width="728" height="90" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" srcdoc="" data-google-container-id="5" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

There're various approaches we can use for running it; one of them is to use the *Thread* class:

```java
@Test
public void givenRunnable_whenRunIt_thenResult()
 throws Exception {
    Thread thread = new Thread(new SimpleRunnable(
      "SimpleRunnable executed using Thread"));
    thread.start();
    thread.join();
}
```

We can even use an *ExecutorService*:

```java
@Test
public void givenARunnable_whenSubmitToES_thenResult()
 throws Exception {
    
    executorService.submit(new SimpleRunnable(
      "SimpleRunnable executed using ExecutorService")).get();
}
```

We can read more about *ExecutorService* in [here](https://www.baeldung.com/java-executor-service-tutorial).

Since we're now implementing an interface, we're free to extend another base class if we need to.

Starting with Java 8, any interface which exposes a single abstract method is treated as a functional interface, which makes it a valid lambda expression target.

**We can rewrite the above \*Runnable\* code using a lambda expression**:

```java
@Test
public void givenARunnableLambda_whenSubmitToES_thenResult() 
  throws Exception {
    
    executorService.submit(
      () -> log.info("Lambda runnable executed!"));
}
```

## **4. \*Runnable\* or \*Thread\*?**

Simply put, we generally encourage the use of *Runnable* over *Thread*:

- When extending the *Thread* class, we're not overriding any of its methods. Instead, we override the method of *Runnable (*which *Thread* happens to implement*)*. This is a clear violation of IS-A *Thread* principle
- Creating an implementation of *Runnable* and passing it to the *Thread* class utilizes composition and not inheritance – which is more flexible
- After extending the *Thread* class, we can't extend any other class
- From Java 8 onwards, *Runnables* can be represented as lambda expressions

## **5. Conclusion**

In this quick tutorial, we saw how implementing *Runnable* is typically a better approach than extending the *Thread* class.

The code for this post can be found [over on GitHub](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-concurrency-basic).