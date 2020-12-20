package io;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

/**
 * @Author: alton
 * @Date: Created in 2020/12/20 6:53 下午
 * @Description:
 */
public class RandomAccessFileDemo {
    public static void main(String[] args) throws Exception {

        File demo = new File("demo/randomAccessFile.txt");
        if (!demo.exists()) {
            demo.createNewFile();
        }

        RandomAccessFile raf = new RandomAccessFile(demo, "rw");

        raf.write('A');
        raf.write('B');

        System.out.println(raf.getFilePointer());

        int i = 124;

        // 用 write 方法只能一次写一个字节，如果要把i 写进去就得写 4 次
        /*raf.write((i >>> 24) & 0xff);
        raf.write((i >>> 16) & 0xff);
        raf.write((i >>> 8) & 0xff);
        raf.write((i >>> 0) & 0xff);*/

        System.out.println(raf.getFilePointer());
        // 或者直接写 int
        raf.writeInt(i);
        System.out.println(raf.getFilePointer());

        // 直接用 write byte[]
        String s = "郑";
        byte[] sByte = s.getBytes(StandardCharsets.UTF_8);
        raf.write(sByte);

        System.out.println(raf.getFilePointer());

        // 读， 必须将指针移到头部
        raf.seek(0);
        System.out.println(raf.read());
        System.out.println(raf.readInt());

        // 或者用 byte[] 读
        raf.seek(0);
        byte[] buf = new byte[(int) raf.length()];
        raf.read(buf);

        raf.close();

    }
}
