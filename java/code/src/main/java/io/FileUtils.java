package io;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;

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

    public static void copyFile(File srcFile, File destFile) throws IOException {

        if (!srcFile.exists() || !srcFile.isFile()) {
            throw new IllegalArgumentException("文件" + srcFile + " 不存在或不是文件");
        }

        FileInputStream fis = new FileInputStream(srcFile);
        FileOutputStream fos = new FileOutputStream(destFile);

        byte[] buf = new byte[8 * 1024];

        int b;
        while ((b = fis.read(buf, 0, buf.length)) != -1) {
            fos.write(buf, 0, b);
            fos.flush();
        }

        fos.close();
        fis.close();

    }

    public static void copyFileByBuffer(File src, File dest) throws IOException {

        if (!src.exists() || !src.isFile()) {
            throw new IllegalArgumentException("文件" + src + " 不存在或不是文件");
        }

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(src));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dest));

        // 下面的实现方式根据要求自己实现即可，有很多方式能够实现。很多时间，越简单越好
        byte[] bytes = new byte[8 * 1024];
        int b;

        while ((b = bis.read(bytes, 0, bytes.length)) != -1) {
            bos.write(bytes, 0, b);
            bos.flush();
        }

        bos.close();
        bis.close();

    }

    public static void copyFileByStringReaderAndWriter(File src, File dest) throws IOException {

        if (!src.exists() || !src.isFile()) {
            throw new IllegalArgumentException("文件" + src + " 不存在或不是文件");
        }

        InputStreamReader isr = new InputStreamReader(new FileInputStream(src));
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(dest));

        char[] charArray = new char[8 * 1024];
        int b;
        while ((b = isr.read(charArray, 0, charArray.length)) != -1) {
            osw.write(charArray, 0, b);
            osw.flush();
        }

        osw.close();
        isr.close();
    }

    public static void copyFileByFileReaderAndFileWriter(File src, File dest) throws IOException {

        if (!src.exists() || !src.isFile()) {
            throw new IllegalArgumentException("文件" + src + " 不存在或不是文件");
        }

        FileReader fr = new FileReader(src);
        FileWriter fw = new FileWriter(dest);

        char[] buffer = new char[1024];
        int c;
        while ((c = fr.read(buffer, 0, buffer.length)) != -1) {
            fw.write(buffer, 0, c);
            fw.flush();
        }

        fw.close();
        fr.close();

    }

    public static void copyFileByBrAndBw(File src, File dest) throws IOException {

        if (!src.exists() || !src.isFile()) {
            throw new IllegalArgumentException("文件" + src + " 不存在或不是文件");
        }

        BufferedReader br = new BufferedReader(new FileReader(src));
        /*BufferedWriter bw = new BufferedWriter(new FileWriter(dest));

        // 以 readLine() 为例
        String str;
        while ((str = br.readLine()) != null) {
            bw.write(str, 0, str.length());
            //bw.write("\n");
            bw.newLine();
            bw.flush();
        }*/

        PrintWriter pw = new PrintWriter(new FileWriter(dest), true);

        String str;

        while((str = br.readLine()) != null) {
            pw.println(str);
        }
        pw.close();
        br.close();

    }

}
