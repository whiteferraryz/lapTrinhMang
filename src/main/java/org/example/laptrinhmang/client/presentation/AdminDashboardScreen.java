package org.example.laptrinhmang.client.presentation;

import org.example.laptrinhmang.common.model.User; // Để hiển thị thông tin người dùng
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

/**
 * Màn hình Dashboard dành cho quản trị viên sau khi đăng nhập thành công.
 * Hiển thị thông tin chào mừng và các lựa chọn quản lý hệ thống.
 */
public class AdminDashboardScreen extends JPanel {

    private static final Logger LOGGER = Logger.getLogger(AdminDashboardScreen.class.getName());

    private final MainApplicationFrame parentFrame;
    private User currentUser; // Thông tin người dùng hiện tại (sẽ là Admin)

    private JLabel welcomeLabel;
    private JButton userManagementButton;
    private JButton systemSettingsButton;
    private JButton errorReportsButton;
    private JButton logoutButton;

    public AdminDashboardScreen(MainApplicationFrame parentFrame) {
        this.parentFrame = parentFrame;
        initializeUI();
        addListeners();
    }

    private void initializeUI() {
        setLayout(new GridBagLayout());
        setBackground(new Color(230, 230, 250)); // Lavender

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // Khoảng cách giữa các thành phần
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Welcome Label
        welcomeLabel = new JLabel("Chào mừng, Quản trị viên!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 32));
        welcomeLabel.setForeground(new Color(106, 90, 205)); // SlateBlue
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(welcomeLabel, gbc);

        // Buttons for admin actions
        gbc.gridwidth = 1; // Reset gridwidth
        gbc.ipadx = 30; // Thêm padding ngang
        gbc.ipady = 15; // Thêm padding dọc

        // User Management Button
        gbc.gridx = 0;
        gbc.gridy = 1;
        userManagementButton = new JButton("Quản lý Người dùng");
        userManagementButton.setFont(new Font("Arial", Font.PLAIN, 18));
        userManagementButton.setBackground(new Color(147, 112, 219)); // MediumPurple
        userManagementButton.setForeground(Color.WHITE);
        userManagementButton.setFocusPainted(false);
        add(userManagementButton, gbc);

        // System Settings Button
        gbc.gridx = 1;
        gbc.gridy = 1;
        systemSettingsButton = new JButton("Cấu hình Hệ thống");
        systemSettingsButton.setFont(new Font("Arial", Font.PLAIN, 18));
        systemSettingsButton.setBackground(new Color(147, 112, 219)); // MediumPurple
        systemSettingsButton.setForeground(Color.WHITE);
        systemSettingsButton.setFocusPainted(false);
        add(systemSettingsButton, gbc);

        // Error Reports Button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Nút này có thể chiếm toàn bộ chiều rộng
        errorReportsButton = new JButton("Xem Báo cáo Lỗi Hệ thống");
        errorReportsButton.setFont(new Font("Arial", Font.PLAIN, 18));
        errorReportsButton.setBackground(new Color(255, 99, 71)); // Tomato (màu cam đỏ để nhấn mạnh)
        errorReportsButton.setForeground(Color.WHITE);
        errorReportsButton.setFocusPainted(false);
        add(errorReportsButton, gbc);

        // Logout Button
        gbc.gridx = 0;
        gbc.gridy = 3;
        logoutButton = new JButton("Đăng xuất");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 18));
        logoutButton.setBackground(new Color(220, 20, 60)); // Crimson
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        add(logoutButton, gbc);
    }

    private void addListeners() {
        logoutButton.addActionListener(e -> {
            currentUser = null;
            LOGGER.info("Người dùng Admin đã đăng xuất.");
            parentFrame.showLoginScreen(); // Quay lại màn hình đăng nhập
            JOptionPane.showMessageDialog(this, "Bạn đã đăng xuất thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });

        // TODO: Thêm ActionListeners cho các nút khác sau này
        userManagementButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Chức năng Quản lý Người dùng đang được phát triển!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });
        systemSettingsButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Chức năng Cấu hình Hệ thống đang được phát triển!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });
        errorReportsButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Chức năng Xem Báo cáo Lỗi Hệ thống đang được phát triển!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    /**
     * Cập nhật thông tin người dùng và hiển thị trên dashboard.
     * @param user Đối tượng User đã đăng nhập (vai trò Admin).
     */
    public void setUser(User user) {
        this.currentUser = user;
        if (currentUser != null) {
            welcomeLabel.setText("Chào mừng, Quản trị viên " + currentUser.getUsername() + "!");
        } else {
            welcomeLabel.setText("Chào mừng, Quản trị viên!");
        }
        LOGGER.info("Đã thiết lập người dùng cho AdminDashboardScreen: " + (user != null ? user.getUsername() : "null"));
    }
}