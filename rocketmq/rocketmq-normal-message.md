# 普通消息

更新时间： 2020-11-12

普通消息是指消息队列RocketMQ版中无特性的消息，区别于有特性的定时和延时消息、顺序消息和事务消息。

**说明** 您在调用SDK收发消息时需注意，消息队列RocketMQ版提供的四种消息类型所对应的Topic不能混用，例如，您创建的普通消息的Topic只能用于收发普通消息，不能用于收发其他类型的消息；同理，事务消息的Topic也只能收发事务消息，不能用于收发其他类型的消息，以此类推。

- TCP SDK收发普通消息的示例代码

  - Java
    - [发送普通消息（三种方式）](https://www.alibabacloud.com/help/zh/doc-detail/29547.htm#concept-2047086)
    - [发送消息（多线程）](https://www.alibabacloud.com/help/zh/doc-detail/55385.htm#concept-2047087)
    - [订阅消息](https://www.alibabacloud.com/help/zh/doc-detail/29551.htm#concept-2047092)
  - C或C++
    - [收发普通消息](https://www.alibabacloud.com/help/zh/doc-detail/29556.htm#concept-2047098)
    - [订阅消息](https://www.alibabacloud.com/help/zh/doc-detail/29559.htm#concept-2047102)
  - .NET
    - [收发普通消息](https://www.alibabacloud.com/help/zh/doc-detail/29562.htm#concept-2047107)
    - [订阅消息](https://www.alibabacloud.com/help/zh/doc-detail/29565.htm#concept-2047111)

- HTTP SDK收发普通消息的示例代码

  消息队列RocketMQ版支持RESTful风格的HTTP协议通信，并提供了以下7种语言的SDK：

  - [Go SDK 接入说明](https://www.alibabacloud.com/help/zh/doc-detail/141779.htm#concept-2047123)
  - [Python SDK 接入说明](https://www.alibabacloud.com/help/zh/doc-detail/141780.htm#concept-2047124)
  - [Node.js SDK 接入说明](https://www.alibabacloud.com/help/zh/doc-detail/141781.htm#concept-2047125)
  - [PHP SDK 接入说明](https://www.alibabacloud.com/help/zh/doc-detail/141783.htm#concept-2047126)
  - [Java SDK 接入说明](https://www.alibabacloud.com/help/zh/doc-detail/141784.htm#concept-2047127)
  - [C++ SDK 接入说明](https://www.alibabacloud.com/help/zh/doc-detail/141785.htm#concept-2047128)
  - [C# SDK 接入说明](https://www.alibabacloud.com/help/zh/doc-detail/141786.htm#concept-2047129)

  请到[消息队列RocketMQ版HTTP SDK示例代码](https://github.com/aliyunmq/mq-http-samples)查看消息收发的示例代码。

  HTTP SDK的更多信息请参见[HTTP协议](https://www.alibabacloud.com/help/zh/doc-detail/102996.htm#concept-2047113)。