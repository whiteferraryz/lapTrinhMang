package org.example.laptrinhmang.client.view;

import org.example.laptrinhmang.client.network.ServerCommunicationConnector;
import org.example.laptrinhmang.common.dto.RequestMessage;
import org.example.laptrinhmang.common.dto.ResponseMessage;
import org.example.laptrinhmang.common.dto.UserDTO;
import org.example.laptrinhmang.common.dto.UserListResponseDTO;
import org.example.laptrinhmang.common.dto.UserOperationRequestDTO;
import org.example.laptrinhmang.common.model.User;
import org.example.laptrinhmang.common.util.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException; // Giữ lại nếu cần cho xử lý lỗi cụ thể
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

// THAY ĐỔI TẠI ĐÂY: extends JPanel thay vì extends JFrame
public class UserManagementGUI extends JPanel { // 

    private static final Logger LOGGER = Logger.getLogger(UserManagementGUI.class.getName());

    private final ServerCommunicationConnector client; // 
    private User authenticatedUser; // 

    private JTable userTable; // 
    private DefaultTableModel tableModel; // 
    private JButton refreshButton; // 
    private JButton updateButton; // 
    private JButton deleteButton; // 
    private JButton resetButton; // 
    private JButton backButton; // 
    private final ExecutorService executorService = Executors.newSingleThreadExecutor(); // 

    public UserManagementGUI(ServerCommunicationConnector client, User authenticatedUser) { // 
        this.client = client; // 
        this.authenticatedUser = authenticatedUser; // 

        // XÓA CÁC DÒNG LIÊN QUAN ĐẾN JFrame:
        // setTitle("Quản lý Người dùng - Admin: " + authenticatedUser.getUsername()); // 
        // setSize(800, 600); // 
        // setLocationRelativeTo(null); // 
        // setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 

        initComponents(); // 
        addListeners(); // 
        loadUsers(); // 
    }

    private void initComponents() {
        setLayout(new BorderLayout()); // 

        String[] columnNames = {"ID", "Tên đăng nhập", "Email", "Role ID", "Tên vai trò"}; // 
        tableModel = new DefaultTableModel(columnNames, 0) { // 
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 
            }
        };
        userTable = new JTable(tableModel); // 
        JScrollPane scrollPane = new JScrollPane(userTable); // 
        add(scrollPane, BorderLayout.CENTER); // 

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // 
        refreshButton = new JButton("Làm mới"); // 
        updateButton = new JButton("Cập nhật"); // 
        deleteButton = new JButton("Xóa"); // 
        resetButton = new JButton("Đặt lại mật khẩu"); // 
        backButton = new JButton("Quay lại"); // 

        buttonPanel.add(refreshButton); // 
        buttonPanel.add(updateButton); // 
        buttonPanel.add(deleteButton); // 
        buttonPanel.add(resetButton); // 
        buttonPanel.add(backButton); // 

