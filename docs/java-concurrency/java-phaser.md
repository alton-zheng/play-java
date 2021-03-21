# Guide to the Java Phaser

Last modified: May 12, 2019

by [baeldung](https://www.baeldung.com/author/baeldung/)



- [Java](https://www.baeldung.com/category/java/)**+**

- [Java Concurrency](https://www.baeldung.com/tag/java-concurrency/)

### **Get started with Spring 5 and Spring Boot 2, through the \*Learn Spring\* course:**

**[>> CHECK OUT THE COURSE](https://www.baeldung.com/ls-course-start)**

If you have a few years of experience in the Java ecosystem, and you're interested in sharing that experience with the community (and getting paid for your work of course), have a look at the ["Write for Us" page](https://www.baeldung.com/contribution-guidelines). Cheers, Eugen

## **1. Overview**

In this article, we will be looking at the *[Phaser](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Phaser.html)* construct from the *java.util.concurrent* package. It is a very similar construct to the [*CountDownLatch*](https://www.baeldung.com/java-countdown-latch) that allows us to coordinate execution of threads. In comparison to the *CountDownLatch*, it has some additional functionality.

The *Phaser* is a barrier on which the dynamic number of threads need to wait before continuing execution. In the *CountDownLatch* that number cannot be configured dynamically and needs to be supplied when we're creating the instance.

## **2. \*Phaser\* API**

The *Phaser* allows us to build logic in which **threads need to wait on the barrier before going to the next step of execution**.

We can coordinate multiple phases of execution, reusing a *Phaser* instance for each program phase. Each phase can have a different number of threads waiting for advancing to another phase. We'll have a look at an example of using phases later on.

To participate in the coordination, the thread needs to *register()* itself with the *Phaser* instance. Note that this only increases the number of registered parties, and we can't check whether the current thread is registered – we'd have to subclass the implementation to supports this.

The thread signals that it arrived at the barrier by calling the *arriveAndAwaitAdvance()*, which is a blocking method. **When the number of arrived parties is equal to the number of registered parties, the execution of the program will continue**, and the phase number will increase. We can get the current phase number by calling the *getPhase()* method.

When the thread finishes its job, we should call the *arriveAndDeregister()* method to signal that the current thread should no longer be accounted for in this particular phase.

## **3. Implementing Logic Using \*Phaser\* API**

Let's say that we want to coordinate multiple phases of actions. Three threads will process the first phase, and two threads will process the second phase.

We'll create a *LongRunningAction* class that implements the *Runnable* interface:

```java
class LongRunningAction implements Runnable {
    private String threadName;
    private Phaser ph;

    LongRunningAction(String threadName, Phaser ph) {
        this.threadName = threadName;
        this.ph = ph;
        ph.register();
    }

    @Override
    public void run() {
        ph.arriveAndAwaitAdvance();
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ph.arriveAndDeregister();
    }
}
```

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_0" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_0" width="300" height="250" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="5" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![Freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

When our action class is instantiated, we're registering to the *Phaser* instance using the *register()* method. This will increment the number of threads using that specific *Phaser.*

The call to the *arriveAndAwaitAdvance()* will cause the current thread to wait on the barrier. As already mentioned, when the number of arrived parties becomes the same as the number of registered parties, the execution will continue.

After the processing is done, the current thread is deregistering itself by calling the *arriveAndDeregister()* method.

Let's create a test case in which we will start three *LongRunningAction* threads and block on the barrier. Next, after the action is finished, we will create two additional *LongRunningAction* threads that will perform processing of the next phase.

When creating *Phaser* instance from the main thread, we're passing *1* as an argument. This is equivalent to calling the *register()* method from the current thread. We're doing this because, when we're creating three worker threads, the main thread is a coordinator, and therefore the *Phaser* needs to have four threads registered to it:

```java
ExecutorService executorService = Executors.newCachedThreadPool();
Phaser ph = new Phaser(1);
 
assertEquals(0, ph.getPhase());
```

The phase after the initialization is equal to zero.

The *Phaser* class has a constructor in which we can pass a parent instance to it. It is useful in cases where we have large numbers of parties that would experience massive synchronization contention costs. In such situations, instances of *Phasers* may be set up so that groups of sub-phasers share a common parent.

Next, let's start three *LongRunningAction* action threads, which will be waiting on the barrier until we will call the *arriveAndAwaitAdvance()* method from the main thread.

Keep in mind we've initialized our *Phaser* with *1* and called *register()* three more times. Now, three action threads have announced that they've arrived at the barrier, so one more call of *arriveAndAwaitAdvance()* is needed – the one from the main thread:

```java
executorService.submit(new LongRunningAction("thread-1", ph));
executorService.submit(new LongRunningAction("thread-2", ph));
executorService.submit(new LongRunningAction("thread-3", ph));

ph.arriveAndAwaitAdvance();
 
assertEquals(1, ph.getPhase());
```

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_1" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_1" width="300" height="250" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" sandbox="allow-forms allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts allow-top-navigation-by-user-activation" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="6" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![Freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

After the completion of that phase, the *getPhase()* method will return one because the program finished processing the first step of execution.

Let's say that two threads should conduct the next phase of processing. We can leverage *Phaser* to achieve that because it allows us to configure dynamically the number of threads that should wait on the barrier. We're starting two new threads, but these will not proceed to execute until the call to the *arriveAndAwaitAdvance()* from the main thread (same as in the previous case):

```java
executorService.submit(new LongRunningAction("thread-4", ph));
executorService.submit(new LongRunningAction("thread-5", ph));
ph.arriveAndAwaitAdvance();
 
assertEquals(2, ph.getPhase());

ph.arriveAndDeregister();
```

After this, the *getPhase()* method will return phase number equal to two. When we want to finish our program, we need to call the *arriveAndDeregister()* method as the main thread is still registered in the *Phaser.* When the deregistration causes the number of registered parties to become zero, the *Phaser* is *terminated.* All calls to synchronization methods will not block anymore and will return immediately.

Running the program will produce the following output (full source code with the print line statements can be found in the code repository):

```plaintext
This is phase 0
This is phase 0
This is phase 0
Thread thread-2 before long running action
Thread thread-1 before long running action
Thread thread-3 before long running action
This is phase 1
This is phase 1
Thread thread-4 before long running action
Thread thread-5 before long running action
```

We see that all threads are waiting for execution until the barrier opens. Next phase of the execution is performed only when the previous one finished successfully.

## **4. Conclusion**

In this tutorial, we had a look at the *Phaser* construct from *java.util.concurrent* and we implemented the coordination logic with multiple phases using *Phaser* class.

The implementation of all these examples and code snippets can be found in the [GitHub project](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-concurrency-advanced) – this is a Maven project, so it should be easy to import and run as it is.