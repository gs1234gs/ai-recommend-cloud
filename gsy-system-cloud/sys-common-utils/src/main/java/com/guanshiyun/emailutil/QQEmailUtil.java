package com.guanshiyun.emailutil;

import jakarta.mail.internet.InternetAddress;

public class QQEmailUtil {
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        try {
            // 第二个参数 true 表示开启严格模式，会检查域名部分是否合法
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isNotValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return true;
        }
        try {
            // 第二个参数 true 表示开启严格模式，会检查域名部分是否合法
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
            return false;
        } catch (Exception e) {
            return true;
        }
    }
}
