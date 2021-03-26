# 项目

### 项目描述

业务，周期，架构，技术，规模(人员)，角色，数据两，并发量，改进的想法，你的成长和反思。

&nbsp;

### 个人职责

技术开发职责

团队管理

架构涉及能力

测试的结果（qps, tps)

工具（禅道， jira）

难点

&nbsp;

### 难点

- jvm 调优
- sql 优化
- qps 提升
- 接口优化（响应事件长（调度链路长， 连接数少，代码冗余））

&nbsp;

### 背调

留电话（需要授权）,入职离职事件，离职原因，业务，团队规模，技术，人缘， （99% 不懂技术），岗位。社保（基数低）， 

&nbsp;

### 没有微服务

脑补，原来项目到微服务。 `controller` -> `zuul gatway`,   `dao` 层一起拆, 注册中心（ `nacos`, `zookeeper`） 配置中心

&nbsp;

![Screen Shot 2021-03-25 at 3.21.22 PM](/Users/alton/Desktop/Screen Shot 2021-03-25 at 3.21.22 PM.png)

&nbsp;

### 没有互联网经验的

前后端分离（接口安全，参数，PB, session(jwt)， swagger, 容器化 docker, k8s）, 网约车，商城（OCR）。架构 3 期 Spring cloud 。 

中间件： 

1. jvm: guava(不需要持久化), ehcache（持久化）
2. 非 jvm: redis（） ,  memcached(数据类型单一(10k 一下))， es (大量坐标位置计算)， MongoDB(非关系型数据库)。
   - 双写一致性（先写支持事务的，再写不支持事务的）

&nbsp;

文件存储中间件： fastdfs(不需要鉴别的),  hdfs; 云存储(oss（权限）， 7牛， s3),  es

&nbsp;

数据库中间件： mycat, shardingsphere(apache 顶级项目)

&nbsp;

消息中间件： Kafka,  RocketMQ,  RabbitMQ

定位： hals msg. 

成熟度： 

- RocketMQ （阿里背书，但是开源的版本，其实是阉割版的）差， 

- $RabbitMQ$： 

- Kafka/ActiveMQ: 最高

&nbsp;

优劣对比： 

- Kafka 最健全(scala， kafka 接入互联网，需要包一层)， RabbitMQ(互联网)
- 成熟团队（kafka）, bus(RabbitMQ, kafka), RocketMQ(事务消息)

&nbsp;

接入层：

- nginx
  - HAproxy
  - tengine
  - openrestry
  - 一台 可以抗 2w  并发
- lvs
  - 纯碎，干净
  - 可以抗 30 w QPS 
- keepalived
  - 分布式
- slb
- cdn（用户区域，东北，河南，重庆，广州）
- F5
  - 可以抗 80w QPS
  - 有技术支持
  - 一般公司不用，除非公司不差钱
- 去 亿级流量

&nbsp;

### 没有高并发项目

看项目

erp 在线聊天， 群发通知。

oa: 上下班打卡。

&nbsp;

### Query Per Second 和  Transaction Per Second

- 客观来说，一码事
- 比较复杂，需要事务支持的，TPS
- 纯静态，QPS
- 动态， TPS
- 合并请求

&nbsp;

分布式锁： 网约车 31

分布式id ：  架构 3期 黄老师 第一节理论课

&nbsp;



