# Java Microbenchmarking

&nbsp;

## 1.  介绍

本文主要关注 JMH (Java Microbenchmark 工具)。首先，我们要熟悉 API 并学习它的基础知识。然后我们会看到一些在编写 microbenchmark 时应该考虑的最佳实践。

简而言之，JMH 负责 JVM `warm-up` 和 `code-optimization` 路径等工作，使 benchmark 尽可能简单。

&nbsp;

## 2. 开始

首先，我们可以继续使用 Java 8，并简单地定义依赖项:

```xml
<dependency>
    <groupId>org.openjdk.jmh</groupId>
    <artifactId>jmh-core</artifactId>
    <version>1.28</version>
</dependency>

<dependency>
    <groupId>org.openjdk.jmh</groupId>
    <artifactId>jmh-generator-annprocess</artifactId>
    <version>1.28</version>
</dependency>
```

&nbsp;

在 Maven 中央仓库可以找到最新版本的 [JMH Core](https://search.maven.org/classic/#artifactdetails|org.openjdk.jmh| JMH - Core |1.19|jar) 和 [JMH Annotation Processor](https://search.maven.org/classic/#artifactdetails|org.openjdk.jmh| JMH -generator-annprocess|1.19|jar) 。

接下来，通过使用 *@Benchmark* 注解(在任何公共类中)创建一个简单的 benchmark ：

```java
@Benchmark
public void init() {
    // Do nothing
}
```

&nbsp;

然后我们添加启动 benchmark 过程的main 方法:

```java
public class BenchmarkRunner {
    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
}
```

&nbsp;

现在运行 BenchmarkRunner 将执行我们可能有些无用的 benchmark。一旦运行完成，就会显示一个汇总表：

```diff
# Run complete. Total time: 00:06:45
Benchmark      Mode  Cnt Score            Error        Units
BenchMark.init thrpt 200 3099210741.962 ± 17510507.589 ops/s
```

&nbsp;

## 3. Benchmark 类型

JMH 支持一些可能的 benchmark : *Throughput，* *AverageTime*， *SampleTime*， 和 *SingleShotTime* 。这些可以通过 *@BenchmarkMode* 注解配置：

```java
@Benchmark
@BenchmarkMode(Mode.AverageTime)
public void init() {
    // Do nothing
}
```

&nbsp;

结果表将有一个 `average time` 指标(而不是 `throughput`)：

```diff
# Run complete. Total time: 00:00:40
Benchmark Mode Cnt  Score Error Units
BenchMark.init avgt 20 ≈ 10⁻⁹ s/op
```

&nbsp;

## 4. 配置 Warmup 和 Execution

通过使用 *@Fork* 注释，我们可以设置 benchmark 是如何执行的 : *value* 参数控制 benchmark 将执行多少次，*warmup* 参数控制在收集结果之前 benchmark 将要运行多少次，例如:

```java
@Benchmark
@Fork(value = 1, warmups = 2)
@BenchmarkMode(Mode.Throughput)
public void init() {
    // Do nothing
}
```

&nbsp;

这指示 JMH 在进行实时 benchmark 之前运行两个 `warn-up` fork  并丢弃结果。

同样，@Warnup 注解可以用来控制 warnup 迭代的数量。例如，*@Warnup(iterations = 5)* 告诉 JMH 五个 warn-up iteration 就足够了，而不是默认的 20 个。

&nbsp;

## 5. State

现在，让我们来看看如何利用 *State* 来执行一个不那么琐碎且更具指示性的测试 hash algorithm 的任务。假设我们决定通过对密码哈希几百次来为密码数据库增加额外的保护，以防止字典攻击。

我们可以通过使用 *State* 对象来探究性能影响:

```java
@State(Scope.Benchmark)
public class ExecutionPlan {

    @Param({ "100", "200", "300", "500", "1000" })
    public int iterations;

    public Hasher murmur3;

    public String password = "4v3rys3kur3p455w0rd";

    @Setup(Level.Invocation)
    public void setUp() {
        murmur3 = Hashing.murmur3_128().newHasher();
    }
}
```

&nbsp;

这样我们的 benchmark 看起来就像:

```java
@Fork(value = 1, warmups = 1)
@Benchmark
@BenchmarkMode(Mode.Throughput)
public void benchMurmur3_128(ExecutionPlan plan) {

    for (int i = plan.iterations; i > 0; i--) {
        plan.murmur3.putString(plan.password, Charset.defaultCharset());
    }

    plan.murmur3.hash();
}
```

&nbsp;

在这里，当 JMH 将字段 *iterations* 传递给 benchmark 方法时，它将由 *@Param* 注释中的适当值填充。在每次基准调用之前都会调用带 *@Setup* 注释的方法，并创建一个新的 `Hasher` 以确保隔离。

当执行完成后，我们将得到类似下面的结果:

```diff
# Run complete. Total time: 00:06:47

Benchmark                   (iterations)   Mode  Cnt      Score      Error  Units
BenchMark.benchMurmur3_128           100  thrpt   20  92463.622 ± 1672.227  ops/s
BenchMark.benchMurmur3_128           200  thrpt   20  39737.532 ± 5294.200  ops/s
BenchMark.benchMurmur3_128           300  thrpt   20  30381.144 ±  614.500  ops/s
BenchMark.benchMurmur3_128           500  thrpt   20  18315.211 ±  222.534  ops/s
BenchMark.benchMurmur3_128          1000  thrpt   20   8960.008 ±  658.524  ops/s
```

&nbsp;

## 6. Dead Code 消除

在运行 `microbenchmark` 时，了解 optimization 是非常重要的。否则，它们可能会以一种非常误导性的方式影响 benchmark 结果。

为了让问题更加具体，让我们考虑一个例子:

```java
@Benchmark
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public void doNothing() {
}

@Benchmark
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public void objectCreation() {
    new Object();
}
```

&nbsp;

我们期望对象分配的 cost 比什么都不做要高。然而，如果我们运行 benchmark:

```bash
Benchmark                 Mode  Cnt  Score   Error  Units
BenchMark.doNothing       avgt   40  0.609 ± 0.006  ns/op
BenchMark.objectCreation  avgt   40  0.613 ± 0.007  ns/op
```

&nbsp;

显然，在 [TLAB](https://alidg.me/blog/2019/6/21/tlab-jvm) 中找到一个位置，创建和初始化对象几乎是免费的! 仅仅通过看这些数字，我们就应该知道这里有些东西不太合理。

在这里，我们是死代码消除的受害者。Compiler 非常善于优化多余的代码。事实上，这正是 JIT 编译器在这里所做的。

**为了防止这种优化，我们应该以某种方式欺骗编译器，让它认为代码被其他一些组件使用。** 实现这一点的一种方法是返回创建的对象：

```java
@Benchmark
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public Object pillarsOfCreation() {
    return new Object();
}
```

&nbsp;

同样，我们也可以让 *[Blackhole](http://javadox.com/org.openjdk.jmh/jmh-core/1.6.3/org/openjdk/jmh/infra/Blackhole.html)* 消耗它:

```java
@Benchmark
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public void blackHole(Blackhole blackhole) {
    blackhole.consume(new Object());
}
```

&nbsp;

**让 `Blackhole` 消耗对象是一种说服 JIT 编译器不应用死代码消除优化的方法** 。无论如何，如果我们再次运行这些基准，这些数字将更有意义： 

```bash
Benchmark                    Mode  Cnt  Score   Error  Units
BenchMark.blackHole          avgt   20  4.126 ± 0.173  ns/op
BenchMark.doNothing          avgt   20  0.639 ± 0.012  ns/op
BenchMark.objectCreation     avgt   20  0.635 ± 0.011  ns/op
BenchMark.pillarsOfCreation  avgt   20  4.061 ± 0.037  ns/op
```

&nbsp;

## 7. 常数 Fold

让我们考虑另一个例子:

```java
@Benchmark
public double foldedLog() {
    int x = 8;

    return Math.log(x);
}
```

**基于常量的计算可能会返回完全相同的输出，而不管执行多少次。** 因此，JIT 编译器很有可能会用它的结果替换对数函数（logarithm function）调用： 

```java
@Benchmark
public double foldedLog() {
    return 2.0794415416798357;
}
```

&nbsp;

这种形式的部分求值称为常数折叠。在这种情况下，常数折叠完全避免了 Math.log 调用，这是整个 benchmark 的重点。

为了防止常数 fold，我们可以将常量状态封装在一个 state 对象中:

```java
@State(Scope.Benchmark)
public static class Log {
    public int x = 8;
}

@Benchmark
public double log(Log input) {
     return Math.log(input.x);
}
```

&nbsp;

如果我们互相运行这些 benchmark：

```java
Benchmark             Mode  Cnt          Score          Error  Units
BenchMark.foldedLog  thrpt   20  449313097.433 ± 11850214.900  ops/s
BenchMark.log        thrpt   20   35317997.064 ±   604370.461  ops/s
```

&nbsp;

显然，与 *foldedLog* 相比，*log*  benchmark 做了一些重要的工作，这是明智的。

&nbsp;

## 8. 代码

```java
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

public class BenchMark {

    @State(Scope.Benchmark)
    public static class Log {
        public int x = 8;
    }

    @State(Scope.Benchmark)
    public static class ExecutionPlan {

        @Param({ "100", "200", "300", "500", "1000" })
        public int iterations;

        public Hasher murmur3;

        public String password = "4v3rys3kur3p455w0rd";

        @Setup(Level.Invocation)
        public void setUp() {
            murmur3 = Hashing.murmur3_128().newHasher();
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @Warmup(iterations = 5)
    public void benchMurmur3_128(ExecutionPlan plan) {

        for (int i = plan.iterations; i > 0; i--) {
            plan.murmur3.putString(plan.password, Charset.defaultCharset());
        }

        plan.murmur3.hash();
    }

    @Benchmark
    @Fork(value = 1, warmups = 1)
    @BenchmarkMode(Mode.Throughput)
    public void init() {
        // Do nothing
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void doNothing() {

    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void objectCreation() {
        new Object();
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public Object pillarsOfCreation() {
        return new Object();
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void blackHole(Blackhole blackhole) {
        blackhole.consume(new Object());
    }

    @Benchmark
    public double foldedLog() {
        int x = 8;

        return Math.log(x);
    }

    @Benchmark
    public double log(Log input) {
        return Math.log(input.x);
    }

}
```

&nbsp;

## 9. 总结

本教程重点介绍并展示了 Java 的 benchmark 工具。

