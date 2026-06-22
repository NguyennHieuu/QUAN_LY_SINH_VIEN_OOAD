package com.nhom10.ooad.quanlysinhvien.DAO;

import com.nhom10.ooad.quanlysinhvien.DataBase.DataBaseConnection;
import com.nhom10.ooad.quanlysinhvien.Model.TaiKhoan;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * TẦNG DAO ĐIỀU HÀNH NGHIỆP VỤ XÁC THỰC VÀ QUẢN TRỊ BẢO MẬT TÀI KHOẢN
 */
public class TaiKhoanDAO {

    /**
     * 🌟 THÊM MỚI: Nghiệp vụ Đổi mật khẩu tự phục vụ cho Sinh viên/Giảng viên
     * Kiểm tra đồng thời cả Mật khẩu cũ và Tên đăng nhập trước khi ghi đè dữ liệu mới
     * @return true nếu đổi thành công, false nếu sai mật khẩu cũ hoặc lỗi kết nối
     */
    public boolean doiMatKhau(String tenDangNhap, String matKhauCu, String matKhauMoi) {
        String sql = "UPDATE TaiKhoan SET MatKhau = ? WHERE TenDangNhap = ? AND MatKhau = ?";
        
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, matKhauMoi);
            ps.setString(2, tenDangNhap);
            ps.setString(3, matKhauCu);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi thực thi đổi mật khẩu cho tài khoản: " + tenDangNhap);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * UC03.1: Xác thực thông tin đăng nhập của người dùng.
     */
    public TaiKhoan kiemTraDangNhap(String tenDangNhap, String matKhau) {
        String sql = "SELECT * FROM TaiKhoan WHERE TenDangNhap = ? AND MatKhau = ? AND TrangThaiHoatDong = 1";
        
        try (Connection conn = DataBaseConnection.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) { 
            
            ps.setString(1, tenDangNhap); 
            ps.setString(2, matKhau); 
            
            try (ResultSet rs = ps.executeQuery()) { 
                if (rs.next()) {
                    return new TaiKhoan(
                        rs.getString("TenDangNhap"), 
                        rs.getString("MatKhau"), 
                        rs.getString("VaiTro"), 
                        rs.getInt("TrangThaiHoatDong")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi thực hiện xác thực đăng nhập tài khoản: " + tenDangNhap);
            e.printStackTrace(); 
        }
        return null; 
    }

    /**
     * UC04.2: Tra cứu danh sách tài khoản theo từ khóa (Tìm kiếm tổng thể)
     */
    public List<TaiKhoan> search(String keyword) {
        List<TaiKhoan> list = new ArrayList<>();
        String sql = "SELECT * FROM TaiKhoan WHERE TenDangNhap LIKE ? OR VaiTro LIKE ?";
        
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new TaiKhoan(
                        rs.getString("TenDangNhap"),
                        rs.getString("MatKhau"),
                        rs.getString("VaiTro"),
                        rs.getInt("TrangThaiHoatDong")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi thực hiện tìm kiếm tài khoản với từ khóa: " + keyword);
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Quét danh sách các sinh viên đã có hồ sơ cá nhân nhưng CHƯA CÓ tài khoản hệ thống
     */
    public List<Object[]> laySinhVienChuaCoTaiKhoan() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT s.MSSV, s.HoTen, s.MaLopQuanLy " +
                     "FROM SinhVien s " +
                     "LEFT JOIN TaiKhoan t ON s.TenDangNhap = t.TenDangNhap " +
                     "WHERE t.TenDangNhap IS NULL";
                     
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("MSSV"),
                    rs.getString("HoTen"),
                    rs.getString("MaLopQuanLy"),
                    "Chưa cấp tài khoản"
                });
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi quét danh sách sinh viên chưa cấp tài khoản.");
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Cấp phát tài khoản độc lập cho Sinh viên mồ côi dữ liệu
     */
    public boolean capTaiKhoanMoi(String mssv) {
        Connection conn = null;
        PreparedStatement psIns = null;
        PreparedStatement psUpd = null;
        try {
            conn = DataBaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            String sqlInsert = "INSERT INTO TaiKhoan (TenDangNhap, MatKhau, VaiTro, TrangThaiHoatDong) VALUES (?, ?, N'Sinh viên', 1)";
            psIns = conn.prepareStatement(sqlInsert);
            psIns.setString(1, mssv);
            psIns.setString(2, "123456");
            psIns.executeUpdate();

            String sqlUpdate = "UPDATE SinhVien SET TenDangNhap = ? WHERE MSSV = ?";
            psUpd = conn.prepareStatement(sqlUpdate);
            psUpd.setString(1, mssv);
            psUpd.setString(2, mssv);
            psUpd.executeUpdate();

            conn.commit(); 
            return true;
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            try { if (psIns != null) psIns.close(); } catch (SQLException e) {}
            try { if (psUpd != null) psUpd.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return false;
    }

    /**
     * Khôi phục mật khẩu mặc định (Reset password) về chuỗi "123456"
     */
    public boolean resetMatKhau(String tenDangNhap) {
        String sql = "UPDATE TaiKhoan SET MatKhau = ? WHERE TenDangNhap = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "123456");
            ps.setString(2, tenDangNhap);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Khóa hoặc Mở khóa trạng thái hoạt động của tài khoản
     */
    public boolean capNhatTrangThai(String tenDangNhap, int trangThaiMoi) {
        String sql = "UPDATE TaiKhoan SET TrangThaiHoatDong = ? WHERE TenDangNhap = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trangThaiMoi);
            ps.setString(2, tenDangNhap);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}