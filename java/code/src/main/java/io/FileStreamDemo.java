package io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @Author: alton
 * @Date: Created in 2020/12/21 8:58 下午
 * @Description:
 */
public class FileStreamDemo {

    public static void main(String[] args) throws IOException  {

        // file 存在删除再创建，不存在直接创建， 第二个参数为 true ， 意味着追加数据到文件后面，不会重建文件
        FileOutputStream fos = new FileOutputStream("demo/fileDemoOutPut.txt");
        fos.write('A'); // 写 A 的 低8位
        fos.write('B'); // 写 B 的 低8位
        int i = 10;
        fos.write(i >>> 24);
        fos.write(i >>> 16);
        fos.write(i >>> 8);
        fos.write(i);
        fos.write("中国".getBytes());
        fos.close();

        FileUtils.printHexByRead(new File("demo/fileDemoOutput.txt"));

        FileUtils.copyFile(new File("demo/fileDemo.txt"), new File("demo/fileDemo2.txt"));

    }

}
