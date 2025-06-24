// src/main/java/com/yourcompany/examapp/server/dataaccess/ExamDAO.java
package org.example.laptrinhmang.server.dataaccess;

import org.example.laptrinhmang.common.model.Exam;
import org.example.laptrinhmang.common.model.ExamQuestion; 
import org.example.laptrinhmang.common.model.Question; 
import org.example.laptrinhmang.common.model.Subject; 
import org.example.laptrinhmang.common.model.User;   
import org.example.laptrinhmang.common.util.Constants; 
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExamDAO {
    private static final Logger LOGGER = Logger.getLogger(ExamDAO.class.getName());

    private final DatabaseConnectionManager connectionManager;

    public ExamDAO(DatabaseConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    /**
     * Thêm một bài thi mới vào cơ sở dữ liệu.
     * Bao gồm thêm thông tin bài thi và các câu hỏi liên kết.
     *
     * @param exam Đối tượng Exam chứa thông tin bài thi và danh sách ID câu hỏi.
     * @return Đối tượng Exam đã được thêm với ID mới được tạo, hoặc null nếu lỗi.
     * @throws SQLException Nếu có lỗi khi truy cập CSDL.
     */
    public Exam addExam(Exam exam) throws SQLException {
        String insertExamSQL = "INSERT INTO exams (exam_name, subject_id, duration_minutes, creation_date, creator_user_id, status) VALUES (?, ?, ?, ?, ?, ?)";
        String insertExamQuestionSQL = "INSERT INTO exam_questions (exam_id, question_id) VALUES (?, ?)";

        Connection conn = null;
        PreparedStatement examPst = null;
        PreparedStatement examQuestionPst = null;
        ResultSet rs = null;

        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction

            // 1. Thêm thông tin bài thi vào bảng 'exams'
            examPst = conn.prepareStatement(insertExamSQL, Statement.RETURN_GENERATED_KEYS);
            examPst.setString(1, exam.getExamName());
            examPst.setInt(2, exam.getSubjectId());
            examPst.setInt(3, exam.getDurationMinutes());
            examPst.setTimestamp(4, new Timestamp(exam.getCreationDate().getTime()));
            examPst.setInt(5, exam.getCreatorUserId());
            examPst.setString(6, exam.getStatus());

            int affectedRows = examPst.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Thêm bài thi thất bại, không có hàng nào bị ảnh hưởng.");
            }

            rs = examPst.getGeneratedKeys();
            if (rs.next()) {
                exam.setExamId(rs.getInt(1)); // Gán ID tự tạo cho đối tượng Exam
            } else {
                throw new SQLException("Thêm bài thi thất bại, không lấy được ID.");
            }

            // 2. Thêm các câu hỏi liên kết vào bảng 'exam_questions'
            if (exam.getQuestionIds() != null && !exam.getQuestionIds().isEmpty()) {
                examQuestionPst = conn.prepareStatement(insertExamQuestionSQL);
                for (Integer questionId : exam.getQuestionIds()) {
                    examQuestionPst.setInt(1, exam.getExamId());
                    examQuestionPst.setInt(2, questionId);
                    examQuestionPst.addBatch(); // Thêm vào batch để thực hiện cùng lúc
                }
                examQuestionPst.executeBatch(); // Thực thi tất cả các lệnh trong batch
            }

            conn.commit(); // Hoàn thành transaction
            LOGGER.info("Đã thêm bài thi thành công với ID: " + exam.getExamId());
            return exam;
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // Rollback nếu có lỗi
            }
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm bài thi vào CSDL: " + e.getMessage(), e);
            throw e;
        } finally {
            closeResources(rs, examPst);
            closeResources(null, examQuestionPst);
            if (conn != null) {
                conn.setAutoCommit(true); // Trả lại chế độ auto-commit
                connectionManager.releaseConnection(conn);
            }
        }
    }

    /**
     * Lấy một bài thi theo ID.
     * Bao gồm cả thông tin về các câu hỏi trong bài thi đó.
     *
     * @param examId ID của bài thi cần lấy.
     * @return Đối tượng Exam hoặc null nếu không tìm thấy.
     * @throws SQLException Nếu có lỗi khi truy cập CSDL.
     */
    public Exam getExamById(int examId) throws SQLException {
        String sql = "SELECT e.*, s.subject_name, u.username as creator_username " +
                     "FROM exams e " +
                     "JOIN subjects s ON e.subject_id = s.subject_id " +
                     "JOIN users u ON e.creator_user_id = u.user_id " +
                     "WHERE e.exam_id = ?";
        String questionsSql = "SELECT q.question_id, q.question_content, q.question_type, q.option_a, q.option_b, q.option_c, q.option_d, q.correct_answer, q.difficulty_level, q.creator_user_id, sq.subject_name " +
                              "FROM exam_questions eq " +
                              "JOIN questions q ON eq.question_id = q.question_id " +
                              "JOIN subjects sq ON q.subject_id = sq.subject_id " +
                              "WHERE eq.exam_id = ?";

        Connection conn = null;
        PreparedStatement examPst = null;
        PreparedStatement questionsPst = null;
        ResultSet examRs = null;
        ResultSet questionsRs = null;
        Exam exam = null;

        try {
            conn = connectionManager.getConnection();

            examPst = conn.prepareStatement(sql);
            examPst.setInt(1, examId);
            examRs = examPst.executeQuery();

            if (examRs.next()) {
                exam = new Exam();
                exam.setExamId(examRs.getInt("exam_id"));
                exam.setExamName(examRs.getString("exam_name"));
                exam.setSubjectId(examRs.getInt("subject_id"));
                exam.setSubjectName(examRs.getString("subject_name"));
                exam.setDurationMinutes(examRs.getInt("duration_minutes"));
                exam.setCreationDate(examRs.getTimestamp("creation_date"));
                exam.setCreatorUserId(examRs.getInt("creator_user_id"));
                exam.setCreatorUsername(examRs.getString("creator_username"));
                exam.setStatus(examRs.getString("status"));

                // Lấy danh sách câu hỏi cho bài thi này
                questionsPst = conn.prepareStatement(questionsSql);
                questionsPst.setInt(1, examId);
                questionsRs = questionsPst.executeQuery();
                List<Question> questions = new ArrayList<>();
                List<Integer> questionIds = new ArrayList<>();
                while (questionsRs.next()) {
                    Question question = new Question();
                    question.setQuestionId(questionsRs.getInt("question_id"));
                    question.setQuestionContent(questionsRs.getString("question_content"));
                    question.setQuestionType(questionsRs.getString("question_type"));
                    question.setOptionA(questionsRs.getString("option_a"));
                    question.setOptionB(questionsRs.getString("option_b"));
                    question.setOptionC(questionsRs.getString("option_c"));
                    question.setOptionD(questionsRs.getString("option_d"));
                    question.setCorrectAnswer(questionsRs.getString("correct_answer"));
                    question.setDifficultyLevel(questionsRs.getString("difficulty_level"));
                    question.setCreatorUserId(questionsRs.getInt("creator_user_id"));
                    question.setSubjectName(questionsRs.getString("subject_name")); // Lấy subject_name từ câu hỏi

                    questions.add(question);
                    questionIds.add(question.getQuestionId());
                }
                exam.setQuestions(questions);
                exam.setQuestionIds(questionIds);
            }
        } finally {
            closeResources(examRs, examPst);
            closeResources(questionsRs, questionsPst);
            connectionManager.releaseConnection(conn);
        }
        return exam;
    }

    /**
     * Lấy tất cả các bài thi có trong hệ thống.
     * Chỉ lấy thông tin cơ bản của bài thi, không tải chi tiết câu hỏi để tránh tải quá nhiều dữ liệu.
     * Để lấy chi tiết câu hỏi, dùng getExamById.
     *
     * @return Danh sách các đối tượng Exam.
     * @throws SQLException Nếu có lỗi khi truy cập CSDL.
     */
    public List<Exam> getAllExams() throws SQLException {
        List<Exam> exams = new ArrayList<>();
        String sql = "SELECT e.*, s.subject_name, u.username as creator_username " +
                     "FROM exams e " +
                     "JOIN subjects s ON e.subject_id = s.subject_id " +
                     "JOIN users u ON e.creator_user_id = u.user_id"; // Join để lấy tên môn học và tên người tạo

        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            conn = connectionManager.getConnection();
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            while (rs.next()) {
                Exam exam = new Exam();
                exam.setExamId(rs.getInt("exam_id"));
                exam.setExamName(rs.getString("exam_name"));
                exam.setSubjectId(rs.getInt("subject_id"));
                exam.setSubjectName(rs.getString("subject_name")); // Lấy tên môn học
                exam.setDurationMinutes(rs.getInt("duration_minutes"));
                exam.setCreationDate(rs.getTimestamp("creation_date"));
                exam.setCreatorUserId(rs.getInt("creator_user_id"));
                exam.setCreatorUsername(rs.getString("creator_username")); // Lấy tên người tạo
                exam.setStatus(rs.getString("status"));
                // Không tải danh sách câu hỏi ở đây để tối ưu hiệu suất
                exams.add(exam);
            }
        } finally {
            closeResources(rs, pst);
            connectionManager.releaseConnection(conn);
        }
        return exams;
    }

    /**
     * Cập nhật thông tin của một bài thi hiện có.
     * Bao gồm cập nhật thông tin chính của bài thi và cập nhật lại danh sách câu hỏi liên kết.
     *
     * @param exam Đối tượng Exam chứa thông tin cần cập nhật.
     * @return true nếu cập nhật thành công, false nếu không.
     * @throws SQLException Nếu có lỗi khi truy cập CSDL.
     */
    public boolean updateExam(Exam exam) throws SQLException {
        String updateExamSQL = "UPDATE exams SET exam_name = ?, subject_id = ?, duration_minutes = ?, status = ? WHERE exam_id = ?";
        String deleteExamQuestionsSQL = "DELETE FROM exam_questions WHERE exam_id = ?";
        String insertExamQuestionSQL = "INSERT INTO exam_questions (exam_id, question_id) VALUES (?, ?)";

        Connection conn = null;
        PreparedStatement examPst = null;
        PreparedStatement deletePst = null;
        PreparedStatement insertPst = null;

        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction

            // 1. Cập nhật thông tin bài thi
            examPst = conn.prepareStatement(updateExamSQL);
            examPst.setString(1, exam.getExamName());
            examPst.setInt(2, exam.getSubjectId());
            examPst.setInt(3, exam.getDurationMinutes());
            examPst.setString(4, exam.getStatus());
            examPst.setInt(5, exam.getExamId());
            int affectedRows = examPst.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Cập nhật bài thi thất bại, bài thi không tồn tại hoặc không có thay đổi.");
            }

            // 2. Xóa các câu hỏi cũ liên kết với bài thi này
            deletePst = conn.prepareStatement(deleteExamQuestionsSQL);
            deletePst.setInt(1, exam.getExamId());
            deletePst.executeUpdate();

            // 3. Thêm lại các câu hỏi mới (hoặc đã cập nhật)
            if (exam.getQuestionIds() != null && !exam.getQuestionIds().isEmpty()) {
                insertPst = conn.prepareStatement(insertExamQuestionSQL);
                for (Integer questionId : exam.getQuestionIds()) {
                    insertPst.setInt(1, exam.getExamId());
                    insertPst.setInt(2, questionId);
                    insertPst.addBatch();
                }
                insertPst.executeBatch();
            }

            conn.commit(); // Hoàn thành transaction
            LOGGER.info("Đã cập nhật bài thi thành công với ID: " + exam.getExamId());
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // Rollback nếu có lỗi
            }
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật bài thi trong CSDL: " + e.getMessage(), e);
            throw e;
        } finally {
            closeResources(null, examPst);
            closeResources(null, deletePst);
            closeResources(null, insertPst);
            if (conn != null) {
                conn.setAutoCommit(true);
                connectionManager.releaseConnection(conn);
            }
        }
    }


    /**
     * Xóa một bài thi khỏi cơ sở dữ liệu.
     * Cũng sẽ xóa các liên kết trong exam_questions.
     *
     * @param examId ID của bài thi cần xóa.
     * @return true nếu xóa thành công, false nếu không.
     * @throws SQLException Nếu có lỗi khi truy cập CSDL.
     */
    public boolean deleteExam(int examId) throws SQLException {
        String deleteExamQuestionsSQL = "DELETE FROM exam_questions WHERE exam_id = ?";
        String deleteExamSQL = "DELETE FROM exams WHERE exam_id = ?";

        Connection conn = null;
        PreparedStatement deleteQuestionsPst = null;
        PreparedStatement deleteExamPst = null;

        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction

            // 1. Xóa các liên kết trong exam_questions trước
            deleteQuestionsPst = conn.prepareStatement(deleteExamQuestionsSQL);
            deleteQuestionsPst.setInt(1, examId);
            deleteQuestionsPst.executeUpdate();

            // 2. Xóa bài thi
            deleteExamPst = conn.prepareStatement(deleteExamSQL);
            deleteExamPst.setInt(1, examId);
            int affectedRows = deleteExamPst.executeUpdate();

            conn.commit(); // Hoàn thành transaction
            LOGGER.info("Đã xóa bài thi thành công với ID: " + examId);
            return affectedRows > 0;
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // Rollback nếu có lỗi
            }
            LOGGER.log(Level.SEVERE, "Lỗi khi xóa bài thi khỏi CSDL: " + e.getMessage(), e);
            throw e;
        } finally {
            closeResources(null, deleteQuestionsPst);
            closeResources(null, deleteExamPst);
            if (conn != null) {
                conn.setAutoCommit(true);
                connectionManager.releaseConnection(conn);
            }
        }
    }

    // Phương thức đóng tài nguyên tiện ích
    private void closeResources(ResultSet rs, Statement stmt) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Lỗi khi đóng tài nguyên CSDL: " + e.getMessage(), e);
        }
    }
}