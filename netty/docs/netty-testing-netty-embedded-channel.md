# Testing Netty with EmbeddedChannel

&nbsp;

## 1. Introduction

In this article, we'll see how to use *EmbeddedChannel* to test the functionality of our inbound and outbound channel handlers.

[Netty](netty-introduction.md) is a very versatile framework for writing high-performance asynchronous applications. Unit testing such applications can be tricky without the right tools.

Thankfully the framework provides us with the ***EmbeddedChannel\* class – which facilitates the testing of \*ChannelHandlers\***.

&nbsp;

## 2. Setup

The *EmbeddedChannel* is part of the Netty framework, so the only dependency needed is the one for Netty itself.

The dependency can be found over on [Maven Central](https://search.maven.org/classic/#search|ga|1|g%3A"io.netty" AND a%3A"netty-all"):

```xml
<dependency>
    <groupId>io.netty</groupId>
    <artifactId>netty-all</artifactId>
    <version>4.1.24.Final</version>
</dependency>
```

&nbsp;

## 3. EmbeddedChannel Overview

The **EmbeddedChannel** class is just another implementation of **AbstractChannel** – which transports data without the need for a real network connection.

This is useful because we can simulate incoming messages by writing data on the inbound channels and also check the generated response on the outbound channels. This way we can individually test each *ChannelHandler* or in the whole channel pipeline.

To test one or more *ChannelHandlers*, we first have to create an *EmbeddedChannel* instance using one of its constructors.

&nbsp;

The most common way to initialize an **EmbeddedChannel** is by passing the list of  *ChannelHandlers* to its constructor:

```java
EmbeddedChannel channel = new EmbeddedChannel(
  new HttpMessageHandler(), new CalculatorOperationHandler());
```

&nbsp;

If we want to have more control on the order the handlers are inserted into the pipeline we can create an *EmbeddedChannel* with the default constructor and directly add the handlers:

```java
channel.pipeline()
  .addFirst(new HttpMessageHandler())
  .addLast(new CalculatorOperationHandler());
```

&nbsp;

Also, when we create an **EmbeddedChannel**,  it'll have a default configuration given by the *DefaultChannelConfig* class.

When we want to use a custom configuration, like lowering the connect timeout value from the default one, we can access the *ChannelConfig* object by using the *config()* method:

```java
DefaultChannelConfig channelConfig = (DefaultChannelConfig) channel
  .config();
channelConfig.setConnectTimeoutMillis(500);
```

&nbsp;

The *EmbeddedChannel* includes methods that we can use to read and write data to our *ChannelPipeline*. The most commonly used methods are:

- *readInbound()*
- *readOutbound()*
- *writeInbound(Object… msgs)*
- *writeOutbound(Object… msgs)*

&nbsp;

**The read methods retrieve and remove the first element in the inbound/outbound queue.** When we need access to the whole queue of messages without removing any element, we can use the *outboundMessages()* method:

```java
Object lastOutboundMessage = channel.readOutbound();
Queue<Object> allOutboundMessages = channel.outboundMessages();
```

&nbsp;

**The write methods return \*true\* when the message was successfully added to the inbound/outbound pipeline of the \*Channel:\***

```java
channel.writeInbound(httpRequest)
```

The idea is that we write messages on the inbound pipeline so that the out *ChannelHandlers* will process them and we expect the result to be readable from the outbound pipeline.

&nbsp;

## 4. Testing *ChannelHandlers*

Let's look at a simple example in which we want to test a pipeline composed of two *ChannelHandlers* that receive an HTTP request and expect an HTTP response that contains the result of a calculation:

```java
EmbeddedChannel channel = new EmbeddedChannel(
  new HttpMessageHandler(), new CalculatorOperationHandler());
```

The first one, *HttpMessageHandler* will extract the data from the HTTP request and pass it to the seconds *ChannelHandler* in the pipeline, *CalculatorOperationHandler*, to do processing with the data.

&nbsp;

Now, let's write the HTTP request and see if the inbound pipeline processes it:

```java
FullHttpRequest httpRequest = new DefaultFullHttpRequest(
  HttpVersion.HTTP_1_1, HttpMethod.GET, "/calculate?a=10&b=5");
httpRequest.headers().add("Operator", "Add");

assertThat(channel.writeInbound(httpRequest)).isTrue();
long inboundChannelResponse = channel.readInbound();
assertThat(inboundChannelResponse).isEqualTo(15);
```

We can see that we've sent the HTTP request on the inbound pipeline using the *writeInbound()* method and read the result with *readInbound()*; *inboundChannelResponse* is the message that resulted from the data we've sent after it was processed by all the *ChannelHandlers* in the inbound pipeline.

Now, let's check if our Netty server responds with the correct HTTP response message. To do this, we'll check if a message exists on the outbound pipeline:

```java
assertThat(channel.outboundMessages().size()).isEqualTo(1);
```

The outbound message, in this case, is an HTTP response, so let's check if the content is correct. We do this, by reading the last message in the outbound pipeline:

```java
FullHttpResponse httpResponse = channel.readOutbound();
String httpResponseContent = httpResponse.content()
  .toString(Charset.defaultCharset());
assertThat(httpResponseContent).isEqualTo("15");
```

&nbsp;

## 4. Testing Exception Handling

Another common testing scenario is exception handling.

We can handle exceptions in our *ChannelInboundHandlers* by implementing the *exceptionCaught()* method, but there're some cases when we don't want to handle an exception and instead, we pass it to the next *ChannelHandler*in the pipeline.

We can use the *checkException()* method from the *EmbeddedChannel*class to check if any *Throwable* object was received on the pipeline and rethrows it.

This way we can catch the *Exception* and check whether the *ChannelHandler*should or shouldn't have thrown it:

```java
assertThatThrownBy(() -> {
    channel.pipeline().fireChannelRead(wrongHttpRequest);
    channel.checkException();
}).isInstanceOf(UnsupportedOperationException.class)
  .hasMessage("HTTP method not supported");
```

We can see in the example above, that we've sent an HTTP request that we expect to trigger an *Exception*. By using the *checkException()* method we can rethrow the last exception that exists in the pipeline, so we can assert what is needed from it.

&nbsp;

## 5. Conclusion

The *EmbeddedChannel* is a great feature provided by the Netty framework to help us test the correctness of out *ChannelHandler* pipeline. It can be used to test each *ChannelHandler* individually and more importantly the whole pipeline.