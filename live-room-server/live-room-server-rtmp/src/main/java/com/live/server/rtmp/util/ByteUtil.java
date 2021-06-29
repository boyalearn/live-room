package com.live.server.rtmp.util;

import java.util.Formatter;

public class ByteUtil {


    public static int toInt(byte[] bytes) {
        if (bytes.length != 4) {
            return -1;
        }
        return ((((bytes[3] & 0xff) << 24)
                | ((bytes[2] & 0xff) << 16)
                | ((bytes[1] & 0xff) << 8)
                | ((bytes[0] & 0xff) << 0)));
    }

    public static void printBytes(byte[] bytes) {
        for (byte b : bytes) {
            System.out.print(b);
        }
        System.out.println();
    }


    public static String bytesToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
}
