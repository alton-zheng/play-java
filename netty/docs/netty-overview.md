# Netty 简介

&nbsp;

## Netty 初识

Netty 是一个 NIO 客户端服务器框架，可以快速轻松的开发 network 应用程序，例如协议 server 和 client。 它极大地精简诸如 TCP 和 UDP socket server 之类的网络编程以及提高了它们的效率。

`Quick 和 Easy` 不意味着最终的 application 将遭受可维护性或性能问题的困扰。 

Netty 经过精心设计， 结合了许多协议 （例如 FTP， SMTP， HTTP 以及各种基于 `binary` 和 `text-based` 的老式协议）的实施经验。结果， Netty 成功地找到了一种无须拖鞋即可轻松实现开发，性能，稳定性和灵活性的方法。

&nbsp;

## Design

- 适用于各种传输类型的统一 API
  - blocking socket
  - non-blocking socket
- 基于灵活且可扩展的 event model, 可将关注点分离
- 高度可定制的 thread model
  - single thread
  - 一个或多个 thread pool
  - e.g.  `SEDA`
- 真正的无连接数据报（datagream） socket 支持（since 3.1）

&nbsp;

### 使用方便

- `Well-documented` 的 javadoc， 用户指南和示例
- 没有其它依赖关系， JDK 5 （Netty 3.x) 或  JDK 6（Netty 4.x）就足够了
  - 注意： 某些组件 （e.g. HTTP/2 ）可能有更多要求。请参阅 [Requirement 页面](https://netty.io/wiki/requirements.html) 以获取更多信息

&nbsp;

### 性能

- 更高的吞吐量，更低的延迟
- 减少资源消耗
- 减少不必要的 memory 复制

&nbsp;

### 安全

- 完整的 `SSL/TLS` 和 `StartTLS` 支持

&nbsp;

### 社区

- Release 更新快
- 作者自 2003 年依赖一致在编写类似的框架
- 社区活跃



