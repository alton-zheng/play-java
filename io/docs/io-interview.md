# 网络 和 IO 面试题

```
常见问题
1，【网络到分布式lvs+redis+zk】http://mashibing.com/pc.html
2，网络的小课
3，内存与IO，磁盘IO，网络IO

从程序员的角度理解网络通信

面试突击班怎么学，自己时间衡量，一点带面，能够应付变种问题
你有点体系的基础
查漏补缺，建立体系的过程

做规划，面试辅导，又几个进了腾讯：redis+网络+IO
```

![面试突击班-网络和IO面试题](/Users/alton/Documents/profile/notebook/Java/play-java/io/docs/images/网络和IO模型.png)

&nbsp;

## HTTP 和 HTTPS

>  http 应用层协议
>
> https : http + ssl/tls



&nbsp;

## TCP三次握手

> 内核态完成



1. ## TCP四次分手



&nbsp;

## TCP连接状态



&nbsp;

## Listen 状态的服务器， listen socket 的接受， 发送队列放什么？

> accept 队列  
>
> - 等待程序接受的连接
>
> - 有大小限制 （backlog）不被程序取走
>
>   - backlog
>
>     - 内核
>
>     - `cat /proc/sys/net/core/somaxconn` 
>
>       -  默认 128
>       -  ACCEPT队列  你可以给出backlog 取 min
>
>       - 每个人顾好自己，每个人处理好对别人的打扰
>
>     - `cat /proc/sys/net/ipv4/tcp_max_syn_backlog  DDOS`
>       - 默认 `2048`
>       - `backlog` 满了 `accept` 队列满了，新客户端直接 `Connection refused`

&nbsp;

```bash
$ nc -l localhost 9999

$ netstat -natp

$ ss -lna
```

&nbsp;

## Connection refused？

> 服务器端口没开
>
> 网络不通
>
> socket 队列满了
>
> - 内核态和用户态交互出现问题

&nbsp;

## OSI七层参考模型

> 用户态
>
> - 应用层
> - 表示层
> - 会话层
>
> 内核态
>
> - 传输控制层
> - 网络层
> - 链路层
> - 物理层



## 什么是长连接和短连接？有状态，无状态？

> TCP是长连接吗？
>
> tcp只是连接，受应用层协议
>
> 连接是不是一个“复用”载体
>
> 举一个例子：http 1.0 ，1.1 没有开启keepalive保持，连接只负责一次同步阻塞的请求+响应，短连接！
>
> 举一个例子：http 1.0,1.1 开启了keepalive保持，同步复用连接：多次(请求+响应)，以上是无状态通信，长连接！
>
> 举一个列子：dubbo协议(rpc)，打开连接，同步/异步复用连接：多次（请求+响应）（请求请求）（响应响应），当复用连接的时候，需要消息的ID，而且客户端和服务端同时完成这个约束  有状态通信，长连接！

&nbsp;

## IO模型

>0：IO是程序对着内核的socket-queue的包装
>
>BIO：读取，一直等queue里有才返回，阻塞模型，每连接对应一个线程
>
>NIO：读取，立刻返回：两种结果，读到，没读到，程序逻辑要自己维护，nio noblock
>
>多路复用器：内核增加select，poll，epoll新增的和数据接收，连接接受实质无关的调用，得到是对应socket的事件(listen socket ，socket)，可以有效地去再次accept，R/W
>
>AIO： 异步 IO

&nbsp;

## 同步阻塞，同步非阻塞

>  BIO,NIO,多路复用器，在IO模型上都是同步的，都是程序自己accpet，R/W

&nbsp;

## 粘包，粘包，拆包

> 有程序，有内核，程序和内核协调工作
>
> 有一些是内核做的事情，三次握手，数据发送出去，接受进来，内核，TCP，分包
>
> 到我们自己的程序，即便在一个socket里，也可能收到多个消息在一个字节数组中，我们要自己拆解



