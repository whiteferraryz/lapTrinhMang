package org.example.laptrinhmang.client.presentation;

import org.example.laptrinhmang.common.model.User; // Để hiển thị thông tin người dùng
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

/**
 * Màn hình Dashboard dành cho sinh viên sau khi đăng nhập thành công.
 * Hiển thị thông tin chào mừng và các lựa chọn cơ bản.
 */
public class StudentDashboardScreen extends JPanel {

    private static final Logger LOGGER = Logger.getLogger(StudentDashboardScreen.class.getName());

    private final MainApplicationFrame parentFrame;
    private User currentUser; // Thông tin người dùng hiện tại

    private JLabel welcomeLabel;
    private JButton viewExamsButton;
    private JButton viewResultsButton;
    private JButton logoutButton;

    public StudentDashboardScreen(MainApplicationFrame parentFrame) {
        this.parentFrame = parentFrame;
        initializeUI();
        addListeners();
    }

    private void initializeUI() {
        setLayout(new GridBagLayout());
        setBackground(new Color(245, 255, 250)); // MintCream

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // Khoảng cách giữa các thành phần
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Welcome Label
        welcomeLabel = new JLabel("Chào mừng, Sinh viên!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 32));
        welcomeLabel.setForeground(new Color(46, 139, 87)); // SeaGreen
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(welcomeLabel, gbc);

        // Buttons for student actions
        gbc.gridwidth = 1; // Reset gridwidth
        gbc.ipadx = 30; // Thêm padding ngang
        gbc.ipady = 15; // Thêm padding dọc

        // View Exams Button
        gbc.gridx = 0;
        gbc.gridy = 1;
        viewExamsButton = new JButton("Xem Danh sách Bài thi");
        viewExamsButton.setFont(new Font("Arial", Font.PLAIN, 18));
        viewExamsButton.setBackground(new Color(70, 130, 180)); // SteelBlue
        viewExamsButton.setForeground(Color.WHITE);
        viewExamsButton.setFocusPainted(false);
        add(viewExamsButton, gbc);

        // View Results Button
        gbc.gridx = 1;
        gbc.gridy = 1;
        viewResultsButton = new JButton("Xem Kết quả thi");
        viewResultsButton.setFont(new Font("Arial", Font.PLAIN, 18));
        viewResultsButton.setBackground(new Color(70, 130, 180)); // SteelBlue
        viewResultsButton.setForeground(Color.WHITE);
        viewResultsButton.setFocusPainted(false);
        add(viewResultsButton, gbc);

        // Logout Button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        logoutButton = new JButton("Đăng xuất");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 18));
        logoutButton.setBackground(new Color(220, 20, 60)); // Crimson
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        add(logoutButton, gbc);
    }

    private void addListeners() {
        logoutButton.addActionListener(e -> {
            // Xử lý logic đăng xuất tại đây (ví dụ: xóa thông tin người dùng hiện tại)
            currentUser = null;
            LOGGER.info("Người dùng đã đăng xuất.");
            parentFrame.showLoginScreen(); // Quay lại màn hình đăng nhập
            JOptionPane.showMessageDialog(this, "Bạn đã đăng xuất thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });

        // TODO: Thêm ActionListeners cho viewExamsButton và viewResultsButton sau này
        viewExamsButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Chức năng Xem Danh sách Bài thi đang được phát triển!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });

        viewResultsButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Chức năng Xem Kết quả thi đang được phát triển!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    /**
     * Cập nhật thông tin người dùng và hiển thị trên dashboard.
     * @param user Đối tượng User đã đăng nhập.
     */
    public void setUser(User user) {
        this.currentUser = user;
        if (currentUser != null) {
            welcomeLabel.setText("Chào mừng, " + currentUser.getUsername() + "!");
        } else {
            welcomeLabel.setText("Chào mừng, Sinh viên!");
        }
        LOGGER.info("Đã thiết lập người dùng cho StudentDashboardScreen: " + (user != null ? user.getUsername() : "null"));
    }
}