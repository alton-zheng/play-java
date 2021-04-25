# 基本概念

&nbsp;

----
## 1 Message Model

RocketMQ 主要由 Producer、Broker、Consumer 三部分组成，其中 Producer 负责生产消息，Consumer 负责消费消息，Broker 负责存储消息。Broker 在实际部署过程中对应一台服务器，每个 Broker 可以存储多个 Topic的消息，每个 Topic 的消息也可以分片存储于不同的 Broker。Message Queue 用于存储消息的物理地址，每个Topic中的消息地址存储于多个 Message Queue 中。ConsumerGroup 由多个 Consumer 实例构成。跟 Kafka 基本一致。

&nbsp;

## 2 Producer

 负责生产消息，一般由业务系统负责生产消息。一个消息生产者会把业务应用系统里产生的消息发送到 Broker 服务器。RocketMQ 提供多种发送方式，同步发送、异步发送、顺序发送、单向发送。同步和异步方式均需要 Broker 返回确认信息，单向发送不需要。

&nbsp;

## 3 Consumer
 负责消费消息，一般是后台系统负责异步消费。一个消息消费者会从 Broker 服务器拉取消息、并将其提供给应用程序。从用户应用的角度而言提供了两种消费形式：

- 拉取式消费: Pull 
  - 商业铂金版
    - 产品本身挖了很多坑，简单点说，只有交钱才能好用
- 推动式消费: Push
  - 普通用户

&nbsp;

## 4 Topic
  表示一类消息的集合，每个 topic 包含若干条 message ，每条 message 只能属于一个 topic，是 RocketMQ 进行消息订阅的基本单位。

&nbsp;

## 5 Broker Server
消息中转角色，负责存储消息、转发消息。Broker 在 RocketMQ 系统中负责接收从 Producer 发送来的消息并存储、同时为 Comsumer 的拉取请求作准备。Broker 服务器也存储 message 相关的 meta-data，包括 ComsumerGroup、message offset 和 topic 和 queue 等。

&nbsp;

## 6 Name Server
 Name Server 充当路由消息的提供者。Producer 或 Consumer 能够通过 Name Server 查找各 topic 相应的Broker IP 列表。多个 Namesrv 实例组成 cluster，但相互独立，没有 message 交换。

&nbsp;

## 7 Pull Consumer
  Consumer 消费的一种类型，应用通常主动调用 Consumer 的拉消息方法从 Broker 服务器拉消息、主动权由应用控制。一旦获取了批量消息，应用就会启动消费过程。

&nbsp;

## 8 Push Consumer
 Consumer 消费的一种类型，该模式下 Broker 收到数据后会主动推送给 message 端，该消费模式一般实时性较高。

&nbsp;

## 9 Producer Group
  同一类 Producer 的集合，这类 Producer 发送同一类消息且发送逻辑一致。如果发送的是事务消息且原始 Producer 在发送之后崩溃，则 Broker 服务器会联系同一生产者组的其他生产者实例以提交或回溯消费。

&nbsp;

## 10 Consumer Group
 同一类 Consumer 的集合，这类 Consumer 通常消费同一类消息且消费逻辑一致。消费者组使得在消息消费方面，实现负载均衡和容错的目标变得非常容易。要注意的是，Consumer Group 的 Consumer 实例必须订阅完全相同的 Topic。RocketMQ 支持两种消息模式：

- 集群消费（Clustering）
- 广播消费（Broadcasting）

&nbsp;

## 11 集群消费（Clustering）
集群消费模式下,相同 Consumer Group 的每个 Consumer 实例平均分摊 message。

&nbsp;

## 12 广播消费（Broadcasting）
广播消费模式下，相同 Consumer Group 的每个 Consumer 实例都接收全量的 message.

&nbsp;

## 13 Normal Ordered Message
Normal Ordered Message 模式下，Consumer 通过同一个消费 queue 收到的 message 是有顺序的，不同消息队列收到的消息则可能是无顺序的。

&nbsp;

## 14 Strictly Ordered Message
Strictly Ordered Message 息模式下，消费者收到的所有消息均是有顺序的。

&nbsp;

## 15 Message
 Message 系统所传输信息的物理载体，生产和消费据的最小单位，每条 message 必须属于一个 topic 。RocketMQ 中每个 message 拥有唯一的 Message ID，且可以携带具有业务标识的 Key。系统提供了通过Message ID 和 Key 查询消息的功能。

&nbsp;

## 16 标签（Tag）
 为 message 设置的标志，用于同一 topic 下区分不同类型的消息。来自同一业务单元的消息，可以根据不同业务目的在同一 Topic 下设置不同 Tag。 Tag 能够有效地保持代码的清晰度和连贯性，并优化 RocketMQ 提供的查询系统。Consumer 可以根据 Tag 实现对不同 child-tag 的不同消费逻辑，实现更好的扩展性。

