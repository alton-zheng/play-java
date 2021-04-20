# Netty 介绍

&nbsp;

## 1. 介绍

在本文中，我们将研究 Netty - 一个异步 event-driven 网络应用程序框架。

Netty 的主要目的是基于 NIO（或可能是 NIO.2）构建具有网络和业务逻辑组件的分离和松耦合的 high-performance 协议服务器。它实现了众所周知的协议，例如 HTTP，或自己的特定协议。

&nbsp;

## 2. 核心概念

Netty 是一个 non-blocking 框架。与 blocking IO相比，Netty 有着高吞吐量的特性。**了解 Java non-blocking IO 对于了解 Netty 的核心组件及其关系至关重要。**在上一篇文章中，已经将 Java NIO 的重要性进行了阐述！下面结合 Java NIO 和 Netty 对几个核心组件进行简单介绍。

&nbsp;

### **2.1. Channel**

*Channel* 是 Java NIO 的基础。它表示一个打开的（open）连接，能够执行 IO 操作（例如，读取和写入）。

&nbsp;

### **2.2. Future**

Netty 中 Channel 上的每个 IO 操作都是 non-blocking。

这意味着调用后会立即返回所有操作。标准 Java 库中有一个 *Future* 接口，但是对于 Netty 而言并不方便 - 我们只能向 *Future* 询问操作的完成情况，或者在操作完成之前阻塞当前线程。

这就是 Netty 拥有自己的 `ChannelFuture` 接口的原因。我们可以将 `callback` 传递给 *ChannelFuture*，该 `callback` 将在操作完成时被调用。

> 对此，多说一句， 其实现在的 Java ， Future 的实现类中已经可以支持 callback 处理，Netty 框架研发时， Java 老版本还未支持。对于 Netty 而言，未免不是一种遗憾，加大了开发成本。

&nbsp;

### **2.3. Event 和 Handler**

Netty 使用 event-driven 应用程序范式，因此数据处理的 pipeline 是经过 handler 事件链。`event` 和 `handler` 可以与 `inbound` 和 `outbound` 数据流相关。inbound 时间可以是以下各项：

- channel 激活和停用
- read 操作 event
- 异常 event
- 用户 event

outbound event 更简单，通常与 `open`/`close` 连接以及 `write`/`flush` 数据有关。

&nbsp;

`Netty` 应用程序由几个 `network` 和应用程序逻辑 `event` 及其 `handler` 组成。`channel` 事件 `handler` 基本接口是 *`ChannelHandler`* 及其父类 *`ChannelOutboundHandler`* 和  *`ChannelInboundHandler`*。

`Netty` 提供了  *`ChannelHandler`*  实现的巨大层次结构*。*值得注意的是 `adapter` 只是 empty 实现，例如*ChannelInboundHandlerAdapter* 和 *ChannelOutboundHandlerAdapter* 。当我们只需要处理所有 `event` 的子集时，我们可以 `extends1` 这些适配器。

而且，有许多特定协议（例如HTTP）的实现，例如 *HttpRequestDecoder，HttpResponseEncoder，HttpObjectAggregator*。在 Netty 的 Javadoc 中熟悉它们对后续使用 HTTP 协议会很有帮助。

&nbsp;

### **2.4. Encoder 和 Decoders**

在使用 network 协议时，我们需要对数据进行序列化和反序列化的操作。为此，Netty 为能够解码传入数据的 **`decoder`**  引入了 *ChannelInboundHandler* 的特殊扩展。大多数 **decoder** 的基类是 *ByteToMessageDecoder* 。

为了对传出数据进行 encode，Netty 具有 *ChannelOutboundHandler的* 扩展，称为 **Encoder**。MessageToByteEncoder* 是大多数 **Encoder** 实现的基础*。*我们可以使用 **Decoder** 将消息从 byte 序列转换为 Java 对象，反之则使用 **`Encoder`**。

&nbsp;

## 3. Server Application 示例

让我们创建一个代表简单 protocol 服务器的项目，该服务器接收 request，执行计算并发送 response。

&nbsp;

### 3.1. Dependencies

首先，我们需要在 *pom.xml* 中提供 Netty 依赖项：

