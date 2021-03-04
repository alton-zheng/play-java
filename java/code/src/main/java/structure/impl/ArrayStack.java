package structure.impl;

import structure.Array;
import structure.interfaces.Stack;

/**
 * @Author: alton
 * @Date: Created in 2021/3/3 11:23 上午
 * @Description: Array - based Stack implementation class
 */
public class ArrayStack<T> implements Stack<T> {

    Array<T> array;

    public ArrayStack(int capacity) {
        array = new Array<>(capacity);
    }

    public ArrayStack() {
        array = new Array<>();
    }

    public int getCapacity() {
        return array.getCapacity();
    }

    @Override
    public int size() {
        return array.getSize();
    }

    @Override
    public boolean empty() {
        return array.isEmpty();
    }

    @Override
    public void push(T item) {
         array.insert(item);
    }

    @Override
    public T pop() {
        return array.removeLast();
    }

    @Override
    public T peek() {
        return array.getLast();
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();

        res.append("Stack: [");
        for(int i = 0; i < array.getSize(); i++) {
            res.append(array.get(i));

            if (i != array.getSize() - 1){
                res.append(", ");
            }

        }

        res.append("] top");

        return res.toString();

    }

}
