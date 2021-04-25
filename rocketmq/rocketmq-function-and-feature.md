# 功能与特性概述

更新时间： 2021-04-09

本文列举消息队列RocketMQ版所支持的所有功能与特性。

## 概览

消息队列RocketMQ版在阿里云多个地域（Region）提供了高可用消息云服务。单个地域内采用多机房部署，可用性极高，即使整个机房都不可用，仍然可以为应用提供消息发布服务。

消息队列RocketMQ版提供TCP和HTTP协议的多语言接入方式，方便不同编程语言开发的应用快速接入消息队列RocketMQ版消息云服务。您可以将应用部署在阿里云ECS、企业自建云，或者嵌入到移动端、物联网设备中与消息队列RocketMQ版建立连接进行消息收发；同时，本地开发者也可以通过公网接入消息队列RocketMQ版服务进行消息收发。![functionswithoutstomp](https://static-aliyun-doc.oss-accelerate.aliyuncs.com/assets/img/zh-CN/1002585951/p68860.png)

## 多协议接入

- [TCP协议](https://www.alibabacloud.com/help/zh/doc-detail/114448.htm#concept-2335081)：区别于HTTP简单的接入方式，提供更为专业、可靠、稳定的TCP协议的SDK接入服务。支持的语言包括Java、C/C++ 以及.NET。
- [HTTP协议](https://www.alibabacloud.com/help/zh/doc-detail/102996.htm#concept-2047113)：采用RESTful风格，方便易用，快速接入，跨网络能力强。支持Java、C++、.NET、Go、Python、Node.js和PHP七种语言客户端。

## 管理工具

- Web控制台：支持Topic管理、Group管理、消息查询、消息轨迹展示和查询、资源报表以及监控报警管理。
- OpenAPI：提供开放的API便于将消息队列RocketMQ版管理工具集成到自己的控制台。消息队列RocketMQ版的API的更多信息，请参见[OpenAPI参考](https://www.alibabacloud.com/help/zh/doc-detail/44418.htm#concept-1351495)。

## 消息类型

- [普通消息](https://www.alibabacloud.com/help/zh/doc-detail/96359.htm#concept-2047064)：消息队列RocketMQ版中无特性的消息，区别于有特性的定时和延时消息、顺序消息和事务消息。
- [事务消息](https://www.alibabacloud.com/help/zh/doc-detail/43348.htm#concept-2047067)：实现类似X/Open XA的分布事务功能，以达到事务最终一致性状态。
- [定时和延时消息](https://www.alibabacloud.com/help/zh/doc-detail/43349.htm#concept-2047065)：允许消息生产者对指定消息进行定时（延时）投递，最长支持40天。
- [顺序消息](https://www.alibabacloud.com/help/zh/doc-detail/49319.htm#concept-2047066)：允许消息消费者按照消息发送的顺序对消息进行消费。

## 消息特性

- [消息重试](https://www.alibabacloud.com/help/zh/doc-detail/43490.htm#concept-2047068)：在消费者返回消息重试的响应后，消息队列RocketMQ版会按照相应的[重试规则](https://www.alibabacloud.com/help/zh/doc-detail/43490.htm#concept-2047068)进行消息重投。
- 至少投递一次（At-least-once）：消息队列RocketMQ版保证消息成功被消费一次。消息队列RocketMQ版的分布式特点和瞬变的网络条件，或者用户应用重启发布的情况下，可能导致消费者收到重复的消息。开发人员应将其应用程序设计为多次处理一条消息不会产生任何错误或不一致性。消息幂等最佳实践请参见[消费幂等](https://www.alibabacloud.com/help/zh/doc-detail/44397.htm#concept-2047147)。

## 特性功能

- [消息查询](https://www.alibabacloud.com/help/zh/doc-detail/29540.htm#concept-2047150)：消息队列RocketMQ版提供了三种消息查询的方式，分别是按Message ID、Message Key以及Topic查询。
- [查询消息轨迹](https://www.alibabacloud.com/help/zh/doc-detail/43357.htm#concept-2335151)：通过消息轨迹，能清晰定位消息从生产者发出，经由消息队列RocketMQ版服务端，投递给消息消费者的完整链路，方便定位排查问题。
- [集群消费和广播消费](https://www.alibabacloud.com/help/zh/doc-detail/43163.htm#concept-2047071)：当使用集群消费模式时，消息队列RocketMQ版认为任意一条消息只需要被消费者集群内的任意一个消费者处理即可；当使用广播消费模式时，消息队列RocketMQ版会将每条消息推送给消费者集群内所有注册过的消费者，保证消息至少被每台机器消费一次。
- [重置消费位点](https://www.alibabacloud.com/help/zh/doc-detail/63390.htm#task-2047153)：根据时间或位点重置消费进度，允许用户进行消息回溯或者丢弃堆积消息。
- [死信队列](https://www.alibabacloud.com/help/zh/doc-detail/87277.htm#concept-2047154)：将无法正常消费的消息储存到特殊的死信队列供后续处理。
- [全球消息路由](https://www.alibabacloud.com/help/zh/doc-detail/90483.htm#concept-2047155)：用于全球不同地域之间的消息同步，保证地域之间的数据一致性。
- [资源报表](https://www.alibabacloud.com/help/zh/doc-detail/43733.htm#concept-2047157)：消息生产和消费数据的统计功能。通过该功能，您可查询在一段时间范围内发送至某Topic的消息总量或者TPS（消息生产数据），也可查询在一个时间段内某Topic投递给某Group ID的消息总量或TPS（消息消费数据）。
- [监控报警](https://www.alibabacloud.com/help/zh/doc-detail/43732.htm#concept-2047152)：您可使用消息队列RocketMQ版提供的监控报警功能，监控某Group ID订阅的某Topic的消息消费状态并接收报警短信，帮助您实时掌握消息消费状态，以便及时处理消费异常。