package structure.impl;

import structure.impl.queue.LoopQueue;
import structure.impl.stack.LinkedListStack;
import structure.interfaces.Queue;
import structure.interfaces.Stack;

/**
 * @Author: alton
 * @Date: Created in 2021/3/11 1:32 PM
 * @Description: Binary Search Tree
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


    /**
     * Add the item element to the binary search tree.
     *
     * @param item item element to be added.
     */
    public void add(E item) {

        root = add(root, item);

    }

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
     * Determine if the binary search tree contains item
     *
     * @param item
     * @return {<code>true</code> Contains the item element
     * <code>false</code> Does not contain the item element}
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


    /**
     * Pre-Order Traversal (recursive)
     */
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

    /**
     * Pre-Order Traversal (non-recursive)
     */
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


    /**
     * In-Order Traversal
     */
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

    /**
     * Post-Order Traversal
     */
    public void postOder() {
        postOder(root);
    }

    private void postOder(Node node) {

        if (node != null) {

            postOder(node.left);
            System.out.print(node.item + ",");
            postOder(node.right);

        }

    }

    /**
     * Breadth First Traversal
     */
    public void levelOrder() {

        Queue<Node> queue = new LoopQueue<>();
        queue.add(root);

        while (!queue.empty()) {
            Node cur = queue.remove();
            System.out.print(cur.item + ",");

            if (cur.left != null) {
                queue.add(cur.left);
            }

            if (cur.right != null) {
                queue.add(cur.right);
            }
        }

    }

    /**
     * The minimum item of BST
     *
     * @return minimum item
     */
    public E minimum() {

        return minimum(root).item;

    }

    private Node minimum(Node node) {

        if (node.left == null) {
            return node;
        }

        return minimum(node.left);

    }

    /**
     * The maximum item of BST
     *
     * @return the maximum item
     */
    public E maximum() {

        return maximum(root).item;

    }

    private Node maximum(Node node) {

        if (node.right == null) {
            return node;
        }

        return maximum(node.right);

    }

    /**
     * remove maximum item of BST
     *
     * @return maximum item
     */
    public E removeMaximum() {
        E maximum = maximum();

        root = removeMaximum(root);

        return maximum;
    }

    private Node removeMaximum(Node node) {

        if (node.right == null) {
            Node n = node.left;
            node.left = null;
            return n;
        }

        node.right = removeMaximum(node.right);
        return node;

    }

    /**
     * remove minimum item of BST
     *
     * @return minimum item
     */
    public E removeMinimum() {
        E minimum = minimum();

        root = removeMinimum(root);

        return minimum;
    }

    private Node removeMinimum(Node node) {

        if (node.left == null) {
            Node n = node.right;
            node.right = null;
            return n;
        }

        node.left = removeMinimum(node.left);
        return node;

    }

    /**
     * Remove a specified Item
     */
    public void remove(E item) {

        root = remove(root, item);

    }

    private Node remove(Node node, E item) {

        if (node == null) {
            return node;
        }

        if (item.compareTo(node.item) < 0) {
            node.left = remove(node.left, item);
            return node;
        }

        if (item.compareTo(node.item) > 0) {
            node.right = remove(node.right, item);
            return node;
        }

        if (node.left == null) {
            final Node n = node.right;
            node.right = null;
            return n;
        }

        if (node.right == null) {
            final Node n = node.left;
            node.left = null;
            return n;
        }

        Node newNode = new Node(minimum(node.right).item);
        newNode.right = removeMinimum(node.right);
        newNode.left = node.left;

        node = null;

        return newNode;

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
