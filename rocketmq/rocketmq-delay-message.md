# 定时和延时消息

更新时间： 2020-11-12

本文主要介绍消息队列RocketMQ版的定时消息和延时消息的概念、适用场景以及使用过程中的注意事项。

## 概念介绍

- 定时消息：Producer将消息发送到消息队列RocketMQ版服务端，但并不期望立马投递这条消息，而是推迟到在当前时间点之后的某一个时间投递到Consumer进行消费，该消息即定时消息。
- 延时消息：Producer将消息发送到消息队列RocketMQ版服务端，但并不期望立马投递这条消息，而是延迟一定时间后才投递到Consumer进行消费，该消息即延时消息。

定时消息与延时消息在代码配置上存在一些差异，但是最终达到的效果相同：消息在发送到消息队列RocketMQ版服务端后并不会立马投递，而是根据消息中的属性延迟固定时间后才投递给消费者。

## 适用场景

定时消息和延时消息适用于以下一些场景：

- 消息生产和消费有时间窗口要求，例如在电商交易中超时未支付关闭订单的场景，在订单创建时会发送一条延时消息。这条消息将会在30分钟以后投递给消费者，消费者收到此消息后需要判断对应的订单是否已完成支付。如支付未完成，则关闭订单。如已完成支付则忽略。
- 通过消息触发一些定时任务，例如在某一固定时间点向用户发送提醒消息。

## 使用方式

定时消息和延时消息的使用在代码编写上存在略微的区别：

- 发送定时消息需要明确指定消息发送时间点之后的某一时间点作为消息投递的时间点。
- 发送延时消息时需要设定一个延时时间长度，消息将从当前发送时间点开始延迟固定时间之后才开始投递。

## 注意事项

- 定时消息的精度会有1s~2s的延迟误差。

- 定时和延时消息的`msg.setStartDeliverTime`参数需要设置成当前时间戳之后的某个时刻（单位毫秒）。如果被设置成当前时间戳之前的某个时刻，消息将立刻投递给消费者。

- 定时和延时消息的`msg.setStartDeliverTime`参数可设置40天内的任何时刻（单位毫秒），超过40天消息发送将失败。

- `StartDeliverTime`是服务端开始向消费端投递的时间。如果消费者当前有消息堆积，那么定时和延时消息会排在堆积消息后面，将不能严格按照配置的时间进行投递。

- 由于客户端和服务端可能存在时间差，消息的实际投递时间与客户端设置的投递时间之间可能存在偏差。

- 设置定时和延时消息的投递时间后，依然受3天的消息保存时长限制。

  例如，设置定时消息5天后才能被消费，如果第5天后一直没被消费，那么这条消息将在第8天被删除。

## TCP协议示例代码

收发定时消息和延时消息的示例代码，请参见以下文档：

- Java
  - [收发定时消息](https://www.alibabacloud.com/help/zh/doc-detail/29550.htm#concept-2047091)
  - [收发延时消息](https://www.alibabacloud.com/help/zh/doc-detail/29549.htm#multiTask2520)
- C++
  - [收发定时消息](https://www.alibabacloud.com/help/zh/doc-detail/29557.htm#concept-2047100)
- .NET
  - [收发定时消息](https://www.alibabacloud.com/help/zh/doc-detail/29563.htm#concept-2047109)

## HTTP协议示例代码

收发定时消息和延时消息的示例代码，请参见以下文档：

- [Go SDK 接入说明](https://www.alibabacloud.com/help/zh/doc-detail/141779.htm#concept-2047123)
- [Python SDK 接入说明](https://www.alibabacloud.com/help/zh/doc-detail/141780.htm#concept-2047124)
- [Node.js SDK 接入说明](https://www.alibabacloud.com/help/zh/doc-detail/141781.htm#concept-2047125)
- [PHP SDK 接入说明](https://www.alibabacloud.com/help/zh/doc-detail/141783.htm#concept-2047126)
- [Java SDK 接入说明](https://www.alibabacloud.com/help/zh/doc-detail/141784.htm#concept-2047127)
- [C++ SDK 接入说明](https://www.alibabacloud.com/help/zh/doc-detail/141785.htm#concept-2047128)
- [C# SDK 接入说明](https://www.alibabacloud.com/help/zh/doc-detail/141786.htm#concept-2047129)
- 