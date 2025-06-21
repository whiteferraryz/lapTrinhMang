// src/main/java/org/example/laptrinhmang/server/network/SslServerManager.java
package org.example.laptrinhmang.server.network;

import org.example.laptrinhmang.common.util.Constants; // Để tham chiếu đến Constants.SERVER_KEYSTORE_PATH, etc.
import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Properties; // Thêm import này
import java.util.logging.Level;
import java.util.logging.Logger;

public class SslServerManager {
    private static final Logger LOGGER = Logger.getLogger(SslServerManager.class.getName());

    private String keystorePath;
    private char[] keystorePassword;
    private char[] keyPassword; // Mật khẩu cho key (nếu khác với keystore password)

    private SSLContext sslContext;

    public SslServerManager(String keystorePath, char[] keystorePassword, char[] keyPassword) {
        this.keystorePath = keystorePath;
        this.keystorePassword = keystorePassword;
        this.keyPassword = keyPassword;
        try {
            this.sslContext = createSSLContext();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi khởi tạo SSLContext của Server: " + e.getMessage(), e);
            throw new RuntimeException("Không thể khởi tạo SSLContext cho Server", e);
        }
    }

    // Constructor mặc định để tải cấu hình từ application.properties
    public SslServerManager() {
        try {
            Properties prop = new Properties();
            // Đọc từ classpath
            prop.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
            this.keystorePath = prop.getProperty("server.keystore.path");
            this.keystorePassword = prop.getProperty("server.keystore.password").toCharArray();
            this.keyPassword = prop.getProperty("server.key.password").toCharArray();
            LOGGER.info("Đã tải cấu hình SSL Server từ application.properties.");
            this.sslContext = createSSLContext();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tải cấu hình SSL Server từ application.properties: " + e.getMessage(), e);
            throw new RuntimeException("Không thể khởi tạo SSLServerManager", e);
        }
    }


    private SSLContext createSSLContext()
            throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, KeyManagementException {

        KeyStore ks = KeyStore.getInstance("JKS");
        // Đảm bảo đường dẫn chính xác đến keystore trong resources
        try (java.io.InputStream fis = getClass().getClassLoader().getResourceAsStream(keystorePath)) {
    if (fis == null) {
        throw new IOException("Keystore file not found in classpath: " + keystorePath);
    }
    ks.load(fis, keystorePassword);
}

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, keyPassword);

        SSLContext sslContext = SSLContext.getInstance("TLS"); // hoặc "TLSv1.2", "TLSv1.3"
        sslContext.init(kmf.getKeyManagers(), null, null); // null TrustManager[] để không xác thực Client, null SecureRandom

        LOGGER.info("SSLContext của Server đã được tạo thành công.");
        return sslContext;
    }

    public SSLServerSocket createSSLServerSocket(int port) throws IOException {
        SSLServerSocketFactory ssf = sslContext.getServerSocketFactory();
        SSLServerSocket sslServerSocket = (SSLServerSocket) ssf.createServerSocket(port);

        // Tùy chọn: nếu bạn muốn client phải xác thực bằng chứng chỉ client
        // sslServerSocket.setNeedClientAuth(true); // Chỉ bật nếu bạn có logic Client Certificate Authentication

        LOGGER.info("SSLServerSocket đã được tạo trên cổng: " + port);
        return sslServerSocket;
    }
}