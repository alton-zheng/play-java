# Exceptions in Netty

&nbsp;

## **1. Overview**

**In this quick article, we'll be looking at exception handling in Netty.**

Simply put, Netty is a framework for building high-performance asynchronous and event-driven network applications. I/O operations are handled inside its life-cycle using callback methods.

More details about the framework and how to get started with it can be found in our previous [article here](netty-introduction.md).

&nbsp;

## **2. Handling Exceptions in Netty**

As mentioned earlier, Netty is an event-driven system and has callback methods for specific events. Exceptions are such events too.

Exceptions can occur while processing data received from the client or during I/O operations. When this happens,  a dedicated exception-caught event is fired.

&nbsp;

### **2.1. Handling Exceptions in the Channel of Origin**

The exception-caught event, when fired, is handled by the *exceptionsCaught()* method of the *ChannelInboundHandler* or its adapters and subclasses.

Note that the callback has been deprecated in the *ChannelHandler* interface. It's now limited to the *ChannelInboudHandler* interface.

The method accepts a *Throwable* object and a *ChannelHandlerContext* object as parameters. The *Throwable* object could be used to print the stack trace or get the localized error message.

So let's create a channel handler, *ChannelHandlerA* and override its *exceptionCaught()* with our implementation:

```java
public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) 
  throws Exception {
 
    logger.info(cause.getLocalizedMessage());
    //do more exception handling
    ctx.close();
}
```

In the code snippet above, we logged the exception message and also call the *close()* of the *ChannelHandlerContext*.

This will close the channel between the server and the client. Essentially causing the client to disconnect and terminate.

&nbsp;

### **2.2. Propagating Exceptions**

In the previous section, we handled the exception in its channel of origin. However, we can actually propagate the exception on to another channel handler in the pipeline.

Instead of logging the error message and calling *ctx.close()*, we'll use the *ChannelHandlerContext* object to fire another exception-caught event manually.

This will cause the *exceptionCaught()* of the next channel handler in the pipeline to be invoked.

Let's modify the code snippet in *ChannelHandlerA* to propagate the event by calling the *ctx.fireExceptionCaught()*:

```java
public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) 
  throws Exception {
 
    logger.info("Exception Occurred in ChannelHandler A");
    ctx.fireExceptionCaught(cause);
}
```

&nbsp;

Furthermore, let's create another channel handler, *ChannelHandlerB* and override its *exceptionCaught()* with this implementation:

```java
@Override
public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) 
  throws Exception {
 
    logger.info("Exception Handled in ChannelHandler B");
    logger.info(cause.getLocalizedMessage());
    //do more exception handling
    ctx.close();
}
```

&nbsp;

In the *Server* class, the channels are added to the pipeline in the following order:

```java
ch.pipeline().addLast(new ChannelHandlerA(), new ChannelHandlerB());
```

Propagating exception-caught events manually is useful in cases where all exceptions are being handled by one designated channel handler.

&nbsp;

## **3. Conclusion**

In this tutorial, we've looked at how to handle exceptions in Netty using the callback method and how to propagate the exceptions if needed.