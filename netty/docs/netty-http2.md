# Netty 中 HTTP/2 应用

&nbsp;

## 1. 概览

[Netty](https://netty.io/) 是一个基于 NIO 的 client-server 框架，它使 Java 开发人员能够在 network layer 上进行操作。使用此框架，developer 可以 build 自己的任何已知协议甚至自定义协议的实现。

为了基本了解框架，[Netty 介绍](netty-introduction.md)  是一个不错的开始。

在本教程中，我们将看到如何在Netty中实现 HTTP / 2 serve 和 client。

&nbsp;

## 2. 什么是 *HTTP/2*?

顾名思义，[HTTP 版本 2 或简称为 HTTP/2](https://httpwg.org/specs/rfc7540.html)  是超文本（Hypertext）传输协议的较新版本。

大约在 1989 年，互联网诞生了，HTTP/1.0诞生了。在1997年，它升级到了1.1版。但是，直到 2015 年，它才进行了重大升级，即 version 2。

撰写本文时，[HTTP/3](https://blog.cloudflare.com/http3-the-past-present-and-future/)  也可用，尽管并非所有浏览器默认都支持。

`HTTP/2` 仍然是该协议的最新版本，已被广泛接受和实现。与其他版本相比，它与以前的版本有很大的不同，它具有 multiplex 和 server push 功能。

`HTTP/2` 中的通信通过一组称为 frame(桢) 的字节进行，并且多个 frame 形成一个流。

在我们的代码示例中，我们将看到Netty如何处理 [HEADER](https://tools.ietf.org/html/rfc7540#section-6.2)，[DATA](https://tools.ietf.org/html/rfc7540#section-6.1) 和 [SETTINGS](https://tools.ietf.org/html/rfc7540#section-6.5) 框架的交换。

&nbsp;

## 3. Server

首先看看如何在 Netty 中创建一个 `HTTP/2`  server。

&nbsp;

### 3.1. SslContext

Netty 支持 [通过 TLS 进行 `HTTP/2` 的 APN 协商](https://tools.ietf.org/html/rfc7301)。因此，我们需要创建服务器的第一件事是 [*SslContext*](https://netty.io/4.1/api/io/netty/handler/ssl/SslContext.html)：

```java
    public static SslContext createSSLContext(boolean isServer) throws SSLException, CertificateException {

        SslContext sslCtx;

        SelfSignedCertificate ssc = new SelfSignedCertificate();

        if (isServer) {
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey())
                .sslProvider(SslProvider.JDK)
                .ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
                .applicationProtocolConfig(new ApplicationProtocolConfig(Protocol.ALPN,
                    SelectorFailureBehavior.NO_ADVERTISE,
                    SelectedListenerFailureBehavior.ACCEPT, ApplicationProtocolNames.HTTP_2, ApplicationProtocolNames.HTTP_1_1))
                .build();
        } else {
            sslCtx = SslContextBuilder.forClient()
                .sslProvider(SslProvider.JDK)
                .ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .applicationProtocolConfig(new ApplicationProtocolConfig(Protocol.ALPN,
                    SelectorFailureBehavior.NO_ADVERTISE,
                    SelectedListenerFailureBehavior.ACCEPT, ApplicationProtocolNames.HTTP_2))
                .build();
        }
        return sslCtx;

    }
```

在这里，我们使用 JDK SSL provider 为 server 创建了一个 context，添加了两个密码，并为 `HTTP/2` 配置了 Application-Layer 协议协商。

这意味着 server 将仅支持HTTP/2 及其基础[protocol identifier h2](https://httpwg.org/specs/rfc7540.html#versioning)。

&nbsp;

### 3.2.  Http2Server

接下来，需要一个 *ChannelInitializer* 用于我们的 multiplex 子 channel，以便建立一个 Netty pipeline。

我们将在此 channel 中使用较早的 *sslContext* 来启动 pipeline，然后 bootstrap server：

```java
package com.baeldung.netty.http2.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baeldung.netty.http2.Http2Util;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;

public final class Http2Server {

    private static final int PORT = 8443;
    private static final Logger logger = LoggerFactory.getLogger(Http2Server.class);

    public static void main(String[] args) throws Exception {
        SslContext sslCtx = Http2Util.createSSLContext(true);

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.group(group)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        if (sslCtx != null) {
                            ch.pipeline()
                                .addLast(sslCtx.newHandler(ch.alloc()), Http2Util.getServerAPNHandler());
                        }
                    }

                });

            Channel ch = b.bind(PORT)
                .sync()
                .channel();

            logger.info("HTTP/2 Server is listening on https://127.0.0.1:" + PORT + '/');

            ch.closeFuture()
                .sync();
        } finally {
            group.shutdownGracefully();
        }
    }

}

package com.baeldung.netty.http2.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http2.DefaultHttp2DataFrame;
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.handler.codec.http2.DefaultHttp2HeadersFrame;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2HeadersFrame;
import io.netty.util.CharsetUtil;

@Sharable
public class Http2ServerResponseHandler extends ChannelDuplexHandler {

    static final ByteBuf RESPONSE_BYTES = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hello World", CharsetUtil.UTF_8));

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Http2HeadersFrame) {
            Http2HeadersFrame msgHeader = (Http2HeadersFrame) msg;
            if (msgHeader.isEndStream()) {
                ByteBuf content = ctx.alloc()
                    .buffer();
                content.writeBytes(RESPONSE_BYTES.duplicate());

                Http2Headers headers = new DefaultHttp2Headers().status(HttpResponseStatus.OK.codeAsText());
                ctx.write(new DefaultHttp2HeadersFrame(headers).stream(msgHeader.stream()));
                ctx.write(new DefaultHttp2DataFrame(content, true).stream(msgHeader.stream()));
            }

        } else {
            super.channelRead(ctx, msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

}
```

&nbsp;

作为此通道初始化的一部分，我们在 Http2Util 工具类中定义的 *getServerAPNHandler()* 中向 pipeline 添加了 APN handler：

```java
package com.baeldung.netty.http2;

import static io.netty.handler.logging.LogLevel.INFO;

import java.security.cert.CertificateException;

import javax.net.ssl.SSLException;

import com.baeldung.netty.http2.client.Http2ClientResponseHandler;
import com.baeldung.netty.http2.client.Http2SettingsHandler;
import com.baeldung.netty.http2.server.Http2ServerResponseHandler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpScheme;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http2.DefaultHttp2Connection;
import io.netty.handler.codec.http2.DelegatingDecompressorFrameListener;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.Http2FrameCodecBuilder;
import io.netty.handler.codec.http2.Http2FrameLogger;
import io.netty.handler.codec.http2.Http2SecurityUtil;
import io.netty.handler.codec.http2.HttpConversionUtil;
import io.netty.handler.codec.http2.HttpToHttp2ConnectionHandler;
import io.netty.handler.codec.http2.HttpToHttp2ConnectionHandlerBuilder;
import io.netty.handler.codec.http2.InboundHttp2ToHttpAdapterBuilder;
import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.ApplicationProtocolConfig.Protocol;
import io.netty.handler.ssl.ApplicationProtocolConfig.SelectedListenerFailureBehavior;
import io.netty.handler.ssl.ApplicationProtocolConfig.SelectorFailureBehavior;
import io.netty.handler.ssl.ApplicationProtocolNames;
import io.netty.handler.ssl.ApplicationProtocolNegotiationHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.SupportedCipherSuiteFilter;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;

public class Http2Util {
    public static SslContext createSSLContext(boolean isServer) throws SSLException, CertificateException {

        SslContext sslCtx;

        SelfSignedCertificate ssc = new SelfSignedCertificate();

        if (isServer) {
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey())
                .sslProvider(SslProvider.JDK)
                .ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
                .applicationProtocolConfig(new ApplicationProtocolConfig(Protocol.ALPN,
                    SelectorFailureBehavior.NO_ADVERTISE,
                    SelectedListenerFailureBehavior.ACCEPT, ApplicationProtocolNames.HTTP_2, ApplicationProtocolNames.HTTP_1_1))
                .build();
        } else {
            sslCtx = SslContextBuilder.forClient()
                .sslProvider(SslProvider.JDK)
                .ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .applicationProtocolConfig(new ApplicationProtocolConfig(Protocol.ALPN,
                    SelectorFailureBehavior.NO_ADVERTISE,
                    SelectedListenerFailureBehavior.ACCEPT, ApplicationProtocolNames.HTTP_2))
                .build();
        }
        return sslCtx;

    }

    public static ApplicationProtocolNegotiationHandler getServerAPNHandler() {
        ApplicationProtocolNegotiationHandler serverAPNHandler = new ApplicationProtocolNegotiationHandler(ApplicationProtocolNames.HTTP_2) {

            @Override
            protected void configurePipeline(ChannelHandlerContext ctx, String protocol) throws Exception {
                if (ApplicationProtocolNames.HTTP_2.equals(protocol)) {
                    ctx.pipeline()
                        .addLast(Http2FrameCodecBuilder.forServer()
                            .build(), new Http2ServerResponseHandler());
                    return;
                }
                throw new IllegalStateException("Protocol: " + protocol + " not supported");
            }
        };
        return serverAPNHandler;

    }

    public static ApplicationProtocolNegotiationHandler getClientAPNHandler(int maxContentLength, Http2SettingsHandler settingsHandler, Http2ClientResponseHandler responseHandler) {
        final Http2FrameLogger logger = new Http2FrameLogger(INFO, Http2Util.class);
        final Http2Connection connection = new DefaultHttp2Connection(false);

        HttpToHttp2ConnectionHandler connectionHandler = new HttpToHttp2ConnectionHandlerBuilder()
            .frameListener(new DelegatingDecompressorFrameListener(connection, new InboundHttp2ToHttpAdapterBuilder(connection).maxContentLength(maxContentLength)
            .propagateSettings(true)
            .build()))
            .frameLogger(logger)
            .connection(connection)
            .build();

        ApplicationProtocolNegotiationHandler clientAPNHandler = new ApplicationProtocolNegotiationHandler(ApplicationProtocolNames.HTTP_2) {
            @Override
            protected void configurePipeline(ChannelHandlerContext ctx, String protocol) {
                if (ApplicationProtocolNames.HTTP_2.equals(protocol)) {
                    ChannelPipeline p = ctx.pipeline();
                    p.addLast(connectionHandler);
                    p.addLast(settingsHandler, responseHandler);
                    return;
                }
                ctx.close();
                throw new IllegalStateException("Protocol: " + protocol + " not supported");
            }
        };

        return clientAPNHandler;

    }

    public static FullHttpRequest createGetRequest(String host, int port) {
        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.valueOf("HTTP/2.0"), HttpMethod.GET, "/", Unpooled.EMPTY_BUFFER);
        request.headers()
            .add(HttpHeaderNames.HOST, new String(host + ":" + port));
        request.headers()
            .add(HttpConversionUtil.ExtensionHeaderNames.SCHEME.text(), HttpScheme.HTTPS);
        request.headers()
            .add(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
        request.headers()
            .add(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.DEFLATE);
        return request;
    }
}
```

这个 handler 依次添加了 Netty 提供的 *Http2FrameCodec* 和一个 *Http2ServerResponseHandler* 的自定义 handler。

自定义 handler 扩展了 Netty 的 *ChannelDuplexHandler* 并充当 server 的 inbound 和 outbound handler。首先，它准备要发送给 client 的 response。

出于本 tutorial 的目的，我们将在 *io.netty.buffer.ByteBuf* 中定义一个静态 "Hello World"  response-首选的对象，在 Netty 中 read 和 write byte 的首选对象：

```java
static final ByteBuf RESPONSE_BYTES = Unpooled.unreleasableBuffer(
  Unpooled.copiedBuffer("Hello World", CharsetUtil.UTF_8));
```

&nbsp;

该 buffer 将在 handler 的 *channelRead* 方法中设置为 DATA frame，并写入 *ChannelHandlerContext* 中：

```java
@Override
public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    if (msg instanceof Http2HeadersFrame) {
        Http2HeadersFrame msgHeader = (Http2HeadersFrame) msg;
        if (msgHeader.isEndStream()) {
            ByteBuf content = ctx.alloc().buffer();
            content.writeBytes(RESPONSE_BYTES.duplicate());

            Http2Headers headers = new DefaultHttp2Headers().status(HttpResponseStatus.OK.codeAsText());
            ctx.write(new DefaultHttp2HeadersFrame(headers).stream(msgHeader.stream()));
            ctx.write(new DefaultHttp2DataFrame(content, true).stream(msgHeader.stream()));
        }
    } else {
        super.channelRead(ctx, msg);
    }
}
```

&nbsp;

就是这样，server 已准备好发布 *Hello World*

为了进行快速测试，请启动 server 并使用 `--http2`  option 触发 curl 命令：

```bash
curl -k -v --http2 https://127.0.0.1:8443
```

&nbsp;

它将给出类似于以下内容的 response：

```plaintext
> GET / HTTP/2
> Host: 127.0.0.1:8443
> User-Agent: curl/7.64.1
> Accept: */*
> 
* Connection state changed (MAX_CONCURRENT_STREAMS == 4294967295)!
< HTTP/2 200 
< 
* Connection #0 to host 127.0.0.1 left intact
Hello World* Closing connection 0
```

&nbsp;

## 4. The Client

接下来，让我们看一下 client。当然，其目的是发送 request，然后处理从 server 获得的 response。

我们的 client 代码将包含几个 handler，一个 initializer 类（用于在 pipeline 中对其进行设置）以及最后的 JUnit 测试，以引导 client 并将所有内容整合在一起。

&nbsp;

### 4.1. *SslContext*

但首先，让我们再次看看如何设置客户端的 *SslContext* 。我们将在编写 client JUnit 的过程中编写以下代码：

```java
@Before
public void setup() throws Exception {
    SslContext sslCtx = SslContextBuilder.forClient()
      .sslProvider(SslProvider.JDK)
      .ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
      .trustManager(InsecureTrustManagerFactory.INSTANCE)
      .applicationProtocolConfig(
        new ApplicationProtocolConfig(Protocol.ALPN, SelectorFailureBehavior.NO_ADVERTISE,
          SelectedListenerFailureBehavior.ACCEPT, ApplicationProtocolNames.HTTP_2))
      .build();
}
```

如我们所见，它与 server 的 `SslContext` 非常相似，只是我们在这里没有提供任何 *SelfSignedCertificate*。另一个区别是，我们添加了一个 *InsecureTrustManagerFactory* 来信任任何证书而无需任何验证。

重要的是，此信任管理器仅用于演示目的，不应在生产中使用。要改为使用受信任的证书，Netty 的 [SslContextBuilder](https://netty.io/4.1/api/io/netty/handler/ssl/class-use/SslContextBuilder.html) 提供了许多替代方案。

最后，我们将回到此JUnit引导客户端。

&nbsp;

### 4.2. Handlers

现在，让我们看一下 handler。

首先，我们需要一个称为 *Http2SettingsHandler* 的 handler，以处理 `HTTP/2` 的 `SETTINGS` frame 。它扩展了 Netty 的*SimpleChannelInboundHandler* ：

```java
public class Http2SettingsHandler extends SimpleChannelInboundHandler<Http2Settings> {
    private final ChannelPromise promise;

    // constructor

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Http2Settings msg) throws Exception {
        promise.setSuccess();
        ctx.pipeline().remove(this);
    }
}
```

该类只是初始化 *ChannelPromise* 并将其标记为成功。

它还具有一个 utility 方法 *awaitSettings*，我们的 client 将使用该方法来等待初始 handshake 完成：

```java
package com.baeldung.netty.http2.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http2.Http2Settings;

import java.util.concurrent.TimeUnit;

public class Http2SettingsHandler extends SimpleChannelInboundHandler<Http2Settings> {
    private final ChannelPromise promise;

    public Http2SettingsHandler(ChannelPromise promise) {
        this.promise = promise;
    }

    public void awaitSettings(long timeout, TimeUnit unit) throws Exception {
        if (!promise.awaitUninterruptibly(timeout, unit)) {
            throw new IllegalStateException("Timed out waiting for settings");
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Http2Settings msg) throws Exception {
        promise.setSuccess();

        ctx.pipeline()
            .remove(this);
    }
}
```

&nbsp;

如果在规定的超时时间内未发生 channel read，则抛出 *IllegalStateException*。

其次，需要一个 handler 来处理从 server 获得的 response，我们将其命名为 *Http2ClientResponseHandler*：

```java
package com.baeldung.netty.http2.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http2.HttpConversionUtil;
import io.netty.util.CharsetUtil;

public class Http2ClientResponseHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    private final Logger logger = LoggerFactory.getLogger(Http2ClientResponseHandler.class);
    private final Map<Integer, MapValues> streamidMap;

    public Http2ClientResponseHandler() {
        streamidMap = new HashMap<Integer, MapValues>();
    }

    public MapValues put(int streamId, ChannelFuture writeFuture, ChannelPromise promise) {
        return streamidMap.put(streamId, new MapValues(writeFuture, promise));
    }

    public String awaitResponses(long timeout, TimeUnit unit) {

        Iterator<Entry<Integer, MapValues>> itr = streamidMap.entrySet()
            .iterator();
        
        String response = null;

        while (itr.hasNext()) {
            Entry<Integer, MapValues> entry = itr.next();
            ChannelFuture writeFuture = entry.getValue()
                .getWriteFuture();

            if (!writeFuture.awaitUninterruptibly(timeout, unit)) {
                throw new IllegalStateException("Timed out waiting to write for stream id " + entry.getKey());
            }
            if (!writeFuture.isSuccess()) {
                throw new RuntimeException(writeFuture.cause());
            }
            ChannelPromise promise = entry.getValue()
                .getPromise();

            if (!promise.awaitUninterruptibly(timeout, unit)) {
                throw new IllegalStateException("Timed out waiting for response on stream id " + entry.getKey());
            }
            if (!promise.isSuccess()) {
                throw new RuntimeException(promise.cause());
            }
            logger.info("---Stream id: " + entry.getKey() + " received---");
            response = entry.getValue().getResponse();
            
            itr.remove();
        }
        
        return response;

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
        Integer streamId = msg.headers()
            .getInt(HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text());
        if (streamId == null) {
            logger.error("HttpResponseHandler unexpected message received: " + msg);
            return;
        }

        MapValues value = streamidMap.get(streamId);

        if (value == null) {
            logger.error("Message received for unknown stream id " + streamId);
            ctx.close();
        } else {
            ByteBuf content = msg.content();
            if (content.isReadable()) {
                int contentLength = content.readableBytes();
                byte[] arr = new byte[contentLength];
                content.readBytes(arr);
                String response = new String(arr, 0, contentLength, CharsetUtil.UTF_8);
                logger.info("Response from Server: "+ (response));
                value.setResponse(response);
            }
            
            value.getPromise()
                .setSuccess();
        }
    }

    public static class MapValues {
        ChannelFuture writeFuture;
        ChannelPromise promise;
        String response;

        public String getResponse() {
            return response;
        }

        public void setResponse(String response) {
            this.response = response;
        }

        public MapValues(ChannelFuture writeFuture2, ChannelPromise promise2) {
            this.writeFuture = writeFuture2;
            this.promise = promise2;
        }

        public ChannelFuture getWriteFuture() {
            return writeFuture;
        }

        public ChannelPromise getPromise() {
            return promise;
        }

    }
}
```

此类还继承了 *SimpleChannelInboundHandler* 类，并声明了 *MapValues* 的 *streamidMap*，它是我们 *Http2ClientResponseHandler* 的内部类：

```java
public static class MapValues {
    ChannelFuture writeFuture;
    ChannelPromise promise;

    // constructor and getters
}
```

&nbsp;

我们添加了此类，以便能够为给定的 *Integer* 键存储两个值。

handler 还具有一个实用方法 *put*，当然可以将值放入 *streamidMap* 中：

```java
public MapValues put(int streamId, ChannelFuture writeFuture, ChannelPromise promise) {
    return streamidMap.put(streamId, new MapValues(writeFuture, promise));
}
```

&nbsp;

接下来，让我们看看在 pipeline 中 read channel 时此 handler 的作用。

基本上，这是我们从 server 得到的 DATA 帧 或 *ByteBuf* 内容的地方，作为一个 *FullHttpResponse*，并可以按照我们想要的方式操作它。

在这个例子中，我们只记录它:

```java
@Override
protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
    Integer streamId = msg.headers().getInt(HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text());
    if (streamId == null) {
        logger.error("HttpResponseHandler unexpected message received: " + msg);
        return;
    }

    MapValues value = streamidMap.get(streamId);

    if (value == null) {
        logger.error("Message received for unknown stream id " + streamId);
    } else {
        ByteBuf content = msg.content();
        if (content.isReadable()) {
            int contentLength = content.readableBytes();
            byte[] arr = new byte[contentLength];
            content.readBytes(arr);
            logger.info(new String(arr, 0, contentLength, CharsetUtil.UTF_8));
        }

        value.getPromise().setSuccess();
    }
}
```

&nbsp;

在方法结束时，我们将 *ChannelPromise* 标记为成功以指示正确完成。

作为我们描述的第一个 handler，此类还包含一个供 client 使用的 utility 方法。该方法使 event 循环等待，直到 *ChannelPromise* 成功为止。或者，换句话说，它等待 response 处理完成：

```java
public String awaitResponses(long timeout, TimeUnit unit) {
    Iterator<Entry<Integer, MapValues>> itr = streamidMap.entrySet().iterator();        
    String response = null;

    while (itr.hasNext()) {
        Entry<Integer, MapValues> entry = itr.next();
        ChannelFuture writeFuture = entry.getValue().getWriteFuture();

        if (!writeFuture.awaitUninterruptibly(timeout, unit)) {
            throw new IllegalStateException("Timed out waiting to write for stream id " + entry.getKey());
        }
        if (!writeFuture.isSuccess()) {
            throw new RuntimeException(writeFuture.cause());
        }
        ChannelPromise promise = entry.getValue().getPromise();

        if (!promise.awaitUninterruptibly(timeout, unit)) {
            throw new IllegalStateException("Timed out waiting for response on stream id "
              + entry.getKey());
        }
        if (!promise.isSuccess()) {
            throw new RuntimeException(promise.cause());
        }
        logger.info("---Stream id: " + entry.getKey() + " received---");
        response = entry.getValue().getResponse();
            
        itr.remove();
    }        
    return response;
}
```

&nbsp;

### 4.3. *Http2ClientInitializer*

正如我们在 server 中看到的那样，`*ChannelInitializer*` 的目的是建立 `pipeline`：

```java
package com.baeldung.netty.http2.client;

import com.baeldung.netty.http2.Http2Util;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;

public class Http2ClientInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;
    private final int maxContentLength;
    private Http2SettingsHandler settingsHandler;
    private Http2ClientResponseHandler responseHandler;
    private String host;
    private int port;

    public Http2ClientInitializer(SslContext sslCtx, int maxContentLength, String host, int port) {
        this.sslCtx = sslCtx;
        this.maxContentLength = maxContentLength;
        this.host = host;
        this.port = port;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {

        settingsHandler = new Http2SettingsHandler(ch.newPromise());
        responseHandler = new Http2ClientResponseHandler();
        
        if (sslCtx != null) {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(sslCtx.newHandler(ch.alloc(), host, port));
            pipeline.addLast(Http2Util.getClientAPNHandler(maxContentLength, settingsHandler, responseHandler));
        }
    }

    public Http2SettingsHandler getSettingsHandler() {
        return settingsHandler;
    }
    
    public Http2ClientResponseHandler getResponseHandler() {
        return responseHandler;
    }
}
```

&nbsp;

在这种场景下，我们将使用新的 *SslHandler* 启动管道，以在握手过程开始时添加 [TLS SNI扩展](https://en.wikipedia.org/wiki/Server_Name_Indication) 。

然后，由 *ApplicationProtocolNegotiationHandler* 负责在 pipeline 排列连接 handler 和自定义 handler：

```java
public static ApplicationProtocolNegotiationHandler getClientAPNHandler(
  int maxContentLength, Http2SettingsHandler settingsHandler, Http2ClientResponseHandler responseHandler) {
    final Http2FrameLogger logger = new Http2FrameLogger(INFO, Http2ClientInitializer.class);
    final Http2Connection connection = new DefaultHttp2Connection(false);

    HttpToHttp2ConnectionHandler connectionHandler = 
      new HttpToHttp2ConnectionHandlerBuilder().frameListener(
        new DelegatingDecompressorFrameListener(connection, 
          new InboundHttp2ToHttpAdapterBuilder(connection)
            .maxContentLength(maxContentLength)
            .propagateSettings(true)
            .build()))
          .frameLogger(logger)
          .connection(connection)
          .build();

    ApplicationProtocolNegotiationHandler clientAPNHandler = 
      new ApplicationProtocolNegotiationHandler(ApplicationProtocolNames.HTTP_2) {
        @Override
        protected void configurePipeline(ChannelHandlerContext ctx, String protocol) {
            if (ApplicationProtocolNames.HTTP_2.equals(protocol)) {
                ChannelPipeline p = ctx.pipeline();
                p.addLast(connectionHandler);
                p.addLast(settingsHandler, responseHandler);
                return;
            }
            ctx.close();
            throw new IllegalStateException("Protocol: " + protocol + " not supported");
        }
    };
    return clientAPNHandler;
}
```

现在剩下要做的就是 bootstrap client ， 并通过请求发送。

&nbsp;

### 4.4. Client Bootstrap

在某种程度上，client bootstrap 与 server bootstrap 类似。之后，我们需要添加更多功能来处理发送 request 和介绍 response。

如前所述，我们将其编写为 JUnit 测试：

```java
@Test
public void whenRequestSent_thenHelloWorldReceived() throws Exception {

    EventLoopGroup workerGroup = new NioEventLoopGroup();
    Http2ClientInitializer initializer = new Http2ClientInitializer(sslCtx, Integer.MAX_VALUE, HOST, PORT);

    try {
        Bootstrap b = new Bootstrap();
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.remoteAddress(HOST, PORT);
        b.handler(initializer);

        channel = b.connect().syncUninterruptibly().channel();

        logger.info("Connected to [" + HOST + ':' + PORT + ']');

        Http2SettingsHandler http2SettingsHandler = initializer.getSettingsHandler();
        http2SettingsHandler.awaitSettings(60, TimeUnit.SECONDS);
  
        logger.info("Sending request(s)...");

        FullHttpRequest request = Http2Util.createGetRequest(HOST, PORT);

        Http2ClientResponseHandler responseHandler = initializer.getResponseHandler();
        int streamId = 3;

        responseHandler.put(streamId, channel.write(request), channel.newPromise());
        channel.flush();
 
        String response = responseHandler.awaitResponses(60, TimeUnit.SECONDS);

        assertEquals("Hello World", response);

        logger.info("Finished HTTP/2 request(s)");
    } finally {
        workerGroup.shutdownGracefully();
    }
}
```

&nbsp;

值得注意的是，这些是我们对 server bootstrap 程序采取的额外步骤：

- 首先，我们使用 *Http2SettingsHandler* 的 *awaitSettings* 方法等待初始 handshake。
- 其次，我们将请求创建为 *FullHttpRequest*
- 第三，将 *streamId* 放入 *Http2ClientResponseHandler* 的 *streamIdMap* 中，并调用其 *awaitResponses* 方法
- 最后，我们验证了在 response 中确实获得了 *Hello World*

简而言之，这就是发生的情况– client 发送了HEADER 帧，初始 SSL 握手发生，server 发送了 response 报文（HEADERS）和 DATA 桢

&nbsp;

## 5. 总结

在本教程中，我们看到了如何使用代码示例在 Netty 中实现 `HTTP/2` server 和 client，以使用 `HTTP/2` 框架获得 *Hello World*  response。

我们希望 Netty API 能够在将来处理 `HTTP/2` 框架方面有更多改进，因为它仍在开发中。