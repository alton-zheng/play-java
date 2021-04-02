# ConcurrentSkipListMap 介绍

## 1. 概览

在这篇快速文章中，我们将查看 `java.util.concurrent` 包中 [$ConcurrentSkipListMap$](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ConcurrentSkipListMap.html)

这种结构允许我们以无锁的方式创建线程安全的逻辑。当我们想要在其他线程仍在向 map 插入数据时创建一个不可变的数据快照时，它是理想的解决问题的方法。

我们将解决**对事件流进行排序的问题，并使用该构造**获取最近60秒内到达的事件的快照。

&nbsp;

## 2. Stream 排序逻辑

假设我们有一个不断来自多个线程的事件流。我们需要能够获取最近 60 秒的事件，以及超过 60 秒的事件。

首先，让我们定义事件数据的结构:

```java
public class Event {
    private ZonedDateTime eventTime;
    private String content;

    // standard constructors/getters
}
```

&nbsp;

我们希望使用 *eventTime* 字段来保持事件排序。为了使用 *ConcurrentSkipListMap* 实现这一点，*我们需要在创建它的实例时将一个 *Comparator* 传递给它的构造函数:

```java
ConcurrentSkipListMap<ZonedDateTime, String> events
 = new ConcurrentSkipListMap<>(
 Comparator.comparingLong(v -> v.toInstant().toEpochMilli()));
```

&nbsp;

将使用它们的时间戳来比较所有到达的事件。使用 *comparingLong()* 方法并传递 extract 函数，该函数可以从*ZonedDateTime* 中获取 *long* 时间戳

当事件到达时，我们只需要使用 *put()* 方法将它们添加到映射中。注意，这个方法不需要任何显式的同步：

```java
public void acceptEvent(Event event) {
    events.put(event.getEventTime(), event.getContent());
}
```

&nbsp;

*ConcurrentSkipListMap* 将使用在构造函数中传递给它的 *Comparator* 来处理这些事件的排序。

*ConcurrentSkipListMap* 最显著的优点是可以以无锁的方式创建其数据的不可变快照的方法。要获取在过去一分钟内到达的所有事件，可以使用 *tailMap()* 方法并传递获取元素的时间。

```java
public ConcurrentNavigableMap<ZonedDateTime, String> getEventsFromLastMinute() {
    return events.tailMap(ZonedDateTime.now().minusMinutes(1));
}
```

&nbsp;

将返回过去一分钟内的所有事件。它将是一个不可变的快照，最重要的是，其他写线程可以向 *ConcurrentSkipListMap* 添加新的事件，而不需要做任何显式锁定。

我们现在可以通过使用 *headMap()* 方法获取一分钟后到达的所有事件：

```java
public ConcurrentNavigableMap<ZonedDateTime, String> getEventsOlderThatOneMinute() {
    return events.headMap(ZonedDateTime.now().minusMinutes(1));
}
```

这将返回超过1分钟的所有事件的一个不可变快照。以上所有方法都属于 *EventWindowSort* 类，我们将在下一节中使用。

&nbsp;

## 3. Sorting Stream Logic 测试

一旦我们使用 ConcurrentSkipListMap 实现了排序逻辑，我们现在可以通过创建两个写入线程来测试它，每个线程将发送100个事件:

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

每个线程都在调用 *acceptEvent()* 方法，将具有 *eventTime* 的事件从现在发送到 " now - 100秒"。

同时，我们可以调用 *getEventsFromLastMinute()* 方法，该方法将返回一分钟内事件的快照：

```java
ConcurrentNavigableMap<ZonedDateTime, String> eventsFromLastMinute 
  = eventWindowSort.getEventsFromLastMinute();
```

&nbsp;

在 *eventsFromLastMinute* 中的事件数量将在每次测试运行中变化，这取决于 producer 线程将发送事件到*EventWindowSort* 的速度。我们可以断言在返回的快照中没有一个超过 1 分钟的事件:

```java
long eventsOlderThanOneMinute = eventsFromLastMinute
  .entrySet()
  .stream()
  .filter(e -> e.getKey().isBefore(ZonedDateTime.now().minusMinutes(1)))
  .count();
 
assertEquals(eventsOlderThanOneMinute, 0);
```

&nbsp;

并且在一分钟窗口内快照中有多余 0 个的时间:

```java
long eventYoungerThanOneMinute = eventsFromLastMinute
  .entrySet()
  .stream()
  .filter(e -> e.getKey().isAfter(ZonedDateTime.now().minusMinutes(1)))
  .count();
 
assertTrue(eventYoungerThanOneMinute > 0);
```

&nbsp;

我们的 *getEventsFromLastMinute()* 使用了下面的 *tailMap()* 。

现在让我们测试 *getEventsOlderThatOneMinute()* ，它使用了*ConcurrentSkipListMap* 中的 *headMap()* 方法

```java
ConcurrentNavigableMap<ZonedDateTime, String> eventsFromLastMinute 
  = eventWindowSort.getEventsOlderThatOneMinute();
```

这一次，我们获得了超过1分钟的事件的快照。我们可以断言这样的事件多于零:

```java
long eventsOlderThanOneMinute = eventsFromLastMinute
  .entrySet()
  .stream()
  .filter(e -> e.getKey().isBefore(ZonedDateTime.now().minusMinutes(1)))
  .count();
 
assertTrue(eventsOlderThanOneMinute > 0);
```

&nbsp;
接下来，没有任何一个事件是在最后一分钟发生的:

```java
long eventYoungerThanOneMinute = eventsFromLastMinute
  .entrySet()
  .stream()
  .filter(e -> e.getKey().isAfter(ZonedDateTime.now().minusMinutes(1)))
  .count();
 
assertEquals(eventYoungerThanOneMinute, 0);
```

需要注意的最重要的一点是，我们可以在其它线程仍在向 *ConcurrentSkipListMap* 添加新值时获取数据快照。

&nbsp;

## 4. 总结

在这个快速教程中，我们了解了 *ConcurrentSkipListMap* 的基础知识，以及一些实际示例*.*

我们利用了 *ConcurrentSkipListMap* 的高性能来实现一个非阻塞算法，它可以为我们提供一个不可变的数据快照，即使同时有多个线程在更新映射。
