package org.example.laptrinhmang.common.model;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L; // Đảm bảo dòng này đúng chính tả

    private int userId;
    private String username;
    private String passwordHash; // Mật khẩu đã băm
    private String email;
    private int roleId;
    private String roleName; // <-- ĐÂY LÀ TRƯỜNG CẦN CÓ

    public User() {}

    public User(int userId, String username, String passwordHash, String email, int roleId) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.roleId = roleId;
        // roleName sẽ được set khi truy vấn từ DB hoặc từ DTO (không cần trong constructor này nếu bạn không có thông tin roleName lúc khởi tạo User)
    }

    // --- Getters and Setters ---
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }

    // --- Getter and Setter cho roleName ---
    public String getRoleName() { // <-- ĐÂY LÀ GETTER CẦN CÓ
        return roleName;
    }

    public void setRoleName(String roleName) { // <-- ĐÂY LÀ SETTER CẦN CÓ
        this.roleName = roleName;
    }

    @Override
    public String toString() {
        return "User{" +
               "userId=" + userId +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", roleId=" + roleId +
               ", roleName='" + roleName + '\'' + // Bao gồm roleName trong toString
               '}';
    }
}