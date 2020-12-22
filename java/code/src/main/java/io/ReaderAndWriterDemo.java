package io;

import java.io.File;
import java.io.IOException;

/**
 * @Author: alton
 * @Date: Created in 2020/12/22 6:19 下午
 * @Description:
 */
public class ReaderAndWriterDemo {

    public static void main(String[] args) throws IOException  {

        FileUtils.copyFileByStringReaderAndWriter(
                new File("demo/fileDemo.txt"),
                new File("demo/fileDemoReaderAndWriter.txt")
        );
    }
}
