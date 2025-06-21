// src/main/java/org/example/laptrinhmang/client/presentation/TeacherDashboardScreen.java
package org.example.laptrinhmang.client.presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TeacherDashboardScreen extends JPanel {
    private MainApplicationFrame parentFrame;

    public TeacherDashboardScreen(MainApplicationFrame parentFrame) {
        this.parentFrame = parentFrame;
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 30;
        gbc.ipady = 20;

        JLabel welcomeLabel = new JLabel("Chào mừng Giáo viên!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 30));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(welcomeLabel, gbc);

        JButton btnManageExams = new JButton("Quản Lý Đề Thi");
        btnManageExams.setFont(new Font("Arial", Font.BOLD, 18));
        btnManageExams.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentFrame.showPanel(MainApplicationFrame.TEACHER_EXAM_LIST_PANEL);
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(btnManageExams, gbc);

        JButton btnQuestionBank = new JButton("Ngân Hàng Câu Hỏi");
        btnQuestionBank.setFont(new Font("Arial", Font.BOLD, 18));
        btnQuestionBank.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(TeacherDashboardScreen.this, "Chức năng Ngân Hàng Câu Hỏi đang được phát triển.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(btnQuestionBank, gbc);

        JButton btnManageClasses = new JButton("Quản Lý Lớp Học");
        btnManageClasses.setFont(new Font("Arial", Font.BOLD, 18));
        btnManageClasses.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(TeacherDashboardScreen.this, "Chức năng Quản Lý Lớp Học đang được phát triển.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(btnManageClasses, gbc);

        JButton btnViewStats = new JButton("Thống Kê Điểm");
        btnViewStats.setFont(new Font("Arial", Font.BOLD, 18));
        btnViewStats.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(TeacherDashboardScreen.this, "Chức năng Thống Kê Điểm đang được phát triển.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(btnViewStats, gbc);

        JButton btnLogout = new JButton("Đăng Xuất");
        btnLogout.setFont(new Font("Arial", Font.BOLD, 16));
        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentFrame.showPanel(MainApplicationFrame.LOGIN_PANEL);
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(btnLogout, gbc);
    }
}