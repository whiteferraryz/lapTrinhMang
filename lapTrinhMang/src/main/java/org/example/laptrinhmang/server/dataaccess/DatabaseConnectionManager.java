package org.example.laptrinhmang.server.dataaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnectionManager {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnectionManager.class.getName());
    private static String DB_URL;
    private static String DB_USERNAME;
    private static String DB_PASSWORD;

    static {
        try {
            Properties prop = new Properties();
            InputStream inputStream = DatabaseConnectionManager.class.getClassLoader().getResourceAsStream("application.properties");
            if (inputStream != null) {
                prop.load(inputStream);
                DB_URL = prop.getProperty("db.url");
                DB_USERNAME = prop.getProperty("db.username");
                DB_PASSWORD = prop.getProperty("db.password");
                LOGGER.info("Đã tải cấu hình cơ sở dữ liệu từ application.properties.");
            } else {
                LOGGER.log(Level.SEVERE, "Không tìm thấy file application.properties.");
                throw new RuntimeException("Không tìm thấy file cấu hình application.properties.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tải cấu hình cơ sở dữ liệu: " + e.getMessage(), e);
            throw new RuntimeException("Không thể tải cấu hình cơ sở dữ liệu.", e);
        }
    }

    /**
     * Lấy một kết nối mới từ cơ sở dữ liệu.
     * Lưu ý: Phương thức này tạo một kết nối mới mỗi lần được gọi.
     * Trong ứng dụng thực tế, nên sử dụng Connection Pool để quản lý hiệu quả hơn.
     * @return Một đối tượng Connection đã mở.
     * @throws SQLException Nếu có lỗi khi thiết lập kết nối.
     */
    // THAY ĐỔI THÀNH STATIC
    public static Connection getConnection() throws SQLException { // <-- Đã sửa 
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            LOGGER.fine("Đã mở một kết nối mới đến CSDL.");
            return conn;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Không thể kết nối tới cơ sở dữ liệu: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Giải phóng (đóng) một kết nối cơ sở dữ liệu.
     * Đây là phương thức quan trọng để tránh rò rỉ tài nguyên.
     * @param connection Đối tượng Connection cần đóng.
     */
    // THAY ĐỔI THÀNH STATIC
    public static void releaseConnection(Connection connection) { // <-- Đã sửa 
        if (connection != null) {
            try {
                connection.close();
                LOGGER.fine("Đã đóng một kết nối CSDL.");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Lỗi khi đóng kết nối cơ sở dữ liệu: " + e.getMessage(), e);
            }
        }
    }
}