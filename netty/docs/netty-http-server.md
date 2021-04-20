# HTTP Server with Netty

&nbsp;

## 1. Overview

In this tutorial, we're going to **implement a simple upper-casing server over [HTTP](http://) with Netty**, an asynchronous framework that gives us the flexibility to develop network applications in Java.

&nbsp;

## 2. Server Bootstrapping

Before we start, we should be aware of the [basics concepts of Netty](netty-introduction.md) , such as channel, handler, encoder, and decoder.

Here we'll jump straight into bootstrapping the server, which is mostly the same as a [simple protocol server](netty-introduction.md) :

```java
public class HttpServer {

    private int port;
    private static Logger logger = LoggerFactory.getLogger(HttpServer.class);

    // constructor

    // main method, same as simple protocol server

    public void run() throws Exception {
        ...
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .handler(new LoggingHandler(LogLevel.INFO))
          .childHandler(new ChannelInitializer() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();
                p.addLast(new HttpRequestDecoder());
                p.addLast(new HttpResponseEncoder());
                p.addLast(new CustomHttpServerHandler());
            }
          });
        ...
    }
}
```

So, here **only the \*childHandler\* differs as per the protocol we want to implement**, which is HTTP for us.

&nbsp;

We're adding three handlers to the server's pipeline:

1. Netty's [*HttpResponseEncoder*](https://netty.io/4.0/api/io/netty/handler/codec/http/HttpResponseEncoder.html) – for serialization
2. Netty's [*HttpRequestDecoder*](https://netty.io/4.0/api/io/netty/handler/codec/http/HttpRequestDecoder.html) – for deserialization
3. Our own *CustomHttpServerHandler* – for defining our server's behavior

Let's look at the last handler in detail next.

&nbsp;

## 3. *CustomHttpServerHandler*

Our custom handler's job is to process inbound data and send a response.

Let's break it down to understand its working.

&nbsp;

### 3.1. Structure of the Handler

*CustomHttpServerHandler* extends Netty's abstract *SimpleChannelInboundHandler* and implements its lifecycle methods:

```java
public class CustomHttpServerHandler extends SimpleChannelInboundHandler {
    private HttpRequest request;
    StringBuilder responseData = new StringBuilder();

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
       // implementation to follow
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
```

As the method name suggests, *channelReadComplete* flushes the handler context after the last message in the channel has been consumed so that it's available for the next incoming message. The method *exceptionCaught* is for handling exceptions if any.

So far, all we've seen is the boilerplate code.

Now let's get on with the interesting stuff, the implementation of *channelRead0*.

&nbsp;

### 3.2. Reading the Channel

Our use case is simple, the server will simply transform the request body and query parameters, if any, to uppercase. A word of caution here on reflecting request data in the response – we are doing this only for demonstration purposes, to understand how we can use Netty to implement an HTTP server.

Here, **we'll consume the message or request, and set up its response as [recommended by the protocol](https://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Response)** (note that *RequestUtils* is something we'll write in just a moment)：

```java
if (msg instanceof HttpRequest) {
    HttpRequest request = this.request = (HttpRequest) msg;

    if (HttpUtil.is100ContinueExpected(request)) {
        writeResponse(ctx);
    }
    responseData.setLength(0);            
    responseData.append(RequestUtils.formatParams(request));
}
responseData.append(RequestUtils.evaluateDecoderResult(request));

if (msg instanceof HttpContent) {
    HttpContent httpContent = (HttpContent) msg;
    responseData.append(RequestUtils.formatBody(httpContent));
    responseData.append(RequestUtils.evaluateDecoderResult(request));

    if (msg instanceof LastHttpContent) {
        LastHttpContent trailer = (LastHttpContent) msg;
        responseData.append(RequestUtils.prepareLastResponse(request, trailer));
        writeResponse(ctx, trailer, responseData);
    }
}
```

&nbsp;

