// src/main/java/org/example/laptrinhmang/common/model/Exam.java
package org.example.laptrinhmang.common.model;

import org.example.laptrinhmang.common.dto.ExamDTO; // Đã đổi
import org.example.laptrinhmang.common.dto.QuestionDTO; // Import QuestionDTO
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors; // Để sử dụng Stream API

public class Exam implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer examId;
    private String examName;
    private Integer subjectId;
    private String subjectName; // Thêm trường này để tiện cho DAO và Service
    private Integer durationMinutes; // Thời gian làm bài (phút)
    private Date creationDate;
    private Integer creatorUserId;
    private String creatorUsername; // Thêm trường này để tiện cho DAO và Service
    private String status; // Trạng thái của bài thi (e.g., "Draft", "Published")

    private List<Integer> questionIds; // Chỉ lưu ID câu hỏi
    private List<Question> questions; // Chỉ khi cần chi tiết mới populate

    // Constructors, Getters, Setters (tương tự như các model khác)

    public Exam() {
    }

    public Exam(Integer examId, String examName, Integer subjectId, Integer durationMinutes, Date creationDate, Integer creatorUserId, String status) {
        this.examId = examId;
        this.examName = examName;
        this.subjectId = subjectId;
        this.durationMinutes = durationMinutes;
        this.creationDate = creationDate;
        this.creatorUserId = creatorUserId;
        this.status = status;
    }

    // Getters
    public Integer getExamId() {
        return examId;
    }

    public String getExamName() {
        return examName;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Integer getCreatorUserId() {
        return creatorUserId;
    }

    public String getCreatorUsername() {
        return creatorUsername;
    }

    public String getStatus() {
        return status;
    }

    public List<Integer> getQuestionIds() {
        return questionIds;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    // Setters
    public void setExamId(Integer examId) {
        this.examId = examId;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setCreatorUserId(Integer creatorUserId) {
        this.creatorUserId = creatorUserId;
    }

    public void setCreatorUsername(String creatorUsername) {
        this.creatorUsername = creatorUsername;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setQuestionIds(List<Integer> questionIds) {
        this.questionIds = questionIds;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    /**
     * Chuyển đổi đối tượng Exam thành ExamDTO để gửi đến client.
     * Chỉ bao gồm các thông tin cần thiết và không nhạy cảm.
     * Nếu có danh sách Question object, sẽ chuyển đổi thành QuestionDTO.
     */
    public ExamDTO toDTO() {
        List<QuestionDTO> questionDTOs = null;
        if (this.questions != null) {
            questionDTOs = this.questions.stream()
                                         .map(Question::toDTO)
                                         .collect(Collectors.toList());
        }

        // Ưu tiên questionIds từ trường questionIds, nếu không thì tạo từ questions
        List<Integer> idsToSend = this.questionIds;
        if (idsToSend == null && this.questions != null) {
            idsToSend = this.questions.stream()
                                      .map(Question::getQuestionId)
                                      .collect(Collectors.toList());
        }

        return new ExamDTO(
            this.examId,
            this.examName,
            this.subjectId,
            this.subjectName,
            this.durationMinutes,
            this.creationDate,
            this.creatorUserId,
            this.creatorUsername,
            this.status,
            idsToSend,
            questionDTOs // Sẽ là null nếu this.questions là null
        );
    }

    @Override
    public String toString() {
        return "Exam{" +
               "examId=" + examId +
               ", examName='" + examName + '\'' +
               ", subjectId=" + subjectId +
               ", subjectName='" + subjectName + '\'' +
               ", durationMinutes=" + durationMinutes +
               ", creationDate=" + creationDate +
               ", creatorUserId=" + creatorUserId +
               ", creatorUsername='" + creatorUsername + '\'' +
               ", status='" + status + '\'' +
               ", questionIds=" + questionIds +
               ", questionsCount=" + (questions != null ? questions.size() : 0) +
               '}';
    }
}