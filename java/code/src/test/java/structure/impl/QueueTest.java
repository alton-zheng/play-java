package structure.impl;

import org.junit.jupiter.api.*;
import structure.impl.ArrayQueue;
import structure.interfaces.Queue;

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

        Queue<Integer> queue;

        @BeforeEach
        public void startCase() {

            System.out.println("开始执行 Loop Queue 用例");

            queue = new LoopQueue<>();

            for (int i = 0; i < 50; i++) {
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
        }

        @DisplayName("case2: 删除元素")
        @Test
        void testSecondTest() {
            System.out.println(queue.remove());
            System.out.println(queue.remove());
            System.out.println(queue.remove());
            System.out.println(queue.remove());
            System.out.println(queue.remove());
            System.out.println(queue);
        }
    }

}
