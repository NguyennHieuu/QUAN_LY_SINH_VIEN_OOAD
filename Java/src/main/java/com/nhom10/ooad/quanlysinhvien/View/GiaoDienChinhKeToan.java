package com.nhom10.ooad.quanlysinhvien.View;

import com.nhom10.ooad.quanlysinhvien.Controller.XacThucController;
import javax.swing.*;
import java.awt.*;
import java.sql.*; // 🌟 THÊM IMPORT: Để thực thi các câu lệnh SQL vật lý

/**
 * GiaoDienChinhKeToan - KHUNG GIAO DIỆN CHÍNH phân hệ Kế toán.
 * 🌟 ĐÃ GỘP LÕI: Tích hợp trực tiếp 3 tab chức năng, 2 hàm tiện ích tiền tệ và chức năng Đổi mật khẩu.
 */
public class GiaoDienChinhKeToan extends JFrame {
    
    private final JButton btnDoiMatKhau = new JButton("🔑 Đổi mật khẩu"); 
    private final JButton btnDangXuat = new JButton("Đăng xuất");
    private String userLogined = ""; // Biến lưu trữ tên đăng nhập phiên hiện tại

    // KHỞI TẠO ĐỒNG BỘ FONT SEGOE UI SIZE 14
    private final Font segoeFont = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font segoeBoldFont = new Font("Segoe UI", Font.BOLD, 14);

    public GiaoDienChinhKeToan() {
        // 1. Cấu hình các thuộc tính nền tảng cho Cửa sổ chính JFrame
        setTitle("HỆ THỐNG THÔNG TIN TÀI CHÍNH KẾ TOÁN - NHÓM 10");
        setSize(1250, 780); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        setLayout(new BorderLayout(10, 10));

        // 2. KHỞI TẠO THANH CHỨA TAB ĐIỀU HƯỚNG TRỰC TIẾP
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(segoeBoldFont); 
        
        // Lấy tên đăng nhập của tài khoản Kế toán hiện tại từ Session toàn cục
        if (XacThucController.getTaiKhoanHienTai() != null) {
            userLogined = XacThucController.getTaiKhoanHienTai().getTenDangNhap();
        }

        // Nhúng trực tiếp 3 giao diện chức năng con
        tabs.addTab("Tính học phí",        new GiaoDienTinhHocPhi());
        tabs.addTab("Quản lý thanh toán", new GiaoDienQuanLyThanhToan());
        tabs.addTab("Báo cáo tài chính",  new GiaoDienBaoCaoTaiChinh());

        add(tabs, BorderLayout.CENTER);

        // 3. TẠO THANH ĐÁY CHỨA CÁC NÚT HÀNH ĐỘNG HỆ THỐNG (SOUTH)
        JPanel panelBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        panelBottom.setBackground(Color.WHITE); // 🌟 ĐỂ NỀN TRẮNG: Đồng bộ với form Sinh viên
        
        // 🌟 ĐỊNH DẠNG VẬT LÝ NÚT: Đổi mật khẩu (Copy từ form Sinh viên)
        btnDoiMatKhau.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDoiMatKhau.setPreferredSize(new Dimension(150, 32));
        btnDoiMatKhau.setBackground(new Color(45, 125, 50)); // Màu xanh lá cây bảo mật
        btnDoiMatKhau.setForeground(Color.WHITE);
        btnDoiMatKhau.setOpaque(true);               
        btnDoiMatKhau.setBorderPainted(false);       
        btnDoiMatKhau.setEnabled(true);              

        // 🌟 ĐỊNH DẠNG VẬT LÝ NÚT: Đăng xuất (Copy từ form Sinh viên)
        btnDangXuat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDangXuat.setPreferredSize(new Dimension(120, 32));
        btnDangXuat.setBackground(new Color(211, 47, 47)); // Đỏ cam
        btnDangXuat.setForeground(Color.WHITE); 
        btnDangXuat.setOpaque(true);               
        btnDangXuat.setBorderPainted(false);       
        btnDangXuat.setEnabled(true);              
        
        // Thêm các nút vào thanh đáy
        panelBottom.add(btnDoiMatKhau);
        panelBottom.add(btnDangXuat);
        add(panelBottom, BorderLayout.SOUTH);

        // 4. ĐĂNG KÝ SỰ KIỆN NÚT BẤM
        btnDoiMatKhau.addActionListener(e -> xuLyDoiMatKhauKeToan());
        btnDangXuat.addActionListener(e -> xuLyDangXuatKeToan());
    }

    /**
     * Định dạng tiền VND, vd 6800000 -> "6,800,000 đ". Dùng chung cho toàn bộ phân hệ kế toán. 
     */
    public static String tien(long v) {
        return String.format("%,d", v) + " đ";
    }

    /**
     * Đọc số tiền từ ô nhập (bỏ dấu chấm/phẩy/khoảng trắng/đơn vị chữ). Rỗng/không hợp lệ -> -1. 
     */
    public static long doiTien(String s) {
        if (s == null) return -1;
        String chiSo = s.replaceAll("[^0-9]", "");
        if (chiSo.isEmpty()) return -1;
        try { 
            return Long.parseLong(chiSo); 
        } catch (NumberFormatException e) { 
            return -1; 
        }
    }

    /**
     * 🌟 ĐỔI MẬT KHẨU: Kết nối Database trực tiếp, kiểm tra mật khẩu hiện tại.
     */
    private void xuLyDoiMatKhauKeToan() {
        // Tạo các ô nhập mật khẩu bảo mật ẩn ký tự
        JPasswordField txtMatKhauCu = new JPasswordField();
        JPasswordField txtMatKhauMoi = new JPasswordField();
        JPasswordField txtXacNhanMoi = new JPasswordField();

        Object[] message = {
            "Nhập mật khẩu hiện tại:", txtMatKhauCu,
            "Nhập mật khẩu mới:", txtMatKhauMoi,
            "Xác nhận lại mật khẩu mới:", txtXacNhanMoi
        };

        // Đồng bộ Dialog Font
        UIManager.put("OptionPane.buttonFont", segoeFont);
        UIManager.put("OptionPane.messageFont", segoeFont);
        
        int option = JOptionPane.showConfirmDialog(this, message, 
                "Hệ thống bảo mật tài khoản Kế toán", 
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

            // Bắn câu lệnh SQL vật lý cập nhật mật khẩu trực tiếp dựa trên tên đăng nhập cán bộ kế toán
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
     * Kịch bản xử lý Đăng xuất tác nhân Kế toán
     */
    private void xuLyDangXuatKeToan() {
        UIManager.put("OptionPane.buttonFont", segoeFont);
        UIManager.put("OptionPane.messageFont", segoeFont);
        int choose = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn đăng xuất khỏi phân hệ Kế toán không?", 
                "Xác nhận đăng xuất", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE);
                
        if (choose == JOptionPane.YES_OPTION) {
            XacThucController.dangXuat();
            this.dispose();
            
            GiaoDienDangNhap frameLogin = new GiaoDienDangNhap();
            frameLogin.setVisible(true);
            System.out.println("-> Hệ thống: Cán bộ Kế toán đã đăng xuất an toàn.");
        }
    }
}