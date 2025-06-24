module org.example.laptrinhmang {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;
    requires java.desktop; // Rất quan trọng cho Swing

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.logging; // Giữ lại nếu bạn sử dụng java.util.logging

    // Mở gói chứa MainApplicationFrame và các FXML controllers khác
    opens org.example.laptrinhmang.client.presentation to javafx.fxml;
    // Mở gói chứa MainClientWindow và các views khác nếu có FXML hoặc cần reflection bởi JavaFX
    opens org.example.laptrinhmang.client.view to javafx.fxml; 
    // Thêm các dòng 'opens' khác nếu bạn có FXML controllers trong các gói con khác

    // Xuất (exports) các gói con chứa logic nghiệp vụ và DTO của bạn
    exports org.example.laptrinhmang.client.business;
    exports org.example.laptrinhmang.client.network;
    exports org.example.laptrinhmang.client.presentation;
    exports org.example.laptrinhmang.client.view; // Xuất gói view nếu các lớp ở đây được module khác dùng
    exports org.example.laptrinhmang.common.dto;
    exports org.example.laptrinhmang.common.model;
    exports org.example.laptrinhmang.common.util;
    // Đã loại bỏ: exports org.example.laptrinhmang;
    // Đã loại bỏ: opens org.example.laptrinhmang to javafx.fxml;
}