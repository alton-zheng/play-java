# Channel

&nbsp;

## 概览

<img src="images/nio-channel.png" alt="NIO Channel" style="zoom: 67%;" />

&nbsp;

## 核心接口

![interface](images/nio-channel-interface.png)

&nbsp;

> AsynchronousChannel 接口为了支持 AIO 而存在，在 NIO 专题不对它进行展开讲解

&nbsp;

### ReadableByteBuffer

![readable-bytebuffer](images/nio-channel-readable-byte-channel.png)

&nbsp;

### WriteableByteBuffer

![writable byte channel](images/nio-channel-writable-byte-channel.png)

&nbsp;

### ByteChannel

![nio-channel-byte-channel](images/nio-channel-byte-channel.png)

&nbsp;

### NetworkChannal

![nio network channel](images/nio-channel-network-channel.png)

&nbsp;

### InterruptibleChannel

![nio-channel-interruptible-channel](images/nio-channel-interruptible-channel.png)

&nbsp;

## Channel 实现

> Channel 实现篇幅长，另外讨论
>
> [Chnnel 实现](nio-channel-implement.md)

![nio-channel-implement](images/nio-channel-implement.png)

&nbsp;

## Selector

![nio-channel-selector](images/nio-channel-selector.png)

> Selector 属于 Channel 的核心技术，这里不做多介绍，会起另外一篇来进行专门介绍
>
> [Selector](nio-channel-selector.md)

