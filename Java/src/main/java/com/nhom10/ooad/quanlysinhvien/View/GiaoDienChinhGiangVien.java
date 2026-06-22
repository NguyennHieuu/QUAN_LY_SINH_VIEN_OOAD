package com.nhom10.ooad.quanlysinhvien.View;

import com.nhom10.ooad.quanlysinhvien.Controller.XacThucController; // Import để xóa session khi đăng xuất
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class GiaoDienChinhGiangVien extends JFrame {

    // Khai báo giao diện quản lý điểm thành phần
    private final GiaoDienQuanLyDiem giaoDienQuanLyDiem;
    
    // Khai báo các JButton toàn cục trong Class theo quy chuẩn thiết kế của nhóm
    private final JButton btnDoiMatKhau = new JButton("🔑 Đổi mật khẩu"); // 🌟 THÊM MỚI
    private final JButton btnDangXuat = new JButton("Đăng xuất");
    private String maGVLogined = ""; // Biến lưu trữ Mã giảng viên phiên đăng nhập hiện tại

    public GiaoDienChinhGiangVien() {
        // 1. Cấu hình các thuộc tính nền tảng cho Khung chính của Giảng viên
        setTitle("HỆ THỐNG QUẢN LÝ ĐIỂM SỐ VÀ LỚP HỌC - GIẢNG VIÊN");
        setSize(1250, 750); // Kích thước rộng rãi cho JTable hiển thị điểm số
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Đóng khung là dừng chương trình
        setLayout(new BorderLayout(10, 10));

        // Lấy tên đăng nhập của tài khoản giảng viên hiện tại làm điều kiện định danh
        if (XacThucController.getTaiKhoanHienTai() != null) {
            maGVLogined = XacThucController.getTaiKhoanHienTai().getTenDangNhap();
        }

        // 2. NHÚNG PANEL QUẢN LÝ ĐIỂM VÀO VÙNG TRUNG TÂM (CENTER)
        giaoDienQuanLyDiem = new GiaoDienQuanLyDiem();
        add(giaoDienQuanLyDiem, BorderLayout.CENTER);

        // ====================================================================
        // TẠO THANH ĐÁY CHỨA NÚT HÀNH ĐỘNG HỆ THỐNG (SOUTH)
        // ====================================================================
        JPanel panelBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        panelBottom.setBackground(Color.WHITE); // 🌟 ĐỔI THÀNH NỀN TRẮNG: Giúp font nền nút bấm nổi bật, không bị mờ
        panelBottom.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        // 🌟 ĐỊNH DẠNG NÚT 🔑 ĐỔI MẬT KHẨU MỚI THÊM (Sáng rõ, chuẩn font)
        btnDoiMatKhau.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDoiMatKhau.setPreferredSize(new Dimension(150, 32));
        btnDoiMatKhau.setBackground(new Color(45, 125, 50)); // Màu xanh lá cây bảo mật
        btnDoiMatKhau.setForeground(Color.WHITE);
        btnDoiMatKhau.setOpaque(true);               // Ép hiển thị màu gốc rực rỡ
        btnDoiMatKhau.setBorderPainted(false);       // Xóa viền mờ mặc định của Swing
        btnDoiMatKhau.setEnabled(true);              // Đảm bảo luôn sáng để kích hoạt

        // Định dạng nút Đăng xuất chuẩn Segoe UI, Bold (Sửa lỗi mờ nền)
        btnDangXuat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDangXuat.setPreferredSize(new Dimension(120, 32));
        btnDangXuat.setBackground(new Color(211, 47, 47)); // Màu đỏ cam cảnh báo thoát
        btnDangXuat.setForeground(Color.WHITE); // Chữ trắng nổi bật
        btnDangXuat.setOpaque(true);               // Ép hiển thị màu gốc rực rỡ
        btnDangXuat.setBorderPainted(false);       // Xóa viền mờ mặc định của Swing
        btnDangXuat.setEnabled(true);              // Đảm bảo luôn sáng để kích hoạt
        
        // Đẩy đồng bộ cả 2 nút vào góc phải phía dưới màn hình giảng viên
        panelBottom.add(btnDoiMatKhau);
        panelBottom.add(btnDangXuat);
        add(panelBottom, BorderLayout.SOUTH); // Đặt cố định dưới đáy màn hình chính

        // ====================================================================
        // ĐĂNG KÝ SỰ KIỆN LOGIC HÀNH ĐỘNG (ACTION LISTENERS)
        // ====================================================================
        btnDoiMatKhau.addActionListener(e -> xuLyDoiMatKhauGiangVien()); // 🌟 ĐĂNG KÝ SỰ KIỆN ĐỔI MK
        btnDangXuat.addActionListener(e -> xuLyDangXuatGiangVien());
    }

    /**
     * 🌟 NGHIỆP VỤ MỚI: Xử lý kịch bản Đổi mật khẩu tài khoản cá nhân của Giảng viên
     */
    private void xuLyDoiMatKhauGiangVien() {
        JPasswordField txtMatKhauCu = new JPasswordField();
        JPasswordField txtMatKhauMoi = new JPasswordField();
        JPasswordField txtXacNhanMoi = new JPasswordField();

        Object[] message = {
            "Nhập mật khẩu hiện tại:", txtMatKhauCu,
            "Nhập mật khẩu mới mới:", txtMatKhauMoi,
            "Xác nhận lại mật khẩu mới:", txtXacNhanMoi
        };

        // Hộp thoại form gộp tiện ích - sử dụng QUESTION_MESSAGE để fix lỗi biên dịch
        int option = JOptionPane.showConfirmDialog(this, message, 
                "Hệ thống bảo mật tài khoản Giảng viên", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (option == JOptionPane.OK_OPTION) {
            String passCu = new String(txtMatKhauCu.getPassword()).trim();
            String passMoi = new String(txtMatKhauMoi.getPassword()).trim();
            String confMoi = new String(txtXacNhanMoi.getPassword()).trim();

            // Kiểm tra dữ liệu đầu vào trống
            if (passCu.isEmpty() || passMoi.isEmpty() || confMoi.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lỗi: Vui lòng nhập đầy đủ các trường thông tin mật khẩu!", "Cảnh báo bảo mật", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Kiểm tra khớp mật khẩu xác nhận
            if (!passMoi.equals(confMoi)) {
                JOptionPane.showMessageDialog(this, "Lỗi: Mật khẩu mới và mật khẩu xác nhận không trùng khớp nhau!", "Xung đột dữ liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Ghi nhận trực tiếp dữ liệu thay đổi xuống bảng TaiKhoan thông qua mã giảng viên động
            String sql = "UPDATE TaiKhoan SET MatKhau = ? WHERE TenDangNhap = ? AND MatKhau = ?";
            try (Connection conn = com.nhom10.ooad.quanlysinhvien.DataBase.DataBaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setString(1, passMoi);
                ps.setString(2, maGVLogined);
                ps.setString(3, passCu);
                
                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(this, "Chúc mừng! Đã đổi mật khẩu tài khoản Giảng viên thành công vĩnh viễn.", "Thông báo hệ thống", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi xác thực: Mật khẩu hiện tại bạn nhập không đúng!", "Từ chối thực thi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi kết nối: Không thể thực thi cập nhật dữ liệu bảo mật trên SQL Server!", "Hệ thống bận", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Logic xử lý kịch bản Đăng xuất tác nhân Giảng viên: Xóa phiên, đóng khung chính, quay về Đăng nhập
     */
    private void xuLyDangXuatGiangVien() {
        int choose = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn đăng xuất khỏi phân hệ Giảng viên không?", 
                "Xác nhận đăng xuất", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE);
                
        if (choose == JOptionPane.YES_OPTION) {
            XacThucController.dangXuat();
            this.dispose();
            
            GiaoDienDangNhap frameLogin = new GiaoDienDangNhap();
            frameLogin.setVisible(true);
            
            System.out.println("-> Hệ thống: Giảng viên đã đăng xuất thành công.");
        }
    }
}