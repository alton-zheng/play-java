package structure.impl;

import javax.swing.tree.TreeNode;

/**
 * @Author: alton
 * @Date: Created in 2021/3/11 1:32 PM
 * @Description: BST
 */
public class BST<E extends Comparable<E>> {

    private class Node {

        public E item;

        public Node left, right;

        public Node(E item) {
            this.item = item;
            left = null;
            right = null;
        }
    }

    private Node root;
    private int size;

    public BST() {
        root = null;
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void add(E item) {

        root = add(root, item);

    }

    /**
     * 向以 node 为根的二分搜索树插入元素 e, 递归算法
     *
     * @param node
     * @param item
     * @return 返回插入新节点后二分搜索树的根
     */
    private Node add(Node node, E item) {

        if (node == null) {
            size++;
            return new Node(item);

        }

        if (item.compareTo(node.item) < 0) {
            node.left = add(node.left, item);
        } else if (item.compareTo(node.item) > 0) {
            node.right = add(node.right, item);
        }

        return node;

    }

    /**
     * 看二分搜索树中是否包含元素 item
     *
     * @param item
     * @return {<code>true</code> 包含
     * <code>false</code> 不包含}
     */
    public boolean contains(E item) {
        return contains(root, item);
    }

    private boolean contains(Node node, E item) {

        if (node == null) {
            return false;
        }

        if (node.item == item) {
            return true;
        }

        if (item.compareTo(node.item) < 0) {
            return contains(node.left, item);
        }

        return contains(node.right, item);

    }


    public void preOrder() {
        preOrder(root);
    }

    private void preOrder(Node node) {

        if (node != null) {
            System.out.print(node.item + ",");
            preOrder(node.left);
            preOrder(node.right);
        }
    }

}
