## Socket

- 四元组唯一
  - 同一 资源组（服务器 ip,port + client ip ）可建立 $65535$ 个 socket 链接。

```log
(cip, cport, sip, sport)
```

&nbsp;

![socket-tcp](images/soket-tcp-3-connect.png)

&nbsp;

![io-socket-tcp](images/io-socket-tcp.png)

![socket](images/io-socket.png)

&nbsp;

## Listen 状态的服务器， listen socket 的接受， 发送队列放什么？

> accept 队列  
>
> - 等待程序接收的连接
>
> - 有大小限制 （backlog）不被程序取走
>
>   - backlog
>
>     - 内核
>
>     - `cat /proc/sys/net/core/somaxconn` 
>       -  默认 `128`
>       -  ACCEPT 队列你可以给出 `backlog` 取 min
>
>       -  每个人顾好自己，每个人处理好对别人的打扰
>
>     - `cat /proc/sys/net/ipv4/tcp_max_syn_backlog  DDOS`
>       - 默认 `2048`
>       - `backlog` 满了,  `accept` 队列满了，新 client 直接 `Connection refused`

&nbsp;

```bash
$ nc -l localhost 9999

$ netstat -natp

$ ss -lna
```

&nbsp;

## Connection refused？

- 服务器端口没开

- 网络不通
- socket 队列满了
  - 内核态和用户态交互出现问题

&nbsp;

### Linux 模拟 Socket （与 baidu.com 进行连接）

```bash
$ cd /proc/$$/fd
/proc/pid/fd

## 新建 fd 8
$ exec 8<> /dev/tcp/www.baidu.com/80

## 到此为止，linux 与 baidu.com 建立好通道

## 发起 Request
echo -e `GET / HTTP/1.0 \n` >& 8

## 查看 Response 
cat 0>& 8
```





&nbsp;