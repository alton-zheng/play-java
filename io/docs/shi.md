### 



- 单线程

```java
package com.bjmashibing.system.io;

import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

public class SocketNIO {

    //  what   why  how
    public static void main(String[] args) throws Exception {

        LinkedList<SocketChannel> clients = new LinkedList<>();

        ServerSocketChannel ss = ServerSocketChannel.open();  //服务端开启监听：接受客户端
        ss.bind(new InetSocketAddress(9090));
        ss.configureBlocking(false); //重点  OS  NONBLOCKING!!!  监听 Socket 设置是否 Blocking

//        ss.setOption(StandardSocketOptions.TCP_NODELAY, false);
//        StandardSocketOptions.TCP_NODELAY
//        StandardSocketOptions.SO_KEEPALIVE
//        StandardSocketOptions.SO_LINGER
//        StandardSocketOptions.SO_RCVBUF
//        StandardSocketOptions.SO_SNDBUF
//        StandardSocketOptions.SO_REUSEADDR




        while (true) {
            //接受客户端的连接
            Thread.sleep(1000);
            SocketChannel client = ss.accept(); //不会阻塞？  -1(OS)  NULL(Java)
            /*accept  调用内核了：1，没有客户端连接进来，返回值？在BIO 的时候一直卡着，但是在NIO ，不卡着，返回-1，NULL
            如果来 client 的 connect，accept 返回的是这个 client 的 fd  5，client  object
            NONBLOCKING 意味着代码能往下执行*/

            if (client == null) {
             //   System.out.println("null.....");
            } else {
                client.configureBlocking(false); //重点  socket（服务端的listen socket<连接请求三次握手后，往我这里扔，我去通过accept 得到  连接的socket>，连接socket<连接后的数据读写使用的> ）
                int port = client.socket().getPort();
                System.out.println("client..port: " + port);
                clients.add(client);
            }

            ByteBuffer buffer = ByteBuffer.allocateDirect(4096);  //可以在堆里   堆外

            //遍历已经 connect 进来的 client 能不能 read-write 数据
            for (SocketChannel c : clients) {   //串行化！！！！  多线程！！
                int num = c.read(buffer);  // >0  -1  0   // 不会阻塞
                if (num > 0) {
                    buffer.flip();
                    byte[] aaa = new byte[buffer.limit()];
                    buffer.get(aaa);

                    String b = new String(aaa);
                    System.out.println(c.socket().getPort() + " : " + b);
                    buffer.clear();
                }


            }
        }
    }

}

```

&nbsp;

- 调用上面代码并对程序的所有线程进行监控

```java
javac SocketNIO.java && strace -ff -o out java SocketNIO
```

&nbsp;

- linux 和 Mac 模拟单连接

```bash
$ nc localhost 9090
```

&nbsp;

## Server 单线程， client 连接压服务

> 性能比 BIO 高不少
>
> www.kegal.com/c10k.html

```java
package com.bjmashibing.system.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

public class C10Kclient {

    public static void main(String[] args) {
        LinkedList<SocketChannel> clients = new LinkedList<>();
        InetSocketAddress serverAddr = new InetSocketAddress("localhost", 9090);

        //端口号的问题：65535
        //  windows
        for (int i = 10000; i < 65000; i++) {
            try {
                SocketChannel client1 = SocketChannel.open();

                // SocketChannel client2 = SocketChannel.open();

                /*
                linux中你看到的连接就是：
                client...port: 10508
                client...port: 10508
                 */

                client1.bind(new InetSocketAddress("localhost", i));
                client1.connect(serverAddr);
                clients.add(client1);

/*                client2.bind(new InetSocketAddress("192.168.110.100", i));
                client2.connect(serverAddr);
                clients.add(client2);*/

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        System.out.println("clients "+ clients.size());

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

&nbsp;

## 解决 `Too many open files ` 问题： 

```bash
java.net.SocketException: Too many open files
	at sun.nio.ch.Net.socket0(Native Method)
	at sun.nio.ch.Net.socket(Net.java:411)
	at sun.nio.ch.Net.socket(Net.java:404)
	at sun.nio.ch.SocketChannelImpl.<init>(SocketChannelImpl.java:105)
	at sun.nio.ch.SelectorProviderImpl.openSocketChannel(SelectorProviderImpl.java:60)
	at java.nio.channels.SocketChannel.open(SocketChannel.java:145)
	at com.bjmashibing.system.io.C10Kclient.main(C10Kclient.java:22)
java.net.SocketException: Too many open files
	at sun.nio.ch.Net.socket0(Native Method)
	at sun.nio.ch.Net.socket(Net.java:411)
	at sun.nio.ch.Net.socket(Net.java:404)
	at sun.nio.ch.SocketChannelImpl.<init>(SocketChannelImpl.java:105)
	at sun.nio.ch.SelectorProviderImpl.openSocketChannel(SelectorProviderImpl.java:60)
	at java.nio.channels.SocketChannel.open(SocketChannel.java:145)
	at com.bjmashibing.system.io.C10Kclient.main(C10Kclient.java:22)
```

&nbsp;

场景

- idea（一般不会用 idea 压测）

在/Applications/IntelliJ IDEA.app/Contents/Info.plist中vmoptions项 -XX:-MaxFDLimit=10240


全局限制需要修改目录 /Library/LaunchDaemons directory 下 "limit.maxfiles.plist" 文件：

```xml
<plist version="1.0">
    <dict>
      <key>Label</key>
        <string>limit.maxfiles</string>
      <key>ProgramArguments</key>
        <array>
          <string>launchctl</string>
          <string>limit</string>
          <string>maxfiles</string>
          <string>66000</string>
          <string>66000</string>
        </array>
      <key>RunAtLoad</key>
        <true/>
      <key>ServiceIPC</key>
        <false/>
    </dict>
  </plist>
```

> 一个 IP 的 FD 限制文件个数一般 都是 65535， 超过它即可

&nbsp;

- mac  - 笔者电脑是 524288 是完全够用的。

```bash
$ ulimit -a
-n: file descriptors                524288
```

&nbsp;

- linux
  - 修改相应值（-n）即可
  - 为啥 ulimit -n == 1024, 但是连接数超过 1024 呢
    - root 有特权
    - 一般生产环境，都不会使用 root 作为 Java 程序的启动用户

```bash
$ ulimit -a
open files (-n) 1025

# 修改
ulimit -SHn 500000
```

&nbsp;

- Kernel 文件描述符

```bash
$ vi /etc/security/limits.conf

es hard nofile 65536 # 文件描述符个数
es soft nofile 65536
es hard nproc 4096   # 线程数
es hard nproc 4096   # 线程数
```

&nbsp;

## 单线程多路复用器 

> 性能比单纯的用 ServerSocketChannel 的场景，性能高10倍以上
>
> 这里不做过多介绍，实际生产环境中，Java 程序不可能仅仅只用一个单线程对外服务
>
> 下面对多线程多路复用器做深度剖析
>
> - 深度理解后，就会明白，实际生产环境，上面那种单线程 ServerSocketChannel 是没有任何意义的。
>   - 仅仅用来理解 Channel 的意义，怎么做到 non-blocking Socket 操作，non-blocking 的实际意义所在。 
>   - 与 BIO 的区别又是啥，不用 clone 线程也能实现服务（单线程）， 仅仅而已。

&nbsp;

## 多线程多路复用器

> 性能更佳，压榨服务器多 Core 的性能。达到最佳性能体验

&nbsp;

![io nio selector](/Users/alton/Documents/profile/notebook/Java/play-java/io/docs/images/io-nio-selector.png)

- 代码