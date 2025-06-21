package org.example.laptrinhmang.client.view;

import org.example.laptrinhmang.client.network.ServerCommunicationConnector;
import org.example.laptrinhmang.common.model.User;
import org.example.laptrinhmang.client.presentation.MainApplicationFrame; // <--- THAY ĐỔI IMPORT NÀY

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

public class AdminPanelGUI extends JPanel {

    private static final Logger LOGGER = Logger.getLogger(AdminPanelGUI.class.getName());

    private final ServerCommunicationConnector clientConnector;
    private final User authenticatedUser;
    private MainClientWindow mainClientWindow; // Tham chiếu đến MainClientWindow để quản lý chuyển đổi panel

    private JLabel welcomeLabel;
    private JButton userManagementButton;
    private JButton questionBankManagementButton; // Thêm nút quản lý ngân hàng câu hỏi
    private JButton logoutButton;

    public AdminPanelGUI(ServerCommunicationConnector clientConnector, User authenticatedUser, MainClientWindow mainClientWindow) {
        this.clientConnector = clientConnector;
        this.authenticatedUser = authenticatedUser;
        this.mainClientWindow = mainClientWindow; // Lưu tham chiếu

        initComponents();
        addListeners();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Thêm padding

        welcomeLabel = new JLabel("Chào mừng Admin: " + authenticatedUser.getUsername() + " (Role: " + authenticatedUser.getRoleName() + ")", SwingConstants.CENTER);
        welcomeLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 20)); // Sử dụng java.awt.Font
        add(welcomeLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(15, 15, 15, 15); // Khoảng cách giữa các thành phần
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        userManagementButton = new JButton("Quản lý Người dùng");
        userManagementButton.setPreferredSize(new java.awt.Dimension(250, 50)); // Kích thước cố định
        gbc.gridy = 0;
        buttonPanel.add(userManagementButton, gbc);

        questionBankManagementButton = new JButton("Quản lý Ngân hàng Câu hỏi");
        questionBankManagementButton.setPreferredSize(new java.awt.Dimension(250, 50));
        gbc.gridy = 1;
        buttonPanel.add(questionBankManagementButton, gbc);

        gbc.gridy = 2;
        logoutButton = new JButton("Đăng xuất");
        logoutButton.setPreferredSize(new java.awt.Dimension(250, 50));
        buttonPanel.add(logoutButton, gbc);

        add(buttonPanel, BorderLayout.CENTER);
    }

    private void addListeners() {
        userManagementButton.addActionListener(e -> {
            mainClientWindow.showPanel(new UserManagementGUI(clientConnector, authenticatedUser));
            LOGGER.info("AdminPanelGUI: Chuyển sang Quản lý Người dùng.");
        });

        questionBankManagementButton.addActionListener(e -> {
            mainClientWindow.showPanel(new QuestionBankManagementPanel(authenticatedUser, clientConnector));
            LOGGER.info("AdminPanelGUI: Chuyển sang Quản lý Ngân hàng Câu hỏi.");
        });

        logoutButton.addActionListener(e -> {
            clientConnector.closeConnection(); // Đóng kết nối của ServerCommunicationConnector
            mainClientWindow.dispose(); // Đóng cửa sổ MainClientWindow hiện tại (nếu nó là JFrame)
            // Quay lại màn hình đăng nhập bằng cách khởi tạo lại MainApplicationFrame
            SwingUtilities.invokeLater(() -> {
                MainApplicationFrame newFrame = new MainApplicationFrame(); // <--- ĐÃ THAY ĐỔI
                newFrame.setVisible(true);
            });
            LOGGER.info("Người dùng " + authenticatedUser.getUsername() + " đã đăng xuất.");
        });
    }
}