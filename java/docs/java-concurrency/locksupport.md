# LockSupport

```java
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class T13_TestLockSupport {
    public static void main(String[] args) {
        Thread t = new Thread(()->{
            for (int i = 0; i < 10; i++) {
                System.out.println(i);
                if(i == 5) {
                    LockSupport.park();
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();

        LockSupport.unpark(t);

        /*try {
            TimeUnit.SECONDS.sleep(8);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("after 8 senconds!");

        LockSupport.unpark(t);*/

    }
}
```

>LockSupport 可以实现暂停线程运行（停车）
>
>在需要的时候，将线程 t 重新出发
>
>unpark(t) 可以在 park 前调用，相当于给了车特权，后面可以不听指挥，不停车。
>
>比 wait notify() 更灵活



ArrayList 不是线程安全的。