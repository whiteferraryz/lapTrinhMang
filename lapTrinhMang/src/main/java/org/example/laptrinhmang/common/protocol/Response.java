// src/main/java/org/example/laptrinhmang/common/protocol/Response.java
package org.example.laptrinhmang.common.protocol;

import java.io.Serializable;

public class Response implements Serializable {
    private static final long serialVersionUID = 1L;
    private String status;
    private String message; // Có thể chứa thông báo lỗi/thành công
    private Object data;    // Chứa dữ liệu trả về (User, List<UserDTO>, QuestionDTO, v.v.)

    public Response(String status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public Response(String status, Object data) { // Constructor tiện lợi cho các trường hợp không có message riêng
        this(status, null, data);
    }

    public Response(String status, String message) { // Constructor tiện lợi cho các trường hợp chỉ có status và message
        this(status, message, null);
    }

    // Getters
    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
}