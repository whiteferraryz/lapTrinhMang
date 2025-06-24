// src/main/java/org/example/laptrinhmang/common/dto/LoginRequestDTO.java
package org.example.laptrinhmang.common.dto; // ĐÃ THAY ĐỔI PACKAGE

import java.io.Serializable;

// ĐÃ THAY ĐỔI TÊN LỚP TỪ UserLoginRequest THÀNH LoginRequestDTO
public class LoginRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;

    // Constructor đầy đủ
    public LoginRequestDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Constructor mặc định (không tham số) - Tùy chọn nhưng được khuyến nghị
    public LoginRequestDTO() {
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // Setters - Tùy chọn nhưng được khuyến nghị
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginRequestDTO{" +
                "username='" + username + '\'' +
                ", password='" + "[REDACTED]" + '\'' + // Luôn ẩn mật khẩu khi in ra log
                '}';
    }
}