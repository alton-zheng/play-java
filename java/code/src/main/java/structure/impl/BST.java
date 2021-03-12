package structure.impl;

import structure.impl.stack.LinkedListStack;
import structure.interfaces.Stack;

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

    public void preOrderNR() {
        preOrderNR(root);
    }

    public void preOrderNR(Node node) {

        Stack<Node> stack = new LinkedListStack<>();
        stack.push(node);

        while (!stack.empty()) {

            Node cur = stack.pop();
            System.out.print(cur.item + ",");
            if (cur.right != null) stack.push(cur.right);
            if (cur.left != null) stack.push(cur.left);

        }

    }


    public void inOrder() {
        inOrder(root);
    }

    public void inOrder(Node node) {

        if (node != null) {

            inOrder(node.left);
            System.out.print(node.item + ",");
            inOrder(node.right);

        }
    }

    public void suOrder() {
        suOrder(root);
    }

    private void suOrder(Node node) {

        if (node != null) {

            suOrder(node.left);
            System.out.print(node.item + ",");
            suOrder(node.right);

        }

    }

    @Override
    public String toString() {

        StringBuilder res = new StringBuilder();

        generateBSTString(root, 0, res);

        return res.toString();

    }

    private void generateBSTString(Node node, int depth, StringBuilder res) {

        if (node == null) {
            res.append(generateDepth(depth)).append("NULL\n");
            return;
        }

        res.append(generateDepth(depth)).append(node.item).append("\n");

        depth++;
        generateBSTString(node.left, depth, res);
        generateBSTString(node.right, depth, res);

    }

    private String generateDepth(int depth) {
        StringBuilder res = new StringBuilder();

        for (int i = 0; i < depth; i++) {
            res.append("--");
        }

        return res.toString();

    }

}
