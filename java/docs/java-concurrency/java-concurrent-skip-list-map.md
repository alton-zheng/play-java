# Guide to the ConcurrentSkipListMap

## **1. Overview**

In this quick article, we'll be looking at the [*ConcurrentSkipListMap*](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ConcurrentSkipListMap.html) class from the *java.util.concurrent* package.

This construct allows us to create thread-safe logic in a lock-free way. It's ideal for problems when we want to make an immutable snapshot of the data while other threads are still inserting data into the map.

We will be solving a problem of **sorting a stream of events and getting a snapshot of the events that arrived in the last 60 seconds using that construct**.

在这篇快速文章中，我们将查看*java.util中的[*ConcurrentSkipListMap*](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ConcurrentSkipListMap.html)类。并发*包。

这种结构允许我们以无锁的方式创建线程安全的逻辑。当我们想要在其他线程仍在向map插入数据时创建一个不可变的数据快照时，它是理想的解决问题的方法。

我们将解决**对事件流进行排序的问题，并使用该构造**获取最近60秒内到达的事件的快照。

## **2. Stream Sorting Logic**

Let's say that we have a stream of events that are continually coming from multiple threads. We need to be able to take events from the last 60 seconds, and also events that are older than 60 seconds.

First, let's define the structure of our event data:

假设我们有一个不断来自多个线程的事件流。我们需要能够获取最近60秒的事件，以及超过60秒的事件。

首先，让我们定义事件数据的结构:

```java
public class Event {
    private ZonedDateTime eventTime;
    private String content;

    // standard constructors/getters
}
```

We want to keep our events sorted using the *eventTime* field. To achieve this using the *ConcurrentSkipListMap,* we need to pass a *Comparator* to its constructor while creating an instance of it:

我们希望使用*eventTime*字段来保持事件排序。为了使用*ConcurrentSkipListMap实现这一点，*我们需要在创建它的实例时将一个*Comparator*传递给它的构造函数:

```java
ConcurrentSkipListMap<ZonedDateTime, String> events
 = new ConcurrentSkipListMap<>(
 Comparator.comparingLong(v -> v.toInstant().toEpochMilli()));
```

We'll be comparing all arrived events using their timestamps. We are using the *comparingLong()* method and passing the extract function that can take a *long* timestamp from the *ZonedDateTime.*

When our events are arriving, we need only to add them to the map using the *put()* method. Note that this method does not require any explicit synchronization:

我们希望使用*eventTime*字段来保持事件排序。为了使用*ConcurrentSkipListMap实现这一点，*我们需要在创建它的实例时向它的构造器传递一个*Comparator*——我们将使用它们的时间戳来比较所有到达的事件。我们使用*comparingLong()*方法并传递extract函数，该函数可以从*ZonedDateTime.*中获取*long*时间戳

当事件到达时，我们只需要使用*put()*方法将它们添加到映射中。注意，这个方法不需要任何显式的同步:

```java
public void acceptEvent(Event event) {
    events.put(event.getEventTime(), event.getContent());
}
```

The *ConcurrentSkipListMap* will handle the sorting of those events underneath using the *Comparator* that was passed to it in the constructor.

ConcurrentSkipListMap*将使用在构造函数中传递给它的*Comparator*来处理这些事件的排序。

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_0" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_0" width="728" height="90" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" srcdoc="" data-google-container-id="5" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

The most notable pros of the *ConcurrentSkipListMap* are the methods that can make an immutable snapshot of its data in a lock-free way. To get all events that arrived within the past minute, we can use the *tailMap()* method and pass the time from which we want to get elements:

ConcurrentSkipListMap*最显著的优点是可以以无锁的方式创建其数据的不可变快照的方法。要获取在过去一分钟内到达的所有事件，可以使用*tailMap()*方法并传递获取元素的时间:或。

```java
public ConcurrentNavigableMap<ZonedDateTime, String> getEventsFromLastMinute() {
    return events.tailMap(ZonedDateTime.now().minusMinutes(1));
}
```

It will return all events from the past minute. It will be an immutable snapshot and what is the most important is that other writing threads can add new events to the *ConcurrentSkipListMap* without any need to do explicit locking.

We can now get all events that arrived later that one minute from now – by using the *headMap()* method:

它将返回过去一分钟内的所有事件。它将是一个不可变的快照，最重要的是，其他写线程可以向*ConcurrentSkipListMap*添加新的事件，而不需要做任何显式锁定。

我们现在可以通过使用*headMap()*方法获取一分钟后到达的所有事件:

```java
public ConcurrentNavigableMap<ZonedDateTime, String> getEventsOlderThatOneMinute() {
    return events.headMap(ZonedDateTime.now().minusMinutes(1));
}
```

This will return an immutable snapshot of all events that are older than one minute. All of the above methods belong to the *EventWindowSort* class, which we'll use in the next section.

这将返回超过1分钟的所有事件的一个不可变快照。以上所有方法都属于*EventWindowSort*类，我们将在下一节中使用。

## **3. Testing the Sorting Stream Logic**

Once we implemented our sorting logic using the *ConcurrentSkipListMap,* we can now **test it by creating two writer threads** that will send one hundred events each:

