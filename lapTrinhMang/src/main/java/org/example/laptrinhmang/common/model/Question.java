package org.example.laptrinhmang.common.model;

import org.example.laptrinhmang.common.dto.QuestionDTO;
import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat; // <-- THÊM IMPORT NÀY

public class Question implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer questionId;
    private String questionContent;
    private String questionType; // Ví dụ: "MultipleChoice", "TrueFalse"
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer; // Đáp án đúng (A, B, C, D hoặc True/False)
    private String difficultyLevel; // Ví dụ: "Easy", "Medium", "Hard"
    private Date creationDate;
    private Integer creatorUserId;
    private String creatorUsername; // Tên người tạo câu hỏi (để hiển thị)
    private Integer subjectId;
    private String subjectName; // Tên môn học (để hiển thị)

    // Constructors (có thể thêm các constructor khác tùy nhu cầu)
    public Question() {}

    public Question(Integer questionId, String questionContent, String questionType, String optionA, String optionB, String optionC, String optionD, String correctAnswer, String difficultyLevel, Date creationDate, Integer creatorUserId, Integer subjectId) {
        this.questionId = questionId;
        this.questionContent = questionContent;
        this.questionType = questionType;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
        this.difficultyLevel = difficultyLevel;
        this.creationDate = creationDate;
        this.creatorUserId = creatorUserId;
        this.subjectId = subjectId;
    }

    // Getters and Setters (giữ nguyên)
    public Integer getQuestionId() { return questionId; }
    public void setQuestionId(Integer questionId) { this.questionId = questionId; }
    public String getQuestionContent() { return questionContent; }
    public void setQuestionContent(String questionContent) { this.questionContent = questionContent; }
    public String getQuestionType() { return questionType; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }
    public String getOptionA() { return optionA; }
    public void setOptionA(String optionA) { this.optionA = optionA; }
    public String getOptionB() { return optionB; }
    public void setOptionB(String optionB) { this.optionB = optionB; }
    public String getOptionC() { return optionC; }
    public void setOptionC(String optionC) { this.optionC = optionC; }
    public String getOptionD() { return optionD; }
    public void setOptionD(String optionD) { this.optionD = optionD; }
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }
    public Date getCreationDate() { return creationDate; }
    public void setCreationDate(Date creationDate) { this.creationDate = creationDate; }
    public Integer getCreatorUserId() { return creatorUserId; }
    public void setCreatorUserId(Integer creatorUserId) { this.creatorUserId = creatorUserId; }
    public String getCreatorUsername() { return creatorUsername; }
    public void setCreatorUsername(String creatorUsername) { this.creatorUsername = creatorUsername; }
    public Integer getSubjectId() { return subjectId; }
    public void setSubjectId(Integer subjectId) { this.subjectId = subjectId; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    /**
     * Chuyển đổi đối tượng Question thành QuestionDTO để gửi đến client.
     */
    public QuestionDTO toDTO() {
        // Tạo List<String> từ các option A, B, C, D
        List<String> optionsList = new ArrayList<>();
        if (this.optionA != null) optionsList.add(this.optionA);
        if (this.optionB != null) optionsList.add(this.optionB);
        if (this.optionC != null) optionsList.add(this.optionC);
        if (this.optionD != null) optionsList.add(this.optionD);

        // Chuyển đổi difficultyLevel từ String sang int
        int difficultyLevelInt = 0; // Giá trị mặc định
        if (this.difficultyLevel != null) {
            switch (this.difficultyLevel.toLowerCase()) {
                case "easy":
                    difficultyLevelInt = 1;
                    break;
                case "medium":
                    difficultyLevelInt = 2;
                    break;
                case "hard":
                    difficultyLevelInt = 3;
                    break;
                default:
                    difficultyLevelInt = 0; // Hoặc một giá trị mặc định khác cho "Unknown"
            }
        }

        // Định dạng creationDate từ Date sang String
        String formattedCreationDate = null;
        if (this.creationDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Đảm bảo format này khớp với DTO
            formattedCreationDate = sdf.format(this.creationDate);
        }

        return new QuestionDTO(
            this.questionId != null ? this.questionId : 0, // Xử lý Integer sang int, tránh NullPointerException
            this.questionContent,
            this.questionType,
            optionsList,
            this.correctAnswer,
            difficultyLevelInt,          // <-- ĐÃ SỬA: truyền int
            this.creatorUserId != null ? this.creatorUserId : 0, // <-- ĐÃ SỬA: Đúng thứ tự và xử lý Integer
            this.creatorUsername,        // <-- ĐÃ SỬA: Đúng thứ tự
            this.subjectId != null ? this.subjectId : 0, // <-- ĐÃ SỬA: Đúng thứ tự và xử lý Integer
            this.subjectName,            // <-- ĐÃ SỬA: Đúng thứ tự
            formattedCreationDate        // <-- ĐÃ SỬA: truyền String
        );
    }

    @Override
    public String toString() {
        return "Question{" +
                "questionId=" + questionId +
                ", questionContent='" + questionContent.substring(0, Math.min(questionContent.length(), 50)) + "...'" +
                ", subjectName='" + subjectName + '\'' +
                ", creatorUsername='" + creatorUsername + '\'' +
                '}';
    }
}