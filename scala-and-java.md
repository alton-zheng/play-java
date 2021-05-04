# Scala 与 Java 

概念性的东东没必要多阐述，它们都是高级编程语言中的一种。

&nbsp;

## 联系

|                    | Java                    | Scala              |
| ------------------ | ----------------------- | ------------------ |
| 底层虚拟机         | JVM                     | JVM                |
| 相互兼容           | 是                      | 是                 |
| 面向对象           | 是                      | 是                 |
| 基本类型           |                         | JVM 基本类型的包装 |
| 大数据框架底层语言 | Hadoop, HBase, Flink 等 | Spark, Kafka       |

&nbsp;

## 区别

|                        | Java        | Scala                                       |
| ---------------------- | ----------- | ------------------------------------------- |
| 优雅性                 | 模板编程    | 优雅                                        |
| 函数式编程             | 不友好      | 支持                                        |
| 接口                   | `interface` | `trait`(比 interface 功能更强)              |
| 超类                   | `Object`    | `Any(AnyVal AnyRef(类 Object 别名)，Trait)` |
| 类和方法修饰符的默认值 | `protected` | `public`                                    |
| 默认导入类             | `java.lang` | `java.lang`、`scala`、`scala.Predef`        |
| 类型系统               |             | 比 Java 丰富，更复杂，难理解，但我喜欢      |

&nbsp;

其它语言特性的小区别就不一一列出了

