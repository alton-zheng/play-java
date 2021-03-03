package structure;

import org.junit.jupiter.api.*;

/**
 * @Author: alton
 * @Date: Created in 2021/3/3 1:45 下午
 * @Description:
 */
@DisplayName("Stack 测试用例")
public class StackTestCase {

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
    @DisplayName("Array Stack 测试用例2")
    class ArrayStackTest2 {

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


}
