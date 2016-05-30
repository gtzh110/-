package com.yuzhi.fine.utils;

import java.security.MessageDigest;

/**
 * Created by lemon on 2016/4/2.
 */

public class SecurityUtils {

    private SecurityUtils() {
    }

    public static class MD5 {
        public static final String KEY_MD5 = "MD5";
        public static final String CHAR_SET = "utf-8";

        private MD5() {
        }

        public static String encrypt(String data) throws Exception {
            MessageDigest md5 = MessageDigest.getInstance(KEY_MD5);
            md5.update(data.getBytes(CHAR_SET));
            return byte2hexString(md5.digest());
        }

        private static String byte2hexString(byte[] bytes) {
            StringBuilder buf = new StringBuilder(bytes.length * 2);
            for (byte aByte : bytes) {
                if (((int) aByte & 0xff) < 0x10) {
                    buf.append("0");
                }
                buf.append(Long.toString((int) aByte & 0xff, 16));
            }
            return buf.toString();
        }
    }
}

