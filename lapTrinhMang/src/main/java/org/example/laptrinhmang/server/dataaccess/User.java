// src/main/java/org/example/laptrinhmang/server/dataaccess/User.java
package org.example.laptrinhmang.server.dataaccess; // Hoặc package model nếu bạn tạo

// Lớp này đại diện cho một bản ghi User trong cơ sở dữ liệu
// Chứa đầy đủ thông tin, bao gồm cả mật khẩu băm và role_id (nếu có) hoặc role_name

public class User {
    private int id;
    private String username;
    private String passwordHash; // Chứa mật khẩu đã được băm
    private String email;
    private int roleId; // Id của vai trò
    private String roleName; // Tên vai trò (để dễ đọc)

    public User() {
    }

    public User(int id, String username, String passwordHash, String email, int roleId, String roleName) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.roleId = roleId;
        this.roleName = roleName;
    }

    // --- Getters and Setters ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                '}';
    }
}