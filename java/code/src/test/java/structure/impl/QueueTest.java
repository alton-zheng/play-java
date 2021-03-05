package structure.impl;

import org.junit.jupiter.api.*;
import structure.impl.ArrayQueue;
import structure.interfaces.Queue;

import java.util.Random;

/**
 * @Author: alton
 * @Date: Created in 2021/3/3 1:45 下午
 * @Description:
 */
@DisplayName("Queue 测试用例")
public class QueueTest {

    @BeforeAll
    public static void init() {
        System.out.println("开始执行 Stack 测试用例");
    }

    @AfterAll
    public static void cleanup() {
        System.out.println("用例执行完毕，清理数据");
    }

    @Nested
    @DisplayName("Array Queue 测试用例")
    class ArrayQueueTest {

        Queue<Integer> queue;

        @BeforeEach
        public void startCase() {

            System.out.println("开始执行 Array Queue 用例");

            queue = new ArrayQueue<>();

            for (int i = 0; i < 5; i++) {
                queue.add(i);
            }
        }

        @AfterEach
        public void finshCase() {

            System.out.println("Array Queue 用例执行完毕");
            System.out.println("==========");
        }

        @DisplayName("case1: 查看队列列头元素")
        @Test
        void testFirstTest() {
            System.out.println(queue.element());
        }

        @DisplayName("case2: 删除元素")
        @Test
        void testSecondTest() {
            System.out.println(queue.poll());
            System.out.println(queue);
        }
    }

    @Nested
    @DisplayName("Loop Queue 测试用例")
    class LoopQueueTest {

        LoopQueue<Integer> queue;

        @BeforeEach
        public void startCase() {

            System.out.println("开始执行 Loop Queue 用例");

            queue = new LoopQueue<>();

            for (int i = 1; i <= 32; i++) {
                queue.add(i);
            }
        }

        @AfterEach
        public void finshCase() {

            System.out.println("Loop Queue 用例执行完毕");
            System.out.println("==========");
        }

        @DisplayName("case1: 查看队列列头元素")
        @Test
        void testFirstTest() {
            System.out.println(queue.toString());
            System.out.println(queue.getCapacity());
        }

        @DisplayName("case2: 删除和增加元素")
        @Test
        void testSecondTest() {

            for (int i = 0; i < 25; i++) {
                System.out.println(queue.remove());
            }

            System.out.println("删除元素后的队列容量: " +  queue.getCapacity());
            System.out.println("删除元素后的队列元素个数： " +  queue.size());
            System.out.println("删除元素后的队列： " + queue);

            for (int i = 0; i < 10; i++) {
                queue.offer(i);
            }

            System.out.println("添加元素后的队列容量: " +  queue.getCapacity());
            System.out.println("添加元素后的队列元素个数： " +  queue.size());
            System.out.println("添加元素后的队列： " + queue);
        }
    }

    @Nested
    @DisplayName("ArrayQueue 与 LoopQueue 性能比对")
    class CompareArrayQueueAndLoopQueue {

        Queue<Integer> queue;
        private static final int NUMS = 1000000;

        @BeforeEach
        public void startCase() {

            System.out.println("开始执行用例");
        }

        @AfterEach
        public void finshCase() {

            System.out.println("用例执行完毕");
            System.out.println("==========");
        }

        @DisplayName("case1: Array Queue 增加和删除元素消耗时间")
        @Test
        void testFirstTest() {

            long start = System.currentTimeMillis();

            queue = new ArrayQueue<>();

            Random random = new Random();
            for (int i = 0; i < NUMS; i++) {
                queue.add(random.nextInt(Integer.MAX_VALUE));
            }

            for (int i = 0; i < NUMS; i++) {
                queue.remove();
            }

            System.out.println("Array Queue cost: " + (System.currentTimeMillis() - start) + " ms");

        }

        @DisplayName("case2: Loop Queue 增加和删除元素所消耗时间")
        @Test
        void testSecondTest() {
            long start = System.currentTimeMillis();

            queue = new LoopQueue<>();

            Random random = new Random();
            for (int i = 0; i < NUMS; i++) {
                queue.add(random.nextInt(Integer.MAX_VALUE));
            }

            for (int i = 0; i < NUMS; i++) {
                queue.remove();
            }

            System.out.println("Array Queue cost: " + (System.currentTimeMillis() - start) + " ms");
        }
    }

}
