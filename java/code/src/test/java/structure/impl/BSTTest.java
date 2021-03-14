package structure.impl;

import org.junit.jupiter.api.*;

/**
 * @Author: alton
 * @Date: Created in 2021/3/11 5:12 PM
 * @Description:
 */
@DisplayName("BST Test Case")
public class BSTTest {

    @BeforeAll
    public static void init() {
        System.out.println("started");
    }

    @AfterAll
    public static void cleanup() {
        System.out.println("finished");
    }

    @Nested
    @DisplayName("test case")
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

        @DisplayName("case1: add elements")
        @Test
        void testFirstTest() {

            System.out.println("BST size before adding elements: " + bst.size());

            int[] source = {11, 12};

            for (int i : source) {
                bst.add(i);
            }

            System.out.println("BST size ater adding elements: " + bst.size());

        }

        @DisplayName("case2: Traversal data")
        @Test
        void testSecondTest() {

            System.out.print("Pre-Order Traversal (recursive)");
            bst.preOrder();
            System.out.println();

            System.out.print("Pre-Order Traversal (non-recursive)：");
            bst.preOrderNR();
            System.out.println();

            System.out.print("In-Order Traversal ");
            bst.inOrder();
            System.out.println();

            System.out.print("Post-Order Traversal: ");
            bst.postOder();
            System.out.println();

            System.out.print("Level Order Traversal：");
            bst.levelOrder();
            System.out.println();

        }

        @DisplayName("case 3: Determine if the binary search tree contains tree")
        @Test
        void testThreeTest() {

            System.out.println(bst.contains(6));
            System.out.print(bst.contains(11));

        }

        @DisplayName("case 4: printing bst")
        @Test
        void testFourTest() {

            System.out.println(bst);

        }

    }

}
