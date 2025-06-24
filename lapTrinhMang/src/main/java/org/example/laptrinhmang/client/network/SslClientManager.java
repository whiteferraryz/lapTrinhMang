// src/main/java/org/example/laptrinhmang/client/network/SslClientManager.java
package org.example.laptrinhmang.client.network;

import org.example.laptrinhmang.common.util.Constants;
import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SslClientManager {
    private static final Logger LOGGER = Logger.getLogger(SslClientManager.class.getName());

    private String truststorePath;
    private char[] truststorePassword;

    private SSLContext sslContext;

    public SslClientManager(String truststorePath, char[] truststorePassword) {
        this.truststorePath = truststorePath;
        this.truststorePassword = truststorePassword;
        try {
            this.sslContext = createSSLContext();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi khởi tạo SSLContext của Client: " + e.getMessage(), e);
            throw new RuntimeException("Không thể khởi tạo SSLContext cho Client", e);
        }
    }

    // Constructor mặc định để tải cấu hình từ application.properties
    public SslClientManager() {
        try {
            Properties prop = new Properties();
            prop.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
            this.truststorePath = prop.getProperty("client.truststore.path");
            this.truststorePassword = prop.getProperty("client.truststore.password").toCharArray();
            LOGGER.info("Đã tải cấu hình SSL Client từ application.properties.");
            this.sslContext = createSSLContext();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tải cấu hình SSL Client từ application.properties: " + e.getMessage(), e);
            throw new RuntimeException("Không thể khởi tạo SslClientManager", e);
        }
    }


    private SSLContext createSSLContext()
            throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, KeyManagementException {

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        KeyStore ts = KeyStore.getInstance("JKS");
        // Đảm bảo đường dẫn chính xác đến truststore trong resources
        try (FileInputStream fis = new FileInputStream("src/main/resources/" + truststorePath)) {
            ts.load(fis, truststorePassword);
        }
        tmf.init(ts);

        SSLContext sslContext = SSLContext.getInstance("TLS"); // hoặc "TLSv1.2", "TLSv1.3"
        sslContext.init(null, tmf.getTrustManagers(), null); // null KeyManager[], null SecureRandom

        LOGGER.info("SSLContext của Client đã được tạo thành công.");
        return sslContext;
    }

    public SSLSocket createSSLSocket(String host, int port) throws IOException {
        SSLSocketFactory ssf = sslContext.getSocketFactory();
        SSLSocket sslSocket = (SSLSocket) ssf.createSocket(host, port);

        // Bật chế độ xác thực Hostname (quan trọng cho bảo mật)
        // sslSocket.startHandshake(); // Bắt đầu bắt tay SSL

        LOGGER.info("SSLSocket đã được tạo kết nối tới: " + host + ":" + port);
        return sslSocket;
    }
}