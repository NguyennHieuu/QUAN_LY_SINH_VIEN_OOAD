package com.nhom10.ooad.quanlysinhvien.View;

import com.nhom10.ooad.quanlysinhvien.Controller.XacThucController; // Import để xóa session đăng nhập
import javax.swing.*;
import java.awt.*;
import java.sql.*; // 🌟 THÊM IMPORT: Để thực thi câu lệnh SQL đổi mật khẩu vật lý

public class GiaoDienChinhGiaoVu extends JFrame {

    // Khai báo các phân hệ giao diện thành phần (JPanel) của Giáo vụ
    private final GiaoDienQuanLySinhVien giaoDienQuanLySinhVien;
    private final GiaoDienQuanLyGiangVien giaoDienQuanLyGiangVien;
    private final GiaoDienQuanLyLopHoc giaoDienQuanLyLopHoc;
    private final GiaoDienMoLopHocPhan giaoDienMoLopHocPhan; 
    private final GiaoDienQuanLyTaiKhoan giaoDienQuanLyTaiKhoan;
    
    // 🌟 KHAI BÁO THÊM: Nút Đổi mật khẩu toàn cục trong Class
    private final JButton btnDoiMatKhau = new JButton("🔑 Đổi mật khẩu"); 
    private final JButton btnDangXuat = new JButton("Đăng xuất");
    private String userLogined = ""; // Biến lưu trữ tên đăng nhập phiên hiện tại

    // KHỞI TẠO ĐỒNG BỘ FONT SEGOE UI SIZE 14
    private final Font segoeFont = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font segoeBoldFont = new Font("Segoe UI", Font.BOLD, 14);

    public GiaoDienChinhGiaoVu() {
        // 1. Cấu hình các thuộc tính nền tảng cho Cửa sổ chính Giáo vụ
        setTitle("HỆ THỐNG QUẢN LÝ HỒ SƠ VÀ THÔNG TIN GIÁO VỤ - NHÓM 10");
        setSize(1350, 830); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        setLayout(new BorderLayout(10, 10));

        // 2. Khởi tạo thanh chứa Tab điều hướng phẳng hiện đại
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(segoeBoldFont); // 🌟 ĐÃ SỬA: Đặt font chữ đậm 14 cho tiêu đề các Tab

        // Lấy tên đăng nhập của tài khoản Giáo vụ hiện tại từ Session toàn cục
        if (XacThucController.getTaiKhoanHienTai() != null) {
            userLogined = XacThucController.getTaiKhoanHienTai().getTenDangNhap();
        }

        // 3. Khởi tạo độc lập các khối giao diện chức năng quản lý nền tảng
        giaoDienQuanLySinhVien = new GiaoDienQuanLySinhVien();
        giaoDienQuanLyGiangVien = new GiaoDienQuanLyGiangVien();
        giaoDienQuanLyLopHoc = new GiaoDienQuanLyLopHoc();
        giaoDienMoLopHocPhan = new GiaoDienMoLopHocPhan(); 
        giaoDienQuanLyTaiKhoan = new GiaoDienQuanLyTaiKhoan();

        // 4. Nhúng (Add) mượt mà các khối JPanel vào từng thẻ Tab tương ứng trên thanh điều hướng
        tabbedPane.addTab("Quản lý Sinh viên", giaoDienQuanLySinhVien);
        tabbedPane.addTab("Quản lý Giảng viên", giaoDienQuanLyGiangVien);
        tabbedPane.addTab("Quản lý Lớp học", giaoDienQuanLyLopHoc);
        tabbedPane.addTab("Nghiệp vụ Mở lớp & Tra cứu HP", giaoDienMoLopHocPhan); 
        tabbedPane.addTab("Quản lý Tài khoản", giaoDienQuanLyTaiKhoan);

        // Đưa toàn bộ thanh Tab chứa 5 phân hệ vào vùng trung tâm (CENTER) của Khung lớn
        add(tabbedPane, BorderLayout.CENTER);

        // ====================================================================
        // TẠO THANH ĐÁY CHỨA NÚT HÀNH ĐỘNG HỆ THỐNG (SOUTH)
        // ====================================================================
        JPanel panelBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        panelBottom.setBackground(Color.WHITE); // 🌟 ĐỀ NỀN TRẮNG: Đồng bộ chuẩn phom thiết kế Nhóm 10
        
        // 🌟 ĐỊNH DẠNG VẬT LÝ NÚT: Đổi mật khẩu (Đồng bộ 100% với form Sinh viên/Kế toán)
        btnDoiMatKhau.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDoiMatKhau.setPreferredSize(new Dimension(150, 32));
        btnDoiMatKhau.setBackground(new Color(45, 125, 50)); // Màu xanh lá cây bảo mật
        btnDoiMatKhau.setForeground(Color.WHITE);
        btnDoiMatKhau.setOpaque(true);               
        btnDoiMatKhau.setBorderPainted(false);       
        btnDoiMatKhau.setEnabled(true);              

        // 🌟 ĐỊNH DẠNG VẬT LÝ NÚT: Đăng xuất (Đồng bộ 100% với form Sinh viên/Kế toán)
        btnDangXuat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDangXuat.setPreferredSize(new Dimension(120, 32));
        btnDangXuat.setBackground(new Color(211, 47, 47)); // Đỏ cam
        btnDangXuat.setForeground(Color.WHITE); 
        btnDangXuat.setOpaque(true);               
        btnDangXuat.setBorderPainted(false);       
        btnDangXuat.setEnabled(true);              
        
        // Thêm các nút vào thanh đáy góc bên phải
        panelBottom.add(btnDoiMatKhau);
        panelBottom.add(btnDangXuat);
        add(panelBottom, BorderLayout.SOUTH); 

        // ====================================================================
        // ĐĂNG KÝ SỰ KIỆN LOGIC (ACTION LISTENERS)
        // ====================================================================
        btnDoiMatKhau.addActionListener(e -> xuLyDoiMatKhauGiaoVu());
        btnDangXuat.addActionListener(e -> xuLyDangXuatGiaoVu());
    }

