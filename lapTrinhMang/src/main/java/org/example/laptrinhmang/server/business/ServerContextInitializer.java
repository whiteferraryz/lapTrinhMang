// src/main/java/org/example/laptrinhmang/server/business/ServerContextInitializer.java
package org.example.laptrinhmang.server.business;

import org.example.laptrinhmang.server.dataaccess.DatabaseConnectionManager;
import org.example.laptrinhmang.server.dataaccess.ExamDAO;
import org.example.laptrinhmang.server.dataaccess.QuestionDAO;
import org.example.laptrinhmang.server.dataaccess.SubjectDAO;
import org.example.laptrinhmang.server.dataaccess.UserDAO;

import java.sql.Connection; // Vẫn cần vì DatabaseConnectionManager.getConnection() trả về Connection
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerContextInitializer {
    private static final Logger LOGGER = Logger.getLogger(ServerContextInitializer.class.getName());

    // connection không còn là static property duy nhất được quản lý trực tiếp ở đây nữa,
    // mà được DatabaseConnectionManager quản lý.
    // Tuy nhiên, chúng ta vẫn cần một biến Connection cục bộ trong initialize()
    // để truyền cho các DAO nếu các DAO này cần Connection trực tiếp
    // (nhưng hiện tại các DAO của bạn lại nhận DatabaseConnectionManager).
    // Do đó, biến connection static này có thể không cần thiết nữa.
    // Tôi sẽ loại bỏ nó để tránh nhầm lẫn nếu nó không được dùng consistent.
    // private static Connection connection; // <-- Xóa hoặc để nguyên nếu có mục đích khác

    private static DatabaseConnectionManager connectionManager; // <-- THÊM DÒNG NÀY

    private static UserDAO userDAO;
    private static SubjectDAO subjectDAO;
    private static QuestionDAO questionDAO;
    private static ExamDAO examDAO;

    private static UserAccountService userAccountService;
    private static SubjectService subjectService;
    private static QuestionBankService questionBankService;
    private static ExamManagementService examManagementService;
    // Thêm UserManagementService nếu bạn có nó (tôi sẽ giả định bạn dùng)
    private static UserManagementService userManagementService; 


    private ServerContextInitializer() {}

    public static synchronized void initialize() {
        // Thay vì kiểm tra connection, kiểm tra connectionManager
        if (connectionManager == null) {
            try {
                // Khởi tạo DatabaseConnectionManager đầu tiên
                connectionManager = new DatabaseConnectionManager(); // <-- Sửa lỗi 1. Bạn cần instance để truyền cho DAO

                // Các DAO của bạn nhận DatabaseConnectionManager chứ không phải Connection trực tiếp
                // Ví dụ: userDAO = new UserDAO(connection); sẽ SAI.
                // Nó phải là userDAO = new UserDAO(connectionManager);

                // Khởi tạo DAOs
                userDAO = new UserDAO(connectionManager); // <-- Đã sửa lỗi 2 & 3
                subjectDAO = new SubjectDAO(connectionManager); // <-- Sửa lỗi: Cần constructor này cho SubjectDAO
                questionDAO = new QuestionDAO(connectionManager); // <-- Sửa lỗi: Cần constructor này cho QuestionDAO
                examDAO = new ExamDAO(connectionManager); // <-- Đã sửa lỗi 3

                LOGGER.info("Kết nối cơ sở dữ liệu và khởi tạo DAOs thành công.");

                // Khởi tạo Services, truyền các DAO phụ thuộc vào
                userAccountService = new UserAccountService(userDAO);
                subjectService = new SubjectService(subjectDAO);
                questionBankService = new QuestionBankService(questionDAO);
                examManagementService = new ExamManagementService(examDAO);
                // Khởi tạo UserManagementService
                userManagementService = new UserManagementService(userDAO);

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Lỗi khởi tạo ServerContext: " + e.getMessage(), e);
                System.exit(1);
            }
        }
    }

    public static UserAccountService getUserAccountService() {
        if (userAccountService == null) initialize();
        return userAccountService;
    }

    public static SubjectService getSubjectService() {
        if (subjectService == null) initialize();
        return subjectService;
    }

    public static QuestionBankService getQuestionBankService() {
        if (questionBankService == null) initialize();
        return questionBankService;
    }

    public static ExamManagementService getExamManagementService() {
        if (examManagementService == null) initialize();
        return examManagementService;
    }

    public static UserManagementService getUserManagementService() {
        if (userManagementService == null) initialize();
        return userManagementService;
    }

    public static void shutdown() {
        if (connectionManager != null) {
            // Không cần đóng Connection riêng lẻ nữa vì DAO không giữ Connection lâu.
            // Nếu bạn có một Connection pool, việc shutdown sẽ khác.
            // Với thiết kế hiện tại, DatabaseConnectionManager chỉ đóng các Connection riêng lẻ khi release.
            // Nếu bạn muốn đảm bảo tất cả các kết nối đang mở được đóng khi shutdown,
            // bạn cần có logic trong DatabaseConnectionManager để quản lý pool (nếu có)
            // hoặc đơn giản là chỉ logging.
            LOGGER.info("ServerContextInitializer shutdown complete.");
        }
    }
}