# 消息类型概述

更新时间： 2021-03-30

本文列举了消息队列RocketMQ版所支持的消息类型，以及使用这些消息类型的注意事项。

## 消息类型列表

### [普通消息](https://www.alibabacloud.com/help/zh/doc-detail/96359.html)

消息队列RocketMQ版中无特性的消息，区别于有特性的定时和延时消息、顺序消息和事务消息。

### [定时和延时消息](https://www.alibabacloud.com/help/zh/doc-detail/43349.html)

允许消息生产者对指定消息进行定时（延时）投递，最长支持40天。

### [顺序消息](https://www.alibabacloud.com/help/zh/doc-detail/49319.html)

允许消息消费者按照消息发送的顺序对消息进行消费。

### [事务消息](https://www.alibabacloud.com/help/zh/doc-detail/43348.html)

实现类似X或Open XA的分布事务功能，以达到事务最终一致性状态。



## 注意事项

您在调用SDK收发消息时需注意，消息队列RocketMQ版提供的四种消息类型所对应的Topic不能混用，例如，您创建的普通消息的Topic只能用于收发普通消息，不能用于收发其他类型的消息；同理，事务消息的Topic也只能收发事务消息，不能用于收发其他类型的消息，以此类推。