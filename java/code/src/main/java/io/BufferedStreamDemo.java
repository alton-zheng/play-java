package io;

import java.io.File;
import java.io.IOException;

/**
 * @Author: alton
 * @Date: Created in 2020/12/22 5:15 下午
 * @Description:
 */
public class BufferedStreamDemo {

    public static void main(String[] args) throws IOException  {

        FileUtils.copyFileByBuffer(
                new File("demo/fileDemo.txt"),
                new File("demo/filedemo3.txt")
        );
    }
}
