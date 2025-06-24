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
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TeacherExamEditScreen extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(TeacherExamEditScreen.class.getName());
    private MainApplicationFrame mainFrame;
    private TeacherExamListScreen callerScreen; // Tham chiếu đến màn hình gọi để refresh
    private ExamDTO originalExam; // Đề thi gốc đang được chỉnh sửa

    private TeacherExamManagementClientService examService;
    private TeacherQuestionBankClientService questionBankService;

    // Components cho thông tin đề thi
    private JLabel lblExamId;
    private JTextField txtExamTitle;
    private JComboBox<SubjectDTO> cmbSubject;
    private JSpinner spDuration;
    private JLabel lblStatus; // Hiển thị trạng thái đề thi (Draft/Published)
    private JLabel lblCreationDate; // Hiển thị ngày tạo
    private JLabel lblCreatedBy; // Hiển thị người tạo

    private JButton btnSelectQuestions;
    private JLabel lblSelectedQuestionsCount;

    // Components cho phần câu hỏi đã chọn (hiển thị tóm tắt)
    private JTable selectedQuestionsTable;
    private DefaultTableModel selectedQuestionsTableModel;

    // Nút chức năng
    private JButton btnUpdateExam;
    private JButton btnCancel;

    // Danh sách các câu hỏi hiện có và câu hỏi đã được chọn
    private List<QuestionDTO> allAvailableQuestions;
    private List<QuestionDTO> currentSelectedQuestions; // Các câu hỏi đã chọn cho đề thi này

    public TeacherExamEditScreen(MainApplicationFrame mainFrame, TeacherExamListScreen callerScreen, ExamDTO examToEdit) {
        this.mainFrame = mainFrame;
        this.callerScreen = callerScreen;
        this.originalExam = examToEdit; // Đặt đề thi gốc
        this.examService = ClientContextInitializer.getTeacherExamManagementService();
        this.questionBankService = ClientContextInitializer.getTeacherQuestionBankService();
        this.allAvailableQuestions = new ArrayList<>();
        this.currentSelectedQuestions = new ArrayList<>();

        initComponents();
        setupLayout();
        addListeners();
        loadInitialData(); // Tải môn học và tất cả câu hỏi
        populateExamData(); // Đổ dữ liệu của đề thi gốc vào form
    }

    private void initComponents() {
        // --- Phần thông tin đề thi ---
        lblExamId = new JLabel();
        txtExamTitle = new JTextField(30);
        cmbSubject = new JComboBox<>();
        spDuration = new JSpinner(new SpinnerNumberModel(60, 10, 240, 5));
        ((JSpinner.DefaultEditor) spDuration.getEditor()).getTextField().setEditable(true);

        lblStatus = new JLabel();
        lblCreationDate = new JLabel();
        lblCreatedBy = new JLabel();

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
        selectedQuestionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // --- Nút chức năng ---
        btnUpdateExam = new JButton("Cập nhật đề thi");
        btnCancel = new JButton("Hủy");
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("SỬA ĐỀ THI", SwingConstants.CENTER);
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
        examInfoPanel.add(new JLabel("ID Đề thi:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        examInfoPanel.add(lblExamId, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 0;
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
        examInfoPanel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        examInfoPanel.add(lblStatus, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 0;
        examInfoPanel.add(new JLabel("Ngày tạo:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        examInfoPanel.add(lblCreationDate, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 0;
        examInfoPanel.add(new JLabel("Người tạo:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        examInfoPanel.add(lblCreatedBy, gbc);

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

        JButton btnRemoveSelectedQuestion = new JButton("Xóa câu hỏi đã chọn");
        btnRemoveSelectedQuestion.addActionListener(e -> removeSelectedQuestion());
        JPanel removeButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        removeButtonPanel.add(btnRemoveSelectedQuestion);
        selectedQuestionsPanel.add(removeButtonPanel, BorderLayout.SOUTH);

        add(selectedQuestionsPanel, BorderLayout.CENTER);

        // Panel cho nút chức năng chính
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(btnUpdateExam);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addListeners() {
        btnSelectQuestions.addActionListener(e -> openQuestionSelectionDialog());
        btnUpdateExam.addActionListener(e -> updateExam());
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
            allAvailableQuestions = new ArrayList<>();
            LOGGER.warning("Không thể tải danh sách câu hỏi từ ngân hàng.");
            JOptionPane.showMessageDialog(this, "Không thể tải danh sách câu hỏi. Vui lòng kiểm tra kết nối.", "Lỗi tải dữ liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateExamData() {
        if (originalExam != null) {
            lblExamId.setText(String.valueOf(originalExam.getId()));
            txtExamTitle.setText(originalExam.getTitle());

            // Chọn môn học trong ComboBox
            for (int i = 0; i < cmbSubject.getItemCount(); i++) {
                SubjectDTO subject = cmbSubject.getItemAt(i);
                if (subject.getSubjectId() == originalExam.getSubjectId()) { // Sửa từ getId() -> getSubjectId()
                    cmbSubject.setSelectedItem(subject);
                    break;
                }
            }
            spDuration.setValue(originalExam.getDurationMinutes());
            lblStatus.setText(originalExam.getStatus());
            lblCreationDate.setText(Constants.DATE_FORMAT.format(originalExam.getCreationDate()));
            lblCreatedBy.setText(originalExam.getCreatedByUsername());

            // Tải các câu hỏi đã chọn trước đó
            if (originalExam.getQuestionIds() != null && !originalExam.getQuestionIds().isEmpty() && !allAvailableQuestions.isEmpty()) {
                for (int qId : originalExam.getQuestionIds()) {
                    allAvailableQuestions.stream()
                            .filter(q -> q.getQuestionId() == qId) // Sửa: getId() -> getQuestionId()
                            .findFirst()
                            .ifPresent(currentSelectedQuestions::add);
                }
            }
            updateSelectedQuestionsTable();
        }
    }

    private void openQuestionSelectionDialog() {
        if (allAvailableQuestions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có câu hỏi nào trong ngân hàng để chọn.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog selectionDialog = new JDialog(mainFrame, "Chọn câu hỏi", true);
        selectionDialog.setLayout(new BorderLayout(5, 5));
        selectionDialog.setSize(800, 600);
        selectionDialog.setLocationRelativeTo(mainFrame);

        DefaultTableModel allQuestionsTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };
        allQuestionsTableModel.addColumn("Chọn");
        allQuestionsTableModel.addColumn("ID");
        allQuestionsTableModel.addColumn("Nội dung");
        allQuestionsTableModel.addColumn("Môn học");
        allQuestionsTableModel.addColumn("Độ khó");

        JTable allQuestionsTable = new JTable(allQuestionsTableModel);
        allQuestionsTable.setFillsViewportHeight(true);

        for (QuestionDTO q : allAvailableQuestions) {
            boolean isSelected = currentSelectedQuestions.contains(q);
            Vector<Object> row = new Vector<>();
            row.add(isSelected);
            row.add(q.getQuestionId()); // Sửa: getId() -> getQuestionId()
            row.add(q.getContent());
            row.add(q.getSubjectName());
            row.add(q.getDifficultyLevel());
            allQuestionsTableModel.addRow(row);
        }

        selectionDialog.add(new JScrollPane(allQuestionsTable), BorderLayout.CENTER);

        JPanel dialogButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnConfirmSelection = new JButton("Xác nhận");
        JButton btnCancelSelection = new JButton("Hủy");

        btnConfirmSelection.addActionListener(e -> {
            List<QuestionDTO> newSelectedQuestions = new ArrayList<>();
            for (int i = 0; i < allQuestionsTableModel.getRowCount(); i++) {
                Boolean isChecked = (Boolean) allQuestionsTableModel.getValueAt(i, 0);
                if (isChecked) {
                    int questionId = (int) allQuestionsTableModel.getValueAt(i, 1);
                    allAvailableQuestions.stream()
                            .filter(q -> q.getQuestionId() == questionId)
                            .findFirst()
                            .ifPresent(newSelectedQuestions::add);
                }
            }
            currentSelectedQuestions = newSelectedQuestions;
            updateSelectedQuestionsTable();
            selectionDialog.dispose();
        });

        btnCancelSelection.addActionListener(e -> selectionDialog.dispose());

        dialogButtonPanel.add(btnConfirmSelection);
        dialogButtonPanel.add(btnCancelSelection);
        selectionDialog.add(dialogButtonPanel, BorderLayout.SOUTH);

        selectionDialog.setVisible(true);
    }

    private void updateSelectedQuestionsTable() {
        selectedQuestionsTableModel.setRowCount(0);
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

    private void removeSelectedQuestion() {
        int selectedRow = selectedQuestionsTable.getSelectedRow();
        if (selectedRow != -1) {
            int questionIdToRemove = (int) selectedQuestionsTableModel.getValueAt(selectedRow, 0);
            currentSelectedQuestions.removeIf(q -> q.getQuestionId() == questionIdToRemove);
            updateSelectedQuestionsTable();
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một câu hỏi để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Xử lý logic cập nhật đề thi
    private void updateExam() {
        String title = txtExamTitle.getText().trim();
        SubjectDTO selectedSubject = (SubjectDTO) cmbSubject.getSelectedItem();
        int duration = (int) spDuration.getValue();

        if (title.isEmpty() || selectedSubject == null || currentSelectedQuestions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin đề thi và chọn ít nhất một câu hỏi.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ExamDTO updatedExam = new ExamDTO();
        updatedExam.setId(originalExam.getId()); // Giữ nguyên ID của đề thi gốc
        updatedExam.setTitle(title);
        updatedExam.setSubjectId(selectedSubject.getSubjectId()); // Sửa: getId() -> getSubjectId()
        updatedExam.setSubjectName(selectedSubject.getSubjectName()); // Sửa từ getName() -> getSubjectName()
        updatedExam.setDurationMinutes(duration);

        // Giữ nguyên trạng thái, người tạo, ngày tạo từ đề thi gốc
        updatedExam.setStatus(originalExam.getStatus());
        updatedExam.setCreationDate(originalExam.getCreationDate());
        updatedExam.setCreatedByUserId(originalExam.getCreatedByUserId());
        updatedExam.setCreatedByUsername(originalExam.getCreatedByUsername());

        List<Integer> questionIds = currentSelectedQuestions.stream()
                .map(QuestionDTO::getQuestionId) // Sửa: ::getId -> ::getQuestionId
                .collect(Collectors.toList());
        updatedExam.setQuestionIds(questionIds);
        

        // Gọi service để cập nhật đề thi
        ResponseMessage response = examService.updateExam(updatedExam);

        if (response != null && Constants.RESPONSE_STATUS_SUCCESS.equals(response.getStatus())) {
            JOptionPane.showMessageDialog(this, "Cập nhật đề thi thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.showTeacherExamListAndRefresh(); // Quay lại và làm mới màn hình danh sách
        } else {
            String errorMessage = (response != null && response.getMessage() != null) ? response.getMessage() : "Cập nhật đề thi thất bại. Lỗi không xác định.";
            JOptionPane.showMessageDialog(this, errorMessage, "Lỗi", JOptionPane.ERROR_MESSAGE);
            LOGGER.log(Level.WARNING, "Cập nhật đề thi ID " + originalExam.getId() + " thất bại: " + errorMessage);
        }
    }
}