package org.example.laptrinhmang.client.presentation;
//import các lớp cần thiết
import org.example.laptrinhmang.client.business.UserAuthenticationClientService;
// import org.example.laptrinhmang.common.dto.LoginResponseDTO; // Có thể cần nếu muốn auto-login sau đăng ký, giữ lại nếu có dùng
import org.example.laptrinhmang.common.dto.ResponseMessage; // <--- THÊM IMPORT NÀY
import org.example.laptrinhmang.common.util.Constants; // Để sử dụng các hằng số trạng thái
import org.example.laptrinhmang.common.model.UserRole; // Để hiển thị các vai trò (đã có)

//import các thư viện cần thiết
import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserRegistrationScreen extends JPanel {

    private static final Logger LOGGER = Logger.getLogger(UserRegistrationScreen.class.getName());

    private final MainApplicationFrame parentFrame; // Tham chiếu đến khung chính để điều hướng
    private final UserAuthenticationClientService authService; // Service để gửi yêu cầu đăng ký

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JComboBox<UserRole> roleComboBox; // Cho phép chọn vai trò
    private JButton registerButton;
    private JButton backButton;

    public UserRegistrationScreen(MainApplicationFrame parentFrame, UserAuthenticationClientService authService) {
        this.parentFrame = parentFrame;
        this.authService = authService;
        initializeUI();
        addListeners();
    }

    private void initializeUI() {
        setLayout(new GridBagLayout());
        setBackground(new Color(240, 248, 255)); // AliceBlue

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Khoảng cách giữa các thành phần
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Đăng Ký Tài Khoản Mới");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(60, 60, 60)); // Dark Gray
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

        // Email
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        add(emailField, gbc);

        // Role Selection (using JComboBox for UserRole enum values)
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("Vai trò:"), gbc);
        gbc.gridx = 1;
        roleComboBox = new JComboBox<>(UserRole.values()); // Lấy tất cả các giá trị của enum UserRole
        roleComboBox.setSelectedItem(UserRole.STUDENT); // Đặt mặc định là STUDENT
        add(roleComboBox, gbc);

        // Register Button
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        registerButton = new JButton("Đăng Ký");
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));
        registerButton.setBackground(new Color(70, 130, 180)); // SteelBlue
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        add(registerButton, gbc);

        // Back Button
        gbc.gridx = 0;
        gbc.gridy = 6;
        backButton = new JButton("Quay lại Đăng nhập");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.setBackground(new Color(176, 196, 222)); // LightSteelBlue
        backButton.setForeground(new Color(50, 50, 50));
        backButton.setFocusPainted(false);
        add(backButton, gbc);
    }

    private void addListeners() {
        registerButton.addActionListener(e -> attemptRegistration());
        backButton.addActionListener(e -> parentFrame.showLoginScreen());
    }

    private void attemptRegistration() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String email = emailField.getText().trim();
        UserRole selectedRole = (UserRole) roleComboBox.getSelectedItem();
        int roleId = selectedRole.getRoleId(); // Giả sử UserRole có phương thức getRoleId()

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ tất cả các trường.", "Lỗi đăng ký", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Gọi service để gửi yêu cầu đăng ký
        try {
            new SwingWorker<ResponseMessage, Void>() { // ResponseMessage đã được import
                @Override
                protected ResponseMessage doInBackground() throws Exception {
                    return authService.registerUser(username, password, email, roleId);
                }

                @Override
                protected void done() {
                    try {
                        ResponseMessage response = get(); // ResponseMessage đã được import
                        if (Constants.RESPONSE_STATUS_SUCCESS.equals(response.getStatus())) {
                            JOptionPane.showMessageDialog(UserRegistrationScreen.this,
                                    "Đăng ký thành công! Bạn có thể đăng nhập ngay bây giờ.",
                                    "Đăng ký thành công", JOptionPane.INFORMATION_MESSAGE);
                            // Sau khi đăng ký thành công, quay lại màn hình đăng nhập
                            parentFrame.showLoginScreen();
                        } else {
                            JOptionPane.showMessageDialog(UserRegistrationScreen.this,
                                    response.getMessage(), "Lỗi đăng ký", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        LOGGER.log(Level.SEVERE, "Lỗi khi xử lý phản hồi đăng ký: " + ex.getMessage(), ex);
                        JOptionPane.showMessageDialog(UserRegistrationScreen.this,
                                "Lỗi kết nối hoặc lỗi không xác định. Vui lòng thử lại.", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute(); // Thực thi SwingWorker
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Lỗi tạo SwingWorker để đăng ký: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog(this,
                    "Lỗi nội bộ khi bắt đầu quá trình đăng ký.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}