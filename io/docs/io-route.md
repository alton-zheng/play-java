### 解决路由问题

解决本机 VM Linux 和 Windows socket 链接时，

- 只能从 windows 原 ip 到 Linux 服务，linux 服务 socket 数据 到 windows 原 ip 后被丢弃（待解决的问题）
- vm8 虚拟 ip 和 linux ，正常 建立 socket。

> 本质原因（下面有图解，和文件描述结合一起理解）:
>
> -  linux  与 windows 的 vm 网卡 (vmware)  建立了 vm-net，windows 的2个 ip(原IP 和 虚拟 Ip),都能正常发送数据包给 linux
>
> - 反过来， linux 发送数据给 windows vm ip 时，也是通过 vm-net 来发送的。中间是不需要通过 ip 转换的。
> - 但 linux 发送数据包给 windows 时， 不能通过 net 方式，只能通过 vmware 的网关进行转换才能。转换后
>   - linux 的 ip 和 port 发生了变化， windows client 收到的数据包中的发送方跟它预期的不一致（数据包被丢弃）
>
> 通过下面方法来解决这个问题

&nbsp;

![io-route-windows-linux](/Users/alton/Documents/profile/notebook/Java/play-java/io/docs/images/io-route-windows-linux.png)

首先网络路由信息，查看网络信息

```bash
$ route -n
# 此时可以发现，无法看到 windows 原 ip 相关的路由信息
```

&nbsp;

添加网络路由信息，将 windows ip 与 linux 网关产生路由

```bash
$ route add -host 192.168.110.100 gw 192.168.150.1
```

- `-host` : windows 原 ip
- `gw`:  linux 网关地址