package org.example.laptrinhmang.client.presentation;

import org.example.laptrinhmang.client.business.ClientContextInitializer;
import org.example.laptrinhmang.client.business.UserAuthenticationClientService;
import org.example.laptrinhmang.client.network.ServerCommunicationConnector;
import org.example.laptrinhmang.client.view.MainClientWindow;
import org.example.laptrinhmang.common.model.User;
import org.example.laptrinhmang.common.dto.ExamDTO;
import org.example.laptrinhmang.common.dto.UserDTO;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainApplicationFrame extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(MainApplicationFrame.class.getName());

    private JPanel cards;
    private CardLayout cardLayout;

    public static final String LOGIN_PANEL = "LoginPanel";
    public static final String REGISTRATION_PANEL = "RegistrationPanel";
    public static final String TEACHER_DASHBOARD_PANEL = "TeacherDashboardPanel";
    public static final String TEACHER_EXAM_LIST_PANEL = "TeacherExamListPanel";
    public static final String TEACHER_EXAM_CREATION_PANEL = "TeacherExamCreationPanel";
    public static final String TEACHER_EXAM_EDIT_PANEL = "TeacherExamEditPanel";

    private Map<String, JPanel> panelInstances;

    private ServerCommunicationConnector connector;
    private UserAuthenticationClientService authService;

    public MainApplicationFrame() {
        this("Hệ thống Quản lý Thi Trực tuyến");
    }

    public MainApplicationFrame(String title) {
        super(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);
        panelInstances = new HashMap<>();

        // Khởi tạo ClientContextInitializer để đảm bảo connector và các service sẵn sàng 
        ClientContextInitializer.initialize(); // 
        this.connector = ClientContextInitializer.getServerCommunicationConnector(); // 
        this.authService = ClientContextInitializer.getUserAuthenticationService(); // 

        UserLoginScreen loginScreen = new UserLoginScreen(this, authService);
        cards.add(loginScreen, LOGIN_PANEL);
        panelInstances.put(LOGIN_PANEL, loginScreen);

        UserRegistrationScreen registrationScreen = new UserRegistrationScreen(this, authService);
        cards.add(registrationScreen, REGISTRATION_PANEL);
        panelInstances.put(REGISTRATION_PANEL, registrationScreen);

        TeacherDashboardScreen teacherDashboardScreen = new TeacherDashboardScreen(this);
        cards.add(teacherDashboardScreen, TEACHER_DASHBOARD_PANEL);
        panelInstances.put(TEACHER_DASHBOARD_PANEL, teacherDashboardScreen);

        TeacherExamListScreen teacherExamListScreen = new TeacherExamListScreen(this);
        cards.add(teacherExamListScreen, TEACHER_EXAM_LIST_PANEL);
        panelInstances.put(TEACHER_EXAM_LIST_PANEL, teacherExamListScreen);

        add(cards);

        showPanel(LOGIN_PANEL);
        setVisible(true);
    }

    public void onLoginSuccess(User user) {
        LOGGER.info("Đăng nhập thành công cho người dùng: " + user.getUsername() + ", Role: " + user.getRoleName());

        // LƯU THÔNG TIN NGƯỜI DÙNG VÀO CONTEXT CHUNG
        // Sử dụng constructor mới của UserDTO: UserDTO(int userId, String username, String roleName)
        ClientContextInitializer.setCurrentLoggedInUser(new UserDTO(user.getUserId(), user.getUsername(), user.getRoleName()));

        this.dispose();

        SwingUtilities.invokeLater(() -> {
            new MainClientWindow(user, connector).setVisible(true);
        });
    }

    public void showRegistrationScreen() {
        showPanel(REGISTRATION_PANEL);
    }

    public void showLoginScreen() {
        showPanel(LOGIN_PANEL);
    }

    public void showTeacherExamCreationScreen(TeacherExamListScreen callerScreen) {
        TeacherExamCreationScreen creationScreen = new TeacherExamCreationScreen(this, callerScreen);

        if (panelInstances.containsKey(TEACHER_EXAM_CREATION_PANEL)) {
            cards.remove(panelInstances.get(TEACHER_EXAM_CREATION_PANEL));
        }
        cards.add(creationScreen, TEACHER_EXAM_CREATION_PANEL);
        panelInstances.put(TEACHER_EXAM_CREATION_PANEL, creationScreen);
        showPanel(TEACHER_EXAM_CREATION_PANEL);
    }

    public void showTeacherExamEditScreen(TeacherExamListScreen callerScreen, ExamDTO examToEdit) {
        TeacherExamEditScreen editScreen = new TeacherExamEditScreen(this, callerScreen, examToEdit);

        if (panelInstances.containsKey(TEACHER_EXAM_EDIT_PANEL)) {
            cards.remove(panelInstances.get(TEACHER_EXAM_EDIT_PANEL));
        }
        cards.add(editScreen, TEACHER_EXAM_EDIT_PANEL);
        panelInstances.put(TEACHER_EXAM_EDIT_PANEL, editScreen);
        showPanel(TEACHER_EXAM_EDIT_PANEL);
    }

    public void showPanel(String panelName) {
        if (TEACHER_EXAM_LIST_PANEL.equals(panelName)) {
            TeacherExamListScreen screen = (TeacherExamListScreen) panelInstances.get(TEACHER_EXAM_LIST_PANEL);
            if (screen != null) {
                screen.loadExams();
            }
        }
        cardLayout.show(cards, panelName);
    }

    public void showTeacherExamListAndRefresh() {
        showPanel(TEACHER_EXAM_LIST_PANEL);
    }

    public void showTeacherDashboard() {
        showPanel(TEACHER_DASHBOARD_PANEL);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClientContextInitializer.initialize(); // 
            new MainApplicationFrame();
        });
    }
}