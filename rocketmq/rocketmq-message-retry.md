# 消息重试

更新时间： 2021-03-25

本文介绍消息队列RocketMQ版的消息重试机制和配置方式。

**注意** HTTP SDK的重试机制：无序消息每隔5分钟重试一次，顺序消息每隔1分钟重试一次，最多重试288次。下文的所有内容均只适用于商业版TCP SDK的重试机制。更多信息，请参见[商业版TCP SDK参考](https://www.alibabacloud.com/help/zh/doc-detail/114448.htm#concept-2335081)。

## 顺序消息的重试

对于顺序消息，当消费者消费消息失败后，消息队列RocketMQ版会自动不断地进行消息重试（每次间隔时间为1秒），这时，应用会出现消息消费被阻塞的情况。因此，建议您使用顺序消息时，务必保证应用能够及时监控并处理消费失败的情况，避免阻塞现象的发生。

## 无序消息的重试

对于无序消息（普通、定时、延时、事务消息），当消费者消费消息失败时，您可以通过设置返回状态达到消息重试的结果。

无序消息的重试只针对集群消费方式生效；广播方式不提供失败重试特性，即消费失败后，失败消息不再重试，继续消费新的消息。

**注意** 以下内容都只针对无序消息生效。

## 重试次数

消息队列RocketMQ版默认允许每条消息最多重试16次，每次重试的间隔时间如下。

| 第几次重试 | 与上次重试的间隔时间 | 第几次重试 | 与上次重试的间隔时间 |
| :--------- | :------------------- | :--------- | :------------------- |
| 1          | 10秒                 | 9          | 7分钟                |
| 2          | 30秒                 | 10         | 8分钟                |
| 3          | 1分钟                | 11         | 9分钟                |
| 4          | 2分钟                | 12         | 10分钟               |
| 5          | 3分钟                | 13         | 20分钟               |
| 6          | 4分钟                | 14         | 30分钟               |
| 7          | 5分钟                | 15         | 1小时                |
| 8          | 6分钟                | 16         | 2小时                |

如果消息重试16次后仍然失败，消息将不再投递。如果严格按照上述重试时间间隔计算，某条消息在一直消费失败的前提下，将会在接下来的4小时46分钟之内进行16次重试，超过这个时间范围消息将不再重试投递。

**注意** 一条消息无论重试多少次，这些重试消息的Message ID不会改变。

## 配置方式

- 消费失败后，重试配置方式

  集群消费方式下，消息消费失败后期望消息重试，需要在消息监听器接口的实现中明确进行配置（三种方式任选一种）：

  - 方式1：返回Action.ReconsumeLater（推荐）
  - 方式2：返回Null
  - 方式3：抛出异常

  示例代码

  ```java
  public class MessageListenerImpl implements MessageListener {
  
      @Override
      public Action consume(Message message, ConsumeContext context) {
          //消息处理逻辑抛出异常，消息将重试。
          doConsumeMessage(message);
          //方式1：返回Action.ReconsumeLater，消息将重试。
          return Action.ReconsumeLater;
          //方式2：返回null，消息将重试。
          return null;
          //方式3：直接抛出异常，消息将重试。
          throw new RuntimeException("Consumer Message exception");
      }
  }
  ```

- 消费失败后，无需重试的配置方式

  集群消费方式下，消息失败后期望消息不重试，需要捕获消费逻辑中可能抛出的异常，最终返回Action.CommitMessage，此后这条消息将不会再重试。

  示例代码

  ```java
  public class MessageListenerImpl implements MessageListener {
  
      @Override
      public Action consume(Message message, ConsumeContext context) {
          try {
              doConsumeMessage(message);
          } catch (Throwable e) {
              //捕获消费逻辑中的所有异常，并返回Action.CommitMessage;
              return Action.CommitMessage;
          }
          //消息处理正常，直接返回Action.CommitMessage;
          return Action.CommitMessage;
      }
  }
  ```

- 自定义消息最大重试次数

  **说明** 自定义消息队列RocketMQ版的客户端日志配置，请升级TCP Java SDK版本到1.2.2及以上。

  消息队列RocketMQ版允许Consumer启动的时候设置最大重试次数，重试时间间隔将按照以下策略：

  - 最大重试次数小于等于16次，则重试时间间隔同上表描述。
  - 最大重试次数大于16次，超过16次的重试时间间隔均为每次2小时。

  配置方式如下：

  ```java
  Properties properties = new Properties();
  //配置对应Group ID的最大消息重试次数为20次，最大重试次数为字符串类型。
  properties.put(PropertyKeyConst.MaxReconsumeTimes,"20");
  Consumer consumer =ONSFactory.createConsumer(properties);
  ```

  **注意**

  - 消息最大重试次数的设置对相同Group ID下的所有Consumer实例有效。
  - 如果只对相同Group ID下两个Consumer实例中的其中一个设置了MaxReconsumeTimes，那么该配置对两个Consumer实例均生效。
  - 配置采用覆盖的方式生效，即最后启动的Consumer实例会覆盖之前的启动实例的配置。

## 获取消息重试次数

消费者收到消息后，可按照以下方式获取消息的重试次数：

```java
public class MessageListenerImpl implements MessageListener {

    @Override
    public Action consume(Message message, ConsumeContext context) {
        //获取消息的重试次数。
        System.out.println(message.getReconsumeTimes());
        return Action.CommitMessage;
    }
}
```