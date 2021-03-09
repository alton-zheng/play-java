package structure.impl.queue;

import structure.impl.LinkedList;
import structure.interfaces.Queue;

/**
 * @Author: alton
 * @Date: Created in 2021/3/9 11:06 上午
 * @Description:
 */
public class LinkedListQueue<E> implements Queue<E> {

    private final LinkedList<E> queue = new LinkedList<>();
    @Override
    public void add(E e) {
        queue.addLast(e);
    }

    @Override
    public void offer(E e) {
        queue.offer(e);
    }

    @Override
    public E remove() {
        return queue.remove();
    }

    @Override
    public E poll() {
        return queue.poll();
    }

    @Override
    public E element() {
        return queue.element();
    }

    @Override
    public E peek() {
        return queue.peek();
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public boolean empty() {
        return queue.isEmpty();
    }

}
