package com.tiv.seckill.infra.util.shiro;

import org.apache.shiro.crypto.hash.SimpleHash;

public class CommonUtil {

    public static boolean phoneRegexCheck(String phone) {
        return phone.length() == 11;
    }

    public static int getCode() {
        return (int) ((Math.random() * 9 + 1) * 100000);
    }

    public static String encryptPassword(String password, String userName) {
        return String.valueOf(new SimpleHash("MD5", password, userName, 1024));
    }

}