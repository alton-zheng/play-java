# Netty vs Java NIO

&nbsp;

Netty 相关的知识，不会以官网的路线进行介绍，太浅（了解不到核心）

Netty NIO 底层就是 Java 原生 NIO 的高层抽象，极大的丰富了 Java Native NIO，更易用。但是，如果仅仅是想简单使用它， 不去了解它底层，那么下面知识完全可以忽略

是否对 Java Native NIO 有深刻认识，就是学习 Netty 的基石。熟悉了 Java Native NIO ，就意味着 Netty 就懂了一半。在此处描述这些，不是说没有 Java Native NIO 的基础，就学不好或不能学习 Netty；而是随着 Netty 不断深入学习，还是要回过头来对 Java Native NIO 的学习。可以这么说，不学习 Java Native NIO ，对 Netty 的学习仅仅在表面，无法深入（Java Native NIO 和 操作系统 IO 以及 Kernel 有密切的联系）。

作为主角 Netty， 下面的介绍都会偏向它。 Java NIO 相关内容，可参与 IO 专题。

下面针对他们进行比较来进一步学习 Netty。

&nbsp;

## 核心类

>  首先将Netty 和 Java Native NIO 相关的核心类先列出

| Item               | Java NIO                     | Netty                                                        |
| ------------------ | ---------------------------- | ------------------------------------------------------------ |
| byte buffer        | `java.nio.ByteBuffer`        | `io.netty.buffer.ByteBuf`                                    |
| channel            | `java.nio.channels.Channel`  | `io.netty.channel.Channel`                                   |
| socket nio package | `java.nio.channels`          | `io.netty.channel.socket.nio`                                |
| server socket      | `ServerSocketChannel`        | `EpollServerSocketChannel` ,  ~~`OioServerSocketChannel`~~ , `NioServerSocketChannel` |
| socket             | `SocketChannel`              | `EpollSocketChannel`, `NioSocketChannel`，~~`OioSocketChannel`~~ |
| network channel    | `DatagramChannel`            | ~~`OioDatagramChannel`~~ ,`NioDatagramChannel`               |
| selector           | `java.nio.channels.Selector` | `io.netty.channel.nio.NioEventLoop`                          |
|                    |                              |                                                              |

Oio 系列，Old-Blocking-IO 已经被 Netty 标记为 @Deprecated 

- deprecated use NIO / EPOLL / KQUEUE transport.
- 后续的 Netty 介绍中，将不再有相关内容。

&nbsp;

## ByteBuf v.s ByteBuffer

ByteBuf 在 ByteBuffer 的基础上，加强了很多。

- R/W 双指针
- ByteBuf 可扩容 max Capacity

&nbsp;

## Nio Socket 系列

NioServerSocketChannel , NioSocketChannel 相比 Java Native Nio 不需要手动配置是否阻塞。

- 默认 non-blocking

&nbsp;

## Selector v.s NioEventLoop

Netty 中的 NioEventLoop 将 Java NIO  Selector 和 SelectionKey 有机结合在一起。简单使用 Netty 不需要再关注 Selector 和 SelectionKey 的细节。

