# Configuring Thread Pools for Java Web Servers

Last modified: November 21, 2020

by [Kyle Doyle](https://www.baeldung.com/author/kyle-doyle/)



- [HTTP Client-Side](https://www.baeldung.com/category/http/)
- [Java](https://www.baeldung.com/category/java/)**+**

- [Glassfish](https://www.baeldung.com/tag/glassfish/)
- [Java Concurrency](https://www.baeldung.com/tag/java-concurrency/)
- [Tomcat](https://www.baeldung.com/tag/tomcat/)
- [Weblogic](https://www.baeldung.com/tag/weblogic/)

### **Get started with Spring 5 and Spring Boot 2, through the \*Learn Spring\* course:**

**[>> CHECK OUT THE COURSE](https://www.baeldung.com/ls-course-start)**

## 1. Introduction

In this tutorial, we take a look at thread pool configuration for Java web application servers such as Apache Tomcat, Glassfish Server, and Oracle Weblogic.

## 2. Server Thread Pools

**[Server thread pools](https://www.baeldung.com/thread-pool-java-and-guava) are used and managed by a web application server for a deployed application.** These thread pools exist outside of the web container or servlet so they are not subject to the same context boundary.

Unlike application threads, server threads exist even after a deployed application is stopped.

## 3. Apache Tomcat

First, we can configure Tomcat's server thread pool via the *Executor* configuration class in our *server.xml*:

```xml
<Executor name="tomcatThreadPool" namePrefix="catalina-exec-" maxThreads="150" minSpareThreads="25"/>
```

*minSpareThreads* is the smallest the pool will be, including at startup. *maxThreads* is the largest the pool will be **before the server starts queueing up requests.**

Tomcat defaults these to 25 and 200, respectively. In this configuration, we've made the thread pool a bit smaller than the default.

### 3.1. Embedded Tomcat

Similarly, we can alter an [embedded Tomcat server](https://www.baeldung.com/spring-boot-configure-tomcat) for Spring Boot to configure a thread pool by setting an application property:

```javascript
server.tomcat.max-threads=250
```

Starting with Boot 2.3, the property has changed to:

```xml
server.tomcat.threads.max=250
```

## 4. Glassfish

Next, let's update our Glassfish server.

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_0" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_0" width="728" height="90" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" sandbox="allow-forms allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts allow-top-navigation-by-user-activation" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="c" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

Glassfish uses an admin command in contrast to Tomcat's XML configuration file, *server.xml.* From the prompt, we run:

```shell
create-threadpool
```

We can add to *create-threadpool* the flags *maxthreadpoolsize* and *minthreadpoolsize.* They function similarly to Tomcat *minSpareThreads* and *maxThreads*:

```shell
--maxthreadpoolsize 250 --minthreadpoolsize 25
```

We can also specify how long a thread can be idle before returning to the pool:

```shell
--idletimeout=2
```

And then, we supply the name of our thread pool at the end:

```shell
asadmin> create-threadpool --maxthreadpoolsize 250 --minthreadpoolsize 25 --idletimeout=2 threadpool-1
```

## 5. Weblogic

Oracle Weblogic gives us the ability to alter a self-tuning thread pool with a WorkManager.

Similarly to thread queues, a WorkManager manages a thread pool as a queue. However, the WorkManager adds dynamic threads based on real-time throughput. Weblogic performs analysis on throughput regularly to optimize thread utilization.

What does this mean for us? It means that while we may alter the thread pool, the web server will ultimately decide on whether to spawn new threads.

We can configure our thread pool in the Weblogic Admin Console:

[![img](https://www.baeldung.com/wp-content/uploads/2020/02/Weblogic_screen_1.jpg)](https://www.baeldung.com/wp-content/uploads/2020/02/Weblogic_screen_1.jpg)

Updating the *Self Tuning Minimum Thread Pool Size* and *Self Tuning Thread Maximum Pool Size* values set the min and max boundaries for the WorkManagers.

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_1" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_1" width="728" height="90" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" sandbox="allow-forms allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts allow-top-navigation-by-user-activation" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="d" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

Notice the *Stuck Thread Max Time* and *Stuck Thread Timer Interval* values. These help the WorkManager classify stuck threads.

Sometimes a long-running process may cause a build-up of stuck threads. The WorkManager will spawn new threads from the thread pool to compensate. Any update to these values could prolong the time to allow the process to finish.

**Stuck threads could be indicative of code problems, so it's always best to address the root cause rather than use a workaround.**

## 6. Conclusion

In this quick article, we looked at multiple ways to configure application server thread pools.

While there are differences in how the application servers manage the various thread pools, they are configured using similar concepts.

Finally, let's remember that changing configuration values for web servers are not appropriate fixes for poor performing code and bad application designs.