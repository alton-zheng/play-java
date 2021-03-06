package structure.impl;

import org.junit.jupiter.api.*;
import structure.impl.ArrayStack;
import structure.interfaces.Stack;

import java.util.Random;

/**
 * @Author: alton
 * @Date: Created in 2021/3/3 1:45 下午
 * @Description:
 */
@DisplayName("Stack 测试用例")
public class StackTest {

    @BeforeAll
    public static void init() {
        System.out.println("开始执行 Stack 测试用例");
    }

    @AfterAll
    public static void cleanup() {
        System.out.println("用例执行完毕，清理数据");
    }

    @Nested
    @DisplayName("Array Stack 测试用例")
    class ArrayStackTest {

        ArrayStack<Integer> stack;

        @BeforeEach
        public void startCase() {

            System.out.println("开始执行 Array Stack 用例");

            stack = new ArrayStack<>();

            for (int i = 0; i < 5; i++) {
                stack.push(i);
            }
        }

        @AfterEach
        public void finshCase() {

            System.out.println("Array Stack 用例执行完毕");
            System.out.println("==========");
        }

        @DisplayName("test1: 打印全栈")
        @Test
        void testFirstTest() {
            System.out.println(stack);
        }

        @DisplayName("case2: 推出栈顶元素，然后打印全栈")
        @Test
        void testSecondTest() {
            stack.pop();
            System.out.println(stack);
        }
    }


    @Nested
    @DisplayName("LinkedListStack 与 ArrayStack 性能比对测试用例")
    class LinkedListStackTest {

        Stack<Integer> stack;
        private static final int NUMS = 100;

        @BeforeEach
        public void startCase() {

            System.out.println("开始执行测试用例");

        }

        @AfterEach
        public void finshCase() {

            System.out.println("Stack 用例执行完毕");
            System.out.println("==========");
        }

        @DisplayName("case1: ArrayStack 增加和删除元素所消耗时间")
        @Test
        void testFirstTest() {

            long start = System.currentTimeMillis();

            stack = new ArrayStack<>(1000);

            Random random = new Random();
            for (int i = 0; i < NUMS; i++) {
                stack.push(random.nextInt(Integer.MAX_VALUE));
            }

            System.out.println(stack.size());

            for (int i = 0; i < NUMS; i++) {
                stack.pop();
            }

            System.out.println("Array Queue cost: " + (System.currentTimeMillis() - start) + " ms");

        }

        @DisplayName("case2: LinkedListStack 增加和删除元素所消耗时间")
        @Test
        void testSecondTest() {
            long start = System.currentTimeMillis();

            stack = new LinkedListStack<>();

            Random random = new Random();
            for (int i = 0; i < NUMS; i++) {
                stack.push(random.nextInt(Integer.MAX_VALUE));
            }

            System.out.println(stack.size());

            System.out.println(stack);

            /*for (int i = 0; i < NUMS; i++) {
                stack.pop();
            }*/

            System.out.println("Array Queue cost: " + (System.currentTimeMillis() - start) + " ms");
        }
    }

}
