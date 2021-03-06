# 消息堆积和延迟问题

更新时间： 2021-04-20

本文主要介绍消息队列RocketMQ版TCP协议的Java客户端使用过程中，经常会出现的消息堆积和消息延迟的问题。通过了解消息队列RocketMQ版客户端的消费原理和消息堆积的主要原因，帮助您可以在业务部署前更好的规划资源和配置，或在运维过程中及时调整业务逻辑，避免因消息堆积和延迟影响业务运行。

## 背景信息

消息处理流程中，如果客户端的消费速度跟不上服务端的发送速度，未处理的消息会越来越多，这部分消息就被称为堆积消息。消息出现堆积进而会造成消息消费延迟。以下场景需要重点关注消息堆积和延迟的问题：

- 业务系统上下游能力不匹配造成的持续堆积，且无法自行恢复。
- 业务系统对消息的消费实时性要求较高，即使是短暂的堆积造成的消息延迟也无法接受。

## 客户端消费原理

消息队列RocketMQ版TCP协议客户端的消费流程如下图所示。![消费原理](https://static-aliyun-doc.oss-accelerate.aliyuncs.com/assets/img/zh-CN/9615437061/p188872.png)

SDK客户端使用Push模式消费消息时，分为以下两个阶段：

- 阶段一：获取消息，SDK客户端通过长轮询批量拉取的方式从

  消息队列RocketMQ版

  服务端获取消息，将拉取到的消息缓存到本地缓冲队列中。

  SDK获取消息的方式为批量拉取，常见内网环境下都会有很高的吞吐量，例如：1个单线程单分区的低规格机器（4C8GB）可以达到几万TPS，如果是多个分区可以达到几十万TPS。所以这一阶段一般不会成为消息堆积的瓶颈。

- 阶段二：提交消费线程，SDK客户端将本地缓存的消息提交到消费线程中，使用业务消费逻辑进行处理。

  此时客户端的消费能力就完全依赖于业务逻辑的复杂度（消费耗时）和消费逻辑并发度了。如果业务处理逻辑复杂，处理单条消息耗时都较长，则整体的消息吞吐量肯定不会高，此时就会导致客户端本地缓冲队列达到上限，停止从服务端拉取消息。

通过以上客户端消费原理可以看出，消息堆积的主要瓶颈在于本地客户端的消费能力，即[消费耗时](https://www.alibabacloud.com/#section-qqy-2gu-l2k)和[消费并发度](https://www.alibabacloud.com/#section-86m-h0h-ng2)。想要避免和解决消息堆积问题，必须合理的控制消费耗时和消息并发度，其中消费耗时的优先级高于消费并发度，必须先保证消费耗时的合理性，再考虑消费并发度问题。

## 消费耗时

影响消费耗时的消费逻辑主要分为CPU内存计算和外部I/O操作，通常情况下代码中如果没有复杂的递归和循环的话，内部计算耗时相对外部I/O操作来说几乎可以忽略。外部I/O操作通常包括如下业务逻辑：

- 读写外部数据库，例如Mysql数据库读写。
- 读写外部缓存等系统，例如Redis读写。
- 下游系统调用，例如Dubbo调用或者下游HTTP接口调用。

这类外部调用的逻辑和系统容量您需要提前梳理，掌握每个调用操作预期的耗时，这样才能判断消费逻辑中I/O操作的耗时是否合理。通常消费堆积都是由于这些下游系统出现了服务异常、容量限制导致的消费耗时增加。

例如：某业务消费逻辑中需要写一条数据到数据库，单次消费耗时为1 ms，平时消息量小未出现异常。业务侧进行大促活动时，写数据库TPS爆发式增长，并很快达到数据库容量限制，导致消费单条消息的耗时增加到100 ms，业务侧可以明显感受到消费速度大幅下跌。此时仅通过调整消息队列RocketMQ版SDK的消费并发度并不能解决问题，需要对数据库容量进行升配才能从根本上提高客户端消费能力。

## 消费并发度

消息队列RocketMQ版消费消息的并发度计算方法如下表所示。

| 消息类型       | 消费并发度                         |
| :------------- | :--------------------------------- |
| 普通消息       | 单节点线程数*节点数量              |
| 定时和延时消息 |                                    |
| 事务消息       |                                    |
| 顺序消息       | Min(单节点线程数*节点数量，分区数) |

客户端消费并发度由单节点线程数和节点数量共同决定，一般情况下需要优先调整单节点的线程数，若单机硬件资源达到上限，则必须通过扩容节点来提高消费并发度。

**说明** 顺序消息的消费并发度还受Topic中分区个数的限制，具体分区数，请联系阿里云技术支持根据业务情况进行评估。

单节点的并发度需要谨慎设置，不能盲目直接调大线程数，设置过大的线程数反而会带来大量的线程切换的开销。理想环境下单节点的最优线程数计算模型如下：

- 单机vCPU核数为C。
- 线程切换耗时忽略不计，I/O操作不消耗CPU。
- 线程有足够消息等待处理，且内存充足。
- 逻辑中CPU计算耗时为T1，外部I/O操作为T2。

则单个线程能达到的TPS为1/（T1+T2），如果CPU使用率达到理想状态100%，那么单机达到最大能力时需要设置C*（T1+T2）/T1个线程。

**注意** 这里计算的最大线程数仅仅是在理想环境下得到的理论数据，实际应用环境中建议逐步调大线程数并观察效果再进行调整。

## 如何避免消息堆积和延迟

为了避免在业务使用时出现非预期的消息堆积和延迟问题，您需要在前期设计阶段对整个业务逻辑进行完善的排查和梳理。整理出正常业务运行场景下的性能基线，才能在故障场景下迅速定位到阻塞点。其中最重要的就是梳理消息的消费耗时和消息消费的并发度。

- 梳理消息的消费耗时

  通过压测获取消息的消费耗时，并对耗时较高的操作的代码逻辑进行分析。查询消费耗时，请参见[获取消息消费耗时](https://www.alibabacloud.com/help/zh/doc-detail/193952.htm#step-zbp-czw-m7t)。梳理消息的消费耗时需要关注以下信息：

  - 消息消费逻辑的计算复杂度是否过高，代码是否存在无限循环和递归等缺陷。
  - 消息消费逻辑中的I/O操作（如：外部调用、读写存储等）是否是必须的，能否用本地缓存等方案规避。
  - 消费逻辑中的复杂耗时的操作是否可以做异步化处理，如果可以是否会造成逻辑错乱（消费完成但异步操作未完成）。

- 设置消息的消费并发度

  1. 逐步调大线程的单个节点的线程数，并关测节点的系统指标，得到单个节点最优的消费线程数和消息吞吐量。
  2. 得到单个节点的最优线程数和消息吞吐量后，根据上下游链路的流量峰值计算出需要设置的节点数，节点数=流量峰值/单线程消息吞吐量。

## 如何解决消息堆积和延迟问题

想要快速避免消息堆积和延迟给业务带来的影响，您可以通过消息队列RocketMQ版提供的监控报警功能，设置告警规则提前预警消息堆积问题，或通过业务埋点，触发报警事件，及时监控到消息堆积问题并进行处理。设置报警规则，请参见[监控报警](https://www.alibabacloud.com/help/zh/doc-detail/180809.htm#task-1938659)。

**说明** 配置消息堆积告警规则时，请根据业务情况合理设置阈值。若阈值设置过小可能会造成频繁报警；阈值设置过大则不能及时收到报警并处理问题。

若收到消息堆积报警，处理方法，请参见[如何处理消息堆积](https://www.alibabacloud.com/help/zh/doc-detail/193952.htm#trouble-2004065)。



# 如何处理消息堆积

更新时间： 2021-04-01

## 问题现象

在使用消息队列RocketMQ版实例时收到消息堆积告警，登录[消息队列RocketMQ版控制台](https://ons.console.aliyun.com/?regionId=mq-internet-access&_k=nb9s96)后发现了下列现象：

- 在**Group 详情**页面，看到Group ID的**实时消息堆积量**的值高于预期。
- 导航栏中选择**消息轨迹**，单击**创建查询任务**，选择按**按 Message ID 查询**查询，输入对应的信息，发现部分消息已发送至Broker节点，但未投递给下游消费者。

## 可能原因

消息队列RocketMQ版的消息发送至Broker节点后，配置了Group ID的客户端根据当前的消费位点，从Broker节点拉取部分消息到本地进行消费。一般情况下，客户端从Broker节点拉取消息的过程不会导致消息堆积，主要是客户端本地消费过程中，由于消费耗时过长或消费并发度较小等原因，导致客户端消费能力不足，出现消息堆积的问题。具体的消费原理和消息堆积原因请参见[消息堆积和延迟问题](https://www.alibabacloud.com/help/zh/doc-detail/193875.htm#concept-2004064)。

## 解决方案

若出现消息堆积，可参考以下措施进行定位和处理。

1. 判断消息堆积在消息队列RocketMQ版服务端还是客户端。

   查看客户端本地日志文件`ons.log`搜索如下信息：

   ```
   the cached message count exceeds the threshold
   ```

   - 出现相关日志信息，说明客户端本地缓冲队列已满，消息堆积在客户端，请执行[步骤2](https://www.alibabacloud.com/#step-zbp-czw-m7t)。
   - 若未出现相关日志，说明消息堆积不在客户端，若出现这种特殊情况，请直接[提交工单](https://selfservice.console.aliyun.com/ticket/createIndex.htm)联系阿里云技术支持。

2. 确认消息的消费耗时是否合理。

   - 若查看到消费耗时较长，则需要查看客户端堆栈信息排查具体业务逻辑，请执行[步骤3](https://www.alibabacloud.com/#step-8l0-fak-i8b)。
   - 若查看到消费耗时正常，则有可能是因为消费并发度不够导致消息堆积，需要逐步调大消费线程或扩容节点来解决。

   消息的消费耗时可以通过以下方式查看：

   - 登录[消息队列RocketMQ版控制台](https://ons.console.aliyun.com/?regionId=mq-internet-access&_k=nb9s96)查看消息的消费轨迹，在**消费者**区域中可以看到单条消息的**消费耗时**。具体操作，请参见[查询消息轨迹](https://www.alibabacloud.com/help/zh/doc-detail/43357.htm#concept-2335151)。![查询消息轨迹](https://static-aliyun-doc.oss-accelerate.aliyuncs.com/assets/img/zh-CN/1395517061/p188530.png)
   - 登录[消息队列RocketMQ版控制台](https://ons.console.aliyun.com/?regionId=mq-internet-access&_k=nb9s96)查看消费者状态，在客户端连接信息中查看**业务处理时间**，获取消费耗时的平均值。具体操作，请参见[查看消费者状态](https://www.alibabacloud.com/help/zh/doc-detail/94312.htm#concept-2047152)。![消费状态](https://static-aliyun-doc.oss-accelerate.aliyuncs.com/assets/img/zh-CN/3999517061/p188533.png)
   - 使用[阿里云ARMS](https://www.alibabacloud.com/help/zh/product/34364.html)等其他监控产品做业务埋点采集消息的消费耗时。

3. 查看客户端堆栈信息。只需要关注线程名为ConsumeMessageThread的线程，这些都是业务消费消息的逻辑。可参见[Java官方文档](https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.State.html#RUNNABLE)判断线程的状态并根据具体问题修改业务逻辑。

   客户端堆栈信息可以通过以下方式获取：

   - 登录[消息队列RocketMQ版控制台](https://ons.console.aliyun.com/?regionId=mq-internet-access&_k=nb9s96)查看消费者状态，在客户端连接信息中查看**Java客户端堆栈信息**。具体操作，请参见[查看消费者状态](https://www.alibabacloud.com/help/zh/doc-detail/94312.htm#concept-2047152)。

   - 使用Jstack工具打印堆栈信息。

     1. 请参见[查看消费者状态](https://www.alibabacloud.com/help/zh/doc-detail/94312.htm#concept-2047152)获取消息堆积的消费者实例所对应的宿主机IP地址，并登录该宿主机。

     2. 执行以下任意命令，查看并记录Java进程的PID。

        ```
        ps -ef 
        |grep javajps -lm
        ```

     3. 执行以下命令，查看堆栈信息。

        ```
        jstack -l pid > /tmp/pid.jstack
        ```

     4. 执行以下命令，查看

        ```
        ConsumeMessageThread
        ```

        的信息。

        ```
        cat /tmp/pid.jstack|grep ConsumeMessageThread -A 10 --color
        ```

   常见的异常堆栈信息如下：

   - 示例一：空闲无堆积的堆栈。

     消费空闲情况下消费线程都会处于WAITING状态等待从消费任务队里中获取消息。

     

   - 示例二：消费逻辑有抢锁休眠等待等情况。

     消费线程阻塞在内部的一个睡眠等待上，导致消费缓慢。![堆栈示例二](https://static-aliyun-doc.oss-accelerate.aliyuncs.com/assets/img/zh-CN/4634237061/p188702.png)

   - 示例三：消费逻辑操作数据库等外部存储卡住。

     消费线程阻塞在外部的HTTP调用上，导致消费缓慢。![堆栈示例3](https://static-aliyun-doc.oss-accelerate.aliyuncs.com/assets/img/zh-CN/4634237061/p188705.png)

4. 针对某些特殊业务场景，如果消息堆积已经影响到业务运行，且堆积的消息本身可以丢弃，您可以通过重置消费位点跳过这些堆积的消息做到快速恢复。具体操作请参见[重置消费位点](https://www.alibabacloud.com/help/zh/doc-detail/63390.htm#task-2047153)。重置过程需要保证消费者客户端在线。