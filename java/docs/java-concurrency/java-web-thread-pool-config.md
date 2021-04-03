# Configuring Thread Pools for Java Web Servers

## 1. Introduction

In this tutorial, we take a look at thread pool configuration for Java web application servers such as Apache Tomcat, Glassfish Server, and Oracle Weblogic.

在本教程中，我们将看看Java web应用程序服务器(如Apache Tomcat、Glassfish服务器和Oracle Weblogic)的线程池配置。

## 2. Server Thread Pools

**[Server thread pools](https://www.baeldung.com/thread-pool-java-and-guava) are used and managed by a web application server for a deployed application.** These thread pools exist outside of the web container or servlet so they are not subject to the same context boundary.

Unlike application threads, server threads exist even after a deployed application is stopped.

**[服务器线程池](https://www.baeldung.com/thread-pool-java-and-guava)由部署的应用程序的web应用程序服务器使用和管理。**这些线程池存在于web容器或servlet之外，因此它们不受相同的上下文边界的约束。

与应用程序线程不同，服务器线程即使在已部署的应用程序停止后仍然存在。

## 3. Apache Tomcat

First, we can configure Tomcat's server thread pool via the *Executor* configuration class in our *server.xml*:

首先，我们可以通过*server.xml*中的*Executor*配置类来配置Tomcat的服务器线程池:

```xml
<Executor name="tomcatThreadPool" namePrefix="catalina-exec-" maxThreads="150" minSpareThreads="25"/>
```

*minSpareThreads* is the smallest the pool will be, including at startup. *maxThreads* is the largest the pool will be **before the server starts queueing up requests.**

Tomcat defaults these to 25 and 200, respectively. In this configuration, we've made the thread pool a bit smaller than the default.

minSpareThreads*是最小的线程池，包括在启动时。maxThreads是服务器开始排队请求之前最大的线程池

Tomcat将它们分别默认为25和200。在这个配置中，我们将线程池设置得比默认值小一些。

### 3.1. Embedded Tomcat

Similarly, we can alter an [embedded Tomcat server](https://www.baeldung.com/spring-boot-configure-tomcat) for Spring Boot to configure a thread pool by setting an application property:

类似地，我们可以通过设置一个应用程序属性来改变Spring Boot的[嵌入式Tomcat服务器](https://www.baeldung.com/spring-boot-configure-tomcat)来配置线程池:

```javascript
server.tomcat.max-threads=250
```

Starting with Boot 2.3, the property has changed to:

从Boot 2.3开始，属性更改为:

```xml
server.tomcat.threads.max=250
```

## 4. Glassfish

Next, let's update our Glassfish server.

接下来，让我们更新Glassfish服务器。

Glassfish uses an admin command in contrast to Tomcat's XML configuration file, *server.xml.* From the prompt, we run:

Glassfish使用的是管理命令，而不是Tomcat的XML配置文件*server.xml。*在提示符中运行:

```shell
create-threadpool
```

We can add to *create-threadpool* the flags *maxthreadpoolsize* and *minthreadpoolsize.* They function similarly to Tomcat *minSpareThreads* and *maxThreads*:

我们可以在*create-threadpool*中添加*maxthreadpoolsize*和*minthreadpoolsize标记。它们的功能类似于Tomcat的minSpareThreads*和maxThreads*:

```shell
--maxthreadpoolsize 250 --minthreadpoolsize 25
```

We can also specify how long a thread can be idle before returning to the pool:

我们还可以指定线程在返回池之前可以空闲多长时间:

```shell
--idletimeout=2
```

And then, we supply the name of our thread pool at the end:

然后，我们在最后提供线程池的名称:

```shell
asadmin> create-threadpool --maxthreadpoolsize 250 --minthreadpoolsize 25 --idletimeout=2 threadpool-1
```

## 5. Weblogic

Oracle Weblogic gives us the ability to alter a self-tuning thread pool with a WorkManager.

Similarly to thread queues, a WorkManager manages a thread pool as a queue. However, the WorkManager adds dynamic threads based on real-time throughput. Weblogic performs analysis on throughput regularly to optimize thread utilization.

What does this mean for us? It means that while we may alter the thread pool, the web server will ultimately decide on whether to spawn new threads.

We can configure our thread pool in the Weblogic Admin Console:

Oracle Weblogic让我们能够使用WorkManager更改自调优线程池。

与线程队列类似，WorkManager将线程池作为队列管理。然而，WorkManager会根据实时吞吐量添加动态线程。Weblogic定期对吞吐量进行分析，以优化线程利用率。

这对我们意味着什么?这意味着，虽然我们可能更改线程池，但web服务器最终将决定是否生成新线程。

我们可以在Weblogic管理控制台中配置线程池:

[![img](https://www.baeldung.com/wp-content/uploads/2020/02/Weblogic_screen_1.jpg)](https://www.baeldung.com/wp-content/uploads/2020/02/Weblogic_screen_1.jpg)

Updating the *Self Tuning Minimum Thread Pool Size* and *Self Tuning Thread Maximum Pool Size* values set the min and max boundaries for the WorkManagers.

更新*自调优最小线程池大小*和*自调优线程最大线程池大小*值设置workmanager的最小和最大边界。

Notice the *Stuck Thread Max Time* and *Stuck Thread Timer Interval* values. These help the WorkManager classify stuck threads.

Sometimes a long-running process may cause a build-up of stuck threads. The WorkManager will spawn new threads from the thread pool to compensate. Any update to these values could prolong the time to allow the process to finish.

**Stuck threads could be indicative of code problems, so it's always best to address the root cause rather than use a workaround.**

注意“*Stuck Thread Max Time*”和“*Stuck Thread Timer Interval*”值。这可以帮助WorkManager对被卡住的线程进行分类。

有时，长时间运行的进程可能会导致卡住的线程累积。WorkManager将从线程池中生成新的线程来补偿。对这些值的任何更新都可能延长流程完成的时间。

**线程卡住可能是代码问题的象征，所以最好是解决根本原因，而不是使用变通方法

## 6. Conclusion

In this quick article, we looked at multiple ways to configure application server thread pools.

While there are differences in how the application servers manage the various thread pools, they are configured using similar concepts.

Finally, let's remember that changing configuration values for web servers are not appropriate fixes for poor performing code and bad application designs.

在这篇快速文章中，我们讨论了配置应用程序服务器线程池的多种方法。

尽管应用程序服务器管理各种线程池的方式存在差异，但它们使用类似的概念进行配置。

最后，让我们记住，更改web服务器的配置值并不适合修复性能差的代码和糟糕的应用程序设计。