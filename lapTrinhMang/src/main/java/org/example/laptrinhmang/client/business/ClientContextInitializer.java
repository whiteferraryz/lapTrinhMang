package org.example.laptrinhmang.client.business;

import org.example.laptrinhmang.client.network.ServerCommunicationConnector;
import org.example.laptrinhmang.common.dto.UserDTO;
import org.example.laptrinhmang.common.model.User;

import java.io.IOException; // Vẫn cần import IOException cho sendRequest, nhưng không phải cho connect()
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientContextInitializer {
    private static final Logger LOGGER = Logger.getLogger(ClientContextInitializer.class.getName());

    private static ServerCommunicationConnector serverCommunicationConnector;
    private static UserAuthenticationClientService authClientService;
    private static TeacherExamManagementClientService teacherExamManagementClientService;
    private static TeacherQuestionBankClientService teacherQuestionBankClientService;

    private static UserDTO currentLoggedInUser;

    private ClientContextInitializer() {}

    public static synchronized void initialize() {
        if (serverCommunicationConnector == null) {
            serverCommunicationConnector = new ServerCommunicationConnector();
            // ĐÃ SỬA: GỌI PHƯƠNG THỨC connect() KHÔNG CÓ THAM SỐ
            // Loại bỏ khối try-catch cho IOException ở đây vì connect() của ServerCommunicationConnector đã xử lý
            if (!serverCommunicationConnector.connect()) {
                LOGGER.log(Level.SEVERE, "Không thể kết nối đến server. Ứng dụng sẽ thoát.");
                // Có thể hiển thị JOptionPane hoặc xử lý khác trước khi thoát
                // JOptionPane.showMessageDialog(null, "Không thể kết nối đến server. Vui lòng thử lại sau.", "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }
        // Khởi tạo các service nếu chưa có
        if (authClientService == null) {
            authClientService = new UserAuthenticationClientService(serverCommunicationConnector);
        }
        if (teacherExamManagementClientService == null) {
            teacherExamManagementClientService = new TeacherExamManagementClientService(serverCommunicationConnector);
        }
        if (teacherQuestionBankClientService == null) {
            teacherQuestionBankClientService = new TeacherQuestionBankClientService(serverCommunicationConnector);
        }
    }

    public static ServerCommunicationConnector getServerCommunicationConnector() {
        return serverCommunicationConnector;
    }

    public static UserAuthenticationClientService getUserAuthenticationService() {
        return authClientService;
    }

    public static TeacherExamManagementClientService getTeacherExamManagementService() {
        return teacherExamManagementClientService;
    }

    public static TeacherQuestionBankClientService getTeacherQuestionBankService() {
        return teacherQuestionBankClientService;
    }

    public static void setCurrentLoggedInUser(UserDTO user) {
        currentLoggedInUser = user;
        LOGGER.info("Người dùng đăng nhập đã được thiết lập trong ClientContextInitializer: " + (user != null ? user.getUsername() : "null"));
    }

    public static UserDTO getCurrentLoggedInUser() {
        return currentLoggedInUser;
    }
}