package org.example.laptrinhmang.server.model;

import java.sql.*;

public class UserDAO {
    private final String URL = "jdbc:mysql://localhost:3306/ltm?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private final String USER = "root"; // hoặc user MySQL của bạn
    private final String PASS = "your_password"; // sửa lại mật khẩu của bạn

    public boolean registerUser(User user) {
        String sql = "INSERT INTO Users (username, password_hash, fullname, gender, dob, role_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash()); // hash từ client
            stmt.setString(3, user.getFullname());
            stmt.setString(4, user.getGender());
            stmt.setDate(5, Date.valueOf(user.getDob()));
            stmt.setInt(6, 2); // mặc định role_id là 2 cho người dùng bình thường

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("DB Error: " + e.getMessage());
            return false;
        }
    }
}