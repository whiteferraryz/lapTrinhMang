// src/main/java/org/example/laptrinhmang/common/dto/UserListResponseDTO.java

package org.example.laptrinhmang.common.dto;

import java.io.Serializable;
import java.util.List;

public class UserListResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String status; // Ví dụ: "SUCCESS", "ERROR", "PERMISSION_DENIED"
    private List<UserDTO> users;
    private String message;

    // Constructor mặc định (nếu cần)
    public UserListResponseDTO() {
    }

    // Constructor mà bạn đang cố gắng sử dụng
    public UserListResponseDTO(String status, List<UserDTO> users, String message) {
        this.status = status;
        this.users = users;
        this.message = message;
    }

    // --- Getters and Setters ---
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<UserDTO> getUsers() {
        return users;
    }

    public void setUsers(List<UserDTO> users) {
        this.users = users;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "UserListResponseDTO{" +
               "status='" + status + '\'' +
               ", users=" + (users != null ? users.size() + " users" : "null") +
               ", message='" + message + '\'' +
               '}';
    }
}