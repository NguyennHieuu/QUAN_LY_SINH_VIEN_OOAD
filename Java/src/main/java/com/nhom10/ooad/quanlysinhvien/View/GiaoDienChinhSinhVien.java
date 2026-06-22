package com.nhom10.ooad.quanlysinhvien.View;

import com.nhom10.ooad.quanlysinhvien.Controller.XacThucController; // Import để xóa session
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class GiaoDienChinhSinhVien extends JFrame {

    private GiaoDienDangKyHoc giaoDienDangKyHoc;
    private GiaoDienKetQuaHocTap giaoDienKetQuaHocTap;
    
    // Khai báo các JButton toàn cục trong Class
    private final JButton btnDoiMatKhau = new JButton("🔑 Đổi mật khẩu"); 
    private final JButton btnDangXuat = new JButton("Đăng xuất");
    private String mssvLogined = ""; // Biến lưu trữ MSSV phiên đăng nhập hiện tại

    public GiaoDienChinhSinhVien() {
        setTitle("HỆ THỐNG QUẢN LÝ HỌC VỤ VÀ HỌC PHÍ - NHÓM 10");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Khởi tạo các Tab chứa giao diện chức năng
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Lấy tên đăng nhập của tài khoản hiện tại làm MSSV để truyền vào giao diện
        if (XacThucController.getTaiKhoanHienTai() != null) {
            mssvLogined = XacThucController.getTaiKhoanHienTai().getTenDangNhap();
        }
        
        // Truyền tham số MSSV vào đúng giao diện đăng ký để bộ lọc SQL hoạt động cá nhân hóa
        giaoDienDangKyHoc = new GiaoDienDangKyHoc(mssvLogined);
        giaoDienKetQuaHocTap = new GiaoDienKetQuaHocTap(mssvLogined);
        
        tabbedPane.addTab("Đăng ký tín chỉ", giaoDienDangKyHoc);
        tabbedPane.addTab("Kết quả học tập", giaoDienKetQuaHocTap);
        
        // Đưa thanh Tab vào vùng giữa (CENTER)
        add(tabbedPane, BorderLayout.CENTER);

        // ====================================================================
        // TẠO VÙNG CHỨA CÁC NÚT ĐIỀU KHIỂN HỆ THỐNG (Vùng SOUTH dưới cùng màn hình)
        // ====================================================================
        JPanel panelBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        panelBottom.setBackground(Color.WHITE); // 🌟 ĐỀ NỀN TRẮNG: Tránh việc layout tổng làm mờ nút
        
        // Định dạng nút 🔑 Đổi mật khẩu
        btnDoiMatKhau.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDoiMatKhau.setPreferredSize(new Dimension(150, 32));
        btnDoiMatKhau.setBackground(new Color(45, 125, 50)); // Màu xanh lá cây bảo mật
        btnDoiMatKhau.setForeground(Color.WHITE);
        btnDoiMatKhau.setOpaque(true);               // 🌟 THÊM: Ép hiển thị màu gốc rực rỡ
        btnDoiMatKhau.setBorderPainted(false);       // 🌟 THÊM: Xóa viền mờ mặc định
        btnDoiMatKhau.setEnabled(true);              // 🌟 THÊM: Đảm bảo luôn sáng để kích hoạt

        // Định dạng nút Đăng xuất theo quy chuẩn của nhóm
        btnDangXuat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDangXuat.setPreferredSize(new Dimension(120, 32));
        btnDangXuat.setBackground(new Color(211, 47, 47)); // Đỏ cam
        btnDangXuat.setForeground(Color.WHITE); 
        btnDangXuat.setOpaque(true);               // 🌟 THÊM: Ép hiển thị màu gốc rực rỡ
        btnDangXuat.setBorderPainted(false);       // 🌟 THÊM: Xóa viền mờ mặc định
        btnDangXuat.setEnabled(true);              // 🌟 THÊM: Đảm bảo luôn sáng để kích hoạt
        
        // Đẩy đồng bộ cả 2 nút vào góc phải phía dưới màn hình
        panelBottom.add(btnDoiMatKhau);
        panelBottom.add(btnDangXuat);
        add(panelBottom, BorderLayout.SOUTH); 

        // ====================================================================
        // ĐĂNG KÝ SỰ KIỆN LIÊN KẾT HÀNH ĐỘNG
        // ====================================================================
        btnDoiMatKhau.addActionListener(e -> xuLyDoiMatKhauSinhVien()); 
        btnDangXuat.addActionListener(e -> xuLyDangXuat());
    }

    /**
     * 🌟 NGHIỆP VỤ MỚI: Xử lý kịch bản Đổi mật khẩu tài khoản cá nhân của Sinh viên
     */
    private void xuLyDoiMatKhauSinhVien() {
        // Tạo các ô nhập mật khẩu bảo mật ẩn ký tự
        JPasswordField txtMatKhauCu = new JPasswordField();
        JPasswordField txtMatKhauMoi = new JPasswordField();
        JPasswordField txtXacNhanMoi = new JPasswordField();

        Object[] message = {
            "Nhập mật khẩu hiện tại:", txtMatKhauCu,
            "Nhập mật khẩu mới mới:", txtMatKhauMoi,
            "Xác nhận lại mật khẩu mới:", txtXacNhanMoi
        };

        // Thay thế PASSWORD_MESSAGE thành QUESTION_MESSAGE để fix lỗi biên dịch
        int option = JOptionPane.showConfirmDialog(this, message, 
                "Hệ thống bảo mật tài khoản Sinh viên", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (option == JOptionPane.OK_OPTION) {
            String passCu = new String(txtMatKhauCu.getPassword()).trim();
            String passMoi = new String(txtMatKhauMoi.getPassword()).trim();
            String confMoi = new String(txtXacNhanMoi.getPassword()).trim();

            // Validate dữ liệu trống cục bộ
            if (passCu.isEmpty() || passMoi.isEmpty() || confMoi.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lỗi: Vui lòng nhập đầy đủ các trường thông tin mật khẩu!", "Cảnh báo bảo mật", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Kiểm tra khớp mật khẩu mới gõ hai lần
            if (!passMoi.equals(confMoi)) {
                JOptionPane.showMessageDialog(this, "Lỗi: Mật khẩu mới và mật khẩu xác nhận không trùng khớp nhau!", "Xung đột dữ liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Bắn câu lệnh SQL vật lý cập nhật mật khẩu trực tiếp dựa trên biến mssvLogined động
            String sql = "UPDATE TaiKhoan SET MatKhau = ? WHERE TenDangNhap = ? AND MatKhau = ?";
            try (Connection conn = com.nhom10.ooad.quanlysinhvien.DataBase.DataBaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setString(1, passMoi);
                ps.setString(2, mssvLogined);
                ps.setString(3, passCu);
                
                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(this, "Chúc mừng! Đã đổi mật khẩu tài khoản cá nhân thành công vĩnh viễn.", "Thông báo hệ thống", JOptionPane.INFORMATION_MESSAGE);
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
     * Logic xử lý kịch bản Đăng xuất: Xóa phục hồi session, đóng màn hình chính, mở màn hình đăng nhập
     */
    private void xuLyDangXuat() {
        int choose = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn đăng xuất khỏi hệ thống không?", 
                "Xác nhận đăng xuất", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE);
                
        if (choose == JOptionPane.YES_OPTION) {
            XacThucController.dangXuat();
            this.dispose();
            
            GiaoDienDangNhap frameLogin = new GiaoDienDangNhap();
            frameLogin.setVisible(true);
            
            System.out.println("-> Hệ thống: Sinh viên đã đăng xuất thành công.");
        }
    }
}