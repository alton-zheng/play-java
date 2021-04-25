# 事务消息

更新时间： 2020-11-12

消息队列RocketMQ版提供的分布式事务消息适用于所有对数据最终一致性有强需求的场景。本文介绍消息队列RocketMQ版事务消息的概念、优势、典型场景、交互流程以及使用过程中的注意事项。

## 概念介绍

- 事务消息：消息队列RocketMQ版提供类似X或Open XA的分布式事务功能，通过消息队列RocketMQ版事务消息能达到分布式事务的最终一致。
- 半事务消息：暂不能投递的消息，发送方已经成功地将消息发送到了消息队列RocketMQ版服务端，但是服务端未收到生产者对该消息的二次确认，此时该消息被标记成“暂不能投递”状态，处于该种状态下的消息即半事务消息。
- 消息回查：由于网络闪断、生产者应用重启等原因，导致某条事务消息的二次确认丢失，消息队列RocketMQ版服务端通过扫描发现某条消息长期处于“半事务消息”时，需要主动向消息生产者询问该消息的最终状态（Commit或是Rollback），该询问过程即消息回查。

## 分布式事务消息的优势

消息队列RocketMQ版分布式事务消息不仅可以实现应用之间的解耦，又能保证数据的最终一致性。同时，传统的大事务可以被拆分为小事务，不仅能提升效率，还不会因为某一个关联应用的不可用导致整体回滚，从而最大限度保证核心系统的可用性。在极端情况下，如果关联的某一个应用始终无法处理成功，也只需对当前应用进行补偿或数据订正处理，而无需对整体业务进行回滚。

