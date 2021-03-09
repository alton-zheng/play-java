package structure.impl.stack;

import structure.impl.LinkedList;
import structure.interfaces.Stack;

/**
 * @Author: alton
 * @Date: Created in 2021/3/6 10:29 上午
 * @Description: 基于 LinkedList 实现的 Stack
 */
public class LinkedListStack<E> implements Stack<E> {

    LinkedList<E> linkedList;
    public LinkedListStack() {
        linkedList = new LinkedList<>();
    }

    @Override
    public int size() {
        return linkedList.size();
    }

    @Override
    public boolean empty() {
        return linkedList.isEmpty();
    }

    @Override
    public void push(E item) {
        linkedList.addLast(item);
    }

    @Override
    public E pop() {
        return linkedList.removeLast();
    }

    @Override
    public E peek() {
        return linkedList.getFirst();
    }

    @Override
    public String toString() {
        return "LinkedListStack{" +
                "linkedList=" + linkedList +
                '}';
    }
}
