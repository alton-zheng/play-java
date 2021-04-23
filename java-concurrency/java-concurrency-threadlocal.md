# An Introduction to ThreadLocal in Java

&nbsp;

## **1. Overview**

In this article, we will be looking at the *[ThreadLocal](https://docs.oracle.com/javase/8/docs/api/java/lang/ThreadLocal.html)* construct from the *java.lang* package. This gives us the ability to store data individually for the current thread – and simply wrap it within a special type of object.

&nbsp;

## 2. ThreadLocal API

The *TheadLocal* construct allows us to store data that will be **accessible only** by **a specific thread**.

Let's say that we want to have an *Integer* value that will be bundled with the specific thread:

```java
ThreadLocal<Integer> threadLocalValue = new ThreadLocal<>();
```

Next, when we want to use this value from a thread we only need to call a *get()* or *set()* method. Simply put, we can think that *ThreadLocal* stores data inside of a map – with the thread as the key.

&nbsp;

Due to that fact, when we call a *get()* method on the *threadLocalValue*, we will get an *Integer* value for the requesting thread:

```java
threadLocalValue.set(1);
Integer result = threadLocalValue.get();
```

&nbsp;

We can construct an instance of the *ThreadLocal* by using the *withInitial()* static method and passing a supplier to it:

```java
ThreadLocal<Integer> threadLocal = ThreadLocal.withInitial(() -> 1);
```

To remove the value from the *ThreadLocal*, we can call the *remove()* method:

```java
threadLocal.remove();
```

To see how to use the *ThreadLocal* properly, firstly, we will look at an example that does not use a *ThreadLocal*, then we will rewrite our example to leverage that construct.

&nbsp;

## **3. Storing User Data in a Map**

Let's consider a program that needs to store the user-specific *Context* data per given user id:

```java
public class Context {
    private String userName;

    public Context(String userName) {
        this.userName = userName;
    }
}
```

We want to have one thread per user id. We'll create a *SharedMapWithUserContext* class that implements the *Runnable* interface. The implementation in the *run()* method calls some database through the *UserRepository* class that returns a *Context* object for a given *userId*.

&nbsp;

Next, we store that context in the *ConcurentHashMap* keyed by *userId*:

```java
public class SharedMapWithUserContext implements Runnable {
 
    public static Map<Integer, Context> userContextPerUserId
      = new ConcurrentHashMap<>();
    private Integer userId;
    private UserRepository userRepository = new UserRepository();

    @Override
    public void run() {
        String userName = userRepository.getUserNameForUserId(userId);
        userContextPerUserId.put(userId, new Context(userName));
    }

    // standard constructor
}
```

&nbsp;

We can easily test our code by creating and starting two threads for two different *userIds* and asserting that we have two entries in the *userContextPerUserId* map:

```java
SharedMapWithUserContext firstUser = new SharedMapWithUserContext(1);
SharedMapWithUserContext secondUser = new SharedMapWithUserContext(2);
new Thread(firstUser).start();
new Thread(secondUser).start();

assertEquals(SharedMapWithUserContext.userContextPerUserId.size(), 2);
```

&nbsp;

## 4. Storing User Data in ThreadLocal

We can rewrite our example to store the user *Context* instance using a *ThreadLocal*. Each thread will have its own *ThreadLocal* instance.

When using *ThreadLocal*, we need to be very careful because every *ThreadLocal* instance is associated with a particular thread. In our example, we have a dedicated thread for each particular *userId*, and this thread is created by us, so we have full control over it.

The *run()* method will fetch the user context and store it into the *ThreadLocal* variable using the *set()* method:

```java
public class ThreadLocalWithUserContext implements Runnable {
 
    private static ThreadLocal<Context> userContext 
      = new ThreadLocal<>();
    private Integer userId;
    private UserRepository userRepository = new UserRepository();

    @Override
    public void run() {
        String userName = userRepository.getUserNameForUserId(userId);
        userContext.set(new Context(userName));
        System.out.println("thread context for given userId: " 
          + userId + " is: " + userContext.get());
    }
    
    // standard constructor
}
```

&nbsp;

We can test it by starting two threads that will execute the action for a given *userId*:

```java
ThreadLocalWithUserContext firstUser 
  = new ThreadLocalWithUserContext(1);
ThreadLocalWithUserContext secondUser 
  = new ThreadLocalWithUserContext(2);
new Thread(firstUser).start();
new Thread(secondUser).start();
```

&nbsp;

After running this code we'll see on the standard output that *ThreadLocal* was set per given thread:

```java
thread context for given userId: 1 is: Context{userNameSecret='18a78f8e-24d2-4abf-91d6-79eaa198123f'}
thread context for given userId: 2 is: Context{userNameSecret='e19f6a0a-253e-423e-8b2b-bca1f471ae5c'}
```

We can see that each of the users has its own *Context*.

&nbsp;

## 5. ThreadLocals and Thread Pools

*ThreadLocal* provides an easy-to-use API to confine some values to each thread. This is a reasonable way of achieving thread-safety in Java. However, **we should be extra careful when we're using ThreadLocals** and [thread pools]([>> Java Thread Pool 介绍](java-concurrency/thread-pool-java-and-guava.md)) together.

In order to better understand the possible caveat, let's consider the following scenario:

1. First, the application borrows a thread from the pool.
2. Then it stores some thread-confined values into the current thread's *ThreadLocal*.
3. Once the current execution finishes, the application returns the borrowed thread to the pool.
4. After a while, the application borrows the same thread to process another request.
5. Since the application didn't perform the necessary cleanups last time, it may re-use the same \*ThreadLocal\* data for the new request.

This may cause surprising consequences in highly concurrent applications.

One way to solve this problem is to manually remove each *ThreadLocal* once we're done using it. Because this approach needs rigorous code reviews, it can be error-prone.

&nbsp;

### 5.1. Extending the *ThreadPoolExecutor*

As it turns out, it's possible to extend the *ThreadPoolExecutor* class and provide a custom hook implementation for [beforeExecute()](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ThreadPoolExecutor.html#beforeExecute-java.lang.Thread-java.lang.Runnable-) and [*afterExecute()*](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ThreadPoolExecutor.html#afterExecute-java.lang.Runnable-java.lang.Throwable-) methods. The thread pool will call the *beforeExecute()* method before running anything using the borrowed thread. On the other hand, it will call the *afterExecute()* method after executing our logic.

Therefore, we can extend the *ThreadPoolExecutor* class and remove the *ThreadLocal* data in the *afterExecute()* method:

```java
public class ThreadLocalAwareThreadPool extends ThreadPoolExecutor {

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        // Call remove on each ThreadLocal
    }
}
```

If we submit our requests to this implementation of *[ExecutorService](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ExecutorService.html)*, then we can be sure that using *ThreadLocal* and thread pools won't introduce safety hazards for our application.

&nbsp;

## **6. Conclusion**

In this quick article, we were looking at the *ThreadLocal* construct. We implemented the logic that uses *ConcurrentHashMap* that was shared between threads to store the context associated with a particular *userId.* Next, we rewrote our example to leverage *ThreadLocal* to store data that is associated with a particular *userId* and with a particular thread.