        add(buttonPanel, BorderLayout.SOUTH); // 
    }

    private void addListeners() {
        refreshButton.addActionListener(e -> loadUsers()); // 
        updateButton.addActionListener(e -> showUpdateUserDialog()); // 
        deleteButton.addActionListener(e -> deleteSelectedUser()); // 
        resetButton.addActionListener(e -> showResetPasswordDialog()); // 
        backButton.addActionListener(e -> { // 
            JOptionPane.showMessageDialog(this, "Chức năng Quay lại sẽ được phát triển tiếp.", "Thông báo", JOptionPane.INFORMATION_MESSAGE); // 
            // Khi là JPanel, không dùng dispose() ở đây. Bạn sẽ cần cơ chế để báo cho MainClientWindow biết là muốn quay lại
            // Ví dụ: fire một custom event, hoặc gọi một callback method được truyền vào từ MainClientWindow.
            // Tạm thời, tôi sẽ để trống hoặc thêm một comment.
            // Nếu bạn muốn đóng panel này và hiển thị dashboard mặc định:
            // ((MainClientWindow) SwingUtilities.getWindowAncestor(this)).displayUserDashboard(); // Giả sử MainClientWindow có phương thức này
        });
    }

    private void loadUsers() {
        LOGGER.info("Đang tải danh sách người dùng..."); // 
        executorService.submit(() -> { // 
            try {
                RequestMessage request = new RequestMessage(Constants.REQUEST_TYPE_GET_ALL_USERS, null); // 
                ResponseMessage response = client.sendRequest(request); // 

                if (response.getStatus().equals(Constants.RESPONSE_STATUS_SUCCESS)) { // 
                    UserListResponseDTO usersResponse = (UserListResponseDTO) response.getData(); // 
                    SwingUtilities.invokeLater(() -> { // 
                        tableModel.setRowCount(0); // 
                        for (UserDTO user : usersResponse.getUsers()) { // 
                            Vector<Object> row = new Vector<>(); // 
                            row.add(user.getUserId()); // 
                            row.add(user.getUsername()); // 
                            row.add(user.getEmail()); // 
                            row.add(user.getRoleId()); // 
                            row.add(user.getRoleName()); // 
                            tableModel.addRow(row); // 
                        }
                        LOGGER.info("Tải danh sách người dùng thành công."); // 
                    });
                } else {
                    String errorMessage = response.getMessage() != null ? // 
                            response.getMessage() : "Lỗi không xác định khi tải người dùng."; // 
                    SwingUtilities.invokeLater(() -> { // 
                        JOptionPane.showMessageDialog(this, "Lỗi: " + errorMessage, "Lỗi tải người dùng", JOptionPane.ERROR_MESSAGE); // 
                        LOGGER.warning("Lỗi khi tải danh sách người dùng: " + errorMessage); // 
                    });
                }
            } catch (Exception ex) { // 
                LOGGER.log(Level.SEVERE, "Lỗi mạng hoặc dữ liệu khi tải người dùng.", ex); // 
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Lỗi kết nối hoặc dữ liệu khi tải người dùng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE)); // 
            }
        });
    }

    private void showUpdateUserDialog() {
        int selectedRow = userTable.getSelectedRow(); // 
        if (selectedRow == -1) { // 
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một người dùng để cập nhật.", "Thông báo", JOptionPane.WARNING_MESSAGE); // 
            return; // 
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0); // 
        String currentUsername = (String) tableModel.getValueAt(selectedRow, 1); // 
        String currentEmail = (String) tableModel.getValueAt(selectedRow, 2); // 
        int currentRoleId = (int) tableModel.getValueAt(selectedRow, 3); // 
        JTextField usernameField = new JTextField(currentUsername); // 
        JTextField emailField = new JTextField(currentEmail); // 
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"Sinh viên", "Giáo viên", "Admin"}); // 
        roleComboBox.setSelectedIndex(currentRoleId - 1); // 

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5)); // 
        panel.add(new JLabel("Tên đăng nhập:")); // 
        panel.add(usernameField); // 
        panel.add(new JLabel("Email:")); // 
        panel.add(emailField); // 
        panel.add(new JLabel("Vai trò:")); // 
        panel.add(roleComboBox); // 

        int result = JOptionPane.showConfirmDialog(this, panel, "Cập nhật Người dùng", // 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE); // 
        if (result == JOptionPane.OK_OPTION) { // 
            String newUsername = usernameField.getText().trim(); // 
            String newEmail = emailField.getText().trim(); // 
            int newRoleId = roleComboBox.getSelectedIndex() + 1; // 
            if (newUsername.isEmpty() || newEmail.isEmpty()) { // 
                JOptionPane.showMessageDialog(this, "Tên đăng nhập và Email không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE); // 
                return; // 
            }

            UserDTO userToUpdate = new UserDTO(userId, newUsername, newEmail, newRoleId, null); // 
            UserOperationRequestDTO requestDTO = new UserOperationRequestDTO(userId, userToUpdate, null); // 

            executorService.submit(() -> { // 
                try {
                    RequestMessage request = new RequestMessage(Constants.REQUEST_TYPE_UPDATE_USER, requestDTO); // 
                    ResponseMessage response = client.sendRequest(request); // 

                    SwingUtilities.invokeLater(() -> { // 
                        if (response.getStatus().equals(Constants.RESPONSE_STATUS_SUCCESS)) { // 
                            JOptionPane.showMessageDialog(this, response.getMessage(), "Cập nhật thành công", JOptionPane.INFORMATION_MESSAGE); // 
                            loadUsers(); // 
                        } else {
                            JOptionPane.showMessageDialog(this, "Cập nhật thất bại: " + response.getMessage(), "Lỗi cập nhật", JOptionPane.ERROR_MESSAGE); // 
                        }
                    });
                } catch (Exception ex) { // 
                    LOGGER.log(Level.SEVERE, "Lỗi mạng hoặc dữ liệu khi cập nhật người dùng.", ex); // 
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Lỗi kết nối hoặc dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE)); // 
                }
            });
        }
    }

    private void deleteSelectedUser() {
        int selectedRow = userTable.getSelectedRow(); // 
        if (selectedRow == -1) { // 
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một người dùng để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE); // 
            return; // 
        }

        int userIdToDelete = (int) tableModel.getValueAt(selectedRow, 0); // 
        String usernameToDelete = (String) tableModel.getValueAt(selectedRow, 1); // 

        if (userIdToDelete == authenticatedUser.getUserId()) { // 
            JOptionPane.showMessageDialog(this, "Bạn không thể tự xóa tài khoản của chính mình.", "Lỗi", JOptionPane.ERROR_MESSAGE); // 
            return; // 
        }

        int confirm = JOptionPane.showConfirmDialog(this, // 
                "Bạn có chắc chắn muốn xóa người dùng '" + usernameToDelete + "' (ID: " + userIdToDelete + ") không?", // 
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION); // 
        if (confirm == JOptionPane.YES_OPTION) { // 
            UserOperationRequestDTO requestDTO = new UserOperationRequestDTO(userIdToDelete, null, null); // 
            executorService.submit(() -> { // 
                try {
                    RequestMessage request = new RequestMessage(Constants.REQUEST_TYPE_DELETE_USER, requestDTO); // 
                    ResponseMessage response = client.sendRequest(request); // 

                    SwingUtilities.invokeLater(() -> { // 
                        if (response.getStatus().equals(Constants.RESPONSE_STATUS_SUCCESS)) { // 
                            JOptionPane.showMessageDialog(this, response.getMessage(), "Xóa thành công", JOptionPane.INFORMATION_MESSAGE); // 
                            loadUsers(); // 
                        } else {
                            JOptionPane.showMessageDialog(this, "Xóa thất bại: " + response.getMessage(), "Lỗi xóa", JOptionPane.ERROR_MESSAGE); // 
                        }
                    });
                } catch (Exception ex) { // 
                    LOGGER.log(Level.SEVERE, "Lỗi mạng hoặc dữ liệu khi xóa người dùng.", ex); // 
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Lỗi kết nối hoặc dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE)); // 
                }
            });
        }
    }

    private void showResetPasswordDialog() {
        int selectedRow = userTable.getSelectedRow(); // 
        if (selectedRow == -1) { // 
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một người dùng để đặt lại mật khẩu.", "Thông báo", JOptionPane.WARNING_MESSAGE); // 
            return; // 
        }

        int userIdToReset = (int) tableModel.getValueAt(selectedRow, 0); // 
        String usernameToReset = (String) tableModel.getValueAt(selectedRow, 1); // 

        JPasswordField newPasswordField = new JPasswordField(20); // 
        JPasswordField confirmPasswordField = new JPasswordField(20); // 
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5)); // 
        panel.add(new JLabel("Mật khẩu mới:")); // 
        panel.add(newPasswordField); // 
        panel.add(new JLabel("Xác nhận mật khẩu:")); // 
        panel.add(confirmPasswordField); // 
        int result = JOptionPane.showConfirmDialog(this, panel, "Đặt lại mật khẩu cho " + usernameToReset, // 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE); // 
        if (result == JOptionPane.OK_OPTION) { // 
            String newPassword = new String(newPasswordField.getPassword()); // 
            String confirmPassword = new String(confirmPasswordField.getPassword()); // 

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) { // 
                JOptionPane.showMessageDialog(this, "Mật khẩu không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE); // 
                return; // 
            }
            if (!newPassword.equals(confirmPassword)) { // 
                JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp.", "Lỗi", JOptionPane.ERROR_MESSAGE); // 
                return; // 
            }

            UserOperationRequestDTO requestDTO = new UserOperationRequestDTO(userIdToReset, null, newPassword); // 
            executorService.submit(() -> { // 
                try {
                    RequestMessage request = new RequestMessage(Constants.REQUEST_TYPE_RESET_PASSWORD, requestDTO); // 
                    ResponseMessage response = client.sendRequest(request); // 

                    SwingUtilities.invokeLater(() -> { // 
                        if (response.getStatus().equals(Constants.RESPONSE_STATUS_SUCCESS)) { // 
                            JOptionPane.showMessageDialog(this, response.getMessage(), "Đặt lại mật khẩu thành công", JOptionPane.INFORMATION_MESSAGE); // 
                        } else {
                            JOptionPane.showMessageDialog(this, "Đặt lại mật khẩu thất bại: " + response.getMessage(), "Lỗi đặt lại mật khẩu", JOptionPane.ERROR_MESSAGE); // 
                        }
                    });
                } catch (Exception ex) { // 
                    LOGGER.log(Level.SEVERE, "Lỗi mạng hoặc dữ liệu khi đặt lại mật khẩu.", ex); // 
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Lỗi kết nối hoặc dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE)); // 
                }
            });
        }
    }

    public void closeExecutor() {
        executorService.shutdown(); // 
        LOGGER.info("ExecutorService trong UserManagementGUI đã tắt."); // 
    }
}