![trans_msg_value](https://static-aliyun-doc.oss-cn-hangzhou.aliyuncs.com/assets/img/zh-CN/7087385851/p96619.png)

## 典型场景

在淘宝购物车下单时，涉及到购物车系统和交易系统，这两个系统之间的数据最终一致性可以通过分布式事务消息的异步处理实现。在这种场景下，交易系统是最为核心的系统，需要最大限度地保证下单成功。而购物车系统只需要订阅消息队列RocketMQ版的交易订单消息，做相应的业务处理，即可保证最终的数据一致性。

## 交互流程

事务消息交互流程如下图所示。![事物消息](https://static-aliyun-doc.oss-cn-hangzhou.aliyuncs.com/assets/img/zh-CN/1579264061/p177406.png)

事务消息发送步骤如下：

1. 发送方将半事务消息发送至消息队列RocketMQ版服务端。
2. 消息队列RocketMQ版服务端将消息持久化成功之后，向发送方返回Ack确认消息已经发送成功，此时消息为半事务消息。
3. 发送方开始执行本地事务逻辑。
4. 发送方根据本地事务执行结果向服务端提交二次确认（Commit或是Rollback），服务端收到Commit状态则将半事务消息标记为可投递，订阅方最终将收到该消息；服务端收到Rollback状态则删除半事务消息，订阅方将不会接受该消息。

事务消息回查步骤如下：

1. 在断网或者是应用重启的特殊情况下，上述步骤4提交的二次确认最终未到达服务端，经过固定时间后服务端将对该消息发起消息回查。
2. 发送方收到消息回查后，需要检查对应消息的本地事务执行的最终结果。
3. 发送方根据检查得到的本地事务的最终状态再次提交二次确认，服务端仍按照步骤4对半事务消息进行操作。

## 注意事项

1. 事务消息的Group ID不能与其他类型消息的Group ID共用。与其他类型的消息不同，事务消息有回查机制，回查时消息队列RocketMQ版服务端会根据Group ID去查询客户端。

2. 通过`ONSFactory.createTransactionProducer`创建事务消息的Producer时必须指定`LocalTransactionChecker`的实现类，处理异常情况下事务消息的回查。

3. 事务消息发送完成本地事务后，可在

   ```
   execute
   ```

   方法中返回以下三种状态：

   - `TransactionStatus.CommitTransaction`：提交事务，允许订阅方消费该消息。
   - `TransactionStatus.RollbackTransaction`：回滚事务，消息将被丢弃不允许消费。
   - `TransactionStatus.Unknow`：暂时无法判断状态，等待固定时间以后消息队列RocketMQ版服务端向发送方进行消息回查。

4. 可通过以下方式给每条消息设定第一次消息回查的最快时间：

   ```java
   Message message = new Message();
   // 在消息属性中添加第一次消息回查的最快时间，单位秒。例如，以下设置实际第一次回查时间为120秒~125秒之间message.putUserProperties(PropertyKeyConst.CheckImmunityTimeInSeconds,"120");
   // 以上方式只确定事务消息的第一次回查的最快时间，实际回查时间向后浮动0秒~5秒；如第一次回查后事务仍未提交，后续每隔5秒回查一次
   ```

## 更多信息

### TCP

# 收发事务消息

更新时间： 2021-01-06

本文提供使用TCP协议下的Java SDK收发事务消息的示例代码。

消息队列RocketMQ版提供类似X或Open XA的分布式事务功能，通过消息队列RocketMQ版事务消息，能达到分布式事务的最终一致。

**说明** 对于新手用户，建议在正式收发消息前，阅读[Demo工程](https://www.alibabacloud.com/help/zh/doc-detail/44711.htm#multiTask2572)来了解搭建消息队列RocketMQ版工程的具体步骤。

## 交互流程

事务消息交互流程如下图所示。

![process](https://static-aliyun-doc.oss-accelerate.aliyuncs.com/assets/img/zh-CN/2959388951/p69402.png)

更多信息，请参见[事务消息](https://www.alibabacloud.com/help/zh/doc-detail/43348.htm#concept-2047067)。

## 前提条件

您已完成以下操作：

- 下载Java SDK。Java SDK版本说明，请参见[版本说明](https://www.alibabacloud.com/help/zh/doc-detail/114448.htm#concept-2335081)。
- 准备环境。更多信息，请参见[准备环境](https://www.alibabacloud.com/help/zh/doc-detail/29546.htm#multiTask598)。
- （可选）日志配置。更多信息，请参见[日志配置](https://www.alibabacloud.com/help/zh/doc-detail/43460.htm#multiTask2226)。

## 发送事务消息

**说明** 具体的示例代码，请以[消息队列RocketMQ版代码库](https://code.aliyun.com/aliware_rocketmq/rocketmq-demo/tree/master)为准。

发送事务消息包含以下两个步骤：

1. 发送半事务消息（Half Message）及执行本地事务，示例代码如下。

   ```java
   package com.alibaba.webx.TryHsf.app1;
   
   import com.aliyun.openservices.ons.api.Message;
   import com.aliyun.openservices.ons.api.PropertyKeyConst;
   import com.aliyun.openservices.ons.api.SendResult;
   import com.aliyun.openservices.ons.api.transaction.LocalTransactionExecuter;
   import com.aliyun.openservices.ons.api.transaction.TransactionProducer;
   import com.aliyun.openservices.ons.api.transaction.TransactionStatus;
   import java.util.Properties;
   import java.util.concurrent.TimeUnit;
   
   public class TransactionProducerClient {
    private final static Logger log = ClientLogger.getLog(); // 您需要设置自己的日志，便于排查问题。
   
    public static void main(String[] args) throws InterruptedException {
        final BusinessService businessService = new BusinessService(); // 本地业务。
        Properties properties = new Properties();
           // 您在控制台创建的Group ID。注意：事务消息的Group ID不能与其他类型消息的Group ID共用。
        properties.put(PropertyKeyConst.GROUP_ID,"XXX");
           // AccessKey ID阿里云身份验证，在阿里云RAM控制台创建。
        properties.put(PropertyKeyConst.AccessKey,"XXX");
           // AccessKey Secret阿里云身份验证，在阿里云RAM控制台创建。
        properties.put(PropertyKeyConst.SecretKey,"XXX");
           // 设置TCP接入域名，进入消息队列RocketMQ版控制台的实例详情页面的TCP协议客户端接入点区域查看。
        properties.put(PropertyKeyConst.NAMESRV_ADDR,"XXX");
   
        TransactionProducer producer = ONSFactory.createTransactionProducer(properties,
                new LocalTransactionCheckerImpl());
        producer.start();
        Message msg = new Message("Topic","TagA","Hello MQ transaction===".getBytes());
        try {
                SendResult sendResult = producer.send(msg, new LocalTransactionExecuter() {
                    @Override
                    public TransactionStatus execute(Message msg, Object arg) {
                        // 消息ID（有可能消息体一样，但消息ID不一样，当前消息属于半事务消息，所以消息ID在消息队列RocketMQ版控制台无法查询）。
                        String msgId = msg.getMsgID();
                        // 消息体内容进行crc32，也可以使用其它的如MD5。
                        long crc32Id = HashUtil.crc32Code(msg.getBody());
                        // 消息ID和crc32id主要是用来防止消息重复。
                        // 如果业务本身是幂等的，可以忽略，否则需要利用msgId或crc32Id来做幂等。
                        // 如果要求消息绝对不重复，推荐做法是对消息体使用crc32或MD5来防止重复消息。
                        Object businessServiceArgs = new Object();
                        TransactionStatus transactionStatus = TransactionStatus.Unknow;
                        try {
                            boolean isCommit =
                                businessService.execbusinessService(businessServiceArgs);
                            if (isCommit) {
                                // 本地事务已成功则提交消息。
                                transactionStatus = TransactionStatus.CommitTransaction;
                            } else {
                                // 本地事务已失败则回滚消息。
                                transactionStatus = TransactionStatus.RollbackTransaction;
                            }
                        } catch (Exception e) {
                            log.error("Message Id:{}", msgId, e);
                        }
                        System.out.println(msg.getMsgID());
                        log.warn("Message Id:{}transactionStatus:{}", msgId, transactionStatus.name());
                        return transactionStatus;
                    }
                }, null);
            }
            catch (Exception e) {
                   // 消息发送失败，需要进行重试处理，可重新发送这条消息或持久化这条数据进行补偿处理。
                System.out.println(new Date() + " Send mq message failed. Topic is:" + msg.getTopic());
                e.printStackTrace();
            }
        // demo example防止进程退出（实际使用不需要这样）。
        TimeUnit.MILLISECONDS.sleep(Integer.MAX_VALUE);
    }
   }                        
   ```

2. 提交事务消息状态。

   当本地事务执行完成（执行成功或执行失败），需要通知服务器当前消息的事务状态。通知方式有以下两种：

   - 执行本地事务完成后提交。
   - 执行本地事务一直没提交状态，等待服务器回查消息的事务状态。

   事务状态有以下三种：

   - `TransactionStatus.CommitTransaction` 提交事务，允许订阅方消费该消息。
   - `TransactionStatus.RollbackTransaction` 回滚事务，消息将被丢弃不允许消费。
   - `TransactionStatus.Unknow` 无法判断状态，期待消息队列RocketMQ版的Broker向发送方再次询问该消息对应的本地事务的状态。

   ```java
   public class LocalTransactionCheckerImpl implements LocalTransactionChecker {
      private final static Logger log = ClientLogger.getLog();
      final  BusinessService businessService = new BusinessService();
   
      @Override
      public TransactionStatus check(Message msg) {
          //消息ID（有可能消息体一样，但消息ID不一样，当前消息属于半事务消息，所以消息ID在消息队列RocketMQ版控制台无法查询）。
          String msgId = msg.getMsgID();
          //消息体内容进行crc32，也可以使用其它的方法如MD5。
          long crc32Id = HashUtil.crc32Code(msg.getBody());
          //消息ID和crc32Id主要是用来防止消息重复。
          //如果业务本身是幂等的，可以忽略，否则需要利用msgId或crc32Id来做幂等。
          //如果要求消息绝对不重复，推荐做法是对消息体使用crc32或MD5来防止重复消息。
          //业务自己的参数对象，这里只是一个示例，需要您根据实际情况来处理。
          Object businessServiceArgs = new Object();
          TransactionStatus transactionStatus = TransactionStatus.Unknow;
          try {
              boolean isCommit = businessService.checkbusinessService(businessServiceArgs);
              if (isCommit) {
                  //本地事务已成功则提交消息。
                  transactionStatus = TransactionStatus.CommitTransaction;
              } else {
                  //本地事务已失败则回滚消息。
                  transactionStatus = TransactionStatus.RollbackTransaction;
              }
          } catch (Exception e) {
              log.error("Message Id:{}", msgId, e);
          }
          log.warn("Message Id:{}transactionStatus:{}", msgId, transactionStatus.name());
          return transactionStatus;
      }
    }                        
   ```

   **工具类**

   ```java
   import java.util.zip.CRC32;
   public class HashUtil {
       public static long crc32Code(byte[] bytes) {
           CRC32 crc32 = new CRC32();
           crc32.update(bytes);
           return crc32.getValue();
       }
   }                      
   ```

## 事务回查机制说明

- 发送事务消息为什么必须要实现回查Check机制？

  当步骤1中半事务消息发送完成，但本地事务返回状态为`TransactionStatus.Unknow`，或者应用退出导致本地事务未提交任何状态时，从Broker的角度看，这条Half状态的消息的状态是未知的。因此Broker会定期要求发送方Check该Half状态消息，并上报其最终状态。

- Check被回调时，业务逻辑都需要做些什么？

  事务消息的Check方法里面，应该写一些检查事务一致性的逻辑。消息队列RocketMQ版发送事务消息时需要实现`LocalTransactionChecker`接口，用来处理Broker主动发起的本地事务状态回查请求，因此在事务消息的Check方法中，需要完成两件事情：

  1. 检查该半事务消息对应的本地事务的状态（committed or rollback）。
  2. 向Broker提交该半事务消息本地事务的状态。

## 订阅事务消息

事务消息的订阅与普通消息订阅一致，更多信息，请参见[订阅消息](https://www.alibabacloud.com/help/zh/doc-detail/29551.htm#concept-2047092)。

### HTTP

