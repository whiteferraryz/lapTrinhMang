// src/main/java/org/example/laptrinhmang/common/dto/ExamListResponseDTO.java
package org.example.laptrinhmang.common.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Đối tượng phản hồi dùng để gửi danh sách các ExamDTO từ Server về Client.
 */
public class ExamListResponseDTO extends ResponseMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<ExamDTO> exams;

    public ExamListResponseDTO(String status, String message, List<ExamDTO> exams) {
        super(status, message);
        this.exams = exams;
    }

    // Constructor mặc định (có thể cần cho deserialization)
    public ExamListResponseDTO() {
        super();
    }

    public List<ExamDTO> getExams() {
        return exams;
    }

    public void setExams(List<ExamDTO> exams) {
        this.exams = exams;
    }

    @Override
    public String toString() {
        return "ExamListResponseDTO{" +
               "status='" + getStatus() + '\'' +
               ", message='" + getMessage() + '\'' +
               ", examsCount=" + (exams != null ? exams.size() : 0) +
               '}';
    }
}