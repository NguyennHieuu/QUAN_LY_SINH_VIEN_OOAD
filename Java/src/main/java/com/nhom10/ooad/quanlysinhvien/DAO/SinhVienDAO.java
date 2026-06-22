package com.nhom10.ooad.quanlysinhvien.DAO;

import com.nhom10.ooad.quanlysinhvien.DataBase.DataBaseConnection;
import com.nhom10.ooad.quanlysinhvien.Model.SinhVien;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SinhVienDAO {
    
    // Logic UC01.1: Thêm mới sinh viên vào DB kết hợp kiểm tra an toàn
    public boolean insert(SinhVien sv) {
        Connection conn = null;
        PreparedStatement psTK = null;
        PreparedStatement psSV = null;
        PreparedStatement psLop = null;
        try {
            conn = DataBaseConnection.getConnection();
            conn.setAutoCommit(false); // 🌟 BẮT ĐẦU TRANSACTION ĐỒNG BỘ CẤP TÀI KHOẢN

            // 1. Tạo tự động tài khoản hệ thống đi kèm (Mật khẩu mặc định: 123456)
            String sqlTaiKhoan = "INSERT INTO TaiKhoan (TenDangNhap, MatKhau, VaiTro, TrangThaiHoatDong) VALUES (?, ?, N'Sinh viên', 1)";
            psTK = conn.prepareStatement(sqlTaiKhoan);
            psTK.setString(1, sv.getMssv());
            psTK.setString(2, "123456"); 
            psTK.executeUpdate();

            // 2. Chèn hồ sơ cốt lõi vào bảng SinhVien
            String sqlSinhVien = "INSERT INTO SinhVien (MSSV, HoTen, NgaySinh, GioiTinh, MaLopQuanLy, TenDangNhap) VALUES (?, ?, ?, ?, ?, ?)";
            psSV = conn.prepareStatement(sqlSinhVien);
            psSV.setString(1, sv.getMssv());
            psSV.setString(2, sv.getHoTen());
            if (sv.getNgaySinh() != null) {
                psSV.setDate(3, new java.sql.Date(sv.getNgaySinh().getTime()));
            } else {
                psSV.setNull(3, java.sql.Types.DATE);
            }
            psSV.setString(4, sv.getGioiTinh());
            psSV.setString(5, sv.getMaLopQuanLy());
            psSV.setString(6, sv.getMssv()); // Mặc định lấy MSSV làm tên đăng nhập liên kết khóa ngoại
            psSV.executeUpdate();

            // 3. Tự động tăng sĩ số của Lớp hành chính lên 1 bản ghi
            String sqlTangSiSo = "UPDATE LopHoc SET SiSo = SiSo + 1 WHERE MaLopQuanLy = ?";
            psLop = conn.prepareStatement(sqlTangSiSo);
            psLop.setString(1, sv.getMaLopQuanLy());
            psLop.executeUpdate();

            conn.commit(); // CHỐT TRANSACTION LƯU Ổ CỨNG VĨNH VIỄN
            return true;
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { if (psTK != null) psTK.close(); } catch (SQLException e) {}
            try { if (psSV != null) psSV.close(); } catch (SQLException e) {}
            try { if (psLop != null) psLop.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }

    // Logic UC01.3: Cập nhật thông tin sinh viên từ form
    public boolean update(SinhVien sv) {
        String sql = "UPDATE SinhVien SET HoTen = ?, NgaySinh = ?, GioiTinh = ?, MaLopQuanLy = ? WHERE MSSV = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sv.getHoTen());
            if (sv.getNgaySinh() != null) {
                ps.setDate(2, new java.sql.Date(sv.getNgaySinh().getTime()));
            } else {
                ps.setNull(2, java.sql.Types.DATE);
            }
            ps.setString(3, sv.getGioiTinh());
            ps.setString(4, sv.getMaLopQuanLy());
            ps.setString(5, sv.getMssv());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 🌟 HÀM OVERLOADING PHỤC VỤ TRANSACTION XÓA CHUỖI TỪ CONTROLLER.
     * Nhận trực tiếp Connection quản lý tập trung để thực thi câu lệnh, tránh ngắt kết nối giữa chừng.
     */
    public boolean delete(Connection conn, String mssv) throws SQLException {
        String sql = "DELETE FROM SinhVien WHERE MSSV = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mssv);
            return ps.executeUpdate() > 0;
        }
    }

    // Logic UC01.4: Xoá hồ sơ sinh viên độc lập (Giữ tương thích ngược)
    public boolean delete(String mssv) {
        String sql = "DELETE FROM SinhVien WHERE MSSV = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mssv);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Logic UC01.2: Tìm kiếm sinh viên theo MSSV hoặc Họ tên (Đã đồng bộ trường dữ liệu chuẩn)
    public List<SinhVien> search(String keyword) {
        List<SinhVien> list = new ArrayList<>();
        String sql = "SELECT * FROM SinhVien WHERE MSSV LIKE ? OR HoTen LIKE ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new SinhVien(
                        rs.getString("MSSV"),
                        rs.getString("HoTen"),
                        rs.getDate("NgaySinh"),
                        rs.getString("GioiTinh"),
                        rs.getString("MaLopQuanLy"),
                        rs.getString("TenDangNhap")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}