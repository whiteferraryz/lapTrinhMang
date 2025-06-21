package org.example.laptrinhmang.common.dto;

import java.io.Serializable;
import java.util.Date; // THÊM IMPORT NÀY
import java.util.List;
import java.util.Objects;

public class ExamDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String title;
    private Integer subjectId;
    private String subjectName;
    private Integer durationMinutes;
    // private Integer totalQuestions; // Bạn có thể loại bỏ trường này nếu nó luôn được tính toán từ danh sách câu hỏi
    private String status;
    private Integer createdByUserId;
    private String createdByUsername;
    private Date creationDate; // THÊM TRƯỜNG NÀY
    private List<Integer> questionIds;
    private List<QuestionDTO> questions; // THÊM TRƯỜNG NÀY để chứa danh sách QuestionDTO chi tiết

    public ExamDTO() {}

    // Constructor MỚI - Match với 11 tham số từ Exam.toDTO()
    public ExamDTO(Integer id, String title, Integer subjectId, String subjectName, Integer durationMinutes,
                   Date creationDate, // THAY ĐỔI KIỂU DỮ LIỆU TỪ Integer totalQuestions SANG Date creationDate
                   Integer createdByUserId, String createdByUsername, String status,
                   List<Integer> questionIds, List<QuestionDTO> questions) { // THÊM THAM SỐ List<QuestionDTO>
        this.id = id;
        this.title = title;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.durationMinutes = durationMinutes;
        this.creationDate = creationDate; // GÁN GIÁ TRỊ
        this.createdByUserId = createdByUserId;
        this.createdByUsername = createdByUsername;
        this.status = status;
        this.questionIds = questionIds;
        this.questions = questions; // GÁN GIÁ TRỊ
        // Nếu bạn vẫn muốn có totalQuestions, bạn có thể tính toán nó ở đây
        // this.totalQuestions = (questionIds != null) ? questionIds.size() : 0;
    }

    // Getters and Setters (đã cập nhật và thêm các getter/setter mới)
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getSubjectId() { return subjectId; }
    public void setSubjectId(Integer subjectId) { this.subjectId = subjectId; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    // totalQuestions giờ đây có thể là một getter được tính toán
    public Integer getTotalQuestions() {
        return (questionIds != null) ? questionIds.size() : 0;
    }
    // Không cần setter cho totalQuestions nếu nó được tính toán tự động

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getCreatedByUserId() { return createdByUserId; }
    public void setCreatedByUserId(Integer createdByUserId) { this.createdByUserId = createdByUserId; }

    public String getCreatedByUsername() { return createdByUsername; }
    public void setCreatedByUsername(String createdByUsername) { this.createdByUsername = createdByUsername; }

    public Date getCreationDate() { return creationDate; } // THÊM GETTER
    public void setCreationDate(Date creationDate) { this.creationDate = creationDate; } // THÊM SETTER

    public List<Integer> getQuestionIds() { return questionIds; }
    public void setQuestionIds(List<Integer> questionIds) { this.questionIds = questionIds; }

    public List<QuestionDTO> getQuestions() { return questions; } // THÊM GETTER
    public void setQuestions(List<QuestionDTO> questions) { this.questions = questions; } // THÊM SETTER


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExamDTO examDTO = (ExamDTO) o;
        return Objects.equals(id, examDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ExamDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", subjectId=" + subjectId +
                ", subjectName='" + subjectName + '\'' +
                ", durationMinutes=" + durationMinutes +
                ", creationDate=" + creationDate + // Bao gồm trong toString
                ", totalQuestions=" + getTotalQuestions() + // Gọi getter để lấy giá trị
                ", status='" + status + '\'' +
                ", createdByUserId=" + createdByUserId +
                ", createdByUsername='" + createdByUsername + '\'' +
                ", questionIds=" + questionIds +
                ", questionsCount=" + (questions != null ? questions.size() : 0) + // Hiển thị số lượng câu hỏi chi tiết
                '}';
    }
}