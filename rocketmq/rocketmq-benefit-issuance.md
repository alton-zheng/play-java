# 权益分发

更新时间： 2020-11-12

在电商和新零售领域，权益分发适用于对特定的用户进行指定运营活动的场景。技术层面上，此种场景的DB和缓存的数据强一致性较难保证。针对该问题，阿里云消息队列RocketMQ版推出了权益分发解决方案。本文将以电商场景为例说明权益分发解决方案的背景信息、方案架构、以及方案优势等内容。

## 背景信息

在电商平台运营决定对特定用户进行营销活动时，会针对不同的用户群设置不同的营销策略，可能会涉及但不局限于以下内容：

- 用户规则：针对什么样的用户下发权限，如新用户、会员等，结合业务需求设置具体的判断条件。
- 权益类型：红包、积分或是优惠券等不同类型的权益。
- 领取成本：需要付出成本才能获取相应权益，如消耗会员积分才能领取折扣券。
- 时间控制：发放权益的时间点，如早上10:00针对新用户下发100张优惠券。
- 库存：权益的个数是否充足、与配置的用户个数是否匹配等。

这些运营数据写入DB后，转换成技术策略，写入缓存，再转换成用户发放链路数据（变成一条一条的规则）。只有符合这些规则的用户才能领取对应的权益。

## 痛点

让用户通过访问缓存数据来领取对应的权益，这样既保证了用户高效的访问，也减轻了DB的压力。但是，这就会引发新的问题：

- 当规则特别多时，DB写入缓存的数据量也会特别大，下游的缓存压力较大（此类情况较少）。
- DB写入数据至缓存时只写一次，可能会因为网络抖动等原因导致数据写入失败，进而造成数据更新不及时的结果。例如，库存原定需设置100,000，但运营策略变成10个，如果数据没有及时同步，那么就无法通知到下游，造成资损。

## 方案架构

因为消息队列RocketMQ版具有重试功能且能保证消息不丢失，所以推出此方案来确保DB和缓存数据的强一致性，即在DB和缓存间使用消息队列RocketMQ版，架构如下图所示。

![coupon-scenario](https://static-aliyun-doc.oss-cn-hangzhou.aliyuncs.com/assets/img/zh-CN/6964676951/p88027.png)

## 方案优势

消息队列RocketMQ版有以下优势：

- 巨大的数据吞吐量。
- 支持海量消息堆积，为下游应用减轻流量洪峰的冲击。
- 可以帮助简化DB和缓存间的实现，大大减少代码开发量。如果没有消息队列RocketMQ版，实现方法则十分复杂。
- 支持实时消息收发以及重试功能，确保消息不丢，从而保证消息强一致性。

## 更多信息

消息队列RocketMQ版的功能与特性详情，请参见以下文档：

- [功能与特性概述](https://www.alibabacloud.com/help/zh/doc-detail/155952.htm#concept-2435313)
- [消息重试](https://www.alibabacloud.com/help/zh/doc-detail/43490.htm#concept-2047068)