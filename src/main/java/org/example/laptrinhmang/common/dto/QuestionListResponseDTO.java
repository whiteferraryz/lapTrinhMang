// src/main/java/org/example/laptrinhmang/common/dto/QuestionListResponseDTO.java
package org.example.laptrinhmang.common.dto;

import java.io.Serializable;
import java.util.List;

// ĐÃ SỬA: Kế thừa ResponseMessage
public class QuestionListResponseDTO extends ResponseMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    // Các trường status và message sẽ được kế thừa từ ResponseMessage, không cần định nghĩa lại ở đây.
    private List<QuestionDTO> questions;

    public QuestionListResponseDTO() {
        super(); // Gọi constructor mặc định của lớp cha
    }

    // ĐÃ SỬA: Constructor gọi super() với status và message
    public QuestionListResponseDTO(String status, String message, List<QuestionDTO> questions) {
        super(status, message); // Gọi constructor của lớp cha
        this.questions = questions;
    }

    // Getters và Setters cho trường questions
    public List<QuestionDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionDTO> questions) {
        this.questions = questions;
    }

    // toString() cũng cần điều chỉnh một chút nếu muốn hiển thị các trường từ lớp cha
    @Override
    public String toString() {
        return "QuestionListResponseDTO{" +
                "status='" + getStatus() + '\'' + // Lấy từ lớp cha
                ", message='" + getMessage() + '\'' + // Lấy từ lớp cha
                ", questions=" + questions +
                '}';
    }
}