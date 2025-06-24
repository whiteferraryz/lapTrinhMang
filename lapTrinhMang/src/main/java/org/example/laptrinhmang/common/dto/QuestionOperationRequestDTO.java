package org.example.laptrinhmang.common.dto;

import java.io.Serializable;

public class QuestionOperationRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int questionId;
    private QuestionDTO question;

    public QuestionOperationRequestDTO() {}

    public QuestionOperationRequestDTO(int questionId) {
        this.questionId = questionId;
    }

    public QuestionOperationRequestDTO(QuestionDTO question) {
        this.question = question;
        if (question != null) {
            this.questionId = question.getQuestionId(); // <--- ĐÃ SỬA TẠI ĐÂY: Dùng getQuestionId() thay vì getId()
        }
    }

    public QuestionOperationRequestDTO(int questionId, QuestionDTO question) {
        this.questionId = questionId;
        this.question = question;
    }

    // Getters và Setters (giữ nguyên)
    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public QuestionDTO getQuestion() {
        return question;
    }

    public void setQuestion(QuestionDTO question) {
        this.question = question;
    }

    @Override
    public String toString() {
        return "QuestionOperationRequestDTO{" +
                "questionId=" + questionId +
                ", question=" + question +
                '}';
    }
}