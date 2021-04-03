# Vavr 的 Future 简介

&nbsp;

## **1. **简介

Core Java provides a basic API for asynchronous computations – *Future.* *CompletableFuture* is one of its newest implementations.

Vavr provides its new functional alternative to the *Future* API. In this article, we'll discuss the new API and show how to make use of some of its new features.

More articles on Vavr can be found [here](https://www.baeldung.com/vavr-tutorial).

核心Java提供了用于异步计算的基本API- *Future。* *CompletableFuture*是其最新的实现之一。

Vavr提供了它的新功能替代*Future* API。在本文中，我们将讨论新的API，并展示如何利用其一些新功能。

关于Vavr的更多文章可以在[这里](https://www.baeldung.com/vavr-tutorial)找到。

&nbsp;

## **2. **Maven 依赖

The *Future* API is included in the Vavr Maven dependency.

So, let's add it to our *pom.xml*:

在*未来的*API包括在Vavr Maven的依赖。

因此，让我们将其添加到我们的*pom.xml中*：

```xml
<dependency>
    <groupId>io.vavr</groupId>
    <artifactId>vavr</artifactId>
    <version>0.9.2</version>
</dependency>
```

We can find the latest version of the dependency on [Maven Central](https://search.maven.org/classic/#search|ga|1|a%3A"vavr" AND g%3A"io.vavr").我们可以找到对[Maven Central](https://search.maven.org/classic/#search|ga|1|a%3A"vavr" AND g%3A"io.vavr")的依赖的最新版本。



## 3. Vavr 的 Future

**The \*Future\* can be in one of two states:**

- Pending – the computation is ongoing
- Completed – the computation finished successfully with a result, failed with an exception or was canceled

**The main advantage over the core Java \*Future\* is that we can easily register callbacks and compose operations in a non-blocking way.**

**在\*未来\*可以有两种状态之一：**

- 待定-计算正在进行中
- 已完成–计算成功完成并显示结果，失败并显示异常或被取消

**与核心Java \*Future\*相比，主要优点是我们可以轻松地以非阻塞方式注册回调和编写操作。**

&nbsp;

## 4. Future 的基本操作

### **4.1. Starting Asynchronous Computations**

Now, let's see how we can start asynchronous computations using Vavr:

现在，让我们看看如何使用Vavr启动异步计算：

```java
String initialValue = "Welcome to ";
Future<String> resultFuture = Future.of(() -> someComputation());
```

### 

### 4.2 从 Future 获取 value

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_0" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_0" width="336" height="280" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" sandbox="allow-forms allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts allow-top-navigation-by-user-activation" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="d" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

We can extract values from a *Future* by simply calling one of the *get()* or *getOrElse()* methods:

我们可以通过简单地调用*get（）*或*getOrElse（）*方法之一来从*Future中*提取值：

```java
String result = resultFuture.getOrElse("Failed to get underlying value.");
```

The difference between *get()* and *getOrElse()* is that *get()* is the simplest solution, while *getOrElse()* enables us to return a value of any type in case we weren't able to retrieve the value inside the *Future*.

It's recommended to use *getOrElse()* so we can handle any errors that occur while trying to retrieve the value from a *Future*. **For the sake of simplicity, we'll just use \*get()\* in the next few examples.**

Note that the *get()* method blocks the current thread if it's necessary to wait for the result.

A different approach is to call the nonblocking *getValue()* method, which returns an *Option<Try<T>>* which **will be empty as long as computation is pending.**

We can then extract the computation result which is inside the *Try* object:

之间的差别*的get（）*和*getOrElse（）*是*get（）方法*是最简单的解决方案，同时*getOrElse（）*使我们能够在情况下返回任何类型的值，我们无法检索内部值*未来*。

建议使用*getOrElse（），*这样我们就可以处理尝试从*Future*检索值时发生的任何错误。**为了简单起见，在接下来的几个示例中，我们将仅使用\*get（）\*。**

请注意，如果有必要等待结果，*get（）*方法将阻止当前线程。

另一种方法是调用nonblocking *getValue（）*方法，该方法返回*Option <Try <T >>*，**只要计算未决**，该*选项***将为空。**

然后，我们可以提取*Try*对象内部的计算结果：

```java
Option<Try<String>> futureOption = resultFuture.getValue();
Try<String> futureTry = futureOption.get();
String result = futureTry.get();
```

Sometimes we need to check if the *Future* contains a value before retrieving values from it.

We can simply do that by using:

有时我们需要在从*Future*检索值之前检查*Future是否*包含一个值。

我们可以简单地使用以下方法做到这一点：

```java
resultFuture.isEmpty();
```

It's important to note that the method *isEmpty()* is blocking – it will block the thread until its operation is finished.

重要的是要注意，方法*isEmpty（）*正在阻塞–它将阻塞线程，直到其操作完成为止。

&nbsp;

### 4.3. 更改默认的 ExecutorService

*Futures* use an *ExecutorService* to run their computations asynchronously. The default *ExecutorService* is *Executors.newCachedThreadPool()*.

We can use another *ExecutorService* by passing an implementation of our choice:

*期货*使用*ExecutorService*异步运行其计算。默认的*ExecutorService*是*Executors.newCachedThreadPool（）*。

我们可以通过传递我们选择的实现来使用另一个*ExecutorService*：

```java
@Test
public void whenChangeExecutorService_thenCorrect() {
    String result = Future.of(newSingleThreadExecutor(), () -> HELLO)
      .getOrElse(error);
    
    assertThat(result)
      .isEqualTo(HELLO);
}
```

## **5. Performing Actions Upon Completion**

The API provides the *onSuccess()* method which performs an action as soon as the *Future* completes successfully.

Similarly, the method *onFailure()* is executed upon the failure of the *Future*.

Let's see a quick example:

API提供了*onSuccess（）*方法，该方法会在*Future*成功完成后立即执行操作。

同样，*onFailure（）*方法在*Future*失败时执行。

让我们看一个简单的例子：

```java
Future<String> resultFuture = Future.of(() -> appendData(initialValue))
  .onSuccess(v -> System.out.println("Successfully Completed - Result: " + v))
  .onFailure(v -> System.out.println("Failed - Result: " + v));
```

The method *onComplete()* accepts an action to be run as soon as the *Future* has completed its execution, whether or not the *Future* was successful. The method *andThen()* is similar to *onComplete()* – it just guarantees the callbacks are executed in a specific order:

无论*Future*是否成功，*onComplete（）*方法都会接受一个将在*Future*完成执行后立即运行的操作。方法*andThen（）*与*onComplete（）*类似–它仅保证回调按特定顺序执行：

```java
Future<String> resultFuture = Future.of(() -> appendData(initialValue))
  .andThen(finalResult -> System.out.println("Completed - 1: " + finalResult))
  .andThen(finalResult -> System.out.println("Completed - 2: " + finalResult));
```

## **6. Useful Operations on \*Futures\***

### **6.1. **阻塞当前线程**

The method *await()* has two cases:

- if the *Future* is pending, it blocks the current thread until the Future has completed
- if the *Future* is completed, it finishes immediately

Using this method is straightforward:

方法*await（）*有两种情况：

- 如果*Future*未决，它将阻塞当前线程，直到Future完成
- 如果*未来*完成，则立即完成

使用此方法很简单：

```java
resultFuture.await();
```

### **6.2. Canceling a Computation**

We can always cancel the computation:

我们总是可以取消计算：

```java
resultFuture.cancel();
```

&nbsp;

### **6.3. **检索基础\*执行器服务\***

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_2" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_2" width="300" height="250" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="f" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

To obtain the *ExecutorService* that is used by a *Future*, we can simply call *executorService()*:

为了获得*Future*使用的*ExecutorService*，我们可以简单地调用*executorService（）*：

```java
resultFuture.executorService();
```

&nbsp;

### 6.4. **从失败的\*未来中\*获得可\*投掷的东西\***

We can do that using the *getCause()* method which returns the *Throwable* wrapped in an *io.vavr.control.Option* object.

We can later extract the *Throwable* from the *Option* object:

我们可以使用*getCause（）*方法做到这一点，该方法返回包裹在*io.vavr.control.Option*对象中的*Throwable*。

稍后我们可以从*Option*对象中提取*Throwable*：

```java
@Test
public void whenDivideByZero_thenGetThrowable2() {
    Future<Integer> resultFuture = Future.of(() -> 10 / 0)
      .await();
    
    assertThat(resultFuture.getCause().get().getMessage())
      .isEqualTo("/ by zero");
}
```

Additionally, we can convert our instance to a *Future* holding a *Throwable* instance using the *failed()* method:

另外，我们可以使用*failed（）*方法将实例转换为拥有*Throwable*实例的*Future*：

```

```

```java
@Test
public void whenDivideByZero_thenGetThrowable1() {
    Future<Integer> resultFuture = Future.of(() -> 10 / 0);
    
    assertThatThrownBy(resultFuture::get)
      .isInstanceOf(ArithmeticException.class);
}
```

### **6.5. \*isCompleted(), isSuccess(),\* and \*isFailure()\***

These methods are pretty much self-explanatory. They check if a *Future* completed, whether it completed successfully or with a failure. All of them return *boolean* values, of course.

We're going to use these methods with the previous example:

这些方法几乎是不言自明的。他们检查*Future*是否完成，是否成功完成或失败。当然，它们都返回*布尔*值。

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

### **6.6. Applying Computations on Top of a Future**

### **在未来应用计算**

The *map()* method allows us to apply a computation on top of a pending *Future:*

该*图（）*方法可以让我们在挂起之上应用计算*的未来：*

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

If we pass a function that returns a *Future* to the *map()* method, we can end up with a nested *Future* structure. To avoid this, we can leverage the *flatMap()* method:

如果我们传递一个将*Future*返还给*map（）*方法的函数，那么我们最终会得到一个嵌套的*Future*结构。为了避免这种情况，我们可以利用*flatMap（）*方法：

```java
@Test
public void whenCallFlatMap_thenCorrect() {
    Future<Object> futureMap = Future.of(() -> 1)
      .flatMap((i) -> Future.of(() -> "Hello: " + i));
         
    assertThat(futureMap.get()).isEqualTo("Hello: 1");
}
```

### **6.7. Transforming \*Futures\***

The method *transformValue()* can be used to apply a computation on top of a *Future* and change the value inside it to another value of the same type or a different type:

方法*transformValue（）*可用于在*Future*之上应用计算，并将其内部的值更改为相同类型或不同类型的另一个值：

```java
@Test
public void whenTransform_thenCorrect() {
    Future<Object> future = Future.of(() -> 5)
      .transformValue(result -> Try.of(() -> HELLO + result.get()));
                
    assertThat(future.get()).isEqualTo(HELLO + 5);
}
```

### **6.8. Zipping \*Futures\***

<iframe frameborder="0" src="https://839848466f63d68113810f4a93120cc6.safeframe.googlesyndication.com/safeframe/1-0-38/html/container.html" id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_3" title="3rd party ad content" name="" scrolling="no" marginwidth="0" marginheight="0" width="728" height="90" data-is-safeframe="true" sandbox="allow-forms allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts allow-top-navigation-by-user-activation" allow="conversion-measurement ‘src’" data-google-container-id="g" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

The API provides the *zip()* method which zips *Futures* together into tuples – a tuple is a collection of several elements that may or may not be related to each other. They can also be of different types. Let's see a quick example:

该API提供了*zip（）*方法，该方法将*Futures*一起*压缩*到元组中-元组是可能彼此相关或不相关的多个元素的集合。它们也可以是不同的类型。让我们看一个简单的例子：

```java
@Test
public void whenCallZip_thenCorrect() {
    Future<String> f1 = Future.of(() -> "hello1");
    Future<String> f2 = Future.of(() -> "hello2");
    
    assertThat(f1.zip(f2).get())
      .isEqualTo(Tuple.of("hello1", "hello2"));
}
```

The point to note here is that the resulting *Future* will be pending as long as at least one of the base *Futures* is still pending.

这里要注意的一点是，只要至少一个基础*期货*仍未完成，则生成的*期货*将处于待处理状态。

### **6.9. Conversion Between \*Futures\* and \*CompletableFutures\***

The API supports integration with *java.util.CompletableFuture*. So, we can easily convert a *Future* to a *CompletableFuture* if we want to perform operations that only the core Java API supports.

Let's see how we can do that:

该API支持与*java.util.CompletableFuture*集成。因此，我们可以很容易地一个转换*未来*的*CompletableFuture*如果我们要执行的操作，只有核心Java API支持。

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

We can also convert a *CompletableFuture* to a *Future* using the *fromCompletableFuture()* method.

我们也可以将转换*CompletableFuture*到*未来*使用*fromCompletableFuture（）*方法。

### **6.10. Exception Handling**

Upon the failure of a *Future*, we can handle the error in a few ways.

For example, we can make use of the method *recover()* to return another result, such as an error message:

在*Future*失败时，我们可以通过几种方式处理错误。

例如，我们可以利用方法*restore（）*返回另一个结果，例如错误消息：

```java
@Test
public void whenFutureFails_thenGetErrorMessage() {
    Future<String> future = Future.of(() -> "Hello".substring(-1))
      .recover(x -> "fallback value");
    
    assertThat(future.get())
      .isEqualTo("fallback value");
}
```

Or, we can return the result of another *Future* computation using *recoverWith()*:

或者，我们可以使用*restoreWith（）*返回另一个*Future*计算的结果：

```java
@Test
public void whenFutureFails_thenGetAnotherFuture() {
    Future<String> future = Future.of(() -> "Hello".substring(-1))
      .recoverWith(x -> Future.of(() -> "fallback value"));
    
    assertThat(future.get())
      .isEqualTo("fallback value");
}
```

The method *fallbackTo()* is another way to handle errors. It's called on a *Future* and accepts another *Future* as a parameter.

该方法*fallbackTo（）*是另一种方式来处理错误。它被称为*Future*并接受另一个*Future*作为参数。

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_4" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_4" width="336" height="280" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" sandbox="allow-forms allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts allow-top-navigation-by-user-activation" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="h" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

If the first *Future* is successful, then it returns its result. Otherwise, if the second *Future* is successful, then it returns its result. If both *Futures* fail, then the *failed()* method returns a *Future* of a *Throwable*, which holds the error of the first *Future*:

如果第一个*Future*成功，则返回其结果。否则，如果第二个*Future*成功，则返回其结果。如果两个*Future都*失败，则*failed（）*方法将返回*Future*的*Throwable*，其中包含第一个*Future*的错误：

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

In this article, we've seen what a *Future* is and learned some of its important concepts. We've also walked through some of the features of the API using a few practical examples.

The full version of the code is available [over on GitHub](https://github.com/eugenp/tutorials/tree/master/vavr).

在本文中，我们了解了什么是*未来*，并了解了其中的一些重要概念。我们还通过一些实际示例介绍了API的一些功能。

完整版本的代码可[在GitHub上获得](https://github.com/eugenp/tutorials/tree/master/vavr)。