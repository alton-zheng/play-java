# Guide To CompletableFuture

Last modified: January 30, 2021

by [baeldung](https://www.baeldung.com/author/baeldung/)



- [Java](https://www.baeldung.com/category/java/)**+**

- [Java Concurrency](https://www.baeldung.com/tag/java-concurrency/)

### **Get started with Spring 5 and Spring Boot 2, through the \*Learn Spring\* course:**

**[>> CHECK OUT THE COURSE](https://www.baeldung.com/ls-course-start)**

## **1. Introduction**

This tutorial is a guide to the functionality and use cases of the *CompletableFuture* class that was introduced as a Java 8 Concurrency API improvement.

## Further reading:

## [Runnable vs. Callable in Java](https://www.baeldung.com/java-runnable-callable)

Learn the difference between Runnable and Callable interfaces in Java.

[Read more](https://www.baeldung.com/java-runnable-callable) →

## [Guide to java.util.concurrent.Future](https://www.baeldung.com/java-future)

A guide to java.util.concurrent.Future with an overview of its several implementations

[Read more](https://www.baeldung.com/java-future) →

## **2. Asynchronous Computation in Java**

Asynchronous computation is difficult to reason about. Usually we want to think of any computation as a series of steps, but in the case of asynchronous computation, **actions represented as callbacks tend to be either scattered across the code or deeply nested inside each other**. Things get even worse when we need to handle errors that might occur during one of the steps.

The *Future* interface was added in Java 5 to serve as a result of an asynchronous computation, but it did not have any methods to combine these computations or handle possible errors.

**Java 8 introduced the \*CompletableFuture\* class.** Along with the *Future* interface, it also implemented the *CompletionStage* interface. This interface defines the contract for an asynchronous computation step that we can combine with other steps.

*CompletableFuture* is at the same time a building block and a framework, with **about 50 different methods for composing, combining, and executing asynchronous computation steps and handling errors**.

Such a large API can be overwhelming, but these mostly fall in several clear and distinct use cases.

## **3. Using \*CompletableFuture\* as a Simple \*Future\***

First of all, the *CompletableFuture* class implements the *Future* interface, so we can **use it as a \*Future\* implementation, but with additional completion logic**.

For example, we can create an instance of this class with a no-arg constructor to represent some future result, hand it out to the consumers, and complete it at some time in the future using the *complete* method. The consumers may use the *get* method to block the current thread until this result is provided.

In the example below, we have a method that creates a *CompletableFuture* instance, then spins off some computation in another thread and returns the *Future* immediately.

<iframe frameborder="0" src="https://21594bf41b3c264c979f154deff61709.safeframe.googlesyndication.com/safeframe/1-0-38/html/container.html" id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_0" title="3rd party ad content" name="" scrolling="no" marginwidth="0" marginheight="0" width="728" height="90" data-is-safeframe="true" sandbox="allow-forms allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts allow-top-navigation-by-user-activation" allow="conversion-measurement ‘src’" data-google-container-id="9" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

When the computation is done, the method completes the *Future* by providing the result to the *complete* method:

```java
public Future<String> calculateAsync() throws InterruptedException {
    CompletableFuture<String> completableFuture = new CompletableFuture<>();

    Executors.newCachedThreadPool().submit(() -> {
        Thread.sleep(500);
        completableFuture.complete("Hello");
        return null;
    });

    return completableFuture;
}
```

To spin off the computation, we use the *Executor* API. This method of creating and completing a *CompletableFuture* can be used together with any concurrency mechanism or API, including raw threads.

Notice that **the \*calculateAsync\* method returns a \*Future\* instance**.

We simply call the method, receive the *Future* instance, and call the *get* method on it when we're ready to block for the result.

Also observe that the *get* method throws some checked exceptions, namely *ExecutionException* (encapsulating an exception that occurred during a computation) and *InterruptedException* (an exception signifying that a thread executing a method was interrupted):

```java
Future<String> completableFuture = calculateAsync();

// ... 

String result = completableFuture.get();
assertEquals("Hello", result);
```

**If we already know the result of a computation**, we can use the static *completedFuture* method with an argument that represents a result of this computation. Consequently, the *get* method of the *Future* will never block, immediately returning this result instead:

```java
Future<String> completableFuture = 
  CompletableFuture.completedFuture("Hello");

// ...

String result = completableFuture.get();
assertEquals("Hello", result);
```

As an alternative scenario, we may want to [**cancel the execution of a \*Future\***](https://www.baeldung.com/java-future#2-canceling-a-future-with-cancel).

## **4. \*CompletableFuture\* with Encapsulated Computation Logic**

The code above allows us to pick any mechanism of concurrent execution, but what if we want to skip this boilerplate and simply execute some code asynchronously?

Static methods *runAsync* and *supplyAsync* allow us to create a *CompletableFuture* instance out of *Runnable* and *Supplier* functional types correspondingly.

<iframe frameborder="0" src="https://21594bf41b3c264c979f154deff61709.safeframe.googlesyndication.com/safeframe/1-0-38/html/container.html" id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_1" title="3rd party ad content" name="" scrolling="no" marginwidth="0" marginheight="0" width="728" height="90" data-is-safeframe="true" sandbox="allow-forms allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts allow-top-navigation-by-user-activation" allow="conversion-measurement ‘src’" data-google-container-id="a" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

Both *Runnable* and *Supplier* are functional interfaces that allow passing their instances as lambda expressions thanks to the new Java 8 feature.

The *Runnable* interface is the same old interface that is used in threads and it does not allow to return a value.

The *Supplier* interface is a generic functional interface with a single method that has no arguments and returns a value of a parameterized type.

This allows us to **provide an instance of the \*Supplier\* as a lambda expression that does the calculation and returns the result**. It is as simple as:

```java
CompletableFuture<String> future
  = CompletableFuture.supplyAsync(() -> "Hello");

// ...

assertEquals("Hello", future.get());
```

## **5. Processing Results of Asynchronous Computations**

The most generic way to process the result of a computation is to feed it to a function. The *thenApply* method does exactly that; it accepts a *Function* instance, uses it to process the result, and returns a *Future* that holds a value returned by a function:

```java
CompletableFuture<String> completableFuture
  = CompletableFuture.supplyAsync(() -> "Hello");

CompletableFuture<String> future = completableFuture
  .thenApply(s -> s + " World");

assertEquals("Hello World", future.get());
```

If we don't need to return a value down the *Future* chain, we can use an instance of the *Consumer* functional interface. Its single method takes a parameter and returns *void*.

There's a method for this use case in the *CompletableFuture.* The *thenAccept* method receives a *Consumer* and passes it the result of the computation. Then the final *future.get()* call returns an instance of the *Void* type:

```java
CompletableFuture<String> completableFuture
  = CompletableFuture.supplyAsync(() -> "Hello");

CompletableFuture<Void> future = completableFuture
  .thenAccept(s -> System.out.println("Computation returned: " + s));

future.get();
```

Finally, if we neither need the value of the computation, nor want to return some value at the end of the chain, then we can pass a *Runnable* lambda to the *thenRun* method. In the following example, we simply print a line in the console after calling the *future.get():*

```java
CompletableFuture<String> completableFuture 
  = CompletableFuture.supplyAsync(() -> "Hello");

CompletableFuture<Void> future = completableFuture
  .thenRun(() -> System.out.println("Computation finished."));

future.get();
```

## **6. Combining Futures**

The best part of the *CompletableFuture* API is the **ability to combine \*CompletableFuture\* instances in a chain of computation steps**.

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_2" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_2" width="300" height="250" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" sandbox="allow-forms allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts allow-top-navigation-by-user-activation" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="b" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

The result of this chaining is itself a *CompletableFuture* that allows further chaining and combining. This approach is ubiquitous in functional languages and is often referred to as a monadic design pattern.

**In the following example we use the \*thenCompose\* method to chain two \*Futures\* sequentially.**

Notice that this method takes a function that returns a *CompletableFuture* instance. The argument of this function is the result of the previous computation step. This allows us to use this value inside the next *CompletableFuture*‘s lambda:

```java
CompletableFuture<String> completableFuture 
  = CompletableFuture.supplyAsync(() -> "Hello")
    .thenCompose(s -> CompletableFuture.supplyAsync(() -> s + " World"));

assertEquals("Hello World", completableFuture.get());
```

The *thenCompose* method, together with *thenApply,* implement basic building blocks of the monadic pattern. They closely relate to the *map* and *flatMap* methods of *Stream* and *Optional* classes also available in Java 8.

Both methods receive a function and apply it to the computation result, but the *thenCompose* (*flatMap*) method **receives a function that returns another object of the same type**. This functional structure allows composing the instances of these classes as building blocks.

If we want to execute two independent *Futures* and do something with their results, we can use the *thenCombine* method that accepts a *Future* and a *Function* with two arguments to process both results:

```java
CompletableFuture<String> completableFuture 
  = CompletableFuture.supplyAsync(() -> "Hello")
    .thenCombine(CompletableFuture.supplyAsync(
      () -> " World"), (s1, s2) -> s1 + s2));

assertEquals("Hello World", completableFuture.get());
```

A simpler case is when we want to do something with two *Futures*‘ results, but don't need to pass any resulting value down a *Future* chain. The *thenAcceptBoth* method is there to help:

```java
CompletableFuture future = CompletableFuture.supplyAsync(() -> "Hello")
  .thenAcceptBoth(CompletableFuture.supplyAsync(() -> " World"),
    (s1, s2) -> System.out.println(s1 + s2));
```

## **7. Difference Between \*thenApply()\* and \*thenCompose()\***

In our previous sections, we've shown examples regarding *thenApply()* and *thenCompose()*. Both APIs help chain different *CompletableFuture* calls, but the usage of these 2 functions is different.

### **7.1. \*thenApply()\***

**We can use this method to work with a result of the previous call.** However, a key point to remember is that the return type will be combined of all calls.

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_3" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_3" width="300" height="250" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="c" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

So this method is useful when we want to transform the result of a *CompletableFuture* call:

```java
CompletableFuture<Integer> finalResult = compute().thenApply(s-> s + 1);
```

### **7.2. \*thenCompose()\***

The *thenCompose()* method is similar to *thenApply()* in that both return a new Completion Stage. However, ***thenCompose()\* uses the previous stage as the argument**. It will flatten and return a *Future* with the result directly, rather than a nested future as we observed in *thenApply():*

```java
CompletableFuture<Integer> computeAnother(Integer i){
    return CompletableFuture.supplyAsync(() -> 10 + i);
}
CompletableFuture<Integer> finalResult = compute().thenCompose(this::computeAnother);
```

So if the idea is to chain *CompletableFuture* methods then it’s better to use *thenCompose()*.

Also, note that the difference between these two methods is analogous to [the difference between *map()* and *flatMap()*](https://www.baeldung.com/java-difference-map-and-flatmap)*.*

## **8. Running Multiple \*Futures\* in Parallel**

When we need to execute multiple *Futures* in parallel, we usually want to wait for all of them to execute and then process their combined results.

The *CompletableFuture.allOf* static method allows to wait for completion of all of the *Futures* provided as a var-arg:

```java
CompletableFuture<String> future1  
  = CompletableFuture.supplyAsync(() -> "Hello");
CompletableFuture<String> future2  
  = CompletableFuture.supplyAsync(() -> "Beautiful");
CompletableFuture<String> future3  
  = CompletableFuture.supplyAsync(() -> "World");

CompletableFuture<Void> combinedFuture 
  = CompletableFuture.allOf(future1, future2, future3);

// ...

combinedFuture.get();

assertTrue(future1.isDone());
assertTrue(future2.isDone());
assertTrue(future3.isDone());
```

Notice that the return type of the *CompletableFuture.allOf()* is a *CompletableFuture<Void>*. The limitation of this method is that it does not return the combined results of all *Futures*. Instead, we have to manually get results from *Futures*. Fortunately, *CompletableFuture.join()* method and Java 8 Streams API makes it simple:

```java
String combined = Stream.of(future1, future2, future3)
  .map(CompletableFuture::join)
  .collect(Collectors.joining(" "));

assertEquals("Hello Beautiful World", combined);
```

The *CompletableFuture.join()* method is similar to the *get* method, but it throws an unchecked exception in case the *Future* does not complete normally. This makes it possible to use it as a method reference in the *Stream.map()* method.

## **9. Handling Errors**

For error handling in a chain of asynchronous computation steps, we have to adapt the *throw/catch* idiom in a similar fashion.

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_4" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_4" width="728" height="90" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" sandbox="allow-forms allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts allow-top-navigation-by-user-activation" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="d" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

Instead of catching an exception in a syntactic block, the *CompletableFuture* class allows us to handle it in a special *handle* method. This method receives two parameters: a result of a computation (if it finished successfully), and the exception thrown (if some computation step did not complete normally).

In the following example, we use the *handle* method to provide a default value when the asynchronous computation of a greeting was finished with an error because no name was provided:

```java
String name = null;

// ...

CompletableFuture<String> completableFuture  
  =  CompletableFuture.supplyAsync(() -> {
      if (name == null) {
          throw new RuntimeException("Computation error!");
      }
      return "Hello, " + name;
  })}).handle((s, t) -> s != null ? s : "Hello, Stranger!");

assertEquals("Hello, Stranger!", completableFuture.get());
```

As an alternative scenario, suppose we want to manually complete the *Future* with a value, as in the first example, but also have the ability to complete it with an exception. The *completeExceptionally* method is intended for just that. The *completableFuture.get()* method in the following example throws an *ExecutionException* with a *RuntimeException* as its cause:

```java
CompletableFuture<String> completableFuture = new CompletableFuture<>();

// ...

completableFuture.completeExceptionally(
  new RuntimeException("Calculation failed!"));

// ...

completableFuture.get(); // ExecutionException
```

In the example above, we could have handled the exception with the *handle* method asynchronously, but with the *get* method we can use the more typical approach of a synchronous exception processing.

## **10. Async Methods**

Most methods of the fluent API in *CompletableFuture* class have two additional variants with the *Async* postfix. These methods are usually intended for **running a corresponding step of execution in another thread**.

The methods without the *Async* postfix run the next execution stage using a calling thread. In contrast, the *Async* method without the *Executor* argument runs a step using the common *fork/join* pool implementation of *Executor* that is accessed with the *ForkJoinPool.commonPool()* method. Finally, the *Async* method with an *Executor* argument runs a step using the passed *Executor*.

Here's a modified example that processes the result of a computation with a *Function* instance. The only visible difference is the *thenApplyAsync* method, but under the hood the application of a function is wrapped into a *ForkJoinTask* instance (for more information on the *fork/join* framework, see the article [“Guide to the Fork/Join Framework in Java”](https://www.baeldung.com/java-fork-join)). This allows us to parallelize our computation even more and use system resources more efficiently:

```java
CompletableFuture<String> completableFuture  
  = CompletableFuture.supplyAsync(() -> "Hello");

CompletableFuture<String> future = completableFuture
  .thenApplyAsync(s -> s + " World");

assertEquals("Hello World", future.get());
```

## 11. JDK 9 *CompletableFuture* API

Java 9 enhances the *CompletableFuture* API with the following changes:

- New factory methods added
- Support for delays and timeouts
- Improved support for subclassing

and new instance APIs:

- *Executor defaultExecutor()*
- *CompletableFuture<U> newIncompleteFuture()*
- *CompletableFuture<T> copy()*
- *CompletionStage<T> minimalCompletionStage()*
- *CompletableFuture<T> completeAsync(Supplier<? extends T> supplier, Executor executor)*
- *CompletableFuture<T> completeAsync(Supplier<? extends T> supplier)*
- *CompletableFuture<T> orTimeout(long timeout, TimeUnit unit)*
- *CompletableFuture<T> completeOnTimeout(T value, long timeout, TimeUnit unit)*

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_5" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_5" width="300" height="250" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" sandbox="allow-forms allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts allow-top-navigation-by-user-activation" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="e" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

We also now have a few static utility methods:

- *Executor delayedExecutor(long delay, TimeUnit unit, Executor executor)*
- *Executor delayedExecutor(long delay, TimeUnit unit)*
- *<U> CompletionStage<U> completedStage(U value)*
- *<U> CompletionStage<U> failedStage(Throwable ex)*
- *<U> CompletableFuture<U> failedFuture(Throwable ex)*

Finally, to address timeout, Java 9 has introduced two more new functions:

- *orTimeout()*
- *completeOnTimeout()*

Here's the detailed article for further reading: [Java 9 CompletableFuture API Improvements](https://www.baeldung.com/java-9-completablefuture).

## **12. Conclusion**

In this article, we've described the methods and typical use cases of the *CompletableFuture* class.

The source code for the article is available [over on GitHub](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-concurrency-basic).