# Quick Start

This quick start guide is a detailed instruction of setting up RocketMQ messaging system on your local machine to send and receive messages.

More Details:

- English：https://github.com/apache/rocketmq/tree/master/docs/en
- 中文：https://github.com/apache/rocketmq/tree/master/docs/cn

&nbsp;

# Prerequisite

The following softwares are assumed installed:

1. 64bit OS, Linux/Unix/Mac is recommended;(Windows user see guide below)
2. 64bit JDK 1.8+;
3. Maven 3.2.x;
4. Git;
5. 4g+ free disk for Broker server

&nbsp;

# Download & Build from Release

Click [here](https://www.apache.org/dyn/closer.cgi?path=rocketmq/4.8.0/rocketmq-all-4.8.0-source-release.zip) to download the 4.8.0 source release. Also you could download a binary release from [here](https://www.apache.org/dyn/closer.cgi?path=rocketmq/4.8.0/rocketmq-all-4.8.0-bin-release.zip).

Now execute the following commands to unpack 4.8.0 source release and build the binary artifact.

```bash
  > yum install -y unzip
  > unzip rocketmq-all-4.8.0-source-release.zip
  > cd rocketmq-all-4.8.0/
  > mvn -Prelease-all -DskipTests clean install -U
  > cd distribution/target/rocketmq-4.8.0/rocketmq-4.8.0
```

&nbsp;

# Linux

## Start Name Server

```bash
  > nohup sh bin/mqnamesrv &
  > tail -f ~/logs/rocketmqlogs/namesrv.log
  The Name Server boot success...
```

&nbsp;

## Start Broker

```bash
  > nohup sh bin/mqbroker -n localhost:9876 &
  > tail -f ~/logs/rocketmqlogs/broker.log 
  The broker[%s, 172.30.30.233:10911] boot success...
```

&nbsp;

## Send & Receive Messages

Before sending/receiving messages, we need to tell clients the location of name servers. RocketMQ provides multiple ways to achieve this. For simplicity, we use environment variable `NAMESRV_ADDR`

```bash
 > export NAMESRV_ADDR=localhost:9876
 > sh bin/tools.sh org.apache.rocketmq.example.quickstart.Producer
 SendResult [sendStatus=SEND_OK, msgId= ...

 > sh bin/tools.sh org.apache.rocketmq.example.quickstart.Consumer
 ConsumeMessageThread_%d Receive New Messages: [MessageExt...
```

&nbsp;

## Shutdown Servers

```
> sh bin/mqshutdown broker
The mqbroker(36695) is running...
Send shutdown request to mqbroker(36695) OK

> sh bin/mqshutdown namesrv
The mqnamesrv(36664) is running...
Send shutdown request to mqnamesrv(36664) OK
```

&nbsp;

# Windows

The guide is working for windows 10 , please make sure you have powershell installed.

Download latest binary release. and extract zip file into your local disk. Such as: `D:\rocketmq`

&nbsp;

## Add Environment Variables

You need set environment variables

1. From the desktop, right click the Computer icon.
2. Choose Properties from the context menu.
3. Click the Advanced system settings link.
4. Click Environment Variables.
5. Then add or change Environment Variables.

```log
ROCKETMQ_HOME="D:\rocketmq"
NAMESRV_ADDR="localhost:9876"
```

&nbsp;

Or just in the openning powershell, type the needed environment variables.

```
$Env:ROCKETMQ_HOME="D:\rocketmq"
$Env:NAMESRV_ADDR="localhost:9876"
```

If you choose the powershell way. you should do it for every new open powershell window.

&nbsp;

## Start Name Server

Open new powershell window, after set the correct environment variable. then change directory to rocketmq type and run:

```
.\bin\mqnamesrv.cmd
```

&nbsp;

## Start Broker

Open new powershell window, after set the correct environment variable. then change directory to rocketmq type and run:

```
.\bin\mqbroker.cmd -n localhost:9876 autoCreateTopicEnable=true
```

&nbsp;

## Send & Receive Messages

&nbsp;

### Send Messages

Open new powershell window, after set the correct environment variable. then change directory to rocketmq type and run:

```
.\bin\tools.cmd  org.apache.rocketmq.example.quickstart.Producer
```

&nbsp;

### Receive Messages

Then you will see messages produced. and now we can try consumer messages.

Open new powershell window, after set the correct environment variable. then change directory to rocketmq type and run:

```
.\bin\tools.cmd  org.apache.rocketmq.example.quickstart.Consumer
```

&nbsp;

## Shutdown Servers

Normally, you can just closed these powershell windows. (Do not do it at production environment)