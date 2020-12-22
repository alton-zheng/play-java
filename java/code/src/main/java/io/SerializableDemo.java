package io;

import java.io.*;

/**
 * @Author: alton
 * @Date: Created in 2020/12/22 8:36 下午
 * @Description:
 */
public class SerializableDemo {
    public static void main(String[] args) throws IOException, ClassNotFoundException  {

        String file = "demo/object.txt";

        Student student = new Student("alton", "男", 30);

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
        oos.writeObject(student);
        oos.flush();
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
        Student s = (Student) ois.readObject();
        System.out.println(s.toString());

        /*
         * 添加 writeObject, readObject 的方法前
         * Student{name='alton', sex='男', age=0}
         * 添加 writeObject, readObject 的方法后， 定义了 transient 关键字的 age 也能够进行反序列化
         * Student{name='alton', sex='男', age=30}
         */
        ois.close();
    }
}

class Student implements Serializable  {

    String name;
    String sex;

    // 标注 transient 的变量，保持为默认值 age = 0
    transient int age;

    public Student(String name, String sex, int age) {
        this.name = name;
        this.sex = sex;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", age=" + age +
                '}';
    }

    private void writeObject(ObjectOutputStream s) throws IOException {

        // 将 JVM 能序列化的元素进行序列化
        s.defaultWriteObject();

        // 可以序列化 transient 关键字的元素，当然也可以按照自定义的序列化规则来序列化普通元素，来提高性能（ArrayList 就是这样的例子）
        s.writeInt(age);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {

        // 将 JVM 能反序列化的元素进行反序列化
        ois.defaultReadObject();

        // 可以反序列化 transient 关键字的元素
        this.age = ois.readInt();

    }

}
