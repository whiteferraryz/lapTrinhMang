package org.example.laptrinhmang.client.view;

import org.example.laptrinhmang.common.model.User;
import org.example.laptrinhmang.common.util.Constants;
import org.example.laptrinhmang.client.network.ServerCommunicationConnector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// THÊM CÁC DÒNG IMPORT CẦN THIẾT:
import org.example.laptrinhmang.client.view.UserManagementGUI;
import org.example.laptrinhmang.client.view.QuestionBankManagementPanel;
import org.example.laptrinhmang.client.presentation.MainApplicationFrame; // <--- ĐÃ THAY ĐỔI IMPORT NÀY

public class MainClientWindow extends JFrame {

    private User loggedInUser;
    private JPanel mainPanel; // Nơi các panel chức năng sẽ được hiển thị
    private ServerCommunicationConnector serverCommunicationConnector;

    public MainClientWindow(User user, ServerCommunicationConnector connector) {
        this.loggedInUser = user;
        this.serverCommunicationConnector = connector;

        setTitle("Hệ thống Quản lý Thi Trực tuyến - " + user.getUsername() + " (" + user.getRoleName() + ")");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);

        createMenu();
        displayUserDashboard();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (serverCommunicationConnector != null) {
                    serverCommunicationConnector.closeConnection();
                    System.out.println("Kết nối server đã được đóng khi MainClientWindow đóng.");
                }
                System.exit(0);
            }
        });

        setVisible(true);
    }

    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu adminMenu = new JMenu("Quản trị");
        JMenu teacherMenu = new JMenu("Giáo viên");
        JMenu studentMenu = new JMenu("Học sinh");
        JMenu accountMenu = new JMenu("Tài khoản");

        if (loggedInUser.getRoleId() == Constants.ROLE_ID_ADMIN) {
            JMenuItem manageUsersItem = new JMenuItem("Quản lý Người dùng");
            manageUsersItem.addActionListener(e -> showPanel(new UserManagementGUI(serverCommunicationConnector, loggedInUser)));
            adminMenu.add(manageUsersItem);

            JMenuItem manageQuestionsItem = new JMenuItem("Quản lý Ngân hàng Câu hỏi");
            manageQuestionsItem.addActionListener(e -> showPanel(new QuestionBankManagementPanel(loggedInUser, serverCommunicationConnector)));
            adminMenu.add(manageQuestionsItem);
            menuBar.add(adminMenu);
        }

        if (loggedInUser.getRoleId() == Constants.ROLE_ID_TEACHER) {
            JMenuItem manageQuestionsItem = new JMenuItem("Quản lý Ngân hàng Câu hỏi");
            manageQuestionsItem.addActionListener(e -> showPanel(new QuestionBankManagementPanel(loggedInUser, serverCommunicationConnector)));
            teacherMenu.add(manageQuestionsItem);
            menuBar.add(teacherMenu);
        }

        if (loggedInUser.getRoleId() == Constants.ROLE_ID_STUDENT) {
            menuBar.add(studentMenu);
        }

        JMenuItem logoutItem = new JMenuItem("Đăng xuất");
        logoutItem.addActionListener(e -> logout());
        accountMenu.add(logoutItem);
        menuBar.add(accountMenu);

        setJMenuBar(menuBar);
    }

    private void displayUserDashboard() {
        mainPanel.removeAll();
        mainPanel.add(new JLabel("Chào mừng, " + loggedInUser.getUsername() + "!"), BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public void showPanel(JPanel panel) {
        mainPanel.removeAll();
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void logout() {
        if (serverCommunicationConnector != null) {
            serverCommunicationConnector.closeConnection();
        }
        dispose();
        SwingUtilities.invokeLater(() -> {
            MainApplicationFrame newFrame = new MainApplicationFrame(); // <--- ĐÃ THAY ĐỔI
            newFrame.setVisible(true);
        });
    }
}