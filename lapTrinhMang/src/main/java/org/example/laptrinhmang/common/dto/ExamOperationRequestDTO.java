// src/main/java/org/example/laptrinhmang/common/dto/ExamOperationRequestDTO.java
package org.example.laptrinhmang.common.dto;

import java.io.Serializable;

/**
 * Đối tượng dùng để đóng gói các yêu cầu liên quan đến thao tác với Bài thi,
 * chẳng hạn như xóa bài thi (chỉ cần ID) hoặc cập nhật bài thi (cần ExamDTO).
 */
public class ExamOperationRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer examId; // Dùng cho xóa, lấy theo ID
    private ExamDTO examDTO; // Dùng cho cập nhật

    // Constructor cho các thao tác cần ID (ví dụ: delete, get by id)
    public ExamOperationRequestDTO(Integer examId) {
        this.examId = examId;
    }

    // Constructor cho các thao tác cần ExamDTO (ví dụ: update)
    public ExamOperationRequestDTO(ExamDTO examDTO) {
        this.examDTO = examDTO;
    }

    // Constructor mặc định (có thể cần cho deserialization)
    public ExamOperationRequestDTO() {
    }

    public Integer getExamId() {
        return examId;
    }

    public void setExamId(Integer examId) {
        this.examId = examId;
    }

    public ExamDTO getExamDTO() {
        return examDTO;
    }

    public void setExamDTO(ExamDTO examDTO) {
        this.examDTO = examDTO;
    }

    @Override
    public String toString() {
        return "ExamOperationRequestDTO{" +
               "examId=" + examId +
               ", examDTO=" + examDTO +
               '}';
    }
}