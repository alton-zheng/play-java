# CompletableFuture 指南

&nbsp;

## **1. **简介

本教程是 *CompletableFuture* 类的功能和场景指南，该类是 Java 8 Concurrency API 的改进而引入的。

&nbsp;

## 2. Java 异步计算

异步计算很难推理。通常，我们希望将任何计算都视为一系列步骤，但是在异步计算的情况下，**以回调表示的动作往往分散在代码中或彼此深深地嵌套在一起**。当我们需要处理其中一个步骤中可能发生的错误时，情况变得更加糟糕。

 Future 接口是 Java 5 中添加作为异步计算的结果，但它没有任何方法去合并这些计算或处理可能出现的错误。

Java 8 引入了 *CompletableFuture* 类。除 *Future* 接口外，它还实现了 *CompletionStage* 接口。该接口为异步计算步骤定义了 contract，我们可以将其与其他步骤结合使用。

*CompletableFuture* 同时是一个 build block 和一个 framework，具有**大约 50 种不同的方法来构成，组合和执行异步计算步骤以及处理错误**。

如此庞大的 API 可能会让人不知所措，但是这些 API 大多属于几种清晰且截然不同的用例。

&nbsp;

## 3. 使用 *CompletableFuture* 作为简单的 Future

 首先，*CompletableFuture* 类实现了 *Future* 接口，因此我们可以将其用作 *Future* 实现，但需要附加完成逻辑。

例如，我们可以使用 no-arg 构造方法创建此类的实例，以表示 Future 结果，将其分发给 consumer，并在将来的某个时间使用  *complete* 方法完成该结果。consumer 可以使用 *get* 方法阻塞当前线程，直到提供此结果为止。

在下面的示例中，我们有一个方法，该方法创建一个 *CompletableFuture* 实例，然后在另一个线程中分离一些计算并立即返回 *Future* 。

&nbsp;

完成计算后，该方法通过将结果提供给 *complete* 方法来完成 *Future* ：

```java
public Future<String> calculateAsync() throws InterruptedException {
    CompletableFuture<String> completableFuture = new CompletableFuture<>();

    Executors.newCachedThreadPool().submit(() -> {
        Thread.sleep(500);
        completableFuture.complete("Hello");
        return null;
    });

    return completableFuture;
}
```

&nbsp;

为了剥离计算，我们使用 *Executor* API。这种创建和完成 *CompletableFuture* 的方法可以与任何并发机制或 API（包括原始线程）一起使用。

请注意，该 *calculateAsync* 方法返回一个 *Future* 实例。

我们只需调用该方法，接收 *Future* 实例，并在准备阻塞结果时对其调用 *get* 方法。

还要注意，*get* 方法抛出一些检查过的异常，即 *ExecutionException*（封装了在计算过程中发生的异常）和*InterruptedException*（表示执行方法的线程被中断的异常）：

```java
Future<String> completableFuture = calculateAsync();

// ... 

String result = completableFuture.get();
assertEquals("Hello", result);
```

&nbsp;

**如果我们已经知道了计算的结果**，我们可以使用静态 *completedFuture* 方法表示此计算结果。因此，*Future* 的 *get* 方法将永远不会阻塞，而是立即返回以下结果：

```java
Future<String> completableFuture = 
  CompletableFuture.completedFuture("Hello");

// ...

String result = completableFuture.get();
assertEquals("Hello", result);
```

作为替代方案，我们可能要 [**cancel Future 执行**](java-future.md)。

&nbsp;

## 4. 具有封装计算逻辑的 CompletableFuture

上面的 代码允许我们选择任何并行执行机制，但是如果我们想跳过此样板并简单地异步执行一些代码，该怎么办？

静态方法 *runAsync* 和 *supplyAsync* 允许我们相应地从 *Runnable* 和 *Supplier* 功能类型中创建 *CompletableFuture* 实例。

&nbsp;

*Runnable* 和 *Supplier* 两者都属于函数接口，允许通过得益于 Java 8 新特性的 lambda 表达式进行表达。

