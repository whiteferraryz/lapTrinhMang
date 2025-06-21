// src/main/java/org/example/laptrinhmang/client/network/ServerCommunicationConnector.java
package org.example.laptrinhmang.client.network;

import org.example.laptrinhmang.common.dto.RequestMessage;
import org.example.laptrinhmang.common.dto.ResponseMessage;
import org.example.laptrinhmang.common.util.Constants;

import javax.net.ssl.SSLSocket; // <--- THÊM DÒNG NÀY
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerCommunicationConnector {
    private static final Logger LOGGER = Logger.getLogger(ServerCommunicationConnector.class.getName());

    private SSLSocket clientSocket; // <--- ĐỔI TỪ `Socket` THÀNH `SSLSocket`
    private ObjectOutputStream out;
    private ObjectInputStream in;

    // Khởi tạo SslClientManager
    // Lớp này sẽ tự động tải cấu hình từ application.properties
    private final SslClientManager sslClientManager = new SslClientManager(); // <--- THÊM DÒNG NÀY

    public boolean connect() {
        try {
            // Sử dụng SslClientManager để tạo SSLSocket
            clientSocket = sslClientManager.createSSLSocket(Constants.SERVER_IP, Constants.SERVER_PORT);
            
            // Bắt tay SSL: Quan trọng để hoàn tất quá trình thiết lập kết nối bảo mật
            // và trao đổi khóa mã hóa trước khi dữ liệu ứng dụng được gửi đi.
            clientSocket.startHandshake();

            // Thứ tự quan trọng: OUT trước IN để tránh deadlock
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
            LOGGER.info("Đã kết nối SSL thành công tới server.");
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Lỗi kết nối SSL tới server: " + e.getMessage(), e);
            // Có thể thêm logic để hiển thị thông báo lỗi trên UI
            return false;
        }
    }

    public ResponseMessage sendRequest(RequestMessage request) {
        if (clientSocket == null || clientSocket.isClosed()) {
            LOGGER.warning("Kết nối đến server không tồn tại hoặc đã đóng. Thử kết nối lại.");
            if (!connect()) {
                return new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Không thể kết nối lại server.");
            }
        }
        try {
            // Dữ liệu RequestMessage sẽ được ObjectOutputStream serialize
            // và sau đó SSLSocket sẽ tự động mã hóa nó trước khi gửi qua mạng.
            out.writeObject(request);
            out.flush(); // Đảm bảo dữ liệu được đẩy đi ngay lập tức

            // Đọc phản hồi từ server. SSLSocket sẽ tự động giải mã dữ liệu,
            // sau đó ObjectInputStream sẽ deserialize nó thành ResponseMessage.
            Object obj = in.readObject();
            if (obj instanceof ResponseMessage response) {
                return response;
            } else {
                LOGGER.warning("Nhận được đối tượng không hợp lệ từ server.");
                return new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Dữ liệu phản hồi không hợp lệ.");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Lỗi IO khi gửi/nhận dữ liệu từ server: " + e.getMessage(), e);
            closeConnection(); // Đóng kết nối nếu có lỗi IO
            return new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Lỗi giao tiếp với server: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Lỗi ClassNotFoundException khi đọc đối tượng từ server: " + e.getMessage(), e);
            return new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Lỗi định dạng dữ liệu từ server.");
        }
    }

    public void closeConnection() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
            LOGGER.info("Đã đóng kết nối đến server.");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi đóng kết nối client: " + e.getMessage(), e);
        }
    }
    // ... (Thêm các getters nếu cần)
}