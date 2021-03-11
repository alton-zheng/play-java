package structure.impl;

import org.junit.jupiter.api.*;

/**
 * @Author: alton
 * @Date: Created in 2021/3/11 5:12 PM
 * @Description:
 */
@DisplayName("BST 测试用例")
public class BSTTest {

    @BeforeAll
    public static void init() {
        System.out.println("开始执行 List 测试用例");
    }

    @AfterAll
    public static void cleanup() {
        System.out.println("用例执行完毕，清理数据");
    }

    @Nested
    @DisplayName("测试用例")
    class CaseTest {

        private BST<Integer> bst;

        @BeforeEach
        public void startCase() {

            System.out.println("==========");

            bst = new BST<>();

            int[] d = {1, 6, 10, 9, 7, 8, 4, 3, 16};

            for (int i : d) {
                bst.add(i);
            }
        }

        @AfterEach
        public void finshCase() {

            System.out.println();
            System.out.println("==========");

        }

        @DisplayName("case1: 新增数据")
        @Test
        void testFirstTest() {

            System.out.println("BST 新增数据前，大小： " + bst.size());

            int[] source = {11, 12};

            for (int i : source) {
                bst.add(i);
            }

            System.out.println("LinkedList 新增数据后， 大小： : " + bst.size());

        }

        @DisplayName("case2: 遍历数据")
        @Test
        void testSecondTest() {
            bst.preOrder();
        }

        @DisplayName("case 3: 判断元素是否存在")
        @Test
        void testThreeTest() {

            System.out.println(bst.contains(6));
            System.out.print(bst.contains(11));

        }

    }

}
