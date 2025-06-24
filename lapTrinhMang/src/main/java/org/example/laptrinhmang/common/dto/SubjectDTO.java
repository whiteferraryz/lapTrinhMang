// src/main/java/org/example/laptrinhmang/common/dto/SubjectDTO.java
package org.example.laptrinhmang.common.dto;

import java.io.Serializable;

public class SubjectDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int subjectId;       // <-- Đảm bảo trường này tồn tại
    private String subjectName;  // <-- Đảm bảo trường này tồn tại

    public SubjectDTO() {
    }

    public SubjectDTO(int subjectId, String subjectName) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
    }

    // --- Getters và Setters ---

    // <-- Đảm bảo các getter/setter này tồn tại
    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    @Override
    public String toString() {
        return "SubjectDTO{" +
               "subjectId=" + subjectId +
               ", subjectName='" + subjectName + '\'' +
               '}';
    }
}