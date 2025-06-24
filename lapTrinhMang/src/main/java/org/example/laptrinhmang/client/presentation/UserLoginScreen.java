package org.example.laptrinhmang.client.presentation;
//Giao diện người dùng cho chức năng **ĐĂNG NHẬP**
import org.example.laptrinhmang.client.business.UserAuthenticationClientService;
import org.example.laptrinhmang.common.dto.LoginResponseDTO;
import org.example.laptrinhmang.common.dto.RegisterRequestDTO; // Import RegisterRequestDTO
import org.example.laptrinhmang.common.dto.RequestMessage;
import org.example.laptrinhmang.common.dto.ResponseMessage;
import org.example.laptrinhmang.common.util.Constants;
import org.example.laptrinhmang.common.model.User; // Import User model

import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Màn hình giao diện người dùng cho chức năng Đăng nhập.
 * Người dùng nhập tên đăng nhập và mật khẩu để xác thực.
 */
public class UserLoginScreen extends JPanel {

    private static final Logger LOGGER = Logger.getLogger(UserLoginScreen.class.getName());

    private final MainApplicationFrame parentFrame; // Tham chiếu đến khung chính để điều hướng
    private final UserAuthenticationClientService authService; // Service để gửi yêu cầu đăng nhập

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton; // <-- THÊM NÚT ĐĂNG KÝ

    // Constructor đã đúng với việc truyền authService
    public UserLoginScreen(MainApplicationFrame parentFrame, UserAuthenticationClientService authService) {
        this.parentFrame = parentFrame;
        this.authService = authService;
        initializeUI();
        addListeners();
    }

    private void initializeUI() {
        setLayout(new GridBagLayout());
        setBackground(new Color(230, 240, 250)); // Light Blue background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Khoảng cách giữa các thành phần
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Đăng Nhập Hệ Thống");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setForeground(new Color(40, 40, 40)); // Dark Gray
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);

        gbc.gridwidth = 1; // Reset gridwidth

        // Username
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Tên đăng nhập:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        add(passwordField, gbc);

        // Login Button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        loginButton = new JButton("Đăng Nhập");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setBackground(new Color(60, 179, 113)); // MediumSeaGreen
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        add(loginButton, gbc);

        // Register Button (newly added)
        gbc.gridx = 0;
        gbc.gridy = 4; // Vị trí mới cho nút đăng ký
        registerButton = new JButton("Đăng Ký Tài Khoản Mới"); // <-- KHỞI TẠO NÚT
        registerButton.setFont(new Font("Arial", Font.PLAIN, 14));
        registerButton.setBackground(new Color(100, 149, 237)); // CornflowerBlue
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        add(registerButton, gbc); // <-- THÊM NÚT VÀO PANEL
    }

    private void addListeners() {
        loginButton.addActionListener(e -> attemptLogin());
        registerButton.addActionListener(e -> parentFrame.showRegistrationScreen()); // <-- GÁN ACTION CHO NÚT ĐĂNG KÝ
        // Thêm listener cho phím Enter
        usernameField.addActionListener(e -> attemptLogin());
        passwordField.addActionListener(e -> attemptLogin());
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền tên đăng nhập và mật khẩu.", "Lỗi đăng nhập", JOptionPane.WARNING_MESSAGE);
            return;
        }

        new SwingWorker<ResponseMessage, Void>() {
            @Override
            protected ResponseMessage doInBackground() throws Exception {
                toggleInputState(false); // Tắt các điều khiển khi đang xử lý
                return authService.login(username, password);
            }

            @Override
            protected void done() {
                try {
                    ResponseMessage response = get(); // Lấy kết quả từ doInBackground
                    if (Constants.RESPONSE_STATUS_SUCCESS.equals(response.getStatus())) {
                        LoginResponseDTO loginResponse = (LoginResponseDTO) response.getData();
                        if (loginResponse != null && loginResponse.getUser() != null) {
                            parentFrame.onLoginSuccess(loginResponse.getUser()); // Chuyển thông tin user đã đăng nhập lên MainFrame
                        } else {
                            JOptionPane.showMessageDialog(UserLoginScreen.this,
                                    "Đăng nhập thành công nhưng không nhận được thông tin người dùng.",
                                    "Lỗi dữ liệu", JOptionPane.WARNING_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(UserLoginScreen.this,
                                response.getMessage(), "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Lỗi khi xử lý phản hồi đăng nhập: " + ex.getMessage(), ex);
                    JOptionPane.showMessageDialog(UserLoginScreen.this,
                            "Lỗi kết nối hoặc lỗi không xác định. Vui lòng thử lại.", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
                } finally {
                    toggleInputState(true); // Luôn bật lại các điều khiển sau khi xử lý xong
                }
            }
        }.execute(); // Thực thi SwingWorker
    }

    private void toggleInputState(boolean enable) {
        usernameField.setEnabled(enable);
        passwordField.setEnabled(enable);
        loginButton.setEnabled(enable);
        registerButton.setEnabled(enable);
    }
}