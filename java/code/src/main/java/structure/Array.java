package structure;

/**
 * @Author: alton
 * @Date: Created in 2021/3/2 3:40 下午
 * @Description: 基于 JAVA 数组定义的动态数组，支持增删改查
 */
public class Array<T> {

    private T[] data;
    private int size;

    /**
     * 构造方法，传入数组的容量 capacity 构造 Array
     *
     * @param capacity 数组初始化容量值
     */
    public Array(int capacity) {

        data = (T[]) new Object[capacity];
        size = 0;

    }

    /**
     * 无参构造方法
     */
    public Array() {
        this(10);
    }

    /**
     * 获取数组中的元素个数
     *
     * @return
     */
    public int getSize() {
        return size;
    }

    /**
     * 获取数组的容量
     *
     * @return
     */
    public int getCapacity() {
        return data.length;
    }

    /**
     * 判断数组是否为空
     *
     * @return
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * 向数组中添加元素
     *
     * @param value 需要添加的元素值
     */
    public void insert(T value) {

        insert(size, value);

    }


    /**
     * 头部插入元素
     *
     * @param value 将要插入的值
     */
    public void insertHead(T value) {

        insert(0, value);

    }


    /**
     * @param index 在指定位置，插入 value
     * @param value
     */
    public void insert(int index, T value) {

        if (index < 0 || index > size) {
            throw new IllegalArgumentException("Add failed. Required index >= 0 and index < size");
        }

        if (size == data.length) {
            resize(2 * data.length);
        }

        for (int i = size - 1; i >= index; i--) {
            data[i + 1] = data[i];
        }

        data[index] = value;
        size++;

    }

    /**
     * 动态的扩充或缩减数组容量
     * @param capacity
     */
    private void resize(int capacity) {
        T[] newData = (T[]) new Object[capacity];

        for (int i = 0; i < size; i++) {
            newData[i] = data[i];
        }

        data = newData;

    }

    /**
     * 删除元素
     * @param index
     * @return
     */
    public T remove(int index) {

        if (index < 0 || index > size) {
            throw new IllegalArgumentException("Remove failed. Required index >= 0 and index < size");
        }

        T res = data[index];

        for (int i = index + 1; i < size; i++) {

            data[i - 1] = data[i];
        }

        size--;
        // loitering objects（游离对象） != memory leak(内存泄漏)
        data[size] = null;

        // 当 size 等于 capacity / 4 时，才将数组缩减为数组当前大小一半长度（capacity/2）
        if (size == data.length / 4 && data.length / 2 != 0) {
            resize(data.length / 2);
        }

        return res;

    }


    public T removeFirst() {
        return remove(0);
    }

    public T removeLast() {
        return remove(size - 1);
    }

    /**
     * 从数组中删除元素 value
     *
     * @param value 需要删除的元素
     */
    public boolean removeElement(T value) {
        int index = find(value);

        if (index != -1) {
            remove(index);
            return true;
        }

        return false;

    }

    /**
     * 设置数组指定位置的元素值，覆盖
     *
     * @param index 数组索引位置
     * @param value 需要设置的元素值
     */
    public void set(int index, T value) {

        if (index < 0 || index >= size) {
            throw new IllegalArgumentException("Get failed. Index is illegal.");
        }

        data[index] = value;

    }

    /**
     * 获取指定位置的元素值
     *
     * @param index
     * @return
     */
    public T get(int index) {

        if (index < 0 || index >= size) {
            throw new IllegalArgumentException("Get failed. Index is illegal.");
        }

        return data[index];

    }

    /**
     * 获取数组第一个元素
     * @return
     */
    public T getFirst() {
        return get(0);
    }


    /**
     * 获取数组最后一个元素
     * @return
     */
    public  T getLast() {
        return get(size - 1);
    }

    /**
     * 查找数组是否包含元素 value
     *
     * @param value 判断的元素值
     * @return 返回是否包含元素值
     */
    public boolean contains(T value) {

        for (T item : data) {
            if (item.equals(value)) {
                return true;
            }
        }

        return false;

    }

    /**
     * 查找数组中元素 value 所在的索引值，如果不存在元素 value, 则返回 -1
     *
     * @param value 需要查找的数值
     * @return 元素所在索引值
     */
    public int find(T value) {

        int i = 0;
        for (T item : data) {

            if (item.equals(value)) {
                return i;
            }

            i++;

        }

        return -1;
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        builder.append(
                String.format(
                        "Array: Size = %d, capacity = %d\n",
                        size,
                        data.length
                )
        );

        builder.append("[");

        for (int i = 0; i < size; i++) {
            builder.append(data[i]);
            if (i != size - 1) {
                builder.append(", ");
            }
        }

        builder.append("]");

        return builder.toString();

    }
}
