# Vavr 的 Future 简介

&nbsp;

## **1. **简介

核心 Java 提供了用于异步计算的基本API- *Future。* *CompletableFuture* 是其最新的实现之一。

Vavr提供了它的新功能替代*Future* API。在本文中，我们将讨论新的API，并展示如何利用其一些新功能。

关于Vavr的更多文章可以在 [这里](vavr-tutorial.md) 找到。

&nbsp;

## 2. Maven 依赖

在 Future 的 API 包括在 Vavr Maven 的依赖。

因此，让我们将其添加到我们的 *pom.xml* 中：

```xml
<dependency>
    <groupId>io.vavr</groupId>
    <artifactId>vavr</artifactId>
    <version>0.9.2</version>
</dependency>
```

我们可以找到对 [Maven Central](https://search.maven.org/classic/#search|ga|1|a%3A"vavr" AND g%3A"io.vavr") 的依赖的最新版本。

&nbsp;

## 3. Vavr 的 Future

在 `Future` 可以有以下两种状态之一：

- Pending - 计算正在进行中
- Completed – 计算成功完成并显示结果，失败并显示异常或被取消

> 与核心 Java Future 相比，主要优点是我们可以轻松地以非阻塞方式注册回调和编写操作。

&nbsp;

## 4. Future 的 基本操作

### 4.1. 开始异步计算

现在，让我们看看如何使用 Vavr 启动异步计算：

```java
String initialValue = "Welcome to ";
Future<String> resultFuture = Future.of(() -> someComputation());
```

&nbsp;

### 4.2 从 Future 获取 value

我们可以通过简单地调用 *get()* 或 *getOrElse()* 方法之一来从 Future 中提取值：

```java
String result = resultFuture.getOrElse("Failed to get underlying value.");
```

差别： 

- *get()* - 是最简单的解决方案
- *getOrElse()* - 使我们能够在无法检索到 Future 的内部值的情况下，下返回默认值。

&nbsp;

建议使用 *getOrElse()*，这样我们就可以处理尝试从 *Future* 检索值时发生的任何错误。**为了简单起见，在接下来的几个示例中，我们将仅使用 *get()*

> 请注意，如果有必要等待结果，get() 方法将阻止当前线程。

&nbsp;

另一种方法是调用不阻塞任务的 `getValue()` 方法，该方法返回 `Option<Try<T>>`，**只要计算处于 Pending 状态**，该方法仅返回 empty。

然后，我们可以提取 *Try* 对象内部的计算结果：

```java
Option<Try<String>> futureOption = resultFuture.getValue();
Try<String> futureTry = futureOption.get();
String result = futureTry.get();
```

&nbsp;

有时我们需要在从 *Future* 检索值之前检查 *Future* 是否包含一个值。

我们可以简单地使用以下方法做到这一点：

```java
resultFuture.isEmpty();
```

重要的是要注意，方法 isEmpty() 如正在阻塞 – 它将阻塞线程，直到其操作完成为止。

&nbsp;

### 4.3. 更改默认的 ExecutorService

*Future* 使用 *ExecutorService* 异步运行其计算。默认的 *ExecutorService* 是 *Executors.newCachedThreadPool()*。

我们可以通过传递我们选择的实现来使用另一个 *ExecutorService*：

```java
@Test
public void whenChangeExecutorService_thenCorrect() {
    String result = Future.of(newSingleThreadExecutor(), () -> HELLO)
      .getOrElse(error);
    
    assertThat(result)
      .isEqualTo(HELLO);
}
```

&nbsp;

## 5. 完成后采取行动

API提供了*onSuccess()* 方法，该方法会在 *Future* 成功完成后立即执行操作。

同样，*onFailure()* 方法在 *Future* 失败时执行。

让我们看一个简单的例子：

```java
Future<String> resultFuture = Future.of(() -> appendData(initialValue))
  .onSuccess(v -> System.out.println("Successfully Completed - Result: " + v))
  .onFailure(v -> System.out.println("Failed - Result: " + v));
```

无论 *Future* 是否成功，*onComplete()* 方法都会接受一个将在 *Future* 完成执行后立即运行的操作。方法 *andThen()* 与 *onComplete()* 类似 – 它仅保证回调按特定顺序执行：

```java
Future<String> resultFuture = Future.of(() -> appendData(initialValue))
  .andThen(finalResult -> System.out.println("Completed - 1: " + finalResult))
  .andThen(finalResult -> System.out.println("Completed - 2: " + finalResult));
```

&nbsp;

## 6.  Future 有效操作

### **6.1. **阻塞当前线程

方法 *await()* 有两种情况：

- 如果 *Future* 处于 Pending 状态，它将阻塞当前线程，直到 Future 完成
- 如果 *Future* 完成，则立即完成。

&nbsp;

使用此方法很简单：

```java
resultFuture.await();
```

&nbsp;

### 6.2. 取消运算

我们总是可以取消计算：

```java
resultFuture.cancel();
```

&nbsp;

### 6.3. 检索基础 ExecutorService

为了获得 *Future* 使用的 *ExecutorService* ，我们可以简单地调用 *executorService()*：

```java
resultFuture.executorService();
```

&nbsp;

### 6.4. 从失败的 Future 获得信息

我们可以使用 *getCause()* 方法做到这一点，该方法返回包裹在 *io.vavr.control.Option* 对象中的 *Throwable* 。

稍后我们可以从 *Option* 对象中提取 *Throwable* 信息：

```java
@Test
public void whenDivideByZero_thenGetThrowable2() {
    Future<Integer> resultFuture = Future.of(() -> 10 / 0)
      .await();
    
    assertThat(resultFuture.getCause().get().getMessage())
      .isEqualTo("/ by zero");
}
```

&nbsp;

另外，我们可以使用 *failed()* 方法将实例转换为拥有 *Throwable* 实例的*Future*：

```java
@Test
public void whenDivideByZero_thenGetThrowable1() {
    Future<Integer> resultFuture = Future.of(() -> 10 / 0);
    
    assertThatThrownBy(resultFuture::get)
      .isInstanceOf(ArithmeticException.class);
}
```

&nbsp;

### 6.5. isCompleted(), isSuccess(), 和 isFailure()

这些方法几乎是不言自明的。他们检查 *Future* 是否完成，是否成功完成或失败。当然，它们都返回 boolean 值。

我们将在前面的示例中使用这些方法：

```java
@Test
public void whenDivideByZero_thenCorrect() {
    Future<Integer> resultFuture = Future.of(() -> 10 / 0)
      .await();
    
    assertThat(resultFuture.isCompleted()).isTrue();
    assertThat(resultFuture.isSuccess()).isFalse();
    assertThat(resultFuture.isFailure()).isTrue();
}
```

&nbsp;

### 6.6. 在 Future 上运用计算

该 map() 方法可以让我们在 pending 状态的 Future 上，应用计算：

```java
@Test
public void whenCallMap_thenCorrect() {
    Future<String> futureResult = Future.of(() -> "from Baeldung")
      .map(a -> "Hello " + a)
      .await();
    
    assertThat(futureResult.get())
      .isEqualTo("Hello from Baeldung");
}
```

&nbsp;

如果我们传递一个将 *Future* 返还给 *map()* 方法的函数，那么我们最终会得到一个嵌套的 *Future* 结构。为了避免这种情况，我们可以利用 *flatMap()* 方法：

```java
@Test
public void whenCallFlatMap_thenCorrect() {
    Future<Object> futureMap = Future.of(() -> 1)
      .flatMap((i) -> Future.of(() -> "Hello: " + i));
         
    assertThat(futureMap.get()).isEqualTo("Hello: 1");
}
```

&nbsp;

### 6.7. Future 转换

方法 *transformValue()* 可用于在 *Future* 之上应用计算，并将其内部的值更改为相同类型或不同类型的另一个值：

```java
@Test
public void whenTransform_thenCorrect() {
    Future<Object> future = Future.of(() -> 5)
      .transformValue(result -> Try.of(() -> HELLO + result.get()));
                
    assertThat(future.get()).isEqualTo(HELLO + 5);
}
```

&nbsp;

### 6.8. Future 压缩

该 API 提供了 *zip()* 方法，该方法将多个 *Future* 一起压缩到元组中 - tuple 是可能彼此相关或不相关的多个元素的集合。它们也可以是不同的类型。让我们看一个简单的例子：

```java
@Test
public void whenCallZip_thenCorrect() {
    Future<String> f1 = Future.of(() -> "hello1");
    Future<String> f2 = Future.of(() -> "hello2");
    
    assertThat(f1.zip(f2).get())
      .isEqualTo(Tuple.of("hello1", "hello2"));
}
```

这里要注意的一点是，只要至少一个基础 Future 仍未完成，则生成的 Future 将处于待处理状态。

&nbsp;

### 6.9. Future 和 CompletableFuture 相互转换

该 API 支持与 *java.util.CompletableFuture* 集成。因此，如果我们要执行的操作，只有核心 Java API 支持，我们可以很容易地一个转换 Future 到 CompletableFuture 。

让我们看看如何做到这一点：

```java
@Test
public void whenConvertToCompletableFuture_thenCorrect()
  throws Exception {
 
    CompletableFuture<String> convertedFuture = Future.of(() -> HELLO)
      .toCompletableFuture();
    
    assertThat(convertedFuture.get())
      .isEqualTo(HELLO);
}
```

我们也可以使用 fromCompletableFuture 将转换 *CompletableFuture* 到 Future 。

&nbsp;

### 6.10. 异常处理

在 *Future* 失败时，我们可以通过几种方式处理错误。

例如，我们可以利用方法 *recover* 覆盖失败信息以返回另一个结果，例如错误消息：

```java
@Test
public void whenFutureFails_thenGetErrorMessage() {
    Future<String> future = Future.of(() -> "Hello".substring(-1))
      .recover(x -> "fallback value");
    
    assertThat(future.get())
      .isEqualTo("fallback value");
}
```

&nbsp;

或者，我们可以使用 *recoverWith()* 返回另一个 *Future* 计算的结果：

```java
@Test
public void whenFutureFails_thenGetAnotherFuture() {
    Future<String> future = Future.of(() -> "Hello".substring(-1))
      .recoverWith(x -> Future.of(() -> "fallback value"));
    
    assertThat(future.get())
      .isEqualTo("fallback value");
}
```

fallbackTo() 是另一种方式来处理错误。它被称为 *Future* 并接受另一个 *Future* 作为参数。

如果第一个 *Future* 成功，则返回其结果。否则，如果第二个 *Future* 成功，则返回其结果。如果两个 *Future* 都失败，则 *failed()* 方法将返回 *Future* 的 *Throwable*，其中包含第一个 *Future* 的错误：

```java
@Test
public void whenBothFuturesFail_thenGetErrorMessage() {
    Future<String> f1 = Future.of(() -> "Hello".substring(-1));
    Future<String> f2 = Future.of(() -> "Hello".substring(-2));
    
    Future<String> errorMessageFuture = f1.fallbackTo(f2);
    Future<Throwable> errorMessage = errorMessageFuture.failed();
    
    assertThat(
      errorMessage.get().getMessage())
      .isEqualTo("String index out of range: -1");
}
```

&nbsp;

## 7. 结论

在本文中，我们了解了什么是 Future，并了解了其中的一些重要概念。我们还通过一些实际示例介绍了 API 的一些功能。