*Runnable* 接口与线程中使用的旧接口相同，它不允许返回值。

*Supplier* 接口是一个泛型函数接口，只有一个没有参数的方法，它返回一个参数化类型的值。

这允许我们以 *lambda* 表达式的形式提供 **Supplier** 的实例，该表达式执行计算并返回结果。它很简单:

```java
CompletableFuture<String> future
  = CompletableFuture.supplyAsync(() -> "Hello");

// ...

assertEquals("Hello", future.get());
```

&nbsp;

## 5. 异步计算结果

处理计算结果的最常见方法是将其提供给函数。 *thenApply* 方法正是这样做的; 它接受一个 *Function*  instance，使用它来处理结果，并返回一个 *Future* ，该 *Future* 保存函数返回的值:

```java
CompletableFuture<String> completableFuture
  = CompletableFuture.supplyAsync(() -> "Hello");

CompletableFuture<String> future = completableFuture
  .thenApply(s -> s + " World");

assertEquals("Hello World", future.get());
```

&nbsp;
如果我们不需要在 *Future* 链中返回值，则可以使用 *Consumer* 函数接口的实例。它的单个方法接受一个参数并返回 *void* 。

在 *CompletableFuture* 中有一种用于此场景的方法*。*该 *thenAccept* 方法接收 *Consumer* 并将其传递所述计算的结果。然后，最后的 *future.get()* 调用返回 *Void* 类型的实例：

```java
CompletableFuture<String> completableFuture
  = CompletableFuture.supplyAsync(() -> "Hello");

CompletableFuture<Void> future = completableFuture
  .thenAccept(s -> System.out.println("Computation returned: " + s));

future.get();
```

最后，如果我们既不需要计算的值，也不想在链的末端返回某个值，则可以将 *Runnable* lambda 传递给 *thenRun* 方法。在以下示例中，我们仅在调用 *future.get()* 之后在控制台中打印一行*：*

```java
CompletableFuture<String> completableFuture 
  = CompletableFuture.supplyAsync(() -> "Hello");

CompletableFuture<Void> future = completableFuture
  .thenRun(() -> System.out.println("Computation finished."));

future.get();
```

&nbsp;

## 6. 合并 Future

*CompletableFuture* API 最好的部分是 能够在一系列计算步骤中组合 CompletableFuture 实例的功能。

这种链接的结果本身就是一个 *CompletableFuture*，它允许进一步的链接和结合。这种方法在函数式语言中普遍存在，通常被称为一元设计模式。

&nbsp;

在下面的例子中，我们使用 *thenCompose* 方法将两个 Future 按顺序连接起来。

注意，这个方法接受一个返回 *CompletableFuture* 实例的函数。这个函数的参数是前一个计算步骤的结果。这允许我们在下一个 *CompletableFuture* 的 lambda 中使用这个值：

```java
CompletableFuture<String> completableFuture 
  = CompletableFuture.supplyAsync(() -> "Hello")
    .thenCompose(s -> CompletableFuture.supplyAsync(() -> s + " World"));

assertEquals("Hello World", completableFuture.get());
```

*thenCompose* 方法和 *thenApply* 一起实现了一元模式的基本构建块。它们与 Java 8 中可用的 *Stream* 和 *Optional* 类的 *map* 和 *flatMap* 方法密切相关。

这两个方法都接收一个函数并将其应用于计算结果，但 *thenCompose(flatMap)* 方法**接收一个返回另一个相同类型对象的函数**。这个功能结构允许将这些类的实例组合为构建块。

&nbsp;

如果我们想要执行两个独立的 *Future* 并使用它们的结果做一些事情，我们可以使用 *thenCombine* 方法来接受一个 *Future* 和一个带两个参数的 *Function* 来处理这两个结果:

```java
CompletableFuture<String> completableFuture 
  = CompletableFuture.supplyAsync(() -> "Hello")
    .thenCombine(CompletableFuture.supplyAsync(
      () -> " World"), (s1, s2) -> s1 + s2));

assertEquals("Hello World", completableFuture.get());
```

&nbsp;

