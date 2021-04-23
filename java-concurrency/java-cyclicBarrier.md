# CyclicBarrier

> - 满人发车
> - 单栅栏

```java
CyclicBarrier barrier = new CyclicBarrier(20, () -> System.out.println("满人"));

for (int i = 0; i < 100; i++) {
  new Thread(() ->
            try {
              barrier.await();
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            )
}

barrier.await();
```

&nbsp;

## 应用场景

>  CyclicBarrier
>
> - 栅栏
> - 复杂操作
>   - 数据库
>   - 网络
>   - 文件
> - 并发执行时，可以用 CyclicBarrier

&nbsp;

```java
public class T07_TestCyclicBarrier {
    public static void main(String[] args) {
        //CyclicBarrier barrier = new CyclicBarrier(20);

        CyclicBarrier barrier = new CyclicBarrier(20, () -> System.out.println(""));

        /*CyclicBarrier barrier = new CyclicBarrier(20, new Runnable() {
            @Override
            public void run() {
                System.out.println("ˣ");
            }
        });*/

        for(int i=0; i<100; i++) {

                new Thread(()->{
                    try {
                        barrier.await();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }).start();
            
        }
    }
}
```

