package org.example.laptrinhmang.common.dto;

import java.io.Serializable;

/**
 * Đối tượng truyền tải dữ liệu (DTO) cho yêu cầu đăng ký tài khoản người dùng mới.
 * Chứa các thông tin cần thiết để tạo một tài khoản User trên Server.
 */
public class RegisterRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L; // Để đảm bảo khả năng tương thích khi serialize/deserialize

    private String username;
    private String password; // Mật khẩu dạng plain text từ client (sẽ được băm trên server)
    private String email;
    private int roleId; // ID vai trò (ví dụ: 1 cho Student, 2 cho Teacher, 3 cho Admin)

    // Constructor mặc định (cần thiết cho một số thư viện hoặc nếu bạn tạo instance không đối số)
    public RegisterRequestDTO() {
    }

    /**
     * Constructor đầy đủ để tạo một đối tượng RegisterRequestDTO.
     *
     * @param username Tên đăng nhập mong muốn.
     * @param password Mật khẩu gốc (chưa băm).
     * @param email Địa chỉ email của người dùng.
     * @param roleId ID của vai trò người dùng (ví dụ: từ bảng user_roles).
     */
    public RegisterRequestDTO(String username, String password, String email, int roleId) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.roleId = roleId;
    }

    // --- Getters và Setters ---

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    @Override
    public String toString() {
        return "RegisterRequestDTO{" +
               "username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", roleId=" + roleId +
               '}';
    }
}
