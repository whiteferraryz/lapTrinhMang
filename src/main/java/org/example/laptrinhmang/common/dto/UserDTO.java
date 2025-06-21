package org.example.laptrinhmang.common.dto;

import java.io.Serializable;

/**
 * Đối tượng truyền tải dữ liệu (DTO) cho thông tin người dùng.
 * Được sử dụng để gửi thông tin người dùng giữa client và server
 * mà không bao gồm các thông tin nhạy cảm như mật khẩu băm SAU KHI ĐĂNG NHẬP THÀNH CÔNG.
 * TẠM THỜI CHỨA passwordHash (raw password) KHI ĐĂNG KÝ
 */
public class UserDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private int userId;
    private String username;
    private String email;
    private int roleId;
    private String roleName;
    // Tên vai trò, ví dụ: "Student", "Teacher", "Admin"
    private String passwordHash; // <-- THÊM DÒNG NÀY ĐỂ NHẬN MẬT KHẨU GỐC KHI ĐĂNG KÝ

    public UserDTO() {
    }

    // Constructor đầy đủ cho thông tin người dùng
    public UserDTO(int userId, String username, String email, int roleId, String roleName) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.roleId = roleId;
        this.roleName = roleName;
    }

    // Constructor thêm cho đăng ký (nếu client gửi kèm mật khẩu gốc)
    public UserDTO(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        // Các trường roleId/roleName sẽ được đặt mặc định hoặc từ client sau
    }

    // Constructor đầy đủ bao gồm passwordHash (dùng để tạo UserDTO từ User entity để xử lý nội bộ nếu cần)
    public UserDTO(int userId, String username, String email, int roleId, String roleName, String passwordHash) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.roleId = roleId;
        this.roleName = roleName;
        this.passwordHash = passwordHash;
    }

    // *** ĐÃ THÊM CONSTRUCTOR NÀY ĐỂ KHẮC PHỤC LỖI ***
    // Constructor cho trường hợp chỉ cần userId, username, roleName (ví dụ: sau khi đăng nhập)
    public UserDTO(int userId, String username, String roleName) {
        this.userId = userId;
        this.username = username;
        this.roleName = roleName;
        // Các trường khác có thể để mặc định hoặc null
        this.email = null;
        this.roleId = 0; // Hoặc một giá trị mặc định hợp lý khác
        this.passwordHash = null;
    }


    // --- Getters and Setters ---
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    // THÊM GETTER VÀ SETTER CHO passwordHash
    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                // KHÔNG BAO GỒM passwordHash trong toString khi debug để tránh lộ thông tin nhạy cảm
                // Nếu muốn xem, hãy thêm: ", passwordHash='" + (passwordHash != null ? "[HIDDEN]" : "null") + '\'' +
                '}';
    }
}