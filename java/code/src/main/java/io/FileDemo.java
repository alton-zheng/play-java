package io;

import java.io.File;

/**
 * @Author: alton
 * @Date: Created in 2020/12/20 11:31 上午
 * @Description:
 */
public class FileDemo {

    public static void main(String[] args) throws Exception {
        File file = new File("demo/");

        System.out.println(file.exists());
        if (!file.exists()) {
            file.mkdir();
            // file.mkdirs();
        }

        System.out.println(file.isFile());
        System.out.println(file.isDirectory());

        File fileDemo = new File("demo");

        if (!fileDemo.exists()) {
            fileDemo.createNewFile();
        }

        System.out.println(fileDemo);
        System.out.println(fileDemo.getAbsolutePath());
        System.out.println(fileDemo.getPath());
        System.out.println(fileDemo.getName());

        FileUtils.listDirectory(fileDemo);


    }
}
