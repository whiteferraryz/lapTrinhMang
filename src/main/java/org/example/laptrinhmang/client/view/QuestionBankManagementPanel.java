package org.example.laptrinhmang.client.view;

import org.example.laptrinhmang.common.dto.QuestionDTO;
import org.example.laptrinhmang.common.dto.QuestionListResponseDTO;
import org.example.laptrinhmang.common.dto.QuestionOperationRequestDTO;
import org.example.laptrinhmang.common.dto.SubjectDTO;
import org.example.laptrinhmang.common.dto.SubjectListResponseDTO;
import org.example.laptrinhmang.common.model.Subject;
import org.example.laptrinhmang.common.dto.RequestMessage; // 
import org.example.laptrinhmang.common.dto.ResponseMessage; // 
import org.example.laptrinhmang.common.util.Constants;
import org.example.laptrinhmang.client.network.ServerCommunicationConnector;
import org.example.laptrinhmang.common.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

// Lớp này đã đúng là extends JPanel 
public class QuestionBankManagementPanel extends JPanel { // 

    private static final Logger LOGGER = Logger.getLogger(QuestionBankManagementPanel.class.getName()); // 

    private User currentUser; // 
    private ServerCommunicationConnector serverCommunicationConnector; // 

    // Components
    private JTable questionTable; // 
    private DefaultTableModel tableModel; // 
    private JTextField txtQuestionContent; // 
    private JComboBox<String> cmbQuestionType; // 
    private JTextField txtOptions; // 
    private JTextField txtCorrectAnswer; // 
    private JSpinner spDifficulty; // 
    private JComboBox<Subject> cmbSubject; // 
    private JTextField txtSearch; // 
    private JButton btnAddQuestion; // 
    private JButton btnUpdateQuestion; // 
    private JButton btnDeleteQuestion; // 
    private JButton btnRefresh; // 
    private JButton btnSearch; // 

    private List<QuestionDTO> currentQuestions; // 
    private List<Subject> availableSubjects; // 

    private final ExecutorService executorService = Executors.newSingleThreadExecutor(); // 

