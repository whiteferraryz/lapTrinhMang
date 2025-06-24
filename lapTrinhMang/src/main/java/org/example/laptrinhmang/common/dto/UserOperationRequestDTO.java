package org.example.laptrinhmang.common.dto; // Đảm bảo đúng package của bạn

import java.io.Serializable;

/**
 * Đối tượng truyền tải dữ liệu (DTO) cho các yêu cầu thao tác người dùng (cập nhật, xóa, reset mật khẩu).
 * Chứa thông tin về người dùng bị ảnh hưởng và dữ liệu cần thiết cho thao tác.
 */
public class UserOperationRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int userId; // ID của người dùng bị ảnh hưởng bởi thao tác
    private UserDTO userDTO; // Dùng cho thao tác UPDATE (chứa username, email, roleId mới)
    private String newPassword; // Dùng cho thao tác RESET_PASSWORD

    public UserOperationRequestDTO() {
    }

    // Constructor cho các thao tác dựa trên userId (ví dụ: DELETE, RESET_PASSWORD)
    public UserOperationRequestDTO(int userId, String newPassword) {
        this.userId = userId;
        this.newPassword = newPassword;
    }

    // Constructor cho thao tác UPDATE
    public UserOperationRequestDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
        if (userDTO != null) {
            this.userId = userDTO.getUserId();
        }
    }
    public UserOperationRequestDTO(int userId, UserDTO userDTO, String newPassword) {
    this.userId = userId;
    this.userDTO = userDTO;
    this.newPassword = newPassword;
    }

    // --- Getters and Setters ---
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public UserDTO getUserDTO() {
        return userDTO;
    }

    public void setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
        if (userDTO != null) {
            this.userId = userDTO.getUserId(); // Đảm bảo userId luôn đồng bộ
        }
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @Override
    public String toString() {
        return "UserOperationRequestDTO{" +
               "userId=" + userId +
               ", userDTO=" + (userDTO != null ? userDTO.toString() : "null") +
               ", newPassword=" + (newPassword != null ? "[HIDDEN]" : "null") +
               '}';
    }
}