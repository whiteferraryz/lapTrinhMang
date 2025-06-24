// src/main/java/org/example/laptrinhmang/common/dto/QuestionDTO.java
package org.example.laptrinhmang.common.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Đối tượng truyền tải dữ liệu (DTO) cho thông tin câu hỏi.
 */
public class QuestionDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int questionId;
    private String content;
    private String questionType; // Ví dụ: MULTIPLE_CHOICE, TRUE_FALSE, SHORT_ANSWER
    private List<String> options; // Danh sách các lựa chọn cho trắc nghiệm
    private String correctAnswer;
    private int difficultyLevel; // Độ khó từ 1-5
    private int creatorUserId;
    private String creatorUsername; // Tên người tạo (dùng để hiển thị, không lưu DB nếu có User object)
    private int subjectId;
    private String subjectName; // Tên môn học (dùng để hiển thị, không lưu DB nếu có Subject object)
    private String creationDate; // Ngày tạo, dạng String để dễ hiển thị (vd: yyyy-MM-dd HH:mm:ss)

    // Constructor mặc định (cần cho deserialization)
    public QuestionDTO() {
    }

    // <-- THÊM CONSTRUCTOR NÀY CHO VIỆC THÊM MỚI CÂU HỎI (questionId và creationDate sẽ được tạo ở Server)
    public QuestionDTO(String content, String questionType, List<String> options, String correctAnswer, int difficultyLevel, int creatorUserId, int subjectId) {
        this.content = content;
        this.questionType = questionType;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.difficultyLevel = difficultyLevel;
        this.creatorUserId = creatorUserId;
        this.subjectId = subjectId;
        // creatorUsername, subjectName, creationDate sẽ được set hoặc truy xuất ở phía Server
    }

    // <-- THÊM CONSTRUCTOR NÀY CHO VIỆC CẬP NHẬT CÂU HỎI
    public QuestionDTO(int questionId, String content, String questionType, List<String> options, String correctAnswer, int difficultyLevel, int creatorUserId, String creatorUsername, int subjectId, String subjectName) {
        this.questionId = questionId;
        this.content = content;
        this.questionType = questionType;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.difficultyLevel = difficultyLevel;
        this.creatorUserId = creatorUserId;
        this.creatorUsername = creatorUsername;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        // creationDate không cần truyền vào khi cập nhật nếu nó được quản lý hoàn toàn ở DB hoặc server
    }

    // Constructor đầy đủ cho dữ liệu từ Database (khi đọc về)
    public QuestionDTO(int questionId, String content, String questionType, List<String> options, String correctAnswer,
                       int difficultyLevel, int creatorUserId, String creatorUsername, int subjectId, String subjectName, String creationDate) {
        this.questionId = questionId;
        this.content = content;
        this.questionType = questionType;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.difficultyLevel = difficultyLevel;
        this.creatorUserId = creatorUserId;
        this.creatorUsername = creatorUsername;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.creationDate = creationDate;
    }

    // --- Getters and Setters ---
    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public int getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(int difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public int getCreatorUserId() {
        return creatorUserId;
    }

    public void setCreatorUserId(int creatorUserId) {
        this.creatorUserId = creatorUserId;
    }

    public String getCreatorUsername() {
        return creatorUsername;
    }

    public void setCreatorUsername(String creatorUsername) {
        this.creatorUsername = creatorUsername;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public String toString() {
        return "QuestionDTO{" +
               "questionId=" + questionId +
               ", content='" + content + '\'' +
               ", questionType='" + questionType + '\'' +
               ", options=" + (options != null ? String.join("|", options) : "null") +
               ", correctAnswer='" + correctAnswer + '\'' +
               ", difficultyLevel=" + difficultyLevel +
               ", creatorUserId=" + creatorUserId +
               ", creatorUsername='" + creatorUsername + '\'' +
               ", subjectId=" + subjectId +
               ", subjectName='" + subjectName + '\'' +
               ", creationDate='" + creationDate + '\'' +
               '}';
    }
}