# Guide to java.util.concurrent.Future

## **1. Overview**

In this article, we are going to learn about *[Future](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Future.html)*. An interface that's been around since Java 1.5 and can be quite useful when working with asynchronous calls and concurrent processing.

## **2. Creating \*Futures\***

Simply put, the *Future* class represents a future result of an asynchronous computation – a result that will eventually appear in the *Future* after the processing is complete.

Let's see how to write methods that create and return a *Future* instance.

Long running methods are good candidates for asynchronous processing and the *Future* interface. This enables us to execute some other process while we are waiting for the task encapsulated in *Future* to complete.

Some examples of operations that would leverage the async nature of *Future* are:

- computational intensive processes (mathematical and scientific calculations)
- manipulating large data structures (big data)
- remote method calls (downloading files, HTML scrapping, web services).

### **2.1. Implementing \*Futures\* With \*FutureTask\***

For our example, we are going to create a very simple class that calculates the square of an *Integer*. This definitely doesn't fit the “long-running” methods category, but we are going to put a *Thread.sleep()* call to it to make it last 1 second to complete:

```java
public class SquareCalculator {    
    
    private ExecutorService executor 
      = Executors.newSingleThreadExecutor();
    
    public Future<Integer> calculate(Integer input) {        
        return executor.submit(() -> {
            Thread.sleep(1000);
            return input * input;
        });
    }
}
```

The bit of code that actually performs the calculation is contained within the *call()* method, supplied as a lambda expression. As you can see there's nothing special about it, except for the *sleep()* call mentioned earlier.

It gets more interesting when we direct our attention to the usage of *[Callable](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Callable.html)* and *[ExecutorService](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html)*.

*Callable* is an interface representing a task that returns a result and has a single *call()* method. Here, we've created an instance of it using a lambda expression.

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_0" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_0" width="300" height="250" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="9" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

Creating an instance of *Callable* does not take us anywhere, we still have to pass this instance to an executor that will take care of starting that task in a new thread and give us back the valuable *Future* object. That's where *ExecutorService* comes in.

There are a few ways we can get ahold of an *ExecutorService* instance, most of them are provided by utility class *[Executors](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executors.html)‘* static factory methods. In this example, we've used the basic *newSingleThreadExecutor()*, which gives us an *ExecutorService* capable of handling a single thread at a time.

Once we have an *ExecutorService* object, we just need to call *submit()* passing our *Callable* as an argument. *submit()* will take care of starting the task and return a *[FutureTask](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/FutureTask.html)* object, which is an implementation of the *Future* interface.

## **3. Consuming \*Futures\***

Up to this point, we've learned how to create an instance of *Future*.

In this section, we'll learn how to work with this instance by exploring all methods that are part of *Future*‘s API.

### **3.1. Using \*isDone()\* and \*get()\* to Obtain Results**

Now we need to call *calculate()* and use the returned *Future* to get the resulting *Integer*. Two methods from the *Future* API will help us with this task.

