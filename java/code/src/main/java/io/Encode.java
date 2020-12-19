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
