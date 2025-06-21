package org.example.laptrinhmang.server.business;

import org.example.laptrinhmang.common.util.Constants;
import org.example.laptrinhmang.common.dto.ResponseMessage;
import org.example.laptrinhmang.common.dto.UserDTO;
import org.example.laptrinhmang.common.dto.UserListResponseDTO;
import org.example.laptrinhmang.common.dto.UserOperationRequestDTO;
import org.example.laptrinhmang.common.model.User;
import org.example.laptrinhmang.server.dataaccess.UserDAO;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserManagementService {

    private static final Logger LOGGER = Logger.getLogger(UserManagementService.class.getName());
    private final UserDAO userDAO;

    public UserManagementService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Lấy tất cả người dùng (dưới dạng DTO) từ database.
     * Yêu cầu người dùng đang đăng nhập phải có quyền admin (roleId = Constants.ADMIN_ROLE_ID).
     * @param adminUser Người dùng admin đã xác thực thực hiện yêu cầu.
     * @return UserListResponseDTO chứa danh sách người dùng hoặc thông báo lỗi.
     */
    public UserListResponseDTO getAllUsers(User adminUser) {
        // Sử dụng hằng số Constants.ADMIN_ROLE_ID thay vì số 3
        if (adminUser == null || adminUser.getRoleId() != Constants.ADMIN_ROLE_ID) {
            LOGGER.warning("getAllUsers: Truy cập bị từ chối. Người dùng không phải admin hoặc chưa xác thực.");
            // Đảm bảo UserListResponseDTO constructor phù hợp (đã thảo luận)
            return new UserListResponseDTO(Constants.RESPONSE_STATUS_PERMISSION_DENIED, null, "Bạn không có quyền truy cập chức năng này.");
        }

        try {
            List<UserDTO> users = userDAO.getAllUsers();
            LOGGER.info("getAllUsers: Lấy thành công " + users.size() + " người dùng.");
            return new UserListResponseDTO(Constants.RESPONSE_STATUS_SUCCESS, users, "Lấy danh sách người dùng thành công.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "getAllUsers: Lỗi khi lấy tất cả người dùng: " + e.getMessage(), e);
            return new UserListResponseDTO(Constants.RESPONSE_STATUS_ERROR, null, "Lỗi server khi lấy danh sách người dùng.");
        }
    }

    /**
     * Cập nhật thông tin người dùng.
     * Yêu cầu người dùng đang đăng nhập phải có quyền admin (roleId = Constants.ADMIN_ROLE_ID).
     * Admin không thể tự thay đổi vai trò của mình.
     * @param requestDTO DTO chứa thông tin người dùng cần cập nhật.
     * @param adminUser Người dùng admin đã xác thực thực hiện yêu cầu.
     * @return ResponseMessage cho biết kết quả.
     */
    public ResponseMessage updateUser(UserOperationRequestDTO requestDTO, User adminUser) {
        // Kiểm tra quyền admin
        if (adminUser == null || adminUser.getRoleId() != Constants.ADMIN_ROLE_ID) {
            LOGGER.warning("updateUser: Truy cập bị từ chối. Người dùng không phải admin hoặc chưa xác thực.");
            return new ResponseMessage(Constants.RESPONSE_STATUS_PERMISSION_DENIED, "Bạn không có quyền thực hiện thao tác này.");
        }

        // Kiểm tra requestDTO có null không
        if (requestDTO == null) {
            LOGGER.warning("updateUser: Cập nhật thất bại. Dữ liệu yêu cầu là null.");
            // Nên dùng BAD_REQUEST hoặc INVALID_INPUT cho lỗi dữ liệu
            return new ResponseMessage(Constants.RESPONSE_STATUS_INVALID_INPUT, "Dữ liệu yêu cầu không hợp lệ.");
        }

        // Kiểm tra User ID hợp lệ
        if (requestDTO.getUserId() == 0) {
            LOGGER.warning("updateUser: Cập nhật thất bại. User ID không hợp lệ.");
            return new ResponseMessage(Constants.RESPONSE_STATUS_INVALID_INPUT, "User ID không hợp lệ.");
        }

        // Lấy UserDTO từ requestDTO
        // ĐẶT DÒNG NÀY Ở ĐÂY để userToUpdate có thể được truy cập
        UserDTO userToUpdate = requestDTO.getUserDTO();

        // Kiểm tra userToUpdate có null không (rất quan trọng)
        if (userToUpdate == null) {
            LOGGER.warning("updateUser: Cập nhật thất bại. UserDTO không được cung cấp trong yêu cầu.");
            return new ResponseMessage(Constants.RESPONSE_STATUS_INVALID_INPUT, "Dữ liệu người dùng để cập nhật không hợp lệ.");
        }

        // Đảm bảo userId trong UserDTO khớp với userId trong requestDTO nếu có
        // Điều này đảm bảo tính nhất quán giữa userId trong request và userId trong DTO để cập nhật
        userToUpdate.setUserId(requestDTO.getUserId());

        // Sửa lỗi 'userToUpdate cannot be resolved' và truy cập getRoleId() đúng cách
        // Admin không thể tự thay đổi vai trò của mình
        if (requestDTO.getUserId() == adminUser.getUserId() && userToUpdate.getRoleId() != adminUser.getRoleId()) {
            LOGGER.warning("updateUser: Admin không thể tự thay đổi vai trò của mình.");
            return new ResponseMessage(Constants.RESPONSE_STATUS_FAILED, "Admin không thể tự thay đổi vai trò của chính mình.");
        }

        try {
            boolean success = userDAO.updateUser(userToUpdate);
            if (success) {
                LOGGER.info("updateUser: Người dùng ID " + requestDTO.getUserId() + " được cập nhật thành công.");
                return new ResponseMessage(Constants.RESPONSE_STATUS_SUCCESS, "Cập nhật người dùng thành công.");
            } else {
                LOGGER.warning("updateUser: Cập nhật người dùng ID " + requestDTO.getUserId() + " thất bại.");
                return new ResponseMessage(Constants.RESPONSE_STATUS_FAILED, "Cập nhật người dùng thất bại. Có thể tên đăng nhập/email đã tồn tại hoặc người dùng không tồn tại.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "updateUser: Lỗi khi cập nhật người dùng ID " + requestDTO.getUserId() + ": " + e.getMessage(), e);
            return new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Lỗi server khi cập nhật người dùng.");
        }
    }

    /**
     * Xóa một người dùng khỏi database.
     * Yêu cầu người dùng đang đăng nhập phải có quyền admin (roleId = Constants.ADMIN_ROLE_ID).
     * Admin không thể tự xóa tài khoản của mình.
     * @param requestDTO DTO chứa ID của người dùng cần xóa.
     * @param adminUser Người dùng admin đã xác thực thực hiện yêu cầu.
     * @return ResponseMessage cho biết kết quả.
     */
    public ResponseMessage deleteUser(UserOperationRequestDTO requestDTO, User adminUser) {
        // Kiểm tra quyền admin
        if (adminUser == null || adminUser.getRoleId() != Constants.ADMIN_ROLE_ID) {
            LOGGER.warning("deleteUser: Truy cập bị từ chối. Người dùng không phải admin hoặc chưa xác thực.");
            return new ResponseMessage(Constants.RESPONSE_STATUS_PERMISSION_DENIED, "Bạn không có quyền thực hiện thao tác này.");
        }

        // Kiểm tra requestDTO có null không
        if (requestDTO == null) {
            LOGGER.warning("deleteUser: Xóa thất bại. Dữ liệu yêu cầu là null.");
            return new ResponseMessage(Constants.RESPONSE_STATUS_INVALID_INPUT, "Dữ liệu yêu cầu không hợp lệ.");
        }

        // Kiểm tra User ID hợp lệ
        if (requestDTO.getUserId() == 0) {
            LOGGER.warning("deleteUser: Xóa thất bại. User ID không hợp lệ.");
            return new ResponseMessage(Constants.RESPONSE_STATUS_INVALID_INPUT, "User ID không hợp lệ.");
        }

        // Admin không thể tự xóa tài khoản của mình
        if (requestDTO.getUserId() == adminUser.getUserId()) {
            LOGGER.warning("deleteUser: Admin không thể tự xóa tài khoản của mình.");
            return new ResponseMessage(Constants.RESPONSE_STATUS_FAILED, "Admin không thể tự xóa tài khoản của chính mình.");
        }

        try {
            boolean success = userDAO.deleteUserById(requestDTO.getUserId());
            if (success) {
                LOGGER.info("deleteUser: Người dùng ID " + requestDTO.getUserId() + " được xóa thành công.");
                return new ResponseMessage(Constants.RESPONSE_STATUS_SUCCESS, "Xóa người dùng thành công.");
            } else {
                LOGGER.warning("deleteUser: Xóa người dùng ID " + requestDTO.getUserId() + " thất bại (không tìm thấy).");
                return new ResponseMessage(Constants.RESPONSE_STATUS_NOT_FOUND, "Xóa người dùng thất bại. Người dùng không tồn tại."); // Nên dùng NOT_FOUND
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "deleteUser: Lỗi khi xóa người dùng ID " + requestDTO.getUserId() + ": " + e.getMessage(), e);
            return new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Lỗi server khi xóa người dùng.");
        }
    }

    /**
     * Đặt lại mật khẩu của người dùng.
     * Yêu cầu người dùng đang đăng nhập phải có quyền admin (roleId = Constants.ADMIN_ROLE_ID).
     * @param requestDTO DTO chứa ID của người dùng và mật khẩu mới.
     * @param adminUser Người dùng admin đã xác thực thực hiện yêu cầu.
     * @return ResponseMessage cho biết kết quả.
     */
    public ResponseMessage resetUserPassword(UserOperationRequestDTO requestDTO, User adminUser) {
        // Kiểm tra quyền admin
        if (adminUser == null || adminUser.getRoleId() != Constants.ADMIN_ROLE_ID) {
            LOGGER.warning("resetUserPassword: Truy cập bị từ chối. Người dùng không phải admin hoặc chưa xác thực.");
            return new ResponseMessage(Constants.RESPONSE_STATUS_PERMISSION_DENIED, "Bạn không có quyền thực hiện thao tác này.");
        }

        // Kiểm tra requestDTO có null không
        if (requestDTO == null) {
            LOGGER.warning("resetUserPassword: Đặt lại mật khẩu thất bại. Dữ liệu yêu cầu là null.");
            return new ResponseMessage(Constants.RESPONSE_STATUS_INVALID_INPUT, "Dữ liệu yêu cầu không hợp lệ.");
        }

        // Kiểm tra User ID hoặc mật khẩu mới hợp lệ
        if (requestDTO.getUserId() == 0 || requestDTO.getNewPassword() == null || requestDTO.getNewPassword().isEmpty()) {
            LOGGER.warning("resetUserPassword: Đặt lại mật khẩu thất bại. User ID hoặc mật khẩu mới không hợp lệ.");
            return new ResponseMessage(Constants.RESPONSE_STATUS_INVALID_INPUT, "User ID hoặc mật khẩu mới không hợp lệ.");
        }
        // Admin có thể reset mật khẩu của chính mình, nên không cần kiểm tra requestDTO.getUserId() == adminUser.getUserId() ở đây.

        try {
            boolean success = userDAO.resetUserPassword(requestDTO.getUserId(), requestDTO.getNewPassword());
            if (success) {
                LOGGER.info("resetUserPassword: Mật khẩu người dùng ID " + requestDTO.getUserId() + " được đặt lại thành công.");
                return new ResponseMessage(Constants.RESPONSE_STATUS_SUCCESS, "Đặt lại mật khẩu thành công.");
            } else {
                LOGGER.warning("resetUserPassword: Đặt lại mật khẩu người dùng ID " + requestDTO.getUserId() + " thất bại (không tìm thấy).");
                return new ResponseMessage(Constants.RESPONSE_STATUS_NOT_FOUND, "Đặt lại mật khẩu thất bại. Người dùng không tồn tại."); // Nên dùng NOT_FOUND
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "resetUserPassword: Lỗi khi đặt lại mật khẩu người dùng ID " + requestDTO.getUserId() + ": " + e.getMessage(), e);
            return new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Lỗi server khi đặt lại mật khẩu.");
        }
    }
}