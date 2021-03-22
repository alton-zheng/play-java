# Runnable vs. Callable in Java

&nbsp;

## **1. Overview**

Since the early days of Java, multithreading has been a major aspect of the language. *Runnable* is the core interface provided for representing multi-threaded tasks and *Callable* is an improved version of *Runnable* that was added in Java 1.5.

In this article, we'll explore the differences and the applications of both interfaces.

## **2. Execution Mechanism**

Both interfaces are designed to represent a task that can be executed by multiple threads. *Runnable* tasks can be run using the *Thread* class or *ExecutorService* whereas *Callables* can be run only using the latter.

## **3. Return Values**

Let's have a deeper look at the way these interfaces handle return values.

### **3.1. With \*Runnable\***

The *Runnable* interface is a functional interface and has a single *run()* method which doesn't accept any parameters and does not return any values.

This is suitable for situations where we are not looking for a result of the thread execution, for example, incoming events logging:

```java
public interface Runnable {
    public void run();
}
```

Let's understand this with an example:

```java
public class EventLoggingTask implements  Runnable{
    private Logger logger
      = LoggerFactory.getLogger(EventLoggingTask.class);

    @Override
    public void run() {
        logger.info("Message");
    }
}
```

In this example, the thread will just read a message from the queue and log it in a log file. There's no value returned from the task; the task can be launched using *ExecutorService:*

```java
public void executeTask() {
    executorService = Executors.newSingleThreadExecutor();
    Future future = executorService.submit(new EventLoggingTask());
    executorService.shutdown();
}
```

In this case, the *Future* object will not hold any value.

### **3.2. With \*Callable\***

<iframe frameborder="0" src="https://a1f9e7d686387035b42443e57d7431dc.safeframe.googlesyndication.com/safeframe/1-0-37/html/container.html" id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_0" title="3rd party ad content" name="" scrolling="no" marginwidth="0" marginheight="0" width="300" height="250" data-is-safeframe="true" sandbox="allow-forms allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts allow-top-navigation-by-user-activation" allow="conversion-measurement ‘src’" data-google-container-id="5" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![Freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

The *Callable* interface is a generic interface containing a single *call()* method – which returns a generic value *V*:

```java
public interface Callable<V> {
    V call() throws Exception;
}
```

Let's have a look at calculating the factorial of a number:

```java
public class FactorialTask implements Callable<Integer> {
    int number;

    // standard constructors

    public Integer call() throws InvalidParamaterException {
        int fact = 1;
        // ...
        for(int count = number; count > 1; count--) {
            fact = fact * count;
        }

        return fact;
    }
}
```

The result of *call()* method is returned within a *Future* object:

```java
@Test
public void whenTaskSubmitted_ThenFutureResultObtained(){
    FactorialTask task = new FactorialTask(5);
    Future<Integer> future = executorService.submit(task);
 
    assertEquals(120, future.get().intValue());
}
```

## **4. Exception Handling**

Let's see how suitable they are for exception handling.

### **4.1. With \*Runnable\***

**Since the method signature does not have the “throws” clause specified, there is no way to propagate further checked exceptions.**

### **4.2. With \*Callable\***

*Callable's call()* method contains “throws *Exception”* clause so we can easily propagate checked exceptions further:

```java
public class FactorialTask implements Callable<Integer> {
    // ...
    public Integer call() throws InvalidParamaterException {

        if(number < 0) {
            throw new InvalidParamaterException("Number should be positive");
        }
    // ...
    }
}
```

In case of running a *Callable using* an *ExecutorService,* the exceptions are collected in the *Future* object, which can be checked by making a call to the *Future.get()* method. This will throw an *ExecutionException –* which wraps the original exception:

```java
@Test(expected = ExecutionException.class)
public void whenException_ThenCallableThrowsIt() {
 
    FactorialCallableTask task = new FactorialCallableTask(-5);
    Future<Integer> future = executorService.submit(task);
    Integer result = future.get().intValue();
}
```

In the above test, the *ExecutionException* is being thrown as we are passing an invalid number. We can call the *getCause()* method on this exception object to get the original checked exception.

If we don't make the call to the *get()* method of *Future* class – then the exception thrown by *call()* method will not be reported back, and the task will still be marked as completed:

```java
@Test
public void whenException_ThenCallableDoesntThrowsItIfGetIsNotCalled(){
    FactorialCallableTask task = new FactorialCallableTask(-5);
    Future<Integer> future = executorService.submit(task);
 
    assertEquals(false, future.isDone());
}
```

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_1" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_1" width="300" height="250" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" sandbox="allow-forms allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts allow-top-navigation-by-user-activation" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="6" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![Freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

The above test will pass successfully even though we've thrown an exception for the negative values of the parameter to *FactorialCallableTask.*

## **5. Conclusion**

In this article, we've explored the differences between the *Runnable* and *Callable* interfaces.

As always, the complete code for this article is available [over on GitHub](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-concurrency-basic).