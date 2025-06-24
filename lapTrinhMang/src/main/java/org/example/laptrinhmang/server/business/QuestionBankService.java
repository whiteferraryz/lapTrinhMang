package org.example.laptrinhmang.server.business; // Cần điều chỉnh nếu package thực sự là server.service

import org.example.laptrinhmang.common.dto.QuestionDTO;
import org.example.laptrinhmang.common.dto.QuestionListResponseDTO;
import org.example.laptrinhmang.common.dto.ResponseMessage;
import org.example.laptrinhmang.common.util.Constants;
import org.example.laptrinhmang.server.dataaccess.QuestionDAO;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QuestionBankService {
    private static final Logger LOGGER = Logger.getLogger(QuestionBankService.class.getName());
    private QuestionDAO questionDAO;

    public QuestionBankService(QuestionDAO questionDAO) {
        this.questionDAO = questionDAO;
    }

    public ResponseMessage getAllQuestions() {
        List<QuestionDTO> questions = questionDAO.getAllQuestions();

        if (questions == null) {
            LOGGER.log(Level.WARNING, "QuestionDAO.getAllQuestions() trả về null, có thể có lỗi ẩn.");
            return new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Lỗi hệ thống khi lấy danh sách câu hỏi.");
        } else if (questions.isEmpty()) {
            LOGGER.info("Không có câu hỏi nào trong cơ sở dữ liệu.");
            return new QuestionListResponseDTO(Constants.RESPONSE_STATUS_SUCCESS, "Không có câu hỏi nào được tìm thấy.", questions);
        } else {
            LOGGER.info("Lấy danh sách câu hỏi thành công. Số lượng: " + questions.size());
            return new QuestionListResponseDTO(Constants.RESPONSE_STATUS_SUCCESS, "Lấy danh sách câu hỏi thành công.", questions);
        }
    }

    public ResponseMessage addQuestion(QuestionDTO questionDTO) {
        QuestionDTO addedQuestion = questionDAO.addQuestion(questionDTO);
        if (addedQuestion != null) {
            LOGGER.info("Thêm câu hỏi thành công: " + addedQuestion.getQuestionId()); // <-- ĐÃ SỬA: Dùng getQuestionId()
            return new ResponseMessage(Constants.RESPONSE_STATUS_SUCCESS, "Thêm câu hỏi thành công.");
        } else {
            LOGGER.warning("Thêm câu hỏi thất bại: " + questionDTO.getContent());
            return new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Thêm câu hỏi thất bại. Vui lòng kiểm tra log để biết chi tiết.");
        }
    }

    public ResponseMessage updateQuestion(QuestionDTO questionDTO) {
        boolean success = questionDAO.updateQuestion(questionDTO);
        if (success) {
            LOGGER.info("Cập nhật câu hỏi ID " + questionDTO.getQuestionId() + " thành công."); // <-- ĐÃ SỬA: Dùng getQuestionId()
            return new ResponseMessage(Constants.RESPONSE_STATUS_SUCCESS, "Cập nhật câu hỏi thành công.");
        } else {
            LOGGER.warning("Cập nhật câu hỏi ID " + questionDTO.getQuestionId() + " thất bại."); // <-- ĐÃ SỬA: Dùng getQuestionId()
            return new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Cập nhật câu hỏi thất bại.");
        }
    }

    public ResponseMessage deleteQuestion(int questionId) {
        boolean success = questionDAO.deleteQuestion(questionId);
        if (success) {
            LOGGER.info("Xóa câu hỏi ID " + questionId + " thành công.");
            return new ResponseMessage(Constants.RESPONSE_STATUS_SUCCESS, "Xóa câu hỏi thành công.");
        } else {
            LOGGER.warning("Xóa câu hỏi ID " + questionId + " thất bại.");
            return new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Xóa câu hỏi thất bại.");
        }
    }

    public ResponseMessage getQuestionById(int questionId) {
        QuestionDTO question = questionDAO.getQuestionById(questionId);
        if (question != null) {
            LOGGER.info("Lấy câu hỏi ID " + questionId + " thành công.");
            // Khi trả về một câu hỏi đơn lẻ, bạn có thể đóng gói nó trong một QuestionListResponseDTO
            // hoặc tạo một QuestionResponseDTO nếu bạn có, nhưng ResponseMessage cơ bản cũng được.
            // Để nhất quán với getAllQuestions, có thể dùng QuestionListResponseDTO chứa 1 phần tử
            // Hoặc nếu bạn muốn trả về QuestionDTO trực tiếp trong data của ResponseMessage:
            // return new ResponseMessage(Constants.RESPONSE_STATUS_SUCCESS, "Lấy câu hỏi thành công.", question);
            // Nhưng hiện tại bạn đang dùng "status, message" nên chỉ cần giữ nguyên.
            return new ResponseMessage(Constants.RESPONSE_STATUS_SUCCESS, "Lấy câu hỏi thành công.");
        } else {
            LOGGER.warning("Không tìm thấy câu hỏi ID " + questionId + ".");
            return new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Không tìm thấy câu hỏi.");
        }
    }

    public ResponseMessage searchQuestions(String searchTerm) {
        List<QuestionDTO> questions = questionDAO.searchQuestions(searchTerm);
        if (questions != null && !questions.isEmpty()) {
            LOGGER.info("Tìm kiếm câu hỏi thành công. Số lượng: " + questions.size());
            return new QuestionListResponseDTO(Constants.RESPONSE_STATUS_SUCCESS, "Tìm kiếm câu hỏi thành công.", questions);
        } else {
            LOGGER.info("Không tìm thấy câu hỏi nào với từ khóa: " + searchTerm);
            return new QuestionListResponseDTO(Constants.RESPONSE_STATUS_SUCCESS, "Không tìm thấy câu hỏi nào.", questions);
        }
    }
}