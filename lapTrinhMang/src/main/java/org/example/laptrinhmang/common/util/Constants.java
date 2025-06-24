package org.example.laptrinhmang.common.util; // ĐÃ SỬA PACKAGE TẠY ĐÂY

import java.text.SimpleDateFormat;

public class Constants {
    // Server and Network
    public static final int SERVER_PORT = 12345;
    public static final String SERVER_IP = "localhost"; // Hoặc địa chỉ IP của server

    // Định nghĩa hằng số cho Role ID của Admin (rất quan trọng)
    public static final int ADMIN_ROLE_ID = 3;
    public static final int TEACHER_ROLE_ID = 2;
    public static final int STUDENT_ROLE_ID = 1;

    // Question Types (Thêm hoặc xác nhận các hằng số này)
    public static final String QUESTION_TYPE_MULTIPLE_CHOICE = "MULTIPLE_CHOICE";
    public static final String QUESTION_TYPE_TRUE_FALSE = "TRUE_FALSE";
    public static final String QUESTION_TYPE_SHORT_ANSWER = "SHORT_ANSWER";

    public static final String REQUEST_TYPE_SEARCH_QUESTIONS = "SEARCH_QUESTIONS"; // <--- THÊM DÒNG NÀY

    // Thêm các hằng số này cho User Management
    public static final String REQUEST_TYPE_GET_ALL_USERS = "GET_ALL_USERS"; // <--- THÊM DÒNG NÀY
    public static final String REQUEST_TYPE_UPDATE_USER = "UPDATE_USER"; // <--- THÊM DÒNG NÀY
    public static final String REQUEST_TYPE_DELETE_USER = "DELETE_USER"; // <--- THÊM DÒNG NÀY
    public static final String REQUEST_TYPE_RESET_PASSWORD = "RESET_PASSWORD"; // <--- THÊM DÒNG NÀY

    // Response Status Codes
    public static final String RESPONSE_STATUS_SUCCESS = "SUCCESS";
    public static final String RESPONSE_STATUS_INVALID_INPUT = "INVALID_INPUT";
    public static final String RESPONSE_STATUS_FAILED = "FAILED";
    public static final String RESPONSE_STATUS_ERROR = "ERROR";
    public static final String RESPONSE_STATUS_PERMISSION_DENIED = "PERMISSION_DENIED";
    public static final String RESPONSE_STATUS_BAD_REQUEST = "BAD_REQUEST";
    public static final String RESPONSE_STATUS_NOT_FOUND = "NOT_FOUND"; // THÊM DÒNG NÀY

    // Request Types
    public static final String REQUEST_TYPE_LOGIN = "LOGIN";
    public static final String REQUEST_TYPE_REGISTER = "REGISTER"; // Nếu có chức năng đăng ký
    public static final String REQUEST_TYPE_GET_USER_PROFILE = "GET_USER_PROFILE"; // Nếu có chức năng xem profile

    // Question Management
    public static final String REQUEST_TYPE_ADD_QUESTION = "ADD_QUESTION";
    public static final String REQUEST_TYPE_GET_ALL_QUESTIONS = "GET_ALL_QUESTIONS";
    public static final String REQUEST_TYPE_UPDATE_QUESTION = "UPDATE_QUESTION";
    public static final String REQUEST_TYPE_DELETE_QUESTION = "DELETE_QUESTION";
    public static final String REQUEST_TYPE_GET_QUESTION_BY_ID = "GET_QUESTION_BY_ID"; // Nếu cần lấy 1 câu hỏi cụ thể

    // Subject Management
    public static final String REQUEST_TYPE_ADD_SUBJECT = "ADD_SUBJECT"; // Nếu có chức năng thêm môn học
    public static final String REQUEST_TYPE_GET_ALL_SUBJECTS = "GET_ALL_SUBJECTS";
    
    // SSL/TLS Configuration
    public static final String SERVER_KEYSTORE_PATH = "server-keystore.jks"; // Đường dẫn tới keystore của server
    public static final String SERVER_KEYSTORE_PASSWORD = "your_keystore_password"; // Mật khẩu keystore
    public static final String SERVER_KEY_PASSWORD = "your_key_password"; // Mật khẩu cho private key trong keystore

    // Network Configuration
    public static final int SOCKET_TIMEOUT_MS = 30000;  

    // Exam Management (MỚI)
    public static final String REQUEST_TYPE_ADD_EXAM = "ADD_EXAM";
    public static final String REQUEST_TYPE_GET_ALL_EXAMS = "GET_ALL_EXAMS";
    public static final String REQUEST_TYPE_GET_EXAM_BY_ID = "GET_EXAM_BY_ID";
    public static final String REQUEST_TYPE_UPDATE_EXAM = "UPDATE_EXAM";
    public static final String REQUEST_TYPE_DELETE_EXAM = "DELETE_EXAM";

    // User Roles
    public static final int ROLE_ID_ADMIN = 1;
    public static final int ROLE_ID_TEACHER = 2;
    public static final int ROLE_ID_STUDENT = 3;

    // Exam Status
    public static final String EXAM_STATUS_DRAFT = "DRAFT";
    public static final String EXAM_STATUS_PUBLISHED = "PUBLISHED";
    public static final String EXAM_STATUS_ARCHIVED = "ARCHIVED";

    // Date Format for display
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    // Private constructor to prevent instantiation
    private Constants() {}
}