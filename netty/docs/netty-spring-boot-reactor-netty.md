# Spring Boot Reactor Netty Configuration

&nbsp;

## 1. Overview

In this tutorial, we're going to look at different configuration options for a Reactor Netty server in a Spring Boot application. In the end, we'll have an application showcasing different configuration approaches.

&nbsp;

## 2. What Is Reactor Netty ?

Before we start, let's look at what Reactor Netty is and how it relates to Spring Boot.

Reactor Netty is an [**asynchronous event-driven network application framework**](https://projectreactor.io/docs/netty/snapshot/reference/index.html#getting-started). It provides non-blocking and backpressure-ready TCP, HTTP, and UDP clients and servers. As the name implies, it's based on the [Netty framework](netty-introduction.md).

Now, let's see where Spring and Spring Boot come into the picture.

Spring WebFlux is a part of the Spring framework and provides reactive programming support for web applications. If we're using WebFlux in a Spring Boot application, **Spring Boot** **automatically configures** **Reactor Netty as the default server**. In addition to that, we can explicitly add Reactor Netty to our project, and Spring Boot should again automatically configure it.

Now, we'll build an application to learn how we can customize our auto-configured Reactor Netty server. After that, we'll cover some common configuration scenarios.

&nbsp;

## 3. Dependencies

Firstly, we'll add the required Maven dependency.

To use the Reactor Netty server, we will add the [*spring-boot-starter-webflux*](https://search.maven.org/search?q=g:org.springframework.boot a:spring-boot-starter-webflux) as a dependency in our pom file:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

This will also pull in [*spring-boot-starter-reactor-netty*](https://search.maven.org/search?q=g:org.springframework.boot a:spring-boot-starter-reactor-netty) as a transitive dependency into our project.

&nbsp;

## 4. Server Configuration

&nbsp;

### 4.1. Using Properties Files

As the first option, we can configure the Netty server through properties files. Spring Boot exposes some of the common server configurations in the *application* properties file:

Let's define the server port in *application.properties*:

```plaintext
server.port=8088
```

Or we could have done the same in *application.yml*:

```plaintext
server:
    port: 8088
```

Besides the server port, Spring Boot has many other available server configuration options. **The properties that start with the \*server\* prefix** **let us override the default server configuration**. We can easily look up these properties in the [Spring documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-application-properties.html) under the *EMBEDDED SERVER CONFIGURATION* section*.*

&nbsp;

### 4.2. Using Programmatic Configuration

Now, let's look at how **we can configure our embedded Netty server through code**. For this purpose, Spring Boot gives us the *WebServerFactoryCustomizer* and *NettyServerCustomizer* classes.

Let's use these classes to configure the Netty port as we did previously with our properties file：

```java
@Component
public class NettyWebServerFactoryPortCustomizer 
  implements WebServerFactoryCustomizer<NettyReactiveWebServerFactory> {

    @Override
    public void customize(NettyReactiveWebServerFactory serverFactory) {
        serverFactory.setPort(8088);
    }
}
```

Spring Boot will pick up our factory customizer component during startup and will configure the server port.

Alternatively, we can implement *NettyServerCustomizer*:

```java
private static class PortCustomizer implements NettyServerCustomizer {
    private final int port;

    private PortCustomizer(int port) {
        this.port = port;
    }
    @Override
    public HttpServer apply(HttpServer httpServer) {
        return httpServer.port(port);
    }
}
```

And add it to the server factory:

```java
serverFactory.addServerCustomizers(new PortCustomizer(8088));
```

These two approaches give us a lot of flexibility when configuring our embedded Reactor Netty server.

**Furthermore, we can also access the \*ServerBootstrap\* class from the Netty framework** and make our customizations there:

```java
private static class EventLoopNettyCustomizer implements NettyServerCustomizer {

    @Override
    public HttpServer apply(HttpServer httpServer) {
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup();
        return httpServer.tcpConfiguration(tcpServer -> tcpServer
          .bootstrap(serverBootstrap -> serverBootstrap
            .group(parentGroup, childGroup)
            .channel(NioServerSocketChannel.class)));
    }
}
```

&nbsp;

However, there is a caveat for this case. Since Spring Boot auto-configures the Netty server, **we may need to skip auto-configuration by explicitly defining our \*NettyReactiveWebServerFactory\* bean.**

For this purpose, we should define our bean in a configuration class and add our customizer there:

```java
@Bean
public NettyReactiveWebServerFactory nettyReactiveWebServerFactory() {
    NettyReactiveWebServerFactory webServerFactory = new NettyReactiveWebServerFactory();
    webServerFactory.addServerCustomizers(new EventLoopNettyCustomizer());
    return webServerFactory;
}
```

Next, we'll continue with some common Netty configuration scenarios.

&nbsp;

## 5. SSL Configuration

Let's see how we can configure SSL.

We'll use the *SslServerCustomizer* class which is another implementation of *NettyServerCustomizer*:

```java
@Component
public class NettyWebServerFactorySslCustomizer 
  implements WebServerFactoryCustomizer<NettyReactiveWebServerFactory> {

    @Override
    public void customize(NettyReactiveWebServerFactory serverFactory) {
        Ssl ssl = new Ssl();
        ssl.setEnabled(true);
        ssl.setKeyStore("classpath:sample.jks");
        ssl.setKeyAlias("alias");
        ssl.setKeyPassword("password");
        ssl.setKeyStorePassword("secret");
        Http2 http2 = new Http2();
        http2.setEnabled(false);
        serverFactory.addServerCustomizers(new SslServerCustomizer(ssl, http2, null));
        serverFactory.setPort(8443);
    }
}
```

Here we've defined our keystore related properties, disabled HTTP/2, and set the port to 8443.

&nbsp;

## 6. Access Log Configuration

Now, we'll look at how we can configure access logging using Logback.

Spring Boot lets us configure access logging in the application properties file for Tomcat, Jetty, and Undertow. However, Netty does not have this support just yet.

To enable Netty access logging, **we should set** ***-Dreactor.netty.http.server.accessLogEnabled=true*** when running our application:

```bash
mvn spring-boot:run -Dreactor.netty.http.server.accessLogEnabled=true
```

&nbsp;

## 7. Conclusion

In this article, we've covered how to configure the Reactor Netty server in a Spring Boot application.

Firstly, we used the general Spring Boot property-based configuration capabilities. And then, we explored how to programmatically configure Netty in a fine-grained manner.