    public QuestionBankManagementPanel(User currentUser, ServerCommunicationConnector connector) { // 
        this.currentUser = currentUser; // 
        this.serverCommunicationConnector = connector; // 

        setLayout(new BorderLayout(10, 10)); // 
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 

        // 1. Panel cho Input Form
        JPanel inputPanel = new JPanel(new GridBagLayout()); // 
        inputPanel.setBorder(BorderFactory.createTitledBorder("Thông tin câu hỏi")); // 
        GridBagConstraints gbc = new GridBagConstraints(); // 
        gbc.insets = new Insets(5, 5, 5, 5); // 
        gbc.fill = GridBagConstraints.HORIZONTAL; // 

        int row = 0; // 
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.WEST; // 
        inputPanel.add(new JLabel("Nội dung câu hỏi:"), gbc); // 
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0; // 
        txtQuestionContent = new JTextField(40); // 
        inputPanel.add(txtQuestionContent, gbc); // 

        row++; // 
        gbc.gridx = 0; gbc.gridy = row; // 
        inputPanel.add(new JLabel("Loại câu hỏi:"), gbc); // 
        gbc.gridx = 1; gbc.gridy = row; // 
        cmbQuestionType = new JComboBox<>(new String[]{ // 
                Constants.QUESTION_TYPE_MULTIPLE_CHOICE, // 
                Constants.QUESTION_TYPE_TRUE_FALSE, // 
                Constants.QUESTION_TYPE_SHORT_ANSWER // 
        });
        cmbQuestionType.addActionListener(new ActionListener() { // 
            @Override
            public void actionPerformed(ActionEvent e) { // 
                toggleOptionsVisibility(); // 
            }
        });
        inputPanel.add(cmbQuestionType, gbc); // 

        row++; // 
        gbc.gridx = 0; gbc.gridy = row; // 
        inputPanel.add(new JLabel("Lựa chọn (cách nhau bởi '|'):"), gbc); // 
        gbc.gridx = 1; gbc.gridy = row; // 
        txtOptions = new JTextField(40); // 
        inputPanel.add(txtOptions, gbc); // 

        row++; // 
        gbc.gridx = 0; gbc.gridy = row; // 
        inputPanel.add(new JLabel("Đáp án đúng:"), gbc); // 
        gbc.gridx = 1; gbc.gridy = row; // 
        txtCorrectAnswer = new JTextField(40); // 
        inputPanel.add(txtCorrectAnswer, gbc); // 

        row++; // 
        gbc.gridx = 0; gbc.gridy = row; // 
        inputPanel.add(new JLabel("Độ khó (1-5):"), gbc); // 
        gbc.gridx = 1; gbc.gridy = row; // 
        spDifficulty = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1)); // 
        inputPanel.add(spDifficulty, gbc); // 

        row++; // 
        gbc.gridx = 0; gbc.gridy = row; // 
        inputPanel.add(new JLabel("Môn học:"), gbc); // 
        gbc.gridx = 1; gbc.gridy = row; // 
        cmbSubject = new JComboBox<>(); // 
        inputPanel.add(cmbSubject, gbc); // 

        toggleOptionsVisibility(); // 

        add(inputPanel, BorderLayout.NORTH); // 

        // 2. Panel cho Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5)); // 
        btnAddQuestion = new JButton("Thêm"); // 
        btnUpdateQuestion = new JButton("Cập nhật"); // 
        btnDeleteQuestion = new JButton("Xóa"); // 
        btnRefresh = new JButton("Làm mới"); // 
        btnSearch = new JButton("Tìm kiếm"); // 
        txtSearch = new JTextField(20); // 

        buttonPanel.add(btnAddQuestion); // 
        buttonPanel.add(btnUpdateQuestion); // 
        buttonPanel.add(btnDeleteQuestion); // 
        buttonPanel.add(new JLabel("Tìm kiếm:")); // 
        buttonPanel.add(txtSearch); // 
        buttonPanel.add(btnSearch); // 
        buttonPanel.add(btnRefresh); // 

        add(buttonPanel, BorderLayout.SOUTH); // 

        // 3. Table cho danh sách câu hỏi
        String[] columnNames = {"ID", "Nội dung", "Loại", "Lựa chọn", "Đáp án", "Độ khó", "Người tạo", "Môn học"}; // 
        tableModel = new DefaultTableModel(columnNames, 0) { // 
            @Override
            public boolean isCellEditable(int row, int column) { // 
                return false; // 
            }
        };
        questionTable = new JTable(tableModel); // 
        questionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 
        questionTable.getSelectionModel().addListSelectionListener(e -> { // 
            if (!e.getValueIsAdjusting() && questionTable.getSelectedRow() != -1) { // 
                displayQuestionDetails(questionTable.getSelectedRow()); // 
            }
        });
        JScrollPane scrollPane = new JScrollPane(questionTable); // 
        add(scrollPane, BorderLayout.CENTER); // 

        // Load initial data
        loadAllSubjects(); // 
        loadAllQuestions(); // 

        // Add Action Listeners
        btnAddQuestion.addActionListener(this::addQuestion); // 
        btnUpdateQuestion.addActionListener(this::updateQuestion); // 
        btnDeleteQuestion.addActionListener(this::deleteQuestion); // 
        btnRefresh.addActionListener(e -> loadAllQuestions()); // 
        btnSearch.addActionListener(this::searchQuestions); // 
    }

    private void toggleOptionsVisibility() {
        String selectedType = (String) cmbQuestionType.getSelectedItem(); // 
        boolean isMultipleChoice = Constants.QUESTION_TYPE_MULTIPLE_CHOICE.equals(selectedType); // 
        txtOptions.setEnabled(isMultipleChoice); // 
        txtOptions.setEditable(isMultipleChoice); // 
        if (!isMultipleChoice) { // 
            txtOptions.setText(""); // 
        }
    }

    private void loadAllSubjects() {
        executorService.submit(() -> { // 
            try {
                RequestMessage request = new RequestMessage(Constants.REQUEST_TYPE_GET_ALL_SUBJECTS, null); // 
                ResponseMessage response = serverCommunicationConnector.sendRequest(request); // 
                if (response.getStatus().equals(Constants.RESPONSE_STATUS_SUCCESS)) { // 
                    if (response.getData() instanceof SubjectListResponseDTO) { // 
                        SubjectListResponseDTO subjectResponse = (SubjectListResponseDTO) response.getData(); // 
                        List<SubjectDTO> subjectDTOs = subjectResponse.getSubjects(); // 
                        availableSubjects = new ArrayList<>(); // 
                        if (subjectDTOs != null) { // 
                            for (SubjectDTO dto : subjectDTOs) { // 
                                availableSubjects.add(new Subject(dto.getSubjectId(), dto.getSubjectName())); // 
                            }
                        }
                        SwingUtilities.invokeLater(() -> { // 
                            cmbSubject.removeAllItems(); // 
                            if (availableSubjects != null) { // 
                                for (Subject subject : availableSubjects) { // 
                                    cmbSubject.addItem(subject); // 
                                }
                            }
                            LOGGER.info("Đã tải " + (availableSubjects != null ? availableSubjects.size() : 0) + " môn học."); // 
                        });
                    } else {
                        LOGGER.warning("Phản hồi getAllSubjects không đúng định dạng: " + response.getData().getClass().getName()); // 
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Lỗi: Dữ liệu môn học không hợp lệ từ server.", "Lỗi tải môn học", JOptionPane.ERROR_MESSAGE)); // 
                    }
                } else {
                    String errorMessage = response.getMessage() != null ? // 
                            response.getMessage() : "Lỗi không xác định khi tải môn học."; // 
                    SwingUtilities.invokeLater(() -> { // 
                        JOptionPane.showMessageDialog(this, "Lỗi tải danh sách môn học: " + errorMessage, "Lỗi", JOptionPane.ERROR_MESSAGE); // 
                        LOGGER.warning("Lỗi tải môn học: " + errorMessage); // 
                    });
                }
            } catch (Exception e) { // 
                LOGGER.log(Level.SEVERE, "Lỗi khi gửi yêu cầu tải môn học: " + e.getMessage(), e); // 
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Lỗi kết nối server khi tải môn học.", "Lỗi", JOptionPane.ERROR_MESSAGE)); // 
            }
        });
    }

    private void loadAllQuestions() {
        tableModel.setRowCount(0); // 
        executorService.submit(() -> { // 
            try {
                RequestMessage request = new RequestMessage(Constants.REQUEST_TYPE_GET_ALL_QUESTIONS, null); // 
                ResponseMessage response = serverCommunicationConnector.sendRequest(request); // 
                if (response.getStatus().equals(Constants.RESPONSE_STATUS_SUCCESS)) { // 
                    if (response.getData() instanceof QuestionListResponseDTO) { // 
                        QuestionListResponseDTO questionResponse = (QuestionListResponseDTO) response.getData(); // 
                        currentQuestions = questionResponse.getQuestions(); // 
                        SwingUtilities.invokeLater(() -> { // 
                            tableModel.setRowCount(0); // 
                            if (currentQuestions != null) { // 
                                for (QuestionDTO q : currentQuestions) { // 
                                    addRowToTable(q);
                                }
                            }
                            LOGGER.info("Đã tải " + (currentQuestions != null ? currentQuestions.size() : 0) + " câu hỏi."); // 
                        });
                    } else {
                        LOGGER.warning("Phản hồi getAllQuestions không đúng định dạng: " + // 
                                response.getData().getClass().getName()); // 
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Lỗi: Dữ liệu câu hỏi không hợp lệ từ server.", "Lỗi tải câu hỏi", JOptionPane.ERROR_MESSAGE)); // 
                    }
                } else {
                    String errorMessage = response.getMessage() != null ? // 
                            response.getMessage() : "Lỗi không xác định khi tải câu hỏi."; // 
                    SwingUtilities.invokeLater(() -> { // 
                        JOptionPane.showMessageDialog(this, "Lỗi tải danh sách câu hỏi: " + errorMessage, "Lỗi", JOptionPane.ERROR_MESSAGE); // 
                        LOGGER.warning("Lỗi tải câu hỏi: " + errorMessage); // 
                    });
                }
            } catch (Exception e) { // 
                LOGGER.log(Level.SEVERE, "Lỗi khi gửi yêu cầu tải câu hỏi: " + e.getMessage(), e); // 
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Lỗi kết nối server khi tải câu hỏi.", "Lỗi", JOptionPane.ERROR_MESSAGE)); // 
            }
        });
    }

    private void searchQuestions(ActionEvent e) {
        String searchTerm = txtSearch.getText().trim(); // 
        if (searchTerm.isEmpty()) { // 
            loadAllQuestions(); // 
            return;
        }
        tableModel.setRowCount(0); // 
        executorService.submit(() -> { // 
            try {
                RequestMessage request = new RequestMessage(Constants.REQUEST_TYPE_SEARCH_QUESTIONS, searchTerm); // 
                ResponseMessage response = serverCommunicationConnector.sendRequest(request); // 
                if (response.getStatus().equals(Constants.RESPONSE_STATUS_SUCCESS)) { // 
                    if (response.getData() instanceof QuestionListResponseDTO) { // 
                        QuestionListResponseDTO searchResponse = (QuestionListResponseDTO) response.getData(); // 
                        currentQuestions = searchResponse.getQuestions(); // 
                        SwingUtilities.invokeLater(() -> { // 
                            tableModel.setRowCount(0); // 
                            if (currentQuestions != null) { // 
                                for (QuestionDTO q : currentQuestions) { // 
                                    addRowToTable(q);
                                }
                            }
                            LOGGER.info("Đã tìm thấy " + (currentQuestions != null ? currentQuestions.size() : 0) + " câu hỏi với từ khóa '" + searchTerm + "'."); // 
                        });
                    } else {
                        LOGGER.warning("Phản hồi searchQuestions không đúng định dạng: " + // 
                                response.getData().getClass().getName()); // 
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Lỗi: Dữ liệu tìm kiếm câu hỏi không hợp lệ từ server.", "Lỗi tìm kiếm câu hỏi", JOptionPane.ERROR_MESSAGE));
                    }
                } else {
                    String errorMessage = response.getMessage() != null ?
                            response.getMessage() : "Lỗi không xác định khi tìm kiếm câu hỏi.";
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm danh sách câu hỏi: " + errorMessage, "Lỗi", JOptionPane.ERROR_MESSAGE);
                        LOGGER.warning("Lỗi tìm kiếm câu hỏi: " + errorMessage);
                    });
                }
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Lỗi khi gửi yêu cầu tìm kiếm câu hỏi: " + ex.getMessage(), e);
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Lỗi kết nối server khi tìm kiếm câu hỏi.", "Lỗi", JOptionPane.ERROR_MESSAGE));
            }
        });
    }

    private void displayQuestionDetails(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < currentQuestions.size()) {
            QuestionDTO question = currentQuestions.get(rowIndex);
            txtQuestionContent.setText(question.getContent());
            cmbQuestionType.setSelectedItem(question.getQuestionType());

            // Xử lý options: hiển thị dưới dạng chuỗi nối
            if (question.getOptions() != null) {
                txtOptions.setText(String.join("|", question.getOptions()));
            } else {
                txtOptions.setText("");
            }

            txtCorrectAnswer.setText(question.getCorrectAnswer());
            spDifficulty.setValue(question.getDifficultyLevel());

            // Chọn môn học trong combobox
            if (availableSubjects != null && !availableSubjects.isEmpty()) {
                for (Subject sub : availableSubjects) {
                    if (sub.getSubjectId() == question.getSubjectId()) {
                        cmbSubject.setSelectedItem(sub);
                        break;
                    }
                }
            }
            toggleOptionsVisibility(); // Cập nhật trạng thái hiển thị của txtOptions
        }
    }

    private void addQuestion(ActionEvent e) {
        String content = txtQuestionContent.getText().trim();
        String type = (String) cmbQuestionType.getSelectedItem();
        String optionsStr = txtOptions.getText().trim();
        String correctAnswer = txtCorrectAnswer.getText().trim();
        int difficulty = (int) spDifficulty.getValue();
        Subject selectedSubject = (Subject) cmbSubject.getSelectedItem();

        if (content.isEmpty() || correctAnswer.isEmpty() || selectedSubject == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ nội dung, đáp án và chọn môn học.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<String> optionsList = new ArrayList<>();
        if (Constants.QUESTION_TYPE_MULTIPLE_CHOICE.equals(type)) {
            if (optionsStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Câu hỏi trắc nghiệm phải có các lựa chọn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            optionsList = Arrays.asList(optionsStr.split("\\|")).stream()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            if (optionsList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Các lựa chọn không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!optionsList.contains(correctAnswer)) {
                JOptionPane.showMessageDialog(this, "Đáp án đúng phải nằm trong các lựa chọn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else if (Constants.QUESTION_TYPE_TRUE_FALSE.equals(type)) {
            if (!("True".equalsIgnoreCase(correctAnswer) || "False".equalsIgnoreCase(correctAnswer))) {
                JOptionPane.showMessageDialog(this, "Đáp án đúng cho câu hỏi True/False phải là 'True' hoặc 'False'.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        QuestionDTO newQuestion = new QuestionDTO(
                0, // ID sẽ được server tạo
                content,
                type,
                optionsList,
                correctAnswer,
                difficulty,
                currentUser.getUserId(),
                currentUser.getUsername(),
                selectedSubject.getSubjectId(),
                selectedSubject.getSubjectName(),
                null // Creation date sẽ được server tạo
        );

        executorService.submit(() -> {
            try {
                RequestMessage request = new RequestMessage(Constants.REQUEST_TYPE_ADD_QUESTION, newQuestion);
                ResponseMessage response = serverCommunicationConnector.sendRequest(request);

                SwingUtilities.invokeLater(() -> {
                    if (response.getStatus().equals(Constants.RESPONSE_STATUS_SUCCESS)) {
                        JOptionPane.showMessageDialog(this, response.getMessage(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        clearForm();
                        loadAllQuestions();
                    } else {
                        JOptionPane.showMessageDialog(this, "Lỗi thêm câu hỏi: " + response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                });
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Lỗi mạng hoặc dữ liệu khi thêm câu hỏi.", ex);
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Lỗi kết nối hoặc dữ liệu khi thêm câu hỏi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE));
            }
        });
    }

    private void updateQuestion(ActionEvent e) {
        int selectedRow = questionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một câu hỏi để cập nhật.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        QuestionDTO originalQuestion = currentQuestions.get(selectedRow);

        String content = txtQuestionContent.getText().trim();
        String type = (String) cmbQuestionType.getSelectedItem();
        String optionsStr = txtOptions.getText().trim();
        String correctAnswer = txtCorrectAnswer.getText().trim();
        int difficulty = (int) spDifficulty.getValue();
        Subject selectedSubject = (Subject) cmbSubject.getSelectedItem();

        if (content.isEmpty() || correctAnswer.isEmpty() || selectedSubject == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ nội dung, đáp án và chọn môn học.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<String> optionsList = new ArrayList<>();
        if (Constants.QUESTION_TYPE_MULTIPLE_CHOICE.equals(type)) {
            if (optionsStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Câu hỏi trắc nghiệm phải có các lựa chọn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            optionsList = Arrays.asList(optionsStr.split("\\|")).stream()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            if (optionsList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Các lựa chọn không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!optionsList.contains(correctAnswer)) {
                JOptionPane.showMessageDialog(this, "Đáp án đúng phải nằm trong các lựa chọn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else if (Constants.QUESTION_TYPE_TRUE_FALSE.equals(type)) {
            if (!("True".equalsIgnoreCase(correctAnswer) || "False".equalsIgnoreCase(correctAnswer))) {
                JOptionPane.showMessageDialog(this, "Đáp án đúng cho câu hỏi True/False phải là 'True' hoặc 'False'.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }


        QuestionDTO updatedQuestion = new QuestionDTO(
                originalQuestion.getQuestionId(),
                content,
                type,
                optionsList,
                correctAnswer,
                difficulty,
                originalQuestion.getCreatorUserId(), // Giữ nguyên người tạo
                originalQuestion.getCreatorUsername(),
                selectedSubject.getSubjectId(),
                selectedSubject.getSubjectName(),
                originalQuestion.getCreationDate() // Giữ nguyên ngày tạo
        );

        executorService.submit(() -> {
            try {
                RequestMessage request = new RequestMessage(Constants.REQUEST_TYPE_UPDATE_QUESTION, updatedQuestion);
                ResponseMessage response = serverCommunicationConnector.sendRequest(request);

                SwingUtilities.invokeLater(() -> {
                    if (response.getStatus().equals(Constants.RESPONSE_STATUS_SUCCESS)) {
                        JOptionPane.showMessageDialog(this, response.getMessage(), "Cập nhật thành công", JOptionPane.INFORMATION_MESSAGE);
                        clearForm();
                        loadAllQuestions();
                    } else {
                        JOptionPane.showMessageDialog(this, "Lỗi cập nhật câu hỏi: " + response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                });
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Lỗi mạng hoặc dữ liệu khi cập nhật câu hỏi.", ex);
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Lỗi kết nối hoặc dữ liệu khi cập nhật câu hỏi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE));
            }
        });
    }

    private void deleteQuestion(ActionEvent e) {
        int selectedRow = questionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một câu hỏi để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        QuestionDTO questionToDelete = currentQuestions.get(selectedRow);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa câu hỏi:\n\"" + questionToDelete.getContent() + "\"",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            QuestionOperationRequestDTO requestDTO = new QuestionOperationRequestDTO(questionToDelete.getQuestionId());

            executorService.submit(() -> {
                try {
                    RequestMessage request = new RequestMessage(Constants.REQUEST_TYPE_DELETE_QUESTION, requestDTO);
                    ResponseMessage response = serverCommunicationConnector.sendRequest(request);

                    SwingUtilities.invokeLater(() -> {
                        if (response.getStatus().equals(Constants.RESPONSE_STATUS_SUCCESS)) {
                            JOptionPane.showMessageDialog(this, response.getMessage(), "Xóa thành công", JOptionPane.INFORMATION_MESSAGE);
                            clearForm();
                            loadAllQuestions();
                        } else {
                            JOptionPane.showMessageDialog(this, "Lỗi xóa câu hỏi: " + response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Lỗi mạng hoặc dữ liệu khi xóa câu hỏi.", ex);
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Lỗi kết nối hoặc dữ liệu khi xóa câu hỏi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE));
                }
            });
        }
    }

    private void addRowToTable(QuestionDTO q) {
        Vector<Object> row = new Vector<>();
        row.add(q.getQuestionId());
        row.add(q.getContent());
        row.add(q.getQuestionType());
        row.add(q.getOptions() != null ? String.join(", ", q.getOptions()) : "");
        row.add(q.getCorrectAnswer());
        row.add(q.getDifficultyLevel());
        row.add(q.getCreatorUsername());
        row.add(q.getSubjectName());
        tableModel.addRow(row);
    }

    private void clearForm() {
        txtQuestionContent.setText("");
        cmbQuestionType.setSelectedItem(Constants.QUESTION_TYPE_MULTIPLE_CHOICE);
        txtOptions.setText("");
        txtCorrectAnswer.setText("");
        spDifficulty.setValue(1);
        if (cmbSubject.getItemCount() > 0) {
            cmbSubject.setSelectedIndex(0);
        } else {
            cmbSubject.setSelectedIndex(-1);
        }
        questionTable.clearSelection();
        toggleOptionsVisibility();
    }

    public void closeExecutor() {
        executorService.shutdown();
        LOGGER.info("ExecutorService trong QuestionBankManagementPanel đã tắt.");
    }
}