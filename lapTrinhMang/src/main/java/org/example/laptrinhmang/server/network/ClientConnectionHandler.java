// src/main/java/org/example/laptrinhmang/server/network/ClientConnectionHandler.java
package org.example.laptrinhmang.server.network;

import org.example.laptrinhmang.common.dto.*;
import org.example.laptrinhmang.common.util.Constants;
import org.example.laptrinhmang.server.business.ExamManagementService;
import org.example.laptrinhmang.server.business.QuestionBankService;
import org.example.laptrinhmang.server.business.ServerContextInitializer;
import org.example.laptrinhmang.server.business.SubjectService;
import org.example.laptrinhmang.server.business.UserAccountService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientConnectionHandler implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(ClientConnectionHandler.class.getName());
    private Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    // Lấy các service từ ServerContextInitializer
    private UserAccountService userAccountService = ServerContextInitializer.getUserAccountService();
    private SubjectService subjectService = ServerContextInitializer.getSubjectService();
    private QuestionBankService questionBankService = ServerContextInitializer.getQuestionBankService();
    private ExamManagementService examManagementService = ServerContextInitializer.getExamManagementService();


    public ClientConnectionHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            // Thứ tự quan trọng: OUT trước IN để tránh deadlock trong Object Stream
            this.out = new ObjectOutputStream(clientSocket.getOutputStream());
            this.in = new ObjectInputStream(clientSocket.getInputStream());
            LOGGER.info("Đã khởi tạo ObjectInputStream/OutputStream cho client: " + clientSocket.getInetAddress().getHostAddress());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi khởi tạo luồng nhập/xuất cho client: " + e.getMessage(), e);
            closeResources();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Đọc yêu cầu từ client. Dữ liệu đã được giải mã bởi SSLSocket.
                Object obj = in.readObject();
                if (!(obj instanceof RequestMessage request)) {
                    LOGGER.warning("Nhận được đối tượng không hợp lệ từ client.");
                    sendResponse(new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Invalid request format."));
                    continue;
                }

                LOGGER.info("Nhận yêu cầu từ client (" + clientSocket.getInetAddress().getHostAddress() + "): " + request.getRequestType());
                ResponseMessage response = processRequest(request);
                // Gửi phản hồi về client. Dữ liệu sẽ được mã hóa bởi SSLSocket.
                sendResponse(response);
            }
        } catch (IOException e) {
            LOGGER.info("Client " + clientSocket.getInetAddress().getHostAddress() + " đã ngắt kết nối (hoặc lỗi IO): " + e.getMessage());
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Lỗi ClassNotFoundException khi đọc đối tượng từ client: " + e.getMessage(), e);
        } finally {
            closeResources();
        }
    }

    private ResponseMessage processRequest(RequestMessage request) {
        switch (request.getRequestType()) {
            case Constants.REQUEST_TYPE_LOGIN:
                LoginRequestDTO loginReq = (LoginRequestDTO) request.getData();
                return userAccountService.authenticateUser(loginReq.getUsername(), loginReq.getPassword());

            case Constants.REQUEST_TYPE_REGISTER:
                UserDTO userReq = (UserDTO) request.getData();
                return userAccountService.registerUser(userReq);

            case Constants.REQUEST_TYPE_GET_ALL_SUBJECTS:
                // Lỗi ban đầu: Type mismatch: cannot convert from SubjectListResponseDTO to ResponseMessage
                // Giải pháp: Tạo một ResponseMessage bao bọc SubjectListResponseDTO
                SubjectListResponseDTO subjectListResponse = subjectService.getAllSubjects(); // 
                return new ResponseMessage(
                    subjectListResponse.getStatus(),
                    subjectListResponse.getMessage(),
                    subjectListResponse // Đặt SubjectListResponseDTO vào data của ResponseMessage
                );

            case Constants.REQUEST_TYPE_GET_ALL_QUESTIONS:
                return questionBankService.getAllQuestions(); // Giả định này đã trả về ResponseMessage

            // --- Xử lý các yêu cầu liên quan đến Exam ---
            case Constants.REQUEST_TYPE_ADD_EXAM:
                try {
                    ExamDTO newExam = (ExamDTO) request.getData();
                    // Lỗi ban đầu: Type mismatch: cannot convert from ResponseMessage to boolean
                    // Giải pháp: Trực tiếp trả về ResponseMessage từ service
                    return examManagementService.addExam(newExam); // 
                } catch (ClassCastException e) {
                    LOGGER.log(Level.SEVERE, "Dữ liệu yêu cầu ADD_EXAM không phải ExamDTO.", e);
                    return new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Dữ liệu yêu cầu không hợp lệ.");
                }
            case Constants.REQUEST_TYPE_UPDATE_EXAM:
                try {
                    ExamDTO updatedExam = (ExamDTO) request.getData();
                    // Lỗi ban đầu: Type mismatch: cannot convert from ResponseMessage to boolean
                    // Giải pháp: Trực tiếp trả về ResponseMessage từ service
                    return examManagementService.updateExam(updatedExam); // 
                } catch (ClassCastException e) {
                    LOGGER.log(Level.SEVERE, "Dữ liệu yêu cầu UPDATE_EXAM không phải ExamDTO.", e);
                    return new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Dữ liệu yêu cầu không hợp lệ.");
                }
            case Constants.REQUEST_TYPE_DELETE_EXAM:
                try {
                    Integer examIdToDelete = (Integer) request.getData();
                    // Lỗi ban đầu: Type mismatch: cannot convert from ResponseMessage to boolean
                    // Giải pháp: Trực tiếp trả về ResponseMessage từ service
                    return examManagementService.deleteExam(examIdToDelete); // 
                } catch (ClassCastException e) {
                    LOGGER.log(Level.SEVERE, "Dữ liệu yêu cầu DELETE_EXAM không phải Integer (ID).", e);
                    return new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Dữ liệu yêu cầu không hợp lệ.");
                }
            case Constants.REQUEST_TYPE_GET_EXAM_BY_ID:
                try {
                    Integer examId = (Integer) request.getData();
                    // Lỗi ban đầu: Type mismatch: cannot convert from ResponseMessage to ExamDTO
                    // Giải pháp: Phương thức service đã trả về ResponseMessage chứa ExamDTO.
                    // Chỉ cần trả về trực tiếp kết quả từ service.
                    return examManagementService.getExamById(examId); // 
                } catch (ClassCastException e) {
                    LOGGER.log(Level.SEVERE, "Dữ liệu yêu cầu GET_EXAM_BY_ID không phải Integer (ID).", e);
                    return new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Dữ liệu yêu cầu không hợp lệ.");
                }
            case Constants.REQUEST_TYPE_GET_ALL_EXAMS:
                // Lỗi ban đầu: Type mismatch: cannot convert from ExamListResponseDTO to List<ExamDTO>
                // Giải pháp: Phương thức service đã trả về ExamListResponseDTO.
                // ExamListResponseDTO kế thừa từ ResponseMessage, nên có thể trả về trực tiếp.
                return examManagementService.getAllExams(); // 

            // ... (Thêm các case khác cho các chức năng còn lại nếu bạn mở rộng)

            default:
                LOGGER.warning("Loại yêu cầu không xác định: " + request.getRequestType());
                return new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Unknown request type.");
        }
    }

    private void sendResponse(ResponseMessage response) {
        try {
            // Gửi đối tượng ResponseMessage. SSLSocket sẽ tự động mã hóa nó.
            out.writeObject(response);
            out.flush();
            LOGGER.info("Đã gửi phản hồi về client: Status=" + response.getStatus() + ", Message=" + response.getMessage());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi gửi phản hồi về client: " + e.getMessage(), e);
        }
    }

    private void closeResources() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
            LOGGER.info("Đã đóng tài nguyên cho client: " + (clientSocket != null ? clientSocket.getInetAddress().getHostAddress() : "N/A"));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi đóng tài nguyên client handler: " + e.getMessage(), e);
        }
    }
}   