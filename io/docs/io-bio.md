# BIO

步骤：

- `socket`
- `bind`
- `listen`
- `accept`
  - `fd3 -> fd5 /blocking`
    - client  connect
  - clone() 线程来进行 read blocking
    - clone thread
    - recv(fd5 -> blocking)
    - 每次请求都创建一个线程
      - 效率慢



![](/Users/alton/Documents/profile/notebook/Java/play-java/io/docs/images/BIO.jpg)

&nbsp;

## Server

```java
package com.bjmashibing.system.io;

/**
 * @Author: alton
 * @Date: Created in 2021/4/8 2:32 PM
 * @Description:
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;


public class SocketIOServerProperties {


    //server socket listen property:
    private static final int RECEIVE_BUFFER = 10;
    private static final int SO_TIMEOUT = 0;
    private static final boolean REUSE_ADDR = false;
    private static final int BACK_LOG = 2;
    //client socket listen property on server endpoint:
    private static final boolean CLI_KEEPALIVE = false;
    private static final boolean CLI_OOB = false;
    private static final int CLI_REC_BUF = 20;
    private static final boolean CLI_REUSE_ADDR = false;
    private static final int CLI_SEND_BUF = 20;
    private static final boolean CLI_LINGER = true;
    private static final int CLI_LINGER_N = 0;
    private static final int CLI_TIMEOUT = 0;
    private static final boolean CLI_NO_DELAY = false;
/*

    StandardSocketOptions.TCP_NODELAY
    StandardSocketOptions.SO_KEEPALIVE
    StandardSocketOptions.SO_LINGER
    StandardSocketOptions.SO_RCVBUF
    StandardSocketOptions.SO_SNDBUF
    StandardSocketOptions.SO_REUSEADDR

 */


    public static void main(String[] args) {

        ServerSocket server = null;
        try {

            // ServerSocket
            // 请求的传入连接队列的最大长度。
            server = new ServerSocket(9090, BACK_LOG);
            server.setReceiveBufferSize(RECEIVE_BUFFER);
            server.setReuseAddress(REUSE_ADDR);
            server.setSoTimeout(SO_TIMEOUT);

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            while (true) {

                System.in.read();  //分水岭：


                Socket client = server.accept();  //阻塞的，没有 -1  一直卡着不动  accept(4,
                System.out.println("client port: " + client.getPort());

                client.setKeepAlive(CLI_KEEPALIVE);
                client.setOOBInline(CLI_OOB);
                client.setReceiveBufferSize(CLI_REC_BUF);
                client.setReuseAddress(CLI_REUSE_ADDR);
                client.setSendBufferSize(CLI_SEND_BUF);
                client.setSoLinger(CLI_LINGER, CLI_LINGER_N);
                client.setSoTimeout(CLI_TIMEOUT);
                client.setTcpNoDelay(CLI_NO_DELAY);
                
                new Thread(
                        () -> {
                            try {
                                InputStream in = client.getInputStream();
                                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                                char[] data = new char[1024];
                                while (true) {

                                    int num = reader.read(data);

                                    if (num > 0) {
                                        System.out.println("client read some data is :" + num + " val :" + new String(data, 0, num));
                                    } else if (num == 0) {
                                        System.out.println("client readed nothing!");
                                        continue;
                                    } else {
                                        System.out.println("client readed -1...");
                                        System.in.read();
                                        client.close();
                                        break;
                                    }
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                ).start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
```

&nbsp;

参数： 

- `setKeepAlive(boolean on)`: 
  - Enable/disable SO_KEEPALIVE.
  - on – 是否打开 socket keep alive

```bash
$ netstat -antp
## 抓 tcp dump 网络包， 追踪程序 IO 实现
$ tcpdump -nn -i eth0 port 9090
# 后续学习时，通过抓包慢慢深化知识体系
```

&nbsp;

- 开始 Server

```bash
$ javac SocketIOServerProperties.java && java SocketIOServerProperties
Server up use 9090!!!
```

&nbsp;

- 监控网络

```bash
$ netstat -natp
# 可以看到 9090 端口相关信息

$ lsof -p port 可以看到进程的 FD 信息
```

&nbsp;

## Cient

```java
package com.bjmashibing.system.io;

import java.io.*;
import java.net.Socket;
/**
 * @Author: alton
 * @Date: Created in 2021/4/8 2:32 PM
 * @Description:
 */
public class SocketClient {

    public static void main(String[] args) {

        try {
            Socket client = new Socket("127.0.0.1",9090);
            client.setSendBufferSize(20);
            client.setTcpNoDelay(false);
            OutputStream out = client.getOutputStream();

            InputStream in = System.in;
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            while(true){
                String line = reader.readLine();
                if(line != null ){
                    byte[] bb = line.getBytes();
                    System.out.println("发送内容长度： " +  bb.length);
                    out.write(bb);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

```

```bash
$ javac SocketClient.java && java SocketClient
```

```bash
netstat -natp 
```



> 此时服务器在 System.in.read(); 
>
> - netstat -natp 能看到 client 对 Server 的请求
> - 端口抓包也能发现链接已经建立
>   - Kernel 能发现链接
>
> - 意味着即便服务器没有执行 accept()
> - 但是现在实际上没有对应的描述符

> 当 Client 在 Server 没有 accept() 时，一直发数据，到达了 Server 设置的 Recv 大小，之后的数据就会被丢弃。
>
> - 这里可通过 `netstat -natp` 来进行监控
>
> 这在实际生产环境中，是万万不可的。
>
> 所以 Socket 这快的调优非常非常的重要

&nbsp;

Socket 参数解析：

- `setTcpNoDelay(boolean on)` ： 
  - Enable/disable TCP_NODELAY (disable/enable Nagle's algorithm).
    Params:
    on – true to enable TCP_NODELAY, false to disable.
  - 通俗点说
    - false 时，可以将 client 发送的数据，在 server 端通过延迟的手动，合并接收。而不会像上面代码以字节的发送。
    - true ： 开启 `TCP_NODELAY`, 无延迟模式，让 server 端接收数据时，将 client 发送的 一行数据（readLine） 分开接收（为了不延迟）

&nbsp;

## IO 模型

> BIO 模型
>
> - 同步阻塞模型