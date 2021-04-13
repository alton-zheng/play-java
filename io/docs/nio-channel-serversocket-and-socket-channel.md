# ServerSocketChannel 和 SocketChannel

TCP 网络 Channel ， 结合 `Selector`， `SelectionKey` 使用，在 NIO 体系中，俗称经典。经典 `Netty`  框架结合自身设计和 Java NIO 有机结合在一起。关于 Netty 这块，后续篇章会专门阐述。

ServerSocketChannel 和 SocketChannel ，其实，它们没啥神秘的，一起来揭晓吧。

&nbsp;

## ServerSocketChannel

![nio-channel-serversocket](images/nio-channel-serversocket.png)

&nbsp;

## SocketChannel

![nio channel socket channel](images/nio-channel-socket.png)

&nbsp;

## 总结

> 以上仅仅对 ServerSocketChannel 和 SocketChannel  进行了介绍
>
> 实际在代码中的运用，结合 API 和介绍的理论，进一步熟悉即可
>
> 后续会有 NIO 实操环节以及在它之上衍生的框架介绍以及原理剖析