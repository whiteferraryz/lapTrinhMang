package org.example.laptrinhmang.common.model;

import java.io.Serializable;

public class Subject implements Serializable {
    private static final long serialVersionUID = 1L;

    private int subjectId;
    private String subjectName;
    private String description; // <-- ĐÃ THÊM TRƯỜNG NÀY

    // Constructor mặc định (có thể cần cho deserialization)
    public Subject() {
    }

    // Constructor đã có
    public Subject(int subjectId, String subjectName) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
    }

    // <-- ĐÃ THÊM CONSTRUCTOR NÀY để khớp với SubjectDAO
    public Subject(int subjectId, String subjectName, String description) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.description = description;
    }

    // Getters and Setters
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

    // <-- ĐÃ THÊM GETTER và SETTER cho description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return subjectName; // Hiển thị tên môn học trong JComboBox
    }

    // Quan trọng: Ghi đè equals và hashCode để JComboBox hoạt động đúng khi setSelectedItem
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subject subject = (Subject) o;
        return subjectId == subject.subjectId;
    }

    @Override
    public int hashCode() {
        return subjectId;
    }
}