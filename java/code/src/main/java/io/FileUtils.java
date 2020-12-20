package io;

import java.io.File;
import java.io.IOException;

/**
 * @Author: alton
 * @Date: Created in 2020/12/20 6:38 下午
 * @Description: 列出 File 的常用操作，比如过滤，遍历等操作
 */
public class FileUtils {

    /**
     * 列出 dir 的目录或文件名称
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
}
