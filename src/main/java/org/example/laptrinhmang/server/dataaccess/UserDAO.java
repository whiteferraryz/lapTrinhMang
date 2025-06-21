// src/main/java/org/example/laptrinhmang/server/dataaccess/UserDAO.java
package org.example.laptrinhmang.server.dataaccess;

import org.example.laptrinhmang.common.dto.UserDTO; // CẦN IMPORT LẠI UserDTO
import org.example.laptrinhmang.common.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // Cần import Statement cho closeResources
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO {
    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());
    // Thay vì giữ Connection, hãy giữ DatabaseConnectionManager
    private final DatabaseConnectionManager connectionManager;

    // Constructor phải nhận DatabaseConnectionManager
    public UserDAO(DatabaseConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    // --- Các phương thức đã có, đảm bảo chúng sử dụng connectionManager ---
    // (Tôi sẽ không lặp lại toàn bộ các phương thức đã có, chỉ ví dụ cách dùng connectionManager)

    // Ví dụ: Phương thức getUserByUsername() được sửa đổi
    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT user_id, username, password_hash, email, role_id, role_name FROM Users WHERE username = ?";
        Connection conn = null; // Khai báo Connection ở đây
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            conn = connectionManager.getConnection(); // Lấy Connection từ pool/manager
            statement = conn.prepareStatement(sql);
            statement.setString(1, username);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = new User();
                user.setUserId(resultSet.getInt("user_id")); // Chú ý: trong DB là 'user_id', trong code là 'id' (sẽ gây lỗi nếu không khớp)
                user.setUsername(resultSet.getString("username"));
                user.setPasswordHash(resultSet.getString("password_hash"));
                user.setEmail(resultSet.getString("email"));
                user.setRoleId(resultSet.getInt("role_id"));
                user.setRoleName(resultSet.getString("role_name"));
                LOGGER.info("DAO: Đã lấy user: " + username);
                return user;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "DAO: Lỗi khi lấy user '" + username + "': " + e.getMessage(), e);
            throw e;
        } finally {
            // Đóng ResultSet và Statement
            closeResources(resultSet, statement);
            // TRẢ CONNECTION VỀ POOL/MANAGER
            connectionManager.releaseConnection(conn);
        }
        return null;
    }

    // Ví dụ: Phương thức addUser() được sửa đổi
    public boolean addUser(User user) throws SQLException {
        String sql = "INSERT INTO Users (username, password_hash, email, role_id, role_name) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            conn = connectionManager.getConnection(); // Lấy Connection từ pool/manager
            statement = conn.prepareStatement(sql);
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPasswordHash());
            statement.setString(3, user.getEmail());
            statement.setInt(4, user.getRoleId());
            statement.setString(5, user.getRoleName());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.info("DAO: Đã thêm user: " + user.getUsername());
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "DAO: Lỗi khi thêm user '" + user.getUsername() + "': " + e.getMessage(), e);
            throw e;
        } finally {
            closeResources(null, statement);
            connectionManager.releaseConnection(conn);
        }
        return false;
    }

    // --- BƯỚC 2: THÊM CÁC PHƯƠNG THỨC BỊ THIẾU VÀO UserDAO ---

    // Phương thức getAllUsers
    public List<UserDTO> getAllUsers() throws SQLException {
        List<UserDTO> users = new ArrayList<>();
        // Đảm bảo tên cột trong SQL khớp với tên cột trong DB của bạn
        String sql = "SELECT user_id, username, email, role_id, role_name FROM Users";
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            conn = connectionManager.getConnection();
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                UserDTO user = new UserDTO();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setRoleId(rs.getInt("role_id"));
                user.setRoleName(rs.getString("role_name")); // Đảm bảo cột role_name tồn tại
                users.add(user);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy tất cả người dùng từ CSDL: " + e.getMessage(), e);
            throw e;
        } finally {
            closeResources(rs, pst);
            connectionManager.releaseConnection(conn);
        }
        return users;
    }

    // Phương thức updateUser
    public boolean updateUser(UserDTO userDTO) throws SQLException {
        // Bao gồm full_name nếu bạn có trường đó trong UserDTO và DB
        String sql = "UPDATE Users SET username = ?, email = ?, role_id = ?, role_name = ? WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pst = null;
        try {
            conn = connectionManager.getConnection();
            pst = conn.prepareStatement(sql);
            pst.setString(1, userDTO.getUsername());
            pst.setString(2, userDTO.getEmail());
            pst.setInt(3, userDTO.getRoleId());
            pst.setString(4, userDTO.getRoleName());
            pst.setInt(5, userDTO.getUserId());

            int affectedRows = pst.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật người dùng ID " + userDTO.getUserId() + " trong CSDL: " + e.getMessage(), e);
            // Ném lại lỗi để UserManagementService có thể bắt và trả về thông báo lỗi phù hợp
            throw e;
        } finally {
            closeResources(null, pst);
            connectionManager.releaseConnection(conn);
        }
    }

    // Phương thức deleteUserById
    public boolean deleteUserById(int userId) throws SQLException {
        String sql = "DELETE FROM Users WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pst = null;
        try {
            conn = connectionManager.getConnection();
            pst = conn.prepareStatement(sql);
            pst.setInt(1, userId);

            int affectedRows = pst.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi xóa người dùng ID " + userId + " khỏi CSDL: " + e.getMessage(), e);
            throw e;
        } finally {
            closeResources(null, pst);
            connectionManager.releaseConnection(conn);
        }
    }

    // Phương thức resetUserPassword
    public boolean resetUserPassword(int userId, String newPassword) throws SQLException {
        // CỰC KỲ QUAN TRỌNG: Bạn PHẢI hash mật khẩu trước khi lưu vào DB!
        // Ví dụ: String hashedPassword = org.example.laptrinhmang.common.util.PasswordHasher.hash(newPassword);
        // Tôi giả định bạn có một tiện ích băm mật khẩu. Nếu không, hãy tạo nó.
        // Để đơn giản hóa ví dụ, tôi sẽ lưu trực tiếp newPassword, NHƯNG ĐÂY LÀ ĐIỀU KHÔNG NÊN LÀM TRONG THỰC TẾ.
        String sql = "UPDATE Users SET password_hash = ? WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pst = null;
        try {
            conn = connectionManager.getConnection();
            pst = conn.prepareStatement(sql);
            pst.setString(1, newPassword); // THAY BẰNG hashedPassword nếu bạn đã hash
            pst.setInt(2, userId);

            int affectedRows = pst.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi đặt lại mật khẩu cho người dùng ID " + userId + " trong CSDL: " + e.getMessage(), e);
            throw e;
        } finally {
            closeResources(null, pst);
            connectionManager.releaseConnection(conn);
        }
    }

    // Phương thức tiện ích để đóng tài nguyên (đảm bảo nó có mặt)
    private void closeResources(ResultSet rs, Statement stmt) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Lỗi khi đóng tài nguyên CSDL: " + e.getMessage(), e);
        }
    }
}