As we can see, when our channel receives an *HttpRequest*, it first checks if the request expects a [100 Continue](https://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Status-Codes) status. In that case, we immediately write back with an empty response with a status of *CONTINUE*:

```java
private void writeResponse(ChannelHandlerContext ctx) {
    FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE, 
      Unpooled.EMPTY_BUFFER);
    ctx.write(response);
}
```

After that, the handler initializes a string to be sent as a response and adds the request's query parameters to it to be sent back as-is.

&nbsp;

Let's now define the method *formatParams* and place it in a *RequestUtils* helper class to do that:

```java
StringBuilder formatParams(HttpRequest request) {
    StringBuilder responseData = new StringBuilder();
    QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
    Map<String, List<String>> params = queryStringDecoder.parameters();
    if (!params.isEmpty()) {
        for (Entry<String, List<String>> p : params.entrySet()) {
            String key = p.getKey();
            List<String> vals = p.getValue();
            for (String val : vals) {
                responseData.append("Parameter: ").append(key.toUpperCase()).append(" = ")
                  .append(val.toUpperCase()).append("\r\n");
            }
        }
        responseData.append("\r\n");
    }
    return responseData;
}
```

&nbsp;

Next, on receiving an *HttpContent*, **we take the request body and convert it to upper case**:

```java
StringBuilder formatBody(HttpContent httpContent) {
    StringBuilder responseData = new StringBuilder();
    ByteBuf content = httpContent.content();
    if (content.isReadable()) {
        responseData.append(content.toString(CharsetUtil.UTF_8).toUpperCase())
          .append("\r\n");
    }
    return responseData;
}
```

Also, if the received *HttpContent* is a *LastHttpContent*, we add a goodbye message and trailing headers, if any:

```java
StringBuilder prepareLastResponse(HttpRequest request, LastHttpContent trailer) {
    StringBuilder responseData = new StringBuilder();
    responseData.append("Good Bye!\r\n");

    if (!trailer.trailingHeaders().isEmpty()) {
        responseData.append("\r\n");
        for (CharSequence name : trailer.trailingHeaders().names()) {
            for (CharSequence value : trailer.trailingHeaders().getAll(name)) {
                responseData.append("P.S. Trailing Header: ");
                responseData.append(name).append(" = ").append(value).append("\r\n");
            }
        }
        responseData.append("\r\n");
    }
    return responseData;
}
```

&nbsp;

### 3.3. Writing the Response

Now that our data to be sent is ready, we can write the response to the *ChannelHandlerContext*:

```java
private void writeResponse(ChannelHandlerContext ctx, LastHttpContent trailer,
  StringBuilder responseData) {
    boolean keepAlive = HttpUtil.isKeepAlive(request);
    FullHttpResponse httpResponse = new DefaultFullHttpResponse(HTTP_1_1, 
      ((HttpObject) trailer).decoderResult().isSuccess() ? OK : BAD_REQUEST,
      Unpooled.copiedBuffer(responseData.toString(), CharsetUtil.UTF_8));
    
    httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

    if (keepAlive) {
        httpResponse.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, 
          httpResponse.content().readableBytes());
        httpResponse.headers().set(HttpHeaderNames.CONNECTION, 
          HttpHeaderValues.KEEP_ALIVE);
    }
    ctx.write(httpResponse);

    if (!keepAlive) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
}
```

In this method, we created a *FullHttpResponse* with HTTP/1.1 version, adding the data we'd prepared earlier.

If a request is to be kept-alive, or in other words, if the connection is not to be closed, we set the response's *connection* header as *keep-alive*. Otherwise, we close the connection.

&nbsp;

## 4. Testing the Server

To test our server, let's send some CURL commands and look at the responses.

Of course, **we need to start the server by running the class \*HttpServer\* before this**.

&nbsp;

### 4.1. GET Request

Let's first invoke the server, providing a cookie with the request:

```bash
curl http://127.0.0.1:8080?param1=one
```

As a response, we get:

```bash
Parameter: PARAM1 = ONE

Good Bye!
```

We can also hit *[http://127.0.0.1:8080?param1=one](http://127.0.0.1:8080/?param1=one)* from any browser to see the same result.

&nbsp;

### 4.2. POST Request

As our second test, let's send a POST with body *sample content*：

```bash
curl -d "sample content" -X POST http://127.0.0.1:8080
```

Here's the response:

```bash
SAMPLE CONTENT
Good Bye!
```

This time, since our request contained a body, **the server sent it back in uppercase**

&nbsp;

## 5. Conclusion

In this tutorial, we saw how to implement the HTTP protocol, particularly an HTTP server using Netty.

[HTTP/2 in Netty](netty-http2.md) demonstrates a client-server implementation of the HTTP/2 protocol.