package org.example.laptrinhmang.client.business;
//Xử lý logic phía client cho Đăng nhập.
//import các lớp cần thiết
import org.example.laptrinhmang.client.network.ServerCommunicationConnector;
import org.example.laptrinhmang.common.dto.LoginRequestDTO;
import org.example.laptrinhmang.common.dto.LoginResponseDTO;
import org.example.laptrinhmang.common.dto.RegisterRequestDTO; // Import RegisterRequestDTO
import org.example.laptrinhmang.common.dto.RequestMessage;
import org.example.laptrinhmang.common.dto.ResponseMessage;
import org.example.laptrinhmang.common.util.Constants;
//import các thư viện cần thiết
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Lớp này xử lý logic phía client cho các tác vụ liên quan đến xác thực người dùng,
 * như đăng nhập và đăng ký.
 */
public class UserAuthenticationClientService extends AbstractClientService {

    private static final Logger LOGGER = Logger.getLogger(UserAuthenticationClientService.class.getName());

    // ServerCommunicationConnector được truyền vào từ ClientContextInitializer
    public UserAuthenticationClientService(ServerCommunicationConnector connector) {
        super(connector);
    }

    /**
     * Gửi yêu cầu đăng nhập đến server.
     *
     * @param username Tên đăng nhập.
     * @param password Mật khẩu.
     * @return ResponseMessage từ server chứa kết quả đăng nhập.
     * @throws IOException Nếu có lỗi I/O khi giao tiếp với server.
     * @throws ClassNotFoundException Nếu dữ liệu phản hồi không đúng định dạng.
     */
    public ResponseMessage login(String username, String password) throws IOException, ClassNotFoundException {
        LOGGER.info("Client: Đang cố gắng đăng nhập với username: " + username);
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO(username, password);
        RequestMessage request = new RequestMessage(Constants.REQUEST_TYPE_LOGIN, loginRequestDTO);
        return connector.sendRequest(request); // Sửa tên phương thức
    }

    /**
     * Gửi yêu cầu đăng ký tài khoản mới đến server.
     *
     * @param username Tên đăng nhập mong muốn.
     * @param password Mật khẩu gốc.
     * @param email Email của người dùng.
     * @param roleId Vai trò của người dùng (ví dụ: 1 cho Student).
     * @return ResponseMessage từ server chứa kết quả đăng ký.
     * @throws IOException Nếu có lỗi I/O khi giao tiếp với server.
     * @throws ClassNotFoundException Nếu dữ liệu phản hồi không đúng định dạng.
     */
    public ResponseMessage registerUser(String username, String password, String email, int roleId) throws IOException, ClassNotFoundException {
        LOGGER.info("Client: Đang cố gắng đăng ký người dùng mới: " + username);
        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO(username, password, email, roleId);
        RequestMessage request = new RequestMessage(Constants.REQUEST_TYPE_REGISTER, registerRequestDTO);
        return connector.sendRequest(request);
    }
}
