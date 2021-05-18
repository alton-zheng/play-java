## Netty 源码深入剖析 - EventExecutor

&nbsp;

示例中， EventExecutor 实例初始化入口，请见 [MultithreadEventExecutorGroup 专题](netty-source-analysis-multithread-eventloop-group.md)

```java
                // 使用 executor 和 参数 args 初始化 children 元素
                // newChild 方法以示例中来说，在 NioEventLoopGroup 中覆写。这里为 EventExecutor 专题入口
                children[i] = newChild(executor, args);
```

&nbsp;

### SelectStrategy

在讲解 newChild 方法前， 先剖析下 `DefaultSelectStrategyFactory` 如何创建一个新的 SelectStrategy 实例（为下面讲解 newChild 方法铺垫）。 看了 `DefaultSelectStrategyFactory` 专题的朋友， 应该记得此工厂类中有一个 newSelectStrategy 方法（之前示例还为执行到此方法，当前并未讲解它）， 现在正是剖析它的最好时机。

<img src="images/DefaultSelectStrategyFactory.png" alt="DefaultSelectStrategyFactory" style="zoom: 50%;" />

根据上图不难看出 `newSelectStrategy()` 方法来自其父类 `SelectStrategyFactory` 。  `DefaultSelectStrategyFactory`  对它进行了复写（override）。为了加深印象，这里再次将次工厂类源码贴出。

```java
public final class DefaultSelectStrategyFactory implements SelectStrategyFactory {
    public static final SelectStrategyFactory INSTANCE = new DefaultSelectStrategyFactory();

    // 工厂模式， 构建 DefaultSelectStrategyFactory 实例
    private DefaultSelectStrategyFactory() { }

    @Override
    public SelectStrategy newSelectStrategy() {
        return DefaultSelectStrategy.INSTANCE;
    }
}
```

&nbsp;

下面剖析此方法如何构建 `SelectStrategy` 实例，以及如何通过 SelectStrategy 控制潜在的 select 调用的结果。

```java
package io.netty.channel;

import io.netty.util.IntSupplier;

final class DefaultSelectStrategy implements SelectStrategy {

    /**
     * 示例代码， 源码剖析入口，构建一个 DefaultSelectStategy 实例
     */
    static final SelectStrategy INSTANCE = new DefaultSelectStrategy();

    private DefaultSelectStrategy() { }

    /**
     *
     * @param selectSupplier 具有 select 结果的 supplier 函数， () -> int
     * @param hasTasks  如果任务正在等待处理，则为 true。也就是说，此时有需要等待处理的任务
     * @return 如果下一步应该 blocking ， 那么选择 CONTINUE， 如果下一步应该是不选择，
     *         而是跳回 IO 循环，并再次尝试。任何值 >= 0 都被视为需要完成工作的指示符
     *
     * @throws Exception
     */
    @Override
    public int calculateStrategy(IntSupplier selectSupplier, boolean hasTasks) throws Exception {
        /**
         * hasTasks
         * true: 则获取 IntSupplier, 至于传入何种实现，根据具体场景，大家不妨猜猜，示例代码用的是什么实现
         * false: 返回 -1 值，代表此时没有正在正在执行的任务需要处理
         *        SelectStrategy.SELECT = -1
         */
        return hasTasks ? selectSupplier.get() : SelectStrategy.SELECT;
    }
}

```

看源码时，学会猜和推敲它的代码实际用处是很有必要的，上面有说过 `calculateStrategy` 猜猜哪里用，其实反过头看 newChild 方法如何在 NioEventLoopGroup 用就能看出来了。 这里不是关键点，只是提下如何看源码的一种方法罢了。

&nbsp;

### NioEventLoop

下面返回到 `NioEventLoopGroup` 类中的 `newChild` 方法。

