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
            System.out.println("LinkedList 新增数据前，大小： " + list.size());

            for (int i = 0; i < 10; i++) {
                list.add(i);
            }

            System.out.println("LinkedList 新增数据后， 大小： : " + list.size());

        }

        @AfterEach
        public void finshCase() {

            System.out.println("List 用例执行完毕");
            System.out.println("==========");
        }

        @DisplayName("case1: 打印 list ")
        @Test
        void testFirstTest() {
            System.out.println(list);
        }

        @DisplayName("case2: 添加元素，并打印")
        @Test
        void testSecondTest() {

            list.add(2, 666);
            System.out.println(list);

        }

        @DisplayName("case 3: 删除元素，并打印")
        @Test
        void testThreeTest() {

            list.remove(3);
            System.out.println(list);

        }

        @DisplayName("case 4: 清空元素，并打印")
        @Test
        void testFourTest() {
            list.clear();
            System.out.println(list);
        }

        @DisplayName("case 5: 替换元素，并打印")
        @Test
        void testFiveTest() {
            list.set(2, 666);
            System.out.println(list);
        }
    }

}

