package org.example.laptrinhmang.client.presentation;

import org.example.laptrinhmang.client.business.ClientContextInitializer;
import org.example.laptrinhmang.client.business.TeacherExamManagementClientService;
import org.example.laptrinhmang.client.business.TeacherQuestionBankClientService;
import org.example.laptrinhmang.common.dto.ExamDTO;
import org.example.laptrinhmang.common.dto.QuestionDTO;
import org.example.laptrinhmang.common.dto.ResponseMessage;
import org.example.laptrinhmang.common.dto.SubjectDTO;
import org.example.laptrinhmang.common.util.Constants;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date; // Để sử dụng cho creationDate
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TeacherExamCreationScreen extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(TeacherExamCreationScreen.class.getName());
    private MainApplicationFrame mainFrame;
    private TeacherExamListScreen callerScreen; // Tham chiếu đến màn hình gọi để refresh
    private TeacherExamManagementClientService examService;
    private TeacherQuestionBankClientService questionBankService;

    // Components cho thông tin đề thi
    private JTextField txtExamTitle;
    private JComboBox<SubjectDTO> cmbSubject;
    private JSpinner spDuration;
    private JButton btnSelectQuestions; // Nút để mở cửa sổ chọn câu hỏi
    private JLabel lblSelectedQuestionsCount;

    // Components cho phần câu hỏi đã chọn (hiển thị tóm tắt)
    private JTable selectedQuestionsTable;
    private DefaultTableModel selectedQuestionsTableModel;

    // Nút chức năng
    private JButton btnCreateExam;
    private JButton btnCancel;

    // Danh sách các câu hỏi hiện có và câu hỏi đã được chọn
    private List<QuestionDTO> allAvailableQuestions; // Tất cả các câu hỏi từ ngân hàng
    private List<QuestionDTO> currentSelectedQuestions; // Các câu hỏi đã chọn cho đề thi này

    public TeacherExamCreationScreen(MainApplicationFrame mainFrame, TeacherExamListScreen callerScreen) {
        this.mainFrame = mainFrame;
        this.callerScreen = callerScreen;
        this.examService = ClientContextInitializer.getTeacherExamManagementService();
        this.questionBankService = ClientContextInitializer.getTeacherQuestionBankService();
        this.allAvailableQuestions = new ArrayList<>();
        this.currentSelectedQuestions = new ArrayList<>();

        initComponents();
        setupLayout();
        addListeners();
        loadInitialData(); // Tải môn học và câu hỏi khi khởi tạo màn hình
    }

    private void initComponents() {
        // --- Phần thông tin đề thi ---
        txtExamTitle = new JTextField(30);
        cmbSubject = new JComboBox<>();
        spDuration = new JSpinner(new SpinnerNumberModel(60, 10, 240, 5)); // Thời lượng từ 10-240 phút, bước 5
        ((JSpinner.DefaultEditor) spDuration.getEditor()).getTextField().setEditable(true); // Cho phép sửa trực tiếp

        btnSelectQuestions = new JButton("Chọn câu hỏi");
        lblSelectedQuestionsCount = new JLabel("Đã chọn: 0 câu hỏi");

        // --- Bảng hiển thị tóm tắt câu hỏi đã chọn ---
        selectedQuestionsTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        selectedQuestionsTableModel.addColumn("ID");
        selectedQuestionsTableModel.addColumn("Nội dung");
        selectedQuestionsTableModel.addColumn("Môn học");
        selectedQuestionsTableModel.addColumn("Độ khó");
        selectedQuestionsTable = new JTable(selectedQuestionsTableModel);
        selectedQuestionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Cho phép chọn để xóa

        // --- Nút chức năng ---
        btnCreateExam = new JButton("Tạo đề thi");
        btnCancel = new JButton("Hủy");
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("TẠO ĐỀ THI MỚI", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        add(titleLabel, BorderLayout.NORTH);

        // Panel cho thông tin đề thi
        JPanel examInfoPanel = new JPanel(new GridBagLayout());
        examInfoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Thông tin đề thi", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 16)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.WEST;
        examInfoPanel.add(new JLabel("Tiêu đề đề thi:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        examInfoPanel.add(txtExamTitle, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 0;
        examInfoPanel.add(new JLabel("Môn học:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        examInfoPanel.add(cmbSubject, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 0;
        examInfoPanel.add(new JLabel("Thời lượng (phút):"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        examInfoPanel.add(spDuration, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 0;
        examInfoPanel.add(btnSelectQuestions, gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        examInfoPanel.add(lblSelectedQuestionsCount, gbc);

        add(examInfoPanel, BorderLayout.NORTH);

        // Panel cho câu hỏi đã chọn
        JPanel selectedQuestionsPanel = new JPanel(new BorderLayout(5, 5));
        selectedQuestionsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Câu hỏi đã chọn", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 16)));
        selectedQuestionsPanel.add(new JScrollPane(selectedQuestionsTable), BorderLayout.CENTER);

        // Nút xóa câu hỏi đã chọn
        JButton btnRemoveSelectedQuestion = new JButton("Xóa câu hỏi đã chọn");
        btnRemoveSelectedQuestion.addActionListener(e -> removeSelectedQuestion());
        JPanel removeButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        removeButtonPanel.add(btnRemoveSelectedQuestion);
        selectedQuestionsPanel.add(removeButtonPanel, BorderLayout.SOUTH);

        add(selectedQuestionsPanel, BorderLayout.CENTER);

        // Panel cho nút chức năng chính
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(btnCreateExam);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addListeners() {
        btnSelectQuestions.addActionListener(e -> openQuestionSelectionDialog());
        btnCreateExam.addActionListener(e -> createExam());
        btnCancel.addActionListener(e -> mainFrame.showTeacherExamListAndRefresh());
    }

    private void loadInitialData() {
        // Tải môn học
        List<SubjectDTO> subjects = questionBankService.getAllSubjects();
        if (subjects != null && !subjects.isEmpty()) {
            for (SubjectDTO subject : subjects) {
                cmbSubject.addItem(subject);
            }
        } else {
            LOGGER.warning("Không thể tải danh sách môn học.");
            JOptionPane.showMessageDialog(this, "Không thể tải danh sách môn học. Vui lòng kiểm tra kết nối.", "Lỗi tải dữ liệu", JOptionPane.ERROR_MESSAGE);
        }

        // Tải tất cả câu hỏi từ ngân hàng
        allAvailableQuestions = questionBankService.getAllQuestions();
        if (allAvailableQuestions == null) {
            allAvailableQuestions = new ArrayList<>(); // Đảm bảo không null
            LOGGER.warning("Không thể tải danh sách câu hỏi từ ngân hàng.");
            JOptionPane.showMessageDialog(this, "Không thể tải danh sách câu hỏi. Vui lòng kiểm tra kết nối.", "Lỗi tải dữ liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Mở hộp thoại chọn câu hỏi
    private void openQuestionSelectionDialog() {
        if (allAvailableQuestions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có câu hỏi nào trong ngân hàng để chọn.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Tạo một JDialog mới cho việc chọn câu hỏi
        JDialog selectionDialog = new JDialog(mainFrame, "Chọn câu hỏi", true); // true cho modal
        selectionDialog.setLayout(new BorderLayout(5, 5));
        selectionDialog.setSize(800, 600);
        selectionDialog.setLocationRelativeTo(mainFrame); // Hiển thị giữa màn hình chính

        // Bảng để hiển thị tất cả các câu hỏi
        DefaultTableModel allQuestionsTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            // Thêm cột Checkbox
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) { // Cột đầu tiên là checkbox
                    return Boolean.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };
        allQuestionsTableModel.addColumn("Chọn"); // Cột Checkbox
        allQuestionsTableModel.addColumn("ID");
        allQuestionsTableModel.addColumn("Nội dung");
        allQuestionsTableModel.addColumn("Môn học");
        allQuestionsTableModel.addColumn("Độ khó");

        JTable allQuestionsTable = new JTable(allQuestionsTableModel);
        allQuestionsTable.setFillsViewportHeight(true);

        // Load tất cả câu hỏi vào bảng trong dialog
        for (QuestionDTO q : allAvailableQuestions) {
            boolean isSelected = currentSelectedQuestions.contains(q); // Kiểm tra nếu đã chọn
            Vector<Object> row = new Vector<>();
            row.add(isSelected); // Giá trị ban đầu của checkbox
            row.add(q.getQuestionId());
            row.add(q.getContent());
            row.add(q.getSubjectName());
            row.add(q.getDifficultyLevel());
            allQuestionsTableModel.addRow(row);
        }

        selectionDialog.add(new JScrollPane(allQuestionsTable), BorderLayout.CENTER);

        // Panel chứa các nút trong dialog
        JPanel dialogButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnConfirmSelection = new JButton("Xác nhận");
        JButton btnCancelSelection = new JButton("Hủy");

        btnConfirmSelection.addActionListener(e -> {
            List<QuestionDTO> newSelectedQuestions = new ArrayList<>();
            for (int i = 0; i < allQuestionsTableModel.getRowCount(); i++) {
                Boolean isChecked = (Boolean) allQuestionsTableModel.getValueAt(i, 0); // Lấy trạng thái checkbox
                if (isChecked) {
                    int questionId = (int) allQuestionsTableModel.getValueAt(i, 1); // Lấy ID câu hỏi
                    // Tìm QuestionDTO tương ứng trong allAvailableQuestions
                    allAvailableQuestions.stream()
                            .filter(q -> q.getQuestionId() == questionId)
                            .findFirst()
                            .ifPresent(newSelectedQuestions::add);
                }
            }
            currentSelectedQuestions = newSelectedQuestions; // Cập nhật danh sách đã chọn
            updateSelectedQuestionsTable(); // Cập nhật bảng hiển thị
            selectionDialog.dispose(); // Đóng dialog
        });

        btnCancelSelection.addActionListener(e -> selectionDialog.dispose());

        dialogButtonPanel.add(btnConfirmSelection);
        dialogButtonPanel.add(btnCancelSelection);
        selectionDialog.add(dialogButtonPanel, BorderLayout.SOUTH);

        selectionDialog.setVisible(true);
    }

    // Cập nhật bảng hiển thị các câu hỏi đã chọn
    private void updateSelectedQuestionsTable() {
        selectedQuestionsTableModel.setRowCount(0); // Xóa tất cả các hàng cũ
        for (QuestionDTO q : currentSelectedQuestions) {
            Vector<Object> row = new Vector<>();
            row.add(q.getQuestionId());
            row.add(q.getContent());
            row.add(q.getSubjectName());
            row.add(q.getDifficultyLevel());
            selectedQuestionsTableModel.addRow(row);
        }
        lblSelectedQuestionsCount.setText("Đã chọn: " + currentSelectedQuestions.size() + " câu hỏi");
    }

    // Xóa câu hỏi đã chọn khỏi danh sách
    private void removeSelectedQuestion() {
        int selectedRow = selectedQuestionsTable.getSelectedRow();
        if (selectedRow != -1) {
            int questionIdToRemove = (int) selectedQuestionsTableModel.getValueAt(selectedRow, 0);
            // Xóa khỏi currentSelectedQuestions
            currentSelectedQuestions.removeIf(q -> q.getQuestionId() == questionIdToRemove);
            updateSelectedQuestionsTable(); // Cập nhật lại bảng
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một câu hỏi để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Xử lý logic tạo đề thi
    private void createExam() {
        String title = txtExamTitle.getText().trim();
        SubjectDTO selectedSubject = (SubjectDTO) cmbSubject.getSelectedItem();
        int duration = (int) spDuration.getValue();

        if (title.isEmpty() || selectedSubject == null || currentSelectedQuestions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin đề thi và chọn ít nhất một câu hỏi.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Lấy thông tin người tạo từ session hoặc một nguồn nào đó (giả định có sẵn trong MainApplicationFrame)
        // Hiện tại, tôi sẽ lấy user ID và username từ ClientContextInitializer
        int createdByUserId = ClientContextInitializer.getCurrentLoggedInUser().getUserId();
        String createdByUsername = ClientContextInitializer.getCurrentLoggedInUser().getUsername();

        ExamDTO newExam = new ExamDTO();
        newExam.setTitle(title);
        newExam.setSubjectId(selectedSubject.getSubjectId());
        newExam.setSubjectName(selectedSubject.getSubjectName());
        newExam.setDurationMinutes(duration);
        newExam.setStatus(Constants.EXAM_STATUS_DRAFT); // Mặc định là DRAFT khi tạo mới
        newExam.setCreationDate(new Date()); // Gán ngày tạo hiện tại
        newExam.setCreatedByUserId(createdByUserId);
        newExam.setCreatedByUsername(createdByUsername);

        // Chuyển danh sách QuestionDTO đã chọn thành List<Integer> (chỉ ID)
        List<Integer> questionIds = currentSelectedQuestions.stream()
                .map(QuestionDTO::getQuestionId)
                .collect(Collectors.toList());
        newExam.setQuestionIds(questionIds);
        

        // Gọi service để thêm đề thi
        ResponseMessage response = examService.addExam(newExam);

        if (response != null && Constants.RESPONSE_STATUS_SUCCESS.equals(response.getStatus())) {
            JOptionPane.showMessageDialog(this, "Tạo đề thi thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.showTeacherExamListAndRefresh(); // Quay lại và làm mới màn hình danh sách
        } else {
            String errorMessage = (response != null && response.getMessage() != null) ? response.getMessage() : "Tạo đề thi thất bại. Lỗi không xác định.";
            JOptionPane.showMessageDialog(this, errorMessage, "Lỗi", JOptionPane.ERROR_MESSAGE);
            LOGGER.log(Level.WARNING, "Tạo đề thi thất bại: " + errorMessage);
        }
    }
}