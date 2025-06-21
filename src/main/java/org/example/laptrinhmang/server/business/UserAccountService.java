// src/main/java/org/example/laptrinhmang/server/business/UserAccountService.java
package org.example.laptrinhmang.server.business; // Chú ý package này là `server.business`

import org.example.laptrinhmang.common.dto.ResponseMessage;
import org.example.laptrinhmang.common.dto.UserDTO;
import org.example.laptrinhmang.common.model.User; // Import lớp User model
import org.example.laptrinhmang.common.util.Constants;
import org.example.laptrinhmang.common.util.SecurityUtil; // Để băm mật khẩu
import org.example.laptrinhmang.server.dataaccess.UserDAO;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserAccountService {
    private static final Logger LOGGER = Logger.getLogger(UserAccountService.class.getName());
    private UserDAO userDAO;

    public UserAccountService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public ResponseMessage authenticateUser(String username, String password) {
        try {
            // Thay đổi kiểu trả về từ UserDTO thành User
            User user = userDAO.getUserByUsername(username);
            
            if (user != null) {
                // So sánh mật khẩu đã băm
                String hashedPasswordInput = SecurityUtil.hashPassword(password);
                // Truy cập getPasswordHash() từ đối tượng User (model entity)
                if (hashedPasswordInput != null && hashedPasswordInput.equals(user.getPasswordHash())) {
                    LOGGER.info("Đăng nhập thành công cho user: " + username);
                    
                    // Tạo một UserDTO mới để gửi về client (không chứa mật khẩu băm)
                    UserDTO userDTO = new UserDTO(
                        user.getUserId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRoleId(),
                        user.getRoleName()
                    );
                    return new ResponseMessage(Constants.RESPONSE_STATUS_SUCCESS, "Đăng nhập thành công!", userDTO);
                }
            }
            LOGGER.warning("Đăng nhập thất bại cho user: " + username + " (Sai tên đăng nhập hoặc mật khẩu)");
            return new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Sai tên đăng nhập hoặc mật khẩu.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi DB khi xác thực user '" + username + "': " + e.getMessage(), e);
            return new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Lỗi hệ thống khi xác thực.");
        }
    }

    public ResponseMessage registerUser(UserDTO userDTO) {
        // Kiểm tra null cho userDTO ngay từ đầu
        if (userDTO == null || userDTO.getUsername() == null || userDTO.getUsername().isEmpty() ||
            userDTO.getEmail() == null || userDTO.getEmail().isEmpty() ||
            userDTO.getPasswordHash() == null || userDTO.getPasswordHash().isEmpty()) { // Giả định passwordHash là raw password
            LOGGER.warning("registerUser: Đăng ký thất bại. Dữ liệu người dùng không hợp lệ.");
            return new ResponseMessage(Constants.RESPONSE_STATUS_INVALID_INPUT, "Dữ liệu đăng ký không hợp lệ.");
        }

        try {
            // Kiểm tra tên đăng nhập đã tồn tại chưa
            if (userDAO.getUserByUsername(userDTO.getUsername()) != null) {
                LOGGER.warning("registerUser: Tên đăng nhập '" + userDTO.getUsername() + "' đã tồn tại.");
                return new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Tên đăng nhập đã tồn tại.");
            }
            
            // Băm mật khẩu trước khi lưu
            // userDTO.getPasswordHash() ở đây được coi là mật khẩu gốc từ client
            String hashedPassword = SecurityUtil.hashPassword(userDTO.getPasswordHash());
            if (hashedPassword == null) {
                LOGGER.severe("registerUser: Lỗi khi băm mật khẩu cho user: " + userDTO.getUsername());
                return new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Lỗi khi băm mật khẩu.");
            }
            
            // Tạo một đối tượng User (model entity) để lưu vào DB
            User newUser = new User();
            newUser.setUsername(userDTO.getUsername());
            newUser.setEmail(userDTO.getEmail());
            newUser.setPasswordHash(hashedPassword); // Set mật khẩu đã băm

            // Mặc định vai trò là STUDENT nếu không được set (hoặc set từ client nếu có quyền)
            // Sử dụng roleId và roleName
            if (userDTO.getRoleId() == 0) { // Nếu roleId chưa được set hoặc là giá trị mặc định
                newUser.setRoleId(Constants.STUDENT_ROLE_ID);
                newUser.setRoleName("STUDENT"); // Hoặc lấy từ một map/enum nếu có
            } else {
                newUser.setRoleId(userDTO.getRoleId());
                newUser.setRoleName(userDTO.getRoleName()); // Đảm bảo roleName cũng được set nếu roleId được set
            }

            // Gọi addUser với đối tượng User (model entity)
            boolean success = userDAO.addUser(newUser);
            if (success) {
                LOGGER.info("Đăng ký user mới thành công: " + userDTO.getUsername());
                
                // Tạo một UserDTO mới để gửi về client (không chứa mật khẩu băm)
                UserDTO registeredUserDTO = new UserDTO(
                    newUser.getUserId(), // Lấy ID sau khi thêm vào DB nếu DAO có trả về ID
                    newUser.getUsername(),
                    newUser.getEmail(),
                    newUser.getRoleId(),
                    newUser.getRoleName()
                );
                return new ResponseMessage(Constants.RESPONSE_STATUS_SUCCESS, "Đăng ký thành công!", registeredUserDTO);
            } else {
                LOGGER.warning("registerUser: Đăng ký thất bại cho user: " + userDTO.getUsername());
                return new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Đăng ký thất bại.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi DB khi đăng ký user '" + userDTO.getUsername() + "': " + e.getMessage(), e);
            return new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Lỗi hệ thống khi đăng ký.");
        }
    }
}