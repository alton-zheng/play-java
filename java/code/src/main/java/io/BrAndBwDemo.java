package io;

import java.io.File;
import java.io.IOException;

/**
 * @Author: alton
 * @Date: Created in 2020/12/22 8:14 下午
 * @Description:
 */
public class BrAndBwDemo {

    public static void main(String[] args) throws IOException  {

        FileUtils.copyFileByBrAndBw(
                new File("demo/fileDemo.txt"),
                new File("demo/brAndBwDemo.txt")
        );


    }
}