```java
    /**
     * 示例中，初始化 EventExecutor 实例入口 （MultithreadEventExecutorGroup 类中进行初始化） ， EventLoop 继承自 EventExecutor
     * @param executor 基于 FastThreadLocalThread 运行 task 的 executor
     * @param args
     *        示例中的 SelectorProvider;
     *        DefaultSelectStrategyFactory.INSTANCE;
     *        RejectedExecutionHandlers.reject(); 默认处理方式 new RejectedExecutionException() 异常
     * @return EventLoop
     * @throws Exception
     */
    @Override
    protected EventLoop newChild(Executor executor, Object... args) throws Exception {

        // 源码示例中 args 只有 3 个参数，因此在这里 EventLoopTaskQueueFactory 实例 queryFactory 为 null
        EventLoopTaskQueueFactory queueFactory = args.length == 4 ? (EventLoopTaskQueueFactory) args[3] : null;

        /**
         * 将此 NioEventLoopGroup ,
         * 基于 FastThreadLocalThread 运行 task 的 executor ,
         * args 3个对象,
         * 以及 null queryFactory 传入 NioEventLoop 构造器中，创建实例。
         * 注意： 这里传入的是 args 中 DefaultSelectStrategyFactory 工厂类创建的 DefaultSelectStrategy
         * 
         */
        return new NioEventLoop(this, executor, (SelectorProvider) args[0],
            ((SelectStrategyFactory) args[1]).newSelectStrategy(), (RejectedExecutionHandler) args[2], queueFactory);
    }
```

&nbsp;

好了废话不多说， 现在来讲解 NioEventLoop (核心： EventLoop) 实例如何在示例代码中被创建。EventLoop 会在此带出来。

源码剖析到此，对于整个代码层级应该有写概念在里边了，下面用将它们继承和实现层级以图的形式展示出来。不仅仅包含示例代码部分，还包含了实现了 EventLoop 的 SingleThreadEventLoop 类，以及继承自 SingleThreadEventLoop 类的所有子类（包含 NioEventLoop）。具体自行看下图： 

![netty-arthitecture](images/netty-arthitecture.png)

&nbsp;

下面来看如何构建 NioEventLoop 实例。  在代码讲解的过程中会遇到很多之前没剖析过的类和接口，在学习过程中请对照上图来进行剖析。

![NioEventLoop](images/NioEventLoop.png)

&nbsp;

上面图可以看出， 构建 NioEventLoop 实例中，不断地寻父，从 `SingleThreadEventExecutor` 开始属于 Java 原生库中关于高并发，线程核心库，这里不对它进行阐述（在这里只要知道它是 Java 单个线程的线程池即可，想要了解更多 `SingleThreadEventExecutor` 的信息，请看 Java Concurrency 相关知识）。

```java
```

&nbsp;

### Netty Selector

&nbsp;

#### SelectedSelectionKeySet

SelectedSelectionKeySet 内部很简单，使用数组代替原 Selector 的中的 HashSet，提高性能。数组默认大小为1024，容量不够，扩容 2 倍。

```java
package io.netty.channel.nio;

import java.nio.channels.SelectionKey;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;


final class SelectedSelectionKeySet extends AbstractSet<SelectionKey> {

    SelectionKey[] keys;
    int size;

    // 无参构造，初始化 SelectionKey[] 数组
    SelectedSelectionKeySet() {
        keys = new SelectionKey[1024];
    }

    @Override
    public boolean add(SelectionKey o) {
        if (o == null) {
            return false;
        }

        keys[size++] = o;

        // 当大小等于 SelectionKey[] 长度时，进行扩容
        if (size == keys.length) {
            increaseCapacity();
        }

        return true;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<SelectionKey> iterator() {
        return new Iterator<SelectionKey>() {
            private int idx;

            @Override
            public boolean hasNext() {
                return idx < size;
            }

            @Override
            public SelectionKey next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return keys[idx++];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * 重置
     */
    void reset() {
        reset(0);
    }

    /**
     * 重置 为 null
     * @param start
     */
    void reset(int start) {
        // 将 keys 所有元素置为 null
        Arrays.fill(keys, start, size, null);
        size = 0;
    }

    /**
     * 增容，扩大 2 倍
     */
    private void increaseCapacity() {
        SelectionKey[] newKeys = new SelectionKey[keys.length << 1];
        System.arraycopy(keys, 0, newKeys, 0, size);
        keys = newKeys;
    }
}
```





上一篇： [Netty 源码深入剖析 - ThreadFactory](netty-source-analysis-thread-factory.md)

下一篇：[Netty 源码深入剖析 - EventExecutorChooser](netty-source-analysis-event-executor-chooser.md)