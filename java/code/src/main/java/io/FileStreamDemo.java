package io;

import java.io.File;
import java.io.IOException;

/**
 * @Author: alton
 * @Date: Created in 2020/12/21 8:58 下午
 * @Description:
 */
public class FileStreamDemo {

    public static void main(String[] args) throws IOException  {

        FileUtils.printHexByRead(new File("demo/fileDemo.txt"));
        System.out.println();
        System.out.println("=================");
        FileUtils.printHexByReadByteArray(new File("demo/fileDemo.txt"));
    }

}
