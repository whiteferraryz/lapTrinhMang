package org.example.laptrinhmang.common.dto;

import org.example.laptrinhmang.common.model.User; // Import lớp User
import java.io.Serializable;

/**
 * Đối tượng truyền tải dữ liệu (DTO) cho phản hồi đăng nhập từ Server.
 * Chứa trạng thái đăng nhập, thông báo và thông tin người dùng nếu đăng nhập thành công.
 */
public class LoginResponseDTO extends ResponseMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private User user; // Thông tin người dùng đã đăng nhập

    // Constructor mặc định (cần cho quá trình deserialize)
    public LoginResponseDTO() {
        super(); // Gọi constructor mặc định của lớp cha ResponseMessage
    }

    /**
     * Constructor cho phản hồi đăng nhập thành công.
     * @param status Trạng thái của phản hồi (ví dụ: "SUCCESS")
     * @param message Thông báo đi kèm
     * @param user Đối tượng User đã đăng nhập
     */
    public LoginResponseDTO(String status, String message, User user) {
        super(status, message); // Gọi constructor của lớp cha ResponseMessage
        this.user = user;
    }

    /**
     * Constructor cho phản hồi đăng nhập thất bại (không có đối tượng User).
     * @param status Trạng thái của phản hồi (ví dụ: "ERROR")
     * @param message Thông báo lỗi
     */
    public LoginResponseDTO(String status, String message) {
        super(status, message); // Gọi constructor của lớp cha ResponseMessage
        this.user = null; // Không có thông tin người dùng nếu thất bại
    }

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "LoginResponseDTO{" +
               "status='" + getStatus() + '\'' +
               ", message='" + getMessage() + '\'' +
               ", user=" + (user != null ? user.getUsername() : "null") + // Chỉ hiển thị username để tránh lộ mật khẩu băm
               '}';
    }
}