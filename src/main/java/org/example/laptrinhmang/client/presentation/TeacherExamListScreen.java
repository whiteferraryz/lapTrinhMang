package org.example.laptrinhmang.client.presentation;

import org.example.laptrinhmang.client.business.ClientContextInitializer;
import org.example.laptrinhmang.client.business.TeacherExamManagementClientService;
import org.example.laptrinhmang.common.dto.ExamDTO;
import org.example.laptrinhmang.common.dto.ResponseMessage;
import org.example.laptrinhmang.common.util.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TeacherExamListScreen extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(TeacherExamListScreen.class.getName());
    private MainApplicationFrame mainFrame;
    private TeacherExamManagementClientService examService;
    private JTable examTable;
    private DefaultTableModel tableModel;
    private JButton createExamButton;
    private JButton editExamButton;
    private JButton deleteExamButton;
    private JButton backButton; // Thêm nút Back

    public TeacherExamListScreen(MainApplicationFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.examService = ClientContextInitializer.getTeacherExamManagementService(); // Lấy service từ context
        initComponents();
        setupLayout();
        addListeners();
        loadExams(); // Tải dữ liệu ban đầu
    }

    private void initComponents() {
        // Cấu hình bảng
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa trực tiếp trên bảng
            }
        };
        tableModel.addColumn("ID");
        tableModel.addColumn("Tiêu đề");
        tableModel.addColumn("Môn học");
        tableModel.addColumn("Thời lượng (phút)");
        tableModel.addColumn("Số câu hỏi");
        tableModel.addColumn("Trạng thái");
        tableModel.addColumn("Ngày tạo");
        tableModel.addColumn("Người tạo");

        examTable = new JTable(tableModel);
        examTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Chỉ cho phép chọn 1 hàng

        // Nút chức năng
        createExamButton = new JButton("Tạo đề thi mới");
        editExamButton = new JButton("Sửa đề thi");
        deleteExamButton = new JButton("Xóa đề thi");
        backButton = new JButton("Quay lại Dashboard"); // Khởi tạo nút Back
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10)); // Thêm khoảng cách giữa các thành phần

        // Tiêu đề màn hình
        JLabel titleLabel = new JLabel("DANH SÁCH ĐỀ THI", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Bảng danh sách đề thi
        JScrollPane scrollPane = new JScrollPane(examTable);
        add(scrollPane, BorderLayout.CENTER);

        // Panel chứa các nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10)); // Căn giữa, thêm khoảng cách
        buttonPanel.add(createExamButton);
        buttonPanel.add(editExamButton);
        buttonPanel.add(deleteExamButton);
        buttonPanel.add(backButton); // Thêm nút Back vào panel

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addListeners() {
        createExamButton.addActionListener(e -> {
            mainFrame.showTeacherExamCreationScreen(this); // Truyền instance của màn hình này
        });

        editExamButton.addActionListener(e -> {
            int selectedRow = examTable.getSelectedRow();
            if (selectedRow != -1) {
                // Lấy ID đề thi từ cột 0 (Exam ID) của hàng được chọn
                int examId = (int) tableModel.getValueAt(selectedRow, 0);
                // Lấy chi tiết ExamDTO từ server
                ExamDTO examToEdit = examService.getExamById(examId);
                if (examToEdit != null) {
                    mainFrame.showTeacherExamEditScreen(this, examToEdit); // Truyền instance của màn hình này và ExamDTO
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể tải chi tiết đề thi. Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một đề thi để sửa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        });

        deleteExamButton.addActionListener(e -> {
            int selectedRow = examTable.getSelectedRow();
            if (selectedRow != -1) {
                int examId = (int) tableModel.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Bạn có chắc chắn muốn xóa đề thi ID: " + examId + " không?",
                        "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    deleteExam(examId);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một đề thi để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        });

        backButton.addActionListener(e -> {
            mainFrame.showTeacherDashboard(); // Quay lại màn hình dashboard
        });
    }

    public void loadExams() {
        LOGGER.info("Đang tải danh sách đề thi...");
        // Xóa tất cả các hàng hiện có trong bảng
        tableModel.setRowCount(0);

        List<ExamDTO> exams = examService.getAllExams();
        if (exams != null && !exams.isEmpty()) {
            for (ExamDTO exam : exams) {
                Vector<Object> row = new Vector<>();
                row.add(exam.getId());
                row.add(exam.getTitle());
                row.add(exam.getSubjectName());
                row.add(exam.getDurationMinutes());
                row.add(exam.getTotalQuestions()); // Lấy từ getter đã được tính toán
                row.add(exam.getStatus());
                row.add(Constants.DATE_FORMAT.format(exam.getCreationDate())); // Định dạng ngày
                row.add(exam.getCreatedByUsername());
                tableModel.addRow(row);
            }
            LOGGER.info("Tải thành công " + exams.size() + " đề thi.");
        } else {
            LOGGER.info("Không có đề thi nào để hiển thị hoặc có lỗi khi tải.");
            // Có thể hiển thị thông báo "Không có dữ liệu" hoặc tương tự
        }
    }

    private void deleteExam(int examId) {
        ResponseMessage response = examService.deleteExam(examId);
        if (response != null && Constants.RESPONSE_STATUS_SUCCESS.equals(response.getStatus())) {
            JOptionPane.showMessageDialog(this, "Xóa đề thi thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadExams(); // Tải lại danh sách sau khi xóa
        } else {
            String errorMessage = (response != null && response.getMessage() != null) ? response.getMessage() : "Không thể xóa đề thi. Lỗi không xác định.";
            JOptionPane.showMessageDialog(this, errorMessage, "Lỗi", JOptionPane.ERROR_MESSAGE);
            LOGGER.log(Level.WARNING, "Xóa đề thi ID " + examId + " thất bại: " + errorMessage);
        }
    }
}