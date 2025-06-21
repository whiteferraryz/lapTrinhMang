package org.example.laptrinhmang.server.network;
//Lớp chính Khởi Động Máy Chủ, lắng nghe và chấp nhận các kết nối mới từ Client.
//import các thư viện
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket; // Sử dụng SSLSocket cho kết nối client
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
//import các lớp cần thiết 
import org.example.laptrinhmang.common.util.Constants;
import org.example.laptrinhmang.server.business.ServerContextInitializer;

public class MainServerApplication {
private static final Logger LOGGER = Logger.getLogger(MainServerApplication.class.getName());
    private static final int MAX_THREADS = 10; // Số lượng luồng tối đa cho client handlers
    private SSLServerSocket sslServerSocket;
    private ExecutorService clientThreadPool;

     //Constructor của MainServerApplication.
   
    public MainServerApplication() throws Exception {
        // Khởi tạo ServerContextInitializer để thiết lập các Service và DAO
        ServerContextInitializer.initialize();

        // Khởi tạo SslServerManager với các thông tin từ Constants
        SslServerManager sslManager = new SslServerManager();

        // Tạo SSLServerSocket trên cổng đã định nghĩa
        this.sslServerSocket = sslManager.createSSLServerSocket(Constants.SERVER_PORT);
        LOGGER.info("Server đang lắng nghe các kết nối SSL/TLS trên cổng: " + Constants.SERVER_PORT);

        // Mỗi client mới sẽ được xử lý trong một luồng riêng từ pool này.
        this.clientThreadPool = Executors.newFixedThreadPool(MAX_THREADS);
        LOGGER.info("ExecutorService với " + MAX_THREADS + " luồng đã được khởi tạo.");
    }

    /**
     * Bắt đầu lắng nghe các kết nối từ client.
     * Khi có client kết nối, một ClientConnectionHandler mới sẽ được tạo
     * và chạy trong một luồng riêng biệt từ thread pool.
     */
    public void start() {
        LOGGER.info("Server đã sẵn sàng chấp nhận các kết nối...");
        while (true) { // Vòng lặp vô hạn để liên tục chấp nhận kết nối
            try {
                // Chấp nhận kết nối mới từ client.
                // Phương thức accept() là blocking, nó sẽ dừng lại cho đến khi có client kết nối.
                SSLSocket clientSocket = (SSLSocket) sslServerSocket.accept();
                LOGGER.info("Client mới đã kết nối từ: " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());

                // Thiết lập Timeout cho socket để tránh bị treo vô thời hạn nếu client không phản hồi
                clientSocket.setSoTimeout(Constants.SOCKET_TIMEOUT_MS);

                // Giao nhiệm vụ xử lý client cho một luồng trong thread pool
                // ClientConnectionHandler sẽ xử lý giao tiếp với client này.
                clientThreadPool.submit(new ClientConnectionHandler(clientSocket));
                LOGGER.info("Đã gửi kết nối client tới ClientConnectionHandler.");

            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Lỗi I/O khi chấp nhận kết nối client: " + e.getMessage(), e);
                // Nếu lỗi nghiêm trọng, có thể cần thoát vòng lặp hoặc có chiến lược phục hồi
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Lỗi không mong muốn khi xử lý kết nối client: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Phương thức main để khởi chạy ứng dụng Server.
     *
     * @param args Đối số dòng lệnh (không sử dụng trong trường hợp này).
     */
    public static void main(String[] args) {
        try {
            MainServerApplication server = new MainServerApplication();
            server.start();
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException |
                 CertificateException | UnrecoverableKeyException e) {
            LOGGER.log(Level.SEVERE, "Không thể khởi động Server do lỗi cấu hình SSL/TLS hoặc I/O: " + e.getMessage(), e);
            // Thoát ứng dụng nếu server không thể khởi động đúng cách
            System.exit(1);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi không mong muốn khi khởi động Server: " + e.getMessage(), e);
            System.exit(1);
        }
    }

    /**
     * Đóng server và giải phóng tài nguyên.
     */
    public void shutdown() {
        LOGGER.info("Đang tắt Server...");
        clientThreadPool.shutdownNow(); // Ngừng tất cả các luồng đang chạy
        try {
            if (sslServerSocket != null && !sslServerSocket.isClosed()) {
                sslServerSocket.close(); // Đóng SSLServerSocket
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi đóng SSLServerSocket: " + e.getMessage(), e);
        }
        LOGGER.info("Server đã tắt.");
    }
}
