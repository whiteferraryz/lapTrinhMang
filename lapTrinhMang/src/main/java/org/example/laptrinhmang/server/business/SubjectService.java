// src/main/java/org/example/laptrinhmang/server/business/SubjectService.java
package org.example.laptrinhmang.server.business;


import org.example.laptrinhmang.common.dto.SubjectDTO;
import org.example.laptrinhmang.common.dto.SubjectListResponseDTO;
import org.example.laptrinhmang.common.util.Constants;
import org.example.laptrinhmang.server.dataaccess.SubjectDAO;
import org.example.laptrinhmang.common.model.Subject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SubjectService {
    private static final Logger LOGGER = Logger.getLogger(SubjectService.class.getName());
    private SubjectDAO subjectDAO;

    public SubjectService(SubjectDAO subjectDAO) {
        this.subjectDAO = subjectDAO;
    }

    public SubjectListResponseDTO getAllSubjects() {
        try {
            List<Subject> subjectsFromDao = subjectDAO.getAllSubjects();
            List<SubjectDTO> subjectDTOs = new ArrayList<>();

            for (Subject subject : subjectsFromDao) {
                SubjectDTO dto = new SubjectDTO();
                dto.setSubjectId(subject.getSubjectId());
                dto.setSubjectName(subject.getSubjectName());
                subjectDTOs.add(dto);
            }

            LOGGER.info("getAllSubjects: Đã lấy thành công " + subjectDTOs.size() + " môn học.");
            return new SubjectListResponseDTO(Constants.RESPONSE_STATUS_SUCCESS, "Lấy danh sách môn học thành công.", subjectDTOs);
        }
        // Thay đổi từ SQLException sang Exception
        catch (Exception e) { // <-- Thay đổi ở đây
            LOGGER.log(Level.SEVERE, "getAllSubjects: Lỗi khi lấy tất cả môn học: " + e.getMessage(), e);
            return new SubjectListResponseDTO(Constants.RESPONSE_STATUS_ERROR, "Lỗi hệ thống khi lấy danh sách môn học.", null);
    }
    // Thêm các phương thức khác (add, update, delete) nếu cần
}}