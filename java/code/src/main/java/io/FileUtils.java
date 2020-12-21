package io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @Author: alton
 * @Date: Created in 2020/12/20 6:38 下午
 * @Description: 列出 File 的常用操作，比如过滤，遍历等操作
 */
public class FileUtils {

    /**
     * 列出 dir 的目录或文件名称
     *
     * @param dir
     * @throws IOException
     */
    public static void listDirectory(File dir) throws IOException {
        if (!dir.exists() && !dir.isDirectory()) {
            throw new IllegalArgumentException("目录： " + dir + "不存在或者不是目录");
        }

        String[] fileList = dir.list();
        for (String s : fileList) {
            System.out.println(s);
        }
    }

    public static void printHexByRead(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);

        int b;
        int i = 1;

        while ((b = fis.read()) != -1) {

            // 字节 b的 16 进值的第一位为0时，只会展示第二位，因此，当遇到这种字节时，前面补 "0"
            if (b < 0xf) {
                System.out.print("0");
            }
            System.out.print(Integer.toHexString(b) + " ");

            if (i++ % 10 == 0) {
                System.out.println();
            }

        }

        fis.close();

    }

    public static void printHexByReadByteArray(File file) throws IOException {

        FileInputStream fis = new FileInputStream(file);

        byte[] buf = new byte[20 * 1024];

        /*int length = fis.read(buf);

        int j = 1;
        for (int i = 0; i < length; i++) {

            if (buf[i] < 0xf) {
                System.out.print("0");
            }

            System.out.print(Integer.toHexString(buf[i] & 0xff) + " ");

            if (j++ % 10 == 0) {
                System.out.println();
            }

        }*/

        // 下面这种写法， buf 可以反复用， 但也有缺陷，这种不再说明，有兴趣的可以自己试试
        int i;
        int j = 1;
        while ((i = fis.read(buf, 0, buf.length)) != -1) {
            for (int i1 = 0; i1 < i; i1++) {
                if (buf[i1] < 0xf) {
                    System.out.print("0");
                }
                System.out.print(Integer.toHexString(buf[i1] & 0xff) + " ");

                if (j++ % 10 == 0) {
                    System.out.println();
                }
            }
        }

        fis.close();
    }
}
