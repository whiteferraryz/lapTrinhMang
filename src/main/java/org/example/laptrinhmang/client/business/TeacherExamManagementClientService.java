package org.example.laptrinhmang.client.business;

import org.example.laptrinhmang.client.network.ServerCommunicationConnector;
import org.example.laptrinhmang.common.dto.ExamDTO;
import org.example.laptrinhmang.common.dto.ExamListResponseDTO;
import org.example.laptrinhmang.common.dto.RequestMessage;
import org.example.laptrinhmang.common.dto.ResponseMessage;
import org.example.laptrinhmang.common.util.Constants;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level; // THÊM DÒNG NÀY
import java.util.logging.Logger; // THÊM DÒNG NÀY

public class TeacherExamManagementClientService {
    private static final Logger LOGGER = Logger.getLogger(TeacherExamManagementClientService.class.getName()); // THÊM DÒNG NÀY
    private ServerCommunicationConnector connector;

    public TeacherExamManagementClientService(ServerCommunicationConnector connector) {
        this.connector = connector;
    }

    public ResponseMessage addExam(ExamDTO exam) {
        try { // THÊM try-catch để bắt lỗi giao tiếp mạng
            RequestMessage request = new RequestMessage(Constants.REQUEST_TYPE_ADD_EXAM, exam);
            return connector.sendRequest(request);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm đề thi: " + e.getMessage(), e);
            return new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Lỗi hệ thống khi thêm đề thi.");
        }
    }

    public ResponseMessage updateExam(ExamDTO exam) {
        try { // THÊM try-catch
            RequestMessage request = new RequestMessage(Constants.REQUEST_TYPE_UPDATE_EXAM, exam);
            return connector.sendRequest(request);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật đề thi: " + e.getMessage(), e);
            return new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Lỗi hệ thống khi cập nhật đề thi.");
        }
    }

    public ResponseMessage deleteExam(int examId) {
        try { // THÊM try-catch
            RequestMessage request = new RequestMessage(Constants.REQUEST_TYPE_DELETE_EXAM, examId);
            return connector.sendRequest(request);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi xóa đề thi: " + e.getMessage(), e);
            return new ResponseMessage(Constants.RESPONSE_STATUS_ERROR, "Lỗi hệ thống khi xóa đề thi.");
        }
    }

    public ExamDTO getExamById(int examId) {
        try { // THÊM try-catch
            RequestMessage request = new RequestMessage(Constants.REQUEST_TYPE_GET_EXAM_BY_ID, examId);
            ResponseMessage response = connector.sendRequest(request);
            if (response != null && Constants.RESPONSE_STATUS_SUCCESS.equals(response.getStatus()) && response.getData() instanceof ExamDTO) {
                return (ExamDTO) response.getData();
            }
            LOGGER.warning("Không thể lấy đề thi theo ID " + examId + ": " + (response != null ? response.getMessage() : "Phản hồi null hoặc không thành công."));
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy đề thi theo ID " + examId + ": " + e.getMessage(), e);
            return null;
        }
    }

    public List<ExamDTO> getAllExams() {
        try { // THÊM try-catch
            RequestMessage request = new RequestMessage(Constants.REQUEST_TYPE_GET_ALL_EXAMS, null);
            ResponseMessage response = connector.sendRequest(request);
            // SỬA ĐỔI LOGIC NÀY để lấy dữ liệu từ response.getData()
            if (response != null && Constants.RESPONSE_STATUS_SUCCESS.equals(response.getStatus()) && response.getData() instanceof ExamListResponseDTO) {
                return ((ExamListResponseDTO) response.getData()).getExams();
            }
            LOGGER.warning("Không thể lấy danh sách đề thi: " + (response != null ? response.getMessage() : "Phản hồi null hoặc không thành công."));
            return Collections.emptyList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy tất cả đề thi: " + e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}