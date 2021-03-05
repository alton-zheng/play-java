package structure.impl;

import org.junit.jupiter.api.*;

/**
 * @Author: alton
 * @Date: Created in 2021/3/5 5:38 下午
 * @Description:
 */
@DisplayName("List 测试用例")
public class ListTest {

    @BeforeAll
    public static void init() {
        System.out.println("开始执行 List 测试用例");
    }

    @AfterAll
    public static void cleanup() {
        System.out.println("用例执行完毕，清理数据");
    }

    @Nested
    @DisplayName("List 测试用例")
    class LinkedListTest {

        LinkedList<Integer> list;

        @BeforeEach
        public void startCase() {

            System.out.println("开始执行 List 用例");

            list = new LinkedList<>();
        }

        @AfterEach
        public void finshCase() {

            System.out.println("List 用例执行完毕");
            System.out.println("==========");
        }

        @DisplayName("case1: 测试 LinkedList ")
        @Test
        void testFirstTest() {

            System.out.println(list.size());

            for (int i = 0; i < 10; i++) {
                list.add(i);
            }

            System.out.println("LinkedList 新增数据后: " + list.size());
            System.out.println(list);
        }

        @DisplayName("case2: ")
        @Test
        void testSecondTest() {

        }
    }

}

