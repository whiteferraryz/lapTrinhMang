package org.example.laptrinhmang.server.dataaccess;

import org.example.laptrinhmang.common.dto.QuestionDTO;
import org.example.laptrinhmang.common.model.Subject; // Cần import nếu dùng Subject
import org.example.laptrinhmang.common.util.Constants; // Cần import nếu dùng Constants

import java.sql.*;
import java.text.SimpleDateFormat; // <-- THÊM IMPORT NÀY
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QuestionDAO {

    private static final Logger LOGGER = Logger.getLogger(QuestionDAO.class.getName());
    private final DatabaseConnectionManager connectionManager;

    public QuestionDAO(DatabaseConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    /**
     * Thêm một câu hỏi mới vào cơ sở dữ liệu.
     *
     * @param questionDTO Đối tượng QuestionDTO chứa thông tin câu hỏi.
     * @return QuestionDTO với ID được tạo, hoặc null nếu thêm thất bại.
     */
    public QuestionDTO addQuestion(QuestionDTO questionDTO) {
        String sql = "INSERT INTO questions (content, question_type, options, correct_answer, difficulty_level, creator_user_id, subject_id, creation_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, questionDTO.getContent());
                ps.setString(2, questionDTO.getQuestionType());
                ps.setString(3, String.join("|", questionDTO.getOptions()));
                ps.setString(4, questionDTO.getCorrectAnswer());
                ps.setInt(5, questionDTO.getDifficultyLevel()); // <-- ĐÃ SỬA: Không gọi mapDifficultyLevelToInt nữa
                ps.setInt(6, questionDTO.getCreatorUserId()); // <-- ĐÃ SỬA: Dùng getCreatorUserId
                ps.setInt(7, questionDTO.getSubjectId());
                ps.setTimestamp(8, new Timestamp(System.currentTimeMillis())); // <-- ĐÃ SỬA: Set timestamp tại server
                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            questionDTO.setQuestionId(rs.getInt(1)); // <-- ĐÃ SỬA: Dùng setQuestionId
                            LOGGER.info("Đã thêm câu hỏi mới với ID: " + questionDTO.getQuestionId());
                            return questionDTO;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm câu hỏi vào CSDL: " + e.getMessage(), e);
        } finally {
            connectionManager.releaseConnection(conn);
        }
        return null;
    }

    /**
     * Cập nhật thông tin của một câu hỏi hiện có.
     *
     * @param questionDTO Đối tượng QuestionDTO chứa thông tin cập nhật (phải có questionId).
     * @return true nếu cập nhật thành công, false nếu thất bại.
     */
    public boolean updateQuestion(QuestionDTO questionDTO) {
        String sql = "UPDATE questions SET content = ?, question_type = ?, options = ?, correct_answer = ?, difficulty_level = ?, creator_user_id = ?, subject_id = ? WHERE question_id = ?";
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, questionDTO.getContent());
                ps.setString(2, questionDTO.getQuestionType());
                ps.setString(3, String.join("|", questionDTO.getOptions()));
                ps.setString(4, questionDTO.getCorrectAnswer());
                ps.setInt(5, questionDTO.getDifficultyLevel()); // <-- ĐÃ SỬA: Không gọi mapDifficultyLevelToInt nữa
                ps.setInt(6, questionDTO.getCreatorUserId()); // <-- ĐÃ SỬA: Dùng getCreatorUserId
                ps.setInt(7, questionDTO.getSubjectId());
                ps.setInt(8, questionDTO.getQuestionId());

                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    LOGGER.info("Đã cập nhật câu hỏi ID: " + questionDTO.getQuestionId());
                    return true;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật câu hỏi ID " + questionDTO.getQuestionId() + ": " + e.getMessage(), e);
        } finally {
            connectionManager.releaseConnection(conn);
        }
        return false;
    }

    /**
     * Xóa một câu hỏi khỏi cơ sở dữ liệu.
     *
     * @param questionId ID của câu hỏi cần xóa.
     * @return true nếu xóa thành công, false nếu thất bại.
     */
    public boolean deleteQuestion(int questionId) {
        String sql = "DELETE FROM questions WHERE question_id = ?";
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, questionId);
                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    LOGGER.info("Đã xóa câu hỏi ID: " + questionId);
                    return true;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi xóa câu hỏi ID " + questionId + ": " + e.getMessage(), e);
        } finally {
            connectionManager.releaseConnection(conn);
        }
        return false;
    }

    /**
     * Lấy một câu hỏi theo ID.
     *
     * @param questionId ID của câu hỏi.
     * @return QuestionDTO nếu tìm thấy, null nếu không tìm thấy.
     */
    public QuestionDTO getQuestionById(int questionId) {
        String sql = "SELECT q.*, u.username as creator_username, s.subject_name FROM questions q " +
                     "JOIN users u ON q.creator_user_id = u.user_id " +
                     "JOIN subjects s ON q.subject_id = s.subject_id " +
                     "WHERE q.question_id = ?";
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, questionId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapResultSetToQuestionDTO(rs);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy câu hỏi theo ID " + questionId + ": " + e.getMessage(), e);
        } finally {
            connectionManager.releaseConnection(conn);
        }
        return null;
    }

    /**
     * Lấy tất cả các câu hỏi từ cơ sở dữ liệu.
     *
     * @return Danh sách các QuestionDTO.
     */
    public List<QuestionDTO> getAllQuestions() {
        List<QuestionDTO> questions = new ArrayList<>();
        String sql = "SELECT q.*, u.username as creator_username, s.subject_name FROM questions q " +
                     "JOIN users u ON q.creator_user_id = u.user_id " +
                     "JOIN subjects s ON q.subject_id = s.subject_id";
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    questions.add(mapResultSetToQuestionDTO(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy tất cả câu hỏi từ CSDL: " + e.getMessage(), e);
        } finally {
            connectionManager.releaseConnection(conn);
        }
        return questions;
    }

    /**
     * Tìm kiếm câu hỏi theo nội dung, loại hoặc tên người tạo.
     *
     * @param searchTerm Chuỗi tìm kiếm.
     * @return Danh sách các QuestionDTO khớp với tiêu chí tìm kiếm.
     */
    public List<QuestionDTO> searchQuestions(String searchTerm) {
        List<QuestionDTO> questions = new ArrayList<>();
        String sql = "SELECT q.*, u.username as creator_username, s.subject_name FROM questions q " +
                     "JOIN users u ON q.creator_user_id = u.user_id " +
                     "JOIN subjects s ON q.subject_id = s.subject_id " +
                     "WHERE q.content LIKE ? OR q.question_type LIKE ? OR u.username LIKE ?";
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                String searchPattern = "%" + searchTerm + "%";
                ps.setString(1, searchPattern);
                ps.setString(2, searchPattern);
                ps.setString(3, searchPattern);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        questions.add(mapResultSetToQuestionDTO(rs));
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm kiếm câu hỏi: " + e.getMessage(), e);
        } finally {
            connectionManager.releaseConnection(conn);
        }
        return questions;
    }

    /**
     * Lấy tất cả các môn học. Cần cho Combobox trên client khi tạo/chỉnh sửa câu hỏi.
     * Lưu ý: Phương thức này có thể được chuyển sang SubjectDAO nếu bạn muốn tách biệt rõ ràng.
     * @return List of Subject objects.
     */
    public List<Subject> getAllSubjects() {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT subject_id, subject_name, description FROM subjects";
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    subjects.add(new Subject(
                        rs.getInt("subject_id"),
                        rs.getString("subject_name"),
                        rs.getString("description")
                    ));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy tất cả môn học từ CSDL: " + e.getMessage(), e);
        } finally {
            connectionManager.releaseConnection(conn);
        }
        return subjects;
    }

    /**
     * Lấy môn học theo ID.
     * Lưu ý: Phương thức này có thể được chuyển sang SubjectDAO nếu bạn muốn tách biệt rõ ràng.
     * @param subjectId ID của môn học.
     * @return Subject object nếu tìm thấy, null nếu không.
     */
    public Subject getSubjectById(int subjectId) {
        String sql = "SELECT subject_id, subject_name, description FROM subjects WHERE subject_id = ?";
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, subjectId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new Subject(
                            rs.getInt("subject_id"),
                            rs.getString("subject_name"),
                            rs.getString("description")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy môn học theo ID " + subjectId + ": " + e.getMessage(), e);
        } finally {
            connectionManager.releaseConnection(conn);
        }
        return null;
    }

    /**
     * Phương thức helper để ánh xạ ResultSet thành QuestionDTO.
     */
    private QuestionDTO mapResultSetToQuestionDTO(ResultSet rs) throws SQLException {
        String optionsString = rs.getString("options");
        List<String> optionsList = new ArrayList<>();
        if (optionsString != null && !optionsString.isEmpty()) {
            optionsList = Arrays.asList(optionsString.split("\\|"));
        }

        // Định dạng ngày tháng từ Timestamp sang String
        Timestamp creationTimestamp = rs.getTimestamp("creation_date");
        String formattedCreationDate = null;
        if (creationTimestamp != null) {
            // Đảm bảo định dạng này khớp với định dạng bạn muốn trong QuestionDTO (vd: "yyyy-MM-dd HH:mm:ss")
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formattedCreationDate = sdf.format(creationTimestamp);
        }

        return new QuestionDTO(
            rs.getInt("question_id"),
            rs.getString("content"),
            rs.getString("question_type"),
            optionsList,
            rs.getString("correct_answer"),
            rs.getInt("difficulty_level"),       // <-- ĐÃ SỬA: Truyền int trực tiếp
            rs.getInt("creator_user_id"),        // <-- ĐÃ SỬA: Đúng thứ tự tham số
            rs.getString("creator_username"),    // <-- ĐÃ SỬA: Đúng thứ tự tham số
            rs.getInt("subject_id"),             // <-- ĐÃ SỬA: Đúng thứ tự tham số
            rs.getString("subject_name"),        // <-- ĐÃ SỬA: Đúng thứ tự tham số
            formattedCreationDate                // <-- ĐÃ SỬA: Truyền String đã định dạng
        );
    }

    // Helper method để ánh xạ độ khó từ String (DTO) sang int (DB)
    private int mapDifficultyLevelToInt(String level) {
        if (level == null) return 0;
        switch (level.toLowerCase()) {
            case "easy": return 1;
            case "medium": return 2;
            case "hard": return 3;
            default: return 0;
        }
    }

    // Helper method để ánh xạ độ khó từ int (DB) sang String (DTO)
    private String mapIntToDifficultyLevel(int level) {
        switch (level) {
            case 1: return "Easy";
            case 2: return "Medium";
            case 3: return "Hard";
            default: return "Unknown";
        }
    }
}