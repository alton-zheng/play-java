package structure.impl.queue;

import structure.Array;
import structure.interfaces.Queue;

/**
 * @Author: alton
 * @Date: Created in 2021/3/3 8:07 下午
 * @Description:
 */
public class ArrayQueue<E> implements Queue<E> {

    private Array<E> array;

    public ArrayQueue(int capacity) {
        array = new Array<>(capacity);
    }

    public ArrayQueue() {
        array = new Array<>();
    }

    public int getCapacity() {
        return array.getCapacity();
    }

    @Override
    public void add(E e) {
        array.insert(e);
    }

    @Override
    public void offer(E e) {
        array.insert(e);
    }

    @Override
    public E remove() {
        return array.removeFirst();
    }

    @Override
    public E poll() {
        return array.removeFirst();
    }

    @Override
    public E element() {
        return array.getFirst();
    }

    @Override
    public E peek() {
        return array.getFirst();
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
    public String toString() {
        StringBuilder res = new StringBuilder();

        res.append("Queue: head [");
        for(int i = 0; i < array.getSize(); i++) {
            res.append(array.get(i));

            if (i != array.getSize() - 1){
                res.append(", ");
            }

        }

        res.append("] tail");

        return res.toString();

    }

}