```xml
<dependency>
    <groupId>io.netty</groupId>
    <artifactId>netty-all</artifactId>
    <version>4.1.10.Final</version>
</dependency>
```

我们可以在 [Maven Central](https://search.maven.org/classic/#search|gav|1|g%3A"io.netty" AND a%3A"netty-all") 找到最新版本。

&nbsp;

### **3.2. Data Model**

请求数据 class 将具有以下结构：

```java
package com.baeldung.netty;

public class RequestData {
    private int intValue;
    private String stringValue;

    int getIntValue() {
        return intValue;
    }

    void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    String getStringValue() {
        return stringValue;
    }

    void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    @Override
    public String toString() {
        return "RequestData{" + "intValue=" + intValue + ", stringValue='" + stringValue + '\'' + '}';
    }
}
```

假设 server 收到 request 将 intValue * 2 并返回。因此 response 仅仅有单个 int 值：

```java
package com.baeldung.netty;

public class RequestData {
    private int intValue;
    private String stringValue;

    int getIntValue() {
        return intValue;
    }

    void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    String getStringValue() {
        return stringValue;
    }

    void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    @Override
    public String toString() {
        return "RequestData{" + "intValue=" + intValue + ", stringValue='" + stringValue + '\'' + '}';
    }
}

```

&nbsp;

### 3.3. Request Decoder

现在，我们需要为我们的协议消息创建编码器和解码器。

应当注意，Netty 与 socket 接收 buffer 一起使用，它不是表示为队列，而是表示为一堆 byte。这意味着当 server 未收到完整消息时，可以调用我们的 inbound handler。

比须确保在处理之前接收的信息是完整的，并且有很多方法可以做到这一点。

首先，我们可以创建一个临时的 *ByteBuf* ，并将所有 inbound byte 添加（append）到该 ByteBuf 上，直到获得所需的所有 byte 为止：

&nbsp;

```java
package com.baeldung.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class SimpleProcessingHandler extends ChannelInboundHandlerAdapter {
    private ByteBuf tmp;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        System.out.println("Handler added");
        tmp = ctx.alloc().buffer(4);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        System.out.println("Handler removed");
        tmp.release();
        tmp = null;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf m = (ByteBuf) msg;
        tmp.writeBytes(m);
        m.release();
        if (tmp.readableBytes() >= 4) {
            RequestData requestData = new RequestData();
            requestData.setIntValue(tmp.readInt());
            ResponseData responseData = new ResponseData();
            responseData.setIntValue(requestData.getIntValue() * 2);
            ChannelFuture future = ctx.writeAndFlush(responseData);
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }
}
```

上面示例看起来有些怪异，但可以帮助我们了解 Netty 的工作方式。当其相应的事件发生时，将调用 handler 每个方法。因此，我们在添加 handler 初始化 buffer ，在接收到新 byte 时将其填充数据，并在获得足够数据时开始对其进行处理。

&nbsp;

我们故意不使用 *stringValue*，以这种方式进行 decode 将变得复杂一些，这里不是阐述如何编写可以在生产环境中也能用的代码，仅仅阐述 Netty 的简单使用。回归正题， 这就是 Netty 提供有用的 decode 类的原因，这些类是 *ChannelInboundHandler* 的实现类：*ByteToMessageDecoder* 和 *ReplayingDecoder*。

如上所述，我们可以使用 Netty 创建 channel 处理 pipeline。因此，我们可以将 **Decoder** 作为第一个 hander，然后可以使用处理逻辑 handler。

接下来显示RequestData的解码器：

```java
package com.baeldung.netty;

import java.nio.charset.Charset;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

public class RequestDecoder extends ReplayingDecoder<RequestData> {

    private final Charset charset = Charset.forName("UTF-8");

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        RequestData data = new RequestData();
        data.setIntValue(in.readInt());
        int strLen = in.readInt();
        data.setStringValue(in.readCharSequence(strLen, charset).toString());
        out.add(data);
    }
}
```

这个 **Decoder** 的想法很简单。它使用 *ByteBuf* 的实现，当 buffer 中的数据不足以进行读取操作时，该实现将引发异常。

当捕获到异常时， buffer 将倒退到开头，并且 **Decoder** 等待数据的新部分。当解码执行后， 列表（out）不是空时， 解码停止。

&nbsp;

### 3.4. Response Encoder

除了解码 *RequestData* 之外，我们还需要对 message 进行编码。此操作更为简单，因为在进行 write 操作时，我们拥有完整的消息数据。

我们可以在 main handler 中将数据写入 *Channel* ，也可以分离逻辑代码并创建继承 *MessageToByteEncoder* 的处理程序，该处理程序将捕获 write *ResponseData* 操作：

```java

package com.baeldung.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ResponseDataEncoder extends MessageToByteEncoder<ResponseData> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ResponseData msg, ByteBuf out) throws Exception {
        out.writeInt(msg.getIntValue());
    }
}
```

&nbsp;

### 3.5. Request Processing

由于我们在独立的 handler 中执行了编解码，因此我们需要更改 *ProcessingHandler* ：

```java
package com.baeldung.netty;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ProcessingHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RequestData requestData = (RequestData) msg;
        ResponseData responseData = new ResponseData();
        responseData.setIntValue(requestData.getIntValue() * 2);
        ChannelFuture future = ctx.writeAndFlush(responseData);
        future.addListener(ChannelFutureListener.CLOSE);
        System.out.println(requestData);
    }
}
```

&nbsp;

### 3.6. Server Bootstrap

现在，将它们放在一起并运行我们的服务器：

```java
package com.baeldung.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {

    private int port;

    private NettyServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }
        new NettyServer(port).run();
    }

    private void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new RequestDecoder(), new ResponseDataEncoder(), new ProcessingHandler());
                }
            }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
```

上面的 server bootstrap 示例中使用的 class 详细信息可以在其 Javadoc 中找到。最有趣的部分是这一行：

```java
ch.pipeline().addLast(
  new RequestDecoder(), 
  new ResponseDataEncoder(), 
  new ProcessingHandler());
```

在这里，我们定义了 inbound 和 outbound handler，它们将以正确的顺序处理 request 和 output。

&nbsp;

## 4. Client Application

client 应执行反向编码和解码，因此我们需要具有 `RequestDataEncoder` 和 `ResponseDataDecoer` ：

```java
package com.baeldung.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;

public class RequestDataEncoder extends MessageToByteEncoder<RequestData> {

    private final Charset charset = Charset.forName("UTF-8");

    @Override
    protected void encode(ChannelHandlerContext ctx, RequestData msg, ByteBuf out) throws Exception {
        out.writeInt(msg.getIntValue());
        out.writeInt(msg.getStringValue().length());
        out.writeCharSequence(msg.getStringValue(), charset);
    }
}

package com.baeldung.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

public class ResponseDataDecoder extends ReplayingDecoder<ResponseData> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        ResponseData data = new ResponseData();
        data.setIntValue(in.readInt());
        out.add(data);
    }
}
```

&nbsp;

另外，我们需要定义一个 *ClientHandler* ，它将发送 request 并从服务器接收 response：

```java
package com.baeldung.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        RequestData msg = new RequestData();
        msg.setIntValue(123);
        msg.setStringValue("all work and no play makes jack a dull boy");
        ctx.writeAndFlush(msg);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(msg);
        ctx.close();
    }
}
```

&nbsp;

现在编写 client 端 bootstrap 程序：

```java
package com.baeldung.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {
    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 8080;
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new RequestDataEncoder(), new ResponseDataDecoder(), new ClientHandler());
                }
            });

            ChannelFuture f = b.connect(host, port).sync();

            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
```

如我们所见，server bootstrap 有许多共同点。

现在，我们可以运行 client 的 main 方法，并查看 console 输出。如预期的那样，我们获得了具有 *intValue* 等于 246 的 *ResponseData*。

&nbsp;

## 5. 总结

在本文中，我们对 Netty 进行了快速介绍。我们展示了其核心组件，例如 *Channel* 和 *ChannelHandler*。另外，我们已经制作了一个简单的 non-block 协议 server 和 client。

> 可以理解此篇文章中的 server 和 client 属于 RPC 协议， 可以本机调用，也可以远程调用。
>
> 至于 RPC ，以后有机会再进行讲解。
>
> 至此，已经知道了 Netty 的简单使用了。