一个更简单的场景是，当我们想对两个 *Future* 的结果进行某种处理，而无需将任何结果值传递给 *Future* 链时。该 *thenAcceptBoth* 方法都可以帮助：

```java
CompletableFuture future = CompletableFuture.supplyAsync(() -> "Hello")
  .thenAcceptBoth(CompletableFuture.supplyAsync(() -> " World"),
    (s1, s2) -> System.out.println(s1 + s2));
```

&nbsp;

## 7.  *thenApply()* 和 *thenCompose()* 区别

在前面的部分中，我们显示了有关 *thenApply()* 和 *thenCompose()* 示例。这两个 API 都有助于链接不同的*CompletableFuture* 调用，但是这两个函数的用法不同。

&nbsp;

### 7.1. *thenApply()*

**我们可以使用此方法处理上一个调用的结果。** 但是，要记住的关键一点是，返回类型将结合所有调用。

因此，当我们要转换 *CompletableFuture* 调用的结果时，此方法很有用 ：

```java
CompletableFuture<Integer> finalResult = compute().thenApply(s-> s + 1);
```

&nbsp;

### 7.2. *thenCompose()*

*thenCompose()* 方法类似于 *thenApply()*，两者都返回一个新的完成阶段。然而，thenCompose() 使用前一阶段作为参数。它将直接平铺并返回一个带有结果的 *Future*，而不是我们在 *thenApply()* 中观察到的嵌套 Future

```java
CompletableFuture<Integer> computeAnother(Integer i){
    return CompletableFuture.supplyAsync(() -> 10 + i);
}
CompletableFuture<Integer> finalResult = compute().thenCompose(this::computeAnother);
```

因此，如果想法是链接 *CompletableFuture* 方法，那么最好使用 *thenCompose()*。

另外，请注意，这两种方法之间的区别类似于 map() 和 flatMap() 之间的区别。

&nbsp;

---

## 8. 并行运行多个 Future

当我们需要并行执行多个 Future 时，我们通常要等待所有 Future 执行，然后处理它们的合并结果。

该 *CompletableFuture.allOf* 静态方法允许等待所有的完成 Future 作为一个变种 - var-arg：

```java
CompletableFuture<String> future1  
  = CompletableFuture.supplyAsync(() -> "Hello");
CompletableFuture<String> future2  
  = CompletableFuture.supplyAsync(() -> "Beautiful");
CompletableFuture<String> future3  
  = CompletableFuture.supplyAsync(() -> "World");

CompletableFuture<Void> combinedFuture 
  = CompletableFuture.allOf(future1, future2, future3);

// ...

combinedFuture.get();

assertTrue(future1.isDone());
assertTrue(future2.isDone());
assertTrue(future3.isDone());
```

&nbsp;

注意，*CompletableFuture.allof()* 的返回类型是 *CompletableFuture<Void>* 。此方法的局限性是它不返回所有 *Future* 的组合结果。相反，我们必须手动从 *Future* 中获取结果。幸运的是，*CompletableFuture.join()* 方法和 Java 8 Streams API 使它变得简单:

```java
String combined = Stream.of(future1, future2, future3)
  .map(CompletableFuture::join)
  .collect(Collectors.joining(" "));

assertEquals("Hello Beautiful World", combined);
```

该 *CompletableFuture.join()* 方法类似于 *get* 方法，但是如果 *Future* 没有正常完成，它会抛出一个未检查的异常。这样就可以将其用作 *Stream.map()* 方法中的方法引用。

&nbsp;

## 9. Error 处理

为了在异步计算步骤链中处理错误，我们必须以类似的方式适应 *throw/catch* 习惯用法。

*CompletableFuture* 类不是在语法块中捕获异常，而是使我们可以使用特殊的 *handle* 方法对其进行*处理*。此方法接收两个参数：计算结果（如果成功完成）和 引发的异常（如果某些计算步骤未正常完成）。

在下面的示例中，当问候语的异步计算由于没有提供名称而结束错误时，我们使用 *handle* 方法提供默认值：

