package structure.interfaces;

import java.util.EmptyStackException;

/**
 * @Author: alton
 * @Date: Created in 2021/3/3 11:20 上午
 * @Description: Stack
 */
public interface Stack<E> {

    /**
     * Returns the number of components in this stack.
     * @return  the number of components in this stack
     */
    int size();

    /**
     * Tests if this stack is empty.
     * @return <code>true</code> if and only if this stack contains
     *         no items;
     *         <code>false</code> otherwise.
     */
    boolean empty();

    /**
     * Pushes an item onto the top of this stack. This has exactly
     * the same effect as:
     * <blockquote><pre>
     * addElement(item)</pre></blockquote>
     * @param item  the item to be pushed onto this stack.
     */
    void push (E item);

    /**
     * Removes the object at the top of this stack and returns that
     * object as the value of this function.
     *
     * @return  The object at the top of this stack. {@link E}
     */
    E pop();

    /**
     * Looks at the object at the top of this stack without removing it
     * from the stack.
     *
     * @return  the object at the top of this stack.
     */
    E peek();

}
