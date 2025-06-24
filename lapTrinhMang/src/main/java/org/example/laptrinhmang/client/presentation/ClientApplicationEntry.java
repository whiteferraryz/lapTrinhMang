package org.example.laptrinhmang.client.presentation;

// Import các thư viện cần thiết
import javax.swing.SwingUtilities;
// Import các lớp cần thiết
import org.example.laptrinhmang.client.business.ClientContextInitializer; // THÊM DÒNG NÀY

public class ClientApplicationEntry {
    public static void main(String[] args) { // SỬA: Stringp[] -> String[]
        SwingUtilities.invokeLater(() -> {
            // Khởi tạo các dịch vụ Client-side
            ClientContextInitializer.initialize(); // SỬA: Bỏ "ClientContextInitializer." thừa

            // Khởi tạo khung ứng dụng chính
            MainApplicationFrame mainFrame = new MainApplicationFrame(); // Không cần tham số gì ở đây, constructor rỗng đã được gọi
            
            // Hiển thị màn hình đăng nhập đầu tiên
            // Lớp MainApplicationFrame đã tự động khởi tạo UserLoginScreen và add vào cards
            // và đã gọi showPanel(LOGIN_PANEL) trong constructor của nó.
            // Do đó, không cần khởi tạo UserLoginScreen ở đây nữa.
            // Bạn chỉ cần đảm bảo MainApplicationFrame được hiển thị.
            // mainFrame.showPanel(loginScreen); // Dòng này không cần thiết
            mainFrame.setVisible(true); // Đảm bảo frame được hiển thị
        });
    }
}