一旦我们使用ConcurrentSkipListMap实现了排序逻辑，我们现在可以通过创建两个写入线程来测试它，每个线程将发送100个事件:

```java
ExecutorService executorService = Executors.newFixedThreadPool(3);
EventWindowSort eventWindowSort = new EventWindowSort();
int numberOfThreads = 2;

Runnable producer = () -> IntStream
  .rangeClosed(0, 100)
  .forEach(index -> eventWindowSort.acceptEvent(
      new Event(ZonedDateTime.now().minusSeconds(index), UUID.randomUUID().toString()))
  );

for (int i = 0; i < numberOfThreads; i++) {
    executorService.execute(producer);
}
```

Each thread is invoking the *acceptEvent()* method, sending the events that have *eventTime* from now to “now minus one hundred seconds”.

In the meantime, we can invoke the *getEventsFromLastMinute()* method that will return the snapshot of events that are within the one minute window:

每个线程都在调用*acceptEvent()*方法，将具有*eventTime*的事件从现在发送到" now - 100秒"。

同时，我们可以调用*getEventsFromLastMinute()*方法，该方法将返回一分钟内事件的快照:

```java
ConcurrentNavigableMap<ZonedDateTime, String> eventsFromLastMinute 
  = eventWindowSort.getEventsFromLastMinute();
```

The number of events in the *eventsFromLastMinute* will be varying in each test run depending on the speed at which the producer threads will be sending the events to the *EventWindowSort.* We can assert that there is not a single event in the returned snapshot that is older than one minute:

在*eventsFromLastMinute*中的事件数量将在每次测试运行中变化，这取决于制作人线程将发送事件到*EventWindowSort的速度。*我们可以断言在返回的快照中没有一个超过1分钟的事件:

```java
long eventsOlderThanOneMinute = eventsFromLastMinute
  .entrySet()
  .stream()
  .filter(e -> e.getKey().isBefore(ZonedDateTime.now().minusMinutes(1)))
  .count();
 
assertEquals(eventsOlderThanOneMinute, 0);
```

And that there are more than zero events in the snapshot that are within the one minute window:

并且在一分钟窗口内快照中有多于零的事件:

```java
long eventYoungerThanOneMinute = eventsFromLastMinute
  .entrySet()
  .stream()
  .filter(e -> e.getKey().isAfter(ZonedDateTime.now().minusMinutes(1)))
  .count();
 
assertTrue(eventYoungerThanOneMinute > 0);
```

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_1" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_1" width="728" height="90" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" sandbox="allow-forms allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts allow-top-navigation-by-user-activation" srcdoc="" data-google-container-id="6" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

Our *getEventsFromLastMinute()* uses the *tailMap()* underneath.

Let's test now the *getEventsOlderThatOneMinute()* that is using the *headMap()* method from the *ConcurrentSkipListMap:*

我们的*getEventsFromLastMinute()*使用了下面的*tailMap()*。

现在让我们测试*getEventsOlderThatOneMinute()*，它使用了*ConcurrentSkipListMap:*中的*headMap()*方法

```java
ConcurrentNavigableMap<ZonedDateTime, String> eventsFromLastMinute 
  = eventWindowSort.getEventsOlderThatOneMinute();
```

This time we get a snapshot of events that are older than one minute. We can assert that there are more than zero of such events:

这一次，我们获得了超过1分钟的事件的快照。我们可以断言这样的事件多于零:

```java
long eventsOlderThanOneMinute = eventsFromLastMinute
  .entrySet()
  .stream()
  .filter(e -> e.getKey().isBefore(ZonedDateTime.now().minusMinutes(1)))
  .count();
 
assertTrue(eventsOlderThanOneMinute > 0);
```

And next, that there is not a single event that is from within the last minute:
接下来，没有任何一个事件是在最后一分钟发生的:

```java
long eventYoungerThanOneMinute = eventsFromLastMinute
  .entrySet()
  .stream()
  .filter(e -> e.getKey().isAfter(ZonedDateTime.now().minusMinutes(1)))
  .count();
 
assertEquals(eventYoungerThanOneMinute, 0);
```

The most important thing to note is that **we can take the snapshot of data while other threads are still adding new values** to the *ConcurrentSkipListMap.*

需要注意的最重要的一点是，**我们可以在其他线程仍在向*ConcurrentSkipListMap.*添加新值时获取数据快照

## **4. Conclusion**

In this quick tutorial, we had a look at the basics of the *ConcurrentSkipListMap*, along with some practical examples*.*

We leveraged the high performance of the *ConcurrentSkipListMap* to implement a non-blocking algorithm that can serve us an immutable snapshot of data even if at the same time multiple threads are updating the map.

The implementation of all these examples and code snippets can be found in the [GitHub project](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-concurrency-collections); this is a Maven project, so it should be easy to import and run as it is.

在这个快速教程中，我们了解了*ConcurrentSkipListMap*的基础知识，以及一些实际示例*.*

我们利用了*ConcurrentSkipListMap*的高性能来实现一个非阻塞算法，它可以为我们提供一个不可变的数据快照，即使同时有多个线程在更新映射。

所有这些示例和代码片段的实现可以在[GitHub项目](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java-concurrency-collections)中找到;这是一个Maven项目，所以应该很容易导入和运行。