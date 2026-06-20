/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nhom10.ooad.quanlysinhvien.View;

/**
 *
 * @author ADMIN
 */
import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblMessage;

    public LoginView() {
        initComponent();
    }

    private void initComponent() {
        setTitle("Đăng nhập hệ thống - Nhóm 10");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1, 10, 10));

        Font labelFont = new Font("Tahoma", Font.PLAIN, 14);

        JPanel p1 = new JPanel(new FlowLayout());
        p1.add(new JLabel("Tên đăng nhập:"));
        txtUsername = new JTextField(15);
        p1.add(txtUsername);

        JPanel p2 = new JPanel(new FlowLayout());
        p2.add(new JLabel("Mật khẩu:     "));
        txtPassword = new JPasswordField(15);
        p2.add(txtPassword);

        JPanel p3 = new JPanel(new FlowLayout());
        btnLogin = new JButton("Đăng nhập");
        btnLogin.setPreferredSize(new Dimension(120, 30));
        p3.add(btnLogin);

        lblMessage = new JLabel("", SwingConstants.CENTER);
        lblMessage.setFont(labelFont);
        lblMessage.setForeground(Color.RED); // Màu lỗi theo tiêu chuẩn [7]

        add(p1); add(p2); add(p3); add(lblMessage);
    }

    // Phương thức để Controller lấy dữ liệu và lắng nghe sự kiện
    public String getUsername() { return txtUsername.getText(); }
    public String getPassword() { return new String(txtPassword.getPassword()); }
    public JButton getBtnLogin() { return btnLogin; }
    public void showErrorMessage(String msg) { lblMessage.setText(msg); }
}