package io;

import java.io.*;

/**
 * @Author: alton
 * @Date: Created in 2020/12/22 4:57 下午
 * @Description:
 */
public class DataStreamDemo {

    public static void main(String[] args) throws IOException  {

        String file = "demo/dataStreamDemo.txt";
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));

        dos.writeInt(123);
        dos.writeUTF("中国");
        dos.writeLong(131231231313131L);
        dos.writeBoolean(false);
        dos.writeChars("中国");
        dos.flush();
        dos.close();

        DataInputStream dis = new DataInputStream(new FileInputStream(file));

        System.out.println(dis.readInt());
        System.out.println(dis.readUTF());
        System.out.println(dis.readLong());
        System.out.println(dis.readBoolean());

        dis.close();

    }
}
