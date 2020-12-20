# IO

&nbsp;

## 编码

&nbsp;

![io](img/io.png)

&nbsp;

```java
package io;

import java.nio.charset.StandardCharsets;

/**
 * @Author: alton
 * @Date: Created in 2020/12/19 8:03 下午
 * @Description:
 */
public class Encode {

    public static void main(String[] args) throws Exception {

        String str = "郑A";
        byte[] defaultBytes = str.getBytes();

        for (byte defaultByte : defaultBytes) {
            System.out.print(Integer.toHexString(defaultByte & 0xff) + " ");
        }

        /*
         * result: 默认编码 utf-8
         * e9 83 91 41
         */

        System.out.println();
        System.out.println("==================");
        byte[] gbkBytes = str.getBytes("GBK");
        for (byte gbkByte : gbkBytes) {
            System.out.print(Integer.toHexString(gbkByte & 0xff) + " ");
        }

        /*
         * result: 编码 gbk
         * d6 a3 41
         */

        System.out.println();
        System.out.println("==================");
        byte[] utf16beBytes = str.getBytes(StandardCharsets.UTF_16BE);
        for (byte utf16beByte : utf16beBytes) {
            System.out.print(Integer.toHexString(utf16beByte & 0xff) + " ");
        }

        /*
         * result: 编码 gbk
         * 90 d1 0 41
         */

        System.out.println();
        System.out.println("==================");
        String utf16beString = new String(utf16beBytes, StandardCharsets.UTF_16BE);
        System.out.println(utf16beString);
        
    }
}
```

&nbsp;

## File

- `java.io.File` 表示 `directory` 或 `file`, 访问他们的元信息（名称，大小等），不能访问它们

&nbsp;

```java
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

```

&nbsp;

```java
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
```



### RandomAccessFile

功能

- 文件
  - 读写
  - 随机访问

&nbsp;

模式

- `rw`
- `r`

&nbsp;

构造

- `new RandomAcessFile(file, "rw")`
- 默认为 index = 0;



注意

- 资源用完必须关闭

```java
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

      	// 用完关闭资源
        raf.close();

    }
}
```