    /**
     * 🌟 THÊM MỚI ĐỒNG BỘ: Xử lý đổi mật khẩu kết nối Database trực tiếp dựa trên phiên userLogined hiện hành.
     */
    private void xuLyDoiMatKhauGiaoVu() {
        // Tạo các ô nhập mật khẩu bảo mật ẩn ký tự
        JPasswordField txtMatKhauCu = new JPasswordField();
        JPasswordField txtMatKhauMoi = new JPasswordField();
        JPasswordField txtXacNhanMoi = new JPasswordField();

        Object[] message = {
            "Nhập mật khẩu hiện tại:", txtMatKhauCu,
            "Nhập mật khẩu mới mới:", txtMatKhauMoi,
            "Xác nhận lại mật khẩu mới:", txtXacNhanMoi
        };

        // Ép Font chữ thông báo đồng điệu Segoe UI 14
        UIManager.put("OptionPane.buttonFont", segoeFont);
        UIManager.put("OptionPane.messageFont", segoeFont);
        
        int option = JOptionPane.showConfirmDialog(this, message, 
                "Hệ thống bảo mật tài khoản Giáo vụ", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (option == JOptionPane.OK_OPTION) {
            String passCu = new String(txtMatKhauCu.getPassword()).trim();
            String passMoi = new String(txtMatKhauMoi.getPassword()).trim();
            String confMoi = new String(txtXacNhanMoi.getPassword()).trim();

            // Validate trường rỗng
            if (passCu.isEmpty() || passMoi.isEmpty() || confMoi.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lỗi: Vui lòng nhập đầy đủ các trường thông tin mật khẩu!", "Cảnh báo bảo mật", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Kiểm tra trùng khớp gõ lại
            if (!passMoi.equals(confMoi)) {
                JOptionPane.showMessageDialog(this, "Lỗi: Mật khẩu mới và mật khẩu xác nhận không trùng khớp nhau!", "Xung đột dữ liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Ghi nhận trực tiếp dữ liệu xuống bảng TaiKhoan qua SQL Server vật lý
            String sql = "UPDATE TaiKhoan SET MatKhau = ? WHERE TenDangNhap = ? AND MatKhau = ?";
            try (Connection conn = com.nhom10.ooad.quanlysinhvien.DataBase.DataBaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setString(1, passMoi);
                ps.setString(2, userLogined);
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
     * Kịch bản xử lý Đăng xuất tác nhân Giáo vụ
     */
    private void xuLyDangXuatGiaoVu() {
        UIManager.put("OptionPane.buttonFont", segoeFont);
        UIManager.put("OptionPane.messageFont", segoeFont);
        int choose = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn đăng xuất khỏi phân hệ Quản lý Giáo vụ không?", 
                "Xác nhận đăng xuất", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE);
                
        if (choose == JOptionPane.YES_OPTION) {
            XacThucController.dangXuat();
            this.dispose();
            
            GiaoDienDangNhap frameLogin = new GiaoDienDangNhap();
            frameLogin.setVisible(true);
            
            System.out.println("-> Hệ thống: Cán bộ Giáo vụ đã đăng xuất an toàn.");
        }
    }
}