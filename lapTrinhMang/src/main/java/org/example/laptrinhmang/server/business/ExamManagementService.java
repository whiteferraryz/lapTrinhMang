// src/main/java/org/example/laptrinhmang/server/business/ExamManagementService.java
package org.example.laptrinhmang.server.business;

import org.example.laptrinhmang.common.dto.ExamDTO;
import org.example.laptrinhmang.common.dto.ExamListResponseDTO;
import org.example.laptrinhmang.common.dto.ResponseMessage;
import org.example.laptrinhmang.common.model.Exam; // Import model Exam
// import org.example.laptrinhmang.common.model.Question; // Chỉ cần nếu ExamDTO/Exam có chứa danh sách Question/QuestionDTO cần chuyển đổi sâu
import org.example.laptrinhmang.common.util.Constants;
import org.example.laptrinhmang.server.dataaccess.ExamDAO;
// import org.example.laptrinhmang.server.dataaccess.QuestionDAO; // Chỉ cần nếu bạn cần QuestionDAO để lấy chi tiết Question cho Exam

import java.sql.SQLException;
// import java.sql.Timestamp; // Chỉ cần nếu Date của DTO khác với Date của Model và cần chuyển đổi
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExamManagementService {
    private static final Logger LOGGER = Logger.getLogger(ExamManagementService.class.getName());
    private ExamDAO examDAO;

    public ExamManagementService(ExamDAO examDAO) {
        this.examDAO = examDAO;
    }

    // --- Phương thức trợ giúp để chuyển đổi DTO <-> Model ---

    /**
     * Chuyển đổi một đối tượng ExamDTO sang Exam model.
     * Giả định rằng các trường có tên tương ứng được ánh xạ trực tiếp.
     */
    private Exam convertToExam(ExamDTO examDTO) {
        if (examDTO == null) {
            return null;
        }
        Exam exam = new Exam();
        // Ánh xạ các trường từ DTO sang Model
        exam.setExamId(examDTO.getId()); // Giả định ExamDTO.id -> Exam.examId
        exam.setExamName(examDTO.getTitle()); // Giả định ExamDTO.title -> Exam.examName
        exam.setSubjectId(examDTO.getSubjectId());
        exam.setDurationMinutes(examDTO.getDurationMinutes());
        exam.setCreationDate(examDTO.getCreationDate()); // Giả định kiểu Date tương thích
        // SỬA TÊN PHƯƠNG THỨC Ở ĐÂY:
        exam.setCreatorUserId(examDTO.getCreatedByUserId()); // Đã sửa từ getCreatorUserId()
        exam.setStatus(examDTO.getStatus());
        exam.setQuestionIds(examDTO.getQuestionIds()); // Giả định list ID câu hỏi được truyền trực tiếp

        return exam;
    }

    /**
     * Chuyển đổi một đối tượng Exam model sang ExamDTO.
     * Giả định rằng các trường có tên tương ứng được ánh xạ trực tiếp.
     */
    private ExamDTO convertToExamDTO(Exam exam) {
        if (exam == null) {
            return null;
        }
        ExamDTO examDTO = new ExamDTO();
        // Ánh xạ các trường từ Model sang DTO
        examDTO.setId(exam.getExamId());
        examDTO.setTitle(exam.getExamName());
        examDTO.setSubjectId(exam.getSubjectId());
        examDTO.setDurationMinutes(exam.getDurationMinutes());
        examDTO.setCreationDate(exam.getCreationDate());
        // SỬA TÊN PHƯƠNG THỨC Ở ĐÂY:
        examDTO.setCreatedByUserId(exam.getCreatorUserId()); // Đã sửa từ setCreatorUserId()
        examDTO.setStatus(exam.getStatus());
        examDTO.setQuestionIds(exam.getQuestionIds()); // Giả định list ID câu hỏi được truyền trực tiếp

        // Thêm các trường từ Exam model mà ExamDTO có thể cần hiển thị nhưng không phải để nhập
        examDTO.setSubjectName(exam.getSubjectName());
        examDTO.setCreatedByUsername(exam.getCreatorUsername());
        // Nếu bạn muốn tải chi tiết các câu hỏi vào ExamDTO.questions, bạn sẽ cần thêm logic ở đây
        // và có thể cần một QuestionDAO để lấy QuestionDTOs từ Question IDs hoặc objects.
        return examDTO;
    }

    /**
     * Chuyển đổi một danh sách Exam model sang danh sách ExamDTO.
     */
    private List<ExamDTO> convertToExamDTOList(List<Exam> exams) {
        List<ExamDTO> examDTOs = new ArrayList<>();
        if (exams != null) {
            for (Exam exam : exams) {
                examDTOs.add(convertToExamDTO(exam));
            }
        }
        return examDTOs;
    }

    // --- Các phương thức Service đã sửa đổi ---

    public ResponseMessage addExam(ExamDTO examDTO) {
        try {
            Exam exam = convertToExam(examDTO);
            if (exam == null) {
                return new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Dữ liệu đề thi không hợp lệ.");
            }
            Exam addedExam = examDAO.addExam(exam); // DAO trả về Exam model

            if (addedExam != null && addedExam.getExamId() > 0) {
                LOGGER.info("Thêm đề thi thành công với ID: " + addedExam.getExamId());
                return new ResponseMessage(Constants.RESPONSE_STATUS_SUCCESS, "Thêm đề thi thành công.");
            } else {
                LOGGER.log(Level.WARNING, "Thêm đề thi thất bại: " + examDTO.getTitle() + ". Không lấy được ID.");
                return new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Thêm đề thi thất bại. Vui lòng kiểm tra dữ liệu.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Service: Lỗi DB khi thêm đề thi '" + examDTO.getTitle() + "': " + e.getMessage(), e);
            return new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Lỗi hệ thống khi thêm đề thi.");
        }
    }

    public ResponseMessage updateExam(ExamDTO examDTO) {
        try {
            Exam exam = convertToExam(examDTO);
            if (exam == null) {
                return new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Dữ liệu đề thi không hợp lệ.");
            }
            boolean success = examDAO.updateExam(exam); // DAO trả về boolean

            if (success) {
                LOGGER.info("Cập nhật đề thi ID " + examDTO.getId() + " thành công.");
                return new ResponseMessage(Constants.RESPONSE_STATUS_SUCCESS, "Cập nhật đề thi thành công.");
            } else {
                LOGGER.log(Level.WARNING, "Cập nhật đề thi ID " + examDTO.getId() + " thất bại. Có thể không tìm thấy hoặc không có thay đổi.");
                return new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Cập nhật đề thi thất bại. Không tìm thấy hoặc không có thay đổi.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Service: Lỗi DB khi cập nhật đề thi ID '" + examDTO.getId() + "': " + e.getMessage(), e);
            return new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Lỗi hệ thống khi cập nhật đề thi.");
        }
    }

    public ResponseMessage deleteExam(int examId) {
        try {
            boolean success = examDAO.deleteExam(examId); // DAO trả về boolean

            if (success) {
                LOGGER.info("Xóa đề thi ID " + examId + " thành công.");
                return new ResponseMessage(Constants.RESPONSE_STATUS_SUCCESS, "Xóa đề thi thành công.");
            } else {
                LOGGER.log(Level.WARNING, "Xóa đề thi ID " + examId + " thất bại. Không tìm thấy.");
                return new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Xóa đề thi thất bại. Không tìm thấy.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Service: Lỗi DB khi xóa đề thi ID '" + examId + "': " + e.getMessage(), e);
            return new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Lỗi hệ thống khi xóa đề thi.");
        }
    }

    public ResponseMessage getExamById(int examId) {
        try {
            Exam exam = examDAO.getExamById(examId); // DAO trả về Exam model

            if (exam != null) {
                LOGGER.info("Lấy đề thi ID " + examId + " thành công.");
                ExamDTO examDTO = convertToExamDTO(exam); // Chuyển đổi Model sang DTO
                return new ResponseMessage(Constants.RESPONSE_STATUS_SUCCESS, "Lấy đề thi thành công.", examDTO);
            } else {
                LOGGER.log(Level.WARNING, "Không tìm thấy đề thi ID " + examId);
                return new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Không tìm thấy đề thi.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Service: Lỗi DB khi lấy đề thi ID '" + examId + "': " + e.getMessage(), e);
            return new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Lỗi hệ thống khi lấy đề thi.");
        }
    }

    public ExamListResponseDTO getAllExams() {
        try {
            List<Exam> exams = examDAO.getAllExams(); // DAO trả về List<Exam>
            List<ExamDTO> examDTOs = convertToExamDTOList(exams); // Chuyển đổi List<Model> sang List<DTO>

            if (examDTOs != null && !examDTOs.isEmpty()) {
                LOGGER.info("Lấy danh sách đề thi thành công. Số lượng: " + examDTOs.size());
                return new ExamListResponseDTO(Constants.RESPONSE_STATUS_SUCCESS, "Lấy danh sách đề thi thành công.", examDTOs);
            } else {
                LOGGER.info("Không có đề thi nào được tìm thấy.");
                // Trả về danh sách rỗng nếu không có đề thi nào được tìm thấy, không phải null
                return new ExamListResponseDTO(Constants.RESPONSE_STATUS_SUCCESS, "Không có đề thi nào được tìm thấy.", new ArrayList<>());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Service: Lỗi DB khi lấy tất cả đề thi: " + e.getMessage(), e);
            return new ExamListResponseDTO(Constants.RESPONSE_STATUS_ERROR, "Lỗi hệ thống khi lấy danh sách đề thi.", null); // Trả về null cho danh sách nếu có lỗi
        }
    }
}