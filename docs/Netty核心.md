# Pipeline 设计原理



## Channel 和 ChannelPipeline



```java
protected AbstractChannel(Channel parent) {
  
  this.parent = parent;
  id = newId();
  unsafe = newUnsafe();
  pipeline = new ChannelPipeline();
  
	
}
```





* 特别注意的是 我们在开始的示意图中 head 和 tail 并没有包含 ChannelHandler， 这是因为 HeadContext 和 TailContext 继承自 AbstractChannelHandlerContext 的同时， 并实现了 ChannelHandler 接口， 因为他们有 Context 和 Handler 双重属性。



* 发起请求事件
* 响应请求的事件

```java
public class MyInboundHandler extends ChannelInBoundHandlerAdapter {
  
  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
	    System.out.println("连接成功");
      ctx.fireChannelActive();
   }
 
}

public class MyOutBoundHandler extends ChannelOutboundHandlerAdapter {
  @Override
  public void close(ChannelHandlerContext ctx, ChannelPromise promise) {
    
    System.out.println("客户端关闭");
    ctx.close();
  }
  
  public final ChannelFutre connect(SocketAddress remoteAddress, ChannelPormise promise) {
    
    final AbstractChannelHandlerContext next = findContextOutbound();
    EventExecutor executor = next.executor();
    
    next.invokeConnect(remoteAddress, localAddress, promise);
    
    return promise;
  }
  
}
```