*[Future.isDone()](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Future.html#isDone--)* tells us if the executor has finished processing the task. If the task is completed, it will return *true* otherwise, it returns *false*.

The method that returns the actual result from the calculation is *[Future.get()](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Future.html#get--)*. Notice that this method blocks the execution until the task is complete, but in our example, this won't be an issue since we'll check first if the task is completed by calling *isDone()*.

By using these two methods we can run some other code while we wait for the main task to finish:

```java
Future<Integer> future = new SquareCalculator().calculate(10);

while(!future.isDone()) {
    System.out.println("Calculating...");
    Thread.sleep(300);
}

Integer result = future.get();
```

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_1" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_1" width="300" height="250" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="a" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

In this example, we write a simple message on the output to let the user know the program is performing the calculation.

The method *get()* will block the execution until the task is complete. But we don't have to worry about that since our example only get to the point where *get()* is called after making sure that the task is finished. So, in this scenario, *future.get()* will always return immediately.

It is worth mentioning that *get()* has an overloaded version that takes a timeout and a [*TimeUnit*](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/TimeUnit.html) as arguments:

```java
Integer result = future.get(500, TimeUnit.MILLISECONDS);
```

The difference between *[get(long, TimeUnit)](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Future.html#get-long-java.util.concurrent.TimeUnit-)* and *[get()](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Future.html#get--)*, is that the former will throw a *[TimeoutException](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/TimeoutException.html)* if the task doesn't return before the specified timeout period.

### **3.2. Canceling a \*Future W\*ith \*cancel()\***

Suppose we've triggered a task but, for some reason, we don't care about the result anymore. We can use *[Future.cancel(boolean)](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Future.html#cancel-boolean-)* to tell the executor to stop the operation and interrupt its underlying thread:

```java
Future<Integer> future = new SquareCalculator().calculate(4);

boolean canceled = future.cancel(true);
```

Our instance of *Future* from the code above would never complete its operation. In fact, if we try to call *get()* from that instance, after the call to *cancel()*, the outcome would be a *[CancellationException](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CancellationException.html)*. *[Future.isCancelled()](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Future.html#isCancelled--)* will tell us if a *Future* was already canceled. This can be quite useful to avoid getting a *CancellationException*.

It is possible that a call to *cancel()* fails. In that case, its returned value will be *false*. Notice that *cancel()* takes a *boolean* value as an argument – this controls whether the thread executing this task should be interrupted or not.

## **4. More Multithreading With \*Thread\* Pools**

Our current *ExecutorService* is single threaded since it was obtained with the [Executors.newSingleThreadExecutor](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executors.html#newSingleThreadExecutor--). To highlight this “single threadness”, let's trigger two calculations simultaneously:

```java
SquareCalculator squareCalculator = new SquareCalculator();

Future<Integer> future1 = squareCalculator.calculate(10);
Future<Integer> future2 = squareCalculator.calculate(100);

while (!(future1.isDone() && future2.isDone())) {
    System.out.println(
      String.format(
        "future1 is %s and future2 is %s", 
        future1.isDone() ? "done" : "not done", 
        future2.isDone() ? "done" : "not done"
      )
    );
    Thread.sleep(300);
}

Integer result1 = future1.get();
Integer result2 = future2.get();

System.out.println(result1 + " and " + result2);

squareCalculator.shutdown();
```

Now let's analyze the output for this code:

```java
calculating square for: 10
future1 is not done and future2 is not done
future1 is not done and future2 is not done
future1 is not done and future2 is not done
future1 is not done and future2 is not done
calculating square for: 100
future1 is done and future2 is not done
future1 is done and future2 is not done
future1 is done and future2 is not done
100 and 10000
```

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_2" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_2" width="300" height="250" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="b" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

It is clear that the process is not parallel. Notice how the second task only starts once the first task is completed, making the whole process take around 2 seconds to finish.

To make our program really multi-threaded we should use a different flavor of *ExecutorService*. Let's see how the behavior of our example changes if we use a thread pool, provided by the factory method *[Executors.newFixedThreadPool()](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executors.html#newFixedThreadPool-int-)*:

```java
public class SquareCalculator {
 
    private ExecutorService executor = Executors.newFixedThreadPool(2);
    
    //...
}
```

With a simple change in our *SquareCalculator* class now we have an executor which is able to use 2 simultaneous threads.

If we run the exact same client code again, we'll get the following output:

```java
calculating square for: 10
calculating square for: 100
future1 is not done and future2 is not done
future1 is not done and future2 is not done
future1 is not done and future2 is not done
future1 is not done and future2 is not done
100 and 10000
```

This is looking much better now. Notice how the 2 tasks start and finish running simultaneously, and the whole process takes around 1 second to complete.

There are other factory methods that can be used to create thread pools, like *[Executors.newCachedThreadPool()](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executors.html#newCachedThreadPool--)* that reuses previously used *Thread*s when they are available, and *[Executors.newScheduledThreadPool()](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executors.html#newScheduledThreadPool-int-)* which schedules commands to run after a given delay*.*

For more information about *ExecutorService*, read our [article](https://www.baeldung.com/java-executor-service-tutorial) dedicated to the subject.

## **5. Overview of \*ForkJoinTask\***

*[ForkJoinTask](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ForkJoinTask.html)* is an abstract class which implements *Future* and is capable of running a large number of tasks hosted by a small number of actual threads in *[ForkJoinPool](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ForkJoinPool.html)*.

In this section, we are going to quickly cover the main characteristics of *ForkJoinPool*. For a comprehensive guide about the topic, check our [Guide to the Fork/Join Framework in Java](https://www.baeldung.com/java-fork-join).

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_3" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_3" width="300" height="250" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" sandbox="allow-forms allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts allow-top-navigation-by-user-activation" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="c" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

Then the main characteristic of a *ForkJoinTask* is that it usually will spawn new subtasks as part of the work required to complete its main task. It generates new tasks by calling *[fork()](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ForkJoinTask.html#fork--)* and it gathers all results with *[join()](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ForkJoinTask.html#join--),* thus the name of the class.

There are two abstract classes that implement *ForkJoinTask*: *[RecursiveTask](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/RecursiveTask.html)* which returns a value upon completion, and *[RecursiveAction](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/RecursiveAction.html)* which doesn't return anything. As the names imply, those classes are to be used for recursive tasks, like for example file-system navigation or complex mathematical computation.

Let's expand our previous example to create a class that, given an *Integer*, will calculate the sum squares for all its factorial elements. So, for instance, if we pass the number 4 to our calculator, we should get the result from the sum of 4² + 3² + 2² + 1² which is 30.

First of all, we need to create a concrete implementation of *RecursiveTask* and implement its *compute()* method. This is where we'll write our business logic:

```java
public class FactorialSquareCalculator extends RecursiveTask<Integer> {
 
    private Integer n;

    public FactorialSquareCalculator(Integer n) {
        this.n = n;
    }

    @Override
    protected Integer compute() {
        if (n <= 1) {
            return n;
        }

        FactorialSquareCalculator calculator 
          = new FactorialSquareCalculator(n - 1);

        calculator.fork();

        return n * n + calculator.join();
    }
}
```

Notice how we achieve recursiveness by creating a new instance of *FactorialSquareCalculator* within *compute()*. By calling *fork()*, a non-blocking method, we ask *ForkJoinPool* to initiate the execution of this subtask.

The *join()* method will return the result from that calculation, to which we add the square of the number we are currently visiting.

Now we just need to create a *ForkJoinPool* to handle the execution and thread management:

```java
ForkJoinPool forkJoinPool = new ForkJoinPool();

FactorialSquareCalculator calculator = new FactorialSquareCalculator(10);

forkJoinPool.execute(calculator);
```

## **6. Conclusion**

In this article, we had a comprehensive view of the *Future* interface, visiting all its methods. We've also learned how to leverage the power of thread pools to trigger multiple parallel operations. The main methods from the *ForkJoinTask* class, *fork()* and *join()* were briefly covered as well.

We have many other great articles on parallel and asynchronous operations in Java. Here are three of them that are closely related to the *Future* interface (some of them are already mentioned in the article):

- [Guide to *CompletableFuture*](https://www.baeldung.com/java-completablefuture) – an implementation of *Future* with many extra features introduced in Java 8
- [Guide to the Fork/Join Framework in Java](https://www.baeldung.com/java-fork-join) – more about *ForkJoinTask* we covered in section 5
- [Guide to the Java *ExecutorService*](https://www.baeldung.com/java-executor-service-tutorial) – dedicated to the *ExecutorService* interface

Check the source code used in this article in our [GitHub repository](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-concurrency-basic).