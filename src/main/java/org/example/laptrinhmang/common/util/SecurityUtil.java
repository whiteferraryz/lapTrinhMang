// src/main/java/org/example/laptrinhmang/common/util/SecurityUtil.java
package org.example.laptrinhmang.common.util;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SecurityUtil {
    private static final Logger LOGGER = Logger.getLogger(SecurityUtil.class.getName());

    // Phương thức băm mật khẩu (ví dụ với SHA-256)
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi băm mật khẩu: " + e.getMessage(), e);
            return null;
        }
    }
}