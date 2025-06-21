package org.example.laptrinhmang.common.dto;
//<- Đối tượng dùng để đóng gói và gửi MỌI YÊU CẦU từ Client lên Server.

import java.io.Serializable;

public class RequestMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String requestType;
    private Object data;
    private String authToken; // Thêm trường này để chứa token xác thực

    public RequestMessage(String requestType, Object data) {
        this.requestType = requestType;
        this.data = data;
    }

    public RequestMessage() {}

    // Getters and Setters
    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getAuthToken() { // Getter cho authToken
        return authToken;
    }

    public void setAuthToken(String authToken) { // Setter cho authToken
        this.authToken = authToken;
    }

    @Override
    public String toString() {
        return "RequestMessage{" +
               "requestType='" + requestType + '\'' +
               ", data=" + (data != null ? data.getClass().getSimpleName() : "null") +
               ", authToken='" + (authToken != null ? "[REDACTED]" : "null") + '\'' +
               '}';
    }
}