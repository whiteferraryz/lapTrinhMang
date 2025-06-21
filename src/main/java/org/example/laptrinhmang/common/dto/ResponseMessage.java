    package org.example.laptrinhmang.common.dto;
    //<- Đối tượng dùng để đóng gói và gửi MỌI PHẢN HỒI từ Server về Client.
    import java.io.Serializable;

    public class ResponseMessage implements Serializable {
        private static final long serialVersionUID = 1L;

        private String status; // Constants.RESPONSE_STATUS_SUCCESS, Constants.RESPONSE_STATUS_FAILED, Constants.RESPONSE_STATUS_ERROR, etc.
        private String message;
        private Object data; // Có thể là ExamDTO, List<ExamDTO>, v.v.

        public ResponseMessage(String status, String message, Object data) {
            this.status = status;
            this.message = message;
            this.data = data;
        }

        public ResponseMessage(String status, String message) {
            this(status, message, null);
        }

        public ResponseMessage() {}

        // Getters and Setters
        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "ResponseMessage{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", data=" + (data != null ? data.getClass().getSimpleName() : "null") +
                '}';
        }
    }