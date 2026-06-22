package com.nhom10.ooad.quanlysinhvien.Controller;

import com.nhom10.ooad.quanlysinhvien.DAO.TaiKhoanDAO;
import com.nhom10.ooad.quanlysinhvien.Model.TaiKhoan;

// IMPORT CÁC GIAO DIỆN CHÍNH CỦA 4 TÁC NHÂN
import com.nhom10.ooad.quanlysinhvien.View.GiaoDienChinhSinhVien; 
import com.nhom10.ooad.quanlysinhvien.View.GiaoDienChinhGiangVien; 
import com.nhom10.ooad.quanlysinhvien.View.GiaoDienChinhGiaoVu;    
import com.nhom10.ooad.quanlysinhvien.View.GiaoDienChinhKeToan;   
import com.nhom10.ooad.quanlysinhvien.View.GiaoDienDangNhap; 

import javax.swing.JOptionPane;

public class XacThucController {

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    
    // Biến static đóng vai trò làm Session toàn cục để lưu thông tin người dùng đang đăng nhập
    private static TaiKhoan taiKhoanHienTai = null;

    /**
     * UC03.1: Xử lý logic đăng nhập và điều hướng màn hình chức năng phù hợp.
     * @param viewFrame Nhận vào chính cửa sổ đăng nhập đang chạy để ẩn/đóng khi thành công
     * @param username Tên đăng nhập người dùng nhập từ giao diện View
     * @param password Mật khẩu người dùng nhập từ giao diện View
     * @return Chuỗi thông báo kết quả. Trả về "OK" nếu thành công, ngược lại trả về chuỗi thông báo lỗi cụ thể.
     */
    public String dangNhap(GiaoDienDangNhap viewFrame, String username, String password) {
        // 1. Kiểm tra validate cơ bản dữ liệu đầu vào từ giao diện
        if (username == null || username.trim().isEmpty()) {
            return "Vui lòng nhập tên đăng nhập!";
        }
        if (password == null || password.trim().isEmpty()) {
            return "Vui lòng nhập mật khẩu!";
        }

        // 2. Gọi tầng DAO kiểm tra tài khoản dưới Database
        TaiKhoan tk = taiKhoanDAO.kiemTraDangNhap(username.trim(), password);

        // 3. Xử lý logic nghiệp vụ rẽ nhánh kết quả
        if (tk == null) {
            return "Tài khoản hoặc mật khẩu không chính xác, hoặc tài khoản đã bị khóa!";
        }

        // 4. Kiểm tra trạng thái hoạt động bảo mật
        if (tk.getTrangThaiHoatDong() == 0) {
            return "Tài khoản của bạn hiện đang bị khóa. Vui lòng liên hệ Phòng Đào tạo!";
        }

        // 5. Ghi nhận tài khoản đăng nhập thành công vào hệ thống Session toàn cục
        taiKhoanHienTai = tk;

        // 6. Tắt cửa sổ đăng nhập cũ ngay lập tức trước khi mở cửa sổ mới
        if (viewFrame != null) {
            viewFrame.dispose();
        }

        // 7. Điều hướng mở Giao diện tương ứng bám sát vai trò (Role) phân quyền
        dieuHuongGiaoDien(tk.getVaiTro());

        return "OK";
    }

    /**
     * Hàm phụ trợ điều hướng mở các Frame giao diện tương ứng theo vai trò.
     * 🌟 ĐÃ SỬA: Đồng bộ nhận diện cả hai chuỗi "Tài chính" và "Kế toán" để khớp với Database mẫu.
     */
    private void dieuHuongGiaoDien(String vaiTro) {
        if (vaiTro == null) return;
        
        // Chuẩn hóa chuỗi tránh lỗi khoảng trắng
        String roleStr = vaiTro.trim();

        switch (roleStr) {
            case "Sinh viên":
                GiaoDienChinhSinhVien frameSV = new GiaoDienChinhSinhVien();
                frameSV.setVisible(true);
                System.out.println("-> Đăng nhập thành công: Đã mở màn hình chính Sinh viên.");
                break;
                
            case "Giảng viên":
                GiaoDienChinhGiangVien frameGV = new GiaoDienChinhGiangVien();
                frameGV.setVisible(true);
                System.out.println("-> Đăng nhập thành công: Đã mở màn hình chính Giảng viên.");
                break;
                
            case "Giáo vụ":
                GiaoDienChinhGiaoVu frameGVu = new GiaoDienChinhGiaoVu();
                frameGVu.setVisible(true);
                System.out.println("-> Đăng nhập thành công: Đã mở màn hình chính Giáo vụ.");
                break;
                
            // 🌟 ĐÃ SỬA TẠI ĐÂY: Sử dụng tính năng switch-case gộp nhánh của Java 
            // Giúp hệ thống hiểu "Tài chính" hay "Kế toán" đều chung một luồng xử lý màn hình hóa đơn học phí
            case "Tài chính":
            case "Kế toán":
                GiaoDienChinhKeToan frameKT = new GiaoDienChinhKeToan();
                frameKT.setVisible(true);
                System.out.println("-> Đăng nhập thành công: Đã mở màn hình chính Kế toán (Phân hệ Tài chính).");
                break;
                
            default:
                JOptionPane.showMessageDialog(null, 
                    "Hệ thống không nhận diện được vai trò: " + roleStr, 
                    "Lỗi phân quyền", JOptionPane.ERROR_MESSAGE);
                break;
        }
    }

    /**
     * Hàm tiện ích giúp lấy thông tin tài khoản đang đăng nhập ở bất cứ đâu trong hệ thống.
     */
    public static TaiKhoan getTaiKhoanHienTai() {
        return taiKhoanHienTai;
    }

    /**
     * Chức năng đăng xuất: Xóa sạch phiên làm việc
     */
    public static void dangXuat() {
        taiKhoanHienTai = null;
    }
}