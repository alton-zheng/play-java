# Java Runnable vs. Callable

&nbsp;

## 1. 概览

​		自从 Java 初期以来， 多线程已经成为了语言的重要组成部分。 Runnable 为表示多线程任务提供了核心接口， Java 1.5 版本中， 添加了 $Callable$ 作为 Runnable 的升级版本。

​		在这篇文章中， 我们游览这两个接口在应用中的不同。

&nbsp;

## 2. 执行机制

​		这两个接口设计用来表示一个可以由多线程执行的任务。Runnable 任务可以使用 Thread 类或 ExecutorService 来运行， 而 Callable 对象只能用 ExecutorService 来运行。

&nbsp;

## 3. 返回值

​		让我们深入了解一下这些接口处理返回值的方式。

&nbsp;

### 3.1. Runnable

​		Runnable 接口是一个函数接口， 有一个 run() 方法，它不接受任何参数，也不返回任何值。这适用于我们不需要用到线程执行结果的情况，e.g. 传入事件日志：

```java
public interface Runnable {
    public void run();
}
```

&nbsp;

​		让我们通过一个例子来理解这一点：

```java
public class EventLoggingTask implements  Runnable{
    private Logger logger
      = LoggerFactory.getLogger(EventLoggingTask.class);

    @Override
    public void run() {
        logger.info("Message");
    }
}
```

&nbsp;

​		在这个例子中， 线程将从 queue 中读取一条信息并将其记录到日志文件中。 这个任务没有返回任何值；该任务可以使用 $ExecutorService$ 启动： 

```java
public void executeTask() {
    executorService = Executors.newSingleThreadExecutor();
    Future future = executorService.submit(new EventLoggingTask());
    executorService.shutdown();
}
```

​		在这个例子中， $Future$ 对象不持有任何值。

&nbsp; &nbsp;

### 3.2. Callable

​		$Callable$ 是包含一个 call() 方法的普通接口 - 它返回一个普通的值 V：

```java
public interface Callable<V> {
    V call() throws Exception;
}
```

&nbsp;

​		让我们来看看如何计算一个数字阶乘：

```java
public class FactorialTask implements Callable<Integer> {
    int number;

    // standard constructors

    public Integer call() throws InvalidParamaterException {
        int fact = 1;
        // ...
        for(int count = number; count > 1; count--) {
            fact = fact * count;
        }

        return fact;
    }
}
```

&nbsp;

​		$call()$ 方法的结果在 $Future$ 对象中返回： 

```java
@Test
public void whenTaskSubmitted_ThenFutureResultObtained(){
    FactorialTask task = new FactorialTask(5);
    Future<Integer> future = executorService.submit(task);
 
    assertEquals(120, future.get().intValue());
}
```

&nbsp;

## 4. 异常处理

​		让我们看看它们是否适合异常处理

&nbsp;

### 4.1. Runnable

​		因为方法签名没有指定 『throw』 子句， 所以没有办法传播进一步的 $checked$ 异常。

&nbsp;

### 4.2. Callable

​		Callable 的 $call()$ 包含了 "throws Exception" 子句， 因此我们可以很容易地进一步传播 $checked$ 异常。

```java
public class FactorialTask implements Callable<Integer> {
    // ...
    public Integer call() throws InvalidParamaterException {

        if(number < 0) {
            throw new InvalidParamaterException("Number should be positive");
        }
    // ...
    }
}
```

&nbsp;

​		在使用 ExecutorService 运行 Callable 的情况下， 异常会在 $Future$ 对象中收集， 可以通过调用 `Future.get()` 方法来检查。 这将抛出一个 ExecutionException - 它包装了原始的异常：

```java
@Test(expected = ExecutionException.class)
public void whenException_ThenCallableThrowsIt() {
 
    FactorialCallableTask task = new FactorialCallableTask(-5);
    Future<Integer> future = executorService.submit(task);
    Integer result = future.get().intValue();
}
```

&nbsp;

​		在上面的测试中，当我们传递一个无效的数字时，会抛出 `ExecutionException` 。我们可以调用这个异常对象的 $getCause()$ 方法来获得原始的 $checked$ 异常。

​		如果我们不调用 Future 的 $get()$ 方法 - 那么由 $call()$ 方法抛出的异常将不会被报告， 任务仍然标记为完成。

```java
@Test
public void whenException_ThenCallableDoesntThrowsItIfGetIsNotCalled(){
    FactorialCallableTask task = new FactorialCallableTask(-5);
    Future<Integer> future = executorService.submit(task);
 
    assertEquals(false, future.isDone());
}
```

​		即便我们已经为 *FactorialCallableTask* 的形参的负值抛出了一个异常， 上面的测试将成功通过。

&nbsp;

## 5. 总结

在这篇文章中， 我们游览了 $Runnable$ 和 $Callable$  两个接口的不同。