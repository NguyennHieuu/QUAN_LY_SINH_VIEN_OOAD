package com.nhom10.ooad.quanlysinhvien.View;

import com.nhom10.ooad.quanlysinhvien.Controller.XacThucController;
import java.awt.*;
import javax.swing.*;

public class GiaoDienDangNhap extends JFrame {

    private final XacThucController xacThucController = new XacThucController();

    private final JTextField txtUsername = new JTextField(15);
    private final JPasswordField txtPassword = new JPasswordField(15);
    private final JButton btnDangNhap = new JButton("Đăng nhập");
    private final JButton btnThoat = new JButton("Thoát");
    private final JLabel lblThongBao = new JLabel(" ", SwingConstants.CENTER);

    public GiaoDienDangNhap() {
        setTitle("ĐĂNG NHẬP HỆ THỐNG");
        setSize(420, 280);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Quy chuẩn Font chữ hệ thống
        Font fontLabel = new Font("Segoe UI", Font.PLAIN, 14);
        Font fontInput = new Font("Segoe UI", Font.PLAIN, 14);
        Font fontButton = new Font("Segoe UI", Font.BOLD, 14);

        // --- Bố cục 3 vùng tiêu chuẩn ---
        JPanel panelMain = new JPanel(new BorderLayout(10, 10));
        panelMain.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // 1. Vùng Trên: Tiêu đề
        JLabel lblTitle = new JLabel("ĐĂNG NHẬP", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(25, 118, 210)); // Màu xanh chủ đạo
        panelMain.add(lblTitle, BorderLayout.NORTH);

        // 2. Vùng Giữa: Form nhập liệu
        JPanel panelForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblUser = new JLabel("Tên đăng nhập:");
        lblUser.setFont(fontLabel);
        txtUsername.setFont(fontInput);
        gbc.gridx = 0; gbc.gridy = 0;
        panelForm.add(lblUser, gbc);
        gbc.gridx = 1;
        panelForm.add(txtUsername, gbc);

        JLabel lblPass = new JLabel("Mật khẩu:");
        lblPass.setFont(fontLabel);
        txtPassword.setFont(fontInput);
        gbc.gridx = 0; gbc.gridy = 1;
        panelForm.add(lblPass, gbc);
        gbc.gridx = 1;
        panelForm.add(txtPassword, gbc);
        panelMain.add(panelForm, BorderLayout.CENTER);

        // 3. Vùng Dưới: Nút bấm & Thông báo trạng thái
        JPanel panelBottom = new JPanel(new GridLayout(2, 1, 5, 5));

        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnDangNhap.setFont(fontButton);
        btnThoat.setFont(fontButton);
        
        // Đồng nhất kích thước nút bấm
        Dimension btnSize = new Dimension(120, 32);
        btnDangNhap.setPreferredSize(btnSize);
        btnThoat.setPreferredSize(btnSize);

        panelButtons.add(btnDangNhap);
        panelButtons.add(btnThoat);

        lblThongBao.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblThongBao.setForeground(Color.RED); // Mặc định chữ màu đỏ báo lỗi

        panelBottom.add(panelButtons);
        panelBottom.add(lblThongBao);
        panelMain.add(panelBottom, BorderLayout.SOUTH);

        add(panelMain);

        // --- Xử lý sự kiện (Event Handling) ---
        btnDangNhap.addActionListener(e -> xuLyDangNhap());
        btnThoat.addActionListener(e -> System.exit(0));

        // Nhấn Enter ở ô mật khẩu cũng kích hoạt đăng nhập
        txtPassword.addActionListener(e -> xuLyDangNhap());
    }

    private void xuLyDangNhap() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        // 🌟 ĐÃ SỬA: Truyền 'this' làm đối số đầu tiên để bàn giao quyền tắt Frame cho Controller điều phối
        String ketQua = xacThucController.dangNhap(this, username, password);

        // Nếu kết quả không phải "OK" (tức là có lỗi validate hoặc sai tài khoản)
        if (!"OK".equals(ketQua)) {
            lblThongBao.setText(ketQua); // Hiển thị lỗi màu đỏ lên nhãn thông báo
        } else {
            lblThongBao.setText(" ");
        }
    }

    public static void main(String[] args) {
        // Cấu hình giao diện theo hệ điều hành cho đẹp
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new GiaoDienDangNhap().setVisible(true));
    }
}