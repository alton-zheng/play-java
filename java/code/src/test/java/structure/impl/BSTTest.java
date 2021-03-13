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

            int[] d = {5, 3, 6, 8, 4, 2};

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

        @DisplayName("case2: 递归遍历数据")
        @Test
        void testSecondTest() {

            System.out.print("前序遍历(递归): ");
            bst.preOrder();
            System.out.println();

            System.out.print("前序遍历（非递归）：");
            bst.preOrderNR();
            System.out.println();

            System.out.print("中序遍历: ");
            bst.inOrder();
            System.out.println();

            System.out.print("后续遍历: ");
            bst.postOder();
            System.out.println();

            System.out.print("层级遍历（广度遍历）：");
            bst.levelOrder();
            System.out.println();

        }

        @DisplayName("case 3: 判断元素是否存在")
        @Test
        void testThreeTest() {

            System.out.println(bst.contains(6));
            System.out.print(bst.contains(11));

        }

        @DisplayName("case 4: 打印 bst")
        @Test
        void testFourTest() {

            System.out.println(bst);

        }

    }

}
