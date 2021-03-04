package structure.impl;

import org.junit.jupiter.api.*;
import structure.impl.StackSolution;

/**
 * @Author: alton
 * @Date: Created in 2021/3/3 5:35 下午
 * @Description:
 */
@DisplayName("Stack 测试用例")
public class StackSolutionTest {

    @BeforeAll
    public static void init() {
        System.out.println("开始执行 括号匹配问题 测试用例");
    }

    @AfterAll
    public static void cleanup() {
        System.out.println("用例执行完毕，清理数据");
    }

    @Nested
    @DisplayName("StackSolution 测试用例")
    class ArrayStackTest {

        StackSolution stackSolution;

        @BeforeEach
        public void startCase() {

            System.out.println("开始StackSolution 测试用例");
            stackSolution = new StackSolution();
        }

        @AfterEach
        public void finshCase() {

            System.out.println("结束 StackSolution 测试用例");
            System.out.println("==========");
        }

        @DisplayName("test1: ()")
        @Test
        void testFirstTest() {
            Assertions.assertTrue(stackSolution.isValid("()"));
        }

        @DisplayName("case2: ()[]{}")
        @Test
        void testSecondTest() {
            Assertions.assertTrue(stackSolution.isValid("()[]{}"));
        }

        @DisplayName("case3: ()]{}")
        @Test
        void testThirdTest() {
            Assertions.assertFalse(stackSolution.isValid("()]{}"));
        }
    }
}
