package org.example.laptrinhmang.server.dataaccess;

import org.example.laptrinhmang.common.model.Subject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SubjectDAO {

    private static final Logger LOGGER = Logger.getLogger(SubjectDAO.class.getName());
    // Thay đổi từ Connection sang DatabaseConnectionManager
    private final DatabaseConnectionManager connectionManager;

    // Thay đổi constructor để nhận DatabaseConnectionManager
    public SubjectDAO(DatabaseConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    /**
     * Lấy một môn học theo ID.
     * @param subjectId ID của môn học.
     * @return Subject object nếu tìm thấy, null nếu không.
     */
    public Subject getSubjectById(int subjectId) {
        String sql = "SELECT subject_id, subject_name, description FROM subjects WHERE subject_id = ?";
        Connection conn = null; // Khai báo biến Connection cục bộ
        try {
            conn = connectionManager.getConnection(); // Lấy Connection từ manager
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
            connectionManager.releaseConnection(conn); // Trả lại kết nối
        }
        return null;
    }

    /**
     * Lấy tất cả các môn học từ cơ sở dữ liệu.
     * @return Danh sách các Subject objects.
     */
    public List<Subject> getAllSubjects() {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT subject_id, subject_name, description FROM subjects ORDER BY subject_name";
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

    // Bạn có thể thêm các phương thức CRUD khác cho Subject ở đây nếu cần quản lý Subjects qua giao diện admin riêng
    // Ví dụ: addSubject, updateSubject, deleteSubject
}