package org.example.laptrinhmang.common.dto;

import java.io.Serializable;
import java.util.List;

// ĐÃ SỬA: Kế thừa ResponseMessage
public class SubjectListResponseDTO extends ResponseMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    // Các trường status và message sẽ được kế thừa từ ResponseMessage, không cần định nghĩa lại ở đây.
    private List<SubjectDTO> subjects; // <-- Đảm bảo tên biến là 'subjects' và kiểu là List<SubjectDTO>

    public SubjectListResponseDTO() {
        super(); // Gọi constructor mặc định của lớp cha
    }

    // ĐÃ SỬA: Constructor gọi super() với status và message
    // CHÚ Ý: Đã đổi thứ tự tham số message và subjects để khớp với constructor của ResponseMessage
    public SubjectListResponseDTO(String status, String message, List<SubjectDTO> subjects) {
        super(status, message); // Gọi constructor của lớp cha
        this.subjects = subjects;
    }

    // Getters và Setters cho trường subjects
    public List<SubjectDTO> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<SubjectDTO> subjects) {
        this.subjects = subjects;
    }

    // toString() cũng cần điều chỉnh một chút nếu muốn hiển thị các trường từ lớp cha
    @Override
    public String toString() {
        return "SubjectListResponseDTO{" +
                "status='" + getStatus() + '\'' + // Lấy từ lớp cha
                ", message='" + getMessage() + '\'' + // Lấy từ lớp cha
                ", subjects=" + (subjects != null ? subjects.size() + " items" : "null") +
                '}';
    }
}