```java
String name = null;

// ...

CompletableFuture<String> completableFuture  
  =  CompletableFuture.supplyAsync(() -> {
      if (name == null) {
          throw new RuntimeException("Computation error!");
      }
      return "Hello, " + name;
  })}).handle((s, t) -> s != null ? s : "Hello, Stranger!");

assertEquals("Hello, Stranger!", completableFuture.get());
```

&nbsp;

作为另一种场景，假设我们想手动使用一个值来完成 *Future*，就像第一个示例中那样，但是我们也能够使用一个异常来完成它。*completeExceptionly* 方法就是为此而设计的。下面的例子中的 *completableFuture.get()* 方法抛出一个 *ExecutionException* 和一个 *RuntimeException* 作为其原因：

```java
CompletableFuture<String> completableFuture = new CompletableFuture<>();

// ...

completableFuture.completeExceptionally(
  new RuntimeException("Calculation failed!"));

// ...

completableFuture.get(); // ExecutionException
```

在上面的示例中，我们可以使用 *handle* 方法异步处理异常，但是通过 *get* 方法，我们可以使用更典型的同步异常处理方法。

&nbsp;

## 10. 异步方法

*CompletableFuture* 类中的大多数 fluent API 方法都有两个带有 *Async* 后缀的附加变量。这些方法通常用于在另一个线程中运行相应的执行步骤。

没有 *Async* 后缀的方法使用调用线程运行下一个执行阶段。相反，不带 *Executor* 参数的 *Async* 方法使用 *Executor* 的公共 fork/join pool 实现来运行步骤，该实现是通过 *`ForkJoinPool.commonPool()`* 方法访问的。最后，带有 *Executor* 参数的 *Async* 方法使用传递的 *Executor* 运行一个步骤。

下面是一个经过修改的示例，它使用一个 *Function* 实例来处理计算结果。唯一可见的区别是 *thenApplyAsync* 方法，但在内部，函数的应用被包装成 *ForkJoinTask* 实例(更多关于 *fork/join* 框架的信息，参见文章 [" Java fork/join框架指南"](java-fork-join.md))。这使得我们可以更加并行地进行计算，并更有效地使用系统资源:

```java
CompletableFuture<String> completableFuture  
  = CompletableFuture.supplyAsync(() -> "Hello");

CompletableFuture<String> future = completableFuture
  .thenApplyAsync(s -> s + " World");

assertEquals("Hello World", future.get());
```

&nbsp;

## 11. JDK 9 *CompletableFuture* API

Java 9通过以下更改增强了*CompletableFuture* API：

- 添加了新的工厂方法
- 支持延迟和超时
- 改进了对子类的支持

&nbsp;

和新的实例 API：

- *Executor defaultExecutor()*
- *CompletableFuture<U> newIncompleteFuture()*
- *CompletableFuture<T> copy()*
- *CompletionStage<T> minimalCompletionStage()*
- *CompletableFuture<T> completeAsync(Supplier<? extends T> supplier, Executor executor)*
- *CompletableFuture<T> completeAsync(Supplier<? extends T> supplier)*
- *CompletableFuture<T> orTimeout(long timeout, TimeUnit unit)*
- *CompletableFuture<T> completeOnTimeout(T value, long timeout, TimeUnit unit)*

&nbsp;
然后，还有一些静态实用程序方法：

- *Executor delayedExecutor(long delay, TimeUnit unit, Executor executor)*
- *Executor delayedExecutor(long delay, TimeUnit unit)*
- *<U> CompletionStage<U> completedStage(U value)*
- *<U> CompletionStage<U> failedStage(Throwable ex)*
- *<U> CompletableFuture<U> failedFuture(Throwable ex)*

&nbsp;

最后，为了解决超时问题，Java 9引入了另外两个新功能：

- *orTimeout()*
- *completeOnTimeout()*

以下是详细的文章，进一步阅读： [Java 9 CompletableFuture API改进](java-9-completablefuture.md)。

&nbsp;

## 12. 小节

在本文中，我们描述了*CompletableFuture* 类的方法和典型场景。

